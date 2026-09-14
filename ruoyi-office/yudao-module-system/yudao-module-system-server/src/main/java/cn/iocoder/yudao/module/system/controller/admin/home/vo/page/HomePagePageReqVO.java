package cn.iocoder.yudao.module.system.controller.admin.home.vo.page;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 首页分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class HomePagePageReqVO extends PageParam {

    @Schema(description = "首页名称，模糊匹配", example = "销售")
    private String name;

    @Schema(description = "首页编码，模糊匹配", example = "sales")
    private String code;

    @Schema(description = "展示状态，参见 CommonStatusEnum 枚举类", example = "1")
    private Integer status;

}
