import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace WarehousingApi {
  /** 仓库信息信息 */
  export interface Warehousing {
    id: number; // 主键
    warehousingCode?: string; // 仓库编码
    warehousingName: string; // 仓库名称
    parentId?: number; // 上级id
    level: number; // 级别
    warehousingCategoryCode: string; // 仓库类型编码
    warehousingCategoryName: string; // 仓库类型名称
    warehousingAddress: string; // 仓库地址
    companyId: string; // 公司id
    companyName: string; // 公司名称
    sort: number; // 显示顺序
    status?: number; // 状态（0正常 1停用）
    remark: string; // 备注
  children?: Warehousing[];
  }
}

/** 查询仓库信息列表 */
export function getWarehousingList(params: any) {
  return requestClient.get<WarehousingApi.Warehousing[]>('/wms/warehousing/list', { params });
}

/** 查询仓库信息详情 */
export function getWarehousing(id: number) {
  return requestClient.get<WarehousingApi.Warehousing>(`/wms/warehousing/get?id=${id}`);
}

/** 新增仓库信息 */
export function createWarehousing(data: WarehousingApi.Warehousing) {
  return requestClient.post('/wms/warehousing/create', data);
}

/** 修改仓库信息 */
export function updateWarehousing(data: WarehousingApi.Warehousing) {
  return requestClient.put('/wms/warehousing/update', data);
}

/** 删除仓库信息 */
export function deleteWarehousing(id: number) {
  return requestClient.delete(`/wms/warehousing/delete?id=${id}`);
}


/** 导出仓库信息 */
export function exportWarehousing(params: any) {
  return requestClient.download('/wms/warehousing/export-excel', params);
}

