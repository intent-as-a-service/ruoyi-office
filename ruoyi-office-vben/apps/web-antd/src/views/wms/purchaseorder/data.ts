import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { PurchaseOrderApi } from '#/api/wms/purchaseorder';

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
      fieldName: 'purchaseOrderCode',
      label: '采购订单编码',
      rules: 'required',
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
      fieldName: 'applicantDate',
      label: '申请日期',
      component: 'DatePicker',
      componentProps: {
        showTime: true,
        format: 'YYYY-MM-DD HH:mm:ss',
        valueFormat: 'x',
      },
    },
    {
      fieldName: 'companyId',
      label: '公司id',
      component: 'Input',
      componentProps: {
        placeholder: '请输入公司id',
      },
    },
    {
      fieldName: 'companyName',
      label: '公司名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入公司名称',
      },
    },
    {
      fieldName: 'applicantDeptId',
      label: '申请部门编码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入申请部门编码',
      },
    },
    {
      fieldName: 'applicantDeptName',
      label: '申请部门名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入申请部门名称',
      },
    },
    {
      fieldName: 'applicantUserId',
      label: '申请人编码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入申请人编码',
      },
    },
    {
      fieldName: 'applicantUserName',
      label: '申请人名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入申请人名称',
      },
    },
    {
      fieldName: 'supplierCode',
      label: '供应商编码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入供应商编码',
      },
    },
    {
      fieldName: 'supplierName',
      label: '供应商名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入供应商名称',
      },
    },
    {
      fieldName: 'applicantDesc',
      label: '申请说明',
      component: 'Input',
      componentProps: {
        placeholder: '请输入申请说明',
      },
    },
    {
      fieldName: 'purchaseOrderFile',
      label: '附件',
      component: 'FileUpload',
    },
    {
      fieldName: 'purchaseOrderTotalData',
      label: '采购物品合计',
      component: 'Input',
      componentProps: {
        placeholder: '请输入采购物品合计',
      },
    },
    {
      fieldName: 'purchaseOrderTotalAmount',
      label: '采购物品金额合计',
      component: 'Input',
      componentProps: {
        placeholder: '请输入采购物品金额合计',
      },
    },
    {
      fieldName: 'processInstanceId',
      label: '流程实例编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入流程实例编号',
      },
    },
    {
      fieldName: 'processStatus',
      label: '单据状态',
      component: 'RadioGroup',
      componentProps: {
        options: [],
        buttonStyle: 'solid',
        optionType: 'button',
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
      fieldName: 'purchaseOrderName',
      label: '采购订单名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入采购订单名称',
      },
    },
    {
      fieldName: 'applicantDate',
      label: '申请日期',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
        allowClear: true,
      },
    },
   
    {
      fieldName: 'processStatus',
      label: '单据状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: [],
        placeholder: '请选择单据状态',
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
export function useGridColumns(): VxeTableGridOptions<PurchaseOrderApi.PurchaseOrder>['columns'] {
  return [
  { type: 'checkbox', width: 40 },
   
    {
      field: 'purchaseOrderCode',
      title: '采购订单编码',
      minWidth: 120,
    },
    {
      field: 'purchaseOrderName',
      title: '采购订单名称',
      minWidth: 120,
    },
    {
      field: 'applicantDate',
      title: '申请日期',
      minWidth: 120,
      formatter: 'formatDateTime',
    },
   
    {
      field: 'companyName',
      title: '公司名称',
      minWidth: 120,
    },
    
    {
      field: 'applicantDeptName',
      title: '申请部门名称',
      minWidth: 120,
    },
  
    {
      field: 'applicantUserName',
      title: '申请人名称',
      minWidth: 120,
    },
    {
      field: 'applicantDesc',
      title: '申请说明',
      minWidth: 120,
    },
    {
      field: 'purchaseOrderFile',
      title: '附件',
      minWidth: 120,
    },
    {
      field: 'purchaseOrderTotalData',
      title: '采购数量合计',
      minWidth: 120,
    },
    {
      field: 'purchaseOrderTotalAmount',
      title: '采购金额合计',
      minWidth: 120,
    },
   
    {
      field: 'processStatus',
      title: '单据状态',
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

// ==================== 子表（采购订单明细） ====================

     /** 新增/修改列表的字段 */
    export function usePurchaseOrderDetailGridEditColumns(): VxeTableGridOptions<PurchaseOrderApi.PurchaseOrderDetail>['columns'] {
        return [
                         {
                            field: 'purchaseOrderCode',
                            title: '采购订单编码',
                            minWidth: 120,
                            slots: { default: 'purchaseOrderCode' },
                        },
                         {
                            field: 'purchaseOrderName',
                            title: '采购订单名称',
                            minWidth: 120,
                            slots: { default: 'purchaseOrderName' },
                        },
                         {
                            field: 'categoryCode',
                            title: '资产类别编码',
                            minWidth: 120,
                            slots: { default: 'categoryCode' },
                        },
                         {
                            field: 'categoryName',
                            title: '资产类别名称',
                            minWidth: 120,
                            slots: { default: 'categoryName' },
                        },
                         {
                            field: 'goodsCode',
                            title: '物品名称编码',
                            minWidth: 120,
                            slots: { default: 'goodsCode' },
                        },
                         {
                            field: 'goodsName',
                            title: '物品名称名称',
                            minWidth: 120,
                            slots: { default: 'goodsName' },
                        },
                         {
                            field: 'purchasePrice',
                            title: '采购单据',
                            minWidth: 120,
                            slots: { default: 'purchasePrice' },
                        },
                         {
                            field: 'purchaseNum',
                            title: '采购数量',
                            minWidth: 120,
                            slots: { default: 'purchaseNum' },
                        },
                         {
                            field: 'purchaseTotalAmonut',
                            title: '采购总价',
                            minWidth: 120,
                            slots: { default: 'purchaseTotalAmonut' },
                        },
                         {
                            field: 'sort',
                            title: '显示顺序',
                            minWidth: 120,
                            slots: { default: 'sort' },
                        },
                         {
                            field: 'status',
                            title: '状态（0正常 1停用）',
                            minWidth: 120,
                            slots: { default: 'status' },
                                    params: {
                                        options: [],
                                    },
                        },
                         {
                            field: 'remark',
                            title: '备注',
                            minWidth: 120,
                            slots: { default: 'remark' },
                        },
            {
                title: '操作',
                width: 200,
                fixed: 'right',
                slots: { default: 'actions' },
            },
        ];
    }

