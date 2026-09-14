import type { PageParam, PageResult } from '@vben/request';

import type { AttachmentApi } from '#/api/common/attachment';

import { requestClient } from '#/api/request';

export namespace EmployeeTransferBillApi {
  /** 人事调动申请单信息 */
  export interface EmployeeTransferBill {
    id?: number;
    billCode: string;
    processInstanceId?: string;
    processStatus?: number;
    // 员工信息
    employeeId: number;
    employeeNo?: string;
    name: string;
    sex?: number;
    mobile?: string;
    empDeptId: number;
    empDeptName: string;
    empCompanyId: number;
    empCompanyName: string;
    jobPost?: string;
    jobPosition?: string;
    employeeStatus: number;
    // 调动信息
    transferType?: string;
    transferReason?: string;
    originalJobPost?: string;
    newJobPost?: string;
    originalJobPosition?: string;
    newJobPosition?: string;
    originalCompanyId?: number;
    originalCompanyName?: string;
    originalDeptId?: number;
    originalDeptName?: string;
    newCompanyId?: number;
    newCompanyName?: string;
    newDeptId?: number;
    newDeptName?: string;
    effectiveImmediately?: boolean;
    effectiveDate?: Date;
    // 制单人信息
    deptId: number;
    deptName: string;
    companyId: number;
    companyName: string;
    creator?: number;
    creatorName?: string;
    remark?: string;
    createTime?: Date;
    attachments?: AttachmentApi.AttachmentSaveReq[];
  }

  /** 人事调动申请单分页请求 */
  export interface EmployeeTransferBillPageReqVO extends PageParam {
    billCode?: string;
    processStatus?: number;
    employeeNo?: string;
    name?: string;
    empDeptId?: number;
    empDeptName?: string;
    empCompanyId?: number;
    empCompanyName?: string;
    employeeStatus?: number;
    transferType?: string;
    creator?: string;
    createTime?: Date[];
  }
}

/** 查询人事调动申请单列表 */
export function getEmployeeTransferBillPage(
  params: EmployeeTransferBillApi.EmployeeTransferBillPageReqVO,
) {
  return requestClient.get<
    PageResult<EmployeeTransferBillApi.EmployeeTransferBill>
  >('/hrm/employee-transfer-bill/page', { params });
}

/** 查询人事调动申请单详情 */
export function getEmployeeTransferBill(id: number) {
  return requestClient.get<EmployeeTransferBillApi.EmployeeTransferBill>(
    `/hrm/employee-transfer-bill/get?id=${id}`,
  );
}

/** 新增人事调动申请单 */
export function createEmployeeTransferBill(
  data: EmployeeTransferBillApi.EmployeeTransferBill,
) {
  return requestClient.post('/hrm/employee-transfer-bill/create', data);
}

/** 保存人事调动申请单 */
export function saveEmployeeTransferBill(
  data: EmployeeTransferBillApi.EmployeeTransferBill,
) {
  return requestClient.post('/hrm/employee-transfer-bill/save', data);
}

/** 提交人事调动申请单 */
export function submitEmployeeTransferBill(
  data: EmployeeTransferBillApi.EmployeeTransferBill,
) {
  return requestClient.post('/hrm/employee-transfer-bill/submit', data);
}

/** 修改人事调动申请单 */
export function updateEmployeeTransferBill(
  data: EmployeeTransferBillApi.EmployeeTransferBill,
) {
  return requestClient.put('/hrm/employee-transfer-bill/update', data);
}

/** 删除人事调动申请单 */
export function deleteEmployeeTransferBill(id: number) {
  return requestClient.delete(`/hrm/employee-transfer-bill/delete?id=${id}`);
}

/** 批量删除人事调动申请单 */
export function deleteEmployeeTransferBillList(ids: number[]) {
  return requestClient.delete(
    `/hrm/employee-transfer-bill/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出人事调动申请单 */
export function exportEmployeeTransferBill(params: any) {
  return requestClient.download('/hrm/employee-transfer-bill/export-excel', {
    params,
  });
}

