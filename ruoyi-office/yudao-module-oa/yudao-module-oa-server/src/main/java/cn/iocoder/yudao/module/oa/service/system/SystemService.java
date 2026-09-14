package cn.iocoder.yudao.module.oa.service.system;

import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;

/**
 * System 模块服务接口
 * 统一封装对 System 模块的远程调用
 *
 * @author 宇擎源码
 */
public interface SystemService {

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    AdminUserRespDTO getUser(Long userId);

    /**
     * 获取部门信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    DeptRespDTO getDept(Long deptId);

    /**
     * 获取用户昵称
     *
     * @param userId 用户ID
     * @return 用户昵称
     */
    String getUserNickname(Long userId);

    /**
     * 获取部门名称
     *
     * @param deptId 部门ID
     * @return 部门名称
     */
    String getDeptName(Long deptId);

    /**
     * 获取用户所属部门ID列表
     *
     * @param userId 用户ID
     * @return 部门ID列表
     */
    java.util.List<Long> getUserDeptIds(Long userId);

}
