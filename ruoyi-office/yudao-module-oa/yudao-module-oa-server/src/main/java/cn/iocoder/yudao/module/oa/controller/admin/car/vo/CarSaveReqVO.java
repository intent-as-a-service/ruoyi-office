package cn.iocoder.yudao.module.oa.controller.admin.car.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 车辆信息新增/修改 Request VO")
@Data
public class CarSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "18149")
    private Long id;

    @Schema(description = "公司ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "公司ID不能为空")
    private Long companyId;

    @Schema(description = "公司名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "公司名称不能为空")
    private String companyName;

    @Schema(description = "车牌号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "车牌号不能为空")
    private String carNo;

    @Schema(description = "车辆名称", example = "赵六")
    private String carName;

    @Schema(description = "车型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "车型不能为空")
    private Long carType;

    @Schema(description = "分类", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分类不能为空")
    private Long carCls;

    @Schema(description = "状态（0空闲 1停用 2使用中）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "品牌型号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "品牌型号不能为空")
    private String brand;

    @Schema(description = "车座")
    private String seatNum;

    @Schema(description = "裸车价", example = "1610")
    private BigDecimal barePrice;

    @Schema(description = "交强险到期日期")
    private LocalDate forceInsuranceDate;

    @Schema(description = "商业险到期日期")
    private LocalDate businessInsuranceDate;

    @Schema(description = "年检日期")
    private LocalDate yearCheckDate;

    @Schema(description = "上传照片", example = "https://www.iocoder.cn")
    private String picUrl;

    @Schema(description = "显示顺序")
    private Integer sort;

    @Schema(description = "备注", example = "你猜")
    private String remark;

}