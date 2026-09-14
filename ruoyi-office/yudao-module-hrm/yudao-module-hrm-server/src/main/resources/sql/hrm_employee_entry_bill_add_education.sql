-- ----------------------------
-- 为员工入职申请单表添加文化程度字段（如果表已存在但缺少该字段）
-- ----------------------------

-- 检查并添加 education 字段（如果不存在）
-- 注意：如果字段已存在，执行此语句会报错，可以忽略
ALTER TABLE `hrm_employee_entry_bill` 
ADD COLUMN `education` varchar(50) DEFAULT NULL COMMENT '文化程度' 
AFTER `employee_status`;

-- 为员工入职申请单表添加政治面貌、婚姻状况字段（如果不存在）
ALTER TABLE `hrm_employee_entry_bill`
ADD COLUMN `political_status` varchar(50) DEFAULT NULL COMMENT '政治面貌' AFTER `nation`,
ADD COLUMN `marital_status` varchar(50) DEFAULT NULL COMMENT '婚姻状况' AFTER `political_status`;

-- 为员工档案表添加政治面貌、婚姻状况字段（如果不存在）
ALTER TABLE `hrm_employee`
ADD COLUMN `political_status` varchar(50) DEFAULT NULL COMMENT '政治面貌' AFTER `nation`,
ADD COLUMN `marital_status` varchar(50) DEFAULT NULL COMMENT '婚姻状况' AFTER `political_status`;

