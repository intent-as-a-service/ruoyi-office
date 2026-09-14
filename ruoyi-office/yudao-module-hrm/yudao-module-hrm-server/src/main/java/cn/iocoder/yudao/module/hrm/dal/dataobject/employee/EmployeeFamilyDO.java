package cn.iocoder.yudao.module.hrm.dal.dataobject.employee;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 员工家属信息 DO
 *
 * @author 宇擎源码
 */
@TableName("hrm_employee_family")
@KeySequence("hrm_employee_family_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeFamilyDO extends BaseDO {

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

