<script lang="ts" setup>
import type { BpmProcessInstanceApi } from '#/api/bpm/processInstance';
import type { SystemUserApi } from '#/api/system/user';

// TODO @jason：业务表单审批时，读取不到界面，参见 https://t.zsxq.com/eif2e
import { computed, nextTick, onMounted, ref, shallowRef, watch } from 'vue';
import { useRoute } from 'vue-router';

import { Page } from '@vben/common-ui';
import {
  BpmModelFormType,
  BpmModelType,
  BpmProcessInstanceStatus,
  DICT_TYPE,
  BpmNodeIdEnum,
  BpmTaskStatusEnum,
} from '@vben/constants';
import { useUserStore } from '@vben/stores';

import { Card, Col, message, Row, TabPane, Tabs } from 'ant-design-vue';

import {
  getApprovalDetail as getApprovalDetailApi,
  getProcessInstanceBpmnModelView,
} from '#/api/bpm/processInstance';
import { getSimpleUserList } from '#/api/system/user';
import { setConfAndFields2 } from '#/components/form-create';
import { registerComponent } from '#/utils';
import {
  SvgBpmApproveIcon,
  SvgBpmCancelIcon,
  SvgBpmRejectIcon,
  SvgBpmRunningIcon,
} from '@vben/icons';

import ProcessInstanceBpmnViewer from './modules/bpm-viewer.vue';
import ProcessInstanceOperationButton from './modules/operation-button.vue';
import ProcessInstanceSimpleViewer from './modules/simple-bpm-viewer.vue';
import BpmProcessInstanceTaskList from './modules/task-list.vue';
import ProcessInstanceTimeline from './modules/time-line.vue';

defineOptions({ name: 'BpmProcessInstanceDetail' });

const props = withDefaults(
  defineProps<{
    activityId?: string; // 流程活动编号，用于抄送查看
    id: string; // 流程实例的编号
    isTodo?: boolean; // 是否待办，用于判断是否显示底部操作按钮
    nodeKey?: string; // 任务节点key
    taskId?: string; // 任务编号
  }>(),
  {
    activityId: undefined,
    isTodo: true,
    taskId: undefined,
    nodeKey: undefined,
  },
);

// 处理路由参数中的 isMy（可能是字符串）
const route = useRoute();
const userStore = useUserStore();

const isApproval = computed(() => {
  const queryApproval = route.query.isTodo;
  // 情况1：明确指定为false
  if (queryApproval === 'false') {
    return false;
  }

  // 情况2：queryApproval为'true'且当前任务状态为-1未提交且当前登录人等于制单人
  if (queryApproval === 'true') {
    // 获取流程发起人信息
    const startUser = processInstance.value?.startUser;
    // 获取当前登录用户ID
    const currentUserId = userStore.userInfo?.id;

    // 检查是否满足返回false的条件：
    // 1. 当前任务状态为-1（未提交）
    // 2. 当前登录人等于制单人
    if (
      nodeKey.value === BpmNodeIdEnum.START_USER_NODE_ID &&
      startUser?.id &&
      currentUserId &&
      String(startUser.id) === String(currentUserId)
    ) {
      return false;
    }
  }

  return props.isTodo;
});

enum FieldPermissionType {
  /**
   * 隐藏
   */
  // eslint-disable-next-line no-unused-vars
  NONE = '3',
  /**
   * 只读
   */
  // eslint-disable-next-line no-unused-vars
  READ = '1',
  /**
   * 编辑
   */
  // eslint-disable-next-line no-unused-vars
  WRITE = '2',
}

const processInstanceLoading = ref(false); // 流程实例的加载中
const processInstance = ref<BpmProcessInstanceApi.ProcessInstance>(); // 流程实例
const processDefinition = ref<any>({}); // 流程定义
// 使用 props 中的 nodeKey 或者从 route.query 获取
const nodeKey = computed(
  () => props.nodeKey || (route.query.nodeKey as string),
); // 节点key
const nodeKeyName = ref<string>(); // 节点名称
const processModelView = ref<any>({}); // 流程模型视图
const operationButtonRef = ref(); // 操作按钮组件 ref
const auditIconsMap: {
  [key: string]:
    | typeof SvgBpmApproveIcon
    | typeof SvgBpmCancelIcon
    | typeof SvgBpmRejectIcon
    | typeof SvgBpmRunningIcon;
} = {
  [BpmProcessInstanceStatus.RUNNING]: SvgBpmRunningIcon,
  [BpmProcessInstanceStatus.APPROVE]: SvgBpmApproveIcon,
  [BpmProcessInstanceStatus.REJECT]: SvgBpmRejectIcon,
  [BpmProcessInstanceStatus.CANCEL]: SvgBpmCancelIcon,
};

// ========== 申请信息 ==========
const fApi = ref<any>(); //
const detailForm = ref({
  rule: [],
  option: {},
  value: {},
}); // 流程实例的表单详情

const writableFields: Array<string> = []; // 表单可以编辑的字段

/** 加载流程实例 */
const BusinessFormComponent = shallowRef<any>(null); // 异步组件
const businessFormRef = ref(); // 业务表单组件引用

/** 获取详情 */
async function getDetail() {
  // 获得审批详情
  getApprovalDetail();

  // 获得流程模型视图
  getProcessModelView();
}

async function getApprovalDetail() {
  processInstanceLoading.value = true;
  try {
    const param = {
      processInstanceId: props.id,
      activityId: props.activityId,
      taskId: props.taskId,
    };
    const data = await getApprovalDetailApi(param);

    if (!data) {
      message.error('查询不到审批详情信息！');
    }

    if (!data.processDefinition || !data.processInstance) {
      message.error('查询不到流程信息！');
    }

    processInstance.value = data.processInstance;
    processDefinition.value = data.processDefinition;
    nodeKeyName.value = data.todoTask?.name;

    // 设置表单信息
    if (processDefinition.value.formType === BpmModelFormType.NORMAL) {
      // 获取表单字段权限
      const formFieldsPermission = data.formFieldsPermission;
      // 清空可编辑字段为空
      writableFields.splice(0);
      if (detailForm.value.rule?.length > 0) {
        // 避免刷新 form-create 显示不了
        detailForm.value.value = processInstance.value.formVariables;
      } else {
        setConfAndFields2(
          detailForm,
          processDefinition.value.formConf,
          processDefinition.value.formFields,
          processInstance.value.formVariables,
        );
      }
      nextTick().then(() => {
        fApi.value?.btn.show(false);
        fApi.value?.resetBtn.show(false);
        fApi.value?.disabled(true);
        // 设置表单字段权限
        if (formFieldsPermission) {
          Object.keys(data.formFieldsPermission).forEach((item) => {
            setFieldPermission(item, formFieldsPermission[item]);
          });
        }
      });
    } else {
      // 注意：data.processDefinition.formCustomViewPath 是组件的全路径，例如说：/crm/contract/detail/index.vue
      BusinessFormComponent.value = registerComponent(
        data?.processDefinition?.formCustomViewPath || '',
      );
    }

    // 获取审批节点，显示 Timeline 的数据
    activityNodes.value = data.activityNodes;

    // 获取待办任务显示操作按钮
    operationButtonRef.value?.loadTodoTask(data.todoTask);
  } catch {
    message.error('获取审批详情失败！');
  } finally {
    processInstanceLoading.value = false;
  }
}

/** 获取流程模型视图*/
async function getProcessModelView() {
  if (BpmModelType.BPMN === processDefinition.value?.modelType) {
    // 重置，解决 BPMN 流程图刷新不会重新渲染问题
    processModelView.value = {
      bpmnXml: '',
    };
  }
  const data = await getProcessInstanceBpmnModelView(props.id);
  if (data) {
    processModelView.value = data;
  }
}

// 审批节点信息
const activityNodes = ref<BpmProcessInstanceApi.ApprovalNodeInfo[]>([]);
/**
 * 设置表单权限
 */
function setFieldPermission(field: string, permission: string) {
  if (permission === FieldPermissionType.READ) {
    fApi.value?.disabled(true, field);
  }
  if (permission === FieldPermissionType.WRITE) {
    fApi.value?.disabled(false, field);
    // 加入可以编辑的字段
    writableFields.push(field);
  }
  if (permission === FieldPermissionType.NONE) {
    fApi.value?.hidden(true, field);
  }
}

/**
 * 操作成功后刷新
 */
// const refresh = () => {
//   // 重新获取详情
//   getDetail();
// };

/**
 * 审批前的业务表单处理
 * 在审批通过前调用业务表单的预处理方法
 */
async function handleBeforeApproval(): Promise<boolean> {
  try {
    // 如果是业务表单且有预处理方法，则调用
    if (
      businessFormRef.value &&
      typeof businessFormRef.value.beforeApproval === 'function'
    ) {
      const result = await businessFormRef.value.beforeApproval();
      return result !== false; // 如果返回false则阻止审批
    }
    return true; // 默认允许审批
  } catch (error) {
    console.error('业务表单预处理失败:', error);
    message.error('业务表单处理失败，请检查后重试');
    return false;
  }
}

/** 当前的Tab */
const activeTab = ref('form');
const taskListRef = ref();

/** 监听 Tab 切换，当切换到 "record" 标签时刷新任务列表 */
watch(
  () => activeTab.value,
  (newVal) => {
    if (newVal === 'record') {
      // 如果切换到流转记录标签，刷新任务列表
      nextTick(() => {
        taskListRef.value?.refresh();
      });
    }
  },
);

/** 初始化 */
const userOptions = ref<SystemUserApi.User[]>([]); // 用户列表
onMounted(async () => {
  await getDetail();
  // 获得用户列表
  userOptions.value = await getSimpleUserList();
});
</script>

<template>
  <Page
    auto-content-height
    v-if="processDefinition?.formType === BpmModelFormType.NORMAL"
  >
    <Card
      :body-style="{
        overflowY: 'auto',
        padding: '0px',
      }"
    >
      <div class="flex h-full flex-col">
        <!-- 流程基本信息 -->
        <div class="flex flex-col gap-2">
          <component
            v-if="processInstance?.status"
            :is="auditIconsMap[processInstance?.status]"
            class="absolute right-5 top-2.5 size-28"
          />
        </div>

        <!-- 流程操作 -->
        <div class="process-tabs-container flex flex-1 flex-col">
          <Tabs v-model:active-key="activeTab" class="mt-0 h-full">
            <TabPane tab="审批详情" key="form" class="tab-pane-content">
              <Row :gutter="[48, 24]" class="h-full">
                <Col
                  :xs="24"
                  :sm="24"
                  :md="20"
                  :lg="20"
                  :xl="20"
                  class="h-full"
                >
                  <!-- 流程表单 -->
                  <div class="h-full">
                    <form-create
                      v-model="detailForm.value"
                      v-model:api="fApi"
                      :option="detailForm.option"
                      :rule="detailForm.rule"
                    />
                  </div>

                  <!-- <div
                    v-if="
                      processDefinition?.formType === BpmModelFormType.CUSTOM
                    "
                    class="h-full"
                  >
                    <BusinessFormComponent :id="processInstance?.businessKey" />
                  </div> -->
                </Col>
                <Col :xs="24" :sm="24" :md="4" :lg="4" :xl="4" class="h-full">
                  <div class="mt-4 h-full">
                    <ProcessInstanceTimeline :activity-nodes="activityNodes" />
                  </div>
                </Col>
              </Row>
            </TabPane>

            <TabPane
              tab="流程图"
              key="diagram"
              class="tab-pane-content"
              :force-render="true"
            >
              <div class="h-full">
                <ProcessInstanceSimpleViewer
                  v-show="
                    processDefinition.modelType &&
                    processDefinition.modelType === BpmModelType.SIMPLE
                  "
                  :loading="processInstanceLoading"
                  :model-view="processModelView"
                />
                <ProcessInstanceBpmnViewer
                  v-show="
                    processDefinition.modelType &&
                    processDefinition.modelType === BpmModelType.BPMN
                  "
                  :loading="processInstanceLoading"
                  :model-view="processModelView"
                />
              </div>
            </TabPane>

            <TabPane tab="流转记录" key="record" class="tab-pane-content">
              <div class="h-full">
                <BpmProcessInstanceTaskList
                  ref="taskListRef"
                  :loading="processInstanceLoading"
                  :id="id"
                />
              </div>
            </TabPane>

            <!-- TODO 待开发 -->
            <TabPane
              tab="流转评论"
              key="comment"
              v-if="false"
              class="tab-pane-content"
            >
              <div class="h-full">待开发</div>
            </TabPane>
          </Tabs>
        </div>
      </div>

      <template #actions>
        <div class="px-4">
          <ProcessInstanceOperationButton
            ref="operationButtonRef"
            :process-instance="processInstance"
            :process-definition="processDefinition"
            :user-options="userOptions"
            :normal-form="detailForm"
            :normal-form-api="fApi"
            :writable-fields="writableFields"
            @success="getDetail"
          />
        </div>
      </template>
    </Card>
  </Page>
  <div v-else>
    <Card
      :body-style="{
        overflowY: 'auto',
        padding: '0px',
      }"
    >
      <BusinessFormComponent
        ref="businessFormRef"
        :id="processInstance?.businessKey"
        :is-approval="isApproval"
        :activity-nodes="activityNodes"
        :process-instance="processInstance"
        :process-definition="processDefinition"
        :node-key="nodeKey"
        :node-key-name="nodeKeyName"
      />
      <template #actions>
        <div class="px-4" v-if="isApproval">
          <ProcessInstanceOperationButton
            ref="operationButtonRef"
            :process-instance="processInstance"
            :process-definition="processDefinition"
            :user-options="userOptions"
            :normal-form="detailForm"
            :normal-form-api="fApi"
            :writable-fields="writableFields"
            :before-approval="handleBeforeApproval"
            @success="getDetail"
          />
        </div>
      </template>
    </Card>
  </div>
</template>

<style lang="scss" scoped>
.ant-tabs-content {
  height: 100%;
}

.process-tabs-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

:deep(.ant-tabs) {
  display: flex;
  flex-direction: column;
  height: 100%;
}

:deep(.ant-tabs-content) {
  flex: 1;
  overflow-y: auto;
}

:deep(.ant-tabs-tabpane) {
  height: 100%;
}

.tab-pane-content {
  padding-right: 12px;
  overflow: hidden auto;
}
</style>
