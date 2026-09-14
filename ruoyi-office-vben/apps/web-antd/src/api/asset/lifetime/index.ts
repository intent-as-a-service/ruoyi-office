import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace LifeTimeApi {
  /** 资产历史记录信息 */
  export interface LifeTime {
    id: number; // 主键
    assetId: number; // 采购订单id
    assetCode?: string; // 资产编码
    assetName: string; // 资产名称
    assetMilestone: string; // 资产里程说明
    sort: number; // 显示顺序
    status?: number; // 状态（0正常 1停用）
    storeAddress: string; // 仓库地址
    remark: string; // 备注
  }
}

/** 查询资产历史记录分页 */
export function getLifeTimePage(params: PageParam) {
  return requestClient.get<PageResult<LifeTimeApi.LifeTime>>('/asset/life-time/page', { params });
}

/** 查询资产历史记录详情 */
export function getLifeTime(id: number) {
  return requestClient.get<LifeTimeApi.LifeTime>(`/asset/life-time/get?id=${id}`);
}

/** 新增资产历史记录 */
export function createLifeTime(data: LifeTimeApi.LifeTime) {
  return requestClient.post('/asset/life-time/create', data);
}

/** 修改资产历史记录 */
export function updateLifeTime(data: LifeTimeApi.LifeTime) {
  return requestClient.put('/asset/life-time/update', data);
}

/** 删除资产历史记录 */
export function deleteLifeTime(id: number) {
  return requestClient.delete(`/asset/life-time/delete?id=${id}`);
}

/** 批量删除资产历史记录 */
export function deleteLifeTimeListByIds(ids: number[]) {
  return requestClient.delete(`/asset/life-time/delete-list?ids=${ids.join(',')}`)
}

/** 导出资产历史记录 */
export function exportLifeTime(params: any) {
  return requestClient.download('/asset/life-time/export-excel', params);
}

