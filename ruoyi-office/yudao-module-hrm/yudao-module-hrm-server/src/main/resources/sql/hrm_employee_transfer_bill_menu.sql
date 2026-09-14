-- ----------------------------
-- 人事调动申请单菜单 SQL
-- ----------------------------

-- 获取或创建"人力资源管理"一级菜单
SET @hrm_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = '人力资源管理' AND `parent_id` = 0 LIMIT 1);

-- 如果没有"人力资源管理"菜单，则创建
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
SELECT '人力资源管理', '', 1, 30, 0, '/hrm', 'ep:user', NULL, NULL, 0, 1, 1, 1
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `name` = '人力资源管理' AND `parent_id` = 0);

-- 重新获取"人力资源管理"菜单ID
SET @hrm_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = '人力资源管理' AND `parent_id` = 0 LIMIT 1);

-- 获取或创建"人事管理"二级菜单
SET @personnel_management_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = '人事管理' AND `parent_id` = @hrm_menu_id LIMIT 1);

-- 如果没有"人事管理"菜单，则创建
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
SELECT '人事管理', '', 1, 20, @hrm_menu_id, 'personnel-management', 'ep:user-filled', NULL, NULL, 0, 1, 1, 1
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `name` = '人事管理' AND `parent_id` = @hrm_menu_id);

-- 重新获取"人事管理"菜单ID
SET @personnel_management_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = '人事管理' AND `parent_id` = @hrm_menu_id LIMIT 1);

-- 人事调动 - 三级菜单（列表页面）
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
VALUES ('人事调动', '', 2, 30, @personnel_management_menu_id, 'transfer-list', 'ep:user-filled', 'hrm/employee-relation/transfer/list/index', 'HrmEmployeeTransferBillList', 0, 1, 1, 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 获取人事调动菜单ID
SET @transfer_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = '人事调动' AND `parent_id` = @personnel_management_menu_id LIMIT 1);

-- 人事调动 - 详情页面（隐藏菜单）
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
VALUES ('调动申请详情', 'hrm:employee-transfer-bill:query', 2, 31, @personnel_management_menu_id, 'transfer-info', '', 'hrm/employee-relation/transfer/info/index', 'HrmEmployeeTransferBillInfo', 0, 0, 1, 1)
ON DUPLICATE KEY UPDATE `permission` = VALUES(`permission`);

-- 人事调动 - 按钮权限
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `status`, `visible`, `keep_alive`, `always_show`) VALUES
('查询调动申请', 'hrm:employee-transfer-bill:query', 3, 1, @transfer_menu_id, '', '', '', 0, 1, 1, 1),
('创建调动申请', 'hrm:employee-transfer-bill:create', 3, 2, @transfer_menu_id, '', '', '', 0, 1, 1, 1),
('更新调动申请', 'hrm:employee-transfer-bill:update', 3, 3, @transfer_menu_id, '', '', '', 0, 1, 1, 1),
('删除调动申请', 'hrm:employee-transfer-bill:delete', 3, 4, @transfer_menu_id, '', '', '', 0, 1, 1, 1),
('导出调动申请', 'hrm:employee-transfer-bill:export', 3, 5, @transfer_menu_id, '', '', '', 0, 1, 1, 1),
('提交调动申请', 'hrm:employee-transfer-bill:submit', 3, 6, @transfer_menu_id, '', '', '', 0, 1, 1, 1),
('撤回调动申请', 'hrm:employee-transfer-bill:withdraw', 3, 7, @transfer_menu_id, '', '', '', 0, 1, 1, 1)
ON DUPLICATE KEY UPDATE `permission` = VALUES(`permission`);

COMMIT;




