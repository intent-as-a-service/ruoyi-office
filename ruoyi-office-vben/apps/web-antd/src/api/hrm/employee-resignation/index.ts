import type { PageParam, PageResult } from '@vben/request';

import type { AttachmentApi } from '#/api/common/attachment';

import { requestClient } from '#/api/request';

export namespace EmployeeResignationBillApi {
  /** 员工离职申请单信息 */
  export interface EmployeeResignationBill {
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
    // 离职信息
    resignationType?: string;
    applicationDate?: Date;
    resignationDate?: Date;
    lastWorkingDate?: Date;
    handoverPersonId?: number;
    handoverPersonName?: string;
    resignationReason?: string;
    resignationReasonDesc?: string;
    salarySettlementDate?: Date;
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

  /** 员工离职申请单分页请求 */
  export interface EmployeeResignationBillPageReqVO extends PageParam {
    billCode?: string;
    processStatus?: number;
    employeeNo?: string;
    name?: string;
    empDeptId?: number;
    empDeptName?: string;
    empCompanyId?: number;
    empCompanyName?: string;
    employeeStatus?: number;
    resignationType?: string;
    resignationReason?: string;
    creator?: string;
    createTime?: Date[];
  }
}

/** 查询员工离职申请单列表 */
export function getEmployeeResignationBillPage(
  params: EmployeeResignationBillApi.EmployeeResignationBillPageReqVO,
) {
  return requestClient.get<
    PageResult<EmployeeResignationBillApi.EmployeeResignationBill>
  >('/hrm/employee-resignation-bill/page', { params });
}

/** 查询员工离职申请单详情 */
export function getEmployeeResignationBill(id: number) {
  return requestClient.get<EmployeeResignationBillApi.EmployeeResignationBill>(
    `/hrm/employee-resignation-bill/get?id=${id}`,
  );
}

/** 新增员工离职申请单 */
export function createEmployeeResignationBill(
  data: EmployeeResignationBillApi.EmployeeResignationBill,
) {
  return requestClient.post('/hrm/employee-resignation-bill/create', data);
}

/** 保存员工离职申请单 */
export function saveEmployeeResignationBill(
  data: EmployeeResignationBillApi.EmployeeResignationBill,
) {
  return requestClient.post('/hrm/employee-resignation-bill/save', data);
}

/** 提交员工离职申请单 */
export function submitEmployeeResignationBill(
  data: EmployeeResignationBillApi.EmployeeResignationBill,
) {
  return requestClient.post('/hrm/employee-resignation-bill/submit', data);
}

/** 修改员工离职申请单 */
export function updateEmployeeResignationBill(
  data: EmployeeResignationBillApi.EmployeeResignationBill,
) {
  return requestClient.put('/hrm/employee-resignation-bill/update', data);
}

/** 删除员工离职申请单 */
export function deleteEmployeeResignationBill(id: number) {
  return requestClient.delete(`/hrm/employee-resignation-bill/delete?id=${id}`);
}

/** 批量删除员工离职申请单 */
export function deleteEmployeeResignationBillList(ids: number[]) {
  return requestClient.delete(
    `/hrm/employee-resignation-bill/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出员工离职申请单 */
export function exportEmployeeResignationBill(params: any) {
  return requestClient.download('/hrm/employee-resignation-bill/export-excel', {
    params,
  });
}
