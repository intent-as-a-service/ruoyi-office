package cn.iocoder.yudao.module.hrm.service.employee;

import cn.iocoder.yudao.framework.common.enums.SystemEnum;
import cn.iocoder.yudao.framework.common.util.bill.BillCodeUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.iocoder.yudao.module.bpm.enums.BpmProcessVariableConstants;
import cn.iocoder.yudao.module.bpm.enums.task.BpmTaskStatusEnum;
import cn.iocoder.yudao.module.bpm.util.BpmProcessVariableUtils;
import cn.iocoder.yudao.module.hrm.enums.HrmBillTypeEnum;
import cn.iocoder.yudao.module.hrm.enums.EmployeeStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.time.LocalDate;

import cn.iocoder.yudao.module.hrm.controller.admin.employee.vo.*;
import cn.iocoder.yudao.module.hrm.dal.dataobject.employee.*;
import cn.iocoder.yudao.module.hrm.dal.dataobject.employee.EmployeeDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hrm.dal.mysql.employee.*;
import cn.iocoder.yudao.common.server.attachment.service.AttachmentService;
import cn.iocoder.yudao.common.server.attachment.controller.vo.AttachmentRespVO;
import cn.iocoder.yudao.framework.common.service.FlowBillService;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hrm.enums.ErrorCodeConstants.EMPLOYEE_RESIGNATION_BILL_NOT_EXISTS;
import static cn.iocoder.yudao.module.bpm.enums.task.BpmTaskStatusEnum.APPROVE;

/**
 * 员工离职申请单 Service 实现类
 *
 * @author 宇擎源码
 */
@Slf4j
@Service
@Validated
public class EmployeeResignationBillServiceImpl implements EmployeeResignationBillService, FlowBillService<HrmBillTypeEnum> {

    @Resource
    private EmployeeResignationBillMapper employeeResignationBillMapper;

    @Resource
    private AttachmentService attachmentService;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Resource
    private EmployeeService employeeService;

    @Resource
    private EmployeeMapper employeeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveEmployeeResignationBill(EmployeeResignationBillSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if(StringUtils.isBlank(saveReqVO.getBillCode())){
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.HRM, HrmBillTypeEnum.HRM_EMPLOYEE_RESIGNATION_BILL));
        }

        // 插入或更新
        EmployeeResignationBillDO resignationBill = BeanUtils.toBean(saveReqVO, EmployeeResignationBillDO.class);
        employeeResignationBillMapper.insertOrUpdate(resignationBill);

        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(HrmBillTypeEnum.HRM_EMPLOYEE_RESIGNATION_BILL.getTypeCode(), resignationBill.getId(), saveReqVO.getAttachments());
        }

        // 返回
        return resignationBill.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitEmployeeResignationBill(EmployeeResignationBillSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if(StringUtils.isBlank(saveReqVO.getBillCode())){
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.HRM, HrmBillTypeEnum.HRM_EMPLOYEE_RESIGNATION_BILL));
        }

        // 校验员工档案存在
        validateEmployeeExists(saveReqVO.getEmployeeId());

        // 保存或更新
        EmployeeResignationBillDO resignationBill = BeanUtils.toBean(saveReqVO, EmployeeResignationBillDO.class)
                .setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        employeeResignationBillMapper.insertOrUpdate(resignationBill);

        // 智能提交 BPM 流程（如果流程实例不存在则创建，存在则审批发起人任务）
        Map<String, Object> processInstanceVariables = BpmProcessVariableUtils.buildBillVariables(saveReqVO);
        processInstanceVariables.put(BpmProcessVariableConstants.CAUSE, resignationBill.getName()+"离职申请");
        String processInstanceId = processInstanceApi.submitProcessInstance(Long.valueOf(saveReqVO.getCreator()),
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(HrmBillTypeEnum.HRM_EMPLOYEE_RESIGNATION_BILL.getProcessDefinitionKey())
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(resignationBill.getId()))
        ).getCheckedData();

        // 将工作流的编号，更新到单据中
        employeeResignationBillMapper.updateById(new EmployeeResignationBillDO().setId(resignationBill.getId()).setProcessInstanceId(processInstanceId));
        
        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(HrmBillTypeEnum.HRM_EMPLOYEE_RESIGNATION_BILL.getTypeCode(), resignationBill.getId(), saveReqVO.getAttachments());
        }
        
        // 返回
        return resignationBill.getId();
    }

    @Override
    public Long createEmployeeResignationBill(EmployeeResignationBillSaveReqVO createReqVO) {
        // 插入
        String billCode = BillCodeUtils.generateBillCode(SystemEnum.HRM, HrmBillTypeEnum.HRM_EMPLOYEE_RESIGNATION_BILL);
        createReqVO.setBillCode(billCode);
        // 校验员工档案存在
        validateEmployeeExists(createReqVO.getEmployeeId());
        // 插入
        EmployeeResignationBillDO resignationBill = BeanUtils.toBean(createReqVO, EmployeeResignationBillDO.class);
        employeeResignationBillMapper.insertOrUpdate(resignationBill);

        // 返回
        return resignationBill.getId();
    }

    @Override
    public void updateEmployeeResignationBill(EmployeeResignationBillSaveReqVO updateReqVO) {
        // 校验存在
        validateEmployeeResignationBillExists(updateReqVO.getId());
        // 更新
        EmployeeResignationBillDO updateObj = BeanUtils.toBean(updateReqVO, EmployeeResignationBillDO.class);
        employeeResignationBillMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployeeResignationBill(Long id) {
        // 校验存在
        validateEmployeeResignationBillExists(id);
        
        // 删除主表
        employeeResignationBillMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployeeResignationBillListByIds(List<Long> ids) {
        // 删除主表
        employeeResignationBillMapper.deleteByIds(ids);
    }

    private EmployeeResignationBillDO validateEmployeeResignationBillExists(Long id) {
        EmployeeResignationBillDO bill = employeeResignationBillMapper.selectById(id);
        if (bill == null) {
            throw exception(EMPLOYEE_RESIGNATION_BILL_NOT_EXISTS);
        }
        return bill;
    }

    private void validateEmployeeExists(Long employeeId) {
        if (employeeMapper.selectById(employeeId) == null) {
            throw exception(cn.iocoder.yudao.module.hrm.enums.ErrorCodeConstants.EMPLOYEE_ARCHIVE_NOT_EXISTS);
        }
    }

    @Override
    public EmployeeResignationBillDO getEmployeeResignationBill(Long id) {
        return employeeResignationBillMapper.selectById(id);
    }

    @Override
    public EmployeeResignationBillRespVO getEmployeeResignationBillInfo(Long id) {
        EmployeeResignationBillDO resignationBill = employeeResignationBillMapper.selectById(id);
        if (resignationBill == null) {
            return null;
        }
        
        EmployeeResignationBillRespVO respVO = BeanUtils.toBean(resignationBill, EmployeeResignationBillRespVO.class);
        
        // 获取附件信息
        respVO.setAttachments(BeanUtils.toBean(
            attachmentService.getAttachmentListByBusiness(HrmBillTypeEnum.HRM_EMPLOYEE_RESIGNATION_BILL.getTypeCode(), id),
            AttachmentRespVO.class
        ));
        
        return respVO;
    }
    
    @Override
    public EmployeeResignationBillDO getEmployeeResignationBillByCode(String code) {
        return employeeResignationBillMapper.selectOne(new LambdaQueryWrapperX<EmployeeResignationBillDO>().eq(EmployeeResignationBillDO::getBillCode, code));
    }

    @Override
    public PageResult<EmployeeResignationBillDO> getEmployeeResignationBillPage(EmployeeResignationBillPageReqVO pageReqVO) {
        // 自动添加创建人过滤条件（当前登录用户）
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (currentUserId != null) {
            pageReqVO.setCreator(String.valueOf(currentUserId));
        }
        return employeeResignationBillMapper.selectPage(pageReqVO);
    }

    // ==================== FlowBillService 接口实现 ====================

    @Override
    public HrmBillTypeEnum getSupportedBillType() {
        return HrmBillTypeEnum.HRM_EMPLOYEE_RESIGNATION_BILL;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        log.info("[updateProcessStatus] 更新员工离职申请单流程状态，id: {}, status: {}", id, status);

        // 校验员工离职申请单存在
        EmployeeResignationBillDO bill = validateEmployeeResignationBillExists(id);

        // 更新流程状态
        EmployeeResignationBillDO updateObj = new EmployeeResignationBillDO();
        updateObj.setId(id);
        updateObj.setProcessStatus(status);
        
        // 注意：审批通过后不立即更新员工档案，由定时任务根据离职日期统一处理
        // 定时任务：EmployeeResignationByResignationDateJob
        
        employeeResignationBillMapper.updateById(updateObj);

        log.info("[updateProcessStatus] 员工离职申请单流程状态更新成功，id: {}, status: {}", id, status);
    }

    /**
     * 从离职申请单更新员工档案
     * 
     * 由定时任务调用，根据离职日期统一处理
     *
     * @param resignationBillId 离职申请单ID
     */
    public void updateEmployeeFromResignationBill(Long resignationBillId) {
        // 获取离职申请单信息
        EmployeeResignationBillRespVO resignationBillRespVO = getEmployeeResignationBillInfo(resignationBillId);
        if (resignationBillRespVO == null) {
            log.error("[updateEmployeeFromResignationBill] 离职申请单不存在，id: {}", resignationBillId);
            return;
        }

        // 获取员工档案
        EmployeeDO employee = employeeMapper.selectById(resignationBillRespVO.getEmployeeId());
        if (employee == null) {
            log.error("[updateEmployeeFromResignationBill] 员工档案不存在，employeeId: {}", resignationBillRespVO.getEmployeeId());
            return;
        }

        // 如果员工已经是离职状态，则不需要重复更新
        if (employee.getEmployeeStatus() != null && EmployeeStatusEnum.isResigned(employee.getEmployeeStatus())) {
            log.info("[updateEmployeeFromResignationBill] 员工已经是离职状态，无需重复更新，resignationBillId: {}, employeeId: {}", 
                    resignationBillId, employee.getId());
            return;
        }

        // 更新员工档案：状态改为离职
        EmployeeDO updateEmployee = new EmployeeDO();
        updateEmployee.setId(employee.getId());
        updateEmployee.setEmployeeStatus(EmployeeStatusEnum.RESIGNED.getStatus());
        
        employeeMapper.updateById(updateEmployee);
        log.info("[updateEmployeeFromResignationBill] 从离职申请单更新员工档案成功，resignationBillId: {}, employeeId: {}", 
                resignationBillId, employee.getId());
    }

}

