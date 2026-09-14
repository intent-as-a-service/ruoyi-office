package cn.iocoder.yudao.module.system.dal.dataobject.home;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 首页组件分类表
 *
 * @author 宇擎源码
 */
@TableName("system_home_component_category")
@KeySequence("system_home_component_category_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class HomeComponentCategoryDO extends BaseDO {

    /**
     * 分类ID
     */
    @TableId
    private Long id;
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 分类编码
     */
    private String code;
    
    /**
     * 分类图标
     */
    private String icon;
    
    /**
     * 排序
     */
    private Integer sort;

}
