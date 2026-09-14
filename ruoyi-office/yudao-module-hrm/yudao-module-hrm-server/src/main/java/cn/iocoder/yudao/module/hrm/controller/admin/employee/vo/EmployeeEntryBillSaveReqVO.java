package cn.iocoder.yudao.module.hrm.controller.admin.employee.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.List;
import cn.iocoder.yudao.common.server.attachment.controller.vo.AttachmentSaveReqVO;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 员工入职申请单新增/修改 Request VO")
@Data
public class EmployeeEntryBillSaveReqVO {

    @Schema(description = "ID", example = "1024")
    private Long id;

    @Schema(description = "单据编号", example = "RZ202412010001")
    private String billCode;

    @Schema(description = "流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态", example = "0")
    private Integer processStatus;

    // ========== 员工基本信息 ==========
    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String name;

    @Schema(description = "性别（1:男 2:女）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sex;

    @Schema(description = "出生日期", example = "2000-01-01")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate birthday;

    @Schema(description = "身份证号码", example = "110101199001011234")
    private String idCard;

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    private String mobile;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "民族", example = "汉族")
    private String nation;

    @Schema(description = "政治面貌", example = "中共党员")
    private String politicalStatus;

    @Schema(description = "婚姻状况", example = "已婚")
    private String maritalStatus;

    @Schema(description = "籍贯", example = "北京市海淀区")
    private String nativePlace;

    @Schema(description = "户籍所在地", example = "北京市海淀区中关村大街1号")
    private String householdAddress;

    @Schema(description = "现居住地址", example = "北京市朝阳区建国路1号")
    private String currentAddress;

    @Schema(description = "紧急联系人", example = "李四")
    private String emergencyContact;

    @Schema(description = "联系电话", example = "13900139000")
    private String emergencyPhone;

    @Schema(description = "照片", example = "http://127.0.0.1:48080/admin-api/infra/file/4/get/xxx.jpg")
    private String avatar;

    // ========== 入职相关信息（员工所属的组织信息） ==========
    @Schema(description = "入职日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-01-01")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate entryDate;

    @Schema(description = "试用期（月数）", example = "3")
    private Integer probationPeriod;

    @Schema(description = "预计转正日期", example = "2023-04-01")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate expectedFormalDate;

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

    @Schema(description = "职称", example = "高级工程师")
    private String jobTitle;

    @Schema(description = "人员状态（1:正式 2:试用期 3:实习生 4:兼职 5:零时工）", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer employeeStatus;

    @Schema(description = "文化程度", example = "本科")
    private String education;

    @Schema(description = "薪资", example = "15000.00")
    private BigDecimal salary;

    @Schema(description = "工资开户行", example = "中国工商银行北京分行")
    private String bankName;

    @Schema(description = "工资卡账户", example = "6222021234567890123")
    private String bankAccount;

    // ========== 关联字段 ==========
    @Schema(description = "关联的员工档案ID（审批通过后创建）", example = "1")
    private Long employeeId;

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

    @Schema(description = "备注", example = "优秀员工")
    private String remark;

    @Schema(description = "附件列表")
    private List<AttachmentSaveReqVO> attachments;

    /**
     * 工作经历列表
     */
    @Schema(description = "工作经历列表")
    private List<EmployeeWorkExperienceVO> workExperienceList;

    /**
     * 教育经历列表
     */
    @Schema(description = "教育经历列表")
    private List<EmployeeEducationVO> educationList;

    /**
     * 家属信息列表
     */
    @Schema(description = "家属信息列表")
    private List<EmployeeFamilyVO> familyList;

}


