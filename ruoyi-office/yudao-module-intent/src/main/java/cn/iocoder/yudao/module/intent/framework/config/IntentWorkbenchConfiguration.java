package cn.iocoder.yudao.module.intent.framework.config;

import cn.iocoder.yudao.module.intent.service.executor.ExecutorProfileRegistry;
import cn.iocoder.yudao.module.intent.service.slot.IntentSlotRegistry;
import cn.iocoder.yudao.module.intent.service.slot.IntentSlotType;
import cn.iocoder.yudao.module.intent.service.spec.IntentSpecRegistry;
import cn.iocoder.yudao.module.intent.service.workbench.WorkbenchProfile;
import cn.iocoder.yudao.module.intent.service.workbench.WorkbenchProfileRegistry;
import dev.intent.sdk.executor.IntentExecutor;
import dev.intent.sdk.host.IntentExecutorTypeResolver;
import dev.intent.sdk.host.IntentSlotCandidateProvider;
import dev.intent.sdk.host.IntentSlotResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 意图工作台装配（平台侧）。
 *
 * <p>四件事，都是"缺了它工作台就只剩一个搜索框"级别的：</p>
 * <ol>
 *   <li>{@link IntentSlotRegistry} 槽位类型登记（上下文栈的类型系统）+ 启动期审计；</li>
 *   <li>{@link WorkbenchProfileRegistry} 角色装配档案（不同角色看到的工作台不一样）；</li>
 *   <li>{@link IntentSlotCandidateProvider.Registry} 候选实体聚合（「＋ 加实体」下拉）；</li>
 *   <li>{@link IntentExecutorTypeResolver} 执行器类型解析（这一跑是 13ms 的 skill 还是 20s 的 agent）。</li>
 * </ol>
 *
 * <p><b>两个 SPI 都有降级基线</b>：没人实现 {@link IntentSlotResolver} 就用 {@code none()}
 * （跳过失效校验），没人实现候选提供者就拿空聚合器（退回手输编号）。
 * 工作台是"能力可组装"的，缺一件不该整体不可用——但缺哪件必须能从接口里看出来
 * （{@code /intent/me} 的 {@code hasCandidates} 就是这个作用）。</p>
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class IntentWorkbenchConfiguration {

    /** 槽位类型登记：classpath*:intent-slots/*.yaml（业务模块自带），加载即审计。 */
    @Bean
    public IntentSlotRegistry intentSlotRegistry(IntentSpecRegistry specRegistry) {
        List<IntentSlotType> types = new ArrayList<>();
        Set<String> nonSlots = new LinkedHashSet<>();
        String groupFallbackTitle = null;
        for (Resource resource : load("classpath*:intent-slots/*.yaml")) {
            try (InputStream in = resource.getInputStream()) {
                String yaml = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                types.addAll(IntentSlotRegistry.parse(yaml, resource.getFilename()));
                nonSlots.addAll(IntentSlotRegistry.parseNonSlots(yaml));
                if (groupFallbackTitle == null) {
                    groupFallbackTitle = IntentSlotRegistry.parseGroupFallbackTitle(yaml);
                }
            } catch (Exception e) {
                // 登记表坏掉不该让系统起不来：打出来，然后按"这一类槽位不存在"继续
                log.error("[intent] 槽位登记表加载失败: {}", resource.getFilename(), e);
            }
        }
        IntentSlotRegistry registry = IntentSlotRegistry.of(types, nonSlots, groupFallbackTitle);
        log.info("[intent] 意图分组：{} 个实体组（{}），无实体组名前缀「{}」",
                types.stream().filter(IntentSlotType::entity).count(),
                types.stream().filter(IntentSlotType::entity).map(IntentSlotType::title).toList(),
                groupFallbackTitle == null ? "(未声明，前端退化为「其它」)" : groupFallbackTitle);
        registry.audit(specRegistry.getAll());
        return registry;
    }

    /** 工作台角色装配档案：classpath*:intent-profiles/*.yaml。 */
    @Bean
    public WorkbenchProfileRegistry workbenchProfileRegistry() {
        List<WorkbenchProfile> profiles = new ArrayList<>();
        for (Resource resource : load("classpath*:intent-profiles/*.yaml")) {
            try (InputStream in = resource.getInputStream()) {
                String yaml = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                profiles.addAll(WorkbenchProfileRegistry.parse(yaml));
            } catch (Exception e) {
                throw new IllegalStateException("[intent] 工作台档案加载失败: "
                        + resource.getFilename() + " - " + e.getMessage(), e);
            }
        }
        WorkbenchProfileRegistry registry = WorkbenchProfileRegistry.of(profiles);
        registry.all().forEach(profile -> log.info("[intent] 工作台档案: {} ({}) roles={} blocks={}",
                profile.id(), profile.label(), profile.roles(), profile.blocks()));
        return registry;
    }

    /** 候选实体聚合：容器里所有 {@link IntentSlotCandidateProvider} 按类型索引。 */
    @Bean
    public IntentSlotCandidateProvider.Registry intentSlotCandidateRegistry(
            ObjectProvider<IntentSlotCandidateProvider> providers) {
        List<IntentSlotCandidateProvider> list = providers.orderedStream().toList();
        var registry = new IntentSlotCandidateProvider.Registry(list);
        log.info("[intent] 槽位候选提供者: {} 个，覆盖类型 {}", list.size(), registry.types());
        return registry;
    }

    /**
     * 执行器类型解析：档案 type 优先，宿主 Bean 执行器次之，未知返回 null（前端按"未知"显示）。
     *
     * <p>刻意<b>不编造</b>类型：编一个会直接导致错误的成本预估，而 null 只是少一个提示。</p>
     */
    @Bean
    @ConditionalOnMissingBean(IntentExecutorTypeResolver.class)
    public IntentExecutorTypeResolver intentExecutorTypeResolver(
            ObjectProvider<IntentExecutor> executorsProvider,
            ExecutorProfileRegistry profileRegistry) {
        Map<String, String> types = new LinkedHashMap<>();
        types.put(IntentExecutor.BUILTIN_ID, "agent");
        executorsProvider.orderedStream().forEach(executor -> {
            if (executor.type() != null && !executor.type().isBlank()) {
                types.put(executor.id(), executor.type());
            }
        });
        return executorId -> {
            String known = types.get(executorId);
            if (known != null) {
                return known;
            }
            return profileRegistry.get(executorId)
                    .map(entry -> entry.profile().type())
                    .orElse(null);
        };
    }

    /** 槽位解析的降级基线：没装实现的宿主不报错、不阻塞，前端跳过失效校验。 */
    @Bean
    @ConditionalOnMissingBean(IntentSlotResolver.class)
    public IntentSlotResolver intentSlotResolver() {
        log.info("[intent] 未发现槽位解析实现，上下文栈跳过实体失效校验（降级基线）");
        return IntentSlotResolver.none();
    }

    private static List<Resource> load(String pattern) {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(pattern);
            return List.of(resources);
        } catch (Exception e) {
            return List.of();
        }
    }
}
