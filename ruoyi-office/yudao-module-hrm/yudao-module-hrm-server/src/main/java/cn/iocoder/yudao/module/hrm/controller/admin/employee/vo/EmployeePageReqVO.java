package cn.iocoder.yudao.module.hrm.controller.admin.employee.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 员工档案分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmployeePageReqVO extends PageParam {

    @Schema(description = "员工编号", example = "EMP001")
    private String employeeNo;

    @Schema(description = "姓名", example = "张三")
    private String name;

    @Schema(description = "所属部门", example = "1")
    private Long deptId;

    @Schema(description = "人员状态（1:正式 2:试用期 3:实习生 4:兼职 5:零时工）", example = "1")
    private Integer employeeStatus;

    @Schema(description = "入职日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] entryDate;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}

