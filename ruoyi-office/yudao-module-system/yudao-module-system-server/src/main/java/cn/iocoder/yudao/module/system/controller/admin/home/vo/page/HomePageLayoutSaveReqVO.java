package cn.iocoder.yudao.module.system.controller.admin.home.vo.page;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 保存首页布局 Request VO")
@Data
public class HomePageLayoutSaveReqVO {

    @Schema(description = "首页ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "首页ID不能为空")
    private Long pageId;

    @Schema(description = "布局JSON配置", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "布局配置不能为空")
    private String layoutJson;

}
