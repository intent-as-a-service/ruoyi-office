/**
 * 意图工作台 · 实体选择器契约
 * ============================================================================
 * 框架无关、无 DOM、可单测。跑：`node src/test/js/picker.test.mjs`
 *
 * ## 它解决的是什么问题
 *
 * 「上下文里怎么挑一个实体」这件事，**每个系统都不一样**：
 *   - CRM 要按客户名称模糊搜，还要吃服务端数据权限；
 *   - ERP 要按物料编码前缀搜，还要按仓库过滤；
 *   - 有的系统早就有了自己的实体选择弹窗（组织树 / 商品选择器），不想再写一个；
 *   - 有的系统什么后端能力都没有，只能手输编号。
 *
 * 如果工作台把选择器**写死成一种**，换一个宿主就得改前端源码——这就是"集成难"的直接来源。
 * 所以这里把它做成一张**按槽位类型声明的四选一**（声明位在宿主的 `intent-slots.yaml`）：
 *
 * | kind      | 谁提供           | 数据来源                          | 适用 |
 * | --------- | ---------------- | --------------------------------- | ---- |
 * | `remote`  | SDK 内置         | 宿主实现的候选 SPI（统一端点）      | 实体量大、要服务端数据权限 |
 * | `literal` | SDK 内置         | 登记表里的 `values`               | 时间窗 / 口径这类枚举 |
 * | `custom`  | **宿主注册**     | 宿主自己的组件                    | 宿主已有成熟实体选择组件 |
 * | `manual`  | SDK 内置         | 用户手输编号                      | 降级路径（没有任何后端能力） |
 *
 * ## 统一契约（这是本文件的核心）
 *
 * 不管哪一种，**打开时的入参和返回的形状是同一套**，所以上下文栈不关心值从哪来：
 *
 * ```js
 * open({
 *   type, key, title, multi, selected, keyword, slots, limit
 * })  ->  { values: [{ id, name, meta?, badges? }] } | null    // null = 用户取消
 * ```
 *
 * 其中 `slots`（当前上下文栈）是要特别说明的一条：它让选择器能做
 * **"选合同时默认排这个客户的合同"**——上下文驱动不能只驱动"哪些意图能用"，
 * 还要驱动"选实体时先看到谁"，否则用户每次都要在多选器里重讲一遍"我是给谁选"。
 */

export const PICKER_REMOTE = 'remote';
export const PICKER_LITERAL = 'literal';
export const PICKER_CUSTOM = 'custom';
export const PICKER_MANUAL = 'manual';
export const PICKER_KINDS = [PICKER_REMOTE, PICKER_LITERAL, PICKER_CUSTOM, PICKER_MANUAL];

/** 默认单页候选条数。契约要求"关键字为空时给本人最可能用到的几条，不要返回全表"。 */
export const DEFAULT_PICK_LIMIT = 10;

/**
 * 槽位登记项 → 选择器种类。
 *
 * 优先读宿主在 YAML 里声明的 `picker`；没声明就推导（与 Java 侧
 * `IntentSlotType.derivePicker` 同一套规则，两端必须一致）：
 *   是实体 → remote；有枚举值 → literal；否则 manual。
 *
 * 为什么要有推导而不是要求必填：老登记表（8 类槽位）一行都不用改就能跑，
 * 新宿主想换选择方式时再显式声明。契约要能"渐进增强"，不能"不写就报错"。
 */
export function pickerKindOf(meta) {
  const declared = typeof meta?.picker === 'string' ? meta.picker.trim().toLowerCase() : '';
  if (PICKER_KINDS.includes(declared)) return declared;
  if (meta?.entity) return PICKER_REMOTE;
  return (meta?.values ?? []).length ? PICKER_LITERAL : PICKER_MANUAL;
}

/** 选择器里给用户看的一句说明。宿主没写就按种类给一句实话，绝不承诺做不到的事。 */
export function pickerHintOf(meta) {
  if (meta?.pickerHint) return meta.pickerHint;
  switch (pickerKindOf(meta)) {
    case PICKER_REMOTE: return `按关键字搜索${meta?.title ?? ''}；关键字为空时给你常用的几条`;
    case PICKER_LITERAL: return '从下面几个值里挑一个';
    case PICKER_CUSTOM: return `打开宿主提供的${meta?.title ?? ''}选择器`;
    default: return `直接输入${meta?.title ?? ''}编号后回车`;
  }
}

/**
 * 规整一个槽位值。
 * `id` 一律是字符串：跨系统主键类型完全不同（自增 / 雪花 / UUID / 复合），
 * 槽位值本来就统一存字符串，转换责任在宿主——这是"同一个前端接不同系统"的前提。
 */
export function normalizeSlotValue(raw) {
  if (raw == null) return null;
  if (typeof raw === 'string' || typeof raw === 'number') {
    const id = String(raw).trim();
    return id ? { id, name: id } : null;
  }
  const id = raw.id == null ? '' : String(raw.id).trim();
  if (!id) return null;
  const name = raw.name == null || String(raw.name).trim() === '' ? id : String(raw.name);
  const value = { id, name };
  // meta 是自由文本（"负责人 张三 · 39.9 万 · 最近联系 3 天前"）：SDK 不规定字段，宿主自己拼
  if (raw.meta) value.meta = String(raw.meta);
  // badges 是可选的结构化出口，只为了让风险色能显示；沿用 IntentBadge.level 的同一套语义
  if (Array.isArray(raw.badges) && raw.badges.length) {
    value.badges = raw.badges
      .filter((badge) => badge && badge.text)
      .map((badge) => ({ text: String(badge.text), level: badge.level || 'info' }));
  }
  return value;
}

/**
 * 规整一页候选。
 *
 * **必须同时吃两种形状**，这是升级窗口里唯一安全的做法：
 *   老宿主：`[{id, name, meta}]`                     → 单页、无更多
 *   新宿主：`{ items: [...], nextCursor, total }`    → 可翻页
 * 新前端 + 老宿主必须能用（否则一次前端升级就会把老宿主打挂）。
 */
export function normalizePickPage(raw, { limit = DEFAULT_PICK_LIMIT } = {}) {
  if (Array.isArray(raw)) {
    return { items: raw.map(normalizeSlotValue).filter(Boolean), nextCursor: null, total: null };
  }
  const items = Array.isArray(raw?.items) ? raw.items.map(normalizeSlotValue).filter(Boolean) : [];
  const nextCursor = raw?.nextCursor == null || raw.nextCursor === '' ? null : String(raw.nextCursor);
  const total = Number.isFinite(raw?.total) ? Number(raw.total) : null;
  // 不拿"这一页是不是满的"去猜还有没有下一页：
  // 宿主完全可能返回一页不足 limit 的结果但仍然有下一页（服务端过滤过）。
  // 游标只认宿主给的——没给就是没有；给了下一页空，界面会诚实地说"没有更多"。
  return { items, nextCursor, total };
}

/** 规整自定义选择器的返回：`null` = 用户取消；数组 = 直接当选中值。 */
export function normalizePickResult(raw) {
  if (raw == null) return null;
  const list = Array.isArray(raw) ? raw : (Array.isArray(raw.values) ? raw.values : []);
  const values = list.map(normalizeSlotValue).filter(Boolean);
  return values.length ? { values } : null;
}

/**
 * `remote` 选择器：走 SDK 统一端点。
 * 宿主实现候选 SPI（服务端数据权限随之生效），SDK 只负责按契约发请求与规整返回。
 *
 * @param request 形如 `(query) => Promise<unknown>`，由宿主注入（这样本模块不依赖 fetch，
 *                在 Node 里也能单测；也方便宿主换成自己的 HTTP 客户端）
 */
export function remotePicker(request) {
  return {
    kind: PICKER_REMOTE,
    async search(ctx, cursor) {
      const query = {
        type: ctx.type,
        keyword: ctx.keyword ?? '',
        limit: ctx.limit ?? DEFAULT_PICK_LIMIT,
      };
      if (cursor) query.cursor = cursor;
      const raw = await request(query);
      return normalizePickPage(raw, { limit: query.limit });
    },
  };
}

/** `literal` 选择器：值就是登记表里的枚举，不分页、不搜索。 */
export function literalPicker(meta) {
  const values = (meta?.values ?? []).map(normalizeSlotValue).filter(Boolean);
  return {
    kind: PICKER_LITERAL,
    values() { return values; },
  };
}

/**
 * `custom` 选择器：宿主注册一个具名适配器，SDK 只负责"在对的时机打开它、接住它的返回"。
 *
 * 为什么必须有这一类：多数成熟宿主已经有自己的实体选择组件。如果契约只允许 `remote`，
 * 宿主为了接工作台就得 **实现 Java SPI + 再写一个前端弹层**，集成成本直接翻倍，
 * 而且做出来的东西一定不如它自己已有的那个。
 *
 * 适配器只要求两件事：`open(ctx)` 返回 `PickResult`（用户取消就返回 null）。
 */
export function customPicker(name, adapter) {
  if (!adapter || typeof adapter.open !== 'function') {
    throw new Error(`自定义选择器「${name}」必须实现 open(ctx)`);
  }
  return { kind: PICKER_CUSTOM, name, open: adapter.open };
}

/** `manual` 选择器：没有候选能力时的降级路径——手输编号，不假装能搜。 */
export function manualPicker() {
  return { kind: PICKER_MANUAL };
}

/**
 * 按槽位登记项解析出真正要用的选择器。
 *
 * 降级矩阵（任何一环缺失都不能让"选实体"这件事不可用）：
 *   - 声明 `custom` 但宿主没注册那个名字 → 退 `manual`，并把原因挂在 `degraded` 上给 UI 说；
 *   - 声明 `remote` 但宿主没有候选能力（`hasCandidates === false`）→ 退 `manual`；
 *   - 声明 `remote` 且宿主没注入 `request` → 退 `manual`。
 * 这里绝不抛异常：一个槽位类型没配好，不该让整个工作台起不来。
 */
export function resolvePicker(meta, { pickers = null, request = null, limit = DEFAULT_PICK_LIMIT } = {}) {
  const kind = pickerKindOf(meta);
  const fallback = (reason) => ({ ...manualPicker(), declared: kind, degraded: reason });

  if (kind === PICKER_CUSTOM) {
    const adapter = pickers?.[meta?.customPicker || meta?.type];
    if (!adapter) return fallback(`宿主没有注册选择器「${meta?.customPicker || meta?.type}」`);
    return { ...customPicker(meta?.customPicker || meta?.type, adapter), declared: kind, degraded: null };
  }
  if (kind === PICKER_REMOTE) {
    if (meta?.hasCandidates === false) return fallback('这个槽位类型宿主没实现候选查询');
    if (typeof request !== 'function') return fallback('宿主没有注入候选请求器');
    return { ...remotePicker(request), declared: kind, degraded: null, limit };
  }
  if (kind === PICKER_LITERAL) {
    const picker = literalPicker(meta);
    if (!picker.values().length) return fallback('宿主没有为这个槽位声明可选值');
    return { ...picker, declared: kind, degraded: null };
  }
  return { ...manualPicker(), declared: kind, degraded: null };
}

/**
 * 组装打开选择器时的入参。
 * `selected` 传进去是为了**回显与去重**：宿主组件要能显示"已经选了哪几个"。
 */
export function pickContextOf(meta, { slots = [], selected = [], keyword = '', limit = DEFAULT_PICK_LIMIT } = {}) {
  return {
    type: meta?.type,
    key: meta?.key,
    title: meta?.title ?? meta?.type,
    multi: !!meta?.multi,
    selected: selected.map(normalizeSlotValue).filter(Boolean),
    keyword,
    // ★ 当前上下文栈：让宿主选择器能按当前对象做默认排序（"这个客户的合同"）
    slots,
    limit,
  };
}
