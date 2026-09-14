package cn.iocoder.yudao.common.server.attachment.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 通用附件信息新增/修改 Request VO")
@Data
public class AttachmentSaveReqVO {

    @Schema(description = "附件ID", example = "1882")
    private Long id;

    @Schema(description = "业务类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "seal_apply_bill")
    @NotEmpty(message = "业务类型不能为空")
    private String businessType;

    @Schema(description = "业务单据ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "业务单据ID不能为空")
    private Long businessId;

    @Schema(description = "文件名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "合同文件.pdf")
    @NotEmpty(message = "文件名称不能为空")
    private String fileName;

    @Schema(description = "文件路径", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "文件路径不能为空")
    private String filePath;

    @Schema(description = "文件访问URL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "文件访问URL不能为空")
    private String fileUrl;

    @Schema(description = "文件大小（字节）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "文件大小不能为空")
    private Long fileSize;

    @Schema(description = "文件类型（MIME类型）", example = "application/pdf")
    private String fileType;

    @Schema(description = "文件扩展名", example = "pdf")
    private String fileExtension;

    @Schema(description = "上传时间")
    private LocalDateTime uploadTime;

    @Schema(description = "排序顺序", example = "1")
    private Integer sortOrder;

    @Schema(description = "备注", example = "重要文件")
    private String remark;

}
