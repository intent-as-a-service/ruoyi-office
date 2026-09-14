package cn.iocoder.yudao.module.oa.dal.dataobject.file;

import lombok.*;

import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 企业云盘-收藏文件 DO
 *
 * @author 宇擎源码
 */
@TableName("oa_file_favorite")
@KeySequence("oa_file_favorite_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库,可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileFavoriteDO extends BaseDO {

    /**
     * 收藏ID
     */
    @TableId
    private Long id;
    
    /**
     * 文件ID
     */
    private Long fileId;
    
    /**
     * 收藏人ID
     */
    private Long userId;
    
    /**
     * 收藏人名称
     */
    private String userName;

}

