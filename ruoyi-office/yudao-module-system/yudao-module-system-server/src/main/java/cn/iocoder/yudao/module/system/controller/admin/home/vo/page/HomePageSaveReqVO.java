package cn.iocoder.yudao.module.system.controller.admin.home.vo.page;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 首页创建/修改 Request VO")
@Data
public class HomePageSaveReqVO {

    @Schema(description = "首页ID", example = "1024")
    private Long id;

    @Schema(description = "首页名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "销售仪表板")
    @NotBlank(message = "首页名称不能为空")
    @Size(max = 100, message = "首页名称长度不能超过 100 个字符")
    private String name;

    @Schema(description = "首页编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "sales_dashboard")
    @NotBlank(message = "首页编码不能为空")
    @Size(max = 50, message = "首页编码长度不能超过 50 个字符")
    private String code;

    @Schema(description = "首页描述", example = "用于销售人员查看业绩数据")
    @Size(max = 500, message = "首页描述长度不能超过 500 个字符")
    private String description;

    @Schema(description = "预览图", example = "https://www.iocoder.cn/preview.jpg")
    @Size(max = 255, message = "预览图URL长度不能超过 255 个字符")
    private String previewImage;

    @Schema(description = "是否默认首页", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    @NotNull(message = "是否默认首页不能为空")
    private Boolean isDefault;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    @InEnum(CommonStatusEnum.class)
    private Integer status;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "排序不能为空")
    private Integer sort;

}
