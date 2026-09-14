package cn.iocoder.yudao.module.system.dal.dataobject.home;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户首页关联表
 *
 * @author 宇擎源码
 */
@TableName("system_user_home_page")
@KeySequence("system_user_home_page_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class UserHomePageDO extends BaseDO {

    /**
     * 关联ID
     */
    @TableId
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 首页ID
     */
    private Long pageId;

}
