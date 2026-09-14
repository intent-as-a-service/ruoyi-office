package cn.iocoder.yudao.module.hrm.dal.dataobject.employee;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

/**
 * 员工入职申请单工作经历 DO
 *
 * @author 宇擎源码
 */
@TableName("hrm_employee_entry_bill_work_experience")
@KeySequence("hrm_employee_entry_bill_work_experience_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEntryBillWorkExperienceDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 入职申请单ID
     */
    private Long billId;

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


