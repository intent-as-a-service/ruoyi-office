import type { Component } from 'vue';

// ==============================================
// 注册所有可用组件
// ==============================================
// 应用中心组件
import WorkbenchAppCenter from './app-center/workbench-app-center.vue';
// 图表组件
import AnalyticsVisitsSource from './charts/analytics-visits-source.vue';
// 列表组件
import WorkbenchProject from './lists/workbench-project.vue';
import WorkbenchTrends from './lists/workbench-trends.vue';
// 快捷导航组件
import WorkbenchQuickNav from './navigation/workbench-quick-nav.vue';
// 通知公告组件
import WorkbenchNotice from './notice/workbench-notice.vue';
// 日程组件
import WorkbenchSchedule from './schedule/workbench-schedule.vue';
import AnalyticsVisitsData from './statistics/analytics-visits-data.vue';
// 统计卡片组件
import AnalyticsVisits from './statistics/analytics-visits.vue';
// 任务列表组件
import WorkbenchTaskList from './taskLists/workbench-task-list.vue';
// 欢迎组件
import WorkbenchWelcome from './welcome/workbench-welcome.vue';

/**
 * 组件注册项
 */
export interface ComponentRegistryItem {
  code: string; // 组件编码
  component: Component; // Vue 组件
  name: string; // 组件名称
  description?: string; // 组件描述
}

/**
 * 组件注册表
 * 所有可用的首页组件都需要在此注册
 */
const componentRegistry = new Map<string, ComponentRegistryItem>();

/**
 * 注册组件
 */
export function registerComponent(item: ComponentRegistryItem) {
  componentRegistry.set(item.code, item);
}

/**
 * 获取组件
 */
export function getComponent(code: string): Component | undefined {
  return componentRegistry.get(code)?.component;
}

/**
 * 获取所有已注册组件
 */
export function getAllComponents(): ComponentRegistryItem[] {
  return [...componentRegistry.values()];
}

/**
 * 检查组件是否已注册
 */
export function hasComponent(code: string): boolean {
  return componentRegistry.has(code);
}

registerComponent({
  code: 'analytics_visits',
  component: AnalyticsVisits,
  name: '访问统计',
  description: '展示网站访问数据统计',
});

registerComponent({
  code: 'analytics_visits_data',
  component: AnalyticsVisitsData,
  name: '数据统计',
  description: '展示访问数据详情',
});

registerComponent({
  code: 'analytics_visits_source',
  component: AnalyticsVisitsSource,
  name: '访问来源图表',
  description: '展示访问来源的饼图分析',
});

registerComponent({
  code: 'workbench_project',
  component: WorkbenchProject,
  name: '项目列表',
  description: '展示项目卡片列表',
});

registerComponent({
  code: 'workbench_trends',
  component: WorkbenchTrends,
  name: '动态列表',
  description: '展示最新动态列表',
});

registerComponent({
  code: 'workbench_quick_nav',
  component: WorkbenchQuickNav,
  name: '快捷导航',
  description: '展示快捷导航入口',
});

registerComponent({
  code: 'workbench_task_list',
  component: WorkbenchTaskList,
  name: '任务列表',
  description: '展示我的单据、待办任务、已办任务、抄送我的',
});

registerComponent({
  code: 'workbench_welcome',
  component: WorkbenchWelcome,
  name: '欢迎组件',
  description: '展示欢迎信息、用户信息和天气',
});

registerComponent({
  code: 'workbench_notice',
  component: WorkbenchNotice,
  name: '通知公告',
  description: '展示系统通知公告列表',
});

registerComponent({
  code: 'workbench_app_center',
  component: WorkbenchAppCenter,
  name: '应用中心',
  description: '展示常用应用，支持拖拽排序',
});

registerComponent({
  code: 'workbench_schedule',
  component: WorkbenchSchedule,
  name: '我的日程',
  description: '展示日程日历和待办事项',
});
