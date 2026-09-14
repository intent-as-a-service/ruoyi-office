package cn.iocoder.yudao.module.system.controller.admin.home.vo.component;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 首页组件信息 Response VO")
@Data
public class HomeComponentRespVO {

    @Schema(description = "组件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "分类ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long categoryId;

    @Schema(description = "组件名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "访问统计")
    private String name;

    @Schema(description = "组件编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "analytics_visits")
    private String code;

    @Schema(description = "组件路径", requiredMode = Schema.RequiredMode.REQUIRED, example = "dashboard/home/components/statistics/analytics-visits.vue")
    private String componentPath;

    @Schema(description = "组件描述", example = "展示网站访问数据统计")
    private String description;

    @Schema(description = "预览图", example = "https://www.iocoder.cn/preview.jpg")
    private String previewImage;

    @Schema(description = "默认宽度", requiredMode = Schema.RequiredMode.REQUIRED, example = "12")
    private Integer defaultWidth;

    @Schema(description = "默认高度", requiredMode = Schema.RequiredMode.REQUIRED, example = "4")
    private Integer defaultHeight;

    @Schema(description = "配置Schema", example = "{\"properties\":[]}")
    private String configSchema;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sort;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
