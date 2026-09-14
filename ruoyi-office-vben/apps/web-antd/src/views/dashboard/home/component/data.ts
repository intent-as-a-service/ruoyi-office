import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';

import { computed, h, ref } from 'vue';

import { CommonStatusEnum, DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import { Tag } from 'ant-design-vue';

import { z } from '#/adapter/form';
import { getCategoryList } from '#/api/system/home/component';

// 获取分类选项
const categoryOptions = ref<any[]>([]);

async function loadCategoryOptions() {
  const categories = await getCategoryList();
  categoryOptions.value = categories.map((item: any) => ({
    label: item.name,
    value: item.id,
  }));
}

loadCategoryOptions();

/** 新增/修改的表单 */
export function useFormSchema(): VbenFormSchema[] {
  return [
    {
      component: 'Input',
      fieldName: 'id',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      component: 'Select',
      fieldName: 'categoryId',
      label: '组件分类',
      componentProps: computed(() => ({
        options: categoryOptions.value,
        placeholder: '请选择组件分类',
      })),
      rules: 'required',
    },
    {
      component: 'Input',
      fieldName: 'name',
      label: '组件名称',
      componentProps: {
        placeholder: '请输入组件名称',
      },
      rules: 'required',
    },
    {
      component: 'Input',
      fieldName: 'code',
      label: '组件编码',
      componentProps: {
        placeholder: '请输入组件编码，如: analytics_visits',
      },
      rules: 'required',
    },
    {
      component: 'Input',
      fieldName: 'componentPath',
      label: '组件路径',
      componentProps: {
        placeholder:
          '如: dashboard/home/components/statistics/analytics-visits.vue',
      },
      rules: 'required',
    },
    {
      fieldName: 'description',
      label: '组件描述',
      component: 'Textarea',
      componentProps: {
        placeholder: '请输入组件描述',
        rows: 2,
      },
    },
    {
      fieldName: 'defaultWidth',
      label: '默认宽度',
      component: 'InputNumber',
      componentProps: {
        min: 1,
        max: 24,
        placeholder: '网格列数（1-24）',
      },
      rules: 'required',
    },
    {
      fieldName: 'defaultHeight',
      label: '默认高度',
      component: 'InputNumber',
      componentProps: {
        min: 1,
        max: 20,
        placeholder: '网格行数',
      },
      rules: 'required',
    },
    {
      fieldName: 'status',
      label: '组件状态',
      component: 'RadioGroup',
      componentProps: {
        options: getDictOptions(DICT_TYPE.COMMON_STATUS, 'number'),
        buttonStyle: 'solid',
        optionType: 'button',
      },
      rules: z.number().default(CommonStatusEnum.ENABLE),
    },
    {
      fieldName: 'sort',
      label: '显示顺序',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入显示顺序',
      },
      rules: 'required',
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'name',
      label: '组件名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入组件名称',
        allowClear: true,
      },
    },
    {
      fieldName: 'code',
      label: '组件编码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入组件编码',
        allowClear: true,
      },
    },
    {
      fieldName: 'categoryId',
      label: '组件分类',
      component: 'Select',
      componentProps: computed(() => ({
        options: categoryOptions.value,
        placeholder: '请选择组件分类',
        allowClear: true,
      })),
    },
    {
      fieldName: 'status',
      label: '组件状态',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.COMMON_STATUS, 'number'),
        placeholder: '请选择组件状态',
        allowClear: true,
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    {
      field: 'id',
      title: '组件编号',
      minWidth: 100,
    },
    {
      field: 'name',
      title: '组件名称',
      minWidth: 150,
    },
    {
      field: 'code',
      title: '组件编码',
      minWidth: 150,
    },
    {
      field: 'categoryId',
      title: '组件分类',
      minWidth: 120,
      cellRender: {
        name: 'CellRender',
        content: ({ row }) => {
          const category = categoryOptions.value.find(
            (c) => c.value === row.categoryId,
          );
          return h(Tag, { color: 'blue' }, () => category?.label || '-');
        },
      },
    },
    {
      field: 'description',
      title: '组件描述',
      minWidth: 200,
    },
    {
      field: 'defaultWidth',
      title: '默认宽度',
      minWidth: 100,
    },
    {
      field: 'defaultHeight',
      title: '默认高度',
      minWidth: 100,
    },
    {
      field: 'status',
      title: '组件状态',
      minWidth: 100,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.COMMON_STATUS },
      },
    },
    {
      field: 'sort',
      title: '显示顺序',
      minWidth: 100,
    },
    {
      field: 'createTime',
      title: '创建时间',
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
