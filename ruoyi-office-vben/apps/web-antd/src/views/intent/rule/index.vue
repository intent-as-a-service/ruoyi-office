<script lang="ts" setup>
/**
 * 自动跟进规则编排台：自然语言 → 规则 YAML → 试算 → 启用。
 *
 * 这一页解决的是"会自己跑起来的东西怎么才敢开"的问题：
 * 生成完先试算，看到现在会命中谁（真实数据）再点启用；启用后即时进待办面板，不用发版。
 */
import type { IntentApi } from '#/api/intent';

import { computed, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  Alert,
  Button,
  Card,
  Empty,
  Input,
  message,
  Modal,
  Spin,
  Table,
  Tag,
} from 'ant-design-vue';

import {
  deleteIntentRule,
  draftIntentRule,
  enableIntentRule,
  getIntentRuleDatasetOptions,
  getIntentRuleList,
  previewIntentRule,
  updateIntentRuleStatus,
} from '#/api/intent';

defineOptions({ name: 'IntentRule' });

const naturalLanguage = ref('');
const page = ref('crm/backlog');
const drafting = ref(false);
const draft = ref<IntentApi.RuleDraft | null>(null);
const yamlText = ref('');
const previewing = ref(false);
const preview = ref<IntentApi.RulePreview | null>(null);
const enabling = ref(false);
const items = ref<IntentApi.IntentRuleItem[]>([]);
const loading = ref(false);
const datasets = ref<IntentApi.DatasetOption[]>([]);

const EXAMPLES = [
  '超过 60 天没回款的客户，提醒我立刻催收',
  '重点客户超过 30 天没联系，提醒我跟进',
  '我负责的商机停滞超过 45 天，提醒我推一把',
];

const hasErrors = computed(() => (draft.value?.errors?.length ?? 0) > 0);

const columns = [
  { title: '规则', dataIndex: 'title', key: 'title' },
  { title: '规则编号', dataIndex: 'ruleKey', key: 'ruleKey' },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '原话', dataIndex: 'sourceText', key: 'sourceText' },
  { title: '操作', dataIndex: 'action', key: 'action', width: 180 },
];

async function load() {
  loading.value = true;
  try {
    const [list, ds] = await Promise.all([
      getIntentRuleList(),
      getIntentRuleDatasetOptions().catch(() => [] as IntentApi.DatasetOption[]),
    ]);
    items.value = list || [];
    datasets.value = ds || [];
  } finally {
    loading.value = false;
  }
}

onMounted(load);

async function handleDraft(text?: string) {
  const source = (text ?? naturalLanguage.value).trim();
  if (!source) {
    message.warning('先用一句话描述你想要的提醒');
    return;
  }
  naturalLanguage.value = source;
  drafting.value = true;
  preview.value = null;
  try {
    const result = await draftIntentRule(source, page.value || undefined);
    draft.value = result;
    yamlText.value = result.yaml;
    if (result.errors?.length) {
      message.warning('生成结果未通过校验，可按提示调整或重新生成');
    } else {
      message.success('规则已生成，下一步试算看看会命中谁');
    }
  } catch (error: any) {
    message.error(error?.message || '生成失败');
  } finally {
    drafting.value = false;
  }
}

async function handlePreview() {
  if (!yamlText.value.trim()) return;
  previewing.value = true;
  try {
    preview.value = await previewIntentRule(yamlText.value, page.value || undefined);
    if (!preview.value.itemCount && !preview.value.badges.length) {
      message.info('这条规则现在不会命中任何数据（可能条件太严，或当前没有符合的事实）');
    }
  } catch (error: any) {
    message.error(error?.message || '试算失败');
  } finally {
    previewing.value = false;
  }
}

async function handleEnable() {
  if (!yamlText.value.trim()) return;
  enabling.value = true;
  try {
    await enableIntentRule({
      yaml: yamlText.value,
      title: draft.value?.title || '自定义规则',
      sourceText: naturalLanguage.value,
    });
    message.success('规则已启用，待办面板即时生效');
    draft.value = null;
    preview.value = null;
    yamlText.value = '';
    await load();
  } catch (error: any) {
    message.error(error?.message || '启用失败');
  } finally {
    enabling.value = false;
  }
}

function handleToggle(item: IntentApi.IntentRuleItem) {
  const next = item.status === 1 ? 0 : 1;
  updateIntentRuleStatus(item.id, next)
    .then(() => {
      message.success(next === 1 ? '已启用' : '已停用');
      return load();
    })
    .catch((error: any) => message.error(error?.message || '操作失败'));
}

function handleDelete(item: IntentApi.IntentRuleItem) {
  Modal.confirm({
    content: `确认删除规则「${item.title}」？删除后不再出现在待办面板。`,
    onOk: async () => {
      await deleteIntentRule(item.id);
      message.success('已删除');
      await load();
    },
  });
}
</script>

<template>
  <Page
    content-class="flex flex-col gap-4"
    description="用一句话描述你要的提醒，系统生成声明式规则并试算命中结果；确认后启用，待办面板即时生效。"
    title="自动跟进规则"
  >
    <Card title="① 说一句话，生成规则">
      <div class="flex flex-col gap-3">
        <Input.TextArea
          v-model:value="naturalLanguage"
          :rows="3"
          placeholder="例如：超过 60 天没回款的客户，提醒我立刻催收"
        />
        <div class="flex flex-wrap items-center gap-2">
          <span class="text-xs text-gray-500">试试：</span>
          <Tag
            v-for="example in EXAMPLES"
            :key="example"
            class="cursor-pointer"
            @click="handleDraft(example)"
          >
            {{ example }}
          </Tag>
        </div>
        <div class="flex items-center gap-2">
          <span class="text-xs text-gray-500">挂载页面（决定这条待办在哪个页面出现）</span>
          <Input v-model:value="page" class="w-64" placeholder="crm/backlog" />
          <Button :loading="drafting" type="primary" @click="handleDraft()">
            生成规则
          </Button>
        </div>
      </div>
    </Card>

    <Card v-if="draft || yamlText" title="② 校验结果与规则内容">
      <Alert
        v-if="hasErrors"
        :message="'规则未通过校验：' + draft?.errors?.join('；')"
        class="mb-3"
        type="error"
        show-icon
      />
      <Alert
        v-else
        class="mb-3"
        message="规则语法与契约校验通过（意图存在、参数可映射）"
        type="success"
        show-icon
      />
      <Input.TextArea v-model:value="yamlText" :rows="14" class="font-mono" />
      <div class="mt-3 flex items-center gap-2">
        <Button :loading="previewing" @click="handlePreview"> 试算：现在会命中谁 </Button>
        <Button
          :disabled="hasErrors"
          :loading="enabling"
          type="primary"
          @click="handleEnable"
        >
          确认启用
        </Button>
      </div>
    </Card>

    <Card v-if="preview" title="③ 试算结果（与线上同一套取数口径）">
      <Spin :spinning="previewing">
        <div v-if="preview.badges.length" class="mb-3 flex flex-wrap gap-2">
          <Tag v-for="badge in preview.badges" :key="badge.intentId" color="orange">
            {{ badge.text }}
          </Tag>
        </div>
        <div v-if="!preview.groups.length" class="text-gray-500">没有可试算的规则</div>
        <div v-for="group in preview.groups" :key="group.ruleId" class="mb-4">
          <div class="mb-2 text-sm font-semibold">
            {{ group.ruleId }} → {{ group.intentId }}
            <span class="ml-2 text-xs text-gray-500">
              数据集 {{ group.dataset }} · 事实 {{ group.count }} 条 · 命中 {{ group.items.length }} 条
            </span>
          </div>
          <Table
            :columns="[
              { title: '提醒事项', dataIndex: 'title', key: 'title' },
              { title: '补充', dataIndex: 'subtitle', key: 'subtitle' },
              { title: '理由', dataIndex: 'reason', key: 'reason' },
              { title: '参数', dataIndex: 'params', key: 'params' },
            ]"
            :data-source="group.items"
            :pagination="false"
            row-key="title"
            size="small"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'params'">
                <span class="text-xs">{{ JSON.stringify(record.params) }}</span>
              </template>
            </template>
          </Table>
          <Empty
            v-if="!group.items.length"
            :image="Empty.PRESENTED_IMAGE_SIMPLE"
            description="当前没有命中数据"
          />
        </div>
      </Spin>
    </Card>

    <Card title="已配置规则">
      <Table
        :columns="columns"
        :data-source="items"
        :loading="loading"
        :pagination="false"
        row-key="id"
        size="small"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <Tag :color="record.status === 1 ? 'green' : 'default'">
              {{ record.status === 1 ? '已启用' : '已停用' }}
            </Tag>
          </template>
          <template v-if="column.key === 'action'">
            <Button size="small" type="link" @click="handleToggle(record)">
              {{ record.status === 1 ? '停用' : '启用' }}
            </Button>
            <Button danger size="small" type="link" @click="handleDelete(record)">
              删除
            </Button>
          </template>
        </template>
      </Table>
    </Card>
  </Page>
</template>
