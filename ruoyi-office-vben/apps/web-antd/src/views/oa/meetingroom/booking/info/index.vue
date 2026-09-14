<script lang="ts" setup>
import type { VbenFormSchema } from '#/adapter/form';
import type { MeetingRoomBookingApi } from '#/api/oa/meetingroom/booking';

import { nextTick, onMounted, ref, shallowRef } from 'vue';
import { useRoute } from 'vue-router';

import { Loading } from '@vben/common-ui';
import {
  BpmProcessInstanceStatus,
  BpmProcessInstanceStatusEditValue,
} from '@vben/constants';
import { useTabs } from '@vben/hooks';
import { useUserStore } from '@vben/stores';

import { Button, message } from 'ant-design-vue';

import { withdrawProcessToStart } from '#/api/bpm/task';
import {
  getMeetingRoomBooking,
  saveMeetingRoomBooking,
  submitMeetingRoomBooking,
} from '#/api/oa/meetingroom/booking';
import { AttachmentList } from '#/components/attachment-list';
import { BasicForm, CardContainer } from '#/components/basic-form';
import { $t } from '#/locales';

import { MeetingRoomSelectModal } from '../components';
import { useFormSchema } from './data';

defineOptions({ name: 'OaMeetingRoomBookingInfo' });

// 定义组件 props
const props = defineProps<{
  activityNodes?: any[];
  id?: number | string; // 从 BusinessFormComponent 传递的 id
  isApproval?: boolean; // 是否审批态
  nodeKey?: string; // 节点key
  nodeKeyName?: string; // 节点名称
  processDefinition?: any; // 流程定义信息
  processInstance?: any; // 流程实例信息
}>();

const route = useRoute();
const userStore = useUserStore();

const { closeCurrentTab } = useTabs();

const formData = ref<Partial<MeetingRoomBookingApi.MeetingRoomBooking>>({});

const readonly = ref(false);
const loading = ref(false);

// BasicForm组件引用
const basicFormRef = ref();

// 会议室选择弹窗引用
const modalRef = ref<InstanceType<typeof MeetingRoomSelectModal>>();

// 附件列表引用
const attachmentListRef = ref();

// 表单schema - 使用shallowRef避免深度响应式
const formSchema = shallowRef<VbenFormSchema[]>([]);

// 初始化表单schema
function initFormSchema() {
  formSchema.value = useFormSchema(modalRef, readonly);
}

// 优先使用 props 传递的 id，如果没有则使用路由参数
let id: number | undefined = (() => {
  if (props.id) {
    return typeof props.id === 'string' ? Number(props.id) : props.id;
  }
  return route.query.id ? Number(route.query.id) : undefined;
})();

// 关闭按钮处理
function handleClose() {
  closeCurrentTab();
}

// 保存及提交
async function handleSaveAndSubmit(isSubmit: boolean) {
  loading.value = true;

  if (!basicFormRef.value) return;

  // 提交前校验 - 只有提交时才进行校验，保存时不校验
  if (isSubmit) {
    const { valid } = await basicFormRef.value.validateForm();
    // 如果校验不通过，则不允许提交
    if (!valid) {
      loading.value = false;
      return;
    }
  }

  try {
    // 获取表单值 - 保存时不进行校验
    const formValues = isSubmit
      ? ((await basicFormRef.value.getFormValues()) as MeetingRoomBookingApi.MeetingRoomBooking)
      : ((await basicFormRef.value.getFormValues(
          false,
        )) as MeetingRoomBookingApi.MeetingRoomBooking);

    // 合并表单值和其他数据
    const data = {
      ...formData.value,
      ...formValues,
    };

    id = await (isSubmit
      ? submitMeetingRoomBooking(data)
      : saveMeetingRoomBooking(data));

    message.success({
      content: $t('ui.actionMessage.operationSuccess'),
      key: 'action_key_msg',
    });

    // 保存后重新加载数据
    await loadData();
  } catch (error) {
    console.error('保存失败:', error);
  } finally {
    loading.value = false;
  }
}

// 撤回
async function handleRevoke(reason: string) {
  if (
    formData.value.processInstanceId !== undefined &&
    formData.value.processInstanceId !== null
  ) {
    loading.value = true;
    try {
      await withdrawProcessToStart({
        processInstanceId: formData.value.processInstanceId,
        reason: reason || '制单人撤回',
      });
      message.success('撤回成功');
      await loadData();
    } catch (error) {
      console.error('撤回失败:', error);
    } finally {
      loading.value = false;
    }
  }
}

// 加载数据
async function loadData() {
  // 新建默认数据
  if (id === undefined || id === null) {
    // 新建时设置默认值
    formData.value = {
      creator: userStore.userInfo?.id,
      creatorName: userStore.userInfo?.nickname,
      companyId: userStore.userInfo?.companyId || 0,
      companyName: userStore.userInfo?.companyName || '',
      deptId: userStore.userInfo?.deptId || 0,
      deptName: userStore.userInfo?.deptName || '',
      processStatus: BpmProcessInstanceStatus.NOT_START, // 草稿状态
      useStatus: 0, // 待使用
      createTime: new Date(),
      billCode: '',
      attachments: [],
      attendees: [],
      attendeeNames: [],
    };
    return;
  }

  // 加载数据
  loading.value = true;
  try {
    const data = await getMeetingRoomBooking(id);
    // 扩展数据，添加显示需要的字段
    formData.value = {
      ...data,
    };

    readonly.value =
      props.isApproval === true
        ? props.isApproval
        : !BpmProcessInstanceStatusEditValue.includes(
            formData.value.processStatus as number,
          );

    // 重新初始化表单schema（因为readonly状态可能变化）
    initFormSchema();

    // 设置表单值
    if (basicFormRef.value) {
      await basicFormRef.value.setFormValues(data);
    }
  } catch (error) {
    const errorMessage =
      error instanceof Error ? error.message : '获取会议室预定申请单详情失败';
    message.error(errorMessage);
    console.error('获取会议室预定申请单详情失败:', error);
  } finally {
    loading.value = false;

    // 数据加载完成后，刷新BasicForm组件数据
    nextTick(() => {
      basicFormRef.value?.refreshAllData();
    });
  }
}

// 处理会议室选择
function handleMeetingRoomSelect(val: any) {
  if (basicFormRef.value && val && val.roomName && val.id) {
    basicFormRef.value.setFormValues({
      roomName: val.roomName,
      roomId: val.id,
      roomLocation: val.roomLocation || '',
      roomType: val.roomType,
    });

    // 同时更新formData
    Object.assign(formData.value, {
      roomName: val.roomName,
      roomId: val.id,
      roomLocation: val.roomLocation || '',
      roomType: val.roomType,
    });
  }
}

// 审批前的业务表单处理方法
async function beforeApproval(): Promise<boolean> {
  // 审批时不需要额外保存，只需返回true
  return true;
}

// 处理附件上传
function handleUploadAttachment() {
  if (attachmentListRef.value) {
    attachmentListRef.value.handleTriggerUpload();
  }
}

// 暴露方法给父组件调用
defineExpose({
  beforeApproval,
  loadData,
  handleSaveAndSubmit,
});

onMounted(() => {
  initFormSchema();
  loadData();
});
</script>

<template>
  <Loading :spinning="loading">
    <BasicForm
      ref="basicFormRef"
      :header-data="{
        ...formData,
        billName: '会议室预定申请单',
      }"
      :form-data="formData"
      :form-schema="formSchema"
      :disabled="readonly"
      @close="handleClose"
      @save="handleSaveAndSubmit(false)"
      @submit="handleSaveAndSubmit(true)"
      @revoke="handleRevoke"
      :hide-footer="props.isApproval"
      :activity-nodes="props.activityNodes"
    >
      <!-- 扩展插槽，用于附件等 -->
      <template #form-extension>
        <!-- 附件列表 -->
        <CardContainer :title="$t('common.attachmentInfo')">
          <template #extra>
            <Button
              v-if="!readonly"
              type="primary"
              @click="handleUploadAttachment"
            >
              上传附件
            </Button>
          </template>
          <AttachmentList
            ref="attachmentListRef"
            v-model="formData.attachments"
            :readonly="readonly"
            :max-count="10"
            :max-size="20"
            :hide-upload-button="true"
          />
        </CardContainer>
      </template>
    </BasicForm>

    <!-- 会议室选择弹窗 -->
    <MeetingRoomSelectModal ref="modalRef" @select="handleMeetingRoomSelect" />
  </Loading>
</template>

<style scoped>
/* 业务页面样式已封装到BasicForm组件中，无需重复定义 */
</style>
