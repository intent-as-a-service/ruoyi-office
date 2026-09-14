package cn.iocoder.yudao.module.system.controller.admin.home.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户应用配置 Response VO
 *
 * @author 宇擎源码
 */
@Schema(description = "管理后台 - 用户应用配置 Response VO")
@Data
public class HomeAppUserRespVO {

    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "菜单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long menuId;

    @Schema(description = "自定义应用名称", example = "用户管理")
    private String name;

    @Schema(description = "自定义图标", example = "carbon:user")
    private String icon;

    @Schema(description = "自定义图标颜色", example = "#1890FF")
    private String color;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sort;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

    @Schema(description = "菜单名称", example = "用户管理")
    private String menuName;

    @Schema(description = "菜单路径", example = "/system/user")
    private String menuPath;

    @Schema(description = "菜单图标", example = "carbon:user")
    private String menuIcon;

}

