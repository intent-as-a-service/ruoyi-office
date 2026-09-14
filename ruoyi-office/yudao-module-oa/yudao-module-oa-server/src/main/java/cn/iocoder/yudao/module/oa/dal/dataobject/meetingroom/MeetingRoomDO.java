package cn.iocoder.yudao.module.oa.dal.dataobject.meetingroom;

import lombok.*;

import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 会议室信息 DO
 *
 * @author 宇擎源码
 */
@TableName("oa_meeting_room")
@KeySequence("oa_meeting_room_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingRoomDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    
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
     *
     * 枚举 {@link TODO oa_meeting_room_type 对应的类}
     */
    private Integer roomType;
    
    /**
     * 负责人ID
     */
    private Long managerId;
    
    /**
     * 负责人姓名
     */
    private String managerName;
    
    /**
     * 负责人联系方式
     */
    private String managerPhone;
    
    /**
     * 可用状态（0正常 1维修中 2不可用）
     */
    private Integer availableStatus;
    
    /**
     * 会议室图片URL
     */
    private String picUrl;
    
    /**
     * 坐席数
     */
    private Integer seatCount;
    
    /**
     * 会议室设备（逗号分隔：tv,computer,remote,projector,water_dispenser,locker）
     */
    private String equipment;
    
    /**
     * 附件URL
     */
    private String attachmentUrl;
    
    /**
     * 备注（200字以内）
     */
    private String remark;
    
    /**
     * 允许预定
     */
    private Boolean allowBooking;
    
    /**
     * 预定需审批
     */
    private Boolean needApproval;
    
    /**
     * 可用范围（0全部成员 1指定成员）
     */
    private Integer bookingScope;
    
    /**
     * 可预定成员ID（逗号分隔，当bookingScope=1时有效）
     */
    private String bookingMembers;
    
    /**
     * 显示顺序
     */
    private Integer sort;

}

