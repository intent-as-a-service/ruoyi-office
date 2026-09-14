import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MeetingRoomApi } from '#/api/oa/meetingroom/roominfo';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import { getSimpleUserList } from '#/api/system/user';

/** 新增/修改的表单 */
export function useFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'id',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'roomName',
      label: '会议室名称',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入会议室名称',
      },
    },
    {
      fieldName: 'roomType',
      label: '会议室类型',
      rules: 'required',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.OA_MEETING_ROOM_TYPE, 'number'),
        placeholder: '请选择会议室类型',
      },
    },
    {
      fieldName: 'roomLocation',
      label: '会议室位置',
      rules: 'required',
      component: 'Input',
      formItemClass: 'col-span-2',
      componentProps: {
        placeholder: '请输入会议室位置',
      },
    },
    {
      fieldName: 'managerId',
      label: '负责人',
      rules: 'required',
      component: 'ApiSelect',
      componentProps: (_values, formApi) => ({
        allowClear: true,
        api: async () => {
          const data = await getSimpleUserList();
          return data;
        },
        labelField: 'nickname',
        valueField: 'id',
        placeholder: '请选择负责人',
        showSearch: true,
        filterOption: (input: string, option: any) => {
          return (
            option.nickname?.toLowerCase().includes(input.toLowerCase()) ||
            option.username?.toLowerCase().includes(input.toLowerCase())
          );
        },
        onChange: (value: any, option: any) => {
          if (value && option) {
            // 选择了负责人，设置负责人姓名和手机号
            formApi.setFieldValue('managerName', option.label || '');
            formApi.setFieldValue('managerPhone', option.mobile || '');
          } else {
            // 清空选择，清空相关字段
            formApi.setFieldValue('managerName', '');
            formApi.setFieldValue('managerPhone', '');
          }
        },
      }),
    },
    {
      fieldName: 'managerName',
      label: '负责人姓名',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'managerPhone',
      label: '负责人联系方式',
      component: 'Input',
      componentProps: {
        placeholder: '请输入负责人联系方式',
      },
    },
    {
      fieldName: 'availableStatus',
      label: '可用状态',
      rules: 'required',
      component: 'RadioGroup',
      componentProps: {
        options: getDictOptions(DICT_TYPE.OA_MEETING_ROOM_STATUS, 'number'),
      },
      defaultValue: 0,
    },
    {
      fieldName: 'picUrl',
      label: '会议室图片',
      component: 'ImageUpload',
      componentProps: {
        maxCount: 1,
        listType: 'picture-card',
      },
    },
    {
      fieldName: 'seatCount',
      label: '坐席数',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入坐席数',
        min: 1,
        precision: 0,
      },
    },
    {
      fieldName: 'equipment',
      label: '会议室设备',
      component: 'Select',
      componentProps: {
        mode: 'multiple',
        allowClear: true,
        options: getDictOptions(DICT_TYPE.OA_MEETING_ROOM_EQUIPMENT),
        placeholder: '请选择会议室设备',
      },
    },
    {
      fieldName: 'allowBooking',
      label: '允许预定',
      rules: 'required',
      component: 'Switch',
      defaultValue: true,
      componentProps: {
        style: { width: '40px' },
      },
    },
    {
      fieldName: 'needApproval',
      label: '预定需审批',
      rules: 'required',
      component: 'Switch',
      defaultValue: false,
      componentProps: {
        style: { width: '40px' },
      },
    },
    {
      fieldName: 'bookingScope',
      label: '可用范围',
      rules: 'required',
      component: 'RadioGroup',
      componentProps: {
        options: [
          { label: '全部成员', value: 0 },
          { label: '指定成员', value: 1 },
        ],
      },
      defaultValue: 0,
    },
    {
      fieldName: 'bookingMembers',
      label: '可预定成员',
      component: 'ApiSelect',
      dependencies: {
        triggerFields: ['bookingScope'],
        show: (values) => values.bookingScope === 1,
        rules: (values) => (values.bookingScope === 1 ? 'selectRequired' : ''),
      },
      componentProps: {
        allowClear: true,
        mode: 'multiple',
        api: async () => {
          const data = await getSimpleUserList();
          return data;
        },
        labelField: 'nickname',
        valueField: 'id',
        placeholder: '请选择可预定成员',
        showSearch: true,
        filterOption: (input: string, option: any) => {
          return (
            option.nickname?.toLowerCase().includes(input.toLowerCase()) ||
            option.username?.toLowerCase().includes(input.toLowerCase())
          );
        },
      },
    },
    {
      fieldName: 'sort',
      label: '显示顺序',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入显示顺序',
        min: 0,
      },
      defaultValue: 0,
    },
    {
      fieldName: 'attachmentUrl',
      label: '相关附件',
      component: 'FileUpload',
      // 附件字段占据整行，放在备注上方
      formItemClass: 'col-span-2',
      componentProps: {
        maxCount: 10,
        maxSize: 10,
        listType: 'text',
        showUploadList: {
          showPreviewIcon: true,
          showRemoveIcon: true,
          showDownloadIcon: true,
        },
      },
    },
    {
      fieldName: 'remark',
      label: '备注',
      component: 'Textarea',
      // 备注字段占据整行
      formItemClass: 'col-span-2',
      componentProps: {
        placeholder: '请输入备注（200字以内）',
        maxlength: 200,
        showCount: true,
        rows: 3,
      },
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
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
      fieldName: 'roomLocation',
      label: '会议室位置',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入会议室位置',
      },
    },
    {
      fieldName: 'roomType',
      label: '会议室类型',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.OA_MEETING_ROOM_TYPE, 'number'),
        placeholder: '请选择会议室类型',
      },
    },
    {
      fieldName: 'managerName',
      label: '负责人',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入负责人姓名',
      },
    },
    {
      fieldName: 'availableStatus',
      label: '可用状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.OA_MEETING_ROOM_STATUS, 'number'),
        placeholder: '请选择可用状态',
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<MeetingRoomApi.MeetingRoom>['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    {
      field: 'picUrl',
      title: '会议室图片',
      width: 120,
      slots: { default: 'picUrl' },
    },
    {
      field: 'roomName',
      title: '会议室名称',
      minWidth: 150,
    },
    {
      field: 'seatCount',
      title: '坐席数',
      minWidth: 100,
      align: 'center',
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
      field: 'roomLocation',
      title: '会议室位置',
      minWidth: 150,
    },
    {
      field: 'managerName',
      title: '负责人',
      minWidth: 100,
    },
    {
      field: 'managerPhone',
      title: '联系方式',
      minWidth: 120,
    },
    {
      field: 'availableStatus',
      title: '可用状态',
      minWidth: 100,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.OA_MEETING_ROOM_STATUS },
      },
    },
    {
      field: 'equipment',
      title: '会议室设备',
      minWidth: 200,
      slots: { default: 'equipment' },
    },
    {
      field: 'allowBooking',
      title: '允许预定',
      minWidth: 100,
      slots: { default: 'allowBooking' },
    },
    {
      field: 'needApproval',
      title: '需审批',
      minWidth: 100,
      slots: { default: 'needApproval' },
    },
    {
      field: 'sort',
      title: '显示顺序',
      minWidth: 100,
    },
    {
      field: 'remark',
      title: '备注',
      minWidth: 200,
    },
    {
      field: 'createTime',
      title: '创建时间',
      minWidth: 160,
      formatter: 'formatDateTime',
    },
    {
      title: '操作',
      width: 200,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
