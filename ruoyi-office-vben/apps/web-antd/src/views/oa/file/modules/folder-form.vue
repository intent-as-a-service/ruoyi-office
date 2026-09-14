<script lang="ts" setup>
import type { FileApi } from '#/api/oa/file';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import { createFileInfo, getFileInfo, updateFileInfo } from '#/api/oa/file';
import { $t } from '#/locales';

import { useFolderFormSchema } from '../data';

const emit = defineEmits(['success']);
const formData = ref<FileApi.FileInfo>();
const getTitle = computed(() => {
  return formData.value?.id ? '编辑文件夹' : '新建文件夹';
});

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
    },
  },
  layout: 'horizontal',
  schema: useFolderFormSchema(),
  showDefaultActions: false,
});

const [Modal, modalApi] = useVbenModal({
  class: 'w-1/2',
  async onConfirm() {
    const { valid } = await formApi.validate();
    if (!valid) {
      return;
    }
    modalApi.lock();
    // 提交表单
    const data = (await formApi.getValues()) as FileApi.FileInfo;
    try {
      await (formData.value?.id ? updateFileInfo(data) : createFileInfo(data));
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
    const data = modalApi.getData<FileApi.FileInfo>();
    if (!data) {
      return;
    }
    if (data.id) {
      // 编辑模式
      modalApi.lock();
      try {
        formData.value = await getFileInfo(data.id);
        // 设置到 values
        await formApi.setValues(formData.value);
      } finally {
        modalApi.unlock();
      }
    } else {
      // 新增模式
      formApi.setValues(data);
    }
  },
});
</script>

<template>
  <Modal :title="getTitle">
    <Form class="mx-4" />
  </Modal>
</template>
