package cn.iocoder.yudao.module.wms.service.purchaseinwarehousing;

import java.util.*;

import cn.iocoder.yudao.module.wms.dal.dataobject.goodswarehousingdetail.GoodsWarehousingDetailDO;
import jakarta.validation.*;
import cn.iocoder.yudao.module.wms.controller.admin.purchaseinwarehousing.vo.*;
import cn.iocoder.yudao.module.wms.dal.dataobject.purchaseinwarehousing.PurchaseInWarehousingDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 采购入库 Service 接口
 *
 * @author 宇擎源码
 */
public interface PurchaseInWarehousingService {

    /**
     * 创建采购入库
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPurchaseInWarehousing(@Valid PurchaseInWarehousingSaveReqVO createReqVO);

    /**
     * 更新采购入库
     *
     * @param updateReqVO 更新信息
     */
    void updatePurchaseInWarehousing(@Valid PurchaseInWarehousingSaveReqVO updateReqVO);

    /**
     * 删除采购入库
     *
     * @param id 编号
     */
    void deletePurchaseInWarehousing(Long id);

    /**
    * 批量删除采购入库
    *
    * @param ids 编号
    */
    void deletePurchaseInWarehousingListByIds(List<Long> ids);

    /**
     * 获得采购入库
     *
     * @param id 编号
     * @return 采购入库
     */
    PurchaseInWarehousingDO getPurchaseInWarehousing(Long id);

    /**
     * 获得采购入库分页
     *
     * @param pageReqVO 分页查询
     * @return 采购入库分页
     */
    PageResult<PurchaseInWarehousingDO> getPurchaseInWarehousingPage(PurchaseInWarehousingPageReqVO pageReqVO);

    // ==================== 子表（采购入库、领用、退库、归还、借用、调拨明细） ====================

    /**
     * 获得采购入库、领用、退库、归还、借用、调拨明细列表
     *
     * @param purchaseOrderId 采购订单id
     * @return 采购入库、领用、退库、归还、借用、调拨明细列表
     */
    List<GoodsWarehousingDetailDO> getGoodsWarehousingDetailListByPurchaseOrderId(Long purchaseOrderId);

}