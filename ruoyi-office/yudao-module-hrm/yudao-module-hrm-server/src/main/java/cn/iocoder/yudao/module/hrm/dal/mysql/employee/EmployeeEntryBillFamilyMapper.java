package cn.iocoder.yudao.module.hrm.dal.mysql.employee;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hrm.dal.dataobject.employee.EmployeeEntryBillFamilyDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 员工入职申请单家属信息 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface EmployeeEntryBillFamilyMapper extends BaseMapperX<EmployeeEntryBillFamilyDO> {

    default List<EmployeeEntryBillFamilyDO> selectListByEntryBillId(Long entryBillId) {
        return selectList(new LambdaQueryWrapperX<EmployeeEntryBillFamilyDO>()
                .eq(EmployeeEntryBillFamilyDO::getBillId, entryBillId));
    }

    default void deleteByEntryBillId(Long entryBillId) {
        delete(new LambdaQueryWrapperX<EmployeeEntryBillFamilyDO>()
                .eq(EmployeeEntryBillFamilyDO::getBillId, entryBillId));
    }

}


