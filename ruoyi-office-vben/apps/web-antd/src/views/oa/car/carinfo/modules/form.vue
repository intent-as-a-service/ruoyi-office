<script lang="ts" setup>
import type { CarApi } from '#/api/oa/car/carinfo';

import { useVbenModal } from '@vben/common-ui';
import { message } from 'ant-design-vue';

import { computed, ref } from 'vue';
import { $t } from '#/locales';
import { useVbenForm } from '#/adapter/form';
import { getCar, createCar, updateCar } from '#/api/oa/car/carinfo';

import { useFormSchema } from '../data';

const emit = defineEmits(['success']);
const formData = ref<CarApi.Car>();
const getTitle = computed(() => {
  return formData.value?.id
    ? $t('ui.actionTitle.edit', ['车辆信息'])
    : $t('ui.actionTitle.create', ['车辆信息']);
});


const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
    },
    formItemClass: 'col-span-1', // 每个表单项占1列
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
    const data = (await formApi.getValues()) as CarApi.Car;
        try {
      await (formData.value?.id ? updateCar(data) : createCar(data));
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
    let data = modalApi.getData<CarApi.Car>();
    if (!data) {
      return;
    }
    if (data.id) {
      // 编辑模式
      modalApi.lock();
      try {
        formData.value = await getCar(data.id);
              // 设置到 values
              debugger;
      await formApi.setValues(formData.value);
      } finally {
        modalApi.unlock();
      }
    }else{
      // 新增模式
      formApi.setValues(data)
    }

  },
});
</script>

<template>
  <Modal :title="getTitle">
    <Form class="mx-4" />
      </Modal>
</template>
