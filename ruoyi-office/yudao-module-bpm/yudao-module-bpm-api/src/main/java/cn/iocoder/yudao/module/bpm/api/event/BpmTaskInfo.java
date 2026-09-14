package cn.iocoder.yudao.module.bpm.api.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * BPM 任务信息
 * 包含任务相关的所有字段
 *
 * @author 宇擎源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmTaskInfo {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务定义Key
     */
    private String taskDefinitionKey;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务描述
     */
    private String taskDescription;

    /**
     * 任务状态
     */
    private Integer taskStatus;

    /**
     * 任务审批人ID
     */
    private String assigneeId;

    /**
     * 任务拥有者ID
     */
    private String ownerId;

    /**
     * 任务审批结果
     * 1-通过，2-拒绝，3-转办，4-委派，5-撤回等
     */
    private Integer taskResult;

    /**
     * 任务审批意见
     */
    private String taskReason;

    /**
     * 任务创建时间
     */
    private LocalDateTime taskCreateTime;

    /**
     * 任务结束时间
     */
    private LocalDateTime taskEndTime;

    /**
     * 任务持续时间（毫秒）
     */
    private Long taskDurationInMillis;

    /**
     * 任务优先级
     */
    private Integer taskPriority;

    /**
     * 任务到期时间
     */
    private LocalDateTime taskDueDate;

    /**
     * 任务跟进时间
     */
    private LocalDateTime taskFollowUpDate;

    /**
     * 任务分类
     */
    private String taskCategory;

    /**
     * 任务表单Key
     */
    private String taskFormKey;

    /**
     * 任务执行ID
     */
    private String executionId;

    /**
     * 父任务ID（用于加签等场景）
     */
    private String parentTaskId;

    /**
     * 任务范围类型（用于加签等场景）
     */
    private String scopeType;

    /**
     * 任务委派状态
     */
    private String delegationState;

}
