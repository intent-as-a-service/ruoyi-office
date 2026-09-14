<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MeetingRoomApi } from '#/api/oa/meetingroom/roominfo';

import { nextTick, reactive, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { getBookableMeetingRoomPage } from '#/api/oa/meetingroom/roominfo';

import BookingScheduleModal from './booking-schedule-modal.vue';
import {
  useMeetingRoomSelectColumns,
  useMeetingRoomSelectFormSchema,
} from './meeting-room-select-data';

/** 定义组件事件 */
const emit = defineEmits<{
  (e: 'select', room: MeetingRoomApi.MeetingRoom): void;
}>();

const formData = reactive({
  selectedRoom: null as MeetingRoomApi.MeetingRoom | null,
});

/** 预约信息查看弹窗 */
const bookingScheduleModalRef =
  ref<InstanceType<typeof BookingScheduleModal>>();
/** 当前选中的会议室信息（用于查看预约信息） */
const currentViewRoom = ref<MeetingRoomApi.MeetingRoom>({
  id: 0,
  roomName: '',
} as MeetingRoomApi.MeetingRoom);

/** 查看预定信息 */
function handleViewBooking(row: MeetingRoomApi.MeetingRoom) {
  if (!row.id) {
    return;
  }
  // 更新当前选中的会议室信息
  currentViewRoom.value = { ...row };
  // 使用 nextTick 确保组件已更新
  nextTick(() => {
    bookingScheduleModalRef.value?.modalApi.open();
  });
}

/** 表格实例 */
const [Grid] = useVbenVxeGrid({
  separator: false,
  formOptions: {
    schema: useMeetingRoomSelectFormSchema(),
    submitOnChange: true,
    collapsed: true,
  },
  gridOptions: {
    columns: useMeetingRoomSelectColumns(),
    height: 440,
    keepSource: true,
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) => {
          // 使用可预定会议室接口，该接口会自动过滤：可用状态为正常、允许预定、可用范围包含当前用户
          const queryParams = {
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            ...formValues,
          };
          return await getBookableMeetingRoomPage(queryParams);
        },
      },
    },
    rowConfig: {
      keyField: 'id',
    },
    toolbarConfig: {
      enabled: false,
    },
    radioConfig: {
      highlight: true,
    },
    pagerConfig: {
      enabled: true,
    },
  } as VxeTableGridOptions<MeetingRoomApi.MeetingRoom>,
  gridEvents: {
    radioChange: ({ row }: { row: MeetingRoomApi.MeetingRoom }) => {
      formData.selectedRoom = row;
    },
    cellDblclick: ({ row }: { row: MeetingRoomApi.MeetingRoom }) => {
      // 双击直接选择
      formData.selectedRoom = row;
      handleConfirm();
    },
  },
});

/** 模态框实例 */
const [Modal, modalApi] = useVbenModal({
  title: '选择会议室',
  class: 'w-3/5 max-w-4xl',
  async onConfirm() {
    return handleConfirm();
  },
});

/** 确认选择 */
async function handleConfirm() {
  if (!formData.selectedRoom) {
    message.error('请选择会议室');
    return false;
  }

  emit('select', formData.selectedRoom);
  formData.selectedRoom = null;
  await modalApi.close();
  return true;
}

/** 暴露modal API供外部调用 */
defineExpose({
  modalApi,
});
</script>

<template>
  <Modal>
    <Grid>
      <!-- 操作列插槽 -->
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: '查看预定信息',
              type: 'link',
              icon: ACTION_ICON.VIEW,
              onClick: handleViewBooking.bind(null, row),
            },
          ]"
        />
      </template>
    </Grid>
  </Modal>
  <BookingScheduleModal
    ref="bookingScheduleModalRef"
    :room-id="(currentViewRoom.id as number) || 0"
    :room-name="currentViewRoom.roomName || ''"
    :show-today-approved="false"
  />
</template>
