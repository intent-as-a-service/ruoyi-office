<script setup lang="ts">
import type { FileApi, FileShareReq, FileShareTarget } from '#/api/oa/file';
import type { SystemDeptApi } from '#/api/system/dept';
import type { SystemUserApi } from '#/api/system/user';

import { reactive, ref, watch, withDefaults } from 'vue';

import { IconifyIcon } from '@vben/icons';

import {
  Button,
  Checkbox,
  Form,
  FormItem,
  Input,
  message,
  Modal,
  Radio,
  RadioGroup,
  Select,
  Tag,
  TreeSelect,
} from 'ant-design-vue';

import { getFileShareInfo, shareFile, unshareFile } from '#/api/oa/file';
import { getDeptList } from '#/api/system/dept';
import { getSimpleUserList } from '#/api/system/user';

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  fileInfo: null,
});

const emit = defineEmits<Emits>();

const { TextArea } = Input;

interface Props {
  visible?: boolean;
  fileInfo?: FileApi.FileInfo | null;
}

interface Emits {
  (e: 'update:visible', visible: boolean): void;
  (e: 'success'): void;
}

const formRef = ref();
const targetLoading = ref(false);
const userOptions = ref<Array<{ label: string; value: number }>>([]);
const deptTreeData = ref<
  Array<{ children?: any[]; key: number; title: string; value: number }>
>([]);
const shareList = ref<any[]>([]);

const formData = reactive({
  shareType: 0, // 0人员 1组织
  targets: [] as number[],
  permission: 0, // 0仅查看 1可管理
  inheritPermission: true,
  remark: '',
});

const rules = {
  shareType: [{ required: true, message: '请选择分享类型' }],
  targets: [{ required: true, message: '请选择分享目标' }],
  permission: [{ required: true, message: '请选择权限' }],
};

// 监听弹窗显示状态
watch(
  () => props.visible,
  (visible) => {
    if (visible && props.fileInfo) {
      loadShareInfo();
      loadTargetOptions();
    } else {
      resetForm();
    }
  },
);

// 重置表单
function resetForm() {
  formData.shareType = 0;
  formData.targets = [];
  formData.permission = 0;
  formData.inheritPermission = true;
  formData.remark = '';
  shareList.value = [];
  userOptions.value = [];
  deptTreeData.value = [];
}

// 加载分享信息
async function loadShareInfo() {
  if (!props.fileInfo?.id) return;

  try {
    const data = await getFileShareInfo(props.fileInfo.id);
    shareList.value = data.shareTargets || [];
  } catch (error) {
    console.error('加载分享信息失败:', error);
  }
}

// 加载目标选项（人员或组织）
async function loadTargetOptions() {
  targetLoading.value = true;
  try {
    formData.shareType === 0
      ? await loadUserOptions() // 加载用户列表
      : await loadDeptTree(); // 加载部门树
  } catch (error) {
    console.error('加载目标选项失败:', error);
    message.error('加载选项失败');
  } finally {
    targetLoading.value = false;
  }
}

// 加载用户选项
async function loadUserOptions() {
  const users = await getSimpleUserList();
  userOptions.value = users.map((user: SystemUserApi.User) => ({
    label: `${user.nickname}(${user.id})`,
    value: user.id!,
  }));
}

// 加载部门树
async function loadDeptTree() {
  const depts = await getDeptList();
  deptTreeData.value = buildDeptTree(depts);
}

// 构建部门树结构
function buildDeptTree(depts: SystemDeptApi.Dept[]): any[] {
  const deptMap = new Map<number, any>();
  const rootDepts: any[] = [];

  // 首先创建所有部门节点
  depts.forEach((dept: SystemDeptApi.Dept) => {
    if (dept.status === 0) {
      // 只显示启用的部门
      const node = {
        title: dept.name,
        value: dept.id!,
        key: dept.id!,
        children: [],
        parentId: dept.parentId,
      };
      deptMap.set(dept.id!, node);
    }
  });

  // 构建树形结构
  deptMap.forEach((node) => {
    if (node.parentId && node.parentId !== 0) {
      const parent = deptMap.get(node.parentId);
      if (parent) {
        parent.children.push(node);
      } else {
        // 如果父节点不存在（可能被禁用），则作为根节点
        rootDepts.push(node);
      }
    } else {
      // 根节点
      rootDepts.push(node);
    }
  });

  // 清理空的children数组
  const cleanEmptyChildren = (nodes: any[]) => {
    nodes.forEach((node) => {
      if (node.children && node.children.length === 0) {
        delete node.children;
      } else if (node.children) {
        cleanEmptyChildren(node.children);
      }
    });
  };

  cleanEmptyChildren(rootDepts);
  return rootDepts;
}

// 分享类型改变
function handleShareTypeChange() {
  formData.targets = [];
  loadTargetOptions();
}

// 用户搜索
function handleUserSearch(value: string) {
  if (!value || value.length < 2) {
    loadUserOptions();
    return;
  }

  // 过滤用户选项
  const allUsers = userOptions.value;
  userOptions.value = allUsers.filter((user) =>
    user.label.toLowerCase().includes(value.toLowerCase()),
  );
}

// 目标选择改变
function handleTargetChange() {
  // 可以在这里处理选择变化
}

// 移除分享
async function handleRemoveShare(item: any) {
  if (!props.fileInfo?.id) return;

  try {
    await unshareFile(props.fileInfo.id, item.shareType, item.targetId);
    message.success('移除分享成功');
    loadShareInfo();
  } catch (error) {
    console.error('移除分享失败:', error);
    message.error('移除分享失败');
  }
}

// 提交分享
async function handleSubmit() {
  if (!props.fileInfo?.id) return;

  try {
    await formRef.value.validate();

    const shareTargets: FileShareTarget[] = formData.targets.map((targetId) => {
      let targetName = '';

      if (formData.shareType === 0) {
        // 人员分享
        const user = userOptions.value.find((opt) => opt.value === targetId);
        targetName = user?.label || '';
      } else {
        // 组织分享
        targetName = findDeptNameById(deptTreeData.value, targetId);
      }

      return {
        shareType: formData.shareType,
        targetId,
        targetName,
        permission: formData.permission,
      };
    });

    const shareReq: FileShareReq = {
      fileId: props.fileInfo.id,
      shareTargets,
      inheritPermission: formData.inheritPermission,
      remark: formData.remark,
    };

    await shareFile(shareReq);
    message.success('分享成功');
    emit('success');
    handleCancel();
  } catch (error: any) {
    console.error('分享失败:', error);
    if (error?.errorFields) {
      // 表单验证错误
      return;
    }
    message.error('分享失败');
  }
}

// 根据ID查找部门名称
function findDeptNameById(treeData: any[], targetId: number): string {
  for (const node of treeData) {
    if (node.value === targetId) {
      return node.title;
    }
    if (node.children) {
      const found = findDeptNameById(node.children, targetId);
      if (found) return found;
    }
  }
  return '';
}

// 取消分享
function handleCancel() {
  emit('update:visible', false);
}
</script>

<template>
  <Modal
    :open="visible"
    title="文件分享"
    :width="600"
    @ok="handleSubmit"
    @cancel="handleCancel"
  >
    <div class="file-share-modal">
      <!-- 文件信息 -->
      <div class="file-info mb-4">
        <div class="flex items-center">
          <IconifyIcon
            :icon="
              fileInfo?.fileType === 0
                ? 'ant-design:folder-filled'
                : 'ant-design:file-text-filled'
            "
            class="mr-2 text-lg"
          />
          <span class="font-medium">{{ fileInfo?.fileName }}</span>
        </div>
      </div>

      <!-- 分享设置 -->
      <Form ref="formRef" :model="formData" :rules="rules" layout="vertical">
        <FormItem label="分享类型" name="shareType" required>
          <RadioGroup
            v-model:value="formData.shareType"
            @change="handleShareTypeChange"
          >
            <Radio :value="0">按人员分享</Radio>
            <Radio :value="1">按组织分享</Radio>
          </RadioGroup>
        </FormItem>

        <FormItem
          :label="formData.shareType === 0 ? '选择人员' : '选择组织'"
          name="targets"
          required
        >
          <Select
            v-if="formData.shareType === 0"
            v-model:value="formData.targets"
            mode="multiple"
            placeholder="请选择要分享的人员"
            :options="userOptions"
            :loading="targetLoading"
            @search="handleUserSearch"
            @change="handleTargetChange"
            show-search
            :filter-option="false"
          />
          <TreeSelect
            v-else
            v-model:value="formData.targets"
            multiple
            placeholder="请选择要分享的组织"
            :tree-data="deptTreeData"
            :loading="targetLoading"
            tree-checkable
            show-checked-strategy="SHOW_PARENT"
            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
            allow-clear
            tree-default-expand-all
          />
        </FormItem>

        <FormItem label="权限设置" name="permission" required>
          <RadioGroup v-model:value="formData.permission">
            <Radio :value="0">仅查看</Radio>
            <Radio :value="1">可管理</Radio>
          </RadioGroup>
        </FormItem>

        <FormItem v-if="fileInfo?.fileType === 0" name="inheritPermission">
          <Checkbox v-model:checked="formData.inheritPermission">
            权限继承到子文件夹
          </Checkbox>
        </FormItem>

        <FormItem label="备注" name="remark">
          <TextArea
            v-model:value="formData.remark"
            placeholder="请输入分享备注（可选）"
            :rows="3"
          />
        </FormItem>
      </Form>

      <!-- 已分享列表 -->
      <div v-if="shareList.length > 0" class="shared-list mt-4">
        <div class="mb-2 text-sm font-medium">已分享给：</div>
        <div class="space-y-2">
          <div
            v-for="item in shareList"
            :key="`${item.shareType}-${item.targetId}`"
            class="flex items-center justify-between rounded bg-gray-50 p-2"
          >
            <div class="flex items-center">
              <IconifyIcon
                :icon="
                  item.shareType === 0
                    ? 'ant-design:user-outlined'
                    : 'ant-design:team-outlined'
                "
                class="mr-2"
              />
              <span>{{ item.targetName }}</span>
              <Tag
                :color="item.permission === 0 ? 'blue' : 'green'"
                class="ml-2"
              >
                {{ item.permissionName }}
              </Tag>
            </div>
            <Button
              type="link"
              size="small"
              danger
              @click="handleRemoveShare(item)"
            >
              移除
            </Button>
          </div>
        </div>
      </div>
    </div>
  </Modal>
</template>

<style scoped>
.file-share-modal {
  .file-info {
    padding: 12px;
    background-color: #f5f5f5;
    border-radius: 6px;
  }

  .shared-list {
    padding-top: 16px;
    border-top: 1px solid #f0f0f0;
  }
}
</style>
