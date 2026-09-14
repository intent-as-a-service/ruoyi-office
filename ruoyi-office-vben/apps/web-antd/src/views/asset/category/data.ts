import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { CategoryApi } from '#/api/asset/category';

import { CommonStatusEnum, DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';
import { handleTree } from '@vben/utils';

import { z } from '#/adapter/form';
import { getCategoryList } from '#/api/asset/category';
import { getRangePickerDefaultProps } from '#/utils';
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
      fieldName: 'parentId',
      label: '上级资产类别',
      component: 'ApiTreeSelect',
      componentProps: {
        allowClear: true,
        api: async () => {
          const data = await getCategoryList({});
          data.unshift({
            id: 0,
            categoryName: '顶级资产类别',
            level: 0,
            sort: 0,
            remark: '',
          });
          return handleTree(data);
        },
        labelField: 'categoryName',
        valueField: 'id',
        childrenField: 'children',
        placeholder: '请选择上级资产类别',
        treeDefaultExpandAll: true,
      },
      rules: 'selectRequired',
    },
    {
      fieldName: 'categoryCode',
      label: '类别编码',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入类别编码',
      },
    },
    {
      fieldName: 'categoryName',
      label: '类别名称',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入类别名称',
      },
    },
    {
      fieldName: 'level',
      label: '级别',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入显示顺序',
        min: 1, // 最小值为1
        precision: 0, // 不允许小数
        style: { width: '100%' },
      },
    },
    {
      fieldName: 'sort',
      label: '显示顺序',
      rules: 'required',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入显示顺序',
        min: 1, // 最小值为1
        precision: 0, // 不允许小数
        style: { width: '100%' },
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'RadioGroup',
      componentProps: {
        options: getDictOptions(DICT_TYPE.COMMON_STATUS, 'number'),
        buttonStyle: 'solid',
        optionType: 'button',
      },
      rules: z.number().default(CommonStatusEnum.ENABLE),
    },
    {
      fieldName: 'remark',
      label: '备注',
      component: 'Textarea',
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
      fieldName: 'categoryCode',
      label: '类别编码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入类别编码',
      },
    },
    {
      fieldName: 'categoryName',
      label: '类别名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入类别名称',
      },
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.COMMON_STATUS, 'string'),
        placeholder: '请选择仓库类型',
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
export function useGridColumns<T = CategoryApi.Category>(
  onStatusChange?: (
    newStatus: number,
    row: T,
  ) => PromiseLike<boolean | undefined>,
): VxeTableGridOptions<CategoryApi.Category>['columns'] {
  return [
    // {
    //   field: 'id',
    //   title: '主键',
    //   minWidth: 120,
    // },
    // {
    //   field: 'categoryCode',
    //   title: '类别编码',
    //   minWidth: 120,
    // },
    {
      field: 'categoryName',
      title: '类别名称',
      minWidth: 120,
      treeNode: true,
    },
    // {
    //   field: 'parentId',
    //   title: '上级id',
    //   minWidth: 120,
    // },
    // {
    //   field: 'level',
    //   title: '级别',
    //   minWidth: 120,
    // },
    {
      field: 'sort',
      title: '显示顺序',
      minWidth: 120,
    },
    {
      field: 'status',
      title: '状态',
      minWidth: 120,
      align: 'center',
      cellRender: {
        attrs: { beforeChange: onStatusChange },
        name: 'CellSwitch',
        props: {
          checkedValue: CommonStatusEnum.ENABLE,
          unCheckedValue: CommonStatusEnum.DISABLE,
        },
      },
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
