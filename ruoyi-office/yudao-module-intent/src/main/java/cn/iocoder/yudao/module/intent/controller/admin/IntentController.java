package cn.iocoder.yudao.module.intent.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.intent.service.IntentService;
import cn.iocoder.yudao.module.intent.service.workbench.WorkbenchService;
import dev.intent.protocol.CatalogResponse;
import dev.intent.protocol.ExecutionTraceRecord;
import dev.intent.protocol.IntentFeedback;
import dev.intent.protocol.IntentRequest;
import dev.intent.protocol.IntentResult;
import dev.intent.protocol.ResolvedSlot;
import dev.intent.protocol.SlotRef;
import dev.intent.protocol.SlotView;
import dev.intent.sdk.host.IntentSlotCandidateProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 意图 API（宿主接入后的统一入口）。
 *
 * <p>认证与租户隔离沿用宿主系统（yudao security / tenant）——
 * 前端组件与业务页面同源同鉴权，无需额外账号体系。</p>
 *
 * <p>注：包名含 controller.admin，yudao 框架自动补充 /admin-api 前缀。</p>
 *
 * <h3>意图工作台用到的端点</h3>
 * <pre>
 *   GET  /intent/me                  我该看到的工作台长什么样（角色 / 档案 / 视角 / 槽位类型）
 *   POST /intent/catalog             目录（带上下文栈与视角；槽位只影响建议，不影响意图集合）
 *   GET  /intent/catalog             目录（兼容：只按页面）
 *   POST /intent/execute             执行意图
 *   GET  /intent/runs                我的 Run 列表（签名里没有 userId）
 *   GET  /intent/runs/{traceId}      我的某条 Run（越权与不存在都 404）
 *   POST /intent/slots/resolve       批量校验槽位（实体还在不在 / 我能看吗）
 *   GET  /intent/slots/candidates    「＋ 加实体」的候选下拉
 *   POST /intent/feedback            结果评价
 * </pre>
 */
@Tag(name = "意图中心")
@RestController
@RequestMapping("/intent")
public class IntentController {

    @Resource
    private IntentService intentService;
    @Resource
    private WorkbenchService workbenchService;

    // ------------------------------------------------------------ 工作台

    @GetMapping("/me")
    @Operation(summary = "当前用户的工作台装配（角色 / 档案 / 视角 / 槽位类型 / 预设）")
    public CommonResult<Map<String, Object>> getMe() {
        return success(workbenchService.describe(SecurityFrameworkUtils.getLoginUserId(),
                SecurityFrameworkUtils.getLoginUserNickname()));
    }

    @GetMapping("/catalog")
    @Operation(summary = "获得意图目录（按当前用户角色过滤，含网关装配状态与增强降级记录）")
    public CommonResult<CatalogResponse> getCatalog(
            @RequestParam(value = "page", required = false) String page) {
        return success(intentService.getCatalog(SecurityFrameworkUtils.getLoginUserId(), page));
    }

    @PostMapping("/catalog")
    @Operation(summary = "获得意图目录（工作台版：带上下文栈与视角）")
    public CommonResult<CatalogResponse> queryCatalog(@RequestBody CatalogReqVO reqVO) {
        return success(intentService.getCatalog(SecurityFrameworkUtils.getLoginUserId(), reqVO.toRequest()));
    }

    @PostMapping("/execute")
    @Operation(summary = "执行意图（缺参返回 NEED_INPUT 与参数 Schema，前端渲染补全表单）")
    public CommonResult<IntentResult> executeIntent(@Valid @RequestBody ExecuteReqVO reqVO) {
        IntentResult result = intentService.execute(reqVO.toIntentRequest(
                SecurityFrameworkUtils.getLoginUserId(),
                SecurityFrameworkUtils.getLoginUserNickname()));
        return success(result);
    }

    @GetMapping("/runs")
    @Operation(summary = "我的执行留痕列表")
    public CommonResult<List<ExecutionTraceRecord>> getRuns(
            @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        // 身份只从登录态来，且没有 userId 入参可传——见 IntentService#listRuns 的说明
        return success(intentService.listRuns(SecurityFrameworkUtils.getLoginUserId(), limit));
    }

    @GetMapping("/runs/{traceId}")
    @Operation(summary = "我的某条执行留痕（不属于我 / 不存在，都返回 404）")
    public ResponseEntity<CommonResult<ExecutionTraceRecord>> getRun(
            @PathVariable("traceId") String traceId) {
        return intentService.findRun(SecurityFrameworkUtils.getLoginUserId(), traceId)
                .map(record -> ResponseEntity.ok(success(record)))
                // 404 而不是 403：403 会泄露"这条 traceId 存在但不属于你"
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(CommonResult.error(404, "执行记录不存在")));
    }

    @GetMapping("/history")
    @Operation(summary = "当前用户的意图执行历史（兼容入口，口径同 /runs）")
    public CommonResult<List<ExecutionTraceRecord>> getHistory(
            @RequestParam(value = "intentId", required = false) String intentId,
            @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        return success(intentService.getHistory(SecurityFrameworkUtils.getLoginUserId(), intentId, limit));
    }

    @PostMapping("/slots/resolve")
    @Operation(summary = "批量解析上下文槽位（实体是否还在 / 当前用户是否可见）")
    public CommonResult<Map<String, ResolvedSlot>> resolveSlots(@RequestBody List<SlotRef> refs) {
        return success(intentService.resolveSlots(SecurityFrameworkUtils.getLoginUserId(), refs));
    }

    /**
     * 槽位候选实体（工作台「＋ 加实体」的下拉）。
     *
     * <p><b>两种返回形状，靠 {@code shape=page} 切换</b>（意图工作台四点优化设计 §4.7）：</p>
     * <ul>
     *   <li>不带 {@code shape}（老前端）→ 老形状：直接返回数组，行为与以前完全一致；</li>
     *   <li>{@code shape=page}（新前端）→ {@code {items, nextCursor, total}}，可以"加载更多"。</li>
     * </ul>
     * <p>为什么要这么做而不是直接改形状：直接改会把"新宿主 + 老前端"打挂（老前端对返回做
     * {@code .map()}，拿到对象就炸）。加一个开关，两端都能平滑升级——**旧的一侧永远不用改**。</p>
     */
    @GetMapping("/slots/candidates")
    @Operation(summary = "槽位候选实体（工作台「＋ 加实体」下拉）")
    public CommonResult<?> getSlotCandidates(
            @RequestParam("type") String type,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "shape", required = false) String shape) {
        if (!"page".equals(shape)) {
            return success(workbenchService.candidates(type, keyword, limit));
        }
        return success(workbenchService.candidatesPage(type, keyword, cursor, limit));
    }

    @PostMapping("/feedback")
    @Operation(summary = "结果评价反馈（回流评测库）")
    public CommonResult<Boolean> feedback(@Valid @RequestBody IntentFeedback feedback) {
        intentService.saveFeedback(feedback, SecurityFrameworkUtils.getLoginUserId(),
                SecurityFrameworkUtils.getLoginUserNickname());
        return success(true);
    }

    @GetMapping("/status")
    @Operation(summary = "平台状态（系统名 / 网关装配状态）")
    public CommonResult<Map<String, Object>> getStatus() {
        return success(intentService.getStatus());
    }

    /** 目录请求 VO（工作台）。 */
    public static final class CatalogReqVO {

        private String page;
        /** 上下文栈：工作台左侧摆着哪几个实体，这里就有几个槽位。 */
        private List<SlotView> slots;
        /** 视角 id（self / lead / ...）；只能收窄本人角色。 */
        private String view;
        /** 焦点对象（前端当前打开的业务单据），与槽位无关，供增强器参考。 */
        private String objectType;
        private String objectId;
        private String objectName;

        public IntentService.CatalogRequest toRequest() {
            return new IntentService.CatalogRequest(page, slots, view, objectType, objectId, objectName);
        }

        public String getPage() { return page; }
        public void setPage(String page) { this.page = page; }
        public List<SlotView> getSlots() { return slots; }
        public void setSlots(List<SlotView> slots) { this.slots = slots; }
        public String getView() { return view; }
        public void setView(String view) { this.view = view; }
        public String getObjectType() { return objectType; }
        public void setObjectType(String objectType) { this.objectType = objectType; }
        public String getObjectId() { return objectId; }
        public void setObjectId(String objectId) { this.objectId = objectId; }
        public String getObjectName() { return objectName; }
        public void setObjectName(String objectName) { this.objectName = objectName; }
    }

    /** 执行请求 VO。 */
    public static final class ExecuteReqVO {

        private String intentId;
        private Map<String, Object> params;
        private Map<String, Object> context;

        public IntentRequest toIntentRequest(Long userId, String nickname) {
            return new IntentRequest(intentId, params, context,
                    new dev.intent.protocol.UserInfo(userId == null ? null : String.valueOf(userId),
                            nickname, null, Map.of()),
                    null);
        }

        public String getIntentId() { return intentId; }
        public void setIntentId(String intentId) { this.intentId = intentId; }
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
        public Map<String, Object> getContext() { return context; }
        public void setContext(Map<String, Object> context) { this.context = context; }
    }
}