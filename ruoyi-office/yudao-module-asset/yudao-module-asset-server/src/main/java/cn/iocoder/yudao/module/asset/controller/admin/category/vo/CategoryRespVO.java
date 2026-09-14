package cn.iocoder.yudao.module.asset.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 资产类别 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CategoryRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "10301")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "类别编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("类别编码")
    private String categoryCode;

    @Schema(description = "类别名称", example = "张三")
    @ExcelProperty("类别名称")
    private String categoryName;

    @Schema(description = "上级id", requiredMode = Schema.RequiredMode.REQUIRED, example = "3764")
    @ExcelProperty("上级id")
    private Long parentId;

    @Schema(description = "级别")
    @ExcelProperty("级别")
    private Long level;

    @Schema(description = "显示顺序")
    @ExcelProperty("显示顺序")
    private Integer sort;

    @Schema(description = "状态（0正常 1停用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("状态（0正常 1停用）")
    private Integer status;

    @Schema(description = "备注", example = "你说的对")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}