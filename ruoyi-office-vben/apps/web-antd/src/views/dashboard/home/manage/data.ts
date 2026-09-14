import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';

import { CommonStatusEnum, DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import { z } from '#/adapter/form';

/** 新增/修改的表单 */
export function useFormSchema(): VbenFormSchema[] {
  return [
    {
      component: 'Input',
      fieldName: 'id',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      component: 'Input',
      fieldName: 'name',
      label: '首页名称',
      componentProps: {
        placeholder: '请输入首页名称',
      },
      rules: 'required',
    },
    {
      component: 'Input',
      fieldName: 'code',
      label: '首页编码',
      componentProps: {
        placeholder: '请输入首页编码',
      },
      rules: 'required',
    },
    {
      fieldName: 'description',
      label: '首页描述',
      component: 'Textarea',
      componentProps: {
        placeholder: '请输入首页描述',
        rows: 3,
      },
    },
    {
      fieldName: 'previewImage',
      label: '预览图',
      component: 'Input',
      componentProps: {
        placeholder: '请输入预览图URL',
      },
    },

    {
      fieldName: 'status',
      label: '启用状态',
      component: 'RadioGroup',
      componentProps: {
        options: getDictOptions(DICT_TYPE.COMMON_STATUS, 'number'),
        buttonStyle: 'solid',
        optionType: 'button',
      },
      rules: z.number().default(CommonStatusEnum.ENABLE),
    },
    {
      fieldName: 'sort',
      label: '显示顺序',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入显示顺序',
      },
      rules: 'required',
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'name',
      label: '首页名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入首页名称',
        allowClear: true,
      },
    },
    {
      fieldName: 'code',
      label: '首页编码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入首页编码',
        allowClear: true,
      },
    },
    {
      fieldName: 'status',
      label: '首页状态',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.COMMON_STATUS, 'number'),
        placeholder: '请选择首页状态',
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
      title: '首页编号',
      minWidth: 100,
    },
    {
      field: 'name',
      title: '首页名称',
      minWidth: 150,
    },
    {
      field: 'code',
      title: '首页编码',
      minWidth: 150,
    },
    {
      field: 'description',
      title: '首页描述',
      minWidth: 200,
    },
    {
      field: 'useStatus',
      title: '使用状态',
      minWidth: 100,
      slots: { default: 'useStatus' },
    },
    {
      field: 'status',
      title: '启用状态',
      minWidth: 100,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.COMMON_STATUS },
      },
    },
    {
      field: 'sort',
      title: '显示顺序',
      minWidth: 100,
    },
    {
      field: 'createTime',
      title: '创建时间',
      minWidth: 180,
      formatter: 'formatDateTime',
    },
    {
      title: '操作',
      width: 280,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
