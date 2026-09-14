<script lang="ts" setup>
import type { CategoryApi } from '#/api/asset/category';

import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useTabs } from '@vben/hooks';
// import FormContent from './components/FormContent.vue';

import { message } from 'ant-design-vue';

import {
  createCategory,
  getCategory,
  updateCategory,
} from '#/api/asset/category';
import { NoFlowForm } from '#/components/basic-form';
import { $t } from '#/locales';

import FormContent from './components/FormContent.vue';

const route = useRoute();
const router = useRouter();

const { closeCurrentTab } = useTabs();

const formData = ref<
  Partial<CategoryApi.Category> & {
    applicantName?: string;
    applyDate?: string;
    billName?: string;
    billNo?: string;
    companyName?: string;
    createTime?: string;
    deptName?: string;
  }
>({});

const isEdit = computed(
  () => !!route.query.id && route.query.editType === 'edit',
);
const isView = computed(() => route.query.editType === 'view');
const getTitle = computed(() => {
  return formData.value?.id
    ? $t('ui.actionTitle.edit', ['资产类别'])
    : $t('ui.actionTitle.create', ['资产类别']);
});
const saving = ref(false);
const submitting = ref(false);
const loading = ref(false);

// FormContent组件引用
const formContentRef = ref();

// 返回列表页
function goBack() {
  router.push('/oa/car/carapply');
}

// 关闭按钮处理
function handleClose() {
  closeCurrentTab();
}

// 保存表单
async function handleSave() {
  if (!formContentRef.value) return;

  const { valid } = await formContentRef.value.validateForm();
  if (!valid) {
    return;
  }

  saving.value = true;
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.saving'),
    key: 'action_key_msg',
  });

  try {
    const data =
      (await formContentRef.value.getFormValues()) as CategoryApi.Category;
    if (isEdit.value) {
      data.id = Number(route.query.id);
      await updateCategory(data);
    } else {
      await createCategory(data);
    }

    message.success({
      content: $t('ui.actionMessage.operationSuccess'),
      key: 'action_key_msg',
    });

    // 保存后重新加载数据
    await loadData();
  } catch {
    message.error({
      content: '保存失败',
      key: 'action_key_msg',
    });
  } finally {
    saving.value = false;
    hideLoading();
  }
}

// 提交表单
async function handleSubmit() {
  if (!formContentRef.value) return;

  const { valid } = await formContentRef.value.validateForm();
  if (!valid) {
    return;
  }

  submitting.value = true;
  const hideLoading = message.loading({
    content: '提交中...',
    key: 'action_key_msg',
  });

  try {
    const data =
      (await formContentRef.value.getFormValues()) as CategoryApi.Category;
    if (isEdit.value) {
      data.id = Number(route.query.id);
      await updateCategory(data);
    } else {
      await createCategory(data);
    }

    message.success({
      content: '提交成功',
      key: 'action_key_msg',
    });
    goBack();
  } catch {
    message.error({
      content: '提交失败',
      key: 'action_key_msg',
    });
  } finally {
    submitting.value = false;
    hideLoading();
  }
}

// 加载数据
async function loadData() {
  const id = route.query.id;

  if (!id) {
    // 新建时设置默认值
    formData.value = {};
    return;
  }

  loading.value = true;
  try {
    const data = await getCategory(Number(id));
    // 设置表单值
    if (formContentRef.value) {
      await formContentRef.value.setFormValues(data);
    }
  } catch {
    message.error('获取资产类别详情失败');
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  loadData();
});
</script>

<template>
  <NoFlowForm
    :form-data="formData"
    :header-title="getTitle"
    @close="handleClose"
    @save="handleSave"
    @submit="handleSubmit"
  >
    <template #base-form>
      <FormContent
        ref="formContentRef"
        :form-data="formData"
        :disabled="isView"
      />
    </template>
  </NoFlowForm>
</template>

<style scoped></style>
