package cn.iocoder.yudao.module.oa.controller.admin.meetingroom.vo;

import cn.iocoder.yudao.common.server.attachment.controller.vo.AttachmentSaveReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 会议室预定申请单新增/修改 Request VO")
@Data
public class MeetingRoomBookingSaveReqVO {

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "会议室ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long roomId;

    @Schema(description = "会议室名称", example = "第一会议室")
    private String roomName;

    @Schema(description = "会议室位置", example = "公司本部二楼")
    private String roomLocation;

    @Schema(description = "会议室类型", example = "1")
    private Integer roomType;

    @Schema(description = "会议名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "季度总结会议")
    @Size(max = 200, message = "会议名称长度不能超过200字")
    private String meetingTitle;

    @Schema(description = "会议开始时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2025-11-13 15:00:00")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime meetingStartTime;

    @Schema(description = "会议结束时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2025-11-13 17:00:00")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime meetingEndTime;

    @Schema(description = "主持人ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long moderatorId;

    @Schema(description = "主持人姓名", example = "张三")
    private String moderatorName;

    @Schema(description = "会议备注", example = "请各部门负责人准时参加")
    @Size(max = 1000, message = "会议备注长度不能超过1000字")
    private String meetingRemark;

    @Schema(description = "会议提醒", example = "3")
    private Integer reminderType;

    @Schema(description = "与会人ID列表", example = "[1,2,3]")
    private List<Long> attendees;

    @Schema(description = "与会人姓名列表", example = "[\"张三\",\"李四\",\"王五\"]")
    private List<String> attendeeNames;

    @Schema(description = "使用状态（0待使用 1使用中 2已完成 3已取消）", example = "0")
    private Integer useStatus;

    @Schema(description = "备注", example = "备注信息")
    @Size(max = 500, message = "备注长度不能超过500字")
    private String remark;

    // 审批流相关字段
    @Schema(description = "单据编号", example = "MRB202511130001")
    private String billCode;

    @Schema(description = "流程实例编号", example = "1234567890")
    private String processInstanceId;

    @Schema(description = "单据状态（0草稿 1审批中 2审批通过 3审批拒绝 4已取消）", example = "0")
    private Integer processStatus;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "创建者姓名", example = "芋艿")
    private String creatorName;

    @Schema(description = "部门ID", example = "12287")
    private Long deptId;

    @Schema(description = "部门名称", example = "张三")
    private String deptName;

    @Schema(description = "公司ID", example = "1109")
    private Long companyId;

    @Schema(description = "公司名称", example = "张三")
    private String companyName;

    @Schema(description = "附件列表")
    private List<AttachmentSaveReqVO> attachments;


}

