-- =============================================================
-- 内嵌 AI 意图 SDK · 执行器档案（M2.2）：页面可维护执行器配置
-- 1) intent_executor 表：执行器档案 DB 化存储（classpath YAML 做种子）
-- 2) 菜单与权限：「意图管理」页同级新增「执行器档案」页（挂到同一父菜单）
--    + 意图管理页的执行器下拉权限沿用 intent:config:query
-- 依赖：dev.intent intent-sdk-core 0.1.0-SNAPSHOT（ExecutorProfile / 热更新）
-- =============================================================

CREATE TABLE IF NOT EXISTS `intent_executor` (
    `id`           bigint       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `executor_id`  varchar(128) NOT NULL COMMENT '执行器标识（IntentSpec.executor 引用）',
    `profile_json` mediumtext   NOT NULL COMMENT 'ExecutorProfile 完整定义（JSON）',
    `source`       varchar(16)  NOT NULL DEFAULT 'custom' COMMENT '来源（builtin=classpath 种子 / custom=后台创建）',
    `remark`       varchar(500)          DEFAULT NULL COMMENT '备注',
    `creator`      varchar(64)           DEFAULT '' COMMENT '创建者',
    `create_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`      varchar(64)           DEFAULT '' COMMENT '更新者',
    `update_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`    bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_executor` (`executor_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='执行器档案';

-- ----------------------------
-- 菜单：执行器档案页（挂在「意图中心」目录下，与「意图管理」并列，路径 /intent/executor）
-- 说明：父菜单按 component 精确匹配意图管理页（系统可能存在重名菜单）；
--       若目录结构不同，请手动调整 parent_id。
-- ----------------------------
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`,
                           `icon`, `component`, `component_name`, `status`, `visible`,
                           `keep_alive`, `always_show`, `creator`, `create_time`, `updater`,
                           `update_time`, `deleted`)
SELECT '执行器档案', '', 2, 2, t.parent_id, 'executor',
       'ep:setting', 'intent/executor/index', 'IntentExecutor', 0, b'1',
       b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM `system_menu` t
WHERE t.`component` = 'intent/config/index' AND t.`deleted` = b'0'
LIMIT 1;

-- 按钮权限：查询 / 维护
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`,
                           `icon`, `component`, `component_name`, `status`, `visible`,
                           `keep_alive`, `always_show`, `creator`, `create_time`, `updater`,
                           `update_time`, `deleted`)
SELECT '执行器查询', 'intent:executor:query', 3, 1, m.`id`, '', '', '', NULL, 0, b'1',
       b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM `system_menu` m
WHERE m.`name` = '执行器档案' AND m.`deleted` = b'0'
LIMIT 1;

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`,
                           `icon`, `component`, `component_name`, `status`, `visible`,
                           `keep_alive`, `always_show`, `creator`, `create_time`, `updater`,
                           `update_time`, `deleted`)
SELECT '执行器维护', 'intent:executor:update', 3, 2, m.`id`, '', '', '', NULL, 0, b'1',
       b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM `system_menu` m
WHERE m.`name` = '执行器档案' AND m.`deleted` = b'0'
LIMIT 1;
