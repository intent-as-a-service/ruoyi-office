package cn.iocoder.yudao.module.system.controller.admin.home.vo.page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 首页信息 Response VO")
@Data
public class HomePageRespVO {

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

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sort;

    @Schema(description = "创建者", example = "1")
    private String creator;

    @Schema(description = "使用状态：当前用户在用则为“使用中”，否则为空", example = "使用中")
    private String useStatus;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
