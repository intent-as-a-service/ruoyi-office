package cn.iocoder.yudao.module.asset.controller.admin.goods;

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

import cn.iocoder.yudao.module.asset.controller.admin.goods.vo.*;
import cn.iocoder.yudao.module.asset.dal.dataobject.goods.AssetGoodsDO;
import cn.iocoder.yudao.module.asset.service.goods.AssetGoodsService;

@Tag(name = "管理后台 - 物品信息")
@RestController
@RequestMapping("/asset/goods")
@Validated
public class AssetGoodsController {

    @Resource
    private AssetGoodsService goodsService;

    @PostMapping("/create")
    @Operation(summary = "创建物品信息")
    @PreAuthorize("@ss.hasPermission('asset:goods:create')")
    public CommonResult<Long> createGoods(@Valid @RequestBody AssetGoodsSaveReqVO createReqVO) {
        return success(goodsService.createGoods(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新物品信息")
    @PreAuthorize("@ss.hasPermission('asset:goods:update')")
    public CommonResult<Boolean> updateGoods(@Valid @RequestBody AssetGoodsSaveReqVO updateReqVO) {
        goodsService.updateGoods(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除物品信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('asset:goods:delete')")
    public CommonResult<Boolean> deleteGoods(@RequestParam("id") Long id) {
        goodsService.deleteGoods(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除物品信息")
                @PreAuthorize("@ss.hasPermission('asset:goods:delete')")
    public CommonResult<Boolean> deleteGoodsList(@RequestParam("ids") List<Long> ids) {
        goodsService.deleteGoodsListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得物品信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('asset:goods:query')")
    public CommonResult<AssetGoodsRespVO> getGoods(@RequestParam("id") Long id) {
        AssetGoodsDO goods = goodsService.getGoods(id);
        return success(BeanUtils.toBean(goods, AssetGoodsRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得物品信息分页")
    @PreAuthorize("@ss.hasPermission('asset:goods:query')")
    public CommonResult<PageResult<AssetGoodsRespVO>> getGoodsPage(@Valid AssetGoodsPageReqVO pageReqVO) {
        PageResult<AssetGoodsDO> pageResult = goodsService.getGoodsPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AssetGoodsRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出物品信息 Excel")
    @PreAuthorize("@ss.hasPermission('asset:goods:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportGoodsExcel(@Valid AssetGoodsPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AssetGoodsDO> list = goodsService.getGoodsPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "物品信息.xls", "数据", AssetGoodsRespVO.class,
                        BeanUtils.toBean(list, AssetGoodsRespVO.class));
    }

}