package cn.iocoder.yudao.module.asset.controller.admin.goods.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 物品信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AssetGoodsRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "13400")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "物品编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("物品编码")
    private String goodsCode;

    @Schema(description = "物品名称", example = "张三")
    @ExcelProperty("物品名称")
    private String goodsName;

    @Schema(description = "资产类型编码")
    @ExcelProperty("资产类型编码")
    private String assetCategoryCode;

    @Schema(description = "资产类型名称", example = "王五")
    @ExcelProperty("资产类型名称")
    private String assetCategoryName;

    @Schema(description = "规格型号")
    @ExcelProperty("规格型号")
    private String assetModel;

    @Schema(description = "计量单位")
    @ExcelProperty("计量单位")
    private String assetUnit;

    @Schema(description = "厂商")
    @ExcelProperty("厂商")
    private String manufacturer;

    @Schema(description = "品牌")
    @ExcelProperty("品牌")
    private String brand;

    @Schema(description = "默认月残值率")
    @ExcelProperty("默认月残值率")
    private BigDecimal residualValueRate;

    @Schema(description = "库存下限")
    @ExcelProperty("库存下限")
    private BigDecimal inventoryLowerLimit;

    @Schema(description = "库存上下限")
    @ExcelProperty("库存上下限")
    private BigDecimal inventoryLimit;

    @Schema(description = "是否进入资产列表")
    @ExcelProperty("是否进入资产列表")
    private String isJoinAsset;

    @Schema(description = "资产图片")
    @ExcelProperty("资产图片")
    private String assetIcon;

    @Schema(description = "资产附件")
    @ExcelProperty("资产附件")
    private String assetFile;

    @Schema(description = "显示顺序")
    @ExcelProperty("显示顺序")
    private Integer sort;

    @Schema(description = "状态（0正常 1停用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("状态（0正常 1停用）")
    private Integer status;

    @Schema(description = "仓库地址")
    @ExcelProperty("仓库地址")
    private String storeAddress;

    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}