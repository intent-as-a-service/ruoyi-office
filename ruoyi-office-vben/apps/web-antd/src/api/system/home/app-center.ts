import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace SystemHomeAppCenterApi {
  /** 系统级应用配置 */
  export interface AppConfig {
    id?: number;
    menuId: number;
    name: string;
    icon?: string;
    color?: string;
    description?: string;
    sort: number;
    status: number;
    creator?: string;
    createTime?: Date;
    updater?: string;
    updateTime?: Date;
  }

  /** 用户级应用配置 */
  export interface AppUser {
    id?: number;
    userId: number;
    menuId: number;
    name?: string;
    icon?: string;
    color?: string;
    sort: number;
    status: number;
    creator?: string;
    createTime?: Date;
    updater?: string;
    updateTime?: Date;
  }

  /** 应用配置响应（包含菜单信息） */
  export interface AppConfigVO extends AppConfig {
    menuName?: string;
    menuPath?: string;
    menuIcon?: string;
  }

  /** 用户应用配置响应（包含菜单信息） */
  export interface AppUserVO extends AppUser {
    menuName?: string;
    menuPath?: string;
    menuIcon?: string;
  }

  /** 菜单选项（用于添加应用时选择） */
  export interface MenuOption {
    id: number;
    name: string;
    path?: string;
    icon?: string;
    parentId?: number;
  }
}

// ============================================
// 系统级应用配置 API
// ============================================

/** 查询系统级应用配置分页 */
export function getAppConfigPage(params: PageParam) {
  return requestClient.get<PageResult<SystemHomeAppCenterApi.AppConfigVO>>(
    '/system/home-app-config/page',
    { params },
  );
}

/** 查询系统级应用配置列表 */
export function getAppConfigList() {
  return requestClient.get<SystemHomeAppCenterApi.AppConfigVO[]>(
    '/system/home-app-config/list',
  );
}

/** 查询系统级应用配置详情 */
export function getAppConfig(id: number) {
  return requestClient.get<SystemHomeAppCenterApi.AppConfigVO>(
    `/system/home-app-config/get?id=${id}`,
  );
}

/** 新增系统级应用配置 */
export function createAppConfig(data: SystemHomeAppCenterApi.AppConfig) {
  return requestClient.post('/system/home-app-config/create', data);
}

/** 修改系统级应用配置 */
export function updateAppConfig(data: SystemHomeAppCenterApi.AppConfig) {
  return requestClient.put('/system/home-app-config/update', data);
}

/** 删除系统级应用配置 */
export function deleteAppConfig(id: number) {
  return requestClient.delete(`/system/home-app-config/delete?id=${id}`);
}

/** 更新系统级应用配置排序 */
export function updateAppConfigSort(data: { id: number; sort: number }[]) {
  return requestClient.put('/system/home-app-config/update-sort', data);
}

// ============================================
// 用户级应用配置 API
// ============================================

/** 查询当前用户的应用配置列表 */
export function getUserAppList() {
  return requestClient.get<SystemHomeAppCenterApi.AppUserVO[]>(
    '/system/home-app-user/my-list',
  );
}

/** 查询当前用户的应用配置详情 */
export function getUserApp(id: number) {
  return requestClient.get<SystemHomeAppCenterApi.AppUserVO>(
    `/system/home-app-user/get?id=${id}`,
  );
}

/** 新增用户应用配置 */
export function createUserApp(data: {
  color?: string;
  icon?: string;
  menuId: number;
  name?: string;
}) {
  return requestClient.post('/system/home-app-user/create', data);
}

/** 修改用户应用配置 */
export function updateUserApp(data: {
  color?: string;
  icon?: string;
  id: number;
  name?: string;
  status?: number;
}) {
  return requestClient.put('/system/home-app-user/update', data);
}

/** 删除用户应用配置 */
export function deleteUserApp(id: number) {
  return requestClient.delete(`/system/home-app-user/delete?id=${id}`);
}

/** 批量删除用户应用配置 */
export function deleteUserAppBatch(ids: number[]) {
  return requestClient.delete('/system/home-app-user/delete-batch', {
    data: { ids },
  });
}

/** 更新用户应用配置排序 */
export function updateUserAppSort(data: { id: number; sort: number }[]) {
  return requestClient.put('/system/home-app-user/update-sort', data);
}

/** 初始化用户应用配置（从系统配置复制） */
export function initUserApp() {
  return requestClient.post('/system/home-app-user/init');
}

/** 重置用户应用配置（恢复为系统默认） */
export function resetUserApp() {
  return requestClient.post('/system/home-app-user/reset');
}

// ============================================
// 菜单选项 API（用于添加应用时选择）
// ============================================

/** 获取当前用户有权限的菜单列表（用于选择应用） */
export function getUserMenuOptions() {
  return requestClient.get<SystemHomeAppCenterApi.MenuOption[]>(
    '/system/home-app-user/menu-options',
  );
}
