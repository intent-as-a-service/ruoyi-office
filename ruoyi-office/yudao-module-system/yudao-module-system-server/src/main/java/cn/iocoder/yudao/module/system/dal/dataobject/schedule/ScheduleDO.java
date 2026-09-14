package cn.iocoder.yudao.module.system.dal.dataobject.schedule;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 日程管理表
 *
 * @author 宇擎源码
 */
@TableName("system_schedule")
@KeySequence("system_schedule_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class ScheduleDO extends BaseDO {

    /**
     * 日程ID
     */
    @TableId
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 日程日期
     */
    private LocalDate scheduleDate;

    /**
     * 开始时间
     */
    private LocalTime startTime;

    /**
     * 结束时间
     */
    private LocalTime endTime;

    /**
     * 日程类型
     *
     * 字典类型：schedule_type（会议、任务、提醒等）
     */
    private String scheduleType;

    /**
     * 日程分类
     *
     * 字典类型：schedule_category（工作、个人、重要等）
     */
    private String scheduleCategory;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 创建人姓名
     */
    private String creatorName;

    /**
     * 是否推送（0否 1是）
     */
    private Boolean isPushed;

    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 租户编号
     */
    private Long tenantId;

}

