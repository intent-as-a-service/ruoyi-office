import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { AttachmentApi } from '#/api/common/attachment';

import { ACTION_ICON } from '#/adapter/vxe-table';

/**
 * 格式化文件大小
 * @param size 文件大小（字节）
 * @returns 格式化后的文件大小字符串
 */
export function formatFileSize(size: number): string {
  if (size < 1024) return `${size}B`;
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)}KB`;
  return `${(size / (1024 * 1024)).toFixed(1)}MB`;
}

/**
 * 附件列表表格列配置
 * @param readonly 是否只读模式
 */
export function useAttachmentColumns(readonly: boolean = false): VxeTableGridOptions['columns'] {
  return [
    {
      type: 'seq',
      width: 60,
      title: '序号',
    },
    {
      field: 'fileName',
      title: '文件名',
      minWidth: 200,
      showOverflow: 'tooltip',
    },
    {
      field: 'fileSize',
      title: '文件大小',
      width: 120,
      formatter: ({ cellValue }) => {
        return formatFileSize(cellValue || 0);
      },
    },
    {
      field: 'fileExtension',
      title: '文件类型',
      width: 100,
      formatter: ({ cellValue }) => {
        return cellValue ? cellValue.toUpperCase() : '';
      },
    },
    {
      field: 'uploadTime',
      title: '上传时间',
      width: 160,
      formatter: ({ row }) => {
        // 如果 uploadTime 为 0 或空，则使用 createTime
        const time = row.uploadTime && row.uploadTime !== 0 ? row.uploadTime : row.createTime;
        if (!time) return '';
        
        // 格式化时间显示
        const date = new Date(time);
        return date.toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit',
        });
      },
    },
    {
      field: 'remark',
      title: '备注',
      minWidth: 150,
      showOverflow: 'tooltip',
      // 只在非只读模式下启用编辑功能
      editRender: readonly ? undefined : {
        name: 'input',
        placeholder: '请输入备注',
      },
    },
    {
      title: '操作',
      width: 180,
      fixed: 'right',
      slots: {
        default: 'actions',
      },
    },
  ];
}

/**
 * 附件操作按钮配置
 */
export function useAttachmentActions(
  readonly: boolean,
  onPreview: () => void,
  onDownload: () => void,
  onDelete: () => void,
) {
  return [
    {
      label: '预览',
      type: 'link' as const,
      icon: ACTION_ICON.VIEW,
      onClick: onPreview,
    },
    {
      label: '下载',
      type: 'link' as const,
      icon: ACTION_ICON.DOWNLOAD,
      onClick: onDownload,
    },
    {
      label: '删除',
      type: 'link' as const,
      danger: true,
      icon: ACTION_ICON.DELETE,
      ifShow: () => !readonly,
      popConfirm: {
        title: '确定要删除这个附件吗？',
        confirm: onDelete,
      },
    },
  ];
}

/**
 * 创建新附件对象
 */
export function createAttachment(
  file: File,
  sortOrder: number,
): AttachmentApi.AttachmentSaveReq {
  return {
    id: undefined, // 新文件没有ID，后端会自动生成
    businessType: '',
    businessId: 0,
    fileName: file.name,
    filePath: `/uploads/${file.name}`, // 实际应该是上传后返回的路径
    fileUrl: URL.createObjectURL(file), // 实际应该是上传后返回的URL
    fileSize: file.size,
    fileType: file.type,
    fileExtension: file.name.split('.').pop() || '',
    uploadTime: new Date(),
    sortOrder,
    remark: '',
  };
}
