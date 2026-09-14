package cn.iocoder.yudao.module.hrm.service.employee;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hrm.controller.admin.employee.vo.*;
import cn.iocoder.yudao.module.hrm.dal.dataobject.employee.*;
import cn.iocoder.yudao.module.hrm.dal.mysql.employee.*;
import cn.iocoder.yudao.module.infra.api.config.ConfigApi;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserCreateReqDTO;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserUpdateReqDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hrm.enums.ErrorCodeConstants.EMPLOYEE_ARCHIVE_NOT_EXISTS;

/**
 * 员工档案 Service 实现类
 *
 * @author 宇擎源码
 */
@Service
@Validated
public class EmployeeServiceImpl implements EmployeeService {

    @Resource
    private EmployeeMapper employeeArchiveMapper;

    @Resource
    private EmployeeWorkExperienceMapper employeeWorkExperienceMapper;

    @Resource
    private EmployeeEducationMapper employeeEducationMapper;

    @Resource
    private EmployeeFamilyMapper employeeFamilyMapper;

    @Resource
    private DeptApi deptApi;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private ConfigApi configApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEmployeeArchive(EmployeeSaveReqVO createReqVO) {
        // 自动生成员工工号（如果未提供）
        if (createReqVO.getEmployeeNo() == null || createReqVO.getEmployeeNo().trim().isEmpty()) {
            Long maxEmployeeNo = employeeArchiveMapper.selectMaxEmployeeNo();
            Long nextEmployeeNo = maxEmployeeNo + 1;

            createReqVO.setEmployeeNo(String.format("%08d", nextEmployeeNo));
        }
        
        // 插入主表
        EmployeeDO archive = BeanUtils.toBean(createReqVO, EmployeeDO.class);
        employeeArchiveMapper.insert(archive);

        // 插入工作经历
        saveWorkExperiences(archive.getId(), createReqVO.getWorkExperienceList());

        // 插入教育经历
        saveEducations(archive.getId(), createReqVO.getEducationList());

        // 插入家属信息
        saveFamilies(archive.getId(), createReqVO.getFamilyList());

        return archive.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmployeeArchive(EmployeeSaveReqVO updateReqVO) {
        // 校验存在
        EmployeeDO oldEmployee = employeeArchiveMapper.selectById(updateReqVO.getId());
        if (oldEmployee == null) {
            throw exception(EMPLOYEE_ARCHIVE_NOT_EXISTS);
        }

        // 更新主表
        EmployeeDO updateObj = BeanUtils.toBean(updateReqVO, EmployeeDO.class);
        employeeArchiveMapper.updateById(updateObj);

        // 删除旧的关联记录
        employeeWorkExperienceMapper.deleteByEmployeeId(updateReqVO.getId());
        employeeEducationMapper.deleteByEmployeeId(updateReqVO.getId());
        employeeFamilyMapper.deleteByEmployeeId(updateReqVO.getId());

        // 插入新的关联记录
        saveWorkExperiences(updateReqVO.getId(), updateReqVO.getWorkExperienceList());
        saveEducations(updateReqVO.getId(), updateReqVO.getEducationList());
        saveFamilies(updateReqVO.getId(), updateReqVO.getFamilyList());

        // 如果已生成用户，同步更新用户信息
        if (oldEmployee.getUserGenerated() != null && oldEmployee.getUserGenerated() && oldEmployee.getUserId() != null) {
            syncEmployeeToUser(updateReqVO, oldEmployee.getUserId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployeeArchive(Long id) {
        // 校验存在
        EmployeeDO employee = employeeArchiveMapper.selectById(id);
        if (employee == null) {
            throw exception(EMPLOYEE_ARCHIVE_NOT_EXISTS);
        }

        // 如果已生成用户，则删除关联的用户
        if (employee.getUserGenerated() != null && employee.getUserGenerated() && employee.getUserId() != null) {
            adminUserApi.deleteUser(employee.getUserId());
        }

        // 删除主表
        employeeArchiveMapper.deleteById(id);

        // 删除关联记录
        employeeWorkExperienceMapper.deleteByEmployeeId(id);
        employeeEducationMapper.deleteByEmployeeId(id);
        employeeFamilyMapper.deleteByEmployeeId(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployeeArchiveList(List<Long> ids) {
        // 校验存在
        validateEmployeeArchiveExists(ids);

        // 查询所有员工信息，收集需要删除的用户ID
        List<EmployeeDO> employees = employeeArchiveMapper.selectList(EmployeeDO::getId, ids);
        List<Long> userIdsToDelete = employees.stream()
                .filter(emp -> emp.getUserGenerated() != null && emp.getUserGenerated() && emp.getUserId() != null)
                .map(EmployeeDO::getUserId)
                .collect(Collectors.toList());

        // 如果已生成用户，则批量删除关联的用户
        if (!userIdsToDelete.isEmpty()) {
            adminUserApi.deleteUserList(userIdsToDelete);
        }

        // 删除主表
        employeeArchiveMapper.deleteBatchIds(ids);

        // 删除关联记录
        for (Long id : ids) {
            employeeWorkExperienceMapper.deleteByEmployeeId(id);
            employeeEducationMapper.deleteByEmployeeId(id);
            employeeFamilyMapper.deleteByEmployeeId(id);
        }
    }

    private void validateEmployeeArchiveExists(Long id) {
        if (employeeArchiveMapper.selectById(id) == null) {
            throw exception(EMPLOYEE_ARCHIVE_NOT_EXISTS);
        }
    }

    private void validateEmployeeArchiveExists(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<EmployeeDO> list = employeeArchiveMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(list) || list.size() != ids.size()) {
            throw exception(EMPLOYEE_ARCHIVE_NOT_EXISTS);
        }
    }

    @Override
    public EmployeeRespVO getEmployeeArchive(Long id) {
        EmployeeDO archive = employeeArchiveMapper.selectById(id);
        if (archive == null) {
            return null;
        }

        EmployeeRespVO respVO = BeanUtils.toBean(archive, EmployeeRespVO.class);

        // 获取部门名称（如果数据库中没有保存，则通过部门查找）
        if (archive.getDeptId() != null) {
            // 如果数据库中没有保存部门名称，则从部门信息中获取
            if (archive.getDeptName() == null) {
                CommonResult<DeptRespDTO> dept = deptApi.getDept(archive.getDeptId());
                if (dept != null && dept.isSuccess() && dept.getData() != null) {
                    respVO.setDeptName(dept.getData().getName());
                }
            }
            
            // 如果数据库中没有保存公司ID，则通过部门向上查找公司
            if (archive.getCompanyId() == null) {
                Long companyId = findCompanyIdByDeptId(archive.getDeptId());
                if (companyId != null) {
                    respVO.setCompanyId(companyId);
                }
            }
        }

        // 获取工作经历
        List<EmployeeWorkExperienceDO> workExperiences = employeeWorkExperienceMapper.selectListByEmployeeId(id);
        respVO.setWorkExperienceList(BeanUtils.toBean(workExperiences, EmployeeWorkExperienceVO.class));

        // 获取教育经历
        List<EmployeeEducationDO> educations = employeeEducationMapper.selectListByEmployeeId(id);
        respVO.setEducationList(BeanUtils.toBean(educations, EmployeeEducationVO.class));

        // 获取家属信息
        List<EmployeeFamilyDO> families = employeeFamilyMapper.selectListByEmployeeId(id);
        respVO.setFamilyList(BeanUtils.toBean(families, EmployeeFamilyVO.class));

        return respVO;
    }

    @Override
    public PageResult<EmployeeRespVO> getEmployeeArchivePage(EmployeePageReqVO pageReqVO) {
        PageResult<EmployeeDO> pageResult = employeeArchiveMapper.selectPage(pageReqVO);
        return buildEmployeeRespPage(pageResult);
    }

    @Override
    public PageResult<EmployeeRespVO> getEmployeeArchiveSelectablePage(EmployeeSelectPageReqVO pageReqVO) {
        PageResult<EmployeeDO> pageResult = employeeArchiveMapper.selectPageExcludeFormal(pageReqVO);
        return buildEmployeeRespPage(pageResult);
    }

    /**
     * 保存工作经历列表
     */
    private void saveWorkExperiences(Long employeeId, List<EmployeeWorkExperienceVO> workExperienceList) {
        if (CollUtil.isEmpty(workExperienceList)) {
            return;
        }
        List<EmployeeWorkExperienceDO> workExperiences = BeanUtils.toBean(workExperienceList, EmployeeWorkExperienceDO.class);
        workExperiences.forEach(item -> {
            item.setId(null);
            item.setEmployeeId(employeeId);
            employeeWorkExperienceMapper.insert(item);
        });
    }

    /**
     * 保存教育经历列表
     */
    private void saveEducations(Long employeeId, List<EmployeeEducationVO> educationList) {
        if (CollUtil.isEmpty(educationList)) {
            return;
        }
        List<EmployeeEducationDO> educations = BeanUtils.toBean(educationList, EmployeeEducationDO.class);
        educations.forEach(item -> {
            item.setId(null);
            item.setEmployeeId(employeeId);
            employeeEducationMapper.insert(item);
        });
    }

    /**
     * 保存家属信息列表
     */
    private void saveFamilies(Long employeeId, List<EmployeeFamilyVO> familyList) {
        if (CollUtil.isEmpty(familyList)) {
            return;
        }
        List<EmployeeFamilyDO> families = BeanUtils.toBean(familyList, EmployeeFamilyDO.class);
        families.forEach(item -> {
            item.setId(null);
            item.setEmployeeId(employeeId);
            employeeFamilyMapper.insert(item);
        });
    }

    /**
     * 构建带部门名称的分页结果
     */
    private PageResult<EmployeeRespVO> buildEmployeeRespPage(PageResult<EmployeeDO> pageResult) {
        PageResult<EmployeeRespVO> respPageResult = BeanUtils.toBean(pageResult, EmployeeRespVO.class);

        // 批量获取部门名称
        List<Long> deptIds = respPageResult.getList().stream()
                .map(EmployeeRespVO::getDeptId)
                .filter(deptId -> deptId != null)
                .distinct()
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(deptIds)) {
            Map<Long, DeptRespDTO> deptMap = deptApi.getDeptMap(deptIds);
            respPageResult.getList().forEach(respVO -> {
                if (respVO.getDeptId() != null && deptMap.containsKey(respVO.getDeptId())) {
                    respVO.setDeptName(deptMap.get(respVO.getDeptId()).getName());
                }
            });
        }

        return respPageResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generateUserForEmployee(Long employeeId) {
        // 1. 校验员工存在
        EmployeeDO employee = employeeArchiveMapper.selectById(employeeId);
        if (employee == null) {
            throw exception(EMPLOYEE_ARCHIVE_NOT_EXISTS);
        }

        // 2. 校验是否已生成用户
        if (employee.getUserGenerated() != null && employee.getUserGenerated() && employee.getUserId() != null) {
            throw new RuntimeException("该员工已生成用户，无需重复生成");
        }

        // 3. 创建用户
        AdminUserCreateReqDTO userCreateReqDTO = new AdminUserCreateReqDTO();
        userCreateReqDTO.setUsername(employee.getEmployeeNo()); // 用户名为员工工号
        userCreateReqDTO.setNickname(employee.getName()); // 用户昵称为员工姓名
        userCreateReqDTO.setMobile(employee.getMobile()); // 手机号
        userCreateReqDTO.setEmail(employee.getEmail()); // 邮箱
        userCreateReqDTO.setSex(employee.getSex()); // 性别
        userCreateReqDTO.setAvatar(employee.getAvatar()); // 头像
        userCreateReqDTO.setDeptId(employee.getDeptId()); // 部门ID
        userCreateReqDTO.setRemark(employee.getRemark()); // 备注

        // 获取初始密码配置
        String initPassword = "123456"; // 默认密码
        CommonResult<String> configResult = configApi.getConfigValueByKey("system.user.init-password");
        if (configResult != null && configResult.isSuccess() && configResult.getData() != null) {
            initPassword = configResult.getData();
        }
        userCreateReqDTO.setPassword(initPassword);

        Long userId = adminUserApi.createUser(userCreateReqDTO).getCheckedData();

        // 4. 更新员工关联信息
        EmployeeDO updateObj = new EmployeeDO();
        updateObj.setId(employeeId);
        updateObj.setUserId(userId);
        updateObj.setUserGenerated(true);
        employeeArchiveMapper.updateById(updateObj);

        return userId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchGenerateUserForEmployee(List<Long> employeeIds) {
        if (CollUtil.isEmpty(employeeIds)) {
            return;
        }

        for (Long employeeId : employeeIds) {
            try {
                generateUserForEmployee(employeeId);
            } catch (Exception e) {
                // 记录错误，继续处理下一个
                // 可以根据需要记录日志
            }
        }
    }

    /**
     * 同步员工信息到用户
     */
    private void syncEmployeeToUser(EmployeeSaveReqVO employee, Long userId) {
        AdminUserUpdateReqDTO userUpdateReqDTO = new AdminUserUpdateReqDTO();
        userUpdateReqDTO.setId(userId);
        userUpdateReqDTO.setUsername(employee.getEmployeeNo()); // 用户名为员工工号
        userUpdateReqDTO.setNickname(employee.getName()); // 用户昵称为员工姓名
        userUpdateReqDTO.setMobile(employee.getMobile()); // 手机号
        userUpdateReqDTO.setEmail(employee.getEmail()); // 邮箱
        userUpdateReqDTO.setSex(employee.getSex()); // 性别
        userUpdateReqDTO.setAvatar(employee.getAvatar()); // 头像
        userUpdateReqDTO.setDeptId(employee.getDeptId()); // 部门ID
        userUpdateReqDTO.setRemark(employee.getRemark()); // 备注

        adminUserApi.updateUser(userUpdateReqDTO);
    }

    /**
     * 通过部门ID查找公司ID
     * 从当前部门开始，向上查找第一个组织类型为1（公司）的部门
     *
     * @param deptId 部门ID
     * @return 公司ID，如果找不到则返回null
     */
    private Long findCompanyIdByDeptId(Long deptId) {
        if (deptId == null) {
            return null;
        }

        // 最多查找100层，避免死循环
        for (int i = 0; i < 100; i++) {
            CommonResult<DeptRespDTO> deptResult = deptApi.getDept(deptId);
            if (deptResult == null || !deptResult.isSuccess() || deptResult.getData() == null) {
                break;
            }

            DeptRespDTO dept = deptResult.getData();
            
            // 如果当前部门就是公司（orgType为"1"），返回其ID
            if ("1".equals(dept.getOrgType())) {
                return dept.getId();
            }

            // 如果父部门ID为空或为0，说明已经到根节点，停止查找
            if (dept.getParentId() == null || dept.getParentId() == 0) {
                break;
            }

            // 继续向上查找父部门
            deptId = dept.getParentId();
        }

        // 没有找到公司类型的部门
        return null;
    }

}

