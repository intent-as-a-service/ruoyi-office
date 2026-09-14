<script lang="ts" setup>
import type { SystemNoticeApi } from '#/api/system/notice';

import { computed, onMounted, ref } from 'vue';

import { DICT_TYPE } from '@vben/constants';
import { getDictLabel } from '@vben/hooks';

import { Badge, Empty, Spin, Tag } from 'ant-design-vue';

import { getNoticePage } from '#/api/system/notice';
import { router } from '#/router';

import NoticePreviewModal from './notice-preview-modal.vue';

interface Props {
  maxRecordNum?: number; // 最大显示条数
  showBadge?: boolean; // 是否显示徽章
}

const props = withDefaults(defineProps<Props>(), {
  maxRecordNum: 10,
  showBadge: true,
});

const loading = ref(false);
const noticeList = ref<SystemNoticeApi.Notice[]>([]);
const total = ref(0);
const unreadCount = computed(() => {
  return noticeList.value.filter((notice) => notice.readStatus !== 1).length;
});

// 预览弹窗
const previewVisible = ref(false);
const selectedNotice = ref<null | SystemNoticeApi.Notice>(null);

// 格式化日期
function formatDate(date: Date | string | undefined) {
  if (!date) return '';
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

// 获取公告类型文本
function getNoticeTypeText(type: number) {
  return getDictLabel(DICT_TYPE.SYSTEM_NOTICE_TYPE, type) || '未知';
}

// 加载通知公告列表
async function loadNoticeList() {
  loading.value = true;
  try {
    const response = await getNoticePage({
      pageNo: 1,
      pageSize: props.maxRecordNum,
    });
    noticeList.value = response.list || [];
    total.value = response.total || 0;
  } catch (error) {
    console.error('加载通知公告失败:', error);
    noticeList.value = [];
  } finally {
    loading.value = false;
  }
}

// 查看通知详情
function handleViewNotice(notice: SystemNoticeApi.Notice) {
  selectedNotice.value = notice;
  previewVisible.value = true;
}

// 跳转到完整列表
function handleViewMore() {
  router.push({ name: 'NoticeView' });
}

// 关闭预览弹窗
function handleClosePreview() {
  previewVisible.value = false;
  selectedNotice.value = null;
}

// 显示徽章数量
const badgeCount = computed(() => {
  if (!props.showBadge) return 0;
  return unreadCount.value;
});

// 组件挂载时加载数据
onMounted(() => {
  loadNoticeList();
});
</script>

<template>
  <div class="workbench-notice rounded-lg bg-background">
    <!-- 头部 -->
    <div class="notice-header flex items-center justify-between px-4 py-3">
      <div class="flex items-center gap-2">
        <h3 class="text-base font-semibold">通知公告</h3>
        <Badge v-if="showBadge" :count="badgeCount" :overflow-count="99" />
      </div>
      <a
        class="cursor-pointer text-sm text-primary hover:underline"
        @click="handleViewMore"
      >
        查看更多
      </a>
    </div>

    <!-- 列表内容 -->
    <div class="notice-list">
      <Spin :spinning="loading">
        <div v-if="noticeList.length > 0" class="divide-y">
          <div
            v-for="notice in noticeList"
            :key="notice.id"
            class="notice-item cursor-pointer px-4 py-2.5 transition-colors hover:bg-gray-50"
            @click="handleViewNotice(notice)"
          >
            <div class="flex items-center gap-2 text-sm">
              <!-- 已读状态 -->
              <div
                class="h-2 w-2 flex-shrink-0 rounded-full"
                :style="{
                  backgroundColor:
                    notice.readStatus === 1 ? '#d9d9d9' : '#ff7a00',
                }"
                :title="notice.readStatus === 1 ? '已读' : '未读'"
              ></div>

              <!-- 公告类型 -->
              <span class="text-gray-500">{{
                `【${getNoticeTypeText(notice.type)}】`
              }}</span>

              <!-- 公告标题 -->
              <span class="flex-1 truncate text-gray-900">{{
                notice.title
              }}</span>

              <!-- 是否重要 -->
              <Tag v-if="notice.isImportant" color="red" size="small">重要</Tag>

              <!-- 公告日期 -->
              <span class="flex-shrink-0 text-xs text-gray-400">{{
                formatDate(notice.createTime)
              }}</span>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else-if="!loading" class="py-8">
          <Empty
            description="暂无通知公告"
            :image="Empty.PRESENTED_IMAGE_SIMPLE"
          />
        </div>
      </Spin>
    </div>

    <!-- 预览弹窗 -->
    <NoticePreviewModal
      v-model:visible="previewVisible"
      :notice="selectedNotice"
      @close="handleClosePreview"
      @refresh="loadNoticeList"
    />
  </div>
</template>

<style scoped>
.workbench-notice {
  box-shadow:
    0 1px 2px 0 rgb(0 0 0 / 3%),
    0 1px 6px -1px rgb(0 0 0 / 2%),
    0 2px 4px 0 rgb(0 0 0 / 2%);
}

.notice-header {
  border-bottom: 1px solid #f0f0f0;
}

.notice-list {
  min-height: 300px;
  max-height: 500px;
  overflow-y: auto;
}

.notice-item:last-child {
  border-bottom: none;
}

.line-clamp-2 {
  display: -webkit-box;
  overflow: hidden;
  text-overflow: ellipsis;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

/* 滚动条样式 */
.notice-list::-webkit-scrollbar {
  width: 6px;
}

.notice-list::-webkit-scrollbar-thumb {
  background-color: #d9d9d9;
  border-radius: 3px;
}

.notice-list::-webkit-scrollbar-thumb:hover {
  background-color: #bfbfbf;
}

.notice-list::-webkit-scrollbar-track {
  background-color: #f5f5f5;
}
</style>
