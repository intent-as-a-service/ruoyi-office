<script lang="ts" setup>
import type { VbenFormSchema } from '#/adapter/form';
import type { CarReturnBillApi } from '#/api/oa/car/carreturn';

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

import { cancelProcessInstanceByStartUser } from '#/api/bpm/processInstance';
import {
  getCarReturnBill,
  saveCarReturnBill,
  submitCarReturnBill,
} from '#/api/oa/car/carreturn';
import { AttachmentList } from '#/components/attachment-list';
import { BasicForm, CardContainer } from '#/components/basic-form';
import { $t } from '#/locales';

import { CarApplySelectModal, CarSelectModal } from '../../components';
import { useFormSchema } from './data';

// 定义组件 props
const props = defineProps<{
  id?: number | string; // 从 BusinessFormComponent 传递的 id
  isApproval?: boolean; // 是否审批态
  processDefinition?: any; // 流程定义信息
  processInstance?: any; // 流程实例信息
}>();
const route = useRoute();
const userStore = useUserStore();

const { closeCurrentTab } = useTabs();

const formData = ref<Partial<CarReturnBillApi.CarReturnBill>>({});

const readonly = ref(false);
const loading = ref(false);

// BasicForm组件引用
const basicFormRef = ref();

// 车辆选择弹窗引用
const modalRef = ref<InstanceType<typeof CarSelectModal>>();
// 用车申请单选择弹窗引用
const applyModalRef = ref<InstanceType<typeof CarApplySelectModal>>();

// 附件列表引用
const attachmentListRef = ref();

// 表单schema - 使用shallowRef避免深度响应式
const formSchema = shallowRef<VbenFormSchema[]>([]);

// 初始化表单schema
function initFormSchema() {
  formSchema.value = useFormSchema(modalRef, readonly, applyModalRef);
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

  // 提交前校验
  if (isSubmit) {
    const { valid } = await basicFormRef.value.validateForm();
    // 如果校验不通过，则不允许提交
    if (!valid) {
      loading.value = false;
      return;
    }
  }

  try {
    // 获取表单值
    const formValues =
      (await basicFormRef.value.getFormValues()) as CarReturnBillApi.CarReturnBill;
    // 合并表单值和其他数据
    const data = {
      ...formData.value,
      ...formValues,
    };

    id = await (isSubmit ? submitCarReturnBill(data) : saveCarReturnBill(data));

    message.success({
      content: $t('ui.actionMessage.operationSuccess'),
      key: 'action_key_msg',
    });

    // 保存后重新加载数据
    await loadData();
  } catch (error) {
    const errorMessage = error instanceof Error ? error.message : '保存失败';
    message.error({
      content: errorMessage,
      key: 'action_key_msg',
    });
    console.error('保存还车申请单失败:', error);
  } finally {
    loading.value = false;
  }
}

// 撤回
async function handleRevoke() {
  if (
    formData.value.processInstanceId !== undefined &&
    formData.value.processInstanceId !== null
  ) {
    loading.value = true;
    await cancelProcessInstanceByStartUser(
      formData.value.processInstanceId,
      '撤回',
    );
    message.success('撤回成功');
    await loadData();
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
      companyId: userStore.userInfo?.companyId,
      companyName: userStore.userInfo?.companyName,
      deptId: userStore.userInfo?.deptId,
      deptName: userStore.userInfo?.deptName,
      processStatus: BpmProcessInstanceStatus.NOT_START, // 草稿状态
      createTime: new Date(),
      attachments: [],
    };
    return;
  }

  // 加载数据
  loading.value = true;
  try {
    const data = await getCarReturnBill(id);
    // 扩展数据，添加显示需要的字段
    formData.value = {
      ...data,
    };
    // 如果有 isApproval prop，则以 prop 为准；否则根据流程状态判断
    readonly.value =
      props.isApproval === true
        ? props.isApproval
        : !BpmProcessInstanceStatusEditValue.includes(
            formData.value.processStatus as number,
          );

    // 设置表单值
    if (basicFormRef.value) {
      await basicFormRef.value.setFormValues(data);
    }
  } catch (error) {
    const errorMessage =
      error instanceof Error ? error.message : '获取还车申请单详情失败';
    message.error(errorMessage);
    console.error('获取还车申请单详情失败:', error);
  } finally {
    loading.value = false;

    // 数据加载完成后，刷新BasicForm组件数据
    nextTick(() => {
      basicFormRef.value?.refreshAllData();
    });
  }
}

// 处理车辆选择
function handleCarSelect(val: any) {
  if (basicFormRef.value && val && val.carNo && val.id) {
    basicFormRef.value.setFormValues({
      carNo: val.carNo,
      carId: val.id,
    });
  }
}

// 处理附件上传
function handleUploadAttachment() {
  if (attachmentListRef.value) {
    attachmentListRef.value.handleTriggerUpload();
  }
}

// 处理用车申请单选择
function handleApplySelect(val: any) {
  if (basicFormRef.value && val && val.billCode) {
    const values: Record<string, any> = { applyBill: val.billCode };

    // 自动填充车辆信息
    values.carNo = val.carNo;
    values.carId = val.carId;

    // 自动填充时间信息
    values.goTime = val.goTime;
    values.returnTime = val.returnTime;

    // 自动填充地点信息
    values.goArea = val.goArea;
    values.returnArea = val.returnArea;

    // 自动填充随行人信息
    values.passenger = val.passenger;

    // 自动填充用车事由
    values.cause = val.cause;

    basicFormRef.value.setFormValues(values);
  }
}

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
        billName: '还车申请单',
      }"
      :form-data="formData"
      :form-schema="formSchema"
      :disabled="readonly"
      @close="handleClose"
      @save="handleSaveAndSubmit(false)"
      @submit="handleSaveAndSubmit(true)"
      @revoke="handleRevoke"
      :hide-footer="props.isApproval"
    >
      <!-- 扩展插槽，用于明细表格等 -->
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

    <!-- 车辆选择弹窗 -->
    <CarSelectModal ref="modalRef" @select="handleCarSelect" />
    <!-- 用车申请单选择弹窗 -->
    <CarApplySelectModal ref="applyModalRef" @select="handleApplySelect" />
  </Loading>
</template>

<style scoped>
/* 业务页面样式已封装到BasicForm组件中，无需重复定义 */
</style>
