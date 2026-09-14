package cn.iocoder.yudao.module.system.controller.admin.home.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用户首页 Response VO")
@Data
public class UserHomePageRespVO {

    @Schema(description = "关联ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;

    @Schema(description = "首页ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Long pageId;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
