package cn.iocoder.yudao.module.oa.dal.mysql.car;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.oa.dal.dataobject.car.CarApplyBillDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.oa.controller.admin.car.vo.*;

/**
 * 用车申请单 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface CarApplyBillMapper extends BaseMapperX<CarApplyBillDO> {

    default PageResult<CarApplyBillDO> selectPage(CarApplyBillPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CarApplyBillDO>()
                .eqIfPresent(CarApplyBillDO::getId, reqVO.getId())
                .likeIfPresent(CarApplyBillDO::getBillCode, reqVO.getBillCode())
                .eqIfPresent(CarApplyBillDO::getProcessInstanceId, reqVO.getProcessInstanceId())
                .eqIfPresent(CarApplyBillDO::getProcessStatus, reqVO.getProcessStatus())
                .eqIfPresent(CarApplyBillDO::getCarNo, reqVO.getCarNo())
                .betweenIfPresent(CarApplyBillDO::getGoTime, reqVO.getGoTime())
                .betweenIfPresent(CarApplyBillDO::getReturnTime, reqVO.getReturnTime())
                .eqIfPresent(CarApplyBillDO::getGoArea, reqVO.getGoArea())
                .eqIfPresent(CarApplyBillDO::getReturnArea, reqVO.getReturnArea())
                .eqIfPresent(CarApplyBillDO::getCause, reqVO.getCause())
                .eqIfPresent(CarApplyBillDO::getApplyer, reqVO.getApplyer())
                .eqIfPresent(CarApplyBillDO::getPassenger, reqVO.getPassenger())
                .eqIfPresent(CarApplyBillDO::getCreator, reqVO.getCreator())
                .likeIfPresent(CarApplyBillDO::getCreatorName, reqVO.getCreatorName())
                .betweenIfPresent(CarApplyBillDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(CarApplyBillDO::getDeptId, reqVO.getDeptId())
                .likeIfPresent(CarApplyBillDO::getDeptName, reqVO.getDeptName())
                .eqIfPresent(CarApplyBillDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(CarApplyBillDO::getCompanyName, reqVO.getCompanyName())
                .eqIfPresent(CarApplyBillDO::getReturnStatus, reqVO.getReturnStatus())
                .orderByDesc(CarApplyBillDO::getId));
    }

    

}