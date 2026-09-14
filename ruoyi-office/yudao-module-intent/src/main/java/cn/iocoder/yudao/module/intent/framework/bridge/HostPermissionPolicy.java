package cn.iocoder.yudao.module.intent.framework.bridge;

import cn.iocoder.yudao.framework.common.biz.system.permission.PermissionCommonApi;
import dev.intent.sdk.host.IntentPermissionPolicy;
import dev.intent.sdk.host.IntentPrincipal;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 权限策略（ruoyi-office 实现）：角色判定走宿主统一的权限接口，
 * 因此数据权限、角色继承、租户隔离的口径与系统其它模块完全一致。
 *
 * <p>SDK 侧只依赖 {@link IntentPermissionPolicy} 这一个方法，
 * 换一套权限体系（Sa-Token / 自研）不影响意图能力的任何其它部分。</p>
 */
@Component
public class HostPermissionPolicy implements IntentPermissionPolicy {

    @Resource
    private PermissionCommonApi permissionCommonApi;

    @Override
    public boolean canUse(IntentPrincipal principal, List<String> requiredRoles) {
        if (requiredRoles == null || requiredRoles.isEmpty() || requiredRoles.contains("*")) {
            return true;
        }
        Long userId = principal == null ? null : principal.userIdAsLong();
        if (userId == null) {
            return false;
        }
        Boolean allowed = permissionCommonApi.hasAnyRoles(userId, requiredRoles.toArray(new String[0]))
                .getCheckedData();
        return Boolean.TRUE.equals(allowed);
    }
}