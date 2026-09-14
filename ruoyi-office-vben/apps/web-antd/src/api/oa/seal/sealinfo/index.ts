import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace SealApi {
  /** 印章信息信息 */
  export interface Seal {
    id?: number; // ID
    sealNo?: string; // 印章编号
    sealName?: string; // 印章名称
    sealType?: number; // 印章类型
    sealCls?: number; // 分类
    status?: number; // 状态
    keeperId?: number; // 保管人ID
    keeperName?: string; // 保管人名称
    keeperDeptId?: number; // 保管部门ID
    keeperDeptName?: string; // 保管部门名称
    purchaseDate?: string | Dayjs; // 购买日期
    enableDate?: string | Dayjs; // 启用日期
    disableDate?: string | Dayjs; // 停用日期
    picUrl?: string; // 上传照片
    sort?: number; // 显示顺序
    remark?: string; // 备注
    companyId?: number; // 公司ID
    companyName?: string; // 公司名称
  }
}

/** 查询印章信息分页 */
export function getSealPage(params: PageParam) {
  return requestClient.get<PageResult<SealApi.Seal>>('/oa/seal/page', { params });
}

/** 查询印章信息详情 */
export function getSeal(id: number) {
  return requestClient.get<SealApi.Seal>(`/oa/seal/get?id=${id}`);
}

/** 新增印章信息 */
export function createSeal(data: SealApi.Seal) {
  return requestClient.post('/oa/seal/create', data);
}

/** 修改印章信息 */
export function updateSeal(data: SealApi.Seal) {
  return requestClient.put('/oa/seal/update', data);
}

/** 删除印章信息 */
export function deleteSeal(id: number) {
  return requestClient.delete(`/oa/seal/delete?id=${id}`);
}

/** 批量删除印章信息 */
export function deleteSealListByIds(ids: number[]) {
  return requestClient.delete(`/oa/seal/delete-list?ids=${ids.join(',')}`);
}

/** 导出印章信息 */
export function exportSeal(params: any) {
  return requestClient.download('/oa/seal/export-excel', params);
}




