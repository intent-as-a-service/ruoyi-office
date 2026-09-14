package cn.iocoder.yudao.module.hrm.controller.admin.employee;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.hrm.controller.admin.employee.vo.*;
import cn.iocoder.yudao.module.hrm.dal.dataobject.employee.EmployeeEntryBillDO;
import cn.iocoder.yudao.module.hrm.service.employee.EmployeeEntryBillService;

@Tag(name = "管理后台 - 员工入职申请单")
@RestController
@RequestMapping("/hrm/employee-entry-bill")
@Validated
public class EmployeeEntryBillController {

    @Resource
    private EmployeeEntryBillService employeeEntryBillService;

    @PostMapping("/create")
    @Operation(summary = "创建员工入职申请单")
    @PreAuthorize("@ss.hasPermission('hrm:employee-entry-bill:create')")
    public CommonResult<Long> createEmployeeEntryBill(@Valid @RequestBody EmployeeEntryBillSaveReqVO createReqVO) {
        return success(employeeEntryBillService.createEmployeeEntryBill(createReqVO));
    }

    @PostMapping("/save")
    @Operation(summary = "保存员工入职申请单")
    @PreAuthorize("@ss.hasPermission('hrm:employee-entry-bill:create')")
    public CommonResult<Long> saveEmployeeEntryBill(@Valid @RequestBody EmployeeEntryBillSaveReqVO saveReqVO) {
        return success(employeeEntryBillService.saveEmployeeEntryBill(saveReqVO));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交员工入职申请单")
    @PreAuthorize("@ss.hasPermission('hrm:employee-entry-bill:create')")
    public CommonResult<Long> submitEmployeeEntryBill(@Valid @RequestBody EmployeeEntryBillSaveReqVO submitReqVO) {
        return success(employeeEntryBillService.submitEmployeeEntryBill(submitReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新员工入职申请单")
    @PreAuthorize("@ss.hasPermission('hrm:employee-entry-bill:update')")
    public CommonResult<Boolean> updateEmployeeEntryBill(@Valid @RequestBody EmployeeEntryBillSaveReqVO updateReqVO) {
        employeeEntryBillService.updateEmployeeEntryBill(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除员工入职申请单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('hrm:employee-entry-bill:delete')")
    public CommonResult<Boolean> deleteEmployeeEntryBill(@RequestParam("id") Long id) {
        employeeEntryBillService.deleteEmployeeEntryBill(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除员工入职申请单")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('hrm:employee-entry-bill:delete')")
    public CommonResult<Boolean> deleteEmployeeEntryBillList(@RequestParam("ids") List<Long> ids) {
        employeeEntryBillService.deleteEmployeeEntryBillListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得员工入职申请单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('hrm:employee-entry-bill:query')")
    public CommonResult<EmployeeEntryBillRespVO> getEmployeeEntryBill(@RequestParam("id") Long id) {
        EmployeeEntryBillRespVO respVO = employeeEntryBillService.getEmployeeEntryBillInfo(id);
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得员工入职申请单分页")
    @PreAuthorize("@ss.hasPermission('hrm:employee-entry-bill:query')")
    public CommonResult<PageResult<EmployeeEntryBillRespVO>> getEmployeeEntryBillPage(@Valid EmployeeEntryBillPageReqVO pageReqVO) {
        PageResult<EmployeeEntryBillDO> pageResult = employeeEntryBillService.getEmployeeEntryBillPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, EmployeeEntryBillRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出员工入职申请单 Excel")
    @PreAuthorize("@ss.hasPermission('hrm:employee-entry-bill:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportEmployeeEntryBillExcel(@Valid EmployeeEntryBillPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<EmployeeEntryBillDO> list = employeeEntryBillService.getEmployeeEntryBillPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "员工入职申请单.xls", "数据", EmployeeEntryBillRespVO.class,
                        BeanUtils.toBean(list, EmployeeEntryBillRespVO.class));
    }

}


