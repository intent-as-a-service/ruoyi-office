import { requestClient } from '#/api/request';

export namespace AttachmentApi {
  /** 附件信息 */
  export interface Attachment {
    id?: number;
    businessType: string;
    businessId: number;
    fileName: string;
    filePath: string;
    fileUrl: string;
    fileSize: number;
    fileType?: string;
    fileExtension?: string;
    uploadTime?: Date;
    sortOrder?: number;
    remark?: string;
    createTime?: Date;
  }

  /** 附件保存请求 */
  export interface AttachmentSaveReq {
    id?: number;
    businessType: string;
    businessId: number;
    fileName: string;
    filePath: string;
    fileUrl: string;
    fileSize: number;
    fileType?: string;
    fileExtension?: string;
    uploadTime?: Date;
    sortOrder?: number;
    remark?: string;
    rowKey?: string; // 用于标识行唯一性
  }
}

/** 创建附件 */
export function createAttachment(data: AttachmentApi.AttachmentSaveReq) {
  return requestClient.post<number>('/oa/attachment/create', data);
}

/** 更新附件 */
export function updateAttachment(data: AttachmentApi.AttachmentSaveReq) {
  return requestClient.put('/oa/attachment/update', data);
}

/** 删除附件 */
export function deleteAttachment(id: number) {
  return requestClient.delete(`/oa/attachment/delete?id=${id}`);
}

/** 获得附件 */
export function getAttachment(id: number) {
  return requestClient.get<AttachmentApi.Attachment>(`/oa/attachment/get?id=${id}`);
}

/** 根据业务类型和业务ID获取附件列表 */
export function getAttachmentListByBusiness(businessType: string, businessId: number) {
  return requestClient.get<AttachmentApi.Attachment[]>(
    `/oa/attachment/list-by-business?businessType=${businessType}&businessId=${businessId}`
  );
}
