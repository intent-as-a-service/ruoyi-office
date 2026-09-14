package cn.iocoder.yudao.module.oa.controller.admin.car.vo;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 还车申请单分页 Request VO")
@Data
public class CarReturnBillPageReqVO extends PageParam {

    @Schema(description = "单据编号")
    private String billCode;

    @Schema(description = "流程实例编号", example = "6065")
    private String processInstanceId;

    @Schema(description = "单据状态", example = "1")
    private Integer processStatus;

    @Schema(description = "用车申请单")
    private String applyBill;

    @Schema(description = "车辆", example = "10499")
    private Long carId;

    @Schema(description = "车牌号")
    private String carNo;

    @Schema(description = "出车时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] goTime;

    @Schema(description = "回车时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] returnTime;

    @Schema(description = "出车地点")
    private String goArea;

    @Schema(description = "回车地点")
    private String returnArea;

    @Schema(description = "用车事由")
    private String cause;

    @Schema(description = "申请人")
    private String applyer;

    @Schema(description = "随行人")
    private String passenger;

    @Schema(description = "创建人")
    @ExcelProperty("创建人")
    private String creator;

    @Schema(description = "还车说明", example = "随便")
    private String remark;

    @Schema(description = "创建者姓名", example = "芋艿")
    private String creatorName;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "父级ID", example = "11284")
    private Long parentId;

    @Schema(description = "部门ID", example = "20051")
    private Long deptId;

    @Schema(description = "部门名称", example = "芋艿")
    private String deptName;

    @Schema(description = "公司ID", example = "27382")
    private Long companyId;

    @Schema(description = "公司名称", example = "李四")
    private String companyName;

}