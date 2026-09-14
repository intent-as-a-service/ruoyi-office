-- ----------------------------
-- 员工档案表增加 dept_name 和 company_id 字段
-- ----------------------------

-- 添加部门名称字段
ALTER TABLE `hrm_employee` 
  ADD COLUMN `dept_name` varchar(100) DEFAULT NULL COMMENT '所属部门名称' AFTER `dept_id`;

-- 添加公司ID字段
ALTER TABLE `hrm_employee` 
  ADD COLUMN `company_id` bigint DEFAULT NULL COMMENT '所属公司ID' AFTER `dept_name`;

-- 添加索引
ALTER TABLE `hrm_employee` 
  ADD KEY `idx_company_id` (`company_id`) USING BTREE;



