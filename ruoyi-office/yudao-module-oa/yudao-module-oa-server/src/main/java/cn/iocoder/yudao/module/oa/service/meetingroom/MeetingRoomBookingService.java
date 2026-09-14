package cn.iocoder.yudao.module.oa.service.meetingroom;

import jakarta.validation.*;
import cn.iocoder.yudao.module.oa.controller.admin.meetingroom.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.meetingroom.MeetingRoomBookingDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 会议室预定申请单 Service 接口
 *
 * @author 宇擎源码
 */
public interface MeetingRoomBookingService {

    /**
     * 创建或更新会议室预定申请单（草稿）
     *
     * @param saveReqVO 创建或更新信息
     * @return 编号
     */
    Long saveMeetingRoomBooking(@Valid MeetingRoomBookingSaveReqVO saveReqVO);

    /**
     * 提交会议室预定申请单（发起审批）
     *
     * @param saveReqVO 提交信息
     * @return 编号
     */
    Long submitMeetingRoomBooking(@Valid MeetingRoomBookingSaveReqVO saveReqVO);

    /**
     * 删除会议室预定申请单
     *
     * @param id 编号
     */
    void deleteMeetingRoomBooking(Long id);

    /**
     * 获得会议室预定申请单
     *
     * @param id 编号
     * @return 会议室预定申请单
     */
    MeetingRoomBookingRespVO getMeetingRoomBooking(Long id);

    /**
     * 获得会议室预定申请单分页
     *
     * @param pageReqVO 分页查询
     * @return 会议室预定申请单分页
     */
    PageResult<MeetingRoomBookingDO> getMeetingRoomBookingPage(MeetingRoomBookingPageReqVO pageReqVO);

    /**
     * 取消会议室预定申请单
     *
     * @param id 编号
     */
    void cancelMeetingRoomBooking(Long id);

    /**
     * 通过审批
     *
     * @param id 编号
     */
    void approveMeetingRoomBooking(Long id);

    /**
     * 拒绝审批
     *
     * @param id 编号
     */
    void rejectMeetingRoomBooking(Long id);

    /**
     * 更新会议室预定申请单的使用状态
     *
     * @param id 编号
     * @param useStatus 使用状态
     */
    void updateUseStatus(Long id, Integer useStatus);

    /**
     * 查询会议室预约信息（用于展示预约时间网格）
     *
     * @param reqVO 查询条件
     * @return 预约信息
     */
    MeetingRoomBookingScheduleRespVO getMeetingRoomBookingSchedule(MeetingRoomBookingScheduleReqVO reqVO);

}

