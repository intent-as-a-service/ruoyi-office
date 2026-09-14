import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace SystemHomePageApi {
  /** 首页信息 */
  export interface HomePage {
    id?: number;
    name: string;
    code: string;
    description?: string;
    previewImage?: string;
    isDefault: boolean;
    status: number;
    sort: number;
    creator?: string;
    /** 使用状态：使用中 / 空字符串 */
    useStatus?: string;
    createTime?: Date;
  }

  /** 首页布局项 */
  export interface HomePageLayoutItem {
    id?: number;
    pageId: number;
    componentCode: string;
    positionX: number;
    positionY: number;
    width: number;
    height: number;
    config: string; // JSON字符串
    sort: number;
    createTime?: Date;
  }

  /** 保存布局请求 */
  export interface SaveLayoutReqVO {
    pageId: number;
    layoutJson: string; // 布局JSON字符串
  }

  /** 首页分页查询参数 */
  export interface HomePagePageReqVO extends PageParam {
    name?: string;
    code?: string;
    status?: number;
  }
}

/** 查询首页分页 */
export function getHomePagePage(params: SystemHomePageApi.HomePagePageReqVO) {
  return requestClient.get<PageResult<SystemHomePageApi.HomePage>>(
    '/system/home/page/page',
    { params },
  );
}

/** 查询首页详情 */
export function getHomePage(id: number) {
  return requestClient.get<SystemHomePageApi.HomePage>(
    `/system/home/page/get?id=${id}`,
  );
}

/** 新增首页 */
export function createHomePage(data: SystemHomePageApi.HomePage) {
  return requestClient.post('/system/home/page/create', data);
}

/** 修改首页 */
export function updateHomePage(data: SystemHomePageApi.HomePage) {
  return requestClient.put('/system/home/page/update', data);
}

/** 删除首页 */
export function deleteHomePage(id: number) {
  return requestClient.delete(`/system/home/page/delete?id=${id}`);
}

/** 获取简单首页列表 */
export function getSimpleHomePageList() {
  return requestClient.get<SystemHomePageApi.HomePage[]>(
    '/system/home/page/simple-list',
  );
}

/** 获取当前用户的首页 */
export function getMyHomePage() {
  return requestClient.get<SystemHomePageApi.HomePage>(
    '/system/home/page/my-home',
  );
}

/** 启用首页 */
export function enableHomePage(pageId: number) {
  return requestClient.post(`/system/home/page/enable?pageId=${pageId}`);
}

/** 设置为我的首页 */
export function setMyHomePage(id: number) {
  return requestClient.put(`/system/home/page/set-my-home?id=${id}`);
}

/** 保存首页布局 */
export function saveHomePageLayout(data: SystemHomePageApi.SaveLayoutReqVO) {
  return requestClient.post('/system/home/page/layout/save', data);
}

/** 获取首页布局列表 */
export function getHomePageLayoutList(pageId: number) {
  return requestClient.get<SystemHomePageApi.HomePageLayoutItem[]>(
    `/system/home/page/layout/list?pageId=${pageId}`,
  );
}
