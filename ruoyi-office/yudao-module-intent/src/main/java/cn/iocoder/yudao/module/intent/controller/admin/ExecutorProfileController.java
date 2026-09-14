package cn.iocoder.yudao.module.intent.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.intent.service.IntentService;
import dev.intent.sdk.executor.ExecutorProfile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 执行器档案管理 API（管理员使用）。
 *
 * <p>执行器 = "谁来做"（agent 推理循环 / skill 确定性流程）：类型、模型、
 * 工具白名单、限额以 YAML 档案声明，保存即校验并热更新到运行时；
 * 意图通过 {@code executor: <id>} 按名引用，无需改动意图契约。</p>
 */
@Tag(name = "执行器管理")
@RestController
@RequestMapping("/intent/executor")
public class ExecutorProfileController {

    @Resource
    private IntentService intentService;

    @GetMapping("/list")
    @Operation(summary = "执行器档案列表")
    @PreAuthorize("@ss.hasPermission('intent:executor:query')")
    public CommonResult<List<Map<String, Object>>> getExecutorList() {
        return success(intentService.getExecutorList());
    }

    @GetMapping("/options")
    @Operation(summary = "执行器下拉选项（内置 + 档案）")
    @PreAuthorize("@ss.hasPermission('intent:config:query')")
    public CommonResult<List<Map<String, Object>>> getExecutorOptions() {
        return success(intentService.getExecutorOptions());
    }

    @GetMapping("/get-yaml")
    @Operation(summary = "获得执行器档案的 YAML 定义（编辑器回显）")
    @PreAuthorize("@ss.hasPermission('intent:executor:query')")
    public CommonResult<String> getExecutorYaml(@RequestParam("executorId") String executorId) {
        return success(intentService.getExecutorYaml(executorId));
    }

    @GetMapping("/get-json")
    @Operation(summary = "获得执行器档案的 JSON 定义（结构化编辑器回显）")
    @PreAuthorize("@ss.hasPermission('intent:executor:query')")
    public CommonResult<String> getExecutorJson(@RequestParam("executorId") String executorId) {
        return success(intentService.getExecutorJson(executorId));
    }

    @GetMapping("/tool-options")
    @Operation(summary = "宿主工具清单（供工具白名单 / 流程工具节点勾选）")
    @PreAuthorize("@ss.hasPermission('intent:executor:query')")
    public CommonResult<List<Map<String, Object>>> getToolOptions() {
        return success(intentService.getToolOptions());
    }

    @PostMapping("/create")
    @Operation(summary = "新增执行器（YAML 档案，服务端强校验 + 立即生效）")
    @PreAuthorize("@ss.hasPermission('intent:executor:update')")
    public CommonResult<String> createExecutor(@RequestBody ExecutorYamlReqVO reqVO) {
        ExecutorProfile profile = intentService.createExecutor(reqVO.getYaml());
        return success(profile.id());
    }

    @PutMapping("/update")
    @Operation(summary = "修改执行器档案（executorId 不可变更）")
    @PreAuthorize("@ss.hasPermission('intent:executor:update')")
    public CommonResult<Boolean> updateExecutor(@RequestBody ExecutorYamlReqVO reqVO) {
        intentService.updateExecutor(reqVO.getExecutorId(), reqVO.getYaml());
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除执行器（引用它的意图执行时将明确报错）")
    @PreAuthorize("@ss.hasPermission('intent:executor:update')")
    public CommonResult<Boolean> deleteExecutor(@RequestParam("executorId") String executorId) {
        intentService.deleteExecutor(executorId);
        return success(true);
    }

    public static final class ExecutorYamlReqVO {

        private String executorId;
        private String yaml;

        public String getExecutorId() { return executorId; }
        public void setExecutorId(String executorId) { this.executorId = executorId; }
        public String getYaml() { return yaml; }
        public void setYaml(String yaml) { this.yaml = yaml; }
    }
}
