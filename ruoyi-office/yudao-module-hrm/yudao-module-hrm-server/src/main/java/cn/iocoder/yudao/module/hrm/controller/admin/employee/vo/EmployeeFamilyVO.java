package cn.iocoder.yudao.module.hrm.controller.admin.employee.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - 员工家属信息 VO")
@Data
public class EmployeeFamilyVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotBlank(message = "姓名不能为空")
    private String name;

    @Schema(description = "关系", example = "配偶")
    private String relationship;

    @Schema(description = "联系电话", example = "13900139000")
    private String mobile;

    @Schema(description = "工作单位", example = "腾讯科技有限公司")
    private String workUnit;

}

