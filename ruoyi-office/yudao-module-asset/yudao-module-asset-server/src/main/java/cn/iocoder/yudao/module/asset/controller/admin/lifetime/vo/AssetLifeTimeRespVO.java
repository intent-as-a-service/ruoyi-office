package cn.iocoder.yudao.module.asset.controller.admin.lifetime.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 资产历史记录 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AssetLifeTimeRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "3082")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "采购订单id", example = "4907")
    @ExcelProperty("采购订单id")
    private Long assetId;

    @Schema(description = "资产编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("资产编码")
    private String assetCode;

    @Schema(description = "资产名称", example = "赵六")
    @ExcelProperty("资产名称")
    private String assetName;

    @Schema(description = "资产里程说明")
    @ExcelProperty("资产里程说明")
    private String assetMilestone;

    @Schema(description = "显示顺序")
    @ExcelProperty("显示顺序")
    private Integer sort;

    @Schema(description = "状态（0正常 1停用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("状态（0正常 1停用）")
    private Integer status;

    @Schema(description = "仓库地址")
    @ExcelProperty("仓库地址")
    private String storeAddress;

    @Schema(description = "备注", example = "你说的对")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}