<script lang="ts" setup>
import type { MeetingRoomApi } from '#/api/oa/meetingroom/roominfo';

import { computed, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import {
  createMeetingRoom,
  getMeetingRoom,
  updateMeetingRoom,
} from '#/api/oa/meetingroom/roominfo';
import { $t } from '#/locales';

import { useFormSchema } from '../data';

const emit = defineEmits(['success']);
const formData = ref<MeetingRoomApi.MeetingRoom>();
const getTitle = computed(() => {
  return formData.value?.id
    ? $t('ui.actionTitle.edit', ['会议室信息'])
    : $t('ui.actionTitle.create', ['会议室信息']);
});

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
    },
    formItemClass: 'col-span-1', // 每个表单项占1列
    labelWidth: 120,
  },
  // 设置表单容器为2列网格布局
  wrapperClass: 'grid grid-cols-2 gap-4 p-4',
  layout: 'horizontal',
  schema: useFormSchema(),
  showDefaultActions: false,
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
    const data = (await formApi.getValues()) as MeetingRoomApi.MeetingRoom;
    try {
      await (formData.value?.id
        ? updateMeetingRoom(data)
        : createMeetingRoom(data));
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
    const data = modalApi.getData<MeetingRoomApi.MeetingRoom>();
    if (!data) {
      return;
    }
    if (data.id) {
      // 编辑模式
      modalApi.lock();
      try {
        formData.value = await getMeetingRoom(data.id);
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
    <Form class="meeting-room-form mx-4" />
  </Modal>
</template>

<style scoped>
/* 解决附件上传列表按钮被遮挡问题 */
:deep(.meeting-room-form .ant-upload-list) {
  width: 100%;
}

:deep(.meeting-room-form .ant-upload-list-item) {
  width: 100%;
  padding-right: 40px;
  margin-top: 8px;
}

:deep(.meeting-room-form .ant-upload-list-item-name) {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

:deep(.meeting-room-form .ant-upload-list-item-actions) {
  position: absolute;
  top: 50%;
  right: 0;
  display: flex;
  gap: 8px;
  transform: translateY(-50%);
}

:deep(.meeting-room-form .ant-upload-list-item-actions .anticon) {
  color: rgb(0 0 0 / 45%);
  cursor: pointer;
  transition: color 0.3s;
}

:deep(.meeting-room-form .ant-upload-list-item-actions .anticon:hover) {
  color: rgb(0 0 0 / 85%);
}
</style>
