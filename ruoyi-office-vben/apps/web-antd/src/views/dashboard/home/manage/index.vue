<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { SystemHomePageApi } from '#/api/system/home';

import { ref } from 'vue';
import { useRouter } from 'vue-router';

import { Page, useVbenModal } from '@vben/common-ui';
import { useUserStore } from '@vben/stores';

import { message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteHomePage,
  getHomePagePage,
  setMyHomePage,
} from '#/api/system/home';
import { $t } from '#/locales';

import { useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';

const userStore = useUserStore();
// 获取当前用户ID的辅助函数，在 ifShow 中直接调用以确保响应式追踪
function getCurrentUserId(): string | undefined {
  return userStore.userInfo?.id?.toString();
}

const router = useRouter();

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 创建首页 */
function handleCreate() {
  formModalApi.setData(null).open();
}

/** 编辑首页 */
function handleEdit(row: SystemHomePageApi.HomePage) {
  formModalApi.setData(row).open();
}

/** 删除首页 */
async function handleDelete(row: SystemHomePageApi.HomePage) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.name]),
    key: 'action_key_msg',
  });
  try {
    await deleteHomePage(row.id!);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess', [row.name]),
      key: 'action_key_msg',
    });
    handleRefresh();
  } finally {
    hideLoading();
  }
}

/** 设置为我的首页 */
async function handleSetMyHome(row: SystemHomePageApi.HomePage) {
  const hideLoading = message.loading({
    content: '设置中...',
    duration: 0,
  });
  try {
    await setMyHomePage(row.id!);
    message.success('设置成功');
    handleRefresh();
  } finally {
    hideLoading();
  }
}

/** 进入设计器 */
function handleDesign(row: SystemHomePageApi.HomePage) {
  router.push({
    path: '/workspace/home/designer',
    query: { pageId: row.id },
  });
}

const checkedIds = ref<number[]>([]);
function handleRowCheckboxChange({
  records,
}: {
  records: SystemHomePageApi.HomePage[];
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
          return await getHomePagePage({
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
    <Grid table-title="首页管理">
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

      <template #useStatus="{ row }">
        <Tag v-if="row.useStatus === '使用中'" color="success">使用中</Tag>
      </template>

      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: '编辑',
              type: 'link',
              icon: ACTION_ICON.EDIT,
              ifShow: () => {
                // 只有当前用户创建的首页可以编辑
                return row.creator === getCurrentUserId();
              },
              onClick: () => handleEdit(row),
            },
            {
              label: '设计',
              type: 'link',
              icon: ACTION_ICON.AUDIT,
              ifShow: () => {
                // 只有当前用户创建的首页可以设计
                return row.creator === getCurrentUserId();
              },
              onClick: () => handleDesign(row),
            },
            {
              label: '设置为首页',
              type: 'link',

              // 只有未使用中的首页，才显示“设置为首页”
              ifShow: () => row.useStatus !== '使用中',
              onClick: () => handleSetMyHome(row),
            },
            {
              label: '删除',
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              ifShow: () => {
                // 不允许删除系统默认首页
                if (row.code === 'default_workspace') {
                  return false;
                }
                // 只有创建者可以删除
                return row.creator === getCurrentUserId();
              },
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
  </Page>
</template>
