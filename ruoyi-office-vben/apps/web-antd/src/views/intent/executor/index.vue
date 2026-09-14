<script lang="ts" setup>
import type { IntentApi } from '#/api/intent';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  Button,
  Card,
  Input,
  message,
  Modal,
  Select,
  Table,
  Tag,
  Textarea,
  InputNumber,
} from 'ant-design-vue';

import {
  createExecutorProfile,
  deleteExecutorProfile,
  getExecutorJson,
  getExecutorList,
  getExecutorYaml,
  getHostToolOptions,
  updateExecutorProfile,
} from '#/api/intent';

defineOptions({ name: 'IntentExecutor' });

// ==================== 列表 ====================

const loading = ref(false);
const items = ref<IntentApi.ExecutorProfileItem[]>([]);
const toolOptions = ref<Array<{ name: string; description: string }>>([]);

const columns = [
  { title: '执行器', dataIndex: 'name', key: 'name', width: 220 },
  { title: '类型', dataIndex: 'type', key: 'type', width: 110 },
  { title: '模型', dataIndex: 'model', key: 'model', width: 160 },
  { title: '能力附件', dataIndex: 'tools', key: 'tools' },
  { title: '操作', dataIndex: 'action', key: 'action', width: 160 },
];

async function load() {
  loading.value = true;
  try {
    const [list, tools] = await Promise.all([getExecutorList(), getHostToolOptions()]);
    items.value = list;
    toolOptions.value = tools || [];
  } finally {
    loading.value = false;
  }
}

onMounted(load);

// ==================== 编辑器（表单编排 / YAML 高级） ====================

const editorOpen = ref(false);
const editorMode = ref<'form' | 'yaml'>('form'); // 表单编排 or YAML 高级
const isCreate = ref(true);
const originalExecutorId = ref<string>();
const editorSaving = ref(false);
const editorError = ref('');
const yamlText = ref('');

interface FlowNodeItem {
  id: string;
  type: 'tool' | 'agent' | 'knowledge';
  tool?: string;
  args: Array<{ key: string; value: string }>;
  prompt?: string;
  kb?: string;
  query?: string;
  topK?: number;
  when?: string;
  stopWhen?: string;
}

const form = reactive({
  id: '',
  type: 'agent' as 'agent' | 'skill',
  name: '',
  description: '',
  useModel: true,
  model: { provider: 'deepseek', baseUrl: 'https://api.deepseek.com', modelId: '', apiKey: '' },
  tools: [] as string[],
  flow: [] as FlowNodeItem[],
  outputTitle: '',
  outputSummary: '',
  outputBlocks: '',
});

const MODEL_PRESETS: Record<string, { baseUrl: string }> = {
  deepseek: { baseUrl: 'https://api.deepseek.com' },
  openai: { baseUrl: 'https://api.openai.com/v1' },
  anthropic: { baseUrl: 'https://api.anthropic.com' },
  openai兼容: { baseUrl: '' },
};

function addNode(type: FlowNodeItem['type']) {
  const index = form.flow.length + 1;
  form.flow.push({
    id: `n${index}`,
    type,
    tool: type === 'tool' ? undefined : undefined,
    args: type === 'tool' ? [{ key: '', value: '' }] : [],
    prompt: type === 'agent' ? '' : undefined,
    kb: type === 'knowledge' ? '' : undefined,
    query: type === 'knowledge' ? '' : undefined,
    topK: type === 'knowledge' ? 5 : undefined,
    when: '',
    stopWhen: '',
  });
}

function removeNode(index: number) {
  form.flow.splice(index, 1);
}

function moveNode(index: number, offset: -1 | 1) {
  const target = index + offset;
  if (target < 0 || target >= form.flow.length) return;
  const [node] = form.flow.splice(index, 1);
  form.flow.splice(target, 0, node!);
}

const hasAgentNode = computed(() => form.flow.some((n) => n.type === 'agent'));
const modelRequired = computed(() => form.flow.length === 0 || hasAgentNode.value);

/** 表单 → 档案对象 → JSON 文本（JSON 是合法 YAML，服务端直接解析） */
function buildProfileText(): string {
  const obj: Record<string, any> = {
    id: form.id.trim(),
    type: form.type,
    name: form.name.trim(),
  };
  if (form.description.trim()) obj.description = form.description.trim();
  if (form.useModel && form.model.modelId.trim()) {
    obj.model = {
      baseUrl: form.model.baseUrl,
      provider: form.model.provider,
      modelId: form.model.modelId.trim(),
      ...(form.model.apiKey.trim() ? { apiKey: form.model.apiKey.trim() } : {}),
    };
  }
  if (form.tools.length > 0) obj.tools = [...form.tools];
  if (form.flow.length > 0) {
    obj.flow = form.flow.map((n) => {
      const node: Record<string, any> = { id: n.id, type: n.type };
      if (n.type === 'tool' && n.tool) node.tool = n.tool;
      if (n.type === 'tool' && n.args.some((a) => a.key.trim())) {
        node.args = Object.fromEntries(
          n.args.filter((a) => a.key.trim()).map((a) => [a.key.trim(), a.value]),
        );
      }
      if (n.type === 'agent' && n.prompt) node.prompt = n.prompt;
      if (n.type === 'knowledge' && n.kb) {
        node.kb = n.kb;
        if (n.query) node.query = n.query;
        if (n.topK && n.topK !== 5) node.topK = n.topK;
      }
      if (n.when?.trim()) node.when = n.when.trim();
      if (n.stopWhen?.trim()) node.stopWhen = n.stopWhen.trim();
      return node;
    });
  }
  const output: Record<string, string> = {};
  if (form.outputTitle.trim()) output.title = form.outputTitle.trim();
  if (form.outputSummary.trim()) output.summary = form.outputSummary.trim();
  if (form.outputBlocks.trim()) output.blocks = form.outputBlocks.trim();
  if (Object.keys(output).length > 0) obj.output = output;
  return JSON.stringify(obj, null, 2);
}

/** 档案 JSON → 表单（含 steps 的旧版 skill 档案不支持表单编排，走 YAML） */
function hydrateFromJson(json: string): boolean {
  try {
    const p = JSON.parse(json);
    if ((p.steps?.length ?? 0) > 0) return false;
    form.id = p.id ?? '';
    form.type = p.type === 'skill' ? 'skill' : 'agent';
    form.name = p.name ?? '';
    form.description = p.description ?? '';
    form.useModel = !!p.model;
    form.model = {
      provider: p.model?.provider ?? 'deepseek',
      baseUrl: p.model?.baseUrl ?? 'https://api.deepseek.com',
      modelId: p.model?.modelId ?? '',
      apiKey: p.model?.apiKey ?? '',
    };
    form.tools = p.tools ?? [];
    form.flow = (p.flow ?? []).map((n: any, i: number) => ({
      id: n.id ?? `n${i + 1}`,
      type: n.type ?? 'tool',
      tool: n.tool,
      args: Object.entries(n.args ?? {}).map(([key, value]) => ({ key, value: String(value) })),
      prompt: n.prompt,
      kb: n.kb,
      query: n.query,
      topK: n.topK ?? (n.type === 'knowledge' ? 5 : undefined),
      when: n.when ?? '',
      stopWhen: n.stopWhen ?? '',
    }));
    form.outputTitle = p.output?.title ?? '';
    form.outputSummary = p.output?.summary ?? '';
    form.outputBlocks = p.output?.blocks ?? '';
    return true;
  } catch {
    return false;
  }
}

function resetForm() {
  Object.assign(form, {
    id: '',
    type: 'agent',
    name: '',
    description: '',
    useModel: true,
    model: { provider: 'deepseek', baseUrl: 'https://api.deepseek.com', modelId: '', apiKey: '' },
    tools: [],
    flow: [],
    outputTitle: '',
    outputSummary: '',
    outputBlocks: '',
  });
}

function openCreate() {
  isCreate.value = true;
  editorMode.value = 'form';
  resetForm();
  addNode('tool');
  editorError.value = '';
  editorOpen.value = true;
}

async function openUpdate(record: any) {
  isCreate.value = false;
  originalExecutorId.value = record.executorId;
  editorError.value = '';
  try {
    const json = await getExecutorJson(record.executorId);
    if (hydrateFromJson(json)) {
      editorMode.value = 'form';
    } else {
      // 旧版 skill（steps）档案：结构化表单暂不支持，转 YAML 高级模式
      editorMode.value = 'yaml';
      yamlText.value = await getExecutorYaml(record.executorId);
    }
    editorOpen.value = true;
  } catch (e: any) {
    message.error(`加载执行器档案失败：${e.message}`);
  }
}

function openYamlEditor() {
  editorMode.value = 'yaml';
  if (isCreate.value) {
    yamlText.value = `id: my-executor\nname: 我的执行器\ntype: agent\nmodel:\n  baseUrl: https://api.deepseek.com\n  provider: deepseek\n  modelId: deepseek-chat\n`;
  } else {
    getExecutorYaml(originalExecutorId.value!)
      .then((yaml) => (yamlText.value = yaml))
      .catch((e: any) => message.error(e.message));
  }
  editorOpen.value = true;
}

async function handleSave() {
  editorSaving.value = true;
  editorError.value = '';
  try {
    const text = editorMode.value === 'form' ? buildProfileText() : yamlText.value;
    if (isCreate.value) {
      const executorId = await createExecutorProfile(text);
      message.success(`执行器「${executorId}」已创建并即时生效`);
    } else {
      const executorId =
        editorMode.value === 'form' ? form.id.trim() : originalExecutorId.value!;
      await updateExecutorProfile(executorId, text);
      message.success('执行器档案已更新，引用它的意图立即生效');
    }
    editorOpen.value = false;
    await load();
  } catch (e: any) {
    editorError.value = e.message;
  } finally {
    editorSaving.value = false;
  }
}

function handleDelete(record: any) {
  Modal.confirm({
    title: `删除执行器「${record.name || record.executorId}」？`,
    content:
      '引用它的意图在执行时将明确报错（EXECUTOR_NOT_FOUND），请先在意图管理页调整这些意图的执行器。',
    okText: '确认删除',
    okType: 'danger',
    onOk: async () => {
      await deleteExecutorProfile(record.executorId);
      message.success('已删除');
      await load();
    },
  });
}

const NODE_TYPE_META: Record<string, { label: string; color: string }> = {
  tool: { label: '工具', color: 'blue' },
  agent: { label: '智能体', color: 'purple' },
  knowledge: { label: '知识库', color: 'green' },
};
</script>

<template>
  <Page
    content-class="flex flex-col gap-4"
    description="执行器 = 意图由谁执行：组合模型、工具集、知识库、智能体等能力，并可编排成流程（按顺序执行，支持条件跳过与中止）。意图通过 executor 编号引用执行器——升级任何能力，引用它的意图自动受益。保存后热更新，无需发版。"
    title="执行器档案"
  >
    <Card size="small">
      <template #title>
        <div class="flex items-center justify-between">
          <span>执行器清单</span>
          <Button type="primary" @click="openCreate">＋ 新建执行器</Button>
        </div>
      </template>
      <Table
        :columns="columns"
        :data-source="items"
        :loading="loading"
        :pagination="false"
        row-key="executorId"
        size="small"
      >
        <template #bodyCell="{ column, record }: any">
          <template v-if="column.key === 'name'">
            <div class="font-medium">{{ record.name || record.executorId }}</div>
            <div class="flex items-center gap-1 text-[11px] text-gray-400">
              <span>{{ record.executorId }}</span>
              <Tag v-if="record.source === 'builtin'" class="!mr-0" color="default">内置</Tag>
            </div>
          </template>
          <template v-else-if="column.key === 'type'">
            <Tag v-if="record.type === 'agent'" color="blue">智能体型</Tag>
            <Tag v-else-if="record.type === 'skill'" color="green">技能型</Tag>
            <Tag v-else color="orange">{{ record.type }}</Tag>
          </template>
          <template v-else-if="column.key === 'model'">
            <span v-if="record.model" class="font-mono text-xs">{{ record.model }}</span>
            <span v-else class="text-gray-400">未配模型</span>
          </template>
          <template v-else-if="column.key === 'tools'">
            <span v-if="record.tools && record.tools.length > 0" class="font-mono text-xs">
              工具 × {{ record.tools.length }}
            </span>
            <span v-else class="text-gray-400">—</span>
          </template>
          <template v-else-if="column.key === 'action'">
            <div class="flex items-center gap-2">
              <Button size="small" @click="openUpdate(record)">编辑</Button>
              <Button danger size="small" @click="handleDelete(record)">删除</Button>
            </div>
          </template>
        </template>
      </Table>
    </Card>

    <!-- 编辑器 -->
    <Modal
      v-model:open="editorOpen"
      :confirm-loading="editorSaving"
      :title="
        (isCreate ? '新建执行器' : `编辑执行器 — ${originalExecutorId}`) +
        (editorMode === 'form' ? '（表单编排）' : '（YAML 高级）')
      "
      :width="860"
      ok-text="保存并生效"
      @ok="handleSave"
    >
      <div class="flex flex-col gap-3 py-2">
        <div v-if="editorError" class="rounded-lg bg-red-50 p-2 text-xs text-red-500">
          {{ editorError }}
        </div>

        <template v-if="editorMode === 'form'">
          <!-- 基础信息 -->
          <Card size="small" title="基础信息">
            <div class="grid grid-cols-2 gap-2">
              <div>
                <div class="mb-1 text-xs text-gray-500">编号（小写字母/数字/连字符）</div>
                <Input v-model:value="form.id" :disabled="!isCreate" placeholder="sales-analyst" />
              </div>
              <div>
                <div class="mb-1 text-xs text-gray-500">名称</div>
                <Input v-model:value="form.name" placeholder="销售分析师" />
              </div>
              <div class="col-span-2">
                <div class="mb-1 text-xs text-gray-500">描述</div>
                <Input v-model:value="form.description" placeholder="这个执行器用哪些能力完成意图" />
              </div>
            </div>
          </Card>

          <!-- 模型附件 -->
          <Card size="small">
            <template #title>
              <div class="flex items-center justify-between">
                <span>模型（智能体能力的底座）</span>
                <label class="flex items-center gap-1 text-xs font-normal">
                  <input v-model="form.useModel" type="checkbox" />
                  启用
                </label>
              </div>
            </template>
            <div v-if="form.useModel" class="grid grid-cols-4 gap-2">
              <div>
                <div class="mb-1 text-xs text-gray-500">供应商</div>
                <Select
                  v-model:value="form.model.provider"
                  :options="
                    Object.keys(MODEL_PRESETS).map((p) => ({ label: p, value: p }))
                  "
                  @change="(v: any) => (form.model.baseUrl = MODEL_PRESETS[v]?.baseUrl ?? form.model.baseUrl)"
                />
              </div>
              <div class="col-span-2">
                <div class="mb-1 text-xs text-gray-500">Base URL</div>
                <Input v-model:value="form.model.baseUrl" />
              </div>
              <div>
                <div class="mb-1 text-xs text-gray-500">
                  模型 ID<span v-if="modelRequired" class="text-red-500">*</span>
                </div>
                <Input v-model:value="form.model.modelId" placeholder="deepseek-chat" />
              </div>
              <div class="col-span-4">
                <div class="mb-1 text-xs text-gray-500">
                  API Key（留空 = 回退环境变量，避免密钥入库）
                </div>
                <Input v-model:value="form.model.apiKey" placeholder="sk-..." type="password" />
              </div>
            </div>
          </Card>

          <!-- 工具附件 -->
          <Card size="small" title="工具集（宿主工具，供流程的工具节点使用）">
            <Select
              v-model:value="form.tools"
              :options="
                toolOptions.map((t) => ({ label: `${t.name} · ${t.description}`, value: t.name }))
              "
              allow-clear
              mode="multiple"
              placeholder="不勾选 = 不限制（全部宿主工具）"
              style="width: 100%"
            />
          </Card>

          <!-- 流程编排 -->
          <Card size="small">
            <template #title>
              <div class="flex items-center justify-between">
                <span>
                  流程编排
                  <span class="ml-1 text-xs font-normal text-gray-400">
                    （按顺序执行；不配流程 = 智能体自主推理完成意图）
                  </span>
                </span>
                <div class="flex gap-1">
                  <Button size="small" @click="addNode('tool')">＋工具</Button>
                  <Button size="small" @click="addNode('agent')">＋智能体</Button>
                  <Button size="small" @click="addNode('knowledge')">＋知识库</Button>
                </div>
              </div>
            </template>
            <div v-if="form.flow.length === 0" class="py-2 text-center text-xs text-gray-400">
              未编排流程：由模型 + 工具白名单自主推理（agent 型默认行为）
            </div>
            <div v-else class="flex items-start gap-3">
              <!-- 节点编辑 -->
              <div class="flex max-h-[360px] flex-1 flex-col gap-2 overflow-y-auto pr-1">
                <Card v-for="(node, index) in form.flow" :key="index" size="small">
                  <template #title>
                    <div class="flex items-center gap-2 text-xs">
                      <Tag :color="NODE_TYPE_META[node.type]?.color">
                        {{ index + 1 }}. {{ NODE_TYPE_META[node.type]?.label }}
                      </Tag>
                      <Input v-model:value="node.id" size="small" style="width: 80px" />
                      <div class="ml-auto flex gap-1">
                        <Button size="small" @click="moveNode(index, -1)">↑</Button>
                        <Button size="small" @click="moveNode(index, 1)">↓</Button>
                        <Button danger size="small" @click="removeNode(index)">×</Button>
                      </div>
                    </div>
                  </template>
                  <div class="flex flex-col gap-2 text-xs">
                    <template v-if="node.type === 'tool'">
                      <div>
                        <div class="mb-1 text-gray-500">宿主工具</div>
                        <Select
                          v-model:value="node.tool"
                          :options="
                            toolOptions.map((t) => ({ label: t.name, value: t.name }))
                          "
                          show-search
                          style="width: 100%"
                        />
                      </div>
                      <div>
                        <div class="mb-1 text-gray-500">
                          参数模板（${'{'}params.xxx{'}'} / ${'{'}nodes.节点id.text{'}'}）
                        </div>
                        <div v-for="(arg, ai) in node.args" :key="ai" class="mb-1 flex gap-1">
                          <Input v-model:value="arg.key" placeholder="参数名" size="small" />
                          <Input v-model:value="arg.value" placeholder="值（支持模板）" size="small" />
                          <Button danger size="small" @click="node.args.splice(ai, 1)">×</Button>
                        </div>
                        <Button size="small" @click="node.args.push({ key: '', value: '' })">
                          ＋参数
                        </Button>
                      </div>
                    </template>
                    <template v-else-if="node.type === 'agent'">
                      <div>
                        <div class="mb-1 text-gray-500">提示词（单次推理，支持模板）</div>
                        <Textarea
                          v-model:value="node.prompt"
                          :rows="3"
                          placeholder="结合资料 ${nodes.n1.text} 分析该客户…"
                        />
                      </div>
                    </template>
                    <template v-else>
                      <div class="grid grid-cols-3 gap-2">
                        <div>
                          <div class="mb-1 text-gray-500">知识库标识</div>
                          <Input v-model:value="node.kb" placeholder="产品手册" />
                        </div>
                        <div>
                          <div class="mb-1 text-gray-500">检索词（可空）</div>
                          <Input v-model:value="node.query" />
                        </div>
                        <div>
                          <div class="mb-1 text-gray-500">召回条数</div>
                          <InputNumber v-model:value="node.topK" :min="1" :max="20" style="width: 100%" />
                        </div>
                      </div>
                    </template>
                    <div class="grid grid-cols-2 gap-2">
                      <div>
                        <div class="mb-1 text-gray-500">执行条件 when（假值 = 跳过）</div>
                        <Input v-model:value="node.when" placeholder="${params.vip} == true" />
                      </div>
                      <div>
                        <div class="mb-1 text-gray-500">中止条件 stopWhen（真值 = 结束流程）</div>
                        <Input v-model:value="node.stopWhen" placeholder="${nodes.n1.details.found}" />
                      </div>
                    </div>
                  </div>
                </Card>
              </div>
              <!-- 流程图预览 -->
              <div class="w-52 shrink-0 rounded-lg bg-gray-50 p-2">
                <div class="mb-2 text-center text-xs text-gray-500">流程预览</div>
                <div class="flex flex-col items-center gap-0">
                  <div class="rounded border border-blue-200 bg-white px-2 py-1 text-[11px] text-blue-500">
                    意图参数
                  </div>
                  <template v-for="(node, index) in form.flow" :key="index">
                    <div class="text-gray-300">↓</div>
                    <div
                      class="w-full rounded border px-2 py-1 text-center text-[11px]"
                      :class="`border-${NODE_TYPE_META[node.type]?.color}-200`"
                      style="background: white"
                    >
                      <span :class="`text-${NODE_TYPE_META[node.type]?.color}-500`">
                        {{ NODE_TYPE_META[node.type]?.label }}
                      </span>
                      <span class="ml-1 font-mono">{{ node.id }}</span>
                      <div v-if="node.when" class="text-[10px] text-orange-400">when: {{ node.when }}</div>
                      <div v-if="node.stopWhen" class="text-[10px] text-red-400">
                        stop: {{ node.stopWhen }}
                      </div>
                    </div>
                  </template>
                  <div class="text-gray-300">↓</div>
                  <div class="rounded border border-green-200 bg-white px-2 py-1 text-[11px] text-green-600">
                    意图结果卡片
                  </div>
                </div>
              </div>
            </div>
          </Card>

          <!-- 出参 -->
          <Card size="small" title="出参模板（缺省 = 标准卡片：意图名 + 最后节点输出）">
            <div class="flex flex-col gap-2">
              <Input v-model:value="form.outputTitle" placeholder="title（可空，模板）" />
              <Input v-model:value="form.outputSummary" placeholder="summary（可空，模板）" />
              <Input v-model:value="form.outputBlocks" placeholder="blocks（可空，JSON 数组模板）" />
            </div>
          </Card>
        </template>

        <template v-else>
          <p class="!my-0 text-xs text-gray-400">
            YAML 高级模式：完整档案定义（JSON 亦可）。保存时服务端强校验并热更新运行时。
          </p>
          <Textarea
            v-model:value="yamlText"
            :rows="18"
            class="font-mono text-xs leading-5"
            spellcheck="false"
          />
        </template>

        <div v-if="editorMode === 'form'" class="text-right">
          <Button size="small" type="link" @click="openYamlEditor">切换到 YAML 高级模式</Button>
        </div>
        <div v-else class="text-right">
          <Button size="small" type="link" :disabled="!isCreate ? false : true" @click="editorMode = 'form'">
            返回表单编排
          </Button>
        </div>
      </div>
    </Modal>
  </Page>
</template>
