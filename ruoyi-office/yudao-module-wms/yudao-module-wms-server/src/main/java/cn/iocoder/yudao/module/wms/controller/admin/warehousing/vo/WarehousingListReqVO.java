package cn.iocoder.yudao.module.wms.controller.admin.warehousing.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 仓库信息列表 Request VO")
@Data
public class WarehousingListReqVO {

    @Schema(description = "仓库编码")
    private String warehousingCode;

    @Schema(description = "仓库名称", example = "王五")
    private String warehousingName;

    @Schema(description = "上级id", example = "15065")
    private Long parentId;

    @Schema(description = "仓库类型编码")
    private String warehousingCategoryCode;

    @Schema(description = "仓库地址")
    private String warehousingAddress;

    @Schema(description = "公司id", example = "16176")
    private String companyId;

    @Schema(description = "状态（0正常 1停用）", example = "2")
    private Integer status;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}