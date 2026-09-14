package cn.iocoder.yudao.module.system.controller.admin.schedule.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Schema(description = "管理后台 - 日程分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class SchedulePageReqVO extends PageParam {

    @Schema(description = "标题，模糊匹配", example = "销售部例会")
    private String title;

    @Schema(description = "日程日期", example = "2026-01-15")
    private LocalDate scheduleDate;

    @Schema(description = "日程日期开始", example = "2026-01-01")
    private LocalDate scheduleDateStart;

    @Schema(description = "日程日期结束", example = "2026-01-31")
    private LocalDate scheduleDateEnd;

    @Schema(description = "日程类型，字典类型：schedule_type", example = "meeting")
    private String scheduleType;

    @Schema(description = "日程分类，字典类型：schedule_category", example = "work")
    private String scheduleCategory;

    @Schema(description = "创建人ID", example = "1")
    private Long creatorId;

    @Schema(description = "状态，参见 CommonStatusEnum 枚举类", example = "1")
    private Integer status;

}

