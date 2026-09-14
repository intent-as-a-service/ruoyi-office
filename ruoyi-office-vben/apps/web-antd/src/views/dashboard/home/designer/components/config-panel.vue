<script lang="ts" setup>
import type { GridLayoutItem } from '../../types/layout';

import type { SystemHomeComponentApi } from '#/api/system/home/component';

import { computed, ref, watch } from 'vue';

import {
  Collapse,
  CollapsePanel,
  Empty,
  Form,
  FormItem,
  Input,
  InputNumber,
  Select,
} from 'ant-design-vue';

interface Props {
  selectedItem: GridLayoutItem | null;
  components: SystemHomeComponentApi.Component[];
}

interface Emits {
  (e: 'updateConfig', itemId: string, config: Record<string, any>): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const formData = ref<Record<string, any>>({});
const configSchema = ref<null | SystemHomeComponentApi.ConfigSchema>(null);
const activeKeys = ref<string[]>(['basic']); // 默认展开基础配置
const lastSelectedItemId = ref<null | string>(null); // 记录上次选中的组件ID

/** 当前选中组件的定义信息 */
const currentComponent = computed(() => {
  if (!props.selectedItem) return null;
  return props.components.find(
    (c) => c.code === props.selectedItem?.componentCode,
  );
});

/** 分离基础配置、内边距配置、外边距配置和浮动标题配置 */
const basicProperties = computed(() => {
  if (!configSchema.value?.properties) return [];
  return configSchema.value.properties.filter(
    (prop) =>
      ![
        'floatingTitle',
        'marginBottom',
        'marginLeft',
        'marginRight',
        'marginTop',
        'paddingBottom',
        'paddingLeft',
        'paddingRight',
        'paddingTop',
        'titleBold',
        'titleColor',
        'titleFontSize',
        'titleMarginBottom',
        'titleMarginLeft',
        'titleMarginRight',
        'titleMarginTop',
      ].includes(prop.key),
  );
});

const paddingProperties = computed(() => {
  if (!configSchema.value?.properties) return [];
  return configSchema.value.properties.filter((prop) =>
    ['paddingBottom', 'paddingLeft', 'paddingRight', 'paddingTop'].includes(
      prop.key,
    ),
  );
});

// const marginProperties = computed(() => {
//   if (!configSchema.value?.properties) return [];
//   return configSchema.value.properties.filter((prop) =>
//     ['marginBottom', 'marginLeft', 'marginRight', 'marginTop'].includes(
//       prop.key,
//     ),
//   );
// });

const floatingTitleProperties = computed(() => {
  if (!configSchema.value?.properties) return [];
  return configSchema.value.properties.filter((prop) =>
    [
      'floatingTitle',
      'titleBold',
      'titleColor',
      'titleFontSize',
      'titleMarginBottom',
      'titleMarginLeft',
      'titleMarginRight',
      'titleMarginTop',
    ].includes(prop.key),
  );
});

// 监听 selectedItem 的变化，确保切换组件时更新配置
watch(
  () => props.selectedItem?.i,
  (newId, oldId) => {
    // 检查是否真的切换了组件（同时检查新旧ID）
    if (newId === oldId || newId === lastSelectedItemId.value) {
      return;
    }

    lastSelectedItemId.value = newId || null;
    const newItem = props.selectedItem;

    if (!newItem) {
      formData.value = {};
      configSchema.value = null;
      return;
    }

    // 重新解析当前组件的 schema
    const component = props.components.find(
      (c) => c.code === newItem.componentCode,
    );

    if (!component || !component.configSchema) {
      formData.value = {};
      configSchema.value = null;
      return;
    }

    try {
      const schema = JSON.parse(component.configSchema);
      configSchema.value = schema;

      // 更新 formData 为新组件的配置
      const newFormData: Record<string, any> = {};
      schema.properties?.forEach((prop: any) => {
        if (newItem.config && newItem.config[prop.key] !== undefined) {
          newFormData[prop.key] = newItem.config[prop.key];
        } else if (prop.default !== undefined) {
          newFormData[prop.key] = prop.default;
        }
      });

      formData.value = newFormData;
    } catch (error) {
      console.error('Failed to parse config schema:', error);
      formData.value = {};
      configSchema.value = null;
    }
  },
  { immediate: true, flush: 'post' },
);

/** 更新配置 */
function handleUpdateConfig() {
  if (!props.selectedItem) return;
  emit('updateConfig', props.selectedItem.i, formData.value);
}

/** 根据类型获取表单组件 */
function getFormComponent(type: string) {
  switch (type) {
    case 'number': {
      return InputNumber;
    }
    default: {
      return Input;
    }
  }
}
</script>

<template>
  <div class="config-panel flex h-full flex-col bg-white">
    <div class="border-b px-4 py-3">
      <h3 class="text-base font-semibold">属性配置</h3>
      <p class="mt-1 text-xs text-gray-500">
        {{ selectedItem ? '配置选中组件的属性' : '请先选择一个组件' }}
      </p>
    </div>

    <div class="flex-1 overflow-y-auto">
      <Empty
        v-if="!selectedItem"
        description="请选择一个组件"
        :image="Empty.PRESENTED_IMAGE_SIMPLE"
        class="mt-10"
      />

      <div v-else-if="!configSchema || configSchema.properties.length === 0">
        <Empty
          description="该组件暂无可配置属性"
          :image="Empty.PRESENTED_IMAGE_SIMPLE"
          class="mt-10"
        />
      </div>

      <Collapse v-else v-model:active-key="activeKeys" class="config-collapse">
        <!-- 基础配置 -->
        <CollapsePanel key="basic" header="基础配置">
          <Form layout="vertical" class="px-2">
            <FormItem
              v-for="prop in basicProperties"
              :key="prop.key"
              :label="prop.label"
              :required="prop.required"
            >
              <!-- 布尔下拉选择 -->
              <Select
                v-if="prop.type === 'boolean'"
                v-model:value="formData[prop.key]"
                :options="[
                  { label: '显示', value: true as any },
                  { label: '不显示', value: false as any },
                ]"
                class="w-full"
                @change="handleUpdateConfig"
              />

              <!-- 颜色选择器 -->
              <div
                v-else-if="prop.type === 'color'"
                class="flex items-center gap-2"
              >
                <input
                  v-model="formData[prop.key]"
                  type="color"
                  class="h-8 w-16 cursor-pointer rounded border"
                  @change="handleUpdateConfig"
                />
                <Input
                  v-model:value="formData[prop.key]"
                  :placeholder="`请输入${prop.label}`"
                  class="flex-1"
                  @change="handleUpdateConfig"
                />
              </div>

              <!-- 其他类型 -->
              <component
                :is="getFormComponent(prop.type)"
                v-else
                v-model:value="formData[prop.key]"
                :placeholder="`请输入${prop.label}`"
                class="w-full"
                @change="handleUpdateConfig"
              />
            </FormItem>
          </Form>
        </CollapsePanel>

        <!-- 组件内边距设置 -->
        <CollapsePanel
          v-if="paddingProperties.length > 0"
          key="padding"
          header="组件内边距设置"
        >
          <Form layout="vertical" class="px-2">
            <div class="mb-3 text-xs text-gray-500">
              组件内部内容与边框的距离（padding）
            </div>
            <FormItem
              v-for="prop in paddingProperties"
              :key="prop.key"
              :label="prop.label"
            >
              <InputNumber
                v-model:value="formData[prop.key]"
                :min="prop.min || 0"
                :max="prop.max || 50"
                :step="1"
                class="w-full"
                @change="handleUpdateConfig"
              />
            </FormItem>
          </Form>
        </CollapsePanel>

        <!-- 组件外边距设置 - 已隐藏 -->
        <!-- <CollapsePanel v-if="marginProperties.length > 0" key="margin" header="组件外边距设置">
          <Form layout="vertical" class="px-2">
            <div class="mb-3 text-xs text-gray-500">
              组件与其他组件之间的距离（margin），优先级高于全局配置
            </div>
            <FormItem
              v-for="prop in marginProperties"
              :key="prop.key"
              :label="prop.label"
            >
              <InputNumber
                v-model:value="formData[prop.key]"
                :min="prop.min || 0"
                :max="prop.max || 50"
                :step="1"
                class="w-full"
                @change="handleUpdateConfig"
              />
            </FormItem>
          </Form>
        </CollapsePanel> -->

        <!-- 浮动标题配置 -->
        <CollapsePanel
          v-if="floatingTitleProperties.length > 0"
          key="floatingTitle"
          header="浮动标题设置"
        >
          <Form layout="vertical" class="px-2">
            <div class="mb-3 text-xs text-gray-500">
              浮动标题不占据组件空间，使用绝对定位悬浮在组件上方
            </div>

            <!-- 浮动标题开关 -->
            <FormItem
              v-for="prop in floatingTitleProperties.filter(
                (p) => p.key === 'floatingTitle',
              )"
              :key="prop.key"
              :label="prop.label"
            >
              <Select
                v-model:value="formData[prop.key]"
                :options="[
                  { label: '浮动', value: true as any },
                  { label: '不浮动', value: false as any },
                ]"
                class="w-full"
                @change="handleUpdateConfig"
              />
            </FormItem>

            <!-- 浮动标题边距配置（仅在浮动时显示） -->
            <template v-if="formData.floatingTitle === true">
              <div class="mb-2 mt-3 text-xs text-gray-500">
                标题定位：设置边距控制标题位置（如：上边距0=居顶，右边距0=居右）
              </div>
              <FormItem
                v-for="prop in floatingTitleProperties.filter((p) =>
                  p.key.startsWith('titleMargin'),
                )"
                :key="prop.key"
                :label="prop.label"
              >
                <InputNumber
                  v-model:value="formData[prop.key]"
                  :min="prop.min || 0"
                  :max="prop.max || 100"
                  :step="1"
                  class="w-full"
                  @change="handleUpdateConfig"
                />
              </FormItem>
            </template>

            <!-- 标题样式配置（浮动和不浮动都显示） -->
            <div class="mb-2 mt-3 text-xs text-gray-500">
              标题样式：自定义标题文字的外观
            </div>

            <!-- 文字大小 -->
            <FormItem
              v-for="prop in floatingTitleProperties.filter(
                (p) => p.key === 'titleFontSize',
              )"
              :key="prop.key"
              :label="prop.label"
            >
              <InputNumber
                v-model:value="formData[prop.key]"
                :min="prop.min || 10"
                :max="prop.max || 24"
                :step="1"
                class="w-full"
                @change="handleUpdateConfig"
              />
            </FormItem>

            <!-- 文字加粗 -->
            <FormItem
              v-for="prop in floatingTitleProperties.filter(
                (p) => p.key === 'titleBold',
              )"
              :key="prop.key"
              :label="prop.label"
            >
              <Select
                v-model:value="formData[prop.key]"
                :options="[
                  { label: '加粗', value: true as any },
                  { label: '不加粗', value: false as any },
                ]"
                class="w-full"
                @change="handleUpdateConfig"
              />
            </FormItem>

            <!-- 文字颜色 -->
            <FormItem
              v-for="prop in floatingTitleProperties.filter(
                (p) => p.key === 'titleColor',
              )"
              :key="prop.key"
              :label="prop.label"
            >
              <Input
                v-model:value="formData[prop.key]"
                :placeholder="prop.default || '#000000'"
                class="w-full"
                @change="handleUpdateConfig"
              />
              <div class="mt-1 text-xs text-gray-400">
                支持颜色值，如：#FFFFFF、#000000、rgb(255,255,255)
              </div>
            </FormItem>
          </Form>
        </CollapsePanel>

        <!-- 组件信息 -->
        <CollapsePanel
          v-if="selectedItem && currentComponent"
          key="info"
          header="组件信息"
        >
          <div class="space-y-3 px-2 text-xs text-gray-600">
            <div class="flex justify-between">
              <span>组件名称:</span>
              <span class="font-medium">{{ currentComponent.name }}</span>
            </div>
            <div class="flex justify-between">
              <span>组件编码:</span>
              <span class="font-mono text-gray-800">{{
                currentComponent.code
              }}</span>
            </div>
            <div class="flex justify-between">
              <span>尺寸:</span>
              <span>{{ selectedItem.w }}x{{ selectedItem.h }}</span>
            </div>
            <div class="flex justify-between">
              <span>位置:</span>
              <span>({{ selectedItem.x }}, {{ selectedItem.y }})</span>
            </div>
          </div>
        </CollapsePanel>
      </Collapse>
    </div>
  </div>
</template>

<style scoped>
.config-panel {
  border-left: 1px solid #e8e8e8;
}

.config-collapse {
  border: none;
}

.config-collapse :deep(.ant-collapse-item) {
  border-bottom: 1px solid #f0f0f0;
}

.config-collapse :deep(.ant-collapse-header) {
  padding: 12px 16px;
  font-weight: 500;
}

.config-collapse :deep(.ant-collapse-content-box) {
  padding: 16px 12px;
}
</style>
