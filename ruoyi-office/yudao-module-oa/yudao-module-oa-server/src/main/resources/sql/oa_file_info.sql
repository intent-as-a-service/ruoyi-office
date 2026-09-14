-- ----------------------------
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
  `file_suffix` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件后缀名（不含点号）',
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

