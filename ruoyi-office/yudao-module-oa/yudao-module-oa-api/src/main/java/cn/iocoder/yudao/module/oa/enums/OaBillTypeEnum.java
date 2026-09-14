package cn.iocoder.yudao.module.oa.enums;

import cn.iocoder.yudao.framework.common.enums.BillTypeEnum;

/**
 * 单据类型枚举
 *
 * @author 宇擎源码
 */
public enum OaBillTypeEnum implements BillTypeEnum {
    /**
     * 用车申请单
     */
    OA_CAR_APPLY_BILL("101", "用车申请单","oa_car_apply_bill"),
    
    /**
     * 还车申请单
     */
    OA_CAR_RETURN_BILL("102", "还车申请单","oa_car_return_bill"),
    
    /**
     * 用印申请单
     */
    OA_SEAL_APPLY_BILL("103", "用印申请单","oa_seal_apply_bill"),
    
    /**
     * 会议室预定申请单
     */
    OA_MEETING_ROOM_BOOKING("104", "会议室预定申请单","oa_meeting_room_booking");

    /**
     * 单据类型代码
     */
    private final String code;

    /**
     * 单据类型名称
     */
    private final String name;

    /**
     * 流程定义key
     */
    private final String processDefinitionKey;

    /**
     * 构造方法
     *
     * @param code 单据类型代码
     * @param name 单据类型名称
     */
    OaBillTypeEnum(String code, String name, String processDefinitionKey) {
        this.code = code;
        this.name = name;
        this.processDefinitionKey = processDefinitionKey;
    }

    @Override
    public String getTypeCode() {
        return code;
    }

    @Override
    public String getTypeName() {
        return name;
    }

    @Override
    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

}
