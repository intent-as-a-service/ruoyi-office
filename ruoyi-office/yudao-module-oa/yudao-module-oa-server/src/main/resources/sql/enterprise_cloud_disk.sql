/*
 OA协同办公-企业云盘模块 - 数据库表结构设计
 
 包含三个核心表：
 1. oa_file_info - 文件信息表
 2. oa_file_permission - 文件权限表  
 3. oa_file_favorite - 收藏文件表
 
 Date: 2025-01-09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for oa_file_info
-- 企业云盘 - 文件信息表
-- ----------------------------
DROP TABLE IF EXISTS `oa_file_info`;
CREATE TABLE `oa_file_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父文件夹ID，0表示根目录',
  `file_type` tinyint NOT NULL COMMENT '文件类型（0文件夹 1文件）',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名称',
  `file_size` bigint NULL DEFAULT 0 COMMENT '文件大小（字节），文件夹为0',
  `file_extension` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件扩展名',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件存储路径',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件访问URL',
  `file_md5` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件MD5值',
  `is_shared` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否共享文件夹（0否 1是），仅文件夹有效',
  `share_type` tinyint NULL DEFAULT NULL COMMENT '文件夹分享类型（0人员 1组织），仅共享文件夹有效',
  `share_permission` tinyint NULL DEFAULT NULL COMMENT '分享权限（0仅查看 1可管理），仅共享文件夹有效',
  `owner_id` bigint NOT NULL COMMENT '所有者ID（用户ID）',
  `owner_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所有者名称',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '所属部门ID',
  `dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属部门名称',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序序号',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_owner_id`(`owner_id` ASC) USING BTREE,
  INDEX `idx_file_type`(`file_type` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'OA协同办公-企业云盘-文件信息表';

-- ----------------------------
-- Records of oa_file_info
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for oa_file_permission
-- 企业云盘 - 文件权限表
-- ----------------------------
DROP TABLE IF EXISTS `oa_file_permission`;
CREATE TABLE `oa_file_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `file_id` bigint NOT NULL COMMENT '文件ID',
  `share_type` tinyint NOT NULL COMMENT '分享类型（0人员 1组织）',
  `target_id` bigint NOT NULL COMMENT '目标ID（人员ID或组织ID）',
  `target_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '目标名称（人员名称或组织名称）',
  `permission` tinyint NOT NULL DEFAULT 0 COMMENT '权限（0仅查看 1可管理）',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_file_id`(`file_id` ASC) USING BTREE,
  INDEX `idx_target`(`share_type` ASC, `target_id` ASC) USING BTREE,
  UNIQUE INDEX `uk_file_share_target`(`file_id` ASC, `share_type` ASC, `target_id` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'OA协同办公-企业云盘-文件权限表';

-- ----------------------------
-- Records of oa_file_permission
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for oa_file_favorite
-- 企业云盘 - 收藏文件表
-- ----------------------------
DROP TABLE IF EXISTS `oa_file_favorite`;
CREATE TABLE `oa_file_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `file_id` bigint NOT NULL COMMENT '文件ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名称',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_file_id`(`file_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  UNIQUE INDEX `uk_file_user`(`file_id` ASC, `user_id` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'OA协同办公-企业云盘-收藏文件表';

-- ----------------------------
-- Records of oa_file_favorite
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;

