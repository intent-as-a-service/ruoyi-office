import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace SystemHomeComponentApi {
  /** 组件信息 */
  export interface Component {
    id?: number;
    categoryId: number;
    name: string;
    code: string;
    componentPath: string;
    description?: string;
    previewImage?: string;
    defaultWidth: number;
    defaultHeight: number;
    configSchema: string; // JSON字符串
    status: number;
    sort: number;
    createTime?: Date;
  }

  /** 组件分类 */
  export interface ComponentCategory {
    id?: number;
    name: string;
    code: string;
    icon: string;
    sort: number;
    createTime?: Date;
  }

  /** 组件分页查询参数 */
  export interface ComponentPageReqVO extends PageParam {
    name?: string;
    code?: string;
    categoryId?: number;
    status?: number;
  }

  /** 配置Schema属性 */
  export interface ConfigSchemaProperty {
    key: string;
    label: string;
    type: 'boolean' | 'color' | 'number' | 'string';
    default?: any;
    required: boolean;
    min?: number;
    max?: number;
  }

  /** 配置Schema */
  export interface ConfigSchema {
    properties: ConfigSchemaProperty[];
  }
}

/** 查询组件分页 */
export function getComponentPage(
  params: SystemHomeComponentApi.ComponentPageReqVO,
) {
  return requestClient.get<PageResult<SystemHomeComponentApi.Component>>(
    '/system/home/component/page',
    { params },
  );
}

/** 查询组件详情 */
export function getComponent(id: number) {
  return requestClient.get<SystemHomeComponentApi.Component>(
    `/system/home/component/get?id=${id}`,
  );
}

/** 新增组件 */
export function createComponent(data: SystemHomeComponentApi.Component) {
  return requestClient.post('/system/home/component/create', data);
}

/** 修改组件 */
export function updateComponent(data: SystemHomeComponentApi.Component) {
  return requestClient.put('/system/home/component/update', data);
}

/** 删除组件 */
export function deleteComponent(id: number) {
  return requestClient.delete(`/system/home/component/delete?id=${id}`);
}

/** 获取可用组件列表 */
export function getAvailableComponentList() {
  return requestClient.get<SystemHomeComponentApi.Component[]>(
    '/system/home/component/available-list',
  );
}

/** 按分类获取组件 */
export function getComponentsByCategory() {
  return requestClient.get<Record<number, SystemHomeComponentApi.Component[]>>(
    '/system/home/component/by-category',
  );
}

/** 获取分类列表 */
export function getCategoryList() {
  return requestClient.get<SystemHomeComponentApi.ComponentCategory[]>(
    '/system/home/component/category/list',
  );
}
