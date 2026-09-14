package cn.iocoder.yudao.module.system.controller.admin.home.vo.component;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 首页组件分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class HomeComponentPageReqVO extends PageParam {

    @Schema(description = "组件名称，模糊匹配", example = "访问统计")
    private String name;

    @Schema(description = "组件编码，模糊匹配", example = "analytics")
    private String code;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "展示状态，参见 CommonStatusEnum 枚举类", example = "1")
    private Integer status;

}
