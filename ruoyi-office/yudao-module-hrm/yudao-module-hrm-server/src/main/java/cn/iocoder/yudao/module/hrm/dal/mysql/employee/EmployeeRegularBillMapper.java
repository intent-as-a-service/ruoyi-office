package cn.iocoder.yudao.module.hrm.dal.mysql.employee;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.hrm.dal.dataobject.employee.EmployeeRegularBillDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.hrm.controller.admin.employee.vo.EmployeeRegularBillPageReqVO;

/**
 * 员工转正申请单 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface EmployeeRegularBillMapper extends BaseMapperX<EmployeeRegularBillDO> {

    default PageResult<EmployeeRegularBillDO> selectPage(EmployeeRegularBillPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<EmployeeRegularBillDO>()
                .likeIfPresent(EmployeeRegularBillDO::getBillCode, reqVO.getBillCode())
                .eqIfPresent(EmployeeRegularBillDO::getProcessStatus, reqVO.getProcessStatus())
                .likeIfPresent(EmployeeRegularBillDO::getEmployeeNo, reqVO.getEmployeeNo())
                .likeIfPresent(EmployeeRegularBillDO::getName, reqVO.getName())
                .eqIfPresent(EmployeeRegularBillDO::getEmpDeptId, reqVO.getEmpDeptId())
                .likeIfPresent(EmployeeRegularBillDO::getEmpDeptName, reqVO.getEmpDeptName())
                .eqIfPresent(EmployeeRegularBillDO::getEmpCompanyId, reqVO.getEmpCompanyId())
                .likeIfPresent(EmployeeRegularBillDO::getEmpCompanyName, reqVO.getEmpCompanyName())
                .eqIfPresent(EmployeeRegularBillDO::getEmployeeStatus, reqVO.getEmployeeStatus())
                .eqIfPresent(EmployeeRegularBillDO::getCreator, reqVO.getCreator())
                .betweenIfPresent(EmployeeRegularBillDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(EmployeeRegularBillDO::getId));
    }

}

