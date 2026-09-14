<script lang="ts" setup>
import type { SystemScheduleApi } from '#/api/system/schedule';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Button } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import { useFormSchema } from '#/views/system/schedule/data';

const formData = ref<SystemScheduleApi.Schedule>();
const getTitle = computed(() => {
  return '日程详情';
});

// 获取只读的表单 schema
const readonlySchema = computed(() => {
  const schema = useFormSchema();
  return schema.map((item) => ({
    ...item,
    componentProps: {
      ...item.componentProps,
      disabled: true,
      readonly: true,
    },
  }));
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
  schema: readonlySchema,
  showDefaultActions: false,
});

const [Modal, modalApi] = useVbenModal({
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      formData.value = undefined;
      return;
    }
    // 加载数据
    const data = modalApi.getData<SystemScheduleApi.Schedule>();
    if (!data) {
      return;
    }
    modalApi.lock();
    try {
      formData.value = data;
      // 将 receivers 转换为 receiverIds
      const formValues: any = { ...formData.value };
      formValues.receiverIds =
        formValues.receivers && formValues.receivers.length > 0
          ? formValues.receivers.map(
              (r: SystemScheduleApi.Receiver) => r.receiverId,
            )
          : [];
      // 删除 receivers 字段，只保留 receiverIds
      delete formValues.receivers;
      // 设置到 values
      await formApi.setValues(formValues);
    } finally {
      modalApi.unlock();
    }
  },
});
</script>

<template>
  <Modal :title="getTitle" class="w-2/3">
    <Form class="mx-4" />
    <template #footer>
      <div class="flex justify-end gap-2">
        <Button @click="modalApi.close()">关闭</Button>
      </div>
    </template>
  </Modal>
</template>
