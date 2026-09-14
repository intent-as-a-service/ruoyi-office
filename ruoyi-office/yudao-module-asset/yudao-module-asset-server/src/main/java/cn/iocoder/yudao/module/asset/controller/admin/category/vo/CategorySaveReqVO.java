package cn.iocoder.yudao.module.asset.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 资产类别新增/修改 Request VO")
@Data
public class CategorySaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "10301")
    private Long id;

    @Schema(description = "类别编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "类别编码不能为空")
    private String categoryCode;

    @Schema(description = "类别名称", example = "张三")
    private String categoryName;

    @Schema(description = "上级id", requiredMode = Schema.RequiredMode.REQUIRED, example = "3764")
    @NotNull(message = "上级id不能为空")
    private Long parentId;

    @Schema(description = "级别")
    private Long level;

    @Schema(description = "显示顺序")
    private Integer sort;

    @Schema(description = "状态（0正常 1停用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态（0正常 1停用）不能为空")
    private Integer status;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

}