package cn.iocoder.yudao.module.wms.dal.dataobject.purchaseorderdetail;

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
 * 采购订单明细 DO
 *
 * @author 宇擎源码
 */
@TableName("wms_purchase_order_detail")
@KeySequence("wms_purchase_order_detail_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDetailDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 采购订单id
     */
    private String purchaseOrderId;
    /**
     * 采购订单编码
     */
    private String purchaseOrderCode;
    /**
     * 采购订单名称
     */
    private String purchaseOrderName;
    /**
     * 资产类别编码
     */
    private String categoryCode;
    /**
     * 资产类别名称
     */
    private String categoryName;
    /**
     * 物品名称编码
     */
    private String goodsCode;
    /**
     * 物品名称名称
     */
    private String goodsName;
    /**
     * 采购单据
     */
    private BigDecimal purchasePrice;
    /**
     * 采购数量
     */
    private BigDecimal purchaseNum;
    /**
     * 采购总价
     */
    private BigDecimal purchaseTotalAmonut;
    /**
     * 显示顺序
     */
    private Integer sort;
    /**
     * 状态（0正常 1停用）
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

}