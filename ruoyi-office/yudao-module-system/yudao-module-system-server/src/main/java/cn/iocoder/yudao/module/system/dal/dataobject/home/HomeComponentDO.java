package cn.iocoder.yudao.module.system.dal.dataobject.home;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 首页组件定义表
 *
 * @author 宇擎源码
 */
@TableName("system_home_component")
@KeySequence("system_home_component_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class HomeComponentDO extends BaseDO {

    /**
     * 组件ID
     */
    @TableId
    private Long id;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 组件名称
     */
    private String name;
    
    /**
     * 组件编码
     */
    private String code;
    
    /**
     * 组件路径
     */
    private String componentPath;
    
    /**
     * 组件描述
     */
    private String description;
    
    /**
     * 预览图
     */
    private String previewImage;
    
    /**
     * 默认宽度（网格列数1-24）
     */
    private Integer defaultWidth;
    
    /**
     * 默认高度（网格行数）
     */
    private Integer defaultHeight;
    
    /**
     * 配置Schema（JSON格式）
     */
    private String configSchema;
    
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    
    /**
     * 排序
     */
    private Integer sort;

}
