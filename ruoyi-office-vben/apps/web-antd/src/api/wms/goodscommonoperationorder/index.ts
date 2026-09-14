import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace GoodsCommonOperationOrderApi {
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

  /** 领用、退库、归还、借用、调拨主信息 */
  export interface GoodsCommonOperationOrder {
    id: number; // 主键
    purchaseOrderId: number; // 采购订单id
    purchaseOrderCode: string; // 采购订单编码
    purchaseOrderName: string; // 采购订单名称
    inWarehousingId: number; // 入库单Id
    warehousingEntryCode?: string; // 入库单编码
    warehousingEntryName: string; // 入库单名称
    commonOperationDate: string | Dayjs; // 领用、退库、归还、借用日期
    commonOperationDesc: string; // 领用、退库、归还、借用库说明
    commonOperationFile: string; // 领用、退库、归还、借用附件
    stockReturnTotalData: number; // 采购物品合计
    commonOperationTotalAmount: number; // 入库物品金额合计
    companyId: string; // 公司id
    companyName: string; // 公司名称
    deptId: string; // 领用、退库、归还、借用部门编码
    deptName: string; // 领用、退库、归还、借用部门名称
    userId: string; // 领用、退库、归还、借用人编码
    userName: string; // 领用、退库、归还、借用人名称
    expectReturnDate: string | Dayjs; // 预计归还日期
    transferCompanyId: string; // 调入公司id
    transferCompanyName: string; // 调入公司名称
    transferDeptId: string; // 调入部门编码
    transferDeptName: string; // 调入部门名称
    processInstanceId: string; // 流程实例编号
    processStatus: number; // 单据状态
    sort: number; // 显示顺序
    status?: number; // 状态（0正常 1停用）
    remark: string; // 备注
        goodswarehousingdetails?: GoodsWarehousingDetail[]
  }
}

/** 查询领用、退库、归还、借用、调拨主分页 */
export function getGoodsCommonOperationOrderPage(params: PageParam) {
  return requestClient.get<PageResult<GoodsCommonOperationOrderApi.GoodsCommonOperationOrder>>('/wms/goods-common-operation-order/page', { params });
}

/** 查询领用、退库、归还、借用、调拨主详情 */
export function getGoodsCommonOperationOrder(id: number) {
  return requestClient.get<GoodsCommonOperationOrderApi.GoodsCommonOperationOrder>(`/wms/goods-common-operation-order/get?id=${id}`);
}

/** 新增领用、退库、归还、借用、调拨主 */
export function createGoodsCommonOperationOrder(data: GoodsCommonOperationOrderApi.GoodsCommonOperationOrder) {
  return requestClient.post('/wms/goods-common-operation-order/create', data);
}

/** 修改领用、退库、归还、借用、调拨主 */
export function updateGoodsCommonOperationOrder(data: GoodsCommonOperationOrderApi.GoodsCommonOperationOrder) {
  return requestClient.put('/wms/goods-common-operation-order/update', data);
}

/** 删除领用、退库、归还、借用、调拨主 */
export function deleteGoodsCommonOperationOrder(id: number) {
  return requestClient.delete(`/wms/goods-common-operation-order/delete?id=${id}`);
}

/** 批量删除领用、退库、归还、借用、调拨主 */
export function deleteGoodsCommonOperationOrderListByIds(ids: number[]) {
  return requestClient.delete(`/wms/goods-common-operation-order/delete-list?ids=${ids.join(',')}`)
}

/** 导出领用、退库、归还、借用、调拨主 */
export function exportGoodsCommonOperationOrder(params: any) {
  return requestClient.download('/wms/goods-common-operation-order/export-excel', params);
}


// ==================== 子表（采购入库、领用、退库、归还、借用、调拨明细） ====================

/** 获得采购入库、领用、退库、归还、借用、调拨明细列表 */
export function getGoodsWarehousingDetailListByCommonOperationId(commonOperationId: number) {
  return requestClient.get<GoodsCommonOperationOrderApi.GoodsWarehousingDetail[]>(`/wms/goods-common-operation-order/goods-warehousing-detail/list-by-common-operation-id?commonOperationId=${commonOperationId}`);
}
