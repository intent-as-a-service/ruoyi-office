package cn.iocoder.yudao.module.asset.controller.admin.lifetime.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 资产历史记录分页 Request VO")
@Data
public class AssetLifeTimePageReqVO extends PageParam {

    @Schema(description = "采购订单id", example = "4907")
    private Long assetId;

    @Schema(description = "资产编码")
    private String assetCode;

    @Schema(description = "资产名称", example = "赵六")
    private String assetName;

    @Schema(description = "资产里程说明")
    private String assetMilestone;

    @Schema(description = "显示顺序")
    private Integer sort;

    @Schema(description = "状态（0正常 1停用）", example = "1")
    private Integer status;

    @Schema(description = "仓库地址")
    private String storeAddress;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}