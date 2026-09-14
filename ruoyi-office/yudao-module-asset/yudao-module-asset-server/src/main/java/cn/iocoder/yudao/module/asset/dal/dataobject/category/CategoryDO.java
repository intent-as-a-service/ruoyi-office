package cn.iocoder.yudao.module.asset.dal.dataobject.category;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 资产类别 DO
 *
 * @author 宇擎源码
 */
@TableName("asset_category")
@KeySequence("asset_category_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDO extends BaseDO {

    public static final Long PARENT_ID_ROOT = 0L;

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 类别编码
     */
    private String categoryCode;
    /**
     * 类别名称
     */
    private String categoryName;
    /**
     * 上级id
     */
    private Long parentId;
    /**
     * 级别
     */
    private Long level;
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
