<script lang="ts" setup>
import type { SystemHomeComponentApi } from '#/api/system/home/component';

import { onMounted, ref } from 'vue';

import { Card, Collapse, CollapsePanel, Empty } from 'ant-design-vue';

import {
  getCategoryList,
  getComponentsByCategory,
} from '#/api/system/home/component';

interface Props {
  disabled?: boolean;
}

withDefaults(defineProps<Props>(), {
  disabled: false,
});

const emit = defineEmits<{
  (e: 'addComponent', component: SystemHomeComponentApi.Component): void;
}>();

const categories = ref<SystemHomeComponentApi.ComponentCategory[]>([]);
const componentsByCategory = ref<
  Record<number, SystemHomeComponentApi.Component[]>
>({});
const loading = ref(false);
const activeKeys = ref<number[]>([]);

/** 加载组件数据 */
async function loadComponents() {
  loading.value = true;
  try {
    const [categoriesRes, componentsRes] = await Promise.all([
      getCategoryList(),
      getComponentsByCategory(),
    ]);

    categories.value = categoriesRes;
    componentsByCategory.value = componentsRes;

    // 默认展开第一个分类
    if (categories.value.length > 0) {
      activeKeys.value = [categories.value[0].id!];
    }
  } finally {
    loading.value = false;
  }
}

/** 开始拖拽组件 */
function handleDragStart(
  event: DragEvent,
  component: SystemHomeComponentApi.Component,
) {
  if (event.dataTransfer) {
    event.dataTransfer.effectAllowed = 'copy';
    event.dataTransfer.setData('component', JSON.stringify(component));
  }
}

/** 点击添加组件 */
function handleAddComponent(component: SystemHomeComponentApi.Component) {
  emit('addComponent', component);
}

onMounted(() => {
  loadComponents();
});
</script>

<template>
  <div class="component-panel flex h-full flex-col">
    <div class="border-b px-4 py-3">
      <h3 class="text-base font-semibold">组件库</h3>
      <p class="mt-1 text-xs text-gray-500">拖拽或点击添加组件到画布</p>
    </div>

    <div class="flex-1 overflow-y-auto p-4">
      <Collapse v-model:active-key="activeKeys" :bordered="false">
        <CollapsePanel
          v-for="category in categories"
          :key="category.id!"
          :header="category.name"
        >
          <template #extra>
            <span class="text-xs text-gray-500">
              {{ componentsByCategory[category.id!]?.length || 0 }} 个
            </span>
          </template>

          <div
            v-if="componentsByCategory[category.id!]?.length"
            class="space-y-2"
          >
            <Card
              v-for="component in componentsByCategory[category.id!]"
              :key="component.id"
              class="cursor-move transition-shadow hover:shadow-md"
              :class="{ 'cursor-not-allowed opacity-50': disabled }"
              size="small"
              :draggable="!disabled"
              @dragstart="handleDragStart($event, component)"
              @click="!disabled && handleAddComponent(component)"
            >
              <div class="flex items-start space-x-2">
                <div class="min-w-0 flex-1">
                  <div class="truncate text-sm font-medium">
                    {{ component.name }}
                  </div>
                  <div class="mt-1 line-clamp-2 text-xs text-gray-500">
                    {{ component.description || '暂无描述' }}
                  </div>
                  <div class="mt-2 text-xs text-gray-400">
                    尺寸: {{ component.defaultWidth }}x{{
                      component.defaultHeight
                    }}
                  </div>
                </div>
              </div>
            </Card>
          </div>

          <Empty
            v-else
            description="该分类暂无组件"
            :image="Empty.PRESENTED_IMAGE_SIMPLE"
          />
        </CollapsePanel>
      </Collapse>

      <Empty
        v-if="!loading && categories.length === 0"
        description="暂无可用组件"
        :image="Empty.PRESENTED_IMAGE_SIMPLE"
      />
    </div>
  </div>
</template>

<style scoped>
.component-panel {
  background: #fff;
  border-right: 1px solid #e8e8e8;
}

:deep(.ant-collapse-borderless > .ant-collapse-item) {
  border-bottom: 1px solid #e8e8e8;
}

:deep(.ant-collapse-content-box) {
  padding: 12px 0;
}
</style>
