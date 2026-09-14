-- ----------------------------
-- 员工入职申请单表结构
-- ----------------------------

DROP TABLE IF EXISTS `hrm_employee_entry_bill`;
CREATE TABLE `hrm_employee_entry_bill` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `bill_code` varchar(50) NOT NULL COMMENT '单据编号',
    `process_instance_id` varchar(64) DEFAULT NULL COMMENT '流程实例编号',
    `process_status` tinyint DEFAULT '0' COMMENT '单据状态（0草稿 1审批中 2审批通过 3审批拒绝 4已取消）',
    
    -- 员工基本信息（冗余存储，方便审批时查看）
    `name` varchar(100) NOT NULL COMMENT '姓名',
    `sex` tinyint NOT NULL COMMENT '性别（1:男 2:女）',
    `birthday` date DEFAULT NULL COMMENT '出生日期',
    `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号码',
    `mobile` varchar(20) NOT NULL COMMENT '手机号',
    `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
    `nation` varchar(50) DEFAULT NULL COMMENT '民族',
    `political_status` varchar(50) DEFAULT NULL COMMENT '政治面貌',
    `marital_status` varchar(50) DEFAULT NULL COMMENT '婚姻状况',
    `native_place` varchar(200) DEFAULT NULL COMMENT '籍贯',
    `household_address` varchar(500) DEFAULT NULL COMMENT '户籍所在地',
    `current_address` varchar(500) DEFAULT NULL COMMENT '现居住地址',
    `emergency_contact` varchar(100) DEFAULT NULL COMMENT '紧急联系人',
    `emergency_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
    `avatar` varchar(500) DEFAULT NULL COMMENT '照片',
    
    -- 入职相关信息（员工所属的组织信息）
    `entry_date` date NOT NULL COMMENT '入职日期',
    `probation_period` int DEFAULT 3 COMMENT '试用期（月数）',
    `expected_formal_date` date DEFAULT NULL COMMENT '预计转正日期',
    `emp_dept_id` bigint NOT NULL COMMENT '员工所属部门ID',
    `emp_dept_name` varchar(100) NOT NULL COMMENT '员工所属部门名称',
    `emp_company_id` bigint NOT NULL COMMENT '员工所属公司ID',
    `emp_company_name` varchar(100) NOT NULL COMMENT '员工所属公司名称',
    `job_position` varchar(100) DEFAULT NULL COMMENT '职务',
    `job_title` varchar(100) DEFAULT NULL COMMENT '职称',
    `employee_status` tinyint NOT NULL DEFAULT 2 COMMENT '人员状态（1:正式 2:试用期 3:实习生 4:兼职 5:零时工）',
    `education` varchar(50) DEFAULT NULL COMMENT '文化程度',
    `salary` decimal(10,2) DEFAULT NULL COMMENT '薪资',
    `bank_name` varchar(200) DEFAULT NULL COMMENT '工资开户行',
    `bank_account` varchar(50) DEFAULT NULL COMMENT '工资卡账户',
    
    -- 关联字段
    `employee_id` bigint DEFAULT NULL COMMENT '关联的员工档案ID（审批通过后创建）',
    
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
    KEY `idx_entry_date` (`entry_date`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_creator` (`creator`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工入职申请单';

-- 注意：工作经历、教育经历、家属信息使用员工档案的明细表
-- hrm_employee_work_experience（工作经历表）
-- hrm_employee_education（教育经历表）
-- hrm_employee_family（家属信息表）
-- 这些表通过 employee_id 关联，入职申请审批通过后创建员工档案时使用


