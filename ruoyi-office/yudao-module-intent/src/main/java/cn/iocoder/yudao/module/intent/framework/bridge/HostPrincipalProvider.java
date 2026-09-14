package cn.iocoder.yudao.module.intent.framework.bridge;

import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import dev.intent.sdk.host.IntentPrincipal;
import dev.intent.sdk.host.IntentPrincipalProvider;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 身份提供者（ruoyi-office 实现）：从宿主登录态读取"当前是谁"。
 *
 * <p>角色不在本类内解析——ruoyi 的角色判定要经 {@code PermissionCommonApi} 查库，
 * 交给 {@link HostPermissionPolicy} 在真正需要时按需判定，避免每次目录查询都多查一次角色。</p>
 */
@Component
public class HostPrincipalProvider implements IntentPrincipalProvider {

    @Override
    public IntentPrincipal current() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null) {
            return IntentPrincipal.anonymous();
        }
        Long tenantId = TenantContextHolder.getTenantId();
        return IntentPrincipal.of(String.valueOf(userId), null,
                tenantId == null ? null : String.valueOf(tenantId), List.of());
    }
}