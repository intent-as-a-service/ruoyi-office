<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { SystemHomeComponentApi } from '#/api/system/home/component';

import { ref } from 'vue';

import { Page, useVbenModal } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { deleteComponent, getComponentPage } from '#/api/system/home/component';
import { $t } from '#/locales';

import { useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';
import PreviewModal from './modules/preview-modal.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

const [PreviewModalComp, previewModalApi] = useVbenModal({
  connectedComponent: PreviewModal,
  destroyOnClose: true,
});

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 创建组件 */
function handleCreate() {
  formModalApi.setData(null).open();
}

/** 编辑组件 */
function handleEdit(row: SystemHomeComponentApi.Component) {
  formModalApi.setData(row).open();
}

/** 删除组件 */
async function handleDelete(row: SystemHomeComponentApi.Component) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.name]),
    key: 'action_key_msg',
  });
  try {
    await deleteComponent(row.id!);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess', [row.name]),
      key: 'action_key_msg',
    });
    handleRefresh();
  } finally {
    hideLoading();
  }
}

/** 预览组件 */
function handlePreview(row: SystemHomeComponentApi.Component) {
  previewModalApi.setData(row).open();
}

const checkedIds = ref<number[]>([]);
function handleRowCheckboxChange({
  records,
}: {
  records: SystemHomeComponentApi.Component[];
}) {
  checkedIds.value = records.map((item) => item.id!);
}

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useGridFormSchema(),
  },
  gridOptions: {
    columns: useGridColumns(),
    height: 'auto',
    keepSource: true,
    checkboxConfig: {
      reserve: true,
    },
    pagerConfig: {
      enabled: true,
    },
    rowConfig: {
      keyField: 'id',
      isHover: true,
    },
    toolbarConfig: {
      refresh: { code: 'query' },
      search: true,
    },
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) => {
          return await getComponentPage({
            ...formValues,
            pageNo: page.currentPage,
            pageSize: page.pageSize,
          });
        },
      },
    },
  } as VxeTableGridOptions,
  gridEvents: {
    checkboxAll: handleRowCheckboxChange,
    checkboxChange: handleRowCheckboxChange,
  },
});
</script>

<template>
  <Page auto-content-height>
    <Grid table-title="组件管理">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: '新增',
              type: 'primary',
              icon: ACTION_ICON.ADD,
              onClick: handleCreate,
            },
          ]"
        />
      </template>

      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: '编辑',
              type: 'link',
              icon: ACTION_ICON.EDIT,
              onClick: () => handleEdit(row),
            },
            {
              label: '预览',
              type: 'link',
              icon: ACTION_ICON.VIEW,
              onClick: () => handlePreview(row),
            },
            {
              label: '删除',
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.name]),
                confirm: () => handleDelete(row),
              },
            },
          ]"
        />
      </template>
    </Grid>

    <FormModal @success="handleRefresh" />
    <PreviewModalComp />
  </Page>
</template>
