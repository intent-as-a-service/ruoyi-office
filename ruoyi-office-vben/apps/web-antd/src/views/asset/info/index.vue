<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { InfoApi } from '#/api/asset/info';

import { Page, useVbenModal } from '@vben/common-ui';
import { message } from 'ant-design-vue';
import Form from './modules/form.vue';


import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { deleteInfo, deleteInfoListByIds, exportInfo, getInfoPage } from '#/api/asset/info';
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

/** 创建资产信息 */
function handleCreate() {
  formModalApi.setData({}).open();
}

/** 编辑资产信息 */
function handleEdit(row: InfoApi.Info) {
  formModalApi.setData(row).open();
}


/** 删除资产信息 */
async function handleDelete(row: InfoApi.Info) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.id]),
    key: 'action_key_msg',
  });
  try {
    await deleteInfo(row.id as number);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess', [row.id]),
      key: 'action_key_msg',
    });
    onRefresh();
  } finally {
    hideLoading();
  }
}

/** 批量删除资产信息 */
async function handleDeleteBatch() {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting'),
    key: 'action_key_msg',
  });
  try {
    await deleteInfoListByIds(checkedIds.value);
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
  records: InfoApi.Info[];
}) {
  checkedIds.value = records.map((item) => item.id);
}

/** 导出表格 */
async function handleExport() {
  const data = await exportInfo(await gridApi.formApi.getValues());
  downloadFileFromBlobPart({ fileName: '资产信息.xls', source: data });
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
          return await getInfoPage({
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
  } as VxeTableGridOptions<InfoApi.Info>,
  gridEvents:{
      checkboxAll: handleRowCheckboxChange,
      checkboxChange: handleRowCheckboxChange,
  }
});
</script>

<template>
  <Page auto-content-height>
    <FormModal @success="onRefresh" />

    <Grid table-title="资产信息列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: $t('ui.actionTitle.create', ['资产信息']),
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['asset:info:create'],
              onClick: handleCreate,
            },
            {
              label: $t('ui.actionTitle.export'),
              type: 'primary',
              icon: ACTION_ICON.DOWNLOAD,
              auth: ['asset:info:export'],
              onClick: handleExport,
            },
            {
              label: $t('ui.actionTitle.deleteBatch'),
              type: 'primary',
              danger: true,
              icon: ACTION_ICON.DELETE,
              disabled: isEmpty(checkedIds),
              auth: ['asset:info:delete'],
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
              auth: ['asset:info:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['asset:info:delete'],
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
