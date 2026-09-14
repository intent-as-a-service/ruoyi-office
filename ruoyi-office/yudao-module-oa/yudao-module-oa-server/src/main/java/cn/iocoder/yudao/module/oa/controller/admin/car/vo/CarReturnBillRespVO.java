package cn.iocoder.yudao.module.oa.controller.admin.car.vo;

import cn.iocoder.yudao.common.server.attachment.controller.vo.AttachmentRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 还车申请单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CarReturnBillRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "31608")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "单据编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("单据编号")
    private String billCode;

    @Schema(description = "流程实例编号", example = "6065")
    @ExcelProperty("流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态", example = "1")
    @ExcelProperty("单据状态")
    private Integer processStatus;

    @Schema(description = "用车申请单")
    @ExcelProperty("用车申请单")
    private String applyBill;

    @Schema(description = "车辆", example = "10499")
    @ExcelProperty("车辆")
    private Long carId;

    @Schema(description = "车牌号")
    @ExcelProperty("车牌号")
    private String carNo;

    @Schema(description = "出车时间")
    @ExcelProperty("出车时间")
    private LocalDateTime goTime;

    @Schema(description = "回车时间")
    @ExcelProperty("回车时间")
    private LocalDateTime returnTime;

    @Schema(description = "出车地点")
    @ExcelProperty("出车地点")
    private String goArea;

    @Schema(description = "回车地点")
    @ExcelProperty("回车地点")
    private String returnArea;

    @Schema(description = "用车事由")
    @ExcelProperty("用车事由")
    private String cause;

    @Schema(description = "申请人")
    @ExcelProperty("申请人")
    private String applyer;

    @Schema(description = "随行人")
    @ExcelProperty("随行人")
    private String passenger;

    @Schema(description = "创建人")
    @ExcelProperty("创建人")
    private String creator;

    @Schema(description = "还车说明", example = "随便")
    @ExcelProperty("还车说明")
    private String remark;

    @Schema(description = "创建者姓名", example = "芋艿")
    @ExcelProperty("创建者姓名")
    private String creatorName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "父级ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11284")
    @ExcelProperty("父级ID")
    private Long parentId;

    @Schema(description = "部门ID", example = "20051")
    @ExcelProperty("部门ID")
    private Long deptId;

    @Schema(description = "部门名称", example = "芋艿")
    @ExcelProperty("部门名称")
    private String deptName;

    @Schema(description = "公司ID", example = "27382")
    @ExcelProperty("公司ID")
    private Long companyId;

    @Schema(description = "公司名称", example = "李四")
    @ExcelProperty("公司名称")
    private String companyName;

    @Schema(description = "附件列表")
    private List<AttachmentRespVO> attachments;
}
