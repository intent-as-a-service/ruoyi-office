package cn.iocoder.yudao.common.server.process.controller;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.common.server.process.FlowProcessPrefixUtils;
import cn.iocoder.yudao.framework.common.enums.BillTypeEnum;
import cn.iocoder.yudao.framework.common.enums.SystemEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.service.FlowBillService;
import cn.iocoder.yudao.framework.common.service.FlowBillServiceFactory;
import cn.iocoder.yudao.module.bpm.api.event.*;
import lombok.extern.slf4j.Slf4j;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 通用流程回调 Controller 抽象类
 * <p>
 * 提供统一的 BPM 回调处理逻辑，模块只需继承并提供：
 * <ul>
 *     <li>模块前缀：使用 {@link SystemEnum}</li>
 *     <li>流程单据服务工厂：{@link FlowBillServiceFactory}</li>
 * </ul>
 *
 * @param <T> 单据类型枚举
 * @author 宇擎
 */
@Slf4j
public abstract class AbstractFlowNotificationController<T extends BillTypeEnum> {

    /**
     * 当前模块对应的系统枚举（用于前缀过滤）
     */
    protected abstract SystemEnum getSystem();

    /**
     * 当前模块的流程服务工厂
     */
    protected abstract FlowBillServiceFactory<T> getFlowBillServiceFactory();

    /**
     * 统一接收 BPM 事件（Feign 调用）
     */
    protected CommonResult<Boolean> doHandleBpmEvent(BpmProcessInstanceStatusMessage message) {
        log.info("[doHandleBpmEvent][Feign回调] eventType={}, processInstanceId={}, processDefinitionKey={}",
                message.getEventType() != null ? message.getEventType().getName() : "unknown",
                message.getProcessInstanceId(), message.getProcessDefinitionKey());

        if (StrUtil.isBlank(message.getProcessDefinitionKey()) || message.getEventType() == null) {
            log.warn("[doHandleBpmEvent] 关键参数为空，跳过处理");
            return CommonResult.error(400, "关键参数为空");
        }

        // 按模块前缀过滤
        if (!FlowProcessPrefixUtils.match(getSystem(), message.getProcessDefinitionKey())) {
            log.debug("[doHandleBpmEvent] 非本模块流程，跳过处理: {}", message.getProcessDefinitionKey());
            return success(true);
        }

        BpmEventTypeEnum eventType = message.getEventType();
        if (eventType.isProcessInstanceEvent()) {
            return handleProcessInstanceEvent(message);
        }
        if (eventType.isTaskEvent()) {
            return handleTaskEvent(message);
        }
        log.warn("[doHandleBpmEvent] 未知事件类型: {}", eventType);
        return CommonResult.error(400, "未知事件类型");
    }

    /**
     * 兼容旧版的状态变更回调
     */
    protected CommonResult<Boolean> doHandleProcessStatusChange(BpmProcessInstanceStatusMessage message) {
        log.info("[doHandleProcessStatusChange][Feign回调] 兼容模式: {}", message);

        String processDefinitionKey = message.getProcessDefinitionKey();
        Integer status = message.getProcessInstanceInfo() != null ? message.getProcessInstanceInfo().getStatus() : null;
        String businessKey = message.getBusinessKey();

        if (StrUtil.isBlank(processDefinitionKey) || StrUtil.isBlank(businessKey) || status == null) {
            log.warn("[doHandleProcessStatusChange] 参数不完整，processDefinitionKey: {}, businessKey: {}, status: {}",
                    processDefinitionKey, businessKey, status);
            return CommonResult.error(400, "参数不完整");
        }

        if (!FlowProcessPrefixUtils.match(getSystem(), processDefinitionKey)) {
            log.debug("[doHandleProcessStatusChange] 非本模块流程，跳过处理: {}", processDefinitionKey);
            return success(true);
        }

        try {
            FlowBillService<T> flowBillService = getFlowBillServiceFactory().getServiceByProcessKey(processDefinitionKey);
            flowBillService.updateProcessStatus(businessKey, status);
            log.info("[doHandleProcessStatusChange] 流程状态更新成功，processDefinitionKey: {}, businessKey: {}, status: {}",
                    processDefinitionKey, businessKey, status);
            return success(true);
        } catch (IllegalArgumentException ex) {
            log.warn("[doHandleProcessStatusChange] 未知流程类型: {}", processDefinitionKey);
            return CommonResult.error(404, "未知的流程类型: " + processDefinitionKey);
        } catch (Exception e) {
            log.error("[doHandleProcessStatusChange] 处理失败", e);
            return CommonResult.error(500, "处理失败: " + e.getMessage());
        }
    }

    /**
     * 处理流程实例事件
     */
    protected CommonResult<Boolean> handleProcessInstanceEvent(BpmProcessInstanceStatusMessage message) {
        String processDefinitionKey = message.getProcessDefinitionKey();
        String businessKey = message.getBusinessKey();
        BpmProcessInstanceInfo processInstanceInfo = message.getProcessInstanceInfo();
        Integer status = processInstanceInfo != null ? processInstanceInfo.getStatus() : null;

        if (StrUtil.isBlank(businessKey) || status == null) {
            log.warn("[handleProcessInstanceEvent] businessKey 或 status 为空，跳过处理");
            return CommonResult.error(400, "businessKey 或 status 为空");
        }

        log.info("[handleProcessInstanceEvent] eventType: {}, processDefinitionKey: {}, businessKey: {}, status: {}",
                message.getEventType().getName(), processDefinitionKey, businessKey, status);

        try {
            FlowBillService<T> flowBillService = getFlowBillServiceFactory().getServiceByProcessKey(processDefinitionKey);
            flowBillService.updateProcessStatus(businessKey, status);
            log.info("[handleProcessInstanceEvent] 流程实例状态更新成功，processDefinitionKey: {}, businessKey: {}, status: {}",
                    processDefinitionKey, businessKey, status);
            return success(true);
        } catch (IllegalArgumentException ex) {
            log.warn("[handleProcessInstanceEvent] 未知流程类型: {}", processDefinitionKey);
            return CommonResult.error(404, "未知的流程类型: " + processDefinitionKey);
        } catch (Exception e) {
            log.error("[handleProcessInstanceEvent] 处理失败", e);
            return CommonResult.error(500, "处理失败: " + e.getMessage());
        }
    }


    /**
     * 处理任务事件
     */
    private CommonResult<Boolean> handleTaskEvent(BpmProcessInstanceStatusMessage message) {
        BpmEventTypeEnum eventType = message.getEventType();
        BpmTaskInfo taskInfo = message.getTaskInfo();

        log.info("[handleTaskEvent] 处理任务事件，eventType: {}, taskId: {}, taskName: {}, assigneeId: {}, taskResult: {}, taskReason: {}",
                eventType.getName(), taskInfo.getTaskId(), taskInfo.getTaskName(),
                taskInfo.getAssigneeId(), taskInfo.getTaskResult(), taskInfo.getTaskReason());

        // 根据不同的任务事件类型进行处理
        switch (eventType) {
            case TASK_CREATED:
                return handleTaskCreated(message);
            case TASK_APPROVED:
                return handleTaskApproved(message);
            case TASK_REJECTED:
                return handleTaskRejected(message);
            case TASK_WITHDRAWN:
                return handleTaskWithdrawn(message);
            case TASK_TRANSFERRED:
                return handleTaskTransferred(message);
            case TASK_DELEGATED:
                return handleTaskDelegated(message);
            default:
                log.debug("[handleTaskEvent] 暂不处理的任务事件类型: {}", eventType.getName());
                return success(true);
        }
    }

    /**
     * 处理任务创建事件
     */
    private CommonResult<Boolean> handleTaskCreated(BpmProcessInstanceStatusMessage message) {
        BpmEventTypeEnum eventType = message.getEventType();
        BpmTaskInfo taskInfo = message.getTaskInfo();

        log.info("[handleTaskCreated] 任务创建通知，taskId: {}, taskName: {}, assigneeId: {}",
                taskInfo.getTaskId(), taskInfo.getTaskName(), taskInfo.getAssigneeId());

        // 这里可以实现具体的业务逻辑，比如：
        // 1. 发送待办提醒
        // 2. 更新业务单据状态
        // 3. 记录操作日志等

        return success(true);
    }

    /**
     * 处理任务审批通过事件
     */
    private CommonResult<Boolean> handleTaskApproved(BpmProcessInstanceStatusMessage message) {
        BpmEventTypeEnum eventType = message.getEventType();
        BpmTaskInfo taskInfo = message.getTaskInfo();

        log.info("[handleTaskApproved] 任务审批通过，taskId: {}, assigneeId: {}, reason: {}",
                taskInfo.getTaskId(), taskInfo.getAssigneeId(), taskInfo.getTaskReason());

        // 这里可以实现具体的业务逻辑，比如：
        // 1. 更新业务单据审批记录
        // 2. 发送审批结果通知
        // 3. 触发下游业务流程等

        return success(true);
    }

    /**
     * 处理任务审批拒绝事件
     */
    private CommonResult<Boolean> handleTaskRejected(BpmProcessInstanceStatusMessage message) {
        BpmEventTypeEnum eventType = message.getEventType();
        BpmTaskInfo taskInfo = message.getTaskInfo();

        log.info("[handleTaskRejected] 任务审批拒绝，taskId: {}, assigneeId: {}, reason: {}",
                taskInfo.getTaskId(), taskInfo.getAssigneeId(), taskInfo.getTaskReason());

        // 这里可以实现具体的业务逻辑，比如：
        // 1. 更新业务单据为拒绝状态
        // 2. 发送拒绝通知给申请人
        // 3. 记录拒绝原因等

        return success(true);
    }

    /**
     * 处理任务撤回事件
     */
    private CommonResult<Boolean> handleTaskWithdrawn(BpmProcessInstanceStatusMessage message) {
        BpmEventTypeEnum eventType = message.getEventType();
        BpmTaskInfo taskInfo = message.getTaskInfo();

        log.info("[handleTaskWithdrawn] 任务撤回，taskId: {}, reason: {}",
                taskInfo.getTaskId(), taskInfo.getTaskReason());

        // 这里可以实现具体的业务逻辑，比如：
        // 1. 更新业务单据为撤回状态
        // 2. 发送撤回通知
        // 3. 清理相关数据等

        return success(true);
    }

    /**
     * 处理任务转办事件
     */
    private CommonResult<Boolean> handleTaskTransferred(BpmProcessInstanceStatusMessage message) {
        BpmEventTypeEnum eventType = message.getEventType();
        BpmTaskInfo taskInfo = message.getTaskInfo();

        log.info("[handleTaskTransferred] 任务转办，taskId: {}, assigneeId: {}",
                taskInfo.getTaskId(), taskInfo.getAssigneeId());

        // 这里可以实现具体的业务逻辑，比如：
        // 1. 发送转办通知
        // 2. 更新审批人信息等

        return success(true);
    }

    /**
     * 处理任务委派事件
     */
    private CommonResult<Boolean> handleTaskDelegated(BpmProcessInstanceStatusMessage message) {
        BpmEventTypeEnum eventType = message.getEventType();
        BpmTaskInfo taskInfo = message.getTaskInfo();

        log.info("[handleTaskDelegated] 任务委派，taskId: {}, assigneeId: {}",
                taskInfo.getTaskId(), taskInfo.getAssigneeId());

        // 这里可以实现具体的业务逻辑，比如：
        // 1. 发送委派通知
        // 2. 更新委派人信息等

        return success(true);
    }
}

