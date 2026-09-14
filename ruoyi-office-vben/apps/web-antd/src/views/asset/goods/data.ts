import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { GoodsApi } from '#/api/asset/goods';

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
      fieldName: 'goodsCode',
      label: '物品编码',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入物品编码',
      },
    },
    {
      fieldName: 'goodsName',
      label: '物品名称',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入物品名称',
      },
    },

    {
      fieldName: 'assetCategoryCode',
      label: '资产类别',
      component: 'ApiTreeSelect',
      componentProps: (_values, formApi) => ({
        allowClear: true,
        api: async () => {
          const data = await getCategoryList({});
          data.unshift({
            id: 0,
            categoryName: '资产类别',
            level: 0,
            sort: 0,
            remark: '',
          });
          return handleTree(data);
        },
        labelField: 'categoryName',
        valueField: 'id',
        childrenField: 'children',
        placeholder: '请选择资产类别',
        treeDefaultExpandAll: true,
        onChange: (value: any, option: any) => {
          if (value && option) {
            formApi.setFieldValue('assetCategoryName', option[0]);
          } else {
            formApi.setFieldValue('assetCategoryName', '');
          }
        },
      }),
      rules: 'selectRequired',
    },

    {
      fieldName: 'assetModel',
      label: '规格型号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入规格型号',
      },
    },
    {
      fieldName: 'assetUnit',
      label: '计量单位',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.ASSET_GOODS_UNIT, 'number'),
        placeholder: '请输入计量单位',
      },
    },

    {
      fieldName: 'manufacturer',
      label: '厂商',
      component: 'Input',
      componentProps: {
        placeholder: '请输入厂商',
      },
    },
    {
      fieldName: 'brand',
      label: '品牌',
      component: 'Input',
      componentProps: {
        placeholder: '请输入品牌',
      },
    },
    {
      fieldName: 'residualValueRate',
      label: '默认月残值率',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入默认月残值率',
      },
      suffix: () => '%',
    },
    {
      fieldName: 'inventoryLowerLimit',
      label: '库存下限',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入库存下限',
      },
    },
    {
      fieldName: 'inventoryLimit',
      label: '库存上限',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入库存上下限',
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
      fieldName: 'isJoinAsset',
      label: '是否在资产列表显示',
      component: 'Select',
      rules: 'required',
      componentProps: {
        options: getDictOptions(DICT_TYPE.COMMON_YES_NO, 'number'),
        placeholder: '请选择是否在资产列表显示',
      },
    },

    {
      fieldName: 'assetIcon',
      label: '物品图片',
      component: 'ImageUpload',
    },
    {
      fieldName: 'assetFile',
      label: '附件',
      component: 'FileUpload',
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
    // {
    //   fieldName: 'storeAddress',
    //   label: '仓库地址',
    //   component: 'Input',
    //   componentProps: {
    //     placeholder: '请输入仓库地址',
    //   },
    // },
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
      fieldName: 'goodsCode',
      label: '物品编码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入物品编码',
      },
    },
    {
      fieldName: 'goodsName',
      label: '物品名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入物品名称',
      },
    },
    {
      fieldName: 'assetCategoryCode',
      label: '资产类型编码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入资产类型编码',
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
export function useGridColumns<T = GoodsApi.Goods>(
  onStatusChange?: (
    newStatus: number,
    row: T,
  ) => PromiseLike<boolean | undefined>,
): VxeTableGridOptions<GoodsApi.Goods>['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    // {
    //   field: 'id',
    //   title: '主键',
    //   minWidth: 120,
    // },
    {
      field: 'goodsCode',
      title: '物品编码',
      minWidth: 120,
    },
    {
      field: 'goodsName',
      title: '物品名称',
      minWidth: 120,
    },

    {
      field: 'assetCategoryName',
      title: '资产类型名称',
      minWidth: 120,
    },
    {
      field: 'assetModel',
      title: '规格型号',
      minWidth: 120,
    },
    {
      field: 'assetUnit',
      title: '计量单位',
      minWidth: 120,
    },
    {
      field: 'manufacturer',
      title: '厂商',
      minWidth: 120,
    },
    {
      field: 'brand',
      title: '品牌',
      minWidth: 120,
    },
    {
      field: 'residualValueRate',
      title: '默认月残值率',
      minWidth: 120,
    },
    {
      field: 'inventoryLowerLimit',
      title: '库存下限',
      minWidth: 120,
    },
    {
      field: 'inventoryLimit',
      title: '库存上下限',
      minWidth: 120,
    },
    {
      field: 'isJoinAsset',
      title: '是否进入资产列表',
      minWidth: 120,
    },
    {
      field: 'assetIcon',
      title: '资产图片',
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
