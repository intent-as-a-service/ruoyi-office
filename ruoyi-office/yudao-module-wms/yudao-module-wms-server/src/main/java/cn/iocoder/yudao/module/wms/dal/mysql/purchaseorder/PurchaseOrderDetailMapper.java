package cn.iocoder.yudao.module.wms.dal.mysql.purchaseorder;

import java.util.*;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.wms.dal.dataobject.purchaseorderdetail.PurchaseOrderDetailDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购订单明细 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface PurchaseOrderDetailMapper extends BaseMapperX<PurchaseOrderDetailDO> {


    default List<PurchaseOrderDetailDO> selectListByPurchaseOrderId(String purchaseOrderId) {
        return selectList(PurchaseOrderDetailDO::getPurchaseOrderId, purchaseOrderId);
    }

    default int deleteByPurchaseOrderId(String purchaseOrderId) {
        return delete(PurchaseOrderDetailDO::getPurchaseOrderId, purchaseOrderId);
    }

	default int deleteByPurchaseOrderIds(List<Long> purchaseOrderIds) {
	    return deleteBatch(PurchaseOrderDetailDO::getPurchaseOrderId, purchaseOrderIds);
	}

}
