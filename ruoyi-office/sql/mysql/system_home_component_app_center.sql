-- =============================================
-- 首页组件 - 应用中心组件初始化SQL
-- =============================================
-- 组件说明：展示常用应用，支持拖拽排序
-- 组件特点：
-- 1. 网格显示常用应用
-- 2. 支持拖拽排序
-- 3. 支持添加/删除应用
-- 4. 从有权限的菜单中选择
-- 5. 支持系统级和用户级配置
-- =============================================

-- 插入组件定义
-- 注意：id 和 category_id 需要根据实际情况调整
INSERT INTO `ruoyi-office`.system_home_component
(id, category_id, name, code, component_path, description, preview_image, default_width, default_height, config_schema, status, sort, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES(
  22,                                                           -- id: 组件ID（需要根据实际情况调整）
  2,                                                            -- category_id: 分类ID（2=工作台组件）
  '应用中心',                                                   -- name: 组件名称
  'workbench_app_center',                                       -- code: 组件编码（唯一）
  'dashboard/home/components/app-center/workbench-app-center.vue',  -- component_path: 组件路径
  '展示常用应用，支持拖拽排序和个性化配置',                    -- description: 组件描述
  NULL,                                                         -- preview_image: 预览图（可选）
  12,                                                           -- default_width: 默认宽度（12列=半宽）
  8,                                                            -- default_height: 默认高度（8行=480px）
  '{
    "properties": [
      {
        "key": "maxAppCount",
        "type": "number",
        "label": "最大显示应用数",
        "default": 12,
        "min": 4,
        "max": 24,
        "required": false
      },
      {
        "key": "gridCols",
        "type": "number",
        "label": "网格列数",
        "default": 4,
        "min": 2,
        "max": 6,
        "required": false
      },
      {
        "key": "enableDrag",
        "type": "boolean",
        "label": "启用拖拽排序",
        "default": true,
        "required": false
      },
      {
        "key": "paddingTop",
        "type": "number",
        "label": "内边距-上(px)",
        "default": 0,
        "min": 0,
        "max": 100,
        "required": false
      },
      {
        "key": "paddingRight",
        "type": "number",
        "label": "内边距-右(px)",
        "default": 0,
        "min": 0,
        "max": 100,
        "required": false
      },
      {
        "key": "paddingBottom",
        "type": "number",
        "label": "内边距-下(px)",
        "default": 0,
        "min": 0,
        "max": 100,
        "required": false
      },
      {
        "key": "paddingLeft",
        "type": "number",
        "label": "内边距-左(px)",
        "default": 0,
        "min": 0,
        "max": 100,
        "required": false
      },
      {
        "key": "marginTop",
        "type": "number",
        "label": "外边距-上(px)",
        "default": 0,
        "min": 0,
        "max": 100,
        "required": false
      },
      {
        "key": "marginRight",
        "type": "number",
        "label": "外边距-右(px)",
        "default": 0,
        "min": 0,
        "max": 100,
        "required": false
      },
      {
        "key": "marginBottom",
        "type": "number",
        "label": "外边距-下(px)",
        "default": 0,
        "min": 0,
        "max": 100,
        "required": false
      },
      {
        "key": "marginLeft",
        "type": "number",
        "label": "外边距-左(px)",
        "default": 0,
        "min": 0,
        "max": 100,
        "required": false
      }
    ]
  }',                                                           -- config_schema: 组件配置Schema（JSON格式）
  0,                                                            -- status: 状态（0=启用）
  7,                                                            -- sort: 排序（数字越小越靠前）
  '1',                                                          -- creator: 创建者
  '2026-01-09 16:00:00',                                        -- create_time: 创建时间
  '',                                                           -- updater: 更新者
  '2026-01-09 16:00:00',                                        -- update_time: 更新时间
  0,                                                            -- deleted: 是否删除
  1                                                             -- tenant_id: 租户ID
);

-- =============================================
-- 组件配置项说明
-- =============================================
-- 
-- maxAppCount: 最大显示应用数
--   - 类型: number
--   - 默认值: 12
--   - 范围: 4-24
--   - 控制网格中最多显示多少个应用
-- 
-- gridCols: 网格列数
--   - 类型: number
--   - 默认值: 4
--   - 范围: 2-6
--   - 控制每行显示多少个应用
--   - 推荐: 宽度12列时用3-4列，宽度24列时用4-6列
-- 
-- enableDrag: 启用拖拽排序
--   - 类型: boolean
--   - 默认值: true
--   - 控制是否允许用户拖拽应用进行排序
-- 
-- 内边距和外边距配置：
--   - paddingTop/Right/Bottom/Left: 组件内部内容与边框的距离
--   - marginTop/Right/Bottom/Left: 组件与其他组件之间的距离
--   - 默认值均为0，可根据实际布局需求调整
-- 
-- =============================================
-- 功能说明
-- =============================================
-- 
-- 1. 应用显示：
--    - 应用图标（支持iconify图标）
--    - 应用名称
--    - 应用颜色主题
--    - 网格布局，响应式
-- 
-- 2. 用户操作：
--    - 点击应用：跳转到对应菜单页面
--    - 拖拽排序：调整应用顺序
--    - 删除应用：从常用应用中移除
--    - 添加应用：从有权限的菜单中选择
--    - 重置配置：恢复为系统默认配置
-- 
-- 3. 权限控制：
--    - 只能选择有权限的菜单作为常用应用
--    - 自动过滤无权限的应用
--    - 支持菜单权限动态更新
-- 
-- 4. 配置层级：
--    - 系统级配置：管理员统一配置（表: system_home_app_config）
--    - 用户级配置：用户个性化配置（表: system_home_app_user）
--    - 首次使用自动从系统配置初始化
-- 
-- 5. 数据流程：
--    - 加载用户配置 -> 权限过滤 -> 按排序显示
--    - 添加应用 -> 从有权限菜单选择 -> 保存到用户配置
--    - 拖拽排序 -> 更新sort字段 -> 实时保存
--    - 重置配置 -> 清空用户配置 -> 从系统配置复制
-- 
-- 6. 视觉效果：
--    - 应用卡片hover效果
--    - 拖拽时的动画效果
--    - 删除按钮悬浮显示
--    - 拖拽手柄图标
-- 
-- 7. 推荐布局：
--    - 宽度: 12列（半宽，gridCols=3或4）或 16列（2/3宽，gridCols=4或5）
--    - 高度: 6-10行
--    - 位置: 中间内容区或右侧边栏
-- 
-- =============================================
-- 相关表结构
-- =============================================
-- 
-- 系统级应用配置表：system_home_app_config
--   - 管理员配置默认的常用应用
--   - 所有用户共享
--   - 用户首次使用时自动初始化
-- 
-- 用户级应用配置表：system_home_app_user
--   - 用户个性化配置
--   - 支持添加、删除、排序、隐藏
--   - 优先级高于系统配置
-- 
-- 详见：system_home_app_center.sql
-- 

