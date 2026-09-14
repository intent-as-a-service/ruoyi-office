package cn.iocoder.yudao.module.intent.service.rule;

import cn.iocoder.yudao.module.intent.dal.dataobject.IntentRuleDO;
import cn.iocoder.yudao.module.intent.dal.mysql.IntentRuleMapper;
import cn.iocoder.yudao.module.intent.service.spec.IntentSpecRegistry;
import dev.intent.sdk.host.IntentCatalogEntries;
import dev.intent.sdk.host.rule.IntentRuleLoader;
import dev.intent.sdk.host.rule.IntentSuggestionRule;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 已启用规则的中心：DB 规则 → 规则对象的唯一加载点。
 *
 * <p>与 classpath 种子规则的区别只是"谁来存"：这里存 DB，运营在后台改完即时生效。
 * 加载即校验（自校验 + 对意图目录校验），校验不过的规则被跳过并记日志——
 * 一条写错的规则不应该让整个待办面板消失。</p>
 *
 * <p>{@code version} 是给目录增强器用的失效信号：版本没变就不重建求值器。</p>
 */
@Slf4j
@Service
public class IntentRuleRegistry {

    private final IntentRuleMapper ruleMapper;
    private final IntentSpecRegistry specRegistry;

    private volatile List<IntentSuggestionRule> rules = List.of();
    private volatile long version = 0;

    public IntentRuleRegistry(IntentRuleMapper ruleMapper, IntentSpecRegistry specRegistry) {
        this.ruleMapper = ruleMapper;
        this.specRegistry = specRegistry;
    }

    @PostConstruct
    public void init() {
        reload();
    }

    /** 重新加载已启用规则（启用 / 停用 / 删除后调用）。 */
    public synchronized void reload() {
        List<IntentSuggestionRule> loaded = new ArrayList<>();
        for (IntentRuleDO row : ruleMapper.selectListByStatus(IntentRuleDO.STATUS_ENABLED)) {
            try {
                List<IntentSuggestionRule> parsed = IntentRuleLoader.parse(row.getYaml(),
                        "intent_rule#" + row.getId());
                IntentRuleLoader.validate(parsed, id -> specRegistry.get(id)
                        .map(IntentCatalogEntries::of).orElse(null));
                loaded.addAll(parsed);
            } catch (Exception e) {
                log.error("[intent] 规则加载失败已跳过: {} -> {}", row.getRuleKey(), e.getMessage());
            }
        }
        this.rules = List.copyOf(loaded);
        this.version++;
        log.info("[intent] 自动跟进规则加载: {} 条（v{}）", rules.size(), version);
    }

    public List<IntentSuggestionRule> rules() {
        return rules;
    }

    public long version() {
        return version;
    }
}
