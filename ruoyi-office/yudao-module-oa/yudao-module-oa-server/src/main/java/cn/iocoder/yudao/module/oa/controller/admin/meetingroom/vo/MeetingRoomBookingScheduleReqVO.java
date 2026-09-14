package cn.iocoder.yudao.module.oa.controller.admin.meetingroom.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "管理后台 - 会议室预约信息查询 Request VO")
@Data
public class MeetingRoomBookingScheduleReqVO {

    @Schema(description = "会议室ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "会议室ID不能为空")
    private Long roomId;

    @Schema(description = "开始日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2025-01-13")
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @Schema(description = "结束日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2025-01-17")
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;
}

