-- ----------------------------
-- 离职类型字典 SQL
-- ----------------------------

-- 创建字典类型
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '离职类型', 'hrm_resignation_type', 0, '员工离职申请单的离职类型', 'admin', NOW(), 'admin', NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `type` = 'hrm_resignation_type');

-- 获取字典类型ID
SET @dict_type_id = (SELECT `id` FROM `system_dict_type` WHERE `type` = 'hrm_resignation_type' LIMIT 1);

-- 插入字典数据
INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES
(1, '主动离职', '1', 'hrm_resignation_type', 0, '', '', '主动离职', 'admin', NOW(), 'admin', NOW(), 0),
(2, '被动离职', '2', 'hrm_resignation_type', 0, '', '', '被动离职', 'admin', NOW(), 'admin', NOW(), 0),
(3, '其它', '3', 'hrm_resignation_type', 0, '', '', '其它', 'admin', NOW(), 'admin', NOW(), 0)
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`);

COMMIT;

