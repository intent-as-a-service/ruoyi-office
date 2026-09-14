package cn.iocoder.yudao.module.oa.dal.mysql.car;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.oa.dal.dataobject.car.CarReturnBillDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.oa.controller.admin.car.vo.*;

/**
 * 还车申请单 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface CarReturnBillMapper extends BaseMapperX<CarReturnBillDO> {

    default PageResult<CarReturnBillDO> selectPage(CarReturnBillPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CarReturnBillDO>()
                .eqIfPresent(CarReturnBillDO::getBillCode, reqVO.getBillCode())
                .eqIfPresent(CarReturnBillDO::getProcessInstanceId, reqVO.getProcessInstanceId())
                .eqIfPresent(CarReturnBillDO::getProcessStatus, reqVO.getProcessStatus())
                .eqIfPresent(CarReturnBillDO::getApplyBill, reqVO.getApplyBill())
                .eqIfPresent(CarReturnBillDO::getCarId, reqVO.getCarId())
                .eqIfPresent(CarReturnBillDO::getCarNo, reqVO.getCarNo())
                .betweenIfPresent(CarReturnBillDO::getGoTime, reqVO.getGoTime())
                .betweenIfPresent(CarReturnBillDO::getReturnTime, reqVO.getReturnTime())
                .eqIfPresent(CarReturnBillDO::getGoArea, reqVO.getGoArea())
                .eqIfPresent(CarReturnBillDO::getReturnArea, reqVO.getReturnArea())
                .eqIfPresent(CarReturnBillDO::getCause, reqVO.getCause())
                .eqIfPresent(CarReturnBillDO::getApplyer, reqVO.getApplyer())
                .eqIfPresent(CarReturnBillDO::getPassenger, reqVO.getPassenger())
                .eqIfPresent(CarReturnBillDO::getRemark, reqVO.getRemark())
                .eqIfPresent(CarReturnBillDO::getCreator, reqVO.getCreator())
                .likeIfPresent(CarReturnBillDO::getCreatorName, reqVO.getCreatorName())
                .betweenIfPresent(CarReturnBillDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(CarReturnBillDO::getParentId, reqVO.getParentId())
                .eqIfPresent(CarReturnBillDO::getDeptId, reqVO.getDeptId())
                .likeIfPresent(CarReturnBillDO::getDeptName, reqVO.getDeptName())
                .eqIfPresent(CarReturnBillDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(CarReturnBillDO::getCompanyName, reqVO.getCompanyName())
                .orderByDesc(CarReturnBillDO::getId));
    }

}