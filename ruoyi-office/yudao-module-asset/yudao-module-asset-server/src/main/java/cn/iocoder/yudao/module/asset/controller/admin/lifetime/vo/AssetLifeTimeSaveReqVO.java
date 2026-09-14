package cn.iocoder.yudao.module.asset.controller.admin.lifetime.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 资产历史记录新增/修改 Request VO")
@Data
public class AssetLifeTimeSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "3082")
    private Long id;

    @Schema(description = "采购订单id", example = "4907")
    private Long assetId;

    @Schema(description = "资产编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "资产编码不能为空")
    private String assetCode;

    @Schema(description = "资产名称", example = "赵六")
    private String assetName;

    @Schema(description = "资产里程说明")
    private String assetMilestone;

    @Schema(description = "显示顺序")
    private Integer sort;

    @Schema(description = "状态（0正常 1停用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态（0正常 1停用）不能为空")
    private Integer status;

    @Schema(description = "仓库地址")
    private String storeAddress;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

}