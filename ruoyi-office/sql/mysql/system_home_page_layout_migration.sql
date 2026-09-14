-- ----------------------------
-- 首页布局表结构迁移脚本
-- 从单JSON字段结构迁移到多字段结构
-- ----------------------------

-- 1. 如果表已存在，先备份数据（可选）
-- CREATE TABLE `system_home_page_layout_backup` AS SELECT * FROM `system_home_page_layout`;

-- 2. 删除旧表（如果需要重建）
DROP TABLE IF EXISTS `system_home_page_layout`;

-- 3. 创建新表结构
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

-- 注意：如果你已经有数据需要迁移，请根据实际情况编写数据转换脚本
-- 由于旧结构是单JSON字段，新结构是多行记录，需要手动处理JSON解析和插入
