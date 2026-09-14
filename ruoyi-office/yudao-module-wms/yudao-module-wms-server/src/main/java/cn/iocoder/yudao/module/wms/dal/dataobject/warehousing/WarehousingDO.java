package cn.iocoder.yudao.module.wms.dal.dataobject.warehousing;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 仓库信息 DO
 *
 * @author 宇擎源码
 */
@TableName("wms_warehousing")
@KeySequence("wms_warehousing_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehousingDO extends BaseDO {

    public static final Long PARENT_ID_ROOT = 0L;

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 仓库编码
     */
    private String warehousingCode;
    /**
     * 仓库名称
     */
    private String warehousingName;
    /**
     * 上级id
     */
    private Long parentId;
    /**
     * 级别
     */
    private Long level;
    /**
     * 仓库类型编码
     *
     * 枚举
     */
    private String warehousingCategoryCode;
    /**
     * 仓库类型名称
     */
    private String warehousingCategoryName;
    /**
     * 仓库地址
     */
    private String warehousingAddress;
    /**
     * 公司id
     */
    private String companyId;
    /**
     * 公司名称
     */
    private String companyName;
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
