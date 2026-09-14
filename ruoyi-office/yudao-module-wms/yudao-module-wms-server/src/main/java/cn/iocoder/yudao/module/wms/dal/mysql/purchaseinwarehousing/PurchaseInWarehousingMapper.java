package cn.iocoder.yudao.module.wms.dal.mysql.purchaseinwarehousing;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.wms.dal.dataobject.purchaseinwarehousing.PurchaseInWarehousingDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.wms.controller.admin.purchaseinwarehousing.vo.*;

/**
 * 采购入库 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface PurchaseInWarehousingMapper extends BaseMapperX<PurchaseInWarehousingDO> {

    default PageResult<PurchaseInWarehousingDO> selectPage(PurchaseInWarehousingPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PurchaseInWarehousingDO>()
                .eqIfPresent(PurchaseInWarehousingDO::getPurchaseOrderId, reqVO.getPurchaseOrderId())
                .eqIfPresent(PurchaseInWarehousingDO::getPurchaseOrderCode, reqVO.getPurchaseOrderCode())
                .likeIfPresent(PurchaseInWarehousingDO::getPurchaseOrderName, reqVO.getPurchaseOrderName())
                .eqIfPresent(PurchaseInWarehousingDO::getWarehousingEntryCode, reqVO.getWarehousingEntryCode())
                .likeIfPresent(PurchaseInWarehousingDO::getWarehousingEntryName, reqVO.getWarehousingEntryName())
                .betweenIfPresent(PurchaseInWarehousingDO::getInWarehousingDate, reqVO.getInWarehousingDate())
                .eqIfPresent(PurchaseInWarehousingDO::getInWarehousingDesc, reqVO.getInWarehousingDesc())
                .eqIfPresent(PurchaseInWarehousingDO::getInWarehousingFile, reqVO.getInWarehousingFile())
                .eqIfPresent(PurchaseInWarehousingDO::getInWarehousingTotalData, reqVO.getInWarehousingTotalData())
                .eqIfPresent(PurchaseInWarehousingDO::getInWarehousingTotalAmount, reqVO.getInWarehousingTotalAmount())
                .eqIfPresent(PurchaseInWarehousingDO::getSort, reqVO.getSort())
                .eqIfPresent(PurchaseInWarehousingDO::getStatus, reqVO.getStatus())
                .eqIfPresent(PurchaseInWarehousingDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(PurchaseInWarehousingDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PurchaseInWarehousingDO::getId));
    }

}