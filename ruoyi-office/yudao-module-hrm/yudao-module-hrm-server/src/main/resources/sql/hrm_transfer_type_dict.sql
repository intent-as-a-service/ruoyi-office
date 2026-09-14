-- ----------------------------
-- 异动类型字典 SQL
-- ----------------------------

-- 创建字典类型
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '异动类型', 'hrm_transfer_type', 0, '人事调动申请单的异动类型', 'admin', NOW(), 'admin', NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `type` = 'hrm_transfer_type');

-- 获取字典类型ID
SET @dict_type_id = (SELECT `id` FROM `system_dict_type` WHERE `type` = 'hrm_transfer_type' LIMIT 1);

-- 插入字典数据
INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES
(1, '调岗', '1', 'hrm_transfer_type', 0, '', '', '调岗', 'admin', NOW(), 'admin', NOW(), 0),
(2, '调薪', '2', 'hrm_transfer_type', 0, '', '', '调薪', 'admin', NOW(), 'admin', NOW(), 0),
(3, '调部门', '3', 'hrm_transfer_type', 0, '', '', '调部门', 'admin', NOW(), 'admin', NOW(), 0),
(4, '调公司', '4', 'hrm_transfer_type', 0, '', '', '调公司', 'admin', NOW(), 'admin', NOW(), 0),
(5, '其他', '5', 'hrm_transfer_type', 0, '', '', '其他', 'admin', NOW(), 'admin', NOW(), 0)
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`);

COMMIT;




