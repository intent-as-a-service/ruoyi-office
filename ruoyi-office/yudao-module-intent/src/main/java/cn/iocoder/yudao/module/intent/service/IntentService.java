package cn.iocoder.yudao.module.intent.service;

import cn.iocoder.yudao.module.intent.dal.dataobject.IntentConfigDO;
import cn.iocoder.yudao.module.intent.dal.mysql.IntentConfigMapper;
import cn.iocoder.yudao.module.intent.framework.config.IntentProperties;
import cn.iocoder.yudao.module.intent.service.workbench.WorkbenchService;
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
import dev.intent.protocol.ResolvedSlot;
import dev.intent.protocol.SlotRef;
import dev.intent.protocol.SlotView;
import dev.intent.sdk.catalog.IntentCatalogAssembler;
import dev.intent.sdk.catalog.IntentCatalogContext;
import dev.intent.sdk.catalog.IntentCatalogEnricher;
import dev.intent.sdk.catalog.IntentCatalogKeys;
import dev.intent.sdk.host.IntentContextBridge;
import dev.intent.sdk.host.IntentCatalogEntries;
import dev.intent.sdk.host.IntentExecutorTypeResolver;
import dev.intent.sdk.host.IntentPermissionPolicy;
import dev.intent.sdk.host.IntentPrincipal;
import dev.intent.sdk.host.IntentSlotResolver;
import dev.intent.sdk.pi.IntentRuntime;
import dev.intent.sdk.store.TraceStore;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    /** 工作台装配（角色 / 视角 / 档案 / 槽位登记）：见 {@link WorkbenchService}。 */
    @Resource
    private WorkbenchService workbenchService;
    /** 执行器类型解析：目录项与执行结果要带 agent/skill/flow，前端才能做成本预估。 */
    @Resource
    private IntentExecutorTypeResolver executorTypeResolver;
    /** 槽位解析 SPI：没装实现的宿主用 none()，工作台跳过失效校验（不报错、不阻塞）。 */
    @Resource
    private IntentSlotResolver slotResolver;
    /** 宿主目录增强器（动态提示 / 动态建议）：业务模块声明 Bean 即接入，缺省无增强。 */
    @Autowired(required = false)
    private List<IntentCatalogEnricher> catalogEnrichers = new ArrayList<>();
    /**
     * 意图集合缓存：键 = 租户|用户|角色|页面|对象，TTL 较长（上下架 / 角色变更会显式清）。
     *
     * <p>与建议缓存<b>分开</b>是刻意的：槽位一变不该把整份意图菜单缓存废掉。</p>
     */
    private final TimedCache<List<IntentCatalogEntry>> entriesCache = new TimedCache<>();
    /** 目录增强（建议 / 徽标）结果缓存：键里多了视角与槽位哈希，TTL 短（建议是"事实"，放久会误导）。 */
    private final TimedCache<IntentCatalogAssembler.Result> suggestionsCache = new TimedCache<>();

    /** 意图集合缓存 TTL（秒）：上下架与角色变更都会显式清，这里只防高频重复扫描。 */
    private static final int ENTRIES_TTL_SECONDS = 600;

    // ------------------------------------------------------------ 目录与鉴权

    /**
     * 目录请求（工作台一次查询的全部输入）。
     *
     * @param page       当前页面标识（可空 = 意图中心，返回全量）
     * @param slots      上下文栈（可空）；它<b>只影响增强结果，不影响意图集合</b>
     * @param view       视角 id（可空 / self = 以本人全部角色看）；视角只能收窄角色
     * @param objectType 焦点对象类型（可空）：前端当前打开的业务单据
     * @param objectId   焦点对象编号（可空）
     * @param objectName 焦点对象展示名（可空，仅供理由文案）
     */
    public record CatalogRequest(String page, List<SlotView> slots, String view,
            String objectType, String objectId, String objectName) {

        public CatalogRequest {
            slots = slots == null ? List.of() : List.copyOf(slots);
        }

        /** 只按页面的请求（页面内嵌调用 / 老前端）。 */
        public static CatalogRequest of(String page) {
            return new CatalogRequest(page, List.of(), null, null, null, null);
        }
    }

    /** 兼容入口：只按页面取目录（页面内嵌调用 / 老前端）。 */
    public CatalogResponse getCatalog(Long userId, String page) {
        return getCatalog(userId, CatalogRequest.of(page));
    }

    /**
     * 目录（工作台版本）：意图集合由「角色 × 页面」决定，槽位只改变增强结果。
     *
     * <p>缓存拆两个键，别合并（合并的后果见 {@code IntentCatalogKeys} 的类注释）：</p>
     * <ol>
     *   <li>{@code entriesKey} = 租户|用户|角色|页面|对象 —— 槽位不在里面；</li>
     *   <li>{@code suggestionsKey} = entriesKey|视角|槽位哈希 —— 槽位只脏这一层。</li>
     * </ol>
     */
    public CatalogResponse getCatalog(Long userId, CatalogRequest request) {
        WorkbenchService.UserScope scope = workbenchService.scope(userId, request.view());
        IntentCatalogContext context = buildCatalogContext(userId, request.page(),
                request.objectType(), request.objectId(), request.objectName());
        // ① 当前作用域（角色 × 视角）下可见的意图：建议通道复用该集合做准入，
        //    下架/无权限的意图不得从"动态建议"漏出
        String entriesKey = IntentCatalogKeys.entriesKey(context, scope.effectiveRoles());
        List<IntentCatalogEntry> visible = entriesCache.get(entriesKey, ENTRIES_TTL_SECONDS);
        if (visible == null) {
            visible = specRegistry.getAll().stream()
                    .filter(this::isEnabled)
                    .filter(spec -> visibleTo(userId, spec, scope))
                    .map(spec -> IntentCatalogEntries.of(spec, executorTypeResolver))
                    .toList();
            entriesCache.put(entriesKey, userId, visible, ENTRIES_TTL_SECONDS);
        }
        // ② 当前页面装载的意图（前端渲染主体）：与槽位无关，是纯函数
        List<IntentCatalogEntry> entries = visible.stream()
                .filter(entry -> matchesPage(entry.pages(), request.page()))
                .toList();
        // ③ 目录增强：动态提示（徽标）与动态建议，带缓存与失败降级
        String suggestionsKey = IntentCatalogKeys.suggestionsKey(entriesKey, request.view(), request.slots());
        IntentCatalogAssembler.Result enriched = enrich(userId, request, context, entries, visible, suggestionsKey);
        // issues 必须原样带出：丢了它，前端就分不出"今天真没待办"与"取数挂了"
        return new CatalogResponse(properties.getSystemName(),
                properties.getGateway().isEnabled() ? GatewayStatus.ENABLED : GatewayStatus.UNAVAILABLE,
                enriched.entries(), enriched.suggestions(), enriched.issues());
    }

    /**
     * 目录增强：调用宿主 {@link IntentCatalogEnricher} 求值用户相关的事实
     * （如"2 份合同即将到期"），求值失败/超时不影响意图菜单可用性。
     */
    private IntentCatalogAssembler.Result enrich(Long userId, CatalogRequest request,
            IntentCatalogContext context, List<IntentCatalogEntry> entries,
            List<IntentCatalogEntry> visible, String suggestionsKey) {
        IntentProperties.Suggestions config = properties.getSuggestions();
        if (!config.isEnabled() || catalogEnrichers.isEmpty()) {
            return IntentCatalogAssembler.Result.of(entries);
        }
        IntentCatalogAssembler.Result cached = suggestionsCache.get(suggestionsKey, config.getCacheSeconds());
        if (cached != null) {
            return cached;
        }
        // 槽位在这里透传给增强器：它是"按用户当前谈的对象推待办"的唯一入口
        IntentCatalogAssembler.Result result = IntentCatalogAssembler.assemble(
                context, request.slots(), entries, visible, catalogEnrichers, config.getMax());
        suggestionsCache.put(suggestionsKey, userId, result, config.getCacheSeconds());
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
        suggestionsCache.evictUser(userId);
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
                .map(spec -> IntentCatalogEntries.of(spec, executorTypeResolver))
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
        return buildCatalogContext(userId, page, null, null, null);
    }

    /** 目录求值上下文（用户 / 租户 / 页面 / 焦点对象 / 时区）。 */
    public IntentCatalogContext buildCatalogContext(Long userId, String page,
            String objectType, String objectId, String objectName) {
        Long tenantId = cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.getTenantId();
        return new IntentCatalogContext(
                userId == null ? null : String.valueOf(userId), null,
                tenantId == null ? null : String.valueOf(tenantId),
                page, objectType, objectId, objectName, properties.getTimeZone());
    }

    /**
     * 目录增强缓存全量失效：规则启用/停用、意图配置（上架/角色/执行器）变更、
     * 意图规范（别名/名称/描述）增删改之后调用——运营改完就该立刻生效，而不是等 TTL。
     */
    public void evictCatalogCache() {
        entriesCache.clear();
        suggestionsCache.clear();
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

    /** 兼容入口：不考虑视角（页面内嵌调用 / 规则预览）。 */
    private boolean visibleTo(Long userId, IntentSpec spec) {
        return visibleTo(userId, spec, null);
    }

    /**
     * 可见性判定：角色 + 视角。
     *
     * <p>视角<b>只可能减、不可能加</b>——{@code scope.effectiveRoles()} 是本人角色的子集
     * （见 {@code WorkbenchProfileRegistry.effectiveRoles}）。所以这里用它做的是"收窄"，
     * 而真正的权限判定仍然交给宿主权限体系。</p>
     */
    private boolean visibleTo(Long userId, IntentSpec spec, WorkbenchService.UserScope scope) {
        List<String> roles = rolesOf(spec);
        if (roles.isEmpty() || roles.contains("*")) {
            return true;
        }
        if (scope != null && scope.narrowed()
                && java.util.Collections.disjoint(roles, scope.effectiveRoles())) {
            return false;
        }
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

    // ------------------------------------------------------------ Run（执行留痕）

    /** Run 上限：P0 只做 limit，不做游标分页——JSONL 是流式全量读，写游标是假的。 */
    private static final int MAX_RUN_LIMIT = 100;

    /**
     * 我的 Run 列表。
     *
     * <p><b>方法签名里没有"查谁的"，也没有可选的 userId</b>：参数不存在就无从伪造。
     * 将来真要做"主管看团队"，正确做法是另开端点 + 另做权限判定，
     * 而不是在这里加一个可选参数——那会让这道防线当场失效。</p>
     *
     * <p>底下走的是 {@code TraceStore.listOwned}：它对本办法要求的 userId 为空的场景
     * <b>直接抛错</b>，而不是退化成 {@code list(null, n)} "查所有人"。</p>
     */
    public List<ExecutionTraceRecord> listRuns(Long userId, int limit) {
        return traceStore.listOwned(requireUserId(userId), clampLimit(limit));
    }

    /**
     * 取一条<b>属于我</b>的 Run：不属于我 / 不存在 / 入参为空，一律 {@link Optional#empty()}。
     *
     * <p>端点把它映射成 404 而不是 403：403 会泄露"这条 traceId 存在但不属于你"，
     * 于是 traceId 就成了探测他人执行记录的口子。这是安全口径，不是 UI 偏好。</p>
     */
    public Optional<ExecutionTraceRecord> findRun(Long userId, String traceId) {
        return traceStore.findOwned(requireUserId(userId), traceId);
    }

    /** 兼容老前端的"按意图过滤"：口径与 {@link #listRuns} 完全一致，没有第二条取数路径。 */
    public List<ExecutionTraceRecord> getHistory(Long userId, String intentId, int limit) {
        return listRuns(userId, limit).stream()
                .filter(record -> intentId == null || intentId.equals(record.intentId()))
                .toList();
    }

    /** 身份缺失时提前炸：不让"身份丢了"静默退化成一次查询所有人的调用。 */
    private static String requireUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("需要登录身份：Run 只按归属查询");
        }
        return String.valueOf(userId);
    }

    private static int clampLimit(int limit) {
        return Math.min(Math.max(limit, 1), MAX_RUN_LIMIT);
    }

    // ------------------------------------------------------------ 槽位

    /**
     * 批量解析槽位（失效检测 / 接力反查）。
     *
     * <p>没装 {@code IntentSlotResolver} 实现的宿主拿到空 Map，前端跳过校验——
     * 这是降级基线，不报错、不阻塞。</p>
     */
    public Map<String, ResolvedSlot> resolveSlots(Long userId, List<SlotRef> refs) {
        IntentPrincipal principal = userId == null
                ? IntentPrincipal.anonymous()
                : IntentPrincipal.of(String.valueOf(userId), null, null, List.of());
        return slotResolver.resolve(principal, refs);
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

    /**
     * 带归属的定时缓存：TTL 到期失效，或按<b>归属用户</b>精确失效。
     *
     * <p>把 userId 存在值里、而不是靠键的前缀去匹配，是一个刻意的选择：
     * 键的形状（{@code 租户|用户|角色|页面|对象}）是 SDK 定的，
     * 宿主按"第二个竖线前面就是用户"去猜，SDK 哪天调整键的构成就静默失效——
     * 表现是"用户刚办完事、待办计数却不变"，而且不报错，极难定位。</p>
     */
    private static final class TimedCache<V> {

        private record Holder<V>(Long userId, V value, long at) {
        }

        private final Map<String, Holder<V>> entries = new java.util.concurrent.ConcurrentHashMap<>();

        V get(String key, int ttlSeconds) {
            if (ttlSeconds <= 0) {
                return null;
            }
            Holder<V> holder = entries.get(key);
            if (holder == null) {
                return null;
            }
            if (System.currentTimeMillis() - holder.at() > ttlSeconds * 1000L) {
                entries.remove(key);
                return null;
            }
            return holder.value();
        }

        void put(String key, Long userId, V value, int ttlSeconds) {
            if (ttlSeconds > 0) {
                entries.put(key, new Holder<>(userId, value, System.currentTimeMillis()));
            }
        }

        /** 某个用户执行完意图后，只清他自己的那几份（不误伤别人，也不漏清）。 */
        void evictUser(Long userId) {
            entries.entrySet().removeIf(entry -> java.util.Objects.equals(entry.getValue().userId(), userId));
        }

        void clear() {
            entries.clear();
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
