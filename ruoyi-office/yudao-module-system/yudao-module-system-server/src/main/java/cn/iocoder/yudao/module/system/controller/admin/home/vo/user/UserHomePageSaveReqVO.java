package cn.iocoder.yudao.module.system.controller.admin.home.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 用户启用首页 Request VO")
@Data
public class UserHomePageSaveReqVO {

    @Schema(description = "首页ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "首页ID不能为空")
    private Long pageId;

}
