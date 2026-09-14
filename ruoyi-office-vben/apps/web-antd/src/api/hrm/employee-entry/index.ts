import type { PageParam, PageResult } from '@vben/request';

import type { AttachmentApi } from '#/api/common/attachment';

import { requestClient } from '#/api/request';

export namespace EmployeeEntryBillApi {
  /** 员工入职申请单信息 */
  export interface EmployeeEntryBill {
    id?: number;
    billCode: string;
    processInstanceId?: string;
    processStatus?: number;
    companyId?: number;
    companyName?: string;
    deptId?: number;
    deptName?: string;
    // 员工基本信息
    name: string;
    sex: number;
    birthday?: Date;
    idCard?: string;
    mobile: string;
    email?: string;
    nation?: string;
    politicalStatus?: string;
    maritalStatus?: string;
    nativePlace?: string;
    householdAddress?: string;
    currentAddress?: string;
    emergencyContact?: string;
    emergencyPhone?: string;
    avatar?: string;
    // 入职相关信息（员工所属的组织信息）
    entryDate: Date;
    probationPeriod?: number;
    expectedFormalDate?: Date;
    empDeptId: number;
    empDeptName: string;
    empCompanyId: number;
    empCompanyName: string;
    jobPosition?: string;
    jobTitle?: string;
    employeeStatus: number;
    education?: string;
    salary?: number;
    bankName?: string;
    bankAccount?: string;
    // 关联字段
    employeeId?: number;
    creator?: number;
    creatorName?: string;
    remark?: string;
    createTime?: Date;
    attachments?: AttachmentApi.AttachmentSaveReq[];
    // 明细列表
    workExperienceList?: EmployeeWorkExperience[];
    educationList?: EmployeeEducation[];
    familyList?: EmployeeFamily[];
  }

  /** 工作经历 */
  export interface EmployeeWorkExperience {
    id?: number;
    startTime?: string;
    endTime?: string;
    jobPosition?: string;
    companyName?: string;
  }

  /** 教育经历 */
  export interface EmployeeEducation {
    id?: number;
    startTime?: string;
    endTime?: string;
    major?: string;
    schoolName?: string;
  }

  /** 家属信息 */
  export interface EmployeeFamily {
    id?: number;
    name?: string;
    relationship?: string;
    mobile?: string;
    workUnit?: string;
  }

  /** 员工入职申请单分页请求 */
  export interface EmployeeEntryBillPageReqVO extends PageParam {
    billCode?: string;
    processStatus?: number;
    name?: string;
    mobile?: string;
    empDeptId?: number;
    empDeptName?: string;
    empCompanyId?: number;
    empCompanyName?: string;
    employeeStatus?: number;
    entryDate?: Date[];
    createTime?: Date[];
  }
}

/** 查询员工入职申请单列表 */
export function getEmployeeEntryBillPage(
  params: EmployeeEntryBillApi.EmployeeEntryBillPageReqVO,
) {
  return requestClient.get<PageResult<EmployeeEntryBillApi.EmployeeEntryBill>>(
    '/hrm/employee-entry-bill/page',
    { params },
  );
}

/** 查询员工入职申请单详情 */
export function getEmployeeEntryBill(id: number) {
  return requestClient.get<EmployeeEntryBillApi.EmployeeEntryBill>(
    `/hrm/employee-entry-bill/get?id=${id}`,
  );
}

/** 新增员工入职申请单 */
export function createEmployeeEntryBill(
  data: EmployeeEntryBillApi.EmployeeEntryBill,
) {
  return requestClient.post('/hrm/employee-entry-bill/create', data);
}

/** 保存员工入职申请单 */
export function saveEmployeeEntryBill(
  data: EmployeeEntryBillApi.EmployeeEntryBill,
) {
  return requestClient.post('/hrm/employee-entry-bill/save', data);
}

/** 提交员工入职申请单 */
export function submitEmployeeEntryBill(
  data: EmployeeEntryBillApi.EmployeeEntryBill,
) {
  return requestClient.post('/hrm/employee-entry-bill/submit', data);
}

/** 修改员工入职申请单 */
export function updateEmployeeEntryBill(
  data: EmployeeEntryBillApi.EmployeeEntryBill,
) {
  return requestClient.put('/hrm/employee-entry-bill/update', data);
}

/** 删除员工入职申请单 */
export function deleteEmployeeEntryBill(id: number) {
  return requestClient.delete(`/hrm/employee-entry-bill/delete?id=${id}`);
}

/** 批量删除员工入职申请单 */
export function deleteEmployeeEntryBillList(ids: number[]) {
  return requestClient.delete(
    `/hrm/employee-entry-bill/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出员工入职申请单 */
export function exportEmployeeEntryBill(params: any) {
  return requestClient.download('/hrm/employee-entry-bill/export-excel', {
    params,
  });
}
