<script lang="ts" setup>
import type { Dayjs } from 'dayjs';

import type { SystemScheduleApi } from '#/api/system/schedule';

import { computed, onMounted, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { DICT_TYPE } from '@vben/constants';
import { getDictLabel } from '@vben/hooks';
import { IconifyIcon } from '@vben/icons';

import { Calendar, Spin, Tag } from 'ant-design-vue';
import dayjs from 'dayjs';

import {
  getSchedule,
  getScheduleDates,
  getScheduleListByDate,
} from '#/api/system/schedule';
import { router } from '#/router';

import ScheduleDetailModal from './schedule-detail-modal.vue';

interface Props {
  title?: string; // 标题
  maxRecordNum?: number; // 最大显示条数
}

const props = withDefaults(defineProps<Props>(), {
  title: '日程待办',
  maxRecordNum: 10,
});

const loading = ref(false);
const selectedDate = ref<Dayjs>(dayjs());
const scheduleDates = ref<string[]>([]); // 有日程的日期列表
const selectedDateSchedules = ref<SystemScheduleApi.Schedule[]>([]); // 选中日期的日程列表
const showFloatingList = ref(false); // 是否显示浮动列表
const floatingListExpanded = ref(true); // 浮动列表是否展开
const calendarRef = ref<HTMLElement>(); // 日历容器引用

// 详情弹窗
const [DetailModal, detailModalApi] = useVbenModal({
  connectedComponent: ScheduleDetailModal,
  destroyOnClose: true,
});

// 当前月份的开始和结束日期
const currentMonth = ref<Dayjs>(dayjs());
const monthStart = computed(() => currentMonth.value.startOf('month'));
const monthEnd = computed(() => currentMonth.value.endOf('month'));

// 格式化日期为 YYYY-MM-DD
function formatDate(date: Dayjs | string): string {
  if (typeof date === 'string') return date;
  return date.format('YYYY-MM-DD');
}

// 检查日期是否有日程
function hasSchedule(date: Dayjs): boolean {
  const dateStr = formatDate(date);
  return scheduleDates.value.includes(dateStr);
}

// 日期选择处理
async function handleDateSelect(date: Dayjs) {
  selectedDate.value = date;
  await loadScheduleListByDate(date);
  // 如果有日程，显示浮动列表
  if (selectedDateSchedules.value.length > 0) {
    showFloatingList.value = true;
    floatingListExpanded.value = true;
  } else {
    showFloatingList.value = false;
  }
}

// 月份变化处理
async function handlePanelChange(date: Dayjs | string) {
  const dateObj = typeof date === 'string' ? dayjs(date) : date;
  currentMonth.value = dateObj;
  await loadScheduleDates();
}

// 加载有日程的日期列表
async function loadScheduleDates() {
  try {
    const startDate = formatDate(monthStart.value);
    const endDate = formatDate(monthEnd.value);
    const response = await getScheduleDates(startDate, endDate);
    scheduleDates.value = response || [];
  } catch (error) {
    console.error('加载日程日期失败:', error);
    scheduleDates.value = [];
  }
}

// 根据日期加载日程列表
async function loadScheduleListByDate(date: Dayjs) {
  loading.value = true;
  try {
    const dateStr = formatDate(date);
    const response = await getScheduleListByDate({ scheduleDate: dateStr });
    selectedDateSchedules.value = (response || []).slice(0, props.maxRecordNum);
  } catch (error) {
    console.error('加载日程列表失败:', error);
    selectedDateSchedules.value = [];
  } finally {
    loading.value = false;
  }
}

// 格式化时间
function formatTime(time?: string): string {
  if (!time) return '';
  return time.slice(0, 5); // HH:mm
}

// 获取日程类型文本
function getScheduleTypeText(type?: string): string {
  if (!type) return '';
  return getDictLabel(DICT_TYPE.SCHEDULE_TYPE, type) || type;
}

// 获取日程类型的tag颜色
function getScheduleTypeColor(type?: string): string {
  const colorMap: Record<string, string> = {
    meeting: 'blue', // 会议
    task: 'orange', // 任务
    reminder: 'green', // 提醒
    other: 'default', // 其他
  };
  return colorMap[type || ''] || 'default';
}

// 切换浮动列表展开/收起
function toggleFloatingList() {
  floatingListExpanded.value = !floatingListExpanded.value;
}

// 关闭浮动列表
function closeFloatingList() {
  showFloatingList.value = false;
}

// 打开日程详情（只读）
async function handleViewDetail(schedule: SystemScheduleApi.Schedule) {
  if (!schedule.id) return;
  try {
    const detail = await getSchedule(schedule.id);
    detailModalApi.setData(detail).open();
  } catch (error) {
    console.error('加载日程详情失败:', error);
  }
}

// 跳转到全部日程
function handleViewAll() {
  // 使用路径跳转，因为路由名称可能不同
  router.push({ path: '/oa/schedule' });
}

// 组件挂载时加载数据
onMounted(async () => {
  await loadScheduleDates();
  await loadScheduleListByDate(selectedDate.value);
});
</script>

<template>
  <div class="workbench-schedule rounded-lg bg-background">
    <!-- 头部 -->
    <div class="schedule-header flex items-center justify-between px-4 py-3">
      <h3 class="text-base font-semibold">{{ props.title }}</h3>
      <a
        class="cursor-pointer text-sm text-primary hover:underline"
        @click="handleViewAll"
      >
        全部日程 >
      </a>
    </div>

    <!-- 日历 -->
    <div ref="calendarRef" class="schedule-calendar px-4 pb-4">
      <Calendar
        v-model:value="selectedDate"
        @select="handleDateSelect"
        @panel-change="handlePanelChange"
        :fullscreen="false"
      >
        <template #dateCellRender="{ current: date }">
          <div class="schedule-date-wrapper">
            <div v-if="hasSchedule(date)" class="schedule-dot"></div>
          </div>
        </template>
      </Calendar>
    </div>

    <!-- 浮动日程列表 - 从下方滑入 -->
    <Transition name="slide-up">
      <div
        v-if="showFloatingList && selectedDateSchedules.length > 0"
        class="floating-schedule-list"
      >
        <div class="floating-header flex items-center justify-between">
          <span class="text-sm font-medium">
            {{ formatDate(selectedDate) }} 的日程
          </span>
          <div class="flex items-center gap-2">
            <button class="floating-toggle-btn" @click="toggleFloatingList">
              <IconifyIcon
                v-if="floatingListExpanded"
                icon="ant-design:up-outlined"
                class="text-xs"
              />
              <IconifyIcon
                v-else
                icon="ant-design:down-outlined"
                class="text-xs"
              />
            </button>
            <button class="floating-close-btn" @click="closeFloatingList">
              ×
            </button>
          </div>
        </div>
        <div v-show="floatingListExpanded" class="floating-content">
          <Spin :spinning="loading">
            <div class="space-y-1">
              <div
                v-for="schedule in selectedDateSchedules.slice(0, 3)"
                :key="schedule.id"
                class="floating-schedule-item"
              >
                <!-- 时间 -->
                <span
                  v-if="schedule.startTime"
                  class="schedule-time whitespace-nowrap text-xs text-gray-500"
                >
                  {{ formatTime(schedule.startTime) }}
                  <span v-if="schedule.endTime">
                    - {{ formatTime(schedule.endTime) }}
                  </span>
                </span>
                <!-- 标题（可点击） -->
                <span
                  class="schedule-title-link cursor-pointer text-sm font-medium"
                  @click="handleViewDetail(schedule)"
                >
                  {{ schedule.title }}
                </span>
                <!-- 日程类型Tag -->
                <Tag
                  v-if="schedule.scheduleType"
                  :color="getScheduleTypeColor(schedule.scheduleType)"
                  size="small"
                  class="flex-shrink-0"
                >
                  {{ getScheduleTypeText(schedule.scheduleType) }}
                </Tag>
                <!-- 创建人 -->
                <span
                  v-if="schedule.creatorName"
                  class="schedule-creator whitespace-nowrap text-xs text-gray-500"
                >
                  {{ schedule.creatorName }}
                </span>
              </div>
            </div>
          </Spin>
        </div>
      </div>
    </Transition>

    <!-- 详情弹窗 -->
    <DetailModal />
  </div>
</template>

<style scoped>
.workbench-schedule {
  box-shadow:
    0 1px 2px 0 rgb(0 0 0 / 3%),
    0 1px 6px -1px rgb(0 0 0 / 2%),
    0 2px 4px 0 rgb(0 0 0 / 2%);
}

.schedule-header {
  border-bottom: 1px solid #f0f0f0;
}

/* 日历容器样式 */
.schedule-calendar :deep(.ant-picker-calendar) {
  background: transparent;
  border: none;
}

/* 隐藏日历头部不必要的元素 */
.schedule-calendar :deep(.ant-picker-calendar-header) {
  padding: 8px 0;
  border-bottom: none;
}

/* 日历表格样式 - 移除边框和间距 */
.schedule-calendar :deep(.ant-picker-calendar .ant-picker-panel) {
  background: transparent;
  border: none;
}

.schedule-calendar :deep(.ant-picker-calendar .ant-picker-body) {
  padding: 0;
}

/* 星期标题行 - 移除下边框 */
.schedule-calendar
  :deep(
    .ant-picker-calendar
      .ant-picker-calendar-date-panel
      .ant-picker-content
      thead
      tr
      th
  ) {
  padding: 4px 0;
  font-weight: normal;
  border-bottom: none;
}

/* 日期单元格 - 移除所有边框和间距 */
.schedule-calendar :deep(.ant-picker-calendar .ant-picker-cell) {
  height: auto;
  min-height: 40px;
  padding: 0;
  border: none;
}

.schedule-calendar :deep(.ant-picker-calendar .ant-picker-cell::before) {
  display: none;
}

/* 日期内容 - 移除上边框和间距 */
.schedule-calendar :deep(.ant-picker-calendar .ant-picker-calendar-date) {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 40px;
  padding: 2px 0;
  margin: 0;
  border: none;
  border-top: none;
}

/* 移除日期单元格的悬停边框 */
.schedule-calendar
  :deep(.ant-picker-calendar .ant-picker-cell:hover .ant-picker-calendar-date) {
  background: transparent;
  border: none;
}

/* 选中日期时显示蓝色背景和文字 */
.schedule-calendar
  :deep(
    .ant-picker-calendar .ant-picker-cell-selected .ant-picker-calendar-date
  ) {
  background: hsl(var(--primary) / 10%);
  border: none;
  border-radius: 4px;
}

.schedule-calendar
  :deep(
    .ant-picker-calendar
      .ant-picker-cell-selected
      .ant-picker-calendar-date-value
  ) {
  color: hsl(var(--primary));
}

/* 当前日期样式 */
.schedule-calendar
  :deep(.ant-picker-calendar .ant-picker-cell-today .ant-picker-calendar-date) {
  font-weight: normal;
  border: none;
}

.schedule-calendar
  :deep(
    .ant-picker-calendar .ant-picker-cell-today .ant-picker-calendar-date-value
  ) {
  font-weight: normal;
  color: hsl(var(--primary));
}

/* 其他月份的日期 */
.schedule-calendar
  :deep(
    .ant-picker-calendar
      .ant-picker-cell-disabled
      .ant-picker-calendar-date-value
  ) {
  color: #bfbfbf;
}

/* 日期值样式 */
.schedule-calendar :deep(.ant-picker-calendar .ant-picker-calendar-date-value) {
  display: block;
  padding: 0;
  margin: 0;
  line-height: 1.2;
}

/* 日程红点容器 - 放在日期下方 */
.schedule-date-wrapper {
  position: absolute;
  right: 0;
  bottom: 0;
  left: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 12px;
  pointer-events: none;
}

/* 日程红点 */
.schedule-dot {
  flex-shrink: 0;
  width: 6px;
  height: 6px;
  background-color: #ff4d4f;
  border-radius: 50%;
}

.schedule-list {
  max-height: 300px;
  overflow-y: auto;
}

.schedule-item {
  cursor: pointer;
}

/* 滚动条样式 */
.schedule-list::-webkit-scrollbar {
  width: 6px;
}

.schedule-list::-webkit-scrollbar-thumb {
  background-color: #d9d9d9;
  border-radius: 3px;
}

.schedule-list::-webkit-scrollbar-thumb:hover {
  background-color: #bfbfbf;
}

.schedule-list::-webkit-scrollbar-track {
  background-color: #f5f5f5;
}

/* 浮动日程列表样式 - 从下方滑入 */
.floating-schedule-list {
  position: absolute;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 100;
  margin: 0 16px; /* 与日历的 padding 对齐 */
  overflow: hidden;
  background: white;
  border: 1px solid #e5e7eb;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
  box-shadow:
    0 -4px 6px -1px rgb(0 0 0 / 10%),
    0 -2px 4px -1px rgb(0 0 0 / 6%);
}

.floating-header {
  padding: 8px 12px;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
}

.floating-toggle-btn,
.floating-close-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  color: #6b7280;
  cursor: pointer;
  background: transparent;
  border: none;
  border-radius: 4px;
  transition: color 0.2s;
}

.floating-toggle-btn:hover,
.floating-close-btn:hover {
  color: #374151;
  background: #e5e7eb;
}

.floating-content {
  max-height: 180px; /* 约三行日程的高度 */
  padding: 8px 12px;
  overflow-y: auto;
}

.floating-schedule-item {
  display: flex;
  flex-wrap: nowrap;
  gap: 8px;
  align-items: center;
  padding: 6px 0;
  border-bottom: 1px solid #f3f4f6;
}

.floating-schedule-item:last-child {
  border-bottom: none;
}

.floating-schedule-item .schedule-time {
  flex-shrink: 0;
  min-width: 80px;
}

.floating-schedule-item .schedule-title {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.floating-schedule-item .schedule-creator {
  flex-shrink: 0;
}

/* 日程标题链接样式 - 参考单据编号样式 */
.schedule-title-link {
  color: hsl(var(--primary));
  text-decoration: none;
  cursor: pointer;
  transition: color 0.3s;
}

.schedule-title-link:hover {
  color: hsl(var(--primary) / 80%);
  text-decoration: underline;
}

/* 从下方滑入的过渡动画 */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}

.slide-up-enter-from {
  opacity: 0;
  transform: translateY(100%);
}

.slide-up-leave-to {
  opacity: 0;
  transform: translateY(100%);
}
</style>
