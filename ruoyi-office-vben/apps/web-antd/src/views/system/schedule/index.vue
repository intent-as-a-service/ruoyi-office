<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { SystemScheduleApi } from '#/api/system/schedule';

import { computed, ref } from 'vue';

import { confirm, Page, useVbenModal } from '@vben/common-ui';
import { useUserStore } from '@vben/stores';
import { isEmpty } from '@vben/utils';

import { message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteSchedule,
  getMySchedulePage,
  pushSchedule,
} from '#/api/system/schedule';
import { $t } from '#/locales';

import Form from './components/schedule-form.vue';
import { useGridColumns, useGridFormSchema } from './data';

const userStore = useUserStore();
const currentUserId = computed(() => userStore.userInfo?.id);

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 创建日程 */
function handleCreate() {
  const currentId = currentUserId.value;
  // 新增时，默认接收人选中自己
  const defaultData = currentId ? { receiverIds: [currentId] } : null;
  formModalApi.setData(defaultData).open();
}

/** 编辑日程 */
function handleEdit(row: SystemScheduleApi.Schedule) {
  formModalApi.setData(row).open();
}

/** 删除日程 */
async function handleDelete(row: SystemScheduleApi.Schedule) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.title]),
    duration: 0,
  });
  try {
    await deleteSchedule(row.id!);
    message.success($t('ui.actionMessage.deleteSuccess', [row.title]));
    handleRefresh();
  } finally {
    hideLoading();
  }
}

/** 批量删除日程 */
async function handleDeleteBatch() {
  await confirm($t('ui.actionMessage.deleteBatchConfirm'));
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deletingBatch'),
    duration: 0,
  });
  try {
    // 批量删除
    for (const id of checkedIds.value) {
      await deleteSchedule(id);
    }
    checkedIds.value = [];
    message.success($t('ui.actionMessage.deleteSuccess'));
    handleRefresh();
  } finally {
    hideLoading();
  }
}

const checkedIds = ref<number[]>([]);
function handleRowCheckboxChange({
  records,
}: {
  records: SystemScheduleApi.Schedule[];
}) {
  checkedIds.value = records.map((item) => item.id!);
}

/** 推送日程 */
async function handlePush(row: SystemScheduleApi.Schedule) {
  // 这里可以打开一个选择用户的弹窗，暂时使用空数组
  // TODO: 实现用户选择弹窗
  const receiverIds: number[] = [];
  if (receiverIds.length === 0) {
    message.warning('请选择接收人');
    return;
  }
  const hideLoading = message.loading({
    content: '正在推送中...',
    duration: 0,
  });
  try {
    await pushSchedule({
      scheduleId: row.id!,
      receiverIds,
    });
    message.success($t('ui.actionMessage.operationSuccess'));
    handleRefresh();
  } finally {
    hideLoading();
  }
}

/** 判断是否可以编辑 */
function canEdit(row: SystemScheduleApi.Schedule): boolean {
  return row.creatorId === currentUserId.value;
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
          return await getMySchedulePage({
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
  } as VxeTableGridOptions<SystemScheduleApi.Schedule>,
  gridEvents: {
    checkboxAll: handleRowCheckboxChange,
    checkboxChange: handleRowCheckboxChange,
  },
});
</script>

<template>
  <Page auto-content-height>
    <FormModal @success="handleRefresh" />
    <Grid table-title="我的日程">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: $t('ui.actionTitle.create', ['日程']),
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['system:schedule:create'],
              onClick: handleCreate,
            },
            {
              label: $t('ui.actionTitle.deleteBatch'),
              type: 'primary',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['system:schedule:delete'],
              disabled: isEmpty(checkedIds),
              onClick: handleDeleteBatch,
            },
          ]"
        />
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('ui.actionTitle.edit'),
              icon: ACTION_ICON.EDIT,
              auth: ['system:schedule:update'],
              disabled: !canEdit(row),
              onClick: () => handleEdit(row),
            },
            {
              label: $t('ui.actionTitle.delete'),
              icon: ACTION_ICON.DELETE,
              danger: true,
              auth: ['system:schedule:delete'],
              disabled: !canEdit(row),
              onClick: () => handleDelete(row),
            },
            {
              label: '推送',
              icon: ACTION_ICON.PUSH,
              auth: ['system:schedule:push'],
              // 只要是创建人就可用，无论是否推送过
              disabled: !canEdit(row),
              onClick: () => handlePush(row),
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
