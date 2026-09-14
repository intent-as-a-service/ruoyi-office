package cn.iocoder.yudao.module.wms.service.purchaseorder;

import java.util.*;

import cn.iocoder.yudao.module.wms.framework.security.service.IBillBizService;
import jakarta.validation.*;
import cn.iocoder.yudao.module.wms.controller.admin.purchaseorder.vo.*;
import cn.iocoder.yudao.module.wms.dal.dataobject.purchaseorder.PurchaseOrderDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.purchaseorderdetail.PurchaseOrderDetailDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 采购订单 Service 接口
 *
 * @author 宇擎源码
 */
public interface PurchaseOrderService extends IBillBizService {

    /**
     * 创建采购订单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPurchaseOrder(@Valid PurchaseOrderSaveReqVO createReqVO);

    /**
     * 提交采购订单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long submitPurchaseOrder(@Valid PurchaseOrderSaveReqVO createReqVO);

    /**
     * 更新采购订单
     *
     * @param updateReqVO 更新信息
     */
    void updatePurchaseOrder(@Valid PurchaseOrderSaveReqVO updateReqVO);

    /**
     * 删除采购订单
     *
     * @param id 编号
     */
    void deletePurchaseOrder(Long id);

    /**
    * 批量删除采购订单
    *
    * @param ids 编号
    */
    void deletePurchaseOrderListByIds(List<Long> ids);

    /**
     * 获得采购订单
     *
     * @param id 编号
     * @return 采购订单
     */
    PurchaseOrderDO getPurchaseOrder(Long id);

    /**
     * 获得采购订单分页
     *
     * @param pageReqVO 分页查询
     * @return 采购订单分页
     */
    PageResult<PurchaseOrderDO> getPurchaseOrderPage(PurchaseOrderPageReqVO pageReqVO);

    // ==================== 子表（采购订单明细） ====================

    /**
     * 获得采购订单明细列表
     *
     * @param purchaseOrderId 采购订单id
     * @return 采购订单明细列表
     */
    List<PurchaseOrderDetailDO> getPurchaseOrderDetailListByPurchaseOrderId(String purchaseOrderId);

}