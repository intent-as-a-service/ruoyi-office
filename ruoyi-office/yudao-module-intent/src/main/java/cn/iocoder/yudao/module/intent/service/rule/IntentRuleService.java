package cn.iocoder.yudao.module.intent.service.rule;

import cn.iocoder.yudao.module.intent.dal.dataobject.IntentRuleDO;
import cn.iocoder.yudao.module.intent.dal.mysql.IntentRuleMapper;
import cn.iocoder.yudao.module.intent.service.IntentService;
import cn.iocoder.yudao.module.intent.service.spec.IntentSpecRegistry;
import dev.intent.protocol.IntentBadge;
import dev.intent.protocol.IntentCatalogEntry;
import dev.intent.protocol.IntentSuggestion;
import dev.intent.sdk.catalog.IntentCatalogAssembler;
import dev.intent.sdk.catalog.IntentCatalogContext;
import dev.intent.sdk.host.IntentCatalogEntries;
import dev.intent.sdk.host.rule.DatasetSpec;
import dev.intent.sdk.host.rule.IntentFactProvider;
import dev.intent.sdk.host.rule.IntentRuleLoader;
import dev.intent.sdk.host.rule.IntentSuggestionRule;
import dev.intent.sdk.host.rule.RuleBasedIntentCatalogEnricher;
import dev.intent.sdk.llm.LlmConfig;
import dev.intent.sdk.pi.LlmConnector;
import dev.pi.ai.AssistantMessage;
import dev.pi.ai.AssistantMessageEventStream;
import dev.pi.ai.Context;
import dev.pi.ai.Models;
import dev.pi.ai.StopReason;
import dev.pi.ai.StreamOptions;
import dev.pi.ai.UserMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 自动跟进规则编排：<b>自然语言 → 规则 YAML → 校验 → 试算预览 → 启用</b>。
 *
 * <p>为什么要有"试算"这一步：规则是会自己跑起来的东西。写错了不会报错，
 * 只会安静地不出待办（或天天误报），这类问题在线很难排查。所以启用前必须先算一遍
 * <b>现在会命中谁</b>——用与线上完全相同的取数与装配链路算，看到的人名和数字
 * 就是上线后的效果。</p>
 *
 * <p>生成侧受强约束：只能用事实源自报的 dataset 与列、只能用已登记意图的入参，
 * 因此模型不可能"编"出一条引用不存在字段的规则。</p>
 */
@Slf4j
@Service
public class IntentRuleService {

    private final IntentRuleMapper ruleMapper;
    private final IntentRuleRegistry ruleRegistry;
    private final IntentSpecRegistry specRegistry;
    private final IntentFactProvider facts;
    private final IntentService intentService;
    private final LlmConnector llm;

    public IntentRuleService(IntentRuleMapper ruleMapper, IntentRuleRegistry ruleRegistry,
            IntentSpecRegistry specRegistry, IntentFactProvider facts,
            IntentService intentService, LlmConfig llmConfig) {
        this.ruleMapper = ruleMapper;
        this.ruleRegistry = ruleRegistry;
        this.specRegistry = specRegistry;
        this.facts = facts;
        this.intentService = intentService;
        this.llm = new LlmConnector(llmConfig);
    }

    // ------------------------------------------------------------ 查询

    public List<IntentRuleDO> getList() {
        return ruleMapper.selectList(new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<IntentRuleDO>()
                .orderByDesc(IntentRuleDO::getId));
    }

    /** 事实源自报的数据集目录（生成规则时给模型看，管理端也可用于提示）。 */
    public List<Map<String, Object>> getDatasetOptions() {
        List<Map<String, Object>> options = new ArrayList<>();
        for (DatasetSpec spec : facts.datasets()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", spec.id());
            item.put("description", spec.description());
            item.put("fields", spec.fields());
            options.add(item);
        }
        return options;
    }

    // ------------------------------------------------------------ 生成

    /** 草拟结果：YAML 原文 + 解析出的规则 + 校验问题（校验不通过时 errors 非空）。 */
    public record RuleDraft(String title, String yaml, List<String> ruleIds,
                            List<IntentSuggestionRule> rules, List<String> errors) {
    }

    /**
     * 自然语言 → 规则。校验不过会自动带着错误重试一轮——这类"格式/契约"错误
     * 模型看到具体报错后基本都能自己改对，比让人重写一遍划算。
     */
    public RuleDraft draft(String naturalLanguage, String page) {
        if (naturalLanguage == null || naturalLanguage.isBlank()) {
            throw new IllegalArgumentException("请先描述你想要什么提醒");
        }
        String system = buildSystemPrompt();
        String user = "运营的原话：" + naturalLanguage.trim()
                + "\n当前页面标识：" + (page == null || page.isBlank() ? "（未指定）" : page)
                + "\n请输出规则 YAML。";
        String yaml = complete(system, user);
        List<String> errors = validateDraft(yaml);
        if (!errors.isEmpty()) {
            log.info("[intent] 规则草拟校验未通过，带错误重试: {}", errors);
            yaml = complete(system, user + "\n\n上一次生成的 YAML 存在以下问题，请修正后重新输出：\n"
                    + String.join("\n", errors) + "\n上一次的 YAML：\n" + yaml);
            errors = validateDraft(yaml);
        }
        List<IntentSuggestionRule> rules = List.of();
        if (errors.isEmpty()) {
            rules = IntentRuleLoader.parse(yaml, "(draft)");
        }
        String title = rules.isEmpty() ? "未命名规则" : String.join("、", rules.stream()
                .map(IntentSuggestionRule::id).toList());
        return new RuleDraft(title, yaml, rules.stream().map(IntentSuggestionRule::id).toList(), rules, errors);
    }

    private List<String> validateDraft(String yaml) {
        List<String> problems = new ArrayList<>();
        if (yaml == null || yaml.isBlank()) {
            problems.add("模型没有返回内容");
            return problems;
        }
        try {
            List<IntentSuggestionRule> parsed = IntentRuleLoader.parse(yaml, "(draft)");
            IntentRuleLoader.validate(parsed, id -> specRegistry.get(id)
                    .map(IntentCatalogEntries::of).orElse(null));
            for (IntentSuggestionRule rule : parsed) {
                if (ruleMapper.selectByRuleKey(rule.id()) != null) {
                    problems.add(rule.id() + ": 规则编号已存在，请换一个 id");
                }
            }
        } catch (Exception e) {
            problems.add(e.getMessage());
        }
        return problems;
    }

    // ------------------------------------------------------------ 试算预览

    /** 试算结果：徽标 + 按规则分组的条目（与线上目录增强同一条装配链路）。 */
    public Map<String, Object> preview(String yaml, String page, Long userId) {
        List<IntentSuggestionRule> rules = IntentRuleLoader.parse(yaml, "(preview)");
        List<IntentCatalogEntry> visible = intentService.getVisibleEntries(userId);
        IntentRuleLoader.validate(rules, id -> visible.stream()
                .filter(entry -> entry.id().equals(id)).findFirst().orElse(null));
        IntentCatalogContext context = intentService.buildCatalogContext(userId, page);
        List<IntentCatalogEntry> pageEntries = intentService.getPageEntries(userId, page);
        RuleBasedIntentCatalogEnricher enricher = new RuleBasedIntentCatalogEnricher(rules, facts);
        IntentCatalogAssembler.Result result = IntentCatalogAssembler.assemble(context, pageEntries, visible,
                List.of(enricher), intentService.getProperties().getSuggestions().getMax());

        // 按规则归组：条目的 id 前缀就是规则 id（引擎生成时就带上了）
        Map<String, Map<String, Object>> groups = new LinkedHashMap<>();
        for (IntentSuggestionRule rule : rules) {
            Map<String, Object> group = new LinkedHashMap<>();
            group.put("ruleId", rule.id());
            group.put("intentId", rule.intentId());
            group.put("dataset", rule.dataset());
            group.put("count", facts.count(rule.dataset(), context));
            group.put("items", new ArrayList<Map<String, Object>>());
            groups.put(rule.id(), group);
        }
        for (IntentSuggestion suggestion : result.suggestions()) {
            for (IntentSuggestionRule rule : rules) {
                if (suggestion.id() != null && suggestion.id().startsWith(rule.id() + "-")) {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("title", suggestion.title());
                    item.put("subtitle", suggestion.subtitle());
                    item.put("reason", suggestion.reason());
                    item.put("params", suggestion.params());
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> items = (List<Map<String, Object>>) groups.get(rule.id()).get("items");
                    items.add(item);
                    break;
                }
            }
        }
        List<Map<String, Object>> badges = new ArrayList<>();
        for (IntentCatalogEntry entry : result.entries()) {
            IntentBadge badge = entry.badge();
            if (badge != null) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("intentId", entry.id());
                item.put("text", badge.text());
                item.put("level", badge.level());
                item.put("count", badge.count());
                badges.add(item);
            }
        }
        Map<String, Object> preview = new LinkedHashMap<>();
        preview.put("page", page == null ? "" : page);
        preview.put("badges", badges);
        preview.put("groups", new ArrayList<>(groups.values()));
        preview.put("itemCount", result.suggestions().size());
        return preview;
    }

    // ------------------------------------------------------------ 启用 / 停用

    public List<Long> enable(String yaml, String title, String sourceText) {
        List<IntentSuggestionRule> rules = IntentRuleLoader.parse(yaml, "(enable)");
        IntentRuleLoader.validate(rules, id -> specRegistry.get(id)
                .map(IntentCatalogEntries::of).orElse(null));
        List<Long> ids = new ArrayList<>();
        for (IntentSuggestionRule rule : rules) {
            if (ruleMapper.selectByRuleKey(rule.id()) != null) {
                throw new IllegalArgumentException("规则编号已存在: " + rule.id());
            }
            IntentRuleDO row = new IntentRuleDO();
            row.setRuleKey(rule.id());
            row.setTitle(title == null || title.isBlank() ? rule.id() : title);
            row.setStatus(IntentRuleDO.STATUS_ENABLED);
            row.setYaml(yaml);
            row.setSourceText(sourceText);
            ruleMapper.insert(row);
            ids.add(row.getId());
        }
        ruleRegistry.reload();
        intentService.evictCatalogCache();
        log.info("[intent] 自动跟进规则已启用: {} 条", ids.size());
        return ids;
    }

    public void setStatus(Long id, Integer status) {
        IntentRuleDO row = ruleMapper.selectById(id);
        if (row == null) {
            throw new IllegalArgumentException("规则不存在: " + id);
        }
        row.setStatus(status);
        ruleMapper.updateById(row);
        ruleRegistry.reload();
        intentService.evictCatalogCache();
    }

    public void delete(Long id) {
        ruleMapper.deleteById(id);
        ruleRegistry.reload();
        intentService.evictCatalogCache();
    }

    // ------------------------------------------------------------ LLM

    /**
     * 生成提示词：把"能用什么"全部摆到模型面前——数据集、列名、意图入参、
     * 算子与过滤器白名单。约束越具体，生成的规则越不需要人返工。
     */
    private String buildSystemPrompt() {
        StringBuilder sb = new StringBuilder();
        sb.append("你是「声明式事实规则」生成器：把运营的一句自然语言需求，翻译成一条规则引擎可执行的 YAML 规则。\n")
                .append("一条规则 = 查询(dataset) + 条件(filter) + 输出(badge/items) + 参数映射(params)。\n\n")
                .append("硬性约束（违反即失败）：\n")
                .append("1. dataset 只能从【可用数据集】里选，不能发明。\n")
                .append("2. 条件与模板引用列只能写 row.<列名>，列名必须在该数据集的【可用列】里。\n")
                .append("3. 模板可用的上下文只有：ctx.userId / ctx.userName / ctx.tenantId / ctx.page / ")
                .append("ctx.objectType / ctx.objectId / ctx.objectName / ctx.today / ctx.now / ctx.count / matched。\n")
                .append("4. 算子只能用：eq / ne / gt / gte / lt / lte / in / contains / isNull / notNull；")
                .append("取值可用字面量、row.xxx、ctx.xxx、now()、today()、daysUntil(row.xxx)。\n")
                .append("5. intent 只能从【可用意图】里选；items.params 的 key 必须是该意图声明过的入参；")
                .append("意图的必填参数必须由 params 映射出来，或由上下文提供。\n")
                .append("6. 模板过滤器只能用：money（千分位金额）、date（yyyy-MM-dd）、days（整数天）、expiryText（到期文案）。\n")
                .append("7. items.reason 必须写清“为什么现在要提醒”，一句话；items.title 要带具体对象名。\n")
                .append("8. scope.page 是页面前缀白名单，支持 crm/* 通配；空字符串表示意图中心。\n")
                .append("9. 只输出 YAML（可含多条规则，顶层是列表），不要任何解释、不要 Markdown 代码围栏。\n\n")
                .append("10. badge 的 {{count}} 是数据集总数（不随 filter 变化），{{matched}} 是本规则 filter 之后的命中数；badge 文案里写了过滤条件时，必须用 {{matched}}，否则会出现「写着 60 天、实际一条都没命中」的假提醒。\n")
                .append("【可用数据集】\n");
        for (DatasetSpec spec : facts.datasets()) {
            sb.append(spec.describe()).append('\n');
        }
        sb.append("\n【可用意图】\n");
        for (IntentCatalogEntry entry : intentService.getVisibleEntries(
                cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId())) {
            sb.append("- ").append(entry.id()).append("（").append(entry.name()).append("）：")
                    .append(entry.description() == null ? "" : entry.description()).append('\n');
            Map<String, Object> schema = entry.paramsSchema() == null ? Map.of() : entry.paramsSchema();
            Object properties = schema.get("properties");
            if (properties instanceof Map<?, ?> map && !map.isEmpty()) {
                List<String> declared = new ArrayList<>();
                for (Map.Entry<?, ?> property : map.entrySet()) {
                    boolean required = schema.get("required") instanceof List<?> list
                            && list.contains(String.valueOf(property.getKey()));
                    declared.add(property.getKey() + (required ? "(必填)" : ""));
                }
                sb.append("  入参：").append(String.join("、", declared)).append('\n');
            }
        }
        sb.append("\n【参考示例】\n")
                .append("- id: crm.rule.overdue-60\n")
                .append("  intent: crm.receivable.overdue-warning\n")
                .append("  dataset: crm.overdue-receivable-plans\n")
                .append("  scope:\n")
                .append("    page: [\"crm/receivable\", \"crm/backlog\", \"\"]\n")
                .append("  filter:\n")
                .append("    all:\n")
                .append("      - { op: gte, left: row.overdueDays, right: 60 }\n")
                .append("  badge:\n")
                .append("    text: \"{{matched}} 笔回款已逾期 60 天以上\"\n")
                .append("    level: danger\n")
                .append("  items:\n")
                .append("    limit: 5\n")
                .append("    title: \"第 {{row.period}} 期回款已逾期 {{row.overdueDays | days}} 天\"\n")
                .append("    subtitle: \"应收 {{row.price | money}} 元\"\n")
                .append("    reason: \"逾期超过 60 天，需要立刻升级催收动作\"\n")
                .append("    params:\n")
                .append("      customerId: { from: row.customerId, type: string }\n");
        return sb.toString();
    }

    /** 单轮文本生成：不挂工具、不带历史——生成规则不需要多轮推理，越简单越可控。 */
    private String complete(String system, String user) {
        StreamOptions options = new StreamOptions()
                .apiKey(llm.config().apiKey())
                .timeoutMs(120_000L);
        AssistantMessageEventStream stream = Models.streamSimple(llm.model(),
                new Context(system, List.of(UserMessage.of(user)), List.of()), options);
        AssistantMessage message;
        try {
            message = stream.result().get(150, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new IllegalStateException("模型调用失败: " + e.getMessage(), e);
        }
        if (message.stopReason == StopReason.ERROR || message.stopReason == StopReason.ABORTED) {
            throw new IllegalStateException("模型调用失败: " + message.errorMessage);
        }
        return stripFences(message.text());
    }

    /** 模型偶尔仍会套 Markdown 围栏：这里剥掉，否则解析一定失败。 */
    private static String stripFences(String text) {
        if (text == null) {
            return "";
        }
        String trimmed = text.strip();
        if (trimmed.startsWith("```")) {
            int firstLine = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");
            if (firstLine > 0 && lastFence > firstLine) {
                trimmed = trimmed.substring(firstLine + 1, lastFence);
            }
        }
        return trimmed.strip();
    }
}
