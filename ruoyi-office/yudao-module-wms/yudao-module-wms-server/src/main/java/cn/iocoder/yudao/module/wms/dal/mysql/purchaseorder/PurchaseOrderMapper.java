package cn.iocoder.yudao.module.wms.dal.mysql.purchaseorder;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.wms.dal.dataobject.purchaseorder.PurchaseOrderDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.wms.controller.admin.purchaseorder.vo.*;

/**
 * 采购订单 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface PurchaseOrderMapper extends BaseMapperX<PurchaseOrderDO> {

    default PageResult<PurchaseOrderDO> selectPage(PurchaseOrderPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PurchaseOrderDO>()
                .eqIfPresent(PurchaseOrderDO::getPurchaseOrderCode, reqVO.getPurchaseOrderCode())
                .likeIfPresent(PurchaseOrderDO::getPurchaseOrderName, reqVO.getPurchaseOrderName())
                .betweenIfPresent(PurchaseOrderDO::getApplicantDate, reqVO.getApplicantDate())
                .eqIfPresent(PurchaseOrderDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(PurchaseOrderDO::getCompanyName, reqVO.getCompanyName())
                .eqIfPresent(PurchaseOrderDO::getApplicantDeptId, reqVO.getApplicantDeptId())
                .likeIfPresent(PurchaseOrderDO::getApplicantDeptName, reqVO.getApplicantDeptName())
                .eqIfPresent(PurchaseOrderDO::getApplicantUserId, reqVO.getApplicantUserId())
                .likeIfPresent(PurchaseOrderDO::getApplicantUserName, reqVO.getApplicantUserName())
                .eqIfPresent(PurchaseOrderDO::getSupplierCode, reqVO.getSupplierCode())
                .likeIfPresent(PurchaseOrderDO::getSupplierName, reqVO.getSupplierName())
                .eqIfPresent(PurchaseOrderDO::getApplicantDesc, reqVO.getApplicantDesc())
                .eqIfPresent(PurchaseOrderDO::getPurchaseOrderFile, reqVO.getPurchaseOrderFile())
                .eqIfPresent(PurchaseOrderDO::getPurchaseOrderTotalData, reqVO.getPurchaseOrderTotalData())
                .eqIfPresent(PurchaseOrderDO::getPurchaseOrderTotalAmount, reqVO.getPurchaseOrderTotalAmount())
                .eqIfPresent(PurchaseOrderDO::getProcessInstanceId, reqVO.getProcessInstanceId())
                .eqIfPresent(PurchaseOrderDO::getProcessStatus, reqVO.getProcessStatus())
                .eqIfPresent(PurchaseOrderDO::getSort, reqVO.getSort())
                .eqIfPresent(PurchaseOrderDO::getStatus, reqVO.getStatus())
                .eqIfPresent(PurchaseOrderDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(PurchaseOrderDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PurchaseOrderDO::getId));
    }

}