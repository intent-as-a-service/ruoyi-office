package cn.iocoder.yudao.module.hrm.dal.mysql.employee;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hrm.dal.dataobject.employee.EmployeeWorkExperienceDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 员工工作经历 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface EmployeeWorkExperienceMapper extends BaseMapperX<EmployeeWorkExperienceDO> {

    default List<EmployeeWorkExperienceDO> selectListByEmployeeId(Long employeeId) {
        return selectList(new LambdaQueryWrapperX<EmployeeWorkExperienceDO>()
                .eq(EmployeeWorkExperienceDO::getEmployeeId, employeeId)
                .orderByDesc(EmployeeWorkExperienceDO::getStartTime));
    }

    default void deleteByEmployeeId(Long employeeId) {
        delete(new LambdaQueryWrapperX<EmployeeWorkExperienceDO>()
                .eq(EmployeeWorkExperienceDO::getEmployeeId, employeeId));
    }

}

