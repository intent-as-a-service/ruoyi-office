package cn.iocoder.yudao.module.bpm.service.notification;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceStatusMessage;
import cn.iocoder.yudao.module.bpm.api.event.BpmNotificationTypeEnum;

/**
 * BPM 流程通知处理器接口
 *
 * @author 宇擎源码
 */
public interface BpmNotificationHandler {

    /**
     * 获取支持的通知类型
     */
    BpmNotificationTypeEnum getSupportType();

    /**
     * 处理流程状态通知
     *
     * @param message 状态消息
     */
    void handleNotification(BpmProcessInstanceStatusMessage message);

    /**
     * 是否启用该通知方式
     */
    default boolean isEnabled() {
        return true;
    }

} 