package cn.iocoder.yudao.module.oa.service.meetingroom;

import cn.iocoder.yudao.common.server.attachment.controller.vo.AttachmentRespVO;
import cn.iocoder.yudao.common.server.attachment.service.AttachmentService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import cn.iocoder.yudao.module.oa.controller.admin.meetingroom.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.meetingroom.MeetingRoomBookingDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.oa.dal.mysql.meetingroom.MeetingRoomBookingMapper;
import cn.iocoder.yudao.framework.common.service.FlowBillService;
import cn.iocoder.yudao.module.bpm.util.BpmProcessVariableUtils;
import cn.iocoder.yudao.module.oa.service.meetingroom.MeetingRoomService;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.oa.enums.ErrorCodeConstants.*;
import static cn.iocoder.yudao.module.oa.enums.OaProcessVariableConstants.*;

/**
 * 会议室预定申请单 Service 实现类
 *
 * @author 宇擎源码
 */
@Slf4j
@Service
@Validated
public class MeetingRoomBookingServiceImpl implements MeetingRoomBookingService, FlowBillService<OaBillTypeEnum> {

    @Resource
    private MeetingRoomBookingMapper meetingRoomBookingMapper;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Resource
    private AttachmentService attachmentService;

    @Resource
    private MeetingRoomService meetingRoomService;


    @Override
    public Long saveMeetingRoomBooking(MeetingRoomBookingSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_MEETING_ROOM_BOOKING));
        }

        // 插入或更新
        MeetingRoomBookingDO meetingRoomBooking = BeanUtils.toBean(saveReqVO, MeetingRoomBookingDO.class);
        // 转换参会人员列表为逗号分隔字符串
        if (saveReqVO.getAttendees() != null && !saveReqVO.getAttendees().isEmpty()) {
            meetingRoomBooking.setAttendees(String.join(",", saveReqVO.getAttendees().stream()
                    .map(String::valueOf)
                    .toArray(String[]::new)));
        }
        meetingRoomBookingMapper.insertOrUpdate(meetingRoomBooking);

        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_MEETING_ROOM_BOOKING.getTypeCode(), meetingRoomBooking.getId(), saveReqVO.getAttachments());
        }

        // 返回
        return meetingRoomBooking.getId();
    }

    @Override
    public Long submitMeetingRoomBooking(MeetingRoomBookingSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_MEETING_ROOM_BOOKING));
        }

        // 校验会议时间
        validateMeetingTime(saveReqVO);

        // 校验时间冲突
        validateTimeConflict(saveReqVO);

        // 保存或更新
        MeetingRoomBookingDO meetingRoomBooking = BeanUtils.toBean(saveReqVO, MeetingRoomBookingDO.class)
                .setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus())
                .setUseStatus(0); // 待使用

        // 转换参会人员列表为逗号分隔字符串
        if (saveReqVO.getAttendees() != null && !saveReqVO.getAttendees().isEmpty()) {
            meetingRoomBooking.setAttendees(String.join(",", saveReqVO.getAttendees().stream()
                    .map(String::valueOf)
                    .toArray(String[]::new)));
        }

        meetingRoomBookingMapper.insertOrUpdate(meetingRoomBooking);

        // 查询会议室的"预定需审批"属性
        Boolean needApproval = false;
        if (saveReqVO.getRoomId() != null) {
            var meetingRoom = meetingRoomService.getMeetingRoom(saveReqVO.getRoomId());
            if (meetingRoom != null) {
                needApproval = meetingRoom.getNeedApproval() != null ? meetingRoom.getNeedApproval() : false;
            }
        }

        // 智能提交 BPM 流程（如果流程实例不存在则创建，存在则审批发起人任务）
        Map<String, Object> processInstanceVariables = BpmProcessVariableUtils.buildBillVariables(saveReqVO);
        // 添加会议室预定需审批流程变量
        processInstanceVariables.put(PV_MEETING_ROOM_NEED_APPROVAL, needApproval);
        String processInstanceId = processInstanceApi.submitProcessInstance(Long.valueOf(saveReqVO.getCreator()),
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(OaBillTypeEnum.OA_MEETING_ROOM_BOOKING.getProcessDefinitionKey())
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(meetingRoomBooking.getId()))
        ).getCheckedData();

        // 将工作流的编号，更新到单据中
        meetingRoomBookingMapper.updateById(new MeetingRoomBookingDO().setId(meetingRoomBooking.getId()).setProcessInstanceId(processInstanceId));

        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_MEETING_ROOM_BOOKING.getTypeCode(), meetingRoomBooking.getId(), saveReqVO.getAttachments());
        }

        // 返回
        return meetingRoomBooking.getId();
    }

    /**
     * 校验会议时间
     */
    private void validateMeetingTime(MeetingRoomBookingSaveReqVO saveReqVO) {
        if (saveReqVO.getMeetingStartTime() == null || saveReqVO.getMeetingEndTime() == null) {
            throw exception(MEETING_ROOM_BOOKING_TIME_INVALID);
        }

        // 校验开始时间必须早于结束时间
        if (!saveReqVO.getMeetingStartTime().isBefore(saveReqVO.getMeetingEndTime())) {
            throw exception(MEETING_ROOM_BOOKING_TIME_INVALID);
        }

        // 校验开始时间不能是过去时间
        if (saveReqVO.getMeetingStartTime().isBefore(LocalDateTime.now())) {
            throw exception(MEETING_ROOM_BOOKING_TIME_PAST);
        }
    }

    /**
     * 校验时间冲突
     */
    private void validateTimeConflict(MeetingRoomBookingSaveReqVO saveReqVO) {
        // 查询该会议室在指定时间段内是否有其他已批准的预定
        List<MeetingRoomBookingDO> existingBookings = meetingRoomBookingMapper.selectList(
                new LambdaQueryWrapperX<MeetingRoomBookingDO>()
                        .eq(MeetingRoomBookingDO::getRoomId, saveReqVO.getRoomId())
                        .in(MeetingRoomBookingDO::getUseStatus, Arrays.asList(0, 1)) // 待使用或使用中
                        .and(wrapper -> wrapper
                                // 情况1: 新预定的开始时间在已有预定时间段内
                                .or(w -> w.le(MeetingRoomBookingDO::getMeetingStartTime, saveReqVO.getMeetingStartTime())
                                        .gt(MeetingRoomBookingDO::getMeetingEndTime, saveReqVO.getMeetingStartTime()))
                                // 情况2: 新预定的结束时间在已有预定时间段内
                                .or(w -> w.lt(MeetingRoomBookingDO::getMeetingStartTime, saveReqVO.getMeetingEndTime())
                                        .ge(MeetingRoomBookingDO::getMeetingEndTime, saveReqVO.getMeetingEndTime()))
                                // 情况3: 新预定时间段完全包含已有预定
                                .or(w -> w.ge(MeetingRoomBookingDO::getMeetingStartTime, saveReqVO.getMeetingStartTime())
                                        .le(MeetingRoomBookingDO::getMeetingEndTime, saveReqVO.getMeetingEndTime()))
                        )
        );

        // 如果是更新操作，需要排除当前记录
        if (saveReqVO.getId() != null) {
            existingBookings = existingBookings.stream()
                    .filter(booking -> !booking.getId().equals(saveReqVO.getId()))
                    .toList();
        }

        if (!existingBookings.isEmpty()) {
            throw exception(MEETING_ROOM_BOOKING_TIME_CONFLICT);
        }
    }


    @Override
    public MeetingRoomBookingRespVO getMeetingRoomBooking(Long id) {
        MeetingRoomBookingDO meetingRoomBooking = meetingRoomBookingMapper.selectById(id);
        if (meetingRoomBooking == null) {
            return null;
        }

        MeetingRoomBookingRespVO respVO = BeanUtils.toBean(meetingRoomBooking, MeetingRoomBookingRespVO.class);

        // 获取附件信息
        respVO.setAttachments(BeanUtils.toBean(
                attachmentService.getAttachmentListByBusiness(OaBillTypeEnum.OA_MEETING_ROOM_BOOKING.getTypeCode(), id),
                AttachmentRespVO.class
        ));

        return respVO;
    }


    @Override
    public void deleteMeetingRoomBooking(Long id) {
        // 校验存在
        validateMeetingRoomBookingExists(id);

        // 校验状态：只能删除草稿状态的单据
        MeetingRoomBookingDO booking = meetingRoomBookingMapper.selectById(id);
        if (booking.getProcessStatus() != null && booking.getProcessStatus() != 0) {
            throw exception(MEETING_ROOM_BOOKING_CANNOT_DELETE);
        }

        // 删除
        meetingRoomBookingMapper.deleteById(id);
    }

    private void validateMeetingRoomBookingExists(Long id) {
        if (meetingRoomBookingMapper.selectById(id) == null) {
            throw exception(MEETING_ROOM_BOOKING_NOT_EXISTS);
        }
    }


    @Override
    public PageResult<MeetingRoomBookingDO> getMeetingRoomBookingPage(MeetingRoomBookingPageReqVO pageReqVO) {
        // 自动添加创建人过滤条件（当前登录用户）
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (currentUserId != null) {
            pageReqVO.setCreator(String.valueOf(currentUserId));
        }
        return meetingRoomBookingMapper.selectPage(pageReqVO);
    }

    @Override
    public void cancelMeetingRoomBooking(Long id) {
        // 校验存在
        validateMeetingRoomBookingExists(id);

        // 更新状态为已取消
        meetingRoomBookingMapper.updateById(new MeetingRoomBookingDO()
                .setId(id)
                .setUseStatus(3) // 已取消
                .setProcessStatus(4)); // 已取消
    }

    @Override
    public void approveMeetingRoomBooking(Long id) {
        // 校验存在
        validateMeetingRoomBookingExists(id);

        // 更新状态为审批通过
        meetingRoomBookingMapper.updateById(new MeetingRoomBookingDO()
                .setId(id)
                .setProcessStatus(2)); // 审批通过
    }

    @Override
    public void rejectMeetingRoomBooking(Long id) {
        // 校验存在
        validateMeetingRoomBookingExists(id);

        // 更新状态为审批拒绝
        meetingRoomBookingMapper.updateById(new MeetingRoomBookingDO()
                .setId(id)
                .setProcessStatus(3)); // 审批拒绝
    }

    @Override
    public void updateUseStatus(Long id, Integer useStatus) {
        // 校验存在
        validateMeetingRoomBookingExists(id);

        // 更新使用状态
        meetingRoomBookingMapper.updateById(new MeetingRoomBookingDO()
                .setId(id)
                .setUseStatus(useStatus));
    }

    // ==================== FlowBillService 接口实现 ====================

    @Override
    public OaBillTypeEnum getSupportedBillType() {
        return OaBillTypeEnum.OA_MEETING_ROOM_BOOKING;
    }

    @Override
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        log.info("[updateProcessStatus] 更新会议室预定申请单流程状态，id: {}, status: {}", id, status);

        // 更新流程状态
        MeetingRoomBookingDO updateObj = new MeetingRoomBookingDO().setId(id).setProcessStatus(status);

        // 根据流程状态更新使用状态
        if (status == BpmTaskStatusEnum.CANCEL.getStatus()) {
            updateObj.setUseStatus(3); // 已取消
        }

        meetingRoomBookingMapper.updateById(updateObj);
    }

    @Override
    public MeetingRoomBookingScheduleRespVO getMeetingRoomBookingSchedule(MeetingRoomBookingScheduleReqVO reqVO) {
        // 查询指定会议室在指定日期范围内的所有预约记录
        LocalDateTime startDateTime = reqVO.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = reqVO.getEndDate().atTime(23, 59, 59);

        List<MeetingRoomBookingDO> bookings = meetingRoomBookingMapper.selectList(
                new LambdaQueryWrapperX<MeetingRoomBookingDO>()
                        .eq(MeetingRoomBookingDO::getRoomId, reqVO.getRoomId())
                        .and(wrapper -> wrapper
                                // 预约开始时间在查询范围内
                                .or(w -> w.ge(MeetingRoomBookingDO::getMeetingStartTime, startDateTime)
                                        .le(MeetingRoomBookingDO::getMeetingStartTime, endDateTime))
                                // 预约结束时间在查询范围内
                                .or(w -> w.ge(MeetingRoomBookingDO::getMeetingEndTime, startDateTime)
                                        .le(MeetingRoomBookingDO::getMeetingEndTime, endDateTime))
                                // 预约时间段完全包含查询范围
                                .or(w -> w.le(MeetingRoomBookingDO::getMeetingStartTime, startDateTime)
                                        .ge(MeetingRoomBookingDO::getMeetingEndTime, endDateTime))
                        )
                        .orderByAsc(MeetingRoomBookingDO::getMeetingStartTime)
        );

        // 转换为响应VO
        List<MeetingRoomBookingScheduleRespVO.BookingItem> bookingItems = bookings.stream()
                .map(booking -> {
                    MeetingRoomBookingScheduleRespVO.BookingItem item = new MeetingRoomBookingScheduleRespVO.BookingItem();
                    item.setId(booking.getId());
                    item.setBillCode(booking.getBillCode());
                    item.setMeetingTitle(booking.getMeetingTitle());
                    item.setMeetingStartTime(booking.getMeetingStartTime());
                    item.setMeetingEndTime(booking.getMeetingEndTime());
                    item.setModeratorName(booking.getModeratorName());
                    item.setCreatorName(booking.getCreatorName());
                    item.setProcessStatus(booking.getProcessStatus());
                    item.setUseStatus(booking.getUseStatus());
                    return item;
                })
                .toList();

        // 查询当天审批通过的预约记录（用于会议室信息列表展示）
        LocalDate today = LocalDate.now();
        List<MeetingRoomBookingDO> todayApprovedBookings = meetingRoomBookingMapper.selectList(
                new LambdaQueryWrapperX<MeetingRoomBookingDO>()
                        .eq(MeetingRoomBookingDO::getRoomId, reqVO.getRoomId())
                        .eq(MeetingRoomBookingDO::getProcessStatus, 2) // 审批通过
                        .ge(MeetingRoomBookingDO::getMeetingStartTime, today.atStartOfDay())
                        .lt(MeetingRoomBookingDO::getMeetingStartTime, today.plusDays(1).atStartOfDay())
                        .orderByDesc(MeetingRoomBookingDO::getMeetingStartTime)
        );

        List<MeetingRoomBookingScheduleRespVO.BookingItem> todayApprovedItems = todayApprovedBookings.stream()
                .map(booking -> {
                    MeetingRoomBookingScheduleRespVO.BookingItem item = new MeetingRoomBookingScheduleRespVO.BookingItem();
                    item.setId(booking.getId());
                    item.setBillCode(booking.getBillCode());
                    item.setMeetingTitle(booking.getMeetingTitle());
                    item.setMeetingStartTime(booking.getMeetingStartTime());
                    item.setMeetingEndTime(booking.getMeetingEndTime());
                    item.setModeratorName(booking.getModeratorName());
                    item.setCreatorName(booking.getCreatorName());
                    item.setProcessStatus(booking.getProcessStatus());
                    item.setUseStatus(booking.getUseStatus());
                    return item;
                })
                .toList();

        MeetingRoomBookingScheduleRespVO respVO = new MeetingRoomBookingScheduleRespVO();
        respVO.setBookings(bookingItems);
        respVO.setTodayApprovedBookings(todayApprovedItems);
        return respVO;
    }

}

