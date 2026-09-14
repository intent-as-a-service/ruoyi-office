import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MeetingRoomBookingApi } from '#/api/oa/meetingroom/booking';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import { createRouterLinkColumn } from '#/adapter/vxe-table';
import { getRangePickerDefaultProps } from '#/utils';
import { getCurrentUserCompanyDeptTree } from '#/utils/dept-tree';

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'billCode',
      label: '单据编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入单据编号',
      },
    },
    {
      fieldName: 'processStatus',
      label: '单据状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(
          DICT_TYPE.BPM_PROCESS_INSTANCE_STATUS,
          'number',
        ),
        placeholder: '请选择单据状态',
      },
    },
    {
      fieldName: 'roomName',
      label: '会议室名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入会议室名称',
      },
    },
    {
      fieldName: 'meetingTitle',
      label: '会议主题',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入会议主题',
      },
    },
    {
      fieldName: 'moderatorName',
      label: '主持人',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入主持人姓名',
      },
    },
    {
      fieldName: 'deptId',
      label: '申请部门',
      component: 'ApiTreeSelect',
      componentProps: {
        allowClear: true,
        api: () => getCurrentUserCompanyDeptTree(false), // false表示不包含公司本身
        labelField: 'name',
        valueField: 'id',
        childrenField: 'children',
        placeholder: '请选择申请部门',
        treeDefaultExpandAll: true,
      },
    },
    {
      fieldName: 'useStatus',
      label: '使用状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(
          DICT_TYPE.OA_MEETING_BOOKING_USE_STATUS,
          'number',
        ),
        placeholder: '请选择使用状态',
      },
    },
    {
      fieldName: 'meetingStartTime',
      label: '会议开始时间',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
        showTime: true,
        format: 'YYYY-MM-DD HH:mm:ss',
        valueFormat: 'YYYY-MM-DD HH:mm:ss',
      },
    },
    {
      fieldName: 'createTime',
      label: '创建时间',
      component: 'RangePicker',
      componentProps: getRangePickerDefaultProps(),
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<MeetingRoomBookingApi.MeetingRoomBooking>['columns'] {
  // 获取使用状态字典选项
  const useStatusOptions = getDictOptions(
    DICT_TYPE.OA_MEETING_BOOKING_USE_STATUS,
    'number',
  );

  return [
    { type: 'checkbox', width: 40 },
    createRouterLinkColumn({
      field: 'billCode',
      title: '单据编号',
      path: '/oa/meetingroom/booking-info',
      idField: 'id',
      queryParam: 'id',
    }),
    {
      field: 'processStatus',
      title: '单据状态',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.BPM_PROCESS_INSTANCE_STATUS },
      },
    },
    {
      field: 'useStatus',
      title: '使用状态',
      minWidth: 120,
      editRender: {
        name: 'VxeSelect',
        options: useStatusOptions,
        props: {
          placeholder: '请选择使用状态',
        },
      },
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.OA_MEETING_BOOKING_USE_STATUS },
      },
    },
    {
      field: 'roomName',
      title: '会议室名称',
      minWidth: 150,
    },
    {
      field: 'roomLocation',
      title: '会议室位置',
      minWidth: 150,
    },
    {
      field: 'roomType',
      title: '会议室类型',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.OA_MEETING_ROOM_TYPE },
      },
    },
    {
      field: 'meetingTitle',
      title: '会议主题',
      minWidth: 200,
    },
    {
      field: 'meetingStartTime',
      title: '会议开始时间',
      minWidth: 160,
      formatter: 'formatDateTime',
    },
    {
      field: 'meetingEndTime',
      title: '会议结束时间',
      minWidth: 160,
      formatter: 'formatDateTime',
    },
    {
      field: 'moderatorName',
      title: '主持人',
      minWidth: 100,
    },
    {
      field: 'creatorName',
      title: '申请人',
      minWidth: 100,
    },
    {
      field: 'deptName',
      title: '申请部门',
      minWidth: 120,
    },
    {
      field: 'companyName',
      title: '所属公司',
      minWidth: 120,
    },
    {
      field: 'createTime',
      title: '创建时间',
      minWidth: 160,
      formatter: 'formatDateTime',
    },
    {
      title: '操作',
      width: 100,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
