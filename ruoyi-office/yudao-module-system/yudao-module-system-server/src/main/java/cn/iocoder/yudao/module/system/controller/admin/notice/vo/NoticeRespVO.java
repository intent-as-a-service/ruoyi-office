package cn.iocoder.yudao.module.system.controller.admin.notice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 通知公告信息 Response VO")
@Data
public class NoticeRespVO {

    @Schema(description = "通知公告序号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "公告标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "小博主")
    private String title;

    @Schema(description = "公告类型，字典类型：system_notice_type（通知公告、公司动态、行业咨询、规章制度等）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer type;

    @Schema(description = "公告内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "半生编码")
    private String content;

    @Schema(description = "状态，参见 CommonStatusEnum 枚举类", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "是否重要通知", example = "true")
    private Boolean isImportant;

    @Schema(description = "已读状态：1已读，0未读", example = "1")
    private Integer readStatus;

    @Schema(description = "创建者", example = "admin")
    private String creator;

    @Schema(description = "创建者名称", example = "管理员")
    private String creatorName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "时间戳格式")
    private LocalDateTime createTime;

}
