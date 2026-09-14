<script lang="ts" setup>
import type { SystemDeptApi } from '#/api/system/dept';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import { createDept, getDept, updateDept } from '#/api/system/dept';
import { $t } from '#/locales';
import { OrgTypeEnum } from '@vben/constants';

import { useFormSchema } from '../data';

const emit = defineEmits(['success']);
const formData = ref<SystemDeptApi.Dept>();
const getTitle = computed(() => {
  return formData.value?.id
    ? $t('ui.actionTitle.edit', ['组织'])
    : $t('ui.actionTitle.create', ['组织']);
});

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
    },
    formItemClass: 'col-span-2',
    labelWidth: 80,
  },
  layout: 'horizontal',
  schema: useFormSchema(),
  showDefaultActions: false,
});

const [Modal, modalApi] = useVbenModal({
  async onConfirm() {
    const { valid } = await formApi.validate();
    if (!valid) {
      return;
    }
    // 提交表单
    const data = (await formApi.getValues()) as SystemDeptApi.Dept;
    debugger;
    // 当前节点为部门，校验上级组织类型与当前组织类型的合理性
    if (data.orgType === OrgTypeEnum.COMPANY && data.parentId) {
      try {
        const parentDept = await getDept(data.parentId);
        if (parentDept.orgType === OrgTypeEnum.DEPARTMENT) {
          message.error('部门组织下不能添加公司');
          return;
        }
      } catch (error) {
        console.error('获取上级组织信息失败:', error);
        message.error('获取上级组织信息失败，请重试');
        return;
      }
    }

    modalApi.lock();
    try {
      await (formData.value?.id ? updateDept(data) : createDept(data));
      // 关闭并提示
      await modalApi.close();
      emit('success');
      message.success($t('ui.actionMessage.operationSuccess'));
    } finally {
      modalApi.unlock();
    }
  },
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      formData.value = undefined;
      return;
    }
    // 加载数据
    const data = modalApi.getData<SystemDeptApi.Dept>();
    if (!data || !data.id) {
      // 设置上级
      await formApi.setValues(data);
      return;
    }
    modalApi.lock();
    try {
      formData.value = await getDept(data.id);
      // 设置到 values
      await formApi.setValues(formData.value);
    } finally {
      modalApi.unlock();
    }
  },
});
</script>

<template>
  <Modal :title="getTitle">
    <Form class="mx-4" />
  </Modal>
</template>
