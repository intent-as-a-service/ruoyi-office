import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { CarApplyBillApi } from '#/api/oa/car/carapply';

/** 用车申请单选择-搜索表单 */
export function useCarApplySelectFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'billCode',
      label: '单据编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入单据编号',
        allowClear: true,
      },
    },
    {
      fieldName: 'carNo',
      label: '车辆',
      component: 'Input',
      componentProps: {
        allowClear: true,
        placeholder: '请输入车牌号',
      },
    },
  ];
}

/** 用车申请单选择-表格列 */
export function useCarApplySelectColumns(): VxeTableGridOptions<CarApplyBillApi.CarApplyBill>['columns'] {
  return [
    { type: 'radio', width: 60, align: 'center' },
    { field: 'billCode', title: '单据编号', width: 140 },
    { field: 'carNo', title: '车牌号', width: 120 },
    { field: 'cause', title: '用车事由', minWidth: 160 },
    {
      field: 'goTime',
      title: '出车时间',
      width: 160,
      formatter: 'formatDateTime',
    },
    {
      field: 'returnTime',
      title: '回车时间',
      width: 160,
      formatter: 'formatDateTime',
    },
    { field: 'creatorName', title: '申请人', width: 100 },
    { field: 'deptName', title: '部门', width: 140 },
  ];
}
