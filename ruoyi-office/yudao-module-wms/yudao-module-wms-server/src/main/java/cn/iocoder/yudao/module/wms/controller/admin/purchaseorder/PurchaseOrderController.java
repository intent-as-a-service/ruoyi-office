package cn.iocoder.yudao.module.wms.controller.admin.purchaseorder;

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

import cn.iocoder.yudao.module.wms.controller.admin.purchaseorder.vo.*;
import cn.iocoder.yudao.module.wms.dal.dataobject.purchaseorder.PurchaseOrderDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.purchaseorderdetail.PurchaseOrderDetailDO;
import cn.iocoder.yudao.module.wms.service.purchaseorder.PurchaseOrderService;

@Tag(name = "仓库管理 - 采购订单")
@RestController
@RequestMapping("/wms/purchase-order")
@Validated
public class PurchaseOrderController {

    @Resource
    private PurchaseOrderService purchaseOrderService;

    @PostMapping("/create")
    @Operation(summary = "创建采购订单")
    @PreAuthorize("@ss.hasPermission('wms:purchase-order:create')")
    public CommonResult<Long> createPurchaseOrder(@Valid @RequestBody PurchaseOrderSaveReqVO createReqVO) {
        return success(purchaseOrderService.createPurchaseOrder(createReqVO));
    }


    @PostMapping("/submit")
    @Operation(summary = "创建采购订单")
    @PreAuthorize("@ss.hasPermission('wms:purchase-order:create')")
    public CommonResult<Long> submitPurchaseOrder(@Valid @RequestBody PurchaseOrderSaveReqVO createReqVO) {
        return success(purchaseOrderService.submitPurchaseOrder(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新采购订单")
    @PreAuthorize("@ss.hasPermission('wms:purchase-order:update')")
    public CommonResult<Boolean> updatePurchaseOrder(@Valid @RequestBody PurchaseOrderSaveReqVO updateReqVO) {
        purchaseOrderService.updatePurchaseOrder(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除采购订单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('wms:purchase-order:delete')")
    public CommonResult<Boolean> deletePurchaseOrder(@RequestParam("id") Long id) {
        purchaseOrderService.deletePurchaseOrder(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除采购订单")
                @PreAuthorize("@ss.hasPermission('wms:purchase-order:delete')")
    public CommonResult<Boolean> deletePurchaseOrderList(@RequestParam("ids") List<Long> ids) {
        purchaseOrderService.deletePurchaseOrderListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得采购订单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('wms:purchase-order:query')")
    public CommonResult<PurchaseOrderRespVO> getPurchaseOrder(@RequestParam("id") Long id) {
        PurchaseOrderDO purchaseOrder = purchaseOrderService.getPurchaseOrder(id);
        return success(BeanUtils.toBean(purchaseOrder, PurchaseOrderRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得采购订单分页")
    @PreAuthorize("@ss.hasPermission('wms:purchase-order:query')")
    public CommonResult<PageResult<PurchaseOrderRespVO>> getPurchaseOrderPage(@Valid PurchaseOrderPageReqVO pageReqVO) {
        PageResult<PurchaseOrderDO> pageResult = purchaseOrderService.getPurchaseOrderPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PurchaseOrderRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出采购订单 Excel")
    @PreAuthorize("@ss.hasPermission('wms:purchase-order:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPurchaseOrderExcel(@Valid PurchaseOrderPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PurchaseOrderDO> list = purchaseOrderService.getPurchaseOrderPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "采购订单.xls", "数据", PurchaseOrderRespVO.class,
                        BeanUtils.toBean(list, PurchaseOrderRespVO.class));
    }

    // ==================== 子表（采购订单明细） ====================

    @GetMapping("/purchase-order-detail/list-by-purchase-order-id")
    @Operation(summary = "获得采购订单明细列表")
    @Parameter(name = "purchaseOrderId", description = "采购订单id")
    @PreAuthorize("@ss.hasPermission('wms:purchase-order:query')")
    public CommonResult<List<PurchaseOrderDetailDO>> getPurchaseOrderDetailListByPurchaseOrderId(@RequestParam("purchaseOrderId") String purchaseOrderId) {
        return success(purchaseOrderService.getPurchaseOrderDetailListByPurchaseOrderId(purchaseOrderId));
    }

}