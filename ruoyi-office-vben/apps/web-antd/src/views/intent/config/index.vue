<script lang="ts" setup>
import type { IntentApi } from '#/api/intent';
import type { SystemRoleApi } from '#/api/system/role';

import { onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  Button,
  Card,
  message,
  Modal,
  Select,
  Switch,
  Table,
  Tag,
} from 'ant-design-vue';

import {
  createIntentSpec,
  deleteIntentSpec,
  getExecutorOptions,
  getIntentConfigList,
  getSpecYaml,
  updateIntentConfig,
  updateIntentSpec,
} from '#/api/intent';
import { getSimpleRoleList } from '#/api/system/role';

defineOptions({ name: 'IntentConfig' });

const loading = ref(false);
const items = ref<IntentApi.ConfigItem[]>([]);
const roleOptions = ref<Array<{ label: string; value: string }>>([]);
const executorOptions = ref<IntentApi.ExecutorOption[]>([]);
// 行内编辑态：intentId → { enabled, roles, executor }
const editing = reactive<
  Record<string, { enabled: boolean; roles: string[]; executor: string }>
>({});
const savingId = ref<string>();

// YAML 编辑器（新增 / 修改共用）
const editorOpen = ref(false);
const editorMode = ref<'create' | 'update'>('create');
const editorIntentId = ref<string>();
const editorYaml = ref('');
const editorSaving = ref(false);
const editorError = ref('');

const NEW_TEMPLATE = `id: crm.newIntent.demo
name: 新意图示例
description: 一句话描述这个意图做什么
version: 1
scope: local
pages:
  - crm/customer
cardType: analysis
promptTemplate: |
  你是 XX 领域的智能助理。基于工具返回的数据完成任务，禁止编造。
paramsSchema:
  type: object
  properties:
    customerId:
      type: string
      title: 客户编号
  required: [customerId]
context:
  - key: page
    title: 来源页面
    required: false
tools: []
policy:
  roles: ["*"]
  timeoutSeconds: 120
  maxRetries: 1
`;

const columns = [
  { title: '意图', dataIndex: 'name', key: 'name', width: 200 },
  { title: '执行位置', dataIndex: 'scope', key: 'scope', width: 100 },
  { title: '执行器', dataIndex: 'executor', key: 'executor', width: 190 },
  { title: '上架', dataIndex: 'enabled', key: 'enabled', width: 90 },
  { title: '可见角色', dataIndex: 'roles', key: 'roles' },
  { title: '操作', dataIndex: 'action', key: 'action', width: 240 },
];

async function load() {
  loading.value = true;
  try {
    const [list, roles] = await Promise.all([getIntentConfigList(), getSimpleRoleList()]);
    items.value = list;
    roleOptions.value = (roles || [])
      .filter((r: SystemRoleApi.Role) => r.status === 0)
      .map((r: SystemRoleApi.Role) => ({ label: `${r.name}（${r.code}）`, value: r.code }));
    try {
      executorOptions.value = await getExecutorOptions();
    } catch {
      executorOptions.value = [{ executorId: 'builtin-agent', name: '内置推理循环（缺省）', type: 'agent' }];
    }
    list.forEach((item) => {
      editing[item.intentId] = {
        enabled: item.enabled,
        roles: [...item.roles],
        executor: item.executor || 'builtin-agent',
      };
    });
  } finally {
    loading.value = false;
  }
}

onMounted(load);

async function handleSave(item: any) {
  const edit = editing[item.intentId];
  if (!edit) return;
  savingId.value = item.intentId;
  try {
    await updateIntentConfig({
      intentId: item.intentId,
      enabled: edit.enabled,
      roles: edit.roles,
      executor: edit.executor,
    });
    message.success(`「${item.name}」配置已保存，目录即时生效`);
    await load();
  } finally {
    savingId.value = undefined;
  }
}

// ------------------------------------------------------------ 规范维护（新增/修改/删除）

function openCreate() {
  editorMode.value = 'create';
  editorIntentId.value = undefined;
  editorYaml.value = NEW_TEMPLATE;
  editorError.value = '';
  editorOpen.value = true;
}

async function openUpdate(record: any) {
  editorMode.value = 'update';
  editorIntentId.value = record.intentId;
  editorError.value = '';
  try {
    editorYaml.value = await getSpecYaml(record.intentId);
    editorOpen.value = true;
  } catch (e: any) {
    message.error(`加载意图定义失败：${e.message}`);
  }
}

async function handleEditorSave() {
  editorSaving.value = true;
  editorError.value = '';
  try {
    if (editorMode.value === 'create') {
      const intentId = await createIntentSpec(editorYaml.value);
      message.success(`意图「${intentId}」已创建，即时生效`);
    } else {
      await updateIntentSpec(editorIntentId.value!, editorYaml.value);
      message.success('意图已更新，即时生效');
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
    title: `删除意图「${record.name}」？`,
    content: '将同时清理该意图的上架与角色配置，操作后用户端立即不可见。',
    okText: '确认删除',
    okType: 'danger',
    onOk: async () => {
      await deleteIntentSpec(record.intentId);
      message.success('已删除');
      await load();
    },
  });
}
</script>

<template>
  <Page
    content-class="flex flex-col gap-4"
    description="维护意图的定义（五要素 YAML）、上架状态与可见角色。保存后用户端即时生效（无需发版）。角色为空表示不限制。"
    title="意图管理"
  >
    <Card size="small">
      <template #title>
        <div class="flex items-center justify-between">
          <span>意图清单</span>
          <Button type="primary" @click="openCreate">＋ 新增意图</Button>
        </div>
      </template>
      <Table
        :columns="columns"
        :data-source="items"
        :loading="loading"
        :pagination="false"
        row-key="intentId"
        size="small"
      >
        <template #bodyCell="{ column, record }: any">
          <template v-if="column.key === 'name'">
            <div class="font-medium">{{ record.name }}</div>
            <div class="text-[11px] text-gray-400">{{ record.intentId }} · v{{ record.version }}</div>
          </template>
          <template v-else-if="column.key === 'scope'">
            <Tag v-if="record.scope === 'LOCAL'" color="blue">本地</Tag>
            <Tag v-else color="orange">远程 · {{ record.scope }}</Tag>
          </template>
          <template v-else-if="column.key === 'executor'">
            <Select
              v-if="editing[record.intentId]"
              v-model:value="editing[record.intentId]!.executor"
              :options="
                executorOptions.map((o) => ({
                  label: `${o.name} · ${o.executorId}`,
                  value: o.executorId,
                }))
              "
              placeholder="内置推理循环"
              style="width: 100%"
            />
          </template>
          <template v-else-if="column.key === 'enabled'">
            <Switch
              v-if="editing[record.intentId]"
              v-model:checked="editing[record.intentId]!.enabled"
              checked-children="上架"
              un-checked-children="下架"
            />
          </template>
          <template v-else-if="column.key === 'roles'">
            <Select
              v-if="editing[record.intentId]"
              v-model:value="editing[record.intentId]!.roles"
              :options="roleOptions"
              allow-clear
              mode="multiple"
              placeholder="不限制（所有角色可见）"
              style="width: 100%"
            />
          </template>
          <template v-else-if="column.key === 'action'">
            <div class="flex items-center gap-2">
              <Button size="small" @click="openUpdate(record)">修改</Button>
              <Button danger size="small" @click="handleDelete(record)">删除</Button>
              <Button
                :loading="savingId === record.intentId"
                size="small"
                type="primary"
                @click="handleSave(record)"
              >
                保存配置
              </Button>
            </div>
          </template>
        </template>
      </Table>
    </Card>

    <Card size="small" title="说明">
      <ul class="flex flex-col gap-1 text-xs leading-6 text-gray-500">
        <li>· 新增/修改通过 YAML 编辑意图五要素（标识、输入槽位、页面挂载 pages、输出契约、治理策略），保存时服务端强校验；</li>
        <li>· 执行器列选择该意图由谁执行（缺省 builtin-agent 内置推理循环）；执行器档案在「执行器档案」页维护，保存时若引用了未注册的执行器会明确报错；</li>
        <li>· 下架后：用户端意图菜单不再出现，直接调用返回「该意图已下架」；</li>
        <li>· 角色为空 = 不限制（所有登录用户可见可用）；配置角色后仅这些角色可见可用；</li>
        <li>· 执行接口在目录之外独立鉴权：绕过菜单直接调用同样会被拒绝；</li>
        <li>· 删除为逻辑删除，同时清理该意图的上架与角色配置。</li>
      </ul>
    </Card>

    <!-- YAML 编辑器 -->
    <Modal
      v-model:open="editorOpen"
      :confirm-loading="editorSaving"
      :title="editorMode === 'create' ? '新增意图（YAML 定义）' : `修改意图 — ${editorIntentId}`"
      :width="760"
      ok-text="保存"
      @ok="handleEditorSave"
    >
      <div class="flex flex-col gap-2 py-2">
        <div v-if="editorError" class="rounded-lg bg-red-50 p-2 text-xs text-red-500">
          {{ editorError }}
        </div>
        <p class="!my-0 text-xs text-gray-400">
          五要素：① 标识与元数据 ② 输入规范（槽位） ③ 执行编排与页面挂载 ④ 输出规范 ⑤ 治理策略。保存时服务端强校验。
        </p>
        <textarea
          v-model="editorYaml"
          class="min-h-[380px] w-full rounded-lg border border-gray-200 p-3 font-mono text-xs leading-5"
          spellcheck="false"
        ></textarea>
      </div>
    </Modal>
  </Page>
</template>
