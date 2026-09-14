<!--
 * @Author: zhanghui
 * @Date: 2025-08-17 18:29:47
 * @LastEditTime: 2025-09-13 17:24:07
 * @LastEditors: zhanghui
 * @Description: helpInput
-->
<script setup lang="ts">
import type { Ref } from 'vue';

import { toRefs } from 'vue';

interface Props {
  bind?: {
    onClick?: (e?: any, rowData?: any, rowIndex?: number) => void;
    readonly: Ref<boolean>;
  };
}
const props = withDefaults(defineProps<Props>(), {}) as Props;

const { bind } = toRefs(props);
// 限制键盘直接输入的内容
function onKeyDown(e: any) {
  e.preventDefault();
}
// 限制通过粘贴带入
function onPaste(e: any) {
  e.preventDefault();
}
function helpClick() {
  if (bind.value?.readonly) {
    return;
  }
  bind?.value?.onClick?.();
}
</script>
<template>
  <a-input
    v-bind="$attrs"
    placeholder="请选择"
    class="help-input"
    allow-clear
    @paste="onPaste"
    @keydown="onKeyDown"
    inputmode="none"
    enterkeyhint="none"
  >
    <template #suffix>
      <svg
        class="search-icon"
        viewBox="0 0 1024 1024"
        width="14"
        height="14"
        fill="currentColor"
        @click="helpClick"
      >
        <path
          d="M909.6 854.5L649.9 594.8C690.2 542.7 712 479 712 412c0-80.2-31.3-155.4-87.9-212.1-56.6-56.7-132-87.9-212.1-87.9s-155.5 31.3-212.1 87.9C143.2 256.5 112 331.8 112 412c0 80.1 31.3 155.5 87.9 212.1C256.5 680.8 331.8 712 412 712c67 0 130.6-21.8 182.7-62l259.7 259.6a8.2 8.2 0 0 0 11.6 0l43.6-43.5a8.2 8.2 0 0 0 0-11.6zM570.4 570.4C528 612.7 471.8 636 412 636s-116-23.3-158.4-65.6C211.3 528 188 471.8 188 412s23.3-116 65.6-158.4C296 211.3 352.2 188 412 188s116 23.3 158.4 65.6S636 352.2 636 412s-23.3 116-65.6 158.4z"
        />
      </svg>
    </template>
  </a-input>
</template>
<style scoped>
.help-input :deep(.ant-input) {
  cursor: pointer;
  caret-color: transparent; /* 隐藏输入光标 */
  user-select: none; /* 禁止文本选择 */
  text-size-adjust: 100%;
}

.help-input :deep(.ant-input-disabled) {
  cursor: no-drop;
}

.search-icon {
  font-size: 14px;
  color: #00000040; /* placeholder 颜色 */
  cursor: pointer;
  transition: color 0.2s;
}

.search-icon.disabled {
  color: #00000025;
  cursor: no-drop;
}

.ant-input-affix-wrapper-disabled .search-icon {
  cursor: no-drop;
}
</style>
