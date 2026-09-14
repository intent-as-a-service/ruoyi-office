<script lang="ts" setup>
import type { EmployeeSelectConfig } from './employee-select-data';

import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EmployeeArchiveApi } from '#/api/hrm/employee';

import { reactive } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { useVbenVxeGrid } from '#/adapter/vxe-table';

import {
  queryEmployeeSelectPage,
  useEmployeeSelectColumns,
  useEmployeeSelectFormSchema,
} from './employee-select-data';

/** 组件Props */
const props = withDefaults(
  defineProps<{
    /** 排除的人员状态列表 */
    excludeEmployeeStatusList?: number[];
    /** 包含的人员状态列表（优先级高于excludeEmployeeStatusList） */
    includeEmployeeStatusList?: number[];
    /** 是否在搜索表单中显示人员状态筛选 */
    showEmployeeStatusFilter?: boolean;
  }>(),
  {
    excludeEmployeeStatusList: undefined,
    includeEmployeeStatusList: undefined,
    showEmployeeStatusFilter: true,
  },
);

/** 定义组件事件 */
const emit = defineEmits<{
  (e: 'select', employee: EmployeeArchiveApi.EmployeeArchive): void;
}>();

const formData = reactive({
  selectedEmployee: null as EmployeeArchiveApi.EmployeeArchive | null,
});

// 构建配置对象
const config: EmployeeSelectConfig = {
  includeEmployeeStatusList: props.includeEmployeeStatusList,
  excludeEmployeeStatusList: props.excludeEmployeeStatusList,
  showEmployeeStatusFilter: props.showEmployeeStatusFilter,
};

// 构建gridOptions配置
const gridOptions = {
  columns: useEmployeeSelectColumns(config),
  height: 440,
  keepSource: true,
  proxyConfig: {
    ajax: {
      query: async ({ page }, formValues) => {
        return await queryEmployeeSelectPage(page, formValues, config);
      },
    },
  },
  rowConfig: {
    keyField: 'id',
  },
  radioConfig: {
    labelField: 'id',
    trigger: 'row',
  },
  pagerConfig: {
    enabled: true,
  },
} as VxeTableGridOptions<EmployeeArchiveApi.EmployeeArchive>;

/** 表格实例 */
const [Grid] = useVbenVxeGrid({
  separator: false,
  formOptions: {
    schema: useEmployeeSelectFormSchema(config),
    submitOnChange: true,
    collapsed: true,
  },
  gridOptions: gridOptions as any,
  gridEvents: {
    radioChange: ({ row }: { row: EmployeeArchiveApi.EmployeeArchive }) => {
      formData.selectedEmployee = row;
    },
    cellDblclick: ({ row }: { row: EmployeeArchiveApi.EmployeeArchive }) => {
      formData.selectedEmployee = row;
      handleConfirm();
    },
  },
});

/** 模态框实例 */
const [Modal, modalApi] = useVbenModal({
  title: '选择员工',
  class: 'w-3/5 max-w-4xl',
  async onConfirm() {
    return handleConfirm();
  },
});

/** 确认选择 */
async function handleConfirm() {
  if (!formData.selectedEmployee) {
    message.error('请选择员工');
    return false;
  }

  emit('select', formData.selectedEmployee);
  formData.selectedEmployee = null;
  await modalApi.close();
  return true;
}

/** 暴露modal API供外部调用 */
defineExpose({
  modalApi,
});
</script>

<template>
  <Modal>
    <Grid />
  </Modal>
</template>
