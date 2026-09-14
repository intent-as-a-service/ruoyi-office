-- ========================================
-- 企业云盘文件权限表结构更新脚本
-- 执行前请备份数据库！
-- ========================================

-- 1. 如果表已存在，先备份数据（如果有的话）
-- CREATE TABLE oa_file_permission_backup AS SELECT * FROM oa_file_permission WHERE 1=1;

-- 2. 删除旧表并重新创建（包含所有必需字段）
DROP TABLE IF EXISTS `oa_file_permission`;

CREATE TABLE `oa_file_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `file_id` bigint NOT NULL COMMENT '文件ID',
  `share_type` tinyint NOT NULL COMMENT '分享类型（0人员 1组织）',
  `target_id` bigint NOT NULL COMMENT '目标ID（人员ID或组织ID）',
  `target_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '目标名称（人员名称或组织名称）',
  `permission` tinyint NOT NULL DEFAULT 0 COMMENT '权限（0仅查看 1可管理）',
  `inherit_permission` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否继承权限（0否 1是）',
  `share_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分享路径（用于显示层级结构）',
  `root_share_id` bigint NULL DEFAULT NULL COMMENT '根分享文件夹ID（用于快速定位）',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `share_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分享码（可选）',
  `access_count` int NOT NULL DEFAULT 0 COMMENT '访问次数',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_file_id`(`file_id` ASC) USING BTREE,
  INDEX `idx_target`(`share_type` ASC, `target_id` ASC) USING BTREE,
  INDEX `idx_root_share`(`root_share_id` ASC) USING BTREE,
  INDEX `idx_share_code`(`share_code` ASC) USING BTREE,
  UNIQUE INDEX `uk_file_share_target`(`file_id` ASC, `share_type` ASC, `target_id` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'OA协同办公-企业云盘-文件权限表';

-- 3. 如果需要恢复备份数据，可以执行以下语句（根据实际情况调整）
-- INSERT INTO oa_file_permission (file_id, share_type, target_id, target_name, permission, creator, create_time, updater, update_time, deleted, tenant_id)
-- SELECT file_id, share_type, target_id, target_name, permission, creator, create_time, updater, update_time, deleted, tenant_id
-- FROM oa_file_permission_backup;

-- 4. 清理备份表（可选）
-- DROP TABLE IF EXISTS oa_file_permission_backup;

COMMIT;
