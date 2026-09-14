package cn.iocoder.yudao.module.bpm.api.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * BPM 事件统一消息
 * 支持流程实例事件和任务事件的统一通知
 * 支持 ApplicationEvent、MQ、Feign 等多种通知方式
 *
 * @author 宇擎源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmProcessInstanceStatusMessage {

    /**
     * 事件类型
     */
    @NotNull(message = "事件类型不能为空")
    private BpmEventTypeEnum eventType;

    /**
     * 事件发生时间
     */
    private LocalDateTime eventTime;

    /**
     * 流程实例信息（所有事件都包含）
     */
    @NotNull(message = "流程实例信息不能为空")
    private BpmProcessInstanceInfo processInstanceInfo;

    /**
     * 任务信息（仅任务事件包含）
     */
    private BpmTaskInfo taskInfo;

    /**
     * 扩展信息
     */
    private String extInfo;

    /**
     * 扩展属性（用于传递更多自定义数据）
     */
    private Map<String, Object> extData;

    // ========== 便捷方法（向后兼容） ==========

    /**
     * 获取流程实例ID
     */
    public String getProcessInstanceId() {
        return processInstanceInfo != null ? processInstanceInfo.getProcessInstanceId() : null;
    }

    /**
     * 获取流程定义Key
     */
    public String getProcessDefinitionKey() {
        return processInstanceInfo != null ? processInstanceInfo.getProcessDefinitionKey() : null;
    }

    /**
     * 获取业务Key
     */
    public String getBusinessKey() {
        return processInstanceInfo != null ? processInstanceInfo.getBusinessKey() : null;
    }


} 