package cn.iocoder.yudao.module.oa.controller.admin.car;

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

import cn.iocoder.yudao.module.oa.controller.admin.car.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.car.CarReturnBillDO;
import cn.iocoder.yudao.module.oa.service.car.CarReturnBillService;

@Tag(name = "管理后台 - 还车申请单")
@RestController
@RequestMapping("/oa/car-return-bill")
@Validated
public class CarReturnBillController {

    @Resource
    private CarReturnBillService carReturnBillService;

    @PostMapping("/save")
    @Operation(summary = "保存还车申请单")
    @PreAuthorize("@ss.hasPermission('oa:car-return-bill:save')")
    public CommonResult<Long> saveCarReturnBill(@Valid @RequestBody CarReturnBillSaveReqVO saveReqVO) {
        return success(carReturnBillService.saveCarReturnBill(saveReqVO));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交还车申请单")
    @PreAuthorize("@ss.hasPermission('oa:car-return-bill:submit')")
    public CommonResult<Long> submitCarReturnBill(@Valid @RequestBody CarReturnBillSaveReqVO createReqVO) {
        return success(carReturnBillService.submitCarReturnBill(createReqVO));
    }

    @PostMapping("/create")
    @Operation(summary = "创建还车申请单")
    @PreAuthorize("@ss.hasPermission('oa:car-return-bill:create')")
    public CommonResult<Long> createCarReturnBill(@Valid @RequestBody CarReturnBillSaveReqVO createReqVO) {
        return success(carReturnBillService.createCarReturnBill(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新还车申请单")
    @PreAuthorize("@ss.hasPermission('oa:car-return-bill:update')")
    public CommonResult<Boolean> updateCarReturnBill(@Valid @RequestBody CarReturnBillSaveReqVO updateReqVO) {
        carReturnBillService.updateCarReturnBill(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除还车申请单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:car-return-bill:delete')")
    public CommonResult<Boolean> deleteCarReturnBill(@RequestParam("id") Long id) {
        carReturnBillService.deleteCarReturnBill(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除还车申请单")
                @PreAuthorize("@ss.hasPermission('oa:car-return-bill:delete')")
    public CommonResult<Boolean> deleteCarReturnBillList(@RequestParam("ids") List<Long> ids) {
        carReturnBillService.deleteCarReturnBillListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得还车申请单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:car-return-bill:query')")
    public CommonResult<CarReturnBillRespVO> getCarReturnBill(@RequestParam("id") Long id) {
        CarReturnBillRespVO respVO = carReturnBillService.getCarReturnBillInfo(id);
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得还车申请单分页")
    @PreAuthorize("@ss.hasPermission('oa:car-return-bill:query')")
    public CommonResult<PageResult<CarReturnBillRespVO>> getCarReturnBillPage(@Valid CarReturnBillPageReqVO pageReqVO) {
        PageResult<CarReturnBillDO> pageResult = carReturnBillService.getCarReturnBillPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CarReturnBillRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出还车申请单 Excel")
    @PreAuthorize("@ss.hasPermission('oa:car-return-bill:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCarReturnBillExcel(@Valid CarReturnBillPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CarReturnBillDO> list = carReturnBillService.getCarReturnBillPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "还车申请单.xls", "数据", CarReturnBillRespVO.class,
                        BeanUtils.toBean(list, CarReturnBillRespVO.class));
    }

}