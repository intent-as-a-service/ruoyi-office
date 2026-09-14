package cn.iocoder.yudao.module.hrm.service.employee;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.hrm.controller.admin.employee.vo.*;
import cn.iocoder.yudao.module.hrm.dal.dataobject.employee.EmployeeTransferBillDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 人事调动申请单 Service 接口
 *
 * @author 宇擎源码
 */
public interface EmployeeTransferBillService {

    /**
     * 保存人事调动申请单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long saveEmployeeTransferBill(@Valid EmployeeTransferBillSaveReqVO saveReqVO);

    /**
     * 提交人事调动申请单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long submitEmployeeTransferBill(@Valid EmployeeTransferBillSaveReqVO saveReqVO);

    /**
     * 创建人事调动申请单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createEmployeeTransferBill(@Valid EmployeeTransferBillSaveReqVO createReqVO);

    /**
     * 更新人事调动申请单
     *
     * @param updateReqVO 更新信息
     */
    void updateEmployeeTransferBill(@Valid EmployeeTransferBillSaveReqVO updateReqVO);

    /**
     * 删除人事调动申请单
     *
     * @param id 编号
     */
    void deleteEmployeeTransferBill(Long id);

    /**
     * 批量删除人事调动申请单
     *
     * @param ids 编号列表
     */
    void deleteEmployeeTransferBillListByIds(List<Long> ids);

    /**
     * 获得人事调动申请单
     *
     * @param id 编号
     * @return 人事调动申请单
     */
    EmployeeTransferBillDO getEmployeeTransferBill(Long id);

    /**
     * 获得人事调动申请单（包含附件信息）
     *
     * @param id 编号
     * @return 人事调动申请单响应VO
     */
    EmployeeTransferBillRespVO getEmployeeTransferBillInfo(Long id);

    /**
     * 根据单据编号获得人事调动申请单
     *
     * @param code 单据编号
     * @return 人事调动申请单
     */
    EmployeeTransferBillDO getEmployeeTransferBillByCode(String code);

    /**
     * 获得人事调动申请单分页
     *
     * @param pageReqVO 分页查询
     * @return 人事调动申请单分页
     */
    PageResult<EmployeeTransferBillDO> getEmployeeTransferBillPage(EmployeeTransferBillPageReqVO pageReqVO);

    /**
     * 从调动申请单更新员工档案
     *
     * @param transferBillId 调动申请单ID
     */
    void updateEmployeeFromTransferBill(Long transferBillId);

}




