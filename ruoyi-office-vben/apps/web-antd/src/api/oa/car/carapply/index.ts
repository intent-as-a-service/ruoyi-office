import type { Dayjs } from 'dayjs';

import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';
import type { AttachmentApi } from '#/api/common/attachment';

export namespace CarApplyBillApi {
  /** 用车申请单信息 */
  export interface CarApplyBill {
    id: number; // ID
    billCode?: string; // 单据编号
    processInstanceId: string; // 流程实例编号
    processStatus: number; // 单据状态
    carId: number; // 车辆
    carNo: string; // 车牌号码
    goTime: Dayjs | string; // 出车时间
    returnTime: Dayjs | string; // 回车时间
    goArea: string; // 出车地点
    returnArea: string; // 回车地点
    cause: string; // 用车事由
    applyer: string; // 申请人
    passenger: string; // 随行人
    remark: string; // 备注
    creator: number; // 创建者ID
    creatorName: string; // 创建者姓名
    deptId: number; // 部门ID
    deptName: string; // 部门名称
    companyId: number; // 公司ID
    companyName: string; // 公司名称
    createTime: Date | string; // 创建时间
    returnStatus?: number; // 还车状态
    attachments?: AttachmentApi.AttachmentSaveReq[];
  }
}

/** 查询用车申请单分页 */
export function getCarApplyBillPage(params: PageParam) {
  return requestClient.get<PageResult<CarApplyBillApi.CarApplyBill>>(
    '/oa/car-apply-bill/page',
    { params },
  );
}

/** 查询用车申请单详情 */
export function getCarApplyBill(id: number) {
  return requestClient.get<CarApplyBillApi.CarApplyBill>(
    `/oa/car-apply-bill/get?id=${id}`,
  );
}

/** 保存用车申请单  */
export function saveCarApplyBill(data: CarApplyBillApi.CarApplyBill) {
  return requestClient.post(`/oa/car-apply-bill/save`, data);
}

/** 提交用车申请单  */
export function submitCarApplyBill(data: CarApplyBillApi.CarApplyBill) {
  return requestClient.post(`/oa/car-apply-bill/submit`, data);
}

/** 修改用车申请单 */
export function updateCarApplyBill(data: CarApplyBillApi.CarApplyBill) {
  return requestClient.put('/oa/car-apply-bill/update', data);
}

/** 删除用车申请单 */
export function deleteCarApplyBill(id: number) {
  return requestClient.delete(`/oa/car-apply-bill/delete?id=${id}`);
}

/** 批量删除用车申请单 */
export function deleteCarApplyBillListByIds(ids: number[]) {
  return requestClient.delete(
    `/oa/car-apply-bill/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出用车申请单 */
export function exportCarApplyBill(params: any) {
  return requestClient.download('/oa/car-apply-bill/export-excel', params);
}
