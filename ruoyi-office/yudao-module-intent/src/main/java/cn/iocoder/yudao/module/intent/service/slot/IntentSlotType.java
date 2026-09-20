package cn.iocoder.yudao.module.intent.service.slot;

import java.util.List;

/**
 * 槽位类型登记项：一条「上下文键 ↔ 实体类型」的映射。
 *
 * <p>它是意图工作台上下文栈的类型系统。没有它，工作台只知道"用户摆了 customerId=320"，
 * 不知道 320 是客户主键、该去哪个服务反查、允不允许装一个集合。</p>
 *
 * <p><b>为什么 type 与 key 要分开</b>：{@code key} 是 {@code IntentSpec.context[].key} 的取值
 * （形如 {@code customerId}），是意图规范的既有契约，不能动；{@code type} 是工作台与
 * 槽位解析 SPI 用的稳定标识（形如 {@code customer}），要与后端实体名词对齐。
 * 两者不是一个东西——同一个 key 明天可能指向别的实体，而 type 一旦上线就不能改。</p>
 *
 * <p><b>picker 是"怎么挑一个值"的声明位</b>（意图工作台四点优化设计 §4.2）。
 * 不同系统的实体选择方式完全不同：CRM 要按名称模糊搜客户，ERP 要按编码搜物料，
 * 有的系统已经有自己的实体选择弹窗，有的系统什么后端能力都没有。
 * 如果工作台把选择器写死成一种，换宿主就得改前端源码——这正是"集成难"的来源之一。
 * 所以这里把它做成**按槽位类型声明**的四选一：</p>
 * <ul>
 *   <li>{@code remote} —— 走 SDK 统一端点（宿主实现候选 SPI），能吃到服务端数据权限；</li>
 *   <li>{@code literal} —— 值就是登记表里的 {@code values}（时间窗、口径这类枚举）；</li>
 *   <li>{@code custom} —— 宿主注册一个具名选择器（复用宿主已有的实体选择组件）；</li>
 *   <li>{@code manual} —— 只能手输编号（什么能力都没有时的降级路径）。</li>
 * </ul>
 *
 * @param type       槽位类型标识（customer / contract / opportunity ...）
 * @param key        对齐的上下文键（customerId / contractId / businessId ...）
 * @param title      展示名（客户 / 合同 / 商机 ...）
 * @param multi      是否允许集合模式；只有 true 的槽位能进「批量摸底」
 * @param entity     是否指向实体（true = 槽位解析器要反查；false = 纯修饰型槽位，如时间窗）
 * @param values     修饰型槽位的候选字面值（entity=false 时使用；可为空）
 * @param hint       为什么需要它（前端"加实体"面板的解释文案）
 * @param picker     选择器种类：remote / literal / custom / manual；为空时按 entity+values 推导
 * @param pickerHint 给用户的一句说明（"按名称搜索，默认给你最近在跟的客户"）
 * @param source     登记来源（YAML 文件名，用于启动期审计定位）
 */
public record IntentSlotType(String type, String key, String title, boolean multi, boolean entity,
        List<String> values, String hint, String picker, String pickerHint, String source) {

    public static final String PICKER_REMOTE = "remote";
    public static final String PICKER_LITERAL = "literal";
    public static final String PICKER_CUSTOM = "custom";
    public static final String PICKER_MANUAL = "manual";

    public IntentSlotType {
        values = values == null ? List.of() : List.copyOf(values);
        // 没声明 picker 时推导一个：老登记表一行不用改（存量 8 类槽位因此不用动 YAML）
        picker = picker == null || picker.isBlank() ? derivePicker(entity, values) : picker.trim();
    }

    /**
     * 兼容构造器：picker 未声明的调用点（含既有测试）原样可用。
     * record 加组件会改规范构造器，所以必须补一个少两参的版本，否则按位置构造的代码全部编译失败。
     */
    public IntentSlotType(String type, String key, String title, boolean multi, boolean entity,
            List<String> values, String hint, String source) {
        this(type, key, title, multi, entity, values, hint, null, null, source);
    }

    /** 推导默认选择器：是实体 → remote；有枚举值 → literal；否则只能手输。 */
    public static String derivePicker(boolean entity, List<String> values) {
        if (entity) {
            return PICKER_REMOTE;
        }
        return values != null && !values.isEmpty() ? PICKER_LITERAL : PICKER_MANUAL;
    }
}