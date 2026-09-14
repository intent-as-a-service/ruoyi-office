package cn.iocoder.yudao.module.hrm.dal.dataobject.employee;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

/**
 * 员工离职申请单 DO
 *
 * @author 宇擎源码
 */
@TableName("hrm_employee_resignation_bill")
@KeySequence("hrm_employee_resignation_bill_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResignationBillDO extends BaseDO {

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
     * 手机号
     */
    private String mobile;

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

    // ========== 离职信息 ==========
    /**
     * 离职类型（1:主动离职 2:被动离职 3:其他）
     */
    private String resignationType;

    /**
     * 申请日期
     */
    private LocalDate applicationDate;

    /**
     * 离职日期
     */
    private LocalDate resignationDate;

    /**
     * 最后工作日期
     */
    private LocalDate lastWorkingDate;

    /**
     * 工作交接人ID
     */
    private Long handoverPersonId;

    /**
     * 工作交接人姓名
     */
    private String handoverPersonName;

    /**
     * 离职原因（1:个人原因 2:薪资原因 3:晋升原因 4:工作时长 5:其它）
     */
    private String resignationReason;

    /**
     * 离职原因说明
     */
    private String resignationReasonDesc;

    /**
     * 薪资结算日期
     */
    private LocalDate salarySettlementDate;

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

