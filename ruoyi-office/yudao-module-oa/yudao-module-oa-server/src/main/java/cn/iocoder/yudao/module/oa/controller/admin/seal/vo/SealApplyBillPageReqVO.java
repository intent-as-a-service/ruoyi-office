package cn.iocoder.yudao.module.oa.controller.admin.seal.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用印申请单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SealApplyBillPageReqVO extends PageParam {

    @Schema(description = "单据编号", example = "YZ202412010001")
    private String billCode;

    @Schema(description = "单据状态", example = "1")
    private Integer processStatus;

    @Schema(description = "印章ID", example = "1")
    private Long sealId;

    @Schema(description = "印章编号", example = "YZ001")
    private String sealNo;

    @Schema(description = "印章名称", example = "公司公章")
    private String sealName;

    @Schema(description = "用章类型", example = "1")
    private Integer useType;

    @Schema(description = "用章方式", example = "1")
    private Integer useMode;

    @Schema(description = "用章状态", example = "0")
    private Integer useStatus;

    @Schema(description = "是否紧急", example = "0")
    private Integer isUrgent;

    @Schema(description = "公司ID", example = "1")
    private Long companyId;

    @Schema(description = "公司名称", example = "宇擎科技")
    private String companyName;

    @Schema(description = "部门ID", example = "1")
    private Long deptId;

    @Schema(description = "部门名称", example = "技术部")
    private String deptName;

    @Schema(description = "预计用章时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] expectedUseTime;

    @Schema(description = "创建人（用户ID）", example = "1")
    private String creator;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
