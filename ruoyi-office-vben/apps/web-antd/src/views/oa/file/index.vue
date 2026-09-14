<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { FileApi } from '#/api/oa/file';

import { h, onMounted, ref } from 'vue';

import { Page, useVbenModal } from '@vben/common-ui';
import { IconifyIcon } from '@vben/icons';
import { useUserStore } from '@vben/stores';
import { downloadFileFromBlobPart, isEmpty } from '@vben/utils';

import { Breadcrumb, Card, message, Modal, Progress } from 'ant-design-vue';

import { ACTION_ICON, TableAction, useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteFileInfo,
  deleteFileInfoListByIds,
  exportFileInfo,
  favoriteFile,
  getFavoriteFileList,
  getFileInfoPage,
  getFileStorageStats,
  getSharedFileList,
  getSharedSubFiles,
  renameFileInfo,
  unfavoriteFile,
  uploadFile,
  type FileStorageStats,
} from '#/api/oa/file';
import { $t } from '#/locales';

import { formatFileSize, getFileIcon, useGridColumns, useGridFormSchema } from './data';
import FileShareModal from './modules/FileShareModal.vue';
import FolderForm from './modules/folder-form.vue';

const [FolderFormModal, folderFormModalApi] = useVbenModal({
  connectedComponent: FolderForm,
  destroyOnClose: true,
});

// 分享弹窗相关
const shareModalVisible = ref(false);
const currentShareFile = ref<FileApi.FileInfo | null>(null);

const userStore = useUserStore();

// 当前路径和面包屑
const currentParentId = ref<number>(0);
const pathStack = ref<Array<{ id: number; name: string }>>([
  { id: 0, name: '我的文件' },
]);

// 视图模式：list、shared、favorite
const viewMode = ref<'favorite' | 'list' | 'shared'>('list');
// 文件类型筛选
const fileTypeFilter = ref<
  'all' | 'archive' | 'audio' | 'document' | 'image' | 'other' | 'video'
>('all');
// 当前表格标题
const tableTitle = ref('我的文件');

// 共享文件相关状态
const sharedFileState = ref({
  rootShareId: 0, // 根分享文件夹ID
  parentId: 0, // 当前父文件夹ID
});

// 文件上传进度相关
const uploadProgress = ref({
  visible: false,
  current: 0,
  total: 0,
  currentFileName: '',
  percent: 0,
  currentFilePercent: 0, // 当前文件的上传进度
});

// 存储统计信息
const storageStats = ref<FileStorageStats>({
  usedSize: 0,
  totalSize: 5 * 1024 * 1024 * 1024, // 5GB
  fileCount: 0,
  sharedFileCount: 0,
});

/** 刷新表格 */
function onRefresh() {
  gridApi.query();
}

/** 创建文件夹 */
function handleCreateFolder() {
  const folder: FileApi.FileInfo = {
    parentId: currentParentId.value,
    fileType: 0, // 文件夹（0表示文件夹，1表示文件）
    ownerId: userStore.userInfo?.id || undefined,
    ownerName: userStore.userInfo?.nickname || undefined,
    sortOrder: 0,
  };
  folderFormModalApi.setData(folder).open();
}

/** 重命名 */
async function handleRename(row: FileApi.FileInfo) {
  Modal.confirm({
    title: '重命名',
    content: h('div', [
      h('p', { style: { marginBottom: '8px' } }, '请输入新名称：'),
      h('input', {
        id: 'rename-input',
        style: {
          width: '100%',
          padding: '4px 8px',
          border: '1px solid #d9d9d9',
          borderRadius: '4px',
          fontSize: '14px',
        },
        value: row.fileName,
        onMounted: () => {
          // 聚焦并选中文本
          setTimeout(() => {
            const input = document.querySelector(
              '#rename-input',
            ) as HTMLInputElement;
            if (input) {
              input.focus();
              input.select();
            }
          }, 100);
        },
      }),
    ]),
    okText: '确定',
    cancelText: '取消',
    onOk: async () => {
      const input = document.querySelector('#rename-input') as HTMLInputElement;
      const newName = input?.value?.trim();

      if (!newName) {
        message.error('文件名不能为空');
        throw new Error('文件名不能为空');
      }

      if (newName === row.fileName) {
        return;
      }

    const hideLoading = message.loading({
      content: '重命名中...',
      key: 'action_key_msg',
    });

    try {
      await renameFileInfo(row.id as number, newName);
      message.success({
        content: '重命名成功',
        key: 'action_key_msg',
      });
      onRefresh();
    } catch (error) {
      hideLoading();
        throw error;
    }
    },
  });
}

/** 删除文件/文件夹 */
async function handleDelete(row: FileApi.FileInfo) {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting', [row.fileName]),
    key: 'action_key_msg',
  });
  try {
    await deleteFileInfo(row.id as number);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess', [row.fileName]),
      key: 'action_key_msg',
    });
    onRefresh();
    loadStorageStats(); // 刷新统计数据
  } finally {
    hideLoading();
  }
}

/** 批量删除 */
async function handleDeleteBatch() {
  const hideLoading = message.loading({
    content: $t('ui.actionMessage.deleting'),
    key: 'action_key_msg',
  });
  try {
    await deleteFileInfoListByIds(deleteIds.value);
    message.success({
      content: $t('ui.actionMessage.deleteSuccess'),
      key: 'action_key_msg',
    });
    onRefresh();
    loadStorageStats(); // 刷新统计数据
  } finally {
    hideLoading();
  }
}

const deleteIds = ref<number[]>([]); // 待删除文件/文件夹 ID
function setDeleteIds({ records }: { records: FileApi.FileInfo[] }) {
  deleteIds.value = records
    .map((item) => item.id)
    .filter((id): id is number => id !== undefined);
}

/** 下载文件 */
function handleDownload(row: FileApi.FileInfo) {
  if (row.fileType === 0) {
    message.warning('文件夹不支持下载');
    return;
  }
  if (row.fileUrl) {
    window.open(row.fileUrl, '_blank');
  } else {
    message.warning('文件URL不存在');
  }
}

/** 收藏/取消收藏 */
async function handleToggleFavorite(row: FileApi.FileInfo) {
  try {
    if (row.isFavorite) {
      await unfavoriteFile(row.id as number);
      message.success('已取消收藏');
    } else {
      await favoriteFile(row.id as number);
      message.success('收藏成功');
    }
    onRefresh();
  } catch (error) {
    console.error('操作失败:', error);
  }
}

/** 分享文件 */
function handleShare(row: FileApi.FileInfo) {
  currentShareFile.value = row;
  shareModalVisible.value = true;
}

/** 分享成功回调 */
function handleShareSuccess() {
  onRefresh();
  loadStorageStats(); // 刷新统计数据
}

/** 打开文件夹 */
function handleOpenFolder(row: FileApi.FileInfo) {
  if (row.fileType === 0) {
    if (viewMode.value === 'shared') {
      // 共享文件夹导航
      const rootShareId = (row as any).rootShareId || row.id;
      const parentId = row.fileId;

      // 更新共享文件状态
      sharedFileState.value.rootShareId = Number(rootShareId);
      sharedFileState.value.parentId = Number(parentId);

      // 初始化或更新面包屑
      if (
        pathStack.value.length === 0 ||
        pathStack.value[0]?.name !== '共享文件'
      ) {
        pathStack.value = [{ id: 0, name: '共享文件' }];
      }
      pathStack.value.push({
        id: row.id as number,
        name: row.fileName as string,
      });
    } else if (viewMode.value === 'favorite') {
      // 收藏文件夹导航
    currentParentId.value = row.id as number;

      // 初始化或更新面包屑
      if (
        pathStack.value.length === 0 ||
        pathStack.value[0]?.name !== '我的收藏'
      ) {
        pathStack.value = [{ id: 0, name: '我的收藏' }];
      }
      pathStack.value.push({
        id: row.id as number,
        name: row.fileName as string,
      });
    } else {
      // 普通文件夹导航
      currentParentId.value = row.id as number;
      pathStack.value.push({
        id: row.id as number,
        name: row.fileName as string,
      });
    }
    // 不更新 tableTitle，保持为左侧菜单的标题
    onRefresh();
  }
}

/** 获取当前视图的面包屑路径 */
function getCurrentPathStack() {
  switch (viewMode.value) {
    case 'favorite': {
      // 如果 pathStack 为空或第一个不是"我的收藏"，则初始化
      if (
        pathStack.value.length === 0 ||
        pathStack.value[0]?.name !== '我的收藏'
      ) {
        return [{ id: 0, name: '我的收藏' }];
      }
      return pathStack.value;
    }
    case 'shared': {
      // 如果 pathStack 为空或第一个不是"共享文件"，则初始化
      if (
        pathStack.value.length === 0 ||
        pathStack.value[0]?.name !== '共享文件'
      ) {
        return [{ id: 0, name: '共享文件' }];
      }
      return pathStack.value;
    }
    default: {
      return pathStack.value;
    }
  }
}

/** 面包屑导航点击 */
function handleBreadcrumbClick(index: number) {
  const currentStack = getCurrentPathStack();
  if (index === currentStack.length - 1) {
    // 点击当前层级，不需要操作
    return;
  }
  
  // 跳转到指定层级
  const target = currentStack[index];
  if (!target) return;
  
  if (viewMode.value === 'shared') {
    // 共享文件视图的面包屑导航
    if (index === 0) {
      // 点击根级别，回到共享文件列表
      sharedFileState.value.rootShareId = 0;
      sharedFileState.value.parentId = 0;
      pathStack.value = [{ id: 0, name: '共享文件' }];
    } else {
      // 点击子文件夹，需要找到对应的文件夹ID
      const targetFolder = currentStack[index];
      if (targetFolder) {
        sharedFileState.value.parentId = targetFolder.id;
        pathStack.value = pathStack.value.slice(0, index + 1);
      }
    }
  } else if (viewMode.value === 'favorite') {
    // 收藏视图的面包屑导航
    if (index === 0) {
      // 点击根级别，回到收藏列表
      currentParentId.value = 0;
      pathStack.value = [{ id: 0, name: '我的收藏' }];
    } else {
      // 点击子文件夹
      const targetFolder = currentStack[index];
      if (targetFolder) {
        currentParentId.value = targetFolder.id;
        pathStack.value = pathStack.value.slice(0, index + 1);
      }
    }
  } else {
    // 我的文件视图的面包屑导航
  currentParentId.value = target.id;
  pathStack.value = pathStack.value.slice(0, index + 1);
  }
  // 不更新 tableTitle，保持为左侧菜单的标题
  onRefresh();
}

/** 切换到我的文件视图 */
function handleShowMyFiles() {
  viewMode.value = 'list';
  currentParentId.value = 0;
  pathStack.value = [{ id: 0, name: '我的文件' }];
  tableTitle.value = '我的文件';
  fileTypeFilter.value = 'all';
  // 重置共享文件状态
  sharedFileState.value.rootShareId = 0;
  sharedFileState.value.parentId = 0;
  onRefresh();
}

/** 切换到共享文件视图 */
function handleShowSharedFiles() {
  viewMode.value = 'shared';
  currentParentId.value = 0;
  pathStack.value = [{ id: 0, name: '共享文件' }];
  tableTitle.value = '共享文件';
  fileTypeFilter.value = 'all';
  // 重置共享文件状态（回到根级别）
  sharedFileState.value.rootShareId = 0;
  sharedFileState.value.parentId = 0;
  onRefresh();
}

/** 切换到收藏视图 */
function handleShowFavorites() {
  viewMode.value = 'favorite';
  currentParentId.value = 0;
  pathStack.value = [{ id: 0, name: '我的收藏' }];
  tableTitle.value = '我的收藏';
  fileTypeFilter.value = 'all';
  // 重置共享文件状态
  sharedFileState.value.rootShareId = 0;
  sharedFileState.value.parentId = 0;
  onRefresh();
}

/** 按文件类型筛选 */
function handleFilterByType(
  type: 'all' | 'archive' | 'audio' | 'document' | 'image' | 'other' | 'video',
) {
  fileTypeFilter.value = type;
  onRefresh();
}

/** 根据视图模式和权限获取操作按钮 */
function getActionsByViewMode(row: FileApi.FileInfo) {
  const baseActions = [
    {
      label: '打开',
      type: 'link' as const,
      icon: ACTION_ICON.VIEW,
      onClick: handleOpenFolder.bind(null, row),
      ifShow: row.fileType === 0,
    },
    {
      label: '下载',
      type: 'link' as const,
      icon: ACTION_ICON.DOWNLOAD,
      onClick: handleDownload.bind(null, row),
      ifShow: row.fileType === 1,
    },
  ];

  if (viewMode.value === 'favorite') {
    // 我的收藏：只能打开、下载、取消收藏
    return [
      ...baseActions,
      {
        label: '取消收藏',
        type: 'link' as const,
        icon: 'ant-design:star-filled',
        onClick: handleToggleFavorite.bind(null, row),
        ifShow: true,
      },
    ];
  } else if (viewMode.value === 'shared') {
    // 共享文件：根据权限控制操作
    const sharedRow = row as any;
    const userPermission = sharedRow.userPermission ?? 0; // 0=仅查看，1=可管理
    const canManage = userPermission === 1;

    return canManage
      ? // 管理权限：所有操作都可用
        [
          ...baseActions,
          {
            label: '重命名',
            type: 'link' as const,
            icon: ACTION_ICON.EDIT,
            onClick: handleRename.bind(null, row),
            ifShow: true,
          },
          {
            label: row.isFavorite ? '取消收藏' : '收藏',
            type: 'link' as const,
            icon: row.isFavorite
              ? 'ant-design:star-filled'
              : 'ant-design:star-outlined',
            onClick: handleToggleFavorite.bind(null, row),
            ifShow: true,
          },
          {
            label: '分享',
            type: 'link' as const,
            icon: 'ant-design:share-alt-outlined',
            onClick: handleShare.bind(null, row),
            ifShow: true,
          },
          {
            label: $t('common.delete'),
            type: 'link' as const,
            icon: ACTION_ICON.DELETE,
            danger: true,
            ifShow: true,
            popConfirm: {
              title: $t('ui.actionMessage.deleteConfirm', [row.fileName]),
              confirm: handleDelete.bind(null, row),
            },
          } as any,
        ]
      : // 查看权限：只能打开、下载、收藏
        [
          ...baseActions,
          {
            label: row.isFavorite ? '取消收藏' : '收藏',
            type: 'link' as const,
            icon: row.isFavorite
              ? 'ant-design:star-filled'
              : 'ant-design:star-outlined',
            onClick: handleToggleFavorite.bind(null, row),
            ifShow: true,
          },
        ];
  } else {
    // 我的文件：所有操作都可用
    return [
      ...baseActions,
      {
        label: '重命名',
        type: 'link' as const,
        icon: ACTION_ICON.EDIT,
        onClick: handleRename.bind(null, row),
        ifShow: true,
      },
      {
        label: row.isFavorite ? '取消收藏' : '收藏',
        type: 'link' as const,
        icon: row.isFavorite
          ? 'ant-design:star-filled'
          : 'ant-design:star-outlined',
        onClick: handleToggleFavorite.bind(null, row),
        ifShow: true,
      },
      {
        label: '分享',
        type: 'link' as const,
        icon: 'ant-design:share-alt-outlined',
        onClick: handleShare.bind(null, row),
        ifShow: true,
      },
      {
        label: $t('common.delete'),
        type: 'link' as const,
        icon: ACTION_ICON.DELETE,
        danger: true,
        ifShow: true,
        popConfirm: {
          title: $t('ui.actionMessage.deleteConfirm', [row.fileName]),
          confirm: handleDelete.bind(null, row),
        },
      } as any,
    ];
  }
}

/** 文件上传处理 */
async function handleUploadFile() {
  // 创建文件输入元素
  const input = document.createElement('input');
  input.type = 'file';
  input.multiple = true;
  input.accept = '*/*';

  input.addEventListener('change', async (event) => {
    const files = (event.target as HTMLInputElement).files;
    if (!files || files.length === 0) return;

    // 检查文件大小限制（100MB = 100 * 1024 * 1024 bytes）
    const maxSize = 100 * 1024 * 1024;
    const oversizedFiles = [...files].filter((file) => file.size > maxSize);

    if (oversizedFiles.length > 0) {
      message.error('上传文件不能超过100M，请压缩后上传');
      return;
    }

    // 显示上传进度弹窗
    uploadProgress.value = {
      visible: true,
      current: 0,
      total: files.length,
      currentFileName: '',
      percent: 0,
      currentFilePercent: 0,
    };

    try {
      // 逐个上传文件
      for (let i = 0; i < files.length; i++) {
        const file = files[i];
        if (!file) continue;

        uploadProgress.value.current = i + 1;
        uploadProgress.value.currentFileName = file.name;
        uploadProgress.value.currentFilePercent = 0;

        // 计算总体进度（基于已完成的文件数量）
        const basePercent = (i / files.length) * 100;

        await uploadFile(file, currentParentId.value, (progressEvent) => {
          // 计算当前文件的上传进度
          if (progressEvent.total) {
            const currentFileProgress = Math.round(
              (progressEvent.loaded / progressEvent.total) * 100,
            );
            uploadProgress.value.currentFilePercent = currentFileProgress;

            // 计算总体进度：已完成文件的进度 + 当前文件的进度
            const currentFileContribution =
              (currentFileProgress / 100) * (100 / files.length);
            uploadProgress.value.percent = Math.round(
              basePercent + currentFileContribution,
            );
          }
        });

        // 当前文件上传完成，更新进度为该文件完成的状态
        uploadProgress.value.percent = Math.round(
          ((i + 1) / files.length) * 100,
        );
        uploadProgress.value.currentFilePercent = 100;
      }

      // 上传完成
      uploadProgress.value.visible = false;
      message.success(`成功上传 ${files.length} 个文件`);

      // 刷新表格和统计数据
      onRefresh();
      loadStorageStats();
    } catch (error: any) {
      // 上传失败
      uploadProgress.value.visible = false;

      // 根据错误类型显示不同的错误信息
      let errorMessage = '文件上传失败';
      if (error?.response?.status === 413) {
        errorMessage = '文件太大，请选择较小的文件上传';
      } else if (
        error?.response?.status === 408 ||
        error?.code === 'ECONNABORTED'
      ) {
        errorMessage = '上传超时，请检查网络连接或选择较小的文件';
      } else if (error?.response?.data?.msg) {
        errorMessage = `上传失败：${error.response.data.msg}`;
      } else if (error?.message) {
        errorMessage = `上传失败：${error.message}`;
      }

      message.error(errorMessage);
      console.error('文件上传失败:', error);
    }
  });

  // 触发文件选择
  input.click();
}

/** 导出表格 */
async function handleExport() {
  const data = await exportFileInfo({ parentId: currentParentId.value });
  downloadFileFromBlobPart({ fileName: '企业云盘.xls', source: data });
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
    pagerConfig: {
      enabled: true,
    },
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) => {
          if (viewMode.value === 'favorite') {
            if (currentParentId.value === 0) {
              let data = await getFavoriteFileList();
              // 前端过滤：文件分类、文件名称、文件类型、所有者
              data = data.filter((item: FileApi.FileInfo) => {
                // 文件分类过滤
                if (
                  fileTypeFilter.value !== 'all' &&
                  item.fileCategory !== fileTypeFilter.value
                ) {
                  return false;
                }
                // 文件名称过滤
                if (
                  formValues.fileName &&
                  !item.fileName?.includes(formValues.fileName)
                ) {
                  return false;
                }
                // 文件类型过滤
                if (
                  formValues.fileType !== undefined &&
                  item.fileType !== formValues.fileType
                ) {
                  return false;
                }
                // 所有者过滤
                if (
                  formValues.ownerName &&
                  !item.ownerName?.includes(formValues.ownerName)
                ) {
                  return false;
                }
                return true;
              });
            return {
                list: data,
                total: data.length,
            };
          } else {
            const queryParams = {
              pageNo: page.currentPage,
              pageSize: page.pageSize,
                parentId: currentParentId.value,
                fileCategoryFilter:
                  fileTypeFilter.value === 'all'
                    ? undefined
                    : fileTypeFilter.value,
                ...formValues,
              };
              return await getFileInfoPage(queryParams);
            }
          } else if (viewMode.value === 'shared') {
            // 共享文件视图
            const { rootShareId, parentId } = sharedFileState.value;

            let data =
              rootShareId === 0
                ? await getSharedFileList() // 获取共享文件夹下的子文件
                : await getSharedSubFiles(rootShareId, parentId); // 获取根级别共享文件

            // 前端过滤：文件分类、文件名称、文件类型、所有者
            data = data.filter((item: any) => {
              // 文件分类过滤
              if (
                fileTypeFilter.value !== 'all' &&
                item.fileCategory !== fileTypeFilter.value
              ) {
                return false;
              }
              // 文件名称过滤
              if (
                formValues.fileName &&
                !item.fileName?.includes(formValues.fileName)
              ) {
                return false;
              }
              // 文件类型过滤
              if (
                formValues.fileType !== undefined &&
                item.fileType !== formValues.fileType
              ) {
                return false;
              }
              // 所有者过滤
              if (
                formValues.ownerName &&
                !item.ownerName?.includes(formValues.ownerName)
              ) {
                return false;
              }
              return true;
            });

            return {
              list: data.map((item) => ({ ...item, id: item.fileId })),
              total: data.length,
            };
          } else {
            const queryParams = {
              pageNo: page.currentPage,
              pageSize: page.pageSize,
              parentId: currentParentId.value,
              fileCategoryFilter:
                fileTypeFilter.value === 'all'
                  ? undefined
                  : fileTypeFilter.value,
              ...formValues,
            };
            return await getFileInfoPage(queryParams);
          }
        },
      },
    },
    rowConfig: {
      keyField: 'id',
      isHover: true,
    },
    toolbarConfig: {
      refresh: { code: 'query' },
      search: false,
    },
  } as VxeTableGridOptions<FileApi.FileInfo>,
  gridEvents: {
    checkboxAll: setDeleteIds,
    checkboxChange: setDeleteIds,
    cellDblclick: ({ row }: { row: FileApi.FileInfo }) => {
      handleOpenFolder(row);
    },
  },
});

// 组件挂载时加载数据
// 加载存储统计信息
async function loadStorageStats() {
  try {
    const stats = await getFileStorageStats();
    storageStats.value = stats;
  } catch (error) {
    console.error('加载存储统计信息失败:', error);
  }
}

onMounted(() => {
  onRefresh();
  loadStorageStats();
});
</script>

<template>
  <Page auto-content-height>
    <FolderFormModal @success="onRefresh" />

    <!-- 文件上传进度弹窗 -->
    <Modal
      v-model:open="uploadProgress.visible"
      title="文件上传中"
      :closable="false"
      :mask-closable="false"
      :footer="null"
      :width="400"
    >
      <div class="upload-progress-content">
        <div class="mb-4">
          <div class="mb-2 text-sm text-gray-600">
            正在上传: {{ uploadProgress.currentFileName }}
          </div>
          <div class="mb-2 text-xs text-gray-500">
            进度: {{ uploadProgress.current }} / {{ uploadProgress.total }}
          </div>
        </div>

        <!-- 总体上传进度 -->
        <Progress
          :percent="uploadProgress.percent"
          :show-info="true"
          status="active"
        />

        <div class="mt-2 text-center text-xs text-gray-500">
          请勿关闭页面，正在上传文件...
        </div>
      </div>
    </Modal>

    <div class="flex h-full gap-4">
      <!-- 左侧导航 -->
      <div class="w-64 flex-shrink-0 space-y-4">
        <!-- 我的Cloud区域 -->
        <Card title="我的Cloud" size="small">
          <div class="space-y-3">
            <!-- 存储信息 -->
            <div class="text-sm text-gray-600">
              <div class="mb-2 flex items-center gap-2">
                <IconifyIcon icon="lucide:cloud" class="size-4 text-blue-500" />
                <span class="font-medium">存储空间</span>
              </div>
              <div class="mb-1 h-2 rounded-full bg-gray-100">
                <div
                  class="storage-progress h-2 rounded-full"
                  :style="{
                    width: `${Math.min((storageStats.usedSize / storageStats.totalSize) * 100, 100)}%`,
                  }"
                ></div>
              </div>
              <div class="flex justify-between text-xs text-gray-500">
                <span>已用: {{ formatFileSize(storageStats.usedSize) }}</span>
                <span>总计: {{ formatFileSize(storageStats.totalSize) }}</span>
              </div>
            </div>
            
            <!-- 统计信息 -->
            <div class="grid grid-cols-2 gap-2 text-xs">
              <div class="stat-card rounded bg-gray-50 p-2">
                <div class="text-gray-500">文件数量</div>
                <div class="font-medium text-blue-600">{{ storageStats.fileCount }}</div>
              </div>
              <div class="stat-card rounded bg-gray-50 p-2">
                <div class="text-gray-500">共享文件</div>
                <div class="font-medium text-green-600">{{ storageStats.sharedFileCount }}</div>
              </div>
            </div>
          </div>
        </Card>

        <!-- 文件筛选区域 -->
        <Card title="文件筛选" size="small" class="flex-1">
          <div class="space-y-2">
            <div
              class="menu-item flex cursor-pointer items-center gap-2 p-2 transition-colors"
              :class="[viewMode === 'list' ? 'menu-item-active' : '']"
              @click="handleShowMyFiles"
            >
              <IconifyIcon icon="lucide:folder" class="size-4" />
              <span>我的文件</span>
            </div>
            <div
              class="menu-item flex cursor-pointer items-center gap-2 p-2 transition-colors"
              :class="[viewMode === 'shared' ? 'menu-item-active' : '']"
              @click="handleShowSharedFiles"
            >
              <IconifyIcon icon="lucide:users" class="size-4" />
              <span>共享文件</span>
            </div>
            <div
              class="menu-item flex cursor-pointer items-center gap-2 p-2 transition-colors"
              :class="[viewMode === 'favorite' ? 'menu-item-active' : '']"
              @click="handleShowFavorites"
            >
              <IconifyIcon icon="lucide:star" class="size-4" />
              <span>我的收藏</span>
            </div>
            
            <!-- 分隔线 -->
            <div class="my-3 border-t border-gray-200"></div>
            
            <!-- 文件类型筛选 -->
            <div class="text-muted-foreground mb-2 text-xs">按分类筛选</div>
            <div
              class="menu-item flex cursor-pointer items-center gap-2 p-2 transition-colors"
              :class="[fileTypeFilter === 'all' ? 'menu-item-active' : '']"
              @click="handleFilterByType('all')"
            >
              <IconifyIcon icon="lucide:file" class="size-4" />
              <span>全部</span>
            </div>
            <div
              class="menu-item flex cursor-pointer items-center gap-2 p-2 transition-colors"
              :class="[fileTypeFilter === 'image' ? 'menu-item-active' : '']"
              @click="handleFilterByType('image')"
            >
              <IconifyIcon icon="lucide:image" class="size-4" />
              <span>图片</span>
            </div>
            <div
              class="menu-item flex cursor-pointer items-center gap-2 p-2 transition-colors"
              :class="[fileTypeFilter === 'document' ? 'menu-item-active' : '']"
              @click="handleFilterByType('document')"
            >
              <IconifyIcon icon="lucide:file-text" class="size-4" />
              <span>文档</span>
            </div>
            <div
              class="menu-item flex cursor-pointer items-center gap-2 p-2 transition-colors"
              :class="[fileTypeFilter === 'video' ? 'menu-item-active' : '']"
              @click="handleFilterByType('video')"
            >
              <IconifyIcon icon="lucide:video" class="size-4" />
              <span>视频</span>
            </div>
            <div
              class="menu-item flex cursor-pointer items-center gap-2 p-2 transition-colors"
              :class="[fileTypeFilter === 'audio' ? 'menu-item-active' : '']"
              @click="handleFilterByType('audio')"
            >
              <IconifyIcon icon="lucide:music" class="size-4" />
              <span>音频</span>
            </div>
            <div
              class="menu-item flex cursor-pointer items-center gap-2 p-2 transition-colors"
              :class="[fileTypeFilter === 'archive' ? 'menu-item-active' : '']"
              @click="handleFilterByType('archive')"
            >
              <IconifyIcon icon="lucide:archive" class="size-4" />
              <span>压缩包</span>
            </div>
            <div
              class="menu-item flex cursor-pointer items-center gap-2 p-2 transition-colors"
              :class="[fileTypeFilter === 'other' ? 'menu-item-active' : '']"
              @click="handleFilterByType('other')"
            >
              <IconifyIcon icon="lucide:file-question" class="size-4" />
              <span>其他</span>
            </div>
          </div>
        </Card>
      </div>

      <!-- 右侧内容 -->
      <div class="min-w-0 flex-1">
        <Grid>
          <template #toolbar-tools>
            <div class="toolbar-content">
              <!-- 面包屑导航 -->
              <div class="breadcrumb-section">
                <Breadcrumb>
                  <Breadcrumb.Item 
                    v-for="(path, index) in getCurrentPathStack()"
                    :key="path.id"
                  >
                    <span
                      class="inline-flex items-center gap-1"
                      :class="[
                        index === getCurrentPathStack().length - 1
                          ? 'cursor-default text-gray-600'
                          : 'cursor-pointer text-blue-500 hover:text-blue-700',
                      ]"
                      @click="handleBreadcrumbClick(index)"
                    >
                      <span>{{ path.name }}</span>
                    </span>
                  </Breadcrumb.Item>
                </Breadcrumb>
              </div>
              
              <!-- 操作按钮 -->
              <div class="actions-section">
                <TableAction
              :actions="[
                {
                  label: '新建文件夹',
                  type: 'primary',
                  icon: 'ant-design:folder-add-outlined',
                  onClick: handleCreateFolder,
                  ifShow: viewMode === 'list',
                },
                {
                  label: '上传文件',
                  type: 'primary',
                  icon: ACTION_ICON.UPLOAD,
                      onClick: handleUploadFile,
                  ifShow: viewMode === 'list' || viewMode === 'shared',
                },
                {
                  label: $t('ui.actionTitle.deleteBatch'),
                  type: 'primary',
                  danger: true,
                  icon: ACTION_ICON.DELETE,
                  disabled: isEmpty(deleteIds),
                  onClick: handleDeleteBatch,
                },
                {
                  label: $t('ui.actionTitle.export'),
                  type: 'primary',
                  icon: ACTION_ICON.DOWNLOAD,
                  onClick: handleExport,
                },
              ]"
                />
              </div>
            </div>
          </template>
          <template #fileName="{ row }">
            <div class="flex items-center gap-2">
              <component
                :is="getFileIcon(row.fileType || 0, row.fileExtension || '')"
              />
              <span
                :class="{
                  'cursor-pointer hover:text-blue-500': row.fileType === 0,
                }"
                @click="row.fileType === 0 ? handleOpenFolder(row) : undefined"
              >
                {{ row.fileName }}
              </span>
            </div>
          </template>
          <template #actions="{ row }">
            <TableAction :actions="getActionsByViewMode(row)" />
          </template>
        </Grid>
      </div>
    </div>

    <!-- 分享弹窗 -->
    <FileShareModal
      v-model:visible="shareModalVisible"
      :file-info="currentShareFile"
      @success="handleShareSuccess"
    />
  </Page>
</template>

<style scoped>
/* 左侧导航样式优化 */
:deep(.ant-card-body) {
  padding: 12px;
}

/* 存储进度条样式 - 使用主题色 */
.storage-progress {
  background: linear-gradient(
    90deg,
    hsl(var(--primary)) 0%,
    hsl(var(--primary) / 80%) 100%
  );
}

/* 统一菜单项样式 */
.menu-item {
  position: relative;
  border-radius: 0; /* 去掉圆角，与其他模块保持一致 */
  transition: all 0.2s ease;
}

/* 悬停状态 - 使用 accent 色 */
.menu-item:hover {
  color: hsl(var(--primary)) !important;
  background-color: hsl(var(--accent)) !important;
  transform: translateX(2px);
}

/* 激活状态 - 使用主题变量 */
.menu-item-active {
  font-weight: 500 !important;
  color: hsl(var(--primary)) !important;
  background-color: hsl(var(--primary) / 10%) !important;
}

/* 去掉右侧蓝线，保持与其他模块一致 */
.menu-item-active::after {
  display: none;
}

/* 统计卡片样式 */
.stat-card {
  transition: all 0.2s ease;
}

.stat-card:hover {
  box-shadow: 0 2px 8px hsl(var(--foreground) / 10%);
  transform: translateY(-1px);
}

/* Grid 工具栏布局调整 */
:deep(.vxe-grid .vxe-toolbar) {
  display: flex;
  align-items: center;
  padding: 8px 16px;
}

/* 固定 table-title 宽度，去掉右边距 */
:deep(.vxe-grid .vxe-toolbar .vxe-buttons--wrapper) {
  flex-grow: 0 !important; /* 覆盖全局的 flex-grow: 1 */
  flex-shrink: 0;
  width: auto !important;
  margin-right: 0 !important;
}

/* 让 toolbar-tools 区域扩展，去掉左边距 */
:deep(.vxe-grid .vxe-toolbar .vxe-tools--wrapper) {
  display: flex;
  flex: 1;
  align-items: center;
  margin-left: 0 !important;
}

/* 工具栏内容布局 */
.toolbar-content {
  display: flex;
  gap: 0; /* 去掉间隙 */
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.breadcrumb-section {
  display: flex;
  flex: 1;
  align-items: center;
  min-width: 0;
  margin-left: 8px; /* 添加小间距，让面包屑紧靠标题 */
}

.actions-section {
  flex-shrink: 0;
}

/* 面包屑样式 */
.breadcrumb-section :deep(.ant-breadcrumb) {
  margin: 0;
  font-size: 1rem; /* 与标题字体大小保持一致 */
}

.breadcrumb-section :deep(.ant-breadcrumb-link) {
  display: inline-flex;
  align-items: center;
}

.breadcrumb-section :deep(.ant-breadcrumb-separator) {
  margin: 0 8px;
  color: #d9d9d9;
}

/* 确保面包屑在工具栏中垂直居中 */
.breadcrumb-section :deep(.ant-breadcrumb-item) {
  display: inline-flex;
  align-items: center;
}
</style>
