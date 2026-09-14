-- ----------------------------
-- 会议室管理表添加图片和坐席数字段
-- ----------------------------
-- 添加会议室图片字段
ALTER TABLE `oa_meeting_room` 
ADD COLUMN `pic_url` varchar(500) DEFAULT NULL COMMENT '会议室图片URL' AFTER `available_status`;

-- 添加坐席数字段
ALTER TABLE `oa_meeting_room` 
ADD COLUMN `seat_count` int DEFAULT NULL COMMENT '坐席数' AFTER `pic_url`;

