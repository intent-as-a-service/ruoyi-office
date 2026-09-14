-- ----------------------------
-- 千人千面首页自定义系统 - 数据库表结构
-- ----------------------------

-- ----------------------------
-- 1. 首页配置表
-- ----------------------------
CREATE TABLE `system_home_page` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '首页ID',
  `name` varchar(100) NOT NULL COMMENT '首页名称',
  `code` varchar(50) NOT NULL COMMENT '首页编码',
  `description` varchar(500) DEFAULT NULL COMMENT '首页描述',
  `preview_image` varchar(255) DEFAULT NULL COMMENT '预览图',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认首页',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态（0停用 1启用）',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`, `deleted`) USING BTREE,
  KEY `idx_is_default` (`is_default`, `deleted`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`, `deleted`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='首页配置表';

-- ----------------------------
-- 2. 首页布局配置表
-- ----------------------------
CREATE TABLE `system_home_page_layout` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '布局ID',
  `page_id` bigint NOT NULL COMMENT '首页ID',
  `component_code` varchar(100) NOT NULL COMMENT '组件编码',
  `position_x` int NOT NULL DEFAULT '0' COMMENT 'X坐标',
  `position_y` int NOT NULL DEFAULT '0' COMMENT 'Y坐标',
  `width` int NOT NULL DEFAULT '6' COMMENT '宽度（栅格数）',
  `height` int NOT NULL DEFAULT '4' COMMENT '高度（栅格数）',
  `config` text COMMENT '组件配置（JSON）',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_page_id` (`page_id`, `deleted`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`, `deleted`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='首页布局配置表';

-- ----------------------------
-- 3. 用户首页关联表
-- ----------------------------
CREATE TABLE `system_user_home_page` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `page_id` bigint NOT NULL COMMENT '首页ID',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_id` (`user_id`, `deleted`) USING BTREE,
  KEY `idx_page_id` (`page_id`, `deleted`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`, `deleted`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户首页关联表';

-- ----------------------------
-- 初始化默认首页数据
-- ----------------------------
INSERT INTO `system_home_page` (`name`, `code`, `description`, `is_default`, `status`, `sort`, `creator`)
VALUES ('默认工作台', 'default_workspace', '系统默认首页', 1, 1, 0, '1');

-- 初始化默认布局（可选：添加一些默认组件）
-- SET @default_page_id = LAST_INSERT_ID();
-- INSERT INTO `system_home_page_layout` (`page_id`, `component_code`, `position_x`, `position_y`, `width`, `height`, `config`, `sort`, `creator`)
-- VALUES (@default_page_id, 'welcome_banner', 0, 0, 24, 4, '{"title":"欢迎使用"}', 0, '1');
