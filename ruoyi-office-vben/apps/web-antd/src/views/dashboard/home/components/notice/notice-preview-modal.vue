<script lang="ts" setup>
import type { SystemNoticeApi } from '#/api/system/notice';

import { computed, watch } from 'vue';

import { DICT_TYPE } from '@vben/constants';
import { getDictLabel } from '@vben/hooks';

import { Modal, Tag } from 'ant-design-vue';

import { markNoticeAsRead } from '#/api/system/notice';

interface Props {
  visible: boolean;
  notice: null | SystemNoticeApi.Notice;
}

interface Emits {
  (e: 'close'): void;
  (e: 'update:visible', value: boolean): void;
  (e: 'refresh'): void; // 刷新列表事件
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

// 获取通知类型文本
function getNoticeTypeText(type: number) {
  return getDictLabel(DICT_TYPE.SYSTEM_NOTICE_TYPE, type) || '未知';
}

// 格式化日期时间
function formatDateTime(date: Date | string | undefined) {
  if (!date) return '';
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const hour = String(d.getHours()).padStart(2, '0');
  const minute = String(d.getMinutes()).padStart(2, '0');
  const second = String(d.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
}

// 处理关闭
function handleClose() {
  emit('update:visible', false);
  emit('close');
}

// 弹窗标题
const modalTitle = computed(() => {
  if (!props.notice) return '通知详情';
  return `${getNoticeTypeText(props.notice.type)}详情`;
});

// 监听弹窗打开，标记为已读
watch(
  () => props.visible,
  (visible) => {
    if (visible && props.notice?.id && props.notice.readStatus !== 1) {
      markNoticeAsRead(props.notice.id)
        .then(() => {
          // 通知父组件刷新列表（父组件会重新获取数据，更新 readStatus）
          emit('refresh');
        })
        .catch((error) => {
          console.error('标记已读失败:', error);
        });
    }
  },
);
</script>

<template>
  <Modal
    :open="visible"
    :title="modalTitle"
    width="900px"
    :footer="null"
    @cancel="handleClose"
  >
    <div v-if="notice" class="notice-preview">
      <!-- 标题 -->
      <div class="mb-4">
        <h2 class="mb-2 text-xl font-semibold">{{ notice.title }}</h2>
        <div class="flex items-center gap-2">
          <Tag color="blue">{{ getNoticeTypeText(notice.type) }}</Tag>
          <Tag v-if="notice.isImportant" color="red">重要</Tag>
        </div>
      </div>

      <!-- 元信息 -->
      <div class="mb-4 rounded-lg bg-gray-50 p-3">
        <div class="grid grid-cols-2 gap-2 text-sm">
          <div class="flex items-center gap-2 text-gray-600">
            <iconify-icon icon="carbon:user" class="text-base" />
            <span>发布人:</span>
            <span class="text-gray-900">{{
              notice.creatorName || notice.creator || '-'
            }}</span>
          </div>
          <div class="flex items-center gap-2 text-gray-600">
            <iconify-icon icon="carbon:time" class="text-base" />
            <span>发布时间:</span>
            <span class="text-gray-900">{{
              formatDateTime(notice.createTime)
            }}</span>
          </div>
        </div>
      </div>

      <!-- 内容 -->
      <div
        class="notice-content mb-4"
        style="max-height: 60vh; overflow-y: auto"
      >
        <h3 class="mb-2 text-sm font-medium text-gray-700">通知内容</h3>
        <!-- eslint-disable vue/no-v-html -->
        <div
          class="rounded-lg border border-gray-200 bg-white p-4 text-sm text-gray-900"
          v-html="notice.content || '暂无内容'"
        ></div>
        <!-- eslint-enable vue/no-v-html -->
      </div>

      <!-- 备注 -->
      <div v-if="notice.remark" class="notice-remark">
        <h3 class="mb-2 text-sm font-medium text-gray-700">备注</h3>
        <div class="text-sm text-gray-600">{{ notice.remark }}</div>
      </div>
    </div>

    <div v-else class="py-8 text-center text-gray-400">暂无数据</div>
  </Modal>
</template>

<style scoped>
.notice-content :deep(p) {
  margin-bottom: 0.5rem;
  line-height: 1.6;
}

.notice-content :deep(p:last-child) {
  margin-bottom: 0;
}

.notice-content :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
}

.notice-content :deep(a) {
  color: hsl(var(--primary));
  text-decoration: underline;
}

.notice-content :deep(ul),
.notice-content :deep(ol) {
  margin-bottom: 0.5rem;
  margin-left: 1.5rem;
}

.notice-content :deep(li) {
  margin-bottom: 0.25rem;
}
</style>
