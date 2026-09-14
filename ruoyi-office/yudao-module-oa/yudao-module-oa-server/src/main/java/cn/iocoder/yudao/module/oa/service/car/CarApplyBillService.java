package cn.iocoder.yudao.module.oa.service.car;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.oa.controller.admin.car.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.car.CarApplyBillDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 用车申请单 Service 接口
 *
 * @author 宇擎源码
 */
public interface CarApplyBillService {
    /**
     * 保存用车申请单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long saveCarApplyBill(@Valid CarApplyBillSaveReqVO saveReqVO);

    /**
     * 创建用车申请单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCarApplyBill(@Valid CarApplyBillSaveReqVO createReqVO);

    /**
     * 提交用车申请单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long submitCarApplyBill(CarApplyBillSaveReqVO createReqVO);

    /**
     * 更新用车申请单
     *
     * @param updateReqVO 更新信息
     */
    void updateCarApplyBill(@Valid CarApplyBillSaveReqVO updateReqVO);

    /**
     * 删除用车申请单
     *
     * @param id 编号
     */
    void deleteCarApplyBill(Long id);

    /**
    * 批量删除用车申请单
    *
    * @param ids 编号
    */
    void deleteCarApplyBillListByIds(List<Long> ids);

    /**
     * 获得用车申请单
     *
     * @param id 编号
     * @return 用车申请单
     */
    CarApplyBillDO getCarApplyBill(Long id);

    /**
     * 获得用车申请单（包含附件信息）
     *
     * @param id 编号
     * @return 用车申请单响应VO
     */
    CarApplyBillRespVO getCarApplyBillInfo(Long id);
    
    /**
     * 获得用车申请单
     *
     * @param code 编号
     * @return 用车申请单
     */
    CarApplyBillDO getCarApplyBillByCode(String code);

    /**
     * 获得用车申请单分页
     *
     * @param pageReqVO 分页查询
     * @return 用车申请单分页
     */
    PageResult<CarApplyBillDO> getCarApplyBillPage(CarApplyBillPageReqVO pageReqVO);

    /**
     * 更新用车申请单还车状态
     *
     * @param id 用车申请单编号
     * @param returnStatus 还车状态
     */
    void updateReturnStatus(Long id, Integer returnStatus);

    /**
     * 标记用车申请单为已还车
     *
     * @param id 用车申请单编号
     */
    void markAsReturned(Long id);
    
    /**
     * 标记用车申请单为还车中
     *
     * @param id 用车申请单编号
     */
    void markAsReturning(Long id);
    
    /**
     * 标记用车申请单为未还车
     *
     * @param id 用车申请单编号
     */
    void markAsNotReturned(Long id);

}