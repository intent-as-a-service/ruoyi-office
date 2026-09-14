package cn.iocoder.yudao.module.wms.controller.admin.goodscommonoperationorder.vo;

import cn.iocoder.yudao.module.wms.dal.dataobject.goodswarehousingdetail.GoodsWarehousingDetailDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 领用、退库、归还、借用、调拨主新增/修改 Request VO")
@Data
public class GoodsCommonOperationOrderSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "29078")
    private Long id;

    @Schema(description = "采购订单id", example = "7468")
    private Long purchaseOrderId;

    @Schema(description = "采购订单编码")
    private String purchaseOrderCode;

    @Schema(description = "采购订单名称", example = "张三")
    private String purchaseOrderName;

    @Schema(description = "入库单Id", example = "14728")
    private Long inWarehousingId;

    @Schema(description = "入库单编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "入库单编码不能为空")
    private String warehousingEntryCode;

    @Schema(description = "入库单名称", example = "李四")
    private String warehousingEntryName;

    @Schema(description = "领用、退库、归还、借用日期")
    private LocalDateTime commonOperationDate;

    @Schema(description = "领用、退库、归还、借用库说明")
    private String commonOperationDesc;

    @Schema(description = "领用、退库、归还、借用附件")
    private String commonOperationFile;

    @Schema(description = "采购物品合计")
    private BigDecimal stockReturnTotalData;

    @Schema(description = "入库物品金额合计")
    private BigDecimal commonOperationTotalAmount;

    @Schema(description = "公司id", example = "32290")
    private String companyId;

    @Schema(description = "公司名称", example = "张三")
    private String companyName;

    @Schema(description = "领用、退库、归还、借用部门编码", example = "12625")
    private String deptId;

    @Schema(description = "领用、退库、归还、借用部门名称", example = "芋艿")
    private String deptName;

    @Schema(description = "领用、退库、归还、借用人编码", example = "7475")
    private String userId;

    @Schema(description = "领用、退库、归还、借用人名称", example = "张三")
    private String userName;

    @Schema(description = "预计归还日期")
    private LocalDateTime expectReturnDate;

    @Schema(description = "调入公司id", example = "28856")
    private String transferCompanyId;

    @Schema(description = "调入公司名称", example = "张三")
    private String transferCompanyName;

    @Schema(description = "调入部门编码", example = "23780")
    private String transferDeptId;

    @Schema(description = "调入部门名称", example = "芋艿")
    private String transferDeptName;

    @Schema(description = "流程实例编号", example = "30757")
    private String processInstanceId;

    @Schema(description = "单据状态", example = "2")
    private Integer processStatus;

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