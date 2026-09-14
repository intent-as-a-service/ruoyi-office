package cn.iocoder.yudao.common.server.process.mq;

import cn.iocoder.yudao.framework.mq.redis.core.stream.AbstractRedisStreamMessage;
import cn.iocoder.yudao.module.bpm.api.event.BpmEventTypeEnum;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceInfo;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceStatusMessage;
import cn.iocoder.yudao.module.bpm.api.event.BpmTaskInfo;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * BPM 流程实例状态消息（Redis Stream 版本）
 * <p>
 * 用于 MQ 消息传递，继承 {@link AbstractRedisStreamMessage} 以支持 Redis Stream
 *
 * @author 宇擎源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmProcessInstanceStatusStreamMessage extends AbstractRedisStreamMessage {

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

    /**
     * 转换为标准的 BpmProcessInstanceStatusMessage
     */
    public BpmProcessInstanceStatusMessage toStandardMessage() {
        return BpmProcessInstanceStatusMessage.builder()
                .eventType(this.eventType)
                .eventTime(this.eventTime)
                .processInstanceInfo(this.processInstanceInfo)
                .taskInfo(this.taskInfo)
                .extInfo(this.extInfo)
                .extData(this.extData)
                .build();
    }

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

