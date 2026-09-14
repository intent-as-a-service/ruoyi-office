<script lang="ts" setup>
import { onBeforeUnmount, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';

import { configureIntentUi } from '../sdk';

defineOptions({ name: 'IntentCenter' });

/**
 * 意图调试台：全量目录 + 点击意图先弹槽位表单（debug 模式），填写后执行验证。
 * 面板渲染完全交给 SDK，与业务页面共用同一份实现。配置入口见「意图管理」。
 */
const container = ref<HTMLDivElement>();
let panel: undefined | { destroy: () => void };

onMounted(() => {
  panel = configureIntentUi().mount({
    container: container.value,
    mode: 'debug',
    // 不传 page = 全量目录（调试台需要看到所有意图，而不是某个页面的挂载点）
    page: '',
  });
});

onBeforeUnmount(() => {
  panel?.destroy();
  panel = undefined;
});
</script>

<template>
  <Page
    content-class="flex flex-col gap-4"
    description="调试台：选择意图 → 按槽位填写参数（实体槽位自动渲染候选下拉）→ 执行验证。配置入口见「意图管理」。"
    title="意图调试台"
  >
    <div ref="container" class="rounded-lg bg-white p-4 shadow-sm"></div>
  </Page>
</template>
