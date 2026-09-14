import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EmployeeTransferBillApi } from '#/api/hrm/employee-transfer';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import { createRouterLinkColumn } from '#/adapter/vxe-table';
import { getRangePickerDefaultProps } from '#/utils';
import { getCurrentUserCompanyDeptTree } from '#/utils/dept-tree';

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'billCode',
      label: '单据编号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入单据编号',
      },
    },
    {
      fieldName: 'processStatus',
      label: '单据状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(
          DICT_TYPE.BPM_PROCESS_INSTANCE_STATUS,
          'number',
        ),
        placeholder: '请选择单据状态',
      },
    },
    {
      fieldName: 'employeeNo',
      label: '员工工号',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入员工工号',
      },
    },
    {
      fieldName: 'name',
      label: '员工姓名',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入员工姓名',
      },
    },
    {
      fieldName: 'empDeptId',
      label: '员工所属部门',
      component: 'ApiTreeSelect',
      componentProps: {
        allowClear: true,
        api: () => getCurrentUserCompanyDeptTree(false), // false表示不包含公司本身
        labelField: 'name',
        valueField: 'id',
        childrenField: 'children',
        placeholder: '请选择员工所属部门',
        treeDefaultExpandAll: true,
      },
    },
    {
      fieldName: 'transferType',
      label: '异动类型',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.HRM_TRANSFER_TYPE),
        placeholder: '请选择异动类型',
      },
    },
    {
      fieldName: 'createTime',
      label: '创建时间',
      component: 'RangePicker',
      componentProps: getRangePickerDefaultProps(),
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<EmployeeTransferBillApi.EmployeeTransferBill>['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    createRouterLinkColumn({
      field: 'billCode',
      title: '单据编号',
      path: '/hrm/employee-relation/transfer-info',
      idField: 'id',
      queryParam: 'id',
    }),
    {
      field: 'processStatus',
      title: '单据状态',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.BPM_PROCESS_INSTANCE_STATUS },
      },
    },
    {
      field: 'employeeNo',
      title: '员工工号',
      minWidth: 120,
    },
    {
      field: 'name',
      title: '员工姓名',
      minWidth: 100,
    },
    {
      field: 'empDeptName',
      title: '员工所属部门',
      minWidth: 120,
    },
    {
      field: 'empCompanyName',
      title: '员工所属公司',
      minWidth: 120,
    },
    {
      field: 'transferType',
      title: '异动类型',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.HRM_TRANSFER_TYPE },
      },
    },
    {
      field: 'originalDeptName',
      title: '原部门',
      minWidth: 120,
    },
    {
      field: 'newDeptName',
      title: '变更为部门',
      minWidth: 120,
    },
    {
      field: 'originalCompanyName',
      title: '原公司',
      minWidth: 120,
    },
    {
      field: 'newCompanyName',
      title: '变更为公司',
      minWidth: 120,
    },
    {
      field: 'effectiveDate',
      title: '生效日期',
      minWidth: 120,
      formatter: 'formatDate',
    },
    {
      field: 'creatorName',
      title: '申请人',
      minWidth: 100,
    },
    {
      field: 'createTime',
      title: '创建时间',
      minWidth: 140,
      formatter: 'formatDateTime',
    },
    {
      title: '操作',
      width: 100,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}

