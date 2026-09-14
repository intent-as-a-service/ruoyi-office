package cn.iocoder.yudao.module.wms.dal.dataobject.goodswarehousingdetail;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 采购入库、领用、退库、归还、借用、调拨明细 DO
 *
 * @author 宇擎源码
 */
@TableName("wms_goods_warehousing_detail")
@KeySequence("wms_goods_warehousing_detail_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsWarehousingDetailDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 采购订单id
     */
    private Long purchaseOrderId;
    /**
     * 关联采购单编码
     */
    private String purchaseOrderCode;
    /**
     * 采购订单名称
     */
    private String purchaseOrderName;
    /**
     * 公共操作Id
     */
    private Long commonOperationId;
    /**
     * 公共操作编码
     */
    private String commonOperationCode;
    /**
     * 公共操作名称
     */
    private String commonOperationName;
    /**
     * 公共操作类型
     */
    private String commonOperationType;
    /**
     * 资产ID
     */
    private Long assetId;
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
     * 调入部门编码
     */
    private String transferUseDeptId;
    /**
     * 调入部门名称
     */
    private String transferUseDeptName;
    /**
     * 调入部门使用人员编码
     */
    private String transferUseAccountId;
    /**
     * 调入使用人员名称
     */
    private String transferUseAccountName;
    /**
     * 资产图片
     */
    private String assetIcon;
    /**
     * 资产附件
     */
    private String assetFile;
    /**
     * 是否进入资产列表
     */
    private String isJoinAsset;
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
