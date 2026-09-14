<script lang="ts" setup>
import type { SystemHomeAppCenterApi } from '#/api/system/home/app-center';

import { computed, nextTick, onMounted, ref } from 'vue';

import { IconifyIcon } from '@vben/icons';
import { useAccessStore } from '@vben/stores';

import { Empty, message, Popconfirm, Spin } from 'ant-design-vue';
import Draggable from 'vuedraggable';

import {
  createUserApp,
  deleteUserApp,
  getUserAppList,
  initUserApp,
  updateUserAppSort,
} from '#/api/system/home/app-center';
import { router } from '#/router';

import AppSelectModal from './app-select-modal.vue';
import { getColorByKey } from './color-utils';

interface Props {
  maxAppCount?: number; // 最大显示应用数
  gridCols?: number; // 网格列数
  enableDrag?: boolean; // 是否启用拖拽
}

const props = withDefaults(defineProps<Props>(), {
  maxAppCount: 12,
  gridCols: 4,
  enableDrag: true,
});

const loading = ref(false);
const appList = ref<SystemHomeAppCenterApi.AppUserVO[]>([]);
const isDragging = ref(false);
const accessStore = useAccessStore();
const isInitializing = ref(false); // 防止重复初始化

// 添加应用模态框
const selectModalVisible = ref(false);

// 从权限菜单获取可用的菜单选项
const menuOptions = computed(() => {
  const menus = accessStore.accessMenus || [];
  // 将菜单转换为选项格式（只取有实际页面的“叶子菜单”）
  const options: SystemHomeAppCenterApi.MenuOption[] = [];

  const processMenus = (menuList: any[], parents: any[] = []) => {
    for (const menu of menuList) {
      const children = Array.isArray(menu.children) ? menu.children : [];
      const currentParents = [...parents, menu];
      const isLeaf = children.length === 0;

      // 只选择有 path 的叶子菜单，不再依赖后端的 type 字段，避免类型差异导致全为空
      if (isLeaf && menu.path) {
        // 获取菜单 ID（MenuRecordRaw 现在有 id 字段，但为了兼容性也检查其他位置）
        const id =
          (menu as any).id ??
          (menu as any).menuId ??
          (menu as any).meta?.id ??
          (menu as any).meta?.menuId ??
          null;

        // 如果确实没有 ID，跳过该菜单（因为后端需要 menuId）
        if (!id) {
          console.warn('菜单缺少 ID，已跳过:', {
            path: menu.path,
            name: menu.name || (menu as any).meta?.title,
            menuKeys: Object.keys(menu),
          });
          return;
        }

        const icon =
          menu.icon ??
          menu.menuIcon ??
          menu.meta?.icon ??
          menu.meta?.menuIcon ??
          'carbon:application';
        const name =
          menu.meta?.title ??
          menu.name ??
          menu.meta?.menuName ??
          menu.path ??
          '未命名菜单';

        // 一级菜单中文名称（用于弹窗左侧分组显示）
        const rootMenu = currentParents[0] ?? menu;
        const rootName =
          rootMenu.meta?.title ??
          rootMenu.name ??
          rootMenu.meta?.menuName ??
          rootMenu.path ??
          '其他';

        options.push({
          id: Number(id), // 确保是数字类型
          name,
          path: menu.path,
          icon,
          parentId: menu.parentId ?? menu.menuParentId ?? null,
          // 附加字段：一级菜单名称
          rootName,
        } as SystemHomeAppCenterApi.MenuOption & {
          rootName?: string;
        });
      }

      // 递归处理子菜单
      if (children.length > 0) {
        processMenus(children, currentParents);
      }
    }
  };

  processMenus(menus);
  return options;
});

// 显示的应用列表（限制数量）
const displayApps = computed(() => {
  return appList.value.slice(0, props.maxAppCount);
});

// 网格样式
const gridStyle = computed(() => {
  return {
    gridTemplateColumns: `repeat(${props.gridCols}, minmax(0, 1fr))`,
  };
});

// 加载应用列表
async function loadAppList() {
  loading.value = true;
  try {
    appList.value = await getUserAppList();
    // 只在第一次加载且数据为空且未初始化过时，才自动初始化
    if (appList.value.length === 0 && !isInitializing.value) {
      isInitializing.value = true; // 先设置标志，防止重复初始化
      await autoInitApps();
    }
  } catch (error) {
    console.error('加载应用列表失败:', error);
    appList.value = [];
  } finally {
    loading.value = false;
  }
}

// 自动初始化应用（仅在首次加载数据为空时调用）
async function autoInitApps() {
  try {
    await initUserApp();
    // 重新获取数据，避免再次调用 loadAppList 造成循环
    const newList = await getUserAppList();
    appList.value = newList;
    if (newList.length > 0) {
      message.success('初始化成功');
    }
  } catch (error) {
    console.error('自动初始化应用失败:', error);
    // 初始化失败时重置标志，允许用户手动重试
    isInitializing.value = false;
  }
}

// 手动初始化应用（从系统配置复制）
async function handleInitApps() {
  try {
    loading.value = true;
    await initUserApp();
    message.success('初始化成功');
    // 重新加载列表
    appList.value = await getUserAppList();
  } catch (error) {
    console.error('初始化应用失败:', error);
    message.error('初始化失败，请重试');
  } finally {
    loading.value = false;
  }
}

// 打开添加应用模态框
function handleAddApp() {
  selectModalVisible.value = true;
}

// 选择应用
async function handleSelectApp(menuId: number) {
  // 验证 menuId 是否存在
  if (!menuId) {
    message.error('菜单ID不能为空');
    console.error('handleSelectApp: menuId 为空', menuId);
    return;
  }

  try {
    await createUserApp({ menuId });
    message.success('添加成功');
    selectModalVisible.value = false;
    // 直接刷新列表，不触发自动初始化
    await refreshAppList();
  } catch (error) {
    console.error('添加应用失败:', error);
    message.error('添加应用失败，请重试');
  }
}

// 删除应用
async function handleDeleteApp(app: SystemHomeAppCenterApi.AppUserVO) {
  if (!app.id) return;
  try {
    await deleteUserApp(app.id);
    message.success('删除成功');
    // 直接刷新列表，不触发自动初始化
    await refreshAppList();
  } catch (error) {
    console.error('删除应用失败:', error);
  }
}

// 刷新应用列表（不触发自动初始化）
async function refreshAppList() {
  loading.value = true;
  try {
    appList.value = await getUserAppList();
  } catch (error) {
    console.error('刷新应用列表失败:', error);
    appList.value = [];
  } finally {
    loading.value = false;
  }
}

// 拖拽开始
function handleDragStart() {
  isDragging.value = true;
}

// 拖拽结束
async function handleDragEnd() {
  isDragging.value = false;
  await nextTick();

  // 更新排序
  const sortData = appList.value.map((app, index) => ({
    id: app.id!,
    sort: index,
  }));

  try {
    await updateUserAppSort(sortData);
    message.success('排序已保存');
  } catch (error) {
    console.error('更新排序失败:', error);
    // 失败时重新加载列表，不触发自动初始化
    await refreshAppList();
  }
}

// 点击应用
function handleClickApp(app: SystemHomeAppCenterApi.AppUserVO) {
  // 后端已经返回完整路径（已拼接父级路径），直接使用
  if (!app.menuPath) {
    message.warning('该应用暂无访问路径');
    return;
  }

  // 确保路径格式正确（以 / 开头）
  let targetPath = app.menuPath.trim();
  if (!targetPath.startsWith('/')) {
    targetPath = `/${targetPath}`;
  }

  // 使用 Vue Router 导航，只更新内容区域，不刷新整个页面
  // 参考框架菜单的跳转方式：packages/effects/layouts/src/basic/menu/use-navigation.ts
  router.push({ path: targetPath }).catch((error) => {
    // 忽略重复导航错误（Vue Router 3.x 会抛出此错误，但导航仍然成功）
    if (error.name !== 'NavigationDuplicated') {
      console.error('路由跳转失败:', error);
      message.error('页面跳转失败，请检查路径是否正确');
    }
  });
}

// 获取应用图标
function getAppIcon(app: SystemHomeAppCenterApi.AppUserVO) {
  return app.icon || app.menuIcon || 'carbon:application';
}

// 获取应用名称
function getAppName(app: SystemHomeAppCenterApi.AppUserVO) {
  return app.name || app.menuName || '未命名应用';
}

// 获取应用颜色
function getAppColor(app: SystemHomeAppCenterApi.AppUserVO) {
  if (app.color) {
    return app.color;
  }
  const key =
    app.name || app.menuName || app.menuPath || String(app.menuId || app.id);
  return getColorByKey(key);
}

// 组件挂载时加载数据
onMounted(() => {
  loadAppList();
});
</script>

<template>
  <div class="workbench-app-center rounded-lg bg-background px-4">
    <!-- 应用网格 -->
    <div class="app-grid-wrapper">
      <Spin :spinning="loading">
        <div
          v-if="
            !loading && (displayApps.length > 0 || appList.length < maxAppCount)
          "
          class="app-grid"
          :style="gridStyle"
        >
          <Draggable
            v-model="appList"
            :disabled="!enableDrag"
            class="app-grid-draggable"
            :style="gridStyle"
            item-key="id"
            animation="200"
            @start="handleDragStart"
            @end="handleDragEnd"
          >
            <template #item="{ element: app }">
              <div
                class="app-item group relative cursor-pointer transition-all"
                :class="{
                  'is-dragging': isDragging,
                  'opacity-50': app.status === 1,
                }"
                @click="handleClickApp(app)"
              >
                <!-- 图标和名称 -->
                <div class="flex flex-col items-center gap-1">
                  <div class="relative" style="padding: 4px; margin: -4px">
                    <div
                      class="flex h-11 w-11 items-center justify-center rounded-xl"
                      :style="{ backgroundColor: `${getAppColor(app)}44` }"
                    >
                      <IconifyIcon
                        :icon="getAppIcon(app)"
                        class="text-xl"
                        :style="{ color: getAppColor(app) }"
                      />
                    </div>
                    <!-- 删除按钮（放在图标容器内，使用 transform 偏移到外部） -->
                    <Popconfirm
                      title="确定要删除此应用吗？"
                      ok-text="确定"
                      cancel-text="取消"
                      @confirm.stop="handleDeleteApp(app)"
                    >
                      <div
                        class="app-delete-btn absolute right-1 top-1 z-20 flex h-3.5 w-3.5 cursor-pointer items-center justify-center rounded-full bg-red-500 text-white opacity-0 shadow-md transition-opacity group-hover:opacity-100"
                        style="transform: translate(30%, -30%)"
                        @click.stop
                      >
                        <IconifyIcon icon="carbon:close" class="text-[7px]" />
                      </div>
                    </Popconfirm>
                  </div>
                  <div class="text-center text-xs text-gray-600">
                    {{ getAppName(app) }}
                  </div>
                </div>
              </div>
            </template>
          </Draggable>

          <!-- 新增按钮（超过最大应用数时隐藏） -->
          <div
            v-if="appList.length < maxAppCount"
            class="app-item group cursor-pointer transition-all"
            @click="handleAddApp"
          >
            <div class="flex flex-col items-center gap-1">
              <div
                class="flex h-11 w-11 items-center justify-center rounded-xl border-2 border-dashed border-gray-300 transition-colors group-hover:border-primary"
              >
                <IconifyIcon
                  icon="carbon:add"
                  class="text-xl text-gray-400 transition-colors group-hover:text-primary"
                />
              </div>
              <div
                class="text-center text-xs text-gray-500 transition-colors group-hover:text-primary"
              >
                添加应用
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="py-8">
          <Empty description="暂无应用" :image="Empty.PRESENTED_IMAGE_SIMPLE">
            <template #footer>
              <div class="flex items-center justify-center gap-4">
                <a class="text-primary" @click="handleInitApps">
                  <IconifyIcon icon="carbon:reset" class="mr-1" />
                  初始化默认应用
                </a>
                <span class="text-gray-300">|</span>
                <a class="text-primary" @click="handleAddApp">
                  <IconifyIcon icon="carbon:add" class="mr-1" />
                  手动添加应用
                </a>
              </div>
            </template>
          </Empty>
        </div>
      </Spin>
    </div>

    <!-- 添加应用模态框 -->
    <AppSelectModal
      v-model:visible="selectModalVisible"
      :menu-options="menuOptions"
      :selected-menu-ids="appList.map((app) => app.menuId)"
      @select="handleSelectApp"
    />
  </div>
</template>

<style scoped>
.app-header {
  border-bottom: 1px solid #f0f0f0;
}

.app-grid-wrapper {
  overflow: visible; /* 确保删除按钮不被裁剪 */
}

.app-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px 32px;
  overflow: visible; /* 确保删除按钮不被裁剪 */
}

.app-grid-draggable {
  display: flex;
  flex-wrap: wrap;
  gap: 16px 32px;
  overflow: visible; /* 确保删除按钮不被裁剪 */
}

.app-item {
  overflow: visible; /* 确保删除按钮不被裁剪 */
}

/* 删除按钮样式 */
.app-delete-btn {
  /* 使用 transform 将按钮偏移到图标外部 */

  /* transform: translate(25%, -25%) 已在模板中设置 */
  z-index: 100;
}

.app-item.is-dragging {
  cursor: move;
}

/* 拖拽时的样式 */
.sortable-ghost {
  opacity: 0.5;
}

.sortable-chosen {
  cursor: move;
}
</style>
