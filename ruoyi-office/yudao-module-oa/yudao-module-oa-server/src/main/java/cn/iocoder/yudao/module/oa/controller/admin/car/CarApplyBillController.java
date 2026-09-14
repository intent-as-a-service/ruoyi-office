package cn.iocoder.yudao.module.oa.controller.admin.car;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

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

import cn.iocoder.yudao.module.oa.controller.admin.car.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.car.CarApplyBillDO;
import cn.iocoder.yudao.module.oa.service.car.CarApplyBillService;

@Tag(name = "OA协同办公 - 用车申请单")
@RestController
@RequestMapping("/oa/car-apply-bill")
@Validated
public class CarApplyBillController {

    @Resource
    private CarApplyBillService carApplyBillService;


    @PostMapping("/save")
    @Operation(summary = "保存用车申请单")
    @PreAuthorize("@ss.hasPermission('oa:car-apply-bill:save')")
    public CommonResult<Long> saveCarApplyBill(@Valid @RequestBody CarApplyBillSaveReqVO saveReqVO) {
        return success(carApplyBillService.saveCarApplyBill(saveReqVO));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交用车申请单")
    @PreAuthorize("@ss.hasPermission('oa:car-apply-bill:submit')")
    public CommonResult<Long> submitCarApplyBill(@Valid @RequestBody CarApplyBillSaveReqVO createReqVO) {
        return success(carApplyBillService.submitCarApplyBill(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用车申请单")
    @PreAuthorize("@ss.hasPermission('oa:car-apply-bill:update')")
    public CommonResult<Boolean> updateCarApplyBill(@Valid @RequestBody CarApplyBillSaveReqVO updateReqVO) {
        carApplyBillService.updateCarApplyBill(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用车申请单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:car-apply-bill:delete')")
    public CommonResult<Boolean> deleteCarApplyBill(@RequestParam("id") Long id) {
        carApplyBillService.deleteCarApplyBill(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除用车申请单")
                @PreAuthorize("@ss.hasPermission('oa:car-apply-bill:delete')")
    public CommonResult<Boolean> deleteCarApplyBillList(@RequestParam("ids") List<Long> ids) {
        carApplyBillService.deleteCarApplyBillListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用车申请单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:car-apply-bill:query')")
    public CommonResult<CarApplyBillRespVO> getCarApplyBill(@RequestParam("id") Long id) {
        CarApplyBillRespVO respVO = carApplyBillService.getCarApplyBillInfo(id);
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得用车申请单分页")
    @PreAuthorize("@ss.hasPermission('oa:car-apply-bill:query')")
    public CommonResult<PageResult<CarApplyBillRespVO>> getCarApplyBillPage(@Valid CarApplyBillPageReqVO pageReqVO) {
        PageResult<CarApplyBillDO> pageResult = carApplyBillService.getCarApplyBillPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CarApplyBillRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用车申请单 Excel")
    @PreAuthorize("@ss.hasPermission('oa:car-apply-bill:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCarApplyBillExcel(@Valid CarApplyBillPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CarApplyBillDO> list = carApplyBillService.getCarApplyBillPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用车申请单.xls", "数据", CarApplyBillRespVO.class,
                        BeanUtils.toBean(list, CarApplyBillRespVO.class));
    }

}