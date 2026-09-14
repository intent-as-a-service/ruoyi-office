import { requestClient } from '#/api/request';

/**
 * 内嵌 AI 意图 SDK · API
 * 认证沿用宿主系统（requestClient 自动携带 token 与租户）。
 */
export namespace IntentApi {
  /** 上下文字段声明 */
  export interface ContextField {
    key: string;
    title: string;
    required: boolean;
  }

  /** 目录增强 · 动态提示徽标（宿主事实驱动，如「2 份合同即将到期」） */
  export interface IntentBadge {
    text: string;
    level: 'danger' | 'info' | 'warning';
    count?: number;
  }

  /** 目录增强 · 个性化意图建议（宿主事实驱动，点击即执行） */
  export interface IntentSuggestion {
    id: string;
    intentId: string;
    title: string;
    subtitle?: string;
    /** item = 单个对象，可直接执行；aggregate = 聚合待办，逐条办理 */
    kind: 'aggregate' | 'item';
    count?: number;
    params?: Record<string, any>;
    reason?: string;
    dedupKey?: string;
  }

  /** 目录项 */
  export interface CatalogEntry {
    id: string;
    name: string;
    description: string;
    scope: 'LOCAL' | 'REMOTE' | 'COMPOSITE';
    targetSystem?: string;
    cardType: string;
    paramsSchema: Record<string, any>;
    context: ContextField[];
    /** 口语别名：用户可能怎么说这件事（前端口语匹配框据此命中） */
    aliases?: string[];
    /** 宿主注入的提示徽标（可选） */
    badge?: IntentBadge;
  }

  /** 自动跟进规则 */
  export interface IntentRuleItem {
    id: number;
    ruleKey: string;
    title: string;
    /** 0=草稿 1=已启用 */
    status: number;
    yaml: string;
    sourceText?: string;
    createTime?: string;
  }

  /** 规则可用数据集（事实源自报：列名即约束，生成规则时才不会编字段） */
  export interface DatasetOption {
    id: string;
    description: string;
    fields: Record<string, string>;
  }

  export interface RuleDraft {
    title: string;
    yaml: string;
    ruleIds: string[];
    errors: string[];
  }

  export interface RulePreview {
    page: string;
    badges: Array<{ intentId: string; text: string; level: string; count?: number }>;
    groups: Array<{
      ruleId: string;
      intentId: string;
      dataset: string;
      count: number;
      items: Array<{
        title?: string;
        subtitle?: string;
        reason?: string;
        params?: Record<string, any>;
      }>;
    }>;
    itemCount: number;
  }

  export interface CatalogResponse {
    systemName: string;
    gatewayStatus: 'ENABLED' | 'UNAVAILABLE';
    entries: CatalogEntry[];
    /** 宿主注入的个性化建议（可选，条数受后端 intent.suggestions.max 限制） */
    suggestions?: IntentSuggestion[];
  }

  /** 执行步骤轨迹 */
  export interface StepTrace {
    kind: 'tool' | 'llm';
    name: string;
    argsSummary?: string;
    resultSummary?: string;
    ok: boolean;
    error?: string;
    durationMs: number;
  }

  /** 执行结果（标准输出信封） */
  export interface IntentResult {
    traceId: string;
    intentId: string;
    status: 'SUCCESS' | 'NEED_INPUT' | 'FAILED';
    output?: {
      title: string;
      summary: string;
      blocks: Array<{
        kind: 'text' | 'kv' | 'table' | 'list' | 'badges';
        title?: string;
        text?: string;
        items?: any[];
        columns?: string[];
        rows?: string[][];
        level?: 'info' | 'warning' | 'danger';
      }>;
      followups?: string[];
    };
    missingParams?: string[];
    error?: { code: string; message: string };
    steps: StepTrace[];
    usage?: { inputTokens: number; outputTokens: number; totalTokens: number };
    durationMs: number;
  }

  /** 配置项（管理端） */
  export interface ConfigItem {
    intentId: string;
    name: string;
    description: string;
    scope: string;
    version: number;
    /** 执行器引用（builtin-agent = 缺省内置推理循环） */
    executor: string;
    enabled: boolean;
    roles: string[];
    configured: boolean;
  }

  /** 执行器下拉选项 */
  export interface ExecutorOption {
    executorId: string;
    name: string;
    type: string;
  }

  /** 执行器档案（管理端列表项） */
  export interface ExecutorProfileItem {
    executorId: string;
    name?: string;
    type: 'agent' | 'skill' | string;
    description?: string;
    /** 模型 ID（纯工具技能为空） */
    model?: string;
    tools: string[];
    stepCount: number;
    maxTurns: number;
    outputMaxRetries: number;
    source: 'builtin' | 'custom' | string;
  }
}

// ==================== 用户端 ====================

export function getCatalog(params?: { page?: string }) {
  return requestClient.get<IntentApi.CatalogResponse>('/intent/catalog', { params });
}

export function executeIntent(data: {
  intentId: string;
  params: Record<string, any>;
  context: Record<string, any>;
}) {
  return requestClient.post<IntentApi.IntentResult>('/intent/execute', data);
}

export function getIntentHistory(params: { intentId?: string; limit?: number }) {
  return requestClient.get<any[]>('/intent/history', { params });
}

export function getIntentTrace(traceId: string) {
  return requestClient.get<any>(`/intent/trace/${traceId}`);
}

export function sendFeedback(data: {
  traceId: string;
  intentId: string;
  rating: 'UP' | 'DOWN';
  comment?: string;
}) {
  return requestClient.post<boolean>('/intent/feedback', data);
}

// ==================== 管理端 ====================

export function getIntentConfigList() {
  return requestClient.get<IntentApi.ConfigItem[]>('/intent/config/list');
}

export function updateIntentConfig(data: {
  intentId: string;
  enabled: boolean;
  roles: string[];
  remark?: string;
  /** 执行器引用变更（builtin-agent = 缺省） */
  executor?: string;
}) {
  return requestClient.put<boolean>('/intent/config/update', data);
}

// ==================== 意图规范维护（新增/修改/删除） ====================

export function getSpecYaml(intentId: string) {
  return requestClient.get<string>('/intent/config/spec/get-yaml', { params: { intentId } });
}

export function createIntentSpec(yaml: string) {
  return requestClient.post<string>('/intent/config/spec/create', { yaml });
}

export function updateIntentSpec(intentId: string, yaml: string) {
  return requestClient.put<boolean>('/intent/config/spec/update', { intentId, yaml });
}

export function deleteIntentSpec(intentId: string) {
  return requestClient.delete<boolean>('/intent/config/spec/delete', { params: { intentId } });
}

// ==================== 执行器档案管理（M2.2） ====================

export function getExecutorList() {
  return requestClient.get<IntentApi.ExecutorProfileItem[]>('/intent/executor/list');
}

export function getExecutorOptions() {
  return requestClient.get<IntentApi.ExecutorOption[]>('/intent/executor/options');
}

export function getExecutorYaml(executorId: string) {
  return requestClient.get<string>('/intent/executor/get-yaml', { params: { executorId } });
}

export function getExecutorJson(executorId: string) {
  return requestClient.get<string>('/intent/executor/get-json', { params: { executorId } });
}

export function getHostToolOptions() {
  return requestClient.get<Array<{ name: string; description: string }>>(
    '/intent/executor/tool-options',
  );
}

export function createExecutorProfile(yaml: string) {
  return requestClient.post<string>('/intent/executor/create', { yaml });
}

export function updateExecutorProfile(executorId: string, yaml: string) {
  return requestClient.put<boolean>('/intent/executor/update', { executorId, yaml });
}

export function deleteExecutorProfile(executorId: string) {
  return requestClient.delete<boolean>('/intent/executor/delete', { params: { executorId } });
}

// ==================== 自动跟进规则（自然语言 → 规则 → 试算 → 启用） ====================

export function getIntentRuleList() {
  return requestClient.get<IntentApi.IntentRuleItem[]>('/intent/rule/list');
}

export function getIntentRuleDatasetOptions() {
  return requestClient.get<IntentApi.DatasetOption[]>('/intent/rule/dataset-options');
}

export function draftIntentRule(naturalLanguage: string, page?: string) {
  return requestClient.post<IntentApi.RuleDraft>('/intent/rule/draft', {
    naturalLanguage,
    page,
  });
}

export function previewIntentRule(yaml: string, page?: string) {
  return requestClient.post<IntentApi.RulePreview>('/intent/rule/preview', { yaml, page });
}

export function enableIntentRule(payload: {
  sourceText?: string;
  title?: string;
  yaml: string;
}) {
  return requestClient.post<number[]>('/intent/rule/enable', payload);
}

export function updateIntentRuleStatus(id: number, status: number) {
  return requestClient.post<boolean>('/intent/rule/status', { id, status });
}

export function deleteIntentRule(id: number) {
  return requestClient.delete<boolean>('/intent/rule/delete', { params: { id } });
}
