-- 用章申请单表
CREATE TABLE `oa_seal_apply_bill` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `bill_code` varchar(50) NOT NULL COMMENT '单据编号',
    `process_instance_id` varchar(64) DEFAULT NULL COMMENT '流程实例编号',
    `process_status` tinyint DEFAULT '0' COMMENT '单据状态（0草稿 1审批中 2审批通过 3审批拒绝 4已取消）',
    
    -- 印章相关信息
    `seal_id` bigint NOT NULL COMMENT '印章ID',
    `seal_no` varchar(50) NOT NULL COMMENT '印章编号',
    `seal_name` varchar(100) DEFAULT NULL COMMENT '印章名称',
    `seal_type` tinyint DEFAULT NULL COMMENT '印章类型',
    `keeper_id` bigint DEFAULT NULL COMMENT '保管人ID',
    `keeper_name` varchar(50) DEFAULT NULL COMMENT '保管人名称',
    `keeper_dept_id` bigint DEFAULT NULL COMMENT '保管部门ID',
    `keeper_dept_name` varchar(100) DEFAULT NULL COMMENT '保管部门名称',
    
    -- 用章申请信息
    `use_purpose` varchar(500) NOT NULL COMMENT '用章事由',
    `use_type` tinyint NOT NULL COMMENT '用章类型（1合同用章 2证明用章 3公函用章 4其他用章）',
    `use_mode` tinyint NOT NULL COMMENT '用章方式（1现场用章 2外借用章）',
    `document_title` varchar(200) DEFAULT NULL COMMENT '文件标题',
    `document_type` varchar(100) DEFAULT NULL COMMENT '文件类型',
    `document_count` int DEFAULT 1 COMMENT '文件份数',
    `contract_amount` decimal(15,2) DEFAULT NULL COMMENT '合同金额（合同用章时填写）',
    `contract_party` varchar(200) DEFAULT NULL COMMENT '合同对方（合同用章时填写）',
    
    -- 时间相关
    `expected_use_time` datetime DEFAULT NULL COMMENT '预计用章时间',
    `actual_use_time` datetime DEFAULT NULL COMMENT '实际用章时间',
    
    -- 外借用章专用字段
    `expected_return_time` datetime DEFAULT NULL COMMENT '预计归还时间（外借用章时填写）',
    `actual_return_time` datetime DEFAULT NULL COMMENT '实际归还时间（外借用章时填写）',

    -- 用章状态
    `use_status` tinyint DEFAULT '0' COMMENT '用章状态（0待处理 1已完成 2外借中 3已归还 4已逾期）',
    `is_urgent` tinyint DEFAULT '0' COMMENT '是否紧急（0否 1是）',
    
    -- 基础字段
    `creator_name` varchar(100) DEFAULT NULL COMMENT '申请人姓名',
    `company_id` bigint NOT NULL COMMENT '公司ID',
    `company_name` varchar(100) NOT NULL COMMENT '公司名称',
    `dept_id` bigint NOT NULL COMMENT '部门ID',
    `dept_name` varchar(100) NOT NULL COMMENT '部门名称',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `creator` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_bill_code` (`bill_code`),
    KEY `idx_seal_id` (`seal_id`),
    KEY `idx_company_id` (`company_id`),
    KEY `idx_creator_id` (`creator`),
    KEY `idx_process_status` (`process_status`),
    KEY `idx_use_status` (`use_status`),
    KEY `idx_use_mode` (`use_mode`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用章申请单';