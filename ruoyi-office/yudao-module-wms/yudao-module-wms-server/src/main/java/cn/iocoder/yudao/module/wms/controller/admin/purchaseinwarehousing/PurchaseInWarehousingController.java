package cn.iocoder.yudao.module.wms.controller.admin.purchaseinwarehousing;

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

import cn.iocoder.yudao.module.wms.controller.admin.purchaseinwarehousing.vo.*;
import cn.iocoder.yudao.module.wms.dal.dataobject.purchaseinwarehousing.PurchaseInWarehousingDO;
import cn.iocoder.yudao.module.wms.service.purchaseinwarehousing.PurchaseInWarehousingService;

@Tag(name = "管理后台 - 采购入库")
@RestController
@RequestMapping("/wms/purchase-in-warehousing")
@Validated
public class PurchaseInWarehousingController {

    @Resource
    private PurchaseInWarehousingService purchaseInWarehousingService;

    @PostMapping("/create")
    @Operation(summary = "创建采购入库")
    @PreAuthorize("@ss.hasPermission('wms:purchase-in-warehousing:create')")
    public CommonResult<Long> createPurchaseInWarehousing(@Valid @RequestBody PurchaseInWarehousingSaveReqVO createReqVO) {
        return success(purchaseInWarehousingService.createPurchaseInWarehousing(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新采购入库")
    @PreAuthorize("@ss.hasPermission('wms:purchase-in-warehousing:update')")
    public CommonResult<Boolean> updatePurchaseInWarehousing(@Valid @RequestBody PurchaseInWarehousingSaveReqVO updateReqVO) {
        purchaseInWarehousingService.updatePurchaseInWarehousing(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除采购入库")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('wms:purchase-in-warehousing:delete')")
    public CommonResult<Boolean> deletePurchaseInWarehousing(@RequestParam("id") Long id) {
        purchaseInWarehousingService.deletePurchaseInWarehousing(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除采购入库")
                @PreAuthorize("@ss.hasPermission('wms:purchase-in-warehousing:delete')")
    public CommonResult<Boolean> deletePurchaseInWarehousingList(@RequestParam("ids") List<Long> ids) {
        purchaseInWarehousingService.deletePurchaseInWarehousingListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得采购入库")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('wms:purchase-in-warehousing:query')")
    public CommonResult<PurchaseInWarehousingRespVO> getPurchaseInWarehousing(@RequestParam("id") Long id) {
        PurchaseInWarehousingDO purchaseInWarehousing = purchaseInWarehousingService.getPurchaseInWarehousing(id);
        return success(BeanUtils.toBean(purchaseInWarehousing, PurchaseInWarehousingRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得采购入库分页")
    @PreAuthorize("@ss.hasPermission('wms:purchase-in-warehousing:query')")
    public CommonResult<PageResult<PurchaseInWarehousingRespVO>> getPurchaseInWarehousingPage(@Valid PurchaseInWarehousingPageReqVO pageReqVO) {
        PageResult<PurchaseInWarehousingDO> pageResult = purchaseInWarehousingService.getPurchaseInWarehousingPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PurchaseInWarehousingRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出采购入库 Excel")
    @PreAuthorize("@ss.hasPermission('wms:purchase-in-warehousing:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPurchaseInWarehousingExcel(@Valid PurchaseInWarehousingPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PurchaseInWarehousingDO> list = purchaseInWarehousingService.getPurchaseInWarehousingPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "采购入库.xls", "数据", PurchaseInWarehousingRespVO.class,
                        BeanUtils.toBean(list, PurchaseInWarehousingRespVO.class));
    }

    // ==================== 子表（采购入库、领用、退库、归还、借用、调拨明细） ====================

    @GetMapping("/goods-warehousing-detail/list-by-purchase-order-id")
    @Operation(summary = "获得采购入库、领用、退库、归还、借用、调拨明细列表")
    @Parameter(name = "purchaseOrderId", description = "采购订单id")
    @PreAuthorize("@ss.hasPermission('wms:purchase-in-warehousing:query')")
    public CommonResult<List<GoodsWarehousingDetailDO>> getGoodsWarehousingDetailListByPurchaseOrderId(@RequestParam("purchaseOrderId") Long purchaseOrderId) {
        return success(purchaseInWarehousingService.getGoodsWarehousingDetailListByPurchaseOrderId(purchaseOrderId));
    }

}