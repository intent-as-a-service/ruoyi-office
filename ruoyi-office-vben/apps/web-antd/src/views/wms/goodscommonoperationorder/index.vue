<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { GoodsCommonOperationOrderApi } from '#/api/wms/goodscommonoperationorder';

import { Page, useVbenModal } from '@vben/common-ui';
import { message } from 'ant-design-vue';
import Form from './modules/form.vue';


import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { deleteGoodsCommonOperationOrder, deleteGoodsCommonOperationOrderListByIds, exportGoodsCommonOperationOrder, getGoodsCommonOperationOrderPage } from '#/api/wms/goodscommonoperationorder';
import { $t } from '#/locales';
import { downloadFileFromBlobPart, isEmpty } from '@vben/utils';
import { ref } from 'vue';

import { useGridColumns, useGridFormSchema } from './data';


const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});


/** 刷新表格 */
function onRefresh() {
  gridApi.query();
}

/** 创建领用、退库、归还、借用、调拨主 */
function handleCreate() {
  formModalApi.setData({}).open();
}

/** 编辑领用、退库、归还、借用、调拨主 */
function handleEdit(row: GoodsCommonOperationOrderApi.GoodsCommonOperationOrder) {
  formModalApi.setData(row).open();
}


/** 删除领用、退库、归还、借用、调拨主 */
async function handleDelete(row: GoodsCommonOperationOrderApi.GoodsCommonOperationOrder) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.id]),
    key: 'action_key_msg',
  });
  try {
    await deleteGoodsCommonOperationOrder(row.id as number);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess', [row.id]),
      key: 'action_key_msg',
    });
    onRefresh();
  } finally {
    hideLoading();
  }
}

/** 批量删除领用、退库、归还、借用、调拨主 */
async function handleDeleteBatch() {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting'),
    key: 'action_key_msg',
  });
  try {
    await deleteGoodsCommonOperationOrderListByIds(checkedIds.value);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess'),
      key: 'action_key_msg',
    });
    onRefresh();
  } finally {
    hideLoading();
  }
}

const checkedIds = ref<number[]>([])
function handleRowCheckboxChange({
  records,
}: {
  records: GoodsCommonOperationOrderApi.GoodsCommonOperationOrder[];
}) {
  checkedIds.value = records.map((item) => item.id);
}

/** 导出表格 */
async function handleExport() {
  const data = await exportGoodsCommonOperationOrder(await gridApi.formApi.getValues());
  downloadFileFromBlobPart({ fileName: '领用、退库、归还、借用、调拨主.xls', source: data });
}

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useGridFormSchema(),
  },
  gridOptions: {
    columns: useGridColumns(),
    height: 'auto',
    pagerConfig: {
      enabled: true,
    },
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) => {
          return await getGoodsCommonOperationOrderPage({
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            ...formValues,
          });
        },
      },
    },
    rowConfig: {
      keyField: 'id',
      isHover: true,
    },
    toolbarConfig: {
      refresh: true,
      search: true,
    },
  } as VxeTableGridOptions<GoodsCommonOperationOrderApi.GoodsCommonOperationOrder>,
  gridEvents:{
      checkboxAll: handleRowCheckboxChange,
      checkboxChange: handleRowCheckboxChange,
  }
});
</script>

<template>
  <Page auto-content-height>
    <FormModal @success="onRefresh" />

    <Grid table-title="领用、退库、归还、借用、调拨主列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: $t('ui.actionTitle.create', ['领用、退库、归还、借用、调拨主']),
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['wms:goods-common-operation-order:create'],
              onClick: handleCreate,
            },
            {
              label: $t('ui.actionTitle.export'),
              type: 'primary',
              icon: ACTION_ICON.DOWNLOAD,
              auth: ['wms:goods-common-operation-order:export'],
              onClick: handleExport,
            },
            {
              label: $t('ui.actionTitle.deleteBatch'),
              type: 'primary',
              danger: true,
              icon: ACTION_ICON.DELETE,
              disabled: isEmpty(checkedIds),
              auth: ['wms:goods-common-operation-order:delete'],
              onClick: handleDeleteBatch,
            },
          ]"
        />
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['wms:goods-common-operation-order:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['wms:goods-common-operation-order:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.id]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>

  </Page>
</template>
