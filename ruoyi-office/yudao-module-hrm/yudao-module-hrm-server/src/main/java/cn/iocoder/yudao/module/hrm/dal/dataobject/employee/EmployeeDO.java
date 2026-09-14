package cn.iocoder.yudao.module.hrm.dal.dataobject.employee;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 员工档案 DO
 *
 * @author 宇擎源码
 */
@TableName("hrm_employee")
@KeySequence("hrm_employee_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 员工编号
     */
    private String employeeNo;

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
     * 血型（1:A 2:B 3:AB 4:O）
     */
    private Integer bloodType;

    /**
     * 文化程度
     */
    private String education;

    /**
     * 民族
     */
    private String nation;
    /**
     * 政治面貌
     */
    private String politicalStatus;
    /**
     * 婚姻状况
     */
    private String maritalStatus;

    /**
     * 职称
     */
    private String jobTitle;

    /**
     * 籍贯
     */
    private String nativePlace;

    /**
     * 身高(cm)
     */
    private BigDecimal height;

    /**
     * 体重(kg)
     */
    private BigDecimal weight;

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

    /**
     * 工资开户行
     */
    private String bankName;

    /**
     * 工资卡账户
     */
    private String bankAccount;

    /**
     * 职位
     */
    private String jobPost;

    /**
     * 职务
     */
    private String jobPosition;

    /**
     * 人员状态（1:正式 2:试用期 3:实习生 4:兼职 5:零时工）
     */
    private Integer employeeStatus;

    /**
     * 所属部门
     */
    private Long deptId;

    /**
     * 所属部门名称
     */
    private String deptName;

    /**
     * 所属公司ID
     */
    private Long companyId;

    /**
     * 所属单位
     */
    private String companyName;

    /**
     * 入职日期
     */
    private LocalDate entryDate;

    /**
     * 转正日期
     */
    private LocalDate formalDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 关联用户ID
     */
    private Long userId;

    /**
     * 是否已生成用户
     */
    private Boolean userGenerated;

}

