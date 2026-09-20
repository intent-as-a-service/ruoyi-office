/**
 * 意图工作台 · 中文文案表（zh-CN）
 * ---------------------------------------------------------------------------
 * 规格说明见 `docs/意图工作台-前端文案清单.md`；错误码来源见
 * `意图工作台技术方案.md` §6.3。
 *
 * 定位：**全部面向用户的工作台字符串都从这里取**。改文案只改这一个文件，
 * 后台 vben / 演示页 / jQuery 老页面共用同一份（与 intent-ui-sdk 的复用口径一致）。
 *
 * 三条铁律（与本文件里的每个字符串都相关）：
 *   1. 说发生了什么，不说感觉如何；
 *   2. 每个失败态必须带一个动作；
 *   3. 不承诺界面给不了的东西——尤其是**不许出现百分比进度与"马上就好"**。
 *
 * 落点：`yudao-module-intent/src/main/resources/intent-ui/js/copy.zh-CN.js`
 */

/* ---------------------------------------------------------------- 工具 */

/** 秒数 → 人话（14 → "14 秒"；92 → "1 分 32 秒"）。 */
export function formatSeconds(seconds) {
  const s = Math.max(0, Math.round(Number(seconds) || 0));
  if (s < 60) return `${s} 秒`;
  const m = Math.floor(s / 60);
  const rest = s % 60;
  return rest === 0 ? `${m} 分` : `${m} 分 ${rest} 秒`;
}

/** token 数 → 人话（8000 → "8k"；1200000 → "1.2M"）。 */
export function formatTokens(tokens) {
  const n = Math.max(0, Math.round(Number(tokens) || 0));
  if (n === 0) return '0 token';
  if (n < 1000) return `${n} token`;
  if (n < 1000000) return `${Math.round(n / 1000)}k token`;
  return `${(n / 1000000).toFixed(1)}M token`;
}

/**
 * 毫秒 → 人话：不足 1 秒用毫秒。
 *
 * <p>不能直接四舍五入到秒：skill 类意图实测 13~51ms，全都会被显示成"0 秒"，
 * 看起来像"根本没跑"——而这恰恰是这一类执行器最大的卖点（即时、不烧 token）。</p>
 */
export function formatDuration(durationMs) {
  const ms = Math.max(0, Math.round(Number(durationMs) || 0));
  return ms < 1000 ? `${ms} 毫秒` : formatSeconds(ms / 1000);
}

/* ------------------------------------------------------------ 执行器类型 */

export const EXECUTOR_LABEL = {
  skill: '技能',
  agent: '推理',
  flow: '流程',
  remote: '跨系统',
};

/**
 * 执行前的成本预估。**估算值必须带"约"**——写成精确值会让用户觉得系统在骗他。
 *
 * @param {string|null} executorType 目录项上的 executorType（未登记时为 null）
 * @param {{seconds?:number, tokens?:number}} [history] 同意图的历史中位数
 */
export function costBeforeCopy(executorType, history = {}) {
  const { seconds, tokens } = history;
  const hasHistory = typeof seconds === 'number' && seconds > 0;
  switch (executorType) {
    case 'skill':
      return '⚡ 即时 · 不消耗模型';
    case 'agent':
      return hasHistory
        ? `⏱ 约 ${formatSeconds(seconds)} · ≈${formatTokens(tokens ?? 0)}`
        : '⏱ 约 20 秒 · ≈8k token';
    case 'flow':
      return hasHistory ? `⏱ 约 ${formatSeconds(seconds)}` : '⏱ 多步流程';
    case 'remote':
      return '🌐 跨系统调用，耗时取决于对方系统';
    default:
      return '耗时未知';
  }
}

/** 执行后的实际成本。 */
export function costAfterCopy(executorType, { durationMs = 0, tokens = 0 } = {}) {
  const spent = formatDuration(durationMs);
  const label = EXECUTOR_LABEL[executorType];
  if (executorType === 'skill') return `⚡ ${spent} · 不消耗模型`;
  if (executorType === 'remote') return `🌐 ${spent}`;
  if (!label) return `耗时 ${spent} · 类型未登记`;
  return `⏱ ${spent} · ${formatTokens(tokens)} · ${label}`;
}

/* ---------------------------------------------------------------- 错误码 */

const ERROR_TABLE = {
  MISSING_PARAMS: {
    tone: 'info',
    title: (c) => `还差 ${c.count ?? '几'} 个参数`,
    body: () => '补全后会接着跑，不用重新开始。',
    primary: '补全并执行',
    secondary: '取消',
  },
  VALIDATION_ERROR: {
    tone: 'warning',
    title: () => '参数格式不对',
    body: (c) => (c.field ? `已定位到「${c.field}」，改完再跑一次。` : '改完再跑一次。'),
    primary: '去修改',
    secondary: '看原始报错',
  },
  FORBIDDEN: {
    tone: 'warning',
    title: () => '你没有这个意图的权限',
    body: (c) => {
      const need = c.requiredRoles?.length ? c.requiredRoles.join('、') : '更高角色';
      const mine = c.myRoles?.length ? c.myRoles.join('、') : '当前角色';
      return `该意图要求角色：${need}；你当前是：${mine}。`;
    },
    primary: '申请权限',
    secondary: '关闭',
  },
  INTENT_NOT_FOUND: {
    tone: 'warning',
    title: () => '这个意图已下架',
    body: () => '它已从目录移除，「我常做的」里也会同步去掉。',
    primary: '找相近的意图',
    secondary: '关闭',
  },
  EXECUTOR_NOT_FOUND: {
    tone: 'warning',
    title: () => '暂时不可用',
    body: (c) => (c.isAdmin
      ? `执行器「${c.executorId ?? '未知'}」未注册。已注册：${(c.registeredExecutors ?? []).join('、') || '无'}。`
      : '这个意图的执行器没注册上，已记录配置问题。'),
    primary: '关闭',
    secondary: '复制诊断信息',
  },
  REMOTE_UNAVAILABLE: {
    tone: 'warning',
    title: () => '需要跨系统网关',
    body: () => '该意图要调外部系统，当前环境未装配网关。',
    primary: '联系管理员',
    secondary: '关闭',
  },
  LLM_ERROR: {
    tone: 'danger',
    title: () => '模型调用失败',
    body: (c) => (c.phase === 'after-writeback'
      ? '已写回，但后续步骤失败。先去核对那条记录，再决定要不要重跑。'
      : c.isWriteback ? '这次没有写回任何数据，重跑不会重复。' : '这次没有产生结果。'),
    primary: (c) => (c.phase === 'after-writeback' ? '去看那条记录' : '重跑'),
    secondary: '看依据',
  },
  TOOL_ERROR: {
    tone: 'danger',
    title: () => '取数失败',
    body: (c) => (c.tool
      ? `调用 ${c.tool} 时失败${c.reason ? `：${c.reason}` : ''}。`
      : '取数时失败，已记录。'),
    primary: (c) => (c.phase === 'after-writeback' ? '去看那条记录' : '重跑'),
    secondary: '复制工具名',
  },
  OUTPUT_INVALID: {
    tone: 'danger',
    title: () => '结果格式不对，已自动重试',
    body: () => '重试后仍不符合输出规范，已留痕。',
    primary: '重跑',
    secondary: '看依据',
  },
  TIMEOUT: {
    tone: 'danger',
    title: () => '执行超时',
    body: (c) => `已用 ${formatSeconds(c.elapsedSeconds ?? 0)}，超过该意图的 ${formatSeconds(c.limitSeconds ?? 0)}上限。`,
    primary: '重跑',
    secondary: '看已完成的步骤',
  },
  MVP_NOT_IMPLEMENTED: {
    tone: 'info',
    title: () => '这个能力还在做',
    body: () => '已在计划里，暂不能使用。',
    primary: '关闭',
    secondary: null,
  },
};

/**
 * 错误码 → 可渲染文案。
 *
 * **绝不返回"未知错误"**：`code` 是排查的唯一线索，兜底也把它显示出来。
 *
 * @param {string} code IntentErrorCodes 里的取值
 * @param {object} [ctx] 插值上下文：field / requiredRoles / myRoles / tool / reason /
 *                       elapsedSeconds / limitSeconds / count / isWriteback /
 *                       phase('before-writeback'|'after-writeback') / isAdmin / executorId
 * @returns {{title:string, body:string, primary:string|null, secondary:string|null, tone:string}}
 */
export function errorCopy(code, ctx = {}) {
  const entry = ERROR_TABLE[code];
  if (!entry) {
    return {
      tone: 'danger',
      title: '没跑成',
      body: ctx.message || `错误码：${code || 'UNKNOWN'}`,
      primary: '重跑',
      secondary: '复制错误码',
    };
  }
  return {
    tone: entry.tone,
    title: entry.title(ctx),
    body: entry.body(ctx),
    primary: typeof entry.primary === 'function' ? entry.primary(ctx) : entry.primary,
    secondary: entry.secondary ?? null,
  };
}

/* -------------------------------------------------------------- Run 状态 */

/** 超过 30 秒后给的话——把等待从"占用注意力"变成"可以走开"。 */
export const LONG_RUNNING_HINT = '可以先去做别的，跑完在工作台会留记录。';

/**
 * Run 状态 → 徽标与主文案。
 *
 * 注意 `ABANDONED`：它是唯一一个"用户以为结束了、系统其实还在动"的状态，
 * 这条副文案是必须的，不是美化。
 *
 * @param {{status:string, elapsedSeconds?:number, durationMs?:number,
 *          tokens?:number, executorType?:string|null, isWriteback?:boolean}} run
 */
export function runStatusCopy(run = {}) {
  const elapsed = run.elapsedSeconds ?? 0;
  switch (run.status) {
    case 'QUEUED':
      return { badge: '排队中', title: '排队中', detail: '', actions: ['abandon'] };
    case 'RUNNING':
      return {
        badge: '执行中',
        title: elapsed <= 5 ? `执行中 · 已用 ${formatSeconds(elapsed)}` : `仍在计算 · 已用 ${formatSeconds(elapsed)}`,
        detail: elapsed > 30 ? LONG_RUNNING_HINT : '',
        // 写回类不给"放弃等待"：一个看起来能停、实际停不掉的按钮是主动误导
        actions: run.isWriteback ? [] : ['abandon'],
        writebackNote: run.isWriteback ? '写回类执行不支持中途停止。' : '',
      };
    case 'SUCCESS':
      return {
        badge: '完成',
        title: `完成 · ${formatDuration(run.durationMs ?? 0)}`,
        detail: costAfterCopy(run.executorType ?? null, run),
        actions: ['evidence'],
      };
    case 'NEED_INPUT':
      return {
        badge: '需要确认',
        title: '需要你确认一下',
        detail: '这不是缺参数，是业务上要你拍板。',
        actions: ['expand-form'],
      };
    case 'ABANDONED':
      return {
        badge: '已放弃等待',
        title: '已放弃等待',
        detail: '服务端可能仍在跑；它跑完后这里会变成「完成」。',
        actions: [],
      };
    case 'FAILED':
    default:
      return { badge: '没跑成', title: '没跑成', detail: '', actions: [] };
  }
}

/* ------------------------------------------------------------------ 空态 */

/**
 * 空态文案。
 *
 * **降级与"确实没有"必须视觉可分、文案可分**——旧实现里两者长得一模一样，
 * 于是空态在对用户说谎。
 *
 * @param {'today'|'todo'|'history'|'slot'} place
 * @param {{degraded?:boolean, failedRules?:number, totalRules?:number, slotName?:string}} [ctx]
 */
export function emptyCopy(place, ctx = {}) {
  const degraded = !!ctx.degraded;
  if (place === 'today') {
    return degraded
      ? { tone: 'warning', title: '优先级算不出来', body: '数据源没有返回结果，这不代表你没有待办。', actions: ['retry'] }
      : { tone: 'info', title: '今天没有需要优先处理的', body: '', actions: ['change-window', 'browse-all'] };
  }
  if (place === 'todo') {
    if (degraded) {
      const { failedRules = 0, totalRules = 0 } = ctx;
      return {
        tone: 'warning',
        title: '待办暂时取不到',
        body: totalRules ? `${totalRules} 条规则里有 ${failedRules} 条没返回。` : '数据源没有返回结果。',
        actions: ['retry'],
      };
    }
    return { tone: 'info', title: '没有待办', body: '今天可以安心做自己的事。', actions: [] };
  }
  if (place === 'history') {
    return degraded
      ? { tone: 'warning', title: '历史暂时打不开', body: '', actions: ['retry'] }
      : { tone: 'info', title: '还没有跑过意图', body: '', actions: ['pick-shortcut'] };
  }
  // 上下文栈的空槽：给动作，不给说明
  const name = ctx.slotName || '对象';
  return { tone: 'info', title: `还没有${name}`, body: '', actions: ['pick-slot', 'carry-from-page'] };
}

/* -------------------------------------------------------------- 上下文栈 */

export const SLOT_COPY = {
  /** 钉住 vs 带入冲突：文案里必须出现**两个值**，用户才知道自己在选什么。 */
  pinnedConflict: (pinnedValue, incomingValue) => ({
    title: `你钉住了「${pinnedValue}」，本页想带入「${incomingValue}」`,
    actions: ['keep-pinned', 'use-incoming'],
  }),
  invalid: (slotName) => ({
    tone: 'warning',
    title: `这个${slotName}已不存在或不在你的权限内`,
    actions: ['replace-slot', 'clear-slot'],
  }),
  /** 无法校验不要标红：宿主还没登记这个槽位类型，不是用户的问题。 */
  unverifiable: () => ({
    tone: 'muted',
    title: '无法校验',
    body: '不影响使用，只是不会帮你检查它还有没有效。',
    actions: [],
  }),
  setMode: (slotName, count) => ({
    title: `${slotName} = ${count} 个`,
    actions: ['expand-set', 'clear-slot'],
  }),
  acceptProposal: (entityName) => ({
    title: `结果里提到「${entityName}」，加进上下文？`,
    actions: ['accept-proposal', 'dismiss-proposal'],
  }),
  /** 执行后上下文已变：跑完 30 秒后改了上下文，结果必须还能对得上。 */
  staleContext: () => ({
    tone: 'info',
    title: '后来你改过上下文',
    actions: ['compare-context', 'rerun'],
  }),
  clearAll: (count) => ({
    title: `清掉这 ${count} 个上下文？`,
    actions: ['clear-all', 'cancel'],
  }),
};

/* ------------------------------------------------------- 意图可用性三态 */

/**
 * 意图按钮的可用性说明，对应 `slots.mjs#satisfiability` 的 `ready / locked / unknown`。
 *
 * `ready` **没有文案**——能跑就跑，多说一句都是噪音。
 *
 * 另外两个状态必须说**不同的话**，而且这是本文件里最容易写错的一处：
 *   · `locked` 知道缺什么 → 指名道姓说出缺哪个上下文，并给动作；
 *   · `unknown` 不知道缺什么（宿主还没登记这个槽位类型）→ **不许出现"缺 X"**。
 *     编一句"还差客户"是最自然的写法，也是最糟的写法：那是在陈述一个我们并不掌握的事实。
 *
 * 入参是**展示名**（`ContextField.title`），不是 key：`satisfiability()` 返回的是
 * `missing: ['customerId']`，UI 要先按 `entry.context[].title` 映射成「客户」再调这里。
 */
export const AVAILABILITY_COPY = {
  /** 缺必填上下文：说出缺哪个 + 给一个动作（补上下文，或直接点开手填）。 */
  locked: (missingTitles) => {
    const list = (Array.isArray(missingTitles) ? missingTitles : []).filter(Boolean);
    return {
      tone: 'muted',
      title: list.length ? `还差「${list.join('、')}」` : '还差一些上下文',
      body: '补上就能直接跑；也可以点开手动填。',
      actions: ['fill-slot', 'open-form'],
    };
  },
  /** 判不了：只说"确认参数"，绝不编造缺什么。 */
  unknown: () => ({
    tone: 'muted',
    title: '需要你确认参数',
    body: '这条意图要用的上下文类型还没登记，所以没法替你判断要填什么——点开看一下。',
    actions: ['open-form'],
  }),
};

/* ---------------------------------------------------------------- 批量 */

export const BATCH_COPY = {
  budgetGate: (count, estimatedTokens, budgetTokens) => ({
    title: `这轮要跑 ${count} 次`,
    body: `预估 ≈${formatTokens(estimatedTokens)}，超过单次预算 ${formatTokens(budgetTokens)}。`,
    actions: ['run-all', 'run-first-5'],
  }),
  progress: (done, total, tokens) => `已完成 ${done}/${total} · 已用 ${formatTokens(tokens)}`,
  stopped: (inFlight) => `已停止入队 · 在跑的 ${inFlight} 个会跑完并计入结果。`,
  allDone: (count, durationMs, tokens) =>
    `${count} 个全部跑完 · 用时 ${formatDuration(durationMs)} · ${formatTokens(tokens)}`,
  partial: (total, ok, bad) => `${total} 个里 ${ok} 个成功、${bad} 个没跑成`,
  agentWarning: (count, secondsPerRun, tokensPerRun) => ({
    title: `这轮包含 ${count} 个推理类意图`,
    body: `每个约 ${formatSeconds(secondsPerRun)}、≈${formatTokens(tokensPerRun)}，建议先跑 5 个。`,
    actions: ['run-first-5', 'run-all'],
  }),
  toTodo: (count) => `已把 ${count} 条加到「我该办的」`,
};

/* ------------------------------------------------------------ 全局禁忌 */

/**
 * 这些词一旦出现在界面上就是 bug——单测可以拿它扫一遍渲染结果。
 * 来源：本文件顶部的三条铁律 + 技术方案 §7.1（不许假进度）。
 */
export const FORBIDDEN_PHRASES = [
  '正在思考', '马上就好', '请稍等', '请稍后重试', '未知错误', '执行失败', '出错了', '置信度',
];
