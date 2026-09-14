package cn.iocoder.yudao.module.intent.service;

import cn.iocoder.yudao.module.intent.dal.dataobject.IntentConfigDO;
import cn.iocoder.yudao.module.intent.dal.mysql.IntentConfigMapper;
import cn.iocoder.yudao.module.intent.framework.config.IntentProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.intent.protocol.CatalogResponse;
import dev.intent.protocol.ExecutionTraceRecord;
import dev.intent.protocol.GatewayStatus;
import dev.intent.protocol.IntentCatalogEntry;
import dev.intent.protocol.IntentFeedback;
import dev.intent.protocol.IntentRequest;
import dev.intent.protocol.IntentResult;
import dev.intent.protocol.IntentSpec;
import dev.intent.protocol.IntentStatus;
import dev.intent.sdk.catalog.IntentCatalogAssembler;
import dev.intent.sdk.catalog.IntentCatalogContext;
import dev.intent.sdk.catalog.IntentCatalogEnricher;
import dev.intent.sdk.host.IntentContextBridge;
import dev.intent.sdk.host.IntentPermissionPolicy;
import dev.intent.sdk.host.IntentPrincipal;
import dev.intent.sdk.pi.IntentRuntime;
import dev.intent.sdk.store.TraceStore;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 意图服务：目录（按角色过滤）、执行（角色鉴权）、留痕、反馈、上架配置。
 *
 * <p>可见性规则：intent_config.roles 为角色编码数组，["*"] 或空 = 不限制；
 * 未配置的意图沿用 IntentSpec.policy.roles（缺省不限制）。</p>
 */
@Slf4j
@Service
public class IntentService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Resource
    private cn.iocoder.yudao.module.intent.service.spec.IntentSpecRegistry specRegistry;
    @Resource
    private cn.iocoder.yudao.module.intent.service.executor.ExecutorProfileRegistry executorProfileRegistry;
    @Resource
    private IntentRuntime intentRuntime;
    @Resource
    private TraceStore traceStore;
    @Resource
    private IntentProperties properties;
    @Resource
    private IntentConfigMapper intentConfigMapper;
    @Resource
    private IntentPermissionPolicy permissionPolicy;
    @Resource
    private IntentContextBridge contextBridge;
    @Resource
    private dev.intent.sdk.tool.HostToolRegistry hostToolRegistry;
    /** 宿主目录增强器（动态提示 / 动态建议）：业务模块声明 Bean 即接入，缺省无增强。 */
    @Autowired(required = false)
    private List<IntentCatalogEnricher> catalogEnrichers = new ArrayList<>();
    /** 目录增强结果缓存：同一用户 + 页面在 TTL 内复用，执行意图后立即失效。 */
    private final CatalogCache catalogCache = new CatalogCache();

    // ------------------------------------------------------------ 目录与鉴权

        private IntentCatalogEntry toCatalogEntry(IntentSpec spec) {
        return new IntentCatalogEntry(spec.getId(), spec.getName(),
                spec.getDescription(), spec.getScope(), spec.getTargetSystem(),
                spec.getCardType(), spec.getParamsSchema(), spec.getContext(), spec.getPages(),
                spec.getAliases() == null || spec.getAliases().isEmpty() ? null : spec.getAliases(),
                null);
    }

    public CatalogResponse getCatalog(Long userId, String page) {
        // ① 当前用户可见的意图（上架 + 角色）：建议通道复用该集合做准入，
        //    下架/无权限的意图不得从"动态建议"漏出
        List<IntentCatalogEntry> visible = specRegistry.getAll().stream()
                .filter(this::isEnabled)
                .filter(spec -> visibleTo(userId, spec))
                .map(this::toCatalogEntry)
                .toList();
        // ② 当前页面装载的意图（前端渲染主体）
        List<IntentCatalogEntry> entries = visible.stream()
                .filter(entry -> matchesPage(entry.pages(), page))
                .toList();
        // ③ 目录增强：动态提示（徽标）与动态建议，带缓存与失败降级
        IntentCatalogAssembler.Result enriched = enrich(userId, page, entries, visible);
        return new CatalogResponse(properties.getSystemName(),
                properties.getGateway().isEnabled() ? GatewayStatus.ENABLED : GatewayStatus.UNAVAILABLE,
                enriched.entries(), enriched.suggestions());
    }

    /**
     * 目录增强：调用宿主 {@link IntentCatalogEnricher} 求值用户相关的事实
     * （如"2 份合同即将到期"），求值失败/超时不影响意图菜单可用性。
     */
    private IntentCatalogAssembler.Result enrich(Long userId, String page,
            List<IntentCatalogEntry> entries, List<IntentCatalogEntry> visible) {
        IntentProperties.Suggestions config = properties.getSuggestions();
        if (!config.isEnabled() || catalogEnrichers.isEmpty()) {
            return IntentCatalogAssembler.Result.of(entries);
        }
        IntentCatalogContext context = buildCatalogContext(userId, page);
        String cacheKey = context.cacheKey();
        IntentCatalogAssembler.Result cached = catalogCache.get(cacheKey, config.getCacheSeconds());
        if (cached != null) {
            return cached;
        }
        IntentCatalogAssembler.Result result = IntentCatalogAssembler.assemble(
                context, entries, visible, catalogEnrichers, config.getMax());
        catalogCache.put(cacheKey, result, config.getCacheSeconds());
        return result;
    }

    public IntentResult execute(IntentRequest request) {
        IntentSpec spec = requireSpec(request.intentId());
        // 上架与角色鉴权（目录隐藏 ≠ 免鉴权，执行前再校验一次）
        Long userId = request.user() == null ? null
                : (request.user().id() == null ? null : Long.valueOf(request.user().id()));
        if (!isEnabled(spec)) {
            return forbidden(spec, request, "该意图已下架");
        }
        if (!visibleTo(userId, spec)) {
            return forbidden(spec, request, "当前角色无权使用该意图");
        }
        log.info("[intent] 执行意图: {} user={}", spec.getId(),
                request.user() == null ? null : request.user().name());
        // 桥接宿主上下文（安全/租户）到执行器的工具线程：进程内直调，权限随宿主调用栈生效
        // 执行留痕由运行时统一落盘（TraceStore 装配进 IntentRuntime），服务层不再重复写
        IntentResult result = intentRuntime.execute(spec, request.params(), request.context(),
                request.user(), contextBridge.toolDecorator());
        // 用户刚办完一件事：目录增强（待办计数 / 建议）立即失效，下次打开即刷新
        catalogCache.evictUser(userId);
        return result;
    }

    private static IntentResult forbidden(IntentSpec spec, IntentRequest request, String message) {
        return new IntentResult("trc-" + java.util.UUID.randomUUID(), spec.getId(),
                IntentStatus.FAILED, null, null,
                new dev.intent.protocol.IntentError(dev.intent.protocol.IntentErrorCodes.FORBIDDEN, message),
                List.of(), dev.intent.protocol.UsageInfo.ZERO, 0);
    }

    // ------------------------------------------------------------ 上架配置

    /** 管理端列表：全部意图 + 运营配置合并视图。 */
    public List<Map<String, Object>> getConfigList() {
        List<Map<String, Object>> items = new ArrayList<>();
        for (IntentSpec spec : specRegistry.getAll()) {
            IntentConfigDO config = intentConfigMapper.selectByIntentId(spec.getId());
            Map<String, Object> item = new java.util.LinkedHashMap<>();
            item.put("intentId", spec.getId());
            item.put("name", spec.getName());
            item.put("description", spec.getDescription());
            item.put("scope", spec.getScope().name());
            item.put("version", spec.getVersion());
            item.put("executor", spec.getExecutor() == null || spec.getExecutor().isBlank()
                    ? "builtin-agent" : spec.getExecutor());
            item.put("enabled", config == null || !Boolean.FALSE.equals(config.getEnabled()));
            item.put("roles", config != null ? parseRoles(config.getRoles())
                    : normalizeRoles(spec.getPolicy().getRoles()));
            item.put("configured", config != null);
            items.add(item);
        }
        return items;
    }

    /** 更新配置（上架状态 + 可见角色 + 执行器引用）；roleCodes 为空 = 不限制。 */
    public void updateConfig(String intentId, Boolean enabled, List<String> roleCodes, String remark,
            String executor) {
        IntentSpec spec = requireSpec(intentId);
        List<String> roles = roleCodes == null || roleCodes.isEmpty()
                ? List.of("*") : List.copyOf(roleCodes);
        IntentConfigDO config = intentConfigMapper.selectByIntentId(intentId);
        if (config == null) {
            config = new IntentConfigDO();
            config.setIntentId(intentId);
        }
        config.setEnabled(enabled == null || enabled);
        config.setRoles(toJson(roles));
        config.setRemark(remark);
        if (config.getId() == null) {
            intentConfigMapper.insert(config);
        } else {
            intentConfigMapper.updateById(config);
        }
        // 执行器引用变更（builtin-agent = 缺省内置推理循环）：保存时报错，与槽位校验同级
        setExecutor(intentId, executor);
        log.info("[intent] 更新意图配置: {} enabled={} roles={}", intentId, config.getEnabled(), roles);
        evictCatalogCache();
    }

    // ------------------------------------------------------------ 供规则编排复用（同一口径，避免预览与线上两套逻辑）

    /** 当前用户可见的目录项（上架 + 角色过滤）。 */
    public List<IntentCatalogEntry> getVisibleEntries(Long userId) {
        return specRegistry.getAll().stream()
                .filter(this::isEnabled)
                .filter(spec -> visibleTo(userId, spec))
                .map(this::toCatalogEntry)
                .toList();
    }

    /** 页面装载的目录项（可见集合 + 页面挂载点过滤）。 */
    public List<IntentCatalogEntry> getPageEntries(Long userId, String page) {
        return getVisibleEntries(userId).stream()
                .filter(entry -> matchesPage(entry.pages(), page))
                .toList();
    }

    /** 目录求值上下文（用户 / 租户 / 页面 / 时区）：事实取数与规则预览共用。 */
    public IntentCatalogContext buildCatalogContext(Long userId, String page) {
        Long tenantId = cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.getTenantId();
        return new IntentCatalogContext(
                userId == null ? null : String.valueOf(userId), null,
                tenantId == null ? null : String.valueOf(tenantId),
                page, null, null, null, properties.getTimeZone());
    }

    /**
     * 目录增强缓存全量失效：规则启用/停用、意图配置（上架/角色/执行器）变更、
     * 意图规范（别名/名称/描述）增删改之后调用——运营改完就该立刻生效，而不是等 TTL。
     */
    public void evictCatalogCache() {
        catalogCache.clear();
    }

    /** 目录增强配置（规则预览的条数配额与缓存开关同源）。 */
    public IntentProperties getProperties() {
        return properties;
    }

    // ------------------------------------------------------------ helpers

    /** 页面挂载过滤：pages 声明了挂载点的意图只出现在匹配页面；未声明 = 全局意图（所有页面可见）。page 为空 = 意图中心（全量）。 */
    private boolean matchesPage(List<String> pages, String page) {
        if (page == null || page.isBlank()) {
            return true;
        }
        if (pages == null || pages.isEmpty()) {
            return true;
        }
        // 精确匹配，或前缀通配（crm/customer/* 命中 crm/customer/detail/320），
        // 与声明式规则的 page 白名单保持同一套语义
        for (String pattern : pages) {
            if (pattern.equals(page)) {
                return true;
            }
            if (pattern.endsWith("*") && page.startsWith(pattern.substring(0, pattern.length() - 1))) {
                return true;
            }
        }
        return false;
    }

    private boolean isEnabled(IntentSpec spec) {
        IntentConfigDO config = intentConfigMapper.selectByIntentId(spec.getId());
        return config == null || !Boolean.FALSE.equals(config.getEnabled());
    }

    private boolean visibleTo(Long userId, IntentSpec spec) {
        List<String> roles = rolesOf(spec);
        IntentPrincipal principal = userId == null
                ? IntentPrincipal.anonymous()
                : IntentPrincipal.of(String.valueOf(userId), null, null, List.of());
        return permissionPolicy.canUse(principal, roles);
    }

    private List<String> rolesOf(IntentSpec spec) {
        IntentConfigDO config = intentConfigMapper.selectByIntentId(spec.getId());
        if (config != null) {
            return normalizeRoles(parseRoles(config.getRoles()));
        }
        return normalizeRoles(spec.getPolicy().getRoles());
    }

    private static List<String> normalizeRoles(List<String> roles) {
        return roles == null ? List.of("*") : List.copyOf(roles);
    }

    private List<String> parseRoles(String json) {
        try {
            return MAPPER.readValue(json == null ? "[\"*\"]" : json,
                    MAPPER.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (Exception e) {
            return List.of("*");
        }
    }

    private static String toJson(List<String> roles) {
        try {
            return MAPPER.writeValueAsString(roles);
        } catch (Exception e) {
            return "[\"*\"]";
        }
    }

    public List<ExecutionTraceRecord> getHistory(Long userId, String intentId, int limit) {
        String userFilter = userId == null ? null : String.valueOf(userId);
        return traceStore.list(userFilter, limit).stream()
                .filter(r -> intentId == null || intentId.equals(r.intentId()))
                .toList();
    }

    public ExecutionTraceRecord getTrace(String traceId) {
        return traceStore.get(traceId).orElseThrow(
                () -> new IllegalArgumentException("执行留痕不存在: " + traceId));
    }

    public void saveFeedback(IntentFeedback feedback, Long userId, String nickname) {
        traceStore.saveFeedback(new IntentFeedback(feedback.traceId(), feedback.intentId(),
                userId == null ? null : String.valueOf(userId),
                feedback.rating(), feedback.comment()));
    }

    public Map<String, Object> getStatus() {
        return Map.of(
                "systemName", properties.getSystemName(),
                "gatewayEnabled", properties.getGateway().isEnabled(),
                "gatewayStatus", properties.getGateway().isEnabled()
                        ? GatewayStatus.ENABLED.name() : GatewayStatus.UNAVAILABLE.name(),
                "intentCount", specRegistry.getAll().size());
    }

    private IntentSpec requireSpec(String intentId) {
        return specRegistry.get(intentId)
                .orElseThrow(() -> new IllegalArgumentException("意图不存在: " + intentId));
    }

    /** 目录增强结果缓存：key = 用户|页面，TTL 到期或用户执行意图后失效。 */
    private static final class CatalogCache {

        void clear() {
            entries.clear();
        }

        private record Entry(IntentCatalogAssembler.Result result, long at) {
        }

        private final Map<String, Entry> entries = new java.util.concurrent.ConcurrentHashMap<>();

        IntentCatalogAssembler.Result get(String key, int ttlSeconds) {
            if (ttlSeconds <= 0) {
                return null;
            }
            Entry entry = entries.get(key);
            if (entry == null) {
                return null;
            }
            if (System.currentTimeMillis() - entry.at() > ttlSeconds * 1000L) {
                entries.remove(key);
                return null;
            }
            return entry.result();
        }

        void put(String key, IntentCatalogAssembler.Result result, int ttlSeconds) {
            if (ttlSeconds > 0) {
                entries.put(key, new Entry(result, System.currentTimeMillis()));
            }
        }

        void evictUser(Long userId) {
            String prefix = userId + "|";
            entries.keySet().removeIf(key -> key.startsWith(prefix));
        }
    }

    // ------------------------------------------------------------ 意图规范 CRUD（可维护）

    public List<Map<String, Object>> getSpecList() {
        List<Map<String, Object>> items = new java.util.ArrayList<>();
        for (IntentSpec spec : specRegistry.getAll()) {
            Map<String, Object> item = new java.util.LinkedHashMap<>();
            item.put("intentId", spec.getId());
            item.put("name", spec.getName());
            item.put("description", spec.getDescription());
            item.put("scope", spec.getScope().name());
            item.put("version", spec.getVersion());
            items.add(item);
        }
        return items;
    }

    public String getSpecYaml(String intentId) {
        IntentSpec spec = requireSpec(intentId);
        return dev.intent.sdk.spec.IntentSpecLoader.toYaml(spec);
    }

    public IntentSpec createSpec(String yaml) {
        IntentSpec spec = dev.intent.sdk.spec.IntentSpecLoader.parse(yaml, true);
        validateExecutorRef(spec.getExecutor());
        specRegistry.create(yaml);
        log.info("[intent] 新增意图: {}", spec.getId());
        evictCatalogCache();
        return spec;
    }

    public IntentSpec updateSpec(String intentId, String yaml) {
        IntentSpec spec = dev.intent.sdk.spec.IntentSpecLoader.parse(yaml, true);
        validateExecutorRef(spec.getExecutor());
        spec = specRegistry.update(intentId, yaml);
        log.info("[intent] 修改意图: {}", intentId);
        evictCatalogCache();
        return spec;
    }

    public void deleteSpec(String intentId) {
        specRegistry.delete(intentId);
        // 同步清理运营配置（上架/角色），避免残留
        intentConfigMapper.deleteByIntentId(intentId);
        log.info("[intent] 删除意图: {}", intentId);
        evictCatalogCache();
    }

    /** 执行器引用校验：保存意图时与槽位校验同级拦截执行器错配。 */
    private void validateExecutorRef(String executorId) {
        if (executorId == null || executorId.isBlank()) {
            return; // 缺省 = builtin-agent
        }
        List<String> known = intentRuntime.executorIds();
        if (!known.contains(executorId.trim())) {
            throw new IllegalArgumentException("意图声明的执行器未注册: " + executorId
                    + "（已注册: " + known + "），请先在「执行器档案」页创建");
        }
    }

    // ------------------------------------------------------------ 执行器档案管理（M2.2）

    /** 执行器档案列表（含来源与构建信息）。 */
    public List<Map<String, Object>> getExecutorList() {
        List<Map<String, Object>> items = new ArrayList<>();
        for (cn.iocoder.yudao.module.intent.service.executor.ExecutorProfileRegistry.Entry entry
                : executorProfileRegistry.entries()) {
            dev.intent.sdk.executor.ExecutorProfile p = entry.profile();
            Map<String, Object> item = new java.util.LinkedHashMap<>();
            item.put("executorId", p.id());
            item.put("name", p.name());
            item.put("type", p.type());
            item.put("description", p.description());
            item.put("model", p.model() == null ? null : p.model().modelId());
            item.put("tools", p.tools());
            item.put("stepCount", p.steps().size());
            item.put("maxTurns", p.limits().maxTurns());
            item.put("outputMaxRetries", p.limits().outputMaxRetries());
            item.put("source", entry.source());
            items.add(item);
        }
        return items;
    }

    /** 执行器下拉选项：内置推理循环 + 档案执行器（宿主 Bean 执行器为代码级 SPI，不在此列）。 */
    public List<Map<String, Object>> getExecutorOptions() {
        List<Map<String, Object>> options = new ArrayList<>();
        options.add(new java.util.LinkedHashMap<>(Map.of(
                "executorId", "builtin-agent", "name", "内置推理循环（缺省）", "type", "agent")));
        for (cn.iocoder.yudao.module.intent.service.executor.ExecutorProfileRegistry.Entry entry
                : executorProfileRegistry.entries()) {
            Map<String, Object> option = new java.util.LinkedHashMap<>();
            option.put("executorId", entry.profile().id());
            option.put("name", entry.profile().name() == null || entry.profile().name().isBlank()
                    ? entry.profile().id() : entry.profile().name());
            option.put("type", entry.profile().type());
            options.add(option);
        }
        return options;
    }

    public String getExecutorYaml(String executorId) {
        return dev.intent.sdk.executor.ExecutorProfileLoader.toYaml(requireExecutor(executorId));
    }

    /** 执行器档案 JSON（结构化编辑器回显）。 */
    public String getExecutorJson(String executorId) {
        return dev.intent.sdk.executor.ExecutorProfileLoader.toJson(requireExecutor(executorId));
    }

    /** 宿主工具清单（执行器档案页勾选工具白名单 / 流程工具节点）。 */
    public List<Map<String, Object>> getToolOptions() {
        List<Map<String, Object>> items = new ArrayList<>();
        hostToolRegistry.all().values().stream()
                .sorted(java.util.Comparator.comparing(dev.intent.sdk.tool.IntentTool::name))
                .forEach(tool -> {
                    Map<String, Object> item = new java.util.LinkedHashMap<>();
                    item.put("name", tool.name());
                    item.put("description", tool.description());
                    items.add(item);
                });
        return items;
    }

    public dev.intent.sdk.executor.ExecutorProfile createExecutor(String yaml) {
        dev.intent.sdk.executor.ExecutorProfile profile = executorProfileRegistry.create(yaml);
        executorProfileRegistry.applyTo(intentRuntime);
        log.info("[intent] 新增执行器: {}", profile.id());
        return profile;
    }

    public dev.intent.sdk.executor.ExecutorProfile updateExecutor(String executorId, String yaml) {
        dev.intent.sdk.executor.ExecutorProfile profile = executorProfileRegistry.update(executorId, yaml);
        executorProfileRegistry.applyTo(intentRuntime);
        log.info("[intent] 修改执行器: {}", executorId);
        return profile;
    }

    public void deleteExecutor(String executorId) {
        executorProfileRegistry.delete(executorId);
        executorProfileRegistry.applyTo(intentRuntime);
        log.info("[intent] 删除执行器: {}", executorId);
    }

    /** 修改意图的执行器引用（executorId 为空 / builtin-agent = 回到缺省内置推理循环）。 */
    public void setExecutor(String intentId, String executorId) {
        IntentSpec spec = requireSpec(intentId);
        String target = executorId == null || executorId.isBlank()
                || "default".equals(executorId) || "builtin-agent".equals(executorId)
                ? null : executorId.trim();
        validateExecutorRef(target == null ? "builtin-agent" : target);
        if (java.util.Objects.equals(spec.getExecutor() == null || spec.getExecutor().isBlank()
                ? null : spec.getExecutor().trim(), target)) {
            return; // 无变化
        }
        IntentSpec updated = copySpecWithExecutor(spec, target);
        specRegistry.update(intentId, dev.intent.sdk.spec.IntentSpecLoader.toYaml(updated));
        log.info("[intent] 意图 {} 执行器变更为: {}", intentId,
                target == null ? "缺省(builtin-agent)" : target);
        evictCatalogCache();
    }

    private static IntentSpec copySpecWithExecutor(IntentSpec spec, String executor) {
        return new IntentSpec.Builder()
                .id(spec.getId())
                .name(spec.getName())
                .description(spec.getDescription())
                .version(spec.getVersion())
                .aliases(spec.getAliases())
                .scope(spec.getScope())
                .targetSystem(spec.getTargetSystem())
                .paramsSchema(spec.getParamsSchema())
                .context(spec.getContext())
                .outputSchema(spec.getOutputSchema())
                .cardType(spec.getCardType())
                .tools(spec.getTools())
                .pages(spec.getPages())
                .promptTemplate(spec.getPromptTemplate())
                .executor(executor)
                .policy(spec.getPolicy())
                .build();
    }

    private dev.intent.sdk.executor.ExecutorProfile requireExecutor(String executorId) {
        return executorProfileRegistry.get(executorId)
                .map(cn.iocoder.yudao.module.intent.service.executor.ExecutorProfileRegistry.Entry::profile)
                .orElseThrow(() -> new IllegalArgumentException("执行器不存在: " + executorId));
    }
}
