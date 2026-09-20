/**
 * 意图工作台 · 启动冒烟（假 DOM）
 * ---------------------------------------------------------------------------
 * 跑：node src/test/js/workbench-boot.test.mjs
 *
 * 为什么需要它：另外两个 node 测试都只调**渲染函数**，没跑过 `boot()` 这条真实启动链。
 * 而零构建页面最怕的失败恰恰是"一进页面就抛异常 → 白屏"——这种失败在任何静态断言里
 * 都看不出来，只有把整条链跑一遍才会暴露：
 *
 *   boot() → loadMe() → applyInitialSlots() → refreshRuns() → refreshCatalog() → resolveSlots()
 *          → renderLeft() + renderRight() + renderViewSwitch() + renderUserMenu()
 *
 * 这里用一个**够用的假 DOM**（只实现工作台真正用到的那几个方法）把这整条链跑通，
 * 并断言最终落进 `#left` / `#runbar` / `#stream` 的 HTML 是完整的首屏。
 * 它不能替代浏览器回归（没有布局、没有 CSS、没有真实事件），但能把"白屏"这类问题挡在提交前。
 */

/* ---------------------------------------------------------------- 假 DOM */

function makeElement(id) {
  return {
    id,
    innerHTML: '',
    textContent: '',
    value: '',
    hidden: false,
    dataset: {},
    classList: { toggle() {}, add() {}, remove() {}, contains() { return false; } },
    addEventListener() {},
    removeEventListener() {},
    focus() {},
    setSelectionRange() {},
    querySelector() { return null; },
    querySelectorAll() { return []; },
    closest() { return null; },
  };
}

const elements = new Map();
const getElementById = (id) => {
  if (!elements.has(id)) elements.set(id, makeElement(id));
  return elements.get(id);
};

/** 启动时 DOM 里已经有工作台骨架（host index.html 的场景），否则模块不会自启。 */
for (const id of ['left', 'runbar', 'stream', 'overlay', 'toast', 'nav-badge',
  'brand-name', 'avatar', 'view-switch', 'view-menu', 'view-label', 'user-switch', 'user-menu',
  'ctxbar',
  // 中间区（意图网络）：头 / 画布+抽屉的并列容器 / 画布 / 抽屉
  'graph', 'ghead', 'gbody', 'gcanvas', 'gdrawer']) {
  getElementById(id);
}
elements.get('overlay').hidden = true;
elements.get('toast').hidden = true;

globalThis.document = {
  title: '',
  addEventListener() {},
  getElementById,
  querySelector: () => null,
  querySelectorAll: () => [],
};
const store = new Map([['ACCESS_TOKEN', 'boot-smoke-token']]);
globalThis.localStorage = {
  getItem: (key) => (store.has(key) ? store.get(key) : null),
  setItem: (key, value) => store.set(key, String(value)),
  removeItem: (key) => store.delete(key),
};

/* ---------------------------------------------------------------- fixtures */

const SLOT_TYPES = [
  { type: 'customer', key: 'customerId', title: '客户', entity: true, multi: true, picker: 'remote', hasCandidates: true },
  { type: 'contract', key: 'contractId', title: '合同', entity: true, multi: true, picker: 'remote', hasCandidates: true },
  { type: 'timeWindow', key: 'timeWindow', title: '时间窗', entity: false, multi: false, picker: 'literal', values: ['近90天'] },
];
const ME = {
  userId: 1, name: '管理员', roles: ['super_admin'], systemName: '企业管理一体化平台',
  groupFallbackTitle: '跨实体与经营分析',
  views: [{ id: 'self', label: '我自己' }, { id: 'lead', label: '主管视角', roles: ['super_admin'] }],
  defaultView: 'self', slotTypes: SLOT_TYPES,
  profile: {
    id: 'manager', label: '主管', title: '我的管理驾驶舱',
    blocks: ['context', 'today', 'todos', 'frequent', 'explore', 'history'],
    pinnedIntents: [], initialSlots: [{ type: 'timeWindow', values: ['近90天'] }],
    presets: [{ id: 'bid', label: '打单作战' }],
    explore: { label: '团队客户体检', intents: ['crm.customer.health-check'] },
  },
};
const ENTRIES = [
  { id: 'crm.customer.health-check', name: '客户健康度诊断', context: [{ key: 'customerId', title: '客户', required: true }], executorType: 'agent' },
  { id: 'crm.contract.renewal-plan', name: '续约策略', context: [{ key: 'contractId', title: '合同', required: true }], executorType: 'agent' },
  // SUGGESTIONS 里那条命中挂在它上面；不放进 ENTRIES 的话组名会退化成建议标题（=对象名），
  // "意图当主语"就没法验了（生产里所有命中意图都在目录里，不存在这个退化）
  { id: 'crm.customer.churn-warning', name: '客户流失预警', context: [{ key: 'customerId', title: '客户', required: true }], executorType: 'agent' },
];
const SUGGESTIONS = [
  { id: 's1', intentId: 'crm.customer.churn-warning', title: '朗润医疗', subtitle: '商机停滞 42 天', reason: '逾期未联系', kind: 'item', count: 2 },
  { id: 'a1', intentId: 'crm.backlog.today-priority', title: '客户超期未联系', kind: 'aggregate', count: 4 },
];
// 两条 run：第一条跑完建议了「续约策略」，第二条**带着 `_from` 去跑了它**。
// 这正好是闭环的形状 —— 网络应该画出「客户健康度诊断 → 续约策略」，
// 而且因为第二条记了来源，那条边必须是 `walked`（事实），不是 `proposed`（建议）。
const RUNS = [
  {
    traceId: 't1', intentId: 'crm.customer.health-check', intentName: '客户健康度诊断',
    status: 'SUCCESS', startedAt: Date.now() - 60000, durationMs: 900,
    params: { customerId: '320' }, contextSnapshot: { _slots: [] },
    output: { title: '客户健康度诊断', summary: '宏图建筑设计院 62/100。',
      blocks: [], nextIntents: [{ intentId: 'crm.contract.renewal-plan', title: '续约策略', reason: '这份合同还有 45 天到期' }] },
  },
  {
    traceId: 't2', intentId: 'crm.contract.renewal-plan', intentName: '续约策略',
    status: 'SUCCESS', startedAt: Date.now(), durationMs: 800, params: {},
    contextSnapshot: { _slots: [], _from: { intentId: 'crm.customer.health-check', traceId: 't1', kind: 'nextIntent' } },
    output: { title: '续约策略', summary: '建议按原价续约。', blocks: [] },
  },
];

const calls = [];
async function fetcher(path) {
  calls.push(path);
  if (path.endsWith('/intent/me')) return ME;
  // ⚠️ 这里以前写的是 `path.endsWith('/intent/runs')`，而真实路径是
  // `/intent/runs?limit=N` —— 带查询串，永远匹配不上，于是 `state.runs` 一直是空的。
  // 旧断言看的是"执行流空态"，所以照样绿：**测试自己把自己骗过去了**。
  // 现在用 includes，并且下面断言真的渲出了节点与连线。
  if (path.includes('/intent/runs')) return RUNS;
  if (path.endsWith('/intent/catalog')) {
    return { systemName: ME.systemName, gatewayStatus: 'ENABLED', entries: ENTRIES, suggestions: SUGGESTIONS, issues: [] };
  }
  if (path.includes('/intent/slots/resolve')) {
    return { customer: { type: 'customer', id: '320', name: '宏图建筑设计院', accessible: true } };
  }
  if (path.includes('/intent/slots/candidates')) return { items: [{ id: '320', name: '宏图建筑设计院' }], nextCursor: null, total: 1 };
  return null;
}

/* 宿主在**加载模块之前**注入配置——这是文档里写的正式接入口，这里顺便把它测了。
   注意必须放在 import 之前：模块顶层的自启读的是加载那一刻的 CONFIG。 */
globalThis.__INTENT_WORKBENCH__ = { fetcher };

/* ---------------------------------------------------------------- 跑启动链 */

const failures = [];
let passed = 0;
const expect = (name, condition, detail) => {
  if (condition) passed += 1;
  else failures.push(`${name}${detail ? ` → ${detail}` : ''}`);
};

const { mountWorkbench } = await import(
  new URL('../../main/resources/intent-ui/js/workbench.js', import.meta.url).href);

// 模块加载时因为 #left 已存在、且 localStorage 里有 token，会**自动 boot**。
await new Promise((resolve) => setTimeout(resolve, 400));

const left = elements.get('left').innerHTML;
const ctxbar = elements.get('ctxbar').innerHTML;

expect('启动链跑通且没有白屏', left.length > 500, `#left 只有 ${left.length} 字符`);
expect('请求依次打到 /me、/runs、/catalog', calls.some((p) => p.endsWith('/intent/me'))
  && calls.some((p) => p.includes('/intent/runs'))
  && calls.some((p) => p.endsWith('/intent/catalog')), calls.join(' | '));

expect('首屏出现主角卡', left.includes('class="hero'), left.slice(0, 200));
expect('主角卡带唯一实心主按钮', (left.match(/btn pri lg/g) || []).length === 1);
// 上下文已经搬到顶部通栏：左栏不该再有它，通栏里必须能看到有值的槽位
expect('上下文搬到了顶部通栏', ctxbar.includes('ctxbar-in') && !left.includes('class="ctx'),
  `ctxbar=${ctxbar.slice(0, 80)} leftHasCtx=${left.includes('class="ctx')}`);
expect('通栏里能看到初始化的时间窗槽位', ctxbar.includes('时间窗') && ctxbar.includes('近90天'), ctxbar.slice(0, 300));
expect('通栏默认是收起态（不摊开编辑面板）', !ctxbar.includes('ctx-editor'));
// 左栏这一版**精简成两块**：命中意图（按意图分组，左栏主内容）+ 做过的（执行回执）。
// 撤掉的 today / frequent / explore 见 workbench.js 的 NAV_BLOCKS 注释。
// 注意「批量摸底」**仍然出现在页面里**——它从"块"变成了块动作上的入口（功能没丢），
// 所以这里只断言它不再是瓦片，不能简单断言"页面里没有这四个字"。
const tileTitles = [...left.matchAll(/data-act="select-block"[\s\S]*?<span class="ntt">([^<]*)</g)]
  .map((m) => m[1].trim());
expect('左栏导航面板只剩两块：命中意图 + 做过的',
  tileTitles.length === 2 && tileTitles.includes('命中意图') && tileTitles.includes('做过的'),
  `实际瓦片：${tileTitles.join('、')}`);
expect('撤掉的三个块不再作为导航出现',
  !['今天要办的', '我该做的', '我常做的', '批量摸底'].some((t) => tileTitles.includes(t)),
  tileTitles.join('、'));
expect('「批量摸底」退回成块动作上的入口（不是被删功能）',
  left.includes('data-act="open-batch"'), left.slice(-400));
expect('瓦片是可点的导航项（不是只读标题）',
  (left.match(/data-act="select-block"/g) || []).length === 2,
  `实际 ${(left.match(/data-act="select-block"/g) || []).length} 个`);
expect('瓦片带内联 SVG 图标', (left.match(/<svg /g) || []).length >= 2);
expect('默认只渲染选中那一块的正文（不把两块全铺开）',
  left.includes('class="navpane"') && (left.match(/class="navpane"/g) || []).length === 1);
expect('命中的意图是**意图当主语**（行首是意图名，不是对象名）',
  /<div class="q-t">客户流失预警/.test(left), left.slice(-400));
expect('行上带对象数与条数（分组的分量看得见）',
  left.includes('个对象') || left.includes('条<'), left.slice(-300));
expect('展开入口在行尾（点开才出对象明细）',
  left.includes('data-act="toggle-todo"'), left.slice(-200));
// 通栏里不再有「建议：X ＋」——它和主角卡的主语是同一个对象，一屏说两遍
expect('通栏里不再出现「建议：X ＋」', !ctxbar.includes('accept-subject') && !ctxbar.includes('建议：'),
  ctxbar.slice(0, 300));

expect('顶栏徽标写上了待办数', elements.get('nav-badge').textContent !== '', `badge="${elements.get('nav-badge').textContent}"`);
expect('页面标题用的是装配标题', document.title.includes('我的管理驾驶舱'), document.title);
expect('视角菜单渲染了（且不含重复的「我自己」）',
  (elements.get('view-menu').innerHTML.match(/我自己/g) || []).length === 1);
expect('账号菜单给出了角色与装配', elements.get('user-menu').innerHTML.includes('角色：super_admin'));

// 中间区现在是**意图网络**（上一版是执行流）。执行流被收进抽屉，没有丢。
const ghead = elements.get('ghead').innerHTML;
const gcanvas = elements.get('gcanvas').innerHTML;
const gdrawer = elements.get('gdrawer').innerHTML;
expect('中间区有网络骨架', ghead.includes('意图网络') && gcanvas.length > 0,
  `ghead=${ghead.slice(0, 60)} gcanvas=${gcanvas.length}`);
expect('网络头部如实写清统计口径（前瞻 / 建议过 / 跑过）',
  ghead.includes('前瞻') && ghead.includes('建议过') && ghead.includes('跑过'), ghead.slice(0, 200));
expect('画布里有节点、有连线', gcanvas.includes('class="gnode') && gcanvas.includes('class="ge '),
  `nodes=${(gcanvas.match(/class="gnode/g) || []).length} edges=${(gcanvas.match(/class="ge /g) || []).length}`);
// 闭环的形状：第一条运行建议了续约策略，第二条带着 `_from` 真的去跑了它
expect('闭环成立：带 _from 的那一跳被画成"走过的"（事实），不是"建议过的"',
  gcanvas.includes('class="ge walked"'), gcanvas.slice(0, 400));
expect('画布上有节点（跑过的 = executed）', gcanvas.includes('gnode executed'));
expect('抽屉里有执行流（收起来了，不是删掉了）', gdrawer.includes('gd-in'), gdrawer.slice(0, 120));

/* ---- mountWorkbench 这条显式挂载路径也要通（宿主不用全局配置时走它） ---- */
const handle = mountWorkbench({ token: 'boot-smoke-token', fetcher, autoBoot: false });
expect('mountWorkbench 返回值带 reload', typeof handle?.reload === 'function');

/* ---- 目录取数失败：不许把已渲染的首屏清空 ---- */
const before = elements.get('left').innerHTML;
const brokenFetcher = async (path) => {
  if (path.endsWith('/intent/catalog')) throw new Error('模拟取数失败');
  return fetcher(path);
};
mountWorkbench({ token: 'boot-smoke-token', fetcher: brokenFetcher, autoBoot: false });
elements.get('toast').textContent = '';
try {
  await handle.reload();
  await new Promise((resolve) => setTimeout(resolve, 250));
  expect('目录取数失败时不清空已渲染内容',
    elements.get('left').innerHTML.length >= before.length * 0.5,
    `#left 从 ${before.length} 掉到 ${elements.get('left').innerHTML.length}`);
  expect('目录取数失败会说出来（toast）',
    elements.get('toast').textContent.includes('目录没取到'),
    `toast="${elements.get('toast').textContent}"`);
} catch (e) {
  failures.push(`目录取数失败时抛出了异常（应当降级而不是崩） → ${e.message}`);
}

/* ---- /me 取数失败：给的是可读的错误块，而不是空白页 ---- */
const deadFetcher = async () => { throw new Error('模拟装配失败'); };
mountWorkbench({ token: 'boot-smoke-token', fetcher: deadFetcher, autoBoot: false });
try {
  await handle.reload();
  await new Promise((resolve) => setTimeout(resolve, 200));
  const html = elements.get('left').innerHTML;
  expect('/me 失败时给出可读的错误块而不是空白',
    html.includes('工作台没装配起来') && html.includes('模拟装配失败'), html.slice(0, 120));
} catch (e) {
  failures.push(`/me 失败时抛出了异常（应当降级而不是崩） → ${e.message}`);
}

/* ---------------------------------------------------------------- 报告 */
console.log(`启动冒烟：${passed} 项通过，${failures.length} 项失败`);
if (failures.length) {
  console.log('');
  for (const line of failures) console.log(`  ✕ ${line}`);
  process.exit(1);
}
console.log('✓ 启动链无异常、首屏三级结构完整、徽标与标题正确、取数失败时降级不清屏');
