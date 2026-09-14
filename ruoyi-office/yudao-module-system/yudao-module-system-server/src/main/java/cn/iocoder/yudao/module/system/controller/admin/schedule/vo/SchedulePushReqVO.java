package cn.iocoder.yudao.module.system.controller.admin.schedule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 推送日程 Request VO")
@Data
public class SchedulePushReqVO {

    @Schema(description = "日程ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "日程ID不能为空")
    private Long scheduleId;

    @Schema(description = "接收人ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 2, 3]")
    @NotEmpty(message = "接收人ID列表不能为空")
    private List<Long> receiverIds;

}

