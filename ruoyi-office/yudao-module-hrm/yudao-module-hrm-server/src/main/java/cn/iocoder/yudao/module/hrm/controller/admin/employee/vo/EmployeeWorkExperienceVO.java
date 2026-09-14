package cn.iocoder.yudao.module.hrm.controller.admin.employee.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 员工工作经历 VO")
@Data
public class EmployeeWorkExperienceVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2020-01-01")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate startTime;

    @Schema(description = "截止时间", example = "2023-01-01")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate endTime;

    @Schema(description = "职务", example = "高级工程师")
    private String jobPosition;

    @Schema(description = "单位名称", example = "阿里巴巴集团")
    private String companyName;

}

