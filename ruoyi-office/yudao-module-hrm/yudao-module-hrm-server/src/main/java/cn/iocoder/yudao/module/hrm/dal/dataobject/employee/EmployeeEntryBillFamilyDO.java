package cn.iocoder.yudao.module.hrm.dal.dataobject.employee;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 员工入职申请单家属信息 DO
 *
 * @author 宇擎源码
 */
@TableName("hrm_employee_entry_bill_family")
@KeySequence("hrm_employee_entry_bill_family_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEntryBillFamilyDO extends BaseDO {

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
     * 姓名
     */
    private String name;

    /**
     * 关系
     */
    private String relationship;

    /**
     * 联系电话
     */
    private String mobile;

    /**
     * 工作单位
     */
    private String workUnit;

}


