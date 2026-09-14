-- ----------------------------
-- 为员工档案表添加职位字段
-- ----------------------------
ALTER TABLE `hrm_employee` 
ADD COLUMN `job_post` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职位' AFTER `bank_account`;

-- ----------------------------
-- 为员工入职申请单表添加职位字段
-- ----------------------------
ALTER TABLE `hrm_employee_entry_bill` 
ADD COLUMN `job_post` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职位' AFTER `emp_company_name`;

