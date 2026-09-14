-- ----------------------------
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

