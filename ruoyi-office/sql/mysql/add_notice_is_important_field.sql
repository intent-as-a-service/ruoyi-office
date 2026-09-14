-- =============================================
-- 为通知公告表添加是否重要通知字段
-- 执行时间：2025-01-XX
-- 说明：添加is_important字段，用于标识是否为重要通知
-- =============================================

-- 添加是否重要通知字段
ALTER TABLE `system_notice` 
ADD COLUMN `is_important` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否重要通知（0否 1是）' AFTER `status`;

-- 更新公告类型字段注释，说明使用字典
ALTER TABLE `system_notice` 
MODIFY COLUMN `type` tinyint NOT NULL COMMENT '公告类型，字典类型：system_notice_type（通知公告、公司动态、行业咨询、规章制度等）';
