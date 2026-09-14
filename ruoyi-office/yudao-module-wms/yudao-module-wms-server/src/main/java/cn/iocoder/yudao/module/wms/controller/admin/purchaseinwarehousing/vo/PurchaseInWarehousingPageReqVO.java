package cn.iocoder.yudao.module.wms.controller.admin.purchaseinwarehousing.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 采购入库分页 Request VO")
@Data
public class PurchaseInWarehousingPageReqVO extends PageParam {

    @Schema(description = "采购订单id", example = "21864")
    private Long purchaseOrderId;

    @Schema(description = "采购订单编码")
    private String purchaseOrderCode;

    @Schema(description = "采购订单名称", example = "王五")
    private String purchaseOrderName;

    @Schema(description = "入库单编码")
    private String warehousingEntryCode;

    @Schema(description = "入库单名称", example = "芋艿")
    private String warehousingEntryName;

    @Schema(description = "入库日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] inWarehousingDate;

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

    @Schema(description = "状态（0正常 1停用）", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}