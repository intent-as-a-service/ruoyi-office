package cn.iocoder.yudao.module.intent.service.rule;

import dev.intent.protocol.IntentBadge;
import dev.intent.protocol.IntentSuggestion;
import dev.intent.sdk.catalog.IntentCatalogContext;
import dev.intent.sdk.catalog.IntentCatalogEnricher;
import dev.intent.sdk.host.rule.IntentFactProvider;
import dev.intent.sdk.host.rule.RuleBasedIntentCatalogEnricher;

import java.util.List;
import java.util.Map;

/**
 * DB 规则目录增强器：把运营在后台启用的规则接进待办与推荐通道。
 *
 * <p>规则求值器按 {@link IntentRuleRegistry#version()} 惰性重建——规则没变时
 * 复用同一个实例（目录接口是高频调用，不能每次请求都重建求值器）。</p>
 */
public class HostRuleCatalogEnricher implements IntentCatalogEnricher {

    private final IntentRuleRegistry registry;
    private final IntentFactProvider facts;

    private volatile RuleBasedIntentCatalogEnricher delegate;
    private volatile long builtVersion = -1;

    public HostRuleCatalogEnricher(IntentRuleRegistry registry, IntentFactProvider facts) {
        this.registry = registry;
        this.facts = facts;
    }

    @Override
    public Map<String, IntentBadge> badges(IntentCatalogContext context) {
        return delegate().badges(context);
    }

    @Override
    public List<IntentSuggestion> suggestions(IntentCatalogContext context) {
        return delegate().suggestions(context);
    }

    private RuleBasedIntentCatalogEnricher delegate() {
        if (delegate == null || builtVersion != registry.version()) {
            synchronized (this) {
                if (delegate == null || builtVersion != registry.version()) {
                    delegate = new RuleBasedIntentCatalogEnricher(registry.rules(), facts);
                    builtVersion = registry.version();
                }
            }
        }
        return delegate;
    }
}
