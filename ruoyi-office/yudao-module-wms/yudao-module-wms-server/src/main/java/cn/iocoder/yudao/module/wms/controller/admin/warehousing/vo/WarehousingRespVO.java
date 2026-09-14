package cn.iocoder.yudao.module.wms.controller.admin.warehousing.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 仓库信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class WarehousingRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "2866")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "仓库编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("仓库编码")
    private String warehousingCode;

    @Schema(description = "仓库名称", example = "王五")
    @ExcelProperty("仓库名称")
    private String warehousingName;

    @Schema(description = "上级id", requiredMode = Schema.RequiredMode.REQUIRED, example = "15065")
    @ExcelProperty("上级id")
    private Long parentId;

    @Schema(description = "级别")
    @ExcelProperty("级别")
    private Long level;

    @Schema(description = "仓库类型编码")
    @ExcelProperty(value = "仓库类型编码", converter = DictConvert.class)
    @DictFormat("wms_category_code") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String warehousingCategoryCode;

    @Schema(description = "仓库类型名称", example = "王五")
    @ExcelProperty("仓库类型名称")
    private String warehousingCategoryName;

    @Schema(description = "仓库地址")
    @ExcelProperty("仓库地址")
    private String warehousingAddress;

    @Schema(description = "公司id", example = "16176")
    @ExcelProperty("公司id")
    private String companyId;

    @Schema(description = "公司名称", example = "王五")
    @ExcelProperty("公司名称")
    private String companyName;

    @Schema(description = "显示顺序")
    @ExcelProperty("显示顺序")
    private Integer sort;

    @Schema(description = "状态（0正常 1停用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("状态（0正常 1停用）")
    private Integer status;

    @Schema(description = "备注", example = "你说的对")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建者")
    @ExcelProperty("创建者")
    private String creator;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}