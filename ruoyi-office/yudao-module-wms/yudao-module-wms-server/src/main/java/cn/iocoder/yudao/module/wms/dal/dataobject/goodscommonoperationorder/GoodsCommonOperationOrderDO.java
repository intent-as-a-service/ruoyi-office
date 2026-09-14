package cn.iocoder.yudao.module.wms.dal.dataobject.goodscommonoperationorder;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 领用、退库、归还、借用、调拨主 DO
 *
 * @author 宇擎源码
 */
@TableName("wms_goods_common_operation_order")
@KeySequence("wms_goods_common_operation_order_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsCommonOperationOrderDO extends BaseDO {

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
     * 领用、退库、归还、借用日期
     */
    private LocalDateTime commonOperationDate;
    /**
     * 领用、退库、归还、借用库说明
     */
    private String commonOperationDesc;
    /**
     * 领用、退库、归还、借用附件
     */
    private String commonOperationFile;
    /**
     * 采购物品合计
     */
    private BigDecimal stockReturnTotalData;
    /**
     * 入库物品金额合计
     */
    private BigDecimal commonOperationTotalAmount;
    /**
     * 公司id
     */
    private String companyId;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 领用、退库、归还、借用部门编码
     */
    private String deptId;
    /**
     * 领用、退库、归还、借用部门名称
     */
    private String deptName;
    /**
     * 领用、退库、归还、借用人编码
     */
    private String userId;
    /**
     * 领用、退库、归还、借用人名称
     */
    private String userName;
    /**
     * 预计归还日期
     */
    private LocalDateTime expectReturnDate;
    /**
     * 调入公司id
     */
    private String transferCompanyId;
    /**
     * 调入公司名称
     */
    private String transferCompanyName;
    /**
     * 调入部门编码
     */
    private String transferDeptId;
    /**
     * 调入部门名称
     */
    private String transferDeptName;
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
