package cn.iocoder.yudao.module.oa.dal.mysql.car;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.oa.dal.dataobject.car.CarDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.oa.controller.admin.car.vo.*;

/**
 * 车辆信息 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface CarMapper extends BaseMapperX<CarDO> {

    default PageResult<CarDO> selectPage(CarPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CarDO>()
                .eqIfPresent(CarDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(CarDO::getCompanyName, reqVO.getCompanyName())
                .likeIfPresent(CarDO::getCarNo, reqVO.getCarNo())
                .likeIfPresent(CarDO::getCarName, reqVO.getCarName())
                .eqIfPresent(CarDO::getStatus, reqVO.getStatus())
                .eqIfPresent(CarDO::getCarType, reqVO.getCarType())
                .eqIfPresent(CarDO::getCarCls, reqVO.getCarCls())
                .eqIfPresent(CarDO::getBrand, reqVO.getBrand())
                .eqIfPresent(CarDO::getSeatNum, reqVO.getSeatNum())
                .eqIfPresent(CarDO::getBarePrice, reqVO.getBarePrice())
                .betweenIfPresent(CarDO::getForceInsuranceDate, reqVO.getForceInsuranceDate())
                .betweenIfPresent(CarDO::getBusinessInsuranceDate, reqVO.getBusinessInsuranceDate())
                .betweenIfPresent(CarDO::getYearCheckDate, reqVO.getYearCheckDate())
                .eqIfPresent(CarDO::getPicUrl, reqVO.getPicUrl())
                .eqIfPresent(CarDO::getSort, reqVO.getSort())
                .eqIfPresent(CarDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(CarDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CarDO::getId));
    }

}