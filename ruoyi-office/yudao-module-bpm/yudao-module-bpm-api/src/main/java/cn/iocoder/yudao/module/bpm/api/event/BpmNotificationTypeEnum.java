package cn.iocoder.yudao.module.bpm.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * BPM 流程通知方式枚举
 *
 * @author 宇擎源码
 */
@Getter
@AllArgsConstructor
public enum BpmNotificationTypeEnum {

    /**
     * 本地事件通知（ApplicationEvent）
     * 适用于单体架构或同一JVM内的服务通信
     */
    LOCAL_EVENT("local", "本地事件通知"),

    /**
     * 消息队列通知
     * 适用于异步、高吞吐量的场景
     */
    MQ("mq", "消息队列通知"),

    /**
     * Feign远程调用通知
     * 适用于实时性要求高的同步调用场景
     */
    FEIGN("feign", "Feign远程调用"),

    /**
     * HTTP回调通知
     * 适用于第三方系统集成
     */
    HTTP_CALLBACK("http_callback", "HTTP回调通知");

    /**
     * 类型编码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 根据编码获取枚举
     */
    public static BpmNotificationTypeEnum getByCode(String code) {
        for (BpmNotificationTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return LOCAL_EVENT; // 默认返回本地事件
    }

} 