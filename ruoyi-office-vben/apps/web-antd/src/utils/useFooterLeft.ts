import { computed } from 'vue';
import { preferences, usePreferences } from '@vben/preferences';

/**
 * 动态获取侧边栏宽度的 composable
 * 用于计算固定底部组件的 left 偏移量
 */
export function useFooterLeft() {
  const { 
    sidebarCollapsed, 
    isMobile, 
    isHeaderMixedNav, 
    isSideMixedNav 
  } = usePreferences();

  /**
   * 动态获取侧边宽度（参考 vben-layout.vue 的 getSidebarWidth 逻辑）
   */
  const footerLeft = computed(() => {
    const { sidebar } = preferences;
    let width = 0;

    if (sidebar.hidden) {
      return width;
    }

    if (!sidebar.enable) {
      return width;
    }

    if ((isHeaderMixedNav.value || isSideMixedNav.value) && !isMobile.value) {
      width = sidebar.mixedWidth;
    } else if (sidebarCollapsed.value) {
      width = isMobile.value ? 0 : sidebar.collapseWidth;
    } else {
      width = sidebar.width;
    }
    return width;
  });

  return {
    footerLeft
  };
} 