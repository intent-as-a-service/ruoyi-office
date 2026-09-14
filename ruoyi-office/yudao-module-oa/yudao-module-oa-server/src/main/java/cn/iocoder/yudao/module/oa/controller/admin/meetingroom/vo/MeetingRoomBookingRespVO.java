package cn.iocoder.yudao.module.oa.controller.admin.meetingroom.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import cn.iocoder.yudao.common.server.attachment.controller.vo.AttachmentRespVO;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 会议室预定申请单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MeetingRoomBookingRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "单据编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "MRB202511130001")
    @ExcelProperty("单据编号")
    private String billCode;

    @Schema(description = "流程实例编号", example = "1234567890")
    private String processInstanceId;

    @Schema(description = "单据状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "单据状态", converter = DictConvert.class)
    @DictFormat("oa_process_status")
    private Integer processStatus;

    @Schema(description = "会议室ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("会议室ID")
    private Long roomId;

    @Schema(description = "会议室名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "第一会议室")
    @ExcelProperty("会议室名称")
    private String roomName;

    @Schema(description = "会议室位置", example = "公司本部二楼")
    @ExcelProperty("会议室位置")
    private String roomLocation;

    @Schema(description = "会议室类型", example = "1")
    @ExcelProperty(value = "会议室类型", converter = DictConvert.class)
    @DictFormat("oa_meeting_room_type")
    private Integer roomType;

    @Schema(description = "会议名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "季度总结会议")
    @ExcelProperty("会议名称")
    private String meetingTitle;

    @Schema(description = "会议开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("会议开始时间")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime meetingStartTime;

    @Schema(description = "会议结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("会议结束时间")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime meetingEndTime;

    @Schema(description = "主持人ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("主持人ID")
    private Long moderatorId;

    @Schema(description = "主持人姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("主持人姓名")
    private String moderatorName;

    @Schema(description = "会议备注", example = "请各部门负责人准时参加")
    @ExcelProperty("会议备注")
    private String meetingRemark;

    @Schema(description = "会议提醒", example = "3")
    @ExcelProperty(value = "会议提醒", converter = DictConvert.class)
    @DictFormat("oa_meeting_reminder_type")
    private Integer reminderType;

    @Schema(description = "与会人ID列表", example = "[1,2,3]")
    private List<Long> attendees;

    @Schema(description = "与会人姓名列表", example = "[\"张三\",\"李四\",\"王五\"]")
    private List<String> attendeeNames;

    @Schema(description = "使用状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty(value = "使用状态", converter = DictConvert.class)
    @DictFormat("oa_meeting_booking_use_status")
    private Integer useStatus;

    @Schema(description = "申请人姓名", example = "张三")
    @ExcelProperty("申请人")
    private String creatorName;

    @Schema(description = "公司名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "示例公司")
    @ExcelProperty("公司名称")
    private String companyName;

    @Schema(description = "部门名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "研发部")
    @ExcelProperty("部门名称")
    private String deptName;

    @Schema(description = "创建人")
    @ExcelProperty("创建人")
    private String creator;

    @Schema(description = "备注", example = "备注信息")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime createTime;

    @Schema(description = "附件列表")
    private List<AttachmentRespVO> attachments;
}

