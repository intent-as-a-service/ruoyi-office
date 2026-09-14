package cn.iocoder.yudao.module.system.dal.dataobject.home;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 首页布局配置表
 *
 * @author 宇擎源码
 */
@TableName("system_home_page_layout")
@KeySequence("system_home_page_layout_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class HomePageLayoutDO extends BaseDO {

    /**
     * 布局ID
     */
    @TableId
    private Long id;
    
    /**
     * 首页ID
     */
    private Long pageId;
    
    /**
     * 组件编码
     */
    private String componentCode;
    
    /**
     * X坐标
     */
    private Integer positionX;
    
    /**
     * Y坐标
     */
    private Integer positionY;
    
    /**
     * 宽度（栅格数）
     */
    private Integer width;
    
    /**
     * 高度（栅格数）
     */
    private Integer height;
    
    /**
     * 组件配置（JSON格式）
     */
    private String config;
    
    /**
     * 排序
     */
    private Integer sort;

}
