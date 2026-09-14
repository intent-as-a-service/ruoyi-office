import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'title',
      label: '公告标题',
      component: 'Input',
      componentProps: {
        placeholder: '请输入公告标题',
        allowClear: true,
      },
    },
    {
      fieldName: 'type',
      label: '公告类型',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.SYSTEM_NOTICE_TYPE, 'number'),
        placeholder: '请选择公告类型',
        allowClear: true,
      },
    },
    {
      fieldName: 'status',
      label: '公告状态',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.COMMON_STATUS, 'number'),
        placeholder: '请选择公告状态',
        allowClear: true,
      },
    },
    {
      fieldName: 'isImportant',
      label: '是否重要通知',
      component: 'Select',
      componentProps: {
        options: [
          { label: '是', value: true },
          { label: '否', value: false },
        ],
        placeholder: '请选择',
        allowClear: true,
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions['columns'] {
  return [
    {
      field: 'id',
      title: '公告编号',
      minWidth: 100,
    },
    {
      field: 'readStatus',
      title: '已读状态',
      minWidth: 100,
      formatter: ({ cellValue }) => {
        return cellValue === 1 ? '已读' : '未读';
      },
      cellRender: {
        name: 'VxeTag',
        props: ({ row }) => {
          const isRead = row.readStatus === 1;
          return {
            type: isRead ? 'success' : 'warning',
            content: isRead ? '已读' : '未读',
          };
        },
      },
    },
    {
      field: 'type',
      title: '公告类型',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.SYSTEM_NOTICE_TYPE },
      },
    },
    {
      field: 'title',
      title: '公告标题',
      minWidth: 200,
    },
    {
      field: 'isImportant',
      title: '是否重要',
      minWidth: 100,
      formatter: ({ cellValue }) => {
        return cellValue ? '是' : '否';
      },
    },
    {
      field: 'status',
      title: '公告状态',
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
      width: 120,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
