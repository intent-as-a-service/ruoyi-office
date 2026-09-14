package cn.iocoder.yudao.module.oa.controller.admin.seal.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 印章信息新增/修改 Request VO")
@Data
public class SealSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "18149")
    private Long id;

    @Schema(description = "公司ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "公司ID不能为空")
    private Long companyId;

    @Schema(description = "公司名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "公司名称不能为空")
    private String companyName;

    @Schema(description = "印章编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "印章编号不能为空")
    private String sealNo;

    @Schema(description = "印章名称", example = "公司公章")
    private String sealName;

    @Schema(description = "印章类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "印章类型不能为空")
    private Long sealType;

    @Schema(description = "分类", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分类不能为空")
    private Long sealCls;

    @Schema(description = "保管人ID")
    private Long keeperId;

    @Schema(description = "保管人名称")
    private String keeperName;

    @Schema(description = "保管部门ID")
    private Long keeperDeptId;

    @Schema(description = "保管部门名称")
    private String keeperDeptName;

    @Schema(description = "状态（0在库 1停用 2使用中）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "购买日期")
    private LocalDate purchaseDate;

    @Schema(description = "启用日期")
    private LocalDate enableDate;

    @Schema(description = "停用日期")
    private LocalDate disableDate;

    @Schema(description = "上传照片", example = "https://www.iocoder.cn")
    private String picUrl;

    @Schema(description = "显示顺序")
    private Integer sort;

    @Schema(description = "备注", example = "重要印章")
    private String remark;

}

