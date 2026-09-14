package cn.iocoder.yudao.module.hrm.dal.dataobject.employee;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

/**
 * 员工转正申请单 DO
 *
 * @author 宇擎源码
 */
@TableName("hrm_employee_regular_bill")
@KeySequence("hrm_employee_regular_bill_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRegularBillDO extends BaseDO {

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

    // ========== 员工信息（冗余存储，方便审批时查看） ==========
    /**
     * 关联的员工档案ID
     */
    private Long employeeId;

    /**
     * 员工工号
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
     * 当前人员状态（1:正式 2:试用期 3:实习生 4:兼职 5:零时工）
     */
    private Integer employeeStatus;

    /**
     * 入职日期
     */
    private LocalDate entryDate;

    /**
     * 转正日期
     */
    private LocalDate formalDate;

    /**
     * 预计转正日期
     */
    private LocalDate expectedFormalDate;

    /**
     * 试用期（月数）
     */
    private Integer probationPeriod;

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

