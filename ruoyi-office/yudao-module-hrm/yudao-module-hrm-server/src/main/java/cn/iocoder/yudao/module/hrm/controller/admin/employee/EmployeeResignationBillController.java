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
import cn.iocoder.yudao.module.hrm.dal.dataobject.employee.EmployeeResignationBillDO;
import cn.iocoder.yudao.module.hrm.service.employee.EmployeeResignationBillService;

@Tag(name = "管理后台 - 员工离职申请单")
@RestController
@RequestMapping("/hrm/employee-resignation-bill")
@Validated
public class EmployeeResignationBillController {

    @Resource
    private EmployeeResignationBillService employeeResignationBillService;

    @PostMapping("/create")
    @Operation(summary = "创建员工离职申请单")
    @PreAuthorize("@ss.hasPermission('hrm:employee-resignation-bill:create')")
    public CommonResult<Long> createEmployeeResignationBill(@Valid @RequestBody EmployeeResignationBillSaveReqVO createReqVO) {
        return success(employeeResignationBillService.createEmployeeResignationBill(createReqVO));
    }

    @PostMapping("/save")
    @Operation(summary = "保存员工离职申请单")
    @PreAuthorize("@ss.hasPermission('hrm:employee-resignation-bill:create')")
    public CommonResult<Long> saveEmployeeResignationBill(@Valid @RequestBody EmployeeResignationBillSaveReqVO saveReqVO) {
        return success(employeeResignationBillService.saveEmployeeResignationBill(saveReqVO));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交员工离职申请单")
    @PreAuthorize("@ss.hasPermission('hrm:employee-resignation-bill:create')")
    public CommonResult<Long> submitEmployeeResignationBill(@Valid @RequestBody EmployeeResignationBillSaveReqVO submitReqVO) {
        return success(employeeResignationBillService.submitEmployeeResignationBill(submitReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新员工离职申请单")
    @PreAuthorize("@ss.hasPermission('hrm:employee-resignation-bill:update')")
    public CommonResult<Boolean> updateEmployeeResignationBill(@Valid @RequestBody EmployeeResignationBillSaveReqVO updateReqVO) {
        employeeResignationBillService.updateEmployeeResignationBill(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除员工离职申请单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('hrm:employee-resignation-bill:delete')")
    public CommonResult<Boolean> deleteEmployeeResignationBill(@RequestParam("id") Long id) {
        employeeResignationBillService.deleteEmployeeResignationBill(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除员工离职申请单")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('hrm:employee-resignation-bill:delete')")
    public CommonResult<Boolean> deleteEmployeeResignationBillList(@RequestParam("ids") List<Long> ids) {
        employeeResignationBillService.deleteEmployeeResignationBillListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得员工离职申请单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('hrm:employee-resignation-bill:query')")
    public CommonResult<EmployeeResignationBillRespVO> getEmployeeResignationBill(@RequestParam("id") Long id) {
        EmployeeResignationBillRespVO respVO = employeeResignationBillService.getEmployeeResignationBillInfo(id);
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得员工离职申请单分页")
    @PreAuthorize("@ss.hasPermission('hrm:employee-resignation-bill:query')")
    public CommonResult<PageResult<EmployeeResignationBillRespVO>> getEmployeeResignationBillPage(@Valid EmployeeResignationBillPageReqVO pageReqVO) {
        PageResult<EmployeeResignationBillDO> pageResult = employeeResignationBillService.getEmployeeResignationBillPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, EmployeeResignationBillRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出员工离职申请单 Excel")
    @PreAuthorize("@ss.hasPermission('hrm:employee-resignation-bill:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportEmployeeResignationBillExcel(@Valid EmployeeResignationBillPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<EmployeeResignationBillDO> list = employeeResignationBillService.getEmployeeResignationBillPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "员工离职申请单.xls", "数据", EmployeeResignationBillRespVO.class,
                        BeanUtils.toBean(list, EmployeeResignationBillRespVO.class));
    }

}

