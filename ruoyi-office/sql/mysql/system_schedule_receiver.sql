-- ----------------------------
-- 日程接收人关系表
-- ----------------------------
CREATE TABLE `system_schedule_receiver` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `schedule_id` bigint NOT NULL COMMENT '日程ID',
  `receiver_id` bigint NOT NULL COMMENT '接收人ID',
  `receiver_name` varchar(100) DEFAULT NULL COMMENT '接收人姓名',
  `read_status` tinyint NOT NULL DEFAULT '0' COMMENT '已读状态（0未读 1已读）',
  `read_time` datetime DEFAULT NULL COMMENT '已读时间',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_schedule_id` (`schedule_id`, `deleted`) USING BTREE,
  KEY `idx_receiver_id` (`receiver_id`, `deleted`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`, `deleted`) USING BTREE,
  KEY `idx_read_status` (`read_status`, `deleted`) USING BTREE,
  UNIQUE KEY `uk_schedule_receiver` (`schedule_id`, `receiver_id`, `deleted`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日程接收人关系表';

