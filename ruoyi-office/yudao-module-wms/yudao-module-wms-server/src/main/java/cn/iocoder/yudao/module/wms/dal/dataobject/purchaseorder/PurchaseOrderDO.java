package cn.iocoder.yudao.module.wms.dal.dataobject.purchaseorder;

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
 * 采购订单 DO
 *
 * @author 宇擎源码
 */
@TableName("wms_purchase_order")
@KeySequence("wms_purchase_order_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 采购订单编码
     */
    private String purchaseOrderCode;
    /**
     * 采购订单名称
     */
    private String purchaseOrderName;
    /**
     * 申请日期
     */
    private LocalDateTime applicantDate;
    /**
     * 公司id
     */
    private String companyId;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 申请部门编码
     */
    private String applicantDeptId;
    /**
     * 申请部门名称
     */
    private String applicantDeptName;
    /**
     * 申请人编码
     */
    private String applicantUserId;
    /**
     * 申请人名称
     */
    private String applicantUserName;
    /**
     * 供应商编码
     */
    private String supplierCode;
    /**
     * 供应商名称
     */
    private String supplierName;
    /**
     * 申请说明
     */
    private String applicantDesc;
    /**
     * 附件
     */
    private String purchaseOrderFile;
    /**
     * 采购物品合计
     */
    private BigDecimal purchaseOrderTotalData;
    /**
     * 采购物品金额合计
     */
    private BigDecimal purchaseOrderTotalAmount;
    /**
     * 流程实例编号
     */
    private String processInstanceId;
    /**
     * 单据状态
     */
    private Integer processStatus;
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
