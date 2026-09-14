package cn.iocoder.yudao.common.server.process.mq;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.common.server.process.FlowProcessPrefixUtils;
import cn.iocoder.yudao.framework.common.enums.BillTypeEnum;
import cn.iocoder.yudao.framework.common.enums.SystemEnum;
import cn.iocoder.yudao.framework.common.service.FlowBillService;
import cn.iocoder.yudao.framework.common.service.FlowBillServiceFactory;
import cn.iocoder.yudao.framework.mq.redis.core.stream.AbstractRedisStreamMessageListener;
import cn.iocoder.yudao.module.bpm.api.event.BpmEventTypeEnum;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceInfo;
import cn.iocoder.yudao.module.bpm.api.event.BpmTaskInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * 通用流程 MQ 消息消费者抽象类
 * <p>
 * 提供统一的 BPM MQ 消息处理逻辑，模块只需继承并提供：
 * <ul>
 *     <li>模块前缀：使用 {@link SystemEnum}</li>
 *     <li>流程单据服务工厂：{@link FlowBillServiceFactory}</li>
 * </ul>
 *
 * @param <T> 单据类型枚举
 * @author 宇擎源码
 */
@Slf4j
public abstract class AbstractFlowMqNotificationConsumer<T extends BillTypeEnum>
        extends AbstractRedisStreamMessageListener<BpmProcessInstanceStatusStreamMessage> {

    /**
     * 当前模块对应的系统枚举（用于前缀过滤）
     */
    protected abstract SystemEnum getSystem();

    /**
     * 当前模块的流程服务工厂
     */
    protected abstract FlowBillServiceFactory<T> getFlowBillServiceFactory();

    @Override
    public void onMessage(BpmProcessInstanceStatusStreamMessage message) {
        log.info("[onMessage][MQ消费] 收到BPM事件消息: eventType={}, processInstanceId={}, processDefinitionKey={}",
                message.getEventType() != null ? message.getEventType().getName() : "unknown",
                message.getProcessInstanceId(), message.getProcessDefinitionKey());

        try {
            // 参数校验
            if (StrUtil.isBlank(message.getProcessDefinitionKey()) || message.getEventType() == null) {
                log.warn("[onMessage] 关键参数为空，跳过处理");
                return;
            }

            // 只处理本模块流程
            if (!FlowProcessPrefixUtils.match(getSystem(), message.getProcessDefinitionKey())) {
                log.debug("[onMessage] 非本模块流程，跳过处理: {}", message.getProcessDefinitionKey());
                return;
            }

            // 根据事件类型进行不同处理
            if (message.getEventType().isProcessInstanceEvent()) {
                handleProcessInstanceEvent(message);
            } else if (message.getEventType().isTaskEvent()) {
                handleTaskEvent(message);
            }

        } catch (Exception e) {
            log.error("[onMessage][MQ消费] 处理BPM事件消息失败", e);
            // 重新抛出异常，触发重试机制
            throw e;
        }
    }

    /**
     * 处理流程实例事件
     */
    protected void handleProcessInstanceEvent(BpmProcessInstanceStatusStreamMessage message) {
        BpmProcessInstanceInfo processInstanceInfo = message.getProcessInstanceInfo();
        String processDefinitionKey = processInstanceInfo.getProcessDefinitionKey();
        String businessKey = processInstanceInfo.getBusinessKey();
        Integer status = processInstanceInfo.getStatus();

        if (StrUtil.isBlank(businessKey) || status == null) {
            log.warn("[handleProcessInstanceEvent] businessKey或status为空，跳过处理");
            return;
        }

        log.info("[handleProcessInstanceEvent] 处理流程实例事件，eventType: {}, processDefinitionKey: {}, businessKey: {}, status: {}",
                message.getEventType().getName(), processDefinitionKey, businessKey, status);

        try {
            // 通过工厂获取对应的服务实现
            FlowBillService<T> flowBillService = getFlowBillServiceFactory().getServiceByProcessKey(processDefinitionKey);

            // 统一调用接口方法更新流程状态
            flowBillService.updateProcessStatus(businessKey, status);

            log.info("[handleProcessInstanceEvent] 流程实例状态更新成功，eventType: {}, processDefinitionKey: {}, businessKey: {}, status: {}",
                    message.getEventType().getName(), processDefinitionKey, businessKey, status);

        } catch (IllegalArgumentException e) {
            log.debug("[handleProcessInstanceEvent] 未知的流程类型: {}", processDefinitionKey);
        } catch (Exception e) {
            log.error("[handleProcessInstanceEvent] 处理流程实例事件失败", e);
            // 重新抛出异常，触发重试机制
            throw e;
        }
    }

    /**
     * 处理任务事件（默认仅日志，子类可重写）
     */
    protected void handleTaskEvent(BpmProcessInstanceStatusStreamMessage message) {
        BpmEventTypeEnum eventType = message.getEventType();
        BpmProcessInstanceInfo processInstanceInfo = message.getProcessInstanceInfo();
        BpmTaskInfo taskInfo = message.getTaskInfo();
        String processDefinitionKey = processInstanceInfo.getProcessDefinitionKey();

        log.info("[handleTaskEvent] 处理任务事件，eventType: {}, processDefinitionKey: {}, taskId: {}, taskName: {}, assigneeId: {}, taskResult: {}, taskReason: {}",
                eventType.getName(), processDefinitionKey,
                taskInfo != null ? taskInfo.getTaskId() : null,
                taskInfo != null ? taskInfo.getTaskName() : null,
                taskInfo != null ? taskInfo.getAssigneeId() : null,
                taskInfo != null ? taskInfo.getTaskResult() : null,
                taskInfo != null ? taskInfo.getTaskReason() : null);

        // 根据不同的任务事件类型进行处理
        switch (eventType) {
            case TASK_CREATED:
                handleTaskCreated(message);
                break;
            case TASK_APPROVED:
                handleTaskApproved(message);
                break;
            case TASK_REJECTED:
                handleTaskRejected(message);
                break;
            case TASK_WITHDRAWN:
                handleTaskWithdrawn(message);
                break;
            case TASK_TRANSFERRED:
                handleTaskTransferred(message);
                break;
            case TASK_DELEGATED:
                handleTaskDelegated(message);
                break;
            default:
                log.debug("[handleTaskEvent] 暂不处理的任务事件类型: {}", eventType.getName());
                break;
        }
    }

    /**
     * 处理任务创建事件（默认仅日志，子类可重写）
     */
    protected void handleTaskCreated(BpmProcessInstanceStatusStreamMessage message) {
        BpmTaskInfo taskInfo = message.getTaskInfo();
        log.info("[handleTaskCreated] 任务创建通知，taskId: {}, taskName: {}, assigneeId: {}",
                taskInfo != null ? taskInfo.getTaskId() : null,
                taskInfo != null ? taskInfo.getTaskName() : null,
                taskInfo != null ? taskInfo.getAssigneeId() : null);
    }

    /**
     * 处理任务审批通过事件（默认仅日志，子类可重写）
     */
    protected void handleTaskApproved(BpmProcessInstanceStatusStreamMessage message) {
        BpmTaskInfo taskInfo = message.getTaskInfo();
        log.info("[handleTaskApproved] 任务审批通过，taskId: {}, assigneeId: {}, reason: {}",
                taskInfo != null ? taskInfo.getTaskId() : null,
                taskInfo != null ? taskInfo.getAssigneeId() : null,
                taskInfo != null ? taskInfo.getTaskReason() : null);
    }

    /**
     * 处理任务审批拒绝事件（默认仅日志，子类可重写）
     */
    protected void handleTaskRejected(BpmProcessInstanceStatusStreamMessage message) {
        BpmTaskInfo taskInfo = message.getTaskInfo();
        log.info("[handleTaskRejected] 任务审批拒绝，taskId: {}, assigneeId: {}, reason: {}",
                taskInfo != null ? taskInfo.getTaskId() : null,
                taskInfo != null ? taskInfo.getAssigneeId() : null,
                taskInfo != null ? taskInfo.getTaskReason() : null);
    }

    /**
     * 处理任务撤回事件（默认仅日志，子类可重写）
     */
    protected void handleTaskWithdrawn(BpmProcessInstanceStatusStreamMessage message) {
        BpmTaskInfo taskInfo = message.getTaskInfo();
        log.info("[handleTaskWithdrawn] 任务撤回，taskId: {}, reason: {}",
                taskInfo != null ? taskInfo.getTaskId() : null,
                taskInfo != null ? taskInfo.getTaskReason() : null);
    }

    /**
     * 处理任务转办事件（默认仅日志，子类可重写）
     */
    protected void handleTaskTransferred(BpmProcessInstanceStatusStreamMessage message) {
        BpmTaskInfo taskInfo = message.getTaskInfo();
        log.info("[handleTaskTransferred] 任务转办，taskId: {}, assigneeId: {}",
                taskInfo != null ? taskInfo.getTaskId() : null,
                taskInfo != null ? taskInfo.getAssigneeId() : null);
    }

    /**
     * 处理任务委派事件（默认仅日志，子类可重写）
     */
    protected void handleTaskDelegated(BpmProcessInstanceStatusStreamMessage message) {
        BpmTaskInfo taskInfo = message.getTaskInfo();
        log.info("[handleTaskDelegated] 任务委派，taskId: {}, assigneeId: {}",
                taskInfo != null ? taskInfo.getTaskId() : null,
                taskInfo != null ? taskInfo.getAssigneeId() : null);
    }
}

