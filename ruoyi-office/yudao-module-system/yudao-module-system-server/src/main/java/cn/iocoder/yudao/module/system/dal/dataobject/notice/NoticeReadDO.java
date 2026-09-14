package cn.iocoder.yudao.module.system.dal.dataobject.notice;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户公告已读关系表
 *
 * @author ruoyi
 */
@TableName("system_notice_read")
@KeySequence("system_notice_read_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class NoticeReadDO extends BaseDO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 公告ID
     */
    private Long noticeId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

}
