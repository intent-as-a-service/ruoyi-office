import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { CarApplyBillApi } from '#/api/oa/car/carapply';

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
      fieldName: 'carNo',
      label: '车辆',
      component: 'HelpInput',
      componentProps: {
        allowClear: true,
        placeholder: '请选择车辆',
        onClick: () => {
          modalRef.value?.modalApi.open();
        },
      },
    },
    {
      fieldName: 'companyId',
      label: '所属公司',
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
      fieldName: 'createTime',
      label: '创建时间',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
        allowClear: true,
      },
    },
    {
      fieldName: 'returnStatus',
      label: '还车状态',
      component: 'Select',
      componentProps: {
        allowClear: true,
        options: getDictOptions(DICT_TYPE.OA_CAR_RETURN_STATUS, 'number'),
        placeholder: '请选择还车状态',
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<CarApplyBillApi.CarApplyBill>['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    createRouterLinkColumn({
      field: 'billCode',
      title: '单据编号',
      path: '/oa/car/car-apply-info',
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
      field: 'carNo',
      title: '车辆',
      minWidth: 120,
      headerAlign: 'center',
      align: 'left',
    },
    {
      field: 'cause',
      title: '用车事由',
      minWidth: 120,
      headerAlign: 'center',
      align: 'left',
    },
    {
      field: 'goTime',
      title: '出车时间',
      minWidth: 140,
      formatter: 'formatDateTime',
      headerAlign: 'center',
      align: 'left',
    },
    {
      field: 'returnTime',
      title: '回车时间',
      minWidth: 140,
      formatter: 'formatDateTime',
      headerAlign: 'center',
      align: 'left',
    },
    {
      field: 'goArea',
      title: '出车地点',
      minWidth: 120,
      headerAlign: 'center',
      align: 'left',
    },
    {
      field: 'returnArea',
      title: '回车地点',
      minWidth: 120,
      headerAlign: 'center',
      align: 'left',
    },
    {
      field: 'passenger',
      title: '随行人',
      minWidth: 120,
      headerAlign: 'center',
      align: 'left',
    },
    {
      field: 'returnStatus',
      title: '还车状态',
      minWidth: 100,
      headerAlign: 'center',
      align: 'center',
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.OA_CAR_RETURN_STATUS },
      },
    },
    {
      field: 'creatorName',
      title: '创建者姓名',
      minWidth: 120,
      headerAlign: 'center',
      align: 'left',
    },
    {
      field: 'deptName',
      title: '申请部门',
      minWidth: 120,
      headerAlign: 'center',
      align: 'left',
    },
    {
      field: 'companyName',
      title: '公司名称',
      minWidth: 120,
      headerAlign: 'center',
      align: 'left',
    },
    {
      field: 'createTime',
      title: '创建时间',
      minWidth: 140,
      formatter: 'formatDateTime',
      headerAlign: 'center',
      align: 'left',
    },

    {
      title: '操作',
      width: 100,
      fixed: 'right',
      headerAlign: 'center',
      slots: { default: 'actions' },
    },
  ];
}
