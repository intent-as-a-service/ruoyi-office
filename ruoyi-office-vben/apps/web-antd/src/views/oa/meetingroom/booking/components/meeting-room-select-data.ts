import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MeetingRoomApi } from '#/api/oa/meetingroom/roominfo';

import { h } from 'vue';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import { Tag } from 'ant-design-vue';

/** 会议室选择搜索表单配置 */
export function useMeetingRoomSelectFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'roomName',
      label: '会议室名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入会议室名称',
      },
    },
    {
      fieldName: 'roomLocation',
      label: '会议室位置',
      component: 'Input',
      componentProps: {
        placeholder: '请输入会议室位置',
      },
    },
    {
      fieldName: 'roomType',
      label: '会议室类型',
      component: 'Select',
      componentProps: {
        placeholder: '请选择会议室类型',
        options: getDictOptions(DICT_TYPE.OA_MEETING_ROOM_TYPE, 'number'),
      },
    },
    {
      fieldName: 'availableStatus',
      label: '可用状态',
      component: 'Select',
      componentProps: {
        placeholder: '请选择可用状态',
        options: getDictOptions(DICT_TYPE.OA_MEETING_ROOM_STATUS, 'number'),
      },
    },
  ];
}

/** 会议室选择表格列配置 */
export function useMeetingRoomSelectColumns(): VxeTableGridOptions<MeetingRoomApi.MeetingRoom>['columns'] {
  return [
    {
      type: 'radio',
      width: 60,
      align: 'center',
    },
    {
      field: 'roomName',
      title: '会议室名称',
      width: 180,
      slots: {
        default: ({ row }: { row: MeetingRoomApi.MeetingRoom }) => {
          return h(Tag, { color: 'blue' }, { default: () => row.roomName });
        },
      },
    },
    {
      field: 'roomLocation',
      title: '会议室位置',
      width: 200,
    },
    {
      field: 'roomType',
      title: '会议室类型',
      width: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.OA_MEETING_ROOM_TYPE },
      },
    },
    {
      field: 'seatCount',
      title: '坐席数',
      width: 100,
      align: 'center',
    },
    {
      field: 'availableStatus',
      title: '可用状态',
      width: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.OA_MEETING_ROOM_STATUS },
      },
    },
    {
      field: 'managerName',
      title: '负责人',
      width: 120,
    },
    {
      field: 'actions',
      title: '操作',
      width: 120,
      fixed: 'right',
      slots: {
        default: 'actions',
      },
    },
  ];
}
