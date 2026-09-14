package cn.iocoder.yudao.module.hrm.service.employee;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.hrm.controller.admin.employee.vo.*;
import cn.iocoder.yudao.module.hrm.dal.dataobject.employee.EmployeeResignationBillDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 员工离职申请单 Service 接口
 *
 * @author 宇擎源码
 */
public interface EmployeeResignationBillService {

    /**
     * 保存员工离职申请单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long saveEmployeeResignationBill(@Valid EmployeeResignationBillSaveReqVO saveReqVO);

    /**
     * 提交员工离职申请单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long submitEmployeeResignationBill(@Valid EmployeeResignationBillSaveReqVO saveReqVO);

    /**
     * 创建员工离职申请单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createEmployeeResignationBill(@Valid EmployeeResignationBillSaveReqVO createReqVO);

    /**
     * 更新员工离职申请单
     *
     * @param updateReqVO 更新信息
     */
    void updateEmployeeResignationBill(@Valid EmployeeResignationBillSaveReqVO updateReqVO);

    /**
     * 删除员工离职申请单
     *
     * @param id 编号
     */
    void deleteEmployeeResignationBill(Long id);

    /**
     * 批量删除员工离职申请单
     *
     * @param ids 编号列表
     */
    void deleteEmployeeResignationBillListByIds(List<Long> ids);

    /**
     * 获得员工离职申请单
     *
     * @param id 编号
     * @return 员工离职申请单
     */
    EmployeeResignationBillDO getEmployeeResignationBill(Long id);

    /**
     * 获得员工离职申请单（包含附件信息）
     *
     * @param id 编号
     * @return 员工离职申请单响应VO
     */
    EmployeeResignationBillRespVO getEmployeeResignationBillInfo(Long id);

    /**
     * 根据单据编号获得员工离职申请单
     *
     * @param code 单据编号
     * @return 员工离职申请单
     */
    EmployeeResignationBillDO getEmployeeResignationBillByCode(String code);

    /**
     * 获得员工离职申请单分页
     *
     * @param pageReqVO 分页查询
     * @return 员工离职申请单分页
     */
    PageResult<EmployeeResignationBillDO> getEmployeeResignationBillPage(EmployeeResignationBillPageReqVO pageReqVO);

    /**
     * 从离职申请单更新员工档案
     * 
     * 由定时任务调用，根据离职日期统一处理
     *
     * @param resignationBillId 离职申请单ID
     */
    void updateEmployeeFromResignationBill(Long resignationBillId);

}

