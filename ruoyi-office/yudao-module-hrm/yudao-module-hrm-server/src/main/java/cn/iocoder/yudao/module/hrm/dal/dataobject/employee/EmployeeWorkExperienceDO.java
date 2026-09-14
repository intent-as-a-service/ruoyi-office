package cn.iocoder.yudao.module.hrm.dal.dataobject.employee;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

/**
 * 员工工作经历 DO
 *
 * @author 宇擎源码
 */
@TableName("hrm_employee_work_experience")
@KeySequence("hrm_employee_work_experience_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeWorkExperienceDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 开始时间
     */
    private LocalDate startTime;

    /**
     * 截止时间
     */
    private LocalDate endTime;

    /**
     * 职务
     */
    private String jobPosition;

    /**
     * 单位名称
     */
    private String companyName;

}

