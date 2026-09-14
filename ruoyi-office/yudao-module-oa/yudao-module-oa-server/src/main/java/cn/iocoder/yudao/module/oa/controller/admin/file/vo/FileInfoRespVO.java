package cn.iocoder.yudao.module.oa.controller.admin.file.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import cn.idev.excel.annotation.ExcelProperty;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 企业云盘文件信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class FileInfoRespVO {

    @Schema(description = "文件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("文件ID")
    private Long id;

    @Schema(description = "父文件夹ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty("父文件夹ID")
    private Long parentId;

    @Schema(description = "文件类型(0文件 1文件夹)", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "文件类型", converter = DictConvert.class)
    @DictFormat("oa_file_type")
    private Integer fileType;

    @Schema(description = "文件名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "我的文档")
    @ExcelProperty("文件名称")
    private String fileName;

    @Schema(description = "文件扩展名", example = "pdf")
    @ExcelProperty("文件扩展名")
    private String fileExtension;

    @Schema(description = "文件后缀名（不含点号）", example = "pdf")
    @ExcelProperty("文件后缀名")
    private String fileSuffix;

    @Schema(description = "文件分类（all全部 image图片 document文档 video视频 audio音频 archive压缩包 other其他）", example = "document")
    @ExcelProperty(value = "文件分类", converter = DictConvert.class)
    @DictFormat("oa_file_category")
    private String fileCategory;

    @Schema(description = "文件大小(字节)", example = "1024")
    @ExcelProperty("文件大小")
    private Long fileSize;

    @Schema(description = "文件存储路径", example = "/upload/2023/12/01/xxx.pdf")
    @ExcelProperty("文件存储路径")
    private String filePath;

    @Schema(description = "文件访问URL", example = "https://example.com/xxx.pdf")
    @ExcelProperty("文件访问URL")
    private String fileUrl;

    @Schema(description = "文件MD5值", example = "d41d8cd98f00b204e9800998ecf8427e")
    @ExcelProperty("文件MD5值")
    private String fileMd5;

    @Schema(description = "所有者ID", example = "1")
    @ExcelProperty("所有者ID")
    private Long ownerId;

    @Schema(description = "所有者名称", example = "张三")
    @ExcelProperty("所有者名称")
    private String ownerName;

    @Schema(description = "部门ID", example = "1")
    @ExcelProperty("部门ID")
    private Long deptId;

    @Schema(description = "部门名称", example = "研发部")
    @ExcelProperty("部门名称")
    private String deptName;

    @Schema(description = "是否共享文件夹", example = "false")
    @ExcelProperty("是否共享")
    private Boolean isShared;

    @Schema(description = "文件夹分享类型(0人员 1组织)", example = "0")
    @ExcelProperty(value = "分享类型", converter = DictConvert.class)
    @DictFormat("oa_file_share_type")
    private Integer shareType;

    @Schema(description = "分享权限(0仅查看 1可管理)", example = "0")
    @ExcelProperty(value = "分享权限", converter = DictConvert.class)
    @DictFormat("oa_file_permission")
    private Integer sharePermission;

    @Schema(description = "排序", example = "1")
    @ExcelProperty("排序")
    private Integer sortOrder;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("更新时间")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime updateTime;

    @Schema(description = "是否收藏", example = "false")
    private Boolean isFavorite;

}

