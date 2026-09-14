package cn.iocoder.yudao.module.oa.controller.admin.meetingroom.vo;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 会议室预定申请单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MeetingRoomBookingPageReqVO extends PageParam {

    @Schema(description = "单据编号", example = "MRB202511130001")
    private String billCode;

    @Schema(description = "会议室名称", example = "第一会议室")
    private String roomName;

    @Schema(description = "会议名称", example = "季度总结会议")
    private String meetingTitle;

    @Schema(description = "主持人姓名", example = "张三")
    private String moderatorName;

    @Schema(description = "单据状态", example = "1")
    private Integer processStatus;

    @Schema(description = "使用状态", example = "0")
    private Integer useStatus;

    @Schema(description = "会议开始时间范围")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] meetingStartTime;

    @Schema(description = "会议结束时间范围")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] meetingEndTime;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "创建人（用户ID）", example = "1")
    private String creator;

}

