<script lang="ts" setup>
import type { OnActionClickParams } from '#/adapter/vxe-table';
import type { PurchaseOrderApi } from '#/api/wms/purchaseorder';

import { h, nextTick, watch } from 'vue';

import { Plus } from '@vben/icons';

import { Button, Input, RadioGroup } from 'ant-design-vue';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { getPurchaseOrderDetailListByPurchaseOrderId } from '#/api/wms/purchaseorder';
import { $t } from '#/locales';

import { usePurchaseOrderDetailGridEditColumns } from '../data';

const props = defineProps<{
  purchaseOrderId?: number; // 采购订单id（主表的关联字段）
}>();

/** 表格操作按钮的回调函数 */
function onActionClick({
  code,
  row,
}: OnActionClickParams<PurchaseOrderApi.PurchaseOrderDetail>) {
  switch (code) {
    case 'delete': {
      onDelete(row);
      break;
    }
  }
}

const [Grid, gridApi] = useVbenVxeGrid({
  gridOptions: {
    columns: usePurchaseOrderDetailGridEditColumns(onActionClick),
    border: true,
    showOverflow: true,
    autoResize: true,
    keepSource: true,
    rowConfig: {
      keyField: 'id',
    },
    pagerConfig: {
      enabled: false,
    },
    toolbarConfig: {
      enabled: false,
    },
  },
});

/** 添加采购订单明细 */
const onAdd = async () => {
  await gridApi.grid.insertAt({} as PurchaseOrderApi.PurchaseOrderDetail, -1);
};

/** 删除采购订单明细 */
const onDelete = async (row: PurchaseOrderApi.PurchaseOrderDetail) => {
  await gridApi.grid.remove(row);
};

/** 提供获取表格数据的方法供父组件调用 */
defineExpose({
  getData: (): PurchaseOrderApi.PurchaseOrderDetail[] => {
    const data =
      gridApi.grid.getData() as PurchaseOrderApi.PurchaseOrderDetail[];
    const removeRecords =
      gridApi.grid.getRemoveRecords() as PurchaseOrderApi.PurchaseOrderDetail[];
    const insertRecords =
      gridApi.grid.getInsertRecords() as PurchaseOrderApi.PurchaseOrderDetail[];
    return [
      ...data.filter(
        (row) => !removeRecords.some((removed) => removed.id === row.id),
      ),
      ...insertRecords.map((row: any) => ({ ...row, id: undefined })),
    ];
  },
});

/** 监听主表的关联字段的变化，加载对应的子表数据 */
watch(
  () => props.purchaseOrderId,
  async (val) => {
    if (!val) {
      return;
    }
    await nextTick();
    await gridApi.grid.loadData(
      await getPurchaseOrderDetailListByPurchaseOrderId(props.purchaseOrderId!),
    );
  },
  { immediate: true },
);
</script>

<template>
  <Grid class="mx-4">
    <template #purchaseOrderCode="{ row }">
      <Input v-model:value="row.purchaseOrderCode" />
    </template>
    <template #purchaseOrderName="{ row }">
      <Input v-model:value="row.purchaseOrderName" />
    </template>
    <template #categoryCode="{ row }">
      <Input v-model:value="row.categoryCode" />
    </template>
    <template #categoryName="{ row }">
      <Input v-model:value="row.categoryName" />
    </template>
    <template #goodsCode="{ row }">
      <Input v-model:value="row.goodsCode" />
    </template>
    <template #goodsName="{ row }">
      <Input v-model:value="row.goodsName" />
    </template>
    <template #purchasePrice="{ row }">
      <Input v-model:value="row.purchasePrice" />
    </template>
    <template #purchaseNum="{ row }">
      <Input v-model:value="row.purchaseNum" />
    </template>
    <template #purchaseTotalAmonut="{ row }">
      <Input v-model:value="row.purchaseTotalAmonut" />
    </template>
    <template #sort="{ row }">
      <Input v-model:value="row.sort" />
    </template>
    <template #status="{ row, column }">
      <RadioGroup v-model:value="row.status" :options="column.params.options" />
    </template>
    <template #remark="{ row }">
      <Input v-model:value="row.remark" />
    </template>
  </Grid>
  <div class="-mt-4 flex justify-center">
    <Button
      :icon="h(Plus)"
      type="primary"
      ghost
      @click="onAdd"
      v-access:code="['wms:purchase-order:create']"
    >
      {{ $t('ui.actionTitle.create', ['采购订单明细']) }}
    </Button>
  </div>
</template>
