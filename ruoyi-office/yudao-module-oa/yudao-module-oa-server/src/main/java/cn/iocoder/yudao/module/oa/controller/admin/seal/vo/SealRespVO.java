package cn.iocoder.yudao.module.oa.controller.admin.seal.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.ExcelProperty;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 印章信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SealRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "18149")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "公司ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("公司ID")
    private Long companyId;

    @Schema(description = "公司名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("公司名称")
    private String companyName;

    @Schema(description = "印章编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("印章编号")
    private String sealNo;

    @Schema(description = "印章名称", example = "公司公章")
    @ExcelProperty("印章名称")
    private String sealName;

    @Schema(description = "印章类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "印章类型", converter = DictConvert.class)
    @DictFormat("oa_seal_type")
    private Long sealType;

    @Schema(description = "分类", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "分类", converter = DictConvert.class)
    @DictFormat("oa_seal_cls")
    private Long sealCls;

    @Schema(description = "保管人ID")
    @ExcelProperty("保管人ID")
    private Long keeperId;

    @Schema(description = "保管人名称")
    @ExcelProperty("保管人名称")
    private String keeperName;

    @Schema(description = "保管部门ID")
    @ExcelProperty("保管部门ID")
    private Long keeperDeptId;

    @Schema(description = "保管部门名称")
    @ExcelProperty("保管部门名称")
    private String keeperDeptName;

    @Schema(description = "状态（0在库 1停用 2使用中）")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat("oa_seal_status")
    private Integer status;

    @Schema(description = "购买日期")
    @ExcelProperty("购买日期")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate purchaseDate;

    @Schema(description = "启用日期")
    @ExcelProperty("启用日期")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate enableDate;

    @Schema(description = "停用日期")
    @ExcelProperty("停用日期")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate disableDate;

    @Schema(description = "上传照片", example = "https://www.iocoder.cn")
    @ExcelProperty("上传照片")
    private String picUrl;

    @Schema(description = "显示顺序")
    @ExcelProperty("显示顺序")
    private Integer sort;

    @Schema(description = "备注", example = "重要印章")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}

