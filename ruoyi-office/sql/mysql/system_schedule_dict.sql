-- =============================================
-- 日程管理字典配置
-- =============================================

-- 1. 日程类型字典类型
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
VALUES ('日程类型', 'schedule_type', 0, '日程类型：会议、任务、提醒等', '1', NOW(), '', NOW(), 0, 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `remark` = VALUES(`remark`);

-- 2. 日程分类字典类型
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
VALUES ('日程分类', 'schedule_category', 0, '日程分类：工作、个人、重要等', '1', NOW(), '', NOW(), 0, 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `remark` = VALUES(`remark`);

-- 3. 日程类型字典数据
-- 获取字典类型ID
SET @schedule_type_dict_type_id = (SELECT id FROM `system_dict_type` WHERE `type` = 'schedule_type' AND `deleted` = 0 LIMIT 1);

-- 插入日程类型字典数据
INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
VALUES
(1, '会议', 'meeting', 'schedule_type', 0, 'default', '', '会议类型日程', '1', NOW(), '', NOW(), 0, 1),
(2, '任务', 'task', 'schedule_type', 0, 'default', '', '任务类型日程', '1', NOW(), '', NOW(), 0, 1),
(3, '提醒', 'reminder', 'schedule_type', 0, 'default', '', '提醒类型日程', '1', NOW(), '', NOW(), 0, 1),
(4, '其他', 'other', 'schedule_type', 0, 'default', '', '其他类型日程', '1', NOW(), '', NOW(), 0, 1)
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`), `sort` = VALUES(`sort`), `remark` = VALUES(`remark`);

-- 4. 日程分类字典数据
-- 获取字典类型ID
SET @schedule_category_dict_type_id = (SELECT id FROM `system_dict_type` WHERE `type` = 'schedule_category' AND `deleted` = 0 LIMIT 1);

-- 插入日程分类字典数据
INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
VALUES
(1, '工作', 'work', 'schedule_category', 0, 'default', '', '工作相关日程', '1', NOW(), '', NOW(), 0, 1),
(2, '个人', 'personal', 'schedule_category', 0, 'default', '', '个人相关日程', '1', NOW(), '', NOW(), 0, 1),
(3, '重要', 'important', 'schedule_category', 0, 'default', '', '重要日程', '1', NOW(), '', NOW(), 0, 1),
(4, '其他', 'other', 'schedule_category', 0, 'default', '', '其他分类日程', '1', NOW(), '', NOW(), 0, 1)
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`), `sort` = VALUES(`sort`), `remark` = VALUES(`remark`);

