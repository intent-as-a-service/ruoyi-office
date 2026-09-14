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
import cn.iocoder.yudao.module.oa.dal.dataobject.car.CarDO;
import cn.iocoder.yudao.module.oa.service.car.CarService;

@Tag(name = "OA协同办公 - 车辆信息")
@RestController
@RequestMapping("/oa/car")
@Validated
public class CarController {

    @Resource
    private CarService carService;

    @PostMapping("/create")
    @Operation(summary = "创建车辆信息")
    @PreAuthorize("@ss.hasPermission('oa:car:create')")
    public CommonResult<Long> createCar(@Valid @RequestBody CarSaveReqVO createReqVO) {
        return success(carService.createCar(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新车辆信息")
    @PreAuthorize("@ss.hasPermission('oa:car:update')")
    public CommonResult<Boolean> updateCar(@Valid @RequestBody CarSaveReqVO updateReqVO) {
        carService.updateCar(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除车辆信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:car:delete')")
    public CommonResult<Boolean> deleteCar(@RequestParam("id") Long id) {
        carService.deleteCar(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除车辆信息")
                @PreAuthorize("@ss.hasPermission('oa:car:delete')")
    public CommonResult<Boolean> deleteCarList(@RequestParam("ids") List<Long> ids) {
        carService.deleteCarListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得车辆信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:car:query')")
    public CommonResult<CarRespVO> getCar(@RequestParam("id") Long id) {
        CarDO car = carService.getCar(id);
        return success(BeanUtils.toBean(car, CarRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得车辆信息分页")
    @PreAuthorize("@ss.hasPermission('oa:car:query')")
    public CommonResult<PageResult<CarRespVO>> getCarPage(@Valid CarPageReqVO pageReqVO) {
        PageResult<CarDO> pageResult = carService.getCarPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CarRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出车辆信息 Excel")
    @PreAuthorize("@ss.hasPermission('oa:car:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCarExcel(@Valid CarPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CarDO> list = carService.getCarPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "车辆信息.xls", "数据", CarRespVO.class,
                        BeanUtils.toBean(list, CarRespVO.class));
    }

}