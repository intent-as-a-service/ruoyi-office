<script lang="ts" setup>
import type { WorkbenchProjectItem } from '@vben/common-ui';

import { WorkbenchProject } from '@vben/common-ui';
import { openWindow } from '@vben/utils';

interface Props {
  title?: string;
}

withDefaults(defineProps<Props>(), {
  title: '项目',
});

// 示例项目数据
const projectItems: WorkbenchProjectItem[] = [
  {
    color: '#6DB33F',
    content: 'github.com/YunaiV/ruoyi-vue-pro',
    date: '2025-01-02',
    group: 'Spring Boot 单体架构',
    icon: 'simple-icons:springboot',
    title: 'ruoyi-vue-pro',
    url: 'https://github.com/YunaiV/ruoyi-vue-pro',
  },
  {
    color: '#409EFF',
    content: 'github.com/yudaocode/yudao-ui-admin-vue3',
    date: '2025-02-03',
    group: 'Vue3 + element-plus 管理后台',
    icon: 'ep:element-plus',
    title: 'yudao-ui-admin-vue3',
    url: 'https://github.com/yudaocode/yudao-ui-admin-vue3',
  },
];

function navTo(nav: WorkbenchProjectItem) {
  if (nav.url?.startsWith('http')) {
    openWindow(nav.url);
  }
}
</script>

<template>
  <WorkbenchProject :items="projectItems" :title="title" @click="navTo" />
</template>
