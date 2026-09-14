package cn.iocoder.yudao.module.bpm.service.notification.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.framework.mq.redis.core.RedisMQTemplate;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceStatusMessage;
import cn.iocoder.yudao.module.bpm.api.event.BpmNotificationTypeEnum;
import cn.iocoder.yudao.module.bpm.service.notification.BpmNotificationHandler;
import cn.iocoder.yudao.module.bpm.service.notification.message.BpmProcessInstanceStatusRedisMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * MQ 通知处理器
 * 适用于异步、高吞吐量的跨服务通信场景
 *
 * @author 宇擎源码
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "yudao.bpm.notification.mq.enabled", havingValue = "true", matchIfMissing = false)
public class BpmMqNotificationHandler implements BpmNotificationHandler {

    @Resource
    private RedisMQTemplate redisMQTemplate;

    @Override
    public BpmNotificationTypeEnum getSupportType() {
        return BpmNotificationTypeEnum.MQ;
    }

    @Override
    public void handleNotification(BpmProcessInstanceStatusMessage message) {
        try {
            log.info("[handleNotification][MQ通知] 发送流程状态变化消息，processInstanceId: {}, status: {}", 
                    message.getProcessInstanceId(), message.getProcessInstanceInfo().getStatus());
            
            // 转换为Redis Stream消息对象
            BpmProcessInstanceStatusRedisMessage redisMessage = BeanUtil.copyProperties(message, BpmProcessInstanceStatusRedisMessage.class);
            
            // 发送MQ消息
            redisMQTemplate.send(redisMessage);
            
            log.info("[handleNotification][MQ通知] 流程状态变化消息发送成功");
        } catch (Exception e) {
            log.error("[handleNotification][MQ通知] 流程状态变化消息发送失败", e);
            // 可以考虑重试机制或死信队列
        }
    }

} 