import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { FileApi } from '#/api/oa/file';

import { h } from 'vue';

import { IconifyIcon } from '@vben/icons';

/** 文件类型图标映射 */
export function getFileIcon(fileType: number, fileExtension?: string) {
  // 文件夹
  if (fileType === 0) {
    return h(IconifyIcon, {
      icon: 'lucide:folder',
      style: { color: '#ffa940', fontSize: '20px' },
    });
  }

  // 文件 - 根据扩展名显示不同图标
  if (!fileExtension) {
    return h(IconifyIcon, {
      icon: 'lucide:file',
      style: { color: '#8c8c8c', fontSize: '20px' },
    });
  }

  const ext = fileExtension.toLowerCase();

  // 图片
  if (['bmp', 'gif', 'jpeg', 'jpg', 'png', 'svg', 'webp'].includes(ext)) {
    return h(IconifyIcon, {
      icon: 'lucide:file-image',
      style: { color: '#52c41a', fontSize: '20px' },
    });
  }

  // Excel
  if (['csv', 'xls', 'xlsx'].includes(ext)) {
    return h(IconifyIcon, {
      icon: 'lucide:file-spreadsheet',
      style: { color: '#52c41a', fontSize: '20px' },
    });
  }

  // Word
  if (['doc', 'docx'].includes(ext)) {
    return h(IconifyIcon, {
      icon: 'lucide:file-text',
      style: { color: 'hsl(var(--primary))', fontSize: '20px' },
    });
  }

  // PDF
  if (ext === 'pdf') {
    return h(IconifyIcon, {
      icon: 'lucide:file-type',
      style: { color: '#f5222d', fontSize: '20px' },
    });
  }

  // 压缩包
  if (['7z', 'gz', 'rar', 'tar', 'zip'].includes(ext)) {
    return h(IconifyIcon, {
      icon: 'lucide:file-archive',
      style: { color: '#722ed1', fontSize: '20px' },
    });
  }

  // 文本
  if (['log', 'md', 'txt'].includes(ext)) {
    return h(IconifyIcon, {
      icon: 'lucide:file-text',
      style: { color: '#8c8c8c', fontSize: '20px' },
    });
  }

  // 默认
  return h(IconifyIcon, {
    icon: 'lucide:file',
    style: { color: '#8c8c8c', fontSize: '20px' },
  });
}

/** 格式化文件大小 */
export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '-';
  if (!bytes) return '-';

  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));

  return `${(bytes / k ** i).toFixed(2)} ${sizes[i]}`;
}

/** 新增/修改文件夹的表单 */
export function useFolderFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'id',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'parentId',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'fileType',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'ownerId',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'ownerName',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'fileName',
      label: '文件夹名称',
      rules: 'required',
      component: 'Input',
      componentProps: {
        placeholder: '请输入文件夹名称',
      },
    },
    {
      fieldName: 'sortOrder',
      label: '排序',
      component: 'Input',
      componentProps: {
        placeholder: '请输入排序（数字越小越靠前）',
        type: 'number',
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions<FileApi.FileInfo>['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    {
      field: 'fileId',
      title: '文件ID',
      minWidth: 120,
      visible: false,
    },
    {
      field: 'fileName',
      title: '名称',
      minWidth: 300,
      slots: { default: 'fileName' },
    },
    {
      field: 'fileSize',
      title: '大小',
      minWidth: 120,
      formatter: ({ row }) => {
        if (row.fileType === 0) return '-'; // 文件夹不显示大小
        return formatFileSize(row.fileSize || 0);
      },
    },
    {
      field: 'ownerName',
      title: '所有者',
      minWidth: 120,
    },
    {
      field: 'updateTime',
      title: '修改时间',
      minWidth: 160,
      formatter: 'formatDateTime',
    },
    {
      title: '操作',
      width: 300,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}

/** 搜索表单配置 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'fileName',
      label: '文件名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入文件名称',
        allowClear: true,
      },
    },
    {
      fieldName: 'fileType',
      label: '文件类型',
      component: 'Select',
      componentProps: {
        placeholder: '请选择文件类型',
        allowClear: true,
        options: [
          { label: '文件夹', value: 0 },
          { label: '文件', value: 1 },
        ],
      },
    },
    {
      fieldName: 'ownerName',
      label: '所有者',
      component: 'Input',
      componentProps: {
        placeholder: '请输入所有者',
        allowClear: true,
      },
    },
    {
      fieldName: 'createTime',
      label: '创建时间',
      component: 'RangePicker',
      componentProps: {
        placeholder: ['开始日期', '结束日期'],
        allowClear: true,
      },
    },
    {
      fieldName: 'isShared',
      label: '是否共享',
      component: 'Select',
      componentProps: {
        placeholder: '请选择是否共享',
        allowClear: true,
        options: [
          { label: '是', value: true },
          { label: '否', value: false },
        ],
      },
    },
  ];
}
