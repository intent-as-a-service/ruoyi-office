package cn.iocoder.yudao.module.hrm.controller.admin.employee.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;
import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 员工转正申请单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmployeeRegularBillPageReqVO extends PageParam {

    @Schema(description = "单据编号", example = "ZZ202412010001")
    private String billCode;

    @Schema(description = "单据状态", example = "1")
    private Integer processStatus;

    @Schema(description = "员工工号", example = "E001")
    private String employeeNo;

    @Schema(description = "姓名", example = "张三")
    private String name;

    @Schema(description = "员工所属部门ID", example = "1")
    private Long empDeptId;

    @Schema(description = "员工所属部门名称", example = "技术部")
    private String empDeptName;

    @Schema(description = "员工所属公司ID", example = "1")
    private Long empCompanyId;

    @Schema(description = "员工所属公司名称", example = "宇擎科技")
    private String empCompanyName;

    @Schema(description = "人员状态", example = "2")
    private Integer employeeStatus;

    @Schema(description = "创建人（用户ID）", example = "1")
    private String creator;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}

