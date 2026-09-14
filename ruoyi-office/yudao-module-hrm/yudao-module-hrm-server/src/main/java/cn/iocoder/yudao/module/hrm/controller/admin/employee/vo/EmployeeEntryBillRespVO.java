package cn.iocoder.yudao.module.hrm.controller.admin.employee.vo;

import cn.iocoder.yudao.common.server.attachment.controller.vo.AttachmentRespVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 员工入职申请单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class EmployeeEntryBillRespVO {

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

    // ========== 员工基本信息 ==========
    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("姓名")
    private String name;

    @Schema(description = "性别（1:男 2:女）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("性别")
    private Integer sex;

    @Schema(description = "出生日期", example = "2000-01-01")
    @ExcelProperty("出生日期")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate birthday;

    @Schema(description = "身份证号码", example = "110101199001011234")
    @ExcelProperty("身份证号码")
    private String idCard;

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    @ExcelProperty("手机号")
    private String mobile;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    @ExcelProperty("邮箱")
    private String email;

    @Schema(description = "民族", example = "汉族")
    @ExcelProperty("民族")
    private String nation;

    @Schema(description = "政治面貌", example = "中共党员")
    @ExcelProperty("政治面貌")
    private String politicalStatus;

    @Schema(description = "婚姻状况", example = "已婚")
    @ExcelProperty("婚姻状况")
    private String maritalStatus;

    @Schema(description = "籍贯", example = "北京市海淀区")
    @ExcelProperty("籍贯")
    private String nativePlace;

    @Schema(description = "户籍所在地", example = "北京市海淀区中关村大街1号")
    @ExcelProperty("户籍所在地")
    private String householdAddress;

    @Schema(description = "现居住地址", example = "北京市朝阳区建国路1号")
    @ExcelProperty("现居住地址")
    private String currentAddress;

    @Schema(description = "紧急联系人", example = "李四")
    @ExcelProperty("紧急联系人")
    private String emergencyContact;

    @Schema(description = "联系电话", example = "13900139000")
    @ExcelProperty("联系电话")
    private String emergencyPhone;

    @Schema(description = "照片", example = "http://127.0.0.1:48080/admin-api/infra/file/4/get/xxx.jpg")
    @ExcelProperty("照片")
    private String avatar;

    // ========== 入职相关信息（员工所属的组织信息） ==========
    @Schema(description = "入职日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-01-01")
    @ExcelProperty("入职日期")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate entryDate;

    @Schema(description = "试用期（月数）", example = "3")
    @ExcelProperty("试用期（月数）")
    private Integer probationPeriod;

    @Schema(description = "预计转正日期", example = "2023-04-01")
    @ExcelProperty("预计转正日期")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate expectedFormalDate;

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

    @Schema(description = "职称", example = "高级工程师")
    @ExcelProperty("职称")
    private String jobTitle;

    @Schema(description = "人员状态（1:正式 2:试用期 3:实习生 4:兼职 5:零时工）", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("人员状态")
    private Integer employeeStatus;

    @Schema(description = "文化程度", example = "本科")
    @ExcelProperty("文化程度")
    private String education;

    @Schema(description = "薪资", example = "15000.00")
    @ExcelProperty("薪资")
    private BigDecimal salary;

    @Schema(description = "工资开户行", example = "中国工商银行北京分行")
    @ExcelProperty("工资开户行")
    private String bankName;

    @Schema(description = "工资卡账户", example = "6222021234567890123")
    @ExcelProperty("工资卡账户")
    private String bankAccount;

    // ========== 关联字段 ==========
    @Schema(description = "关联的员工档案ID（审批通过后创建）", example = "1")
    @ExcelProperty("关联的员工档案ID")
    private Long employeeId;

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

    @Schema(description = "备注", example = "优秀员工")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "附件列表")
    private List<AttachmentRespVO> attachments;

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

