-- ----------------------------
-- 印章管理表结构
-- ----------------------------
CREATE TABLE IF NOT EXISTS `oa_seal` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `company_id` bigint NOT NULL COMMENT '公司ID',
  `company_name` varchar(255) NOT NULL COMMENT '公司名称',
  `seal_no` varchar(50) NOT NULL COMMENT '印章编号',
  `seal_name` varchar(100) DEFAULT NULL COMMENT '印章名称',
  `seal_type` bigint NOT NULL COMMENT '印章类型',
  `seal_cls` bigint NOT NULL COMMENT '分类',
  `keeper_id` bigint DEFAULT NULL COMMENT '保管人ID',
  `keeper_name` varchar(100) DEFAULT NULL COMMENT '保管人名称',
  `keeper_dept_id` bigint DEFAULT NULL COMMENT '保管部门ID',
  `keeper_dept_name` varchar(255) DEFAULT NULL COMMENT '保管部门名称',
  `purchase_date` date DEFAULT NULL COMMENT '购买日期',
  `enable_date` date DEFAULT NULL COMMENT '启用日期',
  `disable_date` date DEFAULT NULL COMMENT '停用日期',
  `pic_url` varchar(500) DEFAULT NULL COMMENT '上传照片',
  `sort` int DEFAULT 0 COMMENT '显示顺序',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态（0在库 1停用 2使用中）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_company_id` (`company_id`) USING BTREE,
  KEY `idx_seal_no` (`seal_no`) USING BTREE,
  KEY `idx_seal_cls` (`seal_cls`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='印章信息表';

-- ----------------------------
-- 印章类型字典
-- ----------------------------
INSERT INTO `system_dict_type` (`name`, `type`, `status`) 
VALUES ('印章类型', 'oa_seal_type', 0, '印章类型字典')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 获取刚插入的字典类型ID（用于插入字典数据）
SET @seal_type_dict_id = (SELECT `id` FROM `system_dict_type` WHERE `type` = 'oa_seal_type');

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`) VALUES
(1, '公章', '1', 'oa_seal_type', 0, 'primary', '', '公司公章'),
(2, '合同章', '2', 'oa_seal_type', 0, 'success', '', '合同专用章'),
(3, '财务章', '3', 'oa_seal_type', 0, 'warning', '', '财务专用章'),
(4, '法人章', '4', 'oa_seal_type', 0, 'danger', '', '法人代表章'),
(5, '发票章', '5', 'oa_seal_type', 0, 'info', '', '发票专用章'),
(6, '部门章', '6', 'oa_seal_type', 0, 'default', '', '部门印章')
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`), `value` = VALUES(`value`);

-- ----------------------------
-- 印章分类字典
-- ----------------------------
INSERT INTO `system_dict_type` (`name`, `type`, `status`) 
VALUES ('印章分类', 'oa_seal_cls', 0, '印章分类字典')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`) VALUES
(1, '行政类', '1', 'oa_seal_cls', 0, 'primary', '', '行政管理类印章'),
(2, '业务类', '2', 'oa_seal_cls', 0, 'success', '', '业务专用类印章'),
(3, '财务类', '3', 'oa_seal_cls', 0, 'warning', '', '财务相关类印章'),
(4, '其他类', '4', 'oa_seal_cls', 0, 'default', '', '其他类印章')
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`), `value` = VALUES(`value`);

-- ----------------------------
-- 印章状态字典
-- ----------------------------
INSERT INTO `system_dict_type` (`name`, `type`, `status`) 
VALUES ('印章状态', 'oa_seal_status', 0, '印章使用状态')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`) VALUES
(1, '在库', '0', 'oa_seal_status', 0, 'success', '', '印章在库可用'),
(2, '停用', '1', 'oa_seal_status', 0, 'danger', '', '印章已停用'),
(3, '使用中', '2', 'oa_seal_status', 0, 'warning', '', '印章使用中')
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`), `value` = VALUES(`value`);

-- ----------------------------
-- 印章管理菜单权限
-- ----------------------------
-- 父菜单：OA协同办公
SET @parent_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = 'OA协同办公' LIMIT 1);

-- 如果没有OA协同办公菜单，则创建
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
SELECT 'OA协同办公', '', 1, 20, 0, '/oa', 'ep:briefcase', NULL, NULL, 0, 1, 1, 1
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `name` = 'OA协同办公');

SET @parent_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = 'OA协同办公' LIMIT 1);

-- 印章管理菜单
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
VALUES ('印章管理', '', 2, 20, @parent_menu_id, 'sealinfo', 'ep:stamp', 'oa/seal/sealinfo/index', 'OaSealInfo', 0, 1, 1, 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

SET @seal_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = '印章管理' AND `parent_id` = @parent_menu_id LIMIT 1);

-- 印章管理按钮权限
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `status`, `visible`, `keep_alive`, `always_show`) VALUES
('查询印章', 'oa:seal:query', 3, 1, @seal_menu_id, '', '', '', 0, 1, 1, 1),
('创建印章', 'oa:seal:create', 3, 2, @seal_menu_id, '', '', '', 0, 1, 1, 1),
('更新印章', 'oa:seal:update', 3, 3, @seal_menu_id, '', '', '', 0, 1, 1, 1),
('删除印章', 'oa:seal:delete', 3, 4, @seal_menu_id, '', '', '', 0, 1, 1, 1),
('导出印章', 'oa:seal:export', 3, 5, @seal_menu_id, '', '', '', 0, 1, 1, 1)
ON DUPLICATE KEY UPDATE `permission` = VALUES(`permission`);

-- ----------------------------
-- 示例数据（可选）
-- ----------------------------
-- INSERT INTO `oa_seal` (`company_id`, `company_name`, `seal_no`, `seal_name`, `seal_type`, `seal_cls`, `status`, `sort`, `remark`, `creator`, `create_time`)
-- VALUES
-- (1, '示例公司', 'SEAL001', '公司公章', 1, 1, 0, 1, '公司主要公章', 'admin', NOW()),
-- (1, '示例公司', 'SEAL002', '合同专用章', 2, 2, 0, 2, '用于签订合同', 'admin', NOW()),
-- (1, '示例公司', 'SEAL003', '财务专用章', 3, 3, 0, 3, '用于财务审批', 'admin', NOW());

