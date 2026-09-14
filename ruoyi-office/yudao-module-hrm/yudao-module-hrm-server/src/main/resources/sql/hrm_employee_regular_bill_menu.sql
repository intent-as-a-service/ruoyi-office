-- ----------------------------
-- 员工转正申请单菜单 SQL
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

-- 员工转正 - 三级菜单（列表页面）
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
VALUES ('员工转正', '', 2, 20, @personnel_management_menu_id, 'regular-list', 'ep:user-filled', 'hrm/employee-relation/regular/list/index', 'HrmEmployeeRegularBillList', 0, 1, 1, 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 获取员工转正菜单ID
SET @regular_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = '员工转正' AND `parent_id` = @personnel_management_menu_id LIMIT 1);

-- 员工转正 - 详情页面（隐藏菜单）
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
VALUES ('转正申请详情', 'hrm:employee-regular-bill:query', 2, 21, @personnel_management_menu_id, 'regular-info', '', 'hrm/employee-relation/regular/info/index', 'HrmEmployeeRegularBillInfo', 0, 0, 1, 1)
ON DUPLICATE KEY UPDATE `permission` = VALUES(`permission`);

-- 员工转正 - 按钮权限
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `status`, `visible`, `keep_alive`, `always_show`) VALUES
('查询转正申请', 'hrm:employee-regular-bill:query', 3, 1, @regular_menu_id, '', '', '', 0, 1, 1, 1),
('创建转正申请', 'hrm:employee-regular-bill:create', 3, 2, @regular_menu_id, '', '', '', 0, 1, 1, 1),
('更新转正申请', 'hrm:employee-regular-bill:update', 3, 3, @regular_menu_id, '', '', '', 0, 1, 1, 1),
('删除转正申请', 'hrm:employee-regular-bill:delete', 3, 4, @regular_menu_id, '', '', '', 0, 1, 1, 1),
('导出转正申请', 'hrm:employee-regular-bill:export', 3, 5, @regular_menu_id, '', '', '', 0, 1, 1, 1),
('提交转正申请', 'hrm:employee-regular-bill:submit', 3, 6, @regular_menu_id, '', '', '', 0, 1, 1, 1),
('撤回转正申请', 'hrm:employee-regular-bill:withdraw', 3, 7, @regular_menu_id, '', '', '', 0, 1, 1, 1)
ON DUPLICATE KEY UPDATE `permission` = VALUES(`permission`);

COMMIT;

