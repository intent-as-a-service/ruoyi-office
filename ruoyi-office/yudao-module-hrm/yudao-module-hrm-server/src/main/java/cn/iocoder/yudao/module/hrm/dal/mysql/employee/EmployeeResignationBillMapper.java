package cn.iocoder.yudao.module.hrm.dal.mysql.employee;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.hrm.dal.dataobject.employee.EmployeeResignationBillDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.hrm.controller.admin.employee.vo.EmployeeResignationBillPageReqVO;

/**
 * 员工离职申请单 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface EmployeeResignationBillMapper extends BaseMapperX<EmployeeResignationBillDO> {

    default PageResult<EmployeeResignationBillDO> selectPage(EmployeeResignationBillPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<EmployeeResignationBillDO>()
                .likeIfPresent(EmployeeResignationBillDO::getBillCode, reqVO.getBillCode())
                .eqIfPresent(EmployeeResignationBillDO::getProcessStatus, reqVO.getProcessStatus())
                .likeIfPresent(EmployeeResignationBillDO::getEmployeeNo, reqVO.getEmployeeNo())
                .likeIfPresent(EmployeeResignationBillDO::getName, reqVO.getName())
                .eqIfPresent(EmployeeResignationBillDO::getEmpDeptId, reqVO.getEmpDeptId())
                .likeIfPresent(EmployeeResignationBillDO::getEmpDeptName, reqVO.getEmpDeptName())
                .eqIfPresent(EmployeeResignationBillDO::getEmpCompanyId, reqVO.getEmpCompanyId())
                .likeIfPresent(EmployeeResignationBillDO::getEmpCompanyName, reqVO.getEmpCompanyName())
                .eqIfPresent(EmployeeResignationBillDO::getEmployeeStatus, reqVO.getEmployeeStatus())
                .eqIfPresent(EmployeeResignationBillDO::getResignationType, reqVO.getResignationType())
                .eqIfPresent(EmployeeResignationBillDO::getResignationReason, reqVO.getResignationReason())
                .eqIfPresent(EmployeeResignationBillDO::getCreator, reqVO.getCreator())
                .betweenIfPresent(EmployeeResignationBillDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(EmployeeResignationBillDO::getId));
    }

}

