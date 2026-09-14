package cn.iocoder.yudao.module.hrm.api.employee;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.hrm.api.employee.dto.EmployeeUpdateReqDTO;
import cn.iocoder.yudao.module.hrm.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import static cn.iocoder.yudao.module.hrm.api.employee.EmployeeApi.PREFIX;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 员工档案")
public interface EmployeeApi {

    String PREFIX = ApiConstants.PREFIX + "/employee";

    @PutMapping(PREFIX + "/update-by-user-id")
    @Operation(summary = "根据用户ID更新员工信息")
    @Parameter(name = "userId", description = "用户编号", example = "1", required = true)
    CommonResult<Boolean> updateEmployeeByUserId(@RequestParam("userId") Long userId, @RequestBody EmployeeUpdateReqDTO updateReqDTO);

    @PutMapping(PREFIX + "/update-user-generated-status")
    @Operation(summary = "更新员工的用户生成标记")
    @Parameter(name = "userId", description = "用户编号", example = "1", required = true)
    CommonResult<Boolean> updateUserGeneratedStatus(@RequestParam("userId") Long userId, @RequestParam("userGenerated") Boolean userGenerated);

}

