package cn.iocoder.yudao.module.system.controller.admin.schedule.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;
import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 日程信息 Response VO")
@Data
public class ScheduleRespVO {

    @Schema(description = "日程ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "销售部例会")
    private String title;

    @Schema(description = "内容", example = "讨论本月销售情况")
    private String content;

    @Schema(description = "日程日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-01-15")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate scheduleDate;

    @Schema(description = "开始时间", example = "14:00:00")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @Schema(description = "结束时间", example = "16:00:00")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    @Schema(description = "日程类型，字典类型：schedule_type", example = "meeting")
    private String scheduleType;

    @Schema(description = "日程分类，字典类型：schedule_category", example = "work")
    private String scheduleCategory;

    @Schema(description = "创建人ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long creatorId;

    @Schema(description = "创建人姓名", example = "张三")
    private String creatorName;

    @Schema(description = "是否推送", example = "false")
    private Boolean isPushed;

    @Schema(description = "状态，参见 CommonStatusEnum 枚举类", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "备注", example = "重要会议")
    private String remark;

    @Schema(description = "接收人列表")
    private List<ReceiverVO> receivers;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;

    @Schema(description = "接收人信息")
    @Data
    public static class ReceiverVO {
        @Schema(description = "接收人ID", example = "1")
        private Long receiverId;

        @Schema(description = "接收人姓名", example = "李四")
        private String receiverName;

        @Schema(description = "已读状态（0未读 1已读）", example = "0")
        private Integer readStatus;

        @Schema(description = "已读时间")
        private LocalDateTime readTime;
    }

}

