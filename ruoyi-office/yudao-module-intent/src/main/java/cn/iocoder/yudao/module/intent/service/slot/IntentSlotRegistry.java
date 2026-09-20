package cn.iocoder.yudao.module.intent.service.slot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.intent.protocol.ContextField;
import dev.intent.protocol.IntentSpec;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * 槽位类型登记中心：意图工作台上下文栈的"类型系统"单一事实来源。
 *
 * <p>数据来自 {@code classpath*:intent-slots/*.yaml}（业务模块自带），
 * 平台侧只负责加载、索引与<b>审计</b>。审计这一步不能省：</p>
 * <ul>
 *   <li><b>未登记的上下文键</b>（意图声明了、登记表里没有、也不在 nonSlots 里）→ WARN。
 *       这类键进不了上下文栈，用户会看到"这条意图明明说了要客户，工作台却摆不出客户"；</li>
 *   <li><b>已登记但无意图使用</b> → INFO。不是错误（可能是为将来的意图预留），
 *       但要说出来，避免"登记表越写越长、没人知道哪条还有用"。</li>
 * </ul>
 *
 * <p>审计只打印、不阻断启动：登记表缺一条不该让整个系统起不来；
 * 但它是启动日志里能一眼看到的那几行，属于"必须被看见的降级"。</p>
 */
@Slf4j
public class IntentSlotRegistry {

    private static final ObjectMapper YAML = new ObjectMapper(new YAMLFactory());

    private final List<IntentSlotType> types;
    private final Set<String> nonSlots;
    private final String groupFallbackTitle;
    private final Map<String, IntentSlotType> byType;
    private final Map<String, IntentSlotType> byKey;

    private IntentSlotRegistry(List<IntentSlotType> types, Set<String> nonSlots, String groupFallbackTitle) {
        this.types = List.copyOf(types);
        this.nonSlots = Set.copyOf(nonSlots);
        this.groupFallbackTitle = groupFallbackTitle;
        Map<String, IntentSlotType> typeIndex = new LinkedHashMap<>();
        Map<String, IntentSlotType> keyIndex = new LinkedHashMap<>();
        for (IntentSlotType type : this.types) {
            IntentSlotType previous = typeIndex.putIfAbsent(type.type(), type);
            if (previous != null) {
                throw new IllegalStateException("槽位类型重复登记: " + type.type()
                        + "（" + previous.source() + " 与 " + type.source() + "）");
            }
            keyIndex.put(type.key(), type);
        }
        this.byType = Map.copyOf(typeIndex);
        this.byKey = Map.copyOf(keyIndex);
    }

    /** 解析单份 YAML 里的 slots 段。 */
    public static List<IntentSlotType> parse(String yaml, String source) {
        List<IntentSlotType> result = new ArrayList<>();
        Map<String, Object> root = readRoot(yaml);
        Object raw = root.get("slots");
        if (!(raw instanceof List<?> list)) {
            return result;
        }
        for (Object item : list) {
            if (!(item instanceof Map<?, ?> map)) {
                continue;
            }
            String type = str(map.get("type"));
            String key = str(map.get("key"));
            if (type == null || key == null) {
                throw new IllegalStateException(source + " 中的槽位登记缺少 type 或 key: " + map);
            }
            result.add(new IntentSlotType(type, key, str(map.get("title")),
                    bool(map.get("multi")), map.get("entity") == null || bool(map.get("entity")),
                    strList(map.get("values")), str(map.get("hint")),
                    str(map.get("picker")), str(map.get("pickerHint")), source));
        }
        return result;
    }

    /** 解析单份 YAML 里的 nonSlots 段：明确不算槽位的上下文键。 */
    public static Set<String> parseNonSlots(String yaml) {
        Set<String> keys = new LinkedHashSet<>();
        Object raw = readRoot(yaml).get("nonSlots");
        if (!(raw instanceof List<?> list)) {
            return keys;
        }
        for (Object item : list) {
            if (item instanceof Map<?, ?> map) {
                String key = str(map.get("key"));
                if (key != null) {
                    keys.add(key);
                }
            } else if (item instanceof String text && !text.isBlank()) {
                keys.add(text.trim());
            }
        }
        return keys;
    }

    /**
     * 解析 YAML 根部的 {@code groupFallbackTitle}：<b>挂不上任何实体槽位的意图</b>归到哪一组。
     *
     * <p>为什么这个组名也要从登记表来：工作台按实体给意图分组时，组名直接取槽位的
     * {@code title}（客户 / 合同 / …）——那 7 组由登记表自动产出。剩下那一组没有槽位可依，
     * 如果写死在前端，换一个宿主（例如系统/监控口径的宿主）就得改前端源码，
     * 「换个宿主只改 YAML」这条承诺当场失效。</p>
     */
    public static String parseGroupFallbackTitle(String yaml) {
        return str(readRoot(yaml).get("groupFallbackTitle"));
    }

    private static Map<String, Object> readRoot(String yaml) {
        try {
            Map<String, Object> root = YAML.readValue(yaml,
                    YAML.getTypeFactory().constructMapType(LinkedHashMap.class, String.class, Object.class));
            return root == null ? Map.of() : root;
        } catch (Exception e) {
            throw new IllegalStateException("槽位登记表解析失败: " + e.getMessage(), e);
        }
    }

    public static IntentSlotRegistry of(List<IntentSlotType> types, Collection<String> nonSlots) {
        return of(types, nonSlots, null);
    }

    public static IntentSlotRegistry of(List<IntentSlotType> types, Collection<String> nonSlots,
            String groupFallbackTitle) {
        return new IntentSlotRegistry(types, new LinkedHashSet<>(nonSlots), groupFallbackTitle);
    }

    public List<IntentSlotType> all() {
        return types;
    }

    public Optional<IntentSlotType> byType(String type) {
        return type == null ? Optional.empty() : Optional.ofNullable(byType.get(type));
    }

    public Optional<IntentSlotType> byKey(String key) {
        return key == null ? Optional.empty() : Optional.ofNullable(byKey.get(key));
    }

    public Set<String> nonSlots() {
        return nonSlots;
    }

    /** 挂不上实体槽位的意图的组名；宿主没声明时为 {@code null}（前端退化成"其它"）。 */
    public String groupFallbackTitle() {
        return groupFallbackTitle;
    }

    /**
     * 启动期审计：把"意图要的键"与"登记表有的键"对一遍。
     *
     * @return 未登记的上下文键（调用方可断言；启动路径只打日志）
     */
    public Set<String> audit(List<IntentSpec> specs) {
        Set<String> declared = new TreeSet<>();
        for (IntentSpec spec : specs) {
            List<ContextField> context = spec.getContext();
            if (context == null) {
                continue;
            }
            for (ContextField field : context) {
                if (field != null && field.key() != null && !field.key().isBlank()) {
                    declared.add(field.key().trim());
                }
            }
        }
        Set<String> unregistered = new TreeSet<>();
        for (String key : declared) {
            if (nonSlots.contains(key) || byKey.containsKey(key)) {
                continue;
            }
            unregistered.add(key);
        }
        if (!unregistered.isEmpty()) {
            log.warn("[intent] 有 {} 个上下文键未登记槽位类型，这些实体在工作台上下文栈里摆不出来: {}"
                            + "（登记表 intent-slots/*.yaml；确实不是实体的键请写进 nonSlots）",
                    unregistered.size(), unregistered);
        }
        Set<String> unused = new TreeSet<>(byKey.keySet());
        unused.removeAll(declared);
        if (!unused.isEmpty()) {
            log.info("[intent] 已登记但当前无意图使用的槽位: {}", unused);
        }
        log.info("[intent] 槽位类型登记: {} 个（{}）", types.size(),
                types.stream().map(IntentSlotType::type).toList());
        return unregistered;
    }

    // ------------------------------------------------------------ YAML 取值（弱类型，逐项兜底）

    private static String str(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : text;
    }

    private static boolean bool(Object value) {
        return value instanceof Boolean b ? b : Boolean.parseBoolean(String.valueOf(value));
    }

    private static List<String> strList(Object value) {
        if (!(value instanceof List<?> list)) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        for (Object item : list) {
            String text = str(item);
            if (text != null) {
                result.add(text);
            }
        }
        return result;
    }
}
