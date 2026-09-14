-- ----------------------------
-- 用印申请单表结构更新脚本
-- 添加申请人姓名字段
-- ----------------------------

-- 检查字段是否存在，如果不存在则添加
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                   WHERE TABLE_SCHEMA = DATABASE() 
                   AND TABLE_NAME = 'oa_seal_apply_bill' 
                   AND COLUMN_NAME = 'creator_name');

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE oa_seal_apply_bill ADD COLUMN creator_name varchar(100) DEFAULT NULL COMMENT ''申请人姓名'' AFTER is_urgent',
    'SELECT ''Column creator_name already exists'' as message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 如果字段刚刚添加，更新现有数据的creator_name字段
-- 这里可以根据实际需求设置默认值或从其他表关联获取
UPDATE oa_seal_apply_bill 
SET creator_name = '系统用户' 
WHERE creator_name IS NULL AND creator IS NOT NULL;
