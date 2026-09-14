package cn.iocoder.yudao.module.intent.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.intent.service.IntentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import dev.intent.protocol.IntentSpec;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 意图管理 API（上架 / 角色配置，管理员使用）。
 */
@Tag(name = "意图管理")
@RestController
@RequestMapping("/intent/config")
public class IntentConfigController {

    @Resource
    private IntentService intentService;

    @GetMapping("/list")
    @Operation(summary = "意图配置列表（全部意图 + 运营配置合并视图）")
    @PreAuthorize("@ss.hasPermission('intent:config:query')")
    public CommonResult<List<Map<String, Object>>> getConfigList() {
        return success(intentService.getConfigList());
    }

    @PutMapping("/update")
    @Operation(summary = "更新意图配置（上架状态 + 可见角色 + 执行器引用）")
    @PreAuthorize("@ss.hasPermission('intent:config:update')")
    public CommonResult<Boolean> updateConfig(@RequestBody UpdateReqVO reqVO) {
        intentService.updateConfig(reqVO.getIntentId(), reqVO.getEnabled(), reqVO.getRoles(),
                reqVO.getRemark(), reqVO.getExecutor());
        return success(true);
    }

    // ==================== 意图规范维护（新增 / 修改 / 删除） ====================

    @GetMapping("/spec/get-yaml")
    @Operation(summary = "获得意图规范的 YAML 定义（编辑器回显）")
    @PreAuthorize("@ss.hasPermission('intent:config:query')")
    public CommonResult<String> getSpecYaml(@RequestParam("intentId") String intentId) {
        return success(intentService.getSpecYaml(intentId));
    }

    @PostMapping("/spec/create")
    @Operation(summary = "新增意图（YAML 定义，服务端强校验）")
    @PreAuthorize("@ss.hasPermission('intent:config:update')")
    public CommonResult<String> createSpec(@RequestBody SpecYamlReqVO reqVO) {
        dev.intent.protocol.IntentSpec spec = intentService.createSpec(reqVO.getYaml());
        return success(spec.getId());
    }

    @PutMapping("/spec/update")
    @Operation(summary = "修改意图定义（intentId 不可变更）")
    @PreAuthorize("@ss.hasPermission('intent:config:update')")
    public CommonResult<Boolean> updateSpec(@RequestBody SpecYamlReqVO reqVO) {
        intentService.updateSpec(reqVO.getIntentId(), reqVO.getYaml());
        return success(true);
    }

    @DeleteMapping("/spec/delete")
    @Operation(summary = "删除意图（同时清理上架/角色配置）")
    @PreAuthorize("@ss.hasPermission('intent:config:update')")
    public CommonResult<Boolean> deleteSpec(@RequestParam("intentId") String intentId) {
        intentService.deleteSpec(intentId);
        return success(true);
    }

    public static final class SpecYamlReqVO {

        private String intentId;
        private String yaml;

        public String getIntentId() { return intentId; }
        public void setIntentId(String intentId) { this.intentId = intentId; }
        public String getYaml() { return yaml; }
        public void setYaml(String yaml) { this.yaml = yaml; }
    }

    public static final class UpdateReqVO {

        private String intentId;
        private Boolean enabled;
        private List<String> roles;
        private String remark;
        /** 执行器引用（builtin-agent / 档案 id；空 = 缺省内置推理循环） */
        private String executor;

        public String getIntentId() { return intentId; }
        public void setIntentId(String intentId) { this.intentId = intentId; }
        public Boolean getEnabled() { return enabled; }
        public void setEnabled(Boolean enabled) { this.enabled = enabled; }
        public List<String> getRoles() { return roles; }
        public void setRoles(List<String> roles) { this.roles = roles; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
        public String getExecutor() { return executor; }
        public void setExecutor(String executor) { this.executor = executor; }
    }
}
