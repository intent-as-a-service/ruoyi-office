package cn.iocoder.yudao.module.bpm.service.notification;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.bpm.api.event.BpmEventTypeEnum;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceStatusMessage;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceInfo;
import cn.iocoder.yudao.module.bpm.api.event.BpmTaskInfo;
import cn.iocoder.yudao.module.bpm.api.event.BpmNotificationTypeEnum;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * BPM 流程通知管理器
 * 统一管理各种通知方式的分发
 *
 * @author 宇擎源码
 */
@Slf4j
@Component
public class BpmNotificationManager {

    @Resource
    private List<BpmNotificationHandler> notificationHandlers;

    /**
     * 默认通知方式
     */
    @Value("${yudao.bpm.notification.default-type:local}")
    private String defaultNotificationType;

    /**
     * 是否异步处理
     */
    @Value("${yudao.bmp.notification.async:true}")
    private Boolean asyncProcess;

    /**
     * 流程特定的通知配置
     * key: processDefinitionKey, value: 通知方式
     */
    @Value("#{${yudao.bpm.notification.process-config:{}}}")
    private Map<String, String> processNotificationConfig;

    /**
     * 通知处理器映射
     */
    private Map<BpmNotificationTypeEnum, BpmNotificationHandler> handlerMap = new HashMap<>();

    @PostConstruct
    public void init() {
        // 初始化处理器映射
        for (BpmNotificationHandler handler : notificationHandlers) {
            if (handler.isEnabled()) {
                handlerMap.put(handler.getSupportType(), handler);
                log.info("[init] 注册通知处理器: {}", handler.getSupportType().getName());
            }
        }
        log.info("[init] 通知管理器初始化完成，共注册 {} 个处理器", handlerMap.size());
    }

    /**
     * 发送流程状态通知
     *
     * @param processInstance 流程实例
     * @param status         流程状态
     */
    public void sendProcessStatusNotification(ProcessInstance processInstance, Integer status) {
        if (processInstance == null) {
            log.warn("[sendProcessStatusNotification] 流程实例为空，跳过通知");
            return;
        }

        // 构建通知消息
        BpmProcessInstanceStatusMessage message = buildProcessInstanceNotificationMessage(processInstance, status);
        
        // 发送通知
        sendNotification(message, processInstance.getProcessDefinitionKey());
    }

    /**
     * 发送任务事件通知
     *
     * @param processInstance 流程实例
     * @param task           任务信息
     * @param eventType      事件类型
     * @param taskResult     任务结果
     * @param taskReason     任务原因
     */
    public void sendTaskEventNotification(ProcessInstance processInstance, Task task,
                                          BpmEventTypeEnum eventType, Integer taskResult, String taskReason) {
        if (processInstance == null || task == null) {
            log.warn("[sendTaskEventNotification] 流程实例或任务为空，跳过通知");
            return;
        }

        // 构建任务事件通知消息
        BpmProcessInstanceStatusMessage message = buildTaskEventNotificationMessage(processInstance, task, eventType, taskResult, taskReason);
        
        // 发送通知
        sendNotification(message, processInstance.getProcessDefinitionKey());
    }

    /**
     * 统一发送通知的方法
     */
    private void sendNotification(BpmProcessInstanceStatusMessage message, String processDefinitionKey) {
        // 获取通知方式
        BpmNotificationTypeEnum notificationType = getNotificationType(processDefinitionKey);
        
        // 获取对应的处理器
        BpmNotificationHandler handler = handlerMap.get(notificationType);
        if (handler == null) {
            log.warn("[sendNotification] 未找到对应的通知处理器: {}", notificationType);
            return;
        }

        // 发送通知
        if (Boolean.TRUE.equals(asyncProcess)) {
            // 异步处理
            CompletableFuture.runAsync(() -> {
                try {
                    handler.handleNotification(message);
                } catch (Exception e) {
                    log.error("[sendNotification] 异步通知处理失败", e);
                }
            });
        } else {
            // 同步处理
            try {
                handler.handleNotification(message);
            } catch (Exception e) {
                log.error("[sendNotification] 同步通知处理失败", e);
            }
        }
    }

    /**
     * 构建流程实例通知消息
     */
    private BpmProcessInstanceStatusMessage buildProcessInstanceNotificationMessage(ProcessInstance processInstance, Integer status) {
        // 根据状态确定事件类型
        BpmEventTypeEnum eventType = determineProcessInstanceEventType(status);
        
        // 构建流程实例信息
        BpmProcessInstanceInfo processInstanceInfo = BpmProcessInstanceInfo.builder()
                .processInstanceId(processInstance.getId())
                .processDefinitionKey(processInstance.getProcessDefinitionKey())
                .processDefinitionId(processInstance.getProcessDefinitionId())
                .status(status)
                .businessKey(processInstance.getBusinessKey())
                .startUserId(processInstance.getStartUserId())
                .processInstanceName(processInstance.getName())
                .tenantId(processInstance.getTenantId())
                .suspended(processInstance.isSuspended())
                .build();
        
        return BpmProcessInstanceStatusMessage.builder()
                .eventType(eventType)
                .processInstanceInfo(processInstanceInfo)
                .eventTime(LocalDateTime.now())
                .build();
    }

    /**
     * 构建任务事件通知消息
     */
    private BpmProcessInstanceStatusMessage buildTaskEventNotificationMessage(ProcessInstance processInstance, 
                                                                            Task task, 
                                                                            BpmEventTypeEnum eventType, 
                                                                            Integer taskResult, 
                                                                            String taskReason) {
        // 构建流程实例信息
        BpmProcessInstanceInfo processInstanceInfo = BpmProcessInstanceInfo.builder()
                .processInstanceId(processInstance.getId())
                .processDefinitionKey(processInstance.getProcessDefinitionKey())
                .processDefinitionId(processInstance.getProcessDefinitionId())
                .businessKey(processInstance.getBusinessKey())
                .startUserId(processInstance.getStartUserId())
                .processInstanceName(processInstance.getName())
                .tenantId(processInstance.getTenantId())
                .suspended(processInstance.isSuspended())
                .build();

        // 构建任务信息
        BpmTaskInfo taskInfo = BpmTaskInfo.builder()
                .taskId(task.getId())
                .taskDefinitionKey(task.getTaskDefinitionKey())
                .taskName(task.getName())
                .taskDescription(task.getDescription())
                .assigneeId(task.getAssignee())
                .ownerId(task.getOwner())
                .taskResult(taskResult)
                .taskReason(taskReason)
                .taskCreateTime(task.getCreateTime() != null ? 
                    task.getCreateTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null)
                .taskPriority(task.getPriority())
                .taskDueDate(task.getDueDate() != null ? 
                    task.getDueDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null)
                .taskCategory(task.getCategory())
                .taskFormKey(task.getFormKey())
                .executionId(task.getExecutionId())
                .parentTaskId(task.getParentTaskId())
                .scopeType(task.getScopeType())
                .delegationState(task.getDelegationState() != null ? task.getDelegationState().toString() : null)
                .build();
        
        return BpmProcessInstanceStatusMessage.builder()
                .eventType(eventType)
                .processInstanceInfo(processInstanceInfo)
                .taskInfo(taskInfo)
                .eventTime(LocalDateTime.now())
                .build();
    }

    /**
     * 根据流程状态确定事件类型
     */
    private BpmEventTypeEnum determineProcessInstanceEventType(Integer status) {
        // 这里需要根据实际的状态枚举来映射
        // 假设状态值：1-运行中，2-已完成，3-已取消，4-已撤回
        switch (status) {
            case 1:
                return BpmEventTypeEnum.PROCESS_INSTANCE_STARTED;
            case 2:
                return BpmEventTypeEnum.PROCESS_INSTANCE_COMPLETED;
            case 3:
                return BpmEventTypeEnum.PROCESS_INSTANCE_CANCELLED;
            case 4:
                return BpmEventTypeEnum.PROCESS_INSTANCE_WITHDRAWN;
            default:
                return BpmEventTypeEnum.PROCESS_INSTANCE_STARTED;
        }
    }

    /**
     * 获取通知方式
     */
    private BpmNotificationTypeEnum getNotificationType(String processDefinitionKey) {

        // 1. 查找流程特定配置
        if (processNotificationConfig != null) {
            String configType = processNotificationConfig.get(processDefinitionKey);
            if (StrUtil.isNotBlank(configType)) {
                return BpmNotificationTypeEnum.getByCode(configType);
            }
        }

        // 2. 使用默认配置
        if (StrUtil.isNotBlank(defaultNotificationType)) {
            return BpmNotificationTypeEnum.getByCode(defaultNotificationType);
        }

        // 3. 返回默认通知类型或null（根据业务需求）
        return null; // 或者返回一个默认的枚举值
    }

    /**
     * 获取可用的通知方式
     */
    public Map<BpmNotificationTypeEnum, BpmNotificationHandler> getAvailableHandlers() {
        return new HashMap<>(handlerMap);
    }

} 