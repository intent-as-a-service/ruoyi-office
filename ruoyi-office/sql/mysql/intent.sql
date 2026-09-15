-- =====================================================================
-- 意图即服务（Intent as a Service）· 建库脚本
--
-- 用途：为 yudao 宿主补齐意图功能所需的全部表结构与后台菜单，
--       普通研发 clone 仓库后按下述顺序导入即可跑起来：
--
--         CREATE DATABASE `ruoyi-office` DEFAULT CHARACTER SET utf8mb4;
--         mysql -uroot -p ruoyi-office < sql/mysql/ruoyi-vue-pro.sql   -- ① 基础表（上游 yudao）
--         mysql -uroot -p ruoyi-office < sql/mysql/quartz.sql          -- ② 定时任务表（可选）
--         mysql -uroot -p ruoyi-office < sql/mysql/intent.sql          -- ③ 本文件：意图相关
--
-- 四张表的分工：
--   intent_spec      意图规范（入参 Schema / 提示词 / 工具白名单 / 输出契约）
--                    —— 启动时由 classpath 下的 intent/*.yaml 自动播种
--   intent_config    意图运营配置（上架状态、可见角色）——「意图管理」页维护
--   intent_rule      声明式事实规则（页面待办与徽标）—— 启动时由 intent-rules/*.yaml 播种
--   intent_executor  执行器档案（技能 / 流程编排）——「执行器档案」页维护
--
-- 菜单：意图中心（/intent）下含 意图调试台 / 意图管理 / 执行器档案 / 自动跟进规则 及按钮权限。
--       本脚本附带超级管理员的授权；其他角色请在「系统管理 → 角色」里自行勾选。
--
-- 说明：本脚本全部幂等（IF NOT EXISTS / INSERT ... 判重），可重复执行；
--       它已覆盖历史增量脚本 add_intent_executor.sql 的内容，无需再单独执行后者。
-- =====================================================================

-- ---------------------------------------------------------------- 表结构

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `intent_spec` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `intent_id` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '意图编号（系统.域.动作）',
  `spec_json` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'IntentSpec 完整定义（JSON）',
  `source` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'custom' COMMENT '来源（builtin=classpath 种子 / custom=后台创建）',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_intent` (`intent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='意图规范';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `intent_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `intent_id` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '意图编号',
  `enabled` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否上架',
  `roles` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '["*"]' COMMENT '可见角色编码（JSON 数组，["*"]=不限制）',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_intent` (`intent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='意图配置';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `intent_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `rule_key` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '规则标识（YAML 里的 id）',
  `title` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '规则名称',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态：0=草稿 1=已启用',
  `yaml` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '规则定义（YAML）',
  `source_text` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '生成规则时的自然语言原话',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_rule_key` (`rule_key`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='自动跟进规则';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `intent_executor` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `executor_id` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '执行器标识（IntentSpec.executor 引用）',
  `profile_json` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'ExecutorProfile 完整定义（JSON）',
  `source` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'custom' COMMENT '来源（builtin=classpath 种子 / custom=后台创建）',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_executor` (`executor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='执行器档案';
/*!40101 SET character_set_client = @saved_cs_client */;

-- ---------------------------------------------------------------- 后台菜单

INSERT  IGNORE INTO `system_menu` VALUES (9100,'意图中心','',1,90,0,'/intent','ant-design:api-outlined',NULL,NULL,0,_binary '',_binary '',_binary '','1','2026-09-11 15:39:05','1','2026-09-11 15:39:05',_binary '\0'),(9101,'意图调试台','',2,1,9100,'center','ant-design:thunderbolt-outlined','intent/center/index','IntentCenter',0,_binary '',_binary '',_binary '','1','2026-09-11 15:39:05','1','2026-09-12 02:09:25',_binary '\0'),(9102,'意图管理','intent:config:query',2,2,9100,'config','ant-design:setting-outlined','intent/config/index','IntentConfig',0,_binary '',_binary '',_binary '','1','2026-09-11 15:39:05','1','2026-09-11 15:39:05',_binary '\0'),(9103,'意图配置保存','intent:config:update',3,1,9102,'','',NULL,NULL,0,_binary '',_binary '',_binary '','1','2026-09-11 15:39:05','1','2026-09-11 15:39:05',_binary '\0'),(9111,'执行器档案','',2,2,9100,'executor','ep:setting','intent/executor/index','IntentExecutor',0,_binary '',_binary '',_binary '','1','2026-09-12 15:50:55','1','2026-09-12 16:41:53',_binary '\0'),(9112,'执行器查询','intent:executor:query',3,1,9111,'','','',NULL,0,_binary '',_binary '',_binary '','1','2026-09-12 15:50:55','1','2026-09-12 15:50:55',_binary '\0'),(9113,'执行器维护','intent:executor:update',3,2,9111,'','','',NULL,0,_binary '',_binary '',_binary '','1','2026-09-12 15:50:55','1','2026-09-12 15:50:55',_binary '\0'),(9121,'自动跟进规则','',2,3,9100,'rule','ep:magic-stick','intent/rule/index','IntentRule',0,_binary '',_binary '',_binary '','1','2026-09-14 15:14:43','1','2026-09-14 15:15:17',_binary '\0'),(9122,'规则查询','intent:rule:query',3,1,9121,'','','',NULL,0,_binary '',_binary '',_binary '','1','2026-09-14 15:14:43','1','2026-09-14 15:15:17',_binary '\0'),(9123,'规则维护','intent:rule:update',3,2,9121,'','','',NULL,0,_binary '',_binary '',_binary '','1','2026-09-14 15:14:43','1','2026-09-14 15:15:17',_binary '\0');

-- ---------------------------------------------------------------- 菜单授权（超级管理员）

-- 给超级管理员（role_id = 1）授予上述菜单。
-- 刻意不写自增主键 id：线上库那批 id 在别的库里可能已被占用，
-- 用 INSERT IGNORE 会连整段授权一起静默跳过（实测踩到）。
-- 这里按 (role_id, menu_id) 判重，可移植且可重复执行。
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`,
                                  `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 1, m.id, '1', NOW(), '', NOW(), b'0', 0
FROM `system_menu` m
WHERE m.id IN (9100,9101,9102,9103,9111,9112,9113,9121,9122,9123)
  AND NOT EXISTS (SELECT 1 FROM `system_role_menu` rm
                  WHERE rm.role_id = 1 AND rm.menu_id = m.id);
