<!--
 * @Author: zhanghui
 * @Date: 2025-08-16 15:05:24
 * @LastEditTime: 2025-08-16 19:38:32
 * @LastEditors: zhanghui
 * @Description: 无审批流基本表单
-->

<script lang="ts" setup>
import type { CSSProperties } from 'vue';

import { onMounted } from 'vue';

import { Page } from '@vben/common-ui';

import footerForm from './footer-form.vue';

interface Props {
  headerTitle?: string;
  formData?: object;
}
const props = withDefaults(defineProps<Props>(), {
  headerTitle: '详情页',
  formData: () => ({}),
});

const emit = defineEmits(['close', 'save', 'submit']);

// 表头样式
const headerStyle: CSSProperties = {
  height: 'auto',
  lineHeight: '20px',
  backgroundColor: '#fff',
  padding: '20px',
};

const contentStyle: CSSProperties = {
  minHeight: 120,
  padding: '20px',
};

const footerStyle: CSSProperties = {
  textAlign: 'center',
  background: '#fff',
  display: 'flex',
  justifyContent: 'center',
  gap: '15px',
  margin: '10px 0 0',
  padding: '10px 0',
};
// 关闭
const closeForm = () => {
  emit('close');
};
// 保存
const saveForm = () => {
  emit('save');
};
// 提交
const submitForm = () => {
  emit('submit');
};
/** 初始化 */
onMounted(async () => {});
</script>
<template>
  <Page auto-content-height class="mx-6 border-b bg-white">
    <a-layout style="min-height: 100%; background: #fff">
      <a-layout-header :style="headerStyle">
        <!-- 表头部分 -->
        <div class="header-title">{{ props.headerTitle }}</div>
      </a-layout-header>
      <a-divider style="width: auto; margin: -5px -15px -10px" />
      <a-layout-content :style="contentStyle">
        <!-- 主体部分 -->
        <slot name="base-form"></slot>
      </a-layout-content>
      <a-divider style="width: auto; margin: -5px -15px -10px" />
      <a-layout-footer :style="footerStyle">
        <!-- 底部按钮 -->
        <footerForm @submit="submitForm" @close="closeForm" @save="saveForm" />
      </a-layout-footer>
    </a-layout>
  </Page>
</template>
<style lang="scss" scoped>
:where(.css-dev-only-do-not-override-14589v).ant-layout .ant-layout-content {
  height: calc(100vh - 259px);
  padding: 0 !important;
  margin: 20px;
  overflow-y: auto;
}

.header-title {
  font-size: 20px;
}

.header-title::before {
  display: inline-block;
  width: 5px;
  height: 20px;
  margin-right: 8px;
  vertical-align: bottom;
  content: ' ';
  background: hsl(var(--primary));
  border-radius: 3px;
}
</style>
