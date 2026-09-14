package cn.iocoder.yudao.module.bpm.controller.admin.task.vo.task;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.date.DateUtils;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.bpm.enums.task.BpmTaskStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 流程任务的的分页 Request VO") // 待办、已办，都使用该分页
@Data
public class BpmTaskPageReqVO extends PageParam {

    @Schema(description = "流程任务名", example = "宇擎")
    private String name;

    @Schema(description = "流程分类", example = "1")
    private String category;

    @Schema(description = "流程定义的标识", example = "2048")
    private String processDefinitionKey; // 精准匹配

    @Schema(description = "审批状态", example = "1")
    @InEnum(BpmTaskStatusEnum.class)
    private Integer status; // 仅【已办】使用

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    // ========== 流程变量搜索字段 ==========
    
    @Schema(description = "单据类型（流程变量）", example = "OA用车申请单")
    private String billType;

    @Schema(description = "单据编号（流程变量）", example = "YC20250101001")
    private String billCode;

    @Schema(description = "单据日期（流程变量）")
    @DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] billCreateTime;

    @Schema(description = "接收时间（任务创建时间）")
    @DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] receiveTime;

    @Schema(description = "所属公司ID（流程变量）", example = "1")
    private Long companyId;

    @Schema(description = "所属部门ID（流程变量）", example = "1")
    private Long deptId;

}
