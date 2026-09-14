package cn.iocoder.yudao.module.oa.service;

import cn.iocoder.yudao.framework.common.service.FlowBillServiceFactory;
import cn.iocoder.yudao.module.oa.enums.OaBillTypeEnum;
import org.springframework.stereotype.Component;

/**
 * OA流程表单服务工厂类
 * 
 * @author 宇擎源码
 */
@Component
public class OaFlowBillServiceFactory extends FlowBillServiceFactory<OaBillTypeEnum> {

    @Override
    protected OaBillTypeEnum[] getBillTypeValues() {
        return OaBillTypeEnum.values();
    }
}
