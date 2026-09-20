package cn.iocoder.yudao.module.intent.service;

import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.module.intent.controller.admin.IntentController;
import cn.iocoder.yudao.module.intent.dal.mysql.IntentConfigMapper;
import cn.iocoder.yudao.module.intent.framework.config.IntentProperties;
import cn.iocoder.yudao.module.intent.service.executor.ExecutorProfileRegistry;
import cn.iocoder.yudao.module.intent.service.spec.IntentSpecRegistry;
import cn.iocoder.yudao.module.intent.service.workbench.WorkbenchService;
import dev.intent.protocol.IntentSuggestion;
import dev.intent.sdk.catalog.IntentCatalogEnricher;
import dev.intent.sdk.catalog.IntentCatalogQuery;
import dev.intent.sdk.host.IntentContextBridge;
import dev.intent.sdk.host.IntentExecutorTypeResolver;
import dev.intent.sdk.host.IntentPermissionPolicy;
import dev.intent.sdk.host.IntentSlotResolver;
import dev.intent.sdk.pi.IntentRuntime;
import dev.intent.sdk.spec.IntentSpecLoader;
import dev.intent.sdk.store.TraceStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 目录契约测试：<b>用真的 IntentService</b>（只把仓储 / 权限 / 运行时换成 mock），
 * 断言三件"看起来做了、其实最容易没做"的事。
 *
 * <table>
 *   <tr><th>断言</th><th>没做到会怎样</th></tr>
 *   <tr><td>传 slots 只改 suggestions，<b>不改 entries</b></td>
 *       <td>用户每动一下上下文，意图菜单整片重排——工作台最刺眼的抖动</td></tr>
 *   <tr><td>增强器抛错 → issues 非空、HTTP 仍 200、菜单不缩水</td>
 *       <td>一条规则取数挂了，整个目录 500；或空态撒谎说"今天没有待办"</td></tr>
 *   <tr><td>slots 变化不使意图集合缓存失效（双键）</td>
 *       <td>槽位并进 entriesKey，等于每动一次上下文就废掉一次全量扫描</td></tr>
 * </table>
 *
 * <p>用真 IntentService 而不是 mock 它，是因为这三条全是<b>逻辑</b>性质：
 * mock 掉之后测的就只是 mock 的返回值，等于没测。</p>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CatalogContractTest {

    private static final Long ME = 150L;

    @Mock
    private IntentSpecRegistry specRegistry;
    @Mock
    private IntentConfigMapper intentConfigMapper;
    @Mock
    private IntentPermissionPolicy permissionPolicy;
    @Mock
    private TraceStore traceStore;
    @Mock
    private IntentContextBridge contextBridge;
    @Mock
    private IntentRuntime intentRuntime;
    @Mock
    private ExecutorProfileRegistry executorProfileRegistry;
    @Mock
    private WorkbenchService workbenchService;

    private final IntentProperties properties = new IntentProperties();
    private final List<IntentCatalogEnricher> enrichers = new ArrayList<>();

    private IntentService intentService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        properties.setSystemName("ruoyi-office");
        intentService = new IntentService();
        ReflectionTestUtils.setField(intentService, "specRegistry", specRegistry);
        ReflectionTestUtils.setField(intentService, "executorProfileRegistry", executorProfileRegistry);
        ReflectionTestUtils.setField(intentService, "intentRuntime", intentRuntime);
        ReflectionTestUtils.setField(intentService, "traceStore", traceStore);
        ReflectionTestUtils.setField(intentService, "properties", properties);
        ReflectionTestUtils.setField(intentService, "intentConfigMapper", intentConfigMapper);
        ReflectionTestUtils.setField(intentService, "permissionPolicy", permissionPolicy);
        ReflectionTestUtils.setField(intentService, "contextBridge", contextBridge);
        ReflectionTestUtils.setField(intentService, "workbenchService", workbenchService);
        ReflectionTestUtils.setField(intentService, "executorTypeResolver", IntentExecutorTypeResolver.none());
        ReflectionTestUtils.setField(intentService, "slotResolver", IntentSlotResolver.none());
        ReflectionTestUtils.setField(intentService, "catalogEnrichers", enrichers);

        when(specRegistry.getAll()).thenReturn(List.of(customerSpec(), contractSpec()));
        when(intentConfigMapper.selectByIntentId(anyString())).thenReturn(null);
        when(permissionPolicy.canUse(any(), anyList())).thenReturn(true);
        when(workbenchService.scope(any(), any())).thenReturn(
                new WorkbenchService.UserScope(Set.of("crm_admin"), null, Set.of("crm_admin"), false));

        IntentController controller = new IntentController();
        ReflectionTestUtils.setField(controller, "intentService", intentService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        login();
    }

    @Test
    @DisplayName("上下文栈只改建议：加了客户槽位，意图集合一字不动")
    void slotsOnlyAffectSuggestions() {
        enrichers.add(new CustomerSignalEnricher());

        var withoutSlots = intentService.getCatalog(ME, "crm/customer/detail/320");
        var withSlots = intentService.getCatalog(ME, new IntentService.CatalogRequest(
                "crm/customer/detail/320",
                List.of(new dev.intent.protocol.SlotView("customer", "customerId", "宏图建筑设计院",
                        List.of("320"), "SINGLE", "PINNED")),
                null, null, null, null));

        assertThat(withSlots.entries()).isEqualTo(withoutSlots.entries());
        assertThat(withoutSlots.suggestions()).isEmpty();
        assertThat(withSlots.suggestions()).hasSize(1);
        assertThat(withSlots.suggestions().get(0).params()).containsEntry("customerId", "320");
    }

    @Test
    @DisplayName("槽位变化不使意图集合缓存失效：两次不同槽位只扫描一次规范表")
    void slotsDoNotEvictEntriesCache() {
        enrichers.add(new CustomerSignalEnricher());

        intentService.getCatalog(ME, new IntentService.CatalogRequest("crm", List.of(slot("320")), null, null, null, null));
        intentService.getCatalog(ME, new IntentService.CatalogRequest("crm", List.of(slot("321")), null, null, null, null));

        verify(specRegistry, times(1)).getAll();
    }

    @Test
    @DisplayName("增强器抛错：issues 非空、菜单不缩水、HTTP 仍是 200 —— 且 issues 必须原样出现在响应体里")
    void enricherFailureDegradesWithoutBreakingCatalog() throws Exception {
        enrichers.add(new ExplodingEnricher());

        var response = intentService.getCatalog(ME, "crm");
        assertThat(response.issues()).hasSize(1);
        assertThat(response.degraded()).isTrue();
        assertThat(response.entries()).hasSize(2); // 菜单一条不少

        mockMvc.perform(post("/intent/catalog")
                        .contentType("application/json")
                        .content("{\"page\":\"crm\",\"slots\":[]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.entries.length()").value(2))
                // 这一条是"宿主有没有把 issues 抹掉"的唯一判据
                .andExpect(jsonPath("$.data.issues[0].source").exists());
    }

    private static dev.intent.protocol.SlotView slot(String customerId) {
        return new dev.intent.protocol.SlotView("customer", "customerId", null,
                List.of(customerId), "SINGLE", "PINNED");
    }

    private static void login() {
        LoginUser loginUser = new LoginUser();
        loginUser.setId(ME);
        loginUser.setUserType(1);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null, List.of()));
    }

    // ------------------------------------------------------------ fixtures

    private static dev.intent.protocol.IntentSpec customerSpec() {
        return IntentSpecLoader.parse("""
                id: crm.customer.health-check
                name: 客户健康度诊断
                description: 给客户打分
                version: 1
                scope: local
                pages: [crm, crm/customer, crm/customer/*]
                cardType: analysis
                promptTemplate: 给这个客户按八个维度打分。
                paramsSchema:
                  type: object
                  properties:
                    customerId:
                      type: string
                  required: [customerId]
                context:
                  - key: customerId
                    title: 当前客户
                    required: true
                policy:
                  roles: ["*"]
                  timeoutSeconds: 60
                """, true);
    }

    private static dev.intent.protocol.IntentSpec contractSpec() {
        return IntentSpecLoader.parse("""
                id: crm.contract.renewal-plan
                name: 续约策略
                description: 生成续约打法
                version: 2
                scope: local
                pages: [crm]
                cardType: analysis
                promptTemplate: 生成续约打法。
                paramsSchema:
                  type: object
                  properties:
                    contractId:
                      type: string
                  required: [contractId]
                context:
                  - key: contractId
                    title: 当前合同
                    required: true
                policy:
                  roles: ["*"]
                  timeoutSeconds: 60
                """, true);
    }

    /** 事实源：摆了客户槽位就推一条待办，否则什么都不推。 */
    private static final class CustomerSignalEnricher implements IntentCatalogEnricher {

        @Override
        public List<IntentSuggestion> suggestions(IntentCatalogQuery query) {
            String customerId = query.slotValue("customer");
            if (customerId == null) {
                return List.of();
            }
            return List.of(IntentSuggestion.item("sug-" + customerId, "crm.customer.health-check",
                    "给「宏图建筑设计院」做一次健康度诊断", "12 天未联系",
                    java.util.Map.of("customerId", customerId), "摆着的客户 12 天未联系"));
        }
    }

    /** 坏掉的事实源：用来验证"单条规则挂了不该把目录一起带走"。 */
    private static final class ExplodingEnricher implements IntentCatalogEnricher {

        @Override
        public List<IntentSuggestion> suggestions(IntentCatalogQuery query) {
            throw new IllegalStateException("CRM 取数超时");
        }
    }
}
