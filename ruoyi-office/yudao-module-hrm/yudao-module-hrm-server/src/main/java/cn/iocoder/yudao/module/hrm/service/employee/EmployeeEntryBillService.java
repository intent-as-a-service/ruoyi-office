package cn.iocoder.yudao.module.hrm.service.employee;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.hrm.controller.admin.employee.vo.*;
import cn.iocoder.yudao.module.hrm.dal.dataobject.employee.EmployeeEntryBillDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 员工入职申请单 Service 接口
 *
 * @author 宇擎源码
 */
public interface EmployeeEntryBillService {

    /**
     * 保存员工入职申请单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long saveEmployeeEntryBill(@Valid EmployeeEntryBillSaveReqVO saveReqVO);

    /**
     * 提交员工入职申请单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long submitEmployeeEntryBill(@Valid EmployeeEntryBillSaveReqVO saveReqVO);

    /**
     * 创建员工入职申请单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createEmployeeEntryBill(@Valid EmployeeEntryBillSaveReqVO createReqVO);

    /**
     * 更新员工入职申请单
     *
     * @param updateReqVO 更新信息
     */
    void updateEmployeeEntryBill(@Valid EmployeeEntryBillSaveReqVO updateReqVO);

    /**
     * 删除员工入职申请单
     *
     * @param id 编号
     */
    void deleteEmployeeEntryBill(Long id);

    /**
     * 批量删除员工入职申请单
     *
     * @param ids 编号列表
     */
    void deleteEmployeeEntryBillListByIds(List<Long> ids);

    /**
     * 获得员工入职申请单
     *
     * @param id 编号
     * @return 员工入职申请单
     */
    EmployeeEntryBillDO getEmployeeEntryBill(Long id);

    /**
     * 获得员工入职申请单（包含附件信息）
     *
     * @param id 编号
     * @return 员工入职申请单响应VO
     */
    EmployeeEntryBillRespVO getEmployeeEntryBillInfo(Long id);

    /**
     * 根据单据编号获得员工入职申请单
     *
     * @param code 单据编号
     * @return 员工入职申请单
     */
    EmployeeEntryBillDO getEmployeeEntryBillByCode(String code);

    /**
     * 获得员工入职申请单分页
     *
     * @param pageReqVO 分页查询
     * @return 员工入职申请单分页
     */
    PageResult<EmployeeEntryBillDO> getEmployeeEntryBillPage(EmployeeEntryBillPageReqVO pageReqVO);

}


