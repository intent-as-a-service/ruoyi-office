<script lang="ts" setup>
import type { VbenFormSchema } from '#/adapter/form';
import type { EmployeeTransferBillApi } from '#/api/hrm/employee-transfer';

import { nextTick, onMounted, ref, shallowRef, watch } from 'vue';
import { useRoute } from 'vue-router';

import { Loading } from '@vben/common-ui';
import {
  BpmProcessInstanceStatus,
  BpmProcessInstanceStatusEditValue,
} from '@vben/constants';
import { useTabs } from '@vben/hooks';
import { useUserStore } from '@vben/stores';

import { Button, message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import { withdrawProcessToStart } from '#/api/bpm/task';
import {
  getEmployeeTransferBill,
  saveEmployeeTransferBill,
  submitEmployeeTransferBill,
} from '#/api/hrm/employee-transfer';
import { AttachmentList } from '#/components/attachment-list';
import { BasicForm, CardContainer } from '#/components/basic-form';
import { $t } from '#/locales';
import EmployeeSelectModal from '#/views/hrm/employee/components/employee-select-modal.vue';
import { DeptSelectModal } from '#/views/system/dept/components';

import { useFormSchema, useTransferFormSchema } from './data';

defineOptions({ name: 'HrmEmployeeTransferBillInfo' });

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

const formData = ref<Partial<EmployeeTransferBillApi.EmployeeTransferBill>>({});

const readonly = ref(false);
const loading = ref(false);

// BasicForm组件引用
const basicFormRef = ref();

// 附件列表引用
const attachmentListRef = ref();

// 员工选择弹窗引用
const employeeSelectModalRef = ref<InstanceType<
  typeof EmployeeSelectModal
> | null>(null);

// 部门选择弹窗引用
const deptSelectModalRef = ref<InstanceType<typeof DeptSelectModal>>();

// 表单schema - 使用shallowRef避免深度响应式
const formSchema = shallowRef<VbenFormSchema[]>([]);

// 初始化调动信息表单
const [TransferForm, transferFormApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
    },
    formItemClass: 'col-span-1',
    labelWidth: 120,
    disabled: readonly.value,
  },
  wrapperClass: 'grid grid-cols-2 gap-4',
  layout: 'horizontal',
  schema: useTransferFormSchema(deptSelectModalRef, readonly),
  showDefaultActions: false,
});

// 初始化表单schema
function initFormSchema() {
  formSchema.value = useFormSchema(employeeSelectModalRef, readonly);
}

// 更新调动信息表单的schema（当readonly变化时）
function updateTransferFormSchema() {
  if (transferFormApi) {
    const transferSchema = useTransferFormSchema(deptSelectModalRef, readonly);
    // 更新每个字段的disabled状态
    const updatedSchema = transferSchema.map((schema) => {
      const componentProps = schema.componentProps || {};
      // 如果字段有自定义的disabled函数，则优先使用
      const hasCustomDisabled =
        componentProps &&
        typeof componentProps === 'object' &&
        'disabled' in componentProps &&
        typeof componentProps.disabled === 'function';

      return {
        ...schema,
        componentProps: {
          ...componentProps,
          disabled: hasCustomDisabled
            ? componentProps.disabled()
            : readonly.value,
        },
      };
    });
    transferFormApi.updateSchema(updatedSchema);
  }
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
    const { valid: basicValid } = await basicFormRef.value.validateForm();
    const transferValid = await transferFormApi.validate();

    // 如果校验不通过，则不允许提交
    if (!basicValid || !transferValid.valid) {
      loading.value = false;
      return;
    }
  }

  try {
    // 获取表单值 - 保存时不进行校验
    const formValues = isSubmit
      ? ((await basicFormRef.value.getFormValues()) as EmployeeTransferBillApi.EmployeeTransferBill)
      : ((await basicFormRef.value.getFormValues(
          false,
        )) as EmployeeTransferBillApi.EmployeeTransferBill);

    // 获取调动信息表单的值
    const transferValues = await transferFormApi.getValues();

    // 合并表单值和其他数据
    const data = {
      ...formData.value,
      ...formValues,
      ...transferValues,
    };

    id = await (isSubmit
      ? submitEmployeeTransferBill(data)
      : saveEmployeeTransferBill(data));

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
      // 撤回后重新加载数据，readonly状态会自动更新
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
      createTime: new Date(),
      attachments: [],
    };
    return;
  }

  // 加载数据
  loading.value = true;
  try {
    const data = await getEmployeeTransferBill(id);
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

    // 更新调动信息表单的schema（因为readonly状态可能变化）
    updateTransferFormSchema();

    // 设置表单值
    if (basicFormRef.value) {
      await basicFormRef.value.setFormValues(data);
    }

    // 设置调动信息表单值
    await transferFormApi.setValues(data);
  } catch (error) {
    const errorMessage =
      error instanceof Error ? error.message : '获取人事调动申请单详情失败';
    message.error(errorMessage);
    console.error('获取人事调动申请单详情失败:', error);
  } finally {
    loading.value = false;

    // 数据加载完成后，刷新BasicForm组件数据
    nextTick(() => {
      basicFormRef.value?.refreshAllData();
    });
  }
}

// 处理员工选择
function handleEmployeeSelect(employee: any) {
  if (employee) {
    // 从员工档案中读取信息并填充表单
    const employeeData = {
      employeeId: employee.id,
      employeeNo: employee.employeeNo,
      name: employee.name,
      sex: employee.sex,
      mobile: employee.mobile,
      empDeptId: employee.deptId,
      empDeptName: employee.deptName || '',
      empCompanyId: employee.companyId,
      empCompanyName: employee.companyName || '',
      jobPost: employee.jobPost,
      jobPosition: employee.jobPosition,
      employeeStatus: employee.employeeStatus,
      // 设置原职位、原职务、原部门、原公司
      originalJobPost: employee.jobPost,
      originalJobPosition: employee.jobPosition,
      originalDeptId: employee.deptId,
      originalDeptName: employee.deptName || '',
      originalCompanyId: employee.companyId,
      originalCompanyName: employee.companyName || '',
    };

    // 更新基本信息表单（BasicForm）
    if (basicFormRef.value) {
      basicFormRef.value.setFormValues(employeeData);
    }

    // 更新调动信息表单（TransferForm）
    transferFormApi.setValues(employeeData);

    // 同时更新formData
    Object.assign(formData.value, employeeData);
  }
}

// 处理部门选择
function handleDeptSelect(dept: any) {
  if (dept) {
    const deptData = {
      newDeptId: dept.id,
      newDeptName: dept.name,
      newCompanyId: dept.companyId,
      newCompanyName: dept.companyName || '',
    };

    // 更新基本信息表单（BasicForm）
    if (basicFormRef.value) {
      basicFormRef.value.setFormValues(deptData);
    }

    // 更新调动信息表单（TransferForm）
    transferFormApi.setValues(deptData);

    // 同时更新formData
    Object.assign(formData.value, deptData);
  }
}

// 暴露方法给父组件调用
defineExpose({
  loadData,
  handleSaveAndSubmit,
});

// ========== 附件相关操作 ==========
function handleUploadAttachment() {
  if (attachmentListRef.value) {
    attachmentListRef.value.handleTriggerUpload();
  }
}

// 监听readonly变化，更新表单的schema
watch(
  readonly,
  () => {
    initFormSchema();
    updateTransferFormSchema();
  },
  { immediate: false },
);

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
        billName: '人事调动申请单',
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
      <!-- 扩展插槽，用于调动信息等扩展区域 -->
      <template #form-extension>
        <!-- 调动信息 -->
        <CardContainer title="调动信息">
          <TransferForm />
        </CardContainer>

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
    <!-- 员工选择弹窗 -->
    <EmployeeSelectModal
      ref="employeeSelectModalRef"
      :exclude-employee-status-list="[6, 7]"
      @select="handleEmployeeSelect"
    />
    <!-- 部门选择弹窗 -->
    <DeptSelectModal ref="deptSelectModalRef" @select="handleDeptSelect" />
  </Loading>
</template>

<style scoped>
/* 业务页面样式已封装到BasicForm组件中，无需重复定义 */
</style>
