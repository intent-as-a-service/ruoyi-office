package cn.iocoder.yudao.module.bpm.controller.admin.task.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - 撤回流程任务的 Request VO")
@Data
public class BpmTaskWithdrawReqVO {

    @Schema(description = "流程实例编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotEmpty(message = "流程实例编号不能为空")
    private String processInstanceId;

    @Schema(description = "撤回意见", requiredMode = Schema.RequiredMode.REQUIRED, example = "制单人撤回重新提交")
    @NotEmpty(message = "撤回意见不能为空")
    private String reason;

}
