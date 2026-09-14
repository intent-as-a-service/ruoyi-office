package cn.iocoder.yudao.module.hrm.controller.admin.employee.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 员工档案选择分页 Request VO
 *
 * 仅用于选择弹窗
 *
 * @author 宇擎源码
 */
@Schema(description = "管理后台 - 员工档案选择分页 Request VO（过滤正式员工）")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmployeeSelectPageReqVO extends PageParam {

    @Schema(description = "员工编号", example = "EMP001")
    private String employeeNo;

    @Schema(description = "姓名", example = "张三")
    private String name;

    @Schema(description = "所属部门", example = "1")
    private Long deptId;

    @Schema(description = "职位", example = "产品经理")
    private String jobPost;

    @Schema(description = "职务", example = "部门经理")
    private String jobPosition;

    @Schema(description = "人员状态（可选，精确筛选某个状态）", example = "2")
    private Integer employeeStatus;

    @Schema(description = "需要包含的人员状态集合（优先级高于excludeEmployeeStatusList），例如 [2, 3, 5]", example = "[2,3,5]")
    private List<Integer> includeEmployeeStatusList;

    @Schema(description = "需要排除的人员状态集合，例如 [1, 3]", example = "[1,3]")
    private List<Integer> excludeEmployeeStatusList;

    @Schema(description = "入职日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] entryDate;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}

