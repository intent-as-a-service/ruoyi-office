<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MeetingRoomApi } from '#/api/oa/meetingroom/roominfo';

import { nextTick, ref } from 'vue';

import { Page, useVbenModal } from '@vben/common-ui';
import { DICT_TYPE } from '@vben/constants';
import { getDictLabel } from '@vben/hooks';
import { downloadFileFromBlobPart, isEmpty } from '@vben/utils';

import { Image, message, Tag } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteMeetingRoom,
  deleteMeetingRoomListByIds,
  exportMeetingRoom,
  getMeetingRoomPage,
} from '#/api/oa/meetingroom/roominfo';
import { $t } from '#/locales';

import { BookingScheduleModal } from '../booking/components';
import { useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

/** 预约信息查看弹窗 */
const bookingScheduleModalRef =
  ref<InstanceType<typeof BookingScheduleModal>>();
/** 当前选中的会议室信息 */
const currentSelectedRoom = ref<MeetingRoomApi.MeetingRoom>({
  id: 0,
  roomName: '',
} as MeetingRoomApi.MeetingRoom);

/** 查看预定信息 */
function handleViewBooking(row: MeetingRoomApi.MeetingRoom) {
  if (!row.id) {
    return;
  }
  // 更新当前选中的会议室信息
  currentSelectedRoom.value = { ...row };
  // 使用 nextTick 确保组件已更新
  nextTick(() => {
    bookingScheduleModalRef.value?.modalApi.open();
  });
}

/** 刷新表格 */
function onRefresh() {
  gridApi.query();
}

/** 创建会议室信息 */
function handleCreate() {
  const meetingRoom: MeetingRoomApi.MeetingRoom = {
    availableStatus: 0, // 默认正常
    allowBooking: true, // 默认允许预定
    needApproval: false, // 默认不需要审批
    bookingScope: 0, // 默认全部成员
    sort: 0,
  };
  formModalApi.setData(meetingRoom).open();
}

/** 编辑会议室信息 */
function handleEdit(row: MeetingRoomApi.MeetingRoom) {
  formModalApi.setData(row).open();
}

/** 删除会议室信息 */
async function handleDelete(row: MeetingRoomApi.MeetingRoom) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.roomName]),
    key: 'action_key_msg',
  });
  try {
    await deleteMeetingRoom(row.id as number);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess', [row.roomName]),
      key: 'action_key_msg',
    });
    onRefresh();
  } finally {
    hideLoading();
  }
}

/** 批量删除会议室信息 */
async function handleDeleteBatch() {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting'),
    key: 'action_key_msg',
  });
  try {
    await deleteMeetingRoomListByIds(deleteIds.value);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess'),
      key: 'action_key_msg',
    });
    onRefresh();
  } finally {
    hideLoading();
  }
}

const deleteIds = ref<number[]>([]); // 待删除会议室信息 ID
function setDeleteIds({ records }: { records: MeetingRoomApi.MeetingRoom[] }) {
  deleteIds.value = records
    .map((item) => item.id)
    .filter((id): id is number => id !== undefined);
}

/** 导出表格 */
async function handleExport() {
  const formValues = await gridApi.formApi.getValues();
  const data = await exportMeetingRoom(formValues);
  downloadFileFromBlobPart({ fileName: '会议室信息.xls', source: data });
}

/** 预览图片 */
function handlePreviewImage(url: string) {
  Image.preview({
    src: url,
  });
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
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) => {
          const queryParams = {
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            ...formValues,
          };
          return await getMeetingRoomPage(queryParams);
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
  } as VxeTableGridOptions<MeetingRoomApi.MeetingRoom>,
  gridEvents: {
    checkboxAll: setDeleteIds,
    checkboxChange: setDeleteIds,
  },
});
</script>

<template>
  <Page auto-content-height>
    <FormModal @success="onRefresh" />
    <BookingScheduleModal
      ref="bookingScheduleModalRef"
      :room-id="(currentSelectedRoom.id as number) || 0"
      :room-name="currentSelectedRoom.roomName || ''"
      :show-today-approved="true"
    />

    <Grid table-title="会议室信息列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: $t('ui.actionTitle.create', ['会议室信息']),
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['oa:meeting-room:create'],
              onClick: handleCreate,
            },
            {
              label: $t('ui.actionTitle.export'),
              type: 'primary',
              icon: ACTION_ICON.DOWNLOAD,
              auth: ['oa:meeting-room:export'],
              onClick: handleExport,
            },
            {
              label: $t('ui.actionTitle.deleteBatch'),
              type: 'primary',
              danger: true,
              icon: ACTION_ICON.DELETE,
              disabled: isEmpty(deleteIds),
              auth: ['oa:meeting-room:delete'],
              onClick: handleDeleteBatch,
            },
          ]"
        />
      </template>

      <!-- 会议室图片插槽 -->
      <template #picUrl="{ row }">
        <div v-if="row.picUrl" class="flex items-center justify-center">
          <img
            :src="row.picUrl"
            alt="会议室图片"
            class="h-16 w-24 cursor-pointer rounded object-cover"
            @click="handlePreviewImage(row.picUrl)"
          />
        </div>
        <span v-else class="text-gray-400">-</span>
      </template>

      <!-- 设备列表插槽 -->
      <template #equipment="{ row }">
        <div v-if="row.equipment && row.equipment.length > 0">
          <Tag
            v-for="item in row.equipment"
            :key="item"
            color="blue"
            class="mb-1 mr-1"
          >
            {{
              getDictLabel(DICT_TYPE.OA_MEETING_ROOM_EQUIPMENT, item as string)
            }}
          </Tag>
        </div>
        <span v-else>-</span>
      </template>

      <!-- 允许预定插槽 -->
      <template #allowBooking="{ row }">
        <span>{{ row.allowBooking ? '是' : '否' }}</span>
      </template>

      <!-- 需审批插槽 -->
      <template #needApproval="{ row }">
        <span>{{ row.needApproval ? '是' : '否' }}</span>
      </template>

      <!-- 操作列插槽 -->
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: '查看预定信息',
              type: 'link',
              icon: ACTION_ICON.VIEW,
              auth: ['oa:meeting-room-booking:query'],
              onClick: handleViewBooking.bind(null, row),
            },
            {
              label: $t('common.edit'),
              type: 'link',
              icon: ACTION_ICON.EDIT,
              auth: ['oa:meeting-room:update'],
              onClick: handleEdit.bind(null, row),
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              auth: ['oa:meeting-room:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.roomName]),
                confirm: handleDelete.bind(null, row),
              },
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
