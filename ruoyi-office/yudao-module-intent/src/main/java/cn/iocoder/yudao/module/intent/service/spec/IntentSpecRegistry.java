package cn.iocoder.yudao.module.intent.service.spec;

import cn.iocoder.yudao.module.intent.dal.dataobject.IntentSpecDO;
import cn.iocoder.yudao.module.intent.dal.mysql.IntentSpecMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.intent.protocol.IntentSpec;
import dev.intent.sdk.spec.IntentSpecException;
import dev.intent.sdk.spec.IntentSpecLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 意图规范注册中心：意图定义的单一事实来源（DB 化）。
 *
 * <p>启动时将 classpath YAML 种子写入 intent_spec 表（已存在则跳过，删除状态保持），
 * 之后全部以 DB 为准：后台新增/修改/删除即时刷新缓存，目录与执行引擎无感切换。</p>
 */
@Slf4j
@Service
public class IntentSpecRegistry {

    private static final ObjectMapper JSON = new ObjectMapper();

    private final IntentSpecMapper mapper;

    /**
     * 意图规范快照：整体替换而不是清空后重填。
     * 目录 / 执行 / 规则预览都会并发读它，clear() 与 addAll() 之间读到空快照，
     * 会把"没有意图"的结论缓存进目录缓存（表现为页面菜单突然空白 30 秒）。
     */
    private volatile List<IntentSpec> cache = List.of();

    public IntentSpecRegistry(IntentSpecMapper mapper) {
        this.mapper = mapper;
    }

    /** 从 classpath 资源（YAML 文本）播种内置意图：物理不存在才写入。 */
    public void seedFromYaml(String yaml, String sourceName) {
        try {
            IntentSpec spec = IntentSpecLoader.parse(yaml, true);
            if (mapper.countByIntentIdIncludeDeleted(spec.getId()) > 0) {
                return; // 已存在（含删除态）→ 保持现状
            }
            IntentSpecDO row = new IntentSpecDO();
            row.setIntentId(spec.getId());
            row.setSpecJson(JSON.writeValueAsString(spec));
            row.setSource("builtin");
            row.setRemark("classpath 种子");
            mapper.insert(row);
            log.info("[intent] 播种内置意图: {}", spec.getId());
        } catch (Exception e) {
            log.error("[intent] 播种意图失败: {}", sourceName, e);
        }
    }

    /** 重新加载缓存（DB 全量，逻辑删除行自动过滤）。 */
    public synchronized void reload() {
        List<IntentSpec> loaded = new java.util.ArrayList<>();
        for (IntentSpecDO row : mapper.selectList()) {
            try {
                loaded.add(JSON.readValue(row.getSpecJson(), IntentSpec.class));
            } catch (Exception e) {
                log.error("[intent] 意图规范反序列化失败: {}", row.getIntentId(), e);
            }
        }
        loaded.sort(Comparator.comparing(IntentSpec::getId));
        cache = List.copyOf(loaded);
        log.info("[intent] 意图规范缓存刷新: {} 个意图", cache.size());
    }

    /** 全量意图（缓存）。 */
    public List<IntentSpec> getAll() {
        List<IntentSpec> snapshot = cache;
        if (snapshot.isEmpty()) {
            reload();
            snapshot = cache;
        }
        return snapshot;
    }

    public Optional<IntentSpec> get(String intentId) {
        return getAll().stream().filter(s -> s.getId().equals(intentId)).findFirst();
    }

    // ------------------------------------------------------------ 写操作

    /** 新增（YAML 文本）。intentId 已存在（含删除态）时报错。 */
    public IntentSpec create(String yaml) {
        IntentSpec spec = IntentSpecLoader.parse(yaml, true);
        if (mapper.countByIntentIdIncludeDeleted(spec.getId()) > 0) {
            throw new IntentSpecException(spec.getId(),
                    List.of("意图已存在（可能已被删除），请更换编号或使用修改功能"));
        }
        insertRow(spec, "custom");
        reload();
        return spec;
    }

    /** 修改（YAML 文本，intentId 不可变更）。 */
    public IntentSpec update(String intentId, String yaml) {
        IntentSpec spec = IntentSpecLoader.parse(yaml, true);
        if (!spec.getId().equals(intentId)) {
            throw new IntentSpecException(intentId,
                    List.of("YAML 中的 intentId(" + spec.getId() + ") 与目标不一致"));
        }
        IntentSpecDO row = mapper.selectByIntentId(intentId);
        if (row == null) {
            throw new IntentSpecException(intentId, List.of("意图不存在，无法修改"));
        }
        try {
            row.setSpecJson(JSON.writeValueAsString(spec));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        mapper.updateById(row);
        reload();
        return spec;
    }

    /** 删除（逻辑删除）。 */
    public void delete(String intentId) {
        IntentSpecDO row = mapper.selectByIntentId(intentId);
        if (row == null) {
            throw new IntentSpecException(intentId, List.of("意图不存在，无法删除"));
        }
        mapper.deleteById(row.getId());
        reload();
    }

    private void insertRow(IntentSpec spec, String source) {
        try {
            IntentSpecDO row = new IntentSpecDO();
            row.setIntentId(spec.getId());
            row.setSpecJson(JSON.writeValueAsString(spec));
            row.setSource(source);
            mapper.insert(row);
        } catch (Exception e) {
            throw new IllegalStateException("写入意图规范失败", e);
        }
    }
}
