<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { PurchaseInWarehousingApi } from '#/api/wms/purchaseinwarehousing';

import { ref } from 'vue';
// import { useRouter } from 'vue-router';

import { Page, useVbenModal } from '@vben/common-ui';
import { downloadFileFromBlobPart, isEmpty } from '@vben/utils';

import { message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deletePurchaseInWarehousing,
  deletePurchaseInWarehousingListByIds,
  exportPurchaseInWarehousing,
  getPurchaseInWarehousingPage,
} from '#/api/wms/purchaseinwarehousing';
import { $t } from '#/locales';

import Form from '../modules/form.vue';
import { useGridColumns, useGridFormSchema } from './data';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

// const router = useRouter();

/** 刷新表格 */
function onRefresh() {
  gridApi.query();
}

/** 创建采购入库 */
function handleCreate() {
  formModalApi.setData({}).open();

  // router.push({
  //   path: '/wms/purchase-in-warehousing-info',
  //   query: {
  //     t: Date.now(), // 添加时间戳作为随机串
  //   },
  // });
}

/** 编辑采购入库 */
function handleEdit(row: PurchaseInWarehousingApi.PurchaseInWarehousing) {
  formModalApi.setData(row).open();
}

/** 删除采购入库 */
async function handleDelete(
  row: PurchaseInWarehousingApi.PurchaseInWarehousing,
) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.id]),
    key: 'action_key_msg',
  });
  try {
    await deletePurchaseInWarehousing(row.id as number);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess', [row.id]),
      key: 'action_key_msg',
    });
    onRefresh();
  } finally {
    hideLoading();
  }
}

/** 批量删除采购入库 */
async function handleDeleteBatch() {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting'),
    key: 'action_key_msg',
  });
  try {
    await deletePurchaseInWarehousingListByIds(checkedIds.value);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess'),
      key: 'action_key_msg',
    });
    onRefresh();
  } finally {
    hideLoading();
  }
}

const checkedIds = ref<number[]>([]);
function handleRowCheckboxChange({
  records,
}: {
  records: PurchaseInWarehousingApi.PurchaseInWarehousing[];
}) {
  checkedIds.value = records.map((item) => item.id);
}

/** 导出表格 */
async function handleExport() {
  const data = await exportPurchaseInWarehousing(
    await gridApi.formApi.getValues(),
  );
  downloadFileFromBlobPart({ fileName: '采购入库.xls', source: data });
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
          return await getPurchaseInWarehousingPage({
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
  } as VxeTableGridOptions<PurchaseInWarehousingApi.PurchaseInWarehousing>,
  gridEvents: {
    checkboxAll: handleRowCheckboxChange,
    checkboxChange: handleRowCheckboxChange,
  },
});
</script>

<template>
  <Page auto-content-height>
    <FormModal @success="onRefresh" />

    <Grid table-title="采购入库列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: $t('ui.actionTitle.create', ['采购入库']),
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['wms:purchase-in-warehousing:create'],
              onClick: handleCreate,
            },
            {
              label: $t('ui.actionTitle.export'),
              type: 'primary',
              icon: ACTION_ICON.DOWNLOAD,
              auth: ['wms:purchase-in-warehousing:export'],
              onClick: handleExport,
            },
            {
              label: $t('ui.actionTitle.deleteBatch'),
              type: 'primary',
              danger: true,
              icon: ACTION_ICON.DELETE,
              disabled: isEmpty(checkedIds),
              auth: ['wms:purchase-in-warehousing:delete'],
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
              auth: ['wms:purchase-in-warehousing:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['wms:purchase-in-warehousing:delete'],
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
