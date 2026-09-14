import type { PageParam, PageResult } from '@vben/request';
import type { Dayjs } from 'dayjs';

import { requestClient } from '#/api/request';

export namespace CarApi {
  /** 车辆信息信息 */
  export interface Car {
    id?: number; // ID
    carNo?: string; // 车牌号
    carName?: string; // 车辆名称
    carType?: number; // 车型
    carCls?: number; // 分类
    status?: number; // 状态
    brand?: string; // 品牌型号
    seatNum?: string; // 车座
    barePrice?: number; // 裸车价
    forceInsuranceDate?: string | Dayjs; // 交强险到期日期
    businessInsuranceDate?: string | Dayjs; // 商业险到期日期
    yearCheckDate?: string | Dayjs; // 年检日期
    picUrl?: string; // 上传照片
    sort?: number; // 显示顺序
    remark?: string; // 备注
    companyId?: number; // 公司ID
    companyName?: string; // 公司名称
  }
}

/** 查询车辆信息分页 */
export function getCarPage(params: PageParam) {
  return requestClient.get<PageResult<CarApi.Car>>('/oa/car/page', { params });
}

/** 查询车辆信息详情 */
export function getCar(id: number) {
  return requestClient.get<CarApi.Car>(`/oa/car/get?id=${id}`);
}

/** 新增车辆信息 */
export function createCar(data: CarApi.Car) {
  return requestClient.post('/oa/car/create', data);
}

/** 修改车辆信息 */
export function updateCar(data: CarApi.Car) {
  return requestClient.put('/oa/car/update', data);
}

/** 删除车辆信息 */
export function deleteCar(id: number) {
  return requestClient.delete(`/oa/car/delete?id=${id}`);
}

/** 批量删除车辆信息 */
export function deleteCarListByIds(ids: number[]) {
  return requestClient.delete(`/oa/car/delete-list?ids=${ids.join(',')}`)
}

/** 导出车辆信息 */
export function exportCar(params: any) {
  return requestClient.download('/oa/car/export-excel', params);
}


