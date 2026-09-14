<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { SealApi } from '#/api/oa/seal/sealinfo';

import { reactive } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { getSealPage } from '#/api/oa/seal/sealinfo';

import {
  useSealSelectColumns,
  useSealSelectFormSchema,
} from './seal-select-data';

/** 定义组件事件 */
const emit = defineEmits<{
  (e: 'select', seal: SealApi.Seal): void;
}>();

const formData = reactive({
  selectedSeal: null as null | SealApi.Seal,
});

/** 表格实例 */
const [Grid] = useVbenVxeGrid({
  separator: false,
  formOptions: {
    schema: useSealSelectFormSchema(),
    submitOnChange: true,
    collapsed: true,
  },
  gridOptions: {
    columns: useSealSelectColumns(),
    height: 440,
    keepSource: true,
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) => {
          // 合并分类筛选条件
          const queryParams = {
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            status: 0, // 只显示在库状态的印章
            ...formValues,
          };
          return await getSealPage(queryParams);
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
  } as VxeTableGridOptions<SealApi.Seal>,
  gridEvents: {
    radioChange: ({ row }: { row: SealApi.Seal }) => {
      formData.selectedSeal = row;
    },
    cellDblclick: ({ row }: { row: SealApi.Seal }) => {
      // 双击直接选择
      formData.selectedSeal = row;
      handleConfirm();
    },
  },
});

/** 模态框实例 */
const [Modal, modalApi] = useVbenModal({
  title: '选择印章',
  class: 'w-3/5 max-w-4xl',
  async onConfirm() {
    return handleConfirm();
  },
});

/** 确认选择 */
async function handleConfirm() {
  if (!formData.selectedSeal) {
    message.error('请选择印章');
    return false;
  }

  emit('select', formData.selectedSeal);
  formData.selectedSeal = null;
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
