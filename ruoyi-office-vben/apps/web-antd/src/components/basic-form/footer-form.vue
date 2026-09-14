<!--
 * @Author: zhanghui
 * @Date: 2025-08-04 23:13:26
 * @LastEditTime: 2025-08-10 21:42:50
 * @LastEditors: zhanghui
 * @Description: 表单详情页底部操作按钮
-->

<script lang="ts" setup>
import {
  BpmProcessInstanceStatus,
  BpmProcessInstanceStatusEditValue,
} from '@vben/constants';

import { Button, Space, Popover, Form, FormItem, Textarea } from 'ant-design-vue';
import { ref, reactive, computed } from 'vue';
import type { FormInstance } from 'ant-design-vue';

// 传入组件参数
const props = defineProps({
  status: {
    type: String as any,
    default: '',
  },
  processStatus: {
    type: String as any,
    default: BpmProcessInstanceStatus.NOT_START,
  },
  submitText: {
    type: String,
    default: '提交',
  },
});
const emit = defineEmits(['close', 'save', 'submit', 'revoke']);
console.warn(props);

// 撤回弹窗相关
const revokePopoverVisible = ref(false);
const revokeFormRef = ref<FormInstance>();
const revokeReasonForm = reactive({
  reason: '',
});
const revokeReasonRule: any = computed(() => {
  return {
    reason: [{ required: true, message: '请输入撤回原因', trigger: 'blur' }],
  };
});

// 关闭
const closeForm = () => {
  emit('close');
};
// 保存
const saveForm = () => {
  emit('save');
};
// 提交
const submitForm = () => {
  emit('submit');
};

// 打开撤回弹窗
const openRevokePopover = () => {
  revokePopoverVisible.value = true;
};

// 关闭撤回弹窗
const closeRevokePopover = () => {
  revokePopoverVisible.value = false;
  if (revokeFormRef.value) {
    revokeFormRef.value.resetFields();
  }
};

// 确认撤回
const confirmRevoke = async () => {
  if (!revokeFormRef.value) return;
  
  try {
    await revokeFormRef.value.validate();
    emit('revoke', revokeReasonForm.reason);
    closeRevokePopover();
  } catch (error) {
    console.error('撤回表单验证失败:', error);
  }
};
</script>
<template>
  <Space>
    <!-- 【提交】按钮 -->
    <Button
      type="primary"
      @click="submitForm"
      v-if="
        processStatus &&
        BpmProcessInstanceStatusEditValue.includes(processStatus)
      "
    >
      {{ $t('common.submit') }}
    </Button>
    <!-- 【保存】按钮 -->
    <Button
      @click="saveForm"
      v-if="
        processStatus &&
        BpmProcessInstanceStatusEditValue.includes(processStatus)
      "
    >
      {{ $t('common.save') }}
    </Button>
    <!-- 【撤回】按钮 -->
    <Popover
      v-model:open="revokePopoverVisible"
      placement="top"
      :overlay-style="{ minWidth: '400px' }"
      trigger="click"
      v-if="processStatus === BpmProcessInstanceStatus.RUNNING"
    >
      <Button type="primary" @click="openRevokePopover">
        {{ $t('common.revoke') }}
      </Button>
      <template #content>
        <!-- 撤回表单 -->
        <div class="flex flex-1 flex-col px-5 pt-5">
          <Form
            layout="vertical"
            class="mb-auto"
            ref="revokeFormRef"
            :model="revokeReasonForm"
            :rules="revokeReasonRule"
            label-width="100px"
          >
            <FormItem label="撤回原因" name="reason">
              <Textarea
                v-model:value="revokeReasonForm.reason"
                placeholder="请输入撤回原因"
                :rows="4"
              />
            </FormItem>
            <FormItem>
              <Button
                type="primary"
                @click="confirmRevoke"
              >
                确认撤回
              </Button>
              <Button
                class="ml-2"
                @click="closeRevokePopover"
              >
                取消
              </Button>
            </FormItem>
          </Form>
        </div>
      </template>
    </Popover>
    <!-- 【关闭】按钮 -->
    <Button @click="closeForm">{{ $t('common.close') }}</Button>
  </Space>
</template>
<style scoped></style>
