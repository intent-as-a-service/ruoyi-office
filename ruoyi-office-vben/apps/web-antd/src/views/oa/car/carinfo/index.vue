<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { CarApi } from '#/api/oa/car/carinfo';

import { ref, watch } from 'vue';

import { Page, useVbenModal } from '@vben/common-ui';
import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';
import { useUserStore } from '@vben/stores';
import { downloadFileFromBlobPart, isEmpty } from '@vben/utils';

import { Card, Menu, message } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteCar,
  deleteCarListByIds,
  exportCar,
  getCarPage,
} from '#/api/oa/car/carinfo';
import { $t } from '#/locales';

import { useGridColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: Form,
  destroyOnClose: true,
});

const userStore = useUserStore();
// 分类选择相关状态
const selectedCarCls = ref<null | number>(null);
const carClsOptions = getDictOptions(DICT_TYPE.OA_CAR_CLS, 'number');

// 监听分类选择变化，触发表格查询
watch(selectedCarCls, () => {
  onRefresh();
});

/** 刷新表格 */
function onRefresh() {
  gridApi.query();
}

/** 选择分类 */
function handleSelectCarCls(carCls: null | number) {
  selectedCarCls.value = carCls;
}

/** 创建车辆信息 */
function handleCreate() {
  // 设置默认值
  const car: CarApi.Car = {
    carCls: selectedCarCls.value || undefined,
    companyId: userStore.userInfo?.companyId || undefined,
    companyName: userStore.userInfo?.companyName || undefined,
    status: 0, //  空闲
  };
  formModalApi.setData(car).open();
}

/** 编辑车辆信息 */
function handleEdit(row: CarApi.Car) {
  formModalApi.setData(row).open();
}

/** 删除车辆信息 */
async function handleDelete(row: CarApi.Car) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.id]),
    key: 'action_key_msg',
  });
  try {
    await deleteCar(row.id as number);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess', [row.id]),
      key: 'action_key_msg',
    });
    onRefresh();
  } finally {
    hideLoading();
  }
}

/** 批量删除车辆信息 */
async function handleDeleteBatch() {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting'),
    key: 'action_key_msg',
  });
  try {
    await deleteCarListByIds(deleteIds.value);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess'),
      key: 'action_key_msg',
    });
    onRefresh();
  } finally {
    hideLoading();
  }
}

const deleteIds = ref<number[]>([]); // 待删除车辆信息 ID
function setDeleteIds({ records }: { records: CarApi.Car[] }) {
  deleteIds.value = records
    .map((item) => item.id)
    .filter((id): id is number => id !== undefined);
}

/** 导出表格 */
async function handleExport() {
  const formValues = await gridApi.formApi.getValues();
  // 合并分类筛选条件
  const exportParams = {
    ...formValues,
    ...(selectedCarCls.value === null ? {} : { carCls: selectedCarCls.value }),
  };
  const data = await exportCar(exportParams);
  downloadFileFromBlobPart({ fileName: '车辆信息.xls', source: data });
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
          // 合并分类筛选条件
          const queryParams = {
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            ...formValues,
            ...(selectedCarCls.value === null
              ? {}
              : { carCls: selectedCarCls.value }),
          };
          return await getCarPage(queryParams);
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
  } as VxeTableGridOptions<CarApi.Car>,
  gridEvents: {
    checkboxAll: setDeleteIds,
    checkboxChange: setDeleteIds,
  },
});
</script>

<template>
  <Page auto-content-height>
    <FormModal @success="onRefresh" />

    <div class="flex h-full gap-4">
      <!-- 左侧分类选择 -->
      <div class="w-64 flex-shrink-0">
        <Card title="车辆分类" size="small" class="h-full">
          <Menu
            :selected-keys="
              selectedCarCls === null ? ['all'] : [String(selectedCarCls)]
            "
            mode="inline"
            :inline-indent="16"
            class="car-category-menu border-0"
          >
            <Menu.Item key="all" @click="handleSelectCarCls(null)">
              <span> 全部 </span>
            </Menu.Item>
            <Menu.Item
              v-for="option in carClsOptions"
              :key="String(option.value)"
              @click="handleSelectCarCls(Number(option.value))"
            >
              <span>
                {{ option.label }}
              </span>
            </Menu.Item>
          </Menu>
        </Card>
      </div>

      <!-- 右侧表格 -->
      <div class="min-w-0 flex-1">
        <Grid table-title="车辆信息列表">
          <template #toolbar-tools>
            <TableAction
              :actions="[
                {
                  label: $t('ui.actionTitle.create', ['车辆信息']),
                  type: 'primary',
                  icon: ACTION_ICON.ADD,
                  auth: ['oa:car:create'],
                  onClick: handleCreate,
                },
                {
                  label: $t('ui.actionTitle.export'),
                  type: 'primary',
                  icon: ACTION_ICON.DOWNLOAD,
                  auth: ['oa:car:export'],
                  onClick: handleExport,
                },
                {
                  label: $t('ui.actionTitle.deleteBatch'),
                  type: 'primary',
                  danger: true,
                  icon: ACTION_ICON.DELETE,
                  disabled: isEmpty(deleteIds),
                  auth: ['oa:car:delete'],
                  onClick: handleDeleteBatch,
                },
              ]"
            />
          </template>
          <template #actions="{ row }">
            <TableAction
              :actions="[
                {
                  label: $t('common.edit'),
                  type: 'link',
                  icon: ACTION_ICON.EDIT,
                  auth: ['oa:car:update'],
                  onClick: handleEdit.bind(null, row),
                },
                {
                  label: $t('common.delete'),
                  type: 'link',
                  danger: true,
                  icon: ACTION_ICON.DELETE,
                  auth: ['oa:car:delete'],
                  popConfirm: {
                    title: $t('ui.actionMessage.deleteConfirm', [row.carNo]),
                    confirm: handleDelete.bind(null, row),
                  },
                },
              ]"
            />
          </template>
        </Grid>
      </div>
    </div>
  </Page>
</template>

<style scoped>
/* 移除菜单整体的右边框 */
:deep(.car-category-menu.ant-menu-inline) {
  border-right: none !important;
}

/* 统一激活状态样式 - 使用CSS变量支持主题切换 */
:deep(.car-category-menu .ant-menu-item-selected) {
  font-weight: 500 !important;
  color: hsl(var(--primary)) !important; /* 使用主题色 */
  background-color: hsl(var(--primary) / 10%) !important; /* 使用主题色 */
  border-radius: 0 !important; /* 去掉圆角 */
}

/* 去掉右侧蓝线 */
:deep(.car-category-menu .ant-menu-item-selected::after) {
  display: none !important;
}

:deep(.car-category-menu .ant-menu-item:hover) {
  color: hsl(var(--primary)) !important; /* 使用主题色 */
  background-color: hsl(var(--accent)) !important; /* 使用accent色 */
  border-radius: 0 !important; /* 去掉圆角 */
  transform: translateX(2px); /* 添加悬停效果 */
}

:deep(.car-category-menu .ant-menu-item) {
  height: auto !important; /* 自动高度 */
  padding: 8px !important; /* 与企业云盘 p-2 保持一致 (8px) */
  margin: 8px 0 !important; /* 与企业云盘 space-y-2 保持一致 (8px) */
  line-height: 1.5 !important; /* 行高 */
  border-radius: 0 !important; /* 去掉圆角 */
  transition: all 0.2s ease !important;
}

/* 菜单容器样式 - 与企业云盘保持一致 */
:deep(.car-category-menu) {
  padding: 0 !important; /* 移除容器内边距，让菜单项贴边 */
}

/* 菜单项间距调整 */
:deep(.car-category-menu .ant-menu-item:first-child) {
  margin-top: 0 !important; /* 第一个菜单项顶部无边距 */
}

:deep(.car-category-menu .ant-menu-item:last-child) {
  margin-bottom: 0 !important; /* 最后一个菜单项底部无边距 */
}
</style>
