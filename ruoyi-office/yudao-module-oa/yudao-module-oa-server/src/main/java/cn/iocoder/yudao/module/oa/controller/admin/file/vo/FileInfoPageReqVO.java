package cn.iocoder.yudao.module.oa.controller.admin.file.vo;

import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 企业云盘文件信息分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FileInfoPageReqVO extends PageParam {

    @Schema(description = "父文件夹ID", example = "0")
    private Long parentId;

    @Schema(description = "文件类型(0文件 1文件夹)", example = "1")
    private Integer fileType;

    @Schema(description = "文件名称", example = "我的文档")
    private String fileName;

    @Schema(description = "文件分类过滤（all全部 image图片 document文档 video视频 audio音频 archive压缩包 other其他）", example = "document")
    private String fileCategoryFilter;

    @Schema(description = "所有者ID", example = "1")
    private Long ownerId;

    @Schema(description = "所有者名称", example = "张三")
    private String ownerName;

    @Schema(description = "部门ID", example = "1")
    private Long deptId;

    @Schema(description = "是否共享文件夹", example = "false")
    private Boolean isShared;

    @Schema(description = "文件夹分享类型(0人员 1组织)", example = "0")
    private Integer shareType;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}

