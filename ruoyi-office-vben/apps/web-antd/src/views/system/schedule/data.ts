import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';

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
      fieldName: 'title',
      label: '标题',
      component: 'Input',
      componentProps: {
        placeholder: '请输入标题',
      },
      rules: 'required',
    },
    {
      fieldName: 'content',
      label: '内容',
      component: 'Textarea',
      componentProps: {
        placeholder: '请输入内容',
        rows: 4,
      },
    },
    {
      fieldName: 'scheduleDate',
      label: '日程日期',
      component: 'DatePicker',
      componentProps: {
        placeholder: '请选择日程日期',
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
      },
      rules: 'required',
    },
    {
      fieldName: 'startTime',
      label: '开始时间',
      component: 'TimePicker',
      componentProps: {
        placeholder: '请选择开始时间',
        format: 'HH:mm',
        valueFormat: 'HH:mm:ss',
      },
    },
    {
      fieldName: 'endTime',
      label: '结束时间',
      component: 'TimePicker',
      componentProps: {
        placeholder: '请选择结束时间',
        format: 'HH:mm',
        valueFormat: 'HH:mm:ss',
      },
    },
    {
      fieldName: 'scheduleType',
      label: '日程类型',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.SCHEDULE_TYPE, 'string'),
        placeholder: '请选择日程类型',
        allowClear: true,
      },
    },
    {
      fieldName: 'scheduleCategory',
      label: '日程分类',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.SCHEDULE_CATEGORY, 'string'),
        placeholder: '请选择日程分类',
        allowClear: true,
      },
    },
    {
      fieldName: 'receiverIds',
      label: '接收人',
      component: 'ApiSelect',
      componentProps: {
        allowClear: true,
        mode: 'multiple',
        api: async () => {
          const data = await getSimpleUserList();
          return data;
        },
        labelField: 'nickname',
        valueField: 'id',
        placeholder: '请选择接收人（用于推送）',
        showSearch: true,
        filterOption: (input: string, option: any) => {
          return (
            option.nickname?.toLowerCase().includes(input.toLowerCase()) ||
            option.username?.toLowerCase().includes(input.toLowerCase())
          );
        },
      },
      defaultValue: [],
    },
    {
      fieldName: 'remark',
      label: '备注',
      component: 'Textarea',
      componentProps: {
        placeholder: '请输入备注',
        rows: 2,
      },
    },
    {
      fieldName: 'isPushed',
      label: '推送状态',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.INFRA_BOOLEAN_STRING, 'string'),
        placeholder: '推送状态',
        disabled: true,
      },
      dependencies: {
        triggerFields: [''],
        show: (values) => {
          // 编辑时显示推送状态
          return !!values.id;
        },
      },
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'title',
      label: '标题',
      component: 'Input',
      componentProps: {
        placeholder: '请输入标题',
        allowClear: true,
      },
    },
    {
      fieldName: 'scheduleDateStart',
      label: '开始日期',
      component: 'DatePicker',
      componentProps: {
        placeholder: '请选择开始日期',
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
        allowClear: true,
      },
    },
    {
      fieldName: 'scheduleDateEnd',
      label: '结束日期',
      component: 'DatePicker',
      componentProps: {
        placeholder: '请选择结束日期',
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
        allowClear: true,
      },
    },
    {
      fieldName: 'scheduleType',
      label: '日程类型',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.SCHEDULE_TYPE, 'string'),
        placeholder: '请选择日程类型',
        allowClear: true,
      },
    },
    {
      fieldName: 'scheduleCategory',
      label: '日程分类',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.SCHEDULE_CATEGORY, 'string'),
        placeholder: '请选择日程分类',
        allowClear: true,
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.COMMON_STATUS, 'number'),
        placeholder: '请选择状态',
        allowClear: true,
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: '日程编号',
      minWidth: 100,
    },
    {
      field: 'title',
      title: '标题',
      minWidth: 200,
    },
    {
      field: 'scheduleDate',
      title: '日程日期',
      minWidth: 120,
      formatter: 'formatDate',
    },
    {
      field: 'startTime',
      title: '开始时间',
      minWidth: 100,
      formatter: ({ cellValue }) => {
        if (!cellValue) return '-';
        return cellValue.slice(0, 5); // HH:mm
      },
    },
    {
      field: 'endTime',
      title: '结束时间',
      minWidth: 100,
      formatter: ({ cellValue }) => {
        if (!cellValue) return '-';
        return cellValue.slice(0, 5); // HH:mm
      },
    },
    {
      field: 'scheduleType',
      title: '日程类型',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.SCHEDULE_TYPE },
      },
    },
    {
      field: 'scheduleCategory',
      title: '日程分类',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.SCHEDULE_CATEGORY },
      },
    },
    {
      field: 'creatorName',
      title: '创建人',
      minWidth: 100,
    },
    {
      field: 'isPushed',
      title: '推送状态',
      minWidth: 100,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.INFRA_BOOLEAN_STRING },
      },
    },
    {
      field: 'status',
      title: '状态',
      minWidth: 100,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.COMMON_STATUS },
      },
    },
    {
      field: 'createTime',
      title: '创建时间',
      minWidth: 180,
      formatter: 'formatDateTime',
    },
    {
      title: '操作',
      width: 220,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
