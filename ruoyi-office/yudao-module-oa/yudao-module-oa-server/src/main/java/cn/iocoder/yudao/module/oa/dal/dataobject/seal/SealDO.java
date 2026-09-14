package cn.iocoder.yudao.module.oa.dal.dataobject.seal;

import lombok.*;

import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 印章信息 DO
 *
 * @author 宇擎源码
 */
@TableName("oa_seal")
@KeySequence("oa_seal_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SealDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    
    /**
     * 公司ID
     */
    private Long companyId;
    
    /**
     * 公司名称
     */
    private String companyName;
    
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
     *
     * 枚举 {@link TODO oa_seal_type 对应的类}
     */
    private Long sealType;
    
    /**
     * 分类
     *
     * 枚举 {@link TODO oa_seal_cls 对应的类}
     */
    private Long sealCls;
    
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
    
    /**
     * 购买日期
     */
    private LocalDate purchaseDate;
    
    /**
     * 启用日期
     */
    private LocalDate enableDate;
    
    /**
     * 停用日期
     */
    private LocalDate disableDate;
    
    /**
     * 上传照片
     */
    private String picUrl;
    
    /**
     * 显示顺序
     */
    private Integer sort;
    
    /**
     * 状态（0在库 1停用 2使用中）
     */
    private Integer status;
    
    /**
     * 备注
     */
    private String remark;

}

