package cn.iocoder.yudao.module.oa.dal.dataobject.seal;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 用印申请单 DO
 *
 * @author 宇擎源码
 */
@TableName("oa_seal_apply_bill")
@KeySequence("oa_seal_apply_bill_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SealApplyBillDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 单据编号
     */
    private String billCode;
    /**
     * 流程实例编号
     */
    private String processInstanceId;
    /**
     * 单据状态
     */
    private Integer processStatus;
    
    // ========== 印章相关信息 ==========
    /**
     * 印章ID
     */
    private Long sealId;
    /**
     * 印章编号
     */
    private String sealNo;
    /**
     * 印章名称
     */
    private String sealName;
    /**
     * 印章类型
     */
    private Integer sealType;
    /**
     * 保管人ID
     */
    private Long keeperId;
    /**
     * 保管人名称
     */
    private String keeperName;
    /**
     * 保管部门ID
     */
    private Long keeperDeptId;
    /**
     * 保管部门名称
     */
    private String keeperDeptName;
    
    // ========== 用章申请信息 ==========
    /**
     * 用章事由
     */
    private String cause;
    /**
     * 用章类型（1合同用章 2证明用章 3公函用章 4其他用章）
     */
    private Integer useType;
    /**
     * 用章方式（1现场用章 2外借用章）
     */
    private Integer useMode;
    /**
     * 文件标题
     */
    private String documentTitle;
    /**
     * 文件类型
     */
    private String documentType;
    /**
     * 文件份数
     */
    private Integer documentCount;
    /**
     * 合同金额（合同用章时填写）
     */
    private BigDecimal contractAmount;
    /**
     * 合同对方（合同用章时填写）
     */
    private String contractParty;
    
    // ========== 时间相关 ==========
    /**
     * 预计用章时间
     */
    private LocalDateTime expectedUseTime;
    /**
     * 实际用章时间
     */
    private LocalDateTime actualUseTime;
    /**
     * 预计归还时间（外借用章时填写）
     */
    private LocalDateTime expectedReturnTime;
    /**
     * 实际归还时间（外借用章时填写）
     */
    private LocalDateTime actualReturnTime;
    
    // ========== 用章状态 ==========
    /**
     * 用章状态（0待处理 1已完成 2外借中 3已归还 4已逾期）
     */
    private Integer useStatus;
    /**
     * 是否紧急（0否 1是）
     */
    private Integer isUrgent;
    
    // ========== 基础字段 ==========
    /**
     * 申请人姓名
     */
    private String creatorName;
    /**
     * 公司ID
     */
    private Long companyId;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 部门ID
     */
    private Long deptId;
    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 备注
     */
    private String remark;

}
