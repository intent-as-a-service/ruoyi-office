package cn.iocoder.yudao.module.intent.framework.config;

import cn.iocoder.yudao.module.intent.service.spec.IntentSpecRegistry;
import dev.intent.protocol.IntentSpec;
import dev.intent.sdk.tool.IntentTool;
import dev.intent.sdk.analyze.IntentAnalyzer;
import dev.intent.sdk.catalog.IntentCatalogEnricher;
import dev.intent.sdk.llm.LlmConfig;
import dev.intent.sdk.pi.IntentRuntime;
import dev.intent.sdk.spec.IntentSpecLoader;
import dev.intent.sdk.tool.HostToolRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * 内嵌 AI 意图 SDK 装配（平台侧，与业务解耦）。
 *
 * <p>业务系统的接入方式只有两步（无需改动本类）：</p>
 * <ol>
 *   <li>把宿主工具声明为 Spring Bean（实现 SDK 的 {@link IntentTool}）；</li>
 *   <li>在模块 resources 的 {@code classpath:intent/*.yaml} 放置 IntentSpec 作为种子。</li>
 * </ol>
 *
 * <p>意图定义启动后进入 intent_spec 表，后台可增删改即时生效；
 * 网关为可组装项：未启用时 remote/composite 意图自动降级为不可用。</p>
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(IntentProperties.class)
@ConditionalOnProperty(prefix = "intent", name = "enabled", havingValue = "true", matchIfMissing = true)
public class IntentAutoConfiguration {



    /** 上下文桥：把登录态 / 租户 / 请求属性从 HTTP 线程搬到执行器的工作线程。 */
    @Bean
    public dev.intent.sdk.host.IntentContextBridge intentContextBridge() {
        return cn.iocoder.yudao.module.intent.framework.bridge.HostContextBridge::capture;
    }

    /** 宿主工具注册表：自动收集容器内所有 IntentTool Bean。 */
    @Bean
    public dev.intent.sdk.tool.HostToolRegistry intentHostToolRegistry(ObjectProvider<IntentTool> toolsProvider) {
        var registry = new dev.intent.sdk.tool.HostToolRegistry();
        toolsProvider.orderedStream().forEach(tool -> {
            registry.register(tool);
            log.info("[intent] 注册宿主工具: {} - {}", tool.name(), tool.description());
        });
        return registry;
    }

    /**
     * 声明式事实规则引擎：业务模块只写「一条规则 = 查询 + 条件 + 输出模板 + 参数映射」，
     * 事实由各模块的 {@link dev.intent.sdk.host.rule.IntentFactProvider} 供给。
     *
     * <p>规则来源：classpath {@code intent-rules/*.yaml}（业务模块自带）+ 可选外部目录
     * {@code intent.rules-dir}（运维热改）。规则写错（意图不存在、参数不在入参 Schema 内）
     * 直接抛异常让服务起不来——静默不出待办是最难排查的一类故障。</p>
     */
    @Bean
    public IntentCatalogEnricher ruleBasedIntentCatalogEnricher(
            IntentSpecRegistry specRegistry,
            IntentFactRegistry factRegistry,
            IntentProperties properties) {
        var facts = factRegistry.composite();
        java.util.List<dev.intent.sdk.host.rule.IntentSuggestionRule> rules = new java.util.ArrayList<>();
        try {
            for (Resource resource : PathMatchingResourceResolverHelper.load("classpath*:intent-rules/*.yaml")) {
                try (java.io.InputStream in = resource.getInputStream()) {
                    rules.addAll(dev.intent.sdk.host.rule.IntentRuleLoader.parse(
                            new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8),
                            resource.getFilename()));
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("状态规则加载失败: " + e.getMessage(), e);
        }
        if (properties.getRulesDir() != null && !properties.getRulesDir().isBlank()) {
            rules.addAll(dev.intent.sdk.host.rule.IntentRuleLoader.loadDir(
                    java.nio.file.Path.of(properties.getRulesDir())));
        }
        if (!rules.isEmpty()) {
            dev.intent.sdk.host.rule.IntentRuleLoader.validate(rules, id -> specRegistry.get(id)
                    .map(dev.intent.sdk.host.IntentCatalogEntries::of)
                    .orElse(null));
        }
        log.info("[intent] 声明式事实规则: {} 条，事实源 {} 个", rules.size(), facts.size());
        return new dev.intent.sdk.host.rule.RuleBasedIntentCatalogEnricher(rules, facts);
    }

    /**
     * 事实源聚合：各业务模块声明 {@code IntentFactProvider} Bean，这里合成一个给规则引擎用。
     *
     * <p>用包装类型而不是直接暴露 {@code CompositeIntentFactProvider}：后者本身也是
     * {@code IntentFactProvider}，直接注册会被 ObjectProvider 再次收集成自己。</p>
     */
    @Bean
    public IntentFactRegistry intentFactRegistry(ObjectProvider<dev.intent.sdk.host.rule.IntentFactProvider> providers) {
        IntentFactRegistry registry = new IntentFactRegistry(providers.orderedStream().toList());
        log.info("[intent] 事实源 {} 个，数据集 {} 个", registry.size(), registry.composite().datasets().size());
        return registry;
    }

    /**
     * 运营在后台生成/启用的自动跟进规则：与 classpath 种子规则走同一个规则引擎，
     * 差别只在"谁来存、什么时候生效"。规则变了即时重建求值器。
     */
    @Bean
    public IntentCatalogEnricher hostRuleCatalogEnricher(
            cn.iocoder.yudao.module.intent.service.rule.IntentRuleRegistry ruleRegistry,
            IntentFactRegistry factRegistry) {
        return new cn.iocoder.yudao.module.intent.service.rule.HostRuleCatalogEnricher(
                ruleRegistry, factRegistry.composite());
    }

    /** LLM 接入配置（推理循环与规则生成共用同一份，避免两处配置漂移）。 */
    @Bean
    public dev.intent.sdk.llm.LlmConfig intentLlmConfig(IntentProperties properties) {
        LlmConfig base = buildLlm(properties.getLlm());
        return new LlmConfig(base.baseUrl(), base.api(), base.provider(), base.modelId(),
                base.modelName(), base.apiKey(), properties.getLlm().getContextWindow(),
                properties.getLlm().getMaxTokens());
    }

    /** 意图规范注册中心：classpath YAML 播种 + DB 存储与缓存。 */
    @Bean
    public IntentSpecRegistry intentSpecRegistry(
            cn.iocoder.yudao.module.intent.dal.mysql.IntentSpecMapper specMapper) {
        IntentSpecRegistry registry = new IntentSpecRegistry(specMapper);
        try {
            for (Resource resource : PathMatchingResourceResolverHelper.load("classpath*:intent/*.yaml")) {
                try (java.io.InputStream in = resource.getInputStream()) {
                    String yaml = new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                    registry.seedFromYaml(yaml, resource.getFilename());
                } catch (Exception e) {
                    log.error("[intent] 意图规范种子加载失败: {}", resource, e);
                }
            }
        } catch (Exception e) {
            log.warn("[intent] 意图规范目录扫描失败（可忽略）: {}", e.getMessage());
        }
        registry.reload();
        registry.getAll().forEach(spec -> log.info("[intent] 注册意图: {} v{} scope={} ({})",
                spec.getId(), spec.getVersion(), spec.getScope(), spec.getName()));
        return registry;
    }





    /** 执行留痕存储：JSONL 文件（可替换为 DB 实现，接口不变）。 */
    @Bean
    public dev.intent.sdk.store.TraceStore intentTraceStore(IntentProperties properties) {
        String dir = properties.getTrace().getDir();
        if (dir == null || dir.isBlank()) {
            log.info("[intent] 留痕存储: 内存");
            return new dev.intent.sdk.store.InMemoryTraceStore();
        }
        log.info("[intent] 留痕存储: JSONL @ {}", java.nio.file.Path.of(dir).toAbsolutePath());
        return new dev.intent.sdk.store.JsonlTraceStore(java.nio.file.Path.of(dir));
    }

    /** 执行器档案注册中心：classpath YAML 播种 + DB 存储与缓存 + 运行时热更新。 */
    @Bean
    public cn.iocoder.yudao.module.intent.service.executor.ExecutorProfileRegistry executorProfileRegistry(
            cn.iocoder.yudao.module.intent.dal.mysql.ExecutorProfileMapper executorMapper,
            HostToolRegistry tools,
            IntentSpecRegistry specRegistry,
            ObjectProvider<dev.intent.sdk.executor.IntentExecutor> executorsProvider) {
        // 保留 id：内置推理循环 + 宿主 Bean 执行器（档案不可占用，热更新不会误注销）
        java.util.LinkedHashSet<String> reserved = new java.util.LinkedHashSet<>();
        reserved.add(dev.intent.sdk.executor.IntentExecutor.BUILTIN_ID);
        executorsProvider.orderedStream()
                .map(dev.intent.sdk.executor.IntentExecutor::id).forEach(reserved::add);
        var registry = new cn.iocoder.yudao.module.intent.service.executor.ExecutorProfileRegistry(
                executorMapper, tools, specRegistry::getAll, reserved);
        try {
            for (Resource resource : PathMatchingResourceResolverHelper.load("classpath*:intent-executor/*.yaml")) {
                try (java.io.InputStream in = resource.getInputStream()) {
                    String yaml = new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                    registry.seedFromYaml(yaml, resource.getFilename());
                } catch (Exception e) {
                    log.error("[intent] 执行器档案种子加载失败: {}", resource, e);
                }
            }
        } catch (Exception e) {
            log.warn("[intent] 执行器档案目录扫描失败（可忽略）: {}", e.getMessage());
        }
        registry.reload();
        registry.entries().forEach(entry -> log.info("[intent] 注册执行器档案: {} type={} model={}",
                entry.profile().id(), entry.profile().type(),
                entry.profile().model() == null ? "（纯工具技能）" : entry.profile().model().modelId()));
        return registry;
    }

    /**
     * 意图运行时：通用编排（入参校验 / 执行器路由 / 结果规范化 / 执行留痕）。
     * 执行内核为可替换的 Executor SPI——宿主声明 {@link dev.intent.sdk.executor.IntentExecutor}
     * Bean（工作流 / 规则引擎 / 多智能体编排等）与执行器档案页维护的 DB 档案
     * （ExecutorProfile，agent/skill 型）并存，意图规范用 {@code executor: <id>} 指定，
     * 缺省走内置 builtin-agent 推理循环。
     */
    @Bean
    public IntentRuntime intentRuntime(IntentProperties properties, HostToolRegistry tools,
            IntentSpecRegistry specRegistry,
            cn.iocoder.yudao.module.intent.service.executor.ExecutorProfileRegistry executorRegistry,
            dev.intent.sdk.store.TraceStore traceStore,
            LlmConfig llm,
            ObjectProvider<dev.intent.sdk.executor.IntentExecutor> executorsProvider) {
        List<dev.intent.sdk.executor.IntentExecutor> customs =
                new ArrayList<>(executorsProvider.orderedStream().toList());
        customs.addAll(executorRegistry.executors());
        customs.forEach(executor -> log.info("[intent] 注册自定义执行器: {}", executor.id()));
        log.info("[intent] LLM 接入: {} / {} @ {}", properties.getLlm().getProvider(),
                llm.modelId(), llm.baseUrl());
        return new IntentRuntime(llm, tools, specRegistry::getAll, customs, null, traceStore,
                properties.getLlm().getMaxTurns(), properties.getLlm().getOutputMaxRetries());
    }

    private LlmConfig buildLlm(IntentProperties.Llm llm) {
        String key = (llm.getApiKey() == null || llm.getApiKey().isBlank())
                ? dev.pi.ai.Models.resolveApiKey(llm.getProvider())
                : llm.getApiKey();
        return switch (llm.getProvider()) {
            // deepseek 的预置配置把模型名写死成 deepseek-chat，于是 `intent.llm.model-id`
            // 在这条路径上**静默失效**——配置改了、日志也打了，但实际请求用的还是内置模型。
            // 这里把 model-id / base-url 真正应用上去，让"配置里写什么就跑什么"。
            case "deepseek" -> withOverrides(LlmConfig.deepseek(key), llm);
            case "openai" -> LlmConfig.openai(key, valueOr(llm.getModelId(), "gpt-4o-mini"));
            case "anthropic" -> LlmConfig.anthropic(key, valueOr(llm.getModelId(), "claude-sonnet-4-5"));
            default -> LlmConfig.openAiCompatible(llm.getBaseUrl(), key,
                    valueOr(llm.getModelId(), "deepseek-chat"));
        };
    }

    /**
     * 把配置里的 {@code model-id} / {@code base-url} / 窗口参数覆盖到预置 LlmConfig 上。
     *
     * <p>为什么要这么写：{@code LlmConfig.deepseek(apiKey)} 是 SDK 提供的预置，模型名与端点
     * 都是常量。而宿主配置里就有 {@code intent.llm.model-id} 与 {@code intent.llm.base-url}，
     * 不覆盖的话这两个配置项对 deepseek 用户完全是摆设——**配了不生效、也不报错**，
     * 属于最难排查的一类问题。未配置的项一律保留预置值，所以老配置行为不变。</p>
     */
    private static LlmConfig withOverrides(LlmConfig base, IntentProperties.Llm llm) {
        String modelId = valueOr(llm.getModelId(), base.modelId());
        String baseUrl = valueOr(llm.getBaseUrl(), base.baseUrl());
        long window = llm.getContextWindow() > 0 ? llm.getContextWindow() : base.contextWindow();
        int maxTokens = llm.getMaxTokens() > 0 ? llm.getMaxTokens() : base.maxTokens();
        if (modelId.equals(base.modelId()) && baseUrl.equals(base.baseUrl())
                && window == base.contextWindow() && maxTokens == base.maxTokens()) {
            return base;
        }
        return new LlmConfig(baseUrl, base.api(), base.provider(), modelId, modelId,
                base.apiKey(), window, maxTokens);
    }

    private static String valueOr(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    /**
     * 事实源聚合的宿主侧包装：只暴露 {@code composite()}，自身不是 IntentFactProvider，
     * 因此不会被再次收集（否则聚合器会把自己也聚合进去）。
     */
    public static final class IntentFactRegistry {

        private final dev.intent.sdk.host.rule.CompositeIntentFactProvider composite;

        IntentFactRegistry(java.util.List<dev.intent.sdk.host.rule.IntentFactProvider> providers) {
            this.composite = new dev.intent.sdk.host.rule.CompositeIntentFactProvider(providers);
        }

        public dev.intent.sdk.host.rule.CompositeIntentFactProvider composite() {
            return composite;
        }

        public int size() {
            return composite.size();
        }
    }

    /** classpath 模式资源加载辅助（隔离 checked exception）。 */
    private static final class PathMatchingResourceResolverHelper {

        static List<Resource> load(String pattern) {
            try {
                Resource[] resources = new PathMatchingResourcePatternResolver().getResources(pattern);
                return List.of(resources);
            } catch (Exception e) {
                return List.of();
            }
        }
    }
}
