package cn.iocoder.yudao.module.oa.controller.admin.seal.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import cn.iocoder.yudao.common.server.attachment.controller.vo.AttachmentSaveReqVO;

@Schema(description = "管理后台 - 用印申请单新增/修改 Request VO")
@Data
public class SealApplyBillSaveReqVO {

    @Schema(description = "ID", example = "1024")
    private Long id;

    @Schema(description = "单据编号", example = "YZ202412010001")
    private String billCode;

    @Schema(description = "流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态", example = "1")
    private Integer processStatus;

    @Schema(description = "印章ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "印章ID不能为空")
    private Long sealId;

    @Schema(description = "印章编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "YZ001")
    @NotEmpty(message = "印章编号不能为空")
    private String sealNo;

    @Schema(description = "印章名称", example = "公司公章")
    private String sealName;

    @Schema(description = "印章类型", example = "1")
    private Integer sealType;

    @Schema(description = "保管人ID", example = "1")
    private Long keeperId;

    @Schema(description = "保管人名称", example = "张三")
    private String keeperName;

    @Schema(description = "保管部门ID", example = "1")
    private Long keeperDeptId;

    @Schema(description = "保管部门名称", example = "行政部")
    private String keeperDeptName;

    @Schema(description = "用章事由", requiredMode = Schema.RequiredMode.REQUIRED, example = "合同签署")
    @NotEmpty(message = "用章事由不能为空")
    private String cause;

    @Schema(description = "用章类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "用章类型不能为空")
    private Integer useType;

    @Schema(description = "用章方式", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "用章方式不能为空")
    private Integer useMode;

    @Schema(description = "文件标题", example = "销售合同")
    private String documentTitle;

    @Schema(description = "文件类型", example = "合同")
    private String documentType;

    @Schema(description = "文件份数", example = "3")
    private Integer documentCount;

    @Schema(description = "合同金额", example = "100000.00")
    private BigDecimal contractAmount;

    @Schema(description = "合同对方", example = "ABC公司")
    private String contractParty;

    @Schema(description = "预计用章时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expectedUseTime;

    @Schema(description = "实际用章时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualUseTime;

    @Schema(description = "预计归还时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expectedReturnTime;

    @Schema(description = "实际归还时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualReturnTime;

    @Schema(description = "用章状态", example = "0")
    private Integer useStatus;

    @Schema(description = "是否紧急", example = "0")
    private Integer isUrgent;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "创建者姓名", example = "芋艿")
    private String creatorName;

    @Schema(description = "公司ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "公司ID不能为空")
    private Long companyId;

    @Schema(description = "公司名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "宇擎科技")
    @NotEmpty(message = "公司名称不能为空")
    private String companyName;

    @Schema(description = "部门ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "部门ID不能为空")
    private Long deptId;

    @Schema(description = "部门名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "技术部")
    @NotEmpty(message = "部门名称不能为空")
    private String deptName;

    @Schema(description = "备注", example = "紧急用章")
    private String remark;

    @Schema(description = "附件列表")
    private List<AttachmentSaveReqVO> attachments;

}
