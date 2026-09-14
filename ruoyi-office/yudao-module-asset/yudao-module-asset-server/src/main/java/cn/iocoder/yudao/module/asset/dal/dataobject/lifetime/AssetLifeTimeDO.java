package cn.iocoder.yudao.module.asset.dal.dataobject.lifetime;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 资产历史记录 DO
 *
 * @author 宇擎源码
 */
@TableName("asset_life_time")
@KeySequence("asset_life_time_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetLifeTimeDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 采购订单id
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
     * 资产里程说明
     */
    private String assetMilestone;
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
