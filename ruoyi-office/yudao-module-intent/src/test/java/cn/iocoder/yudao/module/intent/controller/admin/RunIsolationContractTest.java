package cn.iocoder.yudao.module.intent.controller.admin;

import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.module.intent.service.IntentService;
import cn.iocoder.yudao.module.intent.service.workbench.WorkbenchService;
import dev.intent.protocol.ExecutionTraceRecord;
import dev.intent.protocol.IntentStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Run 归属隔离的 HTTP 契约测试。
 *
 * <p>为什么这一条必须在宿主仓库测：越权防线落在<b>端点</b>上（SDK 只能把
 * "不属于你 / 不存在 / 入参空"合并成同一个 {@code Optional.empty()}），
 * 而端点在本仓库之外。SDK 侧的 {@code TraceIsolationTest} 覆盖不到 HTTP 状态码。</p>
 *
 * <p>断言口径（任一条被改坏都意味着越权或信息泄露）：</p>
 * <ol>
 *   <li>以 A 的身份请求 B 的 traceId → <b>404</b>，不是 403、不是 200；
 *       403 会泄露"这条 traceId 存在但不属于你"；</li>
 *   <li>请求里塞 {@code userId=B} 没有任何用——控制器签名里根本没有这个入参；</li>
 *   <li>{@code /intent/runs} 永远只查登录态里的那个人。</li>
 * </ol>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RunIsolationContractTest {

    private static final Long ME = 1L;
    private static final Long OTHER = 2L;

    @Mock
    private IntentService intentService;
    @Mock
    private WorkbenchService workbenchService;
    @InjectMocks
    private IntentController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        login(ME);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("以 A 的身份请求 B 的 traceId：404（不是 403，也不是 200）")
    void otherUsersTraceIsNotFound() throws Exception {
        when(intentService.findRun(any(), anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/intent/runs/trc-belongs-to-B").param("userId", String.valueOf(OTHER)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data").doesNotExist());

        // 身份只来自登录态；请求里那个 userId 连"被读到"都没机会
        verify(intentService).findRun(ME, "trc-belongs-to-B");
        verify(intentService, never()).findRun(eq(OTHER), anyString());
    }

    @Test
    @DisplayName("请求自己的 traceId：200，且返回的就是那条记录")
    void ownTraceIsReturned() throws Exception {
        when(intentService.findRun(any(), anyString())).thenReturn(Optional.of(record("trc-mine", ME)));

        mockMvc.perform(get("/intent/runs/trc-mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.traceId").value("trc-mine"));

        verify(intentService).findRun(ME, "trc-mine");
    }

    @Test
    @DisplayName("/intent/runs 只查登录态里的人，userId 入参不存在")
    void runsAlwaysScopeToLoginUser() throws Exception {
        when(intentService.listRuns(any(), anyInt())).thenReturn(List.of(record("trc-mine", ME)));

        mockMvc.perform(get("/intent/runs").param("limit", "5").param("userId", String.valueOf(OTHER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));

        verify(intentService).listRuns(ME, 5);
        verify(intentService, never()).listRuns(eq(OTHER), anyInt());
    }

    private static void login(Long userId) {
        LoginUser loginUser = new LoginUser();
        loginUser.setId(userId);
        loginUser.setUserType(1);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null, List.of()));
    }

    private static ExecutionTraceRecord record(String traceId, Long userId) {
        return new ExecutionTraceRecord(traceId, "crm.customer.health-check", "客户健康度诊断",
                String.valueOf(userId), "销售小王", System.currentTimeMillis(), 1234L,
                IntentStatus.SUCCESS, Map.of(), Map.of(), null, List.of());
    }
}