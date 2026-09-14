package cn.iocoder.yudao.module.asset.controller.admin.goods.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 物品信息新增/修改 Request VO")
@Data
public class AssetGoodsSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "13400")
    private Long id;

    @Schema(description = "物品编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "物品编码不能为空")
    private String goodsCode;

    @Schema(description = "物品名称", example = "张三")
    private String goodsName;

    @Schema(description = "资产类型编码")
    private String assetCategoryCode;

    @Schema(description = "资产类型名称", example = "王五")
    private String assetCategoryName;

    @Schema(description = "规格型号")
    private String assetModel;

    @Schema(description = "计量单位")
    private String assetUnit;

    @Schema(description = "厂商")
    private String manufacturer;

    @Schema(description = "品牌")
    private String brand;

    @Schema(description = "默认月残值率")
    private BigDecimal residualValueRate;

    @Schema(description = "库存下限")
    private BigDecimal inventoryLowerLimit;

    @Schema(description = "库存上下限")
    private BigDecimal inventoryLimit;

    @Schema(description = "是否进入资产列表")
    private String isJoinAsset;

    @Schema(description = "资产图片")
    private String assetIcon;

    @Schema(description = "资产附件")
    private String assetFile;

    @Schema(description = "显示顺序")
    private Integer sort;

    @Schema(description = "状态（0正常 1停用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态（0正常 1停用）不能为空")
    private Integer status;

    @Schema(description = "仓库地址")
    private String storeAddress;

    @Schema(description = "备注", example = "你猜")
    private String remark;

}