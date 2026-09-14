<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { WarehousingApi } from '#/api/wms/warehousing';

import { Page, useVbenModal } from '@vben/common-ui';
import { message,Tabs } from 'ant-design-vue';
import Form from './modules/form.vue';


import { ref, computed } from 'vue';
import { $t } from '#/locales';
import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { getWarehousingList, deleteWarehousing, exportWarehousing } from '#/api/wms/warehousing';
import { downloadFileFromBlobPart, isEmpty } from '@vben/utils';

import { useGridColumns, useGridFormSchema } from './data';


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

/** 创建仓库信息 */
function handleCreate() {
  formModalApi.setData({}).open();
}

/** 编辑仓库信息 */
function handleEdit(row: WarehousingApi.Warehousing) {
  formModalApi.setData(row).open();
}

/** 新增下级仓库信息 */
function handleAppend(row: WarehousingApi.Warehousing) {
  formModalApi.setData({ parentId: row.id }).open();
}

/** 删除仓库信息 */
async function handleDelete(row: WarehousingApi.Warehousing) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.id]),
    key: 'action_key_msg',
  });
  try {
    await deleteWarehousing(row.id as number);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess', [row.id]),
      key: 'action_key_msg',
    });
    onRefresh();
  } finally {
    hideLoading();
  }
}


/** 导出表格 */
async function handleExport() {
  const data = await exportWarehousing(await gridApi.formApi.getValues());
  downloadFileFromBlobPart({ fileName: '仓库信息.xls', source: data });
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
          return await getWarehousingList(formValues);
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
  } as VxeTableGridOptions<WarehousingApi.Warehousing>,
  gridEvents:{
  }
});
</script>

<template>
  <Page auto-content-height>
    <FormModal @success="onRefresh" />

    <Grid table-title="仓库信息列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: isExpanded ? '收缩' : '展开',
              type: 'primary',
              onClick: toggleExpand,
            },
            {
              label: $t('ui.actionTitle.create', ['仓库信息']),
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['wms:warehousing:create'],
              onClick: handleCreate,
            },
            {
              label: $t('ui.actionTitle.export'),
              type: 'primary',
              icon: ACTION_ICON.DOWNLOAD,
              auth: ['wms:warehousing:export'],
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
              auth: ['wms:warehousing:create'],
              onClick: handleAppend.bind(null, row),
            },
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['wms:warehousing:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['wms:warehousing:delete'],
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