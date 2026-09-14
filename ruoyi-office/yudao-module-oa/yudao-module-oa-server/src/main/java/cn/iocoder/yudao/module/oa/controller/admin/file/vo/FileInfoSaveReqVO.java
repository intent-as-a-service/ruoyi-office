package cn.iocoder.yudao.module.oa.controller.admin.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 企业云盘文件信息新增/修改 Request VO")
@Data
public class FileInfoSaveReqVO {

    @Schema(description = "文件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "父文件夹ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "父文件夹ID不能为空")
    private Long parentId;

    @Schema(description = "文件类型(0文件 1文件夹)", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "文件类型不能为空")
    private Integer fileType;

    @Schema(description = "文件名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "我的文档")
    @NotEmpty(message = "文件名称不能为空")
    private String fileName;

    @Schema(description = "文件扩展名", example = "pdf")
    private String fileExtension;

    @Schema(description = "文件后缀名（不含点号）", example = "pdf")
    private String fileSuffix;

    @Schema(description = "文件分类（all全部 image图片 document文档 video视频 audio音频 archive压缩包 other其他）", example = "document")
    private String fileCategory;

    @Schema(description = "文件大小(字节)", example = "1024")
    private Long fileSize;

    @Schema(description = "文件存储路径", example = "/upload/2023/12/01/xxx.pdf")
    private String filePath;

    @Schema(description = "文件访问URL", example = "https://example.com/xxx.pdf")
    private String fileUrl;

    @Schema(description = "文件MD5值", example = "d41d8cd98f00b204e9800998ecf8427e")
    private String fileMd5;

    @Schema(description = "所有者ID", example = "1")
    private Long ownerId;

    @Schema(description = "所有者名称", example = "张三")
    private String ownerName;

    @Schema(description = "部门ID", example = "1")
    private Long deptId;

    @Schema(description = "部门名称", example = "研发部")
    private String deptName;

    @Schema(description = "是否共享文件夹", example = "false")
    private Boolean isShared;

    @Schema(description = "文件夹分享类型(0人员 1组织)", example = "0")
    private Integer shareType;

    @Schema(description = "分享权限(0仅查看 1可管理)", example = "0")
    private Integer sharePermission;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

}

