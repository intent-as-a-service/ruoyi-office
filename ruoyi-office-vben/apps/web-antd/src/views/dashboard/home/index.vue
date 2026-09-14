<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

import { Page } from '@vben/common-ui';

import { Alert, Button, Spin } from 'ant-design-vue';

import { getMyHomePage } from '#/api/system/home';

import LayoutRenderer from './renderer/layout-renderer.vue';

const route = useRoute();

const loading = ref(false);
const homePageInfo = ref<any>(null);

// 支持预览模式（从设计器跳转）
const previewPageId = computed(() => {
  const pageId = route.query.preview;
  return pageId ? Number(pageId) : null;
});

const currentPageId = computed(() => {
  return previewPageId.value || homePageInfo.value?.id;
});

/** 加载用户首页信息 */
async function loadHomePage() {
  loading.value = true;
  try {
    homePageInfo.value = await getMyHomePage();
  } catch (error) {
    console.error('Failed to load home page:', error);
  } finally {
    loading.value = false;
  }
}

/** 跳转到首页管理 */
function goToManage() {
  window.location.href = '/#/home/manage';
}

onMounted(() => {
  loadHomePage();
});
</script>

<template>
  <Page
    :content-class="{
      'p-0!': true,
      'm-0!': true,
    }"
    :content-style="{ height: 'calc(100vh - 64px)', padding: '0', margin: '0' }"
  >
    <Spin :spinning="loading" tip="加载首页配置...">
      <div v-if="previewPageId" class="preview-banner">
        <Alert
          message="预览模式"
          description="您正在预览首页配置，此模式下的修改不会保存。"
          type="info"
          show-icon
          closable
        />
      </div>

      <div v-if="!loading && currentPageId" class="h-full">
        <LayoutRenderer :page-id="currentPageId" />
      </div>

      <div
        v-else-if="!loading && !currentPageId"
        class="flex h-full items-center justify-center"
      >
        <div class="text-center">
          <p class="mb-4 text-gray-500">您还没有配置首页</p>
          <Button type="primary" @click="goToManage"> 去配置首页 </Button>
        </div>
      </div>
    </Spin>
  </Page>
</template>

<style scoped>
.preview-banner {
  padding: 16px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
}
</style>
