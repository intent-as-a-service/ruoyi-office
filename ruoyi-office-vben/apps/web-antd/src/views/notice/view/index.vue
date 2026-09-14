<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { SystemNoticeApi } from '#/api/system/notice';

import { ref } from 'vue';

import { Page } from '@vben/common-ui';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { getNoticePage } from '#/api/system/notice';

import { useGridColumns, useGridFormSchema } from './data';
import NoticePreviewModal from './notice-preview-modal.vue';

const previewVisible = ref(false);
const selectedNotice = ref<null | SystemNoticeApi.Notice>(null);

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 查看公告详情 */
function handleView(row: SystemNoticeApi.Notice) {
  selectedNotice.value = row;
  previewVisible.value = true;
}

/** 关闭预览弹窗 */
function handleClosePreview() {
  previewVisible.value = false;
  selectedNotice.value = null;
  // 刷新列表以更新已读状态
  handleRefresh();
}

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useGridFormSchema(),
  },
  gridOptions: {
    columns: useGridColumns(),
    height: 'auto',
    keepSource: true,
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) => {
          return await getNoticePage({
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
  } as VxeTableGridOptions<SystemNoticeApi.Notice>,
});
</script>

<template>
  <Page auto-content-height>
    <NoticePreviewModal
      v-model:visible="previewVisible"
      :notice="selectedNotice"
      @close="handleClosePreview"
      @refresh="handleRefresh"
    />
    <Grid table-title="通知公告">
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: '查看详情',
              type: 'link',
              icon: ACTION_ICON.VIEW,
              onClick: handleView.bind(null, row),
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
