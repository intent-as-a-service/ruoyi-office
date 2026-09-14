package cn.iocoder.yudao.module.oa.process.local;

import cn.iocoder.yudao.common.server.process.listener.AbstractFlowLocalNotificationListener;
import cn.iocoder.yudao.framework.common.enums.SystemEnum;
import cn.iocoder.yudao.framework.common.service.FlowBillServiceFactory;
import cn.iocoder.yudao.module.oa.enums.OaBillTypeEnum;
import cn.iocoder.yudao.module.oa.service.OaFlowBillServiceFactory;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * OA 模块统一BPM事件监听器
 * 支持流程实例事件和任务事件的统一处理
 *
 * @author 宇擎源码
 */
@Component
public class OaLocalNotificationListener extends AbstractFlowLocalNotificationListener<OaBillTypeEnum> {

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
