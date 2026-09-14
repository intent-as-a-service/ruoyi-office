package cn.iocoder.yudao.module.oa.dal.dataobject.meetingroom;

import lombok.*;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 会议室预定申请单 DO
 *
 * @author 宇擎源码
 */
@TableName("oa_meeting_room_booking")
@KeySequence("oa_meeting_room_booking_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingRoomBookingDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    
    /**
     * 单据编号
     */
    private String billCode;
    
    /**
     * 流程实例编号
     */
    private String processInstanceId;
    
    /**
     * 单据状态（0草稿 1审批中 2审批通过 3审批拒绝 4已取消）
     */
    private Integer processStatus;
    
    // ========== 会议室相关信息 ==========
    /**
     * 会议室ID
     */
    private Long roomId;
    
    /**
     * 会议室名称
     */
    private String roomName;
    
    /**
     * 会议室位置
     */
    private String roomLocation;
    
    /**
     * 会议室类型
     */
    private Integer roomType;
    
    // ========== 会议信息 ==========
    /**
     * 会议名称
     */
    private String meetingTitle;
    
    /**
     * 会议开始时间
     */
    private LocalDateTime meetingStartTime;
    
    /**
     * 会议结束时间
     */
    private LocalDateTime meetingEndTime;
    
    /**
     * 主持人ID
     */
    private Long moderatorId;
    
    /**
     * 主持人姓名
     */
    private String moderatorName;
    
    /**
     * 会议备注
     */
    private String meetingRemark;
    
    // ========== 提醒和参会人 ==========
    /**
     * 会议提醒（1不提醒 2提前5分钟 3提前10分钟 4提前15分钟 5提前30分钟）
     */
    private Integer reminderType;
    
    /**
     * 与会人ID列表（JSON数组格式）
     */
    private String attendees;
    
    /**
     * 与会人姓名列表（JSON数组格式）
     */
    private String attendeeNames;
    
    // ========== 附件 ==========
    /**
     * 附件URL列表（JSON数组格式）
     */
    private String attachmentUrls;
    
    // ========== 使用状态 ==========
    /**
     * 使用状态（0待使用 1使用中 2已完成 3已取消）
     */
    private Integer useStatus;
    
    // ========== 基础字段 ==========
    /**
     * 申请人姓名
     */
    private String creatorName;
    
    /**
     * 公司ID
     */
    private Long companyId;
    
    /**
     * 公司名称
     */
    private String companyName;
    
    /**
     * 部门ID
     */
    private Long deptId;
    
    /**
     * 部门名称
     */
    private String deptName;
    
    /**
     * 备注
     */
    private String remark;

}

