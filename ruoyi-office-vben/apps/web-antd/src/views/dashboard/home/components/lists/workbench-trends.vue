<script lang="ts" setup>
import type { WorkbenchTrendItem } from '@vben/common-ui';

import { WorkbenchTrends } from '@vben/common-ui';

interface Props {
  title?: string;
}

withDefaults(defineProps<Props>(), {
  title: '最新动态',
});

// 示例动态数据
const trendItems: WorkbenchTrendItem[] = [
  {
    avatar: 'svg:avatar-1',
    content: `在 <a>开源组</a> 创建了项目 <a>Vue</a>`,
    date: '刚刚',
    title: '威廉',
  },
  {
    avatar: 'svg:avatar-2',
    content: `关注了 <a>威廉</a> `,
    date: '1个小时前',
    title: '艾文',
  },
  {
    avatar: 'svg:avatar-3',
    content: `发布了 <a>个人动态</a> `,
    date: '1天前',
    title: '克里斯',
  },
  {
    avatar: 'svg:avatar-4',
    content: `发表文章 <a>如何编写一个Vite插件</a> `,
    date: '2天前',
    title: 'Vben',
  },
];
</script>

<template>
  <WorkbenchTrends :items="trendItems" :title="title" />
</template>
