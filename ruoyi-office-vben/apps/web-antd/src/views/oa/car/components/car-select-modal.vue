<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { CarApi } from '#/api/oa/car/carinfo';

import { reactive } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { getCarPage } from '#/api/oa/car/carinfo';

import { useCarSelectColumns, useCarSelectFormSchema } from './car-select-data';

/** 定义组件事件 */
const emit = defineEmits<{
  (e: 'select', car: CarApi.Car): void;
}>();

const formData = reactive({
  selectedCar: null as CarApi.Car | null,
});

/** 表格实例 */
const [Grid] = useVbenVxeGrid({
  separator: false,
  formOptions: {
    schema: useCarSelectFormSchema(),
    submitOnChange: true,
    collapsed: true,
  },
  gridOptions: {
    columns: useCarSelectColumns(),
    height: 440,
    keepSource: true,
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) => {
          // 合并分类筛选条件
          const queryParams = {
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            ...formValues,
          };
          return await getCarPage(queryParams);
        },
      },
    },
    rowConfig: {
      keyField: 'id',
    },
    toolbarConfig: {
      enabled: false,
    },
    radioConfig: {
      highlight: true,
    },
    pagerConfig: {
      enabled: true,
    },
  } as VxeTableGridOptions<CarApi.Car>,
  gridEvents: {
    radioChange: ({ row }: { row: CarApi.Car }) => {
      formData.selectedCar = row;
    },
    cellDblclick: ({ row }: { row: CarApi.Car }) => {
      // 双击直接选择
      formData.selectedCar = row;
      handleConfirm();
    },
  },
});

/** 模态框实例 */
const [Modal, modalApi] = useVbenModal({
  title: '选择车辆',
  class: 'w-3/5 max-w-4xl',
  async onConfirm() {
    return handleConfirm();
  },
});

/** 确认选择 */
async function handleConfirm() {
  if (!formData.selectedCar) {
    message.error('请选择车辆');
    return false;
  }

  emit('select', formData.selectedCar);
  formData.selectedCar = null;
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
