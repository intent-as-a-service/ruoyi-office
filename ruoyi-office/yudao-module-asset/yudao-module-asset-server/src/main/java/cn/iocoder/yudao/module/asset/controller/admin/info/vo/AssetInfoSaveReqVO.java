package cn.iocoder.yudao.module.asset.controller.admin.info.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 资产信息新增/修改 Request VO")
@Data
public class AssetInfoSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "21848")
    private Long id;

    @Schema(description = "采购订单id", example = "9655")
    private Long purchaseOrderId;

    @Schema(description = "采购订单编码")
    private String purchaseOrderCode;

    @Schema(description = "采购订单名称", example = "王五")
    private String purchaseOrderName;

    @Schema(description = "入库单Id", example = "21728")
    private Long inWarehousingId;

    @Schema(description = "入库单编码")
    private String warehousingEntryCode;

    @Schema(description = "入库单名称", example = "王五")
    private String warehousingEntryName;

    @Schema(description = "资产编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "资产编码不能为空")
    private String assetCode;

    @Schema(description = "资产名称", example = "芋艿")
    private String assetName;

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

    @Schema(description = "序列号")
    private String serialNumber;

    @Schema(description = "资产状态编码")
    private String assetStatusCode;

    @Schema(description = "资产状态名称", example = "芋艿")
    private String assetStatusName;

    @Schema(description = "资产来源编码")
    private String assetSourceCode;

    @Schema(description = "资料来源名称", example = "芋艿")
    private String assetSourceName;

    @Schema(description = "购买日期")
    private LocalDateTime purchaseDate;

    @Schema(description = "购买价格", example = "1268")
    private BigDecimal purchasePrice;

    @Schema(description = "出场日期")
    private LocalDateTime dateOfProduction;

    @Schema(description = "管理公司id", example = "13355")
    private String adminCompanyId;

    @Schema(description = "管理公司名称", example = "赵六")
    private String adminCompanyName;

    @Schema(description = "管理部门编码", example = "32530")
    private String adminDeptId;

    @Schema(description = "管理部门名称", example = "赵六")
    private String adminDeptName;

    @Schema(description = "管理人员编码", example = "23566")
    private String adminManagerId;

    @Schema(description = "管理人员名称", example = "李四")
    private String adminManagerName;

    @Schema(description = "使用公司id", example = "10526")
    private String useCompanyId;

    @Schema(description = "使用公司名称", example = "张三")
    private String useCompanyName;

    @Schema(description = "使用部门编码", example = "26824")
    private String useDeptId;

    @Schema(description = "使用部门名称", example = "赵六")
    private String useDeptName;

    @Schema(description = "使用人员编码", example = "10315")
    private String useAccountId;

    @Schema(description = "使用人员名称", example = "李四")
    private String useAccountName;

    @Schema(description = "存放仓库编码")
    private String wmsStoreCode;

    @Schema(description = "存放仓库名称", example = "芋艿")
    private String wmsStoreName;

    @Schema(description = "残值率")
    private BigDecimal residualValueRate;

    @Schema(description = "资产图片")
    private String assetIcon;

    @Schema(description = "资产附件")
    private String assetFile;

    @Schema(description = "显示顺序")
    private Integer sort;

    //@Schema(description = "状态（0正常 1停用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态（0正常 1停用）不能为空")
    private Integer status;

    @Schema(description = "仓库地址")
    private String storeAddress;

    @Schema(description = "备注", example = "随便")
    private String remark;

}