package cn.iocoder.yudao.module.wms.service.goodscommonoperationorder;

import java.util.*;

import cn.iocoder.yudao.module.wms.dal.dataobject.goodswarehousingdetail.GoodsWarehousingDetailDO;
import jakarta.validation.*;
import cn.iocoder.yudao.module.wms.controller.admin.goodscommonoperationorder.vo.*;
import cn.iocoder.yudao.module.wms.dal.dataobject.goodscommonoperationorder.GoodsCommonOperationOrderDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 领用、退库、归还、借用、调拨主 Service 接口
 *
 * @author 宇擎源码
 */
public interface GoodsCommonOperationOrderService {

    /**
     * 创建领用、退库、归还、借用、调拨主
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createGoodsCommonOperationOrder(@Valid GoodsCommonOperationOrderSaveReqVO createReqVO);

    /**
     * 更新领用、退库、归还、借用、调拨主
     *
     * @param updateReqVO 更新信息
     */
    void updateGoodsCommonOperationOrder(@Valid GoodsCommonOperationOrderSaveReqVO updateReqVO);

    /**
     * 删除领用、退库、归还、借用、调拨主
     *
     * @param id 编号
     */
    void deleteGoodsCommonOperationOrder(Long id);

    /**
    * 批量删除领用、退库、归还、借用、调拨主
    *
    * @param ids 编号
    */
    void deleteGoodsCommonOperationOrderListByIds(List<Long> ids);

    /**
     * 获得领用、退库、归还、借用、调拨主
     *
     * @param id 编号
     * @return 领用、退库、归还、借用、调拨主
     */
    GoodsCommonOperationOrderDO getGoodsCommonOperationOrder(Long id);

    /**
     * 获得领用、退库、归还、借用、调拨主分页
     *
     * @param pageReqVO 分页查询
     * @return 领用、退库、归还、借用、调拨主分页
     */
    PageResult<GoodsCommonOperationOrderDO> getGoodsCommonOperationOrderPage(GoodsCommonOperationOrderPageReqVO pageReqVO);

    // ==================== 子表（采购入库、领用、退库、归还、借用、调拨明细） ====================

    /**
     * 获得采购入库、领用、退库、归还、借用、调拨明细列表
     *
     * @param commonOperationId 公共操作Id
     * @return 采购入库、领用、退库、归还、借用、调拨明细列表
     */
    List<GoodsWarehousingDetailDO> getGoodsWarehousingDetailListByCommonOperationId(Long commonOperationId);

}