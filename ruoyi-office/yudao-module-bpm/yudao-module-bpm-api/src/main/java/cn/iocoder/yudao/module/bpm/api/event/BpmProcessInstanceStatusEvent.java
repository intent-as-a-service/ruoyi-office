package cn.iocoder.yudao.module.bpm.api.event;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 流程实例的状态（结果）发生变化的 Event
 *
 * @author 宇擎源码
 */
@SuppressWarnings("ALL")
@Data
public class BpmProcessInstanceStatusEvent extends ApplicationEvent {

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


    public BpmProcessInstanceStatusEvent() {
        // new Object() 保证非空
        super(new Object());
    }

    public BpmProcessInstanceStatusEvent(Object source) {
        super(source);
    }

}
