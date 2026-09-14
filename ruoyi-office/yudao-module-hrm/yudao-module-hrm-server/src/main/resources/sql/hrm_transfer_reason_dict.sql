-- ----------------------------
-- 异动原因字典 SQL
-- ----------------------------

-- 创建字典类型
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '异动原因', 'hrm_transfer_reason', 0, '人事调动申请单的异动原因', 'admin', NOW(), 'admin', NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `type` = 'hrm_transfer_reason');

-- 获取字典类型ID
SET @dict_type_id = (SELECT `id` FROM `system_dict_type` WHERE `type` = 'hrm_transfer_reason' LIMIT 1);

-- 插入字典数据
INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES
(1, '个人申请', '1', 'hrm_transfer_reason', 0, '', '', '个人申请', 'admin', NOW(), 'admin', NOW(), 0),
(2, '内部调岗', '2', 'hrm_transfer_reason', 0, '', '', '内部调岗', 'admin', NOW(), 'admin', NOW(), 0),
(3, '其他', '3', 'hrm_transfer_reason', 0, '', '', '其他', 'admin', NOW(), 'admin', NOW(), 0)
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`);

COMMIT;


