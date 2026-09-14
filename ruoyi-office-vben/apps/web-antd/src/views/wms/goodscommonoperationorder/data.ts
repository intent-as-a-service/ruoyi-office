import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { GoodsCommonOperationOrderApi } from '#/api/wms/goodscommonoperationorder';

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
      rules: 'required',
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
      fieldName: 'commonOperationDate',
      label: '领用、退库、归还、借用日期',
      component: 'DatePicker',
      componentProps: {
        showTime: true,
        format: 'YYYY-MM-DD HH:mm:ss',
        valueFormat: 'x',
      },
    },
    {
      fieldName: 'commonOperationDesc',
      label: '领用、退库、归还、借用库说明',
      component: 'Input',
      componentProps: {
        placeholder: '请输入领用、退库、归还、借用库说明',
      },
    },
    {
      fieldName: 'commonOperationFile',
      label: '领用、退库、归还、借用附件',
      component: 'FileUpload',
    },
    {
      fieldName: 'stockReturnTotalData',
      label: '采购物品合计',
      component: 'Input',
      componentProps: {
        placeholder: '请输入采购物品合计',
      },
    },
    {
      fieldName: 'commonOperationTotalAmount',
      label: '入库物品金额合计',
      component: 'Input',
      componentProps: {
        placeholder: '请输入入库物品金额合计',
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
      fieldName: 'deptId',
      label: '领用、退库、归还、借用部门编码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入领用、退库、归还、借用部门编码',
      },
    },
    {
      fieldName: 'deptName',
      label: '领用、退库、归还、借用部门名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入领用、退库、归还、借用部门名称',
      },
    },
    {
      fieldName: 'userId',
      label: '领用、退库、归还、借用人编码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入领用、退库、归还、借用人编码',
      },
    },
    {
      fieldName: 'userName',
      label: '领用、退库、归还、借用人名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入领用、退库、归还、借用人名称',
      },
    },
    {
      fieldName: 'expectReturnDate',
      label: '预计归还日期',
      component: 'DatePicker',
      componentProps: {
        showTime: true,
        format: 'YYYY-MM-DD HH:mm:ss',
        valueFormat: 'x',
      },
    },
    {
      fieldName: 'transferCompanyId',
      label: '调入公司id',
      component: 'Input',
      componentProps: {
        placeholder: '请输入调入公司id',
      },
    },
    {
      fieldName: 'transferCompanyName',
      label: '调入公司名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入调入公司名称',
      },
    },
    {
      fieldName: 'transferDeptId',
      label: '调入部门编码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入调入部门编码',
      },
    },
    {
      fieldName: 'transferDeptName',
      label: '调入部门名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入调入部门名称',
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
      fieldName: 'purchaseOrderId',
      label: '采购订单id',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入采购订单id',
      },
    },
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
      fieldName: 'inWarehousingId',
      label: '入库单Id',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入入库单Id',
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
      fieldName: 'warehousingEntryName',
      label: '入库单名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入入库单名称',
      },
    },
    {
      fieldName: 'commonOperationDate',
      label: '领用、退库、归还、借用日期',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
        allowClear: true,
      },
    },
    {
      fieldName: 'commonOperationDesc',
      label: '领用、退库、归还、借用库说明',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入领用、退库、归还、借用库说明',
      },
    },
    {
      fieldName: 'commonOperationFile',
      label: '领用、退库、归还、借用附件',
    },
    {
      fieldName: 'stockReturnTotalData',
      label: '采购物品合计',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入采购物品合计',
      },
    },
    {
      fieldName: 'commonOperationTotalAmount',
      label: '入库物品金额合计',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入入库物品金额合计',
      },
    },
    {
      fieldName: 'companyId',
      label: '公司id',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入公司id',
      },
    },
    {
      fieldName: 'companyName',
      label: '公司名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入公司名称',
      },
    },
    {
      fieldName: 'deptId',
      label: '领用、退库、归还、借用部门编码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入领用、退库、归还、借用部门编码',
      },
    },
    {
      fieldName: 'deptName',
      label: '领用、退库、归还、借用部门名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入领用、退库、归还、借用部门名称',
      },
    },
    {
      fieldName: 'userId',
      label: '领用、退库、归还、借用人编码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入领用、退库、归还、借用人编码',
      },
    },
    {
      fieldName: 'userName',
      label: '领用、退库、归还、借用人名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入领用、退库、归还、借用人名称',
      },
    },
    {
      fieldName: 'expectReturnDate',
      label: '预计归还日期',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
        allowClear: true,
      },
    },
    {
      fieldName: 'transferCompanyId',
      label: '调入公司id',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入调入公司id',
      },
    },
    {
      fieldName: 'transferCompanyName',
      label: '调入公司名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入调入公司名称',
      },
    },
    {
      fieldName: 'transferDeptId',
      label: '调入部门编码',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入调入部门编码',
      },
    },
    {
      fieldName: 'transferDeptName',
      label: '调入部门名称',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入调入部门名称',
      },
    },
    {
      fieldName: 'processInstanceId',
      label: '流程实例编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入流程实例编号',
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
export function useGridColumns(): VxeTableGridOptions<GoodsCommonOperationOrderApi.GoodsCommonOperationOrder>['columns'] {
  return [
  { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: '主键',
      minWidth: 120,
    },
    {
      field: 'purchaseOrderId',
      title: '采购订单id',
      minWidth: 120,
    },
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
      field: 'inWarehousingId',
      title: '入库单Id',
      minWidth: 120,
    },
    {
      field: 'warehousingEntryCode',
      title: '入库单编码',
      minWidth: 120,
    },
    {
      field: 'warehousingEntryName',
      title: '入库单名称',
      minWidth: 120,
    },
    {
      field: 'commonOperationDate',
      title: '领用、退库、归还、借用日期',
      minWidth: 120,
      formatter: 'formatDateTime',
    },
    {
      field: 'commonOperationDesc',
      title: '领用、退库、归还、借用库说明',
      minWidth: 120,
    },
    {
      field: 'commonOperationFile',
      title: '领用、退库、归还、借用附件',
      minWidth: 120,
    },
    {
      field: 'stockReturnTotalData',
      title: '采购物品合计',
      minWidth: 120,
    },
    {
      field: 'commonOperationTotalAmount',
      title: '入库物品金额合计',
      minWidth: 120,
    },
    {
      field: 'companyId',
      title: '公司id',
      minWidth: 120,
    },
    {
      field: 'companyName',
      title: '公司名称',
      minWidth: 120,
    },
    {
      field: 'deptId',
      title: '领用、退库、归还、借用部门编码',
      minWidth: 120,
    },
    {
      field: 'deptName',
      title: '领用、退库、归还、借用部门名称',
      minWidth: 120,
    },
    {
      field: 'userId',
      title: '领用、退库、归还、借用人编码',
      minWidth: 120,
    },
    {
      field: 'userName',
      title: '领用、退库、归还、借用人名称',
      minWidth: 120,
    },
    {
      field: 'expectReturnDate',
      title: '预计归还日期',
      minWidth: 120,
      formatter: 'formatDateTime',
    },
    {
      field: 'transferCompanyId',
      title: '调入公司id',
      minWidth: 120,
    },
    {
      field: 'transferCompanyName',
      title: '调入公司名称',
      minWidth: 120,
    },
    {
      field: 'transferDeptId',
      title: '调入部门编码',
      minWidth: 120,
    },
    {
      field: 'transferDeptName',
      title: '调入部门名称',
      minWidth: 120,
    },
    {
      field: 'processInstanceId',
      title: '流程实例编号',
      minWidth: 120,
    },
    {
      field: 'processStatus',
      title: '单据状态',
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

// ==================== 子表（采购入库、领用、退库、归还、借用、调拨明细） ====================

     /** 新增/修改列表的字段 */
    export function useGoodsWarehousingDetailGridEditColumns(): VxeTableGridOptions<GoodsCommonOperationOrderApi.GoodsWarehousingDetail>['columns'] {
        return [
                         {
                            field: 'purchaseOrderId',
                            title: '采购订单id',
                            minWidth: 120,
                            slots: { default: 'purchaseOrderId' },
                        },
                         {
                            field: 'purchaseOrderCode',
                            title: '关联采购单编码',
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
                            field: 'commonOperationCode',
                            title: '公共操作编码',
                            minWidth: 120,
                            slots: { default: 'commonOperationCode' },
                        },
                         {
                            field: 'commonOperationName',
                            title: '公共操作名称',
                            minWidth: 120,
                            slots: { default: 'commonOperationName' },
                        },
                         {
                            field: 'commonOperationType',
                            title: '公共操作类型',
                            minWidth: 120,
                            slots: { default: 'commonOperationType' },
                                    params: {
                                        options: [],
                                    },
                        },
                         {
                            field: 'assetId',
                            title: '资产ID',
                            minWidth: 120,
                            slots: { default: 'assetId' },
                        },
                         {
                            field: 'assetCode',
                            title: '资产编码',
                            minWidth: 120,
                            slots: { default: 'assetCode' },
                        },
                         {
                            field: 'assetName',
                            title: '资产名称',
                            minWidth: 120,
                            slots: { default: 'assetName' },
                        },
                         {
                            field: 'assetCategoryCode',
                            title: '资产类型编码',
                            minWidth: 120,
                            slots: { default: 'assetCategoryCode' },
                        },
                         {
                            field: 'assetCategoryName',
                            title: '资产类型名称',
                            minWidth: 120,
                            slots: { default: 'assetCategoryName' },
                        },
                         {
                            field: 'assetModel',
                            title: '规格型号',
                            minWidth: 120,
                            slots: { default: 'assetModel' },
                        },
                         {
                            field: 'assetUnit',
                            title: '计量单位',
                            minWidth: 120,
                            slots: { default: 'assetUnit' },
                        },
                         {
                            field: 'manufacturer',
                            title: '厂商',
                            minWidth: 120,
                            slots: { default: 'manufacturer' },
                        },
                         {
                            field: 'brand',
                            title: '品牌',
                            minWidth: 120,
                            slots: { default: 'brand' },
                        },
                         {
                            field: 'serialNumber',
                            title: '序列号',
                            minWidth: 120,
                            slots: { default: 'serialNumber' },
                        },
                         {
                            field: 'assetStatusCode',
                            title: '资产状态编码',
                            minWidth: 120,
                            slots: { default: 'assetStatusCode' },
                        },
                         {
                            field: 'assetStatusName',
                            title: '资产状态名称',
                            minWidth: 120,
                            slots: { default: 'assetStatusName' },
                        },
                         {
                            field: 'assetSourceCode',
                            title: '资产来源编码',
                            minWidth: 120,
                            slots: { default: 'assetSourceCode' },
                        },
                         {
                            field: 'assetSourceName',
                            title: '资料来源名称',
                            minWidth: 120,
                            slots: { default: 'assetSourceName' },
                        },
                         {
                            field: 'purchaseDate',
                            title: '购买日期',
                            minWidth: 120,
                            slots: { default: 'purchaseDate' },
                        },
                         {
                            field: 'purchasePrice',
                            title: '购买价格',
                            minWidth: 120,
                            slots: { default: 'purchasePrice' },
                        },
                         {
                            field: 'dateOfProduction',
                            title: '出场日期',
                            minWidth: 120,
                            slots: { default: 'dateOfProduction' },
                        },
                         {
                            field: 'adminCompanyId',
                            title: '管理公司id',
                            minWidth: 120,
                            slots: { default: 'adminCompanyId' },
                        },
                         {
                            field: 'adminCompanyName',
                            title: '管理公司名称',
                            minWidth: 120,
                            slots: { default: 'adminCompanyName' },
                        },
                         {
                            field: 'adminDeptId',
                            title: '管理部门编码',
                            minWidth: 120,
                            slots: { default: 'adminDeptId' },
                        },
                         {
                            field: 'adminDeptName',
                            title: '管理部门名称',
                            minWidth: 120,
                            slots: { default: 'adminDeptName' },
                        },
                         {
                            field: 'adminManagerId',
                            title: '管理人员编码',
                            minWidth: 120,
                            slots: { default: 'adminManagerId' },
                        },
                         {
                            field: 'adminManagerName',
                            title: '管理人员名称',
                            minWidth: 120,
                            slots: { default: 'adminManagerName' },
                        },
                         {
                            field: 'wmsStoreCode',
                            title: '存放仓库编码',
                            minWidth: 120,
                            slots: { default: 'wmsStoreCode' },
                        },
                         {
                            field: 'wmsStoreName',
                            title: '存放仓库名称',
                            minWidth: 120,
                            slots: { default: 'wmsStoreName' },
                        },
                         {
                            field: 'residualValueRate',
                            title: '残值率',
                            minWidth: 120,
                            slots: { default: 'residualValueRate' },
                        },
                         {
                            field: 'useCompanyId',
                            title: '使用公司id',
                            minWidth: 120,
                            slots: { default: 'useCompanyId' },
                        },
                         {
                            field: 'useCompanyName',
                            title: '使用公司名称',
                            minWidth: 120,
                            slots: { default: 'useCompanyName' },
                        },
                         {
                            field: 'useDeptId',
                            title: '使用部门编码',
                            minWidth: 120,
                            slots: { default: 'useDeptId' },
                        },
                         {
                            field: 'useDeptName',
                            title: '使用部门名称',
                            minWidth: 120,
                            slots: { default: 'useDeptName' },
                        },
                         {
                            field: 'useAccountId',
                            title: '使用人员编码',
                            minWidth: 120,
                            slots: { default: 'useAccountId' },
                        },
                         {
                            field: 'useAccountName',
                            title: '使用人员名称',
                            minWidth: 120,
                            slots: { default: 'useAccountName' },
                        },
                         {
                            field: 'transferUseDeptId',
                            title: '调入部门编码',
                            minWidth: 120,
                            slots: { default: 'transferUseDeptId' },
                        },
                         {
                            field: 'transferUseDeptName',
                            title: '调入部门名称',
                            minWidth: 120,
                            slots: { default: 'transferUseDeptName' },
                        },
                         {
                            field: 'transferUseAccountId',
                            title: '调入部门使用人员编码',
                            minWidth: 120,
                            slots: { default: 'transferUseAccountId' },
                        },
                         {
                            field: 'transferUseAccountName',
                            title: '调入使用人员名称',
                            minWidth: 120,
                            slots: { default: 'transferUseAccountName' },
                        },
                         {
                            field: 'assetIcon',
                            title: '资产图片',
                            minWidth: 120,
                            slots: { default: 'assetIcon' },
                        },
                         {
                            field: 'assetFile',
                            title: '资产附件',
                            minWidth: 120,
                            slots: { default: 'assetFile' },
                        },
                         {
                            field: 'isJoinAsset',
                            title: '是否进入资产列表',
                            minWidth: 120,
                            slots: { default: 'isJoinAsset' },
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
                            field: 'storeAddress',
                            title: '仓库地址',
                            minWidth: 120,
                            slots: { default: 'storeAddress' },
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
