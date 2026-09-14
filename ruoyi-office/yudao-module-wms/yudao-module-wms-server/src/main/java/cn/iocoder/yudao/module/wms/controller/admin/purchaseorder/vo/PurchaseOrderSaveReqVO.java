package cn.iocoder.yudao.module.wms.controller.admin.purchaseorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.iocoder.yudao.module.wms.dal.dataobject.purchaseorderdetail.PurchaseOrderDetailDO;

@Schema(description = "管理后台 - 采购订单新增/修改 Request VO")
@Data
public class PurchaseOrderSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "27711")
    private Long id;

    @Schema(description = "采购订单编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "采购订单编码不能为空")
    private String purchaseOrderCode;

    @Schema(description = "采购订单名称", example = "张三")
    private String purchaseOrderName;

    @Schema(description = "申请日期")
    private LocalDateTime applicantDate;

    @Schema(description = "公司id", example = "13618")
    private String companyId;

    @Schema(description = "公司名称", example = "芋艿")
    private String companyName;

    @Schema(description = "申请部门编码", example = "14505")
    private String applicantDeptId;

    @Schema(description = "申请部门名称", example = "李四")
    private String applicantDeptName;

    @Schema(description = "申请人编码", example = "11670")
    private String applicantUserId;

    @Schema(description = "申请人名称", example = "芋艿")
    private String applicantUserName;

    @Schema(description = "供应商编码")
    private String supplierCode;

    @Schema(description = "供应商名称", example = "王五")
    private String supplierName;

    @Schema(description = "申请说明")
    private String applicantDesc;

    @Schema(description = "附件")
    private String purchaseOrderFile;

    @Schema(description = "采购物品合计")
    private BigDecimal purchaseOrderTotalData;

    @Schema(description = "采购物品金额合计")
    private BigDecimal purchaseOrderTotalAmount;

    @Schema(description = "流程实例编号", example = "21395")
    private String processInstanceId;

    @Schema(description = "单据状态", example = "2")
    private Integer processStatus;

    @Schema(description = "显示顺序")
    private Integer sort;

    @Schema(description = "状态（0正常 1停用）")
     private Integer status;

    @Schema(description = "备注", example = "你猜")
    private String remark;

    @Schema(description = "采购订单明细列表")
    private List<PurchaseOrderDetailDO> purchaseOrderDetails;

}