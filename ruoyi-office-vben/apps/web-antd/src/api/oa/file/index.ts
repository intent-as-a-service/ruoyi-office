import type { Dayjs } from 'dayjs';

import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace FileApi {
  /** 企业云盘文件信息 */
  export interface FileInfo {
    id?: number; // 文件ID
    parentId?: number; // 父文件夹ID
    fileType?: number; // 文件类型(0文件 1文件夹)
    fileId?: number; // 文件ID
    fileName?: string; // 文件名称
    fileExtension?: string; // 文件扩展名
    fileSuffix?: string; // 文件后缀名（不含点号）
    fileCategory?: string; // 文件分类（all全部 image图片 document文档 video视频 audio音频 archive压缩包 other其他）
    fileSize?: number; // 文件大小(字节)
    filePath?: string; // 文件存储路径
    fileUrl?: string; // 文件访问URL
    fileMd5?: string; // 文件MD5值
    ownerId?: number; // 所有者ID
    ownerName?: string; // 所有者名称
    deptId?: number; // 部门ID
    deptName?: string; // 部门名称
    isShared?: boolean; // 是否共享文件夹
    shareType?: number; // 文件夹分享类型(0人员 1组织)
    sharePermission?: number; // 分享权限(0仅查看 1可管理)
    sortOrder?: number; // 排序
    createTime?: Dayjs | string; // 创建时间
    updateTime?: Dayjs | string; // 更新时间
    isFavorite?: boolean; // 是否收藏
  }
}

/** 查询文件/文件夹分页 */
export function getFileInfoPage(params: PageParam) {
  return requestClient.get<PageResult<FileApi.FileInfo>>('/oa/file/page', {
    params,
  });
}

/** 查询文件/文件夹详情 */
export function getFileInfo(id: number) {
  return requestClient.get<FileApi.FileInfo>(`/oa/file/get?id=${id}`);
}

/** 查询指定文件夹下的文件列表 */
export function getFileInfoListByParentId(parentId: number) {
  return requestClient.get<FileApi.FileInfo[]>(
    `/oa/file/list?parentId=${parentId}`,
  );
}

/** 新增文件/文件夹 */
export function createFileInfo(data: FileApi.FileInfo) {
  return requestClient.post('/oa/file/create', data);
}

/** 修改文件/文件夹 */
export function updateFileInfo(data: FileApi.FileInfo) {
  return requestClient.put('/oa/file/update', data);
}

/** 删除文件/文件夹 */
export function deleteFileInfo(id: number) {
  return requestClient.delete(`/oa/file/delete?id=${id}`);
}

/** 批量删除文件/文件夹 */
export function deleteFileInfoListByIds(ids: number[]) {
  return requestClient.delete(`/oa/file/delete-list?ids=${ids.join(',')}`);
}

/** 移动文件/文件夹 */
export function moveFileInfo(id: number, targetParentId: number) {
  return requestClient.put(
    `/oa/file/move?id=${id}&targetParentId=${targetParentId}`,
  );
}

/** 重命名文件/文件夹 */
export function renameFileInfo(id: number, newName: string) {
  return requestClient.put(
    `/oa/file/rename?id=${id}&newName=${encodeURIComponent(newName)}`,
  );
}

/** 收藏文件 */
export function favoriteFile(fileId: number) {
  return requestClient.post(`/oa/file/favorite?fileId=${fileId}`);
}

/** 取消收藏文件 */
export function unfavoriteFile(fileId: number) {
  return requestClient.delete(`/oa/file/unfavorite?fileId=${fileId}`);
}

/** 获取收藏的文件列表 */
export function getFavoriteFileList() {
  return requestClient.get<FileApi.FileInfo[]>('/oa/file/favorite-list');
}

/** 上传文件 */
export function uploadFile(
  file: File,
  parentId: number = 0,
  onUploadProgress?: (progressEvent: any) => void,
) {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('parentId', parentId.toString());
  return requestClient.post<number>('/oa/file/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
    timeout: 300_000, // 5分钟超时
    onUploadProgress,
  });
}

// ==================== 文件分享相关接口 ====================

export interface FileShareTarget {
  shareType: number; // 0人员 1组织
  targetId: number;
  targetName: string;
  permission: number; // 0仅查看 1可管理
}

export interface FileShareReq {
  fileId: number;
  shareTargets: FileShareTarget[];
  inheritPermission?: boolean;
  remark?: string;
}

export interface FileShareInfo {
  id: number;
  shareType: number;
  shareTypeName: string;
  targetId: number;
  targetName: string;
  permission: number;
  permissionName: string;
  inheritPermission: boolean;
  accessCount: number;
  createTime: string;
}

export interface FileShareResp {
  fileId: number;
  fileName: string;
  fileType: number;
  fileSize: number;
  ownerName: string;
  isShared: boolean;
  shareTargets: FileShareInfo[];
  createTime: string;
}

export interface SharedFileInfo {
  fileId: number;
  fileName: string;
  fileType: number;
  fileSize: number;
  fileExtension: string;
  fileSuffix: string;
  fileCategory?: string; // 文件分类（all全部 image图片 document文档 video视频 audio音频 archive压缩包 other其他）
  ownerId: number;
  ownerName: string;
  deptName: string;
  userPermission: number;
  permissionName: string;
  sharePath: string;
  rootShareId: number;
  isRootShare: boolean;
  canEdit: boolean;
  canDelete: boolean;
  canShare: boolean;
  isFavorite?: boolean; // 是否收藏
  createTime: string;
  updateTime: string;
}

/** 分享文件 */
export function shareFile(data: FileShareReq) {
  return requestClient.post<number>('/oa/file/share', data);
}

/** 取消分享 */
export function unshareFile(
  fileId: number,
  shareType: number,
  targetId: number,
) {
  return requestClient.delete('/oa/file/unshare', {
    params: { fileId, shareType, targetId },
  });
}

/** 获取文件分享信息 */
export function getFileShareInfo(fileId: number) {
  return requestClient.get<FileShareResp>('/oa/file/share-info', {
    params: { fileId },
  });
}

/** 获取共享文件列表(根级别) */
export function getSharedFileList() {
  return requestClient.get<SharedFileInfo[]>('/oa/file/shared-list');
}

/** 获取共享文件夹下的子文件列表 */
export function getSharedSubFiles(rootShareId: number, parentId: number) {
  return requestClient.get<SharedFileInfo[]>('/oa/file/shared-sub-files', {
    params: { rootShareId, parentId },
  });
}

/** 检查文件权限 */
export function checkFilePermission(fileId: number) {
  return requestClient.get<number>('/oa/file/check-permission', {
    params: { fileId },
  });
}

/** 导出文件信息 */
export function exportFileInfo(params: any) {
  return requestClient.download('/oa/file/export-excel', params);
}

// ==================== 文件存储统计相关接口 ====================

/** 文件存储统计信息 */
export interface FileStorageStats {
  usedSize: number; // 已用空间（字节）
  totalSize: number; // 总空间限制（字节）
  fileCount: number; // 文件数量（仅文件，不包括文件夹）
  sharedFileCount: number; // 共享文件数量
}

/** 获取用户文件存储统计信息 */
export function getFileStorageStats() {
  return requestClient.get<FileStorageStats>('/oa/file/storage-stats');
}
