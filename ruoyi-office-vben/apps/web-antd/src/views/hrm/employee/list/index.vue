<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EmployeeArchiveApi } from '#/api/hrm/employee';

import { onActivated, ref } from 'vue';
import { useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';
import { downloadFileFromBlobPart, isEmpty } from '@vben/utils';

import { message } from 'ant-design-vue';
import type { SystemDeptApi } from '#/api/system/dept';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteEmployeeArchive,
  deleteEmployeeArchiveList,
  exportEmployeeArchiveExcel,
  getEmployeeArchivePage,
  generateUserForEmployee,
  batchGenerateUserForEmployee,
} from '#/api/hrm/employee';
import { $t } from '#/locales';

import { DeptSelectModal } from '#/views/system/dept/components';

import { useGridColumns, useGridFormSchema } from './data';

defineOptions({ name: 'HrmEmployeeArchiveList' });

const router = useRouter();

// 选中的记录ID列表
const checkedIds = ref<number[]>([]);

// 部门选择弹窗引用
const deptSelectModalRef = ref<InstanceType<typeof DeptSelectModal>>();

/** 刷新表格 */
function onRefresh() {
  gridApi.query();
}

/** 新增员工档案 */
function handleCreate() {
  router.push({
    path: '/hrm/employee/employee-archive-info',
    query: {
      t: Date.now(), // 添加时间戳作为随机串
    },
  });
}

/** 编辑员工档案 */
function handleEdit(row: EmployeeArchiveApi.EmployeeArchive) {
  router.push({
    path: '/hrm/employee/employee-archive-info',
    query: {
      id: row.id,
      t: Date.now(),
    },
  });
}

/** 删除员工档案 */
async function handleDelete(row: EmployeeArchiveApi.EmployeeArchive) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.name]),
    key: 'action_key_msg',
  });
  try {
    await deleteEmployeeArchive(row.id as number);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess', [row.name]),
      key: 'action_key_msg',
    });
    onRefresh();
  } finally {
    hideLoading();
  }
}

/** 批量删除员工档案 */
async function handleDeleteBatch() {
  if (isEmpty(checkedIds.value)) {
    message.warning('请先选择要删除的记录');
    return;
  }

  const hideLoading = message.loading('正在删除...', 0);
  try {
    await deleteEmployeeArchiveList(checkedIds.value);
    message.success('批量删除成功');
    checkedIds.value = [];
    onRefresh();
  } catch (error) {
    message.error('批量删除失败');
  } finally {
    hideLoading();
  }
}

/** 导出 Excel */
async function handleExport() {
  const hideLoading = message.loading('正在导出...', 0);
  try {
    const data = await exportEmployeeArchiveExcel(await gridApi.formApi.getValues());
    downloadFileFromBlobPart({ fileName: '员工档案.xls', source: data });
    message.success('导出成功');
  } catch (error) {
    message.error('导出失败');
  } finally {
    hideLoading();
  }
}

/** 生成用户 */
async function handleGenerateUser(row: EmployeeArchiveApi.EmployeeArchive) {
  if (row.userGenerated) {
    message.warning('该员工已生成用户，无需重复生成');
    return;
  }
  const hideLoading = message.loading('正在生成用户...', 0);
  try {
    await generateUserForEmployee(row.id as number);
    message.success('生成用户成功');
    onRefresh();
  } catch (error) {
    message.error('生成用户失败');
  } finally {
    hideLoading();
  }
}

/** 批量生成用户 */
async function handleBatchGenerateUser() {
  if (isEmpty(checkedIds.value)) {
    message.warning('请先选择要生成用户的记录');
    return;
  }
  const hideLoading = message.loading('正在批量生成用户...', 0);
  try {
    await batchGenerateUserForEmployee(checkedIds.value);
    message.success('批量生成用户成功');
    checkedIds.value = [];
    onRefresh();
  } catch (error) {
    message.error('批量生成用户失败');
  } finally {
    hideLoading();
  }
}

/** 复选框变化事件 */
function handleRowCheckboxChange() {
  const records = gridApi.grid.getCheckboxRecords();
  checkedIds.value = records.map((item: EmployeeArchiveApi.EmployeeArchive) => item.id as number);
}

/** 部门选择处理 */
function handleDeptSelect(dept: SystemDeptApi.Dept & { companyName?: string }) {
  // 设置部门ID和部门名称
  gridApi.formApi.setFieldValue('deptId', dept.id);
  gridApi.formApi.setFieldValue('deptName', dept.name);
}

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useGridFormSchema(deptSelectModalRef),
    wrapperClass: 'grid-cols-4',
    collapsed: true,
  },
  gridOptions: {
    columns: useGridColumns(),
    height: 'auto',
    pagerConfig: {
      enabled: true,
    },
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) => {
          // 过滤掉空的 deptId，避免清空后仍然传递参数
          const queryParams = { ...formValues };
          if (!queryParams.deptId || queryParams.deptId === '') {
            delete queryParams.deptId;
          }
          // 如果 deptName 为空，也删除 deptId
          if (!queryParams.deptName || queryParams.deptName === '') {
            delete queryParams.deptId;
            delete queryParams.deptName;
          }
          return await getEmployeeArchivePage({
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            ...queryParams,
          });
        },
      },
    },
    rowConfig: {
      keyField: 'id',
      isHover: true,
    },
    toolbarConfig: {
      refresh: { code: 'query' },
      search: true,
    },
  } as VxeTableGridOptions<EmployeeArchiveApi.EmployeeArchive>,
  gridEvents: {
    checkboxAll: handleRowCheckboxChange,
    checkboxChange: handleRowCheckboxChange,
  },
});

// 页签切换时自动刷新表格数据
onActivated(() => {
  onRefresh();
});
</script>

<template>
  <Page auto-content-height>
    <Grid table-title="员工档案列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: $t('ui.actionTitle.create'),
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['hrm:employee-archive:create'],
              onClick: handleCreate,
            },
            {
              label: $t('ui.actionTitle.export'),
              type: 'primary',
              icon: ACTION_ICON.DOWNLOAD,
              auth: ['hrm:employee-archive:export'],
              onClick: handleExport,
            },
            {
              label: '批量生成用户',
              type: 'primary',
              icon: ACTION_ICON.ADD,
              disabled: isEmpty(checkedIds),
              auth: ['hrm:employee-archive:create'],
              onClick: handleBatchGenerateUser,
            },
            {
              label: $t('ui.actionTitle.deleteBatch'),
              type: 'primary',
              danger: true,
              icon: ACTION_ICON.DELETE,
              disabled: isEmpty(checkedIds),
              auth: ['hrm:employee-archive:delete'],
              onClick: handleDeleteBatch,
            },
          ]"
        />
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('ui.actionTitle.edit'),
              icon: ACTION_ICON.EDIT,
              auth: ['hrm:employee-archive:update'],
              onClick: () => handleEdit(row),
            },
            {
              label: '生成用户',
              type: 'link',
              icon: ACTION_ICON.ADD,
              disabled: row.userGenerated,
              auth: ['hrm:employee-archive:create'],
              onClick: () => handleGenerateUser(row),
            },
            {
              label: $t('ui.actionTitle.delete'),
              type: 'link',
              danger: true,
              icon: ACTION_ICON.DELETE,
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.name]),
                confirm: () => handleDelete(row),
              },
              auth: ['hrm:employee-archive:delete'],
            },
          ]"
        />
      </template>
    </Grid>
    <!-- 部门选择弹窗 -->
    <DeptSelectModal ref="deptSelectModalRef" @select="handleDeptSelect" />
  </Page>
</template>

