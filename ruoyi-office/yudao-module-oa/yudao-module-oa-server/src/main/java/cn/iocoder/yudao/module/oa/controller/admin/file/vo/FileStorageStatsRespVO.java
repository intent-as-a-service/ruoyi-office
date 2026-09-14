package cn.iocoder.yudao.module.oa.controller.admin.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 文件存储统计信息 Response VO")
@Data
public class FileStorageStatsRespVO {

    @Schema(description = "已用空间（字节）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024000")
    private Long usedSize;

    @Schema(description = "总空间限制（字节）", requiredMode = Schema.RequiredMode.REQUIRED, example = "5368709120")
    private Long totalSize;

    @Schema(description = "文件数量（仅文件，不包括文件夹）", requiredMode = Schema.RequiredMode.REQUIRED, example = "56")
    private Long fileCount;

    @Schema(description = "共享文件数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Long sharedFileCount;

}

