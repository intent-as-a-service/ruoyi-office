package cn.iocoder.yudao.module.asset.controller.admin.category;

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

import cn.iocoder.yudao.module.asset.controller.admin.category.vo.*;
import cn.iocoder.yudao.module.asset.dal.dataobject.category.CategoryDO;
import cn.iocoder.yudao.module.asset.service.category.CategoryService;

@Tag(name = "管理后台 - 资产类别")
@RestController
@RequestMapping("/asset/category")
@Validated
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @PostMapping("/create")
    @Operation(summary = "创建资产类别")
    @PreAuthorize("@ss.hasPermission('asset:category:create')")
    public CommonResult<Long> createCategory(@Valid @RequestBody CategorySaveReqVO createReqVO) {
        return success(categoryService.createCategory(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新资产类别")
    @PreAuthorize("@ss.hasPermission('asset:category:update')")
    public CommonResult<Boolean> updateCategory(@Valid @RequestBody CategorySaveReqVO updateReqVO) {
        categoryService.updateCategory(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除资产类别")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('asset:category:delete')")
    public CommonResult<Boolean> deleteCategory(@RequestParam("id") Long id) {
        categoryService.deleteCategory(id);
        return success(true);
    }


    @GetMapping("/get")
    @Operation(summary = "获得资产类别")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('asset:category:query')")
    public CommonResult<CategoryRespVO> getCategory(@RequestParam("id") Long id) {
        CategoryDO category = categoryService.getCategory(id);
        return success(BeanUtils.toBean(category, CategoryRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得资产类别列表")
    @PreAuthorize("@ss.hasPermission('asset:category:query')")
    public CommonResult<List<CategoryRespVO>> getCategoryList(@Valid CategoryListReqVO listReqVO) {
        List<CategoryDO> list = categoryService.getCategoryList(listReqVO);
        return success(BeanUtils.toBean(list, CategoryRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出资产类别 Excel")
    @PreAuthorize("@ss.hasPermission('asset:category:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCategoryExcel(@Valid CategoryListReqVO listReqVO,
              HttpServletResponse response) throws IOException {
        List<CategoryDO> list = categoryService.getCategoryList(listReqVO);
        // 导出 Excel
        ExcelUtils.write(response, "资产类别.xls", "数据", CategoryRespVO.class,
                        BeanUtils.toBean(list, CategoryRespVO.class));
    }

}