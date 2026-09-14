package cn.iocoder.yudao.module.system.controller.admin.notice.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 通知公告分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class NoticePageReqVO extends PageParam {

    @Schema(description = "通知公告名称，模糊匹配", example = "宇擎")
    private String title;

    @Schema(description = "公告类型，字典类型：system_notice_type", example = "1")
    private Integer type;

    @Schema(description = "展示状态，参见 CommonStatusEnum 枚举类", example = "1")
    private Integer status;

    @Schema(description = "是否重要通知", example = "true")
    private Boolean isImportant;

}
