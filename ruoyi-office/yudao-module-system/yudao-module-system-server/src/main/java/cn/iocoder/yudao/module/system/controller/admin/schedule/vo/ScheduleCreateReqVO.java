package cn.iocoder.yudao.module.system.controller.admin.schedule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Schema(description = "管理后台 - 日程创建 Request VO")
@Data
public class ScheduleCreateReqVO {

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "销售部例会")
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题不能超过200个字符")
    private String title;

    @Schema(description = "内容", example = "讨论本月销售情况")
    private String content;

    @Schema(description = "日程日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-01-15")
    @NotNull(message = "日程日期不能为空")
    private LocalDate scheduleDate;

    @Schema(description = "开始时间", example = "14:00:00")
    private LocalTime startTime;

    @Schema(description = "结束时间", example = "16:00:00")
    private LocalTime endTime;

    @Schema(description = "日程类型，字典类型：schedule_type", example = "meeting")
    private String scheduleType;

    @Schema(description = "日程分类，字典类型：schedule_category", example = "work")
    private String scheduleCategory;

    @Schema(description = "接收人ID列表（用于推送）", example = "[1, 2, 3]")
    private List<Long> receiverIds;

    @Schema(description = "备注", example = "重要会议")
    private String remark;

}

