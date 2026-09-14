import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { WarehousingApi } from '#/api/wms/warehousing';

import { CommonStatusEnum, DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';
import { handleTree } from '@vben/utils';

import { z } from '#/adapter/form';
import { getCompanyList } from '#/api/system/dept';
import { getWarehousingList } from '#/api/wms/warehousing';
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
      label: '上级仓库',
      component: 'ApiTreeSelect',
      componentProps: {
        allowClear: true,
        api: async () => {
          const data = await getWarehousingList({});
          data.unshift({
            id: 0,
            warehousingName: '顶级仓库',
            level: 0,
            warehousingCategoryCode: '',
            warehousingCategoryName: '',
            warehousingAddress: '',
            companyId: '',
            companyName: '',
            sort: 0,
            remark: '',
          });
          return handleTree(data);
        },
        labelField: 'warehousingName',
        valueField: 'id',
        childrenField: 'children',
        placeholder: '请选择上级仓库',
        treeDefaultExpandAll: true,
      },
      rules: 'selectRequired',
    },
    {
      fieldName: 'warehousingCode',
      label: '仓库编码',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入仓库编码',
      },
    },
    {
      fieldName: 'warehousingName',
      label: '仓库名称',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入仓库名称',
      },
    },

    {
      fieldName: 'warehousingCategoryCode',
      label: '仓库类型',
      component: 'Select',
      rules: 'selectRequired',
      componentProps: (_values, formApi) => ({
        options: getDictOptions(DICT_TYPE.WMS_CATEGORY_CODE, 'string'),
        placeholder: '请选择仓库类型',
        onChange: (value: any, option: any) => {
          if (value && option) {
            formApi.setFieldValue('warehousingCategoryName', option.label);
          } else {
            formApi.setFieldValue('warehousingCategoryName', '');
          }
        },
      }),
    },

    {
      fieldName: 'warehousingAddress',
      label: '仓库地址',
      component: 'Input',
      componentProps: {
        placeholder: '请输入仓库地址',
      },
    },
    {
      fieldName: 'companyId',
      label: '所属公司',
      component: 'ApiTreeSelect',
      componentProps: (_values, formApi) => ({
        allowClear: true,
        api: async () => {
          const data = await getCompanyList();
          return handleTree(data);
        },
        labelField: 'name',
        valueField: 'id',
        childrenField: 'children',
        placeholder: '请选择公司',
        treeDefaultExpandAll: true,
        onChange: (value: any, option: any) => {
          if (value && option) {
            // 选择了公司，设置公司名称
            formApi.setFieldValue('companyName', option[0]);
          } else {
            // 清空选择，清空公司名称
            formApi.setFieldValue('companyName', '');
          }
        },
      }),
      rules: 'selectRequired',
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
      fieldName: 'warehousingCode',
      label: '仓库编码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入仓库编码',
      },
    },
    {
      fieldName: 'warehousingName',
      label: '仓库名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入仓库名称',
      },
    },

    {
      fieldName: 'warehousingCategoryCode',
      label: '仓库类型',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.WMS_CATEGORY_CODE, 'string'),
        placeholder: '请选择仓库类型',
      },
    },

    {
      fieldName: 'companyId',
      label: '所属公司',
      component: 'ApiTreeSelect',
      componentProps: {
        allowClear: true,
        api: async () => {
          const data = await getCompanyList();
          return handleTree(data);
        },
        labelField: 'name',
        valueField: 'id',
        childrenField: 'children',
        placeholder: '请选择公司',
        treeDefaultExpandAll: true,
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
export function useGridColumns<T = WarehousingApi.Warehousing>(
  onStatusChange?: (
    newStatus: number,
    row: T,
  ) => PromiseLike<boolean | undefined>,
): VxeTableGridOptions<WarehousingApi.Warehousing>['columns'] {
  return [
    {
      field: 'warehousingName',
      title: '仓库名称',
      minWidth: 120,
      treeNode: true,
    },
    {
      field: 'warehousingCode',
      title: '仓库编码',
      minWidth: 120,
    },
    {
      field: 'warehousingCategoryName',
      title: '仓库类型',
      minWidth: 120,
    },
    {
      field: 'warehousingAddress',
      title: '仓库地址',
      minWidth: 120,
    },
    {
      field: 'companyName',
      title: '公司名称',
      minWidth: 120,
    },
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
