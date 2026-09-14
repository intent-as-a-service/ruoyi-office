package cn.iocoder.yudao.module.bpm.service.notification.impl;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceStatusMessage;
import cn.iocoder.yudao.module.bpm.api.event.BpmNotificationTypeEnum;
import cn.iocoder.yudao.module.bpm.service.notification.BpmNotificationHandler;
import cn.iocoder.yudao.module.oa.api.process.OaFeignNotificationApi;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Feign 通知处理器
 * 适用于实时性要求高的同步调用场景
 *
 * @author 宇擎源码
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "yudao.bpm.notification.feign.enabled", havingValue = "true", matchIfMissing = false)
public class BpmFeignNotificationHandler implements BpmNotificationHandler {

    @Resource
    private OaFeignNotificationApi oaFeignNotificationApi;

    @Override
    public BpmNotificationTypeEnum getSupportType() {
        return BpmNotificationTypeEnum.FEIGN;
    }

    @Override
    public void handleNotification(BpmProcessInstanceStatusMessage message) {
        // 只处理OA相关的流程
        if (!isOaProcess(message.getProcessDefinitionKey())) {
            log.debug("[handleNotification][Feign通知] 非OA流程，跳过处理，processDefinitionKey: {}", 
                    message.getProcessDefinitionKey());
            return;
        }

        try {
            log.info("[handleNotification][Feign通知] 调用OA服务，processInstanceId: {}, status: {}", 
                    message.getProcessInstanceId(), message.getProcessInstanceInfo().getStatus());
            
            // 转换为Map，方便传输
//            Map<String, Object> messageMap = BeanUtil.beanToMap(message);
            
            // 调用OA服务接口
            CommonResult<Boolean> result = oaFeignNotificationApi.processStatusChange(message);
            
            if (result.isSuccess()) {
                log.info("[handleNotification][Feign通知] OA服务调用成功");
            } else {
                log.error("[handleNotification][Feign通知] OA服务调用失败，错误信息: {}", result.getMsg());
            }
        } catch (Exception e) {
            log.error("[handleNotification][Feign通知] OA服务调用异常", e);
            // 可以考虑降级处理或重试机制
        }
    }

    /**
     * 判断是否为OA相关流程
     */
    private boolean isOaProcess(String processDefinitionKey) {
        return StrUtil.isNotBlank(processDefinitionKey) && processDefinitionKey.startsWith("oa_");
    }

} 