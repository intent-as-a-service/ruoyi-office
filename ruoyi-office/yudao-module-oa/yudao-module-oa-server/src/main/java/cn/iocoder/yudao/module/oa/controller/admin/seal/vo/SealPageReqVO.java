package cn.iocoder.yudao.module.oa.controller.admin.seal.vo;

import lombok.*;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;
import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 印章信息分页 Request VO")
@Data
public class SealPageReqVO extends PageParam {

    @Schema(description = "公司ID")
    private Long companyId;

    @Schema(description = "公司名称")
    private String companyName;

    @Schema(description = "印章编号")
    private String sealNo;

    @Schema(description = "印章名称", example = "公司公章")
    private String sealName;

    @Schema(description = "印章类型", example = "1")
    private Long sealType;

    @Schema(description = "分类")
    private Long sealCls;

    @Schema(description = "保管人ID")
    private Long keeperId;

    @Schema(description = "保管人名称")
    private String keeperName;

    @Schema(description = "保管部门ID")
    private Long keeperDeptId;

    @Schema(description = "保管部门名称")
    private String keeperDeptName;

    @Schema(description = "状态（0在库 1停用 2使用中）")
    private Integer status;

    @Schema(description = "购买日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate[] purchaseDate;

    @Schema(description = "启用日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate[] enableDate;

    @Schema(description = "停用日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate[] disableDate;

    @Schema(description = "上传照片", example = "https://www.iocoder.cn")
    private String picUrl;

    @Schema(description = "显示顺序")
    private Integer sort;

    @Schema(description = "备注", example = "重要印章")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}

