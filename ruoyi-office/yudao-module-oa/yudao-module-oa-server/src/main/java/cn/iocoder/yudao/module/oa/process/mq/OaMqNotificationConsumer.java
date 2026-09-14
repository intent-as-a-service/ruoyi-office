package cn.iocoder.yudao.module.oa.process.mq;

import cn.iocoder.yudao.common.server.process.mq.AbstractFlowMqNotificationConsumer;
import cn.iocoder.yudao.framework.common.enums.SystemEnum;
import cn.iocoder.yudao.framework.common.service.FlowBillServiceFactory;
import cn.iocoder.yudao.module.oa.enums.OaBillTypeEnum;
import cn.iocoder.yudao.module.oa.service.OaFlowBillServiceFactory;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * OA 模块统一BPM事件MQ消费者
 * 支持流程实例事件和任务事件的统一处理
 *
 * @author 宇擎源码
 */
@Component
@ConditionalOnProperty(name = "yudao.bpm.notification.mq.enabled", havingValue = "true", matchIfMissing = false)
public class OaMqNotificationConsumer extends AbstractFlowMqNotificationConsumer<OaBillTypeEnum> {

    @Resource
    private OaFlowBillServiceFactory flowBillServiceFactory;

    @Override
    protected SystemEnum getSystem() {
        return SystemEnum.OA;
    }

    @Override
    protected FlowBillServiceFactory<OaBillTypeEnum> getFlowBillServiceFactory() {
        return flowBillServiceFactory;
    }
}
