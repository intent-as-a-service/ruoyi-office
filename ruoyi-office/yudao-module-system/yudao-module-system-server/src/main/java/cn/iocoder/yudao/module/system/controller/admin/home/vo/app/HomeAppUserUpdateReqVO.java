package cn.iocoder.yudao.module.system.controller.admin.home.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户应用配置 Update Request VO
 *
 * @author 宇擎源码
 */
@Schema(description = "管理后台 - 用户应用配置更新 Request VO")
@Data
public class HomeAppUserUpdateReqVO {

    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "应用ID不能为空")
    private Long id;

    @Schema(description = "自定义应用名称", example = "用户管理")
    private String name;

    @Schema(description = "自定义图标", example = "carbon:user")
    private String icon;

    @Schema(description = "自定义图标颜色", example = "#1890FF")
    private String color;

    @Schema(description = "状态", example = "0")
    private Integer status;

}

