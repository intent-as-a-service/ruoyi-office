package cn.iocoder.yudao.module.system.controller.admin.user.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 用户精简信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSimpleRespVO {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "宇擎")
    private String nickname;

    @Schema(description = "部门ID", example = "我是一个用户")
    private Long deptId;

    @Schema(description = "部门名称", example = "IT 部")
    private String deptName;

    @Schema(description = "手机号码", example = "15601691300")
    private String mobile;

    @Schema(description = "邮箱", example = "yudao@iocoder.cn")
    private String email;

    @Schema(description = "用户名称", example = "芋艿")
    private String username;

}
