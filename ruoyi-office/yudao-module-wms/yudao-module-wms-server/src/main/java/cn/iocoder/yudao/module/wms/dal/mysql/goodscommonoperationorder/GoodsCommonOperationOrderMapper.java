package cn.iocoder.yudao.module.wms.dal.mysql.goodscommonoperationorder;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.wms.dal.dataobject.goodscommonoperationorder.GoodsCommonOperationOrderDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.wms.controller.admin.goodscommonoperationorder.vo.*;

/**
 * 领用、退库、归还、借用、调拨主 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface GoodsCommonOperationOrderMapper extends BaseMapperX<GoodsCommonOperationOrderDO> {

    default PageResult<GoodsCommonOperationOrderDO> selectPage(GoodsCommonOperationOrderPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<GoodsCommonOperationOrderDO>()
                .eqIfPresent(GoodsCommonOperationOrderDO::getPurchaseOrderId, reqVO.getPurchaseOrderId())
                .eqIfPresent(GoodsCommonOperationOrderDO::getPurchaseOrderCode, reqVO.getPurchaseOrderCode())
                .likeIfPresent(GoodsCommonOperationOrderDO::getPurchaseOrderName, reqVO.getPurchaseOrderName())
                .eqIfPresent(GoodsCommonOperationOrderDO::getInWarehousingId, reqVO.getInWarehousingId())
                .eqIfPresent(GoodsCommonOperationOrderDO::getWarehousingEntryCode, reqVO.getWarehousingEntryCode())
                .likeIfPresent(GoodsCommonOperationOrderDO::getWarehousingEntryName, reqVO.getWarehousingEntryName())
                .betweenIfPresent(GoodsCommonOperationOrderDO::getCommonOperationDate, reqVO.getCommonOperationDate())
                .eqIfPresent(GoodsCommonOperationOrderDO::getCommonOperationDesc, reqVO.getCommonOperationDesc())
                .eqIfPresent(GoodsCommonOperationOrderDO::getCommonOperationFile, reqVO.getCommonOperationFile())
                .eqIfPresent(GoodsCommonOperationOrderDO::getStockReturnTotalData, reqVO.getStockReturnTotalData())
                .eqIfPresent(GoodsCommonOperationOrderDO::getCommonOperationTotalAmount, reqVO.getCommonOperationTotalAmount())
                .eqIfPresent(GoodsCommonOperationOrderDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(GoodsCommonOperationOrderDO::getCompanyName, reqVO.getCompanyName())
                .eqIfPresent(GoodsCommonOperationOrderDO::getDeptId, reqVO.getDeptId())
                .likeIfPresent(GoodsCommonOperationOrderDO::getDeptName, reqVO.getDeptName())
                .eqIfPresent(GoodsCommonOperationOrderDO::getUserId, reqVO.getUserId())
                .likeIfPresent(GoodsCommonOperationOrderDO::getUserName, reqVO.getUserName())
                .betweenIfPresent(GoodsCommonOperationOrderDO::getExpectReturnDate, reqVO.getExpectReturnDate())
                .eqIfPresent(GoodsCommonOperationOrderDO::getTransferCompanyId, reqVO.getTransferCompanyId())
                .likeIfPresent(GoodsCommonOperationOrderDO::getTransferCompanyName, reqVO.getTransferCompanyName())
                .eqIfPresent(GoodsCommonOperationOrderDO::getTransferDeptId, reqVO.getTransferDeptId())
                .likeIfPresent(GoodsCommonOperationOrderDO::getTransferDeptName, reqVO.getTransferDeptName())
                .eqIfPresent(GoodsCommonOperationOrderDO::getProcessInstanceId, reqVO.getProcessInstanceId())
                .eqIfPresent(GoodsCommonOperationOrderDO::getProcessStatus, reqVO.getProcessStatus())
                .eqIfPresent(GoodsCommonOperationOrderDO::getSort, reqVO.getSort())
                .eqIfPresent(GoodsCommonOperationOrderDO::getStatus, reqVO.getStatus())
                .eqIfPresent(GoodsCommonOperationOrderDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(GoodsCommonOperationOrderDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(GoodsCommonOperationOrderDO::getId));
    }

}