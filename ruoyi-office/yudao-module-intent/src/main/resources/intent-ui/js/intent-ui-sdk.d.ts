/**
 * Intent UI SDK v0.4.0 · 类型声明
 * 与 js/intent-ui-sdk.js 的对外 API 一一对应。
 */

/** 宿主自定义请求器：直接拿到 url / fetch options / 鉴权头，返回后端 { code, data } 信封或 data 本体 */
export type IntentFetcher = (
  url: string,
  options?: RequestInit,
  headers?: Record<string, string>,
) => Promise<unknown>;

/** 槽位实体下拉的候选值 */
export interface IntentEntityOption {
  label: string;
  value: string;
}

export interface IntentUiConfig {
  /** 后端意图接口前缀（含宿主的 API 网关前缀），默认 /intent */
  apiPrefix?: string;
  /** 与 basePath 拼接在 apiPrefix 之前（宿主部署在子路径时用） */
  basePath?: string;
  /** 静态 token；登录态会过期时改用 authHeaders */
  token?: null | string;
  /** 静态租户号；同上 */
  tenantId?: null | number | string;
  /** 每次请求动态取鉴权头（推荐，登录态刷新后自动生效） */
  authHeaders?: null | (() => Record<string, string>);
  /** 固定附加请求头 */
  headers?: null | Record<string, string>;
  /** 自定义请求器（走宿主 HTTP 客户端时注入） */
  fetcher?: IntentFetcher | null;
  /** 文案语言，内置 zh-CN / en-US */
  locale?: 'en-US' | 'zh-CN' | string;
  /** 覆盖内置文案（按 key 合并） */
  strings?: null | Record<string, string>;
  /** 主题标记（预留） */
  theme?: string;
  /** 层级基数：悬浮球 = z，遮罩 = z+1，抽屉 = z+2，弹层 = z+3 */
  zIndex?: null | number;
  /** 抽屉宽度（px），默认 920 */
  drawerWidth?: number;
  /** 待办分组内每页条数，默认 3 */
  todoPageSize?: number;
  /** 最多并列展示几个待办分组，默认 5 */
  maxTodoGroups?: number;
  /** 「当前对象快捷条」最多展示几个意图，默认 3（0 = 不展示） */
  quickIntents?: number;
  /** 槽位实体候选：{ customerId: [{label,value}] | () => 数组/Promise } */
  entityOptions?: null | Record<
    string,
    IntentEntityOption[] | (() => IntentEntityOption[] | Promise<IntentEntityOption[]>)
  >;
}

export interface IntentMountOptions {
  /** 面板容器（不传 = document.body） */
  container?: Element;
  /** 页面标识；不传 = 全量目录（调试台场景） */
  page?: string;
  /** 页面上下文（静态对象，或随页面数据变化的取值函数） */
  context?: Record<string, unknown>;
  /** 页面上下文的取值函数（优先于 context） */
  getContext?: () => Record<string, unknown>;
  /** 页面标识的取值函数（优先于 page） */
  getPage?: () => string;
  /** debug = 点击意图先弹槽位表单（调试台）；不传 = 缺参才弹表单 */
  mode?: 'debug' | 'embed';
  /** 目录装载完成后是否自动执行第一个意图 */
  autoRun?: boolean;
  /** 宿主事件回调：executed 等 */
  onEvent?: (type: string, payload: unknown) => void;
}

export interface IntentFloatingOptions extends IntentMountOptions {
  /** 悬浮球位置在 localStorage 里的 key，默认 intent-fab-position */
  storageKey?: string;
}

export interface IntentFloatingHandle {
  root: HTMLElement;
  fab: HTMLElement;
  open: () => void;
  close: () => void;
  isOpen: () => boolean;
  reload: () => Promise<void>;
  /** 清空运行态（切菜单/切页面时宿主调用；重开面板也会自动重置） */
  reset: () => void;
  /** 当前面板已装载的页面标识（宿主用它判断是否真的换了页面） */
  page: () => string;
  resetPosition: () => void;
  destroy: () => void;
}

export interface IntentMountHandle {
  root: HTMLElement;
  panelRef: unknown;
  reload: () => Promise<void>;
  render: () => void;
  /** 清空运行态（内嵌面板换页时宿主调用） */
  reset: () => void;
  destroy: () => void;
}

export interface IntentUiApi {
  version: string;
  configure: (options: IntentUiConfig) => IntentUiApi;
  config: () => IntentUiConfig;
  mount: (options: IntentMountOptions) => IntentMountHandle;
  mountFloating: (options: IntentFloatingOptions) => IntentFloatingHandle;
  /** 口语匹配（纯函数）：按 别名/名称/描述 与输入串的相似度排序，命中项带 hit（命中的短语） */
  match: (
    entries: Array<Record<string, any>>,
    query: string,
  ) => Array<{ entry: Record<string, any>; score: number; hit: string }>;
  t: (key: string, vars?: Record<string, unknown>) => string;
  strings: Record<string, Record<string, string>>;
}

export declare const IntentUI: IntentUiApi;
export default IntentUI;

declare global {
  interface Window {
    IntentUI: IntentUiApi;
  }
}
