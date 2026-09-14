import { reactive } from 'vue';

/**
 * 意图页面上下文注册中心。
 *
 * 业务页面在 setup 中调用 registerIntentPageContext 声明本页面的上下文来源
 * （如客户列表页返回当前选中/首个可见客户），全局意图面板打开时按当前路由
 * 取上下文——保证"打开业务页面即装载该页面的意图与数据"。
 *
 * 面板的开关与渲染状态由 SDK（intent-ui-sdk）内部维护，这里只负责
 * "当前页面能提供什么上下文"以及"谁来把抽屉打开"。
 */
export const intentPageState = reactive({
  /** 各页面注册的上下文提供者 */
  providers: new Map<string, () => Record<string, any>>(),
  /** 行级入口指定的页面（优先于当前路由） */
  overridePage: null as null | string,
});

/** 注册页面上下文提供者（页面标识 → 上下文对象） */
export function registerIntentPageContext(
  page: string,
  provider: () => Record<string, any>,
) {
  intentPageState.providers.set(page, provider);
}

/** 获取指定页面的上下文（未注册返回空对象，绝不抛错） */
export function getIntentPageContext(page: string): Record<string, any> {
  const provider = intentPageState.providers.get(page);
  try {
    return provider ? provider() : {};
  } catch {
    return {};
  }
}

/** 悬浮入口组件注册的"打开抽屉"动作（SDK 实例由它持有） */
let drawerOpener: (() => void) | null = null;

export function setIntentDrawerOpener(opener: (() => void) | null) {
  drawerOpener = opener;
}

/** 打开意图抽屉；传入 page 时强制按该页面装载（行级入口场景） */
export function openIntentDrawer(page?: string) {
  intentPageState.overridePage = page ?? null;
  drawerOpener?.();
}
