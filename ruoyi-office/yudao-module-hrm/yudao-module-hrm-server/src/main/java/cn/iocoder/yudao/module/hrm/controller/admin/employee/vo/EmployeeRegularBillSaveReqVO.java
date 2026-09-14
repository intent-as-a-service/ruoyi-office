package cn.iocoder.yudao.module.hrm.controller.admin.employee.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.List;
import cn.iocoder.yudao.common.server.attachment.controller.vo.AttachmentSaveReqVO;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 员工转正申请单新增/修改 Request VO")
@Data
public class EmployeeRegularBillSaveReqVO {

    @Schema(description = "ID", example = "1024")
    private Long id;

    @Schema(description = "单据编号", example = "ZZ202412010001")
    private String billCode;

    @Schema(description = "流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态", example = "0")
    private Integer processStatus;

    // ========== 员工信息 ==========
    @Schema(description = "关联的员工档案ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long employeeId;

    @Schema(description = "员工工号", example = "E001")
    private String employeeNo;

    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String name;

    @Schema(description = "性别（1:男 2:女）", example = "1")
    private Integer sex;

    @Schema(description = "员工所属部门ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long empDeptId;

    @Schema(description = "员工所属部门名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "技术部")
    private String empDeptName;

    @Schema(description = "员工所属公司ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long empCompanyId;

    @Schema(description = "员工所属公司名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "宇擎科技")
    private String empCompanyName;

    @Schema(description = "职位", example = "产品经理")
    private String jobPost;

    @Schema(description = "职务", example = "部门经理")
    private String jobPosition;

    @Schema(description = "当前人员状态（1:正式 2:试用期 3:实习生 4:兼职 5:零时工）", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer employeeStatus;

    @Schema(description = "入职日期", example = "2023-01-01")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate entryDate;

    @Schema(description = "转正日期", example = "2023-04-01")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate formalDate;

    @Schema(description = "预计转正日期", example = "2023-04-01")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate expectedFormalDate;

    @Schema(description = "试用期（月数）", example = "3")
    private Integer probationPeriod;

    // ========== 制单人信息（单据必须的信息） ==========
    @Schema(description = "制单人部门ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "制单人部门ID不能为空")
    private Long deptId;

    @Schema(description = "制单人部门名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "人事部")
    @NotEmpty(message = "制单人部门名称不能为空")
    private String deptName;

    @Schema(description = "制单人公司ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "制单人公司ID不能为空")
    private Long companyId;

    @Schema(description = "制单人公司名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "宇擎科技")
    @NotEmpty(message = "制单人公司名称不能为空")
    private String companyName;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "创建者姓名", example = "芋艿")
    private String creatorName;
    @Schema(description = "备注", example = "试用期表现优秀")
    private String remark;

    @Schema(description = "附件列表")
    private List<AttachmentSaveReqVO> attachments;

}

