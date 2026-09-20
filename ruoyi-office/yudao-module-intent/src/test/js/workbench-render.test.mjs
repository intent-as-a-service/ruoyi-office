/**
 * 意图工作台 · 渲染断言
 * ---------------------------------------------------------------------------
 * 跑：node src/test/js/workbench-render.test.mjs
 *
 * 为什么需要它：工作台是**零构建**页面，没有编译期；而 `workbench-selfcheck.mjs`
 * 只能验"动作分发闭环 / 导入名 / 禁用词"这类**文本层面**的事，验不了"长什么样"。
 * 结果就是"改了渲染，只有打开浏览器点一遍才知道有没有坏"。
 *
 * 这个文件把 `workbench.js` 直接在 Node 里 import（DOM 绑定与自启都在守卫里，
 * 所以不需要 jsdom），灌一份 fixture 状态，对**生成的 HTML 字符串**做断言。
 * 它锁的是两轮设计里那几条最容易被改回去的规则：
 *
 *   1. 首屏必须有一个"主角"（唯一实心主按钮），**主语是对象名、谓语是意图名**；
 *   2. 风险三档排序：红在前、灰在后；
 *   3. 队列按**对象**聚合（不是一个信号一行）、金额是一级信息、多余的折成"还有 N 件"；
 *   4. **左栏上下文默认收成一行**（它是发起区，不是配置区）；
 *   5. 左栏是**导航面板**：图标瓦片 + 色块编码类别（不是一块一色），正文区不重复瓦片的标题；
 *   6. 档案没声明的块（如客服没有 explore）不许出现；
 *   7. **执行中不许有进度条**（设计稿 §6.3 / 技术方案 §7.1 的红线）；
 *   8. 没值的槽位不许被渲染成"有值"；
 *   9. **右栏是执行流**：同一意图的 Run 折叠成组、失败单独成组且不与成功混排；
 *  10. **同一个对象不许在一屏内说两遍**（「建议：X ＋」已从通栏挪到主角卡主语旁）。
 */
import { resolve, dirname } from 'node:path';
import { fileURLToPath, pathToFileURL } from 'node:url';

const here = dirname(fileURLToPath(import.meta.url));
const UI = resolve(here, '../../main/resources/intent-ui');

const failures = [];
const checks = [];
function ok(name) { checks.push(name); }
function fail(name, detail) { failures.push(`${name}${detail ? ` → ${detail}` : ''}`); }
function expect(name, condition, detail) { if (condition) ok(name); else fail(name, detail); }

const wb = await import(pathToFileURL(resolve(UI, 'js/workbench.js')).href);
const w = wb.__workbench();

/* ---------------------------------------------------------------- fixture */

const SLOT_TYPES = [
  { type: 'customer', key: 'customerId', title: '客户', entity: true, multi: true, hasCandidates: true },
  { type: 'contract', key: 'contractId', title: '合同', entity: true, multi: true, hasCandidates: true },
  { type: 'opportunity', key: 'businessId', title: '商机', entity: true, multi: true, hasCandidates: true },
  { type: 'timeWindow', key: 'timeWindow', title: '时间窗', entity: false, multi: false, values: ['近7天', '近30天', '近90天'] },
];

const ME = {
  userId: 1, name: '管理员', roles: ['super_admin'], systemName: '企业管理一体化平台',
  groupFallbackTitle: '跨实体与经营分析',
  views: [{ id: 'self', label: '我自己', roles: [] }, { id: 'lead', label: '主管视角', roles: ['super_admin'] }],
  defaultView: 'self',
  slotTypes: SLOT_TYPES,
  profile: {
    id: 'manager', label: '主管', title: '我的管理驾驶舱',
    blocks: ['context', 'today', 'todos', 'frequent', 'explore', 'history'],
    pinnedIntents: ['crm.customer.health-check'],
    presets: [{ id: 'renewal', label: '续约作战', description: '客户 + 合同' }],
    explore: { label: '团队客户体检', intents: ['crm.customer.health-check'] },
    emptyHint: '',
  },
};

const ENTRIES = [
  { id: 'crm.receivable.overdue-warning', name: '逾期风险预警', context: [{ key: 'customerId', title: '客户', required: true }], executorType: 'agent' },
  { id: 'crm.customer.health-check', name: '客户健康度诊断', context: [{ key: 'customerId', title: '客户', required: true }], executorType: 'agent' },
  // 续约策略 / 合同审批预检：生产里都有，而且**从来没跑过** ——
  // 网络那节要靠它们验"没跑过但在能力里 = available"这条状态。
  { id: 'crm.contract.renewal-plan', name: '续约策略', context: [{ key: 'contractId', title: '合同', required: true }], executorType: 'agent' },
  { id: 'crm.contract.audit-precheck', name: '合同审批预检', context: [{ key: 'contractId', title: '合同', required: true }], executorType: 'agent' },
  { id: 'crm.business.advance-strategy', name: '商机推进策略', context: [{ key: 'businessId', title: '商机', required: true }], executorType: 'agent' },
];

/** 一份照抄真实接口形状的 fixture：标题里有对象名、副标题里有金额、params 里有对象 id。 */
const SUGGESTIONS = [
  // 同一个客户的两期逾期 → 必须聚合成 **1 件事**，金额相加（155,200 + 36,000）
  { id: 's1', intentId: 'crm.receivable.overdue-warning', kind: 'item', count: 1,
    title: '客户「金辉电子科技有限公司」第 1 期回款已逾期 23 天',
    subtitle: '应收 155,200.00 元 · 约定 2026-08-27',
    reason: '逾期未回款，建议按分级动作尽快催收', params: { customerId: '326' } },
  { id: 's2', intentId: 'crm.receivable.overdue-warning', kind: 'item', count: 1,
    title: '客户「金辉电子科技有限公司」第 2 期回款已逾期 11 天',
    subtitle: '应收 36,000.00 元 · 约定 2026-09-08',
    reason: '逾期未回款，建议按分级动作尽快催收', params: { customerId: '326' } },
  { id: 's3', intentId: 'crm.receivable.overdue-warning', kind: 'item', count: 1,
    title: '客户「天诚智慧物流园区」第 1 期回款已逾期 17 天',
    subtitle: '应收 130,000.00 元 · 约定 2026-09-02',
    reason: '逾期未回款，建议按分级动作尽快催收', params: { customerId: '322' } },
  // 同一个客户身上挂着"钱"和"关系"两件事（来自不同意图）→ 同一组里摊开，各自可执行
  { id: 's4', intentId: 'crm.receivable.overdue-warning', kind: 'item', count: 1,
    title: '客户「宏图建筑设计院有限公司」第 1 期回款已逾期 2 天',
    subtitle: '应收 12,000.00 元 · 约定 2026-09-17',
    reason: '逾期未回款', params: { customerId: '320' } },
  { id: 's5', intentId: 'crm.customer.health-check', kind: 'item', count: 1,
    title: '客户「宏图建筑设计院有限公司」待跟进',
    subtitle: '上次联系 2026-08-24',
    reason: '分配给我的客户还没完成跟进', params: { customerId: '320' } },
  { id: 's6', intentId: 'crm.contract.audit-precheck', kind: 'item', count: 1,
    title: '合同「盛世传媒项目协作平台合同」等待审批',
    subtitle: '编号 HT-2026-0404 · 金额 90,000.00 元',
    reason: '需要我处理的审批', params: { contractId: '396' } },
  { id: 's7', intentId: 'crm.business.advance-strategy', kind: 'item', count: 1,
    title: '商机「朗润医疗·进销存管理」已停滞 46 天',
    subtitle: '商机金额 80,000.00 元',
    reason: '商机长期没有推进动作', params: { businessId: '370' } },
  { id: 'a1', intentId: 'crm.backlog.today-priority', kind: 'aggregate', count: 12, title: '今日跟进优先级' },
];

function applyFixture(overrides = {}) {
  w.setMe({ ...ME, ...(overrides.me || {}) });
  w.setEntries(overrides.entries || ENTRIES);
  w.setSuggestions(overrides.suggestions || SUGGESTIONS);
  w.setRuns(overrides.runs || []);
  w.setSlots(overrides.slots ?? [w.normalizeSlot('customer', ['320'], { label: '宏图建筑设计院' })]);
  w.state.navBlock = null;
  w.state.ctxOpen = false;
  w.state.view = 'self';
  w.state.session = [];
  w.state.hiddenRuns = [];
  w.state.runOpen = {};
  w.state.failedRunsOpen = false;
  w.state.right = 'runs';
  w.state.focus = null;
}

/* ---------------------------------------------------------------- 1. 主角卡 */

applyFixture();
const hero = w.heroCard();

expect('主角卡存在且带风险等级类', /class="hero sev-(danger|warning|info)"/.test(hero), hero.slice(0, 120));
expect('主角卡的主语是**对象名**（从数据里自动带出，不让用户再选一次）',
  hero.includes('金辉电子科技有限公司'), hero.slice(0, 200));
expect('主角卡的谓语是**意图名**（动词短语），不是一条事实陈述',
  hero.includes('逾期风险预警') && !hero.includes('第 1 期回款已逾期'),
  hero.slice(0, 260));
expect('主角卡只给一个实心主按钮', (hero.match(/btn pri/g) || []).length === 1,
  `实心按钮 ${(hero.match(/btn pri/g) || []).length} 个——有两个主按钮等于没有主按钮`);
expect('主角卡的"为什么是现在"带上了金额与逾期天数',
  hero.includes('19.12 万') && hero.includes('逾期 23 天'), hero.slice(0, 300));
expect('主角卡不出现假进度条', !hero.includes('progress-big') && !/width:\s*\d+%/.test(hero));

// 没有待办、也没有上下文时：主语位给的是**动作**，不是一句说明
applyFixture({ suggestions: [], slots: [] });
const heroEmpty = w.heroCard();
expect('既没有待办也没有对象时，主语位给的是动作',
  heroEmpty.includes('先选一个对象') || heroEmpty.includes('还没有可办的事'), heroEmpty.slice(0, 200));

/* ---------------------------------------------------------------- 2. 风险三档与排序 */

applyFixture();

const sev = (text, extra = {}) => w.severityOf({ title: text, ...extra });
expect('「逾期」判为 danger', sev('回款逾期') === 'danger');
expect('「停滞」判为 danger', sev('商机停滞 42 天') === 'danger');
expect('「即将到期」判为 warning', sev('合同即将到期') === 'warning');
expect('中性文案判为 info', sev('时间线整理') === 'info');
expect('5 条以上至少 warning', sev('例行事项', { count: 9 }) === 'warning');

const ordered = w.sortedSuggestions();
expect('排序：最该救的排在第一（标题里带"逾期"的那条）', ordered[0].id === 's1',
  `实际第一条 ${ordered[0]?.id}`);
expect('排序：风险等级单调不减', ordered.map((item) => w.severityOf(item))
  .every((s, i, arr) => i === 0 || ['danger', 'warning', 'info'].indexOf(arr[i - 1])
    <= ['danger', 'warning', 'info'].indexOf(s)), ordered.map((item) => w.severityOf(item)).join(' > '));
expect('排序不改动 aggregate（只排 item）', w.sortedSuggestions().every((item) => item.kind === 'item'));

/* ---------------------------------------------------------------- 3. 队列（按对象聚合） */

const groups = w.aggregates();
expect('8 条建议按对象聚合成 5 件事', groups.length === 5, `实际 ${groups.length} 组`);
expect('同一个客户的两期逾期合成一组', groups[0].items.length === 2 && groups[0].title === '金辉电子科技有限公司',
  JSON.stringify({ n: groups[0].items.length, title: groups[0].title }));
expect('同一意图内的金额相加（15.52 万 + 3.6 万 = 19.12 万）', groups[0].amount === 191200,
  String(groups[0].amount));
const crossIntent = groups.find((group) => group.title === '宏图建筑设计院有限公司');
expect('跨意图的两件事落在同一个对象组里', crossIntent?.items.length === 2 && crossIntent.intentIds.length === 2,
  JSON.stringify({ n: crossIntent?.items.length, intents: crossIntent?.intentIds }));
expect('不该跨意图求和：宏图的金额只取有钱的那条意图', crossIntent?.amount === 12000, String(crossIntent?.amount));
// 风险优先于金额：先把"要救"的救完，再谈钱。所以只比较**同一风险档内**的金额顺序。
const sameTier = ['danger', 'warning', 'info'].map((tier) =>
  groups.filter((g) => g.severity === tier).map((g) => g.amount ?? 0));
expect('同一风险档内：金额大的在前',
  sameTier.every((list) => list.every((n, i) => i === 0 || list[i - 1] >= n)),
  JSON.stringify(sameTier));

const queue = w.queueSection();
const shownGroups = groups.slice(0, 4);
const rowCount = (queue.match(/class="q-row/g) || []).length;
expect('队列最多显示 4 条', rowCount === 4, `实际 ${rowCount} 条`);
expect('队列把多余的折成"还有 N 件"', queue.includes('还有 1 件'), '没有给出剩余条数');
expect('队列每行有风险色条', (queue.match(/class="q-row sev-/g) || []).length === 4);
expect('队列行的**标题**是对象名（不是一个信号）',
  (queue.match(/class="q-t">([^<]*)</g) || []).map((m) => m.replace(/.*q-t">/, '').replace(/<$/, ''))
    .includes('金辉电子科技有限公司'),
  (queue.match(/class="q-t">[^<]*/g) || []).join(' | '));
expect('无主语的信号标题只能出现在对象名的**下级**（.q-subt），不能当行标题',
  !/class="q-t">[^<]*第 \d+ 期回款/.test(queue), '标题位又变回信号描述了');
expect('金额做成一级信息（money 标签）', queue.includes('class="money') && queue.includes('19.12 万'));
expect('尾部给出金额合计，并点名是哪条意图的口径',
  queue.includes('合计') && queue.includes('逾期风险预警'), queue.slice(-400));
// 一个对象上挂 N 件事 → N 个执行按钮；不合并成"办理 N 件"（一次执行只能跑一条意图）
const expectedButtons = shownGroups.reduce((sum, group) => sum + group.items.length, 0);
expect('一个对象上的多件事各自带执行按钮（不假装能一次办完）',
  (queue.match(/data-act="run-suggestion"/g) || []).length === expectedButtons,
  `期望 ${expectedButtons} 个，实际 ${(queue.match(/data-act="run-suggestion"/g) || []).length} 个`);
expect('不出现"办理 N 件"这种做不到的承诺', !/办理 \d+ 件/.test(queue));

// 少于可见条数时不该出现"还有 N 件"
applyFixture({ suggestions: SUGGESTIONS.slice(0, 3) });
expect('队列不足 4 件事时不出现折叠入口', !w.queueSection().includes('q-more'));

/* ---------------------------------------------------------------- 3b. 顶部通栏上下文 */

applyFixture();
const ctxCollapsed = w.contextBarHtml();
expect('上下文在顶部通栏里，且默认收起', ctxCollapsed.includes('ctxbar-in') && !ctxCollapsed.includes('ctx-editor'));
expect('收起态一行里能看到有值的槽位', ctxCollapsed.includes('客户') && ctxCollapsed.includes('宏图建筑设计院'),
  ctxCollapsed.slice(0, 300));
expect('收起态只给一个"加实体"入口',
  (ctxCollapsed.match(/data-act="add-slot"/g) || []).length === 1,
  `实际 ${(ctxCollapsed.match(/data-act="add-slot"/g) || []).length} 个`);
expect('收起态不显示"几条能力收起"的实施者语言', !ctxCollapsed.includes('能多用'),
  '首屏不该出现这句话');
// 「建议的对象」**不在通栏里**。它原来渲染在通栏（`建议：X ＋`），
// 但通栏的语义是"现在上下文里有什么"，而且它和主角卡的主语是**同一个对象**
// （两处都调 topActionable()），一屏内出现两遍。现已搬到主角卡主语旁边。
expect('通栏里不再出现「建议：X」——它和主角卡的主语是同一个对象，一屏说两遍',
  !ctxCollapsed.includes('accept-subject') && !ctxCollapsed.includes('建议：'),
  ctxCollapsed.slice(0, 400));
expect('主角卡主语旁的「摆进上下文」承担了原来那个采纳入口',
  w.subjectAdoptChip(w.topActionable()).includes('data-act="accept-subject"')
  && w.subjectAdoptChip(w.topActionable()).includes('摆进上下文'),
  w.subjectAdoptChip(w.topActionable()));
expect('主角卡真的把它渲染出来了',
  w.heroCard().includes('data-act="accept-subject"'), w.heroCard().slice(-300));

// 采纳之后不该再提示
w.state.slots = [w.normalizeSlot('customer', ['326'], { label: '金辉电子科技有限公司' })];
expect('对象已摆进上下文后不再提示', w.subjectAdoptChip(w.topActionable()) === '',
  w.subjectAdoptChip(w.topActionable()));

w.state.ctxOpen = true;
const ctxOpen = w.contextBarHtml();
expect('展开态给出空槽位与预设', ctxOpen.includes('＋ 选择合同') && ctxOpen.includes('续约作战'));
expect('展开态把"几条能力收起"收成一句', (ctxOpen.match(/能多用/g) || []).length <= 1,
  '又变成了好几条并列');
w.state.ctxOpen = false;

/* ---------------------------------------------------------------- 3b2. 上下文快照只在"不一致"时提醒 */

applyFixture();
// 没有任何执行记录时：给的是"摆上对象后会发生什么"，不是一句假同步
expect('没有执行记录时不说"已同步"', !w.contextStateHtml().includes('最近一次执行 ='), w.contextStateHtml());
applyFixture({
  slots: [w.normalizeSlot('timeWindow', ['近90天'], { label: '近90天' })],
  runs: [{ traceId: 'r1', intentId: 'x', intentName: 'X', status: 'SUCCESS', startedAt: Date.now(),
    durationMs: 100, contextSnapshot: { _slots: [{ type: 'timeWindow', key: 'timeWindow', values: ['近90天'] }] } }],
});
expect('上下文与最近一次执行一致时，给"已同步"', w.contextStateHtml().includes('最近一次执行 ='),
  w.contextStateHtml());
// 改一下上下文 → 必须变成"已变"的提醒（结果对应的是当时那套）
w.setSlots([w.normalizeSlot('customer', ['326'], { label: '金辉电子科技有限公司' })]);
expect('上下文变了之后提醒"结果按当时那套算"', w.contextStateHtml().includes('上下文已变'),
  w.contextStateHtml());

/* ---------------------------------------------------------------- 3c. 右栏是执行流，不是日志墙 */

const mkRun = (over, i) => ({
  traceId: `t${i}`, intentId: 'crm.receivable.overdue-warning', intentName: '逾期风险预警',
  status: 'SUCCESS', startedAt: Date.now() - i * 60000, durationMs: 23000,
  usage: { totalTokens: 7000 }, params: { customerId: '326' }, contextSnapshot: { _slots: [] }, ...over,
});
applyFixture({ runs: [
  mkRun({}, 0), mkRun({}, 1), mkRun({}, 2),
  mkRun({ traceId: 'f1', status: 'FAILED', durationMs: 48, error: { code: 'LLM_ERROR', message: 'No API key' } }, 3),
  mkRun({ traceId: 'f2', status: 'FAILED', durationMs: 44, error: { code: 'LLM_ERROR', message: 'No API key' } }, 4),
] });
const stream = w.streamPanels();
expect('同一意图的 3 次成功折叠成**一组**', (stream.match(/跑了 3 次/g) || []).length === 1,
  '还是在逐条平铺');
expect('组内只展开最新一条，其余折成一行', (stream.match(/mini-run/g) || []).length === 2);
expect('失败单独成组，不与成功混排', stream.includes('没跑成') && stream.includes('class="panel group-panel fail"'));
expect('失败组给了清理入口', stream.includes('data-act="clear-failed-runs"'));
expect('失败组说清是哪个时间段的记录', /没跑成[\s\S]{0,200}\d{2}:\d{2}/.test(stream));

// 有内容时不再渲染空态
expect('有 Run 时不再渲染"这一侧是执行流"空态', !stream.includes('这一侧是执行流'));
applyFixture({ runs: [] });
expect('确实没有 Run 时才给空态', w.streamPanels().includes('这一侧是执行流'));

/* ---------------------------------------------------------------- 4. 左栏导航面板 */

const ALL_BLOCKS = ['context', 'today', 'todos', 'frequent', 'explore', 'history'];

applyFixture();
const tiles = w.navTiles(ALL_BLOCKS);
expect('导航面板只剩两块（命中意图 + 做过的）',
  tiles.includes('命中意图') && tiles.includes('做过的')
  && !tiles.includes('今天要办的') && !tiles.includes('我常做的') && !tiles.includes('批量摸底'),
  tiles.slice(0, 200));
expect('每块都带图标（内联 SVG，不是 emoji）',
  (tiles.match(/<svg /g) || []).length === 2, `实际 ${(tiles.match(/<svg /g) || []).length} 个`);
expect('每块都可点且带块 id',
  (tiles.match(/data-act="select-block"/g) || []).length === 2);
expect('默认选中档案里的第一块（命中意图）', w.activeNavBlock() === 'todos', w.activeNavBlock());

// 配色：蓝=要你动手 / 灰=发生过。**红与琥珀不参与**——
// 它们已经被风险等级占用，导航块染红会被读成"这块有危险"。
// 左栏精简成两块之后只剩蓝与灰；这条断言跟着块数走，不是"必须三种颜色"。
expect('色块按类别分组（蓝=要你动手，灰=发生过）',
  /tone-blue/.test(tiles) && /tone-slate/.test(tiles)
  && !/tone-(red|amber)/.test(tiles),
  `blue=${(tiles.match(/tone-blue/g) || []).length} slate=${(tiles.match(/tone-slate/g) || []).length}`);
expect('导航色块里不出现红/琥珀（那是风险等级的颜色）',
  !/tone-(red|amber)/.test(tiles) && !tiles.includes('--tone: #EF4444') && !tiles.includes('--tone: #F59E0B'));

// 正文区不重复瓦片上的标题：改版前通栏里那个「建议：X ＋」就是被这条原则删掉的
applyFixture();
const pane = w.navBlockPanel();
expect('正文区渲染的是选中块的身体', pane.includes('q-row'), pane.slice(0, 200));
expect('正文区**不重复**块标题（瓦片已经说过了）', !pane.includes('今天要办的'), pane.slice(0, 300));

// 切块：换成"做过的"，正文跟着换
applyFixture({ runs: [{ traceId: 't1', intentId: 'x', intentName: '逾期风险预警', status: 'SUCCESS', startedAt: Date.now(), durationMs: 100, params: {}, contextSnapshot: { _slots: [] } }] });
w.state.navBlock = 'history';
expect('切到「做过的」后正文换成执行记录', w.navBlockPanel().includes('run-line'), w.navBlockPanel().slice(0, 200));
expect('切块后瓦片的选中态跟着走', /data-id="history"[^>]*|class="ntile tone-slate on"/.test(w.navTiles(ALL_BLOCKS))
  || w.navTiles(ALL_BLOCKS).includes('tone-slate on'));
w.state.navBlock = null;

// 「我该办的」曾经**在生产里恒空**，而测试看不出来。
//
// 病根：它读的是 `kind: 'aggregate'`，而后端从来不产出这个 kind
// （`/intent/catalog` 实测只返回 `kind: 'item'`）。上面那份 SUGGESTIONS 里的
// `{ kind: 'aggregate', count: 12 }` 是**测试自己造的** —— 绿测试 + 空功能。
//
// 现在改成按 intentId 聚合真实的 kind:'item'，所以下面这份"生产形状"的 fixture
// 必须能数出内容来（这正是回归点：谁再把数据源改回 aggregate，这条会立刻红）。
const PROD_SHAPED = SUGGESTIONS.filter((item) => item.kind !== 'aggregate');
applyFixture({ suggestions: PROD_SHAPED });
const todosTile = w.navTileInfo('todos');
expect('生产形状的数据下（只有 kind:item），「命中意图」数得出内容（不再恒空）',
  todosTile.empty !== true && /条意图/.test(todosTile.count), JSON.stringify(todosTile));
expect('分组口径是"一条意图一批"：组数 = 去重后的意图数',
  w.hitGroups().length === new Set(PROD_SHAPED.map((s) => s.intentId)).size,
  `${w.hitGroups().length} 组 vs ${new Set(PROD_SHAPED.map((s) => s.intentId)).size} 个意图`);
expect('组名用**意图名**，不是第一条建议的标题（分组的轴是能力，不是对象）',
  w.hitGroups().every((g) => g.title && !g.title.includes('客户「')),
  w.hitGroups().map((g) => g.title).join(' / '));
expect('组里带上对象数与条数（一行就是一条意图的分量）',
  w.hitGroups().every((g) => g.count > 0 && Array.isArray(g.objects)),
  JSON.stringify(w.hitGroups().map((g) => ({ t: g.title, c: g.count, o: g.objects.length }))));

// 压暗机制本身还要留着用（其它块可能真的数不出内容）
applyFixture({ suggestions: [] });
expect('真的没有命中时，瓦片压暗', w.navTileInfo('todos').empty === true);
expect('压暗的块在 HTML 上带 off 类', w.navTiles(ALL_BLOCKS).includes('off'));

/* ---------------------------------------------------------------- 5. 档案门控 */

applyFixture({ me: { ...ME, profile: { ...ME.profile, blocks: ['context', 'today', 'todos', 'frequent', 'history'], explore: null } } });
const noExplore = w.navTiles(['context', 'today', 'todos', 'frequent', 'history']);
expect('档案没声明 explore 时不出现批量摸底', !noExplore.includes('批量摸底'));
expect('档案没声明 explore 时不给「批量办理」', !w.navBlockPanel().includes('批量办理'));
expect('档案里没有的块不许被选中（不记一个不存在的选择）',
  (() => { w.state.navBlock = 'explore'; const got = w.activeNavBlock(); w.state.navBlock = null; return got !== 'explore'; })());

// 同一块里不许出现两个**动作、目标、文案都一样**的入口。
// 「批量摸底」原来就是这样：头部一个「新建」、身体里又一个「新建」——
// 和通栏那个「建议：X ＋」是同一个毛病（同一屏上把同一件事说两遍），只是形状不同。
//
// 比对的是 `动作 + 目标 + 可见文案` 三元组，不是只比动作名，理由有两条：
//   · 队列里每行各有自己的「执行」（同动作、**不同 id**）是对的；
//   · 「全部 ▸」和「还有 12 件 ▸」都开命令面板，但一个是总入口、一个是"剩下的"，
//     **文案不同、作用域不同**，不属于重复。
// 这样只有"同一个入口用同一句话出现两遍"才会被拦下。
//
// **`focus-intent` 例外**：它的目标是**意图**，而同一个意图会出现在多个对象行上
// （两个客户都该做「逾期风险预警」是正常的，不是重复）。而且它的行文案嵌在子 div 里，
// 这个正则读不到，硬比会误报。
const DUP_EXEMPT = new Set(['focus-intent']);
applyFixture();
for (const id of w.navBlockIds()) {
  w.state.navBlock = id;
  const html = w.navBlockPanel();
  const keys = [...html.matchAll(/data-act="([a-z-]+)"((?:\s+data-[a-z-]+="[^"]*")*)[^>]*>([^<]*)/g)]
    .filter((m) => !DUP_EXEMPT.has(m[1]))
    .map((m) => `${m[1]}|${(/data-id="([^"]*)"/.exec(m[2]) ?? [])[1] ?? ''}|${m[3].trim()}`);
  const dup = keys.find((k, i) => keys.indexOf(k) !== i);
  expect(`「${w.NAV_BLOCKS[id].title}」里没有"动作+目标+文案"全同的重复入口`,
    !dup, dup ? `重复：${dup}` : '');
}
// 但 `focus-intent` 的挂载必须**真的指得到图上的节点**：
// 挂多了 = 一行点两下同一件事；挂到一个图上没有的 id = 点了被忽略、抽屉却开出别的节点。
{
  w.state.navBlock = 'today';
  const rows = w.navBlockPanel();
  const rowCount = (rows.match(/class="q-row/g) || []).length;
  const attach = [...rows.matchAll(/class="q-main"[^>]*data-act="focus-intent"[^>]*data-id="([^"]*)"/g)]
    .map((m) => m[1]);
  expect('每个对象行最多挂一个 focus-intent（不重复挂）', attach.length <= rowCount,
    `行 ${rowCount} / 挂点 ${attach.length}`);
  expect('挂上去的 id 全都真的在网络上（否则点了会被忽略、还开出别的节点）',
    attach.every((id) => w.intentGraph().byId.has(id)),
    attach.filter((id) => !w.intentGraph().byId.has(id)).join(','));
  // 命中的意图现在**一定**会进图（见 intentGraph 里的说明），所以"图外意图不挂点击"
  // 从"常态"变成了防御性分支。这里断言的是那个不变量本身：挂上去的必须指得到。
  expect('命中意图全部进图 → 左栏不会出现"右侧找不到"的行',
    w.hitGroups().every((group) => w.intentGraph().byId.has(group.key)),
    w.hitGroups().filter((group) => !w.intentGraph().byId.has(group.key)).map((g) => g.title).join('、'));
}
w.state.navBlock = null;

/* ---------------------------------------------------------------- 6. 执行中不造假进度 */

applyFixture();
const runningRun = {
  traceId: 't1', intentId: 'crm.customer.health-check', intentName: '客户健康度诊断',
  status: 'RUNNING', startedAt: Date.now(), elapsedSeconds: 3, local: true, params: {}, contextSnapshot: { _slots: [] },
};
const running = w.renderRunCard(runningRun);
expect('执行中卡片不含进度条元素', !running.includes('progress-big'), '又出现了编出来的进度条');
expect('执行中卡片不含任何百分比宽度', !/width:\s*\d+%/.test(running));
expect('执行中卡片只给秒表', running.includes('已用') || running.includes('执行中'), running.slice(0, 160));

// 闸门看的是 state.session 里有没有在跑的 Run（不是按钮自己的状态）；
// 而 RunBar 只对"当前对准的那条意图"画执行按钮，所以还要给 state.current
w.state.session = [runningRun];
w.state.current = 'crm.customer.health-check';
const bar = w.renderRunBar();
expect('执行中时执行按钮被闸门挡住', bar.includes('先等它跑完'), '并发闸门没生效');
expect('被挡住时不出现实心执行按钮', !/btn pri" data-act="run-intent/.test(bar));
w.state.session = [];
w.state.current = null;

/* ---------------------------------------------------------------- 7. 没值就是没值 */

const emptySlot = w.normalizeSlot('customer', []);
expect('空槽位 values 为空数组', Array.isArray(emptySlot.values) && emptySlot.values.length === 0);
expect('空槽位 label 必须为空（不许把槽位名冒充成值）', emptySlot.label === '',
  `实际 label="${emptySlot.label}"，会被渲染成「客户 = 客户」`);
const dirtySlot = w.normalizeSlot('customer', ['', null, ' 320 ', '321']);
expect('空值被剔除、值被规整成字符串', dirtySlot.values.join(',') === '320,321', dirtySlot.values.join(','));
expect('多值自动进集合模式', dirtySlot.mode === 'SET');
const presetSlot = w.normalizeSlot('contract', [], { mode: 'SET' });
expect('预设声明的集合模式在空值时保留', presetSlot.mode === 'SET' && presetSlot.values.length === 0);

/* ---------------------------------------------------------------- 8. 主角卡的兜底 */

applyFixture({ suggestions: [] });
const heroFallback = w.heroCard();
expect('没有待办时，主角卡从「我常做的」里挑一条能跑的',
  heroFallback.includes('客户健康度诊断'), heroFallback.slice(0, 200));
expect('兜底主角卡仍然只有一个实心主按钮', (heroFallback.match(/btn pri/g) || []).length === 1);

/* ---------------------------------------------------------------- 9. 上下文驱动 */

// 一组里所有意图都缺同一件东西 → 整组收起 + 组头写清缺什么。
// 这是设计稿 §5.1「整块收起」在不改 entries 契约前提下的落地方式。
applyFixture({ slots: [] });   // 上下文是空的：客户/合同都缺
const contractGroup = [{ id: 'c1', name: '续约策略', context: [{ key: 'contractId', title: '合同', required: true }] }];
const gate = w.groupGateOf(contractGroup);
expect('整组都缺同一件东西时能算出缺什么', gate?.title === '合同', JSON.stringify(gate));

const mixedGroup = [
  { id: 'c1', name: '续约策略', context: [{ key: 'contractId', title: '合同', required: true }] },
  { id: 'c2', name: '客户体检', context: [] },
];
expect('组里有能跑的时候不整组收起', w.groupGateOf(mixedGroup) === null);

const gatedHtml = w.groupsHtml([{ key: 'contract', title: '合同', items: contractGroup }], 'palette', {}, '', () => '');
expect('缺参的组默认收起', !gatedHtml.includes('grp-bd'), '缺参的组被展开了，等于置灰一排用不上的按钮');
expect('缺参的组头写明缺什么', gatedHtml.includes('需要「合同」'), gatedHtml);

// 用户点过就听用户的：手动展开过就不该被自动收起
const openedHtml = w.groupsHtml([{ key: 'contract', title: '合同', items: contractGroup }], 'palette', { contract: true }, '', () => '<i>x</i>');
expect('用户手动展开后不再自动收起', openedHtml.includes('grp-bd'));

// 「我常做的」按焦点对象重排
const runsWithCustomer = [
  { intentId: 'crm.customer.health-check', params: { customerId: 320 } },
  { intentId: 'crm.customer.health-check', params: { customerId: 321 } },
  { intentId: 'crm.contract.renewal-plan', params: { contractId: 396 } },
  { intentId: 'crm.contract.renewal-plan', params: { contractId: 397 } },
  { intentId: 'crm.contract.renewal-plan', params: { contractId: 398 } },
];
applyFixture({ runs: runsWithCustomer, slots: [w.normalizeSlot('customer', ['320'], { label: '宏图' })] });
expect('焦点是客户时，「我常做的」用客户类历史计数',
  w.frequencyOf('crm.customer.health-check') === 2 && w.frequencyOf('crm.contract.renewal-plan') === 0,
  `health=${w.frequencyOf('crm.customer.health-check')} renewal=${w.frequencyOf('crm.contract.renewal-plan')}`);
applyFixture({ runs: runsWithCustomer, slots: [w.normalizeSlot('contract', ['396'], { label: 'HT-396' })] });
expect('焦点换成合同时，排序随之反转',
  w.frequencyOf('crm.contract.renewal-plan') === 3 && w.frequencyOf('crm.customer.health-check') === 0,
  `renewal=${w.frequencyOf('crm.contract.renewal-plan')} health=${w.frequencyOf('crm.customer.health-check')}`);
applyFixture({ runs: runsWithCustomer, slots: [] });
expect('没有焦点对象时退回全局计数', w.frequencyOf('crm.contract.renewal-plan') === 3);

// 因果提示（现在渲染在通栏右侧的状态位）
w.state.ctxDelta = { type: 'contract', title: '合同', delta: 3, action: 'add' };
expect('增量提示说清是"加了什么、多了几条"',
  w.contextStateHtml().includes('合同') && w.contextStateHtml().includes('3 条能力'), w.contextStateHtml());
expect('增量提示不外泄内部动作名', !w.contextStateHtml().includes('add'));
w.state.ctxDelta = { type: 'contract', title: '合同', delta: -2, action: 'remove' };
expect('去掉槽位时提示是"收起"', w.contextStateHtml().includes('收起'));
w.state.ctxDelta = null;
expect('没有变更时回到普通状态位', !w.contextStateHtml().includes('条能力'),
  w.contextStateHtml());

/* ---------------------------------------------------------------- 10. 结果卡的视觉语法 */

// 摘要里的带单位数字要被挑出来加粗；日期、序号不能被误伤
const lead = w.emphasizeNumbers('逾期 155,200.00 元，最长 23 天，约定 2026-08-27，共 5 笔');
expect('摘要里的金额/天数/笔数被加粗', (lead.match(/<b class="n">/g) || []).length === 3, lead);
expect('日期不会被误当成指标', !lead.includes('<b class="n">2026'), lead);

// kv → 指标卡：数字大号、标签小号
const metrics = w.metricsHtml([
  { label: '回款计划逾期', value: '5 笔' },
  { label: '今日需联系客户', value: '0' },
  { label: '状态', value: '已完成' },
]);
expect('kv 渲染成指标卡',
  metrics.includes('class="metrics"') && (metrics.match(/class="metric[ "]/g) || []).length === 3, metrics);
expect('纯文字值不假装成大数字', metrics.includes('metric word'));

// table → 行动卡：编号 + 对象当标题 + 动作当正文 + 其余降为元信息
const table = w.tableHtml({
  columns: ['优先级', '对象', '具体动作', '为什么现在做', '预计耗时'],
  rows: [
    ['1', '逾期回款计划 5 笔', '逐笔调出对应合同与客户，按逾期天数从长到短电话催收', '回款计划已逾期 5 笔，是当前唯一直接压在资金上的事项', '约 45 分钟'],
    ['2', '即将到期合同 1 份', '确认到期日与客户续约意向，当天发起续约沟通', '合同只剩 1 份即将到期，时间窗口最窄', '约 30 分钟'],
  ],
});
expect('表格转成行动卡（不再是 5 列文字墙）', table.includes('act-cards') && !table.includes('<table'), table.slice(0, 200));
expect('编号当了徽标', table.includes('<span class="ac-no">1</span>'));
expect('第二列当标题、第三列当正文', table.includes('ac-title') && table.includes('ac-body'));
expect('其余列降为元信息', table.includes('ac-meta') && table.includes('为什么现在做') && table.includes('预计耗时'));

// 判不了就退回表格（不能硬转）
const wide = w.tableHtml({
  columns: ['A', 'B', 'C', 'D', 'E', 'F'],
  rows: [['甲甲甲甲甲', '乙', '丙', '丁', '戊', '己']],
});
expect('首列不像编号就退回普通表格', wide.includes('<table'), wide.slice(0, 160));

// 整张结果卡：结论先行 + 块级渲染
applyFixture();
const cardHtml = w.renderOutput({
  output: {
    title: '今日跟进优先级',
    summary: '今天排在最前面的是金辉电子科技有限公司：2 期回款逾期共 19.12 万。',
    blocks: [
      { kind: 'kv', title: '总览', items: [{ label: '逾期回款', value: '5 笔' }] },
      { kind: 'table', columns: ['优先级', '对象', '具体动作', '为什么现在做'],
        rows: [['1', '逾期回款计划 5 笔', '逐笔电话催收并确认到账日期', '唯一直接压在资金上的事项']] },
    ],
  },
});
expect('结果卡以"结论"开头', cardHtml.trimStart().startsWith('<div class="out-lead">'), cardHtml.slice(0, 80));
expect('结论里的数字被加粗', cardHtml.includes('<b class="n">19.12 万</b>'), cardHtml.slice(0, 200));
expect('块标题有视觉锚点（blk-title）', cardHtml.includes('class="blk-title"'));
expect('总览渲染成指标卡而不是两列小字', cardHtml.includes('class="metric'));

/* ---------------------------------------------------------------- 11. 结果卡默认态：一行结论 + 下一步 + 查看详情 */

const LONG_OUTPUT = {
  title: '今日跟进优先级',
  summary: '今天排在最前面的是金辉电子科技有限公司：2 期回款逾期共 19.12 万，已逾期 23 天且 38 天没有联系；'
    + '另有 1 份合同即将到期、2 份合同待你审批。建议先处理逾期回款与合同节点，再做客户与线索触达。',
  blocks: [
    { kind: 'kv', title: '今日待办总览', items: [
      { label: '回款计划逾期', value: '5 笔' }, { label: '待跟进客户', value: '5 位' }] },
    { kind: 'table', title: '今天先做的 5 件事',
      columns: ['优先级', '对象', '具体动作', '为什么现在做', '预计耗时'],
      rows: [['1', '逾期回款计划 5 笔', '逐笔调出对应合同与客户，按逾期天数从长到短电话催收并确认到账日期',
        '回款计划已逾期 5 笔，是当前唯一直接压在资金上的事项，拖一天就多一天账期风险', '约 45 分钟']] },
    { kind: 'text', title: '线索明细', text: '线索·盛世广告传媒（L3）：距上次联系 15 天，需求模糊' },
  ],
  nextIntents: [
    // 字段名照抄生产信封：intentId / title / reason / params
    { intentId: 'crm.customer.health-check', title: '客户健康度诊断（宏图建筑设计院）',
      reason: '客户 320 待跟进', params: { customerId: '320' } },
    { intentId: 'crm.contract.audit-precheck', title: '合同审批预检（HT-2026-0404）',
      reason: '2 份合同卡在我这里', params: { contractId: '396' } },
  ],
};
const CARD_RUN = {
  traceId: 'card-1', intentId: 'crm.receivable.overdue-warning', intentName: '逾期风险预警',
  status: 'SUCCESS', startedAt: Date.now(), durationMs: 23000, usage: { totalTokens: 7000 },
  params: { customerId: '326' }, contextSnapshot: { _slots: [] }, output: LONG_OUTPUT,
};

applyFixture();
const brief = w.briefHtml(CARD_RUN);
expect('默认态只给结论、不铺全文（最多两句）',
  brief.includes('19.12 万') && !brief.includes('建议先处理逾期回款'), brief);
expect('结论里的数字被加粗', brief.includes('<b class="n">'));
const THIN = '结论：预警档，健康度 28/100。A 级重点客户已 37 天未联系、在手商机为 0。'
  + '同时 3 份合同里已有 2 笔逾期。合同存量尚可但两端同时亮红灯。';
expect('首句太薄时会补第二句（否则不知道说的是谁）',
  w.briefLead(THIN).includes('28/100') && w.briefLead(THIN).includes('37 天未联系'), w.briefLead(THIN));
expect('补到第二句就停，不会连成一段',
  !w.briefLead(THIN).includes('两端同时亮红灯'), w.briefLead(THIN));
expect('没有句号时按长度截断', w.briefLead('啊'.repeat(200)).endsWith('…'),
  `长度 ${w.briefLead('啊'.repeat(200)).length}`);

const detail = w.detailHtml(CARD_RUN);
expect('详情默认收起', detail.includes('查看详情') && !detail.includes('rc-detail">'));
expect('收起时告诉用户里面有什么', detail.includes('class="rh"') && detail.includes('项总览')
  && detail.includes('件事') && detail.includes('段文字'), detail.slice(0, 200));
expect('收起时不渲染正文', !detail.includes('<table') && !detail.includes('act-cards'), detail.slice(0, 300));

w.state.detailOpen['card-1'] = true;
const opened = w.detailHtml(CARD_RUN);
expect('展开后给出完整正文', opened.includes('rc-detail') && opened.includes('act-cards'));
expect('展开后摘要不再重复（默认态已经显示过第一句）',
  !opened.includes('class="out-lead"'), opened.slice(0, 300));
w.state.detailOpen = {};

const card = w.renderRunCard(CARD_RUN);
expect('结果卡默认不铺大段正文', !card.includes('act-cards') && !card.includes('<table'),
  '又把正文铺开了');
expect('结果卡默认给下一步意图', card.includes('下一步') && card.includes('客户健康度诊断'), card.slice(-600));
expect('结果卡默认给查看详情入口', card.includes('data-act="toggle-detail"'));
expect('结果卡仍然保留依据与重跑', card.includes('依据') || card.includes('data-act="toggle-evidence"'),
  card.slice(0, 400));

// 回归：信封字段是 `nextIntents`（驼峰）。前端曾写成全小写 `nextintents`，
// JS 区分大小写 → 取值恒为 undefined → 「下一步意图」自上线起就没渲染出来过。
expect('认得驼峰 nextIntents（历史 bug 回归）',
  w.renderRunCard(CARD_RUN).includes('客户健康度诊断'), '又读成小写导致下一步为空');
expect('也容错全小写 nextintents', (() => {
  const lower = { ...CARD_RUN, output: { ...LONG_OUTPUT, nextintents: LONG_OUTPUT.nextIntents } };
  delete lower.output.nextIntents;
  return w.renderRunCard(lower).includes('客户健康度诊断');
})());

/* ---------------------------------------------------------------- 12. 意图网络 */

/**
 * 这一节锁的是网络的**语义**，不是长相。三条最容易被改回去的规则：
 *   1. "建议过" ≠ "走过"——只有留痕里带 `_from` 的那一跳才算事实；
 *   2. 边权是 M/N，不是次数；样本少的要标出来；
 *   3. 没跑过的节点不说"没有下一步"，只说"没观测过"。
 */
const NODE_A = 'crm.customer.health-check';
const NODE_B = 'crm.contract.renewal-plan';

/** 一条跑过的 run + 它的下一步建议。 */
const mkGraphRun = (over = {}) => ({
  traceId: 'g1', intentId: NODE_A, intentName: '客户健康度诊断', status: 'SUCCESS',
  startedAt: Date.now() - 60000, durationMs: 1000, params: { customerId: '320' },
  contextSnapshot: { _slots: [] },
  output: { title: 'X', summary: 'Y', blocks: [], nextIntents: [{ intentId: NODE_B, title: '续约策略', reason: '合同快到期' }] },
  ...over,
});

applyFixture({ runs: [mkGraphRun()] });
let g = w.intentGraph();
expect('节点 = 跑过的 ∪ 边提到过的 ∪ **命中的**',
  [NODE_A, NODE_B].every((id) => g.byId.has(id))
  && w.hitGroups().every((group) => g.byId.has(group.key)),
  g.nodes.map((n) => n.name).join(','));
expect('边从 nextIntents 来', g.edges.length === 1 && g.edges[0].proposed === 1);
expect('没有任何 _from 时，边不是"走过的"',
  w.graphEdgeKind(g.edges[0], '', g.live) === 'proposed', w.graphEdgeKind(g.edges[0], '', g.live));

// ★ 闭环：第二次运行带着 `_from` 去跑了那条建议 → 边升级成"走过的"（事实）
applyFixture({ runs: [mkGraphRun(), mkGraphRun({
  traceId: 'g2', intentId: NODE_B, intentName: '续约策略', startedAt: Date.now(), params: {},
  contextSnapshot: { _slots: [], _from: { intentId: NODE_A, traceId: 'g1', kind: 'nextIntent' } },
  output: { title: 'Y', summary: 'Z', blocks: [] },
})] });
g = w.intentGraph();
const loopEdge = g.edges.find((e) => e.from === NODE_A && e.to === NODE_B);
expect('闭环：带 _from 的那一跳被记成"走过 1 次"', loopEdge?.walked === 1, JSON.stringify(loopEdge));
expect('走过的边压过建议过的（事实强于建议）',
  w.graphEdgeKind(loopEdge, '', g.live) === 'walked', w.graphEdgeKind(loopEdge, '', g.live));
expect('走过的边标签给次数，不给 M/N',
  w.graphEdgeLabel(loopEdge, 'walked').text === '走过 1 次', w.graphEdgeLabel(loopEdge, 'walked').text);
expect('网络头部把"走过几条"单独报出来', w.graphHeadHtml(g, NODE_A).includes('走过'));

// 焦点切换 → 前瞻边换人，但**布局不变**（重排会丢空间记忆）
applyFixture({ runs: [mkGraphRun(), mkGraphRun({
  traceId: 'g2', intentId: NODE_B, intentName: '续约策略', startedAt: Date.now(),
  contextSnapshot: { _slots: [] }, output: { title: 'Y', summary: 'Z', blocks: [] },
})] });
g = w.intentGraph();
const layoutA = w.graphLayout(g);
const posA = JSON.stringify([...layoutA.pos.entries()]);
expect('焦点在 A 时，A→B 是前瞻边',
  w.graphEdgeKind(g.edges[0], NODE_A, g.live) === 'live');
expect('焦点切到 B 之后，同一条边不再算前瞻',
  w.graphEdgeKind(g.edges[0], NODE_B, g.live) !== 'live');
expect('切焦点不重排布局（坐标完全一致）',
  JSON.stringify([...w.graphLayout(w.intentGraph()).pos.entries()]) === posA);

// M/N 口径 + 样本少
applyFixture({ runs: [mkGraphRun(), mkGraphRun({ traceId: 'g2' }), mkGraphRun({ traceId: 'g3' })] });
g = w.intentGraph();
const thin = g.edges[0];
expect('父节点跑 3 次、建议 3 次 → 3/3，且不再算"样本少"',
  thin.parentRuns === 3 && thin.proposed === 3 && w.graphEdgeLabel(thin, 'proposed').cls === 'proposed',
  JSON.stringify({ p: thin.proposed, n: thin.parentRuns, cls: w.graphEdgeLabel(thin, 'proposed').cls }));
applyFixture({ runs: [mkGraphRun()] });
g = w.intentGraph();
expect('父节点只跑 1 次 → 1/1 并标"样本少"（不给一个没分母的 100%）',
  w.graphEdgeLabel(g.edges[0], 'proposed').text === '1/1'
  && w.graphEdgeLabel(g.edges[0], 'proposed').cls === 'thin',
  JSON.stringify(w.graphEdgeLabel(g.edges[0], 'proposed')));

// 状态与"现在能跑"：这是不依赖历史的那一层
applyFixture({ runs: [], suggestions: [] });
g = w.intentGraph();
expect('没有 run、也没有命中 → 图是空的（一个节点都不编）', g.nodes.length === 0, `${g.nodes.length}`);
// 反过来：没跑过、但**上下文命中了** → 必须进图（否则左栏那行在右侧找不到）
applyFixture({ runs: [], suggestions: SUGGESTIONS });
g = w.intentGraph();
expect('没跑过但命中的意图也进图（左栏有的，右侧一定找得到）',
  g.nodes.length === w.hitGroups().length && g.edges.length === 0,
  `节点 ${g.nodes.length} / 命中 ${w.hitGroups().length} / 边 ${g.edges.length}`);
applyFixture({ runs: [mkGraphRun()] });
g = w.intentGraph();
const bNode = g.byId.get(NODE_B);
expect('跑过的节点标成 executed', g.byId.get(NODE_A).source === 'executed');
expect('没跑过但在能力里的节点标成 available（不是"没有下一步"）',
  bNode.source === 'available', bNode.source);
expect('没跑过的节点给出"没观测过"而不是"没有下一步"',
  w.graphDrawerHtml(g, NODE_B).includes('没观测过'), w.graphDrawerHtml(g, NODE_B).slice(0, 300));

// 不在档案能力里的节点：画出来，但不给执行入口
applyFixture({
  runs: [mkGraphRun({ output: { title: 'X', summary: 'Y', blocks: [],
    nextIntents: [{ intentId: 'crm.not.in.catalog', title: '档案外意图', reason: 'x' }] } })],
});
g = w.intentGraph();
const outside = g.byId.get('crm.not.in.catalog');
expect('档案外的下一步也画出来（模型确实建议过），并标成 outside',
  outside?.source === 'outside', outside?.source);
expect('档案外的节点不给"现在能跑"',
  !w.graphNodeHtml(outside, { x: 0, y: 0 }, false, false).includes('现在能跑'));

// 左栏角标：只有跑过才有，且用**前瞻**数（不是累计建议过的不同目标数）
applyFixture({ runs: [mkGraphRun()] });
expect('跑过的意图：角标 = 最近一次运行给出的下一步数',
  w.graphDownstream(NODE_A) === 1, String(w.graphDownstream(NODE_A)));
expect('没跑过的意图：角标为 0（左栏据此**不渲染**角标，而不是画一个 →0）',
  w.graphDownstream(NODE_B) === 0, String(w.graphDownstream(NODE_B)));

/* ------------------------------------------------ 12b. 左栏与中间的"缝合" */

/**
 * 这一节锁的是**左边和右边不许割裂**。第一版它们真的断了：
 *   · 左栏行只渲染对象名、网络节点只渲染意图名 → 两边零重合字，用户对不上；
 *   · 左栏完全不读焦点 → 点了行只有右边动，左边毫无反应；
 *   · 主角卡（系统建议）和焦点（用户在看的）是两个"当前"，不一致时界面一声不吭。
 */

// ① 共同语言：左栏行里必须出现意图名，而且**和网络节点是同一个字符串**
//
// 注意不能拿"第一条队列行"去比：队列里有些意图**根本不在网络上**
// （没跑过、也没被任何边提到），那些行没有对应节点，比不出共同语言。
// 所以先让队列第一条的意图进图，再比。
applyFixture({ runs: [mkGraphRun()] });
const topIntent = w.suggestedIntentId();
applyFixture({ runs: [mkGraphRun(), mkGraphRun({
  traceId: 'gq', intentId: topIntent, intentName: '队列第一条', startedAt: Date.now() - 20000,
  contextSnapshot: { _slots: [] }, output: { title: 'x', summary: 'y', blocks: [] },
})] });
const queueHtml = (() => { w.state.navBlock = 'todos'; const h = w.navBlockPanel(); w.state.navBlock = null; return h; })();
const graphNodeNames = w.intentGraph().nodes.map((n) => n.name);
// 这一版**行首就是意图名**（意图当主语），不再另挂一个 chip——
// 所以共同语言验的是"行首那个字符串和网络节点逐字相同"。
expect('命中意图的行首是意图名（和网络节点同一套词）',
  graphNodeNames.some((name) => queueHtml.includes(`<div class="q-t">${name}`)),
  `图上节点名：${graphNodeNames.join(' / ')}`);
expect('没进网的意图不冒充"图上有它"（角标与点击都不给）',
  !/q-row[^>]*>\s*<div class="q-main" data-act="focus-intent"[^>]*data-id="crm\.receivable\.overdue-warning"/.test(queueHtml)
  || w.intentGraph().byId.has('crm.receivable.overdue-warning'),
  '队列里未进网的意图不该挂 focus-intent');

// ② 回显焦点：焦点所在的行要亮，别的行不亮
applyFixture({ runs: [mkGraphRun(), mkGraphRun({
  traceId: 'gq', intentId: topIntent, intentName: '队列第一条', startedAt: Date.now() - 20000,
  contextSnapshot: { _slots: [] }, output: { title: 'x', summary: 'y', blocks: [] },
})] });
w.state.current = topIntent;
const focusedHtml = (() => { w.state.navBlock = 'today'; const h = w.navBlockPanel(); w.state.navBlock = null; return h; })();
expect('焦点那一行亮（.q-row.sel）', /\bq-row\b[^"]*\bsel\b/.test(focusedHtml),
  focusedHtml.slice(0, 400));
expect('点左栏行 → 网络焦点确实跟着走（两边读同一个 state.current）',
  w.graphFocusId(w.intentGraph()) === topIntent, String(w.graphFocusId(w.intentGraph())));
w.state.current = null;

// ③ 两个"当前"的缝合：焦点 ≠ 建议时明说，且给一键回到建议
applyFixture({ runs: [mkGraphRun()] });
// 让**系统建议的那条意图也进图**——否则"回到建议"无处可去，按钮本来就该不显示
// （这不是缺陷：建议的意图还没进过网络时，网络上没有它的节点）
const suggestedId = w.suggestedIntentId();
expect('fixture 里能算出系统建议的意图', !!suggestedId, String(suggestedId));
applyFixture({ runs: [mkGraphRun(), mkGraphRun({
  traceId: 'g9', intentId: suggestedId, intentName: '建议的那条', startedAt: Date.now() - 30000,
  contextSnapshot: { _slots: [] }, output: { title: 'x', summary: 'y', blocks: [] },
})] });
expect('焦点跟着系统建议时，不出现「你正在看」提示条（不制造噪声）',
  !w.focusBarHtml().includes('你正在看'), w.focusBarHtml());
w.state.current = NODE_B;   // 用户去看了别的东西
const focusbar = w.focusBarHtml();
expect('焦点偏离系统建议时，明说「你正在看 X」',
  focusbar.includes('你正在看') && focusbar.includes('续约策略'), focusbar);
expect('并给一键回到建议的入口', focusbar.includes('data-act="clear-focus"'), focusbar);
expect('「回到建议」写的是建议那条意图的名字', focusbar.includes('回到建议'), focusbar);
w.state.current = null;
expect('焦点交还之后提示条消失', !w.focusBarHtml().includes('你正在看'), w.focusBarHtml());

// ④ 布局按可用宽度自适应：抽屉并列占宽后，第三列不许被推到屏幕外
applyFixture({ runs: [mkGraphRun()] });
const wideL = w.graphLayout(w.intentGraph(), 1120);
const narrowL = w.graphLayout(w.intentGraph(), 720);
const inWidth = (layout, width) => [...layout.pos.values()]
  .every((p) => p.x >= 0 && p.x + w.GRAPH.nodeW <= width + 1);
expect('宽屏下所有节点在画布内', inWidth(wideL, 1120), JSON.stringify(wideL.xs));
expect('窄屏（抽屉放开）下所有节点仍在画布内', inWidth(narrowL, 720),
  `xs=${JSON.stringify(narrowL.xs)} 右缘=${Math.max(...[...narrowL.pos.values()].map((p) => p.x + w.GRAPH.nodeW))}`);
expect('窄屏的列距比宽屏小（是收窄，不是硬裁）', narrowL.xs[1] < wideL.xs[1],
  `${JSON.stringify(narrowL.xs)} vs ${JSON.stringify(wideL.xs)}`);
expect('但列距有下限，不会挤成一坨',
  narrowL.xs[1] - narrowL.xs[0] >= w.GRAPH.nodeW + w.GRAPH.minGap - 1,
  `gap=${narrowL.xs[1] - narrowL.xs[0]}`);

/* ------------------------------------------- 12c. 评审后修掉的三处"名不副实" */

/**
 * 一位业务视角评审给出的三条，全部是"界面说的比数据能支撑的强"：
 *   ① 「我该办的」恒空 —— 却和上面「今天要办的 16 件」并列，两块互相打脸；
 *   ② 「我常做的」前 3 个跑过 0 次（档案预置的），标签却叫"我常做的"；
 *   ③ 网络的横向排布被读成 SOP，而同层其实是并列、边多数只是"建议过"。
 */

// ② 左栏精简成两块：命中意图 + 做过的
//
// 「我常做的」整块撤了（它不随上下文变，且实测前 3 个跑过 0 次），所以**它的断言也一起走**——
// 留着就是"绿测试 + 死代码"，正是 `kind:'aggregate'` 那次事故的形状。
applyFixture({ runs: [mkGraphRun()] });
expect('导航面板只剩两块：命中意图 + 做过的',
  Object.keys(w.NAV_BLOCKS).join(',') === 'todos,history', Object.keys(w.NAV_BLOCKS).join(','));
expect('「我常做的」正文函数已删除（不留看着还活着的死代码）',
  typeof w.NAV_BLOCKS.frequent === 'undefined');
// 批量摸底的动作是**函数**（渲染时才算），因为 NAV_BLOCKS 是模块加载时求值的常量，
  // 那时 state.me 还是 null —— 写成字符串会被永久冻成空
expect('「批量摸底」不再是块，但入口转移到块动作上（功能没丢）',
  typeof w.NAV_BLOCKS.explore === 'undefined'
  && typeof w.NAV_BLOCKS.todos.action === 'function'
  && (w.NAV_BLOCKS.todos.action() ?? '').includes('open-batch'),
  typeof w.NAV_BLOCKS.todos.action);
expect('块动作在渲染时求值（不是模块加载时冻住的）',
  w.navBlockPanel().includes('open-batch') || !(w.state.me?.profile?.explore?.intents ?? []).length,
  'NAV_BLOCKS.action 必须是函数，否则 state.me 还没加载就被算成空串');
const tileInfo = w.navTileInfo('todos');
expect('命中意图瓦片：几条意图 + 几条命中',
  /条意图/.test(tileInfo.count) && /条命中/.test(tileInfo.tag), JSON.stringify(tileInfo));

// ③ 命中意图：**意图当主语**，展开才出对象明细
applyFixture({ runs: [mkGraphRun()] });
const hits = w.hitGroups();
const hitsHtml = w.hitIntentsBody();
expect('按意图分组：组数 = 去重后的 intentId 数',
  hits.length === new Set(w.state.suggestions.filter((s) => s.kind === 'item').map((s) => s.intentId)).size,
  `${hits.length} 组`);
expect('行首是**意图名**，不是对象名',
  /<div class="q-t">[^<]*客户健康度诊断|>[\u4e00-\u9fa5]{2,}<span class="q-badge"/.test(hitsHtml),
  hitsHtml.slice(0, 200));
expect('一行里给出分量：金额 / 条数 / 对象数',
  hitsHtml.includes('class="money') && hitsHtml.includes('条</span>'), hitsHtml.slice(0, 300));
expect('对象明细默认收起（展开才出）', !hitsHtml.includes('class="q-sub"'));
w.state.openTodos = hits[0]?.key;
const expanded = w.hitIntentsBody();
expect('展开后出对象明细，每行一个执行按钮',
  expanded.includes('class="q-sub"') && expanded.includes('data-act="run-suggestion"'), expanded.slice(0, 400));
expect('展开后给"对这一批一起办理"（批量没丢，只是换了位置）',
  expanded.includes('data-act="run-todo"'), expanded.slice(0, 500));
w.state.openTodos = null;

// ④ 网络标明"参考不是流程"，同层并列
applyFixture({ runs: [mkGraphRun()] });
const gHead = w.graphHeadHtml(w.intentGraph(), NODE_A);
expect('网络头部常驻「参考，不是流程」', gHead.includes('参考，不是流程'), gHead.slice(0, 200));
expect('头部写明「同层并列，没有先后」', gHead.includes('同层并列'), gHead.slice(0, 240));
applyFixture({ runs: [mkGraphRun(), mkGraphRun({
  traceId: 'g2', intentId: NODE_B, intentName: '续约策略', startedAt: Date.now(),
  contextSnapshot: { _slots: [] },
  output: { title: 'y', summary: 'z', blocks: [], nextIntents: [{ intentId: 'crm.contract.audit-precheck', title: '合同审批预检', reason: 'r' }] },
})] });
const gCanvas = w.graphCanvasHtml(w.intentGraph(), w.graphLayout(w.intentGraph()), NODE_A);
const colHeads = [...gCanvas.matchAll(/class="gcol"[^>]*>([^<]*)/g)].map((m) => m[1].trim());
expect('起点列头不再自称「你自己发起的」（判据是"没被推荐过"）',
  colHeads[0]?.includes('没被别处推荐过'), colHeads[0]);
expect('非起点的列头写明「并列 N 条（没有先后）」',
  colHeads.slice(1).every((l) => l.includes('并列') && l.includes('没有先后')), colHeads.join(' | '));

/* ---------------------------------------------------------------- 报告 */

console.log(`意图工作台渲染断言：${checks.length} 项通过，${failures.length} 项失败`);
if (failures.length) {
  console.log('');
  for (const line of failures) console.log(`  ✕ ${line}`);
  process.exit(1);
}
console.log('✓ 主角卡（对象当主语/意图当谓语）/ 按对象聚合 / 金额一级信息 / 队列截断 /'
  + ' 顶部通栏上下文 / 快照只在漂移时提醒 / 左栏导航面板（图标瓦片·色块编码类别） / 档案门控 / 无反假进度 /'
  + ' 结果卡视觉语法（结论→指标卡→行动卡） / 意图网络（建议≠走过·闭环·M/N·切焦点不重排）全部成立');
