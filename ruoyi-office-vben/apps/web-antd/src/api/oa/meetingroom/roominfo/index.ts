import type { PageParam, PageResult } from '@vben/request';

import { requestClient } from '#/api/request';

export namespace MeetingRoomApi {
  /** 会议室信息 */
  export interface MeetingRoom {
    id?: number; // ID
    roomName?: string; // 会议室名称
    roomLocation?: string; // 会议室位置
    roomType?: number; // 会议室类型
    managerId?: number; // 负责人ID
    managerName?: string; // 负责人姓名
    managerPhone?: string; // 负责人联系方式
    availableStatus?: number; // 可用状态（0正常 1维修中 2不可用）
    picUrl?: string; // 会议室图片URL
    seatCount?: number; // 坐席数
    equipment?: string[]; // 会议室设备（数组）
    attachmentUrl?: string; // 附件URL
    remark?: string; // 备注
    allowBooking?: boolean; // 允许预定
    needApproval?: boolean; // 预定需审批
    bookingScope?: number; // 可用范围（0全部成员 1指定成员）
    bookingMembers?: number[]; // 可预定成员ID（数组）
    sort?: number; // 显示顺序
    createTime?: string; // 创建时间
  }
}

/** 查询会议室信息分页 */
export function getMeetingRoomPage(params: PageParam) {
  return requestClient.get<PageResult<MeetingRoomApi.MeetingRoom>>(
    '/oa/meeting-room/page',
    { params },
  );
}

/** 查询会议室信息详情 */
export function getMeetingRoom(id: number) {
  return requestClient.get<MeetingRoomApi.MeetingRoom>(
    `/oa/meeting-room/get?id=${id}`,
  );
}

/** 新增会议室信息 */
export function createMeetingRoom(data: MeetingRoomApi.MeetingRoom) {
  return requestClient.post('/oa/meeting-room/create', data);
}

/** 修改会议室信息 */
export function updateMeetingRoom(data: MeetingRoomApi.MeetingRoom) {
  return requestClient.put('/oa/meeting-room/update', data);
}

/** 删除会议室信息 */
export function deleteMeetingRoom(id: number) {
  return requestClient.delete(`/oa/meeting-room/delete?id=${id}`);
}

/** 批量删除会议室信息 */
export function deleteMeetingRoomListByIds(ids: number[]) {
  return requestClient.delete(
    `/oa/meeting-room/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出会议室信息 */
export function exportMeetingRoom(params: any) {
  return requestClient.download('/oa/meeting-room/export-excel', params);
}

/** 获取会议室下拉列表（用于选择器） */
export function getMeetingRoomSelectList() {
  return requestClient.get<MeetingRoomApi.MeetingRoom[]>(
    '/oa/meeting-room/simple-list',
  );
}

/** 查询可预定的会议室信息分页（用于会议预定单选择会议室） */
export function getBookableMeetingRoomPage(params: PageParam) {
  return requestClient.get<PageResult<MeetingRoomApi.MeetingRoom>>(
    '/oa/meeting-room/bookable-page',
    { params },
  );
}
