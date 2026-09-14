-- ----------------------------
-- 员工入职管理菜单 SQL
-- ----------------------------

-- 获取或创建"人力资源管理"一级菜单
SET @hrm_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = '人力资源管理' AND `parent_id` = 0 LIMIT 1);

-- 如果没有"人力资源管理"菜单，则创建
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
SELECT '人力资源管理', '', 1, 30, 0, '/hrm', 'ep:user', NULL, NULL, 0, 1, 1, 1
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `name` = '人力资源管理' AND `parent_id` = 0);

-- 重新获取"人力资源管理"菜单ID
SET @hrm_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = '人力资源管理' AND `parent_id` = 0 LIMIT 1);

-- 获取或创建"员工关系"二级菜单
SET @employee_relation_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = '员工关系' AND `parent_id` = @hrm_menu_id LIMIT 1);

-- 如果没有"员工关系"菜单，则创建
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
SELECT '员工关系', '', 1, 10, @hrm_menu_id, 'employee-relation', 'ep:connection', NULL, NULL, 0, 1, 1, 1
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `name` = '员工关系' AND `parent_id` = @hrm_menu_id);

-- 重新获取"员工关系"菜单ID
SET @employee_relation_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = '员工关系' AND `parent_id` = @hrm_menu_id LIMIT 1);

-- 入职管理 - 三级菜单（列表页面）
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
VALUES ('入职管理', '', 2, 10, @employee_relation_menu_id, 'entry', 'ep:user-filled', 'hrm/employee-relation/entry/list/index', 'HrmEmployeeEntryBillList', 0, 1, 1, 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 获取入职管理菜单ID
SET @entry_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = '入职管理' AND `parent_id` = @employee_relation_menu_id LIMIT 1);

-- 入职管理 - 详情页面（隐藏菜单）
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
VALUES ('入职申请详情', 'hrm:employee-entry-bill:query', 2, 1, @entry_menu_id, 'info', '', 'hrm/employee-relation/entry/info/index', 'HrmEmployeeEntryBillInfo', 0, 0, 1, 1)
ON DUPLICATE KEY UPDATE `permission` = VALUES(`permission`);

-- 入职管理 - 按钮权限
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `status`, `visible`, `keep_alive`, `always_show`) VALUES
('查询入职申请', 'hrm:employee-entry-bill:query', 3, 1, @entry_menu_id, '', '', '', 0, 1, 1, 1),
('创建入职申请', 'hrm:employee-entry-bill:create', 3, 2, @entry_menu_id, '', '', '', 0, 1, 1, 1),
('更新入职申请', 'hrm:employee-entry-bill:update', 3, 3, @entry_menu_id, '', '', '', 0, 1, 1, 1),
('删除入职申请', 'hrm:employee-entry-bill:delete', 3, 4, @entry_menu_id, '', '', '', 0, 1, 1, 1),
('导出入职申请', 'hrm:employee-entry-bill:export', 3, 5, @entry_menu_id, '', '', '', 0, 1, 1, 1),
('提交入职申请', 'hrm:employee-entry-bill:submit', 3, 6, @entry_menu_id, '', '', '', 0, 1, 1, 1),
('撤回入职申请', 'hrm:employee-entry-bill:withdraw', 3, 7, @entry_menu_id, '', '', '', 0, 1, 1, 1)
ON DUPLICATE KEY UPDATE `permission` = VALUES(`permission`);

COMMIT;


