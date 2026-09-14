import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';
import type { AttachmentApi } from '#/api/common/attachment';

export namespace CarReturnBillApi {
  /** 还车申请单信息 */
  export interface CarReturnBill {
    id: number; // ID
    billCode?: string; // 单据编号
    processInstanceId: string; // 流程实例编号
    processStatus: number; // 单据状态
    applyBill: string; // 用车申请单
    carId: number; // 车辆
    carNo: string; // 车牌号
    goTime: string | Dayjs; // 出车时间
    returnTime: string | Dayjs; // 回车时间
    goArea: string; // 出车地点
    returnArea: string; // 回车地点
    cause: string; // 用车事由
    applyer: string; // 申请人
    passenger: string; // 随行人
    remark: string; // 还车说明
    creator: number; // 创建者ID
    creatorName: string; // 创建者姓名
    parentId?: number; // 父级ID
    deptId: number; // 部门ID
    deptName: string; // 部门名称
    companyId: number; // 公司ID
    companyName: string; // 公司名称
    createTime: Date | string; // 创建时间
    attachments?: AttachmentApi.AttachmentSaveReq[];
  }
}

/** 查询还车申请单分页 */
export function getCarReturnBillPage(params: PageParam) {
  return requestClient.get<PageResult<CarReturnBillApi.CarReturnBill>>('/oa/car-return-bill/page', { params });
}

/** 查询还车申请单详情 */
export function getCarReturnBill(id: number) {
  return requestClient.get<CarReturnBillApi.CarReturnBill>(`/oa/car-return-bill/get?id=${id}`);
}

/** 保存还车申请单  */
export function saveCarReturnBill(data: CarReturnBillApi.CarReturnBill) {
  return requestClient.post(`/oa/car-return-bill/save`, data);
}

/** 提交还车申请单  */
export function submitCarReturnBill(data: CarReturnBillApi.CarReturnBill) {
  return requestClient.post(`/oa/car-return-bill/submit`, data);
}

/** 新增还车申请单 */
export function createCarReturnBill(data: CarReturnBillApi.CarReturnBill) {
  return requestClient.post('/oa/car-return-bill/create', data);
}

/** 修改还车申请单 */
export function updateCarReturnBill(data: CarReturnBillApi.CarReturnBill) {
  return requestClient.put('/oa/car-return-bill/update', data);
}

/** 删除还车申请单 */
export function deleteCarReturnBill(id: number) {
  return requestClient.delete(`/oa/car-return-bill/delete?id=${id}`);
}

/** 批量删除还车申请单 */
export function deleteCarReturnBillList(ids: number[]) {
  return requestClient.delete(`/oa/car-return-bill/delete-list?ids=${ids.join(',')}`);
}

/** 导出还车申请单 */
export function exportCarReturnBill(params: any) {
  return requestClient.download('/oa/car-return-bill/export-excel', params);
}


