-- ----------------------------
-- 会议室管理表结构
-- ----------------------------
CREATE TABLE IF NOT EXISTS `oa_meeting_room` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `room_name` varchar(100) NOT NULL COMMENT '会议室名称',
  `room_location` varchar(255) NOT NULL COMMENT '会议室位置',
  `room_type` int NOT NULL COMMENT '会议室类型',
  `manager_id` bigint NOT NULL COMMENT '负责人ID',
  `manager_name` varchar(100) NOT NULL COMMENT '负责人姓名',
  `manager_phone` varchar(20) DEFAULT NULL COMMENT '负责人联系方式',
  `available_status` int NOT NULL DEFAULT 0 COMMENT '可用状态（0正常 1维修中 2不可用）',
  `pic_url` varchar(500) DEFAULT NULL COMMENT '会议室图片URL',
  `seat_count` int DEFAULT NULL COMMENT '坐席数',
  `equipment` varchar(500) DEFAULT NULL COMMENT '会议室设备（逗号分隔：tv,computer,remote,projector,water_dispenser,locker）',
  `attachment_url` varchar(1000) DEFAULT NULL COMMENT '附件URL',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注（200字以内）',
  `allow_booking` bit(1) NOT NULL DEFAULT b'1' COMMENT '允许预定',
  `need_approval` bit(1) NOT NULL DEFAULT b'0' COMMENT '预定需审批',
  `booking_scope` int NOT NULL DEFAULT 0 COMMENT '可用范围（0全部成员 1指定成员）',
  `booking_members` varchar(2000) DEFAULT NULL COMMENT '可预定成员ID（逗号分隔，当booking_scope=1时有效）',
  `sort` int DEFAULT 0 COMMENT '显示顺序',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_room_type` (`room_type`) USING BTREE,
  KEY `idx_available_status` (`available_status`) USING BTREE,
  KEY `idx_manager_id` (`manager_id`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会议室信息表';

-- ----------------------------
-- 会议室类型字典
-- ----------------------------
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`) 
VALUES ('会议室类型', 'oa_meeting_room_type', 0, '会议室类型字典')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`) VALUES
(1, '内部会议室', '1', 'oa_meeting_room_type', 0, 'primary', '', '公司内部会议室'),
(2, '外部会议室', '2', 'oa_meeting_room_type', 0, 'success', '', '外部租用会议室'),
(3, '培训室', '3', 'oa_meeting_room_type', 0, 'info', '', '培训专用会议室'),
(4, '多功能厅', '4', 'oa_meeting_room_type', 0, 'warning', '', '多功能会议厅')
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`), `value` = VALUES(`value`);

-- ----------------------------
-- 会议室可用状态字典
-- ----------------------------
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`) 
VALUES ('会议室可用状态', 'oa_meeting_room_status', 0, '会议室可用状态字典')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`) VALUES
(1, '正常', '0', 'oa_meeting_room_status', 0, 'success', '', '会议室正常可用'),
(2, '维修中', '1', 'oa_meeting_room_status', 0, 'warning', '', '会议室维修中'),
(3, '不可用', '2', 'oa_meeting_room_status', 0, 'danger', '', '会议室不可用')
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`), `value` = VALUES(`value`);

-- ----------------------------
-- 会议室使用状态字典
-- ----------------------------
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`) 
VALUES ('会议室使用状态', 'oa_meeting_room_use_status', 0, '会议室使用状态字典')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`) VALUES
(1, '空闲中', '0', 'oa_meeting_room_use_status', 0, 'success', '', '会议室空闲中'),
(2, '使用中', '1', 'oa_meeting_room_use_status', 0, 'warning', '', '会议室使用中')
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`), `value` = VALUES(`value`);

-- ----------------------------
-- 会议室设备类型字典
-- ----------------------------
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`) 
VALUES ('会议室设备类型', 'oa_meeting_room_equipment', 0, '会议室设备类型字典')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`) VALUES
(1, '电视', 'tv', 'oa_meeting_room_equipment', 0, 'default', '', '电视设备'),
(2, '电脑', 'computer', 'oa_meeting_room_equipment', 0, 'default', '', '电脑设备'),
(3, '遥控器', 'remote', 'oa_meeting_room_equipment', 0, 'default', '', '遥控器'),
(4, '投影仪', 'projector', 'oa_meeting_room_equipment', 0, 'default', '', '投影仪'),
(5, '饮水机', 'water_dispenser', 'oa_meeting_room_equipment', 0, 'default', '', '饮水机'),
(6, '置物柜', 'locker', 'oa_meeting_room_equipment', 0, 'default', '', '置物柜')
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`), `value` = VALUES(`value`);

-- ----------------------------
-- 会议室管理菜单权限
-- ----------------------------
-- 父菜单：OA协同办公
SET @parent_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = 'OA协同办公' LIMIT 1);

-- 如果没有OA协同办公菜单，则创建
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
SELECT 'OA协同办公', '', 1, 20, 0, '/oa', 'ep:briefcase', NULL, NULL, 0, 1, 1, 1
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `name` = 'OA协同办公');

SET @parent_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = 'OA协同办公' LIMIT 1);

-- 会议室管理菜单
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
VALUES ('会议室管理', '', 2, 30, @parent_menu_id, 'meetingroom', 'ep:office-building', 'oa/meetingroom/index', 'OaMeetingRoom', 0, 1, 1, 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

SET @meeting_room_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = '会议室管理' AND `parent_id` = @parent_menu_id LIMIT 1);

-- 会议室管理按钮权限
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `status`, `visible`, `keep_alive`, `always_show`) VALUES
('查询会议室', 'oa:meeting-room:query', 3, 1, @meeting_room_menu_id, '', '', '', 0, 1, 1, 1),
('创建会议室', 'oa:meeting-room:create', 3, 2, @meeting_room_menu_id, '', '', '', 0, 1, 1, 1),
('更新会议室', 'oa:meeting-room:update', 3, 3, @meeting_room_menu_id, '', '', '', 0, 1, 1, 1),
('删除会议室', 'oa:meeting-room:delete', 3, 4, @meeting_room_menu_id, '', '', '', 0, 1, 1, 1),
('导出会议室', 'oa:meeting-room:export', 3, 5, @meeting_room_menu_id, '', '', '', 0, 1, 1, 1)
ON DUPLICATE KEY UPDATE `permission` = VALUES(`permission`);

-- ----------------------------
-- 示例数据（可选）
-- ----------------------------
-- INSERT INTO `oa_meeting_room` (`room_name`, `room_location`, `room_type`, `manager_id`, `manager_name`, `manager_phone`, `available_status`, `equipment`, `sort`, `remark`, `creator`, `create_time`)
-- VALUES
-- ('第一会议室', '公司本部二楼', 1, 1, '张三', '13800138000', 0, 'tv,computer,projector', 1, '可容纳20人', 'admin', NOW()),
-- ('第二会议室', '公司本部三楼', 1, 2, '李四', '13800138001', 0, 'tv,projector', 2, '可容纳10人', 'admin', NOW());

