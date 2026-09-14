<script setup lang="ts">
import { computed } from 'vue';

import { Alert } from 'ant-design-vue';

import { getComponent } from '../registry';

interface Props {
  componentCode: string; // 组件编码
  config?: Record<string, any>; // 组件配置
}

const props = withDefaults(defineProps<Props>(), {
  config: () => ({}),
});

const component = computed(() => {
  return getComponent(props.componentCode);
});

const hasError = computed(() => {
  return !component.value;
});

// 是否允许溢出（某些组件需要显示超出容器的元素，如删除按钮）
const allowOverflow = computed(() => {
  // 应用中心组件需要允许溢出，以便删除按钮完整显示
  return props.componentCode === 'workbench_app_center';
});

// 标题配置
const showTitle = computed(() => {
  // 如果 showTitle 未定义，检查是否有 title，有 title 就显示
  if (props.config?.showTitle === undefined) {
    return !!props.config?.title;
  }
  return props.config.showTitle === true;
});
const title = computed(() => props.config?.title || '');

// 浮动标题配置
const isFloatingTitle = computed(() => {
  return props.config?.floatingTitle === true;
});

// 浮动标题样式（绝对定位，不占空间）
const floatingTitleStyle = computed(() => {
  if (!isFloatingTitle.value) return {};

  const style: Record<string, string> = {
    position: 'absolute',
    zIndex: '10',
  };

  // 设置上下左右边距来定位
  if (props.config?.titleMarginTop !== undefined) {
    style.top = `${props.config.titleMarginTop}px`;
  }
  if (props.config?.titleMarginRight !== undefined) {
    style.right = `${props.config.titleMarginRight}px`;
  }
  if (props.config?.titleMarginBottom !== undefined) {
    style.bottom = `${props.config.titleMarginBottom}px`;
  }
  if (props.config?.titleMarginLeft !== undefined) {
    style.left = `${props.config.titleMarginLeft}px`;
  }

  return style;
});

// 标题文字样式
const titleTextStyle = computed(() => {
  const style: Record<string, string> = {};

  // 文字大小
  if (props.config?.titleFontSize !== undefined) {
    style.fontSize = `${props.config.titleFontSize}px`;
  }

  // 文字颜色
  if (props.config?.titleColor) {
    style.color = props.config.titleColor;
  }

  // 文字加粗
  if (props.config?.titleBold !== undefined) {
    style.fontWeight = props.config.titleBold ? 'bold' : 'normal';
  }

  return style;
});

// 边距配置（组件级别，优先级高于全局配置）
const wrapperStyle = computed(() => {
  const style: Record<string, string> = {};

  // 内边距（padding）：组件内部内容与边框的距离
  if (props.config?.paddingTop !== undefined) {
    style.paddingTop = `${props.config.paddingTop}px`;
  }
  if (props.config?.paddingRight !== undefined) {
    style.paddingRight = `${props.config.paddingRight}px`;
  }
  if (props.config?.paddingBottom !== undefined) {
    style.paddingBottom = `${props.config.paddingBottom}px`;
  }
  if (props.config?.paddingLeft !== undefined) {
    style.paddingLeft = `${props.config.paddingLeft}px`;
  }

  // 外边距（margin）：组件与其他组件之间的距离
  if (props.config?.marginTop !== undefined) {
    style.marginTop = `${props.config.marginTop}px`;
  }
  if (props.config?.marginRight !== undefined) {
    style.marginRight = `${props.config.marginRight}px`;
  }
  if (props.config?.marginBottom !== undefined) {
    style.marginBottom = `${props.config.marginBottom}px`;
  }
  if (props.config?.marginLeft !== undefined) {
    style.marginLeft = `${props.config.marginLeft}px`;
  }

  return style;
});
</script>

<template>
  <div
    class="component-wrapper flex h-full w-full flex-col"
    :style="wrapperStyle"
  >
    <!-- 普通标题（不浮动，占据空间） -->
    <div
      v-if="showTitle && title && !isFloatingTitle"
      class="component-title flex items-center px-2 py-0.5"
    >
      <span :style="titleTextStyle">{{ title }}</span>
    </div>

    <!-- 浮动标题（绝对定位，不占空间） -->
    <div
      v-if="showTitle && title && isFloatingTitle"
      class="component-title-floating flex items-center px-2 py-0.5"
      :style="floatingTitleStyle"
    >
      <span :style="titleTextStyle">{{ title }}</span>
    </div>

    <!-- 组件内容 -->
    <div
      class="component-content flex-1"
      :class="{
        'overflow-hidden': !allowOverflow,
        'overflow-visible': allowOverflow,
      }"
    >
      <component
        :is="component"
        v-if="component && !hasError"
        v-bind="config"
        class="h-full w-full"
      />
      <Alert
        v-else
        :message="`组件未找到: ${componentCode}`"
        description="该组件可能未注册或已被删除，请检查组件配置"
        type="error"
        show-icon
      />
    </div>
  </div>
</template>

<style scoped>
.component-wrapper {
  position: relative;
}

.component-title {
  flex-shrink: 0;
  min-height: 40px;
}

.component-title-floating {
  flex-shrink: 0;
  white-space: nowrap;
}

.component-content {
  min-height: 0;
}
</style>
