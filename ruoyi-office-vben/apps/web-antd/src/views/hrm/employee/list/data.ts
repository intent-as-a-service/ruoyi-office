import type { VbenFormSchema } from '@vben/common-ui';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EmployeeArchiveApi } from '#/api/hrm/employee';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import { createRouterLinkColumn } from '#/adapter/vxe-table';

/**
 * 表格搜索表单配置
 */
export function useGridFormSchema(
  deptSelectModalRef?: any,
): VbenFormSchema[] {
  return [
    {
      fieldName: 'employeeNo',
      label: '员工工号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入员工工号',
      },
    },
    {
      fieldName: 'name',
      label: '姓名',
      component: 'Input',
      componentProps: {
        placeholder: '请输入姓名',
      },
    },
    {
      fieldName: 'deptName',
      label: '所属部门',
      component: 'HelpInput',
      componentProps: {
        placeholder: '请选择所属部门',
        bind: {
          onClick: () => {
            if (deptSelectModalRef?.value) {
              deptSelectModalRef.value.modalApi.open();
            }
          },
        },
      },
      dependencies: {
        triggerFields: ['deptName'],
        trigger: (values: any, form: any) => {
          // 当部门名称被清空时，同时清空部门ID
          if (!values.deptName || values.deptName === '') {
            form.setFieldValue('deptId', undefined);
          }
        },
      },
    },
    {
      fieldName: 'deptId',
      label: '部门ID',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'employeeStatus',
      label: '人员状态',
      component: 'Select',
      componentProps: {
        placeholder: '请选择人员状态',
        options: getDictOptions(DICT_TYPE.HRM_EMPLOYEE_STATUS),
      },
    },
    {
      fieldName: 'entryDate',
      label: '入职日期',
      component: 'RangePicker',
      componentProps: {
        placeholder: ['开始日期', '结束日期'],
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD HH:mm:ss',
      },
    },
  ];
}

/**
 * 表格列配置
 */
export function useGridColumns(): VxeTableGridOptions<EmployeeArchiveApi.EmployeeArchive>['columns'] {
  return [
    {
      type: 'checkbox',
      width: 50,
      fixed: 'left',
    },
    {
      ...createRouterLinkColumn({
        field: 'employeeNo',
        title: '员工工号',
        path: '/hrm/employee/employee-archive-info',
        idField: 'id',
        queryParam: 'id',
        minWidth: 120,
      }),
      fixed: 'left',
    },
    {
      title: '姓名',
      field: 'name',
      minWidth: 100,
      fixed: 'left',
    },
    {
      title: '性别',
      field: 'sex',
      width: 80,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.SYSTEM_USER_SEX },
      },
    },
    {
      title: '所属部门',
      field: 'deptName',
      minWidth: 150,
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
      title: '人员状态',
      field: 'employeeStatus',
      width: 100,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.HRM_EMPLOYEE_STATUS },
      },
    },
    {
      title: '入职日期',
      field: 'entryDate',
      width: 120,
      formatter: 'formatDate',
    },
    {
      title: '转正日期',
      field: 'formalDate',
      width: 120,
      formatter: 'formatDate',
    },
    {
      title: '所属单位',
      field: 'companyName',
      minWidth: 180,
    },
    {
      title: '手机号',
      field: 'mobile',
      width: 120,
    },
    {
      title: '邮箱',
      field: 'email',
      width: 180,
    },
    {
      title: '用户状态',
      field: 'userGenerated',
      width: 100,
      cellRender: {
        name: 'VxeCellRender',
        props: {
          render: ({ row }: any) => {
            return row.userGenerated ? '已生成' : '未生成';
          },
        },
      },
    },
    {
      title: '创建时间',
      field: 'createTime',
      width: 160,
      formatter: 'formatDateTime',
    },
    {
      title: '操作',
      field: 'action',
      fixed: 'right',
      width: 220,
      slots: { default: 'actions' },
    },
  ];
}

