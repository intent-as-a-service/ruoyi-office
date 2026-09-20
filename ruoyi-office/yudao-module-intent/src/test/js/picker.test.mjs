/**
 * 意图工作台 · 选择器契约自检
 * ---------------------------------------------------------------------------
 * 跑：node src/test/js/picker.test.mjs
 *
 * 锁住《四点优化设计》§4 里那几条"看着像小事、换宿主就出事"的规则：
 *   1. 四类 kind 的解析与**降级**（声明了 custom 但宿主没注册，必须退到手输而不是抛异常）；
 *   2. `id` 一律字符串（跨系统主键类型不同，这是同一个前端接不同系统的前提）；
 *   3. 候选响应**两种形状都要吃**（新前端 + 老宿主必须能用）；
 *   4. 满页却没给游标时**不许**假装"还有更多"；
 *   5. `custom` 适配器的返回被规整成统一契约，取消返回 null。
 */
import { resolve, dirname } from 'node:path';
import { fileURLToPath, pathToFileURL } from 'node:url';

const here = dirname(fileURLToPath(import.meta.url));
const UI = resolve(here, '../../main/resources/intent-ui');
const P = await import(pathToFileURL(resolve(UI, 'js/picker.mjs')).href);

const failures = [];
let passed = 0;
function expect(name, condition, detail) {
  if (condition) passed += 1;
  else failures.push(`${name}${detail ? ` → ${detail}` : ''}`);
}

/* ---- 1. kind 解析与推导（与 Java 侧 derivePicker 同规则） ---- */
expect('显式声明优先', P.pickerKindOf({ picker: 'custom' }) === 'custom');
expect('实体槽位推导为 remote', P.pickerKindOf({ entity: true }) === 'remote');
expect('有枚举值的推导为 literal', P.pickerKindOf({ entity: false, values: ['近30天'] }) === 'literal');
expect('什么都没有的推导为 manual', P.pickerKindOf({ entity: false }) === 'manual');
expect('大小写与空格容错', P.pickerKindOf({ picker: ' Remote ' }) === 'remote');
expect('非法声明退回推导', P.pickerKindOf({ picker: 'tree', entity: true }) === 'remote');

/* ---- 2. 降级矩阵：任何一环缺失都不能让"选实体"不可用 ---- */
const customMissing = P.resolvePicker({ type: 'product', picker: 'custom', customPicker: 'product-catalog' }, { pickers: {} });
expect('声明的 custom 没注册时退到手输', customMissing.kind === 'manual', customMissing.kind);
expect('退化原因要留给 UI 说', typeof customMissing.degraded === 'string' && customMissing.degraded.includes('product-catalog'));

const remoteNoProvider = P.resolvePicker({ type: 'customer', picker: 'remote', hasCandidates: false }, { request: async () => [] });
expect('宿主没有候选能力时退到手输', remoteNoProvider.kind === 'manual');

const remoteNoRequest = P.resolvePicker({ type: 'customer', picker: 'remote', hasCandidates: true }, {});
expect('宿主没注入请求器时退到手输', remoteNoRequest.kind === 'manual');

const literalEmpty = P.resolvePicker({ type: 'timeWindow', picker: 'literal', values: [] }, {});
expect('声明 literal 却没有值 → 手输', literalEmpty.kind === 'manual');

const customOk = P.resolvePicker({ type: 'product', picker: 'custom', customPicker: 'product-catalog' },
  { pickers: { 'product-catalog': { open: async () => null } } });
expect('注册了 custom 就用 custom', customOk.kind === 'custom');

/* ---- 3. 值规整：id 一律字符串 ---- */
expect('数字 id 转字符串', P.normalizeSlotValue(320).id === '320');
expect('字符串 id 去掉前后空格', P.normalizeSlotValue('  320  ').id === '320');
expect('对象值缺 name 时回退 id', P.normalizeSlotValue({ id: 12 }).name === '12');
expect('空值返回 null', P.normalizeSlotValue('') === null && P.normalizeSlotValue(null) === null && P.normalizeSlotValue({}) === null);
const withMeta = P.normalizeSlotValue({ id: 3, name: '宏图', meta: '负责人 张三', badges: [{ text: '高风险', level: 'danger' }] });
expect('meta 是自由文本', withMeta.meta === '负责人 张三');
expect('badges 结构化保留 level', withMeta.badges?.[0]?.level === 'danger');
expect('没有 badges 时不造字段', !('badges' in P.normalizeSlotValue({ id: 1, name: 'a' })));

/* ---- 4. 候选响应两种形状都要吃（新前端 + 老宿主） ---- */
const legacy = P.normalizePickPage([{ id: 1, name: 'A' }, { id: 2, name: 'B' }], { limit: 10 });
expect('老宿主返回数组 → 单页无更多', legacy.items.length === 2 && legacy.nextCursor === null && legacy.total === null);

const paged = P.normalizePickPage({ items: [{ id: 1, name: 'A' }], nextCursor: 'p2', total: 128 }, { limit: 1 });
expect('新宿主返回分页对象', paged.items.length === 1 && paged.nextCursor === 'p2' && paged.total === 128);

const noCursor = P.normalizePickPage({ items: [{ id: 1, name: 'A' }, { id: 2, name: 'B' }] }, { limit: 2 });
expect('宿主没给游标就是没有下一页（不靠"满页"去猜）', noCursor.nextCursor === null,
  '否则界面会永远挂着一个点了没新内容的"加载更多"');

const shortPage = P.normalizePickPage({ items: [{ id: 1, name: 'A' }], nextCursor: 'p3' }, { limit: 10 });
expect('宿主给了游标就照用（哪怕这页不满）', shortPage.nextCursor === 'p3',
  '服务端过滤后可能不足一页但仍有下一页，不能拿页长去推翻宿主的判断');

const emptyPage = P.normalizePickPage({ items: [] }, { limit: 10 });
expect('空页安全', emptyPage.items.length === 0 && emptyPage.nextCursor === null);

/* ---- 5. remote 选择器真的按契约发请求 ---- */
const calls = [];
const remote = P.remotePicker(async (query) => {
  calls.push(query);
  return { items: [{ id: query.keyword ? 9 : 1, name: 'hit' }], nextCursor: 'next' };
});
const page1 = await remote.search({ type: 'customer', keyword: '', limit: 10 });
expect('首次查询带 type/keyword/limit', calls[0].type === 'customer' && calls[0].limit === 10 && !('cursor' in calls[0]));
expect('首次查询不传游标', !('cursor' in calls[0]));
await remote.search({ type: 'customer', limit: 10 }, 'next');
expect('翻页时带上游标', calls[1].cursor === 'next');
expect('返回被规整成契约形状', page1.items[0].id === '1' && page1.nextCursor === 'next');

/* ---- 6. custom 适配器：统一契约 + 取消 ---- */
const customPick = P.customPicker('x', { open: async () => [{ id: 7, name: '商品' }] });
expect('custom 返回数组被规整', P.normalizePickResult(await customPick.open()).values[0].id === '7');
expect('custom 返回 {values} 也被规整', P.normalizePickResult({ values: [{ id: 8 }] }).values[0].id === '8');
expect('取消返回 null', P.normalizePickResult(null) === null && P.normalizePickResult([]) === null);
expect('返回空 values 视为取消', P.normalizePickResult({ values: [] }) === null);
let threw = false;
try { P.customPicker('y', {}); } catch { threw = true; }
expect('custom 适配器缺 open 时报错（这是宿主接入错误，必须立刻可见）', threw);

/* ---- 7. 选择器入参：上下文栈必须传进去 ---- */
const ctx = P.pickContextOf(
  { type: 'contract', key: 'contractId', title: '合同', multi: true },
  { slots: [{ type: 'customer', values: ['320'] }], selected: [396], limit: 10 });
expect('入参带当前上下文栈（让宿主能排"这个客户的合同"）', Array.isArray(ctx.slots) && ctx.slots[0].type === 'customer');
expect('已选项回显且被规整', ctx.selected[0].id === '396');
expect('多选标记透传', ctx.multi === true);
expect('limit 透传', ctx.limit === 10);

/* ---- 8. 提示文案不许承诺做不到的事 ---- */
expect('remote 没写 hint 时给的是"搜索"而非"编号"', P.pickerHintOf({ entity: true, title: '客户' }).includes('搜索'));
expect('manual 如实说"输入编号"', P.pickerHintOf({ entity: false, title: '旧编号' }).includes('编号'));
expect('宿主写了就用宿主的', P.pickerHintOf({ pickerHint: '按编码前缀搜' }) === '按编码前缀搜');

/* ---- 报告 ---- */
console.log(`选择器契约自检：${passed} 项通过，${failures.length} 项失败`);
if (failures.length) {
  console.log('');
  for (const line of failures) console.log(`  ✕ ${line}`);
  process.exit(1);
}
console.log('✓ 四类 kind 解析与降级 / id 字符串化 / 两种响应形状 / 游标诚实性 / custom 契约 全部成立');
