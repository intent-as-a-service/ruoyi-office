<script lang="ts" setup>
import type { Dayjs } from 'dayjs';

import type { MeetingRoomBookingScheduleApi } from '#/api/oa/meetingroom/booking';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { DICT_TYPE } from '@vben/constants';
import { getDictLabel } from '@vben/hooks';

import { message, Tag } from 'ant-design-vue';
import dayjs from 'dayjs';

import { getMeetingRoomBookingSchedule } from '#/api/oa/meetingroom/booking';

interface Props {
  roomId: number;
  roomName: string;
  showTodayApproved?: boolean; // 是否显示当天审批通过的记录列表
}

const props = withDefaults(defineProps<Props>(), {
  showTodayApproved: false,
});

/** 当前选中的日期 */
const selectedDate = ref<Dayjs>(dayjs());

/** 日期列表（今天及之后4天，共5天） */
const dateList = computed(() => {
  const dates: Dayjs[] = [];
  const today = dayjs();
  for (let i = 0; i < 5; i++) {
    dates.push(today.add(i, 'day'));
  }
  return dates;
});

/** 预约数据 */
const scheduleData = ref<MeetingRoomBookingScheduleApi.ScheduleResp>({
  bookings: [],
  todayApprovedBookings: [],
});

/** 加载中 */
const loading = ref(false);

/** 加载预约数据 */
async function loadScheduleData() {
  if (!props.roomId) {
    return;
  }

  loading.value = true;
  try {
    const dates = dateList.value;
    if (!dates || dates.length === 0) {
      return;
    }
    const firstDate = dates[0];
    const lastDate = dates[dates.length - 1];
    if (!firstDate || !lastDate) {
      return;
    }
    const startDate = firstDate.format('YYYY-MM-DD');
    const endDate = lastDate.format('YYYY-MM-DD');

    const data = await getMeetingRoomBookingSchedule({
      roomId: props.roomId,
      startDate,
      endDate,
    });
    scheduleData.value = data;
  } catch (error) {
    message.error('加载预约信息失败');
    console.error(error);
  } finally {
    loading.value = false;
  }
}

/** 切换日期 */
function handleDateChange(date: Dayjs) {
  selectedDate.value = date;
}

/** 获取当前日期对应的预约记录 */
const currentDateBookings = computed(() => {
  const dateStr = selectedDate.value.format('YYYY-MM-DD');
  return (
    scheduleData.value.bookings?.filter((booking) => {
      if (!booking.meetingStartTime || !booking.meetingEndTime) {
        return false;
      }
      const startDate = dayjs(booking.meetingStartTime).format('YYYY-MM-DD');
      const endDate = dayjs(booking.meetingEndTime).format('YYYY-MM-DD');
      // 预约记录的开始日期或结束日期与选中日期相同，或者选中日期在预约时间段内
      return (
        startDate === dateStr ||
        endDate === dateStr ||
        (dayjs(dateStr).isAfter(startDate) && dayjs(dateStr).isBefore(endDate))
      );
    }) || []
  );
});

/** 生成24小时的时间块（每小时2个方块，每30分钟一个，共48个） */
const timeSlots = computed(() => {
  const slots: Array<{
    hour: number;
    minute: number;
    time: string;
    timestamp: number;
  }> = [];
  for (let hour = 0; hour < 24; hour++) {
    for (let minute = 0; minute < 60; minute += 30) {
      const time = dayjs().hour(hour).minute(minute).second(0).millisecond(0);
      slots.push({
        hour,
        minute,
        time: time.format('HH:mm'),
        timestamp: time.valueOf(),
      });
    }
  }
  return slots;
});

/** 将时间块按行分组（每行12小时，共2行） */
const timeSlotRows = computed(() => {
  const slots = timeSlots.value;
  return [
    slots.slice(0, 24), // 00:00-11:30 (24个时间块，12小时 * 2)
    slots.slice(24, 48), // 12:00-23:30 (24个时间块，12小时 * 2)
  ];
});

/** 时间轴标签分组（每行12小时） */
const timeAxisRows = computed(() => {
  return [
    Array.from({ length: 12 }, (_, i) => i), // 00:00-11:00
    Array.from({ length: 12 }, (_, i) => i + 12), // 12:00-23:00
  ];
});

/** 获取时间块的状态 */
function getTimeSlotStatus(slot: {
  hour: number;
  minute: number;
  timestamp: number;
}): 'available' | 'booked' | 'expired' {
  const now = dayjs();
  const slotStart = selectedDate.value
    .hour(slot.hour)
    .minute(slot.minute)
    .second(0)
    .millisecond(0);
  // 时间块的结束时间（30分钟后）
  const slotEnd = slotStart.add(30, 'minute');

  // 已过期（当前时间之后的时间块）
  if (slotEnd.isBefore(now) || slotEnd.isSame(now)) {
    return 'expired';
  }

  // 检查是否在预约时间段内
  const isBooked = currentDateBookings.value.some((booking) => {
    if (!booking.meetingStartTime || !booking.meetingEndTime) {
      return false;
    }
    const start = dayjs(booking.meetingStartTime);
    const end = dayjs(booking.meetingEndTime);
    // 只显示审批通过且未取消的预约
    if (booking.processStatus !== 2 || booking.useStatus === 3) {
      return false;
    }
    // 检查时间块是否与预约时间段有重叠
    // 时间块与预约时间段重叠的条件：slotStart < end && slotEnd > start
    return slotStart.isBefore(end) && slotEnd.isAfter(start);
  });

  return isBooked ? 'booked' : 'available';
}

/** 获取时间块的样式类 */
function getTimeSlotClass(status: 'available' | 'booked' | 'expired'): string {
  switch (status) {
    case 'booked': {
      return 'bg-green-500 hover:bg-green-600 cursor-pointer';
    }
    case 'expired': {
      return 'bg-gray-300 cursor-not-allowed opacity-50';
    }
    default: {
      return 'bg-white hover:bg-gray-100 border border-gray-200 cursor-pointer';
    }
  }
}

/** 获取时间块的提示信息 */
function getTimeSlotTooltip(slot: {
  hour: number;
  minute: number;
  time: string;
  timestamp: number;
}): string {
  const status = getTimeSlotStatus(slot);
  const slotStart = selectedDate.value
    .hour(slot.hour)
    .minute(slot.minute)
    .second(0)
    .millisecond(0);
  const slotEnd = slotStart.add(30, 'minute');
  const timeRange = `${slotStart.format('HH:mm')}-${slotEnd.format('HH:mm')}`;

  if (status === 'booked') {
    const booking = currentDateBookings.value.find((b) => {
      if (!b.meetingStartTime || !b.meetingEndTime) {
        return false;
      }
      const start = dayjs(b.meetingStartTime);
      const end = dayjs(b.meetingEndTime);
      // 检查时间块是否与预约时间段有重叠
      return (
        slotStart.isBefore(end) &&
        slotEnd.isAfter(start) &&
        b.processStatus === 2 &&
        b.useStatus !== 3
      );
    });
    if (booking) {
      return `${timeRange} - ${booking.meetingTitle} (${dayjs(booking.meetingStartTime).format('HH:mm')}-${dayjs(booking.meetingEndTime).format('HH:mm')})`;
    }
  }
  return status === 'expired'
    ? `${timeRange} - 已过期`
    : `${timeRange} - 可预约`;
}

/** 选中日期当天的审批通过的预约记录 */
const selectedDateApprovedBookings = computed(() => {
  const dateStr = selectedDate.value.format('YYYY-MM-DD');
  return (
    scheduleData.value.bookings?.filter((booking) => {
      if (!booking.meetingStartTime || !booking.meetingEndTime) {
        return false;
      }
      const startDate = dayjs(booking.meetingStartTime).format('YYYY-MM-DD');
      // 只显示选中日期当天的预约记录
      if (startDate !== dateStr) {
        return false;
      }
      // 只显示审批通过且未取消的预约
      return booking.processStatus === 2 && booking.useStatus !== 3;
    }) || []
  ).sort((a, b) => {
    // 按预约开始时间倒序排列
    if (!a.meetingStartTime || !b.meetingStartTime) {
      return 0;
    }
    return (
      dayjs(b.meetingStartTime).valueOf() - dayjs(a.meetingStartTime).valueOf()
    );
  });
});

/** 模态框标题 */
const modalTitle = computed(() => `${props.roomName} - 预约信息`);

/** 模态框实例 */
const [Modal, modalApi] = useVbenModal({
  class: 'w-4/5 max-w-6xl',
  onOpened: () => {
    // 重置选中日期为今天
    selectedDate.value = dayjs();
    // 加载预约数据
    if (props.roomId) {
      loadScheduleData();
    }
  },
});

/** 暴露modal API供外部调用 */
defineExpose({
  modalApi,
});
</script>

<template>
  <Modal :title="modalTitle">
    <div v-loading="loading" class="booking-schedule-container">
      <!-- 日期切换 -->
      <div class="mb-4 flex gap-2 border-b pb-2">
        <div
          v-for="date in dateList"
          :key="date.format('YYYY-MM-DD')"
          class="cursor-pointer rounded px-4 py-2 text-center transition-colors"
          :class="
            selectedDate.format('YYYY-MM-DD') === date.format('YYYY-MM-DD')
              ? 'bg-blue-500 text-white'
              : 'bg-gray-100 hover:bg-gray-200'
          "
          @click="handleDateChange(date)"
        >
          <div class="text-sm font-medium">
            {{ date.format('MM-DD') }}
          </div>
          <div class="text-xs">
            {{
              date.day() === 0
                ? '周日'
                : date.day() === 1
                  ? '周一'
                  : date.day() === 2
                    ? '周二'
                    : date.day() === 3
                      ? '周三'
                      : date.day() === 4
                        ? '周四'
                        : date.day() === 5
                          ? '周五'
                          : '周六'
            }}
          </div>
        </div>
      </div>

      <div class="flex gap-4">
        <!-- 左侧：时间网格 -->
        <div class="min-w-0 flex-1">
          <div class="mb-3 text-lg font-semibold">
            {{ selectedDate.format('YYYY年MM月DD日') }} 预约情况
          </div>

          <!-- 时间轴和时间网格容器 -->
          <div class="time-schedule-wrapper">
            <!-- 按行显示时间轴和时间网格 -->
            <div
              v-for="(hourRow, rowIndex) in timeAxisRows"
              :key="rowIndex"
              class="time-row"
            >
              <div class="time-row-content">
                <!-- 行标签 -->
                <div class="time-row-label">
                  {{ rowIndex === 0 ? '上午' : '下午' }}
                </div>

                <div class="time-row-main">
                  <!-- 时间轴标签 -->
                  <div class="time-axis">
                    <div
                      v-for="hour in hourRow"
                      :key="hour"
                      class="time-axis-item"
                    >
                      {{ String(hour).padStart(2, '0') }}:00
                    </div>
                  </div>

                  <!-- 时间网格 -->
                  <div class="time-grid">
                    <div
                      v-for="(slot, index) in timeSlotRows[rowIndex]"
                      :key="index"
                      :title="getTimeSlotTooltip(slot)"
                      class="time-slot"
                      :class="getTimeSlotClass(getTimeSlotStatus(slot))"
                    ></div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 图例 -->
          <div class="mt-4 flex gap-6 text-sm">
            <div class="flex items-center gap-2">
              <div class="legend-item legend-available"></div>
              <span>可预约</span>
            </div>
            <div class="flex items-center gap-2">
              <div class="legend-item legend-booked"></div>
              <span>已预约</span>
            </div>
            <div class="flex items-center gap-2">
              <div class="legend-item legend-expired"></div>
              <span>已过期</span>
            </div>
          </div>
        </div>

        <!-- 右侧：选中日期当天的审批通过的预约记录列表（仅会议室信息列表打开时显示） -->
        <div v-if="showTodayApproved" class="w-80 border-l pl-4">
          <div class="mb-2 text-lg font-semibold">
            {{ selectedDate.format('MM月DD日') }}已预约
          </div>
          <div
            v-if="selectedDateApprovedBookings.length === 0"
            class="text-gray-400"
          >
            暂无预约记录
          </div>
          <div v-else class="max-h-[600px] space-y-2 overflow-y-auto">
            <div
              v-for="booking in selectedDateApprovedBookings"
              :key="booking.id"
              class="rounded border p-3 hover:bg-gray-50"
            >
              <div class="mb-1 font-medium text-blue-600">
                {{ booking.meetingTitle }}
              </div>
              <div class="mb-1 text-sm text-gray-600">
                <span class="mr-2">
                  {{ dayjs(booking.meetingStartTime).format('HH:mm') }} -
                  {{ dayjs(booking.meetingEndTime).format('HH:mm') }}
                </span>
              </div>
              <div class="mb-1 text-xs text-gray-500">
                <span class="mr-2">主持人：{{ booking.moderatorName }}</span>
                <span>申请人：{{ booking.creatorName }}</span>
              </div>
              <div class="text-xs">
                <Tag color="green" size="small">
                  {{
                    getDictLabel(
                      DICT_TYPE.BPM_PROCESS_INSTANCE_STATUS,
                      booking.processStatus,
                    )
                  }}
                </Tag>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Modal>
</template>

<style scoped>
.booking-schedule-container {
  min-height: 500px;
}

/* 时间轴和时间网格容器 */
.time-schedule-wrapper {
  overflow: hidden;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
}

/* 时间行容器 */
.time-row {
  border-bottom: 1px solid #e5e7eb;
}

.time-row:last-child {
  border-bottom: none;
}

/* 时间行内容 */
.time-row-content {
  display: flex;
  align-items: flex-start;
}

/* 行标签 */
.time-row-label {
  display: flex;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;
  width: 60px;
  min-height: 60px;
  padding: 12px 8px;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  text-align: center;
  background: #f9fafb;
  border-right: 1px solid #e5e7eb;
}

/* 时间行主内容 */
.time-row-main {
  flex: 1;
  min-width: 0;
}

/* 时间轴标签 */
.time-axis {
  display: flex;
  padding: 8px 4px;
  background: #f3f4f6;
  border-bottom: 1px solid #e5e7eb;
}

.time-axis-item {
  flex: 0 0 calc(100% / 12);
  min-width: 0;
  font-size: 13px;
  font-weight: 500;
  color: #6b7280;
  text-align: center;
}

/* 时间网格 */
.time-grid {
  display: flex;
  padding: 8px 4px;
  background: white;
}

.time-slot {
  position: relative;
  flex: 0 0 calc(100% / 24);
  min-width: 0;
  min-width: 12px;
  height: 36px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
}

/* 可预约状态 */
.time-slot.bg-white {
  background-color: #fff;
  border: 1px solid #e5e7eb;
}

.time-slot.bg-white:hover {
  background-color: #f3f4f6;
  border-color: #d1d5db;
}

/* 已预约状态 */
.time-slot.bg-green-500 {
  background-color: #10b981;
  border: 1px solid #059669;
}

.time-slot.bg-green-500:hover {
  background-color: #059669;
}

/* 已过期状态 */
.time-slot.bg-gray-300 {
  cursor: not-allowed;
  background-color: #d1d5db;
  border: 1px solid #9ca3af;
  opacity: 0.6;
}

/* 图例样式 */
.legend-item {
  flex-shrink: 0;
  width: 16px;
  height: 16px;
  border-radius: 2px;
}

.legend-available {
  background-color: #fff;
  border: 1px solid #e5e7eb;
}

.legend-booked {
  background-color: #10b981;
  border: 1px solid #059669;
}

.legend-expired {
  background-color: #d1d5db;
  border: 1px solid #9ca3af;
  opacity: 0.6;
}

/* 优化时间块的视觉效果 */
.time-slot:hover {
  z-index: 1;
  box-shadow: 0 2px 4px rgb(0 0 0 / 10%);
  transform: scale(1.05);
}
</style>
