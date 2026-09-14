package cn.iocoder.yudao.module.bpm.api.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * BPM 流程实例信息
 * 包含流程实例相关的所有字段
 *
 * @author 宇擎源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmProcessInstanceInfo {

    /**
     * 流程实例的编号
     */
    @NotNull(message = "流程实例的编号不能为空")
    private String processInstanceId;

    /**
     * 流程实例的 key
     */
    @NotNull(message = "流程实例的 key 不能为空")
    private String processDefinitionKey;

    /**
     * 流程定义ID
     */
    private String processDefinitionId;

    /**
     * 流程实例的状态
     */
    private Integer status;

    /**
     * 流程实例对应的业务标识
     * 例如说，请假单ID、用车申请单ID
     */
    private String businessKey;

    /**
     * 流程发起人ID
     */
    private String startUserId;

    /**
     * 流程实例名称
     */
    private String processInstanceName;

    /**
     * 租户ID（多租户支持）
     */
    private String tenantId;

    /**
     * 流程实例结束原因
     */
    private String reason;

    /**
     * 流程实例是否挂起
     */
    private Boolean suspended;

}
