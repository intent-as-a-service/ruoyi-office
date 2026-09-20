/**
 * 上下文槽位 · 前端可执行参考实现
 * ============================================================================
 * 这份文件不是"示意"，是**可以直接拷进宿主前端**的实现：`slots.mjs` 只依赖 ES2020
 * 运行环境（`TextEncoder`），不需要打包器、不依赖 `node:crypto`、不引入异步
 * （`crypto.subtle` 是异步的，而缓存键必须在渲染路径上同步算出）。
 *
 * 它把技术方案 §3.1（槽位类型从哪来）与 §3.2（目录 = f(角色, 槽位) 可不可算）
 * 两节文字变成可执行的东西，解决四件事：
 *
 *   1. `SLOT_TYPES`        —— 9 类槽位的**登记表骨架**（顺序即引导顺序，宿主可增删）
 *   2. `slotTypeOf(key)`   —— `ContextField.key` → 槽位类型（登记表 → 命名推导 → unknown）
 *   3. `satisfiability()`  —— `(entry.context × slots)` → 可发起 / 待补参 / 判不了
 *   4. `slotsHash(slots)`  —— 与 Java `IntentCatalogKeys#slotsHash` **逐字节一致**的缓存键
 *
 * 与 Java 的契约：`intent-sdk-core` 的 `IntentCatalogKeysTest` 里把三个哈希常量钉死了
 * （`55bb899b7b47ce53` / `71f3fbefc001208d` / `23ab111310764c86`），`slots.selftest.mjs`
 * 断言同一组常量。**任一端改了规范串，另一端的测试立刻红**——这是本项目里唯一
 * 跨越语言边界的契约，因为它决定了"前端算出来的键能不能命中后端缓存"。
 *
 * 三条红线（与设计方案 §5.1 一致，实现时不许绕过）：
 *   · 槽位类型必须登记过或可推导，**不接受模型生成的值**；
 *   · 槽位值只能来自宿主查询或用户显式选择，**不做"猜一个"**；
 *   · 这里算出来的只是**声明级**可满足性（参数齐、类型对），**不承诺后端一定能跑通**。
 */

/* ------------------------------------------------------------------ 常量 */

/** 单值 / 集合。与 Java `SlotView.MODE_SINGLE` / `MODE_SET` 同字面量。 */
export const MODE_SINGLE = 'SINGLE';
export const MODE_SET = 'SET';

/** 来源优先级 PINNED > CARRIED > INFERRED。与 Java `SlotView.SOURCE_*` 同字面量。 */
export const SOURCE_PINNED = 'PINNED';
export const SOURCE_CARRIED = 'CARRIED';
export const SOURCE_INFERRED = 'INFERRED';

/** 类型判不出来时的兜底：可显示、可手工填，但不做失效校验、不参与"解锁"计算。 */
export const TYPE_UNKNOWN = 'unknown';

/**
 * 槽位类型登记表（等价于宿主 `intent-slots.yaml` 的默认内容）。
 *
 * **顺序就是引导顺序**：客户在最上，因为它是多数意图的必填项；`unknown` 不在表里。
 * `vocabulary` 是命名推导用的词元（技术方案 §3.1 的词汇表），`kind` 区分实体与标量
 * ——标量槽（时间窗 / 口径）**不参与 `*Id` 推导**，只能靠登记表声明，所以它们的
 * `vocabulary` 是空的。
 *
 * 宿主可以在这里增删：加一类槽位只影响"哪些意图能被解锁"，不改任何协议。
 */
export const SLOT_TYPES = [
  { type: 'customer',    label: '客户',       kind: 'entity', defaultMode: MODE_SINGLE, vocabulary: ['customer'],           placeholder: '选客户' },
  { type: 'opportunity', label: '商机',       kind: 'entity', defaultMode: MODE_SINGLE, vocabulary: ['opportunity'],        placeholder: '选商机' },
  { type: 'contract',    label: '合同',       kind: 'entity', defaultMode: MODE_SINGLE, vocabulary: ['contract'],           placeholder: '选合同' },
  { type: 'payment',     label: '回款',       kind: 'entity', defaultMode: MODE_SINGLE, vocabulary: ['payment'],            placeholder: '选回款计划' },
  { type: 'contact',     label: '联系人',     kind: 'entity', defaultMode: MODE_SINGLE, vocabulary: ['contact'],            placeholder: '选联系人' },
  { type: 'product',     label: '产品',       kind: 'entity', defaultMode: MODE_SINGLE, vocabulary: ['product'],            placeholder: '选产品' },
  // 标量槽：key 是 timeWindow / scope 这样的名字，不带 Id 后缀，推导不出来，必须登记
  { type: 'timeWindow',  label: '时间窗',     kind: 'scalar', defaultMode: MODE_SINGLE, vocabulary: [],                     placeholder: '近 7 / 30 / 90 天' },
  { type: 'scope',       label: '口径',       kind: 'scalar', defaultMode: MODE_SINGLE, vocabulary: [],                     placeholder: '全部数据 / 仅我的 / 仅团队' },
  // 「负责人 · 组织」一个槽位装两种主键：doc 的词表里是 user / dept，落到同一类
  { type: 'owner',       label: '负责人·组织', kind: 'entity', defaultMode: MODE_SINGLE, vocabulary: ['user', 'dept', 'org', 'owner'], placeholder: '选负责人或部门' },
];

/**
 * POC（22 条意图）的槽位登记示例。
 *
 * 为什么必须有这一段：命名推导只认识 `customerId / contractId / contractIds` 这类
 * **词表内 + Id 后缀**的 key；POC 真实用的是 `businessId`（商机）、`clueId`（线索）、
 * `receivableId`（回款计划）——三个都不在词表里，推导必然失败。
 * 这正是技术方案 §3.1 的结论：**登记表是配置，推导只是省事的默认值。**
 */
export const POC_SLOT_REGISTRY = Object.freeze({
  customerId: 'customer',
  businessId: 'opportunity',
  contractId: 'contract',
  contractIds: 'contract',
  clueId: 'opportunity',
  contactId: 'contact',
  productId: 'product',
  receivableId: 'payment',
  ownerUserId: 'owner',
  deptId: 'owner',
  timeWindow: 'timeWindow',
  scope: 'scope',
});

/* -------------------------------------------------------------- 类型推导 */

const DECLARED_TYPES = new Set(SLOT_TYPES.map((entry) => entry.type));

/** 词元 → 槽位类型。多个词元映射到同一类型是正常的（user / dept → owner）。 */
const VOCABULARY = new Map();
for (const entry of SLOT_TYPES) {
  for (const word of entry.vocabulary) VOCABULARY.set(word, entry.type);
}

/**
 * `ContextField.key` → 槽位类型。三级，与 §3.1 完全一致：
 *
 *   1. **登记表命中** —— 值原样作为类型返回（允许宿主注册自定义类型，不校验是否是这 9 类）
 *   2. **命名推导**   —— 剥掉 `Ids` / `Id` 后缀，再按 camelCase 取**最后一个词元**查词表
 *   3. **unknown**    —— 判不出来就给 `unknown`，绝不猜
 *
 * 第 2 步为什么只取"最后一个词元"：`ownerUserId → user`、`contractIds → contract`
 * 都对得上，而 `businessId → business` 对不上——**这正是我们应该暴露的缺口**，
 * 猜成 `customer` 的后果是给错误的对象做失效校验，而这件事和数据权限沾边。
 *
 * @param {string} key     `ContextField.key`（如 `customerId`）
 * @param {Record<string,string>} [registry] 宿主登记表；不传则只用推导
 * @returns {string} 槽位类型，或 `unknown`
 */
export function slotTypeOf(key, registry = {}) {
  if (typeof key !== 'string') return TYPE_UNKNOWN;
  const trimmed = key.trim();
  if (!trimmed) return TYPE_UNKNOWN;

  const declared = registry[trimmed];
  if (typeof declared === 'string' && declared.trim()) return declared.trim();

  const base = trimmed.replace(/Ids$/, '').replace(/Id$/, '');
  if (base === trimmed) return TYPE_UNKNOWN; // 没有 Id 后缀：标量槽一律走登记表
  const words = base.split(/(?=[A-Z])/).filter(Boolean);
  const last = (words[words.length - 1] || '').toLowerCase();
  return VOCABULARY.get(last) ?? TYPE_UNKNOWN;
}

/** 类型是否已登记（`unknown` 与宿主自定义类型都不算"这 9 类里的"）。 */
export function isDeclaredSlotType(type) {
  return DECLARED_TYPES.has(type);
}

/* --------------------------------------------------------------- 可满足性 */

/** 与 Java `SlotView#hasValue()` 同语义：空数组 = 没值。 */
export function hasValue(slot) {
  return Array.isArray(slot?.values) && slot.values.length > 0;
}

/**
 * 声明级可满足性（技术方案 §3.2 的那条公式，落到代码）：
 *
 * ```
 * 可发起(entry, slots) ⟺ ∀ field ∈ entry.context : field.required ⇒ ∃ slot :
 *        slotTypeOf(field.key) == slot.type ∧ slot.hasValue()
 * ```
 *
 * 返回**三态**而不是布尔，因为 `unknown` 类型不允许被宣判：
 *
 * | state | 含义 | UI 表现（设计稿 §2.1） |
 * | --- | --- | --- |
 * | `ready` | 必填项都能从槽位取到 | 高亮可点 |
 * | `locked` | 有必填项缺，或槽位类型对不上 | 暗淡，但仍可点——点开走 Schema 驱动表单补参 |
 * | `unknown` | 必填项的类型判不出来（既没登记也推导不出） | 暗淡，**不显示"缺 X"**——我们并不知道缺什么 |
 *
 * 注意 `locked` **不是**"不能发起"：`ContextField.required` 的原语义是
 * "页面上取不到就进表单补全"，所以三个状态都允许点击，区别只在提示。
 *
 * @param {{context?: Array<{key: string, required?: boolean}>}} entry 目录项
 * @param {Array<{type: string, values?: string[]}>} slots 上下文栈
 * @param {Record<string,string>} [registry] 宿主登记表
 */
export function satisfiability(entry, slots = [], registry = {}) {
  const fields = Array.isArray(entry?.context) ? entry.context : [];
  const required = fields.filter((field) => field && field.required === true);
  const stack = Array.isArray(slots) ? slots : [];

  const missing = [];
  const undecidable = [];
  for (const field of required) {
    const needed = slotTypeOf(field.key, registry);
    if (needed === TYPE_UNKNOWN) {
      undecidable.push(field.key);
    } else if (!stack.some((slot) => slot?.type === needed && hasValue(slot))) {
      missing.push(field.key);
    }
  }

  const state = missing.length > 0 ? 'locked' : (undecidable.length > 0 ? 'unknown' : 'ready');
  return { state, missing, undecidable };
}

/** 布尔薄封装：只有 `ready` 才算"现在就能发起"。UI 需要区分三态时用 `satisfiability`。 */
export function satisfiable(entry, slots, registry) {
  return satisfiability(entry, slots, registry).state === 'ready';
}

/* ---------------------------------------------------------------- 缓存键 */

const HASH_LENGTH = 16;

/**
 * 槽位的**规范串**：真正的契约在这一步。
 *
 * ```
 * 每槽位: type + ':' + mode + ':' + (values 字典序排序后逗号拼接)
 * 所有槽位串字典序排序后 '|' 拼接；空槽位列表 = '-'
 * ```
 *
 * 只取语义字段（type / mode / values）：`label`、`source`、`invalid` 都**不参与**
 * ——把它们算进键，"改个显示名""标个失效"就会导致建议全量失效重拉。
 * 排序用 JS 默认的字典序（UTF-16 码元），与 Java `Collections.sort` 一致，
 * 所以 `['9','10']` 排成 `10,9`，两端都不会有歧义。
 */
export function canonicalSlots(slots) {
  if (!Array.isArray(slots) || slots.length === 0) return '-';
  const parts = slots.map((slot) => {
    const values = [...(slot?.values ?? [])].map((value) => String(value)).sort();
    return `${dash(slot?.type)}:${dash(slot?.mode)}:${values.join(',')}`;
  });
  return parts.sort().join('|');
}

const dash = (value) => (value == null || String(value).trim() === '' ? '-' : String(value));

/**
 * 槽位哈希：规范串的 SHA-256 取前 16 位十六进制小写；空槽位列表返回 `'-'`（不是哈希）。
 *
 * 与 Java `IntentCatalogKeys#slotsHash` 逐字节一致，两端各有测试钉死同一组常量。
 */
export function slotsHash(slots) {
  const canonical = canonicalSlots(slots);
  if (canonical === '-') return '-';
  return sha256Hex(canonical).slice(0, HASH_LENGTH);
}

/* ------------------------------------------------------- SHA-256（同步） */

/**
 * 为什么自带一份 SHA-256：
 *   · `node:crypto` 在浏览器里不可用；
 *   · `crypto.subtle.digest` 是异步的，而缓存键必须在渲染路径上同步算出。
 * 所以这里用一个约 40 行的同步实现。它只用于缓存键，不承担任何安全职责
 * ——但输出是**标准 SHA-256**，与 Java 的 `MessageDigest.getInstance("SHA-256")` 同值（有测试锁死）。
 */
const K = new Uint32Array([
  0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
  0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
  0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
  0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
  0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
  0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
  0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
  0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2,
]);

const rotr = (value, bits) => ((value >>> bits) | (value << (32 - bits))) >>> 0;

/** UTF-8 字节 → SHA-256 十六进制小写（与 Java `StandardCharsets.UTF_8` 对齐）。 */
export function sha256Hex(text) {
  const bytes = new TextEncoder().encode(String(text));
  const blockCount = Math.floor((bytes.length + 8) / 64) + 1;
  const padded = new Uint8Array(blockCount * 64);
  padded.set(bytes);
  padded[bytes.length] = 0x80;
  const view = new DataView(padded.buffer);
  const bits = bytes.length * 8;
  view.setUint32(padded.length - 8, Math.floor(bits / 0x100000000), false);
  view.setUint32(padded.length - 4, bits >>> 0, false);

  let h0 = 0x6a09e667, h1 = 0xbb67ae85, h2 = 0x3c6ef372, h3 = 0xa54ff53a;
  let h4 = 0x510e527f, h5 = 0x9b05688c, h6 = 0x1f83d9ab, h7 = 0x5be0cd19;
  const w = new Uint32Array(64);

  for (let offset = 0; offset < padded.length; offset += 64) {
    for (let t = 0; t < 16; t += 1) w[t] = view.getUint32(offset + t * 4, false);
    for (let t = 16; t < 64; t += 1) {
      const s0 = rotr(w[t - 15], 7) ^ rotr(w[t - 15], 18) ^ (w[t - 15] >>> 3);
      const s1 = rotr(w[t - 2], 17) ^ rotr(w[t - 2], 19) ^ (w[t - 2] >>> 10);
      w[t] = (w[t - 16] + s0 + w[t - 7] + s1) >>> 0;
    }

    let a = h0, b = h1, c = h2, d = h3, e = h4, f = h5, g = h6, h = h7;
    for (let t = 0; t < 64; t += 1) {
      const S1 = rotr(e, 6) ^ rotr(e, 11) ^ rotr(e, 25);
      const ch = (e & f) ^ (~e & g);
      const temp1 = (h + S1 + ch + K[t] + w[t]) >>> 0;
      const S0 = rotr(a, 2) ^ rotr(a, 13) ^ rotr(a, 22);
      const maj = (a & b) ^ (a & c) ^ (b & c);
      const temp2 = (S0 + maj) >>> 0;
      h = g; g = f; f = e; e = (d + temp1) >>> 0;
      d = c; c = b; b = a; a = (temp1 + temp2) >>> 0;
    }

    h0 = (h0 + a) >>> 0; h1 = (h1 + b) >>> 0; h2 = (h2 + c) >>> 0; h3 = (h3 + d) >>> 0;
    h4 = (h4 + e) >>> 0; h5 = (h5 + f) >>> 0; h6 = (h6 + g) >>> 0; h7 = (h7 + h) >>> 0;
  }

  return [h0, h1, h2, h3, h4, h5, h6, h7]
    .map((word) => word.toString(16).padStart(8, '0'))
    .join('');
}

/* ------------------------------------------------------------- 栈的操作 */

/**
 * 按来源优先级合并槽位（PINNED > CARRIED > INFERRED），**同类型只留一个**。
 *
 * 用来实现设计方案 §5.1 的两条规则：切视角时"清掉推断层、保留钉住层"、
 * 以及"不静默覆盖用户钉住的槽位"（冲突要返回给 UI 让用户选，不在这里替换）。
 */
export function mergeSlots(...stacks) {
  const priority = { [SOURCE_PINNED]: 3, [SOURCE_CARRIED]: 2, [SOURCE_INFERRED]: 1 };
  const byType = new Map();
  const conflicts = [];
  for (const stack of stacks) {
    for (const slot of stack ?? []) {
      if (!slot?.type) continue;
      const current = byType.get(slot.type);
      if (!current) {
        byType.set(slot.type, slot);
        continue;
      }
      const incoming = priority[slot.source] ?? 0;
      const existing = priority[current.source] ?? 0;
      if (incoming > existing) {
        byType.set(slot.type, slot);
      } else if (incoming === existing && String(current.values) !== String(slot.values)) {
        conflicts.push({ type: slot.type, kept: current, dropped: slot });
      }
    }
  }
  // 输出顺序回到登记表顺序：顺序本身就是引导（§2.1），不能让 Map 的插入序决定界面
  const order = new Map(SLOT_TYPES.map((entry, index) => [entry.type, index]));
  const slots = [...byType.values()].sort(
    (a, b) => (order.get(a.type) ?? Number.MAX_SAFE_INTEGER) - (order.get(b.type) ?? Number.MAX_SAFE_INTEGER),
  );
  return { slots, conflicts };
}

/** 执行后固化：把本次请求里命中槽位 key 的参数写回槽位（§2.1 规则 1，纯前端、零接口）。 */
export function fixSlotsAfterRun(slots, params, registry = {}) {
  if (!params || typeof params !== 'object') return slots;
  const next = new Map((slots ?? []).map((slot) => [slot.type, slot]));
  for (const [key, value] of Object.entries(params)) {
    const type = slotTypeOf(key, registry);
    if (type === TYPE_UNKNOWN || value == null || value === '') continue;
    const values = (Array.isArray(value) ? value : [value]).map((item) => String(item));
    if (values.length === 0) continue;
    const existing = next.get(type);
    if (existing?.source === SOURCE_PINNED) continue; // 钉住的不许被固化覆盖
    next.set(type, {
      ...(existing ?? {}),
      type,
      key,
      mode: values.length > 1 ? MODE_SET : MODE_SINGLE,
      values,
      source: existing?.source ?? SOURCE_CARRIED,
    });
  }
  return [...next.values()];
}
