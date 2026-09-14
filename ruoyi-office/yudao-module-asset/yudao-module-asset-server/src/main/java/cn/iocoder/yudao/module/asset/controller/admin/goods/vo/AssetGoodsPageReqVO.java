package cn.iocoder.yudao.module.asset.controller.admin.goods.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 物品信息分页 Request VO")
@Data
public class AssetGoodsPageReqVO extends PageParam {

    @Schema(description = "物品编码")
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

    @Schema(description = "状态（0正常 1停用）", example = "1")
    private Integer status;

    @Schema(description = "仓库地址")
    private String storeAddress;

    @Schema(description = "备注", example = "你猜")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}