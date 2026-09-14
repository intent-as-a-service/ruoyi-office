package cn.iocoder.yudao.module.oa.controller.admin.meetingroom.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 会议室信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MeetingRoomRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "会议室名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "第一会议室")
    @ExcelProperty("会议室名称")
    private String roomName;

    @Schema(description = "会议室位置", requiredMode = Schema.RequiredMode.REQUIRED, example = "公司本部二楼")
    @ExcelProperty("会议室位置")
    private String roomLocation;

    @Schema(description = "会议室类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "会议室类型", converter = DictConvert.class)
    @DictFormat("oa_meeting_room_type")
    private Integer roomType;

    @Schema(description = "负责人ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("负责人ID")
    private Long managerId;

    @Schema(description = "负责人姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "王军")
    @ExcelProperty("负责人姓名")
    private String managerName;

    @Schema(description = "负责人联系方式", example = "13800138000")
    @ExcelProperty("负责人联系方式")
    private String managerPhone;

    @Schema(description = "可用状态（0正常 1维修中 2不可用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty(value = "可用状态", converter = DictConvert.class)
    @DictFormat("oa_meeting_room_status")
    private Integer availableStatus;

    @Schema(description = "会议室图片URL", example = "https://www.iocoder.cn/image.jpg")
    @ExcelProperty("会议室图片")
    private String picUrl;

    @Schema(description = "坐席数", example = "20")
    @ExcelProperty("坐席数")
    private Integer seatCount;

    @Schema(description = "会议室设备（数组形式）", example = "[\"tv\",\"computer\"]")
    private List<String> equipment;

    @Schema(description = "附件URL", example = "https://www.iocoder.cn/file.pdf")
    @ExcelProperty("附件URL")
    private String attachmentUrl;

    @Schema(description = "备注（200字以内）", example = "可容纳20人的会议室")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "允许预定", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @ExcelProperty("允许预定")
    private Boolean allowBooking;

    @Schema(description = "预定需审批", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    @ExcelProperty("预定需审批")
    private Boolean needApproval;

    @Schema(description = "可用范围（0全部成员 1指定成员）", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty("可用范围")
    private Integer bookingScope;

    @Schema(description = "可预定成员ID（数组形式）", example = "[1,2,3]")
    private List<Long> bookingMembers;

    @Schema(description = "显示顺序", example = "1")
    @ExcelProperty("显示顺序")
    private Integer sort;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}

