import type { PageParam, PageResult } from '@vben/request';

import type { AttachmentApi } from '#/api/common/attachment';

import { requestClient } from '#/api/request';

export namespace EmployeeRegularBillApi {
  /** 员工转正申请单信息 */
  export interface EmployeeRegularBill {
    id?: number;
    billCode: string;
    processInstanceId?: string;
    processStatus?: number;
    // 员工信息
    employeeId: number;
    employeeNo?: string;
    name: string;
    sex?: number;
    empDeptId: number;
    empDeptName: string;
    empCompanyId: number;
    empCompanyName: string;
    jobPost?: string;
    jobPosition?: string;
    employeeStatus: number;
    entryDate?: Date;
    formalDate?: Date;
    expectedFormalDate?: Date;
    probationPeriod?: number;
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

  /** 员工转正申请单分页请求 */
  export interface EmployeeRegularBillPageReqVO extends PageParam {
    billCode?: string;
    processStatus?: number;
    employeeNo?: string;
    name?: string;
    empDeptId?: number;
    empDeptName?: string;
    empCompanyId?: number;
    empCompanyName?: string;
    employeeStatus?: number;
    creator?: string;
    createTime?: Date[];
  }
}

/** 查询员工转正申请单列表 */
export function getEmployeeRegularBillPage(
  params: EmployeeRegularBillApi.EmployeeRegularBillPageReqVO,
) {
  return requestClient.get<
    PageResult<EmployeeRegularBillApi.EmployeeRegularBill>
  >('/hrm/employee-regular-bill/page', { params });
}

/** 查询员工转正申请单详情 */
export function getEmployeeRegularBill(id: number) {
  return requestClient.get<EmployeeRegularBillApi.EmployeeRegularBill>(
    `/hrm/employee-regular-bill/get?id=${id}`,
  );
}

/** 新增员工转正申请单 */
export function createEmployeeRegularBill(
  data: EmployeeRegularBillApi.EmployeeRegularBill,
) {
  return requestClient.post('/hrm/employee-regular-bill/create', data);
}

/** 保存员工转正申请单 */
export function saveEmployeeRegularBill(
  data: EmployeeRegularBillApi.EmployeeRegularBill,
) {
  return requestClient.post('/hrm/employee-regular-bill/save', data);
}

/** 提交员工转正申请单 */
export function submitEmployeeRegularBill(
  data: EmployeeRegularBillApi.EmployeeRegularBill,
) {
  return requestClient.post('/hrm/employee-regular-bill/submit', data);
}

/** 修改员工转正申请单 */
export function updateEmployeeRegularBill(
  data: EmployeeRegularBillApi.EmployeeRegularBill,
) {
  return requestClient.put('/hrm/employee-regular-bill/update', data);
}

/** 删除员工转正申请单 */
export function deleteEmployeeRegularBill(id: number) {
  return requestClient.delete(`/hrm/employee-regular-bill/delete?id=${id}`);
}

/** 批量删除员工转正申请单 */
export function deleteEmployeeRegularBillList(ids: number[]) {
  return requestClient.delete(
    `/hrm/employee-regular-bill/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出员工转正申请单 */
export function exportEmployeeRegularBill(params: any) {
  return requestClient.download('/hrm/employee-regular-bill/export-excel', {
    params,
  });
}
