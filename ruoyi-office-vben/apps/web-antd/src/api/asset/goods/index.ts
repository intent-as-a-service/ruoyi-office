import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace GoodsApi {
  /** 物品信息信息 */
  export interface Goods {
    id: number; // 主键
    goodsCode?: string; // 物品编码
    goodsName: string; // 物品名称
    assetCategoryCode: string; // 资产类型编码
    assetCategoryName: string; // 资产类型名称
    assetModel: string; // 规格型号
    assetUnit: string; // 计量单位
    manufacturer: string; // 厂商
    brand: string; // 品牌
    residualValueRate: number; // 默认月残值率
    inventoryLowerLimit: number; // 库存下限
    inventoryLimit: number; // 库存上下限
    isJoinAsset: string; // 是否进入资产列表
    assetIcon: string; // 资产图片
    assetFile: string; // 资产附件
    sort: number; // 显示顺序
    status?: number; // 状态（0正常 1停用）
    storeAddress: string; // 仓库地址
    remark: string; // 备注
  }
}

/** 查询物品信息分页 */
export function getGoodsPage(params: PageParam) {
  return requestClient.get<PageResult<GoodsApi.Goods>>('/asset/goods/page', { params });
}

/** 查询物品信息详情 */
export function getGoods(id: number) {
  return requestClient.get<GoodsApi.Goods>(`/asset/goods/get?id=${id}`);
}

/** 新增物品信息 */
export function createGoods(data: GoodsApi.Goods) {
  return requestClient.post('/asset/goods/create', data);
}

/** 修改物品信息 */
export function updateGoods(data: GoodsApi.Goods) {
  return requestClient.put('/asset/goods/update', data);
}

/** 删除物品信息 */
export function deleteGoods(id: number) {
  return requestClient.delete(`/asset/goods/delete?id=${id}`);
}

/** 批量删除物品信息 */
export function deleteGoodsListByIds(ids: number[]) {
  return requestClient.delete(`/asset/goods/delete-list?ids=${ids.join(',')}`)
}

/** 导出物品信息 */
export function exportGoods(params: any) {
  return requestClient.download('/asset/goods/export-excel', params);
}

