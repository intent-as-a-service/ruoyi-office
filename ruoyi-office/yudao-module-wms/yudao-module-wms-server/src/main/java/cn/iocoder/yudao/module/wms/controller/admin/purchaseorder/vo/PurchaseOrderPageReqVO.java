package cn.iocoder.yudao.module.wms.controller.admin.purchaseorder.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 采购订单分页 Request VO")
@Data
public class PurchaseOrderPageReqVO extends PageParam {

    @Schema(description = "采购订单编码")
    private String purchaseOrderCode;

    @Schema(description = "采购订单名称", example = "张三")
    private String purchaseOrderName;

    @Schema(description = "申请日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] applicantDate;

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

    @Schema(description = "状态（0正常 1停用）", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "你猜")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}