package cn.iocoder.yudao.module.hrm.process.mq;

import cn.iocoder.yudao.common.server.process.mq.AbstractFlowMqNotificationConsumer;
import cn.iocoder.yudao.framework.common.enums.SystemEnum;
import cn.iocoder.yudao.framework.common.service.FlowBillServiceFactory;
import cn.iocoder.yudao.module.hrm.enums.HrmBillTypeEnum;
import cn.iocoder.yudao.module.hrm.service.HrmFlowBillServiceFactory;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * HRM 模块统一BPM事件MQ消费者
 * 支持流程实例事件和任务事件的统一处理
 *
 * @author 宇擎源码
 */
@Component
@ConditionalOnProperty(name = "yudao.bpm.notification.mq.enabled", havingValue = "true", matchIfMissing = false)
public class HrmMqNotificationConsumer extends AbstractFlowMqNotificationConsumer<HrmBillTypeEnum> {

    @Resource
    private HrmFlowBillServiceFactory flowBillServiceFactory;

    @Override
    protected SystemEnum getSystem() {
        return SystemEnum.HRM;
    }

    @Override
    protected FlowBillServiceFactory<HrmBillTypeEnum> getFlowBillServiceFactory() {
        return flowBillServiceFactory;
    }
}

