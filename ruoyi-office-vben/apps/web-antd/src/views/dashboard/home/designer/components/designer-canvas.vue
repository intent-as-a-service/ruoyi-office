<script lang="ts" setup>
import type { GridLayoutItem } from '../../types/layout';

import { computed, nextTick, ref, watch } from 'vue';

import { Button, Empty } from 'ant-design-vue';
import { GridItem, GridLayout } from 'grid-layout-plus';

import ComponentWrapper from '../../components/wrapper/component-wrapper.vue';

interface Props {
  layout: GridLayoutItem[];
  colNum?: number;
  rowHeight?: number;
  selectedItemId?: null | string;
  containerPadding?: number;
  margin?: number;
}

interface Emits {
  (e: 'update:layout', layout: GridLayoutItem[]): void;
  (e: 'update:selectedItemId', id: null | string): void;
  (e: 'itemRemoved', id: string): void;
}

const props = withDefaults(defineProps<Props>(), {
  colNum: 24,
  rowHeight: 60,
  selectedItemId: null,
  containerPadding: 10,
  margin: 10,
});

const emit = defineEmits<Emits>();

const localLayout = ref<GridLayoutItem[]>([]);

// 初始化本地布局
watch(
  () => props.layout,
  (newLayout, oldLayout) => {
    // 避免循环更新：检查是否真的变化了
    if (JSON.stringify(newLayout) === JSON.stringify(oldLayout)) {
      return;
    }
    // eslint-disable-next-line unicorn/prefer-structured-clone
    localLayout.value = JSON.parse(JSON.stringify(newLayout));
  },
  { immediate: true },
);

/** 布局更新事件 */
function handleLayoutUpdated(newLayout: any[]) {
  // grid-layout-plus 返回的是简化的布局对象，需要合并完整配置
  const updatedLayout = newLayout.map((item) => {
    const existingItem = localLayout.value.find((l) => l.i === item.i);
    return {
      ...existingItem,
      x: item.x,
      y: item.y,
      w: item.w,
      h: item.h,
    } as GridLayoutItem;
  });

  // 直接通知父组件，不更新 localLayout（避免循环）
  emit('update:layout', updatedLayout);
}

/** 选中布局项 */
function handleSelectItem(id: string) {
  emit('update:selectedItemId', id);
}

/** 删除布局项 */
function handleRemoveItem(id: string) {
  const newLayout = localLayout.value.filter((item) => item.i !== id);
  emit('update:layout', newLayout);
  emit('itemRemoved', id);

  // 如果删除的是当前选中项，清除选中状态
  if (props.selectedItemId === id) {
    emit('update:selectedItemId', null);
  }
}

/** 处理拖拽进入 */
function handleDragOver(event: DragEvent) {
  event.preventDefault();
  if (event.dataTransfer) {
    event.dataTransfer.dropEffect = 'copy';
  }
}

/** 查找可用的放置位置 */
function findAvailablePosition(
  width: number,
  height: number,
): { x: number; y: number } {
  const colNum = props.colNum || 24;

  // 如果布局为空，放在左上角
  if (localLayout.value.length === 0) {
    return { x: 0, y: 0 };
  }

  // 计算每一行的占用情况
  const maxY = Math.max(...localLayout.value.map((item) => item.y + item.h), 0);

  // 从上到下，从左到右查找可用位置
  for (let y = 0; y <= maxY + 1; y++) {
    for (let x = 0; x <= colNum - width; x++) {
      // 检查这个位置是否与现有组件重叠
      const isOverlap = localLayout.value.some((item) => {
        return !(
          x + width <= item.x || // 在左边
          x >= item.x + item.w || // 在右边
          y + height <= item.y || // 在上边
          y >= item.y + item.h // 在下边
        );
      });

      if (!isOverlap) {
        return { x, y };
      }
    }
  }

  // 如果没找到合适位置，放在最底部
  return { x: 0, y: maxY + 1 };
}

/** 处理放置 */
function handleDrop(event: DragEvent) {
  event.preventDefault();

  const componentData = event.dataTransfer?.getData('component');
  if (!componentData) return;

  try {
    const component = JSON.parse(componentData);

    // 生成唯一ID
    const newId = `item-${Date.now()}-${Math.random().toString(36).slice(2, 11)}`;
    const width = component.defaultWidth || 6;
    const height = component.defaultHeight || 4;

    // 智能查找可用位置
    const position = findAvailablePosition(width, height);

    const newItem: GridLayoutItem = {
      i: newId,
      x: position.x,
      y: position.y,
      w: width,
      h: height,
      componentCode: component.code,
      config: {},
      isDraggable: true,
      isResizable: true,
      minW: 2,
      minH: 2,
    };

    const newLayout = [...localLayout.value, newItem];
    emit('update:layout', newLayout);

    // 选中新添加的组件
    nextTick(() => {
      emit('update:selectedItemId', newId);
    });
  } catch (error) {
    console.error('Failed to add component:', error);
  }
}

const isEmpty = computed(() => localLayout.value.length === 0);
</script>

<template>
  <div
    class="designer-canvas h-full w-full overflow-auto bg-gray-50"
    :style="{ padding: `${containerPadding}px` }"
    @dragover="handleDragOver"
    @drop="handleDrop"
  >
    <div v-if="isEmpty" class="flex h-full items-center justify-center">
      <Empty description="拖拽组件到此处开始设计" />
    </div>

    <GridLayout
      v-else
      :layout="localLayout"
      :col-num="colNum"
      :row-height="rowHeight"
      :is-draggable="true"
      :is-resizable="true"
      :vertical-compact="false"
      :prevent-collision="false"
      :use-css-transforms="true"
      :margin="[margin, margin]"
      :style="{
        marginLeft: containerPadding === 0 ? `-${margin}px` : '0',
        marginTop: containerPadding === 0 ? `-${margin}px` : '0',
        marginRight: containerPadding === 0 ? `-${margin}px` : '0',
        marginBottom: containerPadding === 0 ? `-${margin}px` : '0',
      }"
      @layout-updated="handleLayoutUpdated"
    >
      <GridItem
        v-for="item in localLayout"
        :key="item.i"
        :x="item.x"
        :y="item.y"
        :w="item.w"
        :h="item.h"
        :i="item.i"
        :min-w="item.minW || 2"
        :min-h="item.minH || 2"
        :max-w="item.maxW"
        :max-h="item.maxH"
        :static="item.static"
        :is-draggable="item.isDraggable !== false"
        :is-resizable="item.isResizable !== false"
        class="grid-item-wrapper"
        :class="{ 'is-selected': selectedItemId === item.i }"
        @click.stop="handleSelectItem(item.i)"
      >
        <div class="grid-item-content flex h-full w-full flex-col">
          <!-- 组件工具栏 -->
          <div
            class="item-toolbar flex items-center justify-between border-b bg-white px-2 py-1"
          >
            <span class="truncate text-xs text-gray-600">{{
              item.componentCode
            }}</span>
            <Button
              type="text"
              danger
              size="small"
              @click.stop="handleRemoveItem(item.i)"
            >
              删除
            </Button>
          </div>

          <!-- 组件内容区域 -->
          <div class="item-body flex-1 overflow-hidden bg-white">
            <ComponentWrapper
              :component-code="item.componentCode"
              :config="item.config"
            />
          </div>
        </div>
      </GridItem>
    </GridLayout>
  </div>
</template>

<style scoped>
.designer-canvas {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: #f5f5f5;

  /* 24列布局，使用双层网格线系统 */
  background-image: 
    /* 每4行一条主网格线（深色） */
    repeating-linear-gradient(
      0deg,
      transparent,
      transparent calc(240px - 2px),
      #cbd5e0 calc(240px - 2px),
      #cbd5e0 240px
    ),
    /* 每行一条次网格线（浅色） */
    repeating-linear-gradient(
        0deg,
        transparent,
        transparent calc(60px - 1px),
        #e8e8e8 calc(60px - 1px),
        #e8e8e8 60px
      ),
    /* 每6列一条主网格线（深色，24列分成4组） */
    repeating-linear-gradient(
        90deg,
        transparent,
        transparent calc(100% / 4 - 2px),
        #cbd5e0 calc(100% / 4 - 2px),
        #cbd5e0 calc(100% / 4)
      ),
    /* 每列一条次网格线（浅色） */
    repeating-linear-gradient(
        90deg,
        transparent,
        transparent calc(100% / 24 - 1px),
        #e8e8e8 calc(100% / 24 - 1px),
        #e8e8e8 calc(100% / 24)
      );
  background-position:
    0 0,
    0 0,
    0 0,
    0 0;
  background-size:
    100% 240px,
    100% 60px,
    calc(100% / 4) 100%,
    calc(100% / 24) 100%;
}

.grid-item-wrapper {
  border: 2px solid transparent;
  transition: border-color 0.2s;
}

.grid-item-wrapper:hover {
  border-color: hsl(var(--primary));
}

.grid-item-wrapper.is-selected {
  border-color: hsl(var(--primary));
  box-shadow: 0 0 0 2px hsl(var(--primary) / 20%);
}

.grid-item-content {
  overflow: hidden;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgb(0 0 0 / 10%);
}

.item-toolbar {
  height: 32px;
  min-height: 32px;
}

.item-body {
  padding: 8px;
}

:deep(.vue-grid-layout) {
  min-height: 500px;
}

/* 当容器内边距为0时，使用负margin抵消边缘组件的间距，需要调整overflow */
.designer-canvas[style*='padding: 0px'] {
  overflow: visible;
}

.designer-canvas[style*='padding: 0px'] > .vue-grid-layout {
  position: relative;
}

:deep(.vue-grid-item) {
  transition: all 0.2s ease;
}

/* 拖拽/调整大小时的占位符样式 */
:deep(.vue-grid-item.vue-grid-placeholder) {
  z-index: 2 !important;
  background: hsl(var(--primary) / 15%) !important;
  border: 2px dashed hsl(var(--primary)) !important;
  border-radius: 4px;
  opacity: 1 !important;
}

/* 调整大小的拖拽手柄 */
:deep(.vue-grid-item > .vue-resizable-handle) {
  right: -5px !important;
  bottom: -5px !important;
  width: 10px !important;
  height: 10px !important;
  background-color: hsl(var(--primary));
  background-image: none;
  border-radius: 50%;
  opacity: 0;
  transition: opacity 0.2s;
}

:deep(.vue-grid-item:hover > .vue-resizable-handle) {
  opacity: 1;
}

/* 正在调整大小时的状态 */
:deep(.vue-grid-item.resizing) {
  z-index: 999;
  opacity: 0.8;
}

/* 正在拖拽时的状态 */
:deep(.vue-grid-item.vue-draggable-dragging) {
  z-index: 999;
  opacity: 0.7;
  transition: none !important;
}
</style>
