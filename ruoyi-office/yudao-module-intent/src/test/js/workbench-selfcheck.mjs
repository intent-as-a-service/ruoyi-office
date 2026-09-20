/**
 * 意图工作台 · 静态自检（零构建页面没有编译期，所以这些必须被机器盯住）
 * ---------------------------------------------------------------------------
 * 跑：node src/test/js/workbench-selfcheck.mjs
 *
 * 查五件事：
 *   1. 每个 `data-act="X"` 都有对应的 `case 'X'`——否则点了没反应，且只在运行时才发现；
 *   2. 每个 `case 'X'` 至少被用到一次——死分支是上一版设计的残留；
 *   3. 从 slots.mjs / copy.zh-CN.js 导入的名字都真实存在（拼错导入名是运行时 TypeError）；
 *   4. 文案里不出现 `copy.zh-CN.js` 自己声明的 FORBIDDEN_PHRASES 及其它禁用承诺；
 *   5. 页面里不再有浮球形态的痕迹（这一版明确撤掉了浮球）；
 *   6. `preview.html` 的假数据与生产信封同形状（字段名写错会让预览页"看起来像 bug"）。
 */
import { readFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const here = dirname(fileURLToPath(import.meta.url));
const base = resolve(here, '../../main/resources/intent-ui');
const read = (p) => readFileSync(resolve(base, p), 'utf8');

/**
 * 去掉注释再扫。
 *
 * 必须去：注释里出现的 `data-act="x"`、`import { … } from './x.js'`、以及
 * **"不许出现『悬浮球』"这种说明文字**，都会被下面的正则当成真代码——
 * 自检第一次跑就栽在这上面（mountWorkbench 的用法示例被当成了一个真 import）。
 * 反过来，注释里写的东西本来也不会执行，扫它没有意义。
 */
function stripComments(text) {
  return text
    .replace(/\/\*[\s\S]*?\*\//g, ' ')        // 块注释
    .replace(/<!--[\s\S]*?-->/g, ' ')          // HTML 注释
    .replace(/(^|[^:])\/\/[^\n]*/g, '$1');     // 行注释（避开 https:// 这类）
}

const js = stripComments(read('js/workbench.js'));
const html = stripComments(read('index.html'));
const failures = [];
const warnings = [];

/* ---- 1 & 2：data-act 与 case 双向对账（只认 onAct 里的 case） ---- */

const onActStart = js.indexOf('async function onAct(');
const onActEnd = js.indexOf('\n}', onActStart);
if (onActStart < 0 || onActEnd < 0) failures.push('没找到 onAct 函数，自检脚本需要跟着改');
const onAct = js.slice(onActStart, onActEnd);

const declared = new Set();
for (const source of [js, html]) {
  for (const match of source.matchAll(/data-act="([a-z-]+)"/g)) declared.add(match[1]);
}
const handled = new Set();
for (const match of onAct.matchAll(/case '([a-z-]+)':/g)) handled.add(match[1]);

for (const act of declared) {
  if (!handled.has(act)) failures.push(`data-act="${act}" 没有对应的 case，点了不会有反应`);
}
for (const act of handled) {
  if (!declared.has(act)) warnings.push(`case '${act}' 没有任何 data-act 用到（死分支）`);
}

/* ---- 3：导入名必须真实存在 ---- */

function exportsOf(path) {
  const text = stripComments(readFileSync(resolve(base, path), 'utf8'));
  const names = new Set();
  for (const match of text.matchAll(/export\s+(?:const|function|class|let)\s+([A-Za-z0-9_$]+)/g)) names.add(match[1]);
  for (const match of text.matchAll(/export\s*\{([^}]+)\}/g)) {
    for (const part of match[1].split(',')) {
      const name = part.trim().split(/\s+as\s+/).pop().trim();
      if (name) names.add(name);
    }
  }
  return names;
}

const importBlocks = [...js.matchAll(/import\s*\{([^}]+)\}\s*from\s*'([^']+)'/g)];
if (!importBlocks.length) failures.push('没有解析到任何 import，检查脚本本身是否过期');
for (const [, names, from] of importBlocks) {
  const available = exportsOf(from.replace(/^\.\//, 'js/'));
  for (const part of names.split(',')) {
    const name = part.trim();
    if (!name) continue;
    if (!available.has(name)) failures.push(`从 ${from} 导入了不存在的 ${name}`);
  }
}

/* ---- 4：文案禁用词 ---- */

const { FORBIDDEN_PHRASES } = await import(
  new URL('../../main/resources/intent-ui/js/copy.zh-CN.js', import.meta.url).href);
// 页面自己写的部分（不用 copy.zh-CN.js：那份文件正是禁用词表的出处）
const pageText = [js, html];
const EXTRA_FORBIDDEN = ['预计剩余', '已完成 100%', '请稍候，马上就好', '正在生成中'];
for (const phrase of [...FORBIDDEN_PHRASES, ...EXTRA_FORBIDDEN]) {
  if (pageText.some((source) => source.includes(phrase))) {
    failures.push(`页面文案里出现禁用词「${phrase}」——它要么是不编造，要么是空话，两者都不该出现在执行流里`);
  }
}

/* ---- 5：撤浮球的痕迹 ---- */

for (const phrase of ['floating', 'float-ball', '悬浮球', 'intent-ui-sdk.js']) {
  if (js.includes(phrase) || html.includes(phrase)) {
    failures.push(`页面里还引用着浮球形态的痕迹：「${phrase}」`);
  }
}

/* ---- 6：预览页假数据与生产信封同形状 ---- */

/**
 * 为什么这条要在静态自检里盯：
 * `nextIntents` 曾经因为**字段名大小写写错**（`nextintents`）在前后端两处静默失效，
 * 上线至今没渲染出来过。预览页的 fixture 是给人做视觉验收用的，
 * 它一旦和生产形状不一致（比如意图名写成 `name`，而生产信封是 `title`），
 * 渲染出来就是裸 `intentId`——看着像实现有 bug，其实只是假数据写错了字段。
 * 机器盯住，省掉一次"到底是哪边错了"的排查。
 */
const preview = stripComments(read('preview.html'));
for (const list of preview.matchAll(/nextIntents\s*:\s*\[([\s\S]*?)\]/g)) {
  // 条目里带嵌套对象（`params: { customerId: '326' }`），所以不能用 `{...}` 去框条目——
  // 那样只会框住内层 params、反而漏掉外层。改成看字段顺序：
  // 每个 `intentId` 到下一个 `intentId`（或数组末尾）之间，必须出现过 `title`。
  const marks = [...list[1].matchAll(/\b(intentId|title)\s*:/g)].map((match) => match[1]);
  for (let i = 0; i < marks.length; i += 1) {
    if (marks[i] !== 'intentId') continue;
    const rest = marks.slice(i + 1);
    const nextId = rest.indexOf('intentId');
    const within = nextId < 0 ? rest : rest.slice(0, nextId);
    if (!within.includes('title')) {
      failures.push('preview.html 的 nextIntents 里有条目缺 title'
        + '——生产信封的意图名是 title，写 name 会让预览渲染成裸 intentId');
    }
  }
}

/* ---- 报告 ---- */

console.log(`data-act 声明 ${declared.size} 个，onAct 处理 ${handled.size} 个，禁用词表 ${FORBIDDEN_PHRASES.length} 条`);
if (warnings.length) {
  console.log('\n警告：');
  for (const line of warnings) console.log(`  ! ${line}`);
}
if (failures.length) {
  console.log('\n失败：');
  for (const line of failures) console.log(`  ✕ ${line}`);
  process.exit(1);
}
console.log('\n✓ 自检通过：动作分发闭环、导入名有效、文案无禁用词、无浮球残留');