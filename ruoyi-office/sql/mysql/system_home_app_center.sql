-- =============================================
-- 应用中心数据库表设计
-- =============================================
-- 功能说明：
-- 1. 系统级配置：管理员配置默认的常用应用（所有用户可见）
-- 2. 用户级配置：用户个性化配置自己的常用应用
-- 3. 支持拖拽排序、启用/禁用
-- 4. 关联菜单权限，只显示有权限的应用
-- =============================================

-- =============================================
-- 表1: system_home_app_config - 系统级应用配置表
-- =============================================
CREATE TABLE IF NOT EXISTS `system_home_app_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '应用ID',
  `menu_id` bigint NOT NULL COMMENT '关联菜单ID（来自system_menu表）',
  `name` varchar(100) NOT NULL COMMENT '应用名称',
  `icon` varchar(255) DEFAULT NULL COMMENT '应用图标（支持iconify图标名称或图片URL）',
  `color` varchar(50) DEFAULT NULL COMMENT '图标颜色（十六进制颜色值）',
  `description` varchar(500) DEFAULT NULL COMMENT '应用描述',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序（数字越小越靠前）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0=启用 1=禁用）',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_menu_id` (`menu_id`) USING BTREE COMMENT '菜单ID索引',
  KEY `idx_sort` (`sort`) USING BTREE COMMENT '排序索引',
  KEY `idx_status` (`status`) USING BTREE COMMENT '状态索引'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统级应用配置表';

-- =============================================
-- 表2: system_home_app_user - 用户级应用配置表
-- =============================================
CREATE TABLE IF NOT EXISTS `system_home_app_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户应用ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `menu_id` bigint NOT NULL COMMENT '关联菜单ID（来自system_menu表）',
  `name` varchar(100) DEFAULT NULL COMMENT '自定义应用名称（为空则使用菜单名称）',
  `icon` varchar(255) DEFAULT NULL COMMENT '自定义图标（为空则使用菜单图标）',
  `color` varchar(50) DEFAULT NULL COMMENT '自定义图标颜色',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序（数字越小越靠前）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0=显示 1=隐藏）',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_menu` (`user_id`, `menu_id`, `deleted`) USING BTREE COMMENT '用户菜单唯一索引',
  KEY `idx_user_id` (`user_id`) USING BTREE COMMENT '用户ID索引',
  KEY `idx_menu_id` (`menu_id`) USING BTREE COMMENT '菜单ID索引',
  KEY `idx_sort` (`sort`) USING BTREE COMMENT '排序索引'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户级应用配置表';

-- =============================================
-- 初始化系统级应用配置数据（示例）
-- =============================================
-- 注意：menu_id 需要根据实际的 system_menu 表中的菜单ID来设置
-- 以下是常见功能模块的示例配置

-- 示例：组织架构管理
INSERT INTO `system_home_app_config` 
(`menu_id`, `name`, `icon`, `color`, `description`, `sort`, `status`, `creator`, `tenant_id`)
VALUES
(1, '组织管理', 'carbon:collaborate', '#1890FF', '管理组织架构、部门和员工', 1, 0, 'admin', 1);

-- 示例：权限管理
INSERT INTO `system_home_app_config` 
(`menu_id`, `name`, `icon`, `color`, `description`, `sort`, `status`, `creator`, `tenant_id`)
VALUES
(2, '权限配置', 'carbon:user-role', '#52C41A', '配置角色、菜单和权限', 2, 0, 'admin', 1);

-- 示例：流程审批
INSERT INTO `system_home_app_config` 
(`menu_id`, `name`, `icon`, `color`, `description`, `sort`, `status`, `creator`, `tenant_id`)
VALUES
(3, '流程审批', 'carbon:workflow-automation', '#722ED1', '处理待办任务和审批流程', 3, 0, 'admin', 1);

-- 示例：数据报表
INSERT INTO `system_home_app_config` 
(`menu_id`, `name`, `icon`, `color`, `description`, `sort`, `status`, `creator`, `tenant_id`)
VALUES
(4, '数据报表', 'carbon:chart-line', '#FA8C16', '查看各类数据统计报表', 4, 0, 'admin', 1);

-- 示例：系统设置
INSERT INTO `system_home_app_config` 
(`menu_id`, `name`, `icon`, `color`, `description`, `sort`, `status`, `creator`, `tenant_id`)
VALUES
(5, '系统设置', 'carbon:settings', '#13C2C2', '系统参数和配置管理', 5, 0, 'admin', 1);

-- 示例：消息中心
INSERT INTO `system_home_app_config` 
(`menu_id`, `name`, `icon`, `color`, `description`, `sort`, `status`, `creator`, `tenant_id`)
VALUES
(6, '消息中心', 'carbon:notification', '#EB2F96', '查看系统通知和消息', 6, 0, 'admin', 1);

-- =============================================
-- 使用说明
-- =============================================
-- 
-- 系统级配置（system_home_app_config）：
-- - 由管理员统一配置，作为所有用户的默认常用应用
-- - 用户首次使用应用中心时，自动从系统配置初始化到用户配置
-- - 只显示用户有权限的应用（基于menu_id的权限判断）
-- 
-- 用户级配置（system_home_app_user）：
-- - 用户可以自定义添加、删除、排序自己的常用应用
-- - 用户可以隐藏某些系统默认的应用
-- - 用户可以自定义应用的名称、图标和颜色
-- - 只能选择自己有权限的菜单作为常用应用
-- 
-- 数据流程：
-- 1. 用户首次访问应用中心 → 从系统配置复制到用户配置
-- 2. 用户添加常用应用 → 从有权限的菜单列表中选择
-- 3. 用户拖拽排序 → 更新 sort 字段
-- 4. 用户隐藏应用 → 更新 status 字段为 1
-- 5. 展示应用中心 → 查询用户配置 + 权限过滤 + 按 sort 排序
-- 
-- 权限控制：
-- - 后端API需要检查用户是否有对应menu_id的访问权限
-- - 前端展示时需要根据用户权限菜单列表进行过滤
-- - 只显示用户有权限且状态为启用/显示的应用
-- 

