<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { BpmProcessInstanceApi } from '#/api/bpm/processInstance';

import { h } from 'vue';

import { Page, prompt } from '@vben/common-ui';
import { BpmProcessInstanceStatus, DICT_TYPE } from '@vben/constants';

import { Button, message, Textarea } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import { getProcessInstanceMyPage } from '#/api/bpm/processInstance';
import { withdrawProcessToStart } from '#/api/bpm/task';
import { DictTag } from '#/components/dict-tag';
import { router } from '#/router';

import { useGridColumns, useGridFormSchema } from './data';

// 扩展 Task 接口以包含 assigneeUser 属性
interface ExtendedTask extends BpmProcessInstanceApi.Task {
  assigneeUser?: { nickname: string };
}

// 扩展 ProcessInstance 接口以包含 summary 属性
interface ExtendedProcessInstance
  extends BpmProcessInstanceApi.ProcessInstance {
  summary?: Array<{ key: string; value: string }>;
  tasks?: ExtendedTask[];
}

defineOptions({ name: 'BpmProcessInstanceMy' });

/** 刷新表格 */
function handleRefresh() {
  gridApi.query();
}

/** 查看流程实例 */
function handleDetail(row: ExtendedProcessInstance) {
  router.push({
    name: 'BpmProcessInstanceDetail',
    query: { id: row.id.toString(), isTodo: 'false' },
  });
}

/** 撤回流程实例 */
function handleCancel(row: ExtendedProcessInstance) {
  prompt({
    async beforeClose(scope) {
      if (scope.isConfirm) {
        if (scope.value) {
          try {
            await withdrawProcessToStart({
              processInstanceId: row.id.toString(),
              reason: scope.value,
            });
            message.success('撤回成功');
            handleRefresh();
          } catch {
            return false;
          }
        } else {
          message.error('请输入撤回原因');
          return false;
        }
      }
    },
    component: () => {
      return h(Textarea, {
        placeholder: '请输入撤回原因',
        allowClear: true,
        rows: 2,
        rules: [{ required: true, message: '请输入撤回原因' }],
      });
    },
    content: '请输入撤回原因',
    title: '撤回流程',
    modelPropName: 'value',
  });
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
    keepSource: true,
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) => {
          return await getProcessInstanceMyPage({
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            ...formValues,
          });
        },
      },
    },
    rowConfig: {
      keyField: 'id',
    },
    toolbarConfig: {
      refresh: true,
      search: true,
    },
    cellConfig: {
      height: 64,
    },
  } as VxeTableGridOptions<ExtendedProcessInstance>,
});
</script>

<template>
  <Page auto-content-height>
    <Grid table-title="流程状态">
      <!-- 摘要 -->
      <template #slot-summary="{ row }">
        <div
          class="flex flex-col py-2"
          v-if="row.summary && row.summary.length > 0"
        >
          <div v-for="(item, index) in row.summary" :key="index">
            <span class="text-gray-500">
              {{ item.key }} : {{ item.value }}
            </span>
          </div>
        </div>
        <div v-else>-</div>
      </template>

      <template #slot-status="{ row }">
        <!-- 审批中状态 -->
        <template
          v-if="
            row.status === BpmProcessInstanceStatus.RUNNING &&
            row.tasks &&
            row.tasks.length > 0
          "
        >
          <!-- 单人审批 -->
          <template v-if="row.tasks.length === 1">
            <span>
              <Button type="link" @click="handleDetail(row)">
                {{ row.tasks[0]?.assigneeUser?.nickname || '未知用户' }}
              </Button>
              ({{ row.tasks[0]?.name || '未知任务' }}) 审批中
            </span>
          </template>
          <!-- 多人审批 -->
          <template v-else>
            <span>
              <Button type="link" @click="handleDetail(row)">
                {{ row.tasks[0]?.assigneeUser?.nickname || '未知用户' }}
              </Button>
              等 {{ row.tasks.length }} 人 ({{
                row.tasks[0]?.name || '未知任务'
              }})审批中
            </span>
          </template>
        </template>
        <!-- 非审批中状态 -->
        <template v-else>
          <DictTag
            :type="DICT_TYPE.BPM_PROCESS_INSTANCE_STATUS"
            :value="row.status"
          />
        </template>
      </template>
      <template #actions="{ row }">
        <TableAction
          :actions="[
            {
              label: $t('common.detail'),
              type: 'link',
              icon: ACTION_ICON.VIEW,
              auth: ['bpm:process-instance:query'],
              onClick: handleDetail.bind(null, row),
            },
            {
              label: $t('ui.actionTitle.revoke'),
              type: 'link',
              icon: ACTION_ICON.DELETE,
              ifShow: row.status === BpmProcessInstanceStatus.RUNNING,
              auth: ['bpm:process-instance:cancel'],
              onClick: handleCancel.bind(null, row),
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
