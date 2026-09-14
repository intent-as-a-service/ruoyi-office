package cn.iocoder.yudao.module.intent.framework.bridge;

import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import dev.intent.sdk.tool.IntentToolContext;
import dev.intent.sdk.tool.IntentTool;
import dev.intent.sdk.tool.IntentToolResult;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * 宿主上下文桥接器。
 *
 * <p>pi-agent 的 AgentLoop 通过 ForkJoinPool 执行工具调用（独立线程），
 * 而宿主系统的数据权限 / 租户隔离 / 登录用户解析依赖线程上下文
 * （Spring Security、TenantContextHolder、RequestContextHolder）。
 * 本装饰器在意图执行入口捕获请求线程上下文，并在每个工具线程中恢复——
 * 保证"进程内直调，权限与事务沿用宿主"。</p>
 */
public final class HostContextBridge implements UnaryOperator<IntentTool> {

    private final SecurityContext securityContext;
    private final Long tenantId;
    private final RequestAttributes requestAttributes;

    private HostContextBridge(SecurityContext securityContext, Long tenantId, RequestAttributes requestAttributes) {
        this.securityContext = securityContext;
        this.tenantId = tenantId;
        this.requestAttributes = requestAttributes;
    }

    /** 在当前（HTTP 请求）线程调用，捕获宿主上下文并返回工具装饰器。 */
    public static UnaryOperator<IntentTool> capture() {
        return new HostContextBridge(SecurityContextHolder.getContext(),
                TenantContextHolder.getTenantId(), RequestContextHolder.getRequestAttributes());
    }

    @Override
    public IntentTool apply(IntentTool delegate) {
        return new IntentTool() {

            @Override
            public String name() {
                return delegate.name();
            }

            @Override
            public String description() {
                return delegate.description();
            }

            @Override
            public Map<String, Object> parameters() {
                return delegate.parameters();
            }

            @Override
            public IntentToolResult execute(String toolCallId, Map<String, Object> args, IntentToolContext context)
                    throws Exception {
                RequestAttributes previousRequest = RequestContextHolder.getRequestAttributes();
                SecurityContext previousSecurity = SecurityContextHolder.getContext();
                Long previousTenant = TenantContextHolder.getTenantId();
                try {
                    if (requestAttributes != null) {
                        RequestContextHolder.setRequestAttributes(requestAttributes);
                    }
                    SecurityContextHolder.setContext(securityContext);
                    if (tenantId != null) {
                        TenantContextHolder.setTenantId(tenantId);
                    }
                    return delegate.execute(toolCallId, args, context);
                } finally {
                    if (previousRequest != null) {
                        RequestContextHolder.setRequestAttributes(previousRequest);
                    } else {
                        RequestContextHolder.resetRequestAttributes();
                    }
                    SecurityContextHolder.setContext(previousSecurity);
                    if (previousTenant != null) {
                        TenantContextHolder.setTenantId(previousTenant);
                    } else {
                        TenantContextHolder.clear();
                    }
                }
            }
        };
    }
}
