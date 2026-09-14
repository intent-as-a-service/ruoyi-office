import { requestClient } from '#/api/request';

export namespace CategoryApi {
  /** 资产类别信息 */
  export interface Category {
    id: number; // 主键
    categoryCode?: string; // 类别编码
    categoryName: string; // 类别名称
    parentId?: number; // 上级id
    level: number; // 级别
    sort: number; // 显示顺序
    status?: number; // 状态（0正常 1停用）
    remark: string; // 备注
    children?: Category[];
  }
}

/** 查询资产类别列表 */
export function getCategoryList(params: any) {
  return requestClient.get<CategoryApi.Category[]>('/asset/category/list', {
    params,
  });
}

/** 查询资产类别详情 */
export function getCategory(id: number) {
  return requestClient.get<CategoryApi.Category>(
    `/asset/category/get?id=${id}`,
  );
}

/** 新增资产类别 */
export function createCategory(data: CategoryApi.Category) {
  return requestClient.post('/asset/category/create', data);
}

/** 修改资产类别 */
export function updateCategory(data: CategoryApi.Category) {
  return requestClient.put('/asset/category/update', data);
}

/** 删除资产类别 */
export function deleteCategory(id: number) {
  return requestClient.delete(`/asset/category/delete?id=${id}`);
}

/** 导出资产类别 */
export function exportCategory(params: any) {
  return requestClient.download('/asset/category/export-excel', params);
}
