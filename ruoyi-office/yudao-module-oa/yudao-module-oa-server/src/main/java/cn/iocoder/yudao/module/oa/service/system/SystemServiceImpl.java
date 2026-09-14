package cn.iocoder.yudao.module.oa.service.system;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * System 模块服务实现类
 * 统一封装对 System 模块的远程调用
 *
 * @author 宇擎源码
 */
@Service
@Slf4j
public class SystemServiceImpl implements SystemService {

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private DeptApi deptApi;

    @Override
    public AdminUserRespDTO getUser(Long userId) {
        if (userId == null) {
            return null;
        }
        try {
            CommonResult<AdminUserRespDTO> result = adminUserApi.getUser(userId);
            return result != null && result.isSuccess() ? result.getData() : null;
        } catch (Exception e) {
            log.error("获取用户信息失败, userId: {}", userId, e);
            return null;
        }
    }

    @Override
    public DeptRespDTO getDept(Long deptId) {
        if (deptId == null) {
            return null;
        }
        try {
            CommonResult<DeptRespDTO> result = deptApi.getDept(deptId);
            return result != null && result.isSuccess() ? result.getData() : null;
        } catch (Exception e) {
            log.error("获取部门信息失败, deptId: {}", deptId, e);
            return null;
        }
    }

    @Override
    public String getUserNickname(Long userId) {
        AdminUserRespDTO user = getUser(userId);
        return user != null ? user.getNickname() : null;
    }

    @Override
    public String getDeptName(Long deptId) {
        DeptRespDTO dept = getDept(deptId);
        return dept != null ? dept.getName() : null;
    }

    @Override
    public List<Long> getUserDeptIds(Long userId) {
        List<Long> deptIds = new ArrayList<>();
        AdminUserRespDTO user = getUser(userId);
        if (user != null && user.getDeptId() != null) {
            deptIds.add(user.getDeptId());
            // 这里可以扩展为获取用户所有相关部门（如果有层级关系）
            // 目前简化处理，只返回用户直接所属的部门
        }
        return deptIds;
    }

}
