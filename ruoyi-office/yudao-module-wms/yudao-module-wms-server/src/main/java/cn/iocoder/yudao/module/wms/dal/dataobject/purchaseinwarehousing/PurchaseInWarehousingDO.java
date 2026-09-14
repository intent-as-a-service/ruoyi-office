package cn.iocoder.yudao.module.wms.dal.dataobject.purchaseinwarehousing;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 采购入库 DO
 *
 * @author 宇擎源码
 */
@TableName("wms_purchase_in_warehousing")
@KeySequence("wms_purchase_in_warehousing_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseInWarehousingDO extends BaseDO {

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
     * 采购订单编码
     */
    private String purchaseOrderCode;
    /**
     * 采购订单名称
     */
    private String purchaseOrderName;
    /**
     * 入库单编码
     */
    private String warehousingEntryCode;
    /**
     * 入库单名称
     */
    private String warehousingEntryName;
    /**
     * 入库日期
     */
    private LocalDateTime inWarehousingDate;
    /**
     * 入库说明
     */
    private String inWarehousingDesc;
    /**
     * 附件
     */
    private String inWarehousingFile;
    /**
     * 采购物品合计
     */
    private BigDecimal inWarehousingTotalData;
    /**
     * 入库物品金额合计
     */
    private BigDecimal inWarehousingTotalAmount;
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
