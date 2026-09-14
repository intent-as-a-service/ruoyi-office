<!--
 * @Author: zhanghui
 * @Date: 2025-08-04 23:13:56
 * @LastEditTime: 2025-08-10 21:20:13
 * @LastEditors: zhanghui
 * @Description: 表单审批页
-->

<script lang="ts" setup>
import { getStatusColor } from '@vben/constants';

import { Steps, Tag } from 'ant-design-vue';
// 传入组件参数
const props = defineProps({
  currentStep: {
    type: Number as any,
    default: 1,
  },
  steps: {
    type: Array as any,
    default: () => [],
  },
  approvalData: {
    type: Array as any,
    default: () => [],
  },
});
// 获取审批数据
</script>
<template>
  <div>
    <div class="card-head">
      <span class="card-head-text">{{ $t('common.approvalProgress') }}</span>
    </div>
    <!-- 基础步骤组件 -->
    <Steps :current="props.currentStep" class="vertical-title-steps">
      <!-- 第一步 -->
      <Steps.Step v-for="(step, index) in props.steps" :key="index">
        <!-- 标题插槽 - 放在步骤条上方 -->
        <template #title>
          <div class="step-title">
            <Tag
              :bordered="false"
              :class="`circular-tag ${index !== currentStep ? 'no-active-tag' : 'active'}`"
            >
              <div :title="`${step.title.length > 7 ? step.title : ''}`">
                {{ step.title }}
              </div>
            </Tag>
          </div>
        </template>

        <!-- 描述插槽 - 放在步骤条下方 -->
        <template #description>
          <div
            class="step-description"
            :title="`${step.content.length > 7 ? step.content : ''}`"
          >
            {{ step.content }}
          </div>
        </template>

        <!-- 自定义图标 -->
        <template #icon>
          <Tag
            v-if="step.tags"
            :bordered="false"
            :class="`circular-tag ${index > currentStep ? 'no-active' : 'active'}`"
          >
            {{ step.tags }}
          </Tag>
          <div
            v-else
            :class="`step-icon ${index > currentStep ? 'no-active' : 'active'}`"
          ></div>
        </template>
      </Steps.Step>
    </Steps>
    <div class="card-head">
      <span class="card-head-text">{{ $t('common.approvalRecord') }}</span>
    </div>
    <div class="card-record">
      <div
        class="record-item"
        v-for="(item, index) in props.approvalData"
        :key="index"
      >
        <div class="record-item-head">{{ item.approName }}</div>
        <div class="record-item-main">
          <div
            class="record-item-main-one"
            v-for="(items, ind) in item.approContent"
            :key="ind"
          >
            <div class="item-user-name">{{ items.userName }}</div>
            <div class="item-user-time">{{ items.time }}</div>
            <div class="item-user-opinion">
              {{ items.option }}
            </div>
            <div
              class="item-user-mark"
              :style="{ color: getStatusColor(items.status)?.color }"
            >
              {{ items.statusName }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<style lang="scss" scoped>
.card-head {
  display: inline-flex;
  flex: 1;
  align-items: center;
  justify-content: flex-start;
  width: 100%;
  margin-bottom: 20px;
  font-weight: 700;
  color: hsl(var(--primary));

  .card-head-text::before {
    display: inline-block;
    width: 4px;
    height: 12px;
    margin-right: 8px;
    content: ' ';
    background: hsl(var(--primary));
    border-radius: 2px;
  }
}

:where(.css-dev-only-do-not-override-14589v).ant-steps.ant-steps-horizontal:not(
    .ant-steps-label-vertical
  )
  .ant-steps-item:first-child {
  padding-inline-start: 16px !important;
}

.vertical-title-steps {
  padding: 0 30px;
  margin-bottom: 20px;
}

:deep(.ant-steps-item-container) {
  padding: 30px 25px;
}

.step-title {
  position: absolute;
  top: -30px;
  left: -85px;
  width: 110px;
  font-size: 14px;
  font-weight: 700;
  text-align: center;
}

.step-title .circular-tag div {
  max-width: 95px;

  /* 隐藏超出容器的内容 */
  overflow: hidden;

  /* 用省略号替代超出的部分 */
  text-overflow: ellipsis;

  /* 强制文本在一行内显示 */
  white-space: nowrap;
}

.ant-steps-item-description {
  position: relative;
}

.step-description {
  position: absolute;
  bottom: 0;
  left: 13px;
  width: 100px;

  /* 隐藏超出容器的内容 */
  overflow: hidden;

  /* 用省略号替代超出的部分 */
  text-overflow: ellipsis;
  text-align: center;

  /* 强制文本在一行内显示 */
  white-space: nowrap;

  /* background: red; */
}

/* 核心样式：圆形 Tag */
.circular-tag {
  /* 调整内边距，使形状更圆润美观 */
  padding: 2px 6px !important;
  background-color: #f5f5f5;

  /* 关键属性：设置圆角为很大的值实现圆形 */
  border-radius: 99px !important;

  /* 其他样式优化 */
  transition: all 0.2s ease;
}

.step-icon {
  display: inline-block;
  width: 14px;
  height: 14px;
  margin: 0 20px;
  border-radius: 50%;
}

.no-active-tag {
  background-color: hsl(var(--background));
}

.no-active {
  background-color: #eee;
}

.active {
  color: #fff;
  background-color: hsl(var(--primary));
}

.card-record {
  .record-item {
    .record-item-head {
      height: 20px;
      margin-bottom: 4px;
      overflow: hidden;
      font-size: 16px;
      line-height: 20px;
    }

    .record-item-head::before {
      display: inline-block;
      width: 10px;
      height: 10px;
      margin-right: 8px;
      content: '';
      background-color: hsl(var(--primary));
      border-radius: 100%;
    }

    .record-item-main .record-item-main-one {
      position: relative;
      box-sizing: border-box;
      display: flex;
      flex-wrap: wrap;
      padding: 10px 20px;
      margin-bottom: 15px;
      font-size: 16px;
      line-height: 24px;
      color: #333;
      background: #4591e708;
      border: 1px solid #dcedf7;

      .item-user-time {
        margin-left: 10px;
      }

      .item-user-opinion {
        flex: 1;
        margin: 0 20px;
        word-break: break-all;
        word-wrap: break-word;
      }

      .item-user-mark {
        position: relative;
        box-sizing: border-box;
        display: flex;
        flex-wrap: wrap;
        align-items: center;
        align-self: center;
        justify-content: center;
        width: 50px;
        height: 50px;
        padding: 8px;
        font-size: 16px;
        line-height: 15px;
        outline: 2px dashed;
        border-radius: 50%;
        transform: rotate(-45deg);
      }
    }
  }
}
</style>
