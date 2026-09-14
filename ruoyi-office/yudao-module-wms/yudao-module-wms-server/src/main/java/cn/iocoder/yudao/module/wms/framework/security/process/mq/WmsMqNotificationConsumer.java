package cn.iocoder.yudao.module.wms.framework.security.process.mq;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mq.redis.core.stream.AbstractRedisStreamMessageListener;
import cn.iocoder.yudao.module.wms.framework.security.enums.FlowCodeEnum;
import cn.iocoder.yudao.module.wms.framework.security.service.FlowProcessRespDTO;
import cn.iocoder.yudao.module.wms.framework.security.service.IBillBizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * WMS 流程状态通知消息消费者
 * 监听来自BPM服务的流程状态变化消息
 *
 * @author 宇擎源码
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "yudao.bpm.notification.mq.enabled", havingValue = "true", matchIfMissing = false)
public class WmsMqNotificationConsumer extends AbstractRedisStreamMessageListener<WmsProcessInstanceStatusMessage> {


    private Map<String, IBillBizService> serviceMap;

    @Autowired
    public void businessServiceFactory(List<IBillBizService> services) {
        this.serviceMap = services.stream().collect(Collectors.toMap(service -> AopUtils.getTargetClass(service).getSimpleName(), Function.identity()));
    }

    @Override
    public void onMessage(WmsProcessInstanceStatusMessage message) {
        log.info("[onMessage][MQ消费] 收到流程状态变化消息: processInstanceId={}, processDefinitionKey={}, status={}",
                message.getProcessInstanceId(), message.getProcessDefinitionKey(), message.getStatus());

        try {
            // 根据流程类型分发处理
            if (StrUtil.isBlank(message.getProcessDefinitionKey())) {
                log.warn("[onMessage] processDefinitionKey为空，跳过处理");
                return;
            }

            if (message.getProcessDefinitionKey().startsWith("wms_")) {
                handleOaProcessNotification(message);
            } else {
                log.debug("[onMessage] 非WMS流程，跳过处理: {}", message.getProcessDefinitionKey());
            }

        } catch (Exception e) {
            log.error("[onMessage][MQ消费] 处理流程状态变化消息失败", e);
            // 这里可以考虑重试或死信队列处理
            throw e; // 重新抛出异常，触发重试机制
        }
    }

    /**
     * 处理WMS流程通知
     */
    private void handleOaProcessNotification(WmsProcessInstanceStatusMessage message) {
        String processDefinitionKey = message.getProcessDefinitionKey();
        String businessKey = message.getBusinessKey();
        Integer status = message.getStatus();

        if (StrUtil.isBlank(businessKey) || status == null) {
            log.warn("[handleOaProcessNotification] businessKey或status为空，跳过处理");
            return;
        }

        log.info("[handleOaProcessNotification] 处理WMS流程状态变化，processDefinitionKey: {}, businessKey: {}, status: {}",
                processDefinitionKey, businessKey, status);

        try {
            // 根据流程编码获取对应的业务实现类
            Class<? extends IBillBizService> serviceClass = FlowCodeEnum.getServiceClassByFlowCode(processDefinitionKey);
            assert serviceClass != null;
            String simpleName = serviceClass.getSimpleName();
            IBillBizService billBizService = this.serviceMap.get(simpleName);
            FlowProcessRespDTO flowProcessRespDTO = BeanUtils.toBean(message, FlowProcessRespDTO.class);
            billBizService.updateFlowDataByKey(flowProcessRespDTO);
        } catch (Exception e) {
            log.error("[handleOaProcessNotification] 处理WMS流程状态变化失败", e);
            throw e; // 重新抛出异常，触发重试机制
        }
    }

}