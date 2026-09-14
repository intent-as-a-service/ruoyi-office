package cn.iocoder.yudao.framework.common.service;

import cn.iocoder.yudao.framework.common.enums.BillTypeEnum;

/**
 * 流程表单服务接口
 * 
 * @author 宇擎源码
 */
public interface FlowBillService<T extends BillTypeEnum> {

    /**
     * 获取支持的单据类型
     *
     * @return 单据类型枚举
     */
    T getSupportedBillType();

    /**
     * 更新流程状态
     *
     * @param businessKey 业务主键（通常是单据ID）
     * @param status 流程状态
     */
    void updateProcessStatus(String businessKey, Integer status);

    /**
     * 流程审批通过后的业务处理
     *
     * @param businessKey 业务主键（通常是单据ID）
     */
    default void onProcessApproved(String businessKey) {
        // 默认空实现，子类可根据需要重写
    }

    /**
     * 流程拒绝后的业务处理
     *
     * @param businessKey 业务主键（通常是单据ID）
     */
    default void onProcessRejected(String businessKey) {
        // 默认空实现，子类可根据需要重写
    }

    /**
     * 流程撤回后的业务处理
     *
     * @param businessKey 业务主键（通常是单据ID）
     */
    default void onProcessCancelled(String businessKey) {
        // 默认空实现，子类可根据需要重写
    }
}
