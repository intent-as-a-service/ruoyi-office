package cn.iocoder.yudao.module.system.controller.admin.home.vo.component;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 首页组件创建/修改 Request VO")
@Data
public class HomeComponentSaveReqVO {

    @Schema(description = "组件ID", example = "1024")
    private Long id;

    @Schema(description = "分类ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @Schema(description = "组件名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "访问统计")
    @NotBlank(message = "组件名称不能为空")
    @Size(max = 100, message = "组件名称长度不能超过 100 个字符")
    private String name;

    @Schema(description = "组件编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "analytics_visits")
    @NotBlank(message = "组件编码不能为空")
    @Size(max = 50, message = "组件编码长度不能超过 50 个字符")
    private String code;

    @Schema(description = "组件路径", requiredMode = Schema.RequiredMode.REQUIRED, example = "dashboard/home/components/statistics/analytics-visits.vue")
    @NotBlank(message = "组件路径不能为空")
    @Size(max = 255, message = "组件路径长度不能超过 255 个字符")
    private String componentPath;

    @Schema(description = "组件描述", example = "展示网站访问数据统计")
    @Size(max = 500, message = "组件描述长度不能超过 500 个字符")
    private String description;

    @Schema(description = "预览图", example = "https://www.iocoder.cn/preview.jpg")
    @Size(max = 255, message = "预览图URL长度不能超过 255 个字符")
    private String previewImage;

    @Schema(description = "默认宽度", requiredMode = Schema.RequiredMode.REQUIRED, example = "12")
    @NotNull(message = "默认宽度不能为空")
    private Integer defaultWidth;

    @Schema(description = "默认高度", requiredMode = Schema.RequiredMode.REQUIRED, example = "4")
    @NotNull(message = "默认高度不能为空")
    private Integer defaultHeight;

    @Schema(description = "配置Schema", example = "{\"properties\":[]}")
    private String configSchema;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    @InEnum(CommonStatusEnum.class)
    private Integer status;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "排序不能为空")
    private Integer sort;

}
