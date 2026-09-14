<script lang="ts" setup>
import type { EmployeeArchiveApi } from '#/api/hrm/employee';
import type { SystemDeptApi } from '#/api/system/dept';

import { computed, h, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';
import { useTabs } from '@vben/hooks';

import {
  Button,
  DatePicker,
  Input,
  message,
  Space,
  Table,
} from 'ant-design-vue';
import dayjs from 'dayjs';

import { useVbenForm } from '#/adapter/form';
import {
  createEmployeeArchive,
  getEmployeeArchive,
  updateEmployeeArchive,
} from '#/api/hrm/employee';
import { CardContainer } from '#/components/basic-form';
import { DeptSelectModal } from '#/views/system/dept/components';

import {
  useAvatarFormSchema,
  useBasicFormSchema,
  useWorkFormSchema,
} from './data';

defineOptions({ name: 'HrmEmployeeArchiveInfo' });

const route = useRoute();
const router = useRouter();
const { closeCurrentTab } = useTabs();

const formData = ref<Partial<EmployeeArchiveApi.EmployeeArchive>>({});
const readonly = ref(false);
const loading = ref(false);

// 部门选择弹窗引用
const deptSelectModalRef = ref<InstanceType<typeof DeptSelectModal>>();

// 工作经历列表
const workExperienceList = ref<EmployeeArchiveApi.EmployeeWorkExperience[]>([]);
// 教育经历列表
const educationList = ref<EmployeeArchiveApi.EmployeeEducation[]>([]);
// 家属信息列表
const familyList = ref<EmployeeArchiveApi.EmployeeFamily[]>([]);

// 工作经历表格列定义
const workExperienceColumns = [
  {
    title: '开始时间',
    dataIndex: 'startTime',
    width: 150,
    customRender: ({ text, index }: any) => {
      if (readonly.value) return text || '-';
      return h(DatePicker, {
        value: text ? dayjs(text) : null,
        format: 'YYYY-MM-DD',
        placeholder: '请选择开始时间',
        style: { width: '100%' },
        onChange: (date: any) => {
          if (workExperienceList.value[index]) {
            workExperienceList.value[index].startTime = date
              ? dayjs(date).format('YYYY-MM-DD')
              : undefined;
          }
        },
      } as any);
    },
  },
  {
    title: '截止时间',
    dataIndex: 'endTime',
    width: 150,
    customRender: ({ text, index }: any) => {
      if (readonly.value) return text || '-';
      return h(DatePicker, {
        value: text ? dayjs(text) : null,
        format: 'YYYY-MM-DD',
        placeholder: '请选择截止时间',
        style: { width: '100%' },
        onChange: (date: any) => {
          if (workExperienceList.value[index]) {
            workExperienceList.value[index].endTime = date
              ? dayjs(date).format('YYYY-MM-DD')
              : undefined;
          }
        },
      } as any);
    },
  },
  {
    title: '职务',
    dataIndex: 'jobPosition',
    width: 150,
    customRender: ({ text, index }: any) => {
      if (readonly.value) return text || '-';
      return h(Input, {
        value: text,
        placeholder: '请输入职务',
        onChange: (e: any) => {
          if (workExperienceList.value[index]) {
            workExperienceList.value[index].jobPosition = e.target.value;
          }
        },
      } as any);
    },
  },
  {
    title: '单位名称',
    dataIndex: 'companyName',
    customRender: ({ text, index }: any) => {
      if (readonly.value) return text || '-';
      return h(Input, {
        value: text,
        placeholder: '请输入单位名称',
        onChange: (e: any) => {
          if (workExperienceList.value[index]) {
            workExperienceList.value[index].companyName = e.target.value;
          }
        },
      } as any);
    },
  },
  {
    title: '操作',
    key: 'action',
    width: 100,
    customRender: ({ index }: any) => {
      if (readonly.value) return '-';
      return h(
        Button,
        {
          type: 'link',
          size: 'small',
          danger: true,
          onClick: () => handleDeleteWorkExperience(index),
        },
        () => '删除',
      );
    },
  },
];

// 教育经历表格列定义
const educationColumns = [
  {
    title: '开始时间',
    dataIndex: 'startTime',
    width: 150,
    customRender: ({ text, index }: any) => {
      if (readonly.value) return text || '-';
      return h(DatePicker, {
        value: text ? dayjs(text) : null,
        format: 'YYYY-MM-DD',
        placeholder: '请选择开始时间',
        style: { width: '100%' },
        onChange: (date: any) => {
          if (educationList.value[index]) {
            educationList.value[index].startTime = date
              ? dayjs(date).format('YYYY-MM-DD')
              : undefined;
          }
        },
      } as any);
    },
  },
  {
    title: '截止时间',
    dataIndex: 'endTime',
    width: 150,
    customRender: ({ text, index }: any) => {
      if (readonly.value) return text || '-';
      return h(DatePicker, {
        value: text ? dayjs(text) : null,
        format: 'YYYY-MM-DD',
        placeholder: '请选择截止时间',
        style: { width: '100%' },
        onChange: (date: any) => {
          if (educationList.value[index]) {
            educationList.value[index].endTime = date
              ? dayjs(date).format('YYYY-MM-DD')
              : undefined;
          }
        },
      } as any);
    },
  },
  {
    title: '专业',
    dataIndex: 'major',
    width: 150,
    customRender: ({ text, index }: any) => {
      if (readonly.value) return text || '-';
      return h(Input, {
        value: text,
        placeholder: '请输入专业',
        onChange: (e: any) => {
          if (educationList.value[index]) {
            educationList.value[index].major = e.target.value;
          }
        },
      } as any);
    },
  },
  {
    title: '学校名称',
    dataIndex: 'schoolName',
    customRender: ({ text, index }: any) => {
      if (readonly.value) return text || '-';
      return h(Input, {
        value: text,
        placeholder: '请输入学校名称',
        onChange: (e: any) => {
          if (educationList.value[index]) {
            educationList.value[index].schoolName = e.target.value;
          }
        },
      } as any);
    },
  },
  {
    title: '操作',
    key: 'action',
    width: 100,
    customRender: ({ index }: any) => {
      if (readonly.value) return '-';
      return h(
        Button,
        {
          type: 'link',
          size: 'small',
          danger: true,
          onClick: () => handleDeleteEducation(index),
        },
        () => '删除',
      );
    },
  },
];

// 家属信息表格列定义
const familyColumns = [
  {
    title: '姓名',
    dataIndex: 'name',
    width: 150,
    customRender: ({ text, index }: any) => {
      if (readonly.value) return text || '-';
      return h(Input, {
        value: text,
        placeholder: '请输入姓名',
        onChange: (e: any) => {
          if (familyList.value[index]) {
            familyList.value[index].name = e.target.value;
          }
        },
      } as any);
    },
  },
  {
    title: '关系',
    dataIndex: 'relationship',
    width: 150,
    customRender: ({ text, index }: any) => {
      if (readonly.value) return text || '-';
      return h(Input, {
        value: text,
        placeholder: '请输入关系',
        onChange: (e: any) => {
          if (familyList.value[index]) {
            familyList.value[index].relationship = e.target.value;
          }
        },
      } as any);
    },
  },
  {
    title: '联系电话',
    dataIndex: 'mobile',
    width: 150,
    customRender: ({ text, index }: any) => {
      if (readonly.value) return text || '-';
      return h(Input, {
        value: text,
        placeholder: '请输入联系电话',
        onChange: (e: any) => {
          if (familyList.value[index]) {
            familyList.value[index].mobile = e.target.value;
          }
        },
      } as any);
    },
  },
  {
    title: '工作单位',
    dataIndex: 'workUnit',
    customRender: ({ text, index }: any) => {
      if (readonly.value) return text || '-';
      return h(Input, {
        value: text,
        placeholder: '请输入工作单位',
        onChange: (e: any) => {
          if (familyList.value[index]) {
            familyList.value[index].workUnit = e.target.value;
          }
        },
      } as any);
    },
  },
  {
    title: '操作',
    key: 'action',
    width: 100,
    customRender: ({ index }: any) => {
      if (readonly.value) return '-';
      return h(
        Button,
        {
          type: 'link',
          size: 'small',
          danger: true,
          onClick: () => handleDeleteFamily(index),
        },
        () => '删除',
      );
    },
  },
];

// 初始化基本信息表单
const [BasicForm, basicFormApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
    },
    formItemClass: 'col-span-1',
    labelWidth: 120,
  },
  wrapperClass: 'grid grid-cols-2 gap-4',
  layout: 'horizontal',
  schema: useBasicFormSchema(!!formData.value.id),
  showDefaultActions: false,
});

// 初始化照片表单
const [AvatarForm, avatarFormApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
    },
    labelWidth: 80,
  },
  layout: 'vertical',
  schema: useAvatarFormSchema(),
  showDefaultActions: false,
});

// 初始化工作信息表单
const [WorkForm, workFormApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
    },
    formItemClass: 'col-span-1',
    labelWidth: 120,
  },
  wrapperClass: 'grid grid-cols-2 gap-4',
  layout: 'horizontal',
  schema: useWorkFormSchema(deptSelectModalRef, readonly),
  showDefaultActions: false,
});

const pageTitle = computed(() => {
  if (readonly.value) {
    return '查看员工档案';
  }
  return formData.value.id ? '编辑员工档案' : '新增员工档案';
});

/** 加载数据 */
async function loadData(newId?: string) {
  const id = newId || (route.query.id as string);
  if (!id) {
    return;
  }

  loading.value = true;
  try {
    const data = await getEmployeeArchive(Number(id));
    formData.value = data;

    // 后端返回的日期已经是 YYYY-MM-DD 格式（LocalDate），直接使用
    await basicFormApi.setValues(data);
    await avatarFormApi.setValues(data);
    await workFormApi.setValues(data);

    // 加载工作经历
    if (data.workExperienceList) {
      workExperienceList.value = data.workExperienceList.map((item) => ({
        ...item,
        startTime: item.startTime
          ? dayjs(item.startTime).format('YYYY-MM-DD')
          : '',
        endTime: item.endTime ? dayjs(item.endTime).format('YYYY-MM-DD') : '',
        jobPosition: item.jobPosition || '',
        companyName: item.companyName || '',
      }));
    }

    // 加载教育经历
    if (data.educationList) {
      educationList.value = data.educationList.map((item) => ({
        ...item,
        startTime: item.startTime
          ? dayjs(item.startTime).format('YYYY-MM-DD')
          : '',
        endTime: item.endTime ? dayjs(item.endTime).format('YYYY-MM-DD') : '',
        major: item.major || '',
        schoolName: item.schoolName || '',
      }));
    }

    // 加载家属信息
    if (data.familyList) {
      familyList.value = data.familyList;
    }
  } catch (error) {
    console.error('加载员工档案失败', error);
    message.error('加载员工档案失败');
  } finally {
    loading.value = false;
  }
}

/** 保存 */
async function handleSave() {
  // 验证所有表单
  const basicValid = await basicFormApi.validate();
  const avatarValid = await avatarFormApi.validate();
  const workValid = await workFormApi.validate();

  if (!basicValid.valid || !avatarValid.valid || !workValid.valid) {
    return;
  }

  loading.value = true;
  try {
    // 合并所有表单数据
    const basicValues = await basicFormApi.getValues();
    const avatarValues = await avatarFormApi.getValues();
    const workValues = await workFormApi.getValues();

    const values = {
      ...basicValues,
      ...avatarValues,
      ...workValues,
    } as EmployeeArchiveApi.EmployeeArchive;

    // 新增时，员工工号由后端自动生成，前端不传或传空
    if (!values.id && (!values.employeeNo || values.employeeNo.trim() === '')) {
      values.employeeNo = undefined;
    }

    // 处理日期字段：空值统一转换为 undefined，后端 LocalDate 会自动处理 YYYY-MM-DD 格式
    // 注意：表格中的 onChange 已直接设置为 undefined，这里只处理表单字段可能的空字符串情况
    if (!values.birthday || values.birthday === '') {
      values.birthday = undefined;
    }
    if (!values.entryDate || values.entryDate === '') {
      values.entryDate = undefined;
    }
    if (!values.formalDate || values.formalDate === '') {
      values.formalDate = undefined;
    }

    // 表格中的日期字段已在 onChange 中设置为 undefined，直接使用即可
    values.workExperienceList = workExperienceList.value;
    values.educationList = educationList.value;

    values.familyList = familyList.value;

    if (formData.value.id) {
      values.id = formData.value.id;
      const oldUserGenerated = formData.value.userGenerated;
      await updateEmployeeArchive(values);
      // 保存成功后重新加载数据
      await loadData();
      // 如果已生成用户，提示同步更新
      if (oldUserGenerated) {
        message.success('保存成功，并自动更新用户信息');
      } else {
        message.success('保存成功');
      }
    } else {
      const result = await createEmployeeArchive(values);
      message.success('新增成功');
      // 新增成功后，如果有返回ID，更新路由并加载数据
      if (result && typeof result === 'number') {
        formData.value.id = result;
        await loadData(result);
      }
    }
  } catch (error) {
    console.error('保存失败', error);
    message.error('保存失败');
  } finally {
    loading.value = false;
  }
}

/** 关闭 */
function handleClose() {
  closeCurrentTab();
  router.go(-1);
}

// ========== 工作经历相关操作 ==========
function handleAddWorkExperience() {
  const newItem: EmployeeArchiveApi.EmployeeWorkExperience = {
    startTime: '',
    endTime: '',
    jobPosition: '',
    companyName: '',
  };
  workExperienceList.value.push(newItem);
}

function handleDeleteWorkExperience(index: number) {
  workExperienceList.value.splice(index, 1);
}

// ========== 教育经历相关操作 ==========
function handleAddEducation() {
  const newItem: EmployeeArchiveApi.EmployeeEducation = {
    startTime: '',
    endTime: '',
    major: '',
    schoolName: '',
  };
  educationList.value.push(newItem);
}

function handleDeleteEducation(index: number) {
  educationList.value.splice(index, 1);
}

// ========== 家属信息相关操作 ==========
function handleAddFamily() {
  const newItem: EmployeeArchiveApi.EmployeeFamily = {
    name: '',
    relationship: '',
    mobile: '',
    workUnit: '',
  };
  familyList.value.push(newItem);
}

function handleDeleteFamily(index: number) {
  familyList.value.splice(index, 1);
}

/** 处理部门选择 */
function handleDeptSelect(
  dept: SystemDeptApi.Dept & { companyId?: number; companyName?: string },
) {
  // 设置部门ID、部门名称、公司ID和公司名称
  workFormApi.setFieldValue('deptId', dept.id);
  workFormApi.setFieldValue('deptName', dept.name);
  workFormApi.setFieldValue('companyId', dept.companyId);
  workFormApi.setFieldValue('companyName', dept.companyName || '');
}

// 监听 readonly 状态变化，更新表单的 disabled 状态
watch(
  readonly,
  (isReadonly) => {
    // 更新基本信息表单
    const basicSchema = useBasicFormSchema(!!formData.value.id);
    const updatedBasicSchema = basicSchema.map((item) => ({
      ...item,
      componentProps: {
        ...item.componentProps,
        disabled: isReadonly || item.fieldName === 'employeeNo', // 员工工号始终禁用
      },
    }));
    basicFormApi.updateSchema(updatedBasicSchema);

    // 更新照片表单
    const avatarSchema = useAvatarFormSchema();
    const updatedAvatarSchema = avatarSchema.map((item) => ({
      ...item,
      componentProps: {
        ...item.componentProps,
        disabled: isReadonly,
      },
    }));
    avatarFormApi.updateSchema(updatedAvatarSchema);

    // 更新工作信息表单
    const workSchema = useWorkFormSchema(deptSelectModalRef, readonly);
    const updatedWorkSchema = workSchema.map((item) => ({
      ...item,
      componentProps: {
        ...item.componentProps,
        disabled: isReadonly,
      },
    }));
    workFormApi.updateSchema(updatedWorkSchema);
  },
  { immediate: true },
);

onMounted(async () => {
  // 判断是否只读
  readonly.value = route.query.readonly === 'true';

  // 加载数据
  await loadData();
});
</script>

<template>
  <Page :loading="loading" :title="pageTitle" auto-content-height>
    <template #extra>
      <Space>
        <Button @click="handleClose">关闭</Button>
        <Button v-if="!readonly" type="primary" @click="handleSave">
          保存
        </Button>
      </Space>
    </template>

    <!-- 基本信息 -->
    <div class="mb-4 rounded-lg bg-white p-4 shadow-sm">
      <CardContainer title="基本信息">
        <div class="flex gap-6">
          <!-- 左侧表单区域 -->
          <div class="flex-1">
            <BasicForm />
          </div>
          <!-- 右侧照片区域 -->
          <div class="w-[160px]">
            <AvatarForm />
          </div>
        </div>
      </CardContainer>
    </div>

    <!-- 工作信息 -->
    <div class="mb-4 rounded-lg bg-white p-4 shadow-sm">
      <CardContainer title="工作信息">
        <WorkForm />
        <!-- 部门选择弹窗 -->
        <DeptSelectModal ref="deptSelectModalRef" @select="handleDeptSelect" />
      </CardContainer>
    </div>

    <!-- 工作经历 -->
    <div class="mb-4 rounded-lg bg-white p-4 shadow-sm">
      <CardContainer title="工作经历">
        <template #extra>
          <Button
            v-if="!readonly"
            type="primary"
            @click="handleAddWorkExperience"
          >
            {{ $t('ui.actionTitle.create') }}
          </Button>
        </template>
        <Table
          :columns="workExperienceColumns"
          :data-source="workExperienceList"
          :pagination="false"
          :row-key="(record, index) => record.id || `work_${index}`"
          size="small"
        />
      </CardContainer>
    </div>

    <!-- 教育经历 -->
    <div class="mb-4 rounded-lg bg-white p-4 shadow-sm">
      <CardContainer title="教育经历">
        <template #extra>
          <Button v-if="!readonly" type="primary" @click="handleAddEducation">
            {{ $t('ui.actionTitle.create') }}
          </Button>
        </template>
        <Table
          :columns="educationColumns"
          :data-source="educationList"
          :pagination="false"
          :row-key="(record, index) => record.id || `edu_${index}`"
          size="small"
        />
      </CardContainer>
    </div>

    <!-- 家属信息 -->
    <div class="mb-4 rounded-lg bg-white p-4 shadow-sm">
      <CardContainer title="家属信息">
        <template #extra>
          <Button v-if="!readonly" type="primary" @click="handleAddFamily">
            {{ $t('ui.actionTitle.create') }}
          </Button>
        </template>
        <Table
          :columns="familyColumns"
          :data-source="familyList"
          :pagination="false"
          :row-key="(record, index) => record.id || `family_${index}`"
          size="small"
        />
      </CardContainer>
    </div>
  </Page>
</template>

<style scoped></style>
