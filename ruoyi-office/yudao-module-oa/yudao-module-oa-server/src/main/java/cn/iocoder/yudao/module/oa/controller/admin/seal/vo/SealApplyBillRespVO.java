package cn.iocoder.yudao.module.oa.controller.admin.seal.vo;

import cn.iocoder.yudao.common.server.attachment.controller.vo.AttachmentRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import java.util.List;

@Schema(description = "管理后台 - 用印申请单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SealApplyBillRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1882")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "单据编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("单据编号")
    private String billCode;

    @Schema(description = "流程实例编号", example = "16629")
    @ExcelProperty("流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态", example = "2")
    @ExcelProperty("单据状态")
    private Integer processStatus;

    @Schema(description = "印章ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("印章ID")
    private Long sealId;

    @Schema(description = "印章编号", example = "SEAL001")
    @ExcelProperty("印章编号")
    private String sealNo;

    @Schema(description = "印章名称", example = "公司公章")
    @ExcelProperty("印章名称")
    private String sealName;

    @Schema(description = "印章类型", example = "1")
    @ExcelProperty("印章类型")
    private Integer sealType;

    @Schema(description = "保管人ID", example = "1")
    @ExcelProperty("保管人ID")
    private Long keeperId;

    @Schema(description = "保管人姓名", example = "张三")
    @ExcelProperty("保管人姓名")
    private String keeperName;

    @Schema(description = "保管人部门ID", example = "1")
    @ExcelProperty("保管人部门ID")
    private Long keeperDeptId;

    @Schema(description = "保管人部门名称", example = "行政部")
    @ExcelProperty("保管人部门名称")
    private String keeperDeptName;

    @Schema(description = "用印事由", requiredMode = Schema.RequiredMode.REQUIRED, example = "合同签署")
    @ExcelProperty("用印事由")
    private String cause;

    @Schema(description = "用印类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("用印类型")
    private Integer useType;

    @Schema(description = "用印方式", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("用印方式")
    private Integer useMode;

    @Schema(description = "文件标题", example = "销售合同")
    @ExcelProperty("文件标题")
    private String documentTitle;

    @Schema(description = "文件份数", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    @ExcelProperty("文件份数")
    private Integer documentCount;

    @Schema(description = "合同金额", example = "100000.00")
    @ExcelProperty("合同金额")
    private java.math.BigDecimal contractAmount;

    @Schema(description = "预计用印时间")
    @ExcelProperty("预计用印时间")
    private LocalDateTime expectedUseTime;

    @Schema(description = "借用时间")
    @ExcelProperty("借用时间")
    private LocalDateTime borrowTime;

    @Schema(description = "预计归还时间")
    @ExcelProperty("预计归还时间")
    private LocalDateTime expectedReturnTime;

    @Schema(description = "实际归还时间")
    @ExcelProperty("实际归还时间")
    private LocalDateTime actualReturnTime;

    @Schema(description = "归还接收人ID", example = "1")
    @ExcelProperty("归还接收人ID")
    private Long returnReceiverId;

    @Schema(description = "用印状态", example = "1")
    @ExcelProperty("用印状态")
    private Integer useStatus;

    @Schema(description = "是否紧急", example = "0")
    @ExcelProperty("是否紧急")
    private Integer isUrgent;

    @Schema(description = "申请人ID", example = "1")
    @ExcelProperty("申请人ID")
    private Long applyerId;

    @Schema(description = "附件地址", example = "http://www.iocoder.cn/file.doc")
    @ExcelProperty("附件地址")
    private String attachmentUrls;

    @Schema(description = "创建者", example = "1")
    @ExcelProperty("创建者")
    private String creator;

    @Schema(description = "创建者姓名", example = "芋艿")
    @ExcelProperty("创建者姓名")
    private String creatorName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "部门ID", example = "12287")
    @ExcelProperty("部门ID")
    private Long deptId;

    @Schema(description = "部门名称", example = "技术部")
    @ExcelProperty("部门名称")
    private String deptName;

    @Schema(description = "公司ID", example = "1109")
    @ExcelProperty("公司ID")
    private Long companyId;

    @Schema(description = "公司名称", example = "宇擎科技")
    @ExcelProperty("公司名称")
    private String companyName;

    @Schema(description = "备注", example = "紧急用章")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "附件列表")
    private List<AttachmentRespVO> attachments;

}
