<!--
 * @Author: zhanghui
 * @Date: 2025-08-03 19:49:31
 * @LastEditTime: 2025-08-10 21:41:03
 * @LastEditors: zhanghui
 * @Description: 表单详情页面表头
-->

<script lang="ts" setup>
import type { headerDataProps } from './typing';

import {
  BILL_FLOW_STATUS,
  BpmProcessInstanceStatus,
  getStatusColor,
} from '@vben/constants';
import { IconifyIcon } from '@vben/icons';
import { formatDate } from '@vben/utils';

import { message } from 'ant-design-vue';

interface Props {
  headerData?: headerDataProps;
}
const props = withDefaults(defineProps<Props>(), {
  headerData: () => ({
    billName: '',
    creatorName: '',
    billCode: '',
    createTime: '',
    companyName: '',
    deptName: '',
    processStatus: BpmProcessInstanceStatus.NOT_START,
  }),
});
// 获取审批状态名称
const getStatusName = (val: any) => {
  const name: any = BILL_FLOW_STATUS.find((item: any) => item.value === val);
  return name?.label || '未提交';
};

// 复制单据编号
const handleCopyBillCode = () => {
  const billCode = props.headerData.billCode;
  if (!billCode) {
    message.warning('单据编号为空');
    return;
  }

  if (navigator.clipboard) {
    navigator.clipboard
      .writeText(billCode)
      .then(() => {
        message.success('已复制到剪贴板');
      })
      .catch(() => {
        fallbackCopy(billCode);
      });
  } else {
    fallbackCopy(billCode);
  }
};

// 降级复制方案
const fallbackCopy = (text: string) => {
  const textArea = document.createElement('textarea');
  textArea.value = text;
  document.body.append(textArea);
  textArea.select();
  try {
    document.execCommand('copy');
    message.success('已复制到剪贴板');
  } catch {
    message.error('复制失败，请手动复制');
  }
  textArea.remove();
};
</script>
<template>
  <div class="header-form">
    <div class="header-row">
      <div class="header-content">
        <div class="title-section">
          <span class="title-name">{{ props.headerData.billName }}</span>
        </div>
        <div class="status-section">
          <a-tag
            :color="getStatusColor(props.headerData.processStatus)?.status"
          >
            {{ getStatusName(props.headerData.processStatus) }}
          </a-tag>
        </div>
      </div>
    </div>
    <div class="info-row">
      <div class="info-content">
        <span class="info-item">
          单据编号: {{ props.headerData.billCode }}
          <span
            v-if="props.headerData.billCode"
            @click="handleCopyBillCode"
            title="复制单据编号"
            class="copy-icon"
          >
            <IconifyIcon icon="mdi:content-copy" />
          </span>
        </span>
        <span class="info-item">
          申请人 : {{ props.headerData.creatorName }}
        </span>
        <span class="info-item">
          申请日期 : {{ formatDate(props.headerData.createTime) }}
        </span>
        <span class="info-item">
          所属单位 : {{ props.headerData.companyName }}
        </span>
        <span class="info-item">
          所属部门 : {{ props.headerData.deptName }}
        </span>
      </div>
    </div>
  </div>
</template>
<style scoped>
/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .info-content {
    gap: 20px;
  }

  .info-item {
    font-size: 13px;
  }

  .title-name {
    font-size: 16px;
  }
}

@media (max-width: 480px) {
  .info-content {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
}

.header-form {
  width: 100%;
  padding: 0;
}

/* 头部行 */
.header-row {
  width: 100%;
  margin-bottom: 16px;
}

.header-content {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-start;
  justify-content: space-between;
  width: 100%;
}

.title-section {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 8px;
  min-width: 0;
}

.status-section {
  display: flex;
  flex-shrink: 0;
  align-items: flex-start;
}

/* 信息行 */
.info-row {
  width: 100%;
}

.info-content {
  display: flex;
  flex-wrap: wrap;
  gap: 50px;
  align-items: center;
}

.info-item {
  display: inline-block;
  font-size: 14px;
  color: #333;
  white-space: nowrap;
}

/* 标题样式 */
.title-name {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  line-height: 24px;
  color: #333;
}

/* 复制图标样式 */
.copy-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  margin-left: 4px;
  font-size: 12px;
  color: #999;
  cursor: pointer;
  border-radius: 2px;
  transition: all 0.2s ease;
}

.copy-icon:hover {
  color: hsl(var(--primary));
  background: hsl(var(--primary) / 10%);
}

/* 头部表单容器 */
</style>
