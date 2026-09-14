import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { InfoApi } from '#/api/asset/info';

import {
  getRangePickerDefaultProps
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
      fieldName: 'purchaseOrderId',
      label: '采购订单id',
      component: 'Input',
      componentProps: {
        placeholder: '请输入采购订单id',
      },
    },
    {
      fieldName: 'purchaseOrderCode',
      label: '采购订单编码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入采购订单编码',
      },
    },
    {
      fieldName: 'purchaseOrderName',
      label: '采购订单名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入采购订单名称',
      },
    },
    {
      fieldName: 'inWarehousingId',
      label: '入库单Id',
      component: 'Input',
      componentProps: {
        placeholder: '请输入入库单Id',
      },
    },
    {
      fieldName: 'warehousingEntryCode',
      label: '入库单编码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入入库单编码',
      },
    },
    {
      fieldName: 'warehousingEntryName',
      label: '入库单名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入入库单名称',
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
      fieldName: 'assetCategoryCode',
      label: '资产类型编码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入资产类型编码',
      },
    },
    {
      fieldName: 'assetCategoryName',
      label: '资产类型名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入资产类型名称',
      },
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
      component: 'Input',
      componentProps: {
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
      fieldName: 'serialNumber',
      label: '序列号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入序列号',
      },
    },
    {
      fieldName: 'assetStatusCode',
      label: '资产状态编码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入资产状态编码',
      },
    },
    {
      fieldName: 'assetStatusName',
      label: '资产状态名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入资产状态名称',
      },
    },
    {
      fieldName: 'assetSourceCode',
      label: '资产来源编码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入资产来源编码',
      },
    },
    {
      fieldName: 'assetSourceName',
      label: '资料来源名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入资料来源名称',
      },
    },
    {
      fieldName: 'purchaseDate',
      label: '购买日期',
      component: 'DatePicker',
      componentProps: {
        showTime: true,
        format: 'YYYY-MM-DD HH:mm:ss',
        valueFormat: 'x',
      },
    },
    {
      fieldName: 'purchasePrice',
      label: '购买价格',
      component: 'Input',
      componentProps: {
        placeholder: '请输入购买价格',
      },
    },
    {
      fieldName: 'dateOfProduction',
      label: '出场日期',
      component: 'DatePicker',
      componentProps: {
        showTime: true,
        format: 'YYYY-MM-DD HH:mm:ss',
        valueFormat: 'x',
      },
    },
    {
      fieldName: 'adminCompanyId',
      label: '管理公司id',
      component: 'Input',
      componentProps: {
        placeholder: '请输入管理公司id',
      },
    },
    {
      fieldName: 'adminCompanyName',
      label: '管理公司名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入管理公司名称',
      },
    },
    {
      fieldName: 'adminDeptId',
      label: '管理部门编码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入管理部门编码',
      },
    },
    {
      fieldName: 'adminDeptName',
      label: '管理部门名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入管理部门名称',
      },
    },
    {
      fieldName: 'adminManagerId',
      label: '管理人员编码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入管理人员编码',
      },
    },
    {
      fieldName: 'adminManagerName',
      label: '管理人员名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入管理人员名称',
      },
    },
    {
      fieldName: 'useCompanyId',
      label: '使用公司id',
      component: 'Input',
      componentProps: {
        placeholder: '请输入使用公司id',
      },
    },
    {
      fieldName: 'useCompanyName',
      label: '使用公司名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入使用公司名称',
      },
    },
    {
      fieldName: 'useDeptId',
      label: '使用部门编码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入使用部门编码',
      },
    },
    {
      fieldName: 'useDeptName',
      label: '使用部门名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入使用部门名称',
      },
    },
    {
      fieldName: 'useAccountId',
      label: '使用人员编码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入使用人员编码',
      },
    },
    {
      fieldName: 'useAccountName',
      label: '使用人员名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入使用人员名称',
      },
    },
    {
      fieldName: 'wmsStoreCode',
      label: '存放仓库编码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入存放仓库编码',
      },
    },
    {
      fieldName: 'wmsStoreName',
      label: '存放仓库名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入存放仓库名称',
      },
    },
    {
      fieldName: 'residualValueRate',
      label: '残值率',
      component: 'Input',
      componentProps: {
        placeholder: '请输入残值率',
      },
    },
    {
      fieldName: 'assetIcon',
      label: '资产图片',
      component: 'Input',
      componentProps: {
        placeholder: '请输入资产图片',
      },
    },
    {
      fieldName: 'assetFile',
      label: '资产附件',
      component: 'FileUpload',
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
      fieldName: 'purchaseOrderCode',
      label: '采购订单编码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入采购订单编码',
      },
    },
    
   
    {
      fieldName: 'warehousingEntryCode',
      label: '入库单编码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入入库单编码',
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
      fieldName: 'serialNumber',
      label: '序列号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入序列号',
      },
    },
    {
      fieldName: 'assetStatusCode',
      label: '资产状态编码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入资产状态编码',
      },
    },
   
    {
      fieldName: 'assetSourceCode',
      label: '资产来源编码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入资产来源编码',
      },
    },
   
    {
      fieldName: 'purchaseDate',
      label: '购买日期',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
        allowClear: true,
      },
    },
   
    {
      fieldName: 'dateOfProduction',
      label: '出场日期',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
        allowClear: true,
      },
    },
    {
      fieldName: 'adminCompanyId',
      label: '管理公司id',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入管理公司id',
      },
    },
    
    {
      fieldName: 'adminDeptName',
      label: '管理部门名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入管理部门名称',
      },
    },
    {
      fieldName: 'adminManagerId',
      label: '管理人员编码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入管理人员编码',
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
export function useGridColumns(): VxeTableGridOptions<InfoApi.Info>['columns'] {
  return [
  { type: 'checkbox', width: 40 },
   
   
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
      field: 'assetCategoryCode',
      title: '资产类型编码',
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
      field: 'serialNumber',
      title: '序列号',
      minWidth: 120,
    },
    {
      field: 'assetStatusCode',
      title: '资产状态编码',
      minWidth: 120,
    },
    {
      field: 'assetStatusName',
      title: '资产状态名称',
      minWidth: 120,
    },
    {
      field: 'assetSourceCode',
      title: '资产来源编码',
      minWidth: 120,
    },
    {
      field: 'assetSourceName',
      title: '资料来源名称',
      minWidth: 120,
    },
    {
      field: 'purchaseDate',
      title: '购买日期',
      minWidth: 120,
      formatter: 'formatDateTime',
    },
    {
      field: 'purchasePrice',
      title: '购买价格',
      minWidth: 120,
    },
    {
      field: 'dateOfProduction',
      title: '出场日期',
      minWidth: 120,
      formatter: 'formatDateTime',
    },
 
    {
      field: 'adminCompanyName',
      title: '管理公司名称',
      minWidth: 120,
    },
    
    {
      field: 'adminDeptName',
      title: '管理部门名称',
      minWidth: 120,
    },
   
    {
      field: 'adminManagerName',
      title: '管理人员名称',
      minWidth: 120,
    },

    {
      field: 'useCompanyName',
      title: '使用公司名称',
      minWidth: 120,
    },
   
    {
      field: 'useDeptName',
      title: '使用部门名称',
      minWidth: 120,
    },
 
    {
      field: 'useAccountName',
      title: '使用人员名称',
      minWidth: 120,
    },
  
    {
      field: 'wmsStoreName',
      title: '存放仓库名称',
      minWidth: 120,
    },
    {
      field: 'residualValueRate',
      title: '残值率',
      minWidth: 120,
    },
    {
      field: 'assetIcon',
      title: '资产图片',
      minWidth: 120,
    },
    {
      field: 'assetFile',
      title: '资产附件',
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

