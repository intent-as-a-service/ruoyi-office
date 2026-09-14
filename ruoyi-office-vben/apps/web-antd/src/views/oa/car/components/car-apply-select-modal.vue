<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { CarApplyBillApi } from '#/api/oa/car/carapply';

import { reactive } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { BpmProcessInstanceStatus } from '@vben/constants';
import { useUserStore } from '@vben/stores';

import { message } from 'ant-design-vue';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { getCarApplyBillPage } from '#/api/oa/car/carapply';

import {
  useCarApplySelectColumns,
  useCarApplySelectFormSchema,
} from './car-apply-select-data';

/** 定义组件事件 */
const emit = defineEmits<{
  (e: 'select', bill: CarApplyBillApi.CarApplyBill): void;
}>();

const formData = reactive({
  selectedBill: null as CarApplyBillApi.CarApplyBill | null,
});

/** 表格实例 */
const [Grid, gridApi] = useVbenVxeGrid({
  separator: false,
  formOptions: {
    schema: useCarApplySelectFormSchema(),
    submitOnChange: true,
    collapsed: true,
  },
  gridOptions: {
    columns: useCarApplySelectColumns(),
    height: 440,
    keepSource: true,
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) => {
          const queryParams = {
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            processStatus: BpmProcessInstanceStatus.APPROVE,
            creator: useUserStore().userInfo?.id,
            ...formValues,
            // 仅查询未还车的用车申请单
            returnStatus: 1, // 1-待还车
          };
          return await getCarApplyBillPage(queryParams);
        },
      },
    },
    rowConfig: { keyField: 'id' },
    toolbarConfig: { enabled: false },
    radioConfig: { highlight: true },
    pagerConfig: { enabled: true },
  } as VxeTableGridOptions<CarApplyBillApi.CarApplyBill>,
  gridEvents: {
    radioChange: ({ row }: { row: CarApplyBillApi.CarApplyBill }) => {
      formData.selectedBill = row;
    },
    cellDblclick: ({ row }: { row: CarApplyBillApi.CarApplyBill }) => {
      formData.selectedBill = row;
      handleConfirm();
    },
  },
});

/** 模态框实例 */
const [Modal, modalApi] = useVbenModal({
  title: '选择用车申请单',
  class: 'w-3/5 max-w-4xl',
  async onConfirm() {
    return handleConfirm();
  },
});

/** 确认选择 */
async function handleConfirm() {
  if (!formData.selectedBill) {
    message.error('请选择用车申请单');
    return false;
  }
  emit('select', formData.selectedBill);
  formData.selectedBill = null;
  await modalApi.close();
  return true;
}

/** 暴露modal API供外部调用 */
defineExpose({
  modalApi,
  gridApi,
});
</script>

<template>
  <Modal>
    <Grid />
  </Modal>
</template>
