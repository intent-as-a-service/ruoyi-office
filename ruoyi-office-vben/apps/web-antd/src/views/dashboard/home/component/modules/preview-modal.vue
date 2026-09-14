<script lang="ts" setup>
import type { SystemHomeComponentApi } from '#/api/system/home/component';

import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import {
  Alert,
  Card,
  Descriptions,
  DescriptionsItem,
  Divider,
} from 'ant-design-vue';

import { hasComponent } from '../../components/registry';
import ComponentWrapper from '../../components/wrapper/component-wrapper.vue';

const componentData = ref<SystemHomeComponentApi.Component>();
const previewConfig = ref<Record<string, any>>({});

const [Modal, modalApi] = useVbenModal({
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) {
      componentData.value = undefined;
      previewConfig.value = {};
      return;
    }
    // 加载数据
    const data = modalApi.getData<SystemHomeComponentApi.Component>();
    if (!data) {
      return;
    }
    componentData.value = data;

    // 解析配置Schema并设置默认值
    if (data.configSchema) {
      try {
        const schema = JSON.parse(
          data.configSchema,
        ) as SystemHomeComponentApi.ConfigSchema;
        const config: Record<string, any> = {};
        schema.properties.forEach((prop) => {
          if (prop.default !== undefined) {
            config[prop.key] = prop.default;
          }
        });
        previewConfig.value = config;
      } catch (error) {
        console.error('解析配置Schema失败:', error);
      }
    }
  },
});

const isComponentRegistered = (code: string) => hasComponent(code);
</script>

<template>
  <Modal class="w-[900px]" title="组件预览">
    <div v-if="componentData" class="mx-4 space-y-4">
      <!-- 组件信息 -->
      <Card title="组件信息" size="small">
        <Descriptions :column="2" size="small">
          <DescriptionsItem label="组件名称">
            {{ componentData.name }}
          </DescriptionsItem>
          <DescriptionsItem label="组件编码">
            {{ componentData.code }}
          </DescriptionsItem>
          <DescriptionsItem label="组件路径" :span="2">
            {{ componentData.componentPath }}
          </DescriptionsItem>
          <DescriptionsItem label="默认尺寸">
            宽度: {{ componentData.defaultWidth }} 列 / 高度:
            {{ componentData.defaultHeight }} 行
          </DescriptionsItem>
        </Descriptions>
      </Card>

      <Divider>组件实时预览</Divider>

      <!-- 注册状态提示 -->
      <Alert
        v-if="!isComponentRegistered(componentData.code)"
        message="组件未注册"
        description="该组件尚未在前端组件注册表中注册，请检查 registry.ts 文件"
        type="warning"
        show-icon
        class="mb-4"
      />

      <!-- 组件预览区域 -->
      <Card title="预览效果" size="small">
        <div
          class="preview-container"
          :style="{
            minHeight: `${componentData.defaultHeight * 60}px`,
          }"
        >
          <ComponentWrapper
            :component-code="componentData.code"
            :config="previewConfig"
          />
        </div>
      </Card>

      <!-- 配置信息 -->
      <Card v-if="componentData.configSchema" title="配置Schema" size="small">
        <pre class="max-h-60 overflow-auto rounded bg-gray-50 p-2 text-xs">{{
          JSON.stringify(JSON.parse(componentData.configSchema), null, 2)
        }}</pre>
      </Card>
    </div>
  </Modal>
</template>

<style scoped>
.preview-container {
  padding: 16px;
  background: #fafafa;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
}
</style>
