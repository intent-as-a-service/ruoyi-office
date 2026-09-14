-- ----------------------------
-- 员工档案日期字段类型修改脚本
-- 将 datetime 类型改为 date 类型，统一使用日期格式（不含时间）
-- ----------------------------

-- 修改员工信息表的日期字段
ALTER TABLE `hrm_employee` 
  MODIFY COLUMN `birthday` date DEFAULT NULL COMMENT '出生日期',
  MODIFY COLUMN `entry_date` date DEFAULT NULL COMMENT '入职日期',
  MODIFY COLUMN `formal_date` date DEFAULT NULL COMMENT '转正日期';

ALTER TABLE `hrm_employee` 
  ADD COLUMN `user_id` bigint DEFAULT NULL COMMENT '关联用户ID',
  ADD COLUMN `user_generated` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已生成用户';

ALTER TABLE `hrm_employee` 
  ADD COLUMN `email` varchar(50) DEFAULT NULL COMMENT '邮箱';

-- 修改员工工作经历表的日期字段
ALTER TABLE `hrm_employee_work_experience` 
  MODIFY COLUMN `start_time` date DEFAULT NULL COMMENT '开始时间',
  MODIFY COLUMN `end_time` date DEFAULT NULL COMMENT '截止时间';

-- 修改员工教育经历表的日期字段
ALTER TABLE `hrm_employee_education` 
  MODIFY COLUMN `start_time` date DEFAULT NULL COMMENT '开始时间',
  MODIFY COLUMN `end_time` date DEFAULT NULL COMMENT '截止时间';

