import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace PurchaseOrderApi {
  /** 采购订单明细信息 */
  export interface PurchaseOrderDetail {
            id: number; // 主键
            purchaseOrderId: string; // 采购订单id
            purchaseOrderCode: string; // 采购订单编码
            purchaseOrderName: string; // 采购订单名称
            categoryCode: string; // 资产类别编码
            categoryName: string; // 资产类别名称
            goodsCode: string; // 物品名称编码
            goodsName: string; // 物品名称名称
            purchasePrice: number; // 采购单据
            purchaseNum: number; // 采购数量
            purchaseTotalAmonut: number; // 采购总价
            sort: number; // 显示顺序
            status?: number; // 状态（0正常 1停用）
            remark: string; // 备注
  }

  /** 采购订单信息 */
  export interface PurchaseOrder {
purchaseOrderDetails: PurchaseOrderDetail[] | undefined;
[x: string]: PurchaseOrderDetail[] | undefined;
    id: number; // 主键
    purchaseOrderCode?: string; // 采购订单编码
    purchaseOrderName: string; // 采购订单名称
    applicantDate: string | Dayjs; // 申请日期
    companyId: string; // 公司id
    companyName: string; // 公司名称
    applicantDeptId: string; // 申请部门编码
    applicantDeptName: string; // 申请部门名称
    applicantUserId: string; // 申请人编码
    applicantUserName: string; // 申请人名称
    supplierCode: string; // 供应商编码
    supplierName: string; // 供应商名称
    applicantDesc: string; // 申请说明
    purchaseOrderFile: string; // 附件
    purchaseOrderTotalData: number; // 采购物品合计
    purchaseOrderTotalAmount: number; // 采购物品金额合计
    processInstanceId: string; // 流程实例编号
    processStatus: number; // 单据状态
    sort: number; // 显示顺序
    status?: number; // 状态（0正常 1停用）
    remark: string; // 备注
        purchaseorderdetails?: PurchaseOrderDetail[]
  }
}

/** 查询采购订单分页 */
export function getPurchaseOrderPage(params: PageParam) {
  return requestClient.get<PageResult<PurchaseOrderApi.PurchaseOrder>>('/wms/purchase-order/page', { params });
}

/** 查询采购订单详情 */
export function getPurchaseOrder(id: number) {
  return requestClient.get<PurchaseOrderApi.PurchaseOrder>(`/wms/purchase-order/get?id=${id}`);
}

/** 新增采购订单 */
export function createPurchaseOrder(data: PurchaseOrderApi.PurchaseOrder) {
  return requestClient.post('/wms/purchase-order/create', data);
}

/** 提交采购订单 */
export function submitPurchaseOrder(data: PurchaseOrderApi.PurchaseOrder) {
  return requestClient.post('/wms/purchase-order/submit', data);
}


/** 修改采购订单 */
export function updatePurchaseOrder(data: PurchaseOrderApi.PurchaseOrder) {
  return requestClient.put('/wms/purchase-order/update', data);
}

/** 删除采购订单 */
export function deletePurchaseOrder(id: number) {
  return requestClient.delete(`/wms/purchase-order/delete?id=${id}`);
}

/** 批量删除采购订单 */
export function deletePurchaseOrderListByIds(ids: number[]) {
  return requestClient.delete(`/wms/purchase-order/delete-list?ids=${ids.join(',')}`)
}

/** 导出采购订单 */
export function exportPurchaseOrder(params: any) {
  return requestClient.download('/wms/purchase-order/export-excel', params);
}


// ==================== 子表（采购订单明细） ====================

/** 获得采购订单明细列表 */
export function getPurchaseOrderDetailListByPurchaseOrderId(purchaseOrderId: number) {
  return requestClient.get<PurchaseOrderApi.PurchaseOrderDetail[]>(`/wms/purchase-order/purchase-order-detail/list-by-purchase-order-id?purchaseOrderId=${purchaseOrderId}`);
}

