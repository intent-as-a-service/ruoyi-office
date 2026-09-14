<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { EmployeeEntryBillApi } from '#/api/hrm/employee-entry';

import { onActivated, ref } from 'vue';
import { useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';
import { BpmProcessInstanceStatusEditValue } from '@vben/constants';
import { useUserStore } from '@vben/stores';
import { downloadFileFromBlobPart, isEmpty } from '@vben/utils';

import { message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteEmployeeEntryBill,
  deleteEmployeeEntryBillList,
  exportEmployeeEntryBill,
  getEmployeeEntryBillPage,
} from '#/api/hrm/employee-entry';
import { $t } from '#/locales';

import { useGridColumns, useGridFormSchema } from './data';

defineOptions({ name: 'HrmEmployeeEntryBillList' });
const userStore = useUserStore();
const router = useRouter();

/** 刷新表格 */
function onRefresh() {
  gridApi.query();
}

/** 新增员工入职申请单 */
function handleCreate() {
  router.push({
    path: '/hrm/employee-relation/entry-info',
    query: {
      t: Date.now(), // 添加时间戳作为随机串
    },
  });
}

/** 删除员工入职申请单 */
async function handleDelete(row: EmployeeEntryBillApi.EmployeeEntryBill) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.id]),
    key: 'action_key_msg',
  });
  try {
    await deleteEmployeeEntryBill(row.id as number);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess', [row.id]),
      key: 'action_key_msg',
    });
    onRefresh();
  } finally {
    hideLoading();
  }
}

/** 批量删除员工入职申请单 */
async function handleDeleteBatch() {
  // 检查选中的记录是否都可以删除
  const checkedRecords = gridApi.grid.getCheckboxRecords();
  const notAllowedRecords = checkedRecords.filter(
    (record: EmployeeEntryBillApi.EmployeeEntryBill) =>
      !BpmProcessInstanceStatusEditValue.includes(
        record.processStatus as number,
      ),
  );

  if (notAllowedRecords.length > 0) {
    const billCodes = notAllowedRecords
      .map(
        (record: EmployeeEntryBillApi.EmployeeEntryBill) =>
          record.billCode || record.id,
      )
      .join(', ');
    message.warning(`以下单据不允许删除：${billCodes}`);
    return;
  }

  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting'),
    key: 'action_key_msg',
  });
  try {
    await deleteEmployeeEntryBillList(checkedIds.value);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess'),
      key: 'action_key_msg',
    });
    onRefresh();
    checkedIds.value = [];
  } finally {
    hideLoading();
  }
}

const checkedIds = ref<number[]>([]);
function handleRowCheckboxChange({
  records,
}: {
  records: EmployeeEntryBillApi.EmployeeEntryBill[];
}) {
  checkedIds.value = records
    .map((item) => item.id!)
    .filter((id): id is number => id !== undefined);
}

/** 导出表格 */
async function handleExport() {
  const data = await exportEmployeeEntryBill(await gridApi.formApi.getValues());
  downloadFileFromBlobPart({ fileName: '员工入职申请单.xls', source: data });
}

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useGridFormSchema(),
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
          return await getEmployeeEntryBillPage({
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            ...formValues,
            companyId: userStore.userInfo?.companyId,
            creator: userStore.userInfo?.id,
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
  } as VxeTableGridOptions<EmployeeEntryBillApi.EmployeeEntryBill>,
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
    <Grid table-title="员工入职申请单列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: $t('ui.actionTitle.create'),
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['hrm:employee-entry-bill:create'],
              onClick: handleCreate,
            },
            {
              label: $t('ui.actionTitle.export'),
              type: 'primary',
              icon: ACTION_ICON.DOWNLOAD,
              auth: ['hrm:employee-entry-bill:export'],
              onClick: handleExport,
            },
            {
              label: $t('ui.actionTitle.deleteBatch'),
              type: 'primary',
              danger: true,
              icon: ACTION_ICON.DELETE,
              disabled: isEmpty(checkedIds),
              auth: ['hrm:employee-entry-bill:delete'],
              onClick: handleDeleteBatch,
            },
          ]"
        />
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              ifShow: () =>
                BpmProcessInstanceStatusEditValue.includes(
                  row.processStatus as number,
                ),
              auth: ['hrm:employee-entry-bill:delete'],
              popConfirm: {
                title: $t('ui.actionMessage.deleteConfirm', [row.billCode]),
                confirm: handleDelete.bind(null, row),
              },
            },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              ifShow: () =>
                !BpmProcessInstanceStatusEditValue.includes(
                  row.processStatus as number,
                ),
              disabled: true,
              auth: ['hrm:employee-entry-bill:delete'],
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
