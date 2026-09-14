package cn.iocoder.yudao.module.oa.controller.admin.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 共享文件列表 Response VO")
@Data
public class SharedFileListRespVO {

    @Schema(description = "文件ID", example = "1")
    private Long fileId;

    @Schema(description = "文件名称", example = "项目文档.pdf")
    private String fileName;

    @Schema(description = "文件类型(0文件夹 1文件)", example = "1")
    private Integer fileType;

    @Schema(description = "文件大小", example = "1024")
    private Long fileSize;

    @Schema(description = "文件扩展名", example = "pdf")
    private String fileExtension;

    @Schema(description = "文件后缀", example = "pdf")
    private String fileSuffix;

    @Schema(description = "文件分类（all全部 image图片 document文档 video视频 audio音频 archive压缩包 other其他）", example = "document")
    private String fileCategory;

    @Schema(description = "所有者ID", example = "1")
    private Long ownerId;

    @Schema(description = "所有者名称", example = "张三")
    private String ownerName;

    @Schema(description = "部门名称", example = "研发部")
    private String deptName;

    @Schema(description = "用户对此文件的权限(0仅查看 1可管理)", example = "0")
    private Integer userPermission;

    @Schema(description = "权限名称", example = "仅查看")
    private String permissionName;

    @Schema(description = "分享路径", example = "项目文档/设计图")
    private String sharePath;

    @Schema(description = "根分享文件夹ID", example = "1")
    private Long rootShareId;

    @Schema(description = "是否为根分享文件", example = "true")
    private Boolean isRootShare;

    @Schema(description = "是否可编辑", example = "false")
    private Boolean canEdit;

    @Schema(description = "是否可删除", example = "false")
    private Boolean canDelete;

    @Schema(description = "是否可分享", example = "false")
    private Boolean canShare;

    @Schema(description = "是否收藏", example = "false")
    private Boolean isFavorite;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime updateTime;
}
