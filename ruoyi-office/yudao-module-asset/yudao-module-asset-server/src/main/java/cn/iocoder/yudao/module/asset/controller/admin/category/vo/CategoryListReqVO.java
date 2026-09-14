package cn.iocoder.yudao.module.asset.controller.admin.category.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 资产类别列表 Request VO")
@Data
public class CategoryListReqVO {

    @Schema(description = "类别编码")
    private String categoryCode;

    @Schema(description = "类别名称", example = "张三")
    private String categoryName;

    @Schema(description = "上级id", example = "3764")
    private Long parentId;

    @Schema(description = "级别")
    private Long level;

    @Schema(description = "显示顺序")
    private Integer sort;

    @Schema(description = "状态（0正常 1停用）", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}