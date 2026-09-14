package cn.iocoder.yudao.module.hrm.controller.admin.employee.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 员工教育经历 VO")
@Data
public class EmployeeEducationVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2015-09-01")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate startTime;

    @Schema(description = "截止时间", example = "2019-06-30")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate endTime;

    @Schema(description = "专业", example = "计算机科学与技术")
    private String major;

    @Schema(description = "学校名称", example = "清华大学")
    private String schoolName;

}

