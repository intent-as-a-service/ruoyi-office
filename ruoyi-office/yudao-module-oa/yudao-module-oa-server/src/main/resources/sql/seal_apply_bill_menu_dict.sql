-- ----------------------------
-- 用印申请单菜单和字典配置
-- ----------------------------

-- ----------------------------
-- 用印申请单相关字典类型
-- ----------------------------

-- 用印类型字典
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`) 
VALUES ('用印类型', 'oa_seal_use_type', 0, '用印申请单的用印类型')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`) VALUES
(1, '合同用印', '1', 'oa_seal_use_type', 0, 'primary', '', '用于合同签署'),
(2, '协议用印', '2', 'oa_seal_use_type', 0, 'success', '', '用于协议签署'),
(3, '证明用印', '3', 'oa_seal_use_type', 0, 'warning', '', '用于证明文件'),
(4, '授权用印', '4', 'oa_seal_use_type', 0, 'info', '', '用于授权委托'),
(5, '其他用印', '5', 'oa_seal_use_type', 0, 'default', '', '其他用印需求')
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`), `value` = VALUES(`value`);

-- 用印方式字典
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`) 
VALUES ('用印方式', 'oa_seal_use_mode', 0, '用印申请单的用印方式')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`) VALUES
(1, '现场用印', '1', 'oa_seal_use_mode', 0, 'success', '', '在公司现场使用印章'),
(2, '借用印章', '2', 'oa_seal_use_mode', 0, 'warning', '', '借出印章外出使用')
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`), `value` = VALUES(`value`);

-- 用印状态字典
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`) 
VALUES ('用印状态', 'oa_seal_use_status', 0, '用印申请单的使用状态')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`) VALUES
(1, '待用印', '1', 'oa_seal_use_status', 0, 'info', '', '等待用印'),
(2, '已完成', '2', 'oa_seal_use_status', 0, 'success', '', '用印已完成'),
(3, '已借出', '3', 'oa_seal_use_status', 0, 'warning', '', '印章已借出'),
(4, '已归还', '4', 'oa_seal_use_status', 0, 'success', '', '印章已归还'),
(5, '逾期未还', '5', 'oa_seal_use_status', 0, 'danger', '', '逾期未归还')
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`), `value` = VALUES(`value`);

-- 是否紧急字典
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`) 
VALUES ('是否紧急', 'oa_is_urgent', 0, '是否紧急处理')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`) VALUES
(1, '否', '0', 'oa_is_urgent', 0, 'default', '', '非紧急'),
(2, '是', '1', 'oa_is_urgent', 0, 'danger', '', '紧急处理')
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`), `value` = VALUES(`value`);

-- ----------------------------
-- 用印申请单菜单权限配置
-- ----------------------------

-- 获取或创建OA协同办公父菜单
SET @parent_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = 'OA协同办公' LIMIT 1);

-- 如果没有OA协同办公菜单，则创建
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
SELECT 'OA协同办公', '', 1, 20, 0, '/oa', 'ep:briefcase', NULL, NULL, 0, 1, 1, 1
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `name` = 'OA协同办公');

-- 重新获取OA协同办公菜单ID
SET @parent_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = 'OA协同办公' LIMIT 1);

-- 获取或创建印章管理父菜单
SET @seal_parent_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = '印章管理' AND `parent_id` = @parent_menu_id LIMIT 1);

-- 如果没有印章管理菜单，则创建
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
SELECT '印章管理', '', 1, 30, @parent_menu_id, 'seal', 'ep:stamp', NULL, NULL, 0, 1, 1, 1
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `name` = '印章管理' AND `parent_id` = @parent_menu_id);

-- 重新获取印章管理菜单ID
SET @seal_parent_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = '印章管理' AND `parent_id` = @parent_menu_id LIMIT 1);

-- 用印申请单菜单
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
VALUES ('用印申请单', '', 2, 10, @seal_parent_menu_id, 'sealapply', 'ep:document-checked', 'oa/seal/sealapply/list/index', 'OaSealApplyBillList', 0, 1, 1, 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 获取用印申请单菜单ID
SET @seal_apply_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = '用印申请单' AND `parent_id` = @seal_parent_menu_id LIMIT 1);

-- 用印申请单按钮权限
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `status`, `visible`, `keep_alive`, `always_show`) VALUES
('查询用印申请单', 'oa:seal-apply-bill:query', 3, 1, @seal_apply_menu_id, '', '', '', 0, 1, 1, 1),
('创建用印申请单', 'oa:seal-apply-bill:create', 3, 2, @seal_apply_menu_id, '', '', '', 0, 1, 1, 1),
('更新用印申请单', 'oa:seal-apply-bill:update', 3, 3, @seal_apply_menu_id, '', '', '', 0, 1, 1, 1),
('删除用印申请单', 'oa:seal-apply-bill:delete', 3, 4, @seal_apply_menu_id, '', '', '', 0, 1, 1, 1),
('导出用印申请单', 'oa:seal-apply-bill:export', 3, 5, @seal_apply_menu_id, '', '', '', 0, 1, 1, 1),
('提交用印申请单', 'oa:seal-apply-bill:submit', 3, 6, @seal_apply_menu_id, '', '', '', 0, 1, 1, 1),
('撤回用印申请单', 'oa:seal-apply-bill:withdraw', 3, 7, @seal_apply_menu_id, '', '', '', 0, 1, 1, 1)
ON DUPLICATE KEY UPDATE `permission` = VALUES(`permission`);

-- ----------------------------
-- 流程定义配置（可选，需要在BPM模块中配置）
-- ----------------------------
-- 注意：以下配置需要在实际部署时根据具体的流程定义进行调整

-- 用印申请单流程定义（示例）
-- INSERT INTO `bpm_process_definition_info` (`process_definition_id`, `process_definition_key`, `name`, `description`, `form_type`, `form_id`, `form_custom_create_path`, `form_custom_view_path`, `icon`, `sort`, `status`)
-- VALUES ('oa_seal_apply_bill:1:xxx', 'oa_seal_apply_bill', '用印申请单', '企业用印申请审批流程', 1, NULL, '/oa/seal/sealapply/info/index', '/oa/seal/sealapply/info/index', 'ep:document-checked', 10, 1)
-- ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- ----------------------------
-- 数据权限配置（可选）
-- ----------------------------
-- 如果需要数据权限控制，可以在这里添加相关配置

COMMIT;
