<script lang="ts" setup>
/**
 * 全局 AI 浮标 + 意图抽屉 —— Vben 宿主适配器。
 *
 * 面板本身完全由 SDK 渲染（intent-ui-sdk 是唯一实现），本组件只做三件宿主专属的事：
 *   1. 注入宿主登录态与实体下拉数据源（configureIntentUi，见 ../sdk.ts）；
 *   2. 把当前路由与页面上下文接进 SDK（getPage / getContext）；
 *   3. 生命周期收尾（销毁实例、路由切换清理页面覆盖）。
 *
 * 页面标识 = 当前路由路径（去掉开头斜杠），与 IntentSpec.pages 匹配；
 * 未配置意图的页面显示引导提示。浮标是意图服务的唯一入口，可拖到任意位置（位置记在 localStorage）。
 */
import { onBeforeUnmount, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';

import {
  getIntentPageContext,
  intentPageState,
  setIntentDrawerOpener,
} from '../pageContext';
import { configureIntentUi } from '../sdk';

const route = useRoute();

let floating:
  | undefined
  | { destroy: () => void; open: () => void; reload: () => void };

/** 页面标识：宿主显式指定（overridePage）优先，否则取当前路由路径（去开头斜杠） */
function currentPage() {
  if (intentPageState.overridePage) return intentPageState.overridePage;
  return route.path.replace(/^\//, '');
}

/**
 * 详情页上下文兜底：路由形如 crm/customer/detail/320 时，
 * 直接把路由参数映射成该对象的槽位（无需每个详情页都写一遍注册代码）。
 */
const DETAIL_PAGE_KEYS: Array<[string, string]> = [
  ['crm/clue/detail/', 'clueId'],
  ['crm/customer/detail/', 'customerId'],
  ['crm/contact/detail/', 'contactId'],
  ['crm/business/detail/', 'businessId'],
  ['crm/contract/detail/', 'contractId'],
  ['crm/receivable-plan/detail/', 'planId'],
  ['crm/receivable/detail/', 'receivableId'],
  ['crm/product/detail/', 'productId'],
];

function detailContext(page: string): Record<string, any> {
  const routeId = (route.params as Record<string, any>)?.id;
  if (!routeId) return {};
  for (const [prefix, key] of DETAIL_PAGE_KEYS) {
    if (page.startsWith(prefix)) return { [key]: String(routeId) };
  }
  return {};
}

/** 已注册的页面上下文优先，未注册的详情页走路由参数兜底 */
function currentContext() {
  const page = currentPage();
  const registered = getIntentPageContext(page);
  if (registered && Object.keys(registered).length > 0) return registered;
  return detailContext(page);
}

onMounted(() => {
  floating = configureIntentUi().mountFloating({
    getPage: currentPage,
    getContext: currentContext,
  });
  setIntentDrawerOpener(() => floating?.open());
});

// 路由切换：清除宿主指定的页面覆盖（行级入口用完即弃），并重载面板
// —— SDK 在 load() 里识别到页码变化会清空上一页的执行结果，避免旧结果挂在新页面上
watch(
  () => route.path,
  () => {
    intentPageState.overridePage = null;
    floating?.reload();
  },
);

onBeforeUnmount(() => {
  setIntentDrawerOpener(null);
  floating?.destroy();
  floating = undefined;
});
</script>

<template>
  <!-- 悬浮球与抽屉由 SDK 挂载到 body，这里不需要 DOM -->
</template>
