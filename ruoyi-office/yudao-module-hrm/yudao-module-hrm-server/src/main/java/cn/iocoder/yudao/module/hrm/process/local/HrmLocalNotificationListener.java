package cn.iocoder.yudao.module.hrm.process.local;

import cn.iocoder.yudao.common.server.process.listener.AbstractFlowLocalNotificationListener;
import cn.iocoder.yudao.framework.common.enums.SystemEnum;
import cn.iocoder.yudao.framework.common.service.FlowBillServiceFactory;
import cn.iocoder.yudao.module.hrm.enums.HrmBillTypeEnum;
import cn.iocoder.yudao.module.hrm.service.HrmFlowBillServiceFactory;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * HRM 模块 BPM 本地事件监听
 *
 * @author 宇擎
 */
@Component
public class HrmLocalNotificationListener extends AbstractFlowLocalNotificationListener<HrmBillTypeEnum> {

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

