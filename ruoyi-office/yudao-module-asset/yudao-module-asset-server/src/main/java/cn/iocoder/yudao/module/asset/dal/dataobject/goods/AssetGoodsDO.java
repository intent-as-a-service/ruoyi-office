package cn.iocoder.yudao.module.asset.dal.dataobject.goods;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 物品信息 DO
 *
 * @author 宇擎源码
 */
@TableName("asset_goods")
@KeySequence("asset_goods_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetGoodsDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 物品编码
     */
    private String goodsCode;
    /**
     * 物品名称
     */
    private String goodsName;
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
     * 默认月残值率
     */
    private BigDecimal residualValueRate;
    /**
     * 库存下限
     */
    private BigDecimal inventoryLowerLimit;
    /**
     * 库存上下限
     */
    private BigDecimal inventoryLimit;
    /**
     * 是否进入资产列表
     */
    private String isJoinAsset;
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
