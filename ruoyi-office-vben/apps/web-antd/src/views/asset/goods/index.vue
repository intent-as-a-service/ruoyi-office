<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { GoodsApi } from '#/api/asset/goods';

import { Page, useVbenModal } from '@vben/common-ui';
import { message } from 'ant-design-vue';
import Form from './modules/form.vue';


import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { deleteGoods, deleteGoodsListByIds, exportGoods, getGoodsPage } from '#/api/asset/goods';
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

/** 创建物品信息 */
function handleCreate() {
  formModalApi.setData({}).open();
}

/** 编辑物品信息 */
function handleEdit(row: GoodsApi.Goods) {
  formModalApi.setData(row).open();
}


/** 删除物品信息 */
async function handleDelete(row: GoodsApi.Goods) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.goodsName]),
    key: 'action_key_msg',
  });
  try {
    await deleteGoods(row.id as number);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess', [row.goodsName]),
      key: 'action_key_msg',
    });
    onRefresh();
  } finally {
    hideLoading();
  }
}

/** 批量删除物品信息 */
async function handleDeleteBatch() {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting'),
    key: 'action_key_msg',
  });
  try {
    await deleteGoodsListByIds(checkedIds.value);
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
  records: GoodsApi.Goods[];
}) {
  checkedIds.value = records.map((item) => item.id);
}

/** 导出表格 */
async function handleExport() {
  const data = await exportGoods(await gridApi.formApi.getValues());
  downloadFileFromBlobPart({ fileName: '物品信息.xls', source: data });
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
          return await getGoodsPage({
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
  } as VxeTableGridOptions<GoodsApi.Goods>,
  gridEvents:{
      checkboxAll: handleRowCheckboxChange,
      checkboxChange: handleRowCheckboxChange,
  }
});
</script>

<template>
  <Page auto-content-height>
    <FormModal @success="onRefresh" />

    <Grid table-title="物品信息列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: $t('ui.actionTitle.create', ['物品信息']),
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['asset:goods:create'],
              onClick: handleCreate,
            },
            {
              label: $t('ui.actionTitle.export'),
              type: 'primary',
              icon: ACTION_ICON.DOWNLOAD,
              auth: ['asset:goods:export'],
              onClick: handleExport,
            },
            {
              label: $t('ui.actionTitle.deleteBatch'),
              type: 'primary',
              danger: true,
              icon: ACTION_ICON.DELETE,
              disabled: isEmpty(checkedIds),
              auth: ['asset:goods:delete'],
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
              auth: ['asset:goods:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['asset:goods:delete'],
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
