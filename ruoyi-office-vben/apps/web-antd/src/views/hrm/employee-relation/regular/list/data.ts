import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EmployeeRegularBillApi } from '#/api/hrm/employee-regular';

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
      fieldName: 'employeeStatus',
      label: '人员状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.HRM_EMPLOYEE_STATUS, 'number'),
        placeholder: '请选择人员状态',
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
export function useGridColumns(): VxeTableGridOptions<EmployeeRegularBillApi.EmployeeRegularBill>['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    createRouterLinkColumn({
      field: 'billCode',
      title: '单据编号',
      path: '/hrm/employee-relation/regular-info',
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
      field: 'jobPost',
      title: '职位',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.HRM_JOB_POST },
      },
    },
    {
      field: 'jobPosition',
      title: '职务',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.HRM_JOB_POSITION },
      },
    },
    {
      field: 'employeeStatus',
      title: '人员状态',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.HRM_EMPLOYEE_STATUS },
      },
    },
    {
      field: 'entryDate',
      title: '入职日期',
      minWidth: 120,
      formatter: 'formatDate',
    },
    {
      field: 'formalDate',
      title: '转正日期',
      minWidth: 120,
      formatter: 'formatDate',
    },
    {
      field: 'expectedFormalDate',
      title: '预计转正日期',
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
