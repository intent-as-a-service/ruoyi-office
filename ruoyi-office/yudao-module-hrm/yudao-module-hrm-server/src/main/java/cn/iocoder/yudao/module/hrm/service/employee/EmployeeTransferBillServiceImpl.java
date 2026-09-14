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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

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
import static cn.iocoder.yudao.module.hrm.enums.ErrorCodeConstants.EMPLOYEE_TRANSFER_BILL_NOT_EXISTS;
import static cn.iocoder.yudao.module.bpm.enums.task.BpmTaskStatusEnum.APPROVE;

/**
 * 人事调动申请单 Service 实现类
 *
 * @author 宇擎源码
 */
@Slf4j
@Service
@Validated
public class EmployeeTransferBillServiceImpl implements EmployeeTransferBillService, FlowBillService<HrmBillTypeEnum> {

    @Resource
    private EmployeeTransferBillMapper employeeTransferBillMapper;

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
    public Long saveEmployeeTransferBill(EmployeeTransferBillSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if(StringUtils.isBlank(saveReqVO.getBillCode())){
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.HRM, HrmBillTypeEnum.HRM_EMPLOYEE_TRANSFER_BILL));
        }
        

        // 插入或更新
        EmployeeTransferBillDO transferBill = BeanUtils.toBean(saveReqVO, EmployeeTransferBillDO.class);
        employeeTransferBillMapper.insertOrUpdate(transferBill);

        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(HrmBillTypeEnum.HRM_EMPLOYEE_TRANSFER_BILL.getTypeCode(), transferBill.getId(), saveReqVO.getAttachments());
        }

        // 返回
        return transferBill.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitEmployeeTransferBill(EmployeeTransferBillSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if(StringUtils.isBlank(saveReqVO.getBillCode())){
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.HRM, HrmBillTypeEnum.HRM_EMPLOYEE_TRANSFER_BILL));
        }

        // 校验员工档案存在
        validateEmployeeExists(saveReqVO.getEmployeeId());

        // 保存或更新
        EmployeeTransferBillDO transferBill = BeanUtils.toBean(saveReqVO, EmployeeTransferBillDO.class)
                .setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        employeeTransferBillMapper.insertOrUpdate(transferBill);

        // 智能提交 BPM 流程（如果流程实例不存在则创建，存在则审批发起人任务）
        Map<String, Object> processInstanceVariables = BpmProcessVariableUtils.buildBillVariables(saveReqVO);
        processInstanceVariables.put(BpmProcessVariableConstants.CAUSE, transferBill.getName()+"人事调动申请");
        String processInstanceId = processInstanceApi.submitProcessInstance(Long.valueOf(saveReqVO.getCreator()),
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(HrmBillTypeEnum.HRM_EMPLOYEE_TRANSFER_BILL.getProcessDefinitionKey())
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(transferBill.getId()))
        ).getCheckedData();

        // 将工作流的编号，更新到单据中
        employeeTransferBillMapper.updateById(new EmployeeTransferBillDO().setId(transferBill.getId()).setProcessInstanceId(processInstanceId));
        
        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(HrmBillTypeEnum.HRM_EMPLOYEE_TRANSFER_BILL.getTypeCode(), transferBill.getId(), saveReqVO.getAttachments());
        }
        
        // 返回
        return transferBill.getId();
    }

    @Override
    public Long createEmployeeTransferBill(EmployeeTransferBillSaveReqVO createReqVO) {
        // 插入
        String billCode = BillCodeUtils.generateBillCode(SystemEnum.HRM, HrmBillTypeEnum.HRM_EMPLOYEE_TRANSFER_BILL);
        createReqVO.setBillCode(billCode);
        // 校验员工档案存在
        validateEmployeeExists(createReqVO.getEmployeeId());
        // 插入
        EmployeeTransferBillDO transferBill = BeanUtils.toBean(createReqVO, EmployeeTransferBillDO.class);
        employeeTransferBillMapper.insertOrUpdate(transferBill);

        // 返回
        return transferBill.getId();
    }

    @Override
    public void updateEmployeeTransferBill(EmployeeTransferBillSaveReqVO updateReqVO) {
        // 校验存在
        validateEmployeeTransferBillExists(updateReqVO.getId());
        // 更新
        EmployeeTransferBillDO updateObj = BeanUtils.toBean(updateReqVO, EmployeeTransferBillDO.class);
        employeeTransferBillMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployeeTransferBill(Long id) {
        // 校验存在
        validateEmployeeTransferBillExists(id);
        
        // 删除主表
        employeeTransferBillMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployeeTransferBillListByIds(List<Long> ids) {
        // 删除主表
        employeeTransferBillMapper.deleteByIds(ids);
    }

    private EmployeeTransferBillDO validateEmployeeTransferBillExists(Long id) {
        EmployeeTransferBillDO bill = employeeTransferBillMapper.selectById(id);
        if (bill == null) {
            throw exception(EMPLOYEE_TRANSFER_BILL_NOT_EXISTS);
        }
        return bill;
    }

    private void validateEmployeeExists(Long employeeId) {
        if (employeeMapper.selectById(employeeId) == null) {
            throw exception(cn.iocoder.yudao.module.hrm.enums.ErrorCodeConstants.EMPLOYEE_ARCHIVE_NOT_EXISTS);
        }
    }

    @Override
    public EmployeeTransferBillDO getEmployeeTransferBill(Long id) {
        return employeeTransferBillMapper.selectById(id);
    }

    @Override
    public EmployeeTransferBillRespVO getEmployeeTransferBillInfo(Long id) {
        EmployeeTransferBillDO transferBill = employeeTransferBillMapper.selectById(id);
        if (transferBill == null) {
            return null;
        }
        
        EmployeeTransferBillRespVO respVO = BeanUtils.toBean(transferBill, EmployeeTransferBillRespVO.class);
        
        // 获取附件信息
        respVO.setAttachments(BeanUtils.toBean(
            attachmentService.getAttachmentListByBusiness(HrmBillTypeEnum.HRM_EMPLOYEE_TRANSFER_BILL.getTypeCode(), id),
            AttachmentRespVO.class
        ));
        
        return respVO;
    }
    
    @Override
    public EmployeeTransferBillDO getEmployeeTransferBillByCode(String code) {
        return employeeTransferBillMapper.selectOne(new LambdaQueryWrapperX<EmployeeTransferBillDO>().eq(EmployeeTransferBillDO::getBillCode, code));
    }

    @Override
    public PageResult<EmployeeTransferBillDO> getEmployeeTransferBillPage(EmployeeTransferBillPageReqVO pageReqVO) {
        // 自动添加创建人过滤条件（当前登录用户）
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (currentUserId != null) {
            pageReqVO.setCreator(String.valueOf(currentUserId));
        }
        return employeeTransferBillMapper.selectPage(pageReqVO);
    }

    // ==================== FlowBillService 接口实现 ====================

    @Override
    public HrmBillTypeEnum getSupportedBillType() {
        return HrmBillTypeEnum.HRM_EMPLOYEE_TRANSFER_BILL;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        log.info("[updateProcessStatus] 更新人事调动申请单流程状态，id: {}, status: {}", id, status);

        // 校验人事调动申请单存在
        EmployeeTransferBillDO bill = validateEmployeeTransferBillExists(id);

        // 更新流程状态
        EmployeeTransferBillDO updateObj = new EmployeeTransferBillDO();
        updateObj.setId(id);
        updateObj.setProcessStatus(status);

        // 如果审批通过，根据是否立即生效更新员工档案
        if (APPROVE.getStatus().equals(status)) {
            if (Boolean.TRUE.equals(bill.getEffectiveImmediately())) {
                updateEmployeeFromTransferBill(id);
            }
        }

        employeeTransferBillMapper.updateById(updateObj);

        log.info("[updateProcessStatus] 人事调动申请单流程状态更新成功，id: {}, status: {}", id, status);
    }

    /**
     * 从调动申请单更新员工档案
     *
     * @param transferBillId 调动申请单ID
     */
    @Override
    public void updateEmployeeFromTransferBill(Long transferBillId) {
        // 获取调动申请单信息
        EmployeeTransferBillRespVO transferBillRespVO = getEmployeeTransferBillInfo(transferBillId);
        if (transferBillRespVO == null) {
            log.error("[updateEmployeeFromTransferBill] 调动申请单不存在，id: {}", transferBillId);
            return;
        }

        // 获取员工档案
        EmployeeDO employee = employeeMapper.selectById(transferBillRespVO.getEmployeeId());
        if (employee == null) {
            log.error("[updateEmployeeFromTransferBill] 员工档案不存在，employeeId: {}", transferBillRespVO.getEmployeeId());
            return;
        }

        // 更新员工档案：根据调动信息更新部门、公司、职位、职务等
        EmployeeDO updateEmployee = new EmployeeDO();
        updateEmployee.setId(employee.getId());
        
        // 更新部门信息
        if (transferBillRespVO.getNewDeptId() != null) {
            updateEmployee.setDeptId(transferBillRespVO.getNewDeptId());
            updateEmployee.setDeptName(transferBillRespVO.getNewDeptName());
        }
        
        // 更新公司信息
        if (transferBillRespVO.getNewCompanyId() != null) {
            updateEmployee.setCompanyId(transferBillRespVO.getNewCompanyId());
            updateEmployee.setCompanyName(transferBillRespVO.getNewCompanyName());
        }
        
        // 更新职位
        if (StringUtils.isNotBlank(transferBillRespVO.getNewJobPost())) {
            updateEmployee.setJobPost(transferBillRespVO.getNewJobPost());
        }
        
        // 更新职务
        if (StringUtils.isNotBlank(transferBillRespVO.getNewJobPosition())) {
            updateEmployee.setJobPosition(transferBillRespVO.getNewJobPosition());
        }
        
        employeeMapper.updateById(updateEmployee);
        log.info("[updateEmployeeFromTransferBill] 从调动申请单更新员工档案成功，transferBillId: {}, employeeId: {}, newDeptId: {}, newCompanyId: {}", 
                transferBillId, employee.getId(), transferBillRespVO.getNewDeptId(), transferBillRespVO.getNewCompanyId());
    }

}




