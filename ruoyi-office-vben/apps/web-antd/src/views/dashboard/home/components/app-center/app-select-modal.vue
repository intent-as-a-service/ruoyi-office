<script lang="ts" setup>
import type { SystemHomeAppCenterApi } from '#/api/system/home/app-center';

import { computed, ref, watch } from 'vue';

import { IconifyIcon } from '@vben/icons';

import { Empty, Input, Modal } from 'ant-design-vue';

import { getColorByKey } from './color-utils';

interface Props {
  visible: boolean;
  menuOptions: SystemHomeAppCenterApi.MenuOption[];
  selectedMenuIds: number[]; // 已选择的菜单ID列表
}

interface Emits {
  (e: 'select', menuId: number): void;
  (e: 'update:visible', value: boolean): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const searchKeyword = ref('');
const activeRootKey = ref<string | undefined>(undefined);

// 颜色算法：与工作台应用中心保持一致（复用 color-utils.ts）
function getMenuColor(menu: SystemHomeAppCenterApi.MenuOption) {
  const key = menu.name || menu.path || String(menu.id);
  return getColorByKey(key);
}

// 带有一级、二级分组信息的菜单
const menusWithGroup = computed(() => {
  return props.menuOptions.map((menu) => {
    const path = menu.path || '';
    const segments = path.split('/').filter(Boolean);
    // 一级分组优先使用后端传递的中文 rootName，其次才回退到路径段
    const level1 = (menu as any).rootName || segments[0] || '其他';
    const level2 = segments[1] || menu.name;
    return {
      ...menu,
      level1,
      level2,
    } as SystemHomeAppCenterApi.MenuOption & {
      level1: string;
      level2: string;
    };
  });
});

// 一级菜单列表
const rootCategories = computed(() => {
  const set = new Set<string>();
  menusWithGroup.value.forEach((item) => {
    set.add(item.level1);
  });
  return [...set];
});

// 过滤菜单选项（按一级菜单 + 关键字）
const filteredMenus = computed(() => {
  let list = menusWithGroup.value;

  if (activeRootKey.value) {
    list = list.filter((menu) => menu.level1 === activeRootKey.value);
  }

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    list = list.filter(
      (menu) =>
        menu.name.toLowerCase().includes(keyword) ||
        (menu.path && menu.path.toLowerCase().includes(keyword)),
    );
  }

  return list;
});

// 可用的菜单列表（排除已选择的）
const availableMenus = computed(() => {
  return filteredMenus.value.filter(
    (menu) => !props.selectedMenuIds.includes(menu.id),
  );
});

// 选择菜单
function handleSelectMenu(menu: SystemHomeAppCenterApi.MenuOption) {
  // 验证菜单 ID 是否存在
  if (!menu.id) {
    console.error('handleSelectMenu: 菜单缺少 ID', menu);
    return;
  }
  emit('select', menu.id);
}

// 关闭模态框
function handleClose() {
  emit('update:visible', false);
  searchKeyword.value = '';
}

// 获取菜单图标
function getMenuIcon(menu: SystemHomeAppCenterApi.MenuOption) {
  return menu.icon || 'carbon:application';
}

// 监听visible变化，重置搜索关键词
watch(
  () => props.visible,
  (newVal) => {
    if (!newVal) {
      searchKeyword.value = '';
      activeRootKey.value = undefined;
    } else if (!activeRootKey.value && rootCategories.value.length > 0) {
      activeRootKey.value = rootCategories.value[0];
    }
  },
);
</script>

<template>
  <Modal
    :open="visible"
    title="添加常用应用"
    width="700px"
    :footer="null"
    @cancel="handleClose"
  >
    <!-- 搜索框 -->
    <div class="mb-4">
      <Input
        v-model:value="searchKeyword"
        placeholder="搜索应用名称..."
        allow-clear
      >
        <template #prefix>
          <IconifyIcon icon="carbon:search" />
        </template>
      </Input>
    </div>

    <!-- 菜单列表 -->
    <div class="flex gap-4">
      <!-- 左侧一级菜单筛选 -->
      <div class="w-40 flex-shrink-0 border-r pr-2">
        <div
          v-for="root in rootCategories"
          :key="root"
          class="mb-1 cursor-pointer rounded px-2 py-1 text-sm"
          :class="[
            activeRootKey === root
              ? 'bg-primary/10 font-medium text-primary'
              : 'text-gray-700 hover:bg-gray-100',
          ]"
          @click="activeRootKey = root"
        >
          {{ root }}
        </div>
      </div>

      <!-- 右侧菜单卡片 -->
      <div class="menu-list flex-1">
        <div v-if="availableMenus.length > 0" class="grid grid-cols-3 gap-3">
          <div
            v-for="menu in availableMenus"
            :key="menu.id"
            class="menu-item group cursor-pointer rounded-lg border border-gray-200 bg-white p-3 transition-all hover:border-primary hover:shadow-md"
            @click="handleSelectMenu(menu)"
          >
            <!-- 图标 -->
            <div class="mb-2 flex justify-center">
              <div
                class="flex h-10 w-10 items-center justify-center rounded-lg transition-colors"
                :style="{ backgroundColor: `${getMenuColor(menu)}44` }"
              >
                <IconifyIcon
                  :icon="getMenuIcon(menu)"
                  class="text-xl"
                  :style="{ color: getMenuColor(menu) }"
                />
              </div>
            </div>

            <!-- 名称 -->
            <div class="text-center text-sm font-medium text-gray-900">
              {{ menu.name }}
            </div>

            <!-- 路径 -->
            <div
              v-if="menu.path"
              class="mt-1 truncate text-center text-xs text-gray-500"
            >
              {{ menu.path }}
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="py-8">
          <Empty
            description="没有可添加的应用"
            :image="Empty.PRESENTED_IMAGE_SIMPLE"
          >
            <template #footer>
              <div class="text-sm text-gray-500">
                所有可用应用已添加，或没有符合搜索条件的应用
              </div>
            </template>
          </Empty>
        </div>
      </div>
    </div>
  </Modal>
</template>

<style scoped>
.menu-list {
  max-height: 500px;
  overflow-y: auto;
}

/* 滚动条样式 */
.menu-list::-webkit-scrollbar {
  width: 6px;
}

.menu-list::-webkit-scrollbar-thumb {
  background-color: #d9d9d9;
  border-radius: 3px;
}

.menu-list::-webkit-scrollbar-thumb:hover {
  background-color: #bfbfbf;
}

.menu-list::-webkit-scrollbar-track {
  background-color: #f5f5f5;
}
</style>
