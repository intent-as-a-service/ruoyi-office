import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EmployeeResignationBillApi } from '#/api/hrm/employee-resignation';

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
      fieldName: 'resignationType',
      label: '离职类型',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.HRM_RESIGNATION_TYPE),
        placeholder: '请选择离职类型',
      },
    },
    {
      fieldName: 'resignationReason',
      label: '离职原因',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.HRM_RESIGNATION_REASON),
        placeholder: '请选择离职原因',
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
export function useGridColumns(): VxeTableGridOptions<EmployeeResignationBillApi.EmployeeResignationBill>['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    createRouterLinkColumn({
      field: 'billCode',
      title: '单据编号',
      path: '/hrm/employee-relation/resignation-info',
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
      field: 'resignationType',
      title: '离职类型',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.HRM_RESIGNATION_TYPE },
      },
    },
    {
      field: 'resignationReason',
      title: '离职原因',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.HRM_RESIGNATION_REASON },
      },
    },
    {
      field: 'resignationDate',
      title: '离职日期',
      minWidth: 120,
      formatter: 'formatDate',
    },
    {
      field: 'lastWorkingDate',
      title: '最后工作日期',
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
