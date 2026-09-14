package cn.iocoder.yudao.module.intent.service.executor;

import cn.iocoder.yudao.module.intent.dal.dataobject.ExecutorProfileDO;
import cn.iocoder.yudao.module.intent.dal.mysql.ExecutorProfileMapper;
import dev.intent.protocol.IntentSpec;
import dev.intent.sdk.executor.ExecutorProfile;
import dev.intent.sdk.executor.ExecutorProfileException;
import dev.intent.sdk.executor.ExecutorProfileLoader;
import dev.intent.sdk.pi.ExecutorProfiles;
import dev.intent.sdk.executor.IntentExecutor;
import dev.intent.sdk.pi.SkillExecutor;
import dev.intent.sdk.pi.IntentRuntime;
import dev.intent.sdk.tool.HostToolRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 执行器档案注册中心：执行器配置的单一事实来源（DB 化，与意图规范同模式）。
 *
 * <p>启动时将 classpath YAML 种子写入 intent_executor 表（已存在则跳过，删除状态保持），
 * 之后全部以 DB 为准：后台新增/修改/删除后 {@link #reload()} 重建执行器实例，
 * 再经 {@link #applyTo(IntentRuntime)} 热更新到运行时（无需发版重启）。</p>
 *
 * <p>宿主 Bean 形式声明的执行器（工作流/规则引擎等代码级 SPI）不归本注册中心管理，
 * 其 id 在 {@code reservedIds} 中：档案不可占用，热更新不会误注销。</p>
 */
@Slf4j
public class ExecutorProfileRegistry {

    /** 缓存条目：解析后的档案 + 来源（builtin=classpath 种子 / custom=后台创建）。 */
    public record Entry(ExecutorProfile profile, String source) {
    }

    private final ExecutorProfileMapper mapper;
    private final HostToolRegistry tools;
    private final Supplier<List<IntentSpec>> specCatalog;
    /** 不可被档案占用的执行器 id：内置 builtin-agent + 宿主 Bean 执行器。 */
    private final Set<String> reservedIds;
    private final Map<String, Entry> entries = new LinkedHashMap<>();
    private final List<IntentExecutor> built = new ArrayList<>();

    public ExecutorProfileRegistry(ExecutorProfileMapper mapper, HostToolRegistry tools,
            Supplier<List<IntentSpec>> specCatalog, Set<String> reservedIds) {
        this.mapper = mapper;
        this.tools = tools;
        this.specCatalog = specCatalog;
        this.reservedIds = Set.copyOf(reservedIds);
    }

    // ------------------------------------------------------------ 启动种子与加载

    /** 从 classpath 资源（YAML 文本）播种内置执行器：物理不存在才写入。 */
    public void seedFromYaml(String yaml, String sourceName) {
        try {
            ExecutorProfile profile = ExecutorProfileLoader.parse(yaml, true);
            if (mapper.countByExecutorIdIncludeDeleted(profile.id()) > 0) {
                return; // 已存在（含删除态）→ 保持现状
            }
            ExecutorProfileDO row = new ExecutorProfileDO();
            row.setExecutorId(profile.id());
            row.setProfileJson(ExecutorProfileLoader.toJson(profile));
            row.setSource("builtin");
            row.setRemark("classpath 种子");
            mapper.insert(row);
            log.info("[intent] 播种内置执行器: {}", profile.id());
        } catch (Exception e) {
            log.error("[intent] 播种执行器档案失败: {}", sourceName, e);
        }
    }

    /** 重新加载缓存并重建执行器实例（DB 全量，逻辑删除行自动过滤；坏档案跳过不阻断启动）。 */
    public synchronized void reload() {
        Map<String, Entry> loaded = new LinkedHashMap<>();
        List<IntentExecutor> rebuilt = new ArrayList<>();
        for (ExecutorProfileDO row : mapper.selectList()) {
            try {
                ExecutorProfile profile = ExecutorProfileLoader.parse(row.getProfileJson(), false);
                if (reservedIds.contains(profile.id())) {
                    log.error("[intent] 执行器档案与宿主 Bean 执行器冲突，已跳过: {}", profile.id());
                    continue;
                }
                rebuilt.add(ExecutorProfiles.build(List.of(profile), tools, specCatalog).get(0));
                loaded.put(profile.id(), new Entry(profile, row.getSource()));
            } catch (Exception e) {
                log.error("[intent] 执行器档案加载失败，已跳过: {} - {}", row.getExecutorId(), e.getMessage());
            }
        }
        entries.clear();
        entries.putAll(loaded);
        built.clear();
        built.addAll(rebuilt);
        log.info("[intent] 执行器档案缓存刷新: {} 个执行器", built.size());
    }

    // ------------------------------------------------------------ 读取

    /** 全部档案条目（缓存，含来源）。 */
    public List<Entry> entries() {
        return List.copyOf(entries.values());
    }

    public Optional<Entry> get(String executorId) {
        return Optional.ofNullable(entries.get(executorId));
    }

    /** 已构建的执行器实例（供运行时装配）。 */
    public List<IntentExecutor> executors() {
        return List.copyOf(built);
    }

    /** 不可被档案占用的执行器 id（内置 + 宿主 Bean）。 */
    public Set<String> reservedIds() {
        return reservedIds;
    }

    // ------------------------------------------------------------ 写操作（CRUD 后由调用方 applyTo 热更新）

    /** 新增（YAML 文本）。executorId 已存在（含删除态）或与保留 id 冲突时报错。 */
    public synchronized ExecutorProfile create(String yaml) {
        ExecutorProfile profile = ExecutorProfileLoader.parse(yaml, true);
        if (mapper.countByExecutorIdIncludeDeleted(profile.id()) > 0) {
            throw new ExecutorProfileException(profile.id(),
                    List.of("执行器已存在（可能已被删除），请更换标识或使用修改功能"));
        }
        insertRow(profile, "custom");
        reload();
        return profile;
    }

    /** 修改（YAML 文本，executorId 不可变更）。 */
    public synchronized ExecutorProfile update(String executorId, String yaml) {
        ExecutorProfile profile = ExecutorProfileLoader.parse(yaml, true);
        if (!profile.id().equals(executorId)) {
            throw new ExecutorProfileException(executorId,
                    List.of("YAML 中的 id(" + profile.id() + ") 与目标不一致"));
        }
        ExecutorProfileDO row = mapper.selectByExecutorId(executorId);
        if (row == null) {
            throw new ExecutorProfileException(executorId, List.of("执行器不存在，无法修改"));
        }
        row.setProfileJson(ExecutorProfileLoader.toJson(profile));
        mapper.updateById(row);
        reload();
        return profile;
    }

    /** 删除（逻辑删除）。 */
    public synchronized void delete(String executorId) {
        ExecutorProfileDO row = mapper.selectByExecutorId(executorId);
        if (row == null) {
            throw new ExecutorProfileException(executorId, List.of("执行器不存在，无法删除"));
        }
        mapper.deleteById(row.getId());
        reload();
    }

    /** 与运行时对齐：注销已删除的档案执行器、注册/替换新执行器（内置与宿主 Bean 执行器不受影响）。 */
    public synchronized void applyTo(IntentRuntime runtime) {
        Set<String> target = built.stream().map(IntentExecutor::id)
                .collect(java.util.stream.Collectors.toSet());
        for (String current : runtime.executorIds()) {
            if (!target.contains(current) && !reservedIds.contains(current)) {
                runtime.unregisterExecutor(current);
                log.info("[intent] 注销执行器: {}", current);
            }
        }
        for (IntentExecutor executor : built) {
            runtime.registerExecutor(executor);
            log.info("[intent] 注册执行器: {} ({})", executor.id(),
                    executor instanceof SkillExecutor ? "skill" : "agent");
        }
    }

    private void insertRow(ExecutorProfile profile, String source) {
        try {
            ExecutorProfileDO row = new ExecutorProfileDO();
            row.setExecutorId(profile.id());
            row.setProfileJson(ExecutorProfileLoader.toJson(profile));
            row.setSource(source);
            mapper.insert(row);
        } catch (Exception e) {
            throw new IllegalStateException("写入执行器档案失败", e);
        }
    }
}
