package cn.iocoder.yudao.module.hrm.service.employee;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.hrm.controller.admin.employee.vo.*;
import cn.iocoder.yudao.module.hrm.dal.dataobject.employee.EmployeeRegularBillDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 员工转正申请单 Service 接口
 *
 * @author 宇擎源码
 */
public interface EmployeeRegularBillService {

    /**
     * 保存员工转正申请单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long saveEmployeeRegularBill(@Valid EmployeeRegularBillSaveReqVO saveReqVO);

    /**
     * 提交员工转正申请单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long submitEmployeeRegularBill(@Valid EmployeeRegularBillSaveReqVO saveReqVO);

    /**
     * 创建员工转正申请单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createEmployeeRegularBill(@Valid EmployeeRegularBillSaveReqVO createReqVO);

    /**
     * 更新员工转正申请单
     *
     * @param updateReqVO 更新信息
     */
    void updateEmployeeRegularBill(@Valid EmployeeRegularBillSaveReqVO updateReqVO);

    /**
     * 删除员工转正申请单
     *
     * @param id 编号
     */
    void deleteEmployeeRegularBill(Long id);

    /**
     * 批量删除员工转正申请单
     *
     * @param ids 编号列表
     */
    void deleteEmployeeRegularBillListByIds(List<Long> ids);

    /**
     * 获得员工转正申请单
     *
     * @param id 编号
     * @return 员工转正申请单
     */
    EmployeeRegularBillDO getEmployeeRegularBill(Long id);

    /**
     * 获得员工转正申请单（包含附件信息）
     *
     * @param id 编号
     * @return 员工转正申请单响应VO
     */
    EmployeeRegularBillRespVO getEmployeeRegularBillInfo(Long id);

    /**
     * 根据单据编号获得员工转正申请单
     *
     * @param code 单据编号
     * @return 员工转正申请单
     */
    EmployeeRegularBillDO getEmployeeRegularBillByCode(String code);

    /**
     * 获得员工转正申请单分页
     *
     * @param pageReqVO 分页查询
     * @return 员工转正申请单分页
     */
    PageResult<EmployeeRegularBillDO> getEmployeeRegularBillPage(EmployeeRegularBillPageReqVO pageReqVO);

}

