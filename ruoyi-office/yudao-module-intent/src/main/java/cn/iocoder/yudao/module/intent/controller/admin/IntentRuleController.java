package cn.iocoder.yudao.module.intent.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.intent.dal.dataobject.IntentRuleDO;
import cn.iocoder.yudao.module.intent.service.rule.IntentRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 自动跟进规则 API：自然语言 → 规则 → 试算预览 → 启用。
 *
 * <p>管理端能力，权限沿用宿主（{@code intent:rule:*}）。</p>
 */
@Tag(name = "意图中心 · 自动跟进规则")
@RestController
@RequestMapping("/intent/rule")
public class IntentRuleController {

    @Resource
    private IntentRuleService ruleService;

    @GetMapping("/list")
    @Operation(summary = "规则列表（含草稿与已启用）")
    @PreAuthorize("@ss.hasPermission('intent:rule:query')")
    public CommonResult<List<IntentRuleDO>> getList() {
        return success(ruleService.getList());
    }

    @GetMapping("/dataset-options")
    @Operation(summary = "可用数据集（事实源自报，供生成规则时选择）")
    @PreAuthorize("@ss.hasPermission('intent:rule:query')")
    public CommonResult<List<Map<String, Object>>> getDatasetOptions() {
        return success(ruleService.getDatasetOptions());
    }

    @PostMapping("/draft")
    @Operation(summary = "自然语言生成规则（含校验，校验不过会带错误自动重试一轮）")
    @PreAuthorize("@ss.hasPermission('intent:rule:update')")
    public CommonResult<IntentRuleService.RuleDraft> draft(@RequestBody DraftReqVO reqVO) {
        return success(ruleService.draft(reqVO.getNaturalLanguage(), reqVO.getPage()));
    }

    @PostMapping("/preview")
    @Operation(summary = "试算：用与线上相同的取数与装配链路算出“现在会命中谁”")
    @PreAuthorize("@ss.hasPermission('intent:rule:query')")
    public CommonResult<Map<String, Object>> preview(@RequestBody PreviewReqVO reqVO) {
        return success(ruleService.preview(reqVO.getYaml(), reqVO.getPage(),
                SecurityFrameworkUtils.getLoginUserId()));
    }

    @PostMapping("/enable")
    @Operation(summary = "启用规则（先校验，落库后即时生效）")
    @PreAuthorize("@ss.hasPermission('intent:rule:update')")
    public CommonResult<List<Long>> enable(@RequestBody EnableReqVO reqVO) {
        return success(ruleService.enable(reqVO.getYaml(), reqVO.getTitle(), reqVO.getSourceText()));
    }

    @PostMapping("/status")
    @Operation(summary = "启用 / 停用规则")
    @PreAuthorize("@ss.hasPermission('intent:rule:update')")
    public CommonResult<Boolean> updateStatus(@RequestBody StatusReqVO reqVO) {
        ruleService.setStatus(reqVO.getId(), reqVO.getStatus());
        return success(true);
    }

    public static final class StatusReqVO {

        private Long id;
        private Integer status;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除规则")
    @PreAuthorize("@ss.hasPermission('intent:rule:update')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        ruleService.delete(id);
        return success(true);
    }

    public static final class DraftReqVO {

        private String naturalLanguage;
        private String page;

        public String getNaturalLanguage() { return naturalLanguage; }
        public void setNaturalLanguage(String naturalLanguage) { this.naturalLanguage = naturalLanguage; }
        public String getPage() { return page; }
        public void setPage(String page) { this.page = page; }
    }

    public static final class PreviewReqVO {

        private String yaml;
        private String page;

        public String getYaml() { return yaml; }
        public void setYaml(String yaml) { this.yaml = yaml; }
        public String getPage() { return page; }
        public void setPage(String page) { this.page = page; }
    }

    public static final class EnableReqVO {

        private String yaml;
        private String title;
        private String sourceText;

        public String getYaml() { return yaml; }
        public void setYaml(String yaml) { this.yaml = yaml; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getSourceText() { return sourceText; }
        public void setSourceText(String sourceText) { this.sourceText = sourceText; }
    }
}
