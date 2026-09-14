<script lang="ts" setup>
import type { SystemHomeComponentApi } from '#/api/system/home/component';

import { ref, watch } from 'vue';

import {
  Button,
  Card,
  Form,
  FormItem,
  Input,
  InputNumber,
  Select,
  Switch,
} from 'ant-design-vue';

interface Props {
  value?: SystemHomeComponentApi.ConfigSchema;
}

interface Emits {
  (e: 'update:value', value: SystemHomeComponentApi.ConfigSchema): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const localValue = ref<SystemHomeComponentApi.ConfigSchema>(
  props.value || { properties: [] },
);

watch(
  () => props.value,
  (newVal) => {
    if (newVal) {
      // eslint-disable-next-line unicorn/prefer-structured-clone
      localValue.value = JSON.parse(JSON.stringify(newVal));
    }
  },
  { immediate: true, deep: true },
);

watch(
  localValue,
  (newVal) => {
    emit('update:value', newVal);
  },
  { deep: true },
);

/** 添加属性 */
function addProperty() {
  localValue.value.properties.push({
    key: '',
    label: '',
    type: 'string',
    default: '',
    required: false,
  });
}

/** 删除属性 */
function removeProperty(index: number) {
  localValue.value.properties.splice(index, 1);
}

const typeOptions = [
  { label: '文本', value: 'string' },
  { label: '数字', value: 'number' },
  { label: '布尔', value: 'boolean' },
];
</script>

<template>
  <div class="schema-editor">
    <div class="mb-4 flex items-center justify-between">
      <div class="text-sm text-gray-600">
        定义组件可配置的属性，这些属性将在首页设计器中可编辑
      </div>
      <Button type="primary" @click="addProperty"> 添加属性 </Button>
    </div>

    <div
      v-if="localValue.properties.length === 0"
      class="py-8 text-center text-gray-400"
    >
      暂无配置属性，点击"添加属性"按钮添加
    </div>

    <div class="space-y-4">
      <Card
        v-for="(prop, index) in localValue.properties"
        :key="index"
        size="small"
        class="relative"
      >
        <template #title>
          <span class="text-sm">属性 {{ index + 1 }}</span>
        </template>
        <template #extra>
          <Button
            type="text"
            danger
            size="small"
            @click="removeProperty(index)"
          >
            删除
          </Button>
        </template>

        <Form layout="vertical">
          <FormItem label="属性键名" required>
            <Input
              v-model:value="prop.key"
              placeholder="如: title, refreshInterval"
            />
          </FormItem>

          <FormItem label="属性标签" required>
            <Input
              v-model:value="prop.label"
              placeholder="如: 标题, 刷新间隔"
            />
          </FormItem>

          <div class="grid grid-cols-2 gap-4">
            <FormItem label="属性类型" required>
              <Select v-model:value="prop.type" :options="typeOptions" />
            </FormItem>

            <FormItem label="是否必填">
              <Switch v-model:checked="prop.required" />
            </FormItem>
          </div>

          <FormItem label="默认值">
            <Input
              v-if="prop.type === 'string'"
              v-model:value="prop.default"
              placeholder="请输入默认值"
            />
            <InputNumber
              v-else-if="prop.type === 'number'"
              v-model:value="prop.default"
              class="w-full"
              placeholder="请输入默认值"
            />
            <Switch
              v-else-if="prop.type === 'boolean'"
              v-model:checked="prop.default"
            />
          </FormItem>
        </Form>
      </Card>
    </div>
  </div>
</template>

<style scoped>
.schema-editor {
  max-height: 500px;
  overflow-y: auto;
}
</style>
