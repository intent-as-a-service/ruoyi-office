<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { CategoryApi } from '#/api/asset/category';

import { ref } from 'vue';
import { useRouter } from 'vue-router';

import { Page, useVbenModal } from '@vben/common-ui';
import { downloadFileFromBlobPart } from '@vben/utils';

import { message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteCategory,
  exportCategory,
  getCategoryList,
} from '#/api/asset/category';
import { $t } from '#/locales';

import { useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';

const router = useRouter();

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

/** 切换树形展开/收缩状态 */
const isExpanded = ref(true);
function toggleExpand() {
  isExpanded.value = !isExpanded.value;
  gridApi.grid.setAllTreeExpand(isExpanded.value);
}

/** 刷新表格 */
function onRefresh() {
  gridApi.query();
}

/** 创建资产类别 */
function handleCreate() {
  // formModalApi.setData({}).open();
  router.push({
    path: '/asset/category-info',
    query: {
      editType: 'add',
    },
  });
}

/** 编辑资产类别 */
function handleEdit(row: CategoryApi.Category) {
  // formModalApi.setData(row).open();
  router.push({
    path: '/asset/category-info',
    query: {
      editType: 'edit',
      id: row.id,
    },
  });
}

/** 新增下级资产类别 */
function handleAppend(row: CategoryApi.Category) {
  formModalApi.setData({ parentId: row.id }).open();
}

/** 删除资产类别 */
async function handleDelete(row: CategoryApi.Category) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.categoryName]),
    key: 'action_key_msg',
  });
  try {
    await deleteCategory(row.id as number);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess', [row.categoryName]),
      key: 'action_key_msg',
    });
    onRefresh();
  } finally {
    hideLoading();
  }
}

/** 导出表格 */
async function handleExport() {
  const data = await exportCategory(await gridApi.formApi.getValues());
  downloadFileFromBlobPart({ fileName: '资产类别.xls', source: data });
}

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useGridFormSchema(),
  },
  gridOptions: {
    columns: useGridColumns(),
    height: 'auto',
    treeConfig: {
      parentField: 'parentId',
      rowField: 'id',
      transform: true,
      expandAll: true,
      reserve: true,
    },
    pagerConfig: {
      enabled: false,
    },
    proxyConfig: {
      ajax: {
        query: async (_, formValues) => {
          return await getCategoryList(formValues);
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
  } as VxeTableGridOptions<CategoryApi.Category>,
  gridEvents: {},
});
</script>

<template>
  <Page auto-content-height>
    <FormModal @success="onRefresh" />

    <Grid table-title="资产类别列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: isExpanded ? '收缩' : '展开',
              type: 'primary',
              onClick: toggleExpand,
            },
            {
              label: $t('ui.actionTitle.create', ['资产类别']),
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['asset:category:create'],
              onClick: handleCreate,
            },
            {
              label: $t('ui.actionTitle.export'),
              type: 'primary',
              icon: ACTION_ICON.DOWNLOAD,
              auth: ['asset:category:export'],
              onClick: handleExport,
            },
          ]"
        />
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: '新增下级',
              type: 'link',
              icon: ACTION_ICON.ADD,
              auth: ['asset:category:create'],
              onClick: handleAppend.bind(null, row),
            },
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['asset:category:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['asset:category:delete'],
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
