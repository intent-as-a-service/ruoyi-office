<script lang="ts" setup>
import type { OnActionClickParams } from '#/adapter/vxe-table';
import type { GoodsCommonOperationOrderApi } from '#/api/wms/goodscommonoperationorder';

import { h, nextTick, watch } from 'vue';

import { Plus } from '@vben/icons';

import { Button, DatePicker, Input, RadioGroup, Select } from 'ant-design-vue';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { getGoodsWarehousingDetailListByCommonOperationId } from '#/api/wms/goodscommonoperationorder';
import { FileUpload } from '#/components/upload';
import { $t } from '#/locales';

import { useGoodsWarehousingDetailGridEditColumns } from '../data';

const props = defineProps<{
  commonOperationId?: number; // 公共操作Id（主表的关联字段）
}>();

/** 表格操作按钮的回调函数 */
function onActionClick({
  code,
  row,
}: OnActionClickParams<GoodsCommonOperationOrderApi.GoodsWarehousingDetail>) {
  switch (code) {
    case 'delete': {
      onDelete(row);
      break;
    }
  }
}

const [Grid, gridApi] = useVbenVxeGrid({
  gridOptions: {
    columns: useGoodsWarehousingDetailGridEditColumns(onActionClick),
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

/** 添加采购入库、领用、退库、归还、借用、调拨明细 */
const onAdd = async () => {
  await gridApi.grid.insertAt(
    {} as GoodsCommonOperationOrderApi.GoodsWarehousingDetail,
    -1,
  );
};

/** 删除采购入库、领用、退库、归还、借用、调拨明细 */
const onDelete = async (
  row: GoodsCommonOperationOrderApi.GoodsWarehousingDetail,
) => {
  await gridApi.grid.remove(row);
};

/** 提供获取表格数据的方法供父组件调用 */
defineExpose({
  getData: (): GoodsCommonOperationOrderApi.GoodsWarehousingDetail[] => {
    const data =
      gridApi.grid.getData() as GoodsCommonOperationOrderApi.GoodsWarehousingDetail[];
    const removeRecords =
      gridApi.grid.getRemoveRecords() as GoodsCommonOperationOrderApi.GoodsWarehousingDetail[];
    const insertRecords =
      gridApi.grid.getInsertRecords() as GoodsCommonOperationOrderApi.GoodsWarehousingDetail[];
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
  () => props.commonOperationId,
  async (val) => {
    if (!val) {
      return;
    }
    await nextTick();
    await gridApi.grid.loadData(
      await getGoodsWarehousingDetailListByCommonOperationId(
        props.commonOperationId!,
      ),
    );
  },
  { immediate: true },
);
</script>

<template>
  <Grid class="mx-4">
    <template #purchaseOrderId="{ row }">
      <Input v-model:value="row.purchaseOrderId" />
    </template>
    <template #purchaseOrderCode="{ row }">
      <Input v-model:value="row.purchaseOrderCode" />
    </template>
    <template #purchaseOrderName="{ row }">
      <Input v-model:value="row.purchaseOrderName" />
    </template>
    <template #commonOperationCode="{ row }">
      <Input v-model:value="row.commonOperationCode" />
    </template>
    <template #commonOperationName="{ row }">
      <Input v-model:value="row.commonOperationName" />
    </template>
    <template #commonOperationType="{ row, column }">
      <Select v-model:value="row.commonOperationType" class="w-full">
        <Select.Option
          v-for="option in column.params.options"
          :key="option.value"
          :value="option.value"
        >
          {{ option.label }}
        </Select.Option>
      </Select>
    </template>
    <template #assetId="{ row }">
      <Input v-model:value="row.assetId" />
    </template>
    <template #assetCode="{ row }">
      <Input v-model:value="row.assetCode" />
    </template>
    <template #assetName="{ row }">
      <Input v-model:value="row.assetName" />
    </template>
    <template #assetCategoryCode="{ row }">
      <Input v-model:value="row.assetCategoryCode" />
    </template>
    <template #assetCategoryName="{ row }">
      <Input v-model:value="row.assetCategoryName" />
    </template>
    <template #assetModel="{ row }">
      <Input v-model:value="row.assetModel" />
    </template>
    <template #assetUnit="{ row }">
      <Input v-model:value="row.assetUnit" />
    </template>
    <template #manufacturer="{ row }">
      <Input v-model:value="row.manufacturer" />
    </template>
    <template #brand="{ row }">
      <Input v-model:value="row.brand" />
    </template>
    <template #serialNumber="{ row }">
      <Input v-model:value="row.serialNumber" />
    </template>
    <template #assetStatusCode="{ row }">
      <Input v-model:value="row.assetStatusCode" />
    </template>
    <template #assetStatusName="{ row }">
      <Input v-model:value="row.assetStatusName" />
    </template>
    <template #assetSourceCode="{ row }">
      <Input v-model:value="row.assetSourceCode" />
    </template>
    <template #assetSourceName="{ row }">
      <Input v-model:value="row.assetSourceName" />
    </template>
    <template #purchaseDate="{ row }">
      <DatePicker
        v-model:value="row.purchaseDate"
        :show-time="true"
        format="YYYY-MM-DD HH:mm:ss"
        value-format="x"
      />
    </template>
    <template #purchasePrice="{ row }">
      <Input v-model:value="row.purchasePrice" />
    </template>
    <template #dateOfProduction="{ row }">
      <DatePicker
        v-model:value="row.dateOfProduction"
        :show-time="true"
        format="YYYY-MM-DD HH:mm:ss"
        value-format="x"
      />
    </template>
    <template #adminCompanyId="{ row }">
      <Input v-model:value="row.adminCompanyId" />
    </template>
    <template #adminCompanyName="{ row }">
      <Input v-model:value="row.adminCompanyName" />
    </template>
    <template #adminDeptId="{ row }">
      <Input v-model:value="row.adminDeptId" />
    </template>
    <template #adminDeptName="{ row }">
      <Input v-model:value="row.adminDeptName" />
    </template>
    <template #adminManagerId="{ row }">
      <Input v-model:value="row.adminManagerId" />
    </template>
    <template #adminManagerName="{ row }">
      <Input v-model:value="row.adminManagerName" />
    </template>
    <template #wmsStoreCode="{ row }">
      <Input v-model:value="row.wmsStoreCode" />
    </template>
    <template #wmsStoreName="{ row }">
      <Input v-model:value="row.wmsStoreName" />
    </template>
    <template #residualValueRate="{ row }">
      <Input v-model:value="row.residualValueRate" />
    </template>
    <template #useCompanyId="{ row }">
      <Input v-model:value="row.useCompanyId" />
    </template>
    <template #useCompanyName="{ row }">
      <Input v-model:value="row.useCompanyName" />
    </template>
    <template #useDeptId="{ row }">
      <Input v-model:value="row.useDeptId" />
    </template>
    <template #useDeptName="{ row }">
      <Input v-model:value="row.useDeptName" />
    </template>
    <template #useAccountId="{ row }">
      <Input v-model:value="row.useAccountId" />
    </template>
    <template #useAccountName="{ row }">
      <Input v-model:value="row.useAccountName" />
    </template>
    <template #transferUseDeptId="{ row }">
      <Input v-model:value="row.transferUseDeptId" />
    </template>
    <template #transferUseDeptName="{ row }">
      <Input v-model:value="row.transferUseDeptName" />
    </template>
    <template #transferUseAccountId="{ row }">
      <Input v-model:value="row.transferUseAccountId" />
    </template>
    <template #transferUseAccountName="{ row }">
      <Input v-model:value="row.transferUseAccountName" />
    </template>
    <template #assetIcon="{ row }">
      <Input v-model:value="row.assetIcon" />
    </template>
    <template #assetFile="{ row }">
      <FileUpload v-model:value="row.assetFile" />
    </template>
    <template #isJoinAsset="{ row }">
      <Input v-model:value="row.isJoinAsset" />
    </template>
    <template #sort="{ row }">
      <Input v-model:value="row.sort" />
    </template>
    <template #status="{ row, column }">
      <RadioGroup v-model:value="row.status" :options="column.params.options" />
    </template>
    <template #storeAddress="{ row }">
      <Input v-model:value="row.storeAddress" />
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
      v-access:code="['wms:goods-common-operation-order:create']"
    >
      {{
        $t('ui.actionTitle.create', [
          '采购入库、领用、退库、归还、借用、调拨明细',
        ])
      }}
    </Button>
  </div>
</template>
