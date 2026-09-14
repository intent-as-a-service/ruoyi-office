package cn.iocoder.yudao.module.wms.controller.admin.goodscommonoperationorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 领用、退库、归还、借用、调拨主 Response VO")
@Data
@ExcelIgnoreUnannotated
public class GoodsCommonOperationOrderRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "29078")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "采购订单id", example = "7468")
    @ExcelProperty("采购订单id")
    private Long purchaseOrderId;

    @Schema(description = "采购订单编码")
    @ExcelProperty("采购订单编码")
    private String purchaseOrderCode;

    @Schema(description = "采购订单名称", example = "张三")
    @ExcelProperty("采购订单名称")
    private String purchaseOrderName;

    @Schema(description = "入库单Id", example = "14728")
    @ExcelProperty("入库单Id")
    private Long inWarehousingId;

    @Schema(description = "入库单编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("入库单编码")
    private String warehousingEntryCode;

    @Schema(description = "入库单名称", example = "李四")
    @ExcelProperty("入库单名称")
    private String warehousingEntryName;

    @Schema(description = "领用、退库、归还、借用日期")
    @ExcelProperty("领用、退库、归还、借用日期")
    private LocalDateTime commonOperationDate;

    @Schema(description = "领用、退库、归还、借用库说明")
    @ExcelProperty("领用、退库、归还、借用库说明")
    private String commonOperationDesc;

    @Schema(description = "领用、退库、归还、借用附件")
    @ExcelProperty("领用、退库、归还、借用附件")
    private String commonOperationFile;

    @Schema(description = "采购物品合计")
    @ExcelProperty("采购物品合计")
    private BigDecimal stockReturnTotalData;

    @Schema(description = "入库物品金额合计")
    @ExcelProperty("入库物品金额合计")
    private BigDecimal commonOperationTotalAmount;

    @Schema(description = "公司id", example = "32290")
    @ExcelProperty("公司id")
    private String companyId;

    @Schema(description = "公司名称", example = "张三")
    @ExcelProperty("公司名称")
    private String companyName;

    @Schema(description = "领用、退库、归还、借用部门编码", example = "12625")
    @ExcelProperty("领用、退库、归还、借用部门编码")
    private String deptId;

    @Schema(description = "领用、退库、归还、借用部门名称", example = "芋艿")
    @ExcelProperty("领用、退库、归还、借用部门名称")
    private String deptName;

    @Schema(description = "领用、退库、归还、借用人编码", example = "7475")
    @ExcelProperty("领用、退库、归还、借用人编码")
    private String userId;

    @Schema(description = "领用、退库、归还、借用人名称", example = "张三")
    @ExcelProperty("领用、退库、归还、借用人名称")
    private String userName;

    @Schema(description = "预计归还日期")
    @ExcelProperty("预计归还日期")
    private LocalDateTime expectReturnDate;

    @Schema(description = "调入公司id", example = "28856")
    @ExcelProperty("调入公司id")
    private String transferCompanyId;

    @Schema(description = "调入公司名称", example = "张三")
    @ExcelProperty("调入公司名称")
    private String transferCompanyName;

    @Schema(description = "调入部门编码", example = "23780")
    @ExcelProperty("调入部门编码")
    private String transferDeptId;

    @Schema(description = "调入部门名称", example = "芋艿")
    @ExcelProperty("调入部门名称")
    private String transferDeptName;

    @Schema(description = "流程实例编号", example = "30757")
    @ExcelProperty("流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态", example = "2")
    @ExcelProperty("单据状态")
    private Integer processStatus;

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