package cn.iocoder.yudao.module.hrm.controller.admin.employee.vo;

import cn.iocoder.yudao.common.server.attachment.controller.vo.AttachmentRespVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;

import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 员工转正申请单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class EmployeeRegularBillRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1882")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "单据编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("单据编号")
    private String billCode;

    @Schema(description = "流程实例编号", example = "16629")
    @ExcelProperty("流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态", example = "2")
    @ExcelProperty("单据状态")
    private Integer processStatus;

    // ========== 员工信息 ==========
    @Schema(description = "关联的员工档案ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("员工档案ID")
    private Long employeeId;

    @Schema(description = "员工工号", example = "E001")
    @ExcelProperty("员工工号")
    private String employeeNo;

    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("姓名")
    private String name;

    @Schema(description = "性别（1:男 2:女）", example = "1")
    @ExcelProperty("性别")
    private Integer sex;

    @Schema(description = "员工所属部门ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("员工所属部门ID")
    private Long empDeptId;

    @Schema(description = "员工所属部门名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "技术部")
    @ExcelProperty("员工所属部门名称")
    private String empDeptName;

    @Schema(description = "员工所属公司ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("员工所属公司ID")
    private Long empCompanyId;

    @Schema(description = "员工所属公司名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "宇擎科技")
    @ExcelProperty("员工所属公司名称")
    private String empCompanyName;

    @Schema(description = "职位", example = "产品经理")
    @ExcelProperty("职位")
    private String jobPost;

    @Schema(description = "职务", example = "部门经理")
    @ExcelProperty("职务")
    private String jobPosition;

    @Schema(description = "当前人员状态（1:正式 2:试用期 3:实习生 4:兼职 5:零时工）", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("人员状态")
    private Integer employeeStatus;

    @Schema(description = "入职日期", example = "2023-01-01")
    @ExcelProperty("入职日期")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate entryDate;

    @Schema(description = "转正日期", example = "2023-04-01")
    @ExcelProperty("转正日期")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate formalDate;

    @Schema(description = "预计转正日期", example = "2023-04-01")
    @ExcelProperty("预计转正日期")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate expectedFormalDate;

    @Schema(description = "试用期（月数）", example = "3")
    @ExcelProperty("试用期（月数）")
    private Integer probationPeriod;

    // ========== 制单人信息（单据必须的信息） ==========
    @Schema(description = "制单人部门ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("制单人部门ID")
    private Long deptId;

    @Schema(description = "制单人部门名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "人事部")
    @ExcelProperty("制单人部门名称")
    private String deptName;

    @Schema(description = "制单人公司ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("制单人公司ID")
    private Long companyId;

    @Schema(description = "制单人公司名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "宇擎科技")
    @ExcelProperty("制单人公司名称")
    private String companyName;

    @Schema(description = "创建者", example = "1")
    @ExcelProperty("创建者")
    private String creator;

    @Schema(description = "创建者姓名", example = "芋艿")
    @ExcelProperty("创建者姓名")
    private String creatorName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "备注", example = "试用期表现优秀")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "附件列表")
    private List<AttachmentRespVO> attachments;

}

