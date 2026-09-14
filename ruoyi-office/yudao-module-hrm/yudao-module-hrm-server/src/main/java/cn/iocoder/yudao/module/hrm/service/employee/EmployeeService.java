package cn.iocoder.yudao.module.hrm.service.employee;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.hrm.controller.admin.employee.vo.EmployeePageReqVO;
import cn.iocoder.yudao.module.hrm.controller.admin.employee.vo.EmployeeRespVO;
import cn.iocoder.yudao.module.hrm.controller.admin.employee.vo.EmployeeSelectPageReqVO;
import cn.iocoder.yudao.module.hrm.controller.admin.employee.vo.EmployeeSaveReqVO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 员工档案 Service 接口
 *
 * @author 宇擎源码
 */
public interface EmployeeService {

    /**
     * 创建员工档案
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createEmployeeArchive(@Valid EmployeeSaveReqVO createReqVO);

    /**
     * 更新员工档案
     *
     * @param updateReqVO 更新信息
     */
    void updateEmployeeArchive(@Valid EmployeeSaveReqVO updateReqVO);

    /**
     * 删除员工档案
     *
     * @param id 编号
     */
    void deleteEmployeeArchive(Long id);

    /**
     * 批量删除员工档案
     *
     * @param ids 编号列表
     */
    void deleteEmployeeArchiveList(List<Long> ids);

    /**
     * 获得员工档案
     *
     * @param id 编号
     * @return 员工档案
     */
    EmployeeRespVO getEmployeeArchive(Long id);

    /**
     * 获得员工档案分页
     *
     * @param pageReqVO 分页查询
     * @return 员工档案分页
     */
    PageResult<EmployeeRespVO> getEmployeeArchivePage(EmployeePageReqVO pageReqVO);

    /**
     * 获得可选择的员工档案分页（过滤正式员工）
     *
     * @param pageReqVO 分页查询
     * @return 员工档案分页
     */
    PageResult<EmployeeRespVO> getEmployeeArchiveSelectablePage(EmployeeSelectPageReqVO pageReqVO);

    /**
     * 为员工生成系统用户
     *
     * @param employeeId 员工编号
     * @return 生成的用户编号
     */
    Long generateUserForEmployee(Long employeeId);

    /**
     * 批量为员工生成系统用户
     *
     * @param employeeIds 员工编号列表
     * @return 生成结果（成功数量、失败数量）
     */
    void batchGenerateUserForEmployee(List<Long> employeeIds);

}

