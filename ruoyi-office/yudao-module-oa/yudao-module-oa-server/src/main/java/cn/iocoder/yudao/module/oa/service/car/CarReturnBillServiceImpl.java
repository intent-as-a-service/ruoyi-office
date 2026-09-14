package cn.iocoder.yudao.module.oa.service.car;

import cn.iocoder.yudao.framework.common.enums.SystemEnum;
import cn.iocoder.yudao.framework.common.util.bill.BillCodeUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmTaskStatusEnum;
import cn.iocoder.yudao.module.oa.dal.dataobject.car.CarApplyBillDO;
import cn.iocoder.yudao.module.oa.enums.CarReturnStatusEnum;
import cn.iocoder.yudao.module.oa.enums.OaBillTypeEnum;
import cn.iocoder.yudao.framework.common.service.FlowBillService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.oa.controller.admin.car.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.car.CarReturnBillDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.oa.dal.mysql.car.CarReturnBillMapper;
import cn.iocoder.yudao.common.server.attachment.service.AttachmentService;
import cn.iocoder.yudao.common.server.attachment.controller.vo.AttachmentRespVO;
import cn.iocoder.yudao.module.bpm.util.BpmProcessVariableUtils;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.oa.enums.ErrorCodeConstants.*;

/**
 * 还车申请单 Service 实现类
 *
 * @author 宇擎源码
 */
@Slf4j
@Service
@Validated
public class CarReturnBillServiceImpl implements CarReturnBillService, FlowBillService<OaBillTypeEnum> {

    @Resource
    private CarReturnBillMapper carReturnBillMapper;

    @Resource
    private AttachmentService attachmentService;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Resource
    private CarApplyBillService carApplyBillService;

    @Override
    public Long saveCarReturnBill(CarReturnBillSaveReqVO saveReqVO) {

        // 如果单号为空，需要生成
        if(StringUtils.isBlank(saveReqVO.getBillCode())){
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_CAR_RETURN_BILL));
        }

        // 插入或更新
        CarReturnBillDO carReturnBill = BeanUtils.toBean(saveReqVO, CarReturnBillDO.class);
        carReturnBillMapper.insertOrUpdate(carReturnBill);

        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_CAR_RETURN_BILL.getTypeCode(), carReturnBill.getId(), saveReqVO.getAttachments());
        }

        // 返回
        return carReturnBill.getId();
    }

    @Override
    public Long submitCarReturnBill(CarReturnBillSaveReqVO saveReqVO) {

        // 如果单号为空，需要生成
        if(StringUtils.isBlank(saveReqVO.getBillCode())){
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_CAR_RETURN_BILL));
        }

        // 验证用车申请单是否已还车
        Long applyBillId = validateApplyBillNotReturned(saveReqVO.getApplyBill());

        // 保存或更新
        CarReturnBillDO carReturnBill = BeanUtils.toBean(saveReqVO, CarReturnBillDO.class)
                .setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        carReturnBillMapper.insertOrUpdate(carReturnBill);

        // 发起 BPM 流程
        // 还车申请单使用remark作为cause
        Map<String, Object> additionalVariables = new HashMap<>();
        additionalVariables.put("cause", saveReqVO.getRemark()); // 显式设置cause字段
        
        Map<String, Object> processInstanceVariables = BpmProcessVariableUtils
                .buildBillVariables(saveReqVO, additionalVariables);
        String processInstanceId = processInstanceApi.submitProcessInstance(Long.valueOf(saveReqVO.getCreator()),
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(OaBillTypeEnum.OA_CAR_RETURN_BILL.getProcessDefinitionKey())
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(carReturnBill.getId()))
        ).getCheckedData();

        // 将工作流的编号，更新到单据中
        carReturnBillMapper.updateById(new CarReturnBillDO().setId(carReturnBill.getId()).setProcessInstanceId(processInstanceId));
        
        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_CAR_RETURN_BILL.getTypeCode(), carReturnBill.getId(), saveReqVO.getAttachments());
        }
        
        // 标记对应用车申请单为还车中
        carApplyBillService.markAsReturning(applyBillId);
        
        // 返回
        return carReturnBill.getId();
    }

    @Override
    public Long createCarReturnBill(CarReturnBillSaveReqVO createReqVO) {
        // 插入
        String billCode = BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_CAR_RETURN_BILL);
        createReqVO.setBillCode(billCode);
        // 插入
        CarReturnBillDO carReturnBill = BeanUtils.toBean(createReqVO, CarReturnBillDO.class);
        carReturnBillMapper.insertOrUpdate(carReturnBill);

        // 返回
        return carReturnBill.getId();
    }

    @Override
    public void updateCarReturnBill(CarReturnBillSaveReqVO updateReqVO) {
        // 校验存在
        validateCarReturnBillExists(updateReqVO.getId());
        // 更新
        CarReturnBillDO updateObj = BeanUtils.toBean(updateReqVO, CarReturnBillDO.class);
        carReturnBillMapper.updateById(updateObj);
    }

    @Override
    public void deleteCarReturnBill(Long id) {
        // 校验存在
        validateCarReturnBillExists(id);
        // 删除
        carReturnBillMapper.deleteById(id);
    }

    @Override
    public void deleteCarReturnBillListByIds(List<Long> ids) {
        // 删除
        carReturnBillMapper.deleteByIds(ids);
    }

    private void validateCarReturnBillExists(Long id) {
        if (carReturnBillMapper.selectById(id) == null) {
            throw exception(CAR_RETURN_BILL_NOT_EXISTS);
        }
    }

    @Override
    public CarReturnBillDO getCarReturnBill(Long id) {
        return carReturnBillMapper.selectById(id);
    }

    @Override
    public CarReturnBillRespVO getCarReturnBillInfo(Long id) {
        CarReturnBillDO carReturnBill = carReturnBillMapper.selectById(id);
        if (carReturnBill == null) {
            return null;
        }
        
        CarReturnBillRespVO respVO = BeanUtils.toBean(carReturnBill, CarReturnBillRespVO.class);
        
        // 获取附件信息
        respVO.setAttachments(BeanUtils.toBean(
            attachmentService.getAttachmentListByBusiness(OaBillTypeEnum.OA_CAR_RETURN_BILL.getTypeCode(), id),
            AttachmentRespVO.class
        ));
        
        return respVO;
    }

    @Override
    public PageResult<CarReturnBillDO> getCarReturnBillPage(CarReturnBillPageReqVO pageReqVO) {
        // 自动添加创建人过滤条件（当前登录用户）
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (currentUserId != null) {
            pageReqVO.setCreator(String.valueOf(currentUserId));
        }
        return carReturnBillMapper.selectPage(pageReqVO);
    }



    // ==================== FlowBillService 接口实现 ====================

    @Override
    public OaBillTypeEnum getSupportedBillType() {
        return OaBillTypeEnum.OA_CAR_RETURN_BILL;
    }

    @Override
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        log.info("[updateProcessStatus] 更新还车申请单流程状态，id: {}, status: {}", id, status);

        // 校验还车申请单存在
        validateCarReturnBillExists(id);

        // 更新流程状态
        CarReturnBillDO updateObj = new CarReturnBillDO();
        updateObj.setId(id);
        updateObj.setProcessStatus(status);
        carReturnBillMapper.updateById(updateObj);

        // 根据流程状态处理用车申请单的还车状态
        handleApplyBillReturnStatus(id, status);

        log.info("[updateProcessStatus] 还车申请单流程状态更新成功，id: {}, status: {}", id, status);
    }

    /**
     * 根据还车单流程状态处理用车申请单的还车状态
     *
     * @param returnBillId 还车单ID
     * @param status 流程状态
     */
    private void handleApplyBillReturnStatus(Long returnBillId, Integer status) {
        try {
            CarReturnBillDO returnBill = getCarReturnBill(returnBillId);
            if (returnBill == null || returnBill.getApplyBill() == null) {
                return;
            }

            CarApplyBillDO applyBill = carApplyBillService.getCarApplyBillByCode(returnBill.getApplyBill());
            if (applyBill == null) {
                return;
            }

            // 根据流程状态处理用车申请单的还车状态
            if (BpmTaskStatusEnum.APPROVE.getStatus().equals(status)) {
                // 审批通过：标记为已还车
                carApplyBillService.markAsReturned(applyBill.getId());
                log.info("[handleApplyBillReturnStatus] 还车单审批通过，用车申请单标记为已还车，applyBillId: {}", applyBill.getId());
            } else if (BpmTaskStatusEnum.REJECT.getStatus().equals(status) || 
                       BpmTaskStatusEnum.CANCEL.getStatus().equals(status) ||
                       BpmTaskStatusEnum.RETURN.getStatus().equals(status)) {
                // 审批拒绝、取消或退回：回滚为未还车
                carApplyBillService.markAsNotReturned(applyBill.getId());
                log.info("[handleApplyBillReturnStatus] 还车单流程状态变更为{}，用车申请单回滚为未还车，applyBillId: {}", status, applyBill.getId());
            }
        } catch (Exception e) {
            log.error("[handleApplyBillReturnStatus] 处理用车申请单还车状态失败，returnBillId: {}, status: {}", returnBillId, status, e);
        }
    }

    /**
     * 验证用车申请单是否已还车
     *
     * @param applyBillCode 用车申请单ID
     */
    private Long validateApplyBillNotReturned(String applyBillCode) {
        Long applyBillId = 0L;
        if (applyBillCode == null) {
            throw exception(CAR_APPLY_BILL_NOT_EXISTS);
        }
        
        CarApplyBillDO applyBill = carApplyBillService.getCarApplyBillByCode(applyBillCode);
        if (applyBill == null) {
            throw exception(CAR_APPLY_BILL_NOT_EXISTS);
        }
        
        if (CarReturnStatusEnum.RETURNED.getStatus().equals(applyBill.getReturnStatus())) {
            throw exception(CAR_APPLY_BILL_ALREADY_RETURNED);
        }
        return applyBill.getId();
    }

}