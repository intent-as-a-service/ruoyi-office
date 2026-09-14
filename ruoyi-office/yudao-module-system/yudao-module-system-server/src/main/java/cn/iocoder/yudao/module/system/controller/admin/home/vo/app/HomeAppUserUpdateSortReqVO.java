package cn.iocoder.yudao.module.system.controller.admin.home.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户应用配置排序更新 Request VO
 *
 * @author 宇擎源码
 */
@Schema(description = "管理后台 - 用户应用配置排序更新 Request VO")
@Data
public class HomeAppUserUpdateSortReqVO {

    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "应用ID不能为空")
    private Long id;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "排序不能为空")
    private Integer sort;

}

