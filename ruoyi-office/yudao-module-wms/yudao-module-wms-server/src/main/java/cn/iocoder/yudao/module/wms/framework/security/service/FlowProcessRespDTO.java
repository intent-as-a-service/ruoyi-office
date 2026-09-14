package cn.iocoder.yudao.module.wms.framework.security.service;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlowProcessRespDTO {

    /**
     * 流程实例的编号
     */
    private String processInstanceId;

    /**
     * 流程实例的 key
     */
    private String processDefinitionKey;

    /**
     * 流程实例的结果
     */
    private Integer status;

    /**
     * 流程实例对应的业务标识
     * 例如说，请假单ID、用车申请单ID
     */
    private String businessKey;

    /**
     * 事件发生时间
     */
    private LocalDateTime eventTime;

    /**
     * 租户ID（多租户支持）
     */
    private String tenantId;

    /**
     * 流程发起人ID
     */
    private String startUserId;

    /**
     * 扩展信息
     */
    private String extInfo;



}
