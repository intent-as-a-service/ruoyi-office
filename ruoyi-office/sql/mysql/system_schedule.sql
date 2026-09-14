-- ----------------------------
-- 日程管理表
-- ----------------------------
CREATE TABLE `system_schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日程ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `content` text COMMENT '内容',
  `schedule_date` date NOT NULL COMMENT '日程日期',
  `start_time` time DEFAULT NULL COMMENT '开始时间',
  `end_time` time DEFAULT NULL COMMENT '结束时间',
  `schedule_type` varchar(50) DEFAULT NULL COMMENT '日程类型（字典：schedule_type）',
  `schedule_category` varchar(50) DEFAULT NULL COMMENT '日程分类（字典：schedule_category）',
  `creator_id` bigint NOT NULL COMMENT '创建人ID',
  `creator_name` varchar(100) DEFAULT NULL COMMENT '创建人姓名',
  `is_pushed` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否推送（0否 1是）',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0开启 1关闭）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_schedule_date` (`schedule_date`, `deleted`) USING BTREE,
  KEY `idx_creator_id` (`creator_id`, `deleted`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`, `deleted`) USING BTREE,
  KEY `idx_status` (`status`, `deleted`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日程管理表';

