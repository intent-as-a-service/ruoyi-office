package cn.iocoder.yudao.module.system.dal.dataobject.home;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 首页配置表
 *
 * @author 宇擎源码
 */
@TableName("system_home_page")
@KeySequence("system_home_page_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class HomePageDO extends BaseDO {

    /**
     * 首页ID
     */
    @TableId
    private Long id;
    
    /**
     * 首页名称
     */
    private String name;
    
    /**
     * 首页编码
     */
    private String code;
    
    /**
     * 首页描述
     */
    private String description;
    
    /**
     * 预览图
     */
    private String previewImage;
    
    /**
     * 是否默认首页
     */
    private Boolean isDefault;
    
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
