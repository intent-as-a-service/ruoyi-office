package cn.iocoder.yudao.module.hrm.dal.mysql.employee;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hrm.dal.dataobject.employee.EmployeeFamilyDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 员工家属信息 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface EmployeeFamilyMapper extends BaseMapperX<EmployeeFamilyDO> {

    default List<EmployeeFamilyDO> selectListByEmployeeId(Long employeeId) {
        return selectList(new LambdaQueryWrapperX<EmployeeFamilyDO>()
                .eq(EmployeeFamilyDO::getEmployeeId, employeeId)
                .orderByDesc(EmployeeFamilyDO::getId));
    }

    default void deleteByEmployeeId(Long employeeId) {
        delete(new LambdaQueryWrapperX<EmployeeFamilyDO>()
                .eq(EmployeeFamilyDO::getEmployeeId, employeeId));
    }

}

