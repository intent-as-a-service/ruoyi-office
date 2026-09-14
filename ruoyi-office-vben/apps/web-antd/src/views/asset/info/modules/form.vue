<script lang="ts" setup>
import type { InfoApi } from '#/api/asset/info';

import { useVbenModal } from '@vben/common-ui';
import { message, Tabs, Checkbox, Input, Textarea, Select,RadioGroup,CheckboxGroup, DatePicker } from 'ant-design-vue';

import { computed, ref } from 'vue';
import { $t } from '#/locales';
import { useVbenForm } from '#/adapter/form';
import { getInfo, createInfo, updateInfo } from '#/api/asset/info';

import { useFormSchema } from '../data';

const emit = defineEmits(['success']);
const formData = ref<InfoApi.Info>();
const getTitle = computed(() => {
  return formData.value?.id
    ? $t('ui.actionTitle.edit', ['资产信息'])
    : $t('ui.actionTitle.create', ['资产信息']);
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
  showDefaultActions: false
});

const [Modal, modalApi] = useVbenModal({
  async onConfirm() {
    const { valid } = await formApi.validate();
    if (!valid) {
      return;
    }
        modalApi.lock();
    // 提交表单
    const data = (await formApi.getValues()) as InfoApi.Info;
        try {
      await (formData.value?.id ? updateInfo(data) : createInfo(data));
      // 关闭并提示
      await modalApi.close();
      emit('success');
      message.success( $t('ui.actionMessage.operationSuccess') );
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
    let data = modalApi.getData<InfoApi.Info>();
    if (!data) {
      return;
    }
    if (data.id) {
      modalApi.lock();
      try {
        data = await getInfo(data.id);
      } finally {
        modalApi.unlock();
      }
    }
    // 设置到 values
    formData.value = data;
    await formApi.setValues(formData.value);
  },
});
</script>

<template>
  <Modal :title="getTitle">
    <Form class="mx-4" />
      </Modal>
</template>