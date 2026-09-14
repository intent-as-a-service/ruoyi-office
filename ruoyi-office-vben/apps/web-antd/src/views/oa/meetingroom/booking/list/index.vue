<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MeetingRoomBookingApi } from '#/api/oa/meetingroom/booking';

import { onActivated, ref } from 'vue';
import { useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';
import {
  BpmProcessInstanceStatus,
  BpmProcessInstanceStatusEditValue,
} from '@vben/constants';
import { useUserStore } from '@vben/stores';
import { downloadFileFromBlobPart, isEmpty } from '@vben/utils';

import { message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteMeetingRoomBooking,
  deleteMeetingRoomBookingList,
  exportMeetingRoomBookingExcel,
  getMeetingRoomBookingPage,
  updateMeetingRoomBookingUseStatus,
} from '#/api/oa/meetingroom/booking';
import { $t } from '#/locales';

import { useGridColumns, useGridFormSchema } from './data';

defineOptions({ name: 'OaMeetingRoomBookingList' });
const userStore = useUserStore();
const router = useRouter();

/** 刷新表格 */
function onRefresh() {
  gridApi.query();
}

/** 新增会议室预定申请单 */
function handleCreate() {
  router.push({
    path: '/oa/meetingroom/booking-info',
    query: {
      t: Date.now(), // 添加时间戳作为随机串
    },
  });
}

/** 删除会议室预定申请单 */
async function handleDelete(row: MeetingRoomBookingApi.MeetingRoomBooking) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.billCode]),
    key: 'action_key_msg',
  });
  try {
    await deleteMeetingRoomBooking(row.id as number);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess', [row.billCode]),
      key: 'action_key_msg',
    });
    onRefresh();
  } finally {
    hideLoading();
  }
}

/** 批量删除会议室预定申请单 */
async function handleDeleteBatch() {
  // 检查选中的记录是否都可以删除
  const checkedRecords = gridApi.grid.getCheckboxRecords();
  const notAllowedRecords = checkedRecords.filter(
    (record: MeetingRoomBookingApi.MeetingRoomBooking) =>
      !BpmProcessInstanceStatusEditValue.includes(
        record.processStatus as number,
      ),
  );

  if (notAllowedRecords.length > 0) {
    const billCodes = notAllowedRecords
      .map(
        (record: MeetingRoomBookingApi.MeetingRoomBooking) =>
          record.billCode || record.id,
      )
      .join(', ');
    message.warning(`以下单据不允许删除：${billCodes}`);
    return;
  }

  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting'),
    key: 'action_key_msg',
  });
  try {
    await deleteMeetingRoomBookingList(checkedIds.value);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess'),
      key: 'action_key_msg',
    });
    onRefresh();
    checkedIds.value = [];
  } finally {
    hideLoading();
  }
}

const checkedIds = ref<number[]>([]);
function handleRowCheckboxChange({
  records,
}: {
  records: MeetingRoomBookingApi.MeetingRoomBooking[];
}) {
  checkedIds.value = records
    .map((item) => item.id!)
    .filter((id): id is number => id !== undefined);
}

/** 导出表格 */
async function handleExport() {
  const data = await exportMeetingRoomBookingExcel(
    await gridApi.formApi.getValues(),
  );
  downloadFileFromBlobPart({
    fileName: '会议室预定申请单.xls',
    source: data,
  });
}

/** 处理单元格编辑完成事件 */
async function handleEditClosed({
  row,
  column,
}: {
  row: MeetingRoomBookingApi.MeetingRoomBooking;
  column: any;
}) {
  // 只处理使用状态字段的编辑
  if (column.field === 'useStatus') {
    // beforeEditMethod已经检查了权限，这里直接保存
    const hideLoading = message.loading('正在保存...', 0);
    try {
      await updateMeetingRoomBookingUseStatus(
        row.id as number,
        row.useStatus as number,
      );
      message.success('使用状态更新成功');
      // 刷新表格数据
      onRefresh();
    } catch (error) {
      message.error('使用状态更新失败');
      // 恢复原值
      onRefresh();
    } finally {
      hideLoading();
    }
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
    pagerConfig: {
      enabled: true,
    },
    editConfig: {
      trigger: 'click',
      mode: 'cell',
      enabled: true,
      showStatus: true,
      beforeEditMethod: ({ row, column }: any) => {
        // 只有审批完成的单据（processStatus = 2）才能编辑使用状态
        if (column.field === 'useStatus') {
          if (row.processStatus !== BpmProcessInstanceStatus.APPROVE) {
            message.warning('只有审批完成的单据才能更新使用状态');
            return false;
          }
        }
        return true;
      },
    },
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) => {
          return await getMeetingRoomBookingPage({
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            ...formValues,
            companyId: userStore.userInfo?.companyId,
            creator: userStore.userInfo?.id,
          });
        },
      },
    },
    rowConfig: {
      keyField: 'id',
      isHover: true,
    },
    toolbarConfig: {
      refresh: { code: 'query' },
      search: true,
    },
  } as VxeTableGridOptions<MeetingRoomBookingApi.MeetingRoomBooking>,
  gridEvents: {
    checkboxAll: handleRowCheckboxChange,
    checkboxChange: handleRowCheckboxChange,
    editClosed: handleEditClosed,
  },
});

// 页签切换时自动刷新表格数据
onActivated(() => {
  onRefresh();
});
</script>

<template>
  <Page auto-content-height>
    <Grid table-title="会议室预定申请单列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: $t('ui.actionTitle.create'),
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['oa:meeting-room-booking:create'],
              onClick: handleCreate,
            },
            {
              label: $t('ui.actionTitle.export'),
              type: 'primary',
              icon: ACTION_ICON.DOWNLOAD,
              auth: ['oa:meeting-room-booking:export'],
              onClick: handleExport,
            },
            {
              label: $t('ui.actionTitle.deleteBatch'),
              type: 'primary',
              danger: true,
              icon: ACTION_ICON.DELETE,
              disabled: isEmpty(checkedIds),
              auth: ['oa:meeting-room-booking:delete'],
              onClick: handleDeleteBatch,
            },
          ]"
        />
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              ifShow: () =>
                BpmProcessInstanceStatusEditValue.includes(
                  row.processStatus as number,
                ),
              auth: ['oa:meeting-room-booking:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.billCode]),
                confirm: handleDelete.bind(null, row),
              },
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              ifShow: () =>
                !BpmProcessInstanceStatusEditValue.includes(
                  row.processStatus as number,
                ),
              disabled: true,
              auth: ['oa:meeting-room-booking:delete'],
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
