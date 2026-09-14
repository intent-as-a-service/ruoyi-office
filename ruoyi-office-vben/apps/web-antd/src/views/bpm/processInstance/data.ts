import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';
import { handleTree } from '@vben/utils';

import { getCategorySimpleList } from '#/api/bpm/category';
import { getSimpleProcessDefinitionList } from '#/api/bpm/definition';
import { getCompanyList } from '#/api/system/dept';
import { getRangePickerDefaultProps } from '#/utils';
import { getCurrentUserCompanyDeptTree } from '#/utils/dept-tree';

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    // {
    //   fieldName: 'startUserId',
    //   label: '发起人',
    //   component: 'ApiSelect',
    //   componentProps: {
    //     placeholder: '请选择发起人',
    //     allowClear: true,
    //     api: () => getSimpleUserList(),
    //     labelField: 'nickname',
    //     valueField: 'id',
    //   },
    // },
    // 流程分类
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
    // 流程状态
    {
      fieldName: 'status',
      label: '流程状态',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.BPM_PROCESS_INSTANCE_STATUS,
          'number',
        ),
        placeholder: '请选择流程状态',
        allowClear: true,
      },
    },
    // 发起时间
    {
      fieldName: 'createTime',
      label: '发起时间',
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
      field: 'name',
      title: '单据类型',
      minWidth: 200,
      fixed: 'left',
    },
    {
      field: 'formVariables.billCode',
      title: '单据编号',
      minWidth: 160,
      align: 'center',
      cellRender: {
        name: 'CellRouterLink',
        props: {
          name: 'BpmProcessInstanceDetail',
          queryFields: [{ key: 'id', field: 'id' }],
          query: {
            isTodo: 'false',
          },
        },
      },
      fixed: 'left',
    },
    {
      field: 'summary',
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

    // 流程状态
    {
      field: 'status',
      title: '流程状态',
      minWidth: 250,
      slots: {
        default: 'slot-status',
      },
    },

    {
      field: 'startTime',
      title: '发起时间',
      minWidth: 180,
      formatter: 'formatDateTime',
    },
    {
      field: 'endTime',
      title: '结束时间',
      minWidth: 180,
      formatter: 'formatDateTime',
    },
    {
      title: '操作',
      width: 180,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
