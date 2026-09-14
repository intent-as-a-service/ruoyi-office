import type { Router } from 'vue-router';

declare global {
  interface Window {
    _hmt: any[];
    dataLayer: any[];
    gtag: (...args: any[]) => void;
  }
}

const HM_ID = import.meta.env.VITE_APP_BAIDU_CODE;
const GA_ID = import.meta.env.VITE_APP_GA_ID;

/**
 * 设置百度统计（SPA 路由追踪）
 * @param router Vue Router 实例
 */
function setupBaiduTongJi(router: Router) {
  if (!HM_ID) {
    return;
  }

  window._hmt = window._hmt || [];

  router.afterEach((to) => {
    window._hmt.push(['_trackPageview', to.fullPath]);
  });
}

/**
 * 设置 Google Analytics（SPA 路由追踪）
 * @param router Vue Router 实例
 */
function setupGoogleAnalytics(router: Router) {
  if (!GA_ID || !window.gtag) {
    return;
  }

  router.afterEach((to) => {
    window.gtag('config', GA_ID, {
      page_path: to.fullPath,
    });
  });
}

/**
 * 设置所有流量统计（百度统计 + Google Analytics）
 * @param router Vue Router 实例
 */
function setupAnalytics(router: Router) {
  setupBaiduTongJi(router);
  setupGoogleAnalytics(router);
}

export { setupAnalytics, setupBaiduTongJi, setupGoogleAnalytics };
