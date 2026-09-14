<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { BpmTaskApi } from '#/api/bpm/task';

import { Page } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { getTaskDonePage, withdrawTask } from '#/api/bpm/task';
import { router } from '#/router';

import { useGridColumns, useGridFormSchema } from './data';

defineOptions({ name: 'BpmDoneTask' });

/** 查看历史 */
function handleHistory(row: BpmTaskApi.Task) {
  router.push({
    name: 'BpmProcessInstanceDetail',
    query: {
      id: row.processInstance.id,
      taskId: row.id,
    },
  });
}

/** 撤回任务 */
async function handleWithdraw(row: BpmTaskApi.Task) {
  const hideLoading = message.loading({
    content: '正在撤回中...',
    duration: 0,
  });
  try {
    await withdrawTask(row.id);
    message.success('撤回成功');
    await gridApi.query();
  } finally {
    hideLoading();
  }
}

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useGridFormSchema(),
    wrapperClass: 'grid-cols-4',
    collapsed: true,
  },
  gridOptions: {
    columns: useGridColumns(),
    height: 'auto',
    keepSource: true,
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) => {
          return await getTaskDonePage({
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
  } as VxeTableGridOptions<BpmTaskApi.Task>,
});
</script>

<template>
  <Page auto-content-height>
    <Grid table-title="已办任务">
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: '撤回',
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              popConfirm: {
                title: '确定要撤回该任务吗？',
                confirm: handleWithdraw.bind(null, row),
              },
            },
            {
              label: '历史',
              type: 'link',
              icon: ACTION_ICON.VIEW,
              onClick: handleHistory.bind(null, row),
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
