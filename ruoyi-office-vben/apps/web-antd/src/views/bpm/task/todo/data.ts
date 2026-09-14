import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';

import { handleTree } from '@vben/utils';

import { getCategorySimpleList } from '#/api/bpm/category';
import { getSimpleProcessDefinitionList } from '#/api/bpm/definition';
import { getCompanyList } from '#/api/system/dept';
import { getRangePickerDefaultProps } from '#/utils';
import { getCurrentUserCompanyDeptTree } from '#/utils/dept-tree';

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'category',
      label: '系统分类',
      component: 'ApiSelect',
      componentProps: {
        placeholder: '请输入系统分类',
        allowClear: true,
        api: () => getCategorySimpleList(),
        labelField: 'name',
        valueField: 'code',
      },
    },
    {
      fieldName: 'billType',
      label: '单据类型',
      component: 'ApiSelect',
      dependencies: {
        triggerFields: ['category'],
      },
      componentProps: (values) => ({
        placeholder: '请输入单据类型',
        allowClear: true,
        // 每次下拉展开都重新加载，并携带当前 category 作为查询参数
        immediate: false,
        alwaysLoad: true,
        params: { category: values?.category },
        api: (params: any) => getSimpleProcessDefinitionList(params?.category),
        labelField: 'name',
        valueField: 'key',
      }),
    },
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
      fieldName: 'billCreateTime',
      label: '单据日期',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
        allowClear: true,
      },
    },
    {
      fieldName: 'receiveTime',
      label: '接收时间',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
        allowClear: true,
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
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions['columns'] {
  return [
    {
      field: 'processInstance.name',
      title: '单据类型',
      minWidth: 140,
      fixed: 'left',
    },
    {
      // 单据编号点击打开办理，样式与用车申请一致
      field: 'processInstance.billCode',
      title: '单据编号',
      minWidth: 160,
      align: 'center',
      cellRender: {
        name: 'CellRouterLink',
        props: {
          name: 'BpmProcessInstanceTodoDetail',
          // 传参保持与办理按钮一致
          queryFields: [
            { key: 'id', field: 'processInstance.id' },
            { key: 'taskId', field: 'id' },
            { key: 'nodeKey', field: 'taskDefinitionKey' },
          ],
          query: {
            isTodo: 'true',
          },
        },
      },
    },
    {
      field: 'processInstance.summary',
      title: '摘要',
      minWidth: 200,
      formatter: ({ cellValue }) => {
        return cellValue && cellValue.length > 0
          ? cellValue
              .map((item: any) => {
                const key = item?.key;
                const value = item?.value ?? '';
                return key && `${key}`.trim().length > 0
                  ? `${key} : ${value}`
                  : `${value}`;
              })
              .join('\n')
          : '-';
      },
    },
    {
      field: 'processInstance.startUser.nickname',
      title: '发起人',
      minWidth: 100,
    },
    {
      field: 'processInstance.companyName',
      title: '所属公司',
      minWidth: 160,
    },
    {
      field: 'processInstance.deptName',
      title: '所属部门',
      minWidth: 160,
    },
    {
      field: 'processInstance.createTime',
      title: '单据日期',
      minWidth: 100,
      formatter: 'formatDate',
    },
    {
      field: 'name',
      title: '任务节点',
      minWidth: 100,
    },
    {
      field: 'createTime',
      title: '接收时间',
      minWidth: 180,
      formatter: 'formatDateTime',
    },
    {
      title: '操作',
      width: 120,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
