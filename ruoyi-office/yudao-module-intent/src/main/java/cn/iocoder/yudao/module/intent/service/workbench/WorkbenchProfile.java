package cn.iocoder.yudao.module.intent.service.workbench;

import java.util.List;

/**
 * 工作台装配档案：**同一个工作台，不同角色打开看到的东西不一样**。
 *
 * <p>为什么把"角色差异"做成一份数据（YAML）而不是 if-else：</p>
 * <ul>
 *   <li>差异不是一处，是六处——可见区块、默认视角、初始上下文栈、常用意图、作战预设、
 *       批量摸底模板。写成代码就是六个分散的 switch，加一个角色要改六处；</li>
 *   <li>它是<b>运营配置</b>，和意图上下架（intent_config）同一性质：新上一个角色
 *       不该发版。YAML 只做种子，载入后进内存注册中心，可热改（W3 落库）。</li>
 * </ul>
 *
 * <p><b>装配只决定"看到什么"，绝不决定"能不能做"</b>：档案里的 pinnedIntents 只是排序权重，
 * 真正可见性仍由 intent_config（上架 + 角色）与宿主权限判定说了算；
 * 视角（{@link View}）只能<b>收窄</b>角色，不能放大（见 {@code effectiveRoles}）。</p>
 *
 * @param id            档案标识（sales / manager / service / default）
 * @param label         角色名（前端顶栏展示，如"销售"）
 * @param title         工作台标题（如"我的销售工作台"）
 * @param roles         命中角色编码；为空 = 兜底档案（任何人都可命中）
 * @param blocks        可见区块（context / today / todos / frequent / explore / history）
 * @param defaultView   默认视角 id
 * @param views         可选视角；收窄到其中 roles 与本人角色的交集
 * @param pinnedIntents 「我常做的」置顶意图（排序权重，不授予权限）
 * @param initialSlots  初始上下文栈（打开工作台就摆好的槽位）
 * @param presets       作战预设（一键把上下文栈摆成某套槽位）
 * @param explore       批量摸底默认模板
 * @param emptyHint     空态文案（为什么这里是空的）
 */
public record WorkbenchProfile(
        String id, String label, String title,
        List<String> roles, List<String> blocks,
        String defaultView, List<View> views,
        List<String> pinnedIntents,
        List<InitialSlot> initialSlots,
        List<Preset> presets,
        Explore explore,
        String emptyHint) {

    public WorkbenchProfile {
        roles = roles == null ? List.of() : List.copyOf(roles);
        blocks = blocks == null ? List.of() : List.copyOf(blocks);
        views = views == null ? List.of() : List.copyOf(views);
        pinnedIntents = pinnedIntents == null ? List.of() : List.copyOf(pinnedIntents);
        initialSlots = initialSlots == null ? List.of() : List.copyOf(initialSlots);
        presets = presets == null ? List.of() : List.copyOf(presets);
    }

    /** 视角：id + 展示名 + 生效角色。roles 为空 = 不改变角色（"我自己"）。 */
    public record View(String id, String label, List<String> roles) {
        public View {
            roles = roles == null ? List.of() : List.copyOf(roles);
        }
    }

    /** 初始槽位：打开工作台时预先摆进上下文栈的槽位（来源固定为系统推断）。 */
    public record InitialSlot(String type, List<String> values) {
        public InitialSlot {
            values = values == null ? List.of() : List.copyOf(values);
        }
    }

    /** 作战预设：一键摆好一套槽位（值仍由用户挑，预设只给"要摆哪几类"）。 */
    public record Preset(String id, String label, String description, List<PresetSlot> slots) {
        public Preset {
            slots = slots == null ? List.of() : List.copyOf(slots);
        }
    }

    public record PresetSlot(String type, String mode) {
    }

    /** 批量摸底默认模板：对着哪一类实体、跑哪几条意图。 */
    public record Explore(String label, String slotType, List<String> intents) {
        public Explore {
            intents = intents == null ? List.of() : List.copyOf(intents);
        }
    }
}