package cn.iocoder.yudao.module.hrm.dal.dataobject.employee;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 员工入职申请单 DO
 *
 * @author 宇擎源码
 */
@TableName("hrm_employee_entry_bill")
@KeySequence("hrm_employee_entry_bill_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEntryBillDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;

    /**
     * 单据编号
     */
    private String billCode;

    /**
     * 流程实例编号
     */
    private String processInstanceId;

    /**
     * 单据状态（0草稿 1审批中 2审批通过 3审批拒绝 4已取消）
     */
    private Integer processStatus;

    /**
     * 多租户编号
     */
    private Long tenantId;

    // ========== 员工基本信息 ==========
    /**
     * 姓名
     */
    private String name;

    /**
     * 性别（1:男 2:女）
     */
    private Integer sex;

    /**
     * 出生日期
     */
    private LocalDate birthday;

    /**
     * 身份证号码
     */
    private String idCard;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 民族
     */
    private String nation;

    /**
     * 籍贯
     */
    private String nativePlace;
    /**
     * 政治面貌
     */
    private String politicalStatus;
    /**
     * 婚姻状况
     */
    private String maritalStatus;

    /**
     * 户籍所在地
     */
    private String householdAddress;

    /**
     * 现居住地址
     */
    private String currentAddress;

    /**
     * 紧急联系人
     */
    private String emergencyContact;

    /**
     * 联系电话
     */
    private String emergencyPhone;

    /**
     * 照片
     */
    private String avatar;

    // ========== 入职相关信息（员工所属的组织信息） ==========
    /**
     * 入职日期
     */
    private LocalDate entryDate;

    /**
     * 试用期（月数）
     */
    private Integer probationPeriod;

    /**
     * 预计转正日期
     */
    private LocalDate expectedFormalDate;

    /**
     * 员工所属部门ID
     */
    private Long empDeptId;

    /**
     * 员工所属部门名称
     */
    private String empDeptName;

    /**
     * 员工所属公司ID
     */
    private Long empCompanyId;

    /**
     * 员工所属公司名称
     */
    private String empCompanyName;

    /**
     * 职位
     */
    private String jobPost;

    /**
     * 职务
     */
    private String jobPosition;

    /**
     * 职称
     */
    private String jobTitle;

    /**
     * 人员状态（1:正式 2:试用期 3:实习生 4:兼职 5:零时工）
     */
    private Integer employeeStatus;

    /**
     * 文化程度
     */
    private String education;

    /**
     * 薪资
     */
    private BigDecimal salary;

    /**
     * 工资开户行
     */
    private String bankName;

    /**
     * 工资卡账户
     */
    private String bankAccount;

    // ========== 关联字段 ==========
    /**
     * 关联的员工档案ID（审批通过后创建）
     */
    private Long employeeId;

    // ========== 制单人信息（单据必须的信息） ==========
    /**
     * 制单人部门ID
     */
    private Long deptId;

    /**
     * 制单人部门名称
     */
    private String deptName;

    /**
     * 制单人公司ID
     */
    private Long companyId;

    /**
     * 制单人公司名称
     */
    private String companyName;

    /**
     * 创建者姓名
     */
    private String creatorName;

    /**
     * 备注
     */
    private String remark;

}


