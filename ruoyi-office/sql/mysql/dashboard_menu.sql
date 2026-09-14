-- =============================================
-- Dashboard 模块菜单SQL
-- 执行时间：2026-01-02
-- 说明：将 dashboard.ts 路由配置转换为系统菜单
-- =============================================

-- 注意：执行前请确认父菜单ID，如果Dashboard菜单已存在，请先查询其ID
-- SELECT id FROM system_menu WHERE path = '/dashboard' AND deleted = 0;

-- 1. Dashboard 父菜单（目录类型）
-- 如果已存在，请跳过此条插入，使用现有ID作为parent_id
INSERT INTO `system_menu` (
  `name`, 
  `permission`, 
  `type`, 
  `sort`, 
  `parent_id`, 
  `path`, 
  `icon`, 
  `component`, 
  `component_name`, 
  `status`, 
  `visible`, 
  `keep_alive`, 
  `always_show`, 
  `creator`, 
  `create_time`, 
  `updater`, 
  `update_time`, 
  `deleted`
) VALUES (
  '工作台',  -- 菜单名称（根据 $t('page.dashboard.title') 翻译）
  '',  -- 权限标识（目录类型为空）
  1,  -- 菜单类型：1=目录
  -1,  -- 显示顺序（order: -1）
  0,  -- 父菜单ID（顶级菜单）
  '/dashboard',  -- 路由地址
  'lucide:layout-dashboard',  -- 菜单图标
  NULL,  -- 组件路径（目录类型为空）
  NULL,  -- 组件名（目录类型为空）
  0,  -- 菜单状态：0=启用
  b'1',  -- 是否可见：1=可见
  b'1',  -- 是否缓存：1=缓存
  b'1',  -- 是否总是显示：1=总是显示
  'admin',  -- 创建者
  NOW(),  -- 创建时间
  'admin',  -- 更新者
  NOW(),  -- 更新时间
  b'0'  -- 是否删除：0=未删除
);

-- 获取刚插入的Dashboard菜单ID（如果手动执行，请替换为实际ID）
SET @dashboard_parent_id = LAST_INSERT_ID();

-- 如果Dashboard菜单已存在，请手动设置parent_id：
-- SET @dashboard_parent_id = (SELECT id FROM system_menu WHERE path = '/dashboard' AND deleted = 0 LIMIT 1);

-- 2. 工作台子菜单
INSERT INTO `system_menu` (
  `name`, 
  `permission`, 
  `type`, 
  `sort`, 
  `parent_id`, 
  `path`, 
  `icon`, 
  `component`, 
  `component_name`, 
  `status`, 
  `visible`, 
  `keep_alive`, 
  `always_show`, 
  `creator`, 
  `create_time`, 
  `updater`, 
  `update_time`, 
  `deleted`
) VALUES (
  '工作台',  -- 菜单名称（根据 $t('page.dashboard.workspace') 翻译）
  '',  -- 权限标识（菜单类型可为空）
  2,  -- 菜单类型：2=菜单
  1,  -- 显示顺序
  @dashboard_parent_id,  -- 父菜单ID
  '/workspace',  -- 路由地址
  'carbon:workspace',  -- 菜单图标
  'dashboard/workspace/index',  -- 组件路径
  'Workspace',  -- 组件名
  0,  -- 菜单状态：0=启用
  b'1',  -- 是否可见：1=可见
  b'1',  -- 是否缓存：1=缓存
  b'1',  -- 是否总是显示：1=总是显示
  'admin',  -- 创建者
  NOW(),  -- 创建时间
  'admin',  -- 更新者
  NOW(),  -- 更新时间
  b'0'  -- 是否删除：0=未删除
);

-- 3. 数据分析子菜单
INSERT INTO `system_menu` (
  `name`, 
  `permission`, 
  `type`, 
  `sort`, 
  `parent_id`, 
  `path`, 
  `icon`, 
  `component`, 
  `component_name`, 
  `status`, 
  `visible`, 
  `keep_alive`, 
  `always_show`, 
  `creator`, 
  `create_time`, 
  `updater`, 
  `update_time`, 
  `deleted`
) VALUES (
  '数据分析',  -- 菜单名称（根据 $t('page.dashboard.analytics') 翻译）
  '',  -- 权限标识
  2,  -- 菜单类型：2=菜单
  2,  -- 显示顺序
  @dashboard_parent_id,  -- 父菜单ID
  '/analytics',  -- 路由地址
  'lucide:area-chart',  -- 菜单图标
  'dashboard/analytics/index',  -- 组件路径
  'Analytics',  -- 组件名
  0,  -- 菜单状态：0=启用
  b'1',  -- 是否可见：1=可见
  b'1',  -- 是否缓存：1=缓存（affixTab: true 表示固定标签）
  b'1',  -- 是否总是显示：1=总是显示
  'admin',  -- 创建者
  NOW(),  -- 创建时间
  'admin',  -- 更新者
  NOW(),  -- 更新时间
  b'0'  -- 是否删除：0=未删除
);

-- 4. 我的首页子菜单
INSERT INTO `system_menu` (
  `name`, 
  `permission`, 
  `type`, 
  `sort`, 
  `parent_id`, 
  `path`, 
  `icon`, 
  `component`, 
  `component_name`, 
  `status`, 
  `visible`, 
  `keep_alive`, 
  `always_show`, 
  `creator`, 
  `create_time`, 
  `updater`, 
  `update_time`, 
  `deleted`
) VALUES (
  '我的首页',  -- 菜单名称
  '',  -- 权限标识
  2,  -- 菜单类型：2=菜单
  3,  -- 显示顺序
  @dashboard_parent_id,  -- 父菜单ID
  '/home',  -- 路由地址
  'lucide:home',  -- 菜单图标
  'dashboard/home/index',  -- 组件路径
  'DashboardHome',  -- 组件名
  0,  -- 菜单状态：0=启用
  b'1',  -- 是否可见：1=可见
  b'1',  -- 是否缓存：1=缓存
  b'1',  -- 是否总是显示：1=总是显示
  'admin',  -- 创建者
  NOW(),  -- 创建时间
  'admin',  -- 更新者
  NOW(),  -- 更新时间
  b'0'  -- 是否删除：0=未删除
);

-- 5. 首页管理子菜单
INSERT INTO `system_menu` (
  `name`, 
  `permission`, 
  `type`, 
  `sort`, 
  `parent_id`, 
  `path`, 
  `icon`, 
  `component`, 
  `component_name`, 
  `status`, 
  `visible`, 
  `keep_alive`, 
  `always_show`, 
  `creator`, 
  `create_time`, 
  `updater`, 
  `update_time`, 
  `deleted`
) VALUES (
  '首页管理',  -- 菜单名称
  'system:home:query',  -- 权限标识（authority: ['system:home:query']）
  2,  -- 菜单类型：2=菜单
  4,  -- 显示顺序
  @dashboard_parent_id,  -- 父菜单ID
  '/home/manage',  -- 路由地址
  'lucide:layout-dashboard',  -- 菜单图标
  'dashboard/home/manage/index',  -- 组件路径
  'HomePageManage',  -- 组件名
  0,  -- 菜单状态：0=启用
  b'1',  -- 是否可见：1=可见
  b'1',  -- 是否缓存：1=缓存
  b'1',  -- 是否总是显示：1=总是显示
  'admin',  -- 创建者
  NOW(),  -- 创建时间
  'admin',  -- 更新者
  NOW(),  -- 更新时间
  b'0'  -- 是否删除：0=未删除
);

-- 6. 组件管理子菜单
INSERT INTO `system_menu` (
  `name`, 
  `permission`, 
  `type`, 
  `sort`, 
  `parent_id`, 
  `path`, 
  `icon`, 
  `component`, 
  `component_name`, 
  `status`, 
  `visible`, 
  `keep_alive`, 
  `always_show`, 
  `creator`, 
  `create_time`, 
  `updater`, 
  `update_time`, 
  `deleted`
) VALUES (
  '组件管理',  -- 菜单名称
  'system:home-component:query',  -- 权限标识（authority: ['system:home-component:query']）
  2,  -- 菜单类型：2=菜单
  5,  -- 显示顺序
  @dashboard_parent_id,  -- 父菜单ID
  '/home/component',  -- 路由地址
  'lucide:component',  -- 菜单图标
  'dashboard/home/component/index',  -- 组件路径
  'HomeComponentManage',  -- 组件名
  0,  -- 菜单状态：0=启用
  b'1',  -- 是否可见：1=可见
  b'1',  -- 是否缓存：1=缓存
  b'1',  -- 是否总是显示：1=总是显示
  'admin',  -- 创建者
  NOW(),  -- 创建时间
  'admin',  -- 更新者
  NOW(),  -- 更新时间
  b'0'  -- 是否删除：0=未删除
);

-- 7. 首页设计器子菜单（隐藏菜单，不在菜单中显示）
INSERT INTO `system_menu` (
  `name`, 
  `permission`, 
  `type`, 
  `sort`, 
  `parent_id`, 
  `path`, 
  `icon`, 
  `component`, 
  `component_name`, 
  `status`, 
  `visible`, 
  `keep_alive`, 
  `always_show`, 
  `creator`, 
  `create_time`, 
  `updater`, 
  `update_time`, 
  `deleted`
) VALUES (
  '首页设计器',  -- 菜单名称
  'system:home:update',  -- 权限标识（authority: ['system:home:update']）
  2,  -- 菜单类型：2=菜单
  6,  -- 显示顺序
  @dashboard_parent_id,  -- 父菜单ID
  '/home/designer',  -- 路由地址
  'lucide:layout-dashboard',  -- 菜单图标
  'dashboard/home/designer/index',  -- 组件路径
  'HomeDesigner',  -- 组件名
  0,  -- 菜单状态：0=启用
  b'0',  -- 是否可见：0=不可见（hideInMenu: true）
  b'1',  -- 是否缓存：1=缓存
  b'1',  -- 是否总是显示：1=总是显示
  'admin',  -- 创建者
  NOW(),  -- 创建时间
  'admin',  -- 更新者
  NOW(),  -- 更新时间
  b'0'  -- 是否删除：0=未删除
);

-- 验证插入结果
SELECT 
  id,
  name,
  permission,
  type,
  sort,
  parent_id,
  path,
  icon,
  component,
  component_name,
  status,
  visible,
  keep_alive,
  always_show
FROM `system_menu`
WHERE path LIKE '/dashboard%' OR path LIKE '/home%'
ORDER BY parent_id, sort;
