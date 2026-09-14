<script lang="ts" setup>
import type { MenuRecordRaw } from '@vben/types';

import type { MenuProps } from '@vben-core/menu-ui';

import { computed } from 'vue';
import { useRoute } from 'vue-router';

import { Menu } from '@vben-core/menu-ui';

import { useNavigation } from './use-navigation';

interface Props extends MenuProps {
  collapse?: boolean;
  menus?: MenuRecordRaw[];
  /** 是否展开所有有子菜单的菜单项 */
  expandAllMenus?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  accordion: true,
  menus: () => [],
  expandAllMenus: false,
});

const route = useRoute();
const { navigation } = useNavigation();

async function handleSelect(key: string) {
  await navigation(key);
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
    :key="expandAllMenus ? 'extra-menu-expand-all' : 'extra-menu-normal'"
    :accordion="accordion"
    :collapse="collapse"
    :default-active="route.meta?.activePath || route.path"
    :default-openeds="expandAllMenus ? allSubMenuPaths : defaultOpeneds"
    :menus="menus"
    :rounded="rounded"
    :theme="theme"
    mode="vertical"
    @select="handleSelect"
  />
</template>
