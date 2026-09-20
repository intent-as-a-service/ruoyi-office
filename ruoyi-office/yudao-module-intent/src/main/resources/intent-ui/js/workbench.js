/**
 * 意图工作台 · 宿主页面实现（零构建，ES 模块）
 * ============================================================================
 * 六块 + 右栏：
 *   ⓪ 上下文栈  ① 今天先做  ② 我该办的  ③ 我常做的  ④ 批量摸底  ⑤ 做过的 | Run 执行流
 *
 * 三条实现原则（与设计方案、技术方案对齐）：
 *   1. **谁能看见由服务端说了算**：本文件不做任何权限判断，只渲染 /intent/* 的返回；
 *      视角切换只是把一个 view id 传给服务端，收窄逻辑在 WorkbenchProfileRegistry 里；
 *   2. **本地只算"能不能发起"**：`satisfiability()` 是 (entry.context × slots) 的纯函数，
 *      不为"加个槽位"去请求后端——那会让每次点选都卡一下；
 *   3. **不假装**：没有 SSE 就没有阶段流，只有"执行中 + 已用时"；
 *      没有逐条待办接口就不展开假的实体列表，聚合建议只给"对全部 N 条办理"。
 */

import {
  MODE_SINGLE, MODE_SET, SOURCE_PINNED, SOURCE_CARRIED, SOURCE_INFERRED,
  satisfiability, hasValue, slotTypeOf, TYPE_UNKNOWN,
} from './slots.mjs';
import {
  DEFAULT_PICK_LIMIT, PICKER_CUSTOM, PICKER_LITERAL, PICKER_MANUAL, PICKER_REMOTE,
  normalizePickResult, pickContextOf, pickerHintOf, pickerKindOf, resolvePicker,
} from './picker.mjs';
import {
  errorCopy, runStatusCopy, costBeforeCopy, formatDuration, formatTokens,
  EXECUTOR_LABEL,
} from './copy.zh-CN.js';

// ---------------------------------------------------------------- 常量与状态

/**
 * 宿主配置。**每一项都有默认值，零配置即可跑**（《意图工作台-四点优化设计》§1.3 原则 1）。
 *
 * 宿主在加载本模块之前设置 `window.__INTENT_WORKBENCH__` 覆盖：
 *   <script>window.__INTENT_WORKBENCH__ = { tenantId: '7', apiPrefix: '/admin-api/intent' }</script>
 *
 * 为什么要有它：`tenant-id` 以前是写死的 '1'，换任何一个多租户宿主都会静默错租户——
 * 这是"换系统集成难"里最硬的一条，必须由宿主编译期之外就能改。
 */
const DEFAULTS = {
  /** 意图接口前缀（含宿主的 API 网关前缀） */
  apiPrefix: '/admin-api/intent',
  /** 宿主登录接口前缀（只在工作台自带登录页时用到） */
  authApiPrefix: '/admin-api/system/auth',
  /** 租户号。宿主多租户过滤器在鉴权之前就要它 */
  tenantId: '1',
  /** 读取登录态的两个 localStorage key */
  tokenKeys: { access: 'ACCESS_TOKEN', workbench: 'intent.workbench.token' },
  /** 批量摸底一次最多几个对象：串行跑，成本要可控 */
  maxBatch: 8,
  /** 历史 Run 一次取几条 */
  maxRuns: 20,
  /** 目录刷新防抖毫秒数（连改多个槽位只打一次） */
  catalogDebounceMs: 250,
  /** 槽位候选的具名选择器（见 picker 契约） */
  pickers: null,
  /**
   * 自定义请求器：宿主想用自己的 HTTP 客户端（带统一拦截器 / 重试 / 埋点）时注入。
   * 签名 `(path, { method, body, headers }) => Promise<data | {code, msg, data}>`——
   * 返回信封或 data 本体都吃。**不注入就用 fetch**（零配置可用）。
   */
  fetcher: null,
  /** 埋点出口：宿主接自己的统计 */
  onEvent: null,
};

/**
 * 生效配置。`mountWorkbench(options)` 会在挂载时把宿主给的项合并进来，
 * 所以这里是可变的（派生常量也用 `let`，挂载时重新绑定一次）。
 */
const CONFIG = {
  ...DEFAULTS,
  ...(globalThis.__INTENT_WORKBENCH__ || {}),
  tokenKeys: { ...DEFAULTS.tokenKeys, ...((globalThis.__INTENT_WORKBENCH__ || {}).tokenKeys || {}) },
};

// 下面这些是从 CONFIG 派生的便捷常量。之所以是 `let`：工作台要能被宿主在
// **运行时**挂载（`mountWorkbench({ apiPrefix, tenantId })`），挂载时重新绑定。
let API = CONFIG.apiPrefix;
let AUTH_API = CONFIG.authApiPrefix;
let TOKEN_KEY = CONFIG.tokenKeys.access;
let WORKBENCH_TOKEN_KEY = CONFIG.tokenKeys.workbench;
let TENANT_ID = String(CONFIG.tenantId);
let MAX_BATCH = CONFIG.maxBatch;
let MAX_RUNS = CONFIG.maxRuns;
let CATALOG_DEBOUNCE_MS = CONFIG.catalogDebounceMs;

/** 埋点：宿主没接就什么都不做（不发假数据，也不报错）。 */
function track(type, payload) {
  try { CONFIG.onEvent?.(type, payload); } catch { /* 埋点失败绝不影响主流程 */ }
}

const state = {
  token: null,
  me: null,
  slots: [],
  view: 'self',
  entries: [],
  suggestions: [],
  issues: [],
  runs: [],
  current: null,  // 右栏 runbar 现在对准哪条意图
  session: [],    // 本次打开页面后自己发起的 Run（含批量里的每一跑），新的在前
  right: 'runs',  // runs | explore
  focus: null,    // 正在看的 Run（回看历史时）
  tick: null,
  explore: null,
  openTodos: null,
  pickType: null,
  pickState: null,     // 选择器运行态：{keyword, cursor, items, more, picked, degraded}
  paletteKeyword: '',
  palGroupOpen: {},    // 命令面板：分组折叠状态（分组 key → 是否展开）
  navBlock: null,      // 左栏导航面板：当前选中哪一块（null = 用档案里的第一块）
  graphLiveOnly: false, // 网络：只留"前瞻"边（焦点最近一次运行给出的下一步）
  graphDrawer: false,   // 网络右侧抽屉是否展开
  runningIntent: null,  // 正在跑的意图 id（只用于动画：呼吸状态，**不表示进度百分比**）
  ctxOpen: false,      // 上下文是否展开（默认收起成一行，见首屏再设计 §2 建议二）
  runOpen: {},         // 右栏 Run 分组：意图 id → 是否展开
  detailOpen: {},      // 结果卡的「查看详情」：Run key → 是否展开（默认收起）
  failedRunsOpen: false, // 右栏"没跑成"分组是否展开
  hiddenRuns: [],      // 被用户手动清理掉的失败 runId（只影响本页显示，不动服务端留痕）
  ctxDelta: null,      // 最近一次"槽位变更 → 能力增减"，用于把因果摆在用户眼前
  batchGroupOpen: {},  // 批量摸底：意图多选的分组折叠
};

const $ = (id) => document.getElementById(id);
const esc = (value) => String(value ?? '').replace(/[&<>"']/g, (c) => (
  { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));

// ---------------------------------------------------------------- 网络

async function api(path, { method = 'GET', body, auth = true } = {}) {
  const headers = {};
  if (body !== undefined) headers['Content-Type'] = 'application/json';
  if (auth && state.token) headers.Authorization = `Bearer ${state.token}`;
  // 租户头与登录无关：宿主的多租户过滤器在鉴权之前就要它，登录请求也得带
  headers['tenant-id'] = TENANT_ID;

  // 宿主自带请求器：交给他全权处理（鉴权、拦截、重试都是他的事），
  // 我们只认返回的信封或 data 本体。这条路径让"换宿主"不必改这一层的任何代码。
  if (typeof CONFIG.fetcher === 'function') {
    const raw = await CONFIG.fetcher(path, { method, body, headers });
    if (raw && typeof raw === 'object' && 'code' in raw) {
      if (raw.code !== 0) {
        const error = new Error(raw.msg || `调用失败（${raw.code}）`);
        error.code = raw.code;
        if (raw.code === 401) error.unauthorized = true;
        throw error;
      }
      return raw.data;
    }
    return raw;
  }

  const res = await fetch(path, {
    method, headers, credentials: 'include',
    body: body === undefined ? undefined : JSON.stringify(body),
  });
  if (res.status === 404) {
    const err = new Error('NOT_FOUND');
    err.status = 404;
    throw err;
  }
  const payload = await res.json().catch(() => null);
  if (!payload) {
    const err = new Error(`HTTP ${res.status}`);
    err.status = res.status;
    throw err;
  }
  if (payload.code !== 0) {
    const err = new Error(payload.msg || `调用失败（${payload.code}）`);
    err.code = payload.code;
    err.status = res.status;
    if (payload.code === 401) err.unauthorized = true;
    throw err;
  }
  return payload.data;
}

// ---------------------------------------------------------------- 槽位（上下文栈）

/** 服务端槽位登记表 → {key: type}，给 slots.mjs 的推导用。 */
function registryOf(me) {
  const registry = {};
  for (const item of me?.slotTypes ?? []) registry[item.key] = item.type;
  return registry;
}

function slotTypeMeta(type) {
  return (state.me?.slotTypes ?? []).find((item) => item.type === type) ?? null;
}

/**
 * **槽位的唯一构造入口**：任何写进 `state.slots` 的槽位都必须先过这里。
 *
 * 为什么必须收敛到一处：浏览器回归历史上修过三次同一类缺陷——
 * `applyInitialSlots()` 把没值的槽位渲染成「客户 = 客户」、`resolveSlots()` 对没值的槽位做失效校验、
 * `slotRow()` 漏判 `hasValue()`。三处的根因是"每个渲染点各自判断有没有值"。
 *
 * 这里做的两件防御：
 *   1. `values` 恒为数组、空值被剔干净（渲染层不会拿到 undefined）；
 *   2. **没值时不写 label**——`label` 回落到空串，于是即使某个渲染点忘了判 `hasValue()`，
 *      也只会渲染出一个空的展示位，绝不会把"客户"这个名字冒充成"值"。
 */
function normalizeSlot(type, values, { source = SOURCE_INFERRED, label = null, mode = null } = {}) {
  const meta = slotTypeMeta(type);
  const list = (Array.isArray(values) ? values : [values])
    .filter((v) => v != null)
    // 必须 trim：手输编号那条路径直接把输入框的原值塞进来，前后空格会让
    // `customerId = " 320 "` 与 `321` 都被后端当成不同实体
    .map((v) => String(v).trim())
    .filter((v) => v !== '');
  const filled = list.length > 0;
  return {
    type,
    key: meta?.key || type,
    title: meta?.title || type,
    // 没值 → label 为空。这是"没值就是没值"的代码级兜底，不再靠每个渲染点自觉。
    label: filled ? (label ?? meta?.title ?? list[0]) : '',
    values: list,
    mode: mode ?? (list.length > 1 ? MODE_SET : MODE_SINGLE),
    source,
    state: 'ok',
  };
}

/** 把一个实体放进上下文栈（同类型只保留一个：一个类型在栈里只出现一次）。 */
function putSlot(type, values, source = SOURCE_PINNED, label = null) {
  const meta = slotTypeMeta(type);
  if (!meta) return;
  const next = normalizeSlot(type, values, { source, label });
  if (!next.values.length) return;   // putSlot 的语义是"放一个**有值**的实体"，空值交给 clear
  const before = readyCount();
  state.slots = [...state.slots.filter((slot) => slot.type !== type), next];
  syncExploreSlot();
  noteSlotDelta(type, before, 'add');
  refreshCatalog();
  resolveSlots();
}

function removeSlot(type) {
  const before = readyCount();
  state.slots = state.slots.filter((slot) => slot.type !== type);
  syncExploreSlot();
  noteSlotDelta(type, before, 'remove');
  refreshCatalog();
}

/* ---- 槽位变更的因果可见性 ---- */

let ctxDeltaTimer = null;

/** 当前上下文下"能直接跑"的意图数。它是"上下文真的在驱动界面"的唯一可观测口径。 */
function readyCount() {
  const registry = registryOf(state.me ?? {});
  return state.entries.filter((entry) => satisfiability(entry, state.slots, registry).state === 'ready').length;
}

/**
 * 记一次"槽位变更 → 能力增减"，把因果摆在用户眼前。
 *
 * 为什么必须有：改上下文是工作台最核心的机制，但改完之后界面**不会告诉用户发生了什么**——
 * 加了合同，那几条合同类意图由暗转亮，全凭用户自己发现；用户发现不了，就会觉得
 * "上下文这一层没什么用"（设计稿 §11 把 `ctx_driven_intent_change` 定成 ≥80% 的指标）。
 *
 * 三条克制要求：不弹 toast、不循环动画、8 秒后自己消失。
 * 槽位变更是**纯函数瞬时完成**的，界面就该表现为瞬时——做成"正在重新计算…"反而是假过程。
 */
function noteSlotDelta(type, before, action) {
  const delta = readyCount() - before;
  if (!delta) return;
  const meta = slotTypeMeta(type);
  state.ctxDelta = { type, title: meta?.title || type, delta, action };
  track('ctx_driven_intent_change', { type, delta, action });
  clearTimeout(ctxDeltaTimer);
  ctxDeltaTimer = setTimeout(() => { state.ctxDelta = null; renderLeft(); }, 8000);
}

/** 钉住 / 取消钉住：钉住的槽位在切视角时保留。 */
function togglePin(type) {
  const slot = state.slots.find((item) => item.type === type);
  if (!slot) return;
  slot.source = slot.source === SOURCE_PINNED ? SOURCE_CARRIED : SOURCE_PINNED;
  refreshCatalog();
}

/** 槽位值 → 意图参数：槽位的 key 就是 IntentSpec.paramsSchema 里的参数名。 */
function paramsFromSlots() {
  const params = {};
  for (const slot of state.slots) {
    if (!hasValue(slot)) continue;
    params[slot.key] = slot.mode === MODE_SET ? slot.values : slot.values[0];
  }
  return params;
}

/** 执行时的上下文快照：后端原样落痕，用于回看"这一跑是在什么条件下得出的"。 */
function contextSnapshot() {
  return {
    _slots: state.slots.map((slot) => ({
      type: slot.type, key: slot.key, label: slot.label,
      values: slot.values, mode: slot.mode, source: slot.source,
    })),
  };
}

/** 槽位失效校验：实体删了 / 超出我的数据范围，标红并给"清除"。 */
async function resolveSlots() {
  // 只校验**实体型**槽位：时间窗这类字面量槽位没有"还在不在"的问题，
  // 拿它去反查只会换来一个"未校验"，把噪声当成了信息。
  const targets = state.slots.filter((slot) => hasValue(slot) && slotTypeMeta(slot.type)?.entity);
  if (!targets.length) {
    for (const slot of state.slots) slot.state = 'ok';
    renderLeft();
    return;
  }
  const refs = targets.map((slot) => ({ type: slot.type, id: slot.values[0] }));
  try {
    const resolved = await api(`${API}/slots/resolve`, { method: 'POST', body: refs });
    for (const slot of targets) {
      const hit = resolved?.[slot.type];
      if (!hit) { slot.state = 'unknown'; continue; }
      slot.state = hit.accessible ? 'ok' : 'bad';
      if (hit.name) {
        slot.label = hit.name;
        // 顺手把名字存进对象名缓存：建议按对象聚合时可以直接用，不必再解析标题文本
        objectNames.set(`${slot.type}:${slot.values[0]}`, hit.name);
      }
    }
  } catch (e) {
    for (const slot of targets) slot.state = 'unknown';
  }
  renderLeft();
}
// ---------------------------------------------------------------- 渲染：左栏
//
// 左栏的视觉语法是**三级**（《意图工作台-四点优化设计》§2.3）：
//
//   L1 主角 —— 一屏只有一个：**对「谁」做「什么」**，右边是整页唯一的实心主按钮
//   L2 队列 —— 今天还要做的 3 条：风险色条 + 对象当主语 + 动作用描边按钮
//   L3 背景 —— 我该办的 / 我常做的 / 批量摸底 / 做过的，默认折叠成一行
//
// 为什么必须分三级：改版前六块结构完全一样（`.sec-hd` + `.sec-bd`）、每行语法也一样
// （色点 + 标题 + 灰字 + 执行），信息量不少但**全部同权重**——用户不是看不到东西，
// 是看不到"先看哪一行"。
//
// 分层**不等于砍数据**（设计稿 §10.5：要砍的是"平铺"，不是"数量"）：
// L3 里一条都不少，只是默认收起。

/** 区块顺序由档案决定（profile.blocks），前端不硬编码顺序。 */
function blocksOf() {
  return state.me?.profile?.blocks ?? ['context', 'today', 'todos', 'frequent', 'history'];
}

function viewLabelOf(viewId) {
  if (!viewId || viewId === 'self') return '我自己';
  const hit = (state.me?.views ?? []).find((item) => item.id === viewId);
  return hit ? hit.label : viewId;
}

function renderLeft() {
  // 上下文通栏横跨整页，由这里统一刷新（所有会改上下文的路径最后都会调 renderLeft）
  renderContextBar();
  if (!state.me) return;
  const blocks = blocksOf();
  // 左栏四段，从上到下：焦点头 → 主角卡 → 导航瓦片 → 选中块的正文。
  //
  // 瓦片**不进滚动区**：它是导航，滚动时应该一直在视野里；只有正文滚。
  // 上下文已经搬到顶部通栏，不再占这里的位置。
  $('left').innerHTML = focusHeader() + issuesBanner()
    + focusBarHtml()
    + heroCard()
    + navTiles(blocks)
    + `<div class="left-scroll">${navBlockPanel()}</div>`;
}

function focusHeader() {
  const profile = state.me.profile ?? {};
  return `<div class="focus">
    <div class="fh-l">
      <div class="fname">${esc(profile.title || '意图工作台')}</div>
      <div class="fmeta">${esc(profile.label || '通用')} · ${esc(viewLabelOf(state.view))} · ${state.entries.length} 条能力可用</div>
    </div>
    <div class="switch" data-act="open-palette">全部能力</div>
  </div>`;
}

/* ---------- 风险等级（severity） ---------- */

/**
 * 一条建议的风险等级：`danger` 要救 / `warning` 要看 / `info` 可等。
 *
 * ⚠️ **这是前端降级推导，不是真的 severity。**
 * `IntentSuggestion` 协议里根本没有 severity 字段（只有 id/intentId/title/subtitle/
 * kind/count/params/reason/dedupKey），所以 20 条建议在界面上只能是同一个颜色——
 * 这正是"一眼看不出重点"的直接原因。
 *
 * 在协议补字段之前（见优化设计 Q-F：先前端降级映射、W1 再进 SDK），这里按**已有事实**推：
 *   1. 文本事实优先：reason / subtitle / title 里出现"逾期、停滞、流失、预警…"→ 红；
 *      出现"即将、临近、待审…"→ 黄；
 *   2. 数量兜底：一条建议背后挂着 5 条以上待办，至少是"要看"。
 * 宁可保守（判不出就给 info），也不为了好看去编一个风险等级。
 */
function severityOf(item) {
  const text = `${item.title ?? ''} ${item.subtitle ?? ''} ${item.reason ?? ''}`;
  if (/逾期|超期|过期|停滞|流失|风险|预警|未联系|未跟进|超时/.test(text)) return 'danger';
  if (/即将|临近|到期|待审|待批|应|需|今天/.test(text)) return 'warning';
  if ((item.count ?? 0) >= 5) return 'warning';
  return 'info';
}

const SEVERITY_RANK = { danger: 0, warning: 1, info: 2 };

/** 建议按"该先看哪条"排序：风险 → 条数 → 原序（稳定，不改后端的语义排序）。 */
function sortedSuggestions() {
  return itemsOf('item')
    .map((item, index) => ({ item, index, sev: severityOf(item) }))
    .sort((a, b) => (SEVERITY_RANK[a.sev] - SEVERITY_RANK[b.sev])
      || ((b.item.count ?? 0) - (a.item.count ?? 0))
      || (a.index - b.index))
    .map((row) => row.item);
}

/* ---------- 对象与金额：让它们成为一等公民 ---------- *
 *
 * ⚠️ 这一段里有若干**从标题/副标题里解析**出来的字段（对象名、金额、逾期天数、失联天数）。
 * 这是**临时手段**：`IntentSuggestion` 协议只有
 * `id / intentId / title / subtitle / kind / count / params / reason / dedupKey`，
 * 没有结构化的"对象引用"与"金额"，所以 UI 只能按宿主自己的模板形状去取。
 *
 * 三条自律：
 *   1. 模板形状对不上就**不显示**（返回 null / 空串），绝不猜、绝不编；
 *   2. 解析全部集中在 `parseItemFacts()`，SDK 补上结构化字段后整段可删；
 *   3. 只认宿主自己的模板（`客户「X」…`、`应收 N 元`、`已逾期 N 天`、`上次联系 YYYY-MM-DD`）。
 *
 * 真正的修法（已列入待办）：给 `IntentSuggestion` 加 `objectType / objectId / objectLabel`，
 * 并且让规则模板能直接声明"这一条挂在哪个对象上"。
 */

/** 对象显示名缓存（`type:id` → 名称）。由 `resolveSlots()` 在槽位校验时顺手填充，免费。 */
const objectNames = new Map();

/** 建议参数 → 对象引用。槽位的 key 就是参数名，所以直接用登记表反查类型。 */
function objectRefOf(item) {
  const registry = registryOf(state.me ?? {});
  for (const key of Object.keys(item?.params ?? {})) {
    const value = item.params[key];
    if (value == null || value === '') continue;
    const type = slotTypeOf(key, registry);
    if (type === TYPE_UNKNOWN || !slotTypeMeta(type)?.entity) continue;
    return { type, id: String(Array.isArray(value) ? value[0] : value) };
  }
  return null;
}

/** 标题里「括号」中的对象名。取不到返回空串——不猜。 */
function objectNameFromTitle(item) {
  const hit = /[「『]([^」』]{1,60})[」』]/.exec(item?.title ?? '');
  return hit ? hit[1].trim() : '';
}

/** 建议 → 可比较的事实。取不到的一律为 null，调用方必须容忍空值。 */
function parseItemFacts(item) {
  const title = item?.title ?? '';
  const subtitle = item?.subtitle ?? '';
  const text = `${title} ${subtitle} ${item?.reason ?? ''}`;
  const money = /([\d,]+(?:\.\d+)?)\s*元/.exec(subtitle || title);
  const overdue = /逾期\s*([\d,]+)\s*天/.exec(text);
  const contact = /上次联系\s*(\d{4}-\d{2}-\d{2})/.exec(text);
  return {
    objectName: objectNameFromTitle(item),
    amount: money ? Number(money[1].replace(/,/g, '')) : null,
    overdueDays: overdue ? Number(overdue[1].replace(/,/g, '')) : null,
    daysSinceContact: contact ? daysBetweenDates(contact[1]) : null,
  };
}

function daysBetweenDates(text) {
  const then = new Date(`${text}T00:00:00`);
  if (Number.isNaN(then.getTime())) return null;
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return Math.max(0, Math.round((today - then) / 86400000));
}

/** 金额展示：≥ 1 万走「万」，否则原样。 */
function formatMoney(value) {
  if (value == null) return '';
  return value >= 10000 ? `${(value / 10000).toFixed(2)} 万` : `${value.toFixed(0)} 元`;
}

/**
 * 按对象把建议聚合成"一件事"。
 *
 * **为什么必须聚合**：待办来自「事实规则 × 数据集」，一条规则命中 N 条记录就产生 N 个信号；
 * 但**用户的动作单位是"打给谁"**，不是"第几期"。
 * 实测：18 条信号只落在 **16 个对象**上——客户 326 挂着 2 期回款逾期（15.52 万 + 3.6 万），
 * 客户 320 挂着"逾期 1.2 万 + 待跟进"两条来自不同意图的信号。
 * 聚合本身只省 2 行，真正的价值是：**一次沟通能解决的事被摆在一起**，
 * 以及"这个客户一共欠多少"能算出来。
 */
function aggregateSuggestions(items) {
  const groups = new Map();
  const solo = [];
  for (const item of items) {
    const ref = objectRefOf(item);
    if (!ref) { solo.push({ key: `solo:${item.id}`, ref: null, items: [item] }); continue; }
    const key = `${ref.type}:${ref.id}`;
    if (!groups.has(key)) groups.set(key, { key, ref, items: [] });
    groups.get(key).items.push(item);
  }
  const list = [...groups.values(), ...solo];
  for (const group of list) enrichGroup(group);
  return list;
}

/** 给一组算出：对象名、最严重等级、金额、逾期/失联天数、涉及几条意图。 */
function enrichGroup(group) {
  const facts = group.items.map((item) => ({ item, fact: parseItemFacts(item) }));
  group.facts = facts;
  group.intentIds = [...new Set(group.items.map((item) => item.intentId).filter(Boolean))];
  group.title = facts.find((row) => row.fact.objectName)?.fact.objectName
    || objectNames.get(group.key) || '';
  // 金额只在**同一条意图内**求和：跨意图求和会把"合同金额"和"应收金额"加到一起，那是错的。
  // 同时记住这笔钱是哪条意图贡献的——队列尾部的合计要按意图分别算，并且要点名口径。
  const byIntent = new Map();
  for (const { item, fact } of facts) {
    if (fact.amount == null) continue;
    byIntent.set(item.intentId, (byIntent.get(item.intentId) ?? 0) + fact.amount);
  }
  const ranked = [...byIntent.entries()].sort((a, b) => b[1] - a[1]);
  group.amount = ranked.length ? ranked[0][1] : null;
  group.amountIntentId = ranked.length ? ranked[0][0] : null;
  group.amountShared = ranked.length > 1;
  const overdue = facts.map((row) => row.fact.overdueDays).filter((n) => n != null);
  group.overdueDays = overdue.length ? Math.max(...overdue) : null;
  const contacts = facts.map((row) => row.fact.daysSinceContact).filter((n) => n != null);
  group.daysSinceContact = contacts.length ? Math.max(...contacts) : null;
  group.itemSeverity = group.items.map(severityOf)
    .sort((a, b) => SEVERITY_RANK[a] - SEVERITY_RANK[b])[0] ?? 'info';
  group.severity = severityOfGroup(group);
}

/**
 * 聚合后的风险等级。
 * 不能只看文本：11 万的逾期哪怕文案里没有"逾期"两个字，也必须是要救的那一档。
 */
function severityOfGroup(group) {
  if (group.itemSeverity === 'danger') return 'danger';
  if ((group.amount ?? 0) >= 100000) return 'danger';
  if (group.items.length > 1) return 'warning';
  return group.itemSeverity;
}

/** 组的显示名：对象名拿不到时退化成「类型 #id」，不冒充、不留空。 */
function groupLabelOf(group) {
  if (group.title) return group.title;
  if (group.ref) return `${slotTypeMeta(group.ref.type)?.title || group.ref.type} #${group.ref.id}`;
  return group.items[0]?.title ?? '';
}

/** 一键办理这一组要跑哪条意图：取组里第一条可执行的建议。 */
function primaryItemOf(group) {
  // 防御性判空：`aggregates()` 在目录回来之前是空的，调用方不一定都记得先判
  if (!group?.items?.length) return null;
  return group.items.find((item) => item.kind === 'item') ?? group.items[0];
}

/* ---------- L1 主角 ---------- */

/** 当前上下文里第一个"有值的实体槽位"——它就是这句话的主语。 */
function focusSlot() {
  return state.slots.find((slot) => hasValue(slot) && slotTypeMeta(slot.type)?.entity) ?? null;
}

/**
 * 「我常做的」的排序结果（按个人执行频次，再看档案钉选）。
 * 抽出来是因为主角卡和 L3 区块都要用它，避免两处各排一遍导致顺序不一致。
 */
function rankedFrequent() {
  const pinned = state.me?.profile?.pinnedIntents ?? [];
  const byId = new Map(state.entries.map((entry) => [entry.id, entry]));
  const picked = [];
  for (const id of pinned) {
    const entry = byId.get(id);
    if (entry && !picked.includes(entry)) picked.push(entry);
  }
  const rest = state.entries.filter((entry) => !picked.includes(entry))
    // 先按"当前对象类型上的频次"，同分再看全局频次（有焦点对象时两者的量纲不同，不能混用）
    .sort((a, b) => (frequencyOf(b.id) - frequencyOf(a.id))
      || (globalFrequencyOf(b.id) - globalFrequencyOf(a.id)));
  for (const entry of rest) {
    if (picked.length >= 5) break;
    picked.push(entry);
  }
  return picked;
}

/**
 * 排序并聚合后的"事"。渲染一屏会问它很多次，所以按建议 id 列表做个校验和缓存。
 *
 * 排序口径（首屏再设计 §2 建议一）：**风险 → 金额 → 逾期天数 → 失联天数**。
 * 为什么金额这么靠前：业务人员扫的是钱。"逾期 8 天 7 万"和"逾期 2 天 1.2 万"，
 * 该先打哪个电话，答案在金额里，不在天数里。
 */
let aggregateCache = null;        // null = 还没算过（哨兵，不能用 ''：没有建议时算出来的 key 也是 ''）
let aggregateCacheKey = null;

function aggregates() {
  const items = sortedSuggestions();
  const key = items.map((item) => item.id).join('|');
  if (aggregateCache === null || aggregateCacheKey !== key) {
    aggregateCache = aggregateSuggestions(items).sort((a, b) =>
      (SEVERITY_RANK[a.severity] - SEVERITY_RANK[b.severity])
      || ((b.amount ?? 0) - (a.amount ?? 0))
      || ((b.overdueDays ?? 0) - (a.overdueDays ?? 0))
      || ((b.daysSinceContact ?? 0) - (a.daysSinceContact ?? 0)));
    aggregateCacheKey = key;
  }
  return aggregateCache;
}

/**
 * "下一步做什么"——主角卡的唯一候选。
 *
 * 优先级：**聚合后最该处理的那一件事** > 我常做的里当前就能跑的一条。
 * 第二条退路不能省：没有待办时首屏若只剩一句"今天没有排到前面的事"，
 * 工作台第一眼就是空的——"打开即有内容"是它能不能活下来的前提（设计稿 §5.2）。
 *
 * 三件事必须都从数据里来，不许编：
 *   主语 = 对象名（聚合组的对象，拿不到就退化成「类型 #id」）；
 *   谓语 = **意图名**（宿主里它们本来就是动词短语：逾期风险预警 / 客户健康度诊断）；
 *   依据 = 金额 / 逾期天数 / 失联天数（解析不到就不显示那一段）。
 */
function topActionable() {
  const group = aggregates()[0];
  if (group) {
    const item = primaryItemOf(group);
    const why = [];
    if (group.amount != null) why.push(`${formatMoney(group.amount)}${group.amountShared ? '（单笔）' : ''}`);
    if (group.overdueDays != null) why.push(`逾期 ${group.overdueDays} 天`);
    if (group.daysSinceContact != null) why.push(`${group.daysSinceContact} 天未联系`);
    if (group.items.length > 1) why.push(`共 ${group.items.length} 件`);
    return {
      kind: 'suggestion',
      id: item.id,
      subject: groupLabelOf(group),
      subjectType: group.ref?.type ?? null,
      subjectId: group.ref?.id ?? null,
      action: entryOf(item.intentId)?.name || item.title,
      why: why.join(' · ') || [item.subtitle, item.reason].filter(Boolean).join(' · '),
      sev: group.severity,
    };
  }
  const registry = registryOf(state.me);
  const ready = rankedFrequent().find((entry) => satisfiability(entry, state.slots, registry).state === 'ready');
  if (ready) {
    const slot = focusSlot();
    return {
      kind: 'intent', id: ready.id,
      subject: slot ? slot.label : '',
      subjectType: slot ? slot.type : null,
      subjectId: slot ? slot.values[0] : null,
      action: ready.name,
      why: frequencyOf(ready.id) ? `你跑过 ${frequencyOf(ready.id)} 次，现在就能跑` : '现在就能跑',
      sev: 'info',
    };
  }
  return null;
}

/**
 * L1 主角卡。
 *
 * 它回答的是业务人员打开工作台的前两问——"有没有火烧眉毛的？我现在干哪件？"。
 * 句子形态是 `给 [对象] 做 [意图]`：
 *   · 主语是**对象**，而且是从数据里自动带出来的——不再让用户在主语位置"先选一个对象"；
 *   · 谓语是**意图名**（动词短语），不再是"第 1 期回款已逾期 23 天"这种事实陈述；
 *   · 事实（金额 / 逾期 / 失联）降为第二行"为什么是现在"。
 * 这样"从哪下手"有唯一答案，也不必再解释"上下文有什么用"——它就在句子主语的位置上。
 */
function heroCard() {
  const best = topActionable();
  if (!best) {
    return `<div class="hero dim">
      <div class="hero-line">还没有可办的事</div>
      <div class="hero-empty">
        <span>摆一个对象到上下文里，或者从能力目录挑一件事。</span>
        <span class="lnk" data-act="open-palette">打开全部能力 ▸</span>
      </div>
    </div>`;
  }
  const subject = best.subject
    ? `<span class="subj"${best.subjectType ? ` data-act="add-entity" data-type="${esc(best.subjectType)}" title="换一个"` : ''}>${esc(best.subject)}<span class="caret">▾</span></span>`
    : `<span class="subj empty" data-act="add-slot" title="上下文是空的，先说清对谁办">先选一个对象 ＋</span>`;
  return `<div class="hero sev-${esc(best.sev)}">
    <div class="hero-line">给 ${subject} 做</div>
    <div class="hero-main">
      <div class="hero-act">${esc(best.action)}</div>
      ${best.why ? `<div class="hero-why">${esc(best.why)}</div>` : ''}
    </div>
    <div class="hero-btns">
      <span class="btn pri lg" data-act="${best.kind === 'suggestion' ? 'run-suggestion' : 'run-intent'}"
        data-id="${esc(best.id)}">执行</span>
      <span class="btn ghost lg" data-act="open-palette">换一件事</span>
      ${subjectAdoptChip(best)}
    </div>
  </div>`;
}

/* ---------- L2 队列 ---------- */

const QUEUE_VISIBLE = 4;

/** 一个对象下面的"第 N 件事"：每条有自己的执行按钮，因为一次执行只能跑一条意图。 */
function queueSubRow(item) {
  return `<div class="q-subrow">
    <span class="q-subt">${esc(item.title)}</span>
    <span class="q-go sm" data-act="run-suggestion" data-id="${esc(item.id)}">执行</span>
  </div>`;
}

/**
 * L2 里的一行 = **一个对象的一件事**（不是一个信号）。
 *
 * 关键差别：标题位放**对象名**，金额做成一级信息，件数 / 逾期 / 失联做成标签。
 * 以前这里放的是「第 1 期回款已逾期 23 天」——三条并排摆着，一个客户名都没有。
 *
 * 一个对象上挂多件事时（实测客户 320 同时有"回款逾期"和"待跟进"），
 * 把它们摊在这一行里面，每条各自带执行按钮——**不合并成一次执行**，
 * 因为一次执行只能跑一条意图，写"办理 2 件"是假的。
 */
function queueRowHtml(group) {
  const sev = group.severity;
  const tags = [];
  if (group.amount != null) {
    const tone = sev === 'danger' ? '' : (sev === 'warning' ? ' warn' : ' calm');
    tags.push(`<span class="money${tone}">${group.amountShared ? '单笔 ' : ''}${esc(formatMoney(group.amount))}</span>`);
  }
  if (group.items.length > 1) tags.push(`<span class="tchip">${group.items.length} 件</span>`);
  if (group.overdueDays != null) tags.push(`<span class="tchip">逾期 ${group.overdueDays} 天</span>`);
  if (group.daysSinceContact != null) tags.push(`<span class="tchip">${group.daysSinceContact} 天未联系</span>`);
  const single = group.items.length === 1;
  const item = primaryItemOf(group);
  const intentId = item?.intentId ?? null;
  const entry = intentId ? entryOf(intentId) : null;
  // 这条意图在网络上有没有节点？没有的话**不能给它挂 focus-intent**——
  // `graphFocusId` 只认图里有的 id，点了会被忽略，然后按回退逻辑打开**另一个**节点的抽屉。
  // 那比"点了没反应"更糟：用户以为自己在看 A，抽屉里其实是 B。
  const inGraph = !!intentId && intentGraph().byId.has(intentId);

  // ★ ① 把**意图名**写进这一行：它是左栏与中间唯一的共同语言。
  // 第一版这里只渲染对象名，于是左栏写「宏图设计院·智慧工地管理系统」、
  // 中间写「商机推进策略」——**零重合字**，用户没法把两边对上，页面看起来就是割裂的。
  // 现在这一行读起来是「给 <对象> 做 <意图>」，和主角卡同一套语法。
  const intentChip = !entry
    ? '<span class="q-intent out" title="这条不在你当前的能力里">不在能力里</span>'
    : (inGraph
      ? `<span class="q-intent" title="网络上就是这个名字的节点">${esc(entry.name)}</span>`
      : `<span class="q-intent out" title="这条意图还没进过网络（没跑过、也没被建议过），所以图上暂时没有它">${esc(entry.name)}</span>`);

  // ★ ② 回显焦点：这一行是不是网络正在讲的那条意图。多行共用一个意图时**一起亮**——
  // 那正好说明"图上那一个节点对应左栏这几行"。
  const focused = inGraph && intentId === graphFocusId(intentGraph());

  // ★ 网络价值回流到左栏：这条意图最近一次运行给了几个下一步。
  // **没跑过就不给角标**，不写 `→0`——那会让人读成"没得接"，而实际是"没观测过"。
  const downstream = inGraph ? (graphDownstream(intentId) ?? 0) : 0;
  const badge = downstream
    ? `<span class="q-badge" data-act="focus-intent" data-id="${esc(intentId)}"
        title="这条意图最近一次运行给了 ${downstream} 个下一步，点开看它在网络里的位置">→${downstream}</span>`
    : '';
  return `<div class="q-row sev-${sev}${focused ? ' sel' : ''}">
    <div class="q-main"${inGraph ? ` data-act="focus-intent" data-id="${esc(intentId)}"
        title="在网络里看这条意图"` : ''}>
      <div class="q-t">${esc(groupLabelOf(group))}${badge}</div>
      <div class="q-tags">${intentChip}${tags.join('')}</div>
      ${single ? '' : `<div class="q-sub">${group.items.map(queueSubRow).join('')}</div>`}
    </div>
    ${single && item ? `<span class="q-go" data-act="run-suggestion" data-id="${esc(item.id)}">执行</span>` : ''}
  </div>`;
}

/**
 * 某条意图"最近一次运行"给出的下一步数量（前瞻）。**只读缓存，不触发重算。**
 *
 * 左栏角标用它而不是用"累计建议过的不同目标数"：后者会把一个跑过 13 次、
 * 每次都给同样 5 条的意图说成"有 5 个下游"，而前瞻数说的是"从这儿现在能去哪"。
 */
function graphDownstream(intentId) {
  const graph = intentGraph();
  return graph.live.get(intentId)?.size ?? 0;
}

/**
 * 队列尾部的金额合计。
 *
 * **只在同一条意图内求和**：把"合同待审批金额"和"应收逾期金额"加在一起是没有意义的。
 * 所以这里取金额贡献最大的那条意图，并**在文案里点名是哪条意图**——
 * 不写清楚口径的合计数，比不显示更容易误导。
 */
function queueAmountSummary(groups) {
  const byIntent = new Map();
  for (const group of groups) {
    if (group.amount == null || !group.amountIntentId) continue;
    byIntent.set(group.amountIntentId, (byIntent.get(group.amountIntentId) ?? 0) + group.amount);
  }
  if (!byIntent.size) return '';
  const [intentId, sum] = [...byIntent.entries()].sort((a, b) => b[1] - a[1])[0];
  return `${entryOf(intentId)?.name || intentId} · 合计 ${formatMoney(sum)}`;
}

/**
 * 队列正文（**不含标题头**）。
 *
 * 左栏导航面板里，标题与计数由**瓦片**承担，正文里不再重复一遍——
 * 否则就是"同一件事说两遍"（这正是这一轮从通栏里删掉「建议：X ＋」的同一个理由）。
 * `queueSection()` 保留下来给测试与只读出口用，它 = 头 + 这个正文。
 */
function queueRows() {
  const groups = aggregates();
  if (!groups.length) return '';
  const shown = groups.slice(0, QUEUE_VISIBLE);
  const rest = groups.length - shown.length;
  const summary = queueAmountSummary(shown);
  return `<div class="sec-bd">${shown.map(queueRowHtml).join('')}
    ${summary || rest > 0 ? `<div class="q-sum">
      ${summary ? `<span class="qs-money">${esc(summary)}</span>` : ''}
      ${rest > 0 ? `<span class="q-more" data-act="open-palette">还有 ${rest} 件 ▸</span>` : ''}
    </div>` : ''}
  </div>`;
}

function queueSection() {
  const groups = aggregates();
  if (!groups.length) return '';
  const danger = groups.filter((group) => group.severity === 'danger').length;
  return `<div class="sec queue">
    <div class="sec-hd">
      <span class="hd-main static">
        <span class="t">今天要办的</span>
        <span class="n">${groups.length} 件${danger ? ` · <b class="dn">${danger}</b> 件要救` : ''}</span>
      </span>
      <span class="more" data-act="open-palette">全部 ▸</span>
    </div>
    ${queueRows()}
  </div>`;
}

/* ---------- 左栏导航面板（图标瓦片） ---------- *
 *
 * **改版前**：这里是四块 `▸ 标题 计数 预览` 的折叠条，四块**长得完全一样**——
 * 同一个小三角、同一个字号、同一种灰。信息量其实不缺（每块都有计数和预览），
 * 缺的是"一眼分清哪块是哪块"：要逐行读字才知道「做过的」在第几行。
 * 而且四块平铺下来，左栏看不出"我现在站在哪一块"。
 *
 * **改版后**：每块一块**瓦片**（图标 + 色块 + 计数 + 一个短标签），点哪块，
 * 下面的正文区就是哪块——左栏因此第一次有了"当前在哪一块"这个状态。
 *
 * 四条自律：
 *   1. **色块编码类别，不编码序号**：蓝 = 要你动手（今天要办的 / 我该办的），
 *      紫 = 你可以发起（我常做的 / 批量摸底），灰 = 发生过（做过的）。
 *      5 块配 5 个颜色等于没有信息——用户记不住"紫色到底是哪一块"。
 *   2. **红与琥珀不参与导航配色**。它们已经被风险等级占满（`sev-danger` 要救 /
 *      `sev-warning` 要看）。导航块一旦染红，用户会读成"这块有危险"——
 *      **一个导航项被误读成一条告警，比不好看严重得多**。红仍然只留给数字。
 *   3. **一块都不砍**：档案声明了几块就出几块，只是换排布（设计稿 §10.5）。
 *   4. **正文区不重复瓦片上的标题与计数**：瓦片已经说了「今天要办的 16 件」，
 *      正文里再来一行一样的头，就是刚修掉的那种"一件事说两遍"。
 */

/** 内联 SVG 图标。零构建页面不引图标库；用 currentColor 跟着色块走。 */
const NAV_ICONS = {
  // 收件盘：一摞等你处理的东西
  today: '<path d="M22 12h-6l-2 3h-4l-2-3H2"/>'
    + '<path d="M5.5 5.1 2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.5-6.9A2 2 0 0 0 16.8 4H7.2a2 2 0 0 0-1.7 1.1z"/>',
  // 勾选框：一批待办，一次办完
  todos: '<path d="M9 11.5l2.2 2.2L15.5 9"/><rect x="3" y="4" width="18" height="17" rx="2.5"/>',
  // 闪电：你最常用、发起最快的那几条
  frequent: '<path d="M13 2 4 13.5h7L10 22l9-11.5h-7L13 2z"/>',
  // 星芒：批量摸底 = 一次摸一批
  explore: '<path d="m12 3 2 5.6 5.6 2-5.6 2L12 18.2 10 12.6 4.4 10.6 10 8.6 12 3z"/>'
    + '<path d="M18.5 16.5 19.4 19l2.5.9-2.5.9-.9 2.5"/>',
  // 回转时钟：回看
  history: '<path d="M3.5 12a8.5 8.5 0 1 0 2.6-6.1L3 8.4"/><path d="M3 3.5v5h5"/>'
    + '<path d="M12 7.5V12l3.2 1.9"/>',
};

/** 色块 → 类别。**顺序无关**：这几块属于同一类才同色。 */
const NAV_TONE = {
  today: 'blue', todos: 'blue',
  frequent: 'purple', explore: 'purple',
  history: 'slate',
};

/**
 * 左栏的登记表：顺序由 `profile.blocks` 决定，这里只定义"每块是什么"。
 *
 * ## 这一版**精简成了两块**
 *
 * 左栏的职责收敛成一句话：**"当前上下文命中了哪些意图"**。所以只留两块：
 *   · `todos`   → **命中意图**（按意图分组，意图当主语）—— 左栏的主内容
 *   · `history` → **做过的**（执行回执，也是网络闭环的落点）
 *
 * 撤掉的三块及理由（都不是砍数据，是砍"与上下文无关"）：
 *   · `today`（今天要办的，按对象聚合）—— 它的对象明细并进了命中意图的展开行；
 *     留在左栏等于同一批数据按两条轴各显示一遍，用户要在两个列表之间自己对账。
 *   · `frequent`（我常做的）—— 它不随上下文变，且实测前三个跑过 0 次（档案预置的），
 *     名不副实。仍可从「全部能力」进入。
 *   · `explore`（批量摸底）—— 批量不是一种"内容"，是一种**动作**。
 *     现在挂到每条命中意图的展开行上（「对这一批一起办理」），比单开一块更贴合语义。
 *
 * `action` 是正文区右上角那一个动作；**块自己的身体里已经带了同一个动作时这里必须留空**，
 * 否则就是同一屏上两个一模一样按钮。
 */
const NAV_BLOCKS = {
  // ⚠️ `action` 必须是**函数**，不能是当场求值的字符串：
  // `NAV_BLOCKS` 是模块加载时求值的常量，那时 `state.me` 还是 null，
  // `canBatchTodos()` 会算成空串并永久冻住——「批量摸底」的入口就再也不会出现。
  todos: { title: '命中意图', body: hitIntentsBody, action: () => canBatchTodos() },
  history: { title: '做过的', body: historyBody, action: '<span class="more" data-act="reload-runs">刷新</span>' },
};

/**
 * 「批量摸底」的入口。
 *
 * 它原来是一个独立的块，这一版撤了（批量不是一种"内容"，是一种**动作**）。
 * 但**不能顺手把功能弄丢**：逐条对象的「执行」和整条意图的「批量办理」都在展开行里，
 * 而"自己挑一条意图 + 自己挑一组对象、一次跑完"这件事只有批量面板能做，
 * 所以把入口挂到「命中意图」的动作位上——比原来少一层，也没丢东西。
 */
function canBatchTodos() {
  return (state.me?.profile?.explore?.intents ?? []).length
    ? '<span class="more" data-act="open-batch">批量摸底</span>' : '';
}

/**
 * 瓦片上的两行字：**计数 + 一个短标签**。
 *
 * 瓦片只有 ~144px 宽，放不下整句预览，所以这里只给"有几个"+"最该知道的那一个词"。
 */
function navTileInfo(id) {
  if (id === 'todos') {
    const groups = hitGroups();
    if (!groups.length) return { count: '没有命中', tag: '', empty: true };
    const total = groups.reduce((sum, group) => sum + group.count, 0);
    const danger = groups.filter((group) => group.sev === 'danger').length;
    return { count: `${groups.length} 条意图`, tag: `${total} 条命中`, hot: danger > 0 };
  }
  if (id === 'history') {
    return state.runs.length
      ? { count: `${state.runs.length} 条`, tag: '' }
      : { count: '还没有', tag: '', empty: true };
  }
  return { count: '', tag: '' };
}

/** 档案里真实存在的块（context 已搬到通栏，不算左栏的块）。 */
function navBlockIds() {
  return blocksOf().filter((id) => id !== 'context' && NAV_BLOCKS[id]);
}

/** 当前选中的块。档案里没有它就退回第一块——不记一个不存在的选择。 */
function activeNavBlock() {
  const ids = navBlockIds();
  return (state.navBlock && ids.includes(state.navBlock)) ? state.navBlock : (ids[0] ?? '');
}

/** 导航瓦片。5 块排 2 列，最后一块占满一行（否则右下角空一格，看着像没做完）。 */
function navTiles(blocks) {
  const ids = (blocks ?? navBlockIds()).filter((id) => id !== 'context' && NAV_BLOCKS[id]);
  if (!ids.length) return '';
  const active = activeNavBlock();
  const cards = ids.map((id, index) => {
    const spec = NAV_BLOCKS[id];
    const info = navTileInfo(id);
    const wide = ids.length % 2 === 1 && index === ids.length - 1;
    const hint = `${spec.title}${info.count ? ` · ${info.count}` : ''}${info.tag ? ` · ${info.tag}` : ''}`;
    return `<div class="ntile tone-${NAV_TONE[id]}${id === active ? ' on' : ''}${info.empty ? ' off' : ''}`
      + `${wide ? ' wide' : ''}" data-act="select-block" data-id="${esc(id)}" title="${esc(hint)}">
      <span class="ntic"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
        stroke-linecap="round" stroke-linejoin="round">${NAV_ICONS[id]}</svg></span>
      <span class="ntt">${esc(spec.title)}</span>
      <span class="ntn"><b>${esc(info.count)}</b>${info.tag
        ? `<i${info.hot ? ' class="hot"' : ''}>${esc(info.tag)}</i>` : ''}</span>
    </div>`;
  }).join('');
  return `<div class="navtiles">${cards}</div>`;
}

/** 选中块的正文。**只有身体 + 一个动作**，标题与计数由瓦片承担。 */
function navBlockPanel() {
  const id = activeNavBlock();
  if (!id) return '';
  const spec = NAV_BLOCKS[id];
  // action 支持两种写法：函数（需要在渲染时按当前 state 算）与字符串（静态不变）
  const action = typeof spec.action === 'function' ? (spec.action() ?? '') : (spec.action ?? '');
  const body = spec.body();
  if (!action && !body) return `<div class="navpane">${emptyBox('这一块暂时没有内容', '')}</div>`;
  return `<div class="navpane">
    ${action ? `<div class="np-act">${action}</div>` : ''}
    <div class="np-bd">${body}</div>
  </div>`;
}

/* ---------- 意图网络（中间区） ---------- *
 *
 * **它回答的是"做完接着做什么"，左栏回答"现在做什么"。** 两者不在一条轴上：
 * 左栏的一行是**事项**（一条待办 / 一次运行），网络的一个节点是**能力**（意图）。
 * 所以连接方式只有一种——左栏的每一行归结到一个 `intentId`，投影到网络的一个节点上。
 * 实测 18/18 条待办建议都带 `intentId`，这个投影是成立的。
 *
 * ## 三类边，按"事实强度"排序（这是整张图的诚实性所在）
 *
 * | 边 | 长什么样 | 含义 | 数据来源 |
 * | --- | --- | --- | --- |
 * | **走过的** | 深色实线 + 次数 | **真的从 A 跳到 B 跑了一次** | 留痕里的 `contextSnapshot._from` |
 * | **前瞻的** | 蓝色实线 | 焦点意图**最近一次运行**给出的下一步（能行动） | 那条 run 的 `nextIntents` |
 * | **建议过的** | 灰色虚线 + M/N | 历史上被建议过多少次，**不代表发生过** | 全部 run 的 `nextIntents` |
 *
 * ⚠️ **"建议过" ≠ "走过"。** 实测 17 条运行里，15 个被建议的下一步目标只有 **1 个**
 * 后来真的被跑过。所以第一版设计把边写成"接力/走过的路"是过度解读，这里的措辞全部改掉，
 * 并且**只把 `_from` 记下来的那条画成"走过的"**。
 *
 * `_from` 是前端在发起执行时写进上下文快照的**平台保留键**（`_` 前缀，
 * 与 `_slots` 同一约定）。`IntentRuntime.recordTrace` 会把它原样存进
 * `contextSnapshot`（`Map.copyOf(context)`），所以**不需要改协议或后端**就能记下"从哪条建议来的"。
 */

/**
 * 网络几何。**必须与 CSS 里 `.gnode` 的宽高一致**，否则连线端点会飘。
 *
 * `drawerW` 与 `minGap` 是**一起定的**，不是各自拍的：
 * 三列 ×196px + 两列间距，再加上左右各 34px 内边距，抽屉放开时必须还能塞进中间区。
 *   抽屉开：可用 = 1120 − 380 = 740 ≥ 34 + 588 + 40×2 + 34 = 736 ✓
 * 再窄就横向滚动（`.gcanvas` 本来就是 overflow:auto），但 1440 屏下不会到那一步。
 */
const GRAPH = {
  nodeW: 196, nodeH: 46, step: 56, top: 34, pad: 34, minGap: 40,
  defaultWidth: 1120,
  // 抽屉宽度。与 CSS `.gdrawer.show { flex-basis }`、`.gd-in { width }` 必须一致。
  drawerW: 380,
};

let graphCache = null;
let graphCacheKey = null;

/** 槽位签名：上下文一变，"现在能跑"跟着变，缓存必须失效。 */
function slotsSignature() {
  return state.slots.filter(hasValue).map((slot) => `${slot.type}=${slot.values.join(',')}`).sort().join('|');
}

/**
 * 网络数据：节点 + 边。只在 runs / entries / slots 变化时重算。
 *
 * **焦点不参与计算**——切焦点只换高亮，不重排布局。
 * 重排会让用户每次点击都丢失空间记忆（"刚才那个节点在右上角"本身是导航信息）。
 */
function intentGraph() {
  // 缓存键必须覆盖**一切会改变图的东西**。这里踩过两次：
  //   第一次只用了 traceId 列表 → "同一条 run 补上了 output"不失效；
  //   第二次只加了条数 → "同一条 run 把下一步从 A 换成 B"也不失效（条数没变）。
  // 所以现在把**下一步的目标 id 本身**也编进键里。
  const key = state.runs.map((run) => [
    run.traceId,
    run.status,
    nextIntentsOf(run.output).map((next) => next?.intentId ?? '').join(','),
    run.contextSnapshot?._from?.intentId ?? '',
  ].join(':')).join('|')
    + `#${state.entries.length}#${slotsSignature()}`
    // 命中的意图也要进图（见下），所以命中集合一变缓存必须失效
    + `#${itemsOf('item').map((item) => item.intentId).join(',')}`;
  if (graphCache && graphCacheKey === key) return graphCache;

  const nodes = new Map();
  const ensure = (id, name) => {
    if (!nodes.has(id)) nodes.set(id, { id, name: name || id, runs: 0, lastRun: null, entry: null });
    const node = nodes.get(id);
    if (name && node.name === node.id) node.name = name;
    return node;
  };
  for (const entry of state.entries) ensure(entry.id, entry.name);

  const proposed = new Map();   // 累计"被建议过"
  const walked = new Map();     // "真的走过"
  const seen = new Set();       // 出现在图里的意图（有边或跑过）

  // ★ **命中的意图也要进图**，哪怕它一次没跑过、也没有任何边。
  // 不这么做，左栏就会出现一行"右侧找不到"的意图（实测：线索质量评分 命中 3 条、
  // 既没跑过也没被建议过 → 图上没有节点），用户点它没反应，左右就断在这一行上。
  // 它们落在第 0 层（同样"没被别处推荐过"），只是没有任何连线。
  for (const item of itemsOf('item')) {
    if (!item?.intentId) continue;
    ensure(item.intentId);
    seen.add(item.intentId);
  }

  // 按时间升序走一遍，边累计边把"最近一次运行"定下来
  const asc = [...state.runs].sort((a, b) => (Number(a.startedAt) || 0) - (Number(b.startedAt) || 0));
  for (const run of asc) {
    const node = ensure(run.intentId, run.intentName);
    node.runs += 1;
    node.lastRun = run;
    seen.add(run.intentId);

    // ① 走过的：这次运行是不是从某条建议跳过来的
    const from = run.contextSnapshot?._from;
    if (from?.intentId && from.intentId !== run.intentId) {
      const k = `${from.intentId}->${run.intentId}`;
      const hit = walked.get(k) ?? { from: from.intentId, to: run.intentId, count: 0, kinds: {} };
      hit.count += 1;
      const kind = from.kind || 'unknown';
      hit.kinds[kind] = (hit.kinds[kind] ?? 0) + 1;
      walked.set(k, hit);
      ensure(from.intentId);
      seen.add(from.intentId);
    }

    // ② 建议过的
    for (const next of nextIntentsOf(run.output)) {
      if (!next?.intentId) continue;
      ensure(next.intentId);
      seen.add(next.intentId);
      const k = `${run.intentId}->${next.intentId}`;
      const hit = proposed.get(k) ?? { from: run.intentId, to: next.intentId, count: 0, reason: '' };
      hit.count += 1;
      if (next.reason) hit.reason = next.reason;
      proposed.set(k, hit);
    }
  }

  // ③ 前瞻的：每条意图"最近一次运行"给出的下一步
  const live = new Map();
  for (const node of nodes.values()) {
    live.set(node.id, new Set(nextIntentsOf(node.lastRun?.output).map((n) => n.intentId).filter(Boolean)));
  }

  // 节点状态：能不能跑，是**当下算的**（前瞻，与新老账号无关）
  const registry = registryOf(state.me ?? {});
  for (const node of nodes.values()) {
    node.entry = entryOf(node.id);
    node.runnable = !!node.entry;
    node.gate = node.entry ? satisfiability(node.entry, state.slots, registry).state : 'unknown';
    node.failed = node.lastRun?.status != null && node.lastRun.status !== 'SUCCESS';
    node.status = node.lastRun?.status ?? null;
    node.ms = node.lastRun?.durationMs ?? null;
    node.source = node.runs > 0 ? 'executed' : (node.runnable ? 'available' : 'outside');
  }

  // 边：三类合一，按事实强度定 kind
  const edges = [];
  const keys = new Set([...proposed.keys(), ...walked.keys()]);
  for (const k of keys) {
    const p = proposed.get(k);
    const w = walked.get(k);
    const from = (p ?? w).from;
    const to = (p ?? w).to;
    if (!nodes.has(from) || !nodes.has(to)) continue;
    edges.push({
      from, to,
      proposed: p?.count ?? 0,
      walked: w?.count ?? 0,
      reason: p?.reason ?? '',
      parentRuns: nodes.get(from).runs,
    });
  }

  // 连不上的节点（没跑过、也没被任何边提到）不进图 —— 它们单独放"还没进过网"
  const graphNodes = [...nodes.values()].filter((node) => seen.has(node.id) || node.runs > 0);
  const inGraph = new Set(graphNodes.map((node) => node.id));
  const stranded = [...nodes.values()].filter((node) => !inGraph.has(node.id) && node.runnable);

  graphCache = { nodes: graphNodes, edges, live, stranded, byId: new Map(graphNodes.map((n) => [n.id, n])) };
  graphCacheKey = key;
  return graphCache;
}

/**
 * 分层布局：**最长路径**定层（保证边永远从左指向右），层内按父节点顺序排（少交叉）。
 *
 * 真实现里这步该上 dagre / elk；这里数据量小（十几个节点），手写够用且没有依赖
 * ——工作台是零构建页面，不引第三方布局库。
 */
function graphLayout(graph, width = GRAPH.defaultWidth) {
  const parents = new Map();
  const children = new Map();
  for (const edge of graph.edges) {
    if (!children.has(edge.from)) children.set(edge.from, []);
    children.get(edge.from).push(edge.to);
    if (!parents.has(edge.to)) parents.set(edge.to, []);
    parents.get(edge.to).push(edge.from);
  }
  // 起点 = 没有任何边指向它、但自己跑过的意图（"你自己发起的"）
  const ids = graph.nodes.map((node) => node.id);
  const roots = ids.filter((id) => !parents.has(id));

  const ordered = [];
  const visited = new Set();
  const walk = (id) => {
    if (visited.has(id)) return;
    visited.add(id); ordered.push(id);
    for (const child of (children.get(id) ?? [])) walk(child);
  };
  roots.forEach(walk);
  ids.forEach(walk);   // 兜底：环或孤立节点也要排进去

  const depth = new Map();
  const setDepth = (id, d) => {
    if ((depth.get(id) ?? -1) >= d) return;
    depth.set(id, d);
    for (const child of (children.get(id) ?? [])) setDepth(child, d + 1);
  };
  roots.forEach((id) => setDepth(id, 0));
  ids.forEach((id) => { if (!depth.has(id)) depth.set(id, 0); });

  const maxDepth = Math.max(0, ...[...depth.values()]);
  const cols = Array.from({ length: maxDepth + 1 }, () => []);
  for (const id of ordered) cols[depth.get(id)].push(id);

  const xs = graphColX(cols.length, width);
  const pos = new Map();
  // 最长的那一列先排，其余列对齐到各自子/父节点的中心
  const anchor = cols.reduce((best, col, i) => (col.length > cols[best].length ? i : best), 0);
  cols[anchor].forEach((id, i) => pos.set(id, { x: xs[anchor], y: GRAPH.top + i * GRAPH.step }));
  const centerOf = (list) => {
    const ys = list.map((id) => pos.get(id)).filter(Boolean).map((p) => p.y + GRAPH.nodeH / 2);
    return ys.length ? (Math.min(...ys) + Math.max(...ys)) / 2 : null;
  };
  // 先排锚点左边/右边的列，用它们的邻居定中心
  for (const d of [...cols.keys()].sort((a, b) => Math.abs(a - anchor) - Math.abs(b - anchor))) {
    if (d === anchor) continue;
    cols[d].forEach((id, i) => {
      const centre = centerOf([...(children.get(id) ?? []), ...(parents.get(id) ?? [])]);
      const y = centre == null ? GRAPH.top + i * GRAPH.step : Math.round(centre - GRAPH.nodeH / 2);
      pos.set(id, { x: xs[d], y: Math.max(GRAPH.top - 22, y) });
    });
  }
  const height = Math.max(400, ...([...pos.values()].map((p) => p.y + GRAPH.nodeH))) + 40;
  return { pos, depth, cols, parents, children, height, xs, width: xs[xs.length - 1] + GRAPH.nodeW + GRAPH.pad };
}

/**
 * 列 x 坐标按**可用宽度**算。
 *
 * 为什么不能写死：抽屉改成并列之后，网络的实际宽度会在 1120 ↔ 720 之间变。
 * 写死的话第三列会被推到屏幕外，用户只看到一列半——那比浮层遮住更难用。
 * 宽度变化是**视口**变了（不是焦点变了），所以这时候重排是应该的；
 * 切焦点仍然不重排（见 renderGraph）。
 */
function graphColX(count, width) {
  if (count <= 1) return [GRAPH.pad];
  const usable = Math.max(GRAPH.nodeW * count + GRAPH.minGap * (count - 1),
    width - GRAPH.pad * 2);
  const gap = (usable - GRAPH.nodeW * count) / (count - 1);
  return Array.from({ length: count }, (_, i) => Math.round(GRAPH.pad + i * (GRAPH.nodeW + gap)));
}

/**
 * 当前焦点意图。复用 `state.current`——**不新造第二个"当前意图"**。
 *
 * ⚠️ 这里和主角卡是**两个不同的东西**，第一版把它们混着用了，页面看起来就是割裂的：
 *   · 主角卡 = `topActionable()` —— **系统建议**"最该做的那件事"（数据推出来的）
 *   · 焦点   = `state.current`  —— **用户正在看**哪条意图（点出来的）
 * 用户没点过任何东西时 `state.current` 是空的，这时焦点必须**跟着主角卡**，
 * 否则首屏就会左栏说"做 A"、中间画"B"（实测出现过：左栏商机推进策略 / 网络今日跟进优先级）。
 *
 * 两者不一致时**不偷偷对齐**，而是由 `focusBarHtml()` 在左栏明说"你正在看 X"并给一键回到建议。
 */
function graphFocusId(graph) {
  if (state.current && graph.byId.has(state.current)) return state.current;
  const best = aggregates()[0];
  const primary = best ? primaryItemOf(best) : null;
  if (primary?.intentId && graph.byId.has(primary.intentId)) return primary.intentId;
  const first = graph.nodes.find((node) => node.runs > 0) ?? graph.nodes[0];
  return first?.id ?? '';
}

/** 系统建议的那条意图（主角卡的谓语）。焦点没被用户改过时，两者是同一个。 */
function suggestedIntentId() {
  const best = aggregates()[0];
  return (best ? primaryItemOf(best) : null)?.intentId ?? null;
}

/**
 * 「你正在看」提示条：**只在焦点与系统建议不一致时出现**。
 *
 * 它是把"两个当前"缝起来的那一针：主角卡永远是"建议"，焦点是"你在看什么"，
 * 两者不同时不隐藏、也不偷偷改主角卡，而是明说，并给一个回到建议的入口。
 */
function focusBarHtml() {
  const graph = intentGraph();
  if (!graph.nodes.length) return '';
  const focusId = graphFocusId(graph);
  const suggested = suggestedIntentId();
  if (!focusId || focusId === suggested) return '';
  const node = graph.byId.get(focusId);
  if (!node) return '';
  return `<div class="focusbar">
    <span class="fb-t">你正在看</span>
    <span class="fb-n">${esc(node.name)}</span>
    <span class="fb-m">左栏与中间都在讲它</span>
    ${suggested && graph.byId.has(suggested)
      ? `<span class="fb-back" data-act="clear-focus"
          title="回到系统建议的那件事">回到建议：${esc(graph.byId.get(suggested).name)}</span>`
      : ''}
  </div>`;
}

/** 一条边最终的 kind。**事实强于建议**：走过 > 前瞻 > 建议过。 */
function graphEdgeKind(edge, focusId, live) {
  if (edge.walked > 0) return 'walked';
  if (edge.from === focusId && live.get(focusId)?.has(edge.to)) return 'live';
  return 'proposed';
}

/** 边上的标签：走过的给次数；建议过的给 M/N，N<3 标"样本少"。 */
function graphEdgeLabel(edge, kind) {
  if (kind === 'walked') return { text: `走过 ${edge.walked} 次`, cls: 'walked' };
  if (kind === 'live') return { text: edge.proposed ? `${edge.proposed}/${edge.parentRuns}` : '前瞻', cls: 'live' };
  if (!edge.proposed) return { text: '', cls: 'proposed' };
  return { text: `${edge.proposed}/${edge.parentRuns}`, cls: edge.parentRuns < 3 ? 'thin' : 'proposed' };
}

/* ---- ⓪ 上下文通栏（横跨整页） ---- *
 *
 * **为什么合并到这里**：改版前"上下文"有两个地方各显示一遍——
 * 左栏顶部的可编辑上下文、右栏顶部的执行快照。两者内容相同时（最常见的情况）
 * 用户看到的是同一件事被说两遍（`时间窗 近90天` 与 `时间窗 = 近90天` 并排）；
 * 而它本来就同时驱动左栏（能用什么）与右栏（跑出什么），属于**整个工作台的状态**，
 * 不是某一栏的子块。所以合并成一条横跨整页的通栏。
 *
 * 快照不再常驻显示，只在**与当前上下文不一致**时给一行提醒——
 * "这次结果用的是当时那套上下文"这件事，只在它真的不一样时才需要说。
 */

/** 上下文通栏。 */
function renderContextBar() {
  const bar = $('ctxbar');
  if (!bar) return;
  bar.innerHTML = state.me ? contextBarHtml() : '';
}

/** 通栏的 HTML（纯函数，方便在 Node 里断言）。 */
function contextBarHtml() {
  const open = !!state.ctxOpen;
  const chips = state.slots.filter(hasValue).map(ctxChip).join('');
  return `<div class="ctxbar-in${open ? ' open' : ''}">
    <span class="ctx-label" data-act="toggle-ctx" title="${open ? '收起上下文' : '展开上下文'}">
      <span class="chev">${open ? '▾' : '▸'}</span>上下文
    </span>
    <div class="ctx-chips">
      ${chips}
      ${!chips ? '<span class="ctx-none">还没摆对象 —— 摆上谁，工作台就围着谁转</span>' : ''}
      <span class="cchip add" data-act="add-slot" title="加一个实体到上下文">＋</span>
    </div>
    <div class="ctx-state">${contextStateHtml()}</div>
  </div>
  ${open ? `<div class="ctx-editor">${contextEditor()}</div>` : ''}`;
}

/** 折叠态下的一个槽位 chip：点它换一个对象。 */
function ctxChip(slot) {
  const pinned = slot.source === SOURCE_PINNED;
  const bad = slot.state === 'bad';
  return `<span class="cchip${bad ? ' bad' : ''}" data-act="add-entity" data-type="${esc(slot.type)}"
      title="${esc(slot.values.join('、'))}${bad ? '（已失效，点一下换一个）' : ''}">
    <b>${esc(slot.title)}</b> ${esc(slot.label)}${pinned ? '<i class="pin">钉</i>' : ''}<span class="caret">▾</span>
  </span>`;
}

/**
 * 「建议的对象」= 主角卡要针对的那个对象，还没摆进上下文时，在**主角卡的主语旁边**
 * 给一个虚线小按钮，点一下就摆进去。
 *
 * **为什么从通栏搬到这里**：它原来渲染在顶部通栏里（`建议：X ＋`），
 * 但通栏的语义是"现在上下文里有什么"，塞一个"还没进去的建议"进去已经混淆了两件事；
 * 更要命的是它和主角卡**是同一个对象**——两处都调 `topActionable()`，
 * 实测通栏写「建议：宏图设计院·智慧工地管理系统 ＋」，紧挨着的主角卡写
 * 「给 宏图设计院·智慧工地管理系统 做」，**同一屏内出现两遍**。
 *
 * 搬到主语旁边之后，一件事只在一个地方说，而且位置更对：
 * 用户看到"给 X 做"的这一刻，才最可能想"把 X 摆进上下文"。
 *
 * 已经摆进去了就不显示——不给一个点了没变化的按钮。
 */
function subjectAdoptChip(best) {
  if (!best?.subjectType || !best.subject || !best.subjectId) return '';
  const already = state.slots.some((slot) => slot.type === best.subjectType && hasValue(slot)
    && (slot.values.includes(String(best.subjectId)) || slot.label === best.subject));
  if (already) return '';
  return `<span class="adopt" data-act="accept-subject" data-type="${esc(best.subjectType)}"
      data-id="${esc(best.subjectId)}" data-label="${esc(best.subject)}"
      title="这个对象还没摆进上下文，点一下摆进来">摆进上下文</span>`;
}

/** 当前上下文 vs 最近一次执行用的上下文。只在真的不一样时才提醒。 */
function contextStateHtml() {
  const slots = state.slots.filter(hasValue);
  const delta = state.ctxDelta;
  if (delta) {
    const text = delta.delta > 0
      ? `加了「${esc(delta.title)}」· ${delta.delta} 条能力现在能用了`
      : `去掉「${esc(delta.title)}」· ${-delta.delta} 条能力收起了`;
    return `<span class="cs ${delta.delta > 0 ? 'up' : ''}">${text}</span>`;
  }
  const run = latestContextRun();
  if (!run) return '<span class="cs none">摆上对象后，左边能做的事会跟着变</span>';
  const same = slotsKey(slots) === slotsKey(run.contextSnapshot?._slots ?? []);
  return same
    ? '<span class="cs sync" title="最近一次执行用的就是这套上下文">⟳ 最近一次执行 = 这套上下文</span>'
    : '<span class="cs drift" title="结果对应的是执行那一刻的上下文，不是现在这套">⚠ 上下文已变，右栏结果按当时那套算</span>';
}

function slotsKey(slots) {
  return slots.map((slot) => `${slot.type}=${(slot.values ?? []).join(',')}`).sort().join('|');
}

/** 最近一次带上下文快照的执行。 */
function latestContextRun() {
  const all = [...state.session, ...state.runs];
  return all.find((run) => run?.contextSnapshot?._slots) ?? all[0] ?? null;
}

/** 展开态的编辑面板：空槽位、预设、以及"摆上它能多用几条"的解释。 */
function contextEditor() {
  const presetBar = (state.me.profile?.presets ?? []).map((preset) =>
    `<span class="btn sm" data-act="apply-preset" data-id="${esc(preset.id)}"
      title="${esc(preset.description || '')}">${esc(preset.label)}</span>`).join('');
  const empty = emptySlotTypes();
  return `<div class="ctx-editor-hd">你摆什么，工作台就出什么</div>
    <div class="ctx-editor-bd">${state.slots.map(slotRow).join('')}
      ${empty.map(emptySlotRow).join('')}</div>
    ${presetBar ? `<div class="ctx-editor-ft">${presetBar}</div>` : ''}
    <div class="hintline">${hintLine()}</div>`;
}

/**
 * 兼容老调用点：上下文已经搬到顶部通栏，左栏不再渲染它。
 * 保留这个名字是为了让 `__workbench()` 的出口稳定（测试与宿主都引用它）。
 */
function contextPanel() {
  return '';
}

function slotRow(slot) {
  const meta = slotTypeMeta(slot.type) ?? {};
  // 在栈里但还没挑值的槽位（预设就是这种：只声明"要摆哪几类"，值留给你）没有值，
  // 就不该顶着槽位名去冒充值——否则会渲染成「客户 = 客户」，还把该有的选择入口吃掉。
  if (!hasValue(slot)) {
    return `<div class="slot empty">
      <span class="sk">${esc(slot.title || meta.title || slot.type)}</span>
      <span class="sv" style="cursor:pointer;" data-act="add-entity" data-type="${esc(slot.type)}">＋ 选择${esc(meta.title || slot.type)} ▾</span>
      <span class="sx act" data-act="clear-slot" data-type="${esc(slot.type)}" title="从上下文移除">✕</span>
    </div>`;
  }
  const multi = slot.mode === MODE_SET && slot.values.length > 1;
  const valueText = `${slot.label || slot.values[0]}${multi ? ` 等 ${slot.values.length} 个` : ''}`;
  const pinned = slot.source === SOURCE_PINNED;
  const state_ = slot.state;
  const cls = state_ === 'bad' ? 'slot bad' : 'slot';
  // 钉住 = 切视角/被带入时不覆盖；◇ 是"系统带来的、可被覆盖"，两者必须看得出区别
  const pinTag = pinned ? '<span class="pin">钉住</span>'
    : (slot.source === SOURCE_CARRIED ? '<span class="pin">带入</span>' : '');
  const warn = state_ === 'bad' ? '<span class="sx act" style="color:#B91C1C;">已失效，请替换</span>'
    : (state_ === 'unknown' ? '<span class="sx" title="这个槽位类型宿主还没登记，没法替你校验">未校验</span>' : '');
  const toggle = pinned
    ? `<span class="sx act" data-act="pin-slot" data-type="${esc(slot.type)}" title="取消钉住">◆</span>`
    : `<span class="sx act" data-act="pin-slot" data-type="${esc(slot.type)}" title="钉住：切视角时保留">◇</span>`;
  return `<div class="${cls}">
    <span class="sk">${esc(slot.title || meta.title || slot.type)}</span>
    <span class="sv" title="${esc(slot.values.join('、'))}">${esc(valueText)}</span>
    ${pinTag}${warn}${toggle}
    <span class="sx act" data-act="clear-slot" data-type="${esc(slot.type)}" title="从上下文移除">✕</span>
  </div>`;
}

function emptySlotRow(meta) {
  return `<div class="slot empty">
    <span class="sk">${esc(meta.title)}</span>
    <span class="sv" style="cursor:pointer;" data-act="add-entity" data-type="${esc(meta.type)}">＋ 选择${esc(meta.title)} ▾</span>
  </div>`;
}

/** 还没进栈的实体型槽位（按登记顺序，给"空槽位"行用）。 */
function emptySlotTypes() {
  const inStack = new Set(state.slots.map((slot) => slot.type));
  return (state.me?.slotTypes ?? []).filter((item) => item.entity && !inStack.has(item.type));
}

/**
 * 「摆上 X 就能多用 N 条」的因果。
 *
 * **只在上下文展开时显示**——它是"我为什么要摆上下文"的解释，不是首屏信息。
 * 而且**收成一句**：以前是 5 条并列（`「客户」槽空着 ⇒ 4 条能力收起 · 「合同」槽空着 ⇒ 3 条能力收起 …`），
 * 占了 3 行小灰字，那是实施者语言，销售不关心"几条能力收起"。
 */
function hintLine() {
  const registry = registryOf(state.me);
  const parts = [];
  for (const meta of emptySlotTypes()) {
    let count = 0;
    for (const entry of state.entries) {
      const gate = satisfiability(entry, state.slots, registry);
      if (gate.state === 'locked' && gate.missing.includes(meta.key)) count += 1;
    }
    if (count > 0) parts.push({ title: meta.title, count });
  }
  if (!parts.length) {
    return '上下文是显式的：改了立刻看见后果，工作台不会偷偷替你换实体。';
  }
  parts.sort((a, b) => b.count - a.count);
  const others = parts.length - 1;
  return others > 0
    ? `摆上「${parts[0].title}」能多用 ${parts[0].count} 条能力；另有 ${others} 类槽位可以加。`
    : `摆上「${parts[0].title}」能多用 ${parts[0].count} 条能力。`;
}
/** 增强降级横幅：非空说明"这次少了一部分待办"，空态必须跟着改口，不能装作没有待办。 */
function issuesBanner() {
  if (!state.issues.length) return '';
  const who = state.issues.map((issue) => issue.source).filter(Boolean).join('、');
  return `<div class="degraded">
    <div class="dt">⚠ 一部分待办这次没取到</div>
    <div>${esc(who || '部分增强器')} 求值失败，所以「今天先做 / 我该办的」可能不完整。
      这<strong>不代表</strong>你没有待办。</div>
    <div style="display:flex;gap:5px;margin-top:6px;">
      <span class="btn sm" data-act="reload-catalog">重试</span>
    </div>
  </div>`;
}

function itemsOf(kind) {
  return state.suggestions.filter((item) => item.kind === kind);
}

/* ---- 各块的正文（标题 / 计数 / 展开态已经由左栏的导航瓦片承担） ---- *
 * 所以这一层只剩"身体"：`hitIntentsBody` / `historyBody`。
 * 计数改由 `navTileInfo()` 统一给（一处口径，不会两块对不上）。
 */

/* ---- ① 命中意图（左栏主内容） ---- *
 *
 * ## 这一块的口径
 *
 * **上下文 → 确定性求值 → 命中的意图 → 按意图分组。**
 *
 * "确定性求值"指的是：命中的意图来自 `/intent/catalog`，由宿主的事实规则按当前上下文算出来，
 * **全程不经过 LLM**（架构文档原话："全程确定性求值，不经过 LLM，因此可缓存、可解释、可测"）。
 * 上下文一变，命中的集合就跟着变——这正是左栏要表达的因果。
 *
 * ⚠️ 它**不是**按执行器类型筛的。执行器类型（skill / flow / agent）说的是"这条意图跑起来
 * 是确定性的还是概率性的"，和"它有没有被上下文命中"是两件事。实测当前 23 条意图的执行器
 * 全是 agent——按类型筛左栏会**一条都不剩**。
 *
 * ## 为什么主语是意图
 *
 * 因为分组的轴就是意图。一行读作「**逾期风险预警** · 19.12 万 · 5 条 · 4 个客户」，
 * 和中间网络上的节点**同名**，两边一眼对得上。要逐条对象明细，展开行即可。
 */

/**
 * 命中意图按 `intentId` 分组，并算出这一组的分量。
 *
 * 一条命中天然属于"某条意图推出来的一批"，所以按意图分组是它的自然形状；
 * 而 `category`（金额 / 对象数 / 最严重等级）是业务人员扫这一列时真正在看的东西。
 */
function hitGroups() {
  const buckets = new Map();
  for (const item of itemsOf('item')) {
    const key = item.intentId || item.id;
    if (!buckets.has(key)) buckets.set(key, []);
    buckets.get(key).push(item);
  }
  return [...buckets.entries()].map(([key, list]) => {
    const facts = list.map((item) => ({ item, fact: parseItemFacts(item) }));
    // 对象去重：5 条信号可能落在 4 个客户上（金辉电子挂了 2 期）
    const refs = new Map();
    for (const { item } of facts) {
      const ref = objectRefOf(item);
      if (ref) refs.set(`${ref.type}:${ref.id}`, ref);
    }
    const amounts = facts.map((row) => row.fact.amount).filter((n) => n != null);
    const overdue = facts.map((row) => row.fact.overdueDays).filter((n) => n != null);
    const contacts = facts.map((row) => row.fact.daysSinceContact).filter((n) => n != null);
    return {
      key,
      // 组名用**意图名**：这一块按意图分组，用建议标题当组名会变成"客户「金辉电子」第1期…"，
      // 那是对象不是能力，和分组的轴对不上
      title: entryOf(key)?.name || list[0].title,
      items: list,
      count: list.length,
      objects: [...refs.values()],
      amount: amounts.length ? amounts.reduce((sum, n) => sum + n, 0) : null,
      overdueDays: overdue.length ? Math.max(...overdue) : null,
      daysSinceContact: contacts.length ? Math.max(...contacts) : null,
      sev: list.map(severityOf).sort((a, b) => SEVERITY_RANK[a] - SEVERITY_RANK[b])[0] ?? 'info',
    };
  }).sort((a, b) => (SEVERITY_RANK[a.sev] - SEVERITY_RANK[b.sev])
    || ((b.amount ?? 0) - (a.amount ?? 0))
    || (b.count - a.count));
}

/** 兼容老调用点（测试与旧出口引用 `todoGroups`）。 */
function todoGroups() {
  return hitGroups();
}

/**
 * 一行 = 一条命中的意图。**意图当主语**，金额/条数/对象数当标签，行尾是网络上的前瞻角标。
 *
 * 展开之后才是对象明细（每条一个执行按钮）——因为一次执行只能跑一条意图 + 一个对象，
 * 所以"整条意图一起办"必须有独立的动作（见 `run-todo`），不能骗人写成"一键办完 5 件"。
 */
function hitIntentsBody() {
  const groups = hitGroups();
  if (!groups.length) {
    return emptyBox('当前上下文没有命中任何意图',
      state.me.profile?.emptyHint || '往顶部通栏摆一个对象，或者换个时间窗，这里会跟着变。');
  }
  const focusId = graphFocusId(intentGraph());
  const rows = groups.map((group) => {
    const open = state.openTodos === group.key;
    const inGraph = intentGraph().byId.has(group.key);
    const focused = inGraph && group.key === focusId;
    const downstream = inGraph ? (graphDownstream(group.key) ?? 0) : 0;
    const tags = [];
    if (group.amount != null) {
      const tone = group.sev === 'danger' ? '' : (group.sev === 'warning' ? ' warn' : ' calm');
      tags.push(`<span class="money${tone}">${esc(formatMoney(group.amount))}</span>`);
    }
    tags.push(`<span class="tchip">${group.count} 条</span>`);
    if (group.objects.length) {
      tags.push(`<span class="tchip">${group.objects.length} 个对象</span>`);
    }
    if (group.overdueDays != null) tags.push(`<span class="tchip">逾期 ${group.overdueDays} 天</span>`);
    if (group.daysSinceContact != null) {
      tags.push(`<span class="tchip">${group.daysSinceContact} 天未联系</span>`);
    }
    const badge = downstream
      ? `<span class="q-badge" data-act="focus-intent" data-id="${esc(group.key)}"
          title="这条意图最近一次运行给了 ${downstream} 个下一步">→${downstream}</span>`
      : '';
    const subs = open ? hitSubRows(group) : '';
    return `<div class="q-row sev-${group.sev}${focused ? ' sel' : ''}">
      <div class="q-main"${inGraph ? ` data-act="focus-intent" data-id="${esc(group.key)}"
          title="在网络里看这条意图"` : ''}>
        <div class="q-t">${esc(group.title)}${badge}</div>
        <div class="q-tags">${tags.join('')}</div>
      </div>
      <span class="q-go" data-act="toggle-todo" data-id="${esc(group.key)}">${open ? '收起' : '展开'}</span>
    </div>${subs}`;
  }).join('');
  return rows;
}

/** 展开后的对象明细：**一行一个对象**，各自带执行按钮。 */
function hitSubRows(group) {
  const rows = group.items.map((item) => {
    const fact = parseItemFacts(item);
    const label = fact.objectName
      || objectNames.get(`${objectRefOf(item)?.type}:${objectRefOf(item)?.id}`)
      || item.title;
    return `<div class="q-subrow">
      <span class="q-subt" title="${esc(item.title)}">${esc(label)}</span>
      ${fact.amount != null ? `<span class="money calm">${esc(formatMoney(fact.amount))}</span>` : ''}
      <span class="q-go sm" data-act="run-suggestion" data-id="${esc(item.id)}">执行</span>
    </div>`;
  }).join('');
  return `<div class="q-sub">${rows}
    <div class="q-subrow batch">
      <span class="q-subt muted">对这一批一起办理（一个意图 × 一次执行）</span>
      <span class="q-go" data-act="run-todo" data-id="${esc(group.key)}">批量办理</span>
    </div>
  </div>`;
}

/* ---- ② 我常做的 ---- */

/**
 * 「我常做的」的频次。
 *
 * **按焦点对象重排**（设计稿 §5.1 的表格要求：客户从 A 切到 B ⇒「我常做的」按 B 的历史重排）。
 * 原实现只数 `intentId` 出现次数，完全不看对象——于是换了客户，"我常做的"纹丝不动，
 * 而"工作台懂我"这条主张里，这是最容易兑现的一条。
 *
 * 口径：有焦点对象时**只数作用于该类对象的历史**（0 就是 0，不退回全局）。
 * 为什么不能"查不到就退回全局"：那会让"在别的对象类型上跑过 3 次"压过"在当前对象类型上跑过 2 次"，
 * 于是换了客户之后排序反而是错的。全局次数只作为**同分时的次级排序**（见 `rankedFrequent`）。
 *
 * 为什么按"对象类型"而不是"对象 id"：单客户的历史太稀疏（跑两次都算多），
 * 而"我对客户类对象最常做健康度诊断"才是稳定可用的偏好。
 */
function frequencyOf(intentId) {
  const focus = focusSlot();
  if (focus) {
    return state.runs.filter((run) => run.intentId === intentId && objectTypeOfRun(run) === focus.type).length;
  }
  return globalFrequencyOf(intentId);
}

/** 不看对象的全局频次：只做同分时的次级排序，不参与跨对象类型的比较。 */
function globalFrequencyOf(intentId) {
  return state.runs.filter((run) => run.intentId === intentId).length;
}

/** 从留痕的参数里推断这次执行作用于哪一类对象（用于"我常做的"分对象统计）。 */
function objectTypeOfRun(run) {
  const registry = registryOf(state.me ?? {});
  for (const key of Object.keys(run.params ?? {})) {
    const type = slotTypeOf(key, registry);
    if (type !== TYPE_UNKNOWN && slotTypeMeta(type)?.entity) return type;
  }
  return '';
}

/**
 * 「我常做的」这一块**已经从左栏撤掉**（左栏精简成"命中意图 + 做过的"），
 * 所以它的正文函数也一并删了 —— 不留下"看着还活着、其实没人调"的代码。
 *
 * 撤掉的理由：它不随上下文变，而且实测排出来的 5 个里前 3 个跑过 0 次
 * （档案 `pinnedIntents` 预置的），一个自称"我常做的"列表前三名一次没跑过。
 *
 * `rankedFrequent()` / `frequencyOf()` **留着**：主角卡在没有待办时会用它挑一条能跑的
 * （见 `topActionable`），那是一条仍然活着的路径。
 *
 * ⚠️ 顺带留一句：`rankedFrequent()` 会**静默跳过**档案里钉了、但不在能力目录里的意图
 * （实测 manager 档案钉 5 条，跳过 2 条）。这个缺口现在没有界面暴露它了——
 * 什么时候「我常做的」回来，记得把它一起带回来。
 */

/* ---- ③ 做过的 ---- */

function timeOf(ts) {
  if (!ts) return '';
  const date = new Date(Number(ts));
  if (Number.isNaN(date.getTime())) return '';
  const pad = (n) => String(n).padStart(2, '0');
  return `${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

function historyBody() {
  const rows = state.runs.slice(0, 8).map((run) => `<div class="run-line" data-act="open-run"
      data-id="${esc(run.traceId)}">
      <span class="rt">${esc(timeOf(run.startedAt))}</span>
      <span class="rn">${esc(run.intentName || run.intentId)}${objectLabelOf(run) ? ` · ${esc(objectLabelOf(run))}` : ''}</span>
      <span class="rm">${run.status === 'SUCCESS' ? esc(formatDuration(run.durationMs)) : esc(run.status)}</span>
    </div>`).join('');
  return `${rows ? `<div class="hist-note">只列最近 ${Math.min(8, state.runs.length)} 条 · 完整历史在右栏「做过的」</div>` : ''}
    ${rows || emptyBox('还没有执行记录', '跑过一次之后，这里会留下可回看的痕迹。')}`;
}

/** 从留痕的参数里挑一个能认人的字段当"对象名"。 */
function objectLabelOf(run) {
  const params = run.params ?? {};
  for (const key of ['customerId', 'contractId', 'businessId', 'contactId', 'clueId', 'productId', 'receivableId']) {
    const value = params[key];
    if (value == null || value === '') continue;
    const slot = (run.contextSnapshot?._slots ?? []).find((item) => item.key === key);
    return slot?.label ? `${slot.label}` : `${key}=${value}`;
  }
  return '';
}

function emptyBox(title, hint) {
  return `<div class="empty"><span class="et">${esc(title)}</span>
    ${hint ? `<span class="eh">${esc(hint)}</span>` : ''}</div>`;
}

// ---------------------------------------------------------------- 渲染：右栏

function entryOf(intentId) {
  return state.entries.find((entry) => entry.id === intentId) ?? null;
}

/** 中间区：网络（含抽屉）。**整块都是网络**，结果卡在右侧与它**并列**（不是浮层遮盖）。 */
function renderGraph() {
  const canvas = $('gcanvas');
  if (!canvas) return;
  if (!state.me) { canvas.innerHTML = ''; return; }
  const graph = intentGraph();
  const focusId = graphFocusId(graph);
  // ★ ④ 抽屉改成**并列**：它占宽度，网络自己收窄。
  // 宽度是**视口**变化（不是焦点变化），所以这时候重排布局是应该的；
  // 切焦点仍然不重排——那才是"丢空间记忆"的那种重排。
  const drawer = $('gdrawer');
  if (drawer) drawer.classList.toggle('show', !!state.graphDrawer);
  const total = ($('graph')?.clientWidth ?? GRAPH.defaultWidth);
  const available = Math.max(GRAPH.nodeW * 2 + GRAPH.minGap + GRAPH.pad * 2,
    state.graphDrawer ? total - GRAPH.drawerW : total);
  const layout = graphLayout(graph, available);
  const head = $('ghead');
  if (head) head.innerHTML = graphHeadHtml(graph, focusId);
  canvas.innerHTML = graphCanvasHtml(graph, layout, focusId);
  if (drawer) drawer.innerHTML = graphDrawerHtml(graph, focusId);
}

function renderRight() {
  // 上下文快照不再单独占一条：它已经并进顶部通栏，
  // 而且只在"与当前上下文不一致"时以一行提醒出现。
  //
  // 中间区现在是**意图网络**（上一版是执行流）。执行流没有丢，它被收进了
  // 抽屉：点网络上的节点，抽屉里就是那条意图的执行记录与结果卡。
  renderGraph();
}

/**
 * 头部的文案要如实写清"这张图是什么"。
 *
 * ★ 这里挡的是一个**业务误解**：业务人员看到一张从左到右发散的图，会天然读成
 * "跑完 A 就该做 B，然后再做 C"——当成 SOP。但实测 18 条边里 17 条只是**模型提过一嘴**，
 * 从没被验证过；而且同一层是并列的，没有先后。
 * 所以头部第一行就写明"这是参考不是流程"，而不是把免责声明藏在图例里。
 */
function graphHeadHtml(graph, focusId) {
  const focus = graph.byId.get(focusId);
  const live = graph.live.get(focusId)?.size ?? 0;
  const executed = graph.nodes.filter((node) => node.runs > 0).length;
  const failed = graph.nodes.filter((node) => node.failed).length;
  const walkedCount = graph.edges.filter((edge) => edge.walked > 0).length;
  return `<div class="ghead-l">
      <div class="gt">意图网络 <span class="gk">参考，不是流程</span></div>
      <div class="gs">${focus ? `焦点：<b>${esc(focus.name)}</b> · ` : ''}
        前瞻 ${live} 条 · 建议过 ${graph.edges.length} 条${
  walkedCount ? ` · <b class="gw">走过 ${walkedCount} 条</b>` : ' · <b class="gnone">还没走过任何一条</b>'} ·
        跑过 ${executed}/${graph.nodes.length} 个 · <span class="gdim">同层并列，没有先后</span></div>
    </div>
    <div class="ghead-r">
      ${state.explore ? `<span class="gbtn batch" data-act="open-explore"
        title="看这一轮批量摸底的进度">📦 ${esc(exploreProgressCopy(state.explore))}</span>` : ''}
      ${failed ? `<span class="gbtn warn" data-act="focus-failed">⚠ ${failed} 个没跑成</span>` : ''}
      <span class="gbtn${state.graphLiveOnly ? ' on' : ''}" data-act="toggle-graph-live"
        title="只留焦点意图最近一次运行给出的下一步">只看前瞻</span>
      <span class="gbtn" data-act="open-palette">全部能力</span>
    </div>`;
}

function graphCanvasHtml(graph, layout, focusId) {
  if (!graph.nodes.length) {
    return `<div class="gempty">${emptyBox('还没有可画的路径',
      '跑过一次之后，这里会按"跑完接着做什么"把意图连起来。')}</div>`;
  }
  const live = graph.live.get(focusId) ?? new Set();
  const near = new Set([focusId, ...(layout.parents.get(focusId) ?? []), ...(layout.children.get(focusId) ?? [])]);

  const wires = graph.edges.map((edge) => {
    const a = layout.pos.get(edge.from);
    const b = layout.pos.get(edge.to);
    if (!a || !b) return '';
    const kind = graphEdgeKind(edge, focusId, graph.live);
    if (state.graphLiveOnly && kind === 'proposed') return '';
    const label = graphEdgeLabel(edge, kind);
    const x1 = a.x + GRAPH.nodeW; const y1 = a.y + GRAPH.nodeH / 2;
    const x2 = b.x; const y2 = b.y + GRAPH.nodeH / 2;
    const dx = Math.max(40, (x2 - x1) * 0.5);
    const d = `M${x1},${y1} C${x1 + dx},${y1} ${x2 - dx},${y2} ${x2},${y2}`;
    return `<path class="ge ${kind}" d="${d}" marker-end="url(#garw)"></path>`
      + (label.text ? `<text class="gl ${label.cls}" x="${(x1 + x2) / 2}" y="${(y1 + y2) / 2 - 6}"
          text-anchor="middle">${esc(label.text)}</text>` : '');
  }).join('');

  const nodes = graph.nodes.map((node) => {
    const p = layout.pos.get(node.id);
    if (!p) return '';
    return graphNodeHtml(node, p, node.id === focusId, near.has(node.id));
  }).join('');

  const cols = layout.cols.map((col, i) => {
    if (!col.length) return '';
    // ★ 列头必须挡住一个**业务误解**：从左到右的排布天然被读成"先做A、再做B、再做C"，
    // 但同一层里是**并列的可选**，没有先后。所以不但要写"第 N 层"，还要写明"并列 N 条"。
    const label = i === 0
      ? '起点 · 没被别处推荐过'
      : `第 ${i} 步 · 并列 ${col.length} 条（没有先后）`;
    // 列头 x 用**布局算出来的** xs，不是写死的常量——抽屉打开时网络变窄，列会跟着收
    return `<div class="gcol" style="left:${layout.xs[i] ?? 0}px">${label}
      <span class="cnum">${col.length}</span></div>`;
  }).join('');

  return `<div class="ginner" style="height:${layout.height}px">
    <svg class="gwires" width="${layout.width}" height="${layout.height}">
      <defs><marker id="garw" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto">
        <path d="M0,1 L9,5 L0,9" fill="none" stroke="#B9C2CE" stroke-width="1.4"></path></marker></defs>
      ${wires}
    </svg>
    ${cols}${nodes}
  </div>`;
}

/**
 * 一个节点。四种状态**互斥且各有明确含义**：
 *   executed  跑过 —— 深色描边 + 浅蓝底 + 右上角跑了 N 次（历史事实）
 *   available 没跑过但当前上下文能跑 —— 实线细边 + "现在能跑"（**前瞻，与新老账号无关**）
 *   gated     没跑过且缺槽位 —— 虚线 + 还差什么
 *   outside   不在当前档案能力里 —— 虚线 + 更淡 + 「档案外」
 * 失败另外叠一层红色，不改上面四态。
 */
function graphNodeHtml(node, pos, isFocus, isNear) {
  const cls = ['gnode', node.source];
  if (isFocus) cls.push('focus');
  else if (isNear) cls.push('near');
  else cls.push('faded');
  if (node.failed) cls.push('failed');

  let meta = '';
  if (node.runs > 0) {
    meta = `<span class="gt-ok">✓ ${esc(formatDuration(node.ms))}</span><span>跑过 ${node.runs} 次</span>`;
  } else if (node.source === 'outside') {
    meta = '<span class="gt-out">档案外</span><span>不在你能力里</span>';
  } else if (node.source === 'available') {
    meta = '<span class="gt-can">现在能跑</span>';
  } else {
    meta = '<span>还差槽位</span>';
  }
  const badge = node.runs > 0 ? `<span class="gcnt">${node.runs}</span>` : '';
  return `<div class="${cls.join(' ')}" style="left:${pos.x}px;top:${pos.y}px"
      data-act="focus-intent" data-id="${esc(node.id)}"
      title="${esc(node.name)}${node.runs ? ` · 跑过 ${node.runs} 次` : ''}">
    ${badge}
    <div class="gn">${esc(node.name)}</div>
    <div class="gm">${meta}</div>
  </div>`;
}

/** 没跑过的节点要说清"这不是没有下一步，是没观测过"——两者差别很大。 */
function edgeHint(node) {
  if (node.runs > 0) return '';
  return `<div class="gd-hint">还没跑过它，所以没有前瞻边。<b>这不等于"没有下一步"，是"没观测过"。</b></div>`;
}

/**
 * 抽屉：焦点意图的结果。**它浮在图上**，关掉之后中间 100% 是网络。
 *
 * 这里刻意复用 `streamPanels(intentId)`（只保留这一条意图的运行），
 * 而不是另写一套——分组、失败面板、结果卡那几套规则已经测过了，重写就是重造 bug。
 */
function graphDrawerHtml(graph, focusId) {
  const node = graph.byId.get(focusId);
  // 批量会话进行中时，抽屉展示的是**会话进度**（那是跨意图的），带返回入口。
  // 否则展示焦点意图的执行记录。
  if (state.right === 'explore' && state.explore) {
    return `<div class="gd-in">
      <div class="gd-hd">
        <div>
          <div class="gd-t">批量摸底</div>
          <div class="gd-s">${esc(state.explore.label || '')} · ${esc(exploreProgressCopy(state.explore))}</div>
        </div>
        <span class="gd-x" data-act="back-to-intent" title="返回这条意图">✕</span>
      </div>
      <div class="gd-bd">${explorePanel()}</div>
    </div>`;
  }
  if (!node) return '';
  const outs = graph.edges.filter((edge) => edge.from === focusId);
  const live = graph.live.get(focusId) ?? new Set();
  const parents = graph.edges.filter((edge) => edge.to === focusId);
  // 抽屉**不再自带 show 类**：显隐由 renderGraph 加在外层 #gdrawer 上
  // （并列布局要靠它控制宽度，show 挂在内层的话外层宽度不会变）
  return `<div class="gd-in">
    <div class="gd-hd">
      <div>
        <div class="gd-t">${esc(node.name)}</div>
        <div class="gd-s">${node.runs
          ? `${esc(node.id)} · 跑过 ${node.runs} 次 · 最近 ${esc(node.status)} ${esc(formatDuration(node.ms))}`
          : `${esc(node.id)} · ${node.source === 'outside' ? '不在当前档案能力里' : '还没跑过'}`}</div>
      </div>
      <span class="gd-x" data-act="close-drawer" title="收起">✕</span>
    </div>
    <div class="gd-bd">
      <div class="gd-sec">下一步 · 前瞻 ${live.size} / 建议过 ${outs.length}</div>
      ${outs.length ? outs.map((edge) => {
        const kind = graphEdgeKind(edge, focusId, graph.live);
        const label = graphEdgeLabel(edge, kind);
        return `<span class="gd-chip ${kind}" data-act="focus-intent" data-id="${esc(edge.to)}"
            title="${esc(edge.reason || '')}">${esc(graph.byId.get(edge.to)?.name ?? edge.to)}
            <i>${esc(label.text)}</i></span>`;
      }).join('') : '<span class="gd-chip none">没观测过下一步</span>'}
      ${edgeHint(node)}

      <div class="gd-sec">这条从哪来</div>
      <div class="gd-note">${parents.length
        ? `被 ${parents.map((edge) => esc(graph.byId.get(edge.from)?.name ?? edge.from)).join('、')} 建议过。`
        : '没有任何边指向它——它是你自己发起的起点。'}
        ${node.gate === 'locked' ? '<br>当前上下文还差槽位，补上就能直接跑。' : ''}</div>

      <div class="gd-sec">${node.runs ? '这条的执行记录' : '执行记录'}</div>
      <div class="gd-stream">${streamPanels(focusId)}</div>
    </div>
  </div>`;
}

function historyStats(intentId) {
  const done = state.runs.filter((run) => run.intentId === intentId && run.status === 'SUCCESS');
  if (!done.length) return { count: 0, seconds: null, tokens: null };
  const seconds = done.reduce((sum, run) => sum + (run.durationMs || 0), 0) / done.length / 1000;
  const tokens = done.reduce((sum, run) => sum + (run.usage?.totalTokens || 0), 0) / done.length;
  return { count: done.length, seconds, tokens };
}

/** 单用户并发 Run = 1（技术方案 §7.7）：同一用户的多个 Run 共享上下文栈与权限，并发会带来"结果对不上"。 */
function busyRun() {
  return state.session.find((run) => run.status === 'RUNNING') ?? null;
}

function renderRunBar() {
  const entry = state.current ? entryOf(state.current) : (state.focus ? entryOf(state.focus.intentId) : null);
  if (!entry) {
    // 下方已经有执行记录时，不再摆一整块"从左边挑一件事"的空态——
    // 一块说"还没有东西"的空态压在已经跑出来的结果上面，是自相矛盾的。
    const hasRuns = state.session.length > 0 || state.runs.length > 0;
    if (hasRuns) {
      return `<div class="runbar idle compact">
        <div class="rb1">
          <span class="rb-hint">没对准哪条意图 —— 左栏、顶部命令面板里点一条，它会出现在这里</span>
          <span style="margin-left:auto;"><span class="btn" data-act="open-palette">全部能力</span></span>
        </div>
      </div>`;
    }
    return `<div class="runbar idle">
      <div class="rb1"><span class="rb-title">从左边挑一件事</span></div>
      <div class="rb2"><span class="rb-hint">
        上下文、待办、我常做的、批量摸底里的任何一条点开都会出现在这里。</span>
        <span style="margin-left:auto;"><span class="btn pri" data-act="open-palette">全部能力</span></span></div>
    </div>`;
  }
  const gate = satisfiability(entry, state.slots, registryOf(state.me));
  const executor = entry.executorType || null;
  const stats = historyStats(entry.id);
  const params = paramsFromSlots();
  const chips = Object.entries(params).map(([key, value]) =>
    `<span class="param">${esc(key)}&nbsp;<b>${esc(Array.isArray(value) ? `${value.length} 个` : value)}</b></span>`).join('');
  const missing = (entry.context ?? []).filter((field) => field.required).map((field) => field.key)
    .filter((key) => !(key in params)).map((key) =>
      `<span class="param" style="border-color:#DDE2EA;color:#98A2B3;">${esc(key)}&nbsp;<b style="color:#98A2B3;">未用</b></span>`).join('');
  const objectLabel = objectLabelFromSlots();
  const gateTag = gate.state === 'locked'
    ? `<span class="pill a" style="margin-left:6px;">还差「${esc(gate.missing.join('、'))}」</span>`
    : (gate.state === 'unknown' ? '<span class="pill n" style="margin-left:6px;">参数待确认</span>' : '');
  return `<div class="runbar">
    <div class="rb1">
      ${executor ? `<span class="ctx tag" style="border-radius:4px;padding:2px 6px;">${esc(EXECUTOR_LABEL[executor] || executor)}</span>` : ''}
      <span class="rb-title">${esc(entry.name)}</span>
      ${objectLabel ? `<span class="tag obj">${esc(objectLabel)}</span>` : ''}
      ${gateTag}
      <span style="margin-left:auto;font-size:11.5px;color:#98A2B3;">${esc(entry.id)}</span>
    </div>
    <div class="rb2">
      ${chips}${missing}
      <span class="cost ${esc(executor || '')}">${esc(costBeforeCopy(executor, stats))}</span>
      <span style="font-size:11px;color:#98A2B3;">${stats.count
        ? `本意图最近 ${stats.count} 次均值` : '本意图还没有历史，所以不给预估'}</span>
      <span style="margin-left:auto;display:flex;gap:6px;">
        <span class="btn ghost" data-act="copy-params" data-id="${esc(entry.id)}">复制参数</span>
        ${busyRun()
          ? `<span class="btn off" title="同一时刻只跑一件事：等 ${esc(busyRun().intentName || busyRun().intentId)} 跑完">先等它跑完</span>`
          : `<span class="btn pri" data-act="run-intent" data-id="${esc(entry.id)}">执行</span>`}
      </span>
    </div>
  </div>`;
}

function objectLabelFromSlots() {
  const slot = state.slots.find((item) => hasValue(item) && slotTypeMeta(item.type)?.entity);
  return slot ? `${slot.label} · ${slot.title}` : '';
}

/**
 * 右栏：**执行流**，不是日志墙。
 *
 * 改版前这里是同一意图的 6 条 Run 平铺，每条占一整行，其中 4 条是 44~83 毫秒的失败
 * （配置模型密钥之前的脏记录）——等于**把一个问题重复六遍**，而顶部还留着一块空态。
 *
 * 现在：
 *   · 同一意图的连续 Run **折叠成一组**（`今日跟进优先级 · 跑了 6 次 · 最近 23 秒前`）；
 *   · 组内只展开最新一条，其余折成一行；
 *   · 失败**单独成一组**，标注是哪段时间的记录，并给「清理」；
 *   · 有内容时不再渲染空态。
 */
function streamPanels(onlyIntentId = null) {
  const scope = (run) => !onlyIntentId || run.intentId === onlyIntentId;
  const local = state.session.filter((run) => scope(run) && !state.hiddenRuns.includes(run.traceId));
  const localIds = new Set(local.map((run) => run.traceId).filter(Boolean));
  const focus = state.focus && scope(state.focus) ? state.focus : null;
  const parts = [];
  if (!onlyIntentId && state.right === 'explore' && state.explore) parts.push(explorePanel());

  // 服务端历史里去掉本地已有的、正在回看的、以及被清理掉的
  const history = state.runs.filter((run) => scope(run) && !localIds.has(run.traceId)
    && run.traceId !== focus?.traceId
    && !state.hiddenRuns.includes(run.traceId));
  const all = [...local, ...history];

  // 回看历史时把那一跑展开成完整结果卡：点「做过的」却只看到一行折叠，等于没回看
  if (focus && focus.status !== 'RUNNING' && !state.hiddenRuns.includes(focus.traceId)) {
    parts.push(renderRunCard(focus));
  }

  const running = all.filter((run) => run.status === 'RUNNING');
  parts.push(...running.map(renderRunCard));

  const settled = all.filter((run) => run.status !== 'RUNNING');
  const ok = settled.filter((run) => run.status === 'SUCCESS');
  const failed = settled.filter((run) => run.status !== 'SUCCESS');

  for (const group of groupRunsByIntent(ok)) {
    parts.push(runGroupPanel(group, focus));
  }
  if (failed.length) parts.push(failedRunsPanel(failed));

  if (parts.length === 0) {
    parts.push(`<div class="panel"><div class="p-bd">
      ${emptyBox(onlyIntentId ? '这条还没有执行记录' : '这一侧是执行流',
        onlyIntentId ? '跑过一次之后，结果、依据与当时的上下文快照都会留在这里。'
          : '你发起、或回看的每一次执行都留在这里：结果、依据、当时用的上下文快照。')}
    </div></div>`);
  }
  return parts.join('');
}

/** 成功的 Run 按意图分组，组内按时间倒序。 */
function groupRunsByIntent(runs) {
  const buckets = new Map();
  for (const run of runs) {
    const key = run.intentId || run.traceId;
    if (!buckets.has(key)) buckets.set(key, []);
    buckets.get(key).push(run);
  }
  return [...buckets.values()].map((list) => {
    const sorted = [...list].sort((a, b) => (Number(b.startedAt) || 0) - (Number(a.startedAt) || 0));
    return { key: sorted[0].intentId, latest: sorted[0], rest: sorted.slice(1), runs: sorted };
  }).sort((a, b) => (Number(b.latest.startedAt) || 0) - (Number(a.latest.startedAt) || 0));
}

/** 相对时间：执行流看的是"多久之前跑过"，不是绝对时间戳。 */
function relativeTime(ts) {
  const time = Number(ts);
  if (!time) return '';
  const seconds = Math.max(0, Math.round((Date.now() - time) / 1000));
  if (seconds < 60) return `${seconds} 秒前`;
  if (seconds < 3600) return `${Math.round(seconds / 60)} 分钟前`;
  if (seconds < 86400) return `${Math.round(seconds / 3600)} 小时前`;
  return timeOf(ts);
}

/**
 * 一个意图一组。
 * 组头写清"跑了多少次、最近一次什么时候"，组内只展开最新一条——
 * 一屏能同时看到"当前结果"和"我最近在反复跑什么"。
 */
function runGroupPanel(group, focus) {
  const open = state.runOpen[group.key] !== false;   // 默认展开
  const name = group.latest.intentName || group.key;
  const more = group.rest.length;
  const head = `<div class="panel group-panel">
    <div class="run-group-hd" data-act="toggle-run-group" data-id="${esc(group.key)}">
      <span class="chev">${open ? '▾' : '▸'}</span>
      <span class="gt">${esc(name)}</span>
      <span class="gn">跑了 ${group.runs.length} 次</span>
      <span class="when">最近 ${esc(relativeTime(group.latest.startedAt))}</span>
    </div>`;
  if (!open) return `${head}</div>`;
  const body = `<div class="run-group-bd">${renderRunCard(group.latest)}</div>`;
  const rest = more
    ? `<div class="run-group-rest">${group.rest.map((run) => `<div class="mini-run"
        data-act="open-run" data-id="${esc(run.traceId)}">
        <span class="badge-mini ok">完成</span>
        <span class="mt">${esc(objectLabelOf(run) || run.intentName || run.intentId)}</span>
        <span class="mm">${esc(timeOf(run.startedAt))} · ${esc(formatDuration(run.durationMs))}${run.usage?.totalTokens ? ` · ${esc(formatTokens(run.usage.totalTokens))}` : ''}</span>
        <span class="btn sm">展开</span>
      </div>`).join('')}</div>`
    : '';
  return `${head}${body}${rest}</div>`;
}

/**
 * 失败单独成组。
 * 为什么必须分开：失败和成功混排，用户既看不出"哪些没跑成"，也会怀疑系统在正常运转。
 * 「清理」只清**本页显示**，不动服务端留痕——留痕是审计资产，不该被一次点击抹掉。
 */
function failedRunsPanel(failed) {
  const open = !!state.failedRunsOpen;
  const when = failed.map((run) => timeOf(run.startedAt)).filter(Boolean);
  const span = when.length > 1 ? `${when[when.length - 1]} ~ ${when[0]}` : (when[0] ?? '');
  const head = `<div class="panel group-panel fail">
    <div class="run-group-hd" data-act="toggle-failed-runs">
      <span class="chev">${open ? '▾' : '▸'}</span>
      <span class="gt">没跑成</span>
      <span class="gn">${failed.length} 次</span>
      <span class="when">${esc(span)}</span>
      <span class="act" data-act="clear-failed-runs" title="只是从这一屏移除，服务端留痕不受影响">清理</span>
    </div>`;
  if (!open) return `${head}</div>`;
  const rows = failed.map((run) => {
    const copy = errorCopy(run.error?.code, { message: run.error?.message, myRoles: state.me?.roles });
    return `<div class="mini-run" data-act="open-run" data-id="${esc(run.traceId)}">
      <span class="badge-mini fail">${esc(copy.title || '没跑成')}</span>
      <span class="mt">${esc(run.intentName || run.intentId)}</span>
      <span class="mm">${esc(timeOf(run.startedAt))} · ${esc(formatDuration(run.durationMs))}</span>
      <span class="btn sm">展开</span>
    </div>`;
  }).join('');
  return `${head}<div class="run-group-bd">${rows}</div></div>`;
}

function renderRunCard(run) {
  const statusCopy = runStatusCopy(run);
  const head = `<div class="p-hd">
    <span class="pt">${esc(run.intentName || run.intentId)}</span>
    ${run.status === 'RUNNING' ? '<span class="spinner"></span>' : ''}
    <span class="tag ${run.status === 'SUCCESS' ? 'obj' : 'obj'}" style="${run.status === 'FAILED' ? 'color:#B91C1C;border-color:#FECACA;background:#FEF2F2;' : ''}">
      ${esc(statusCopy.badge)}</span>
    ${objectLabelFromSlots() && run.local ? `<span class="tag obj">${esc(objectLabelFromSlots())}</span>` : ''}
    <span class="acts">
      ${hasEvidence(run) ? `<span class="btn sm" data-act="toggle-evidence" data-id="${esc(run.traceId)}">依据</span>` : ''}
      <span class="btn sm" data-act="copy-result" data-id="${esc(run.traceId || '')}">复制结果</span>
      <span class="btn sm" data-act="rerun" data-id="${esc(run.intentId)}">重跑</span>
    </span>
  </div>`;
  if (run.status === 'RUNNING') {
    // 这里**不许**出现进度条。
    // 设计稿 §6.3 / 技术方案 §7.1 / 文案清单 §7 禁忌③ 三处都明令"不造假进度"：
    // `/intent/execute` 是同步调用，返回之前拿不到任何中间事件，任何百分比都是编的。
    // 以前这里有一条硬编码 width:38% 的条，文案诚实、图形撒谎，是本工作台最不该有的组合。
    return `<div class="panel">${head}<div class="p-bd">
      <div class="running">
        <span class="spinner"></span>
        <span class="rt">${esc(statusCopy.title)}</span>
        <span class="rdots" aria-hidden="true"><i></i><i></i><i></i></span>
      </div>
      <div class="rhint">
        ${esc(statusCopy.detail || '执行器没有返回阶段事件，所以这里只有"已用时"，没有编出来的进度。')}
      </div>
    </div></div>`;
  }
  if (run.status === 'FAILED') {
    const copy = errorCopy(run.error?.code, { message: run.error?.message, myRoles: state.me?.roles });
    return `<div class="panel">${head}<div class="p-bd">
      <div class="errbox"><span class="ec">${esc(copy.title)}</span>${esc(copy.body)}</div>
      ${run.error?.message ? `<div class="rawjson" style="margin-top:10px;">${esc(run.error.code || '')} · ${esc(run.error.message)}</div>` : ''}
      <div style="display:flex;gap:6px;margin-top:10px;">
        <span class="btn sm" data-act="rerun" data-id="${esc(run.intentId)}">${esc(copy.primary || '重跑')}</span>
      </div>
    </div></div>`;
  }
  return `<div class="panel">${head}
    <div class="p-bd">
      ${briefHtml(run)}
      ${relayOf(run)}
      ${detailHtml(run)}
    </div>
    ${evidenceOf(run)}
  </div>`;
}

/* ---- 结果卡：标准输出信封 title / summary / blocks / followups / nextIntents ---- */

const BLOCK_KINDS = ['text', 'kv', 'table', 'list', 'badges'];

/**
 * **默认态 = 一行结论 + 下一步意图。**
 *
 * 为什么不让结果卡默认铺开：一次 agent 执行能产出 8 项总览 + 5 件事 + 2 条明细，
 * 全铺出来就是一堵文字墙——用户真正要的是"这次跑出什么结论、接下来点哪个"，
 * 而不是把生成的内容从头读到尾。正文收在「查看详情」里，想看随时展开。
 *
 * 三个部分固定：
 *   ① 结论：摘要的第一句（数字加粗），一句话说完"最该先做的是什么"；
 *   ② 下一步意图：接力区（nextIntents / followups / 换时间窗），**这是默认态里唯一的动作来源**；
 *   ③ 查看详情：展开完整结果（总览 / 行动卡 / 明细），并标出里面有什么。
 */
function briefHtml(run) {
  const output = run.output ?? {};
  const lead = briefLead(output.summary) || briefLead(textOfFirstTextBlock(output));
  if (!lead) return '';
  return `<div class="rc-brief">${emphasizeNumbers(lead)}</div>`;
}

/**
 * 结论：从摘要里取**最多两句**（或凑到 `limit` 为止，至少一句）。
 *
 * 为什么不是"机械切第一句"，也不是"取到字数上限"：
 * 实测两条真实意图的摘要——
 *   A）首句 49 字，两句 69 字，三句 93 字；
 *   B）首句只有 17 字（「结论：预警档，健康度 28/100。」**连是谁都没说**），第二句 68 字。
 * 纯按字数上限（110）会把 A 的三句全带上（93 字，接近文字墙），
 * 纯按第一句又会让 B 只剩 17 字。
 * **最多两句**同时满足：A 停在 69 字，B 补到 85 字且带上了主体信息。
 */
function briefLead(text, limit = 110, maxSentences = 2) {
  const raw = String(text ?? '').trim();
  if (!raw) return '';
  const sentences = raw.split(/(?<=[。；;！!?])/).map((part) => part.trim()).filter(Boolean);
  let out = '';
  let taken = 0;
  for (const sentence of (sentences.length ? sentences : [raw])) {
    if (taken >= maxSentences) break;
    if (out && out.length + sentence.length > limit) break;
    out += sentence;
    taken += 1;
  }
  if (!out) out = sentences[0] ?? raw;
  return out.length > limit ? `${out.slice(0, limit)}…` : out;
}

function textOfFirstTextBlock(output) {
  const block = (output?.blocks ?? []).find((item) => item?.kind === 'text' && item.text);
  return block?.text ?? '';
}

/**
 * 「查看详情」入口 + 展开后的完整内容。
 * 折叠状态按 Run 记（`state.detailOpen`），换账号会清掉——那是上一个人的视图。
 */
function detailHtml(run) {
  const output = run.output;
  if (!output || typeof output !== 'object') {
    return `<div class="rc-detail open">
      ${emptyBox('执行器没有返回结构化结果', '标准信封要求 title / summary / blocks；这里照原样显示，不做美化。')}
      <div class="rawjson">${esc(JSON.stringify(run.output ?? null))}</div>
    </div>`;
  }
  const key = runKey(run);
  const open = !!state.detailOpen[key];
  const hint = detailHint(output.blocks ?? []);
  const hasDetail = !!(output.blocks ?? []).length || !!(output.followups ?? []).length;
  if (!hasDetail) return '';
  return `<div class="rc-toggle" data-act="toggle-detail" data-id="${esc(key)}">
      <span class="chev">${open ? '▾' : '▸'}</span>${open ? '收起详情' : '查看详情'}
      ${!open && hint ? `<span class="rh">${esc(hint)}</span>` : ''}
    </div>
    ${open ? `<div class="rc-detail">${renderOutput(run, { skipLead: true })}</div>` : ''}`;
}

/** Run 的稳定标识：留痕 id 优先，本地还没回来的用「意图 + 开始时间」。 */
function runKey(run) {
  return run.traceId || `${run.intentId}@${run.startedAt ?? ''}`;
}

/** 详情里有什么：`8 项总览 · 5 件事 · 2 条明细`。用户点之前该知道值不值得点。 */
function detailHint(blocks) {
  const units = { kv: '项总览', badges: '项总览', table: '件事', list: '条明细', text: '段文字' };
  return (blocks ?? []).map((block) => {
    // text 块没有 items/rows，它有 text——按"段"计数
    const count = block?.kind === 'text'
      ? (block.text ? 1 : 0)
      : (block?.items ?? block?.rows ?? []).length;
    if (!count) return '';
    return `${count} ${units[block.kind] ?? '项'}`;
  }).filter(Boolean).join(' · ');
}

/**
 * 结果卡渲染。
 *
 * 改版前它是一堵文字墙：一段 100 多字的摘要、一行挤 8 个指标的"总览"、
 * 一个 5 行 × 5 列且每格都是完整句子的表格，全篇同一个字号同一种颜色——
 * **没有任何视觉锚点，读完不知道先看哪**。
 *
 * 现在的视觉语法（整页统一）：
 *   1. **结论先行**：摘要放大、限宽 760px（生成类文本的舒适阅读宽度），里面的数字挑出来加粗；
 *   2. **数字优先**：kv 从"标签 值"两列小字改成**指标卡**（数字 19px 等宽加粗 + 标签 11px 灰）；
 *   3. **表格变卡片**：每行一张卡（编号 + 对象当标题 + 动作当正文 + 其余降为元信息）；
 *   4. **每种块有固定长相**：list 给圆点、badges 给色片、text 段落化。
 */
function renderOutput(run, { skipLead = false } = {}) {
  const output = run.output;
  if (!output || typeof output !== 'object') {
    return `${emptyBox('执行器没有返回结构化结果', '标准信封要求 title / summary / blocks；这里照原样显示，不做美化。')}
      <div class="rawjson">${esc(JSON.stringify(run.output ?? null))}</div>`;
  }
  const parts = [];
  // 默认态已经把第一句当"结论"显示了，展开详情时不再重复整段摘要
  if (output.summary && !skipLead) parts.push(`<div class="out-lead">${emphasizeNumbers(output.summary)}</div>`);
  const blocks = Array.isArray(output.blocks) ? output.blocks : [];
  parts.push(...blocks.map(renderBlock));
  const followups = Array.isArray(output.followups) ? output.followups.filter(Boolean) : [];
  if (followups.length) {
    parts.push(`<div class="blk-title">还要你跟进（${followups.length}）</div>
      <div class="act-cards">${followups.map((text, index) =>
        `<div class="act-card plain"><span class="ac-no">${index + 1}</span>
          <div class="ac-main"><div class="ac-body">${esc(text)}</div></div></div>`).join('')}</div>`);
  }
  return parts.join('') || emptyBox('结果里没有可渲染的内容', '');
}

/**
 * 把正文里的**带单位数字**挑出来加粗。
 *
 * 业务人员读结果时扫的是数字（金额、笔数、天数、耗时），但它们现在和正文一个字号，
 * 只能逐字读。这里只做"给数字换样式"，**不改一个字、不做任何语义解释**。
 * 只认带单位的数字（万/元/笔/位/条/份/天/个/分钟/小时/%），避免把日期、序号也点亮。
 */
function emphasizeNumbers(text) {
  const raw = String(text ?? '');
  const re = /\d[\d,]*(?:\.\d+)?\s*(?:万|元|笔|位|条|份|天|个|分钟|小时|%)/g;
  let out = '';
  let last = 0;
  let hit;
  while ((hit = re.exec(raw)) !== null) {
    out += esc(raw.slice(last, hit.index)) + `<b class="n">${esc(hit[0].trim())}</b>`;
    last = hit.index + hit[0].length;
  }
  return out + esc(raw.slice(last));
}

function renderBlock(block, index) {
  if (!block || typeof block !== 'object' || !BLOCK_KINDS.includes(block.kind)) {
    return `<div class="blk-title">块 ${index + 1}</div>
      <div class="rawjson">${esc(JSON.stringify(block))}</div>`;
  }
  const title = block.title ? `<div class="blk-title">${esc(block.title)}</div>` : '';
  switch (block.kind) {
    case 'text':
      return `${title}<div class="prose">${proseHtml(block.text)}</div>`;
    case 'kv':
      return `${title}${metricsHtml(block.items)}`;
    case 'table':
      return `${title}${tableHtml(block)}`;
    case 'list':
      return `${title}<ul class="points">${(block.items || []).map((item) => {
        const label = item && typeof item === 'object' ? item.label : item;
        const value = item && typeof item === 'object' ? item.value : '';
        return `<li><b>${esc(label)}</b>${value ? `<span>${esc(value)}</span>` : ''}</li>`;
      }).join('')}</ul>`;
    case 'badges':
      return `${title}<div class="chips">${(block.items || []).map((item) =>
        `<span class="pill ${pillTone(item, block.level)}">${esc(item?.label)}${item?.value ? ` ${esc(item.value)}` : ''}</span>`).join('')}</div>`;
    default:
      return '';
  }
}

/** kv → 指标卡：一眼看到的是数字，标签退到下面当注释。 */
function metricsHtml(items) {
  const cells = (items || []).map((item) => {
    const raw = String(item?.value ?? '');
    // 有数字的当"指标"（大号等宽）；纯文字（如"已完成"）当"标签值"，不假装成数字
    const numeric = /\d/.test(raw);
    return `<div class="metric${numeric ? '' : ' word'}">
      <div class="mv">${esc(raw)}</div>
      <div class="ml">${esc(item?.label ?? '')}</div>
    </div>`;
  }).join('');
  return `<div class="metrics">${cells}</div>`;
}

/** 纯文字段落：按空行切段；行首是 `-`/`•`/`1.` 的连续行合成一个列表。 */
function proseHtml(text) {
  const lines = String(text ?? '').split('\n');
  const out = [];
  let bullets = [];
  const flush = () => {
    if (bullets.length) {
      out.push(`<ul class="points">${bullets.map((line) => `<li><span>${esc(line)}</span></li>`).join('')}</ul>`);
      bullets = [];
    }
  };
  for (const raw of lines) {
    const line = raw.trim();
    if (!line) { flush(); continue; }
    if (/^([-*•]|\d+[.)、])\s*/.test(line)) {
      bullets.push(line.replace(/^([-*•]|\d+[.)、])\s*/, ''));
      continue;
    }
    flush();
    out.push(`<p>${emphasizeNumbers(line)}</p>`);
  }
  flush();
  return out.join('');
}

/**
 * table → 卡片行。
 *
 * 意图产出的表格常常是「优先级 | 对象 | 具体动作 | 为什么现在做 | 预计耗时」这种
 * 每格一句完整话的形状，一行铺 400 字，读起来是一堵墙。
 * 转成卡片：首列当编号、次列当标题、第三列当正文、其余降为 11px 元信息。
 *
 * **判不了就退回普通表格**（只是样式重做过）：列数 < 3、或首列不是短编号、
 * 或元信息超过 3 条（说明它更像一张数据表而不是任务清单）——不硬转。
 */
function tableHtml(block) {
  const columns = (block.columns ?? []).map((col) => String(col ?? ''));
  const rows = (block.rows ?? []).map((row) => (row ?? []).map((cell) => String(cell ?? '')));
  if (columns.length >= 3 && rows.length) {
    const cards = rows.map((row) => rowToCard(columns, row));
    if (cards.every(Boolean)) return `<div class="act-cards">${cards.join('')}</div>`;
  }
  const head = columns.map((col) => `<th>${esc(col)}</th>`).join('');
  const body = rows.map((row) =>
    `<tr>${row.map((cell) => `<td>${esc(cell)}</td>`).join('')}</tr>`).join('');
  return `<table class="rtable"><thead><tr>${head}</tr></thead><tbody>${body}</tbody></table>`;
}

/** 一行表格 → 一张行动卡。返回 null 表示"这行不适合转卡片"。 */
function rowToCard(columns, row) {
  const first = String(row[0] ?? '').trim();
  const hasBadge = first.length <= 4 && /\d/.test(first);
  const title = String(row[1] ?? '').trim();
  const body = String(row[2] ?? '').trim();
  const metas = row.slice(3).map((cell, i) => ({ label: columns[i + 3], value: String(cell ?? '').trim() }))
    .filter((meta) => meta.value);
  // 元信息超过 2 条就说明它更像一张数据表而不是任务清单——转成卡片只会把列名挤成一堆标签
  if (!title || !body || metas.length > 2) return null;
  return `<div class="act-card">
    ${hasBadge ? `<span class="ac-no">${esc(first)}</span>` : ''}
    <div class="ac-main">
      <div class="ac-title">${esc(title)}</div>
      <div class="ac-body">${emphasizeNumbers(body)}</div>
      ${metas.length ? `<div class="ac-metas">${metas.map((meta) =>
        `<span class="ac-meta"><i>${esc(meta.label)}</i>${esc(meta.value)}</span>`).join('')}</div>` : ''}
    </div>
  </div>`;
}

/** 徽标配色只认 value 里的数字阈值与显式 level；不去猜语义。 */
function pillTone(item, level) {
  if (level === 'danger') return 'r';
  if (level === 'warning') return 'a';
  if (level === 'info') return 'n';
  const value = Number(String(item?.value ?? '').replace(/[^\d.-]/g, ''));
  if (Number.isNaN(value)) return 'n';
  if (value >= 80) return 'g';
  if (value >= 60) return 'a';
  return 'r';
}

/**
 * 信封里的「下一步意图」。
 *
 * ⚠️ **必须两种大小写都认**。SDK 序列化出来的字段名是 `nextIntents`（驼峰，见
 * `IntentOutput`），但工作台与内嵌 SDK 两处都写成了 `output.nextIntents`（全小写）——
 * JavaScript 是**区分大小写**的，于是取值恒为 undefined，
 * **「接着办 / 下一步意图」这一块自上线起就没有渲染出来过**（静默失效，不报错）。
 *
 * 这里两种都认：既修掉历史数据，也容错将来某个执行器写成小写。
 */
function nextIntentsOf(output) {
  const list = output?.nextIntents ?? output?.nextintents;
  return Array.isArray(list) ? list : [];
}

/**
 * 接力区。
 * 三个来源，都可点、都真的会做事：followups（原文带进搜索框）、nextIntents（直达）、
 * timeWindow（换个时间窗重跑同一意图）。**不编造**"按行业标准打分"这类没有对应参数的提议。
 */
function relayOf(run) {
  const entry = entryOf(run.intentId);
  const registry = registryOf(state.me);
  const chips = [];
  const output = run.output ?? {};
  const usesTime = (entry?.context ?? []).some((field) => slotTypeOf(field.key, registry) === 'timeWindow');
  if (usesTime) {
    const now = state.slots.find((slot) => slot.type === 'timeWindow')?.values?.[0];
    for (const window of ['近7天', '近30天', '近90天']) {
      if (window === now) continue;
      chips.push(`<span class="rchip" data-act="rerun-tw" data-id="${esc(run.intentId)}"
        data-tw="${esc(window)}">改成${esc(window)}再跑</span>`);
    }
  }
  for (const next of nextIntentsOf(output).slice(0, 3)) {
    if (!next?.intentId) continue;
    // 带上来源：点了这个胶囊去执行时，`_from` 会把"从哪条建议来的"写进留痕，
    // 网络才画得出"走过的"那条边（见 runIntent）。这是闭环的最后一环。
    chips.push(`<span class="rchip next" data-act="run-intent" data-id="${esc(next.intentId)}"
      data-from="${esc(run.intentId)}" data-from-trace="${esc(run.traceId || '')}" data-from-kind="nextIntent"
      title="${esc(next.reason || '')}">${esc(next.title || next.intentId)}</span>`);
  }
  const followups = (Array.isArray(output.followups) ? output.followups : []).slice(0, 2);
  chips.push(...followups.map((text) =>
    `<span class="rchip" data-act="followup-search" data-text="${esc(text)}"
      title="把这句话带进搜索框">${esc(text.length > 22 ? `${text.slice(0, 22)}…` : text)}</span>`));
  if (!chips.length) return '';
  // 默认态收起正文之后，这一行就是结果卡的**主要动作区**——所以它是"下一步"，不是脚注
  return `<div class="relay"><span class="rl">下一步</span>${chips.join('')}</div>`;
}

/* ---- 证据抽屉：steps + 上下文快照，两样都是"你凭什么这么说" ---- */

/** 有步骤或有上下文快照才给"依据"：一个点了什么都不发生的按钮比没有按钮更糟。 */
function hasEvidence(run) {
  return Boolean(run.traceId) && ((run.steps?.length ?? 0) > 0 || (run.contextSnapshot?._slots?.length ?? 0) > 0);
}

function evidenceOf(run) {
  const steps = Array.isArray(run.steps) ? run.steps : [];
  const snapshot = run.contextSnapshot?._slots ?? [];
  return `<details class="evidence" id="ev-${esc(run.traceId || 'live')}">
    <summary>依据：${steps.length} 个执行步骤 · 上下文快照 ${snapshot.length} 项${run.usage?.totalTokens ? ` · ${esc(formatTokens(run.usage.totalTokens))}` : ''}</summary>
    <div style="margin-top:8px;">
      ${steps.length ? steps.map((step, index) => `<div class="step">
        <span class="si">${index + 1}</span>
        <span class="sk">${esc(step.kind || '')}</span>
        <span class="sv"><b>${esc(step.name || '')}</b>
          ${step.argsSummary ? `<div class="muted">入参：${esc(step.argsSummary)}</div>` : ''}
          ${step.resultSummary ? `<div class="muted">结果：${esc(step.resultSummary)}</div>` : ''}
          ${step.error ? `<div style="color:#B91C1C;">${esc(step.error)}</div>` : ''}
          <div class="muted" style="font-size:11px;">${step.ok === false ? '✕ 失败' : '✓'} · ${esc(formatDuration(step.durationMs))}</div>
        </span></div>`).join('')
        : '<div class="muted" style="font-size:11.5px;">这个执行器没有上报步骤。</div>'}
      <div class="sec-title">当时的上下文</div>
      ${snapshot.length
        ? `<div class="kv">${snapshot.map((slot) => `<span class="kk">${esc(slot.label || slot.type)}</span>
            <span>${esc(Array.isArray(slot.values) ? slot.values.join('、') : slot.values)} <span class="muted">(${esc(slot.source || '')})</span></span>`).join('')}</div>`
        : '<div class="muted" style="font-size:11.5px;">这一跑的上下文栈是空的。</div>'}
      ${run.traceId ? `<div class="muted" style="font-size:11px;margin-top:8px;">留痕编号 ${esc(run.traceId)}</div>` : ''}
    </div>
  </details>`;
}

function exploreProgressCopy(session) {
  const done = session.objects.filter((obj) => obj.status !== 'wait').length;
  return `${done} / ${session.objects.length} 已完成 · 已用 ${formatTokens(session.tokens)}`;
}

// ---------------------------------------------------------------- 取数

function applyInitialSlots() {
  const slots = [];
  for (const item of state.me.profile?.initialSlots ?? []) {
    const meta = slotTypeMeta(item.type);
    // 只摆**有值**的初始槽位：空槽位交给「空槽位行」渲染，免得出现一个没有值的"已选中"
    if (!meta || !item.values?.length) continue;
    const values = item.values.map(String);
    // 单值槽位的 label 就是那个值：写成「时间窗 = 时间窗」等于什么都没说。
    // normalizeSlot 在没值时会强制把 label 清空，所以这里可以直接给值。
    slots.push(normalizeSlot(meta.type, values, {
      source: SOURCE_INFERRED,
      label: values.length === 1 ? values[0] : meta.title,
    }));
  }
  state.slots = slots;
}

/**
 * 换账号（登录 / 退出 / 失效重登）时，把上一个账号留下的东西清干净。
 *
 * 这些不是"缓存"，是**上一个人的视图**：右栏执行流、今天先做的折叠状态、批量摸底勾到一半的草稿。
 * 不清的话，客服登录进来会先看到主管刚才跑过的 Run——那正是"看不到别人的 Run"要防的事。
 * 服务端每次都按当前 token 重新取数，所以清掉不会少数据，只是不会多出来。
 */
function resetAccountState() {
  state.session = [];
  state.focus = null;
  state.runs = [];
  state.entries = [];
  state.suggestions = [];
  state.issues = [];
  state.openTodos = null;
  state.paletteKeyword = '';
  state.batchKeyword = '';
  state.exploreDraft = null;
  state.palGroupOpen = {};
  state.navBlock = null;
  state.ctxOpen = false;
  state.runOpen = {};
  state.detailOpen = {};
  state.failedRunsOpen = false;
  state.hiddenRuns = [];
  state.ctxDelta = null;
  state.batchGroupOpen = {};
  // 对象名与聚合结果都是"上一个人看到的东西"，换账号必须一起清
  objectNames.clear();
  aggregateCache = null;
  aggregateCacheKey = null;
}

async function loadMe() {
  resetAccountState();
  state.me = await api(`${API}/me`);
  applyInitialSlots();
  if (state.me.systemName) $('brand-name').textContent = state.me.systemName;
  $('avatar').textContent = (state.me.name || '·').slice(0, 1) || '·';
  state.view = state.me.defaultView || 'self';
  document.title = `${state.me.profile?.title || '意图工作台'} · ${state.me.systemName || 'ruoyi-office'}`;
  renderViewSwitch();
  renderUserMenu();
}

let catalogSeq = 0;
let catalogTimer = null;
let catalogWaiters = [];

/**
 * 拉目录（立即执行，内部实现）。
 * 注意 `page: ''` = 全量：工作台是独立入口，不挂在某个业务页下，"按页面过滤"在这里
 * 退化成"按角色过滤"——这是宿主化的结果，不是漏了页面参数。
 * 槽位只影响 suggestions（服务端双键缓存），entries 的集合不变。
 */
async function refreshCatalogNow() {
  const seq = ++catalogSeq;
  const body = {
    page: '',
    slots: state.slots.filter(hasValue).map((slot) => ({
      type: slot.type, key: slot.key, label: slot.label,
      values: slot.values, mode: slot.mode, source: slot.source,
    })),
    view: state.view,
  };
  try {
    const data = await api(`${API}/catalog`, { method: 'POST', body });
    if (seq !== catalogSeq) return;   // 乱序返回时，只认最后一次请求
    state.entries = data.entries ?? [];
    state.suggestions = data.suggestions ?? [];
    state.issues = data.issues ?? [];
    const badge = $('nav-badge');
    const urgent = state.suggestions.filter((item) => item.kind === 'item').length;
    badge.textContent = String(urgent);
    badge.hidden = urgent === 0;
    renderLeft();
    renderRight();
    track('wb_catalog_ok', { entries: state.entries.length, suggestions: state.suggestions.length, issues: state.issues.length });
  } catch (e) {
    if (e.unauthorized) { handleUnauthorized(); return; }
    toast(`目录没取到：${e.message}`);
  }
}

/**
 * 拉目录（**带防抖**）。
 *
 * 为什么防抖在这里不是优化而是正确性（技术方案 §5.3）：用户连着改"客户 → 合同 → 时间窗"三个槽位，
 * 每一次都可能触发宿主 6 条事实规则去查业务库。防抖前打 3 次，防抖后只打 1 次。
 *
 * `immediate` 只给首次装配用——那时必须等数据回来才渲染，不能等防抖窗口。
 * 用户显式点「刷新 / 重试」也走 immediate：他点了就该立刻有反应。
 */
function refreshCatalog({ immediate = false } = {}) {
  if (immediate) return refreshCatalogNow();
  clearTimeout(catalogTimer);
  const waiting = new Promise((resolve) => { catalogWaiters.push(resolve); });
  catalogTimer = setTimeout(async () => {
    const waiters = catalogWaiters;
    catalogWaiters = [];
    await refreshCatalogNow();
    for (const done of waiters) done();
  }, CATALOG_DEBOUNCE_MS);
  return waiting;
}

async function refreshRuns() {
  try {
    state.runs = (await api(`${API}/runs?limit=${MAX_RUNS}`)) ?? [];
  } catch (e) {
    if (e.unauthorized) { handleUnauthorized(); return; }
    state.runs = [];
  }
  renderLeft();
  renderRight();
}

// ---------------------------------------------------------------- 执行

function applyResult(run, result) {
  run.traceId = result.traceId;
  run.executorType = result.executorType ?? run.executorType;
  run.steps = result.steps ?? [];
  run.usage = result.usage ?? null;
  run.durationMs = result.durationMs ?? 0;
  run.elapsedSeconds = Math.round((result.durationMs ?? 0) / 1000);
  if (result.status === 'NEED_INPUT') {
    // NEED_INPUT 不是"跑完了"，它是"还没跑"——按失败呈现，并把缺的参数摆在明面上
    run.status = 'FAILED';
    run.missingParams = result.missingParams ?? [];
    run.error = { code: 'MISSING_PARAMS', message: `缺参数：${(result.missingParams ?? []).join('、')}` };
    return;
  }
  run.status = result.status === 'SUCCESS' ? 'SUCCESS' : 'FAILED';
  run.output = result.output ?? null;
  run.error = result.error ?? null;
  if (run.status === 'FAILED' && !run.error) run.error = { code: 'EXECUTOR_ERROR', message: '执行器没有给出错误码' };
}

/**
 * 发起一次执行。
 * 先本地造一条 RUNNING 再发请求：IntentStatus 里没有 RUNNING，服务端也不会回报进度，
 * 所以"执行中"只能是我们自己诚实地标出来的状态，不能假装有阶段流。
 */
async function runIntent(intentId, extraParams = null, options = {}) {
  // 单用户并发 Run = 1（技术方案 §7.7）。闸门放在这里而不是只放在按钮上：
  // 「今天先做」「我该办的」「⌘K」「重跑」都会走到这条路，按钮禁用只能挡住其中一个入口。
  const busy = busyRun();
  if (busy) {
    toast(`同一时刻只跑一件事：等「${busy.intentName || busy.intentId}」跑完再开始`);
    return null;
  }
  const entry = entryOf(intentId);
  // **闭环的最后一环**：把"这次是从哪条建议跳过来的"写进上下文快照。
  //
  // 为什么有用：留痕里原本没有任何来源字段，所以"图上的边是建议还是真事"永远分不清
  // （实测 15 个被建议的目标只有 1 个后来真跑过）。`_from` 一并存进 `contextSnapshot`
  // 之后，那条跳转就成了**事实**，网络会把它画成"走过的"。
  //
  // 为什么不需要改后端：`IntentRuntime.recordTrace` 就是 `Map.copyOf(context)`，
  // 前端传什么就存什么、`/intent/runs` 原样返回。`_` 前缀是既有的平台保留键约定（同 `_slots`）。
  const origin = options.from ?? null;
  const snapshot = contextSnapshot();
  if (origin?.intentId) {
    snapshot._from = { intentId: origin.intentId, traceId: origin.traceId ?? null, kind: origin.kind ?? 'unknown' };
  }
  const run = {
    traceId: null,
    intentId,
    intentName: entry?.name || options.name || intentId,
    executorType: entry?.executorType ?? null,
    status: 'RUNNING',
    startedAt: Date.now(),
    elapsedSeconds: 0,
    params: { ...paramsFromSlots(), ...(extraParams || {}) },
    contextSnapshot: snapshot,
    local: true,
  };
  state.session = [run, ...state.session];
  state.current = intentId;
  state.runningIntent = intentId;
  state.focus = null;
  state.right = 'runs';
  startTicker();
  renderLeft();
  renderRight();
  try {
    const result = await api(`${API}/execute`, {
      method: 'POST',
      body: { intentId, params: run.params, context: run.contextSnapshot },
    });
    applyResult(run, result);
  } catch (e) {
    run.status = 'FAILED';
    run.durationMs = Date.now() - run.startedAt;
    run.error = { code: e.code ? String(e.code) : 'REQUEST_FAILED', message: e.message };
    if (e.unauthorized) { handleUnauthorized(); return; }
  }
  state.runningIntent = null;
  stopTickerIfIdle();
  renderLeft();
  renderRight();
  refreshRuns();
  return run;
}

function startTicker() {
  if (state.tick) return;
  state.tick = setInterval(() => {
    let alive = false;
    for (const run of state.session) {
      if (run.status !== 'RUNNING') continue;
      alive = true;
      run.elapsedSeconds = Math.round((Date.now() - run.startedAt) / 1000);
    }
    if (!alive) { stopTickerIfIdle(); return; }
    renderRight();
  }, 1000);
}

function stopTickerIfIdle() {
  if (state.session.some((run) => run.status === 'RUNNING')) return;
  if (state.tick) { clearInterval(state.tick); state.tick = null; }
  renderRight();
}

/** 打开一条 Run。自己刚跑的直接给；历史走 /runs/{traceId}，越权与不存在都是 404。 */
async function openRun(traceId) {
  const local = state.session.find((run) => run.traceId === traceId);
  if (local) {
    state.focus = local;
    state.current = local.intentId;
    state.right = 'runs';
    renderRight();
    return;
  }
  try {
    const record = await api(`${API}/runs/${encodeURIComponent(traceId)}`);
    state.focus = { ...record, local: false, elapsedSeconds: 0 };
    state.current = record.intentId;
    state.right = 'runs';
    renderRight();
  } catch (e) {
    if (e.status === 404) { toast('这条执行记录不存在（或不属于你）'); return; }
    if (e.unauthorized) { handleUnauthorized(); return; }
    toast(e.message || '读取失败');
  }
}
// ---------------------------------------------------------------- 批量摸底

/**
 * 一次执行，不进右栏的执行流。
 * 批量摸底要跑几十次，每次都往执行流里铺一张卡会把真正的"我正在看的东西"冲掉，
 * 而且这些跑的留痕本来就在服务端（每条都有 traceId，点矩阵格子能逐条打开）。
 */
async function executeOnce(intentId, params) {
  return api(`${API}/execute`, {
    method: 'POST',
    body: { intentId, params, context: contextSnapshot() },
  });
}

/** 上下文里的实体槽位 = 批量摸底的种子对象集（槽位是"我在谈谁"，摸底是"我要扫一批"）。 */
function syncExploreSlot() {
  const draft = state.exploreDraft;
  if (!draft || draft.objects.length) return;
  const slot = state.slots.find((item) => item.type === draft.type && hasValue(item));
  if (!slot) return;
  draft.objects = slot.values.map((id) => ({ id, name: slot.label || id, meta: '' }));
}

function openBatchSheet() {
  const types = (state.me?.slotTypes ?? []).filter((item) => item.entity && item.hasCandidates);
  const fallback = (state.me?.slotTypes ?? []).filter((item) => item.entity);
  const usable = types.length ? types : fallback;
  if (!usable.length) { toast('宿主没有登记任何实体槽位，批量摸底无从选对象'); return; }
  const explore = state.me?.profile?.explore;
  const type = usable.some((item) => item.type === explore?.slotType) ? explore.slotType : usable[0].type;
  state.exploreDraft = {
    type,
    objects: [],
    intentIds: (explore?.intents ?? []).filter((id) => entryOf(id)),
  };
  syncExploreSlot();
  renderBatchSheet();
  openSheet();
}

/** 批量摸底里的一条意图候选（带勾选框）。提到模块层，是因为折叠/展开要能单独重画一组。 */
function batchDraftRow(entry) {
  const draft = state.exploreDraft;
  const on = draft.intentIds.includes(entry.id);
  return `<div class="cand ${on ? 'on' : ''}" data-act="draft-intent" data-id="${esc(entry.id)}">
    <span class="ck ${on ? 'on' : ''}"></span>
    <span class="cn">${esc(entry.name)}<span class="cm" style="margin-left:6px;">${esc(entry.id)}</span></span>
    <span class="cm">${esc(EXECUTOR_LABEL[entry.executorType] || '')}</span>
  </div>`;
}

function renderBatchSheet() {
  const draft = state.exploreDraft;
  const types = (state.me?.slotTypes ?? []).filter((item) => item.entity);
  const meta = slotTypeMeta(draft.type) ?? {};
  const chips = draft.objects.map((obj) => `<span class="fb-chip">${esc(obj.name || obj.id)}
    <span class="sx act" data-act="draft-remove" data-id="${esc(obj.id)}" style="cursor:pointer;">✕</span></span>`).join('');
  // 跟命令面板同一套分组（复用 groupsHtml，不重复实现）。
  // 这里**不截断**：勾选列表藏东西比长列表难用——被藏起来的那条根本勾不上。
  const intentGroups = groupEntries(state.entries);
  const intents = groupsHtml(intentGroups, 'batch', state.batchGroupOpen, '', batchDraftRow);
  $('overlay').innerHTML = `<div class="sheet">
    <div class="sheet-hd"><span class="st">批量摸底</span>
      <span class="cm" style="font-size:11.5px;color:#98A2B3;">一个意图 × 一组对象 × 一次跑完</span>
      <span class="sx" data-act="close-sheet">✕</span></div>
    <div class="sheet-bd">
      <div class="tabs">${types.map((item) => `<span class="tab ${item.type === draft.type ? 'on' : ''}"
        data-act="draft-type" data-id="${esc(item.type)}">${esc(item.title)}</span>`).join('')}</div>
      <input id="batch-kw" type="search" placeholder="搜索${esc(meta.title || '对象')}（留空给我最常用的几条）" autocomplete="off">
      <div id="batch-cands" style="margin-top:8px;min-height:44px;">
        <div class="loading">输入关键字，或直接看默认候选…</div>
      </div>
      <div class="sec-title">已选对象（${draft.objects.length}）</div>
      <div style="display:flex;gap:6px;flex-wrap:wrap;">${chips || '<span class="muted" style="font-size:11.5px;">还没选对象</span>'}</div>
      <div class="sec-title">对这批判对象跑哪几条（已选 ${draft.intentIds.length} / ${state.entries.length}）</div>
      <div>${intents}</div>
      <div class="hintline" style="margin-top:10px;">串行执行：一条失败只标它自己，不影响其它行；
        每一跑都会单独留痕，跑完在矩阵里点格子可以逐条回看。</div>
    </div>
    <div class="sheet-ft">
      <span class="muted" style="font-size:11.5px;">${draft.objects.length} 个对象 × ${draft.intentIds.length} 条意图
        = ${draft.objects.length * draft.intentIds.length} 次执行</span>
      <span style="margin-left:auto;display:flex;gap:6px;">
        <span class="btn" data-act="close-sheet">取消</span>
        <span class="btn pri ${(!draft.objects.length || !draft.intentIds.length) ? '' : ''}"
          data-act="start-batch">开始摸底</span>
      </span>
    </div>
  </div>`;
  const input = $('batch-kw');
  if (input) input.value = state.batchKeyword || '';
  loadBatchCandidates(state.batchKeyword || '');
}

let batchSeq = 0;
let batchTimer = null;

function loadBatchCandidates(keyword = '') {
  const draft = state.exploreDraft;
  const seq = ++batchSeq;
  clearTimeout(batchTimer);
  batchTimer = setTimeout(async () => {
    let list = [];
    try {
      list = await api(`${API}/slots/candidates?type=${encodeURIComponent(draft.type)}&keyword=${encodeURIComponent(keyword)}&limit=10`);
    } catch (e) {
      if (e.unauthorized) { handleUnauthorized(); return; }
      list = [];
    }
    if (seq !== batchSeq) return;
    const box = $('batch-cands');
    if (!box) return;
    const meta = slotTypeMeta(draft.type) ?? {};
    if (!list.length) {
      box.innerHTML = `<div class="empty" style="padding:12px;">没有候选。
        ${meta.hasCandidates === false ? '这个槽位类型宿主没实现候选查询。' : '换个关键字试试。'}</div>`;
      return;
    }
    box.innerHTML = list.map((cand) => {
      const on = draft.objects.some((obj) => obj.id === cand.id);
      return `<div class="cand ${on ? 'on' : ''}" data-act="draft-pick" data-id="${esc(cand.id)}"
        data-name="${esc(cand.name)}">
        <span class="ck ${on ? 'on' : ''}"></span>
        <span class="cn">${esc(cand.name)}</span>
        <span class="cm">${esc(cand.meta || '')}</span>
      </div>`;
    }).join('');
  }, keyword ? 220 : 0);
}
/** 串行跑：先到先服务，失败只标那一行。并发会把宿主的连接池和数据权限缓存打满，收益却只是快一点。 */
async function startBatch() {
  const draft = state.exploreDraft;
  if (!draft || !draft.objects.length || !draft.intentIds.length) {
    toast('至少选一个对象和一条意图');
    return;
  }
  const meta = slotTypeMeta(draft.type) ?? {};
  const objects = draft.objects.slice(0, MAX_BATCH);
  if (objects.length < draft.objects.length) {
    toast(`一次最多 ${MAX_BATCH} 个对象，已取前 ${MAX_BATCH} 个`);
  }
  const session = {
    label: `${meta.title || draft.type}批量摸底 · ${objects.length} 个对象`,
    type: draft.type,
    key: meta.key,
    intentIds: [...draft.intentIds],
    objects: objects.map((obj) => ({ id: obj.id, name: obj.name || obj.id, status: 'wait', results: {} })),
    running: true,
    tokens: 0,
    startedAt: Date.now(),
  };
  state.explore = session;
  state.exploreDraft = null;
  closeSheet();
  state.right = 'explore';
  renderLeft();
  renderRight();

  for (const obj of session.objects) {
    if (!session.running) { obj.status = 'wait'; continue; }
    obj.status = 'run';
    renderRight();
    for (const intentId of session.intentIds) {
      try {
        const result = await executeOnce(intentId, { ...paramsFromSlots(), [session.key]: obj.id });
        obj.results[intentId] = result;
        session.tokens += result.usage?.totalTokens || 0;
        obj.status = result.status === 'SUCCESS' ? 'ok' : 'fail';
      } catch (e) {
        obj.results[intentId] = {
          status: 'FAILED',
          error: { code: e.code ? String(e.code) : 'REQUEST_FAILED', message: e.message },
        };
        obj.status = 'fail';
        if (e.unauthorized) { session.running = false; handleUnauthorized(); return; }
      }
      renderRight();
    }
  }
  session.running = false;
  session.finishedAt = Date.now();
  renderLeft();
  renderRight();
  refreshRuns();
}

/** 矩阵单元格的"结论"：先认 kv/badges（可比的），再退到 summary（不可比，就只给一句话）。 */
function cellHeadline(result) {
  const blocks = Array.isArray(result?.output?.blocks) ? result.output.blocks : [];
  for (const block of blocks) {
    if (block?.kind === 'kv' || block?.kind === 'badges') {
      const item = (block.items ?? [])[0];
      if (item?.label) return `${item.label} ${item.value ?? ''}`.trim();
    }
  }
  const summary = result?.output?.summary;
  if (summary) return summary.length > 30 ? `${summary.slice(0, 30)}…` : summary;
  return '';
}

function exploreCell(obj, intentId) {
  const result = obj.results?.[intentId];
  if (result) {
    if (result.status === 'SUCCESS') {
      const text = cellHeadline(result) || '完成';
      return `<td class="cell" ${result.traceId ? `data-act="open-run" data-id="${esc(result.traceId)}"` : ''}
        title="${esc(result.output?.summary || text)}">${esc(text)}</td>`;
    }
    if (result.status === 'NEED_INPUT') {
      return `<td class="cell"><span class="fail">缺参数</span>
        <div class="muted" style="font-size:11px;">${esc((result.missingParams ?? []).join('、'))}</div></td>`;
    }
    return `<td class="cell fail" title="${esc(result.error?.message || '')}">未跑通
      <div class="muted" style="font-size:11px;">${esc(result.error?.code || '')}</div></td>`;
  }
  if (obj.status === 'run') return '<td><span class="spinner"></span></td>';
  if (obj.status === 'wait') return '<td class="wait">排队</td>';
  return '<td class="wait">—</td>';
}

function explorePanel() {
  const session = state.explore;
  const meta = slotTypeMeta(session.type) ?? {};
  const done = session.objects.filter((obj) => obj.status === 'ok' || obj.status === 'fail').length;
  const ok = session.objects.filter((obj) => obj.status === 'ok').length;
  const bad = session.objects.filter((obj) => obj.status === 'fail').length;
  const head = session.intentIds.map((intentId) =>
    `<th>${esc(entryOf(intentId)?.name || intentId)}</th>`).join('');
  const rows = session.objects.map((obj) => `<tr>
      <td class="obj">${esc(obj.name)}${obj.status === 'fail' ? ' <span class="pill f" style="margin-left:6px;">有问题</span>' : ''}</td>
      ${session.intentIds.map((intentId) => exploreCell(obj, intentId)).join('')}
    </tr>`).join('');
  const steps = `<div class="panel"><div class="p-bd" style="padding:14px 18px;">
    <div class="steps">
      <div class="step done"><span class="sn">✓</span><span class="sx">对象集 <b>${session.objects.length}</b> 个${esc(meta.title || '')}</span></div>
      <div class="step-link"></div>
      <div class="step ${session.running ? 'on' : 'done'}"><span class="sn">${session.running ? '·' : '✓'}</span>
        <span class="sx">意图 <b>${session.intentIds.map((id) => esc(entryOf(id)?.name || id)).join(' / ')}</b></span></div>
      <div class="step-link"></div>
      <div class="step ${session.running ? '' : 'done'}"><span class="sn">${session.running ? '3' : '✓'}</span>
        <span class="sx">${esc(exploreProgressCopy(session))}</span></div>
    </div>
  </div></div>`;
  const matrix = `<div class="panel">
    <div class="p-hd"><span class="pt" style="font-size:13.5px;">矩阵</span>
      <span class="tag obj">${done}/${session.objects.length} 行有结果</span>
      <span class="acts">${ok} 行成功${bad ? ` · ${bad} 行有问题` : ''}</span></div>
    <div class="p-bd" style="padding:0 0 6px;overflow-x:auto;">
      <table class="matrix"><thead><tr><th>对象</th>${head}</tr></thead><tbody>${rows}</tbody></table>
    </div>
  </div>`;
  const acts = session.running
    ? `<span class="btn sm" data-act="stop-batch">停止入队</span>`
    : `<span class="btn sm" data-act="retry-batch">重试未跑通的行</span>
       <span class="btn sm" data-act="rerun-batch">整批再跑一次</span>`;
  return `${steps}${matrix}
    <div class="actionbar">
      <span class="ab-l">${esc(session.label)}</span>
      <span style="width:1px;height:18px;background:#E6E9F0;"></span>
      ${acts}
      <span style="margin-left:auto;font-size:11.5px;color:#98A2B3;">
        ${esc(exploreProgressCopy(session))}${session.running ? ' · 正在跑' : ` · 用时 ${esc(formatDuration((session.finishedAt || Date.now()) - session.startedAt))}`}
      </span>
    </div>`;
}
// ---------------------------------------------------------------- 弹层

function openSheet() { $('overlay').hidden = false; }

function closeSheet() {
  $('overlay').hidden = true;
  $('overlay').innerHTML = '';
  state.exploreDraft = null;
  state.pickState = null;
}

/* ---- 实体选择器（「＋ 加实体」） ---------------------------------------- *
 * 选择方式由**宿主的槽位登记表**决定（`picker` 字段，见《四点优化设计》§4）：
 *   remote  —— 走 /intent/slots/candidates（宿主实现候选 SPI，数据权限自动生效）
 *   literal —— 值就是登记表里的 values
 *   custom  —— 宿主注册的具名选择器（复用宿主已有的实体选择组件）
 *   manual  —— 只能手输编号
 * 契约与降级规则都在 `picker.mjs`（无 DOM、可单测）；这里只负责把它画出来。
 */

/** 实体型槽位（只有它们能进上下文栈）。 */
function entitySlotTypes() {
  return (state.me?.slotTypes ?? []).filter((item) => item.entity);
}

/** remote 选择器的请求器：把契约里的 query 落到宿主端点上。 */
async function candidatesRequest(query) {
  const params = new URLSearchParams({
    type: query.type,
    keyword: query.keyword ?? '',
    limit: String(query.limit),
    // shape=page：新形状（{items,nextCursor,total}）。不带它时老宿主返回数组，
    // 前端的 normalizePickPage 两种都吃——这样"新前端 + 老宿主"不会被打挂。
    shape: 'page',
  });
  if (query.cursor) params.set('cursor', query.cursor);
  return api(`${API}/slots/candidates?${params.toString()}`);
}

/** 按登记项解析出这次要用的选择器（含降级）。 */
function pickerFor(type) {
  return resolvePicker(slotTypeMeta(type) ?? {}, {
    pickers: CONFIG.pickers,
    request: candidatesRequest,
  });
}

async function openPicker(type) {
  const types = entitySlotTypes();
  const picked = types.some((item) => item.type === type) ? type : types[0]?.type;
  if (!picked) { toast('宿主没有登记实体槽位'); return; }
  const picker = pickerFor(picked);

  // custom：直接把控制权交给宿主组件——先画自己的面板再弹宿主的框，会闪一下，很脏
  if (picker.kind === PICKER_CUSTOM) {
    await runCustomPicker(picked, picker);
    return;
  }

  state.pickType = picked;
  state.pickState = {
    keyword: '', cursor: null, items: [], loading: false, more: false,
    degraded: picker.degraded, picked: [],
  };
  renderPicker();
  openSheet();
  if (picker.kind === PICKER_REMOTE) loadPickerCandidates('');
  track('wb_picker_open', { type: picked, kind: picker.kind, degraded: picker.degraded });
}

/** 走宿主注册的选择器：它返回什么我们就收什么，值不经过我们的手。 */
async function runCustomPicker(type, picker) {
  const meta = slotTypeMeta(type) ?? {};
  const current = state.slots.find((slot) => slot.type === type);
  const ctx = pickContextOf(meta, {
    slots: state.slots.filter(hasValue),
    selected: current?.values ?? [],
    limit: DEFAULT_PICK_LIMIT,
  });
  let result = null;
  try {
    result = normalizePickResult(await picker.open(ctx));
  } catch (e) {
    toast(`选择器没打开：${e?.message || e}`);
    return;
  }
  if (!result) return;                       // 用户取消
  commitPick(type, result.values.map((value) => value.id), result.values[0]?.name);
}

function renderPicker() {
  const meta = slotTypeMeta(state.pickType) ?? {};
  const picker = pickerFor(state.pickType);
  const types = entitySlotTypes();
  const multi = !!meta.multi;
  const picked = state.pickState?.picked ?? [];
  const degraded = state.pickState?.degraded;
  const input = picker.kind === PICKER_LITERAL
    ? ''
    : `<input id="pick-kw" type="search" autocomplete="off"
        placeholder="${picker.kind === PICKER_MANUAL ? `直接输入${esc(meta.title)}编号后回车` : `搜索${esc(meta.title)}`}">`;
  const hint = degraded
    ? `<span class="degraded-inline">⚠ ${esc(degraded)}，已退化为手输编号</span>`
    : esc(pickerHintOf(meta));

  $('overlay').innerHTML = `<div class="sheet" style="width:560px;">
    <div class="sheet-hd"><span class="st">加入上下文</span>
      <span class="cm" style="font-size:11.5px;color:#98A2B3;">值只能来自宿主实体或你的显式选择，系统不会替你编</span>
      <span class="sx" data-act="close-sheet">✕</span></div>
    <div class="sheet-bd">
      <div class="tabs">${types.map((item) => `<span class="tab ${item.type === state.pickType ? 'on' : ''}"
        data-act="pick-type" data-id="${esc(item.type)}">${esc(item.title)}</span>`).join('')}</div>
      ${input}
      <div id="pick-cands" style="margin-top:8px;min-height:44px;"></div>
      <div class="hintline" style="margin-top:10px;">${hint}</div>
    </div>
    <div class="sheet-ft">
      <span class="muted" style="font-size:11.5px;">${esc(meta.hint || '')}</span>
      <span style="margin-left:auto;display:flex;gap:6px;">
        ${multi && picked.length
          ? `<span class="btn" data-act="close-sheet">取消</span>
             <span class="btn pri" data-act="pick-confirm">加入 ${picked.length} 个</span>`
          : '<span class="btn" data-act="close-sheet">关闭</span>'}
      </span>
    </div>
  </div>`;
  const kw = $('pick-kw');
  if (kw) kw.value = state.pickState?.keyword ?? '';
  if (picker.kind === PICKER_LITERAL) paintLiteral(literalOf(meta));
  else if (picker.kind === PICKER_MANUAL) paintManual();
}

function literalOf(meta) {
  return (meta?.values ?? []).map((value) => ({ id: String(value), name: String(value) }));
}

/** 带勾选状态的一行：多选槽位允许连着挑几个，最后一起确定。 */
function candRowHtml(cand, checked, tag) {
  return `<div class="cand ${checked ? 'on' : ''}" data-act="pick-cand" data-id="${esc(cand.id)}"
      data-name="${esc(cand.name)}">
    <span class="ck ${checked ? 'on' : ''}"></span>
    <span class="cn">${esc(cand.name)}</span>
    ${cand.meta ? `<span class="cm">${esc(cand.meta)}</span>` : ''}
    <span class="cm">${esc(tag ?? `#${cand.id}`)}</span>
  </div>`;
}

function paintLiteral(values) {
  const box = $('pick-cands');
  if (!box) return;
  const slot = state.slots.find((item) => item.type === state.pickType);
  const picked = state.pickState?.picked ?? [];
  box.innerHTML = values.map((value) => candRowHtml(value,
    (slot?.values ?? []).includes(value.id) || picked.includes(value.id), '可选值')).join('')
    || `<div class="empty" style="padding:12px;"><span class="et">没有可选值</span>
        <span class="eh">宿主没有为这个槽位声明 values。</span></div>`;
}

function paintManual() {
  const box = $('pick-cands');
  if (!box) return;
  const meta = slotTypeMeta(state.pickType) ?? {};
  box.innerHTML = `<div class="empty" style="padding:12px;">
    <span class="et">手输${esc(meta.title)}编号</span>
    <span class="eh">这个槽位类型没有候选查询，也没有可选的枚举值——在上面输入编号后回车加入。</span></div>`;
}

let pickSeq = 0;
let pickTimer = null;

function loadPickerCandidates(keyword, { append = false } = {}) {
  const type = state.pickType;
  const seq = ++pickSeq;
  clearTimeout(pickTimer);
  const paint = async () => {
    const ps = state.pickState;
    if (!ps) return;
    if (!append) { ps.loading = true; }
    const box = $('pick-cands');
    if (box && !append) box.innerHTML = '<div class="loading">取候选…</div>';
    let page = { items: [], nextCursor: null, total: null };
    try {
      page = await pickerFor(type).search({
        type, keyword, limit: DEFAULT_PICK_LIMIT, slots: state.slots.filter(hasValue),
      }, append ? ps.cursor : null);
    } catch (e) {
      if (e.unauthorized) { handleUnauthorized(); return; }
      if (seq !== pickSeq) return;
      // 取不到候选**不许静默**：说清是取数失败，并保底给出"手输编号"这条路
      if ($('pick-cands')) {
        $('pick-cands').innerHTML = `<div class="empty" style="padding:12px;">
          <span class="et">候选暂时取不到</span>
          <span class="eh">可以直接在上面的输入框里输编号后回车加入。</span>
          <span class="btn sm" data-act="pick-more" style="margin-top:8px;">重试</span></div>`;
      }
      return;
    }
    if (seq !== pickSeq || !$('pick-cands')) return;
    ps.loading = false;
    ps.items = append ? [...ps.items, ...page.items] : page.items;
    ps.cursor = page.nextCursor;
    ps.more = !!page.nextCursor;
    ps.keyword = keyword;
    const slot = state.slots.find((item) => item.type === type);
    const picked = ps.picked ?? [];
    const rows = ps.items.map((cand) => candRowHtml(cand,
      (slot?.values ?? []).includes(cand.id) || picked.includes(cand.id))).join('');
    const moreRow = ps.more
      ? '<div class="q-more" data-act="pick-more">加载更多</div>'
      : (ps.items.length ? '<div class="hist-note">没有更多了</div>' : '');
    const literal = literalOf(slotTypeMeta(type)).filter((value) =>
      !ps.items.some((cand) => cand.id === value.id));
    const literalHtml = literal.length
      ? `<div class="sec-title" style="margin:12px 0 6px;">预设值</div>
         ${literal.map((value) => candRowHtml(value,
            (slot?.values ?? []).includes(value.id) || picked.includes(value.id), '可选值')).join('')}`
      : '';
    $('pick-cands').innerHTML = (rows || literalHtml)
      ? `${rows}${moreRow}${literalHtml}`
      : `<div class="empty" style="padding:12px;"><span class="et">没有候选</span>
          <span class="eh">换个关键字，或直接按编号回车加入。</span></div>`;
  };
  // 候选接口会被逐字调用：手输关键字做 220ms 防抖，首次打开立刻取
  if (keyword) pickTimer = setTimeout(paint, 220); else paint();
}

/** 用户挑中了几个值：单选立刻落槽，多选攒着等"加入 N 个"。 */
function pickCandidate(id, name) {
  const meta = slotTypeMeta(state.pickType) ?? {};
  const picker = pickerFor(state.pickType);
  if (!meta.multi) {
    commitPick(state.pickType, [id], name);
    return;
  }
  const ps = state.pickState;
  if (!ps) return;
  const list = ps.picked ?? [];
  const index = list.indexOf(String(id));
  if (index >= 0) list.splice(index, 1); else list.push(String(id));
  ps.picked = list;
  if (picker.kind === PICKER_LITERAL) {
    renderPicker();               // 枚举槽位不分页，整块重画最省事
    return;
  }
  // 候选列表可能很长：只就地改勾选状态，不整块重画（重画会把滚动位置打回顶部）
  const slot = state.slots.find((item) => item.type === state.pickType);
  for (const row of document.querySelectorAll('#pick-cands .cand')) {
    const rid = row.dataset.id;
    const on = list.includes(rid) || (slot?.values ?? []).includes(rid);
    row.classList.toggle('on', on);
    row.querySelector('.ck')?.classList.toggle('on', on);
  }
  renderPickerFooter();
}

/** 就地换底部按钮：整块重画会把勾选状态滚回顶部。 */
function renderPickerFooter() {
  const foot = document.querySelector('#overlay .sheet-ft');
  if (!foot) return;
  const picked = state.pickState?.picked ?? [];
  foot.innerHTML = `<span class="muted" style="font-size:11.5px;">${esc(slotTypeMeta(state.pickType)?.hint || '')}</span>
    <span style="margin-left:auto;display:flex;gap:6px;">
      <span class="btn" data-act="close-sheet">取消</span>
      <span class="btn pri" data-act="pick-confirm">加入 ${picked.length} 个</span>
    </span>`;
}

function commitPick(type, ids, name) {
  const meta = slotTypeMeta(type) ?? {};
  if (!ids.length) return;
  const values = ids.map(String);
  putSlot(type, values, SOURCE_PINNED, values.length === 1 ? (name || values[0]) : `${name || values[0]} 等 ${values.length} 个`);
  closeSheet();
}

/* ---- 意图分组：按"它挂在哪个实体上"分 ---------------------------------- */

/**
 * 一条意图属于哪一组。
 *
 * 依据是 `entry.context` 里**第一个既能对上槽位类型、又是实体**的键——
 * `slotTypeOf()` 走宿主登记表，判不出来返回 `unknown`，绝不猜。
 * 组名直接用槽位的 title，所以那几组是登记表自动产出的，前端零硬编码；
 * 挂不上实体的（backlog / stats / team 这些跨实体的）归到宿主声明的兜底组名。
 *
 * 为什么不用 `entry.id` 的第二段（`crm.customer.*`）当分组键：那是命名习惯，不是契约。
 * `crm.contract.risk-review` 实际要的是 customerId，`crm.product.bundle-recommend` 也是——
 * 按 id 分组会把它俩分错组，而按 context 分不会。
 */
function intentGroupOf(entry) {
  const registry = registryOf(state.me ?? {});
  for (const field of entry.context ?? []) {
    const type = slotTypeOf(field?.key, registry);
    if (type === TYPE_UNKNOWN) continue;
    const meta = slotTypeMeta(type);
    if (meta?.entity) return { key: type, title: meta.title || type };
  }
  return { key: '', title: state.me?.groupFallbackTitle || '其它' };
}

/** 按槽位登记顺序分组；"挂不上实体"那组永远排最后（它是兜底，不是一类业务）。 */
function groupEntries(entries) {
  const order = new Map((state.me?.slotTypes ?? []).map((item, index) => [item.type, index]));
  const buckets = new Map();
  for (const entry of entries) {
    const group = intentGroupOf(entry);
    if (!buckets.has(group.key)) buckets.set(group.key, { ...group, items: [] });
    buckets.get(group.key).items.push(entry);
  }
  const ungrouped = buckets.get('') ?? null;
  buckets.delete('');
  const rest = [...buckets.values()].sort((a, b) =>
    ((order.get(a.key) ?? 999) - (order.get(b.key) ?? 999)) || a.title.localeCompare(b.title));
  return ungrouped ? [...rest, ungrouped] : rest;
}

/**
 * 一组意图的"集体缺同一件东西"。
 *
 * 如果这一组里**所有**意图都 `locked`、且缺的是同一个上下文键，就返回那件东西；
 * 否则返回 null（组里有能跑的，就不该整组收起）。
 *
 * 这是设计稿 §5.1「清空『商机』槽 → 商机类意图**整块收起**（不是置灰一排用不上的按钮）」
 * 的落地方式。它和技术方案 §3.2 是**冲突**的：那边证明了 `entries` 集合只依赖
 * `role + page`、不能随槽位变，所以"收起"永远不能做成集合变化。
 * 唯一不破契约的做法就是这里：**视图层的分组折叠**——entries 一条没少，
 * 只是"这一组现在还用不上"被摆在组头上，并且明确告诉用户缺什么。
 */
function groupGateOf(entries) {
  if (!entries?.length) return null;
  const registry = registryOf(state.me ?? {});
  let commonKey = null;
  for (const entry of entries) {
    const gate = satisfiability(entry, state.slots, registry);
    if (gate.state !== 'locked') return null;      // 有能跑的 → 整组不收起
    if (gate.missing.length !== 1) return null;    // 缺的不止一件 → 说不清，就不说
    const key = gate.missing[0];
    if (commonKey === null) commonKey = key;
    else if (commonKey !== key) return null;
  }
  if (!commonKey) return null;
  const type = slotTypeOf(commonKey, registry);
  if (type === TYPE_UNKNOWN) return null;          // 判不出来的类型绝不猜
  const meta = slotTypeMeta(type);
  return meta?.entity ? { key: commonKey, type, title: meta.title || type } : null;
}

/**
 * 分组是否展开。三条规则，顺序即优先级：
 *   1. **搜索态一律全展开**——搜出来的东西再被折叠藏起来，等于搜索没结果；
 *   2. 用户手动点过就听用户的；
 *   3. 否则只有第一组默认展开（"默认只展开最相关的一组"）。
 */
function groupOpen(openMap, key, index, keyword) {
  if (keyword) return true;
  return key in openMap ? openMap[key] : index === 0;
}

/**
 * 分组头：折叠箭头 + 组名 + 计数。三处列表共用同一种长相。
 *
 * `data-act` 刻意是个**字面量**：静态自检靠 `data-act="X"` ↔ `case 'X'` 对账，
 * 动态拼出来的动作名它看不见——而"点了没反应"恰恰是只有运行时才暴露的缺陷。
 * 差异（在哪块列表里）走 `data-surface`。
 */
function groupHeaderHtml(surface, groupKey, title, count, open, extra = '') {
  return `<div class="grp${open ? ' open' : ''}" data-act="toggle-group"
    data-surface="${esc(surface)}" data-id="${esc(groupKey)}">
    <span class="chev">${open ? '▾' : '▸'}</span>
    <span class="gname">${esc(title)}</span>
    <span class="gn">${count}</span>${extra}
  </div>`;
}

/**
 * 把一批分组铺成 HTML：折叠的组只出组头。
 *
 * 搜索态另给一副长相：组头是**纯标签**，没有箭头也点不动。因为搜索态下所有组恒定展开
 * （规则见 `groupOpen`），可点的组头就会变成"点了没反应"——那正是这个页面要消灭的东西。
 */
function groupsHtml(groups, surface, openMap, keyword, rowHtml) {
  return groups.map((group, index) => {
    // 整组都缺同一件东西 → 默认收起，并在组头写清缺什么。
    // 搜索态例外：搜出来的东西再被收起，等于搜索没结果（与 groupOpen 的第 1 条同源）。
    const gate = groupGateOf(group.items);
    // "用户点过就听用户的"优先于自动收起——否则点了箭头也不展开，又变成"点了没反应"
    const autoClosed = !!gate && !keyword && !(group.key in openMap);
    const open = autoClosed ? false : groupOpen(openMap, group.key, index, keyword);
    const gateTag = gate ? `<span class="gate">需要「${esc(gate.title)}」</span>` : '';
    const head = keyword
      ? `<div class="grp static"><span class="chev"></span>
          <span class="gname">${esc(group.title)}</span>
          <span class="gn">${group.items.length}</span>${gateTag}</div>`
      : groupHeaderHtml(surface, group.key, group.title, group.items.length, open, gateTag);
    return open ? `${head}<div class="grp-bd">${group.items.map(rowHtml).join('')}</div>` : head;
  }).join('');
}

/**
 * 三块列表共用一份折叠状态机。
 *
 * 每块要说清四件事：状态放哪（`open`）、搜索词怎么算（`keyword`）、当前分组怎么算（`groups`）、
 * 组内一行怎么画（`rowHtml`）。有了这四件，折叠就能**就地改 DOM**，不必整块重渲染——
 * 重渲染的代价在批量摸底上很具体：每点一次折叠都重跑一次候选查询，长表单还会滚回顶部。
 *
 * `render` 只有命令面板留着，因为它的「展开全部」要一次改十几个组，整体重画更直接。
 */
const GROUP_SURFACES = {
  palette: {
    open: () => state.palGroupOpen,
    keyword: () => state.paletteKeyword.trim(),
    groups: () => groupEntries(paletteRows(state.paletteKeyword).entries),
    rowHtml: (entry) => paletteEntryHtml(entry, registryOf(state.me ?? {})),
    sync: () => syncPaletteExpandAll(),
    render: () => renderPalette(),
  },
  batch: {
    open: () => state.batchGroupOpen,
    keyword: () => '',
    groups: () => groupEntries(state.entries),
    rowHtml: batchDraftRow,
  },
};

/**
 * 就地折叠后，把「展开全部 / 收起全部」的标签重算一遍。
 * 不重算的话，手动把 6 组全点开，标签还写着"展开全部"——那是一个说着假话的按钮。
 */
function syncPaletteExpandAll() {
  const el = document.querySelector('#overlay [data-act="pal-expand-all"]');
  if (!el) return;
  const groups = GROUP_SURFACES.palette.groups();
  const openMap = state.palGroupOpen;
  const allOpen = groups.every((group, index) => groupOpen(openMap, group.key, index, ''));
  el.textContent = allOpen ? '收起全部' : '展开全部';
}

/**
 * 折叠/展开：只动这一个组的 DOM。
 * 组头自己带着 `open` 与否、箭头、以及紧随其后的组体；`openMap` 已在上层翻好，
 * 所以这次局部改动和"下次整块重渲染出来"的结果一致。
 */
function applyGroupToggle(el, group, open) {
  el.classList.toggle('open', open);
  const chev = el.querySelector('.chev');
  if (chev) chev.textContent = open ? '▾' : '▸';
  const next = el.nextElementSibling;
  const body = next && next.classList.contains('grp-bd') ? next : null;
  if (open && !body) {
    const box = document.createElement('div');
    box.className = 'grp-bd';
    box.innerHTML = group.items.map(GROUP_SURFACES[el.dataset.surface].rowHtml).join('');
    el.insertAdjacentElement('afterend', box);
  } else if (!open && body) {
    body.remove();
  }
  GROUP_SURFACES[el.dataset.surface].sync?.();
}
/* ---- 命令面板（⌘K / Ctrl K） ---- */

function openPalette(keyword = '') {
  state.paletteKeyword = keyword;
  renderPalette();
  openSheet();
  const input = $('pal-kw');
  if (input) { input.value = keyword; input.focus(); }
}

function paletteRows(keyword) {
  const kw = keyword.trim().toLowerCase();
  const registry = registryOf(state.me ?? {});
  const hit = (text) => String(text || '').toLowerCase().includes(kw);
  // 这里**不截断**：入口那张 chip 写着「23 条 全部能力」，却只给 8 条，
  // 是"按钮承诺了界面给不了的东西"。全给，靠分组 + 折叠解决长度。
  const entries = state.entries.filter((entry) => !kw
    || hit(entry.name) || hit(entry.id) || hit(entry.description)
    || (entry.aliases ?? []).some(hit));
  const items = state.suggestions.filter((item) => item.kind === 'item'
    && (!kw || hit(item.title) || hit(item.subtitle))).slice(0, 4);
  const runs = state.runs.filter((run) => kw
    && (hit(run.intentName) || hit(run.intentId))).slice(0, 3);
  return { entries, items, runs, registry };
}

/** 命令面板里的一行意图。 */
function paletteEntryHtml(entry, registry) {
  const icon = (EXECUTOR_LABEL[entry.executorType] || '能').slice(0, 1);
  const state_ = satisfiability(entry, state.slots, registry);
  const hint = state_.state === 'locked' ? `需要 ${state_.missing.join('、')} · 参数不全，加入上下文后可直接跑`
    : (state_.state === 'unknown' ? '参数待确认 · 选中后补参' : `参数已从上下文带入 · ${entry.executorType ? EXECUTOR_LABEL[entry.executorType] : '未登记执行器'}`);
  return `<div class="pal-item" data-act="pick-intent" data-id="${esc(entry.id)}">
    <span class="ctx tag" style="border-radius:4px;padding:2px 6px;">${esc(icon)}</span>
    <div><div class="pn"${state_.state !== 'ready' ? ' style="color:#667085;"' : ''}>${esc(entry.name)}</div>
      <div class="pd">${esc(hint)}</div></div>
    <div class="pk">${state_.state === 'ready' ? '<span class="btn sm pri">执行</span>' : '<span class="kbd">Tab</span>'}</div>
  </div>`;
}

function renderPalette() {
  const { entries, items, runs, registry } = paletteRows(state.paletteKeyword);
  const kw = state.paletteKeyword.trim();
  const groups = groupEntries(entries);
  const intentRows = groupsHtml(groups, 'palette', state.palGroupOpen, kw,
    (entry) => paletteEntryHtml(entry, registry));
  const allOpen = !kw && groups.length > 1
    && groups.every((group, index) => groupOpen(state.palGroupOpen, group.key, index, kw));
  const intentHead = groups.length
    ? `<div class="muted" style="font-size:10.5px;letter-spacing:.4px;margin:8px 0 6px;">意图
        <span style="color:#98A2B3;">${entries.length} 条 · ${groups.length} 组</span>
        ${groups.length > 1 && !kw ? `<span class="lnk" style="margin-left:8px;" data-act="pal-expand-all">${allOpen ? '收起全部' : '展开全部'}</span>` : ''}
      </div>`
    : '';
  const itemRows = items.map((item) => `<div class="pal-item" data-act="run-suggestion" data-id="${esc(item.id)}">
      <span class="ctx tag" style="border-radius:4px;padding:2px 6px;">待</span>
      <div><div class="pn">${esc(item.title)}</div><div class="pd">${esc(item.subtitle || item.reason || '')}</div></div>
      <div class="pk"><span class="btn sm">执行</span></div></div>`).join('');
  const runRows = runs.map((run) => `<div class="pal-item" data-act="open-run" data-id="${esc(run.traceId)}">
      <span class="ctx tag" style="border-radius:4px;padding:2px 6px;">↻</span>
      <div><div class="pn" style="color:#667085;">${esc(run.intentName || run.intentId)}${objectLabelOf(run) ? ` · ${esc(objectLabelOf(run))}` : ''}</div>
        <div class="pd">${esc(timeOf(run.startedAt))} · ${esc(formatDuration(run.durationMs))}</div></div>
      <div class="pk"><span class="btn sm">回看</span></div></div>`).join('');
  // 重渲染会把输入框整个换掉。换新的时候带上现值 + 光标，否则"打字打到一半被重画"
  // 就是搜索词和光标一起消失——那个缺陷只在运行时出现，静态自检看不见。
  const prevInput = $('pal-kw');
  const hadFocus = prevInput !== null && document.activeElement === prevInput;
  const caret = prevInput ? prevInput.selectionStart : null;
  $('overlay').innerHTML = `<div class="sheet" style="width:620px;">
    <div class="sheet-hd">
      <input id="pal-kw" type="search" autocomplete="off" style="border:none;height:22px;padding:0;font-size:14px;"
        value="${esc(state.paletteKeyword)}" placeholder="搜索意图、对象，或直接说一句…">
      <span class="sx" data-act="close-sheet">Esc</span>
    </div>
    <div class="sheet-bd">
      ${itemRows ? `<div class="muted" style="font-size:10.5px;letter-spacing:.4px;margin-bottom:6px;">我该办的</div>${itemRows}` : ''}
      ${intentRows ? `${intentHead}${intentRows}` : ''}
      ${runRows ? `<div class="muted" style="font-size:10.5px;letter-spacing:.4px;margin:8px 0 6px;">做过的</div>${runRows}` : ''}
      ${(entries.length || items.length || runs.length) ? '' : `<div class="empty"><span class="et">没找到你要的</span>
        <span class="eh">系统只在已上架的能力里找，不会自由发挥。<br>
        试试去掉限定词，或先把对象加进上下文（「＋ 加实体」）。</span></div>`}
    </div>
    <div class="sheet-ft">
      <span class="muted" style="font-size:11.5px;">↑↓ 选择 · ⏎ 第一条 · Esc 关闭</span>
      <span style="margin-left:auto;" class="muted">只在你能看到的能力里搜 · 走同一条执行管线</span>
    </div>
  </div>`;
  const input = $('pal-kw');
  if (input && hadFocus) {
    input.focus();
    if (caret !== null) input.setSelectionRange(caret, caret);
  }
}
/* ---- 视角 ---- */

function renderViewSwitch() {
  $('view-label').textContent = viewLabelOf(state.view);
  // self 由后端 availableViews() 合成（保留 id，null/未知视角都会退化到它）。
  // 前端再渲染一份、后端又给一份，菜单里就会长出两个「我自己」——按 id 去重，只留自己这行。
  const views = (state.me?.views ?? []).filter((view) => view.id !== 'self');
  $('view-menu').innerHTML = [
    `<div class="vi ${state.view === 'self' ? 'on' : ''}" data-act="switch-view" data-id="self">
      <span class="vl">我自己</span><span class="vd">全部角色</span></div>`,
    ...views.map((view) => `<div class="vi ${state.view === view.id ? 'on' : ''}"
      data-act="switch-view" data-id="${esc(view.id)}"><span class="vl">${esc(view.label)}</span>
      <span class="vd">${esc((view.roles ?? []).join('、'))}</span></div>`),
  ].join('');
  $('view-menu').hidden = true;
}

/**
 * 账号菜单。
 * 「不同角色登进来看到的不一样」是这个工作台的卖点，而卖点必须能被自证：
 * 没有出口，用户就只能去清浏览器存储才能换账号——那等于这条能力不存在。
 * 这里只显示宿主编排后的结果（角色 / 命中的装配），不显示任何自己推断的权限。
 */
function renderUserMenu() {
  const menu = $('user-menu');
  if (!menu) return;
  const me = state.me;
  if (!me) { menu.innerHTML = ''; menu.hidden = true; return; }
  const profile = me.profile ?? {};
  menu.innerHTML = `
    <div class="uhd">
      <div class="un">${esc(me.name || '未命名')}</div>
      <div class="ud">角色：${esc((me.roles ?? []).join('、') || '无')}</div>
      <div class="ud">装配：${esc(profile.label || '通用')} · ${esc(profile.title || '意图工作台')}</div>
    </div>
    <div class="vi" data-act="logout"><span class="vl">退出登录</span>
      <span class="vd">换个账号看另一份装配</span></div>`;
  menu.hidden = true;
}

/** 退出：清掉宿主 token，回到登录态。login 与工作台各存一份，两份都要清。 */
function doLogout() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(WORKBENCH_TOKEN_KEY);
  const menu = $('user-menu');
  if (menu) { menu.hidden = true; menu.innerHTML = ''; }
  renderLogin();
}

/* ---- 登录 / 失效 ---- */

function handleUnauthorized() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(WORKBENCH_TOKEN_KEY);
  toast('登录状态已失效，请重新登录');
  renderLogin();
}

function renderLogin() {
  state.token = null;
  state.me = null;
  const menu = $('user-menu');
  if (menu) { menu.hidden = true; menu.innerHTML = ''; }
  const inputStyle = 'width:100%;height:34px;padding:0 11px;border:1px solid #E6E9F0;'
    + 'border-radius:7px;font-size:13px;font-family:inherit;margin-bottom:8px;';
  $('left').innerHTML = `<div class="focus"><div>
      <div class="fname">意图工作台</div>
      <div class="fmeta">用宿主账号登录 · 不另建账号体系</div></div></div>
    <div class="ctx">
      <div class="ctx-hd">登录</div>
      <input id="lg-user" type="text" autocomplete="username" placeholder="账号" style="${inputStyle}">
      <input id="lg-pass" type="password" autocomplete="current-password" placeholder="密码" style="${inputStyle}">
      <div class="ctx-foot"><span class="btn pri" data-act="do-login">登录</span></div>
      <div class="hintline">登录走宿主的 /admin-api/system/auth/login，拿到的是宿主签发的 token；
        之后的每一次取数与执行都带它，工作台自己也判断不了权限。</div>
    </div>`;
  $('runbar').innerHTML = '';
  $('stream').innerHTML = `<div class="col"><div class="panel"><div class="p-bd">
    ${emptyBox('右侧是执行流', '登录后，你发起的每一次执行都会留在这里：结果、依据、当时用的上下文快照。')}
  </div></div></div>`;
}

async function doLogin() {
  const username = $('lg-user')?.value.trim();
  const password = $('lg-pass')?.value;
  if (!username || !password) { toast('账号和密码都要填'); return; }
  try {
    const data = await api(`${AUTH_API}/login`, {
      method: 'POST',
      auth: false,
      body: { username, password, captchaVerification: '' },
    });
    if (!data?.accessToken) { toast('登录响应里没有 token'); return; }
    state.token = data.accessToken;
    localStorage.setItem(TOKEN_KEY, state.token);
    localStorage.setItem(WORKBENCH_TOKEN_KEY, state.token);
    boot();
  } catch (e) {
    toast(`登录失败：${e.message}`);
  }
}

/* ---- 轻提示与剪贴板 ---- */

let toastTimer = null;

function toast(message) {
  const box = $('toast');
  box.textContent = message;
  box.hidden = false;
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => { box.hidden = true; }, 3200);
}

async function copyText(text, okMessage) {
  try {
    await navigator.clipboard.writeText(text);
    toast(okMessage);
  } catch (e) {
    toast('浏览器没给剪贴板权限，复制失败');
  }
}

/* ---- 动作分发 ---- */

function findRun(traceId) {
  return state.session.find((run) => run.traceId === traceId)
    ?? (state.focus?.traceId === traceId ? state.focus : null)
    ?? state.runs.find((run) => run.traceId === traceId)
    ?? null;
}

/**
 * 应用预设（作战台）。
 *
 * **行为是"叠加"而不是"整体替换"**（原实现 `state.slots = next` 会把档案声明的初始槽位、
 * 以及用户已经钉住的对象一起冲掉——按了「打单作战」反而丢了「时间窗 = 近90天」，且没有任何提示）。
 * 现在的规则：
 *   1. 预设声明了哪几类 → 这几类进入作战台并**钉住**；
 *   2. 其中**已经有值的**保留用户的选择，不覆盖（钉住的槽位永不被自动覆盖，这是 §5.1 的红线）；
 *   3. 栈里**其它**槽位原样保留（预设是"再加上这几样"，不是"重开一局"）；
 *   4. 需要用户挑的，在 toast 里如实说清楚是哪些——不承诺"已摆好"却全是空的。
 */
function applyPreset(presetId) {
  const preset = (state.me.profile?.presets ?? []).find((item) => item.id === presetId);
  if (!preset) return;
  const next = state.slots.map((slot) => ({ ...slot }));   // 保留现有上下文
  const kept = [];
  const pending = [];
  for (const item of preset.slots) {
    const meta = slotTypeMeta(item.type);
    if (!meta) continue;
    const exists = next.find((slot) => slot.type === meta.type);
    if (exists) {
      // 已有值：只把它并进这次作战台（钉住），绝不改用户挑的值
      exists.source = SOURCE_PINNED;
      if (item.mode === MODE_SET) exists.mode = MODE_SET;
      kept.push(meta.title);
      continue;
    }
    // 预设只给"要摆哪几类"：有枚举默认值的（如时间窗）顺手带上，其余留空等用户挑
    const values = (meta.multi ? [] : (meta.values ?? []).slice(0, 1)).map(String);
    next.push(normalizeSlot(meta.type, values, {
      source: SOURCE_PINNED,
      mode: item.mode === MODE_SET ? MODE_SET : null,
    }));
    if (values.length) kept.push(meta.title); else pending.push(meta.title);
  }
  state.slots = next;
  renderLeft();
  refreshCatalog();
  resolveSlots();
  const parts = [`已摆好「${preset.label}」`];
  if (kept.length) parts.push(`带上了 ${kept.join('、')}`);
  if (pending.length) parts.push(`${pending.join('、')} 还要你挑`);
  toast(parts.join(' · '));
  track('wb_preset_apply', { presetId, kept, pending });
}

function setTimeWindow(window) {
  const meta = slotTypeMeta('timeWindow');
  if (!meta) return;
  putSlot('timeWindow', [window], SOURCE_PINNED);
}

async function onAct(act, el) {
  switch (act) {
    case 'close-sheet': closeSheet(); break;
    case 'open-palette': openPalette(); break;
    case 'pal-expand-all': {
      const surface = GROUP_SURFACES.palette;
      const groups = surface.groups();
      const openMap = surface.open();
      const kw = surface.keyword();
      const allOpen = groups.every((group, index) => groupOpen(openMap, group.key, index, kw));
      for (const group of groups) openMap[group.key] = !allOpen;
      surface.render();
      break;
    }
    case 'toggle-group': {
      const surface = GROUP_SURFACES[el.dataset.surface];
      if (!surface) break;
      const key = el.dataset.id;
      const groups = surface.groups();
      const index = groups.findIndex((group) => group.key === key);
      if (index < 0) break;
      const openMap = surface.open();
      const open = !groupOpen(openMap, key, index, surface.keyword());
      openMap[key] = open;
      applyGroupToggle(el, groups[index], open);
      break;
    }
    case 'add-slot': openPicker(el.dataset.type); break;
    case 'add-entity': openPicker(el.dataset.type); break;
    // 上下文展开/收起：纯视图偏好，不重拉数据
    case 'toggle-ctx':
      state.ctxOpen = !state.ctxOpen;
      track('wb_ctx_toggle', { open: state.ctxOpen });
      renderLeft();
      break;
    /**
     * 采纳"建议的对象"：把主角卡针对的那个对象摆进上下文。
     * 这一步是显式的——**不替用户做决定**，但把"这个对象是从哪来的"摊开在通栏里。
     */
    case 'accept-subject':
      putSlot(el.dataset.type, [el.dataset.id], SOURCE_PINNED, el.dataset.label);
      track('wb_accept_subject', { type: el.dataset.type });
      break;
    // 右栏 Run 分组展开/收起：同样是视图偏好
    case 'toggle-run-group':
      state.runOpen[el.dataset.id] = state.runOpen[el.dataset.id] === false;
      renderRight();
      break;
    // 结果卡的「查看详情」：默认收起，正文不进首屏
    case 'toggle-detail':
      state.detailOpen[el.dataset.id] = !state.detailOpen[el.dataset.id];
      track('wb_detail_toggle', { open: state.detailOpen[el.dataset.id] });
      renderRight();
      break;
    case 'toggle-failed-runs':
      state.failedRunsOpen = !state.failedRunsOpen;
      renderRight();
      break;
    /**
     * 清理失败的 Run。
     * **只从当前这一屏移除**（进 hiddenRuns），不动服务端留痕——
     * 留痕是审计资产，一次点击不该把它抹掉。刷新页面它就回来了，
     * 所以按钮的 title 也如实写清"只是从这一屏移除"。
     */
    case 'clear-failed-runs': {
      const failedIds = [...state.session, ...state.runs]
        .filter((run) => run.status !== 'SUCCESS' && run.status !== 'RUNNING' && run.traceId)
        .map((run) => run.traceId);
      state.hiddenRuns = [...new Set([...state.hiddenRuns, ...failedIds])];
      state.failedRunsOpen = false;
      track('wb_clear_failed_runs', { count: failedIds.length });
      renderRight();
      break;
    }
    // 左栏导航面板：选哪一块纯属视图偏好，**不重拉任何数据**
    // （这就是"分层不等于砍数据"的代价控制：切块不产生一次请求）
    case 'select-block':
      state.navBlock = el.dataset.id;
      track('wb_nav_block', { block: el.dataset.id });
      renderLeft();
      break;
    case 'clear-slot': removeSlot(el.dataset.type); break;
    case 'pin-slot': togglePin(el.dataset.type); break;
    // 用户显式点刷新 / 重试：立即执行，不等防抖（他点了就该有反应）
    case 'reload-catalog': refreshCatalog({ immediate: true }); break;
    case 'reload-runs': refreshRuns(); break;
    case 'toggle-todo':
      state.openTodos = state.openTodos === el.dataset.id ? null : el.dataset.id;
      renderLeft();
      break;
    case 'run-todo': await runIntent(el.dataset.id); break;
    case 'run-suggestion': {
      const suggestion = state.suggestions.find((item) => item.id === el.dataset.id);
      if (!suggestion) break;
      state.openTodos = null;
      await runIntent(suggestion.intentId, suggestion.params ?? null);
      break;
    }
    // 注：`open-intent` 已随「我常做的」一起撤掉——命中的意图点行就是 `focus-intent`
    // （切焦点 + 开抽屉），语义重复，留着就是一条永远走不到的死分支。
    case 'pick-intent': {
      const entry = entryOf(el.dataset.id);
      state.current = el.dataset.id;
      state.focus = null;
      state.right = 'runs';
      closeSheet();
      if (entry && satisfiability(entry, state.slots, registryOf(state.me)).state === 'ready') {
        await runIntent(el.dataset.id);
      } else {
        renderRight();
        toast('这条还需要一些上下文：加到左边就能直接跑');
      }
      break;
    }
    case 'run-intent':
      // data-from 只有「下一步」胶囊才有（relayOf）。有它就把来源一起送下去，
      // 让留痕记下"这次是跟着那条建议走的"——否则网络永远分不清建议与事实。
      await runIntent(el.dataset.id, null, el.dataset.from
        ? { from: { intentId: el.dataset.from, traceId: el.dataset.fromTrace, kind: el.dataset.fromKind } }
        : {});
      break;
    // 网络：切焦点。**只重绘高亮，不重排布局**（重排会丢空间记忆）
    case 'focus-intent': {
      state.current = el.dataset.id;
      if (el.dataset.open !== 'keep') state.graphDrawer = true;
      renderGraph();
      renderLeft();
      track('wb_graph_focus', { intentId: el.dataset.id });
      break;
    }
    case 'close-drawer': state.graphDrawer = false; renderGraph(); break;
    // ★ ③ 把焦点交还给系统建议：主角卡与网络重新指同一件事
    case 'clear-focus':
      state.current = null;
      state.graphDrawer = false;
      track('wb_clear_focus', {});
      renderGraph();
      renderLeft();
      break;
    case 'toggle-graph-live':
      state.graphLiveOnly = !state.graphLiveOnly;
      track('wb_graph_live_only', { on: state.graphLiveOnly });
      renderGraph();
      break;
    case 'focus-failed': {
      // "没跑成"不是一个网络节点，是节点上的一个状态；点它就把焦点落到第一个失败的意图上
      const failed = state.runs.find((run) => run.status !== 'SUCCESS' && run.intentId);
      if (failed) { state.current = failed.intentId; state.graphDrawer = true; renderGraph(); renderLeft(); }
      break;
    }
    case 'rerun': await runIntent(el.dataset.id); break;
    case 'rerun-tw': setTimeWindow(el.dataset.tw); await runIntent(el.dataset.id); break;
    case 'followup-search': openPalette(el.dataset.text); break;
    case 'copy-params': await copyText(JSON.stringify(paramsFromSlots(), null, 2), '参数已复制'); break;
    case 'copy-result': {
      const run = findRun(el.dataset.id);
      await copyText(JSON.stringify(run?.output ?? {}, null, 2), '结果已复制（原始 JSON，不是渲染后的样子）');
      break;
    }
    case 'toggle-evidence': {
      const box = $(`ev-${el.dataset.id}`);
      if (box) box.open = !box.open;
      break;
    }
    case 'open-run': await openRun(el.dataset.id); break;
    case 'open-batch': openBatchSheet(); break;
    case 'start-batch': await startBatch(); break;
    case 'stop-batch':
      if (state.explore) {
        state.explore.running = false;
        renderRight();
        toast('已停止入队 · 在跑的会把这一条跑完并计入结果');
      }
      break;
    case 'retry-batch': await retryBatch(); break;
    case 'rerun-batch': rerunBatch(); break;
    case 'open-explore':
      // 批量进行中：把会话面板放进抽屉。
      // ⚠️ 这里原本只设 `state.right='explore'`，但执行流搬进抽屉之后
      // `streamPanels()` 永远带着 intentId 调用（`if (!onlyIntentId && ...)`），
      // 于是**批量会话的进度面板再也渲染不出来**——有入口、看不到进度。
      // 现在既切 right 也把抽屉打开，批量这条路才算闭合。
      state.right = 'explore';
      state.graphDrawer = true;
      renderGraph();
      break;
    case 'back-to-intent':
      state.right = 'runs';
      renderGraph();
      break;
    case 'apply-preset': applyPreset(el.dataset.id); break;
    case 'switch-view':
      state.view = el.dataset.id;
      renderViewSwitch();
      renderLeft();
      // 换视角 = 换生效角色，是一次显式切换，立即重拉
      refreshCatalog({ immediate: true });
      track('view_switch', { view: state.view });
      break;
    case 'draft-type':
      state.exploreDraft.type = el.dataset.id;
      state.exploreDraft.objects = [];
      state.batchKeyword = '';
      syncExploreSlot();
      renderBatchSheet();
      break;
    case 'draft-pick': {
      const draft = state.exploreDraft;
      const id = el.dataset.id;
      if (!draft.objects.some((obj) => obj.id === id)) {
        draft.objects.push({ id, name: el.dataset.name || id });
      }
      renderBatchSheet();
      break;
    }
    case 'draft-remove':
      state.exploreDraft.objects = state.exploreDraft.objects.filter((obj) => obj.id !== el.dataset.id);
      renderBatchSheet();
      break;
    case 'draft-intent': {
      const draft = state.exploreDraft;
      const id = el.dataset.id;
      draft.intentIds = draft.intentIds.includes(id)
        ? draft.intentIds.filter((item) => item !== id) : [...draft.intentIds, id];
      renderBatchSheet();
      break;
    }
    case 'pick-type':
      state.pickType = el.dataset.id;
      // 换类型 = 换选择方式（可能从 remote 换成 literal），所以运行态整个重来
      state.pickState = {
        keyword: '', cursor: null, items: [], loading: false, more: false,
        degraded: pickerFor(el.dataset.id).degraded, picked: [],
      };
      renderPicker();
      if (pickerFor(el.dataset.id).kind === PICKER_REMOTE) loadPickerCandidates('');
      break;
    case 'pick-cand':
      pickCandidate(el.dataset.id, el.dataset.name);
      break;
    case 'pick-confirm': {
      const picked = state.pickState?.picked ?? [];
      const first = state.pickState?.items.find((cand) => cand.id === picked[0]);
      commitPick(state.pickType, picked, first?.name);
      break;
    }
    case 'pick-more': {
      const ps = state.pickState;
      if (!ps || ps.loading) break;
      loadPickerCandidates(ps.keyword, { append: true });
      break;
    }
    case 'do-login': await doLogin(); break;
    case 'logout': doLogout(); break;
    default: break;
  }
}

async function retryBatch() {
  const session = state.explore;
  if (!session) return;
  const rows = session.objects.filter((obj) => obj.status === 'fail');
  if (!rows.length) { toast('没有需要重试的行'); return; }
  session.running = true;
  renderRight();
  for (const obj of rows) {
    obj.status = 'run';
    obj.results = {};
    renderRight();
    for (const intentId of session.intentIds) {
      try {
        const result = await executeOnce(intentId, { ...paramsFromSlots(), [session.key]: obj.id });
        obj.results[intentId] = result;
        session.tokens += result.usage?.totalTokens || 0;
        obj.status = result.status === 'SUCCESS' ? 'ok' : 'fail';
      } catch (e) {
        obj.results[intentId] = { status: 'FAILED', error: { code: e.code ? String(e.code) : 'REQUEST_FAILED', message: e.message } };
        obj.status = 'fail';
        if (e.unauthorized) { session.running = false; handleUnauthorized(); return; }
      }
      renderRight();
    }
  }
  session.running = false;
  session.finishedAt = Date.now();
  renderRight();
  refreshRuns();
}

function rerunBatch() {
  const session = state.explore;
  if (!session || session.running) return;
  state.exploreDraft = {
    type: session.type,
    objects: session.objects.map((obj) => ({ id: obj.id, name: obj.name })),
    intentIds: [...session.intentIds],
  };
  startBatch();
}

/* ---- 事件绑定 ---- *
 * 全部收进 `bindDom()`，模块顶层不再直接碰 document。
 * 这样这个文件可以被非浏览器环境 import（Node 里做渲染断言、宿主里做挂载），
 * 而不是"一 import 就往 document 上挂监听"。
 * 绑定是幂等的：重复 mount 不会挂两遍监听（否则一次点击会触发两次）。
 */
let domBound = false;

function bindDom() {
  if (domBound || typeof document === 'undefined') return;
  domBound = true;

  document.addEventListener('click', (event) => {
    const target = event.target;
    if (target.id === 'overlay') { closeSheet(); return; }
    const el = target.closest('[data-act]');
    if (!el) {
      if (!target.closest('#view-switch')) $('view-menu').hidden = true;
      if (!target.closest('#user-switch')) $('user-menu').hidden = true;
      return;
    }
    const act = el.dataset.act;
    if (act === 'switch-view' || act === 'open-palette' || act === 'pick-intent') event.preventDefault();
    onAct(act, el);
  });

  $('view-switch')?.addEventListener('click', () => {
    const menu = $('view-menu');
    menu.hidden = !menu.hidden;
  });

  $('user-switch')?.addEventListener('click', () => {
    const menu = $('user-menu');
    menu.hidden = !menu.hidden;
  });

  document.addEventListener('input', (event) => {
    const target = event.target;
    if (target.id === 'pal-kw') {
      state.paletteKeyword = target.value;
      const caret = target.selectionStart;
      renderPalette();
      const input = $('pal-kw');
      if (input) { input.value = state.paletteKeyword; input.focus(); input.setSelectionRange(caret, caret); }
    }
    if (target.id === 'pick-kw') {
      loadPickerCandidates(target.value);
    }
    if (target.id === 'batch-kw') {
      state.batchKeyword = target.value;
      loadBatchCandidates(target.value);
    }
  });

  document.addEventListener('keydown', (event) => {
    const open = !$('overlay').hidden;
    if ((event.ctrlKey || event.metaKey) && event.key.toLowerCase() === 'k') {
      event.preventDefault();
      openPalette();
      return;
    }
    // Esc 是"退一层"：弹层优先，没弹层就收起顶栏的两个下拉。以前只关弹层，
    // 视角/账号菜单开了之后按 Esc 毫无反应——同一个键在同一屏上有两种脾气。
    if (event.key === 'Escape') {
      $('view-menu').hidden = true;
      $('user-menu').hidden = true;
      if (open) { closeSheet(); return; }
    }
    if (event.key === 'Enter') {
      if (event.target.id === 'pick-kw') {
        const value = event.target.value.trim();
        const meta = slotTypeMeta(state.pickType);
        // 候选没给结果时，允许按编号手输——这是"没有下拉也不能没法用"
        if (value && meta) commitPick(state.pickType, [value], value);
        return;
      }
      if (event.target.id === 'pal-kw') {
        const first = document.querySelector('#overlay .pal-item');
        if (first) first.click();
        return;
      }
      if (event.target.id === 'lg-user') { $('lg-pass')?.focus(); return; }
      if (event.target.id === 'lg-pass') { doLogin(); }
    }
  });
}

/* ---- 启动 ---- */

async function boot() {
  try {
    await loadMe();
  } catch (e) {
    if (e.unauthorized) { handleUnauthorized(); return; }
    $('left').innerHTML = emptyBox('工作台没装配起来', e.message);
    return;
  }
  await refreshRuns();
  // 首次装配必须等到目录回来才渲染，不能等防抖窗口
  await refreshCatalog({ immediate: true });
  resolveSlots();
}

/* ---------------------------------------------------------------- *
 * 对外入口：mountWorkbench
 *
 * 工作台以前是"一个宿主页面"，`tenant-id`、API 前缀、登录流程全写死在模块里，
 * 换一个系统就得改前端源码（《四点优化设计》§1.1 的 C1–C4）。
 * 现在它是一个**可挂载的组件**：宿主给容器 + 配置，本模块负责其余一切。
 *
 * 最小集成（零配置，配置项全有默认值）：
 *   import { mountWorkbench } from './js/workbench.js';
 *   mountWorkbench({ container: '#wb', tenantId: '7', authHeaders: () => ({ Authorization: `Bearer ${token}` }) });
 *
 * 宿主差异只允许走三条路：**配置项**、**槽位/角色 YAML**、**picker 注册**。
 * 这里刻意不做任何 `if (host === 'crm')` 之类的分支。
 * ---------------------------------------------------------------- */

/** 埋点 / 深链等宿主适配的挂载选项（见 .d.ts 的 IntentWorkbenchOptions）。 */
let mountedExplicitly = false;

export function mountWorkbench(options = {}) {
  Object.assign(CONFIG, options);
  // 派生常量在模块加载时就固定了，这里重新绑定一次，使 mount 时的配置真正生效
  API = CONFIG.apiPrefix;
  AUTH_API = CONFIG.authApiPrefix;
  TOKEN_KEY = CONFIG.tokenKeys?.access ?? TOKEN_KEY;
  WORKBENCH_TOKEN_KEY = CONFIG.tokenKeys?.workbench ?? WORKBENCH_TOKEN_KEY;
  TENANT_ID = String(CONFIG.tenantId);
  MAX_BATCH = CONFIG.maxBatch;
  MAX_RUNS = CONFIG.maxRuns;
  CATALOG_DEBOUNCE_MS = CONFIG.catalogDebounceMs;

  if (typeof document === 'undefined') return { reload: boot };
  mountedExplicitly = true;
  bindDom();
  // 容器由宿主给时，用它替换默认的挂载点。骨架里**必须包含上下文通栏**——
  // 它横跨整页、同时驱动左栏与右栏，不属于任何一栏。
  if (options.container) {
    const root = typeof options.container === 'string'
      ? document.querySelector(options.container) : options.container;
    if (root && !document.getElementById('left')) {
      // 骨架必须和后端 index.html 保持一致：少一个 #gcanvas，宿主自建容器时中间就是白的
      root.innerHTML = '<div class="ctxbar" id="ctxbar"></div>'
        + '<div class="body"><div class="left" id="left"></div>'
        + '<div class="graph" id="graph">'
        + '<div class="ghead" id="ghead"></div>'
        + '<div class="gbody"><div class="gcanvas" id="gcanvas"></div>'
        + '<div class="gdrawer" id="gdrawer"></div></div></div></div>';
    }
  }
  // token 由宿主给（它已经登录过）；不给就走 localStorage / 登录页
  if (options.token !== undefined) state.token = options.token;
  const handle = { reload: boot, run: (intentId, params) => runIntent(intentId, params) };
  if (options.autoBoot !== false) {
    if (state.token) boot(); else renderLogin();
  }
  return handle;
}

/**
 * 供测试与预览页使用的只读出口。
 *
 * 有了它，渲染就不再是"只能在浏览器里肉眼验"的东西：
 * Node 里灌一份 fixture state，就能对生成的 HTML 做断言（见 `workbench-render.test.mjs`）。
 */
export function __workbench() {
  return {
    state, CONFIG,
    heroCard, queueSection, renderRunBar, renderRunCard,
    severityOf, sortedSuggestions, normalizeSlot, focusSlot, topActionable,
    groupGateOf, groupsHtml, groupEntries, frequencyOf, readyCount,
    // 首屏再设计 v2：对象聚合 / 金额解析 / 上下文通栏 / 执行流分组 / 结果卡视觉
    aggregates, aggregateSuggestions, contextPanel, streamPanels, groupRunsByIntent,
    parseItemFacts, objectRefOf, formatMoney, queueRowHtml, queueRows, hintLine, relativeTime,
    renderContextBar, contextBarHtml, contextStateHtml, subjectAdoptChip, emphasizeNumbers,
    metricsHtml, proseHtml, tableHtml, rowToCard, renderOutput, renderBlock,
    briefHtml, detailHtml, detailHint, briefLead, runKey, nextIntentsOf,
    // 左栏导航面板（图标瓦片）
    navTiles, navBlockPanel, navTileInfo, activeNavBlock, navBlockIds, NAV_BLOCKS, NAV_TONE,
    // 中间区：意图网络
    renderGraph, intentGraph, graphLayout, graphFocusId, graphNodeHtml, graphCanvasHtml,
    graphEdgeKind, graphEdgeLabel, graphHeadHtml, graphDrawerHtml, graphDownstream, GRAPH,
    focusBarHtml, suggestedIntentId, graphColX, todoGroups, rankedFrequent,
    hitGroups, hitIntentsBody, hitSubRows,
    setSlots(slots) { state.slots = slots; },
    setMe(me) { state.me = me; },
    setEntries(entries) { state.entries = entries; },
    setSuggestions(suggestions) { state.suggestions = suggestions; },
    setRuns(runs) { state.runs = runs; },
  };
}

/* ---- 启动 ---- *
 * 只在**页面里已经有工作台骨架**时自启（宿主 index.html 的场景）。
 * 没有 DOM 或没有挂载点就什么都不做——这样 Node 里 import 本模块不会炸，
 * 宿主也可以选择自己调 `mountWorkbench()`。
 */
if (typeof document !== 'undefined' && typeof localStorage !== 'undefined'
    && document.getElementById('left') && !mountedExplicitly) {
  bindDom();
  state.token = localStorage.getItem(TOKEN_KEY) || localStorage.getItem(WORKBENCH_TOKEN_KEY);
  if (state.token) boot(); else renderLogin();
}
