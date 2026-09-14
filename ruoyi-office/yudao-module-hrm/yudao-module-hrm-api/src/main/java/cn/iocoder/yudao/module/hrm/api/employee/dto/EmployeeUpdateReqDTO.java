package cn.iocoder.yudao.module.hrm.api.employee.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "RPC 服务 - 员工更新 Request DTO")
@Data
public class EmployeeUpdateReqDTO {

    @Schema(description = "员工姓名", example = "张三")
    private String name;

    @Schema(description = "手机号", example = "13800138000")
    private String mobile;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "性别（1:男 2:女）", example = "1")
    private Integer sex;

    @Schema(description = "头像", example = "http://127.0.0.1:48080/admin-api/infra/file/4/get/xxx.jpg")
    private String avatar;

    @Schema(description = "部门ID", example = "1")
    private Long deptId;

    @Schema(description = "备注", example = "优秀员工")
    private String remark;

}

