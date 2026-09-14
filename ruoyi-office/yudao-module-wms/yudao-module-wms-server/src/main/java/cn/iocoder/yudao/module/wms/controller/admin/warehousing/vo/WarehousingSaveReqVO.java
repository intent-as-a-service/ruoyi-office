package cn.iocoder.yudao.module.wms.controller.admin.warehousing.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 仓库信息新增/修改 Request VO")
@Data
public class WarehousingSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "2866")
    private Long id;

    @Schema(description = "仓库编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "仓库编码不能为空")
    private String warehousingCode;

    @Schema(description = "仓库名称", example = "王五")
    private String warehousingName;

    @Schema(description = "上级id", requiredMode = Schema.RequiredMode.REQUIRED, example = "15065")
    @NotNull(message = "上级id不能为空")
    private Long parentId;

    @Schema(description = "级别")
    private Long level;

    @Schema(description = "仓库类型编码")
    private String warehousingCategoryCode;

    @Schema(description = "仓库类型名称", example = "王五")
    private String warehousingCategoryName;

    @Schema(description = "仓库地址")
    private String warehousingAddress;

    @Schema(description = "公司id", example = "16176")
    private String companyId;

    @Schema(description = "公司名称", example = "王五")
    private String companyName;

    @Schema(description = "显示顺序")
    private Integer sort;

    @Schema(description = "状态（0正常 1停用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "状态（0正常 1停用）不能为空")
    private Integer status;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

}