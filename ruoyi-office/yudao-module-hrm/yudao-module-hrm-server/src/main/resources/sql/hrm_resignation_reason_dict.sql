-- ----------------------------
-- 离职原因字典 SQL
-- ----------------------------

-- 创建字典类型
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '离职原因', 'hrm_resignation_reason', 0, '员工离职申请单的离职原因', 'admin', NOW(), 'admin', NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `type` = 'hrm_resignation_reason');

-- 获取字典类型ID
SET @dict_type_id = (SELECT `id` FROM `system_dict_type` WHERE `type` = 'hrm_resignation_reason' LIMIT 1);

-- 插入字典数据
INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES
(1, '个人原因', '1', 'hrm_resignation_reason', 0, '', '', '个人原因', 'admin', NOW(), 'admin', NOW(), 0),
(2, '薪资原因', '2', 'hrm_resignation_reason', 0, '', '', '薪资原因', 'admin', NOW(), 'admin', NOW(), 0),
(3, '晋升原因', '3', 'hrm_resignation_reason', 0, '', '', '晋升原因', 'admin', NOW(), 'admin', NOW(), 0),
(4, '工作时长', '4', 'hrm_resignation_reason', 0, '', '', '工作时长', 'admin', NOW(), 'admin', NOW(), 0),
(5, '其它', '5', 'hrm_resignation_reason', 0, '', '', '其它', 'admin', NOW(), 'admin', NOW(), 0)
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`);

COMMIT;

