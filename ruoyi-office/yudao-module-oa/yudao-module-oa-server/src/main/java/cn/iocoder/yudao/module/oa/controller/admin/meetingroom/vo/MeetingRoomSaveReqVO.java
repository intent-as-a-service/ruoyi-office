package cn.iocoder.yudao.module.oa.controller.admin.meetingroom.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.*;
import java.util.List;

@Schema(description = "管理后台 - 会议室信息新增/修改 Request VO")
@Data
public class MeetingRoomSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "会议室名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "第一会议室")
    @NotEmpty(message = "会议室名称不能为空")
    private String roomName;

    @Schema(description = "会议室位置", requiredMode = Schema.RequiredMode.REQUIRED, example = "公司本部二楼")
    @NotEmpty(message = "会议室位置不能为空")
    private String roomLocation;

    @Schema(description = "会议室类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "会议室类型不能为空")
    private Integer roomType;

    @Schema(description = "负责人ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "负责人不能为空")
    private Long managerId;

    @Schema(description = "负责人姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "王军")
    @NotEmpty(message = "负责人姓名不能为空")
    private String managerName;

    @Schema(description = "负责人联系方式", example = "13800138000")
    private String managerPhone;

    @Schema(description = "可用状态（0正常 1维修中 2不可用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "可用状态不能为空")
    private Integer availableStatus;

    @Schema(description = "会议室图片URL", example = "https://www.iocoder.cn/image.jpg")
    private String picUrl;

    @Schema(description = "坐席数", example = "20")
    private Integer seatCount;

    @Schema(description = "会议室设备（数组形式，如：[\"tv\",\"computer\",\"projector\"]）", example = "[\"tv\",\"computer\"]")
    private List<String> equipment;

    @Schema(description = "附件URL", example = "https://www.iocoder.cn/file.pdf")
    private String attachmentUrl;

    @Schema(description = "备注（200字以内）", example = "可容纳20人的会议室")
    @Size(max = 200, message = "备注长度不能超过200字")
    private String remark;

    @Schema(description = "允许预定", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "允许预定不能为空")
    private Boolean allowBooking;

    @Schema(description = "预定需审批", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    @NotNull(message = "预定需审批不能为空")
    private Boolean needApproval;

    @Schema(description = "可用范围（0全部成员 1指定成员）", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "可用范围不能为空")
    private Integer bookingScope;

    @Schema(description = "可预定成员ID（数组形式，当bookingScope=1时有效）", example = "[1,2,3]")
    private List<Long> bookingMembers;

    @Schema(description = "显示顺序", example = "1")
    private Integer sort;

}

