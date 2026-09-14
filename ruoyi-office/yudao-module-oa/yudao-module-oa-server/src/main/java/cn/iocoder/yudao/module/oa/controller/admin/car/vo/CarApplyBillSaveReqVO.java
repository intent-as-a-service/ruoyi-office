package cn.iocoder.yudao.module.oa.controller.admin.car.vo;

import cn.iocoder.yudao.common.server.attachment.controller.vo.AttachmentSaveReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用车申请单新增/修改 Request VO")
@Data
public class CarApplyBillSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1882")
    private Long id;

    @Schema(description = "单据编号", requiredMode = Schema.RequiredMode.AUTO)
    private String billCode;

    @Schema(description = "流程实例编号", example = "16629")
    private String processInstanceId;

    @Schema(description = "单据状态", example = "2")
    private Integer processStatus;

    @Schema(description = "车辆", example = "3524")
    private Long carId;

    @Schema(description = "车牌号", example = "3524")
    private String carNo;

    @Schema(description = "出车时间")
    private LocalDateTime goTime;

    @Schema(description = "回车时间")
    private LocalDateTime returnTime;

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

    @Schema(description = "备注", example = "你猜")
    private String remark;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "创建者姓名", example = "芋艿")
    private String creatorName;

    @Schema(description = "部门ID", example = "12287")
    private Long deptId;

    @Schema(description = "部门名称", example = "张三")
    private String deptName;

    @Schema(description = "公司ID", example = "1109")
    private Long companyId;

    @Schema(description = "公司名称", example = "张三")
    private String companyName;

    @Schema(description = "附件列表")
    private List<AttachmentSaveReqVO> attachments;

}