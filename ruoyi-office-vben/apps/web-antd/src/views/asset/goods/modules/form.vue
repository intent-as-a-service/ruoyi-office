<script lang="ts" setup>
import type { GoodsApi } from '#/api/asset/goods';

import { useVbenModal } from '@vben/common-ui';
import { message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import { createGoods, getGoods, updateGoods } from '#/api/asset/goods';
import { $t } from '#/locales';
import { computed, ref } from 'vue';

import { useFormSchema } from '../data';

const emit = defineEmits(['success']);
const formData = ref<GoodsApi.Goods>();
const getTitle = computed(() => {
  return formData.value?.id
    ? $t('ui.actionTitle.edit', ['物品信息'])
    : $t('ui.actionTitle.create', ['物品信息']);
});


const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
    },
    formItemClass: 'col-span-1',
    labelWidth: 80,
  },
   // 设置表单容器为2列网格布局
  wrapperClass: 'grid grid-cols-2 gap-4 p-4',
  layout: 'horizontal',
  schema: useFormSchema(),
  showDefaultActions: false
});

const [Modal, modalApi] = useVbenModal({
   // 设置弹窗宽度，支持两列显示
   class: 'w-3/4 max-w-4xl',
  async onConfirm() {
    const { valid } = await formApi.validate();
    if (!valid) {
      return;
    }
        modalApi.lock();
    // 提交表单
    const data = (await formApi.getValues()) as GoodsApi.Goods;
        try {
      await (formData.value?.id ? updateGoods(data) : createGoods(data));
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
    let data = modalApi.getData<GoodsApi.Goods>();
    if (!data) {
      return;
    }
    if (data.id) {
      modalApi.lock();
      try {
        data = await getGoods(data.id);
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
