package cn.iocoder.yudao.module.intent.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.intent.service.IntentService;
import dev.intent.protocol.CatalogResponse;
import dev.intent.protocol.ExecutionTraceRecord;
import dev.intent.protocol.IntentFeedback;
import dev.intent.protocol.IntentRequest;
import dev.intent.protocol.IntentResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
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
 */
@Tag(name = "意图中心")
@RestController
@RequestMapping("/intent")
public class IntentController {

    @Resource
    private IntentService intentService;

    @GetMapping("/catalog")
    @Operation(summary = "获得意图目录（按当前用户角色过滤，含网关装配状态）")
    public CommonResult<CatalogResponse> getCatalog(
            @RequestParam(value = "page", required = false) String page) {
        return success(intentService.getCatalog(SecurityFrameworkUtils.getLoginUserId(), page));
    }

    @PostMapping("/execute")
    @Operation(summary = "执行意图（缺参返回 NEED_INPUT 与参数 Schema，前端渲染补全表单）")
    public CommonResult<IntentResult> executeIntent(@Valid @RequestBody ExecuteReqVO reqVO) {
        IntentResult result = intentService.execute(reqVO.toIntentRequest(
                SecurityFrameworkUtils.getLoginUserId(),
                SecurityFrameworkUtils.getLoginUserNickname()));
        return success(result);
    }

    @GetMapping("/history")
    @Operation(summary = "当前用户的意图执行历史")
    public CommonResult<List<ExecutionTraceRecord>> getHistory(
            @RequestParam(value = "intentId", required = false) String intentId,
            @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        return success(intentService.getHistory(SecurityFrameworkUtils.getLoginUserId(), intentId, limit));
    }

    @GetMapping("/trace/{traceId}")
    @Operation(summary = "执行留痕详情（步骤级转录，可回放）")
    public CommonResult<ExecutionTraceRecord> getTrace(@PathVariable("traceId") String traceId) {
        return success(intentService.getTrace(traceId));
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
