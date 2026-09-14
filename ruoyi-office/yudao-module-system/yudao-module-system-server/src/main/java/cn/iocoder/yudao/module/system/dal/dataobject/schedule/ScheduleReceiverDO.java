package cn.iocoder.yudao.module.system.dal.dataobject.schedule;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 日程接收人关系表
 *
 * @author 宇擎源码
 */
@TableName("system_schedule_receiver")
@KeySequence("system_schedule_receiver_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class ScheduleReceiverDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 日程ID
     */
    private Long scheduleId;

    /**
     * 接收人ID
     */
    private Long receiverId;

    /**
     * 接收人姓名
     */
    private String receiverName;

    /**
     * 已读状态（0未读 1已读）
     */
    private Integer readStatus;

    /**
     * 已读时间
     */
    private LocalDateTime readTime;

    /**
     * 租户编号
     */
    private Long tenantId;

}

