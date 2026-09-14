<script lang="ts" setup>
import type { MenuRecordRaw } from '@vben/types';

import type { MenuProps } from '@vben-core/menu-ui';

import { computed } from 'vue';

import { Menu } from '@vben-core/menu-ui';

interface Props extends MenuProps {
  menus?: MenuRecordRaw[];
  /** 是否展开所有有子菜单的菜单项 */
  expandAllMenus?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  accordion: true,
  menus: () => [],
  expandAllMenus: false,
});

const emit = defineEmits<{
  open: [string, string[]];
  select: [string, string?];
}>();

function handleMenuSelect(key: string) {
  emit('select', key, props.mode);
}

function handleMenuOpen(key: string, path: string[]) {
  emit('open', key, path);
}

// 计算所有需要展开的子菜单 path（有 children 的节点）
const allSubMenuPaths = computed(() => {
  if (!props.expandAllMenus) return [];
  const result: string[] = [];

  const travel = (menus?: MenuRecordRaw[]) => {
    menus?.forEach((item) => {
      const children = (item as any).children as MenuRecordRaw[] | undefined;
      if (children && children.length > 0) {
        if (item.path) {
          result.push(item.path as string);
        }
        travel(children);
      }
    });
  };

  travel(props.menus);
  return result;
});
</script>

<template>
  <Menu
    :key="expandAllMenus ? 'menu-expand-all' : 'menu-normal'"
    :accordion="accordion"
    :collapse="collapse"
    :collapse-show-title="collapseShowTitle"
    :default-active="defaultActive"
    :default-openeds="expandAllMenus ? allSubMenuPaths : defaultOpeneds"
    :menus="menus"
    :mode="mode"
    :rounded="rounded"
    scroll-to-active
    :theme="theme"
    @open="handleMenuOpen"
    @select="handleMenuSelect"
  />
</template>
