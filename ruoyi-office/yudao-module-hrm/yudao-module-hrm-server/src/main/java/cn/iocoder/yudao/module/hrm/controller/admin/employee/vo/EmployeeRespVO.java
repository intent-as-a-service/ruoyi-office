package cn.iocoder.yudao.module.hrm.controller.admin.employee.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 员工档案 Response VO")
@Data
@ExcelIgnoreUnannotated
public class EmployeeRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "员工编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "EMP001")
    @ExcelProperty("员工编号")
    private String employeeNo;

    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("姓名")
    private String name;

    @Schema(description = "性别（1:男 2:女）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "性别", converter = DictConvert.class)
    @DictFormat("system_user_sex")
    private Integer sex;

    @Schema(description = "出生日期", example = "2000-01-01")
    @ExcelProperty("出生日期")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate birthday;

    @Schema(description = "血型（1:A 2:B 3:AB 4:O）", example = "1")
    @ExcelProperty(value = "血型", converter = DictConvert.class)
    @DictFormat("hrm_blood_type")
    private Integer bloodType;

    @Schema(description = "文化程度", example = "本科")
    @ExcelProperty("文化程度")
    private String education;

    @Schema(description = "民族", example = "汉族")
    @ExcelProperty("民族")
    private String nation;

    @Schema(description = "职称", example = "高级工程师")
    @ExcelProperty("职称")
    private String jobTitle;

    @Schema(description = "籍贯", example = "北京市海淀区")
    @ExcelProperty("籍贯")
    private String nativePlace;

    @Schema(description = "政治面貌", example = "中共党员")
    @ExcelProperty("政治面貌")
    private String politicalStatus;

    @Schema(description = "婚姻状况", example = "已婚")
    @ExcelProperty("婚姻状况")
    private String maritalStatus;

    @Schema(description = "身高(cm)", example = "175")
    @ExcelProperty("身高(cm)")
    private BigDecimal height;

    @Schema(description = "体重(kg)", example = "65")
    @ExcelProperty("体重(kg)")
    private BigDecimal weight;

    @Schema(description = "身份证号码", example = "110101199001011234")
    @ExcelProperty("身份证号码")
    private String idCard;

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    @ExcelProperty("手机号")
    private String mobile;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    @ExcelProperty("邮箱")
    private String email;

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

    @Schema(description = "工资开户行", example = "中国工商银行北京分行")
    @ExcelProperty("工资开户行")
    private String bankName;

    @Schema(description = "工资卡账户", example = "6222021234567890123")
    @ExcelProperty("工资卡账户")
    private String bankAccount;

    @Schema(description = "职位", example = "产品经理")
    @ExcelProperty("职位")
    private String jobPost;

    @Schema(description = "职务", example = "部门经理")
    @ExcelProperty("职务")
    private String jobPosition;

    @Schema(description = "人员状态（1:正式 2:试用期 3:实习生 4:兼职 5:零时工）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "人员状态", converter = DictConvert.class)
    @DictFormat("hrm_employee_status")
    private Integer employeeStatus;

    @Schema(description = "所属部门", example = "1")
    @ExcelProperty("所属部门ID")
    private Long deptId;

    @Schema(description = "所属部门名称", example = "研发部")
    @ExcelProperty("所属部门")
    private String deptName;

    @Schema(description = "所属公司ID", example = "1")
    @ExcelProperty("所属公司ID")
    private Long companyId;

    @Schema(description = "所属单位", example = "北京创星科技发展有限公司")
    @ExcelProperty("所属单位")
    private String companyName;

    @Schema(description = "入职日期", example = "2023-01-01")
    @ExcelProperty("入职日期")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate entryDate;

    @Schema(description = "转正日期", example = "2023-04-01")
    @ExcelProperty("转正日期")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate formalDate;

    @Schema(description = "备注", example = "优秀员工")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "关联用户ID", example = "1")
    private Long userId;

    @Schema(description = "是否已生成用户", example = "true")
    @ExcelProperty("是否已生成用户")
    private Boolean userGenerated;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

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

