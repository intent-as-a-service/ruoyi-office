package cn.iocoder.yudao.module.wms.dal.mysql.goodswarehousingdetail;


import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.wms.dal.dataobject.goodswarehousingdetail.GoodsWarehousingDetailDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 采购入库、领用、退库、归还、借用、调拨明细 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface GoodsWarehousingDetailMapper extends BaseMapperX<GoodsWarehousingDetailDO> {

    default List<GoodsWarehousingDetailDO> selectListByPurchaseOrderId(Long purchaseOrderId) {
        return selectList(GoodsWarehousingDetailDO::getPurchaseOrderId, purchaseOrderId);
    }

    default int deleteByPurchaseOrderId(Long purchaseOrderId) {
        return delete(cn.iocoder.yudao.module.wms.dal.dataobject.goodswarehousingdetail.GoodsWarehousingDetailDO::getPurchaseOrderId, purchaseOrderId);
    }

    default int deleteByPurchaseOrderIds(List<Long> purchaseOrderIds) {
        return deleteBatch(cn.iocoder.yudao.module.wms.dal.dataobject.goodswarehousingdetail.GoodsWarehousingDetailDO::getPurchaseOrderId, purchaseOrderIds);
    }




    default List<GoodsWarehousingDetailDO> selectListByCommonOperationId(Long commonOperationId) {
        return selectList(GoodsWarehousingDetailDO::getCommonOperationId, commonOperationId);
    }

    default int deleteByCommonOperationId(Long commonOperationId) {
        return delete(GoodsWarehousingDetailDO::getCommonOperationId, commonOperationId);
    }

    default int deleteByCommonOperationIds(List<Long> commonOperationIds) {
        return deleteBatch(GoodsWarehousingDetailDO::getCommonOperationId, commonOperationIds);
    }
}