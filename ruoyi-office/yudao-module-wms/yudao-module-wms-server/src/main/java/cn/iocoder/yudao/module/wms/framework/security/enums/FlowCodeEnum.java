package cn.iocoder.yudao.module.wms.framework.security.enums;

import cn.iocoder.yudao.module.wms.framework.security.service.IBillBizService;
import cn.iocoder.yudao.module.wms.service.purchaseorder.PurchaseOrderServiceImpl;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * WMS流程编号枚举类
 *
 *
 */
public enum FlowCodeEnum {
    // 采购订单
    WMS_PURCHASE_ORDER_FLOW("wms_purchase_order_flow", "采购订单流程", PurchaseOrderServiceImpl.class),
    ;


    /**
     * 流程编码
     */
    private String flowCode;

    /**
     * 流程名称
     */
    private String flowName;

    /**
     * 业务类
     */
    private final Class<? extends IBillBizService> serviceClass;

    FlowCodeEnum(String flowCode, String flowName , Class<? extends IBillBizService> serviceClass)  {
        this.flowCode = flowCode;
        this.flowName = flowName;
        this.serviceClass = serviceClass;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public String getFlowName() {
        return flowName;
    }

    public Class<? extends IBillBizService> getServiceClass() {
        return serviceClass;
    }


    /**
     * 根据流程编号获取业务处理类
     * @param flowCode 流程编号
     * @return 业务处理类
     */
    public static Class<? extends IBillBizService>  getServiceClassByFlowCode(String flowCode) {
        if (StringUtils.isBlank(flowCode)) {
            return null;
        }
        for (FlowCodeEnum flowCodeEnum : FlowCodeEnum.values()) {
            String enumFlowCode = flowCodeEnum.getFlowCode();
            if (enumFlowCode != null && flowCode.contains(enumFlowCode)) {
                return flowCodeEnum.getServiceClass();
            }
        }
        return null;
    }
}
