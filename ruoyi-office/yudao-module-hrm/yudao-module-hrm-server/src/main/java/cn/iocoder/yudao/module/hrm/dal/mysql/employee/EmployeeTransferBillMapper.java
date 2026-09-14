package cn.iocoder.yudao.module.hrm.dal.mysql.employee;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.hrm.dal.dataobject.employee.EmployeeTransferBillDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.hrm.controller.admin.employee.vo.EmployeeTransferBillPageReqVO;

/**
 * 人事调动申请单 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface EmployeeTransferBillMapper extends BaseMapperX<EmployeeTransferBillDO> {

    default PageResult<EmployeeTransferBillDO> selectPage(EmployeeTransferBillPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<EmployeeTransferBillDO>()
                .likeIfPresent(EmployeeTransferBillDO::getBillCode, reqVO.getBillCode())
                .eqIfPresent(EmployeeTransferBillDO::getProcessStatus, reqVO.getProcessStatus())
                .likeIfPresent(EmployeeTransferBillDO::getEmployeeNo, reqVO.getEmployeeNo())
                .likeIfPresent(EmployeeTransferBillDO::getName, reqVO.getName())
                .eqIfPresent(EmployeeTransferBillDO::getEmpDeptId, reqVO.getEmpDeptId())
                .likeIfPresent(EmployeeTransferBillDO::getEmpDeptName, reqVO.getEmpDeptName())
                .eqIfPresent(EmployeeTransferBillDO::getEmpCompanyId, reqVO.getEmpCompanyId())
                .likeIfPresent(EmployeeTransferBillDO::getEmpCompanyName, reqVO.getEmpCompanyName())
                .eqIfPresent(EmployeeTransferBillDO::getEmployeeStatus, reqVO.getEmployeeStatus())
                .eqIfPresent(EmployeeTransferBillDO::getTransferType, reqVO.getTransferType())
                .eqIfPresent(EmployeeTransferBillDO::getCreator, reqVO.getCreator())
                .betweenIfPresent(EmployeeTransferBillDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(EmployeeTransferBillDO::getId));
    }

}




