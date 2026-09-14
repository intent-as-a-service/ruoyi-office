import type { PageParam, PageResult } from '@vben/request';

import type { AttachmentApi } from '#/api/common/attachment';

import { requestClient } from '#/api/request';

export namespace MeetingRoomBookingApi {
  /** 会议室预定申请单 */
  export interface MeetingRoomBooking {
    id?: number;
    billCode?: string;
    processInstanceId?: string;
    processStatus?: number;
    roomId?: number;
    roomName?: string;
    roomLocation?: string;
    roomType?: number;
    meetingTitle?: string;
    meetingStartTime?: Date;
    meetingEndTime?: Date;
    moderatorId?: number;
    moderatorName?: string;
    meetingRemark?: string;
    reminderType?: number;
    attendees?: number[];
    attendeeNames?: string[];
    attachments?: AttachmentApi.AttachmentSaveReq[];
    useStatus?: number;
    creator?: number;
    creatorName?: string;
    companyId?: number;
    companyName?: string;
    deptId?: number;
    deptName?: string;
    remark?: string;
    createTime?: Date;
  }

  /** 会议室预定申请单分页请求 */
  export interface MeetingRoomBookingPageReqVO extends PageParam {
    billCode?: string;
    roomName?: string;
    meetingTitle?: string;
    moderatorName?: string;
    processStatus?: number;
    useStatus?: number;
    meetingStartTime?: Date[];
    meetingEndTime?: Date[];
    createTime?: Date[];
    companyId?: number;
    creator?: number;
  }
}

/** 查询会议室预定申请单列表 */
export function getMeetingRoomBookingPage(
  params: MeetingRoomBookingApi.MeetingRoomBookingPageReqVO,
) {
  return requestClient.get<
    PageResult<MeetingRoomBookingApi.MeetingRoomBooking>
  >('/oa/meeting-room-booking/page', { params });
}

/** 查询会议室预定申请单详情 */
export function getMeetingRoomBooking(id: number) {
  return requestClient.get<MeetingRoomBookingApi.MeetingRoomBooking>(
    `/oa/meeting-room-booking/get?id=${id}`,
  );
}

/** 保存会议室预定申请单 */
export function saveMeetingRoomBooking(
  data: MeetingRoomBookingApi.MeetingRoomBooking,
) {
  return requestClient.post<number>('/oa/meeting-room-booking/save', data);
}

/** 提交会议室预定申请单 */
export function submitMeetingRoomBooking(
  data: MeetingRoomBookingApi.MeetingRoomBooking,
) {
  return requestClient.post<number>('/oa/meeting-room-booking/submit', data);
}

/** 删除会议室预定申请单 */
export function deleteMeetingRoomBooking(id: number) {
  return requestClient.delete(`/oa/meeting-room-booking/delete?id=${id}`);
}

/** 批量删除会议室预定申请单 */
export function deleteMeetingRoomBookingList(ids: number[]) {
  return requestClient.delete(
    `/oa/meeting-room-booking/delete-list?ids=${ids.join(',')}`,
  );
}

/** 导出会议室预定申请单 */
export function exportMeetingRoomBookingExcel(params: any) {
  return requestClient.download('/oa/meeting-room-booking/export-excel', {
    params,
  });
}

/** 更新会议室预定申请单的使用状态 */
export function updateMeetingRoomBookingUseStatus(
  id: number,
  useStatus: number,
) {
  return requestClient.put<boolean>(
    `/oa/meeting-room-booking/update-use-status?id=${id}&useStatus=${useStatus}`,
  );
}

/** 查询会议室预约信息（用于展示预约时间网格） */
export namespace MeetingRoomBookingScheduleApi {
  export interface BookingItem {
    id?: number;
    billCode?: string;
    meetingTitle?: string;
    meetingStartTime?: Date;
    meetingEndTime?: Date;
    moderatorName?: string;
    creatorName?: string;
    processStatus?: number;
    useStatus?: number;
  }

  export interface ScheduleResp {
    bookings?: BookingItem[];
    todayApprovedBookings?: BookingItem[];
  }

  export interface ScheduleReq {
    roomId: number;
    startDate: string; // YYYY-MM-DD
    endDate: string; // YYYY-MM-DD
  }
}

export function getMeetingRoomBookingSchedule(
  params: MeetingRoomBookingScheduleApi.ScheduleReq,
) {
  return requestClient.get<MeetingRoomBookingScheduleApi.ScheduleResp>(
    '/oa/meeting-room-booking/schedule',
    { params },
  );
}
