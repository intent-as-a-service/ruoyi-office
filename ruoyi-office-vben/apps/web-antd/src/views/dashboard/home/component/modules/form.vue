<script lang="ts" setup>
import type { SystemHomeComponentApi } from '#/api/system/home/component';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message, TabPane, Tabs } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import {
  createComponent,
  getComponent,
  updateComponent,
} from '#/api/system/home/component';
import { $t } from '#/locales';

import { useFormSchema } from '../data';
import SchemaEditor from './schema-editor.vue';

const emit = defineEmits(['success']);
const formData = ref<SystemHomeComponentApi.Component>();
const getTitle = computed(() => {
  return formData.value?.id
    ? $t('ui.actionTitle.edit', ['组件'])
    : $t('ui.actionTitle.create', ['组件']);
});

const activeTab = ref('basic');
const configSchemaData = ref<SystemHomeComponentApi.ConfigSchema>({
  properties: [],
});

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
    },
    formItemClass: 'col-span-2',
    labelWidth: 100,
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
    modalApi.lock();
    // 提交表单
    const data =
      (await formApi.getValues()) as SystemHomeComponentApi.Component;
    // 添加配置Schema
    data.configSchema = JSON.stringify(configSchemaData.value);

    try {
      await (formData.value?.id
        ? updateComponent(data)
        : createComponent(data));
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
      configSchemaData.value = { properties: [] };
      activeTab.value = 'basic';
      return;
    }
    // 加载数据
    const data = modalApi.getData<SystemHomeComponentApi.Component>();
    if (!data || !data.id) {
      return;
    }
    modalApi.lock();
    try {
      formData.value = await getComponent(data.id);
      // 设置到 values
      await formApi.setValues(formData.value);
      // 解析配置Schema
      if (formData.value.configSchema) {
        try {
          configSchemaData.value = JSON.parse(formData.value.configSchema);
        } catch {
          configSchemaData.value = { properties: [] };
        }
      }
    } finally {
      modalApi.unlock();
    }
  },
});
</script>

<template>
  <Modal class="w-[800px]" :title="getTitle">
    <Tabs v-model:active-key="activeTab" class="mx-4">
      <TabPane key="basic" tab="基本信息">
        <Form />
      </TabPane>
      <TabPane key="schema" tab="配置Schema">
        <SchemaEditor v-model:value="configSchemaData" />
      </TabPane>
    </Tabs>
  </Modal>
</template>
