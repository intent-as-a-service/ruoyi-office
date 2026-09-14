package cn.iocoder.yudao.module.system.controller.admin.schedule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "管理后台 - 日程更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ScheduleUpdateReqVO extends ScheduleCreateReqVO {

    @Schema(description = "日程ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "日程ID不能为空")
    private Long id;

}

