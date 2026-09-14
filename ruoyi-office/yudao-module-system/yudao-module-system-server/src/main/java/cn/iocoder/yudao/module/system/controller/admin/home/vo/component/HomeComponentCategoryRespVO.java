package cn.iocoder.yudao.module.system.controller.admin.home.vo.component;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 首页组件分类 Response VO")
@Data
public class HomeComponentCategoryRespVO {

    @Schema(description = "分类ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "统计卡片")
    private String name;

    @Schema(description = "分类编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "statistics")
    private String code;

    @Schema(description = "分类图标", example = "lucide:bar-chart-2")
    private String icon;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sort;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
