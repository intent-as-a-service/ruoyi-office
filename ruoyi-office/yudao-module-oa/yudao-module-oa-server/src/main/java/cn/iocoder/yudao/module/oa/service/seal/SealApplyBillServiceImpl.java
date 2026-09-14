package cn.iocoder.yudao.module.oa.service.seal;

import cn.iocoder.yudao.framework.common.enums.SystemEnum;
import cn.iocoder.yudao.framework.common.util.bill.BillCodeUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmTaskStatusEnum;
import cn.iocoder.yudao.module.oa.enums.OaBillTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.oa.controller.admin.seal.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.seal.SealApplyBillDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.oa.dal.mysql.seal.SealApplyBillMapper;
import cn.iocoder.yudao.common.server.attachment.service.AttachmentService;
import cn.iocoder.yudao.common.server.attachment.controller.vo.AttachmentRespVO;

import cn.iocoder.yudao.framework.common.service.FlowBillService;

import cn.iocoder.yudao.module.oa.enums.SealUseStatusEnum;
import cn.iocoder.yudao.module.bpm.util.BpmProcessVariableUtils;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.oa.enums.ErrorCodeConstants.*;
import static cn.iocoder.yudao.module.bpm.enums.task.BpmTaskStatusEnum.RUNNING;
import static cn.iocoder.yudao.module.bpm.enums.task.BpmTaskStatusEnum.APPROVE;
import static cn.iocoder.yudao.module.oa.enums.OaProcessVariableConstants.*;

/**
 * 用印申请单 Service 实现类
 *
 * @author 宇擎源码
 */
@Slf4j
@Service
@Validated
public class SealApplyBillServiceImpl implements SealApplyBillService, FlowBillService<OaBillTypeEnum> {

    @Resource
    private SealApplyBillMapper sealApplyBillMapper;

    @Resource
    private AttachmentService attachmentService;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Override
    public Long saveSealApplyBill(SealApplyBillSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if(StringUtils.isBlank(saveReqVO.getBillCode())){
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_SEAL_APPLY_BILL));
        }

        // 插入或更新
        SealApplyBillDO sealApplyBill = BeanUtils.toBean(saveReqVO, SealApplyBillDO.class);
        sealApplyBillMapper.insertOrUpdate(sealApplyBill);

        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_SEAL_APPLY_BILL.getTypeCode(), sealApplyBill.getId(), saveReqVO.getAttachments());
        }

        // 返回
        return sealApplyBill.getId();
    }

    @Override
    public Long submitSealApplyBill(SealApplyBillSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if(StringUtils.isBlank(saveReqVO.getBillCode())){
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_SEAL_APPLY_BILL));
        }

        // 校验时间冲突（仅外借用章需要校验）
        if (saveReqVO.getUseMode() != null && saveReqVO.getUseMode() == 2) {
            validateTimeConflict(saveReqVO);
        }

        // 保存或更新
        SealApplyBillDO sealApplyBill = BeanUtils.toBean(saveReqVO, SealApplyBillDO.class)
                .setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        sealApplyBillMapper.insertOrUpdate(sealApplyBill);

        // 智能提交 BPM 流程（如果流程实例不存在则创建，存在则审批发起人任务）
        Map<String, Object> processInstanceVariables = BpmProcessVariableUtils.buildBillVariables(saveReqVO);
        // 添加用印申请单特有的流程变量
        processInstanceVariables.put(PV_SEAL_USE_MODE, saveReqVO.getUseMode());
        String processInstanceId = processInstanceApi.submitProcessInstance(Long.valueOf(saveReqVO.getCreator()),
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(OaBillTypeEnum.OA_SEAL_APPLY_BILL.getProcessDefinitionKey())
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(sealApplyBill.getId()))
        ).getCheckedData();

        // 将工作流的编号，更新到单据中
        sealApplyBillMapper.updateById(new SealApplyBillDO().setId(sealApplyBill.getId()).setProcessInstanceId(processInstanceId));
        
        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_SEAL_APPLY_BILL.getTypeCode(), sealApplyBill.getId(), saveReqVO.getAttachments());
        }
        
        // 返回
        return sealApplyBill.getId();
    }

    @Override
    public Long createSealApplyBill(SealApplyBillSaveReqVO createReqVO) {
        // 插入
        String billCode = BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_SEAL_APPLY_BILL);
        createReqVO.setBillCode(billCode);
        // 插入
        SealApplyBillDO sealApplyBill = BeanUtils.toBean(createReqVO, SealApplyBillDO.class);
        sealApplyBillMapper.insertOrUpdate(sealApplyBill);

        // 返回
        return sealApplyBill.getId();
    }

    @Override
    public void updateSealApplyBill(SealApplyBillSaveReqVO updateReqVO) {
        // 校验存在
        validateSealApplyBillExists(updateReqVO.getId());
        // 更新
        SealApplyBillDO updateObj = BeanUtils.toBean(updateReqVO, SealApplyBillDO.class);
        sealApplyBillMapper.updateById(updateObj);
    }

    @Override
    public void deleteSealApplyBill(Long id) {
        // 校验存在
        validateSealApplyBillExists(id);
        // 删除
        sealApplyBillMapper.deleteById(id);
    }

    @Override
    public void deleteSealApplyBillListByIds(List<Long> ids) {
        // 删除
        sealApplyBillMapper.deleteByIds(ids);
    }

    private void validateSealApplyBillExists(Long id) {
        if (sealApplyBillMapper.selectById(id) == null) {
            throw exception(SEAL_APPLY_BILL_NOT_EXISTS);
        }
    }

    @Override
    public SealApplyBillDO getSealApplyBill(Long id) {
        return sealApplyBillMapper.selectById(id);
    }

    @Override
    public SealApplyBillRespVO getSealApplyBillInfo(Long id) {
        SealApplyBillDO sealApplyBill = sealApplyBillMapper.selectById(id);
        if (sealApplyBill == null) {
            return null;
        }
        
        SealApplyBillRespVO respVO = BeanUtils.toBean(sealApplyBill, SealApplyBillRespVO.class);
        
        // 获取附件信息
        respVO.setAttachments(BeanUtils.toBean(
            attachmentService.getAttachmentListByBusiness(OaBillTypeEnum.OA_SEAL_APPLY_BILL.getTypeCode(), id),
            AttachmentRespVO.class
        ));
        
        return respVO;
    }
    
    @Override
    public SealApplyBillDO getSealApplyBillByCode(String code) {
        return sealApplyBillMapper.selectOne(new LambdaQueryWrapperX<SealApplyBillDO>().eq(SealApplyBillDO::getBillCode, code));
    }

    @Override
    public PageResult<SealApplyBillDO> getSealApplyBillPage(SealApplyBillPageReqVO pageReqVO) {
        // 自动添加创建人过滤条件（当前登录用户）
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (currentUserId != null) {
            pageReqVO.setCreator(String.valueOf(currentUserId));
        }
        return sealApplyBillMapper.selectPage(pageReqVO);
    }

    // ==================== FlowBillService 接口实现 ====================

    @Override
    public OaBillTypeEnum getSupportedBillType() {
        return OaBillTypeEnum.OA_SEAL_APPLY_BILL;
    }

    @Override
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        log.info("[updateProcessStatus] 更新用印申请单流程状态，id: {}, status: {}", id, status);

        // 校验用印申请单存在
        validateSealApplyBillExists(id);

        // 更新流程状态
        SealApplyBillDO updateObj = new SealApplyBillDO();
        updateObj.setId(id);
        updateObj.setProcessStatus(status);
        
        // 如果审批通过，设置用印状态为待处理
        if (APPROVE.getStatus().equals(status)) {
            updateObj.setUseStatus(SealUseStatusEnum.PENDING.getStatus());
        }
        
        sealApplyBillMapper.updateById(updateObj);

        log.info("[updateProcessStatus] 用印申请单流程状态更新成功，id: {}, status: {}", id, status);
    }

    @Override
    public void updateUseStatus(Long id, Integer useStatus) {
        log.info("[updateUseStatus] 更新用印申请单用印状态，id: {}, useStatus: {}", id, useStatus);
        
        // 校验用印申请单存在
        validateSealApplyBillExists(id);
        
        // 更新用印状态
        SealApplyBillDO updateObj = new SealApplyBillDO();
        updateObj.setId(id);
        updateObj.setUseStatus(useStatus);
        sealApplyBillMapper.updateById(updateObj);
        
        log.info("[updateUseStatus] 用印申请单用印状态更新成功，id: {}, useStatus: {}", id, useStatus);
    }

    @Override
    public void markAsCompleted(Long id) {
        updateUseStatus(id, SealUseStatusEnum.COMPLETED.getStatus());
    }
    
    @Override
    public void markAsBorrowed(Long id) {
        updateUseStatus(id, SealUseStatusEnum.BORROWED.getStatus());
    }
    
    @Override
    public void markAsReturned(Long id) {
        updateUseStatus(id, SealUseStatusEnum.RETURNED.getStatus());
    }
    
    @Override
    public void markAsOverdue(Long id) {
        updateUseStatus(id, SealUseStatusEnum.OVERDUE.getStatus());
    }

    /**
     * 校验印章使用时间冲突（仅外借用章需要校验）
     *
     * @param saveReqVO 保存请求VO
     */
    private void validateTimeConflict(SealApplyBillSaveReqVO saveReqVO) {
        if (saveReqVO.getSealId() == null || saveReqVO.getExpectedUseTime() == null || saveReqVO.getExpectedReturnTime() == null) {
            return; // 如果必要字段为空，跳过校验
        }

        // 校验预计用章时间不能晚于预计归还时间
        if (saveReqVO.getExpectedUseTime().isAfter(saveReqVO.getExpectedReturnTime())) {
            throw exception(SEAL_TIME_CONFLICT);
        }

        // 查询同一印章在相同时间段内的申请单（仅外借用章）
        List<SealApplyBillDO> conflictBills = sealApplyBillMapper.selectList(
                new LambdaQueryWrapperX<SealApplyBillDO>()
                        .eq(SealApplyBillDO::getSealId, saveReqVO.getSealId())
                        .eq(SealApplyBillDO::getUseMode, 2) // 仅检查外借用章
                        .ne(saveReqVO.getId() != null, SealApplyBillDO::getId, saveReqVO.getId()) // 排除当前编辑的记录
                        .and(wrapper -> wrapper
                                // 场景1：存在审批中的申请单且时间重合
                                .and(subWrapper -> subWrapper
                                        .eq(SealApplyBillDO::getProcessStatus, RUNNING.getStatus())
                                        .and(timeWrapper -> timeWrapper
                                                .and(timeWrapper2 -> timeWrapper2
                                                        .le(SealApplyBillDO::getExpectedUseTime, saveReqVO.getExpectedUseTime())
                                                        .ge(SealApplyBillDO::getExpectedReturnTime, saveReqVO.getExpectedUseTime())
                                                )
                                                .or(timeWrapper3 -> timeWrapper3
                                                        .le(SealApplyBillDO::getExpectedUseTime, saveReqVO.getExpectedReturnTime())
                                                        .ge(SealApplyBillDO::getExpectedReturnTime, saveReqVO.getExpectedReturnTime())
                                                )
                                                .or(timeWrapper4 -> timeWrapper4
                                                        .ge(SealApplyBillDO::getExpectedUseTime, saveReqVO.getExpectedUseTime())
                                                        .le(SealApplyBillDO::getExpectedReturnTime, saveReqVO.getExpectedReturnTime())
                                                )
                                        )
                                )
                                // 场景2：存在审批通过且用印状态为外借中的申请单且时间重合
                                .or(subWrapper -> subWrapper
                                        .eq(SealApplyBillDO::getProcessStatus, APPROVE.getStatus())
                                        .eq(SealApplyBillDO::getUseStatus, SealUseStatusEnum.BORROWED.getStatus())
                                        .and(timeWrapper -> timeWrapper
                                                .and(timeWrapper2 -> timeWrapper2
                                                        .le(SealApplyBillDO::getExpectedUseTime, saveReqVO.getExpectedUseTime())
                                                        .ge(SealApplyBillDO::getExpectedReturnTime, saveReqVO.getExpectedUseTime())
                                                )
                                                .or(timeWrapper3 -> timeWrapper3
                                                        .le(SealApplyBillDO::getExpectedUseTime, saveReqVO.getExpectedReturnTime())
                                                        .ge(SealApplyBillDO::getExpectedReturnTime, saveReqVO.getExpectedReturnTime())
                                                )
                                                .or(timeWrapper4 -> timeWrapper4
                                                        .ge(SealApplyBillDO::getExpectedUseTime, saveReqVO.getExpectedUseTime())
                                                        .le(SealApplyBillDO::getExpectedReturnTime, saveReqVO.getExpectedReturnTime())
                                                )
                                        )
                                )
                        )
        );

        if (!conflictBills.isEmpty()) {
            throw exception(SEAL_TIME_CONFLICT);
        }
    }

}
