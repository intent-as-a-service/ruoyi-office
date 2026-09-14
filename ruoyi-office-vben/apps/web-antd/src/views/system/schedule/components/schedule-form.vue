<script lang="ts" setup>
import type { SystemScheduleApi } from '#/api/system/schedule';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Button, message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import {
  createSchedule,
  getSchedule,
  pushSchedule,
  updateSchedule,
} from '#/api/system/schedule';
import { $t } from '#/locales';

import { useFormSchema } from '../data';

const emit = defineEmits(['success']);
const formData = ref<SystemScheduleApi.Schedule>();
const getTitle = computed(() => {
  return formData.value?.id
    ? $t('ui.actionTitle.edit', ['日程'])
    : $t('ui.actionTitle.create', ['日程']);
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

/** 保存日程（不推送） */
async function handleSave(needPush = false) {
  const { valid } = await formApi.validate();
  if (!valid) {
    return;
  }
  modalApi.lock();
  // 提交表单
  const formValues = await formApi.getValues();
  // 确保只发送 receiverIds，不发送 receivers
  const data: any = { ...formValues };
  if (data.receivers) {
    delete data.receivers;
  }
  // 如果 receiverIds 为空数组，也要发送（后端会处理）
  if (!data.receiverIds) {
    data.receiverIds = [];
  }
  try {
    let scheduleId: number;
    if (formData.value?.id) {
      // 更新
      await updateSchedule(data);
      scheduleId = data.id!;
    } else {
      // 创建
      scheduleId = await createSchedule(data);
    }
    // 如果需要推送
    if (needPush && data.receiverIds && data.receiverIds.length > 0) {
      await pushSchedule({
        scheduleId,
        receiverIds: data.receiverIds,
      });
      message.success('保存并推送成功');
    } else {
      message.success($t('ui.actionMessage.operationSuccess'));
    }
    // 关闭并提示
    await modalApi.close();
    emit('success');
  } finally {
    modalApi.unlock();
  }
}

const [Modal, modalApi] = useVbenModal({
  async onConfirm() {
    // 普通确认：只保存，不推送
    await handleSave(false);
  },
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      formData.value = undefined;
      return;
    }
    // 加载数据
    const data = modalApi.getData<SystemScheduleApi.Schedule>();
    if (!data || !data.id) {
      // 新增场景：如果有默认数据，设置到表单
      if (data && Object.keys(data).length > 0) {
        await formApi.setValues(data);
      }
      return;
    }
    // 编辑场景：从后端加载数据
    modalApi.lock();
    try {
      formData.value = await getSchedule(data.id);
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
        <Button @click="modalApi.close()">取消</Button>
        <Button type="primary" @click="handleSave(false)">确认</Button>
        <Button type="primary" @click="handleSave(true)">确认并立即推送</Button>
      </div>
    </template>
  </Modal>
</template>
