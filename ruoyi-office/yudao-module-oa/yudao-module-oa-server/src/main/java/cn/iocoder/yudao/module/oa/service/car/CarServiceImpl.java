package cn.iocoder.yudao.module.oa.service.car;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.oa.controller.admin.car.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.car.CarDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.oa.dal.mysql.car.CarMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.oa.enums.ErrorCodeConstants.*;

/**
 * 车辆信息 Service 实现类
 *
 * @author 宇擎源码
 */
@Service
@Validated
public class CarServiceImpl implements CarService {

    @Resource
    private CarMapper carMapper;

    @Override
    public Long createCar(CarSaveReqVO createReqVO) {
        // 插入
        CarDO car = BeanUtils.toBean(createReqVO, CarDO.class);
        carMapper.insert(car);
        // 返回
        return car.getId();
    }

    @Override
    public void updateCar(CarSaveReqVO updateReqVO) {
        // 校验存在
        validateCarExists(updateReqVO.getId());
        // 更新
        CarDO updateObj = BeanUtils.toBean(updateReqVO, CarDO.class);
        carMapper.updateById(updateObj);
    }

    @Override
    public void deleteCar(Long id) {
        // 校验存在
        validateCarExists(id);
        // 删除
        carMapper.deleteById(id);
    }

    @Override
        public void deleteCarListByIds(List<Long> ids) {
        // 校验存在
        validateCarExists(ids);
        // 删除
        carMapper.deleteByIds(ids);
        }

    private void validateCarExists(List<Long> ids) {
        List<CarDO> list = carMapper.selectByIds(ids);
        if (CollUtil.isEmpty(list) || list.size() != ids.size()) {
            throw exception(CAR_NOT_EXISTS);
        }
    }

    private void validateCarExists(Long id) {
        if (carMapper.selectById(id) == null) {
            throw exception(CAR_NOT_EXISTS);
        }
    }

    @Override
    public CarDO getCar(Long id) {
        return carMapper.selectById(id);
    }

    @Override
    public PageResult<CarDO> getCarPage(CarPageReqVO pageReqVO) {
        return carMapper.selectPage(pageReqVO);
    }

}