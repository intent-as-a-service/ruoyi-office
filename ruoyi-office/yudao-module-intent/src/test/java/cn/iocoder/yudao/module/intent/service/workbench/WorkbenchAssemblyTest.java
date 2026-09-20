package cn.iocoder.yudao.module.intent.service.workbench;

import cn.iocoder.yudao.module.intent.service.slot.IntentSlotRegistry;
import cn.iocoder.yudao.module.intent.service.slot.IntentSlotType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 角色装配规则的单测：<b>这里每一条都是权限相关的，不能靠肉眼</b>。
 *
 * <p>工作台"不同角色看到不同内容"的实现有三个容易写错的地方：</p>
 * <ol>
 *   <li>档案按<b>书写顺序</b>命中（不是"哪个更具体"）——顺序错了，客服会拿到主管的装配；</li>
 *   <li>视角取<b>交集</b>（收窄）而不是并集——写成并集就是提权：前端传 view=lead 就能拿到主管意图；</li>
 *   <li>档案里的 pinnedIntents 只排序、<b>不授权</b>——它不该让任何人看见本来不可见的意图。</li>
 * </ol>
 */
class WorkbenchAssemblyTest {

    // ------------------------------------------------------------ 档案命中

    @Test
    @DisplayName("档案按 YAML 书写顺序命中，兜底档案最后")
    void profileIsChosenByDeclarationOrder() {
        WorkbenchProfileRegistry registry = WorkbenchProfileRegistry.of(List.of(
                profile("manager", List.of("super_admin")),
                profile("sales", List.of("crm_admin")),
                fallback()));

        assertThat(registry.forRoles(List.of("crm_admin", "super_admin")).orElseThrow().id())
                .isEqualTo("manager"); // 顺序在前，即使本人同时有 crm_admin
        assertThat(registry.forRoles(List.of("crm_admin")).orElseThrow().id()).isEqualTo("sales");
        assertThat(registry.forRoles(List.of("common")).orElseThrow().id()).isEqualTo("default");
    }

    @Test
    @DisplayName("一个角色都没有时用兜底档案，而不是 null")
    void fallbackProfileIsUsedForUnknownRoles() {
        WorkbenchProfileRegistry registry = WorkbenchProfileRegistry.of(List.of(
                profile("manager", List.of("super_admin")), fallback()));
        assertThat(registry.forRoles(Set.of()).orElseThrow().id()).isEqualTo("default");
    }

    // ------------------------------------------------------------ 视角（收窄）

    @Test
    @DisplayName("视角取交集：主管切到客服视角后，只剩客服那个角色")
    void viewNarrowsRolesByIntersection() {
        WorkbenchProfile manager = new WorkbenchProfile("manager", "主管", "管理驾驶舱",
                List.of("super_admin"), List.of("context"), "self",
                List.of(new WorkbenchProfile.View("lead", "主管视角", List.of("super_admin")),
                        new WorkbenchProfile.View("service", "客服视角", List.of("common"))),
                List.of(), List.of(), List.of(), null, null);
        WorkbenchProfileRegistry registry = WorkbenchProfileRegistry.of(List.of(manager));

        assertThat(registry.effectiveRoles(manager, "lead", List.of("common", "super_admin")))
                .containsExactlyInAnyOrder("super_admin");
        assertThat(registry.effectiveRoles(manager, "service", List.of("common", "super_admin")))
                .containsExactlyInAnyOrder("common");
        assertThat(registry.effectiveRoles(manager, "self", List.of("common", "super_admin")))
                .containsExactlyInAnyOrder("common", "super_admin");
    }

    @Test
    @DisplayName("视角不能放大：声明角色与本人角色无交集时，退化为本人全部角色")
    void viewCannotEscalateRoles() {
        WorkbenchProfile sales = new WorkbenchProfile("sales", "销售", "销售工作台",
                List.of("crm_admin"), List.of("context"), "self",
                List.of(new WorkbenchProfile.View("lead", "主管视角", List.of("super_admin"))),
                List.of(), List.of(), List.of(), null, null);
        WorkbenchProfileRegistry registry = WorkbenchProfileRegistry.of(List.of(sales));

        // 客服传 view=lead：拿不到 super_admin，只能拿回自己的 common
        assertThat(registry.effectiveRoles(sales, "lead", List.of("common")))
                .containsExactlyInAnyOrder("common");
    }

    @Test
    @DisplayName("可为本人提供的视角：只有他能撑起来的那些（self 永远在）")
    void onlyOwnableViewsAreOffered() {
        WorkbenchProfile manager = new WorkbenchProfile("manager", "主管", "管理驾驶舱",
                List.of("super_admin"), List.of("context"), "self",
                List.of(new WorkbenchProfile.View("lead", "主管视角", List.of("super_admin")),
                        new WorkbenchProfile.View("service", "客服视角", List.of("common"))),
                List.of(), List.of(), List.of(), null, null);
        WorkbenchProfileRegistry registry = WorkbenchProfileRegistry.of(List.of(manager));

        assertThat(registry.availableViews(manager, List.of("common")).stream().map(WorkbenchProfile.View::id))
                .containsExactly("self", "service");
        assertThat(registry.availableViews(manager, List.of("super_admin", "common")).stream()
                .map(WorkbenchProfile.View::id))
                .containsExactly("self", "lead", "service");
    }

    // ------------------------------------------------------------ 槽位登记与审计

    @Test
    @DisplayName("槽位登记表：解析、按 key 反查、nonSlots 不报为漏登记")
    void slotRegistryParsesAndAudits() {
        String yaml = """
                slots:
                  - type: customer
                    key: customerId
                    title: 客户
                    multi: true
                    entity: true
                    hint: 客户级意图都挂在这一槽上
                  - type: timeWindow
                    key: timeWindow
                    title: 时间窗
                    multi: false
                    entity: false
                    values: ["近30天", "近90天"]
                nonSlots:
                  - key: page
                    reason: 页面标识，不是实体
                """;
        IntentSlotRegistry registry = IntentSlotRegistry.of(
                IntentSlotRegistry.parse(yaml, "test.yaml"), IntentSlotRegistry.parseNonSlots(yaml));

        assertThat(registry.byKey("customerId").orElseThrow().type()).isEqualTo("customer");
        assertThat(registry.byType("customer").orElseThrow().multi()).isTrue();
        assertThat(registry.byType("customer").orElseThrow().entity()).isTrue();
        assertThat(registry.byType("timeWindow").orElseThrow().entity()).isFalse();
        assertThat(registry.byType("timeWindow").orElseThrow().values()).hasSize(2);
        assertThat(registry.nonSlots()).containsExactly("page");

        // 审计：page 不算漏登记，unknownKey 算
        Set<String> unregistered = registry.audit(List.of(
                spec("crm.customer.alpha", List.of("page", "customerId")),
                spec("crm.customer.beta", List.of("unknownKey"))));
        assertThat(unregistered).containsExactly("unknownKey");
    }

    @Test
    @DisplayName("同一槽位类型被登记两次：启动即炸，不静默取第一个")
    void duplicateSlotTypeFailsFast() {
        assertThatThrownBy(() -> IntentSlotRegistry.of(List.of(
                new IntentSlotType("customer", "customerId", "客户", true, true, List.of(), null, "a.yaml"),
                new IntentSlotType("customer", "clientId", "客户", true, true, List.of(), null, "b.yaml")),
                Set.of()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("槽位类型重复登记");
    }

    // ------------------------------------------------------------ fixtures

    private static WorkbenchProfile profile(String id, List<String> roles) {
        return new WorkbenchProfile(id, id, id + " 工作台", roles, List.of("context", "today"),
                "self", List.of(), List.of("crm.customer.health-check"), List.of(), List.of(), null, null);
    }

    private static WorkbenchProfile fallback() {
        return new WorkbenchProfile("default", "通用", "意图工作台", List.of(), List.of("context"),
                "self", List.of(), List.of(), List.of(), List.of(), null, null);
    }

    private static dev.intent.protocol.IntentSpec spec(String id, List<String> contextKeys) {
        StringBuilder context = new StringBuilder();
        for (String key : contextKeys) {
            context.append("  - key: ").append(key).append("\n    title: ").append(key)
                    .append("\n    required: false\n");
        }
        return dev.intent.sdk.spec.IntentSpecLoader.parse("""
                id: %s
                name: %s
                description: 测试用
                version: 1
                scope: local
                promptTemplate: 测试
                context:
                %s""".formatted(id, id, context.toString()), true);
    }
}
