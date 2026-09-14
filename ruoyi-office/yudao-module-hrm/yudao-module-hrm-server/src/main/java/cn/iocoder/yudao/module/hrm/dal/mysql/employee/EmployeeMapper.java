package cn.iocoder.yudao.module.hrm.dal.mysql.employee;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hrm.controller.admin.employee.vo.EmployeePageReqVO;
import cn.iocoder.yudao.module.hrm.controller.admin.employee.vo.EmployeeSelectPageReqVO;
import cn.iocoder.yudao.module.hrm.dal.dataobject.employee.EmployeeDO;
import cn.iocoder.yudao.module.hrm.enums.EmployeeStatusEnum;
import org.apache.ibatis.annotations.Mapper;

/**
 * 员工档案 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface EmployeeMapper extends BaseMapperX<EmployeeDO> {

    default PageResult<EmployeeDO> selectPage(EmployeePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<EmployeeDO>()
                .likeIfPresent(EmployeeDO::getEmployeeNo, reqVO.getEmployeeNo())
                .likeIfPresent(EmployeeDO::getName, reqVO.getName())
                .eqIfPresent(EmployeeDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(EmployeeDO::getEmployeeStatus, reqVO.getEmployeeStatus())
                .betweenIfPresent(EmployeeDO::getEntryDate, reqVO.getEntryDate())
                .betweenIfPresent(EmployeeDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(EmployeeDO::getId));
    }

    /**
     * 选择弹窗分页（支持包含和排除人员状态列表，默认排除离职和退休）
     * 
     * 逻辑说明：
     * 1. 如果指定了 includeEmployeeStatusList，则只包含这些状态（优先级更高）
     * 2. 如果指定了 excludeEmployeeStatusList，则排除这些状态
     * 3. 默认排除离职（6）和退休（7）
     * 4. 如果同时指定了包含和排除，包含优先级更高
     */
    default PageResult<EmployeeDO> selectPageExcludeFormal(EmployeeSelectPageReqVO reqVO) {
        LambdaQueryWrapperX<EmployeeDO> wrapper = new LambdaQueryWrapperX<EmployeeDO>()
                .likeIfPresent(EmployeeDO::getEmployeeNo, reqVO.getEmployeeNo())
                .likeIfPresent(EmployeeDO::getName, reqVO.getName())
                .eqIfPresent(EmployeeDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(EmployeeDO::getJobPost, reqVO.getJobPost())
                .eqIfPresent(EmployeeDO::getJobPosition, reqVO.getJobPosition())
                .eqIfPresent(EmployeeDO::getEmployeeStatus, reqVO.getEmployeeStatus())
                .betweenIfPresent(EmployeeDO::getEntryDate, reqVO.getEntryDate())
                .betweenIfPresent(EmployeeDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(EmployeeDO::getId);
        
        // 处理人员状态过滤
        if (reqVO.getIncludeEmployeeStatusList() != null && !reqVO.getIncludeEmployeeStatusList().isEmpty()) {
            // 如果指定了包含状态列表，则只包含这些状态（优先级更高）
            wrapper.in(EmployeeDO::getEmployeeStatus, reqVO.getIncludeEmployeeStatusList());
        } else {
            // 如果没有指定包含列表，则使用排除列表
            java.util.List<Integer> excludeList = new java.util.ArrayList<>();
            
            // 添加用户指定的排除列表
            if (reqVO.getExcludeEmployeeStatusList() != null && !reqVO.getExcludeEmployeeStatusList().isEmpty()) {
                excludeList.addAll(reqVO.getExcludeEmployeeStatusList());
            }
            
            // 默认排除离职和退休
            if (!excludeList.contains(EmployeeStatusEnum.RESIGNED.getStatus())) {
                excludeList.add(EmployeeStatusEnum.RESIGNED.getStatus());
            }
            if (!excludeList.contains(EmployeeStatusEnum.RETIRED.getStatus())) {
                excludeList.add(EmployeeStatusEnum.RETIRED.getStatus());
            }
            
            // 如果有排除列表，则应用排除条件
            if (!excludeList.isEmpty()) {
                wrapper.notIn(EmployeeDO::getEmployeeStatus, excludeList);
            }
        }
        
        return selectPage(reqVO, wrapper);
    }

    /**
     * 获取最大的员工工号（数字部分）
     * 用于自动生成员工工号
     *
     * @return 最大员工工号的数字部分，如果没有记录则返回 9999999（10000000 - 1）
     */
    default Long selectMaxEmployeeNo() {
        EmployeeDO maxEmployee = selectOne(new LambdaQueryWrapperX<EmployeeDO>()
                .select(EmployeeDO::getEmployeeNo)
                .orderByDesc(EmployeeDO::getEmployeeNo)
                .last("LIMIT 1"));
        if (maxEmployee == null || maxEmployee.getEmployeeNo() == null) {
            return 9999999L; // 返回 10000000 - 1，这样下一个就是 10000000
        }
        try {
            Long employeeNo = Long.parseLong(maxEmployee.getEmployeeNo());
            return employeeNo >= 10000000 ? employeeNo : 9999999L;
        } catch (NumberFormatException e) {
            // 如果员工工号不是纯数字，返回默认值
            return 9999999L;
        }
    }

    /**
     * 根据用户ID查询员工
     *
     * @param userId 用户ID
     * @return 员工信息
     */
    default EmployeeDO selectByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapperX<EmployeeDO>()
                .eq(EmployeeDO::getUserId, userId));
    }

}

