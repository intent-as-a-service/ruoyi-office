package cn.iocoder.yudao.common.server.attachment.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;

@Schema(description = "管理后台 - 通用附件信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AttachmentRespVO {

    @Schema(description = "附件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1882")
    @ExcelProperty("附件ID")
    private Long id;

    @Schema(description = "业务类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "seal_apply_bill")
    @ExcelProperty("业务类型")
    private String businessType;

    @Schema(description = "业务单据ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("业务单据ID")
    private Long businessId;

    @Schema(description = "文件名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "合同文件.pdf")
    @ExcelProperty("文件名称")
    private String fileName;

    @Schema(description = "文件路径", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("文件路径")
    private String filePath;

    @Schema(description = "文件访问URL", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("文件访问URL")
    private String fileUrl;

    @Schema(description = "文件大小（字节）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("文件大小")
    private Long fileSize;

    @Schema(description = "文件类型（MIME类型）", example = "application/pdf")
    @ExcelProperty("文件类型")
    private String fileType;

    @Schema(description = "文件扩展名", example = "pdf")
    @ExcelProperty("文件扩展名")
    private String fileExtension;

    @Schema(description = "上传时间")
    @ExcelProperty("上传时间")
    private LocalDateTime uploadTime;

    @Schema(description = "排序顺序", example = "1")
    @ExcelProperty("排序顺序")
    private Integer sortOrder;

    @Schema(description = "备注", example = "重要文件")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
