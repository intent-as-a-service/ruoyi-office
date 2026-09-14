-- ----------------------------
-- 员工离职申请单菜单 SQL
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

-- 员工离职 - 三级菜单（列表页面）
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
VALUES ('员工离职', '', 2, 40, @personnel_management_menu_id, 'resignation-list', 'ep:user-filled', 'hrm/employee-relation/resignation/list/index', 'HrmEmployeeResignationBillList', 0, 1, 1, 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 获取员工离职菜单ID
SET @resignation_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = '员工离职' AND `parent_id` = @personnel_management_menu_id LIMIT 1);

-- 员工离职 - 详情页面（隐藏菜单）
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
VALUES ('离职申请详情', 'hrm:employee-resignation-bill:query', 2, 41, @personnel_management_menu_id, 'resignation-info', '', 'hrm/employee-relation/resignation/info/index', 'HrmEmployeeResignationBillInfo', 0, 0, 1, 1)
ON DUPLICATE KEY UPDATE `permission` = VALUES(`permission`);

-- 员工离职 - 按钮权限
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `status`, `visible`, `keep_alive`, `always_show`) VALUES
('查询离职申请', 'hrm:employee-resignation-bill:query', 3, 1, @resignation_menu_id, '', '', '', 0, 1, 1, 1),
('创建离职申请', 'hrm:employee-resignation-bill:create', 3, 2, @resignation_menu_id, '', '', '', 0, 1, 1, 1),
('更新离职申请', 'hrm:employee-resignation-bill:update', 3, 3, @resignation_menu_id, '', '', '', 0, 1, 1, 1),
('删除离职申请', 'hrm:employee-resignation-bill:delete', 3, 4, @resignation_menu_id, '', '', '', 0, 1, 1, 1),
('导出离职申请', 'hrm:employee-resignation-bill:export', 3, 5, @resignation_menu_id, '', '', '', 0, 1, 1, 1),
('提交离职申请', 'hrm:employee-resignation-bill:submit', 3, 6, @resignation_menu_id, '', '', '', 0, 1, 1, 1),
('撤回离职申请', 'hrm:employee-resignation-bill:withdraw', 3, 7, @resignation_menu_id, '', '', '', 0, 1, 1, 1)
ON DUPLICATE KEY UPDATE `permission` = VALUES(`permission`);

COMMIT;

