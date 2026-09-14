package cn.iocoder.yudao.module.system.controller.admin.home.vo.page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 首页布局 Response VO")
@Data
public class HomePageLayoutRespVO {

    @Schema(description = "布局ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "首页ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long pageId;

    @Schema(description = "组件编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "analytics_visits")
    private String componentCode;

    @Schema(description = "X坐标", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer positionX;

    @Schema(description = "Y坐标", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer positionY;

    @Schema(description = "宽度", requiredMode = Schema.RequiredMode.REQUIRED, example = "6")
    private Integer width;

    @Schema(description = "高度", requiredMode = Schema.RequiredMode.REQUIRED, example = "4")
    private Integer height;

    @Schema(description = "组件配置", example = "{\"title\":\"访问统计\"}")
    private String config;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sort;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
