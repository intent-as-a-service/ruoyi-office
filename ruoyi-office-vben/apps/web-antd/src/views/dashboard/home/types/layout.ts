/**
 * 首页布局相关类型定义
 */

/**
 * 网格布局项
 */
export interface GridLayoutItem {
  /** 唯一标识 */
  i: string;
  /** X轴位置（网格列，0-23） */
  x: number;
  /** Y轴位置（网格行） */
  y: number;
  /** 宽度（网格列数，1-24） */
  w: number;
  /** 高度（网格行数） */
  h: number;
  /** 组件编码 */
  componentCode: string;
  /** 组件配置 */
  config: Record<string, any>;
  /** 是否可拖拽 */
  isDraggable?: boolean;
  /** 是否可调整大小 */
  isResizable?: boolean;
  /** 最小宽度 */
  minW?: number;
  /** 最小高度 */
  minH?: number;
  /** 最大宽度 */
  maxW?: number;
  /** 最大高度 */
  maxH?: number;
  /** 是否静态（不可移动和调整大小） */
  static?: boolean;
}

/**
 * 完整的布局配置
 */
export interface LayoutConfig {
  /** 布局项列表 */
  items: GridLayoutItem[];
  /** 网格列数（默认24） */
  colNum?: number;
  /** 行高（像素） */
  rowHeight?: number;
  /** 是否可拖拽（全局设置） */
  isDraggable?: boolean;
  /** 是否可调整大小（全局设置） */
  isResizable?: boolean;
  /** 边距 [x, y] */
  margin?: [number, number];
  /** 容器内边距 [x, y] */
  containerPadding?: [number, number];
  /** 是否垂直压缩 */
  verticalCompact?: boolean;
  /** 是否阻止碰撞（允许重叠） */
  preventCollision?: boolean;
  /** 是否使用CSS transform */
  useCssTransforms?: boolean;
}

/**
 * 设计器状态
 */
export interface DesignerState {
  /** 当前选中的布局项ID */
  selectedItemId: null | string;
  /** 布局配置 */
  layout: LayoutConfig;
  /** 是否正在编辑 */
  isEditing: boolean;
  /** 是否显示网格线 */
  showGrid: boolean;
}

/**
 * 组件库项（用于组件面板）
 */
export interface ComponentLibraryItem {
  id: number;
  categoryId: number;
  name: string;
  code: string;
  description?: string;
  previewImage?: string;
  defaultWidth: number;
  defaultHeight: number;
  configSchema: string;
  status: number;
  sort: number;
}

/**
 * 组件分类
 */
export interface ComponentCategory {
  id: number;
  name: string;
  code: string;
  icon: string;
  sort: number;
}
