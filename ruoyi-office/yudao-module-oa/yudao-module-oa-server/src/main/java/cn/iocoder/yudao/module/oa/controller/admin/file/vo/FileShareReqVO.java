package cn.iocoder.yudao.module.oa.controller.admin.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "管理后台 - 文件分享 Request VO")
@Data
public class FileShareReqVO {

    @Schema(description = "文件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "文件ID不能为空")
    private Long fileId;

    @Schema(description = "分享目标列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "分享目标不能为空")
    private List<ShareTarget> shareTargets;

    @Schema(description = "是否继承权限", example = "true")
    private Boolean inheritPermission = true;

    @Schema(description = "分享备注", example = "项目文档分享")
    private String remark;

    @Data
    public static class ShareTarget {
        
        @Schema(description = "分享类型(0人员 1组织)", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
        @NotNull(message = "分享类型不能为空")
        private Integer shareType;

        @Schema(description = "目标ID(人员ID或组织ID)", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "目标ID不能为空")
        private Long targetId;

        @Schema(description = "目标名称(人员名称或组织名称)", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
        @NotNull(message = "目标名称不能为空")
        private String targetName;

        @Schema(description = "权限(0仅查看 1可管理)", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
        @NotNull(message = "权限不能为空")
        private Integer permission;
    }
}
