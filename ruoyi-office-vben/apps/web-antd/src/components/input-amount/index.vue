<script setup lang="ts">
import { computed, ref, watch } from 'vue';

import { Input } from 'ant-design-vue';

interface Props {
  value?: null | number | string;
  placeholder?: string;
  showUnit?: boolean; // 是否显示"元"单位
  precision?: number; // 小数位数，默认2位
  max?: number; // 最大值
  min?: number; // 最小值
}

interface Emits {
  (e: 'update:value', value: null | number): void;
  (e: 'change', value: null | number): void;
}

const props = withDefaults(defineProps<Props>(), {
  showUnit: false,
  precision: 2,
  max: 99_999_999.99,
  min: 0,
});

const emit = defineEmits<Emits>();

const isFocused = ref(false);
const inputValue = ref<string>('');

// 格式化数字为千分位显示
function formatNumber(num: number): string {
  return num.toLocaleString('zh-CN', {
    minimumFractionDigits: props.precision,
    maximumFractionDigits: props.precision,
  });
}

// 移除千分位分隔符
function unformatNumber(str: string): string {
  return str.replaceAll(',', '');
}

// 验证和格式化输入值
function validateAndFormat(value: string): {
  number: null | number;
  valid: boolean;
} {
  if (!value || value.trim() === '') {
    return { valid: true, number: null };
  }

  const cleanValue = unformatNumber(value);
  const number = Number.parseFloat(cleanValue);

  if (isNaN(number)) {
    return { valid: false, number: null };
  }

  if (number < props.min || number > props.max) {
    return { valid: false, number: null };
  }

  return { valid: true, number };
}

// 显示值计算
const displayValue = computed(() => {
  if (isFocused.value) {
    return inputValue.value;
  }

  if (props.value === null || props.value === undefined || props.value === '') {
    return '';
  }

  const number =
    typeof props.value === 'string'
      ? Number.parseFloat(props.value)
      : props.value;
  if (isNaN(number)) {
    return '';
  }

  return formatNumber(number);
});

// 监听外部值变化
watch(
  () => props.value,
  (newValue) => {
    if (!isFocused.value) {
      if (newValue === null || newValue === undefined || newValue === '') {
        inputValue.value = '';
      } else {
        const number =
          typeof newValue === 'string' ? Number.parseFloat(newValue) : newValue;
        if (!isNaN(number)) {
          inputValue.value = number.toString();
        }
      }
    }
  },
  { immediate: true },
);

// 处理输入
function handleInput(e: Event) {
  const target = e.target as HTMLInputElement;
  if (!target) return;

  let value = target.value;

  // 只允许数字、小数点和负号
  value = value.replaceAll(/[^\d.-]/g, '');

  // 确保只有一个小数点
  const parts = value.split('.');
  if (parts.length > 2) {
    value = `${parts[0]}.${parts.slice(1).join('')}`;
  }

  // 限制小数位数
  if (parts.length === 2 && parts[1] && parts[1].length > props.precision) {
    value = `${parts[0]}.${parts[1].slice(0, Math.max(0, props.precision))}`;
  }

  inputValue.value = value;
  target.value = value;
}

// 处理失焦
function handleBlur() {
  isFocused.value = false;

  const { valid, number } = validateAndFormat(inputValue.value);

  if (valid) {
    emit('update:value', number);
    emit('change', number);
  } else {
    // 如果输入无效，恢复到原始值
    const originalNumber =
      typeof props.value === 'string'
        ? Number.parseFloat(props.value)
        : props.value;
    inputValue.value =
      originalNumber !== null &&
      originalNumber !== undefined &&
      !isNaN(originalNumber)
        ? originalNumber.toString()
        : '';
  }
}

// 处理聚焦
function handleFocus() {
  isFocused.value = true;

  if (props.value !== null && props.value !== undefined && props.value !== '') {
    const number =
      typeof props.value === 'string'
        ? Number.parseFloat(props.value)
        : props.value;
    if (!isNaN(number)) {
      inputValue.value = number.toString();
    }
  }
}
</script>

<template>
  <Input
    v-bind="$attrs"
    :value="displayValue"
    :placeholder="placeholder || '请输入金额'"
    class="amount-input"
    @input="handleInput"
    @blur="handleBlur"
    @focus="handleFocus"
  >
    <template v-if="showUnit" #suffix>
      <span class="text-gray-400">元</span>
    </template>
  </Input>
</template>

<style scoped>
/* 输入框值居右对齐 */
.amount-input :deep(.ant-input) {
  text-align: right;
}

/* placeholder文字居左对齐 */
.amount-input :deep(.ant-input::placeholder) {
  text-align: left;
}

/* 兼容性处理：针对不同浏览器的placeholder */
.amount-input :deep(.ant-input::input-placeholder) {
  text-align: left;
}

.amount-input :deep(.ant-input::placeholder) {
  text-align: left;
}

.amount-input :deep(.ant-input:input-placeholder) {
  text-align: left;
}
</style>
