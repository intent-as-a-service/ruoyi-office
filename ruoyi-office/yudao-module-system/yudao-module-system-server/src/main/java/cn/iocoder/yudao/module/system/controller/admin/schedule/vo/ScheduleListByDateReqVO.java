package cn.iocoder.yudao.module.system.controller.admin.schedule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "管理后台 - 按日期查询日程 Request VO")
@Data
public class ScheduleListByDateReqVO {

    @Schema(description = "日程日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-01-15")
    @NotNull(message = "日程日期不能为空")
    private LocalDate scheduleDate;

}

