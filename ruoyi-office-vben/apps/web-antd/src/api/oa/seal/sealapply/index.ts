import type { PageParam, PageResult } from '@vben/request';

import type { AttachmentApi } from '#/api/common/attachment';

import { requestClient } from '#/api/request';

export namespace SealApplyBillApi {
  /** 用印申请单信息 */
  export interface SealApplyBill {
    id?: number;
    billCode: string;
    processInstanceId?: string;
    processStatus?: number;
    sealId: number;
    sealNo: string;
    sealName?: string;
    sealType?: number;
    keeperId?: number;
    keeperName?: string;
    keeperDeptId?: number;
    keeperDeptName?: string;
    cause: string;
    useType: number;
    useMode: number;
    documentTitle?: string;
    documentType?: string;
    documentCount?: number;
    contractAmount?: number;
    contractParty?: string;
    expectedUseTime?: Date;
    actualUseTime?: Date;
    expectedReturnTime?: Date;
    actualReturnTime?: Date;
    useStatus?: number;
    isUrgent?: number;
    creator?: number;
    creatorName?: string;
    companyId: number;
    companyName: string;
    deptId: number;
    deptName: string;
    remark?: string;
    createTime?: Date;
    attachments?: AttachmentApi.AttachmentSaveReq[];
  }

  /** 用印申请单分页请求 */
  export interface SealApplyBillPageReqVO extends PageParam {
    billCode?: string;
    processStatus?: number;
    sealId?: number;
    sealNo?: string;
    sealName?: string;
    useType?: number;
    useMode?: number;
    useStatus?: number;
    isUrgent?: number;
    companyId?: number;
    companyName?: string;
    deptId?: number;
    deptName?: string;
    expectedUseTime?: Date[];
    createTime?: Date[];
  }
}

/** 查询用印申请单列表 */
export function getSealApplyBillPage(
  params: SealApplyBillApi.SealApplyBillPageReqVO,
) {
  return requestClient.get<PageResult<SealApplyBillApi.SealApplyBill>>(
    '/oa/seal-apply-bill/page',
    { params },
  );
}

/** 查询用印申请单详情 */
export function getSealApplyBill(id: number) {
  return requestClient.get<SealApplyBillApi.SealApplyBill>(
    `/oa/seal-apply-bill/get?id=${id}`,
  );
}

/** 新增用印申请单 */
export function createSealApplyBill(data: SealApplyBillApi.SealApplyBill) {
  return requestClient.post('/oa/seal-apply-bill/create', data);
}

/** 保存用印申请单 */
export function saveSealApplyBill(data: SealApplyBillApi.SealApplyBill) {
  return requestClient.post('/oa/seal-apply-bill/save', data);
}

/** 提交用印申请单 */
export function submitSealApplyBill(data: SealApplyBillApi.SealApplyBill) {
  return requestClient.post('/oa/seal-apply-bill/submit', data);
}

/** 修改用印申请单 */
export function updateSealApplyBill(data: SealApplyBillApi.SealApplyBill) {
  return requestClient.put('/oa/seal-apply-bill/update', data);
}

/** 删除用印申请单 */
export function deleteSealApplyBill(id: number) {
  return requestClient.delete(`/oa/seal-apply-bill/delete?id=${id}`);
}

/** 批量删除用印申请单 */
export function deleteSealApplyBillList(ids: number[]) {
  return requestClient.delete(
    `/oa/seal-apply-bill/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出用印申请单 */
export function exportSealApplyBill(params: any) {
  return requestClient.download('/oa/seal-apply-bill/export-excel', { params });
}
