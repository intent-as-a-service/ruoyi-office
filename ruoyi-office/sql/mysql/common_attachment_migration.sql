-- 附件表迁移脚本：从 oa_attachment 迁移到 common_attachment
-- 执行时间：请在业务低峰期执行

-- 1. 创建新的通用附件表
CREATE TABLE IF NOT EXISTS `common_attachment` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '附件ID',
    `business_type` varchar(50) NOT NULL COMMENT '业务类型',
    `business_id` bigint NOT NULL COMMENT '业务单据ID',
    `file_name` varchar(255) NOT NULL COMMENT '文件名称',
    `file_path` varchar(500) NOT NULL COMMENT '文件路径',
    `file_url` varchar(500) NOT NULL COMMENT '文件访问URL',
    `file_size` bigint NOT NULL COMMENT '文件大小（字节）',
    `file_type` varchar(100) DEFAULT NULL COMMENT '文件类型（MIME类型）',
    `file_extension` varchar(20) DEFAULT NULL COMMENT '文件扩展名',
    `upload_time` datetime DEFAULT NULL COMMENT '上传时间',
    `sort_order` int DEFAULT 0 COMMENT '排序顺序',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `creator` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_business` (`business_type`, `business_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通用附件信息表';

-- 2. 迁移数据（如果 oa_attachment 表存在）
INSERT INTO `common_attachment` (
    `id`, `business_type`, `business_id`, `file_name`, `file_path`, `file_url`, 
    `file_size`, `file_type`, `file_extension`, `upload_time`, `sort_order`, 
    `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`
)
SELECT 
    `id`, `business_type`, `business_id`, `file_name`, `file_path`, `file_url`, 
    `file_size`, `file_type`, `file_extension`, `upload_time`, `sort_order`, 
    `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`
FROM `oa_attachment`
WHERE NOT EXISTS (
    SELECT 1 FROM `common_attachment` WHERE `common_attachment`.`id` = `oa_attachment`.`id`
);

-- 3. 验证数据迁移结果
-- SELECT COUNT(*) as oa_count FROM `oa_attachment`;
-- SELECT COUNT(*) as common_count FROM `common_attachment`;

-- 4. 备份完成后，可以删除旧表（请谨慎操作，建议先备份）
-- DROP TABLE IF EXISTS `oa_attachment`;
