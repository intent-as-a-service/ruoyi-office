package cn.iocoder.yudao.module.oa.controller.admin.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 文件分享信息 Response VO")
@Data
public class FileShareRespVO {

    @Schema(description = "文件ID", example = "1")
    private Long fileId;

    @Schema(description = "文件名称", example = "项目文档.pdf")
    private String fileName;

    @Schema(description = "文件类型(0文件夹 1文件)", example = "1")
    private Integer fileType;

    @Schema(description = "文件大小", example = "1024")
    private Long fileSize;

    @Schema(description = "所有者名称", example = "张三")
    private String ownerName;

    @Schema(description = "是否已分享", example = "true")
    private Boolean isShared;

    @Schema(description = "分享目标列表")
    private List<ShareTargetInfo> shareTargets;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime createTime;

    @Data
    public static class ShareTargetInfo {
        
        @Schema(description = "权限ID", example = "1")
        private Long id;

        @Schema(description = "分享类型(0人员 1组织)", example = "0")
        private Integer shareType;

        @Schema(description = "分享类型名称", example = "人员")
        private String shareTypeName;

        @Schema(description = "目标ID", example = "1")
        private Long targetId;

        @Schema(description = "目标名称", example = "张三")
        private String targetName;

        @Schema(description = "权限(0仅查看 1可管理)", example = "0")
        private Integer permission;

        @Schema(description = "权限名称", example = "仅查看")
        private String permissionName;

        @Schema(description = "是否继承权限", example = "true")
        private Boolean inheritPermission;

        @Schema(description = "访问次数", example = "5")
        private Integer accessCount;

        @Schema(description = "创建时间")
        @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
        private LocalDateTime createTime;
    }
}
