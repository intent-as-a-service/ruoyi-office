package cn.iocoder.yudao.module.oa.controller.admin.meetingroom;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.oa.controller.admin.meetingroom.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.meetingroom.MeetingRoomBookingDO;
import cn.iocoder.yudao.module.oa.service.meetingroom.MeetingRoomBookingService;

@Tag(name = "管理后台 - 会议室预定申请单")
@RestController
@RequestMapping("/oa/meeting-room-booking")
@Validated
public class MeetingRoomBookingController {

    @Resource
    private MeetingRoomBookingService meetingRoomBookingService;

    @PostMapping("/save")
    @Operation(summary = "保存会议室预定申请单（草稿）")
    @PreAuthorize("@ss.hasPermission('oa:meeting-room-booking:create')")
    public CommonResult<Long> saveMeetingRoomBooking(@Valid @RequestBody MeetingRoomBookingSaveReqVO saveReqVO) {
        return success(meetingRoomBookingService.saveMeetingRoomBooking(saveReqVO));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交会议室预定申请单（发起审批）")
    @PreAuthorize("@ss.hasPermission('oa:meeting-room-booking:submit')")
    public CommonResult<Long> submitMeetingRoomBooking(@Valid @RequestBody MeetingRoomBookingSaveReqVO submitReqVO) {
        return success(meetingRoomBookingService.submitMeetingRoomBooking(submitReqVO));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除会议室预定申请单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:meeting-room-booking:delete')")
    public CommonResult<Boolean> deleteMeetingRoomBooking(@RequestParam("id") Long id) {
        meetingRoomBookingService.deleteMeetingRoomBooking(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得会议室预定申请单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:meeting-room-booking:query')")
    public CommonResult<MeetingRoomBookingRespVO> getMeetingRoomBooking(@RequestParam("id") Long id) {
        MeetingRoomBookingRespVO meetingRoomBooking = meetingRoomBookingService.getMeetingRoomBooking(id);
        return success(meetingRoomBooking);
    }

    @GetMapping("/page")
    @Operation(summary = "获得会议室预定申请单分页")
    @PreAuthorize("@ss.hasPermission('oa:meeting-room-booking:query')")
    public CommonResult<PageResult<MeetingRoomBookingRespVO>> getMeetingRoomBookingPage(@Valid MeetingRoomBookingPageReqVO pageReqVO) {
        PageResult<MeetingRoomBookingDO> pageResult = meetingRoomBookingService.getMeetingRoomBookingPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MeetingRoomBookingRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出会议室预定申请单 Excel")
    @PreAuthorize("@ss.hasPermission('oa:meeting-room-booking:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportMeetingRoomBookingExcel(@Valid MeetingRoomBookingPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MeetingRoomBookingDO> list = meetingRoomBookingService.getMeetingRoomBookingPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "会议室预定申请单.xls", "数据", MeetingRoomBookingRespVO.class,
                        BeanUtils.toBean(list, MeetingRoomBookingRespVO.class));
    }

    @PutMapping("/cancel")
    @Operation(summary = "取消会议室预定申请单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:meeting-room-booking:cancel')")
    public CommonResult<Boolean> cancelMeetingRoomBooking(@RequestParam("id") Long id) {
        meetingRoomBookingService.cancelMeetingRoomBooking(id);
        return success(true);
    }

    @PutMapping("/approve")
    @Operation(summary = "通过会议室预定申请单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:meeting-room-booking:approve')")
    public CommonResult<Boolean> approveMeetingRoomBooking(@RequestParam("id") Long id) {
        meetingRoomBookingService.approveMeetingRoomBooking(id);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "拒绝会议室预定申请单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:meeting-room-booking:reject')")
    public CommonResult<Boolean> rejectMeetingRoomBooking(@RequestParam("id") Long id) {
        meetingRoomBookingService.rejectMeetingRoomBooking(id);
        return success(true);
    }

    @PutMapping("/update-use-status")
    @Operation(summary = "更新会议室预定申请单的使用状态")
    @PreAuthorize("@ss.hasPermission('oa:meeting-room-booking:update')")
    public CommonResult<Boolean> updateUseStatus(
            @RequestParam("id") Long id,
            @RequestParam("useStatus") Integer useStatus) {
        meetingRoomBookingService.updateUseStatus(id, useStatus);
        return success(true);
    }

    @GetMapping("/schedule")
    @Operation(summary = "查询会议室预约信息（用于展示预约时间网格）")
    @PreAuthorize("@ss.hasPermission('oa:meeting-room-booking:query')")
    public CommonResult<MeetingRoomBookingScheduleRespVO> getMeetingRoomBookingSchedule(
            @Valid MeetingRoomBookingScheduleReqVO reqVO) {
        MeetingRoomBookingScheduleRespVO schedule = meetingRoomBookingService.getMeetingRoomBookingSchedule(reqVO);
        return success(schedule);
    }

}

