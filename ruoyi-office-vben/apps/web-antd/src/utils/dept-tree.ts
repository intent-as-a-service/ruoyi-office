import { handleTree } from '@vben/utils';
import { getDeptList } from '#/api/system/dept';

/**
 * 获取指定公司下的所有部门（树形结构）
 * @param companyId 公司ID
 * @returns 部门树形数据
 */
export async function getCompanyDeptTree(companyId: number) {
  if (!companyId) {
    return [];
  }

  // 获取所有部门数据
  const allDepts = await getDeptList();
  
  // 递归函数：获取指定节点下的所有子节点（包括自身）
  function getNodeWithChildren(depts: any[], nodeId: number): any[] {
    const result: any[] = [];
    
    // 找到目标节点
    const targetNode = depts.find(dept => dept.id === nodeId);
    if (targetNode) {
      result.push(targetNode);
    }
    
    // 找到所有子节点
    const children = depts.filter(dept => dept.parentId === nodeId);
    for (const child of children) {
      result.push(...getNodeWithChildren(depts, child.id));
    }
    
    return result;
  }
  
  // 获取当前公司及其所有下级部门
  const companyDepts = getNodeWithChildren(allDepts, companyId);
  
  // 构建树形结构
  return handleTree(companyDepts);
}

/**
 * 获取当前用户公司下的所有部门（树形结构）
 * @param includeCompany 是否包含公司本身，默认为true
 * @returns 部门树形数据
 */
export async function getCurrentUserCompanyDeptTree(includeCompany: boolean = true) {
  const { useUserStore } = await import('@vben/stores');
  const userStore = useUserStore();
  const currentCompanyId = userStore.userInfo?.companyId;
  
  if (!currentCompanyId) {
    return [];
  }
  
  if (includeCompany) {
    return getCompanyDeptTree(currentCompanyId);
  } else {
    // 只获取公司下的部门，不包含公司本身
    return getCompanyDeptTreeWithoutRoot(currentCompanyId);
  }
}

/**
 * 获取指定公司下的所有部门（不包含公司本身）
 * @param companyId 公司ID
 * @returns 部门树形数据
 */
export async function getCompanyDeptTreeWithoutRoot(companyId: number) {
  if (!companyId) {
    return [];
  }

  // 获取所有部门数据
  const allDepts = await getDeptList();
  
  // 递归函数：获取指定节点下的所有子节点（不包括自身）
  function getChildrenNodes(depts: any[], parentId: number): any[] {
    const result: any[] = [];
    
    // 找到所有直接子节点
    const children = depts.filter(dept => dept.parentId === parentId);
    
    for (const child of children) {
      result.push(child);
      // 递归获取子节点的子节点
      result.push(...getChildrenNodes(depts, child.id));
    }
    
    return result;
  }
  
  // 获取公司下的所有部门（不包含公司本身）
  const companyDepts = getChildrenNodes(allDepts, companyId);
  
  // 构建树形结构
  return handleTree(companyDepts);
}
