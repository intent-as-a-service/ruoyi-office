<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { CarReturnBillApi } from '#/api/oa/car/carreturn';

import { onActivated, ref } from 'vue';
import { useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';
import { BpmProcessInstanceStatusEditValue } from '@vben/constants';
import { useUserStore } from '@vben/stores';
import { downloadFileFromBlobPart, isEmpty } from '@vben/utils';

import { message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteCarReturnBill,
  deleteCarReturnBillList,
  exportCarReturnBill,
  getCarReturnBillPage,
} from '#/api/oa/car/carreturn';
import { $t } from '#/locales';

import { CarSelectModal } from '../../components';
import { useGridColumns, useGridFormSchema } from './data';

const userStore = useUserStore();
const router = useRouter();

// 车辆选择弹窗引用
const modalRef = ref<InstanceType<typeof CarSelectModal>>();

/** 刷新表格 */
function onRefresh() {
  gridApi.query();
}

/** 新增还车申请单 */
function handleCreate() {
  router.push({
    path: '/oa/car/car-return-info',
    query: {
      t: Date.now(), // 添加时间戳作为随机串
    },
  });
}

/** 删除还车申请单 */
async function handleDelete(row: CarReturnBillApi.CarReturnBill) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.id]),
    key: 'action_key_msg',
  });
  try {
    await deleteCarReturnBill(row.id as number);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess', [row.id]),
      key: 'action_key_msg',
    });
    onRefresh();
  } finally {
    hideLoading();
  }
}

/** 批量删除还车申请单 */
async function handleDeleteBatch() {
  // 检查选中的记录是否都可以删除
  const checkedRecords = gridApi.grid.getCheckboxRecords();
  const notAllowedRecords = checkedRecords.filter(
    (record: CarReturnBillApi.CarReturnBill) =>
      !BpmProcessInstanceStatusEditValue.includes(
        record.processStatus as number,
      ),
  );

  if (notAllowedRecords.length > 0) {
    const billCodes = notAllowedRecords
      .map(
        (record: CarReturnBillApi.CarReturnBill) =>
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
    await deleteCarReturnBillList(checkedIds.value);
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
  records: CarReturnBillApi.CarReturnBill[];
}) {
  checkedIds.value = records.map((item) => item.id);
}

/** 导出表格 */
async function handleExport() {
  const data = await exportCarReturnBill(await gridApi.formApi.getValues());
  downloadFileFromBlobPart({ fileName: '还车申请单.xls', source: data });
}

// 处理车辆选择
function handleCarSelect(car: any) {
  gridApi.formApi.setFieldValue('carNo', car.carNo);
  gridApi.formApi.setFieldValue('carId', car.id);
}

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: useGridFormSchema(modalRef),
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
          return await getCarReturnBillPage({
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
  } as VxeTableGridOptions<CarReturnBillApi.CarReturnBill>,
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
    <Grid table-title="还车申请单列表">
      <template #toolbar-tools>
        <TableAction
          :actions="[
            {
              label: $t('ui.actionTitle.create'),
              type: 'primary',
              icon: ACTION_ICON.ADD,
              auth: ['oa:car-return-bill:create'],
              onClick: handleCreate,
            },
            {
              label: $t('ui.actionTitle.export'),
              type: 'primary',
              icon: ACTION_ICON.DOWNLOAD,
              auth: ['oa:car-return-bill:export'],
              onClick: handleExport,
            },
            {
              label: $t('ui.actionTitle.deleteBatch'),
              type: 'primary',
              danger: true,
              icon: ACTION_ICON.DELETE,
              disabled: isEmpty(checkedIds),
              auth: ['oa:car-return-bill:delete'],
              onClick: handleDeleteBatch,
            },
          ]"
        />
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            // {
            //   label: $t('common.view'),
            //   type: 'link',
            //   icon: ACTION_ICON.VIEW,
            //   ifShow: () => !BpmProcessInstanceStatusEditValue.includes(row.processStatus as number),
            //   onClick: handleView.bind(null, row),
            // },
            // {
            //   label: $t('common.edit'),
            //   type: 'link',
            //   icon: ACTION_ICON.EDIT,
            //   ifShow: () => BpmProcessInstanceStatusEditValue.includes(row.processStatus as number),
            //   auth: ['oa:car-return-bill:update'],
            //   onClick: handleView.bind(null, row),
            // },
            {
              label: $t('common.delete'),
              type: 'link',
              danger: true,
              ifShow: () =>
                BpmProcessInstanceStatusEditValue.includes(
                  row.processStatus as number,
                ),
              auth: ['oa:car-return-bill:delete'],
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
              auth: ['oa:car-return-bill:delete'],
            },
          ]"
        />
      </template>
    </Grid>

    <!-- 车辆选择弹窗 -->
    <CarSelectModal ref="modalRef" @select="handleCarSelect" />
  </Page>
</template>
