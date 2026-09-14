package cn.iocoder.yudao.module.oa.controller.admin.car.vo;

import lombok.*;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;
import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 车辆信息分页 Request VO")
@Data
public class CarPageReqVO extends PageParam {

    @Schema(description = "公司ID")
    private Long companyId;

    @Schema(description = "公司名称")
    private String companyName;

    @Schema(description = "车牌号")
    private String carNo;

    @Schema(description = "车辆名称", example = "赵六")
    private String carName;

    @Schema(description = "车型", example = "2")
    private Long carType;

    @Schema(description = "分类")
    private Long carCls;

    @Schema(description = "状态（0空闲 1停用 2使用中）")
    private Integer status;

    @Schema(description = "品牌型号")
    private String brand;

    @Schema(description = "车座")
    private String seatNum;

    @Schema(description = "裸车价", example = "1610")
    private BigDecimal barePrice;

    @Schema(description = "交强险到期日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate[] forceInsuranceDate;

    @Schema(description = "商业险到期日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate[] businessInsuranceDate;

    @Schema(description = "年检日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate[] yearCheckDate;

    @Schema(description = "上传照片", example = "https://www.iocoder.cn")
    private String picUrl;

    @Schema(description = "显示顺序")
    private Integer sort;

    @Schema(description = "备注", example = "你猜")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}