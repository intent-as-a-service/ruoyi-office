-- ----------------------------
-- 千人千面首页自定义系统 - Phase 2: 组件定义管理
-- ----------------------------

-- ----------------------------
-- 1. 组件分类表
-- ----------------------------
CREATE TABLE `system_home_component_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `code` varchar(50) NOT NULL COMMENT '分类编码',
  `icon` varchar(100) DEFAULT NULL COMMENT '分类图标',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`, `deleted`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`, `deleted`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='首页组件分类表';

-- ----------------------------
-- 2. 组件定义表
-- ----------------------------
CREATE TABLE `system_home_component` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '组件ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `name` varchar(100) NOT NULL COMMENT '组件名称',
  `code` varchar(50) NOT NULL COMMENT '组件编码',
  `component_path` varchar(255) NOT NULL COMMENT '组件路径',
  `description` varchar(500) DEFAULT NULL COMMENT '组件描述',
  `preview_image` varchar(255) DEFAULT NULL COMMENT '预览图',
  `default_width` int NOT NULL DEFAULT '12' COMMENT '默认宽度（网格列数1-24）',
  `default_height` int NOT NULL DEFAULT '4' COMMENT '默认高度（网格行数）',
  `config_schema` text COMMENT '配置Schema（JSON格式）',
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
  KEY `idx_category_id` (`category_id`, `deleted`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`, `deleted`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='首页组件定义表';

-- ----------------------------
-- 初始化组件分类数据
-- ----------------------------
INSERT INTO `system_home_component_category` (`name`, `code`, `icon`, `sort`, `creator`) VALUES
('统计卡片', 'statistics', 'lucide:bar-chart-2', 1, '1'),
('图表组件', 'chart', 'lucide:pie-chart', 2, '1'),
('列表组件', 'list', 'lucide:list', 3, '1'),
('快捷导航', 'navigation', 'lucide:compass', 4, '1'),
('其他组件', 'other', 'lucide:box', 99, '1');

-- ----------------------------
-- 初始化组件数据
-- ----------------------------
-- 1. 访问统计卡片
INSERT INTO `system_home_component` (
  `category_id`, `name`, `code`, `component_path`, `description`,
  `default_width`, `default_height`, `config_schema`, `status`, `sort`, `creator`
) VALUES (
  1, '访问统计', 'analytics_visits',
  'dashboard/home/components/statistics/analytics-visits.vue',
  '展示网站访问数据统计卡片',
  6, 2,
  '{"properties":[{"key":"title","label":"标题","type":"string","default":"访问统计","required":true}]}',
  1, 1, '1'
);

-- 2. 访问来源图表
INSERT INTO `system_home_component` (
  `category_id`, `name`, `code`, `component_path`, `description`,
  `default_width`, `default_height`, `config_schema`, `status`, `sort`, `creator`
) VALUES (
  2, '访问来源图表', 'analytics_visits_source',
  'dashboard/home/components/charts/analytics-visits-source.vue',
  '展示访问来源的饼图分析',
  12, 4,
  '{"properties":[{"key":"title","label":"标题","type":"string","default":"访问来源","required":true}]}',
  1, 1, '1'
);

-- 3. 项目列表
INSERT INTO `system_home_component` (
  `category_id`, `name`, `code`, `component_path`, `description`,
  `default_width`, `default_height`, `config_schema`, `status`, `sort`, `creator`
) VALUES (
  3, '项目列表', 'workbench_project',
  'dashboard/home/components/lists/workbench-project.vue',
  '展示项目卡片列表',
  12, 6,
  '{"properties":[{"key":"title","label":"标题","type":"string","default":"项目","required":true}]}',
  1, 1, '1'
);

-- 4. 快捷导航
INSERT INTO `system_home_component` (
  `category_id`, `name`, `code`, `component_path`, `description`,
  `default_width`, `default_height`, `config_schema`, `status`, `sort`, `creator`
) VALUES (
  4, '快捷导航', 'workbench_quick_nav',
  'dashboard/home/components/navigation/workbench-quick-nav.vue',
  '展示快捷导航入口',
  12, 3,
  '{"properties":[{"key":"title","label":"标题","type":"string","default":"快捷导航","required":true}]}',
  1, 1, '1'
);

-- 5. 动态列表
INSERT INTO `system_home_component` (
  `category_id`, `name`, `code`, `component_path`, `description`,
  `default_width`, `default_height`, `config_schema`, `status`, `sort`, `creator`
) VALUES (
  3, '动态列表', 'workbench_trends',
  'dashboard/home/components/lists/workbench-trends.vue',
  '展示最新动态列表',
  12, 6,
  '{"properties":[{"key":"title","label":"标题","type":"string","default":"最新动态","required":true}]}',
  1, 2, '1'
);
