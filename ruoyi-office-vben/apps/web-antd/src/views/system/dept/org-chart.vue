<script lang="ts" setup>
import type { EchartsUIType } from '@vben/plugins/echarts';

import type { SystemDeptApi } from '#/api/system/dept';
import type { SystemUserApi } from '#/api/system/user';

import { nextTick, onMounted, ref, shallowRef } from 'vue';

import { Page } from '@vben/common-ui';
import { IconifyIcon } from '@vben/icons';
import { EchartsUI, useEcharts } from '@vben/plugins/echarts';
import { handleTree } from '@vben/utils';

import { Button, Empty, message, Spin } from 'ant-design-vue';

import { getDeptList } from '#/api/system/dept';
import { getSimpleUserList } from '#/api/system/user';

defineOptions({ name: 'SystemDeptOrgChart' });

/** 部门树结构 */
interface DeptTree extends SystemDeptApi.Dept {
  children?: DeptTree[];
}

const loading = ref(false);
const chartRef = ref<EchartsUIType>();
const { renderEcharts } = useEcharts(chartRef);

const userList = shallowRef<SystemUserApi.User[]>([]);

/** 加载数据 */
async function loadData() {
  loading.value = true;
  try {
    const [deptRes, userRes] = await Promise.all([
      getDeptList(),
      getSimpleUserList(),
    ]);
    userList.value = userRes || [];

    const deptList = deptRes || [];
    const deptTree = handleTree(deptList, 'id', 'parentId');

    if (deptTree.length > 0 && deptTree[0]) {
      const graphData = transformToEchartsData(deptTree[0] as DeptTree);
      await nextTick();
      renderChart(graphData);
    } else {
      message.warning('暂无组织架构数据');
    }
  } catch {
    message.error('加载组织架构数据失败');
  } finally {
    loading.value = false;
  }
}

/** 获取负责人姓名 */
function getLeaderName(leaderUserId?: number): string {
  if (!leaderUserId) {
    return '未设置';
  }
  const user = userList.value.find((u) => u.id === leaderUserId);
  return user?.nickname || '未知';
}

/** 获取部门员工数量 */
function getEmployeeCount(deptId?: number): number {
  if (!deptId) {
    return 0;
  }
  return userList.value.filter((u) => u.deptId === deptId).length;
}

/** 判断是否为公司节点 */
function isCompany(orgType?: string): boolean {
  return orgType === '1';
}

/** 转换数据为 ECharts 格式 */
function transformToEchartsData(node: DeptTree, isRoot = true): any {
  const leaderName = getLeaderName(node.leaderUserId);
  const employeeCount = getEmployeeCount(node.id);
  const isCompanyNode = isCompany(node.orgType);

  let nodeType = 'dept'; // 默认部门
  if (isRoot) {
    nodeType = 'root';
  } else if (isCompanyNode) {
    nodeType = 'company';
  }

  const hasChildren = node.children && node.children.length > 0;

  const result: any = {
    name: node.name,
    value: node.id,
    itemStyle: {},
    meta: {
      nodeType,
      leader: leaderName,
      employeeCount: `${employeeCount}人`,
      hasChildren,
    },
  };

  // 根据节点类型设置样式
  if (nodeType === 'root') {
    result.itemStyle = {
      color: {
        type: 'linear',
        x: 0,
        y: 0,
        x2: 1,
        y2: 1,
        colorStops: [
          { offset: 0, color: '#7B68EE' },
          { offset: 1, color: '#9370DB' },
        ],
      },
      borderColor: '#7B68EE',
      borderWidth: 1.5,
      shadowBlur: 6,
      shadowColor: 'rgba(123, 104, 238, 0.2)',
    };
    result.label = {
      color: '#fff',
    };
  } else if (nodeType === 'company') {
    result.itemStyle = {
      color: {
        type: 'linear',
        x: 0,
        y: 0,
        x2: 1,
        y2: 1,
        colorStops: [
          { offset: 0, color: '#4FC3F7' },
          { offset: 1, color: '#29B6F6' },
        ],
      },
      borderColor: '#4FC3F7',
      borderWidth: 1.5,
      shadowBlur: 6,
      shadowColor: 'rgba(79, 195, 247, 0.2)',
    };
    result.label = {
      color: '#fff',
    };
  } else {
    result.itemStyle = {
      color: '#fff',
      borderColor: '#D9D9D9',
      borderWidth: 1.5,
    };
    result.label = {
      color: '#333',
    };
  }

  if (node.children && node.children.length > 0) {
    result.children = node.children.map((child) =>
      transformToEchartsData(child, false),
    );
  }

  return result;
}

/** 渲染图表 */
async function renderChart(data: any) {
  try {
    const option = {
      tooltip: {
        trigger: 'item' as const,
        triggerOn: 'mousemove' as const,
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: '#E8E8E8',
        borderWidth: 1,
        padding: 12,
        textStyle: {
          color: '#333',
        },
        formatter: (params: any) => {
          const { name, meta } = params.data;
          return `
            <div style="min-width: 150px;">
              <div style="font-size: 14px; font-weight: 600; margin-bottom: 8px; color: #333;">${name}</div>
              <div style="font-size: 12px; color: #666; margin-bottom: 4px;">负责人: ${meta.leader}</div>
              <div style="font-size: 12px; color: #666;">员工数量: ${meta.employeeCount}</div>
            </div>
          `;
        },
      },
      series: [
        {
          type: 'tree' as const,
          data: [data],
          top: '8%',
          bottom: '8%',
          left: '3%',
          right: '3%',
          orient: 'vertical' as const,
          layout: 'orthogonal' as const,
          symbol: 'rect' as const,
          symbolSize: [100, 70],
          symbolOffset: [0, 0],
          roam: true, // 允许拖拽和缩放
          scaleLimit: {
            min: 0.5,
            max: 3,
          },
          itemStyle: {
            borderRadius: 20,
          },
          lineStyle: {
            color: '#A3B1BF',
            width: 1.5,
            curveness: 0,
            type: 'solid' as const,
          },
          edgeShape: 'polyline' as const,
          edgeForkPosition: '50%',
          nodeGap: 40,
          layerGap: 80,
          label: {
            position: 'inside' as const,
            verticalAlign: 'middle' as const,
            align: 'center' as const,
            fontSize: 11,
            lineHeight: 14,
            padding: [4, 6],
            color: '#000',
            formatter: (params: any) => {
              const { name, meta } = params.data;
              const isCollapsed = params.collapsed;
              let icon = '';
              if (meta?.hasChildren) {
                icon = isCollapsed ? ' ▶' : ' ▼';
              }
              return `${name}${icon}\n${meta.leader}\n${meta.employeeCount}`;
            },
          },
          leaves: {
            label: {
              position: 'inside' as const,
              verticalAlign: 'middle' as const,
              align: 'center' as const,
              color: '#000',
            },
          },
          expandAndCollapse: true,
          initialTreeDepth: 2,
          animationDuration: 550,
          animationDurationUpdate: 750,
        },
      ],
    };

    await renderEcharts(option);
  } catch {
    message.error('渲染图表失败，请刷新重试');
  }
}

/** 刷新 */
function handleRefresh() {
  loadData();
}

onMounted(() => {
  loadData();
});
</script>

<template>
  <Page
    description="展示企业组织架构的层级关系，包括各部门、公司的负责人和员工数量。支持鼠标拖拽移动、滚轮缩放、点击节点展开/收起"
    title="组织架构图"
  >
    <template #extra>
      <Button type="primary" @click="handleRefresh">
        <template #icon>
          <IconifyIcon icon="ant-design:reload-outlined" />
        </template>
        刷新
      </Button>
    </template>

    <div class="org-chart-container">
      <Spin :spinning="loading" size="large" tip="加载中...">
        <EchartsUI
          v-if="!loading"
          ref="chartRef"
          class="chart-wrapper"
          height="680px"
        />
        <Empty v-if="!loading && !chartRef" description="暂无数据" />
      </Spin>
    </div>
  </Page>
</template>

<style lang="less" scoped>
.org-chart-container {
  width: 100%;
  height: 100%;
  min-height: 700px;
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: relative;
  overflow: hidden;

  .chart-wrapper {
    width: 100%;
    height: 680px;
  }
}
</style>
