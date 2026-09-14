package cn.iocoder.yudao.module.wms.controller.admin.purchaseinwarehousing.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 采购入库 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PurchaseInWarehousingRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "28153")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "采购订单id", example = "21864")
    @ExcelProperty("采购订单id")
    private Long purchaseOrderId;

    @Schema(description = "采购订单编码")
    @ExcelProperty("采购订单编码")
    private String purchaseOrderCode;

    @Schema(description = "采购订单名称", example = "王五")
    @ExcelProperty("采购订单名称")
    private String purchaseOrderName;

    @Schema(description = "入库单编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("入库单编码")
    private String warehousingEntryCode;

    @Schema(description = "入库单名称", example = "芋艿")
    @ExcelProperty("入库单名称")
    private String warehousingEntryName;

    @Schema(description = "入库日期")
    @ExcelProperty("入库日期")
    private LocalDateTime inWarehousingDate;

    @Schema(description = "入库说明")
    @ExcelProperty("入库说明")
    private String inWarehousingDesc;

    @Schema(description = "附件")
    @ExcelProperty("附件")
    private String inWarehousingFile;

    @Schema(description = "采购物品合计")
    @ExcelProperty("采购物品合计")
    private BigDecimal inWarehousingTotalData;

    @Schema(description = "入库物品金额合计")
    @ExcelProperty("入库物品金额合计")
    private BigDecimal inWarehousingTotalAmount;

    @Schema(description = "显示顺序")
    @ExcelProperty("显示顺序")
    private Integer sort;

    @Schema(description = "状态（0正常 1停用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("状态（0正常 1停用）")
    private Integer status;

    @Schema(description = "备注", example = "你说的对")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}