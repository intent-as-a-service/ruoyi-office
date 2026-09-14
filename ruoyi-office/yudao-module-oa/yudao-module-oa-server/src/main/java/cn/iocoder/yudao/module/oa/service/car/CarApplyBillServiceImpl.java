package cn.iocoder.yudao.module.oa.service.car;

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
import cn.iocoder.yudao.module.oa.controller.admin.car.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.car.CarApplyBillDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.oa.dal.mysql.car.CarApplyBillMapper;
import cn.iocoder.yudao.common.server.attachment.service.AttachmentService;
import cn.iocoder.yudao.common.server.attachment.controller.vo.AttachmentRespVO;

import cn.iocoder.yudao.framework.common.service.FlowBillService;

import cn.iocoder.yudao.module.oa.enums.CarReturnStatusEnum;
import cn.iocoder.yudao.module.bpm.util.BpmProcessVariableUtils;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.bpm.enums.ErrorCodeConstants.OA_LEAVE_NOT_EXISTS;
import static cn.iocoder.yudao.module.oa.enums.ErrorCodeConstants.*;
import static cn.iocoder.yudao.module.bpm.enums.task.BpmTaskStatusEnum.RUNNING;
import static cn.iocoder.yudao.module.bpm.enums.task.BpmTaskStatusEnum.APPROVE;

/**
 * 用车申请单 Service 实现类
 *
 * @author 宇擎源码
 */
@Slf4j
@Service
@Validated
public class CarApplyBillServiceImpl implements CarApplyBillService, FlowBillService<OaBillTypeEnum> {

    @Resource
    private CarApplyBillMapper carApplyBillMapper;

    @Resource
    private AttachmentService attachmentService;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;


    @Override
    public Long saveCarApplyBill(CarApplyBillSaveReqVO saveReqVO) {

        // 如果单号为空，需要生成
        if(StringUtils.isBlank(saveReqVO.getBillCode())){
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_CAR_APPLY_BILL));
        }

        // 插入或更新
        CarApplyBillDO carApplyBill = BeanUtils.toBean(saveReqVO, CarApplyBillDO.class);
        carApplyBillMapper.insertOrUpdate(carApplyBill);

        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_CAR_APPLY_BILL.getTypeCode(), carApplyBill.getId(), saveReqVO.getAttachments());
        }

        // 返回
        return carApplyBill.getId();
    }

    @Override
    public Long submitCarApplyBill(CarApplyBillSaveReqVO saveReqVO) {

        // 如果单号为空，需要生成
        if(StringUtils.isBlank(saveReqVO.getBillCode())){
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_CAR_APPLY_BILL));
        }

        // 校验时间冲突
        validateTimeConflict(saveReqVO);

        // 保存或更新
        CarApplyBillDO carApplyBill = BeanUtils.toBean(saveReqVO, CarApplyBillDO.class)
                .setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        carApplyBillMapper.insertOrUpdate(carApplyBill);

        // 智能提交 BPM 流程（如果流程实例不存在则创建，存在则审批发起人任务）
        Map<String, Object> processInstanceVariables = BpmProcessVariableUtils.buildBillVariables(saveReqVO);
        String processInstanceId = processInstanceApi.submitProcessInstance(Long.valueOf(saveReqVO.getCreator()),
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(OaBillTypeEnum.OA_CAR_APPLY_BILL.getProcessDefinitionKey())
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(carApplyBill.getId()))
        ).getCheckedData();

        // 将工作流的编号，更新到单据中
        carApplyBillMapper.updateById(new CarApplyBillDO().setId(carApplyBill.getId()).setProcessInstanceId(processInstanceId));
        
        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_CAR_APPLY_BILL.getTypeCode(), carApplyBill.getId(), saveReqVO.getAttachments());
        }
        
        // 返回
        return carApplyBill.getId();
    }

    @Override
    public Long createCarApplyBill(CarApplyBillSaveReqVO createReqVO) {
        // 插入
        String billCode = BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_CAR_APPLY_BILL);
        createReqVO.setBillCode(billCode);
        // 插入
        CarApplyBillDO carApplyBill = BeanUtils.toBean(createReqVO, CarApplyBillDO.class);
        carApplyBillMapper.insertOrUpdate(carApplyBill);

        // 返回
        return carApplyBill.getId();
    }



    @Override
    public void updateCarApplyBill(CarApplyBillSaveReqVO updateReqVO) {
        // 校验存在
        validateCarApplyBillExists(updateReqVO.getId());
        // 更新
        CarApplyBillDO updateObj = BeanUtils.toBean(updateReqVO, CarApplyBillDO.class);
        carApplyBillMapper.updateById(updateObj);
    }

    @Override
    public void deleteCarApplyBill(Long id) {
        // 校验存在
        validateCarApplyBillExists(id);
        // 删除
        carApplyBillMapper.deleteById(id);
    }

    @Override
    public void deleteCarApplyBillListByIds(List<Long> ids) {
        // 删除
        carApplyBillMapper.deleteByIds(ids);
    }


    private void validateCarApplyBillExists(Long id) {
        if (carApplyBillMapper.selectById(id) == null) {
            throw exception(CAR_APPLY_BILL_NOT_EXISTS);
        }
    }

    @Override
    public CarApplyBillDO getCarApplyBill(Long id) {
        return carApplyBillMapper.selectById(id);
    }

    @Override
    public CarApplyBillRespVO getCarApplyBillInfo(Long id) {
        CarApplyBillDO carApplyBill = carApplyBillMapper.selectById(id);
        if (carApplyBill == null) {
            return null;
        }
        
        CarApplyBillRespVO respVO = BeanUtils.toBean(carApplyBill, CarApplyBillRespVO.class);
        
        // 获取附件信息
        respVO.setAttachments(BeanUtils.toBean(
            attachmentService.getAttachmentListByBusiness(OaBillTypeEnum.OA_CAR_APPLY_BILL.getTypeCode(), id),
            AttachmentRespVO.class
        ));
        
        return respVO;
    }
    
    @Override
    public CarApplyBillDO getCarApplyBillByCode(String code) {
        return carApplyBillMapper.selectOne(new LambdaQueryWrapperX<CarApplyBillDO>().eq(CarApplyBillDO::getBillCode, code));
    }

    @Override
    public PageResult<CarApplyBillDO> getCarApplyBillPage(CarApplyBillPageReqVO pageReqVO) {
        // 自动添加创建人过滤条件（当前登录用户）
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (currentUserId != null) {
            pageReqVO.setCreator(String.valueOf(currentUserId));
        }
        return carApplyBillMapper.selectPage(pageReqVO);
    }


    // ==================== FlowBillService 接口实现 ====================

    @Override
    public OaBillTypeEnum getSupportedBillType() {
        return OaBillTypeEnum.OA_CAR_APPLY_BILL;
    }

    @Override
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        log.info("[updateProcessStatus] 更新用车申请单流程状态，id: {}, status: {}", id, status);

        // 校验用车申请单存在
        validateCarApplyBillExists(id);

        // 更新流程状态
        CarApplyBillDO updateObj = new CarApplyBillDO();
        updateObj.setId(id);
        updateObj.setProcessStatus(status);
        
        // 如果审批通过，设置还车状态为待还车
        if (APPROVE.getStatus().equals(status)) {
            updateObj.setReturnStatus(CarReturnStatusEnum.PENDING_RETURN.getStatus());
        }
        
        carApplyBillMapper.updateById(updateObj);

        log.info("[updateProcessStatus] 用车申请单流程状态更新成功，id: {}, status: {}", id, status);
    }



    @Override
    public void updateReturnStatus(Long id, Integer returnStatus) {
        log.info("[updateReturnStatus] 更新用车申请单还车状态，id: {}, returnStatus: {}", id, returnStatus);
        
        // 校验用车申请单存在
        validateCarApplyBillExists(id);
        
        // 更新还车状态
        CarApplyBillDO updateObj = new CarApplyBillDO();
        updateObj.setId(id);
        updateObj.setReturnStatus(returnStatus);
        carApplyBillMapper.updateById(updateObj);
        
        log.info("[updateReturnStatus] 用车申请单还车状态更新成功，id: {}, returnStatus: {}", id, returnStatus);
    }

    @Override
    public void markAsReturned(Long id) {
        updateReturnStatus(id, CarReturnStatusEnum.RETURNED.getStatus());
    }
    
    @Override
    public void markAsReturning(Long id) {
        updateReturnStatus(id, CarReturnStatusEnum.RETURNING.getStatus());
    }
    
    @Override
    public void markAsNotReturned(Long id) {
        updateReturnStatus(id, CarReturnStatusEnum.NOT_EFFECTIVE.getStatus());
    }

    /**
     * 校验车辆使用时间冲突
     *
     * @param saveReqVO 保存请求VO
     */
    private void validateTimeConflict(CarApplyBillSaveReqVO saveReqVO) {
        if (saveReqVO.getCarId() == null || saveReqVO.getGoTime() == null || saveReqVO.getReturnTime() == null) {
            return; // 如果必要字段为空，跳过校验
        }

        // 校验出车时间不能晚于回车时间
        if (saveReqVO.getGoTime().isAfter(saveReqVO.getReturnTime())) {
            throw exception(CAR_TIME_CONFLICT);
        }

        // 查询同一车辆在相同时间段内的申请单
        List<CarApplyBillDO> conflictBills = carApplyBillMapper.selectList(
                new LambdaQueryWrapperX<CarApplyBillDO>()
                        .eq(CarApplyBillDO::getCarId, saveReqVO.getCarId())
                        .ne(saveReqVO.getId() != null, CarApplyBillDO::getId, saveReqVO.getId()) // 排除当前编辑的记录
                        .and(wrapper -> wrapper
                                // 场景1：存在审批中的申请单且时间重合
                                .and(subWrapper -> subWrapper
                                        .eq(CarApplyBillDO::getProcessStatus, RUNNING.getStatus())
                                        .and(timeWrapper -> timeWrapper
                                                .and(timeWrapper2 -> timeWrapper2
                                                        .le(CarApplyBillDO::getGoTime, saveReqVO.getGoTime())
                                                        .ge(CarApplyBillDO::getReturnTime, saveReqVO.getGoTime())
                                                )
                                                .or(timeWrapper3 -> timeWrapper3
                                                        .le(CarApplyBillDO::getGoTime, saveReqVO.getReturnTime())
                                                        .ge(CarApplyBillDO::getReturnTime, saveReqVO.getReturnTime())
                                                )
                                                .or(timeWrapper4 -> timeWrapper4
                                                        .ge(CarApplyBillDO::getGoTime, saveReqVO.getGoTime())
                                                        .le(CarApplyBillDO::getReturnTime, saveReqVO.getReturnTime())
                                                )
                                        )
                                )
                                // 场景2：存在审批通过且还车状态为待还车或还车中的申请单且时间重合
                                .or(subWrapper -> subWrapper
                                        .eq(CarApplyBillDO::getProcessStatus, APPROVE.getStatus())
                                        .in(CarApplyBillDO::getReturnStatus, 
                                                CarReturnStatusEnum.PENDING_RETURN.getStatus(), 
                                                CarReturnStatusEnum.RETURNING.getStatus())
                                        .and(timeWrapper -> timeWrapper
                                                .and(timeWrapper2 -> timeWrapper2
                                                        .le(CarApplyBillDO::getGoTime, saveReqVO.getGoTime())
                                                        .ge(CarApplyBillDO::getReturnTime, saveReqVO.getGoTime())
                                                )
                                                .or(timeWrapper3 -> timeWrapper3
                                                        .le(CarApplyBillDO::getGoTime, saveReqVO.getReturnTime())
                                                        .ge(CarApplyBillDO::getReturnTime, saveReqVO.getReturnTime())
                                                )
                                                .or(timeWrapper4 -> timeWrapper4
                                                        .ge(CarApplyBillDO::getGoTime, saveReqVO.getGoTime())
                                                        .le(CarApplyBillDO::getReturnTime, saveReqVO.getReturnTime())
                                                )
                                        )
                                )
                        )
        );

        if (!conflictBills.isEmpty()) {
            throw exception(CAR_TIME_CONFLICT);
        }
    }

}