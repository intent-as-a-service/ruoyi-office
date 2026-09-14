<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { AttachmentApi } from '#/api/common/attachment';

import { computed, nextTick, ref, watch } from 'vue';

import { message } from 'ant-design-vue';

import { TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';

import {
  createAttachment,
  useAttachmentActions,
  useAttachmentColumns,
} from './data';

interface Props {
  /** 附件列表 */
  modelValue?: AttachmentApi.AttachmentSaveReq[];
  /** 是否只读 */
  readonly?: boolean;
  /** 最大文件数量 */
  maxCount?: number;
  /** 允许的文件类型 */
  accept?: string;
  /** 最大文件大小（MB） */
  maxSize?: number;
  /** 隐藏上传按钮（当需要在外部自定义按钮位置时使用） */
  hideUploadButton?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => [],
  readonly: false,
  maxCount: 10,
  accept: '*',
  maxSize: 10,
  hideUploadButton: false,
});

const emit = defineEmits<{
  'update:modelValue': [value: AttachmentApi.AttachmentSaveReq[]];
}>();

/** 表格内部数据 */
const tableData = ref<AttachmentApi.AttachmentSaveReq[]>([]);

/** 添加附件 */
function handleAdd(file: File) {
  const attachment = createAttachment(file, tableData.value.length + 1);
  tableData.value.push(attachment);
  handleUpdateValue();
  message.success('文件上传成功');
}

/** 删除附件 */
function handleDelete(row: AttachmentApi.AttachmentSaveReq) {
  const index = tableData.value.findIndex(
    (item) =>
      (item.id && item.id === row.id) ||
      (item.fileName === row.fileName && item.uploadTime === row.uploadTime),
  );
  if (index !== -1) {
    tableData.value.splice(index, 1);
    // 重新排序
    tableData.value.forEach((item, idx) => {
      item.sortOrder = idx + 1;
    });
    handleUpdateValue();
    message.success('删除成功');
  }
}

/** 预览附件 */
function handlePreview(row: AttachmentApi.AttachmentSaveReq) {
  window.open(row.fileUrl, '_blank');
}

/** 下载附件 */
function handleDownload(row: AttachmentApi.AttachmentSaveReq) {
  const link = document.createElement('a');
  link.href = row.fileUrl;
  link.download = row.fileName;
  link.click();
}

/** 将最新数据写回并通知父组件 */
function handleUpdateValue() {
  emit('update:modelValue', [...tableData.value]);
}

/** 备注编辑完成后更新数据 */
function handleRemarkEdit() {
  handleUpdateValue();
}

// 文件验证和处理函数
function handleFileUpload(file: File) {
  // 检查文件大小
  const isLtMaxSize = file.size / 1024 / 1024 < props.maxSize;
  if (!isLtMaxSize) {
    message.error(`文件大小不能超过 ${props.maxSize}MB`);
    return false;
  }

  // 检查文件数量
  if (tableData.value.length >= props.maxCount) {
    message.error(`最多只能上传 ${props.maxCount} 个文件`);
    return false;
  }

  // 添加文件到列表
  handleAdd(file);
  return true;
}

/** 触发文件选择（供外部调用） */
function handleTriggerUpload() {
  const input = document.createElement('input');
  input.type = 'file';
  input.multiple = true;
  input.accept = props.accept === '*' ? '' : props.accept;
  input.addEventListener('change', (e) => {
    const files = (e.target as HTMLInputElement).files;
    if (files) {
      [...files].forEach((file) => {
        handleFileUpload(file);
      });
    }
  });
  input.click();
}

// 上传按钮配置
const uploadActions = computed(() => {
  if (
    props.readonly ||
    tableData.value.length >= props.maxCount ||
    props.hideUploadButton
  ) {
    return [];
  }

  return [
    {
      label: '上传附件',
      type: 'primary' as const,
      onClick: handleTriggerUpload,
    },
  ];
});

// 暴露方法给父组件
defineExpose({
  handleTriggerUpload,
});

const [Grid, gridApi] = useVbenVxeGrid({
  gridOptions: {
    editConfig: {
      trigger: 'click',
      mode: 'cell',
    },
    columns: useAttachmentColumns(props.readonly),
    data: tableData.value,
    // 完全移除高度限制，让表格完全自适应
    height: undefined,
    maxHeight: undefined,
    border: true,
    showOverflow: true,
    autoResize: true,
    keepSource: true,
    // 禁用所有滚动相关配置
    scrollY: {
      enabled: false,
    },
    scrollX: {
      enabled: false,
    },
    // 禁用虚拟滚动
    virtualScrollY: false,
    virtualScrollX: false,
    rowConfig: {
      keyField: 'rowKey',
      isHover: true,
    },
    pagerConfig: {
      enabled: false,
    },
    toolbarConfig: {
      enabled: false,
    },
  } as VxeTableGridOptions<AttachmentApi.AttachmentSaveReq>,
  gridEvents: {
    editClosed: handleRemarkEdit,
  },
});

/** 监听 readonly 变化，动态更新列配置 */
watch(
  () => props.readonly,
  async (readonly) => {
    await nextTick();
    // 重新设置列配置
    const columns = useAttachmentColumns(readonly);
    if (columns) {
      gridApi.grid.reloadColumn(columns);
    }
  },
);

/** 监听外部传入的数据变化 */
watch(
  () => props.modelValue,
  async (attachments) => {
    if (!attachments) {
      return;
    }
    await nextTick();
    tableData.value = [...attachments];
    await gridApi.grid.reloadData(tableData.value);
  },
  {
    immediate: true,
    deep: true,
  },
);
</script>

<template>
  <div class="attachment-list">
    <!-- 上传区域 -->
    <div v-if="uploadActions.length > 0" class="mb-2 flex justify-end">
      <TableAction :actions="uploadActions" />
    </div>

    <!-- 附件列表 -->
    <div>
      <Grid class="w-full">
        <template #actions="{ row }">
          <TableAction
            :actions="
              useAttachmentActions(
                props.readonly,
                () => handlePreview(row),
                () => handleDownload(row),
                () => handleDelete(row),
              )
            "
          />
        </template>
      </Grid>
    </div>
  </div>
</template>

<style scoped>
.attachment-list {
  width: 100%;
}

.attachment-list :deep(.vxe-grid) {
  height: auto !important;
  max-height: none !important;
  padding-right: 0 !important;
  padding-left: 0 !important;
}

/* 确保按钮容器与表格对齐 */
.attachment-list > div {
  padding: 0;
  margin: 0;
}
</style>
