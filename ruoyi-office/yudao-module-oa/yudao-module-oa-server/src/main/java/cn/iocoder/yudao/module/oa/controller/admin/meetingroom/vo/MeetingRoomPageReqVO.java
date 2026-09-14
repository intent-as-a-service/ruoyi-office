package cn.iocoder.yudao.module.oa.controller.admin.meetingroom.vo;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 会议室信息分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MeetingRoomPageReqVO extends PageParam {

    @Schema(description = "会议室名称", example = "第一会议室")
    private String roomName;

    @Schema(description = "会议室位置", example = "公司本部二楼")
    private String roomLocation;

    @Schema(description = "会议室类型", example = "1")
    private Integer roomType;

    @Schema(description = "负责人姓名", example = "王军")
    private String managerName;

    @Schema(description = "可用状态（0正常 1维修中 2不可用）", example = "0")
    private Integer availableStatus;

    @Schema(description = "允许预定", example = "true")
    private Boolean allowBooking;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}

