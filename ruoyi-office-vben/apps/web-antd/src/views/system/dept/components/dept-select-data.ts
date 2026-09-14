import type { SystemDeptApi } from '#/api/system/dept';

import { ref } from 'vue';

import { handleTree } from '@vben/utils';

import { getDeptList } from '#/api/system/dept';

/** 部门树数据 */
const treeData = ref<
  (SystemDeptApi.Dept & { children?: SystemDeptApi.Dept[] })[]
>([]);

/** 扁平化的部门列表（用于查找） */
const flatDeptList = ref<SystemDeptApi.Dept[]>([]);

/** 加载部门树数据 */
export async function loadTreeData() {
  try {
    const data = await getDeptList();
    flatDeptList.value = data || [];
    treeData.value = handleTree(data, 'id', 'parentId') || [];
  } catch (error) {
    console.error('加载部门数据失败', error);
    treeData.value = [];
    flatDeptList.value = [];
  }
}

/** 查找部门所属的公司信息（名称和ID） */
export function findCompany(deptId: number): {
  id: number | undefined;
  name: string;
} {
  if (!deptId) {
    return { name: '', id: undefined };
  }

  // 查找当前部门
  const dept = flatDeptList.value.find((d) => d.id === deptId);
  if (!dept) {
    return { name: '', id: undefined };
  }

  // 如果当前部门就是公司，返回公司信息
  if (dept.orgType === '1') {
    return { name: dept.name, id: dept.id };
  }

  // 递归查找父级公司
  function findParentCompany(parentId?: number): {
    id: number | undefined;
    name: string;
  } {
    if (!parentId) {
      return { name: '', id: undefined };
    }

    const parent = flatDeptList.value.find((d) => d.id === parentId);
    if (!parent) {
      return { name: '', id: undefined };
    }

    // 如果父级是公司，返回公司信息
    if (parent.orgType === '1') {
      return { name: parent.name, id: parent.id };
    }

    // 继续向上查找
    return findParentCompany(parent.parentId);
  }

  return findParentCompany(dept.parentId);
}

/** 查找部门所属的公司名称（便捷方法） */
export function findCompanyName(deptId: number): string {
  return findCompany(deptId).name;
}

/** 查找部门所属的公司ID（便捷方法） */
export function findCompanyId(deptId: number): number | undefined {
  return findCompany(deptId).id;
}

export function useDeptSelectData() {
  return {
    treeData,
    loadTreeData,
    findCompany,
    findCompanyName,
    findCompanyId,
  };
}
