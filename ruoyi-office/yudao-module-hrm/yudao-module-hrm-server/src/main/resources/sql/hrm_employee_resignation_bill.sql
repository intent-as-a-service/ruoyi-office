-- ----------------------------
-- 员工离职申请单表结构
-- ----------------------------

DROP TABLE IF EXISTS `hrm_employee_resignation_bill`;
CREATE TABLE `hrm_employee_resignation_bill` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `bill_code` varchar(50) NOT NULL COMMENT '单据编号',
    `process_instance_id` varchar(64) DEFAULT NULL COMMENT '流程实例编号',
    `process_status` tinyint DEFAULT '0' COMMENT '单据状态（0草稿 1审批中 2审批通过 3审批拒绝 4已取消）',
    
    -- 员工信息（冗余存储，方便审批时查看）
    `employee_id` bigint NOT NULL COMMENT '关联的员工档案ID',
    `employee_no` varchar(50) DEFAULT NULL COMMENT '员工工号',
    `name` varchar(100) NOT NULL COMMENT '姓名',
    `sex` tinyint DEFAULT NULL COMMENT '性别（1:男 2:女）',
    `mobile` varchar(20) DEFAULT NULL COMMENT '手机号',
    `emp_dept_id` bigint NOT NULL COMMENT '员工所属部门ID',
    `emp_dept_name` varchar(100) NOT NULL COMMENT '员工所属部门名称',
    `emp_company_id` bigint NOT NULL COMMENT '员工所属公司ID',
    `emp_company_name` varchar(100) NOT NULL COMMENT '员工所属公司名称',
    `job_post` varchar(100) DEFAULT NULL COMMENT '职位',
    `job_position` varchar(100) DEFAULT NULL COMMENT '职务',
    `employee_status` tinyint NOT NULL DEFAULT 1 COMMENT '当前人员状态（1:正式 2:试用期 3:实习生 4:兼职 5:零时工）',
    
    -- 离职信息
    `resignation_type` varchar(50) DEFAULT NULL COMMENT '离职类型（1:主动离职 2:被动离职 3:其他）',
    `application_date` date DEFAULT NULL COMMENT '申请日期',
    `resignation_date` date DEFAULT NULL COMMENT '离职日期',
    `last_working_date` date DEFAULT NULL COMMENT '最后工作日期',
    `handover_person_id` bigint DEFAULT NULL COMMENT '工作交接人ID',
    `handover_person_name` varchar(100) DEFAULT NULL COMMENT '工作交接人姓名',
    `resignation_reason` varchar(50) DEFAULT NULL COMMENT '离职原因（1:个人原因 2:薪资原因 3:晋升原因 4:工作时长 5:其它）',
    `resignation_reason_desc` varchar(500) DEFAULT NULL COMMENT '离职原因说明',
    `salary_settlement_date` date DEFAULT NULL COMMENT '薪资结算日期',
    
    -- 制单人信息（单据必须的信息）
    `dept_id` bigint NOT NULL COMMENT '制单人部门ID',
    `dept_name` varchar(100) NOT NULL COMMENT '制单人部门名称',
    `company_id` bigint NOT NULL COMMENT '制单人公司ID',
    `company_name` varchar(100) NOT NULL COMMENT '制单人公司名称',
    
    -- 基础字段
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `creator` varchar(64) DEFAULT '' COMMENT '创建者',
    `creator_name` varchar(100) DEFAULT NULL COMMENT '创建者姓名',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_bill_code` (`bill_code`, `deleted`, `tenant_id`),
    KEY `idx_employee_id` (`employee_id`),
    KEY `idx_emp_dept_id` (`emp_dept_id`),
    KEY `idx_emp_company_id` (`emp_company_id`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_company_id` (`company_id`),
    KEY `idx_process_status` (`process_status`),
    KEY `idx_resignation_type` (`resignation_type`),
    KEY `idx_resignation_date` (`resignation_date`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_creator` (`creator`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工离职申请单';

