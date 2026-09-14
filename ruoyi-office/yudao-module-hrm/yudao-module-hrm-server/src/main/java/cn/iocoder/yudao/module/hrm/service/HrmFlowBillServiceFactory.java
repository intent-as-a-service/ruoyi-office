package cn.iocoder.yudao.module.hrm.service;

import cn.iocoder.yudao.framework.common.service.FlowBillServiceFactory;
import cn.iocoder.yudao.module.hrm.enums.HrmBillTypeEnum;
import org.springframework.stereotype.Component;

/**
 * HRM 流程表单服务工厂
 *
 * @author 宇擎
 */
@Component
public class HrmFlowBillServiceFactory extends FlowBillServiceFactory<HrmBillTypeEnum> {

    @Override
    protected HrmBillTypeEnum[] getBillTypeValues() {
        return HrmBillTypeEnum.values();
    }
}

