import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace PurchaseInWarehousingApi {
  /** 采购入库、领用、退库、归还、借用、调拨明细信息 */
  export interface GoodsWarehousingDetail {
            id: number; // 主键
            purchaseOrderId: number; // 采购订单id
            purchaseOrderCode: string; // 关联采购单编码
            purchaseOrderName: string; // 采购订单名称
            commonOperationId: number; // 公共操作Id
            commonOperationCode: string; // 公共操作编码
            commonOperationName: string; // 公共操作名称
            commonOperationType: string; // 公共操作类型
            assetId: number; // 资产ID
            assetCode: string; // 资产编码
            assetName: string; // 资产名称
            assetCategoryCode: string; // 资产类型编码
            assetCategoryName: string; // 资产类型名称
            assetModel: string; // 规格型号
            assetUnit: string; // 计量单位
            manufacturer: string; // 厂商
            brand: string; // 品牌
            serialNumber: string; // 序列号
            assetStatusCode: string; // 资产状态编码
            assetStatusName: string; // 资产状态名称
            assetSourceCode: string; // 资产来源编码
            assetSourceName: string; // 资料来源名称
            purchaseDate: string | Dayjs; // 购买日期
            purchasePrice: number; // 购买价格
            dateOfProduction: string | Dayjs; // 出场日期
            adminCompanyId: string; // 管理公司id
            adminCompanyName: string; // 管理公司名称
            adminDeptId: string; // 管理部门编码
            adminDeptName: string; // 管理部门名称
            adminManagerId: string; // 管理人员编码
            adminManagerName: string; // 管理人员名称
            wmsStoreCode: string; // 存放仓库编码
            wmsStoreName: string; // 存放仓库名称
            residualValueRate: number; // 残值率
            useCompanyId: string; // 使用公司id
            useCompanyName: string; // 使用公司名称
            useDeptId: string; // 使用部门编码
            useDeptName: string; // 使用部门名称
            useAccountId: string; // 使用人员编码
            useAccountName: string; // 使用人员名称
            transferUseDeptId: string; // 调入部门编码
            transferUseDeptName: string; // 调入部门名称
            transferUseAccountId: string; // 调入部门使用人员编码
            transferUseAccountName: string; // 调入使用人员名称
            assetIcon: string; // 资产图片
            assetFile: string; // 资产附件
            isJoinAsset: string; // 是否进入资产列表
            sort: number; // 显示顺序
            status: number; // 状态（0正常 1停用）
            storeAddress: string; // 仓库地址
            remark: string; // 备注
  }

  /** 采购入库信息 */
  export interface PurchaseInWarehousing {
    id: number; // 主键
    purchaseOrderId: number; // 采购订单id
    purchaseOrderCode: string; // 采购订单编码
    purchaseOrderName: string; // 采购订单名称
    warehousingEntryCode?: string; // 入库单编码
    warehousingEntryName: string; // 入库单名称
    inWarehousingDate: string | Dayjs; // 入库日期
    inWarehousingDesc: string; // 入库说明
    inWarehousingFile: string; // 附件
    inWarehousingTotalData: number; // 采购物品合计
    inWarehousingTotalAmount: number; // 入库物品金额合计
    sort: number; // 显示顺序
    status?: number; // 状态（0正常 1停用）
    remark: string; // 备注
        goodswarehousingdetails?: GoodsWarehousingDetail[]
  }
}

/** 查询采购入库分页 */
export function getPurchaseInWarehousingPage(params: PageParam) {
  return requestClient.get<PageResult<PurchaseInWarehousingApi.PurchaseInWarehousing>>('/wms/purchase-in-warehousing/page', { params });
}

/** 查询采购入库详情 */
export function getPurchaseInWarehousing(id: number) {
  return requestClient.get<PurchaseInWarehousingApi.PurchaseInWarehousing>(`/wms/purchase-in-warehousing/get?id=${id}`);
}

/** 新增采购入库 */
export function createPurchaseInWarehousing(data: PurchaseInWarehousingApi.PurchaseInWarehousing) {
  return requestClient.post('/wms/purchase-in-warehousing/create', data);
}

/** 修改采购入库 */
export function updatePurchaseInWarehousing(data: PurchaseInWarehousingApi.PurchaseInWarehousing) {
  return requestClient.put('/wms/purchase-in-warehousing/update', data);
}

/** 删除采购入库 */
export function deletePurchaseInWarehousing(id: number) {
  return requestClient.delete(`/wms/purchase-in-warehousing/delete?id=${id}`);
}

/** 批量删除采购入库 */
export function deletePurchaseInWarehousingListByIds(ids: number[]) {
  return requestClient.delete(`/wms/purchase-in-warehousing/delete-list?ids=${ids.join(',')}`)
}

/** 导出采购入库 */
export function exportPurchaseInWarehousing(params: any) {
  return requestClient.download('/wms/purchase-in-warehousing/export-excel', params);
}


// ==================== 子表（采购入库、领用、退库、归还、借用、调拨明细） ====================

/** 获得采购入库、领用、退库、归还、借用、调拨明细列表 */
export function getGoodsWarehousingDetailListByPurchaseOrderId(purchaseOrderId: number) {
  return requestClient.get<PurchaseInWarehousingApi.GoodsWarehousingDetail[]>(`/wms/purchase-in-warehousing/goods-warehousing-detail/list-by-purchase-order-id?purchaseOrderId=${purchaseOrderId}`);
}
