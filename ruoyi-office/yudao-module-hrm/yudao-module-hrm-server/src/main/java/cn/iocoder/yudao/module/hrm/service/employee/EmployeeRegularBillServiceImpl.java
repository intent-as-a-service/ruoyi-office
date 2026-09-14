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
import static cn.iocoder.yudao.module.hrm.enums.ErrorCodeConstants.EMPLOYEE_REGULAR_BILL_NOT_EXISTS;
import static cn.iocoder.yudao.module.bpm.enums.task.BpmTaskStatusEnum.APPROVE;

/**
 * 员工转正申请单 Service 实现类
 *
 * @author 宇擎源码
 */
@Slf4j
@Service
@Validated
public class EmployeeRegularBillServiceImpl implements EmployeeRegularBillService, FlowBillService<HrmBillTypeEnum> {

    @Resource
    private EmployeeRegularBillMapper employeeRegularBillMapper;

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
    public Long saveEmployeeRegularBill(EmployeeRegularBillSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if(StringUtils.isBlank(saveReqVO.getBillCode())){
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.HRM, HrmBillTypeEnum.HRM_EMPLOYEE_REGULAR_BILL));
        }

        // 校验员工档案存在
        validateEmployeeExists(saveReqVO.getEmployeeId());

        // 插入或更新
        EmployeeRegularBillDO regularBill = BeanUtils.toBean(saveReqVO, EmployeeRegularBillDO.class);
        employeeRegularBillMapper.insertOrUpdate(regularBill);

        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(HrmBillTypeEnum.HRM_EMPLOYEE_REGULAR_BILL.getTypeCode(), regularBill.getId(), saveReqVO.getAttachments());
        }

        // 返回
        return regularBill.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitEmployeeRegularBill(EmployeeRegularBillSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if(StringUtils.isBlank(saveReqVO.getBillCode())){
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.HRM, HrmBillTypeEnum.HRM_EMPLOYEE_REGULAR_BILL));
        }

        // 校验员工档案存在
        validateEmployeeExists(saveReqVO.getEmployeeId());

        // 保存或更新
        EmployeeRegularBillDO regularBill = BeanUtils.toBean(saveReqVO, EmployeeRegularBillDO.class)
                .setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        employeeRegularBillMapper.insertOrUpdate(regularBill);

        // 智能提交 BPM 流程（如果流程实例不存在则创建，存在则审批发起人任务）
        Map<String, Object> processInstanceVariables = BpmProcessVariableUtils.buildBillVariables(saveReqVO);
        processInstanceVariables.put(BpmProcessVariableConstants.CAUSE, regularBill.getName()+"转正申请");
        String processInstanceId = processInstanceApi.submitProcessInstance(Long.valueOf(saveReqVO.getCreator()),
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(HrmBillTypeEnum.HRM_EMPLOYEE_REGULAR_BILL.getProcessDefinitionKey())
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(regularBill.getId()))
        ).getCheckedData();

        // 将工作流的编号，更新到单据中
        employeeRegularBillMapper.updateById(new EmployeeRegularBillDO().setId(regularBill.getId()).setProcessInstanceId(processInstanceId));
        
        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(HrmBillTypeEnum.HRM_EMPLOYEE_REGULAR_BILL.getTypeCode(), regularBill.getId(), saveReqVO.getAttachments());
        }
        
        // 返回
        return regularBill.getId();
    }

    @Override
    public Long createEmployeeRegularBill(EmployeeRegularBillSaveReqVO createReqVO) {
        // 插入
        String billCode = BillCodeUtils.generateBillCode(SystemEnum.HRM, HrmBillTypeEnum.HRM_EMPLOYEE_REGULAR_BILL);
        createReqVO.setBillCode(billCode);
        // 校验员工档案存在
        validateEmployeeExists(createReqVO.getEmployeeId());
        // 插入
        EmployeeRegularBillDO regularBill = BeanUtils.toBean(createReqVO, EmployeeRegularBillDO.class);
        employeeRegularBillMapper.insertOrUpdate(regularBill);

        // 返回
        return regularBill.getId();
    }

    @Override
    public void updateEmployeeRegularBill(EmployeeRegularBillSaveReqVO updateReqVO) {
        // 校验存在
        validateEmployeeRegularBillExists(updateReqVO.getId());
        // 更新
        EmployeeRegularBillDO updateObj = BeanUtils.toBean(updateReqVO, EmployeeRegularBillDO.class);
        employeeRegularBillMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployeeRegularBill(Long id) {
        // 校验存在
        validateEmployeeRegularBillExists(id);
        
        // 删除主表
        employeeRegularBillMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployeeRegularBillListByIds(List<Long> ids) {
        // 删除主表
        employeeRegularBillMapper.deleteByIds(ids);
    }

    private void validateEmployeeRegularBillExists(Long id) {
        if (employeeRegularBillMapper.selectById(id) == null) {
            throw exception(EMPLOYEE_REGULAR_BILL_NOT_EXISTS);
        }
    }

    private void validateEmployeeExists(Long employeeId) {
        if (employeeMapper.selectById(employeeId) == null) {
            throw exception(cn.iocoder.yudao.module.hrm.enums.ErrorCodeConstants.EMPLOYEE_ARCHIVE_NOT_EXISTS);
        }
    }

    @Override
    public EmployeeRegularBillDO getEmployeeRegularBill(Long id) {
        return employeeRegularBillMapper.selectById(id);
    }

    @Override
    public EmployeeRegularBillRespVO getEmployeeRegularBillInfo(Long id) {
        EmployeeRegularBillDO regularBill = employeeRegularBillMapper.selectById(id);
        if (regularBill == null) {
            return null;
        }
        
        EmployeeRegularBillRespVO respVO = BeanUtils.toBean(regularBill, EmployeeRegularBillRespVO.class);
        
        // 获取附件信息
        respVO.setAttachments(BeanUtils.toBean(
            attachmentService.getAttachmentListByBusiness(HrmBillTypeEnum.HRM_EMPLOYEE_REGULAR_BILL.getTypeCode(), id),
            AttachmentRespVO.class
        ));
        
        return respVO;
    }
    
    @Override
    public EmployeeRegularBillDO getEmployeeRegularBillByCode(String code) {
        return employeeRegularBillMapper.selectOne(new LambdaQueryWrapperX<EmployeeRegularBillDO>().eq(EmployeeRegularBillDO::getBillCode, code));
    }

    @Override
    public PageResult<EmployeeRegularBillDO> getEmployeeRegularBillPage(EmployeeRegularBillPageReqVO pageReqVO) {
        // 自动添加创建人过滤条件（当前登录用户）
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (currentUserId != null) {
            pageReqVO.setCreator(String.valueOf(currentUserId));
        }
        return employeeRegularBillMapper.selectPage(pageReqVO);
    }

    // ==================== FlowBillService 接口实现 ====================

    @Override
    public HrmBillTypeEnum getSupportedBillType() {
        return HrmBillTypeEnum.HRM_EMPLOYEE_REGULAR_BILL;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        log.info("[updateProcessStatus] 更新员工转正申请单流程状态，id: {}, status: {}", id, status);

        // 校验员工转正申请单存在
        validateEmployeeRegularBillExists(id);

        // 更新流程状态
        EmployeeRegularBillDO updateObj = new EmployeeRegularBillDO();
        updateObj.setId(id);
        updateObj.setProcessStatus(status);
        
        // 如果审批通过，更新员工档案
        if (APPROVE.getStatus().equals(status)) {
            updateEmployeeFromRegularBill(id);
        }
        
        employeeRegularBillMapper.updateById(updateObj);

        log.info("[updateProcessStatus] 员工转正申请单流程状态更新成功，id: {}, status: {}", id, status);
    }

    /**
     * 从转正申请单更新员工档案
     *
     * @param regularBillId 转正申请单ID
     */
    private void updateEmployeeFromRegularBill(Long regularBillId) {
        // 获取转正申请单信息
        EmployeeRegularBillRespVO regularBillRespVO = getEmployeeRegularBillInfo(regularBillId);
        if (regularBillRespVO == null) {
            log.error("[updateEmployeeFromRegularBill] 转正申请单不存在，id: {}", regularBillId);
            return;
        }

        // 获取员工档案
        EmployeeDO employee = employeeMapper.selectById(regularBillRespVO.getEmployeeId());
        if (employee == null) {
            log.error("[updateEmployeeFromRegularBill] 员工档案不存在，employeeId: {}", regularBillRespVO.getEmployeeId());
            return;
        }

        // 更新员工档案：状态改为正式，更新转正日期
        EmployeeDO updateEmployee = new EmployeeDO();
        updateEmployee.setId(employee.getId());
        updateEmployee.setEmployeeStatus(EmployeeStatusEnum.FORMAL.getStatus());
        
        // 转正日期：优先使用申请单上的转正日期，如果没有则使用预计转正日期
        LocalDate formalDate = regularBillRespVO.getFormalDate();
        if (formalDate == null) {
            formalDate = regularBillRespVO.getExpectedFormalDate();
        }
        if (formalDate != null) {
            updateEmployee.setFormalDate(formalDate);
        }
        
        employeeMapper.updateById(updateEmployee);
        log.info("[updateEmployeeFromRegularBill] 从转正申请单更新员工档案成功，regularBillId: {}, employeeId: {}, formalDate: {}", 
                regularBillId, employee.getId(), formalDate);
    }

}

