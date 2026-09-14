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
import cn.iocoder.yudao.module.hrm.dal.dataobject.employee.EmployeeTransferBillDO;
import cn.iocoder.yudao.module.hrm.service.employee.EmployeeTransferBillService;

@Tag(name = "管理后台 - 人事调动申请单")
@RestController
@RequestMapping("/hrm/employee-transfer-bill")
@Validated
public class EmployeeTransferBillController {

    @Resource
    private EmployeeTransferBillService employeeTransferBillService;

    @PostMapping("/create")
    @Operation(summary = "创建人事调动申请单")
    @PreAuthorize("@ss.hasPermission('hrm:employee-transfer-bill:create')")
    public CommonResult<Long> createEmployeeTransferBill(@Valid @RequestBody EmployeeTransferBillSaveReqVO createReqVO) {
        return success(employeeTransferBillService.createEmployeeTransferBill(createReqVO));
    }

    @PostMapping("/save")
    @Operation(summary = "保存人事调动申请单")
    @PreAuthorize("@ss.hasPermission('hrm:employee-transfer-bill:create')")
    public CommonResult<Long> saveEmployeeTransferBill(@Valid @RequestBody EmployeeTransferBillSaveReqVO saveReqVO) {
        return success(employeeTransferBillService.saveEmployeeTransferBill(saveReqVO));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交人事调动申请单")
    @PreAuthorize("@ss.hasPermission('hrm:employee-transfer-bill:create')")
    public CommonResult<Long> submitEmployeeTransferBill(@Valid @RequestBody EmployeeTransferBillSaveReqVO submitReqVO) {
        return success(employeeTransferBillService.submitEmployeeTransferBill(submitReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新人事调动申请单")
    @PreAuthorize("@ss.hasPermission('hrm:employee-transfer-bill:update')")
    public CommonResult<Boolean> updateEmployeeTransferBill(@Valid @RequestBody EmployeeTransferBillSaveReqVO updateReqVO) {
        employeeTransferBillService.updateEmployeeTransferBill(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除人事调动申请单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('hrm:employee-transfer-bill:delete')")
    public CommonResult<Boolean> deleteEmployeeTransferBill(@RequestParam("id") Long id) {
        employeeTransferBillService.deleteEmployeeTransferBill(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除人事调动申请单")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('hrm:employee-transfer-bill:delete')")
    public CommonResult<Boolean> deleteEmployeeTransferBillList(@RequestParam("ids") List<Long> ids) {
        employeeTransferBillService.deleteEmployeeTransferBillListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得人事调动申请单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('hrm:employee-transfer-bill:query')")
    public CommonResult<EmployeeTransferBillRespVO> getEmployeeTransferBill(@RequestParam("id") Long id) {
        EmployeeTransferBillRespVO respVO = employeeTransferBillService.getEmployeeTransferBillInfo(id);
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得人事调动申请单分页")
    @PreAuthorize("@ss.hasPermission('hrm:employee-transfer-bill:query')")
    public CommonResult<PageResult<EmployeeTransferBillRespVO>> getEmployeeTransferBillPage(@Valid EmployeeTransferBillPageReqVO pageReqVO) {
        PageResult<EmployeeTransferBillDO> pageResult = employeeTransferBillService.getEmployeeTransferBillPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, EmployeeTransferBillRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出人事调动申请单 Excel")
    @PreAuthorize("@ss.hasPermission('hrm:employee-transfer-bill:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportEmployeeTransferBillExcel(@Valid EmployeeTransferBillPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<EmployeeTransferBillDO> list = employeeTransferBillService.getEmployeeTransferBillPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "人事调动申请单.xls", "数据", EmployeeTransferBillRespVO.class,
                        BeanUtils.toBean(list, EmployeeTransferBillRespVO.class));
    }

}




