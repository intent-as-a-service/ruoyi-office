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
import cn.iocoder.yudao.module.hrm.dal.dataobject.employee.EmployeeRegularBillDO;
import cn.iocoder.yudao.module.hrm.service.employee.EmployeeRegularBillService;

@Tag(name = "管理后台 - 员工转正申请单")
@RestController
@RequestMapping("/hrm/employee-regular-bill")
@Validated
public class EmployeeRegularBillController {

    @Resource
    private EmployeeRegularBillService employeeRegularBillService;

    @PostMapping("/create")
    @Operation(summary = "创建员工转正申请单")
    @PreAuthorize("@ss.hasPermission('hrm:employee-regular-bill:create')")
    public CommonResult<Long> createEmployeeRegularBill(@Valid @RequestBody EmployeeRegularBillSaveReqVO createReqVO) {
        return success(employeeRegularBillService.createEmployeeRegularBill(createReqVO));
    }

    @PostMapping("/save")
    @Operation(summary = "保存员工转正申请单")
    @PreAuthorize("@ss.hasPermission('hrm:employee-regular-bill:create')")
    public CommonResult<Long> saveEmployeeRegularBill(@Valid @RequestBody EmployeeRegularBillSaveReqVO saveReqVO) {
        return success(employeeRegularBillService.saveEmployeeRegularBill(saveReqVO));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交员工转正申请单")
    @PreAuthorize("@ss.hasPermission('hrm:employee-regular-bill:create')")
    public CommonResult<Long> submitEmployeeRegularBill(@Valid @RequestBody EmployeeRegularBillSaveReqVO submitReqVO) {
        return success(employeeRegularBillService.submitEmployeeRegularBill(submitReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新员工转正申请单")
    @PreAuthorize("@ss.hasPermission('hrm:employee-regular-bill:update')")
    public CommonResult<Boolean> updateEmployeeRegularBill(@Valid @RequestBody EmployeeRegularBillSaveReqVO updateReqVO) {
        employeeRegularBillService.updateEmployeeRegularBill(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除员工转正申请单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('hrm:employee-regular-bill:delete')")
    public CommonResult<Boolean> deleteEmployeeRegularBill(@RequestParam("id") Long id) {
        employeeRegularBillService.deleteEmployeeRegularBill(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除员工转正申请单")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('hrm:employee-regular-bill:delete')")
    public CommonResult<Boolean> deleteEmployeeRegularBillList(@RequestParam("ids") List<Long> ids) {
        employeeRegularBillService.deleteEmployeeRegularBillListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得员工转正申请单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('hrm:employee-regular-bill:query')")
    public CommonResult<EmployeeRegularBillRespVO> getEmployeeRegularBill(@RequestParam("id") Long id) {
        EmployeeRegularBillRespVO respVO = employeeRegularBillService.getEmployeeRegularBillInfo(id);
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得员工转正申请单分页")
    @PreAuthorize("@ss.hasPermission('hrm:employee-regular-bill:query')")
    public CommonResult<PageResult<EmployeeRegularBillRespVO>> getEmployeeRegularBillPage(@Valid EmployeeRegularBillPageReqVO pageReqVO) {
        PageResult<EmployeeRegularBillDO> pageResult = employeeRegularBillService.getEmployeeRegularBillPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, EmployeeRegularBillRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出员工转正申请单 Excel")
    @PreAuthorize("@ss.hasPermission('hrm:employee-regular-bill:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportEmployeeRegularBillExcel(@Valid EmployeeRegularBillPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<EmployeeRegularBillDO> list = employeeRegularBillService.getEmployeeRegularBillPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "员工转正申请单.xls", "数据", EmployeeRegularBillRespVO.class,
                        BeanUtils.toBean(list, EmployeeRegularBillRespVO.class));
    }

}

