package cn.iocoder.yudao.module.hrm.controller.admin.employee;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.hrm.controller.admin.employee.vo.*;
import cn.iocoder.yudao.module.hrm.service.employee.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 员工档案管理")
@RestController
@RequestMapping("/hrm/employee-archive")
@Validated
public class EmployeeController {

    @Resource
    private EmployeeService employeeArchiveService;

    @PostMapping("/create")
    @Operation(summary = "创建员工档案")
    @PreAuthorize("@ss.hasPermission('hrm:employee-archive:create')")
    public CommonResult<Long> createEmployeeArchive(@Valid @RequestBody EmployeeSaveReqVO createReqVO) {
        return success(employeeArchiveService.createEmployeeArchive(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新员工档案")
    @PreAuthorize("@ss.hasPermission('hrm:employee-archive:update')")
    public CommonResult<Boolean> updateEmployeeArchive(@Valid @RequestBody EmployeeSaveReqVO updateReqVO) {
        employeeArchiveService.updateEmployeeArchive(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除员工档案")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('hrm:employee-archive:delete')")
    public CommonResult<Boolean> deleteEmployeeArchive(@RequestParam("id") Long id) {
        employeeArchiveService.deleteEmployeeArchive(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除员工档案")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "[1,2,3]")
    @PreAuthorize("@ss.hasPermission('hrm:employee-archive:delete')")
    public CommonResult<Boolean> deleteEmployeeArchiveList(@RequestParam("ids") List<Long> ids) {
        employeeArchiveService.deleteEmployeeArchiveList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得员工档案")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('hrm:employee-archive:query')")
    public CommonResult<EmployeeRespVO> getEmployeeArchive(@RequestParam("id") Long id) {
        EmployeeRespVO archive = employeeArchiveService.getEmployeeArchive(id);
        return success(archive);
    }

    @GetMapping("/page")
    @Operation(summary = "获得员工档案分页")
    @PreAuthorize("@ss.hasPermission('hrm:employee-archive:query')")
    public CommonResult<PageResult<EmployeeRespVO>> getEmployeeArchivePage(@Valid EmployeePageReqVO pageReqVO) {
        PageResult<EmployeeRespVO> pageResult = employeeArchiveService.getEmployeeArchivePage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/select-page")
    @Operation(summary = "员工档案选择分页（过滤正式员工）")
    @PreAuthorize("@ss.hasPermission('hrm:employee-archive:query')")
    public CommonResult<PageResult<EmployeeRespVO>> getEmployeeArchiveSelectablePage(@Valid EmployeeSelectPageReqVO pageReqVO) {
        PageResult<EmployeeRespVO> pageResult = employeeArchiveService.getEmployeeArchiveSelectablePage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出员工档案 Excel")
    @PreAuthorize("@ss.hasPermission('hrm:employee-archive:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportEmployeeArchiveExcel(@Valid EmployeePageReqVO pageReqVO,
                                           HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<EmployeeRespVO> list = employeeArchiveService.getEmployeeArchivePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "员工档案.xls", "数据", EmployeeRespVO.class, list);
    }

    @PostMapping("/generate-user")
    @Operation(summary = "为员工生成系统用户")
    @Parameter(name = "id", description = "员工编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('hrm:employee-archive:create')")
    public CommonResult<Long> generateUserForEmployee(@RequestParam("id") Long id) {
        Long userId = employeeArchiveService.generateUserForEmployee(id);
        return success(userId);
    }

    @PostMapping("/batch-generate-user")
    @Operation(summary = "批量为员工生成系统用户")
    @Parameter(name = "ids", description = "员工编号列表", required = true, example = "[1,2,3]")
    @PreAuthorize("@ss.hasPermission('hrm:employee-archive:create')")
    public CommonResult<Boolean> batchGenerateUserForEmployee(@RequestParam("ids") String ids) {
        List<Long> idList = List.of(ids.split(",")).stream()
                .map(Long::valueOf)
                .collect(java.util.stream.Collectors.toList());
        employeeArchiveService.batchGenerateUserForEmployee(idList);
        return success(true);
    }

}

