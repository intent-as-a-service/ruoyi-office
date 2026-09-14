-- ----------------------------
-- 企业云盘 - 文件信息表添加文件分类字段
-- ----------------------------
ALTER TABLE `oa_file_info` 
ADD COLUMN `file_category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'other' COMMENT '文件分类（all全部 image图片 document文档 video视频 audio音频 archive压缩包 other其他）' AFTER `file_suffix`;

-- 添加索引
ALTER TABLE `oa_file_info` 
ADD INDEX `idx_file_category`(`file_category` ASC) USING BTREE;

