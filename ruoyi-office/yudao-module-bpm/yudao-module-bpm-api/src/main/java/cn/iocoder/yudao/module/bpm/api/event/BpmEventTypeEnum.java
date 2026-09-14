package cn.iocoder.yudao.module.bpm.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * BPM 事件类型枚举
 * 用于区分流程实例事件和任务事件
 *
 * @author 宇擎源码
 */
@Getter
@AllArgsConstructor
public enum BpmEventTypeEnum {

    // ========== 流程实例事件 ==========
    
    /**
     * 流程实例启动
     */
    PROCESS_INSTANCE_STARTED("process_instance_started", "流程实例启动"),

    /**
     * 流程实例完成
     */
    PROCESS_INSTANCE_COMPLETED("process_instance_completed", "流程实例完成"),

    /**
     * 流程实例取消
     */
    PROCESS_INSTANCE_CANCELLED("process_instance_cancelled", "流程实例取消"),

    /**
     * 流程实例撤回
     */
    PROCESS_INSTANCE_WITHDRAWN("process_instance_withdrawn", "流程实例撤回"),

    // ========== 任务事件 ==========
    
    /**
     * 任务创建
     */
    TASK_CREATED("task_created", "任务创建"),

    /**
     * 任务创建（重新进入开始节点）
     */
    TASK_CREATED_REENTER("task_created_reenter", "任务创建（重新进入开始节点）"),

    /**
     * 任务分配
     */
    TASK_ASSIGNED("task_assigned", "任务分配"),

    /**
     * 任务完成
     */
    TASK_COMPLETED("task_completed", "任务完成"),

    /**
     * 任务审批通过
     */
    TASK_APPROVED("task_approved", "任务审批通过"),

    /**
     * 任务审批拒绝
     */
    TASK_REJECTED("task_rejected", "任务审批拒绝"),

    /**
     * 任务转办
     */
    TASK_TRANSFERRED("task_transferred", "任务转办"),

    /**
     * 任务委派
     */
    TASK_DELEGATED("task_delegated", "任务委派"),

    /**
     * 任务退回
     */
    TASK_RETURNED("task_returned", "任务退回"),

    /**
     * 任务撤回
     */
    TASK_WITHDRAWN("task_withdrawn", "任务撤回"),

    /**
     * 任务取消
     */
    TASK_CANCELLED("task_cancelled", "任务取消");

    /**
     * 事件编码
     */
    private final String code;

    /**
     * 事件名称
     */
    private final String name;

    /**
     * 根据编码获取枚举
     */
    public static BpmEventTypeEnum getByCode(String code) {
        for (BpmEventTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断是否为流程实例事件
     */
    public boolean isProcessInstanceEvent() {
        return this.code.startsWith("process_instance_");
    }

    /**
     * 判断是否为任务事件
     */
    public boolean isTaskEvent() {
        return this.code.startsWith("task_");
    }

}
