import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace InfoApi {
  /** 资产信息信息 */
  export interface Info {
    id: number; // 主键
    purchaseOrderId: number; // 采购订单id
    purchaseOrderCode: string; // 采购订单编码
    purchaseOrderName: string; // 采购订单名称
    inWarehousingId: number; // 入库单Id
    warehousingEntryCode: string; // 入库单编码
    warehousingEntryName: string; // 入库单名称
    assetCode?: string; // 资产编码
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
    useCompanyId: string; // 使用公司id
    useCompanyName: string; // 使用公司名称
    useDeptId: string; // 使用部门编码
    useDeptName: string; // 使用部门名称
    useAccountId: string; // 使用人员编码
    useAccountName: string; // 使用人员名称
    wmsStoreCode: string; // 存放仓库编码
    wmsStoreName: string; // 存放仓库名称
    residualValueRate: number; // 残值率
    assetIcon: string; // 资产图片
    assetFile: string; // 资产附件
    sort: number; // 显示顺序
    status?: number; // 状态（0正常 1停用）
    storeAddress: string; // 仓库地址
    remark: string; // 备注
  }
}

/** 查询资产信息分页 */
export function getInfoPage(params: PageParam) {
  return requestClient.get<PageResult<InfoApi.Info>>('/asset/info/page', { params });
}

/** 查询资产信息详情 */
export function getInfo(id: number) {
  return requestClient.get<InfoApi.Info>(`/asset/info/get?id=${id}`);
}

/** 新增资产信息 */
export function createInfo(data: InfoApi.Info) {
  return requestClient.post('/asset/info/create', data);
}

/** 修改资产信息 */
export function updateInfo(data: InfoApi.Info) {
  return requestClient.put('/asset/info/update', data);
}

/** 删除资产信息 */
export function deleteInfo(id: number) {
  return requestClient.delete(`/asset/info/delete?id=${id}`);
}

/** 批量删除资产信息 */
export function deleteInfoListByIds(ids: number[]) {
  return requestClient.delete(`/asset/info/delete-list?ids=${ids.join(',')}`)
}

/** 导出资产信息 */
export function exportInfo(params: any) {
  return requestClient.download('/asset/info/export-excel', params);
}

