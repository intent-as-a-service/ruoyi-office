package cn.iocoder.yudao.module.hrm.service.employee;

import cn.iocoder.yudao.framework.common.enums.SystemEnum;
import cn.iocoder.yudao.framework.common.util.bill.BillCodeUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
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

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import cn.iocoder.yudao.module.hrm.controller.admin.employee.vo.*;
import cn.iocoder.yudao.module.hrm.dal.dataobject.employee.*;
import cn.iocoder.yudao.module.hrm.dal.dataobject.employee.EmployeeDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hrm.dal.mysql.employee.*;
import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.common.server.attachment.service.AttachmentService;
import cn.iocoder.yudao.common.server.attachment.controller.vo.AttachmentRespVO;
import cn.iocoder.yudao.framework.common.service.FlowBillService;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hrm.enums.ErrorCodeConstants.EMPLOYEE_ENTRY_BILL_ID_CARD_EXISTS;
import static cn.iocoder.yudao.module.hrm.enums.ErrorCodeConstants.EMPLOYEE_ENTRY_BILL_MOBILE_EXISTS;
import static cn.iocoder.yudao.module.hrm.enums.ErrorCodeConstants.EMPLOYEE_ENTRY_BILL_NOT_EXISTS;
import static cn.iocoder.yudao.module.bpm.enums.task.BpmTaskStatusEnum.APPROVE;

/**
 * 员工入职申请单 Service 实现类
 *
 * @author 宇擎源码
 */
@Slf4j
@Service
@Validated
public class EmployeeEntryBillServiceImpl implements EmployeeEntryBillService, FlowBillService<HrmBillTypeEnum> {

    @Resource
    private EmployeeEntryBillMapper employeeEntryBillMapper;

    @Resource
    private AttachmentService attachmentService;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Resource
    private EmployeeService employeeService;

    @Resource
    private EmployeeMapper employeeMapper;

    @Resource
    private EmployeeWorkExperienceMapper employeeWorkExperienceMapper;

    @Resource
    private EmployeeEducationMapper employeeEducationMapper;

    @Resource
    private EmployeeFamilyMapper employeeFamilyMapper;

    @Resource
    private EmployeeEntryBillWorkExperienceMapper entryBillWorkExperienceMapper;

    @Resource
    private EmployeeEntryBillEducationMapper entryBillEducationMapper;

    @Resource
    private EmployeeEntryBillFamilyMapper entryBillFamilyMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveEmployeeEntryBill(EmployeeEntryBillSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if(StringUtils.isBlank(saveReqVO.getBillCode())){
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.HRM, HrmBillTypeEnum.HRM_EMPLOYEE_ENTRY_BILL));
        }

        // 校验手机号与身份证号在员工档案中唯一
        validateMobileAndIdCardUnique(saveReqVO);

        // 插入或更新
        EmployeeEntryBillDO entryBill = BeanUtils.toBean(saveReqVO, EmployeeEntryBillDO.class);
        employeeEntryBillMapper.insertOrUpdate(entryBill);

        // 保存明细信息
        saveDetailLists(entryBill.getId(), saveReqVO);

        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(HrmBillTypeEnum.HRM_EMPLOYEE_ENTRY_BILL.getTypeCode(), entryBill.getId(), saveReqVO.getAttachments());
        }

        // 返回
        return entryBill.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitEmployeeEntryBill(EmployeeEntryBillSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if(StringUtils.isBlank(saveReqVO.getBillCode())){
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.HRM, HrmBillTypeEnum.HRM_EMPLOYEE_ENTRY_BILL));
        }

        // 校验手机号与身份证号在员工档案中唯一
        validateMobileAndIdCardUnique(saveReqVO);

        // 保存或更新
        EmployeeEntryBillDO entryBill = BeanUtils.toBean(saveReqVO, EmployeeEntryBillDO.class)
                .setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        employeeEntryBillMapper.insertOrUpdate(entryBill);

        // 保存明细信息
        saveDetailLists(entryBill.getId(), saveReqVO);

        // 智能提交 BPM 流程（如果流程实例不存在则创建，存在则审批发起人任务）
        Map<String, Object> processInstanceVariables = BpmProcessVariableUtils.buildBillVariables(saveReqVO);
        processInstanceVariables.put(BpmProcessVariableConstants.CAUSE, entryBill.getName()+"入职申请");
        String processInstanceId = processInstanceApi.submitProcessInstance(Long.valueOf(saveReqVO.getCreator()),
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(HrmBillTypeEnum.HRM_EMPLOYEE_ENTRY_BILL.getProcessDefinitionKey())
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(entryBill.getId()))
        ).getCheckedData();

        // 将工作流的编号，更新到单据中
        employeeEntryBillMapper.updateById(new EmployeeEntryBillDO().setId(entryBill.getId()).setProcessInstanceId(processInstanceId));
        
        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(HrmBillTypeEnum.HRM_EMPLOYEE_ENTRY_BILL.getTypeCode(), entryBill.getId(), saveReqVO.getAttachments());
        }
        
        // 返回
        return entryBill.getId();
    }

    @Override
    public Long createEmployeeEntryBill(EmployeeEntryBillSaveReqVO createReqVO) {
        // 插入
        String billCode = BillCodeUtils.generateBillCode(SystemEnum.HRM, HrmBillTypeEnum.HRM_EMPLOYEE_ENTRY_BILL);
        createReqVO.setBillCode(billCode);
        // 校验手机号与身份证号在员工档案中唯一
        validateMobileAndIdCardUnique(createReqVO);
        // 插入
        EmployeeEntryBillDO entryBill = BeanUtils.toBean(createReqVO, EmployeeEntryBillDO.class);
        employeeEntryBillMapper.insertOrUpdate(entryBill);

        // 返回
        return entryBill.getId();
    }

    @Override
    public void updateEmployeeEntryBill(EmployeeEntryBillSaveReqVO updateReqVO) {
        // 校验存在
        validateEmployeeEntryBillExists(updateReqVO.getId());
        // 更新
        EmployeeEntryBillDO updateObj = BeanUtils.toBean(updateReqVO, EmployeeEntryBillDO.class);
        employeeEntryBillMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployeeEntryBill(Long id) {
        // 校验存在
        validateEmployeeEntryBillExists(id);
        
        // 删除明细信息
        entryBillWorkExperienceMapper.deleteByEntryBillId(id);
        entryBillEducationMapper.deleteByEntryBillId(id);
        entryBillFamilyMapper.deleteByEntryBillId(id);
        
        // 删除主表
        employeeEntryBillMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployeeEntryBillListByIds(List<Long> ids) {
        // 删除明细信息
        for (Long id : ids) {
            entryBillWorkExperienceMapper.deleteByEntryBillId(id);
            entryBillEducationMapper.deleteByEntryBillId(id);
            entryBillFamilyMapper.deleteByEntryBillId(id);
        }
        
        // 删除主表
        employeeEntryBillMapper.deleteByIds(ids);
    }

    private void validateEmployeeEntryBillExists(Long id) {
        if (employeeEntryBillMapper.selectById(id) == null) {
            throw exception(EMPLOYEE_ENTRY_BILL_NOT_EXISTS);
        }
    }

    @Override
    public EmployeeEntryBillDO getEmployeeEntryBill(Long id) {
        return employeeEntryBillMapper.selectById(id);
    }

    @Override
    public EmployeeEntryBillRespVO getEmployeeEntryBillInfo(Long id) {
        EmployeeEntryBillDO entryBill = employeeEntryBillMapper.selectById(id);
        if (entryBill == null) {
            return null;
        }
        
        EmployeeEntryBillRespVO respVO = BeanUtils.toBean(entryBill, EmployeeEntryBillRespVO.class);
        
        // 获取附件信息
        respVO.setAttachments(BeanUtils.toBean(
            attachmentService.getAttachmentListByBusiness(HrmBillTypeEnum.HRM_EMPLOYEE_ENTRY_BILL.getTypeCode(), id),
            AttachmentRespVO.class
        ));
        
        // 获取明细信息：优先从员工档案明细表获取（如果已创建员工档案），否则从入职申请单明细表获取
        if (entryBill.getEmployeeId() != null) {
            // 如果已经创建了员工档案，从员工档案的明细表中获取明细信息
            List<EmployeeWorkExperienceDO> workExperiences = employeeWorkExperienceMapper.selectListByEmployeeId(entryBill.getEmployeeId());
            respVO.setWorkExperienceList(BeanUtils.toBean(workExperiences, EmployeeWorkExperienceVO.class));
            
            List<EmployeeEducationDO> educations = employeeEducationMapper.selectListByEmployeeId(entryBill.getEmployeeId());
            respVO.setEducationList(BeanUtils.toBean(educations, EmployeeEducationVO.class));
            
            List<EmployeeFamilyDO> families = employeeFamilyMapper.selectListByEmployeeId(entryBill.getEmployeeId());
            respVO.setFamilyList(BeanUtils.toBean(families, EmployeeFamilyVO.class));
        } else {
            // 如果还没有创建员工档案，从入职申请单明细表中获取明细信息
            List<EmployeeEntryBillWorkExperienceDO> workExperiences = entryBillWorkExperienceMapper.selectListByEntryBillId(id);
            respVO.setWorkExperienceList(BeanUtils.toBean(workExperiences, EmployeeWorkExperienceVO.class));
            
            List<EmployeeEntryBillEducationDO> educations = entryBillEducationMapper.selectListByEntryBillId(id);
            respVO.setEducationList(BeanUtils.toBean(educations, EmployeeEducationVO.class));
            
            List<EmployeeEntryBillFamilyDO> families = entryBillFamilyMapper.selectListByEntryBillId(id);
            respVO.setFamilyList(BeanUtils.toBean(families, EmployeeFamilyVO.class));
        }
        
        return respVO;
    }
    
    @Override
    public EmployeeEntryBillDO getEmployeeEntryBillByCode(String code) {
        return employeeEntryBillMapper.selectOne(new LambdaQueryWrapperX<EmployeeEntryBillDO>().eq(EmployeeEntryBillDO::getBillCode, code));
    }

    @Override
    public PageResult<EmployeeEntryBillDO> getEmployeeEntryBillPage(EmployeeEntryBillPageReqVO pageReqVO) {
        // 自动添加创建人过滤条件（当前登录用户）
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (currentUserId != null) {
            pageReqVO.setCreator(String.valueOf(currentUserId));
        }
        return employeeEntryBillMapper.selectPage(pageReqVO);
    }

    // ==================== FlowBillService 接口实现 ====================

    @Override
    public HrmBillTypeEnum getSupportedBillType() {
        return HrmBillTypeEnum.HRM_EMPLOYEE_ENTRY_BILL;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        log.info("[updateProcessStatus] 更新员工入职申请单流程状态，id: {}, status: {}", id, status);

        // 校验员工入职申请单存在
        validateEmployeeEntryBillExists(id);

        // 更新流程状态
        EmployeeEntryBillDO updateObj = new EmployeeEntryBillDO();
        updateObj.setId(id);
        updateObj.setProcessStatus(status);
        
        // 如果审批通过，创建员工档案
        if (APPROVE.getStatus().equals(status)) {
            createEmployeeFromEntryBill(id);
        }
        
        employeeEntryBillMapper.updateById(updateObj);

        log.info("[updateProcessStatus] 员工入职申请单流程状态更新成功，id: {}, status: {}", id, status);
    }

    /**
     * 从入职申请单创建员工档案
     *
     * @param entryBillId 入职申请单ID
     */
    private void createEmployeeFromEntryBill(Long entryBillId) {
        // 获取最新的入职申请单信息（包含明细信息）
        EmployeeEntryBillRespVO entryBillRespVO = getEmployeeEntryBillInfo(entryBillId);
        if (entryBillRespVO == null) {
            log.error("[createEmployeeFromEntryBill] 入职申请单不存在，id: {}", entryBillId);
            return;
        }

        // 如果已经创建过员工档案，不再重复创建
        if (entryBillRespVO.getEmployeeId() != null) {
            log.warn("[createEmployeeFromEntryBill] 员工档案已创建，entryBillId: {}, employeeId: {}", entryBillId, entryBillRespVO.getEmployeeId());
            return;
        }
        AtomicReference<EmployeeEntryBillDO> entryBill = new AtomicReference<>(new EmployeeEntryBillDO());
        // 查询入职申请单头信息（包含租户等）
        TenantUtils.executeIgnore(()->{
            entryBill.set(employeeEntryBillMapper.selectById(entryBillId));
        });

        // 构建员工档案保存VO
        EmployeeSaveReqVO employeeSaveReqVO = new EmployeeSaveReqVO();
        // 基本信息
        employeeSaveReqVO.setName(entryBillRespVO.getName());
        employeeSaveReqVO.setSex(entryBillRespVO.getSex());
        employeeSaveReqVO.setBirthday(entryBillRespVO.getBirthday());
        employeeSaveReqVO.setIdCard(entryBillRespVO.getIdCard());
        employeeSaveReqVO.setMobile(entryBillRespVO.getMobile());
        employeeSaveReqVO.setEmail(entryBillRespVO.getEmail());
        employeeSaveReqVO.setNation(entryBillRespVO.getNation());
        employeeSaveReqVO.setNativePlace(entryBillRespVO.getNativePlace());
        employeeSaveReqVO.setHouseholdAddress(entryBillRespVO.getHouseholdAddress());
        employeeSaveReqVO.setCurrentAddress(entryBillRespVO.getCurrentAddress());
        employeeSaveReqVO.setEmergencyContact(entryBillRespVO.getEmergencyContact());
        employeeSaveReqVO.setEmergencyPhone(entryBillRespVO.getEmergencyPhone());
        employeeSaveReqVO.setAvatar(entryBillRespVO.getAvatar());
        employeeSaveReqVO.setPoliticalStatus(entryBillRespVO.getPoliticalStatus());
        employeeSaveReqVO.setMaritalStatus(entryBillRespVO.getMaritalStatus());

        // 工作信息
        employeeSaveReqVO.setEntryDate(entryBillRespVO.getEntryDate());
        employeeSaveReqVO.setDeptId(entryBillRespVO.getEmpDeptId());
        employeeSaveReqVO.setCompanyName(entryBillRespVO.getEmpCompanyName());
        employeeSaveReqVO.setJobPost(entryBillRespVO.getJobPost());
        employeeSaveReqVO.setJobPosition(entryBillRespVO.getJobPosition());
        employeeSaveReqVO.setJobTitle(entryBillRespVO.getJobTitle());
        employeeSaveReqVO.setEmployeeStatus(entryBillRespVO.getEmployeeStatus());
        employeeSaveReqVO.setEducation(entryBillRespVO.getEducation());
        employeeSaveReqVO.setBankName(entryBillRespVO.getBankName());
        employeeSaveReqVO.setBankAccount(entryBillRespVO.getBankAccount());
        employeeSaveReqVO.setRemark(entryBillRespVO.getRemark());

        // 计算转正日期（如果试用期不为空）
        if (entryBill.get() != null) {
            if (entryBill.get().getProbationPeriod() != null && entryBill.get().getEntryDate() != null) {
                employeeSaveReqVO.setFormalDate(entryBill.get().getEntryDate().plusMonths(entryBill.get().getProbationPeriod()));
            } else if (entryBill.get().getExpectedFormalDate() != null) {
                employeeSaveReqVO.setFormalDate(entryBill.get().getExpectedFormalDate());
            }
        }

        // 设置明细信息（从RespVO中获取，如果已创建员工档案则从员工档案明细表获取，否则从入职申请单明细表获取）
        employeeSaveReqVO.setWorkExperienceList(entryBillRespVO.getWorkExperienceList());
        employeeSaveReqVO.setEducationList(entryBillRespVO.getEducationList());
        employeeSaveReqVO.setFamilyList(entryBillRespVO.getFamilyList());

        // 创建员工档案（包含明细信息）
        TenantUtils.execute(entryBill.get().getTenantId(), () -> {
//            TenantContextHolder.setTenantId(entryBill.get().getTenantId());

            Long employeeId = employeeService.createEmployeeArchive(employeeSaveReqVO);

            // 更新入职申请单的employeeId
            EmployeeEntryBillDO updateObj = new EmployeeEntryBillDO();
            updateObj.setId(entryBillId);
            updateObj.setEmployeeId(employeeId);
            employeeEntryBillMapper.updateById(updateObj);
            log.info("[createEmployeeFromEntryBill] 从入职申请单创建员工档案成功，entryBillId: {}, employeeId: {}", entryBillId, employeeId);

        });



    }

    /**
     * 保存明细信息到临时表
     *
     * @param entryBillId 入职申请单ID
     * @param saveReqVO 保存请求VO
     */
    private void saveDetailLists(Long entryBillId, EmployeeEntryBillSaveReqVO saveReqVO) {
        // 删除旧的明细记录
        entryBillWorkExperienceMapper.deleteByEntryBillId(entryBillId);
        entryBillEducationMapper.deleteByEntryBillId(entryBillId);
        entryBillFamilyMapper.deleteByEntryBillId(entryBillId);

        // 保存工作经历
        if (CollUtil.isNotEmpty(saveReqVO.getWorkExperienceList())) {
            List<EmployeeEntryBillWorkExperienceDO> workExperiences = BeanUtils.toBean(saveReqVO.getWorkExperienceList(), EmployeeEntryBillWorkExperienceDO.class);
            workExperiences.forEach(item -> {
                item.setId(null);
                item.setBillId(entryBillId);
                entryBillWorkExperienceMapper.insert(item);
            });
        }

        // 保存教育经历
        if (CollUtil.isNotEmpty(saveReqVO.getEducationList())) {
            List<EmployeeEntryBillEducationDO> educations = BeanUtils.toBean(saveReqVO.getEducationList(), EmployeeEntryBillEducationDO.class);
            educations.forEach(item -> {
                item.setId(null);
                item.setBillId(entryBillId);
                entryBillEducationMapper.insert(item);
            });
        }

        // 保存家属信息
        if (CollUtil.isNotEmpty(saveReqVO.getFamilyList())) {
            List<EmployeeEntryBillFamilyDO> families = BeanUtils.toBean(saveReqVO.getFamilyList(), EmployeeEntryBillFamilyDO.class);
            families.forEach(item -> {
                item.setId(null);
                item.setBillId(entryBillId);
                entryBillFamilyMapper.insert(item);
            });
        }
    }

    /**
     * 校验手机号与身份证号在员工档案表唯一
     *
     * @param reqVO 入职申请单保存/提交请求
     */
    private void validateMobileAndIdCardUnique(EmployeeEntryBillSaveReqVO reqVO) {
        // 校验手机号
        if (StringUtils.isNotBlank(reqVO.getMobile())) {
            Long mobileCount = employeeMapper.selectCount(
                    new LambdaQueryWrapperX<EmployeeDO>()
                            .eq(EmployeeDO::getMobile, reqVO.getMobile())
            );
            if (mobileCount != null && mobileCount > 0) {
                throw exception(EMPLOYEE_ENTRY_BILL_MOBILE_EXISTS);
            }
        }
        // 校验身份证号
        if (StringUtils.isNotBlank(reqVO.getIdCard())) {
            Long idCardCount = employeeMapper.selectCount(
                    new LambdaQueryWrapperX<EmployeeDO>()
                            .eq(EmployeeDO::getIdCard, reqVO.getIdCard())
            );
            if (idCardCount != null && idCardCount > 0) {
                throw exception(EMPLOYEE_ENTRY_BILL_ID_CARD_EXISTS);
            }
        }
    }

}

