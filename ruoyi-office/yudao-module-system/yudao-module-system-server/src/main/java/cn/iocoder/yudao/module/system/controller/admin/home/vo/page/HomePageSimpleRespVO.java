package cn.iocoder.yudao.module.system.controller.admin.home.vo.page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 首页信息的精简 Response VO")
@Data
public class HomePageSimpleRespVO {

    @Schema(description = "首页ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "首页名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "销售仪表板")
    private String name;

    @Schema(description = "首页编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "sales_dashboard")
    private String code;

    @Schema(description = "首页描述", example = "用于销售人员查看业绩数据")
    private String description;

    @Schema(description = "预览图", example = "https://www.iocoder.cn/preview.jpg")
    private String previewImage;

    @Schema(description = "是否默认首页", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    private Boolean isDefault;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

}
