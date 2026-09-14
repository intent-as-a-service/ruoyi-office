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

@Schema(description = "管理后台 - 人事调动申请单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class EmployeeTransferBillRespVO {

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

    @Schema(description = "手机号", example = "13800138000")
    @ExcelProperty("手机号")
    private String mobile;

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

    @Schema(description = "当前人员状态（1:正式 2:试用期 3:实习生 4:兼职 5:零时工）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("人员状态")
    private Integer employeeStatus;

    // ========== 调动信息 ==========
    @Schema(description = "异动类型（1:调岗 2:调薪 3:调部门 4:调公司 5:其他）", example = "1")
    @ExcelProperty("异动类型")
    private String transferType;

    @Schema(description = "异动原因", example = "工作需要")
    @ExcelProperty("异动原因")
    private String transferReason;

    @Schema(description = "原职位", example = "产品经理")
    @ExcelProperty("原职位")
    private String originalJobPost;

    @Schema(description = "变更为职位", example = "高级产品经理")
    @ExcelProperty("变更为职位")
    private String newJobPost;

    @Schema(description = "原职务", example = "部门经理")
    @ExcelProperty("原职务")
    private String originalJobPosition;

    @Schema(description = "变更为职务", example = "高级部门经理")
    @ExcelProperty("变更为职务")
    private String newJobPosition;

    @Schema(description = "原公司ID", example = "1")
    @ExcelProperty("原公司ID")
    private Long originalCompanyId;

    @Schema(description = "原公司名称", example = "宇擎科技")
    @ExcelProperty("原公司名称")
    private String originalCompanyName;

    @Schema(description = "原部门ID", example = "1")
    @ExcelProperty("原部门ID")
    private Long originalDeptId;

    @Schema(description = "原部门名称", example = "技术部")
    @ExcelProperty("原部门名称")
    private String originalDeptName;

    @Schema(description = "变更为公司ID", example = "2")
    @ExcelProperty("变更为公司ID")
    private Long newCompanyId;

    @Schema(description = "变更为公司名称", example = "宇擎科技（上海）")
    @ExcelProperty("变更为公司名称")
    private String newCompanyName;

    @Schema(description = "变更为部门ID", example = "2")
    @ExcelProperty("变更为部门ID")
    private Long newDeptId;

    @Schema(description = "变更为部门名称", example = "产品部")
    @ExcelProperty("变更为部门名称")
    private String newDeptName;

    @Schema(description = "是否立即生效", example = "false")
    @ExcelProperty("是否立即生效")
    private Boolean effectiveImmediately;

    @Schema(description = "生效日期", example = "2024-01-01")
    @ExcelProperty("生效日期")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate effectiveDate;

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

    @Schema(description = "备注", example = "因业务需要调动")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "附件列表")
    private List<AttachmentRespVO> attachments;

}




