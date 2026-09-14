package cn.iocoder.yudao.module.asset.api.assetinfo.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资产信息 DO
 *
 * @author 宇擎源码
 */
@Data
public class AssetInfoReqDTO  {


    /**
     * 采购订单id
     */
    private Long purchaseOrderId;
    /**
     * 采购订单编码
     */
    private String purchaseOrderCode;
    /**
     * 采购订单名称
     */
    private String purchaseOrderName;
    /**
     * 入库单Id
     */
    private Long inWarehousingId;
    /**
     * 入库单编码
     */
    private String warehousingEntryCode;
    /**
     * 入库单名称
     */
    private String warehousingEntryName;
    /**
     * 资产编码
     */
    private String assetCode;
    /**
     * 资产名称
     */
    private String assetName;
    /**
     * 资产类型编码
     */
    private String assetCategoryCode;
    /**
     * 资产类型名称
     */
    private String assetCategoryName;
    /**
     * 规格型号
     */
    private String assetModel;
    /**
     * 计量单位
     */
    private String assetUnit;
    /**
     * 厂商
     */
    private String manufacturer;
    /**
     * 品牌
     */
    private String brand;
    /**
     * 序列号
     */
    private String serialNumber;
    /**
     * 资产状态编码
     */
    private String assetStatusCode;
    /**
     * 资产状态名称
     */
    private String assetStatusName;
    /**
     * 资产来源编码
     */
    private String assetSourceCode;
    /**
     * 资料来源名称
     */
    private String assetSourceName;
    /**
     * 购买日期
     */
    private LocalDateTime purchaseDate;
    /**
     * 购买价格
     */
    private BigDecimal purchasePrice;
    /**
     * 出场日期
     */
    private LocalDateTime dateOfProduction;
    /**
     * 管理公司id
     */
    private String adminCompanyId;
    /**
     * 管理公司名称
     */
    private String adminCompanyName;
    /**
     * 管理部门编码
     */
    private String adminDeptId;
    /**
     * 管理部门名称
     */
    private String adminDeptName;
    /**
     * 管理人员编码
     */
    private String adminManagerId;
    /**
     * 管理人员名称
     */
    private String adminManagerName;
    /**
     * 使用公司id
     */
    private String useCompanyId;
    /**
     * 使用公司名称
     */
    private String useCompanyName;
    /**
     * 使用部门编码
     */
    private String useDeptId;
    /**
     * 使用部门名称
     */
    private String useDeptName;
    /**
     * 使用人员编码
     */
    private String useAccountId;
    /**
     * 使用人员名称
     */
    private String useAccountName;
    /**
     * 存放仓库编码
     */
    private String wmsStoreCode;
    /**
     * 存放仓库名称
     */
    private String wmsStoreName;
    /**
     * 残值率
     */
    private BigDecimal residualValueRate;
    /**
     * 资产图片
     */
    private String assetIcon;
    /**
     * 资产附件
     */
    private String assetFile;
    /**
     * 显示顺序
     */
    private Integer sort;
    /**
     * 状态（0正常 1停用）
     */
    private Integer status;
    /**
     * 仓库地址
     */
    private String storeAddress;
    /**
     * 备注
     */
    private String remark;


}
