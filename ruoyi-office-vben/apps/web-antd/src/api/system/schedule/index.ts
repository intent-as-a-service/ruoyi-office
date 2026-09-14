import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace SystemScheduleApi {
  /** 日程信息 */
  export interface Schedule {
    id?: number;
    title: string;
    content?: string;
    scheduleDate: string; // 日期格式：YYYY-MM-DD
    startTime?: string; // 时间格式：HH:mm:ss
    endTime?: string; // 时间格式：HH:mm:ss
    scheduleType?: string; // 日程类型
    scheduleCategory?: string; // 日程分类
    creatorId?: number;
    creatorName?: string;
    isPushed?: boolean;
    status?: number;
    remark?: string;
    receivers?: Receiver[]; // 后端返回的接收人列表（对象数组）
    receiverIds?: number[]; // 前端表单使用的接收人ID列表
    createTime?: Date;
    updateTime?: Date;
  }

  /** 接收人信息 */
  export interface Receiver {
    receiverId: number;
    receiverName: string;
    readStatus?: number; // 0未读 1已读
    readTime?: Date;
  }

  /** 分页查询参数 */
  export interface SchedulePageReq extends PageParam {
    title?: string;
    scheduleDate?: string;
    scheduleDateStart?: string;
    scheduleDateEnd?: string;
    scheduleType?: string;
    scheduleCategory?: string;
    creatorId?: number;
    status?: number;
  }

  /** 按日期查询参数 */
  export interface ScheduleListByDateReq {
    scheduleDate: string; // 日期格式：YYYY-MM-DD
  }

  /** 推送日程参数 */
  export interface SchedulePushReq {
    scheduleId: number;
    receiverIds: number[];
  }
}

/** 查询日程列表 */
export function getSchedulePage(params: SystemScheduleApi.SchedulePageReq) {
  return requestClient.get<PageResult<SystemScheduleApi.Schedule>>(
    '/system/schedule/page',
    { params },
  );
}

/** 查询日程详情 */
export function getSchedule(id: number) {
  return requestClient.get<SystemScheduleApi.Schedule>(
    `/system/schedule/get?id=${id}`,
  );
}

/** 新增日程 */
export function createSchedule(data: SystemScheduleApi.Schedule) {
  return requestClient.post('/system/schedule/create', data);
}

/** 修改日程 */
export function updateSchedule(data: SystemScheduleApi.Schedule) {
  return requestClient.put('/system/schedule/update', data);
}

/** 删除日程 */
export function deleteSchedule(id: number) {
  return requestClient.delete(`/system/schedule/delete?id=${id}`);
}

/** 根据日期查询日程列表 */
export function getScheduleListByDate(
  params: SystemScheduleApi.ScheduleListByDateReq,
) {
  return requestClient.get<SystemScheduleApi.Schedule[]>(
    '/system/schedule/list-by-date',
    { params },
  );
}

/** 获取有日程的日期列表 */
export function getScheduleDates(startDate: string, endDate: string) {
  return requestClient.get<string[]>('/system/schedule/dates', {
    params: { startDate, endDate },
  });
}

/** 获取我的日程分页 */
export function getMySchedulePage(params: SystemScheduleApi.SchedulePageReq) {
  return requestClient.get<PageResult<SystemScheduleApi.Schedule>>(
    '/system/schedule/my-page',
    { params },
  );
}

/** 推送日程给指定用户 */
export function pushSchedule(data: SystemScheduleApi.SchedulePushReq) {
  return requestClient.post('/system/schedule/push', data);
}
