<script lang="ts" setup>
import type { SystemDeptApi } from '#/api/system/dept';

import { reactive, ref, watch } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { message, Tree } from 'ant-design-vue';

import { useDeptSelectData } from './dept-select-data';

/** 定义组件事件 */
const emit = defineEmits<{
  (
    e: 'select',
    dept: SystemDeptApi.Dept & { companyId?: number; companyName?: string },
  ): void;
}>();

const { treeData, loadTreeData, findCompany } = useDeptSelectData();

const formData = reactive({
  selectedDept: null as
    | null
    | (SystemDeptApi.Dept & { companyId?: number; companyName?: string }),
});

// 树选中的 keys（用于 v-model）
const selectedKeys = ref<number[]>([]);

// 同步 selectedDept 和 selectedKeys
watch(
  () => formData.selectedDept,
  (dept) => {
    selectedKeys.value = dept?.id ? [dept.id] : [];
  },
  { immediate: true },
);

/** 模态框实例 */
const [Modal, modalApi] = useVbenModal({
  title: '选择部门',
  class: 'w-2/3 max-w-3xl',
  async onConfirm() {
    return handleConfirm();
  },
  async onOpened() {
    await loadTreeData();
  },
});

/** 确认选择 */
async function handleConfirm() {
  if (!formData.selectedDept) {
    message.error('请选择部门');
    return false;
  }

  // 查找所属公司信息（名称和ID）
  const company = findCompany(formData.selectedDept.id!);
  const deptWithCompany = {
    ...formData.selectedDept,
    companyName: company.name,
    companyId: company.id,
  };

  emit('select', deptWithCompany);
  formData.selectedDept = null;
  selectedKeys.value = [];
  await modalApi.close();
  return true;
}

/** 树节点选择 */
function handleSelect(keys: any[]) {
  if (keys.length === 0) {
    formData.selectedDept = null;
    selectedKeys.value = [];
    return;
  }

  const selectedId = keys[0];
  const selectedNode = findDeptById(treeData.value, selectedId);

  if (selectedNode) {
    // 检查是否为公司类型，公司类型不可选
    if (selectedNode.orgType === '1') {
      message.warning('不能选择公司，请选择部门');
      formData.selectedDept = null;
      selectedKeys.value = [];
      return;
    }

    formData.selectedDept = selectedNode;
    selectedKeys.value = [selectedId];
  }
}

/** 递归查找部门 */
function findDeptById(
  nodes: (SystemDeptApi.Dept & { children?: SystemDeptApi.Dept[] })[],
  id: number,
): null | SystemDeptApi.Dept {
  for (const node of nodes) {
    if (node.id === id) {
      return node;
    }
    if (node.children) {
      const found = findDeptById(node.children, id);
      if (found) {
        return found;
      }
    }
  }
  return null;
}

/** 暴露modal API供外部调用 */
defineExpose({
  modalApi,
});
</script>

<template>
  <Modal>
    <div class="dept-select-container">
      <Tree
        v-model:selected-keys="selectedKeys"
        :tree-data="treeData as any"
        :field-names="{ children: 'children', title: 'name', key: 'id' }"
        :block-node="true"
        :show-line="{ showLeafIcon: false }"
        @select="handleSelect"
      >
        <template #title="{ orgType, name }">
          <span :class="{ 'text-gray-400': orgType === '1' }">
            {{ name }}
            <span v-if="orgType === '1'" class="ml-2 text-xs text-gray-400">
              (公司)
            </span>
          </span>
        </template>
      </Tree>
    </div>
  </Modal>
</template>

<style lang="scss" scoped>
.dept-select-container {
  min-height: 400px;
  max-height: 500px;
  padding: 16px;
  overflow-y: auto;
}
</style>
