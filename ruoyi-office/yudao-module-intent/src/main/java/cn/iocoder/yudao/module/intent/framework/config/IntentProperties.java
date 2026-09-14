package cn.iocoder.yudao.module.intent.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 意图 SDK 配置项（application-*.yaml 中 intent.* 前缀）。
 */
@Data
@ConfigurationProperties("intent")
public class IntentProperties {

    private boolean enabled = true;
    /** 系统名（目录响应中展示）。 */
    private String systemName = "ruoyi-office";
    /**
     * 租户时区（IANA，如 Asia/Shanghai）。
     * 目录增强求值"还有几天到期"按此计算；不配 = 服务器时区。
     */
    private String timeZone;
    /** 外部规则目录（运维热改；classpath intent-rules/*.yaml 之外的追加来源）。 */
    private String rulesDir;

    private final Llm llm = new Llm();
    private final Trace trace = new Trace();
    private final Gateway gateway = new Gateway();
    private final Suggestions suggestions = new Suggestions();

    @Data
    public static class Llm {
        /** 预置供应商标识：deepseek | openai | anthropic | custom（OpenAI 兼容端点） */
        private String provider = "deepseek";
        /** API Key；为空时回退环境变量（如 DEEPSEEK_API_KEY）。 */
        private String apiKey;
        /** custom 时的端点与模型。 */
        private String baseUrl;
        private String modelId;
        private long contextWindow = 131072;
        private int maxTokens = 8192;
        /** 单次执行最大推理轮数。 */
        private int maxTurns = 12;
        /** 输出不合规时的整体重试次数。 */
        private int outputMaxRetries = 1;
    }

    @Data
    public static class Trace {
        /** JSONL 留痕目录；为空则使用内存存储。 */
        private String dir = "./data/intent-traces";
    }

    @Data
    public static class Gateway {
        /** 装配网关后 remote/composite 意图才可用。 */
        private boolean enabled = false;
        /** 网关地址（M2 启用）。 */
        private String baseUrl;
        private String appKey;
        private String appSecret;
        /** 跨系统意图目录（M2 由网关下发；M1 用于演示不可用态的声明式配置）。 */
        private Map<String, String> remoteIntents;
    }

    /**
     * 目录增强（动态提示与动态建议）：宿主事实驱动的个性化入口。
     * 事实由 {@code IntentCatalogEnricher} 实现按当前用户求值，不经过 LLM。
     */
    @Data
    public static class Suggestions {
        /** 总开关：关闭后目录只返回静态意图，不做任何事实求值。 */
        private boolean enabled = true;
        /** 每个增强器（每条业务规则）最多返回几条动态建议；各规则配额独立，互不挤占。 */
        private int max = 5;
        /** 增强结果缓存秒数（0 = 不缓存）；同一用户执行意图后立即失效。 */
        private int cacheSeconds = 30;
    }
}
