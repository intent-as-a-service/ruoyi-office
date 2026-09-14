package cn.iocoder.yudao.module.wms.controller.admin.goodscommonoperationorder;

import cn.iocoder.yudao.module.wms.dal.dataobject.goodswarehousingdetail.GoodsWarehousingDetailDO;
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

import cn.iocoder.yudao.module.wms.controller.admin.goodscommonoperationorder.vo.*;
import cn.iocoder.yudao.module.wms.dal.dataobject.goodscommonoperationorder.GoodsCommonOperationOrderDO;
import cn.iocoder.yudao.module.wms.service.goodscommonoperationorder.GoodsCommonOperationOrderService;

@Tag(name = "管理后台 - 领用、退库、归还、借用、调拨主")
@RestController
@RequestMapping("/wms/goods-common-operation-order")
@Validated
public class GoodsCommonOperationOrderController {

    @Resource
    private GoodsCommonOperationOrderService goodsCommonOperationOrderService;

    @PostMapping("/create")
    @Operation(summary = "创建领用、退库、归还、借用、调拨主")
    @PreAuthorize("@ss.hasPermission('wms:goods-common-operation-order:create')")
    public CommonResult<Long> createGoodsCommonOperationOrder(@Valid @RequestBody GoodsCommonOperationOrderSaveReqVO createReqVO) {
        return success(goodsCommonOperationOrderService.createGoodsCommonOperationOrder(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新领用、退库、归还、借用、调拨主")
    @PreAuthorize("@ss.hasPermission('wms:goods-common-operation-order:update')")
    public CommonResult<Boolean> updateGoodsCommonOperationOrder(@Valid @RequestBody GoodsCommonOperationOrderSaveReqVO updateReqVO) {
        goodsCommonOperationOrderService.updateGoodsCommonOperationOrder(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除领用、退库、归还、借用、调拨主")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('wms:goods-common-operation-order:delete')")
    public CommonResult<Boolean> deleteGoodsCommonOperationOrder(@RequestParam("id") Long id) {
        goodsCommonOperationOrderService.deleteGoodsCommonOperationOrder(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除领用、退库、归还、借用、调拨主")
                @PreAuthorize("@ss.hasPermission('wms:goods-common-operation-order:delete')")
    public CommonResult<Boolean> deleteGoodsCommonOperationOrderList(@RequestParam("ids") List<Long> ids) {
        goodsCommonOperationOrderService.deleteGoodsCommonOperationOrderListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得领用、退库、归还、借用、调拨主")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('wms:goods-common-operation-order:query')")
    public CommonResult<GoodsCommonOperationOrderRespVO> getGoodsCommonOperationOrder(@RequestParam("id") Long id) {
        GoodsCommonOperationOrderDO goodsCommonOperationOrder = goodsCommonOperationOrderService.getGoodsCommonOperationOrder(id);
        return success(BeanUtils.toBean(goodsCommonOperationOrder, GoodsCommonOperationOrderRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得领用、退库、归还、借用、调拨主分页")
    @PreAuthorize("@ss.hasPermission('wms:goods-common-operation-order:query')")
    public CommonResult<PageResult<GoodsCommonOperationOrderRespVO>> getGoodsCommonOperationOrderPage(@Valid GoodsCommonOperationOrderPageReqVO pageReqVO) {
        PageResult<GoodsCommonOperationOrderDO> pageResult = goodsCommonOperationOrderService.getGoodsCommonOperationOrderPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, GoodsCommonOperationOrderRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出领用、退库、归还、借用、调拨主 Excel")
    @PreAuthorize("@ss.hasPermission('wms:goods-common-operation-order:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportGoodsCommonOperationOrderExcel(@Valid GoodsCommonOperationOrderPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<GoodsCommonOperationOrderDO> list = goodsCommonOperationOrderService.getGoodsCommonOperationOrderPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "领用、退库、归还、借用、调拨主.xls", "数据", GoodsCommonOperationOrderRespVO.class,
                        BeanUtils.toBean(list, GoodsCommonOperationOrderRespVO.class));
    }

    // ==================== 子表（采购入库、领用、退库、归还、借用、调拨明细） ====================

    @GetMapping("/goods-warehousing-detail/list-by-common-operation-id")
    @Operation(summary = "获得采购入库、领用、退库、归还、借用、调拨明细列表")
    @Parameter(name = "commonOperationId", description = "公共操作Id")
    @PreAuthorize("@ss.hasPermission('wms:goods-common-operation-order:query')")
    public CommonResult<List<GoodsWarehousingDetailDO>> getGoodsWarehousingDetailListByCommonOperationId(@RequestParam("commonOperationId") Long commonOperationId) {
        return success(goodsCommonOperationOrderService.getGoodsWarehousingDetailListByCommonOperationId(commonOperationId));
    }

}