package cn.iocoder.yudao.module.wms.controller.admin.purchaseinwarehousing.vo;

import cn.iocoder.yudao.module.wms.dal.dataobject.goodswarehousingdetail.GoodsWarehousingDetailDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 采购入库新增/修改 Request VO")
@Data
public class PurchaseInWarehousingSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "28153")
    private Long id;

    @Schema(description = "采购订单id", example = "21864")
    private Long purchaseOrderId;

    @Schema(description = "采购订单编码")
    private String purchaseOrderCode;

    @Schema(description = "采购订单名称", example = "王五")
    private String purchaseOrderName;

    @Schema(description = "入库单编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "入库单编码不能为空")
    private String warehousingEntryCode;

    @Schema(description = "入库单名称", example = "芋艿")
    private String warehousingEntryName;

    @Schema(description = "入库日期")
    private LocalDateTime inWarehousingDate;

    @Schema(description = "入库说明")
    private String inWarehousingDesc;

    @Schema(description = "附件")
    private String inWarehousingFile;

    @Schema(description = "采购物品合计")
    private BigDecimal inWarehousingTotalData;

    @Schema(description = "入库物品金额合计")
    private BigDecimal inWarehousingTotalAmount;

    @Schema(description = "显示顺序")
    private Integer sort;

    @Schema(description = "状态（0正常 1停用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态（0正常 1停用）不能为空")
    private Integer status;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "采购入库、领用、退库、归还、借用、调拨明细列表")
    private List<GoodsWarehousingDetailDO> goodsWarehousingDetails;

}