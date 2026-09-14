-- ----------------------------
-- 会议室预定申请单表
-- ----------------------------
CREATE TABLE `oa_meeting_room_booking` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `bill_code` varchar(50) NOT NULL COMMENT '单据编号',
    `process_instance_id` varchar(64) DEFAULT NULL COMMENT '流程实例编号',
    `process_status` tinyint DEFAULT '0' COMMENT '单据状态（0草稿 1审批中 2审批通过 3审批拒绝 4已取消）',
    
    -- 会议室相关信息
    `room_id` bigint DEFAULT NULL COMMENT '会议室ID',
    `room_name` varchar(100) DEFAULT NULL COMMENT '会议室名称',
    `room_location` varchar(255) DEFAULT NULL COMMENT '会议室位置',
    `room_type` int DEFAULT NULL COMMENT '会议室类型',
    
    -- 会议信息
    `meeting_title` varchar(200) DEFAULT NULL COMMENT '会议名称',
    `meeting_start_time` datetime DEFAULT NULL COMMENT '会议开始时间',
    `meeting_end_time` datetime DEFAULT NULL COMMENT '会议结束时间',
    `moderator_id` bigint DEFAULT NULL COMMENT '主持人ID',
    `moderator_name` varchar(100) DEFAULT NULL COMMENT '主持人姓名',
    `meeting_remark` varchar(1000) DEFAULT NULL COMMENT '会议备注',
    
    -- 提醒和参会人
    `reminder_type` tinyint DEFAULT NULL COMMENT '会议提醒（1不提醒 2提前5分钟 3提前10分钟 4提前15分钟 5提前30分钟）',
    `attendees` text DEFAULT NULL COMMENT '与会人ID列表（JSON数组格式）',
    `attendee_names` text DEFAULT NULL COMMENT '与会人姓名列表（JSON数组格式）',
    
    -- 附件
    `attachment_urls` text DEFAULT NULL COMMENT '附件URL列表（JSON数组格式）',
    
    -- 使用状态
    `use_status` tinyint DEFAULT '0' COMMENT '使用状态（0待使用 1使用中 2已完成 3已取消）',
    
    -- 基础字段
    `creator_name` varchar(100) DEFAULT NULL COMMENT '申请人姓名',
    `company_id` bigint NOT NULL COMMENT '公司ID',
    `company_name` varchar(100) NOT NULL COMMENT '公司名称',
    `dept_id` bigint NOT NULL COMMENT '部门ID',
    `dept_name` varchar(100) NOT NULL COMMENT '部门名称',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `creator` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_bill_code` (`bill_code`),
    KEY `idx_room_id` (`room_id`),
    KEY `idx_company_id` (`company_id`),
    KEY `idx_creator` (`creator`),
    KEY `idx_process_status` (`process_status`),
    KEY `idx_use_status` (`use_status`),
    KEY `idx_meeting_start_time` (`meeting_start_time`),
    KEY `idx_meeting_end_time` (`meeting_end_time`),
    KEY `idx_moderator_id` (`moderator_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会议室预定申请单';

-- ----------------------------
-- 使用状态字典
-- ----------------------------
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`)
VALUES ('会议室使用状态', 'oa_meeting_booking_use_status', 0, '会议室使用状态字典')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`) VALUES
(1, '待使用', '0', 'oa_meeting_booking_use_status', 0, 'warning', '', '待使用'),
(2, '使用中', '1', 'oa_meeting_booking_use_status', 0, 'primary', '', '使用中'),
(3, '已完成', '2', 'oa_meeting_booking_use_status', 0, 'success', '', '已完成'),
(4, '已取消', '3', 'oa_meeting_booking_use_status', 0, 'info', '', '已取消')
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`), `value` = VALUES(`value`);

-- ----------------------------
-- 会议提醒类型字典
-- ----------------------------
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`) 
VALUES ('会议提醒类型', 'oa_meeting_reminder_type', 0, '会议提醒类型字典')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`) VALUES
(1, '不提醒', '1', 'oa_meeting_reminder_type', 0, 'default', '', '不提醒'),
(2, '提前5分钟', '2', 'oa_meeting_reminder_type', 0, 'default', '', '会议开始前5分钟提醒'),
(3, '提前10分钟', '3', 'oa_meeting_reminder_type', 0, 'default', '', '会议开始前10分钟提醒'),
(4, '提前15分钟', '4', 'oa_meeting_reminder_type', 0, 'default', '', '会议开始前15分钟提醒'),
(5, '提前30分钟', '5', 'oa_meeting_reminder_type', 0, 'default', '', '会议开始前30分钟提醒')
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`), `value` = VALUES(`value`);


-- ----------------------------
-- 会议室预定管理菜单权限
-- ----------------------------
-- 父菜单：会议室管理
SET @parent_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = '会议室管理' LIMIT 1);

-- 会议室预定菜单
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
VALUES ('会议室预定', '', 2, 40, @parent_menu_id, 'booking', 'ep:calendar', 'oa/meetingroom/booking/list/index', 'OaMeetingRoomBookingList', 0, 1, 1, 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

SET @booking_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = '会议室预定' AND `parent_id` = @parent_menu_id LIMIT 1);

-- 会议室预定按钮权限
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `status`, `visible`, `keep_alive`, `always_show`) VALUES
('查询预定', 'oa:meeting-room-booking:query', 3, 1, @booking_menu_id, '', '', '', 0, 1, 1, 1),
('创建预定', 'oa:meeting-room-booking:create', 3, 2, @booking_menu_id, '', '', '', 0, 1, 1, 1),
('更新预定', 'oa:meeting-room-booking:update', 3, 3, @booking_menu_id, '', '', '', 0, 1, 1, 1),
('删除预定', 'oa:meeting-room-booking:delete', 3, 4, @booking_menu_id, '', '', '', 0, 1, 1, 1),
('导出预定', 'oa:meeting-room-booking:export', 3, 5, @booking_menu_id, '', '', '', 0, 1, 1, 1),
('提交审批', 'oa:meeting-room-booking:submit', 3, 6, @booking_menu_id, '', '', '', 0, 1, 1, 1)
ON DUPLICATE KEY UPDATE `permission` = VALUES(`permission`);

