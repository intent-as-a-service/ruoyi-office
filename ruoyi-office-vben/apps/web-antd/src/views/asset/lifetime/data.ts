import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { LifeTimeApi } from '#/api/asset/lifetime';

import { z } from '#/adapter/form';
import {
    getDictOptions,
    getRangePickerDefaultProps,
} from '#/utils';

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
      fieldName: 'assetId',
      label: '采购订单id',
      component: 'Input',
      componentProps: {
        placeholder: '请输入采购订单id',
      },
    },
    {
      fieldName: 'assetCode',
      label: '资产编码',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入资产编码',
      },
    },
    {
      fieldName: 'assetName',
      label: '资产名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入资产名称',
      },
    },
    {
      fieldName: 'assetMilestone',
      label: '资产里程说明',
      component: 'Input',
      componentProps: {
        placeholder: '请输入资产里程说明',
      },
    },
    {
      fieldName: 'sort',
      label: '显示顺序',
      component: 'Input',
      componentProps: {
        placeholder: '请输入显示顺序',
      },
    },
    {
      fieldName: 'status',
      label: '状态（0正常 1停用）',
      rules: 'required',
      component: 'RadioGroup',
      componentProps: {
        options: [],
        buttonStyle: 'solid',
        optionType: 'button',
      },
    },
    {
      fieldName: 'storeAddress',
      label: '仓库地址',
      component: 'Input',
      componentProps: {
        placeholder: '请输入仓库地址',
      },
    },
    {
      fieldName: 'remark',
      label: '备注',
      component: 'Input',
      componentProps: {
        placeholder: '请输入备注',
      },
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'assetId',
      label: '采购订单id',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入采购订单id',
      },
    },
    {
      fieldName: 'assetCode',
      label: '资产编码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入资产编码',
      },
    },
    {
      fieldName: 'assetName',
      label: '资产名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入资产名称',
      },
    },
    {
      fieldName: 'assetMilestone',
      label: '资产里程说明',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入资产里程说明',
      },
    },
    {
      fieldName: 'sort',
      label: '显示顺序',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入显示顺序',
      },
    },
    {
      fieldName: 'status',
      label: '状态（0正常 1停用）',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [],
        placeholder: '请选择状态（0正常 1停用）',
      },
    },
    {
      fieldName: 'storeAddress',
      label: '仓库地址',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入仓库地址',
      },
    },
    {
      fieldName: 'remark',
      label: '备注',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入备注',
      },
    },
    {
      fieldName: 'createTime',
      label: '创建时间',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
        allowClear: true,
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<LifeTimeApi.LifeTime>['columns'] {
  return [
  { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: '主键',
      minWidth: 120,
    },
    {
      field: 'assetId',
      title: '采购订单id',
      minWidth: 120,
    },
    {
      field: 'assetCode',
      title: '资产编码',
      minWidth: 120,
    },
    {
      field: 'assetName',
      title: '资产名称',
      minWidth: 120,
    },
    {
      field: 'assetMilestone',
      title: '资产里程说明',
      minWidth: 120,
    },
    {
      field: 'sort',
      title: '显示顺序',
      minWidth: 120,
    },
    {
      field: 'status',
      title: '状态（0正常 1停用）',
      minWidth: 120,
    },
    {
      field: 'storeAddress',
      title: '仓库地址',
      minWidth: 120,
    },
    {
      field: 'remark',
      title: '备注',
      minWidth: 120,
    },
    {
      field: 'createTime',
      title: '创建时间',
      minWidth: 120,
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
