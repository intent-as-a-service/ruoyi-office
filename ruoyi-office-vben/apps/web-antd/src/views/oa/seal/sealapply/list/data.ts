import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { SealApplyBillApi } from '#/api/oa/seal/sealapply';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';
import { handleTree } from '@vben/utils';

import { createRouterLinkColumn } from '#/adapter/vxe-table';
import { getCompanyList } from '#/api/system/dept';
import { getRangePickerDefaultProps } from '#/utils';
import { getCurrentUserCompanyDeptTree } from '#/utils/dept-tree';

/** 列表的搜索表单 */
export function useGridFormSchema(modalRef?: any): VbenFormSchema[] {
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
      fieldName: 'sealNo',
      label: '印章',
      component: 'HelpInput',
      componentProps: {
        allowClear: true,
        placeholder: '请选择印章',
        onClick: () => {
          modalRef.value?.modalApi.open();
        },
      },
    },
    {
      fieldName: 'sealId',
      label: '印章ID',
      component: 'ApiTreeSelect',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
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
      fieldName: 'deptId',
      label: '申请部门',
      component: 'ApiTreeSelect',
      componentProps: {
        allowClear: true,
        api: () => getCurrentUserCompanyDeptTree(false), // false表示不包含公司本身
        labelField: 'name',
        valueField: 'id',
        childrenField: 'children',
        placeholder: '请选择申请部门',
        treeDefaultExpandAll: true,
      },
    },
    {
      fieldName: 'useType',
      label: '用章类型',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.OA_SEAL_USE_TYPE, 'number'),
        placeholder: '请选择用章类型',
      },
    },
    {
      fieldName: 'useMode',
      label: '用章方式',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.OA_SEAL_USE_MODE, 'number'),
        placeholder: '请选择用章方式',
      },
    },
    {
      fieldName: 'useStatus',
      label: '用印状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.OA_SEAL_USE_STATUS, 'number'),
        placeholder: '请选择用印状态',
      },
    },
    {
      fieldName: 'isUrgent',
      label: '是否紧急',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.OA_IS_URGENT, 'number'),
        placeholder: '请选择是否紧急',
      },
    },
    {
      fieldName: 'expectedUseTime',
      label: '预计用章时间',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
        showTime: true,
        format: 'YYYY-MM-DD HH:mm:ss',
        valueFormat: 'YYYY-MM-DD HH:mm:ss',
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
export function useGridColumns(): VxeTableGridOptions<SealApplyBillApi.SealApplyBill>['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    createRouterLinkColumn({
      field: 'billCode',
      title: '单据编号',
      path: '/oa/seal/seal-apply-info',
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
      field: 'sealNo',
      title: '印章编号',
      minWidth: 120,
    },
    {
      field: 'sealName',
      title: '印章名称',
      minWidth: 120,
    },
    {
      field: 'useType',
      title: '用章类型',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.OA_SEAL_USE_TYPE },
      },
    },
    {
      field: 'useMode',
      title: '用章方式',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.OA_SEAL_USE_MODE },
      },
    },
    {
      field: 'cause',
      title: '用章事由',
      minWidth: 200,
    },
    {
      field: 'expectedUseTime',
      title: '预计用章时间',
      minWidth: 140,
      formatter: 'formatDateTime',
    },
    {
      field: 'expectedReturnTime',
      title: '预计归还时间',
      minWidth: 140,
      formatter: 'formatDateTime',
    },
    {
      field: 'useStatus',
      title: '用印状态',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.OA_SEAL_USE_STATUS },
      },
    },
    {
      field: 'isUrgent',
      title: '是否紧急',
      minWidth: 100,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.OA_IS_URGENT },
      },
    },
    {
      field: 'creatorName',
      title: '申请人',
      minWidth: 100,
    },
    {
      field: 'deptName',
      title: '申请部门',
      minWidth: 120,
    },
    {
      field: 'companyName',
      title: '所属公司',
      minWidth: 120,
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
