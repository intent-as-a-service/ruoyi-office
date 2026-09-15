-- =====================================================================
-- 业务模块建表脚本（仅表结构，不含任何数据）
--
-- 为什么需要它：本仓携带的 yudao 业务模块（CRM / ERP / HRM / OA / IoT / AI / MP / BPM /
-- 资产 / WMS / 在线表单 …）共 154 张表，上游基础脚本 sql/mysql/ruoyi-vue-pro.sql 并不包含，
-- 不导入的话应用虽能启动、意图平台也能注册，但业务意图一执行就会报表不存在。
--
-- 导入顺序：跟在 intent.sql 之后即可
--   mysql -uroot -p ruoyi-office < sql/mysql/business-modules.sql   -- ④ 业务模块表
--
-- 说明：
--   - 全部为 CREATE TABLE IF NOT EXISTS，幂等可重复执行；只建表、不写数据。
--   - Flowable 的 act_* / flw_ru_* 等表由引擎在启动时自行创建，不在本文件内。
--   - 未包含与本项目无关的演示/临时/第三方报表表（test_* / tmp_* / rep_demo_* /
--     jimu_* / huiyuan_*），它们不属于本仓功能。
-- =====================================================================

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `ai_api_key` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
  `api_key` varchar(1024) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密钥',
  `platform` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台',
  `url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '自定义 API 地址',
  `status` int(11) NOT NULL COMMENT '状态',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI API 密钥表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `ai_chat_conversation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '对话编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户编号',
  `role_id` bigint(20) DEFAULT NULL COMMENT '聊天角色',
  `title` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '对话标题',
  `model_id` bigint(20) NOT NULL COMMENT '模型编号',
  `model` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模型标识',
  `pinned` bit(1) NOT NULL COMMENT '是否置顶',
  `pinned_time` datetime DEFAULT NULL COMMENT '置顶时间',
  `system_message` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色设定',
  `temperature` double NOT NULL COMMENT '温度参数',
  `max_tokens` int(11) NOT NULL COMMENT '单条回复的最大 Token 数量',
  `max_contexts` int(11) NOT NULL COMMENT '上下文的最大 Message 数量',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1781604279872581806 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 聊天对话表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `ai_chat_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '消息编号',
  `conversation_id` bigint(20) NOT NULL COMMENT '对话编号',
  `reply_id` bigint(20) DEFAULT NULL COMMENT '回复编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户编号',
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色编号',
  `type` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息类型',
  `model` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模型标识',
  `model_id` bigint(20) NOT NULL COMMENT '模型编号',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `reasoning_content` text COLLATE utf8mb4_unicode_ci COMMENT '推理内容',
  `use_context` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否携带上下文',
  `segment_ids` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '段落编号数组',
  `web_search_pages` text COLLATE utf8mb4_unicode_ci COMMENT '联网搜索的网页内容数组',
  `attachment_urls` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '附件 URL 数组',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3107 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 聊天消息表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `ai_chat_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色编号',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户编号',
  `model_id` bigint(20) DEFAULT NULL COMMENT '模型编号',
  `name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `avatar` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '头像',
  `category` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色类别',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '角色排序',
  `description` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色描述',
  `system_message` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色上下文',
  `knowledge_ids` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联的知识库编号数组',
  `tool_ids` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联的工具编号数组',
  `mcp_client_names` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '引用的 MCP Client 名字列表',
  `public_status` bit(1) NOT NULL COMMENT '是否公开',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 聊天角色表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `ai_image` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户编号',
  `prompt` varchar(2000) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '提示词',
  `platform` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台',
  `model_id` bigint(20) DEFAULT NULL COMMENT '模型编号',
  `model` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模型',
  `width` int(11) NOT NULL COMMENT '图片宽度',
  `height` int(11) NOT NULL COMMENT '图片高度',
  `status` tinyint(4) NOT NULL COMMENT '绘画状态',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  `error_message` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '错误信息',
  `public_status` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否发布',
  `pic_url` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片地址',
  `options` json DEFAULT NULL COMMENT '绘制参数',
  `task_id` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务编号',
  `buttons` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'mj buttons 按钮',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=199 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 绘画表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `ai_knowledge` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '知识库名称',
  `description` longtext COLLATE utf8mb4_unicode_ci COMMENT '知识库描述',
  `embedding_model_id` bigint(20) NOT NULL COMMENT '向量模型编号',
  `embedding_model` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '向量模型标识',
  `top_k` int(11) NOT NULL COMMENT 'topK',
  `similarity_threshold` double NOT NULL COMMENT '相似度阈值',
  `status` tinyint(4) NOT NULL COMMENT '是否启用',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 知识库表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `ai_knowledge_document` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `knowledge_id` bigint(20) NOT NULL COMMENT '知识库编号',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文档名称',
  `url` varchar(1024) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件 URL',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `content_length` int(11) NOT NULL COMMENT '字符数',
  `tokens` int(11) NOT NULL COMMENT 'token 数量',
  `segment_max_tokens` int(11) NOT NULL COMMENT '分片最大 Token 数',
  `retrieval_count` int(11) NOT NULL DEFAULT '0' COMMENT '召回次数',
  `status` tinyint(4) NOT NULL COMMENT '是否启用',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 知识库文档表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `ai_knowledge_segment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `knowledge_id` bigint(20) NOT NULL COMMENT '知识库编号',
  `document_id` bigint(20) NOT NULL COMMENT '文档编号',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分段内容',
  `content_length` int(11) NOT NULL COMMENT '字符数',
  `vector_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '向量库的编号',
  `tokens` int(11) NOT NULL COMMENT 'token 数量',
  `retrieval_count` int(11) NOT NULL DEFAULT '0' COMMENT '召回次数',
  `status` tinyint(4) NOT NULL COMMENT '是否启用',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=201 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 知识库分段表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `ai_mind_map` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户编号',
  `prompt` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '生成内容提示',
  `generated_content` text COLLATE utf8mb4_unicode_ci COMMENT '生成的思维导图内容',
  `platform` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台',
  `model_id` bigint(20) NOT NULL COMMENT '模型编号',
  `model` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模型',
  `error_message` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '错误信息',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 思维导图表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `ai_model` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `key_id` bigint(20) NOT NULL COMMENT 'API 秘钥编号',
  `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模型名字',
  `model` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模型标识',
  `platform` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模型平台',
  `type` tinyint(4) NOT NULL COMMENT '模型类型',
  `sort` int(11) NOT NULL COMMENT '排序',
  `status` tinyint(4) NOT NULL COMMENT '状态',
  `temperature` double DEFAULT NULL COMMENT '温度参数',
  `max_tokens` int(11) DEFAULT NULL COMMENT '单条回复的最大 Token 数量',
  `max_contexts` int(11) DEFAULT NULL COMMENT '上下文的最大 Message 数量',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 模型表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `ai_music` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户编号',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '音乐名称',
  `lyric` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '歌词',
  `image_url` varchar(600) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片地址',
  `audio_url` varchar(600) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '音频地址',
  `video_url` varchar(600) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频地址',
  `status` tinyint(4) NOT NULL COMMENT '音乐状态',
  `description` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述词',
  `prompt` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '提示词',
  `platform` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模型平台',
  `model_id` bigint(20) NOT NULL COMMENT '模型编号',
  `model` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模型',
  `generate_mode` tinyint(4) NOT NULL COMMENT '生成模式',
  `tags` varchar(600) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '音乐风格标签',
  `duration` double DEFAULT NULL COMMENT '音乐时长',
  `public_status` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否发布',
  `task_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务编号',
  `error_message` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '错误信息',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 音乐表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `ai_tool` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '工具编号',
  `name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '工具名称',
  `description` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工具描述',
  `status` tinyint(4) NOT NULL COMMENT '状态',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 工具表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `ai_workflow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(255) NOT NULL COMMENT '流程名称',
  `code` varchar(255) NOT NULL COMMENT '流程标识',
  `graph` longtext NOT NULL COMMENT '流程模型',
  `status` tinyint(4) NOT NULL COMMENT '状态',
  `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='AI 工作流';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `ai_write` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户编号',
  `type` int(11) DEFAULT NULL COMMENT '写作类型',
  `platform` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台',
  `model_id` bigint(20) NOT NULL COMMENT '模型编号',
  `model` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模型',
  `prompt` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '生成内容提示',
  `generated_content` varchar(5120) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '生成的内容',
  `original_content` varchar(5120) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '原文',
  `length` tinyint(4) DEFAULT NULL COMMENT '长度提示词',
  `format` tinyint(4) DEFAULT NULL COMMENT '格式提示词',
  `tone` tinyint(4) DEFAULT NULL COMMENT '语气提示词',
  `language` tinyint(4) DEFAULT NULL COMMENT '语言提示词',
  `error_message` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '错误信息',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=226 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 写作表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `asset_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `category_code` varchar(32) NOT NULL COMMENT '类别编码',
  `category_name` varchar(200) DEFAULT NULL COMMENT '类别名称',
  `parent_id` bigint(20) NOT NULL COMMENT '上级id',
  `level` bigint(20) DEFAULT NULL COMMENT '级别',
  `sort` int(11) DEFAULT '0' COMMENT '显示顺序',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='资产类别';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `asset_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `goods_code` varchar(32) NOT NULL COMMENT '物品编码',
  `goods_name` varchar(200) DEFAULT NULL COMMENT '物品名称',
  `asset_category_code` varchar(36) DEFAULT NULL COMMENT '资产类型编码',
  `asset_category_name` varchar(100) DEFAULT NULL COMMENT '资产类型名称',
  `asset_model` varchar(100) DEFAULT NULL COMMENT '规格型号',
  `asset_unit` varchar(100) DEFAULT NULL COMMENT '计量单位',
  `manufacturer` varchar(100) DEFAULT NULL COMMENT '厂商',
  `brand` varchar(120) DEFAULT NULL COMMENT '品牌',
  `residual_value_rate` decimal(12,4) DEFAULT NULL COMMENT '默认月残值率',
  `inventory_lower_limit` decimal(24,6) DEFAULT NULL COMMENT '库存下限',
  `inventory_limit` decimal(24,6) DEFAULT NULL COMMENT '库存上下限',
  `is_join_asset` varchar(1) DEFAULT NULL COMMENT '是否进入资产列表',
  `asset_icon` varchar(200) DEFAULT NULL COMMENT '资产图片',
  `asset_file` varchar(200) DEFAULT NULL COMMENT '资产附件',
  `sort` int(11) DEFAULT '0' COMMENT '显示顺序',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `store_address` varchar(300) DEFAULT NULL COMMENT '仓库地址',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='物品基本信息';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `asset_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `purchase_order_id` bigint(255) DEFAULT NULL COMMENT '采购订单id',
  `purchase_order_code` varchar(32) DEFAULT NULL COMMENT '采购订单编码',
  `purchase_order_name` varchar(200) DEFAULT NULL COMMENT '采购订单名称',
  `in_warehousing_id` bigint(32) DEFAULT NULL COMMENT '入库单Id',
  `warehousing_entry_code` varchar(32) DEFAULT NULL COMMENT '入库单编码',
  `warehousing_entry_name` varchar(200) DEFAULT NULL COMMENT '入库单名称',
  `asset_code` varchar(32) NOT NULL COMMENT '资产编码',
  `asset_name` varchar(200) DEFAULT NULL COMMENT '资产名称',
  `asset_category_code` varchar(36) DEFAULT NULL COMMENT '资产类型编码',
  `asset_category_name` varchar(100) DEFAULT NULL COMMENT '资产类型名称',
  `asset_model` varchar(100) DEFAULT NULL COMMENT '规格型号',
  `asset_unit` varchar(100) DEFAULT NULL COMMENT '计量单位',
  `manufacturer` varchar(100) DEFAULT NULL COMMENT '厂商',
  `brand` varchar(120) DEFAULT NULL COMMENT '品牌',
  `serial_number` varchar(100) DEFAULT NULL COMMENT '序列号',
  `asset_status_code` varchar(36) DEFAULT NULL COMMENT '资产状态编码',
  `asset_status_name` varchar(64) DEFAULT NULL COMMENT '资产状态名称',
  `asset_source_code` varchar(36) DEFAULT NULL COMMENT '资产来源编码',
  `asset_source_name` varchar(64) DEFAULT NULL COMMENT '资料来源名称',
  `purchase_date` datetime DEFAULT NULL COMMENT '购买日期',
  `purchase_price` decimal(24,6) DEFAULT NULL COMMENT '购买价格',
  `date_of_production` datetime DEFAULT NULL COMMENT '出场日期',
  `admin_company_id` varchar(255) DEFAULT NULL COMMENT '管理公司id',
  `admin_company_name` varchar(255) DEFAULT NULL COMMENT '管理公司名称',
  `admin_dept_id` varchar(32) DEFAULT NULL COMMENT '管理部门编码',
  `admin_dept_name` varchar(100) DEFAULT NULL COMMENT '管理部门名称',
  `admin_manager_id` varchar(32) DEFAULT NULL COMMENT '管理人员编码',
  `admin_manager_name` varchar(64) DEFAULT NULL COMMENT '管理人员名称',
  `use_company_id` varchar(255) DEFAULT NULL COMMENT '使用公司id',
  `use_company_name` varchar(255) DEFAULT NULL COMMENT '使用公司名称',
  `use_dept_id` varchar(32) DEFAULT NULL COMMENT '使用部门编码',
  `use_dept_name` varchar(100) DEFAULT NULL COMMENT '使用部门名称',
  `use_account_id` varchar(32) DEFAULT NULL COMMENT '使用人员编码',
  `use_account_name` varchar(64) DEFAULT NULL COMMENT '使用人员名称',
  `wms_store_code` varchar(36) DEFAULT NULL COMMENT '存放仓库编码',
  `wms_store_name` varchar(100) DEFAULT NULL COMMENT '存放仓库名称',
  `residual_value_rate` decimal(12,4) DEFAULT NULL COMMENT '残值率',
  `asset_icon` varchar(200) DEFAULT NULL COMMENT '资产图片',
  `asset_file` varchar(200) DEFAULT NULL COMMENT '资产附件',
  `sort` int(11) DEFAULT '0' COMMENT '显示顺序',
  `status` tinyint(4) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `store_address` varchar(300) DEFAULT NULL COMMENT '仓库地址',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COMMENT='资产信息';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `asset_inventory` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `inventory_code` varchar(32) DEFAULT NULL COMMENT '资产盘点编码',
  `inventory_name` varchar(200) DEFAULT NULL COMMENT '资产盘点名称',
  `asset_category_code` varchar(36) DEFAULT NULL COMMENT '资产类型编码',
  `asset_category_name` varchar(100) DEFAULT NULL COMMENT '资产类型名称',
  `admin_company_id` varchar(255) DEFAULT NULL COMMENT '管理公司id',
  `admin_company_name` varchar(255) DEFAULT NULL COMMENT '管理公司名称',
  `admin_dept_id` varchar(32) DEFAULT NULL COMMENT '管理部门编码',
  `admin_dept_name` varchar(100) DEFAULT NULL COMMENT '管理部门名称',
  `wms_store_code` varchar(36) DEFAULT NULL COMMENT '存放仓库编码',
  `wms_store_name` varchar(100) DEFAULT NULL COMMENT '存放仓库名称',
  `inventory_status_code` varchar(36) DEFAULT NULL COMMENT '资产盘点状态编码',
  `inventory_status_name` varchar(64) DEFAULT NULL COMMENT '资产盘点状态名称',
  `estimated_start_date` datetime DEFAULT NULL COMMENT '预计开始日期',
  `actual_start_date` datetime DEFAULT NULL COMMENT '实际开始日期',
  `estimated_end_date` datetime DEFAULT NULL COMMENT '预计完成日期',
  `actual_end_date` datetime DEFAULT NULL COMMENT '实际完成日期',
  `inventory_desc` varchar(500) DEFAULT NULL COMMENT '盘点说明',
  `sort` int(11) DEFAULT '0' COMMENT '显示顺序',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产盘点';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `asset_inventory_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `inventory_id` bigint(255) DEFAULT NULL COMMENT '资产盘点id',
  `inventory_code` varchar(32) DEFAULT NULL COMMENT '资产盘点编码',
  `inventory_name` varchar(200) DEFAULT NULL COMMENT '资产盘点名称',
  `asset_id` bigint(255) DEFAULT NULL COMMENT '资产id',
  `asset_code` varchar(32) NOT NULL COMMENT '资产编码',
  `asset_name` varchar(200) DEFAULT NULL COMMENT '资产名称',
  `asset_category_code` varchar(36) DEFAULT NULL COMMENT '资产类型编码',
  `asset_category_name` varchar(100) DEFAULT NULL COMMENT '资产类型名称',
  `asset_model` varchar(100) DEFAULT NULL COMMENT '规格型号',
  `asset_unit` varchar(100) DEFAULT NULL COMMENT '计量单位',
  `manufacturer` varchar(100) DEFAULT NULL COMMENT '厂商',
  `brand` varchar(120) DEFAULT NULL COMMENT '品牌',
  `serial_number` varchar(100) DEFAULT NULL COMMENT '序列号',
  `asset_status_code` varchar(36) DEFAULT NULL COMMENT '资产状态编码',
  `asset_status_name` varchar(64) DEFAULT NULL COMMENT '资产状态名称',
  `asset_source_code` varchar(36) DEFAULT NULL COMMENT '资产来源编码',
  `asset_source_name` varchar(64) DEFAULT NULL COMMENT '资料来源名称',
  `purchase_date` datetime DEFAULT NULL COMMENT '购买日期',
  `purchase_price` decimal(24,6) DEFAULT NULL COMMENT '购买价格',
  `date_of_production` datetime DEFAULT NULL COMMENT '出场日期',
  `admin_company_id` varchar(255) DEFAULT NULL COMMENT '管理公司id',
  `admin_company_name` varchar(255) DEFAULT NULL COMMENT '管理公司名称',
  `admin_dept_id` varchar(32) DEFAULT NULL COMMENT '管理部门编码',
  `admin_dept_name` varchar(100) DEFAULT NULL COMMENT '管理部门名称',
  `admin_manager_id` varchar(32) DEFAULT NULL COMMENT '管理人员编码',
  `admin_manager_name` varchar(64) DEFAULT NULL COMMENT '管理人员名称',
  `use_company_id` varchar(255) DEFAULT NULL COMMENT '使用公司id',
  `use_company_name` varchar(255) DEFAULT NULL COMMENT '使用公司名称',
  `use_dept_id` varchar(32) DEFAULT NULL COMMENT '使用部门编码',
  `use_dept_name` varchar(100) DEFAULT NULL COMMENT '使用部门名称',
  `use_account_id` varchar(32) DEFAULT NULL COMMENT '使用人员编码',
  `use_account_name` varchar(64) DEFAULT NULL COMMENT '使用人员名称',
  `wms_store_code` varchar(36) DEFAULT NULL COMMENT '存放仓库编码',
  `wms_store_name` varchar(100) DEFAULT NULL COMMENT '存放仓库名称',
  `residual_value_rate` decimal(12,4) DEFAULT NULL COMMENT '残值率',
  `asset_icon` varchar(200) DEFAULT NULL COMMENT '资产图片',
  `asset_file` varchar(200) DEFAULT NULL COMMENT '资产附件',
  `inventory_status_code` varchar(32) DEFAULT NULL COMMENT '盘点状态编码',
  `inventory_status_name` varchar(32) DEFAULT NULL COMMENT '盘点状态名称',
  `inventory_user_code` varchar(32) DEFAULT NULL COMMENT '盘点人员编码',
  `inventory_user_name` varchar(64) DEFAULT NULL COMMENT '盘点人员名称',
  `inventory_date` datetime DEFAULT NULL COMMENT '盘点日期',
  `inventory_way` varchar(32) DEFAULT NULL COMMENT '盘点方式',
  `inventory_result_code` varchar(16) DEFAULT NULL COMMENT '盘点结果编码',
  `inventory_result_name` varchar(32) DEFAULT NULL COMMENT '盘点结果名称',
  `sort` int(11) DEFAULT '0' COMMENT '显示顺序',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产盘点明细表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `asset_inventory_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `inventory_id` bigint(255) DEFAULT NULL COMMENT '资产盘点id',
  `inventory_code` varchar(32) DEFAULT NULL COMMENT '资产盘点编码',
  `inventory_name` varchar(200) DEFAULT NULL COMMENT '资产盘点名称',
  `inventory_user_code` varchar(32) DEFAULT NULL COMMENT '盘点人员编码',
  `inventory_user_name` varchar(64) DEFAULT NULL COMMENT '盘点人员名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产盘点人员表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `asset_life_time` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `asset_id` bigint(32) DEFAULT NULL COMMENT '采购订单id',
  `asset_code` varchar(32) NOT NULL COMMENT '资产编码',
  `asset_name` varchar(200) DEFAULT NULL COMMENT '资产名称',
  `asset_milestone` text COMMENT '资产里程说明',
  `sort` int(11) DEFAULT '0' COMMENT '显示顺序',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `store_address` varchar(300) DEFAULT NULL COMMENT '仓库地址',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产的一生表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `bpm_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类编号',
  `name` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '分类名',
  `code` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '分类标志',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '分类描述',
  `status` tinyint(4) DEFAULT NULL COMMENT '分类状态',
  `sort` int(11) DEFAULT NULL COMMENT '分类排序',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='BPM 流程分类';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `bpm_form` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表单名',
  `status` tinyint(4) NOT NULL COMMENT '开启状态',
  `conf` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表单的配置',
  `fields` varchar(5000) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表单项的数组',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `bill_code_prefix` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '单据类型编码（作为单据编号前缀）',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='BPM 表单定义表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `bpm_oa_leave` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '请假表单主键',
  `user_id` bigint(20) NOT NULL COMMENT '申请人的用户编号',
  `type` tinyint(4) NOT NULL COMMENT '请假类型',
  `reason` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请假原因',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `day` tinyint(4) NOT NULL COMMENT '请假天数',
  `status` tinyint(4) NOT NULL COMMENT '审批结果',
  `process_instance_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程实例的编号',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OA 请假申请表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `bpm_process_definition_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `process_definition_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流程定义的编号',
  `model_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流程模型的编号',
  `model_type` tinyint(4) NOT NULL DEFAULT '10' COMMENT '流程模型的类型',
  `category` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流程分类的编码',
  `icon` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `form_type` tinyint(4) NOT NULL COMMENT '表单类型',
  `form_id` bigint(20) DEFAULT NULL COMMENT '表单编号',
  `form_conf` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单的配置',
  `form_fields` varchar(5000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单项的数组',
  `form_custom_create_path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '自定义表单的提交路径',
  `form_custom_view_path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '自定义表单的查看路径',
  `simple_model` text COLLATE utf8mb4_unicode_ci COMMENT 'SIMPLE 设计器模型数据 JSON 格式',
  `sort` bigint(20) DEFAULT '0' COMMENT '排序值',
  `visible` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否可见',
  `start_user_ids` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '可发起用户编号数组',
  `start_dept_ids` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '可发起部门编号数组',
  `manager_user_ids` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '可管理用户编号数组',
  `allow_cancel_running_process` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否允许撤销审批中的申请',
  `process_id_rule` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程 ID 规则',
  `auto_approval_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '自动去重类型',
  `title_setting` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题设置',
  `summary_setting` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摘要设置',
  `process_before_trigger_setting` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程前置通知设置',
  `process_after_trigger_setting` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程后置通知设置',
  `task_before_trigger_setting` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务前置通知设置',
  `task_after_trigger_setting` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务后置通知设置',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  `allow_withdraw_task` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否允许审批人撤回任务',
  `print_template_setting` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '自定义打印模板设置',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=433 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='BPM 流程定义的信息表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `bpm_process_expression` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '表达式名字',
  `status` tinyint(4) NOT NULL COMMENT '表达式状态',
  `expression` varchar(1024) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表达式',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=119 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='BPM 流程表达式表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `bpm_process_instance_copy` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户编号，被抄送人',
  `start_user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '发起流程的用户编号',
  `process_instance_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '流程实例的编号',
  `process_instance_name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '流程实例的名字',
  `process_definition_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流程定义的编号',
  `category` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流程定义的分类',
  `activity_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '流程活动的编号',
  `activity_name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流程活动的名字',
  `task_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '流程任务的编号',
  `reason` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '抄送意见',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='BPM 流程实例抄送表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `bpm_process_listener` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '监听器名字',
  `type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '监听器类型',
  `status` tinyint(4) NOT NULL COMMENT '监听器状态',
  `event` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '监听事件',
  `value_type` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '监听器值类型',
  `value` varchar(1024) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '监听器值',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='BPM 流程监听器表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `bpm_user_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '组名',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '描述',
  `user_ids` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '成员编号数组',
  `status` tinyint(4) NOT NULL COMMENT '状态（0正常 1停用）',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='BPM 用户组表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `common_attachment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '附件ID',
  `business_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '业务类型（如：seal_apply_bill、car_apply_bill等）',
  `business_id` bigint(20) NOT NULL COMMENT '业务单据ID',
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名称',
  `file_path` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件路径',
  `file_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件访问URL',
  `file_size` bigint(20) NOT NULL DEFAULT '0' COMMENT '文件大小（字节）',
  `file_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件类型（MIME类型）',
  `file_extension` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件扩展名',
  `upload_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序顺序',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_business` (`business_type`,`business_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通用附件信息表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `crm_business` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商机名称',
  `customer_id` bigint(20) NOT NULL COMMENT '客户编号',
  `follow_up_status` bit(1) DEFAULT b'0' COMMENT '跟进状态',
  `contact_last_time` datetime DEFAULT NULL COMMENT '最后跟进时间',
  `contact_next_time` datetime DEFAULT NULL COMMENT '下次联系时间',
  `owner_user_id` bigint(20) DEFAULT NULL COMMENT '负责人的用户编号',
  `status_type_id` bigint(20) DEFAULT NULL COMMENT '商机状态类型编号',
  `status_id` bigint(20) DEFAULT NULL COMMENT '商机状态编号',
  `end_status` tinyint(4) DEFAULT NULL COMMENT '结束状态：1-赢单 2-输单3-无效',
  `deal_time` datetime DEFAULT NULL COMMENT '预计成交日期',
  `total_product_price` decimal(24,6) DEFAULT NULL COMMENT '产品总金额，单位：元',
  `discount_percent` decimal(24,6) DEFAULT NULL COMMENT '整单折扣，百分比',
  `total_price` decimal(24,6) DEFAULT NULL COMMENT '商机总金额，单位：元',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `end_remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '结束时的备注',
  `deleted` bit(1) DEFAULT b'0' COMMENT '逻辑删除',
  `tenant_id` bigint(20) DEFAULT '0' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=372 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM 商机表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `crm_business_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `business_id` bigint(20) NOT NULL COMMENT '商机编号',
  `product_id` bigint(20) NOT NULL COMMENT '产品编号',
  `product_price` decimal(24,6) NOT NULL COMMENT '产品单价',
  `business_price` decimal(24,6) NOT NULL COMMENT '商机价格',
  `count` decimal(24,6) NOT NULL COMMENT '数量',
  `total_price` decimal(24,6) NOT NULL COMMENT '总计价格',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM 商机产品关联表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `crm_business_status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type_id` bigint(20) NOT NULL COMMENT '状态类型编号',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态类型名',
  `percent` decimal(24,6) NOT NULL COMMENT '赢单率',
  `sort` int(11) NOT NULL DEFAULT '1' COMMENT '排序',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM 商机状态表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `crm_business_status_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态组名',
  `dept_ids` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '使用的部门编号',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM 商机状态组表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `crm_clue` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号，主键自增',
  `name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '线索名称',
  `follow_up_status` bit(1) DEFAULT b'0' COMMENT '跟进状态',
  `contact_last_time` datetime DEFAULT NULL COMMENT '最后跟进时间',
  `contact_last_content` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后跟进内容',
  `contact_next_time` datetime DEFAULT NULL COMMENT '下次联系时间',
  `owner_user_id` bigint(20) NOT NULL COMMENT '负责人的用户编号',
  `transform_status` bit(1) DEFAULT b'0' COMMENT '转化状态',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '客户编号',
  `mobile` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `telephone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电话',
  `qq` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'QQ',
  `wechat` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信',
  `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `area_id` bigint(20) DEFAULT NULL COMMENT '地区编号',
  `detail_address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '详细地址',
  `industry_id` int(11) DEFAULT NULL COMMENT '所属行业',
  `level` int(11) DEFAULT NULL COMMENT '客户等级',
  `source` int(11) DEFAULT NULL COMMENT '客户来源',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=312 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM 线索表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `crm_contact` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系人名称',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '客户编号',
  `contact_last_time` datetime DEFAULT NULL COMMENT '最后跟进时间',
  `contact_last_content` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后跟进内容',
  `contact_next_time` datetime DEFAULT NULL COMMENT '下次联系时间',
  `owner_user_id` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '负责人用户编号',
  `mobile` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `telephone` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电话',
  `email` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电子邮箱',
  `qq` int(11) DEFAULT NULL,
  `wechat` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `area_id` bigint(20) DEFAULT NULL COMMENT '地区',
  `detail_address` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址',
  `sex` int(11) DEFAULT NULL COMMENT '性别',
  `master` bit(1) DEFAULT NULL COMMENT '是否关键决策人',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '直系上属',
  `post` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职务',
  `remark` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  `tenant_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=354 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM 联系人';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `crm_contact_business` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `contact_id` int(11) DEFAULT NULL COMMENT '联系人id',
  `business_id` int(11) DEFAULT NULL COMMENT '商机id',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM 联系人商机关联表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `crm_contract` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号，主键自增',
  `name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '合同名称',
  `no` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '合同编号',
  `customer_id` bigint(20) NOT NULL COMMENT '客户编号',
  `business_id` bigint(20) DEFAULT NULL COMMENT '商机编号',
  `contact_last_time` datetime DEFAULT NULL COMMENT '最后跟进时间',
  `owner_user_id` bigint(20) DEFAULT NULL COMMENT '负责人的用户编号',
  `process_instance_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作流编号',
  `audit_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '审批状态',
  `order_date` datetime DEFAULT NULL COMMENT '下单日期',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `total_product_price` decimal(24,6) DEFAULT NULL COMMENT '产品总金额',
  `discount_percent` decimal(24,6) DEFAULT NULL COMMENT '整单折扣',
  `total_price` decimal(10,2) DEFAULT NULL COMMENT '合同总金额',
  `sign_contact_id` bigint(20) DEFAULT NULL COMMENT '联系人编号',
  `sign_user_id` bigint(20) DEFAULT NULL COMMENT '公司签约人',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=397 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM 合同表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `crm_contract_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `notify_enabled` tinyint(1) DEFAULT NULL COMMENT '是否开启提前提醒',
  `notify_days` int(11) DEFAULT NULL COMMENT '提前提醒天数',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM 合同配置表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `crm_contract_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `contract_id` bigint(20) NOT NULL COMMENT '合同编号',
  `product_id` bigint(20) NOT NULL COMMENT '产品编号',
  `product_price` decimal(24,6) NOT NULL COMMENT '产品单价',
  `contract_price` decimal(24,6) NOT NULL COMMENT '合同价格',
  `count` decimal(24,6) NOT NULL COMMENT '数量',
  `total_price` decimal(24,6) NOT NULL COMMENT '总计价格',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3962 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM 合同产品关联表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `crm_customer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号，主键自增',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '客户名称',
  `follow_up_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '跟进状态',
  `contact_last_time` datetime DEFAULT NULL COMMENT '最后跟进时间',
  `contact_last_content` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后跟进内容',
  `contact_next_time` datetime DEFAULT NULL COMMENT '下次联系时间',
  `owner_user_id` bigint(20) DEFAULT NULL COMMENT '负责人的用户编号',
  `owner_time` datetime NOT NULL COMMENT '成为负责人的时间',
  `lock_status` bit(1) NOT NULL DEFAULT b'0' COMMENT '锁定状态',
  `deal_status` bit(1) NOT NULL DEFAULT b'0' COMMENT '成交状态',
  `mobile` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机',
  `telephone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电话',
  `qq` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'QQ',
  `wechat` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信',
  `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `area_id` bigint(20) DEFAULT NULL COMMENT '地区编号',
  `detail_address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '详细地址',
  `industry_id` int(11) DEFAULT NULL COMMENT '所属行业',
  `level` int(11) DEFAULT NULL COMMENT '客户等级',
  `source` int(11) DEFAULT NULL COMMENT '客户来源',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `owner_user_id` (`owner_user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=332 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM 客户表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `crm_customer_limit_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `type` int(11) NOT NULL COMMENT '规则类型 1: 拥有客户数限制，2:锁定客户数限制',
  `user_ids` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '规则适用人群',
  `dept_ids` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '规则适用部门',
  `max_count` int(11) NOT NULL COMMENT '数量上限',
  `deal_count_enabled` tinyint(4) DEFAULT NULL COMMENT '成交客户是否占有拥有客户数(当 type = 1 时)',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM 客户限制配置表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `crm_customer_pool_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `enabled` tinyint(1) NOT NULL COMMENT '是否启用客户公海',
  `contact_expire_days` int(11) DEFAULT NULL COMMENT '未跟进放入公海天数',
  `deal_expire_days` int(11) DEFAULT NULL COMMENT '未成交放入公海天数',
  `notify_enabled` tinyint(1) DEFAULT NULL COMMENT '是否开启提前提醒',
  `notify_days` int(11) DEFAULT NULL COMMENT '提前提醒天数',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM 客户公海配置表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `crm_follow_up_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `biz_type` int(11) DEFAULT NULL COMMENT '数据类型',
  `biz_id` bigint(20) DEFAULT NULL COMMENT '数据编号',
  `type` int(11) DEFAULT NULL COMMENT '跟进类型',
  `content` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '跟进内容',
  `next_time` datetime DEFAULT NULL COMMENT '下次联系时间',
  `pic_urls` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片',
  `file_urls` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '附件',
  `business_ids` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '关联的商机编号数组',
  `contact_ids` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '关联的联系人编号数组',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=659 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM 跟进记录';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `crm_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `biz_type` tinyint(4) NOT NULL DEFAULT '100' COMMENT '数据类型',
  `biz_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '数据编号',
  `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户编号',
  `level` int(11) NOT NULL DEFAULT '0' COMMENT '会员等级',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1028 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM 数据权限表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `crm_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '产品编号',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '产品名称',
  `no` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '产品编码',
  `unit` tinyint(4) DEFAULT NULL COMMENT '单位',
  `price` decimal(24,6) DEFAULT '0.000000' COMMENT '价格，单位：元',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态',
  `category_id` bigint(20) NOT NULL COMMENT '产品分类编号',
  `description` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产品描述',
  `owner_user_id` bigint(20) NOT NULL COMMENT '负责人的用户编号',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=210 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM 产品表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `crm_product_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类编号',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `parent_id` bigint(20) NOT NULL COMMENT '父级编号',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM 产品分类表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `crm_receivable` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `no` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '回款编号',
  `plan_id` bigint(20) DEFAULT NULL COMMENT '回款计划ID',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `contract_id` bigint(20) NOT NULL COMMENT '合同ID',
  `owner_user_id` bigint(20) DEFAULT NULL COMMENT '负责人的用户编号',
  `audit_status` tinyint(4) NOT NULL COMMENT '审批状态',
  `process_instance_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作流编号',
  `return_time` datetime DEFAULT NULL COMMENT '回款日期',
  `return_type` int(11) DEFAULT NULL COMMENT '回款方式',
  `price` decimal(24,6) NOT NULL COMMENT '回款金额',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=412 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM 回款表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `crm_receivable_plan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `period` bigint(20) NOT NULL COMMENT '期数',
  `customer_id` bigint(20) NOT NULL COMMENT '客户编号',
  `contract_id` bigint(20) NOT NULL COMMENT '合同编号',
  `owner_user_id` bigint(20) DEFAULT NULL COMMENT '负责人编号',
  `receivable_id` bigint(20) DEFAULT NULL COMMENT '回款编号',
  `return_time` datetime DEFAULT NULL COMMENT '计划回款日期',
  `return_type` tinyint(4) DEFAULT NULL COMMENT '计划还款方式',
  `price` decimal(24,6) NOT NULL COMMENT '计划回款金额',
  `remind_days` bigint(20) DEFAULT NULL COMMENT '提前几天提醒',
  `remind_time` datetime DEFAULT NULL COMMENT '提醒日期',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM 回款计划表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '结算账户编号',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账户名称',
  `no` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账户编码',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `status` tinyint(4) NOT NULL COMMENT '开启状态',
  `sort` int(11) NOT NULL COMMENT '排序',
  `default_status` bit(1) DEFAULT b'0' COMMENT '是否默认',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 结算账户';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_customer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '客户编号',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户名称',
  `contact` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系人',
  `mobile` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号码',
  `telephone` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电子邮箱',
  `fax` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '传真',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `status` tinyint(4) NOT NULL COMMENT '开启状态',
  `sort` int(11) NOT NULL COMMENT '排序',
  `tax_no` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '纳税人识别号',
  `tax_percent` decimal(24,6) DEFAULT NULL COMMENT '税率',
  `bank_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开户行',
  `bank_account` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开户账号',
  `bank_address` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开户地址',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 客户表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_finance_payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '付款单号',
  `status` tinyint(4) NOT NULL COMMENT '状态',
  `payment_time` datetime NOT NULL COMMENT '付款时间',
  `finance_user_id` bigint(20) DEFAULT NULL COMMENT '财务人员编号',
  `supplier_id` bigint(20) NOT NULL COMMENT '供应商编号',
  `account_id` bigint(20) NOT NULL COMMENT '付款账户编号',
  `total_price` decimal(24,6) NOT NULL COMMENT '合计价格，单位：元',
  `discount_price` decimal(24,6) NOT NULL COMMENT '优惠金额，单位：元',
  `payment_price` decimal(24,6) NOT NULL COMMENT '实付金额，单位：分',
  `remark` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 付款单表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_finance_payment_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `payment_id` bigint(20) NOT NULL COMMENT '付款单编号',
  `biz_type` tinyint(4) NOT NULL COMMENT '业务类型',
  `biz_id` bigint(20) NOT NULL COMMENT '业务编号',
  `biz_no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '业务单号',
  `total_price` decimal(24,6) NOT NULL COMMENT '应付欠款，单位：分',
  `paid_price` decimal(24,6) NOT NULL COMMENT '已付欠款，单位：分',
  `payment_price` decimal(24,6) NOT NULL COMMENT '本次付款，单位：分',
  `remark` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 付款项表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_finance_receipt` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收款单号',
  `status` tinyint(4) NOT NULL COMMENT '状态',
  `receipt_time` datetime NOT NULL COMMENT '收款时间',
  `finance_user_id` bigint(20) DEFAULT NULL COMMENT '财务人员编号',
  `customer_id` bigint(20) NOT NULL COMMENT '客户编号',
  `account_id` bigint(20) NOT NULL COMMENT '收款账户编号',
  `total_price` decimal(24,6) NOT NULL COMMENT '合计价格，单位：元',
  `discount_price` decimal(24,6) NOT NULL COMMENT '优惠金额，单位：元',
  `receipt_price` decimal(24,6) NOT NULL COMMENT '实收金额，单位：分',
  `remark` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 收款单表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_finance_receipt_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `receipt_id` bigint(20) NOT NULL COMMENT '收款单编号',
  `biz_type` tinyint(4) NOT NULL COMMENT '业务类型',
  `biz_id` bigint(20) NOT NULL COMMENT '业务编号',
  `biz_no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '业务单号',
  `total_price` decimal(24,6) NOT NULL COMMENT '应收金额，单位：分',
  `receipted_price` decimal(24,6) NOT NULL COMMENT '已收金额，单位：分',
  `receipt_price` decimal(24,6) NOT NULL COMMENT '本次收款，单位：分',
  `remark` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 收款项表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '产品编号',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '产品名称',
  `bar_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '产品条码',
  `category_id` bigint(20) NOT NULL COMMENT '产品分类编号',
  `unit_id` int(11) NOT NULL COMMENT '单位编号',
  `status` tinyint(4) NOT NULL COMMENT '产品状态',
  `standard` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产品规格',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产品备注',
  `expiry_day` int(11) DEFAULT NULL COMMENT '保质期天数',
  `weight` decimal(24,6) DEFAULT NULL COMMENT '基础重量（kg）',
  `purchase_price` decimal(24,6) DEFAULT NULL COMMENT '采购价格，单位：元',
  `sale_price` decimal(24,6) DEFAULT NULL COMMENT '销售价格，单位：元',
  `min_price` decimal(24,6) DEFAULT NULL COMMENT '最低价格，单位：元',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 产品表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_product_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类编号',
  `parent_id` bigint(20) NOT NULL COMMENT '父分类编号',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类编码',
  `sort` int(11) DEFAULT '0' COMMENT '分类排序',
  `status` tinyint(4) NOT NULL COMMENT '开启状态',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 产品分类';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_product_unit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '单位编号',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单位名字',
  `status` tinyint(4) NOT NULL COMMENT '单位状态',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 产品单位表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_purchase_in` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '采购入库编号',
  `status` tinyint(4) NOT NULL COMMENT '采购状态',
  `supplier_id` bigint(20) NOT NULL COMMENT '供应商编号',
  `account_id` bigint(20) NOT NULL COMMENT '结算账户编号',
  `in_time` datetime NOT NULL COMMENT '入库时间',
  `order_id` bigint(20) NOT NULL COMMENT '采购订单编号',
  `order_no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '采购订单号',
  `total_count` decimal(24,6) NOT NULL COMMENT '合计数量',
  `total_price` decimal(24,6) NOT NULL COMMENT '合计价格，单位：元',
  `payment_price` decimal(24,6) NOT NULL DEFAULT '0.000000' COMMENT '已付款金额，单位：元',
  `total_product_price` decimal(24,6) NOT NULL COMMENT '合计产品价格，单位：元',
  `total_tax_price` decimal(24,6) NOT NULL COMMENT '合计税额，单位：元',
  `discount_percent` decimal(24,6) NOT NULL COMMENT '优惠率，百分比',
  `discount_price` decimal(24,6) NOT NULL COMMENT '优惠金额，单位：元',
  `other_price` decimal(24,6) NOT NULL DEFAULT '0.000000' COMMENT '其它金额，单位：元',
  `file_url` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '附件地址',
  `remark` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `no` (`no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 采购入库表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_purchase_in_items` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `in_id` bigint(20) NOT NULL COMMENT '采购入库编号',
  `order_item_id` bigint(20) NOT NULL COMMENT '采购订单项编号',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库编号',
  `product_id` bigint(20) NOT NULL COMMENT '产品编号',
  `product_unit_id` bigint(20) NOT NULL COMMENT '产品单位单位',
  `product_price` decimal(24,6) NOT NULL COMMENT '产品单价',
  `count` decimal(24,6) NOT NULL COMMENT '数量',
  `total_price` decimal(24,6) NOT NULL COMMENT '总价',
  `tax_percent` decimal(24,6) DEFAULT NULL COMMENT '税率，百分比',
  `tax_price` decimal(24,6) DEFAULT NULL COMMENT '税额，单位：元',
  `remark` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 销售入库项表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_purchase_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '采购单编号',
  `status` tinyint(4) NOT NULL COMMENT '采购状态',
  `supplier_id` bigint(20) NOT NULL COMMENT '供应商编号',
  `account_id` bigint(20) DEFAULT NULL COMMENT '结算账户编号',
  `order_time` datetime NOT NULL COMMENT '采购时间',
  `total_count` decimal(24,6) NOT NULL COMMENT '合计数量',
  `total_price` decimal(24,6) NOT NULL COMMENT '合计价格，单位：元',
  `total_product_price` decimal(24,6) NOT NULL COMMENT '合计产品价格，单位：元',
  `total_tax_price` decimal(24,6) NOT NULL COMMENT '合计税额，单位：元',
  `discount_percent` decimal(24,6) NOT NULL COMMENT '优惠率，百分比',
  `discount_price` decimal(24,6) NOT NULL COMMENT '优惠金额，单位：元',
  `deposit_price` decimal(24,6) NOT NULL DEFAULT '0.000000' COMMENT '定金金额，单位：元',
  `file_url` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '附件地址',
  `remark` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `in_count` decimal(24,6) NOT NULL DEFAULT '0.000000' COMMENT '采购入库数量',
  `return_count` decimal(24,6) NOT NULL DEFAULT '0.000000' COMMENT '采购退货数量',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `no` (`no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 采购订单表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_purchase_order_items` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `order_id` bigint(20) NOT NULL COMMENT '采购订单编号',
  `product_id` bigint(20) NOT NULL COMMENT '产品编号',
  `product_unit_id` bigint(20) NOT NULL COMMENT '产品单位单位',
  `product_price` decimal(24,6) NOT NULL COMMENT '产品单价',
  `count` decimal(24,6) NOT NULL COMMENT '数量',
  `total_price` decimal(24,6) NOT NULL COMMENT '总价',
  `tax_percent` decimal(24,6) DEFAULT NULL COMMENT '税率，百分比',
  `tax_price` decimal(24,6) DEFAULT NULL COMMENT '税额，单位：元',
  `remark` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `in_count` decimal(24,6) NOT NULL DEFAULT '0.000000' COMMENT '采购入库数量',
  `return_count` decimal(24,6) NOT NULL DEFAULT '0.000000' COMMENT '采购退货数量',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 采购订单项表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_purchase_return` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '采购退货编号',
  `status` tinyint(4) NOT NULL COMMENT '退货状态',
  `supplier_id` bigint(20) NOT NULL COMMENT '供应商编号',
  `account_id` bigint(20) NOT NULL COMMENT '结算账户编号',
  `return_time` datetime NOT NULL COMMENT '退货时间',
  `order_id` bigint(20) NOT NULL COMMENT '采购订单编号',
  `order_no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '采购订单号',
  `total_count` decimal(24,6) NOT NULL COMMENT '合计数量',
  `total_price` decimal(24,6) NOT NULL COMMENT '合计价格，单位：元',
  `refund_price` decimal(24,6) NOT NULL DEFAULT '0.000000' COMMENT '已退款金额，单位：元',
  `total_product_price` decimal(24,6) NOT NULL COMMENT '合计产品价格，单位：元',
  `total_tax_price` decimal(24,6) NOT NULL COMMENT '合计税额，单位：元',
  `discount_percent` decimal(24,6) NOT NULL COMMENT '优惠率，百分比',
  `discount_price` decimal(24,6) NOT NULL COMMENT '优惠金额，单位：元',
  `other_price` decimal(24,6) NOT NULL DEFAULT '0.000000' COMMENT '其它金额，单位：元',
  `file_url` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '附件地址',
  `remark` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `no` (`no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 采购退货表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_purchase_return_items` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `return_id` bigint(20) NOT NULL COMMENT '采购退货编号',
  `order_item_id` bigint(20) NOT NULL COMMENT '采购订单项编号',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库编号',
  `product_id` bigint(20) NOT NULL COMMENT '产品编号',
  `product_unit_id` bigint(20) NOT NULL COMMENT '产品单位单位',
  `product_price` decimal(24,6) NOT NULL COMMENT '产品单价',
  `count` decimal(24,6) NOT NULL COMMENT '数量',
  `total_price` decimal(24,6) NOT NULL COMMENT '总价',
  `tax_percent` decimal(24,6) DEFAULT NULL COMMENT '税率，百分比',
  `tax_price` decimal(24,6) DEFAULT NULL COMMENT '税额，单位：元',
  `remark` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 采购退货项表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_sale_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '销售单编号',
  `status` tinyint(4) NOT NULL COMMENT '销售状态',
  `customer_id` bigint(20) NOT NULL COMMENT '客户编号',
  `account_id` bigint(20) DEFAULT NULL COMMENT '结算账户编号',
  `sale_user_id` bigint(20) DEFAULT NULL COMMENT '销售用户编号',
  `order_time` datetime NOT NULL COMMENT '下单时间',
  `total_count` decimal(24,6) NOT NULL COMMENT '合计数量',
  `total_price` decimal(24,6) NOT NULL COMMENT '合计价格，单位：元',
  `total_product_price` decimal(24,6) NOT NULL COMMENT '合计产品价格，单位：元',
  `total_tax_price` decimal(24,6) NOT NULL COMMENT '合计税额，单位：元',
  `discount_percent` decimal(24,6) NOT NULL COMMENT '优惠率，百分比',
  `discount_price` decimal(24,6) NOT NULL COMMENT '优惠金额，单位：元',
  `deposit_price` decimal(24,6) NOT NULL DEFAULT '0.000000' COMMENT '定金金额，单位：元',
  `file_url` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '附件地址',
  `remark` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `out_count` decimal(24,6) NOT NULL DEFAULT '0.000000' COMMENT '销售出库数量',
  `return_count` decimal(24,6) NOT NULL DEFAULT '0.000000' COMMENT '销售退货数量',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `no` (`no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 销售订单表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_sale_order_items` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `order_id` bigint(20) NOT NULL COMMENT '销售订单编号',
  `product_id` bigint(20) NOT NULL COMMENT '产品编号',
  `product_unit_id` bigint(20) NOT NULL COMMENT '产品单位单位',
  `product_price` decimal(24,6) DEFAULT NULL COMMENT '产品单价',
  `count` decimal(24,6) NOT NULL COMMENT '数量',
  `total_price` decimal(24,6) DEFAULT NULL COMMENT '总价',
  `tax_percent` decimal(24,6) DEFAULT NULL COMMENT '税率，百分比',
  `tax_price` decimal(24,6) DEFAULT NULL COMMENT '税额，单位：元',
  `remark` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `out_count` decimal(24,6) NOT NULL DEFAULT '0.000000' COMMENT '销售出库数量',
  `return_count` decimal(24,6) NOT NULL DEFAULT '0.000000' COMMENT '销售退货数量',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 销售订单项表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_sale_out` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '销售出库编号',
  `status` tinyint(4) NOT NULL COMMENT '出库状态',
  `customer_id` bigint(20) NOT NULL COMMENT '客户编号',
  `account_id` bigint(20) NOT NULL COMMENT '结算账户编号',
  `sale_user_id` bigint(20) DEFAULT NULL COMMENT '销售用户编号',
  `out_time` datetime NOT NULL COMMENT '出库时间',
  `order_id` bigint(20) NOT NULL COMMENT '销售订单编号',
  `order_no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '销售订单号',
  `total_count` decimal(24,6) NOT NULL COMMENT '合计数量',
  `total_price` decimal(24,6) NOT NULL COMMENT '合计价格，单位：元',
  `receipt_price` decimal(24,6) NOT NULL DEFAULT '0.000000' COMMENT '已收款金额，单位：元',
  `total_product_price` decimal(24,6) NOT NULL COMMENT '合计产品价格，单位：元',
  `total_tax_price` decimal(24,6) NOT NULL COMMENT '合计税额，单位：元',
  `discount_percent` decimal(24,6) NOT NULL COMMENT '优惠率，百分比',
  `discount_price` decimal(24,6) NOT NULL COMMENT '优惠金额，单位：元',
  `other_price` decimal(24,6) NOT NULL DEFAULT '0.000000' COMMENT '其它金额，单位：元',
  `file_url` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '附件地址',
  `remark` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `no` (`no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 销售出库表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_sale_out_items` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `out_id` bigint(20) NOT NULL COMMENT '销售出库编号',
  `order_item_id` bigint(20) NOT NULL COMMENT '销售订单项编号',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库编号',
  `product_id` bigint(20) NOT NULL COMMENT '产品编号',
  `product_unit_id` bigint(20) NOT NULL COMMENT '产品单位单位',
  `product_price` decimal(24,6) NOT NULL COMMENT '产品单价',
  `count` decimal(24,6) NOT NULL COMMENT '数量',
  `total_price` decimal(24,6) NOT NULL COMMENT '总价',
  `tax_percent` decimal(24,6) DEFAULT NULL COMMENT '税率，百分比',
  `tax_price` decimal(24,6) DEFAULT NULL COMMENT '税额，单位：元',
  `remark` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 销售出库项表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_sale_return` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '销售退货编号',
  `status` tinyint(4) NOT NULL COMMENT '退货状态',
  `customer_id` bigint(20) NOT NULL COMMENT '客户编号',
  `account_id` bigint(20) NOT NULL COMMENT '结算账户编号',
  `sale_user_id` bigint(20) DEFAULT NULL COMMENT '销售用户编号',
  `return_time` datetime NOT NULL COMMENT '退货时间',
  `order_id` bigint(20) NOT NULL COMMENT '销售订单编号',
  `order_no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '销售订单号',
  `total_count` decimal(24,6) NOT NULL COMMENT '合计数量',
  `total_price` decimal(24,6) NOT NULL COMMENT '合计价格，单位：元',
  `refund_price` decimal(24,6) NOT NULL DEFAULT '0.000000' COMMENT '已退款金额，单位：元',
  `total_product_price` decimal(24,6) NOT NULL COMMENT '合计产品价格，单位：元',
  `total_tax_price` decimal(24,6) NOT NULL COMMENT '合计税额，单位：元',
  `discount_percent` decimal(24,6) NOT NULL COMMENT '优惠率，百分比',
  `discount_price` decimal(24,6) NOT NULL COMMENT '优惠金额，单位：元',
  `other_price` decimal(24,6) NOT NULL DEFAULT '0.000000' COMMENT '其它金额，单位：元',
  `file_url` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '附件地址',
  `remark` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `no` (`no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 销售退货表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_sale_return_items` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `return_id` bigint(20) NOT NULL COMMENT '销售退货编号',
  `order_item_id` bigint(20) NOT NULL COMMENT '销售订单项编号',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库编号',
  `product_id` bigint(20) NOT NULL COMMENT '产品编号',
  `product_unit_id` bigint(20) NOT NULL COMMENT '产品单位单位',
  `product_price` decimal(24,6) NOT NULL COMMENT '产品单价',
  `count` decimal(24,6) NOT NULL COMMENT '数量',
  `total_price` decimal(24,6) NOT NULL COMMENT '总价',
  `tax_percent` decimal(24,6) DEFAULT NULL COMMENT '税率，百分比',
  `tax_price` decimal(24,6) DEFAULT NULL COMMENT '税额，单位：元',
  `remark` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 销售退货项表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_stock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `product_id` bigint(20) NOT NULL COMMENT '产品编号',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库编号',
  `count` decimal(24,6) NOT NULL COMMENT '库存数量',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 产品库存表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_stock_check` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '盘点编号',
  `no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '盘点单号',
  `check_time` datetime NOT NULL COMMENT '盘点时间',
  `total_count` decimal(24,6) NOT NULL COMMENT '合计数量',
  `total_price` decimal(24,6) NOT NULL COMMENT '合计金额，单位：元',
  `status` tinyint(4) NOT NULL COMMENT '状态',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `file_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '附件 URL',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 库存盘点单表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_stock_check_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '盘点项编号',
  `check_id` bigint(20) NOT NULL COMMENT '盘点编号',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库编号',
  `product_id` bigint(20) NOT NULL COMMENT '产品编号',
  `product_unit_id` bigint(20) NOT NULL COMMENT '产品单位编号',
  `product_price` decimal(24,6) DEFAULT NULL COMMENT '产品单价',
  `stock_count` decimal(24,6) NOT NULL COMMENT '账面数量（当前库存）',
  `actual_count` decimal(24,6) NOT NULL COMMENT '实际数量（实际库存）',
  `count` decimal(24,6) NOT NULL COMMENT '盈亏数量',
  `total_price` decimal(24,6) DEFAULT NULL COMMENT '合计金额，单位：元',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 库存盘点项表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_stock_in` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '入库编号',
  `no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '入库单号',
  `supplier_id` bigint(20) DEFAULT NULL COMMENT '供应商编号',
  `in_time` datetime NOT NULL COMMENT '入库时间',
  `total_count` decimal(24,6) NOT NULL COMMENT '合计数量',
  `total_price` decimal(24,6) NOT NULL COMMENT '合计金额，单位：元',
  `status` tinyint(4) NOT NULL COMMENT '状态',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `file_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '附件 URL',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 其它入库单表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_stock_in_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '入库项编号',
  `in_id` bigint(20) NOT NULL COMMENT '入库编号',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库编号',
  `product_id` bigint(20) NOT NULL COMMENT '产品编号',
  `product_unit_id` bigint(20) NOT NULL COMMENT '产品单位编号',
  `product_price` decimal(24,6) DEFAULT NULL COMMENT '产品单价',
  `count` decimal(24,6) NOT NULL COMMENT '产品数量',
  `total_price` decimal(24,6) DEFAULT NULL COMMENT '合计金额，单位：元',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 其它入库单项表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_stock_move` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '调拨编号',
  `no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调拨单号',
  `move_time` datetime NOT NULL COMMENT '调拨时间',
  `total_count` decimal(24,6) NOT NULL COMMENT '合计数量',
  `total_price` decimal(24,6) NOT NULL COMMENT '合计金额，单位：元',
  `status` tinyint(4) NOT NULL COMMENT '状态',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `file_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '附件 URL',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 库存调拨单表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_stock_move_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '调拨项编号',
  `move_id` bigint(20) NOT NULL COMMENT '调拨编号',
  `from_warehouse_id` bigint(20) NOT NULL COMMENT '调出仓库编号',
  `to_warehouse_id` bigint(20) NOT NULL COMMENT '调入仓库编号',
  `product_id` bigint(20) NOT NULL COMMENT '产品编号',
  `product_unit_id` bigint(20) NOT NULL COMMENT '产品单位编号',
  `product_price` decimal(24,6) DEFAULT NULL COMMENT '产品单价',
  `count` decimal(24,6) NOT NULL COMMENT '产品数量',
  `total_price` decimal(24,6) DEFAULT NULL COMMENT '合计金额，单位：元',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 库存调拨项表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_stock_out` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '出库编号',
  `no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '出库单号',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '客户编号',
  `out_time` datetime NOT NULL COMMENT '出库时间',
  `total_count` decimal(24,6) NOT NULL COMMENT '合计数量',
  `total_price` decimal(24,6) NOT NULL COMMENT '合计金额，单位：元',
  `status` tinyint(4) NOT NULL COMMENT '状态',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `file_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '附件 URL',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 其它入库单表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_stock_out_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '出库项编号',
  `out_id` bigint(20) NOT NULL COMMENT '出库编号',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库编号',
  `product_id` bigint(20) NOT NULL COMMENT '产品编号',
  `product_unit_id` bigint(20) NOT NULL COMMENT '产品单位编号',
  `product_price` decimal(24,6) DEFAULT NULL COMMENT '产品单价',
  `count` decimal(24,6) NOT NULL COMMENT '产品数量',
  `total_price` decimal(24,6) DEFAULT NULL COMMENT '合计金额，单位：元',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 其它出库单项表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_stock_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `product_id` bigint(20) NOT NULL COMMENT '产品编号',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库编号',
  `count` decimal(24,6) NOT NULL COMMENT '出入库数量',
  `total_count` decimal(24,6) NOT NULL COMMENT '总库存量',
  `biz_type` tinyint(4) NOT NULL COMMENT '业务类型',
  `biz_id` bigint(20) NOT NULL COMMENT '业务编号',
  `biz_item_id` bigint(20) NOT NULL COMMENT '业务项编号',
  `biz_no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '业务单号',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 产品库存明细表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_supplier` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '供应商编号',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '供应商名称',
  `contact` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系人',
  `mobile` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号码',
  `telephone` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电子邮箱',
  `fax` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '传真',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `status` tinyint(4) NOT NULL COMMENT '开启状态',
  `sort` int(11) NOT NULL COMMENT '排序',
  `tax_no` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '纳税人识别号',
  `tax_percent` decimal(24,6) DEFAULT NULL COMMENT '税率',
  `bank_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开户行',
  `bank_account` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开户账号',
  `bank_address` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开户地址',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 供应商表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `erp_warehouse` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '仓库编号',
  `name` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '仓库名称',
  `address` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '仓库地址',
  `sort` bigint(20) NOT NULL COMMENT '排序',
  `remark` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `principal` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '负责人',
  `warehouse_price` decimal(24,6) DEFAULT NULL COMMENT '仓储费，单位：元',
  `truckage_price` decimal(24,6) DEFAULT NULL COMMENT '搬运费，单位：元',
  `status` tinyint(4) NOT NULL COMMENT '开启状态',
  `default_status` bit(1) DEFAULT b'0' COMMENT '是否默认',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ERP 仓库表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `flw_ev_databasechangelog` (
  `ID` varchar(255) NOT NULL,
  `AUTHOR` varchar(255) NOT NULL,
  `FILENAME` varchar(255) NOT NULL,
  `DATEEXECUTED` datetime NOT NULL,
  `ORDEREXECUTED` int(11) NOT NULL,
  `EXECTYPE` varchar(10) NOT NULL,
  `MD5SUM` varchar(35) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `COMMENTS` varchar(255) DEFAULT NULL,
  `TAG` varchar(255) DEFAULT NULL,
  `LIQUIBASE` varchar(20) DEFAULT NULL,
  `CONTEXTS` varchar(255) DEFAULT NULL,
  `LABELS` varchar(255) DEFAULT NULL,
  `DEPLOYMENT_ID` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `flw_ev_databasechangeloglock` (
  `ID` int(11) NOT NULL,
  `LOCKED` tinyint(4) NOT NULL,
  `LOCKGRANTED` datetime DEFAULT NULL,
  `LOCKEDBY` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `hrm_employee` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `employee_no` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '员工编号',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `sex` tinyint(4) NOT NULL COMMENT '性别（1:男 2:女）',
  `birthday` date DEFAULT NULL COMMENT '出生日期',
  `blood_type` tinyint(4) DEFAULT NULL COMMENT '血型（1:A 2:B 3:AB 4:O）',
  `education` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文化程度',
  `nation` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '民族',
  `political_status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '政治面貌',
  `marital_status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '婚姻状况',
  `job_title` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职称',
  `native_place` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '籍贯',
  `height` decimal(5,2) DEFAULT NULL COMMENT '身高(cm)',
  `weight` decimal(5,2) DEFAULT NULL COMMENT '体重(kg)',
  `id_card` varchar(18) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '身份证号码',
  `mobile` varchar(11) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `household_address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '户籍所在地',
  `current_address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '现居住地址',
  `emergency_contact` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '紧急联系人',
  `emergency_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `avatar` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '照片',
  `bank_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工资开户行',
  `bank_account` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工资卡账户',
  `job_post` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职位',
  `job_position` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职务',
  `employee_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '人员状态（1:正式 2:试用期 3:实习生 4:兼职 5:零时工）',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '所属部门',
  `dept_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属部门名称',
  `company_id` bigint(20) DEFAULT NULL COMMENT '所属公司ID',
  `company_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属单位',
  `entry_date` date DEFAULT NULL COMMENT '入职日期',
  `formal_date` date DEFAULT NULL COMMENT '转正日期',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  `user_id` bigint(20) DEFAULT NULL COMMENT '关联用户ID',
  `user_generated` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已生成用户',
  `email` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_employee_no` (`employee_no`,`deleted`,`tenant_id`) USING BTREE,
  KEY `idx_dept_id` (`dept_id`) USING BTREE,
  KEY `idx_employee_status` (`employee_status`) USING BTREE,
  KEY `idx_company_id` (`company_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工信息表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `hrm_employee_education` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `employee_id` bigint(20) NOT NULL COMMENT '员工ID',
  `start_time` date DEFAULT NULL COMMENT '开始时间',
  `end_time` date DEFAULT NULL COMMENT '截止时间',
  `major` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '专业',
  `school_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学校名称',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  `bill_id` bigint(20) DEFAULT NULL COMMENT '单据id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_employee_id` (`employee_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工教育经历表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `hrm_employee_entry_bill` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bill_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单据编号',
  `process_instance_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程实例编号',
  `process_status` tinyint(4) DEFAULT '0' COMMENT '单据状态（0草稿 1审批中 2审批通过 3审批拒绝 4已取消）',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '姓名',
  `sex` tinyint(4) DEFAULT NULL COMMENT '性别（1:男 2:女）',
  `birthday` date DEFAULT NULL COMMENT '出生日期',
  `id_card` varchar(18) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '身份证号码',
  `mobile` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `email` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `nation` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '民族',
  `political_status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '政治面貌',
  `marital_status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '婚姻状况',
  `native_place` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '籍贯',
  `household_address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '户籍所在地',
  `current_address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '现居住地址',
  `emergency_contact` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '紧急联系人',
  `emergency_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `avatar` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '照片',
  `entry_date` date DEFAULT NULL COMMENT '入职日期',
  `probation_period` int(11) DEFAULT '3' COMMENT '试用期（月数）',
  `expected_formal_date` date DEFAULT NULL COMMENT '预计转正日期',
  `emp_dept_id` bigint(20) DEFAULT NULL COMMENT '员工所属部门ID',
  `emp_dept_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '员工所属部门名称',
  `emp_company_id` bigint(20) DEFAULT NULL COMMENT '员工所属公司ID',
  `emp_company_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '员工所属公司名称',
  `job_post` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职位',
  `job_position` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职务',
  `job_title` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职称',
  `employee_status` tinyint(4) DEFAULT '2' COMMENT '人员状态（1:正式 2:试用期 3:实习生 4:兼职 5:零时工）',
  `education` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文化程度',
  `salary` decimal(10,2) DEFAULT NULL COMMENT '薪资',
  `bank_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工资开户行',
  `bank_account` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工资卡账户',
  `employee_id` bigint(20) DEFAULT NULL COMMENT '关联的员工档案ID（审批通过后创建）',
  `dept_id` bigint(20) NOT NULL COMMENT '制单人部门ID',
  `dept_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '制单人部门名称',
  `company_id` bigint(20) NOT NULL COMMENT '制单人公司ID',
  `company_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '制单人公司名称',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `creator_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建者姓名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bill_code` (`bill_code`,`deleted`,`tenant_id`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_emp_dept_id` (`emp_dept_id`),
  KEY `idx_emp_company_id` (`emp_company_id`),
  KEY `idx_dept_id` (`dept_id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_process_status` (`process_status`),
  KEY `idx_entry_date` (`entry_date`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_creator` (`creator`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工入职申请单';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `hrm_employee_entry_bill_education` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `bill_id` bigint(20) NOT NULL COMMENT '入职申请单ID',
  `start_time` date DEFAULT NULL COMMENT '开始时间',
  `end_time` date DEFAULT NULL COMMENT '截止时间',
  `major` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '专业',
  `school_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学校名称',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_bill_id` (`bill_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工入职申请单教育经历明细表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `hrm_employee_entry_bill_family` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `bill_id` bigint(20) NOT NULL COMMENT '入职申请单ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '姓名',
  `relationship` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关系',
  `mobile` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `work_unit` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作单位',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_bill_id` (`bill_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工入职申请单家属信息明细表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `hrm_employee_entry_bill_work_experience` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `bill_id` bigint(20) NOT NULL COMMENT '入职申请单ID',
  `start_time` date DEFAULT NULL COMMENT '开始时间',
  `end_time` date DEFAULT NULL COMMENT '截止时间',
  `job_position` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职务',
  `company_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '单位名称',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_bill_id` (`bill_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工入职申请单工作经历明细表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `hrm_employee_family` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `employee_id` bigint(20) NOT NULL COMMENT '员工ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '姓名',
  `relationship` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关系',
  `mobile` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `work_unit` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作单位',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  `bill_id` bigint(20) DEFAULT NULL COMMENT '单据id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_employee_id` (`employee_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工家属信息表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `hrm_employee_regular_bill` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bill_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单据编号',
  `process_instance_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程实例编号',
  `process_status` tinyint(4) DEFAULT '0' COMMENT '单据状态（0草稿 1审批中 2审批通过 3审批拒绝 4已取消）',
  `employee_id` bigint(20) DEFAULT NULL COMMENT '关联的员工档案ID',
  `employee_no` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '员工工号',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '姓名',
  `sex` tinyint(4) DEFAULT NULL COMMENT '性别（1:男 2:女）',
  `emp_dept_id` bigint(20) DEFAULT NULL COMMENT '员工所属部门ID',
  `emp_dept_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '员工所属部门名称',
  `emp_company_id` bigint(20) DEFAULT NULL COMMENT '员工所属公司ID',
  `emp_company_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '员工所属公司名称',
  `job_post` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职位',
  `job_position` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职务',
  `employee_status` tinyint(4) DEFAULT '2' COMMENT '当前人员状态（1:正式 2:试用期 3:实习生 4:兼职 5:零时工）',
  `entry_date` date DEFAULT NULL COMMENT '入职日期',
  `formal_date` date DEFAULT NULL COMMENT '转正日期',
  `expected_formal_date` date DEFAULT NULL COMMENT '预计转正日期',
  `probation_period` int(11) DEFAULT NULL COMMENT '试用期（月数）',
  `dept_id` bigint(20) NOT NULL COMMENT '制单人部门ID',
  `dept_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '制单人部门名称',
  `company_id` bigint(20) NOT NULL COMMENT '制单人公司ID',
  `company_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '制单人公司名称',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `creator_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建者姓名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bill_code` (`bill_code`,`deleted`,`tenant_id`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_emp_dept_id` (`emp_dept_id`),
  KEY `idx_emp_company_id` (`emp_company_id`),
  KEY `idx_dept_id` (`dept_id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_process_status` (`process_status`),
  KEY `idx_formal_date` (`formal_date`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_creator` (`creator`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工转正申请单';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `hrm_employee_resignation_bill` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bill_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单据编号',
  `process_instance_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程实例编号',
  `process_status` tinyint(4) DEFAULT '0' COMMENT '单据状态（0草稿 1审批中 2审批通过 3审批拒绝 4已取消）',
  `employee_id` bigint(20) DEFAULT NULL COMMENT '关联的员工档案ID',
  `employee_no` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '员工工号',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '姓名',
  `sex` tinyint(4) DEFAULT NULL COMMENT '性别（1:男 2:女）',
  `mobile` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `emp_dept_id` bigint(20) DEFAULT NULL COMMENT '员工所属部门ID',
  `emp_dept_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '员工所属部门名称',
  `emp_company_id` bigint(20) DEFAULT NULL COMMENT '员工所属公司ID',
  `emp_company_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '员工所属公司名称',
  `job_post` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职位',
  `job_position` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职务',
  `employee_status` tinyint(4) DEFAULT '1' COMMENT '当前人员状态（1:正式 2:试用期 3:实习生 4:兼职 5:零时工）',
  `resignation_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '离职类型（1:主动离职 2:被动离职 3:其他）',
  `application_date` date DEFAULT NULL COMMENT '申请日期',
  `resignation_date` date DEFAULT NULL COMMENT '离职日期',
  `last_working_date` date DEFAULT NULL COMMENT '最后工作日期',
  `handover_person_id` bigint(20) DEFAULT NULL COMMENT '工作交接人ID',
  `handover_person_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作交接人姓名',
  `resignation_reason` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '离职原因（1:个人原因 2:薪资原因 3:晋升原因 4:工作时长 5:其它）',
  `resignation_reason_desc` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '离职原因说明',
  `salary_settlement_date` date DEFAULT NULL COMMENT '薪资结算日期',
  `dept_id` bigint(20) NOT NULL COMMENT '制单人部门ID',
  `dept_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '制单人部门名称',
  `company_id` bigint(20) NOT NULL COMMENT '制单人公司ID',
  `company_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '制单人公司名称',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `creator_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建者姓名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bill_code` (`bill_code`,`deleted`,`tenant_id`),
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工离职申请单';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `hrm_employee_transfer_bill` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bill_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单据编号',
  `process_instance_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程实例编号',
  `process_status` tinyint(4) DEFAULT '0' COMMENT '单据状态（0草稿 1审批中 2审批通过 3审批拒绝 4已取消）',
  `employee_id` bigint(20) DEFAULT NULL COMMENT '关联的员工档案ID',
  `employee_no` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '员工工号',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '姓名',
  `sex` tinyint(4) DEFAULT NULL COMMENT '性别（1:男 2:女）',
  `mobile` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `emp_dept_id` bigint(20) DEFAULT NULL COMMENT '员工所属部门ID',
  `emp_dept_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '员工所属部门名称',
  `emp_company_id` bigint(20) DEFAULT NULL COMMENT '员工所属公司ID',
  `emp_company_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '员工所属公司名称',
  `job_post` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职位',
  `job_position` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职务',
  `employee_status` tinyint(4) DEFAULT '1' COMMENT '当前人员状态（1:正式 2:试用期 3:实习生 4:兼职 5:零时工）',
  `transfer_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '异动类型（1:调岗 2:调薪 3:调部门 4:调公司 5:其他）',
  `transfer_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '异动原因',
  `original_job_post` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '原职位',
  `new_job_post` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '变更为职位',
  `original_job_position` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '原职务',
  `new_job_position` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '变更为职务',
  `original_company_id` bigint(20) DEFAULT NULL COMMENT '原公司ID',
  `original_company_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '原公司名称',
  `original_dept_id` bigint(20) DEFAULT NULL COMMENT '原部门ID',
  `original_dept_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '原部门名称',
  `new_company_id` bigint(20) DEFAULT NULL COMMENT '变更为公司ID',
  `new_company_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '变更为公司名称',
  `new_dept_id` bigint(20) DEFAULT NULL COMMENT '变更为部门ID',
  `new_dept_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '变更为部门名称',
  `effective_immediately` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否立即生效（1是 0否）',
  `effective_date` date DEFAULT NULL COMMENT '生效日期',
  `dept_id` bigint(20) NOT NULL COMMENT '制单人部门ID',
  `dept_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '制单人部门名称',
  `company_id` bigint(20) NOT NULL COMMENT '制单人公司ID',
  `company_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '制单人公司名称',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `creator_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建者姓名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bill_code` (`bill_code`,`deleted`,`tenant_id`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_emp_dept_id` (`emp_dept_id`),
  KEY `idx_emp_company_id` (`emp_company_id`),
  KEY `idx_dept_id` (`dept_id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_process_status` (`process_status`),
  KEY `idx_transfer_type` (`transfer_type`),
  KEY `idx_effective_date` (`effective_date`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_creator` (`creator`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='人事调动申请单';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `hrm_employee_work_experience` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `employee_id` bigint(20) NOT NULL COMMENT '员工ID',
  `start_time` date DEFAULT NULL COMMENT '开始时间',
  `end_time` date DEFAULT NULL COMMENT '截止时间',
  `job_position` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职务',
  `company_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '单位名称',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  `bill_id` bigint(20) DEFAULT NULL COMMENT '单据id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_employee_id` (`employee_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工工作经历表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `hrm_leave_cancel_bill` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bill_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单据编号',
  `process_instance_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程实例编号',
  `process_status` tinyint(4) DEFAULT '0' COMMENT '单据状态（0草稿 1审批中 2审批通过 3审批拒绝 4已取消）',
  `leave_type` tinyint(4) DEFAULT NULL COMMENT '请假类型',
  `leave_balance` decimal(10,1) DEFAULT NULL COMMENT '假期余额（天）',
  `expected_start_time` datetime DEFAULT NULL COMMENT '预计开始时间',
  `expected_end_time` datetime DEFAULT NULL COMMENT '预计结束时间',
  `expected_days` decimal(10,1) DEFAULT NULL COMMENT '预计申请天数',
  `project_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '项目名称',
  `project_code` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '项目编码',
  `leave_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请假原因',
  `actual_start_time` datetime DEFAULT NULL COMMENT '实际开始时间',
  `actual_end_time` datetime DEFAULT NULL COMMENT '实际结束时间',
  `actual_days` decimal(10,1) DEFAULT NULL COMMENT '实际天数',
  `cancel_remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '销假备注',
  `creator_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建者姓名 / 申请人姓名',
  `company_id` bigint(20) DEFAULT NULL COMMENT '公司ID',
  `company_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公司名称',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `dept_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '部门名称',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bill_code` (`bill_code`,`deleted`,`tenant_id`),
  KEY `idx_process_status` (`process_status`),
  KEY `idx_leave_type` (`leave_type`),
  KEY `idx_dept_id` (`dept_id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_creator` (`creator`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='请假销假申请单';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `iot_alert_config` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '配置编号',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置名称',
  `description` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '配置描述',
  `level` tinyint(4) NOT NULL COMMENT '告警级别',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '配置状态',
  `scene_rule_ids` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联的场景联动规则编号数组',
  `receive_user_ids` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接收的用户编号数组',
  `receive_types` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接收的类型数组',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IoT 告警配置表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `iot_alert_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录编号',
  `config_id` bigint(20) NOT NULL COMMENT '告警配置编号',
  `config_name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '告警名称',
  `config_level` tinyint(4) NOT NULL COMMENT '告警级别',
  `scene_rule_id` bigint(20) NOT NULL COMMENT '场景联动规则编号',
  `product_id` bigint(20) DEFAULT NULL COMMENT '产品编号',
  `device_id` bigint(20) DEFAULT NULL COMMENT '设备编号',
  `device_message` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '触发的设备消息',
  `process_status` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否处理',
  `process_remark` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '处理结果（备注）',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IoT 告警记录表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `iot_data_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '数据流转规格编号',
  `name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据流转规格名称',
  `description` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '数据流转规格描述',
  `status` int(11) NOT NULL COMMENT '数据流转规格状态',
  `source_configs` varchar(10000) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据源配置数组',
  `sink_ids` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据目的编号数组',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IoT 数据流转规则表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `iot_data_sink` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '数据流转目的编号',
  `name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据流转目的名称',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '桥梁描述',
  `status` int(11) NOT NULL COMMENT '桥梁状态',
  `type` int(11) NOT NULL COMMENT '桥梁类型',
  `config` json DEFAULT NULL COMMENT '桥梁配置',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IoT 数据流转目的';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `iot_device` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '设备 ID，主键，自增',
  `device_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设备名称，在产品内唯一，用于标识设备',
  `nickname` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '设备备注名称，供用户自定义备注',
  `serial_number` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '设备序列号',
  `pic_url` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '设备图片',
  `group_ids` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '设备分组编号集合',
  `product_id` bigint(20) unsigned NOT NULL COMMENT '产品 ID',
  `product_key` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '产品 Key',
  `device_type` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '设备类型，参见 IotProductDeviceTypeEnum 枚举',
  `gateway_id` bigint(20) unsigned DEFAULT NULL COMMENT '网关设备 ID，子设备需要关联的网关设备 ID',
  `state` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '设备状态，参见 IotDeviceStateEnum 枚举',
  `online_time` datetime DEFAULT NULL COMMENT '最后上线时间',
  `offline_time` datetime DEFAULT NULL COMMENT '最后离线时间',
  `active_time` datetime DEFAULT NULL COMMENT '设备激活时间',
  `firmware_id` bigint(20) DEFAULT NULL COMMENT 'OTA 固件编号',
  `device_secret` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '设备密钥，用于设备认证，需安全存储',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '设备位置的纬度',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '设备位置的经度',
  `config` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '设备配置，JSON 格式',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_device_name_product_id` (`device_name`,`product_id`) USING BTREE,
  KEY `idx_product_id` (`product_id`) USING BTREE,
  KEY `idx_gateway_id` (`gateway_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IoT 设备表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `iot_device_group` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '分组 ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分组名字',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '分组状态',
  `description` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分组描述',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IoT 设备分组表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `iot_device_modbus_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `device_id` bigint(20) NOT NULL COMMENT '设备编号',
  `product_id` bigint(20) DEFAULT NULL COMMENT '产品编号',
  `ip` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'Modbus 服务器 IP 地址',
  `port` int(11) NOT NULL DEFAULT '502' COMMENT 'Modbus 服务器端口',
  `slave_id` int(11) NOT NULL DEFAULT '1' COMMENT '从站地址',
  `timeout` int(11) NOT NULL DEFAULT '3000' COMMENT '连接超时时间，单位：毫秒',
  `retry_interval` int(11) NOT NULL DEFAULT '1000' COMMENT '重试间隔，单位：毫秒',
  `mode` tinyint(4) NOT NULL DEFAULT '1' COMMENT '工作模式',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态',
  `frame_format` tinyint(4) NOT NULL DEFAULT '1' COMMENT '数据帧格式',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_device_id` (`device_id`,`deleted`,`tenant_id`) USING BTREE COMMENT '设备编号唯一索引'
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IoT 设备 Modbus 连接配置表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `iot_device_modbus_point` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `device_id` bigint(20) NOT NULL COMMENT '设备编号',
  `thing_model_id` bigint(20) NOT NULL COMMENT '物模型属性编号',
  `identifier` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '属性标识符',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '属性名称',
  `function_code` tinyint(4) NOT NULL DEFAULT '3' COMMENT 'Modbus 功能码（1-读线圈 2-读离散输入 3-读保持寄存器 4-读输入寄存器）',
  `register_address` int(11) NOT NULL DEFAULT '0' COMMENT '寄存器起始地址',
  `register_count` int(11) NOT NULL DEFAULT '1' COMMENT '寄存器数量',
  `byte_order` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'AB' COMMENT '字节序（AB/BA/ABCD/CDAB/DCBA/BADC）',
  `raw_data_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'INT16' COMMENT '原始数据类型（INT16/UINT16/INT32/UINT32/FLOAT/DOUBLE/BOOLEAN/STRING）',
  `scale` decimal(20,6) NOT NULL DEFAULT '1.000000' COMMENT '缩放因子',
  `poll_interval` int(11) NOT NULL DEFAULT '5000' COMMENT '轮询间隔，单位：毫秒',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态（0-开启 1-禁用）',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_device_thing_model` (`device_id`,`thing_model_id`,`deleted`,`tenant_id`) USING BTREE COMMENT '设备+物模型唯一索引',
  KEY `idx_device_id` (`device_id`) USING BTREE COMMENT '设备编号索引'
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IoT 设备 Modbus 点位配置表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `iot_ota_firmware` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '固件编号',
  `name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '固件名称',
  `description` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '固件描述',
  `version` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '版本号',
  `product_id` bigint(20) NOT NULL COMMENT '产品编号',
  `file_url` varchar(1024) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '固件文件 URL',
  `file_size` bigint(20) NOT NULL COMMENT '固件文件大小',
  `file_digest_algorithm` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '固件文件签名算法',
  `file_digest_value` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '固件文件签名结果',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IoT OTA 固件表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `iot_ota_task` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '任务编号，主键，自增',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
  `description` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务描述',
  `firmware_id` bigint(20) unsigned NOT NULL COMMENT '固件编号',
  `status` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '任务状态，参见 IotOtaTaskStatusEnum 枚举',
  `device_scope` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '设备升级范围，参见 IotOtaTaskDeviceScopeEnum 枚举',
  `device_total_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '设备总数数量',
  `device_success_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '设备成功数量',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IoT OTA 升级任务表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `iot_ota_task_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '记录编号，主键，自增',
  `firmware_id` bigint(20) unsigned NOT NULL COMMENT '固件编号',
  `task_id` bigint(20) unsigned NOT NULL COMMENT '任务编号',
  `device_id` bigint(20) unsigned NOT NULL COMMENT '设备编号',
  `from_firmware_id` bigint(20) unsigned DEFAULT NULL COMMENT '来源的固件编号',
  `status` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '升级状态，参见 IotOtaTaskRecordStatusEnum 枚举',
  `progress` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '升级进度，百分比（0-100）',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '升级进度描述',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IoT OTA 升级任务记录表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `iot_product` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '产品 ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '产品名称',
  `product_key` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '产品标识',
  `product_secret` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产品密钥，用于动态注册设备',
  `register_enabled` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否开启动态注册',
  `category_id` bigint(20) NOT NULL COMMENT '产品分类 ID',
  `icon` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产品图标',
  `pic_url` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产品图片',
  `description` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产品描述',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '产品状态，参见 IotProductStatusEnum 枚举',
  `device_type` tinyint(4) NOT NULL COMMENT '设备类型，参见 IotProductDeviceTypeEnum 枚举',
  `net_type` tinyint(4) DEFAULT NULL COMMENT '联网方式，参见 IotNetTypeEnum 枚举',
  `protocol_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'mqtt' COMMENT '协议类型',
  `serialize_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'json' COMMENT '序列化类型',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IoT 产品表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `iot_product_category` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '分类 ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名字',
  `sort` int(11) NOT NULL COMMENT '分类排序',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '分类状态',
  `description` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类描述',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IoT 产品分类表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `iot_rule_scene` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '数据流转编号',
  `name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据流转名称',
  `description` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '数据流转描述',
  `status` tinyint(4) NOT NULL COMMENT '数据流转状态',
  `triggers` json NOT NULL COMMENT '触发器数组',
  `actions` json NOT NULL COMMENT '执行器数组',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IoT 场景联动规则';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `iot_scene_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '数据流转编号',
  `name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据流转名称',
  `description` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '数据流转描述',
  `status` tinyint(4) NOT NULL COMMENT '数据流转状态',
  `triggers` json NOT NULL COMMENT '触发器数组',
  `actions` json NOT NULL COMMENT '执行器数组',
  `last_trigger_time` datetime DEFAULT NULL COMMENT '最后触发时间',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IoT 场景联动规则';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `iot_thing_model` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '物模型功能编号',
  `identifier` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '功能标识',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '功能名称',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '功能描述',
  `product_id` bigint(20) unsigned NOT NULL COMMENT '产品ID（关联 IotProductDO 的 id）',
  `product_key` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '产品Key（关联 IotProductDO 的 productKey）',
  `type` tinyint(3) unsigned NOT NULL COMMENT '功能类型（1 - 属性，2 - 服务，3 - 事件）',
  `property` json DEFAULT NULL COMMENT '属性（存储 ThingModelProperty 的 JSON 数据）',
  `event` json DEFAULT NULL COMMENT '事件（存储 ThingModelEvent 的 JSON 数据）',
  `service` json DEFAULT NULL COMMENT '服务（存储服务的 JSON 数据）',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_product_id` (`product_id`) USING BTREE,
  KEY `idx_product_key` (`product_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=125 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IoT 产品物模型功能表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `mp_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公众号名称',
  `account` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公众号账号',
  `app_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公众号appid',
  `app_secret` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公众号密钥',
  `url` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公众号url',
  `token` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公众号token',
  `aes_key` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '加密密钥',
  `qr_code_url` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '二维码图片URL',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公众号账号表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `mp_auto_reply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` bigint(20) NOT NULL COMMENT '公众号账号的编号',
  `app_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公众号 appId',
  `type` tinyint(4) NOT NULL COMMENT '回复类型',
  `request_keyword` varchar(255) DEFAULT NULL COMMENT '请求的关键字',
  `request_match` tinyint(4) DEFAULT NULL COMMENT '请求的关键字的匹配',
  `request_message_type` varchar(32) DEFAULT NULL COMMENT '请求的消息类型',
  `response_message_type` varchar(32) NOT NULL COMMENT '回复的消息类型',
  `response_content` varchar(1024) DEFAULT NULL COMMENT '回复的消息内容',
  `response_media_id` varchar(128) DEFAULT NULL COMMENT '回复的媒体文件 id',
  `response_media_url` varchar(1024) DEFAULT NULL COMMENT '回复的媒体文件 URL',
  `response_title` varchar(128) DEFAULT NULL COMMENT '回复的标题',
  `response_description` varchar(256) DEFAULT NULL COMMENT '回复的描述',
  `response_thumb_media_id` varchar(128) DEFAULT NULL COMMENT '回复的缩略图的媒体 id',
  `response_thumb_media_url` varchar(1024) DEFAULT NULL COMMENT '回复的缩略图的媒体 URL',
  `response_articles` varchar(1024) DEFAULT NULL COMMENT '回复的图文消息数组',
  `response_music_url` varchar(1024) DEFAULT NULL COMMENT '回复的音乐链接',
  `response_hq_music_url` varchar(1024) DEFAULT NULL COMMENT '回复的高质量音乐链接',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8 COMMENT='公众号消息自动回复表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `mp_material` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` bigint(20) NOT NULL COMMENT '公众号账号的编号',
  `app_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公众号 appId',
  `media_id` varchar(128) NOT NULL COMMENT '公众号素材 id',
  `type` varchar(32) NOT NULL COMMENT '文件类型',
  `permanent` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否永久',
  `url` varchar(1024) DEFAULT NULL COMMENT '文件服务器的 URL',
  `name` varchar(255) DEFAULT NULL COMMENT '名字',
  `mp_url` varchar(1024) DEFAULT NULL COMMENT '公众号文件 URL',
  `title` varchar(255) DEFAULT NULL COMMENT '视频素材的标题',
  `introduction` varchar(255) DEFAULT NULL COMMENT '视频素材的描述',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=utf8 COMMENT='公众号素材表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `mp_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` bigint(20) NOT NULL COMMENT '微信公众号ID',
  `app_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '微信公众号 appid',
  `name` varchar(255) DEFAULT NULL COMMENT '菜单名称',
  `menu_key` varchar(255) DEFAULT NULL COMMENT '菜单标识',
  `parent_id` varchar(32) DEFAULT NULL COMMENT '父ID',
  `type` varchar(32) NOT NULL DEFAULT '' COMMENT '按钮类型',
  `url` varchar(500) DEFAULT NULL COMMENT '网页链接',
  `mini_program_app_id` varchar(32) DEFAULT NULL COMMENT '小程序appid',
  `mini_program_page_path` varchar(200) DEFAULT NULL COMMENT '小程序页面路径',
  `article_id` varchar(200) DEFAULT NULL COMMENT '跳转图文的媒体编号',
  `reply_message_type` varchar(32) DEFAULT NULL COMMENT '消息类型',
  `reply_content` varchar(1024) DEFAULT NULL COMMENT '回复的消息内容',
  `reply_media_id` varchar(128) DEFAULT NULL COMMENT '回复的媒体文件 id',
  `reply_media_url` varchar(1024) DEFAULT NULL COMMENT '回复的媒体文件 URL',
  `reply_title` varchar(128) DEFAULT NULL COMMENT '回复的标题',
  `reply_description` varchar(256) DEFAULT NULL COMMENT '回复的描述',
  `reply_thumb_media_id` varchar(128) DEFAULT NULL COMMENT '回复的缩略图的媒体 id',
  `reply_thumb_media_url` varchar(1024) DEFAULT NULL COMMENT '回复的缩略图的媒体 URL',
  `reply_articles` varchar(1024) DEFAULT NULL COMMENT '回复的图文消息数组',
  `reply_music_url` varchar(1024) DEFAULT NULL COMMENT '回复的音乐链接',
  `reply_hq_music_url` varchar(1024) DEFAULT NULL COMMENT '回复的高质量音乐链接',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=170 DEFAULT CHARSET=utf8 COMMENT='公众号菜单表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `mp_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `msg_id` bigint(20) DEFAULT NULL COMMENT '微信公众号的消息编号',
  `account_id` bigint(20) NOT NULL COMMENT '公众号账号的编号',
  `app_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公众号 appId',
  `user_id` bigint(20) NOT NULL COMMENT '公众号粉丝的编号',
  `openid` varchar(100) NOT NULL COMMENT '公众号粉丝标志',
  `type` varchar(32) NOT NULL COMMENT '消息类型',
  `send_from` tinyint(4) NOT NULL COMMENT '消息来源',
  `content` varchar(1024) DEFAULT NULL COMMENT '消息内容',
  `media_id` varchar(128) DEFAULT NULL COMMENT '媒体文件 id',
  `media_url` varchar(1024) DEFAULT NULL COMMENT '媒体文件 URL',
  `recognition` varchar(1024) DEFAULT NULL COMMENT '语音识别后文本',
  `format` varchar(16) DEFAULT NULL COMMENT '语音格式',
  `title` varchar(128) DEFAULT NULL COMMENT '标题',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  `thumb_media_id` varchar(128) DEFAULT NULL COMMENT '缩略图的媒体 id',
  `thumb_media_url` varchar(1024) DEFAULT NULL COMMENT '缩略图的媒体 URL',
  `url` varchar(500) DEFAULT NULL COMMENT '点击图文消息跳转链接',
  `location_x` double DEFAULT NULL COMMENT '地理位置维度',
  `location_y` double DEFAULT NULL COMMENT '地理位置经度',
  `scale` double DEFAULT NULL COMMENT '地图缩放大小',
  `label` varchar(128) DEFAULT NULL COMMENT '详细地址',
  `articles` varchar(1024) DEFAULT NULL COMMENT '图文消息数组',
  `music_url` varchar(1024) DEFAULT NULL COMMENT '音乐链接',
  `hq_music_url` varchar(1024) DEFAULT NULL COMMENT '高质量音乐链接',
  `event` varchar(64) DEFAULT NULL COMMENT '事件类型',
  `event_key` varchar(64) DEFAULT NULL COMMENT '事件 Key',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=436 DEFAULT CHARSET=utf8 COMMENT='公众号消息表 ';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `mp_message_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` bigint(20) NOT NULL COMMENT '公众号账号的编号',
  `app_id` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公众号 appId',
  `template_id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公众号模板ID',
  `title` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `content` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模板内容',
  `example` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模板示例',
  `primary_industry` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模板所属行业的一级行业',
  `deputy_industry` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模板所属行业的二级行业',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公众号模板消息';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `mp_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tag_id` bigint(20) DEFAULT NULL COMMENT '公众号标签 id',
  `name` varchar(32) DEFAULT NULL COMMENT '标签名称',
  `count` int(11) DEFAULT '0' COMMENT '粉丝数量',
  `account_id` bigint(20) NOT NULL COMMENT '公众号账号的编号',
  `app_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公众号 appId',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8 COMMENT='公众号标签表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `mp_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `openid` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户标识',
  `union_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信生态唯一标识',
  `subscribe_status` tinyint(4) NOT NULL COMMENT '关注状态',
  `subscribe_time` datetime NOT NULL COMMENT '关注时间',
  `nickname` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
  `head_image_url` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像地址',
  `unsubscribe_time` datetime DEFAULT NULL COMMENT '取消关注时间',
  `language` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '语言',
  `country` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '国家',
  `province` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '省份',
  `city` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '城市',
  `remark` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `tag_ids` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签编号数组',
  `account_id` bigint(20) NOT NULL COMMENT '微信公众号ID',
  `app_id` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '微信公众号 appid',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公众号粉丝表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `oa_car` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `car_no` varchar(32) NOT NULL COMMENT '车牌号',
  `car_name` varchar(64) DEFAULT NULL COMMENT '车辆名称',
  `car_type` bigint(20) NOT NULL COMMENT '车型',
  `car_cls` bigint(20) NOT NULL COMMENT '分类',
  `brand` varchar(64) NOT NULL COMMENT '品牌型号',
  `seat_num` varchar(32) DEFAULT NULL COMMENT '车座',
  `bare_price` decimal(24,6) DEFAULT NULL COMMENT '裸车价',
  `force_insurance_date` date DEFAULT NULL COMMENT '交强险到期日期',
  `business_insurance_date` date DEFAULT NULL COMMENT '商业险到期日期',
  `year_check_date` date DEFAULT NULL COMMENT '年检日期',
  `pic_url` varchar(255) DEFAULT NULL COMMENT '上传照片',
  `sort` int(11) DEFAULT '0' COMMENT '显示顺序',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  `company_id` bigint(20) NOT NULL COMMENT '公司ID',
  `company_name` varchar(64) NOT NULL COMMENT '公司名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COMMENT='车辆信息表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `oa_car_apply_bill` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bill_code` varchar(32) NOT NULL COMMENT '单据编号',
  `process_instance_id` varchar(64) DEFAULT NULL COMMENT '流程实例编号',
  `process_status` tinyint(4) DEFAULT NULL COMMENT '单据状态',
  `car_id` bigint(20) DEFAULT NULL COMMENT '车辆',
  `go_time` datetime DEFAULT NULL COMMENT '出车时间',
  `return_time` datetime DEFAULT NULL COMMENT '回车时间',
  `go_area` varchar(150) DEFAULT NULL COMMENT '出车地点',
  `return_area` varchar(150) DEFAULT NULL COMMENT '回车地点',
  `cause` varchar(255) DEFAULT NULL COMMENT '用车事由',
  `applyer` varchar(64) DEFAULT NULL COMMENT '申请人',
  `passenger` varchar(255) DEFAULT NULL COMMENT '随行人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `creator_name` varchar(30) DEFAULT NULL COMMENT '创建者姓名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  `parent_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父级ID',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `dept_name` varchar(64) DEFAULT NULL COMMENT '部门名称',
  `company_id` bigint(20) DEFAULT NULL COMMENT '公司ID',
  `company_name` varchar(64) DEFAULT NULL COMMENT '公司名称',
  `return_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '还车状态，0-未还车，1-还车中，2-已还车',
  `car_no` varchar(32) DEFAULT NULL COMMENT '车牌号码',
  PRIMARY KEY (`id`),
  KEY `idx_oa_car_apply_bill_is_returned` (`return_status`),
  KEY `idx_oa_car_apply_bill_company_returned` (`company_id`,`return_status`)
) ENGINE=InnoDB AUTO_INCREMENT=2033 DEFAULT CHARSET=utf8mb4 COMMENT='用车申请单';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `oa_car_return_bill` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bill_code` varchar(32) NOT NULL COMMENT '单据编号',
  `process_instance_id` varchar(64) DEFAULT NULL COMMENT '流程实例编号',
  `process_status` tinyint(4) DEFAULT NULL COMMENT '单据状态',
  `apply_bill` varchar(32) DEFAULT NULL COMMENT '用车申请单',
  `car_id` bigint(20) DEFAULT NULL COMMENT '车辆',
  `car_no` varchar(32) DEFAULT NULL COMMENT '车牌号',
  `go_time` datetime DEFAULT NULL COMMENT '出车时间',
  `return_time` datetime DEFAULT NULL COMMENT '回车时间',
  `go_area` varchar(150) DEFAULT NULL COMMENT '出车地点',
  `return_area` varchar(150) DEFAULT NULL COMMENT '回车地点',
  `cause` varchar(255) DEFAULT NULL COMMENT '用车事由',
  `applyer` varchar(64) DEFAULT NULL COMMENT '申请人',
  `passenger` varchar(255) DEFAULT NULL COMMENT '随行人',
  `remark` varchar(500) DEFAULT NULL COMMENT '还车说明',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `creator_name` varchar(30) DEFAULT NULL COMMENT '创建者姓名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  `parent_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父级ID',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `dept_name` varchar(64) DEFAULT NULL COMMENT '部门名称',
  `company_id` bigint(20) DEFAULT NULL COMMENT '公司ID',
  `company_name` varchar(64) DEFAULT NULL COMMENT '公司名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COMMENT='还车申请单';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `oa_file_favorite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `file_id` bigint(20) NOT NULL COMMENT '文件ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户名称',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_file_user` (`file_id`,`user_id`,`deleted`) USING BTREE,
  KEY `idx_file_id` (`file_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OA协同办公-企业云盘-收藏文件表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `oa_file_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `parent_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父文件夹ID，0表示根目录',
  `file_type` tinyint(4) NOT NULL COMMENT '文件类型（0文件夹 1文件）',
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名称',
  `file_size` bigint(20) DEFAULT '0' COMMENT '文件大小（字节），文件夹为0',
  `file_extension` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件扩展名',
  `file_suffix` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件后缀名（不含点号）',
  `file_category` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'other' COMMENT '文件分类（all全部 image图片 document文档 video视频 audio音频 archive压缩包 other其他）',
  `file_path` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件存储路径',
  `file_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件访问URL',
  `file_md5` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件MD5值',
  `is_shared` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否共享文件夹（0否 1是），仅文件夹有效',
  `share_type` tinyint(4) DEFAULT NULL COMMENT '文件夹分享类型（0人员 1组织），仅共享文件夹有效',
  `share_permission` tinyint(4) DEFAULT NULL COMMENT '分享权限（0仅查看 1可管理），仅共享文件夹有效',
  `owner_id` bigint(20) NOT NULL COMMENT '所有者ID（用户ID）',
  `owner_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所有者名称',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '所属部门ID',
  `dept_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属部门名称',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序序号',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_parent_id` (`parent_id`) USING BTREE,
  KEY `idx_owner_id` (`owner_id`) USING BTREE,
  KEY `idx_file_type` (`file_type`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  KEY `idx_file_category` (`file_category`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OA协同办公-企业云盘-文件信息表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `oa_file_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `file_id` bigint(20) NOT NULL COMMENT '文件ID',
  `share_type` tinyint(4) NOT NULL COMMENT '分享类型（0人员 1组织）',
  `target_id` bigint(20) NOT NULL COMMENT '目标ID（人员ID或组织ID）',
  `target_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '目标名称（人员名称或组织名称）',
  `permission` tinyint(4) NOT NULL DEFAULT '0' COMMENT '权限（0仅查看 1可管理）',
  `inherit_permission` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否继承权限（0否 1是）',
  `share_path` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分享路径（用于显示层级结构）',
  `root_share_id` bigint(20) DEFAULT NULL COMMENT '根分享文件夹ID（用于快速定位）',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `share_code` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分享码（可选）',
  `access_count` int(11) NOT NULL DEFAULT '0' COMMENT '访问次数',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_file_share_target` (`file_id`,`share_type`,`target_id`,`deleted`) USING BTREE,
  KEY `idx_file_id` (`file_id`) USING BTREE,
  KEY `idx_target` (`share_type`,`target_id`) USING BTREE,
  KEY `idx_root_share` (`root_share_id`) USING BTREE,
  KEY `idx_share_code` (`share_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OA协同办公-企业云盘-文件权限表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `oa_meeting_room` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `room_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '会议室名称',
  `room_location` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '会议室位置',
  `room_type` int(11) NOT NULL COMMENT '会议室类型',
  `manager_id` bigint(20) NOT NULL COMMENT '负责人ID',
  `manager_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '负责人姓名',
  `manager_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '负责人联系方式',
  `available_status` int(11) NOT NULL DEFAULT '0' COMMENT '可用状态（0正常 1维修中 2不可用）',
  `pic_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会议室图片URL',
  `seat_count` int(11) DEFAULT NULL COMMENT '坐席数',
  `equipment` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会议室设备（逗号分隔：tv,computer,remote,projector,water_dispenser,locker）',
  `attachment_url` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '附件URL',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注（200字以内）',
  `allow_booking` bit(1) NOT NULL DEFAULT b'1' COMMENT '允许预定',
  `need_approval` bit(1) NOT NULL DEFAULT b'0' COMMENT '预定需审批',
  `booking_scope` int(11) NOT NULL DEFAULT '0' COMMENT '可用范围（0全部成员 1指定成员）',
  `booking_members` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '可预定成员ID（逗号分隔，当booking_scope=1时有效）',
  `sort` int(11) DEFAULT '0' COMMENT '显示顺序',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_room_type` (`room_type`) USING BTREE,
  KEY `idx_available_status` (`available_status`) USING BTREE,
  KEY `idx_manager_id` (`manager_id`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会议室信息表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `oa_meeting_room_booking` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bill_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单据编号',
  `process_instance_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程实例编号',
  `process_status` tinyint(4) DEFAULT '0' COMMENT '单据状态（0草稿 1审批中 2审批通过 3审批拒绝 4已取消）',
  `room_id` bigint(20) DEFAULT NULL COMMENT '会议室ID',
  `room_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会议室名称',
  `room_location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会议室位置',
  `room_type` int(11) DEFAULT NULL COMMENT '会议室类型',
  `meeting_title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会议名称',
  `meeting_start_time` datetime DEFAULT NULL COMMENT '会议开始时间',
  `meeting_end_time` datetime DEFAULT NULL COMMENT '会议结束时间',
  `moderator_id` bigint(20) DEFAULT NULL COMMENT '主持人ID',
  `moderator_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主持人姓名',
  `meeting_remark` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会议备注',
  `reminder_type` tinyint(4) DEFAULT NULL COMMENT '会议提醒（1不提醒 2提前5分钟 3提前10分钟 4提前15分钟 5提前30分钟）',
  `attendees` text COLLATE utf8mb4_unicode_ci COMMENT '与会人ID列表（JSON数组格式）',
  `attendee_names` text COLLATE utf8mb4_unicode_ci COMMENT '与会人姓名列表（JSON数组格式）',
  `attachment_urls` text COLLATE utf8mb4_unicode_ci COMMENT '附件URL列表（JSON数组格式）',
  `use_status` tinyint(4) DEFAULT '0' COMMENT '使用状态（0待使用 1使用中 2已完成 3已取消）',
  `creator_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '申请人姓名',
  `company_id` bigint(20) NOT NULL COMMENT '公司ID',
  `company_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公司名称',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  `dept_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '部门名称',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bill_code` (`bill_code`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_creator` (`creator`),
  KEY `idx_process_status` (`process_status`),
  KEY `idx_use_status` (`use_status`),
  KEY `idx_meeting_start_time` (`meeting_start_time`),
  KEY `idx_meeting_end_time` (`meeting_end_time`),
  KEY `idx_moderator_id` (`moderator_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会议室预定申请单';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `oa_seal` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `company_id` bigint(20) NOT NULL COMMENT '公司ID',
  `company_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公司名称',
  `seal_no` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '印章编号',
  `seal_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '印章名称',
  `seal_type` bigint(20) NOT NULL COMMENT '印章类型',
  `seal_cls` bigint(20) NOT NULL COMMENT '分类',
  `keeper_id` bigint(20) DEFAULT NULL COMMENT '保管人ID',
  `keeper_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '保管人名称',
  `keeper_dept_id` bigint(20) DEFAULT NULL COMMENT '保管部门ID',
  `keeper_dept_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '保管部门名称',
  `purchase_date` date DEFAULT NULL COMMENT '购买日期',
  `enable_date` date DEFAULT NULL COMMENT '启用日期',
  `disable_date` date DEFAULT NULL COMMENT '停用日期',
  `pic_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '上传照片',
  `sort` int(11) DEFAULT '0' COMMENT '显示顺序',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态（0在库 1停用 2使用中）',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_company_id` (`company_id`) USING BTREE,
  KEY `idx_seal_no` (`seal_no`) USING BTREE,
  KEY `idx_seal_cls` (`seal_cls`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='印章信息表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `oa_seal_apply_bill` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bill_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单据编号',
  `process_instance_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程实例编号',
  `process_status` tinyint(4) DEFAULT '0' COMMENT '单据状态（0草稿 1审批中 2审批通过 3审批拒绝 4已取消）',
  `seal_id` bigint(20) NOT NULL COMMENT '印章ID',
  `seal_no` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '印章编号',
  `seal_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '印章名称',
  `seal_type` tinyint(4) DEFAULT NULL COMMENT '印章类型',
  `keeper_id` bigint(20) DEFAULT NULL COMMENT '保管人ID',
  `keeper_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '保管人名称',
  `keeper_dept_id` bigint(20) DEFAULT NULL COMMENT '保管部门ID',
  `keeper_dept_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '保管部门名称',
  `cause` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用章事由',
  `use_type` tinyint(4) NOT NULL COMMENT '用章类型（1合同用章 2证明用章 3公函用章 4其他用章）',
  `use_mode` tinyint(4) NOT NULL COMMENT '用章方式（1现场用章 2外借用章）',
  `document_title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件标题',
  `document_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件类型',
  `document_count` int(11) DEFAULT '1' COMMENT '文件份数',
  `contract_amount` decimal(15,2) DEFAULT NULL COMMENT '合同金额（合同用章时填写）',
  `contract_party` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '合同对方（合同用章时填写）',
  `expected_use_time` datetime DEFAULT NULL COMMENT '预计用章时间',
  `actual_use_time` datetime DEFAULT NULL COMMENT '实际用章时间',
  `expected_return_time` datetime DEFAULT NULL COMMENT '预计归还时间（外借用章时填写）',
  `actual_return_time` datetime DEFAULT NULL COMMENT '实际归还时间（外借用章时填写）',
  `use_status` tinyint(4) DEFAULT '0' COMMENT '用章状态（0待处理 1已完成 2外借中 3已归还 4已逾期）',
  `is_urgent` tinyint(4) DEFAULT '0' COMMENT '是否紧急（0否 1是）',
  `creator_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '申请人姓名',
  `company_id` bigint(20) NOT NULL COMMENT '公司ID',
  `company_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公司名称',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  `dept_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '部门名称',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bill_code` (`bill_code`),
  KEY `idx_seal_id` (`seal_id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_creator_id` (`creator`),
  KEY `idx_process_status` (`process_status`),
  KEY `idx_use_status` (`use_status`),
  KEY `idx_use_mode` (`use_mode`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用章申请单';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `onl_drag_comp` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `parent_id` varchar(32) DEFAULT NULL,
  `comp_name` varchar(50) DEFAULT NULL COMMENT '组件名称',
  `comp_type` varchar(20) DEFAULT NULL,
  `icon` varchar(50) DEFAULT NULL COMMENT '图标',
  `order_num` int(11) DEFAULT NULL COMMENT '排序',
  `type_id` int(11) DEFAULT NULL COMMENT '组件类型',
  `comp_config` longtext COMMENT '组件配置',
  `status` varchar(2) CHARACTER SET utf8 DEFAULT '0' COMMENT '状态0:无效 1:有效',
  `create_by` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '创建人登录名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '更新人登录名称',
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='组件库';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `onl_drag_dataset_head` (
  `id` varchar(32) NOT NULL COMMENT 'id',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `code` varchar(36) DEFAULT NULL COMMENT '编码',
  `parent_id` varchar(36) DEFAULT NULL COMMENT '父id',
  `db_source` varchar(100) DEFAULT NULL COMMENT '动态数据源',
  `query_sql` varchar(5000) DEFAULT '0' COMMENT '查询数据SQL',
  `content` varchar(1000) DEFAULT NULL COMMENT '描述',
  `iz_agent` varchar(10) DEFAULT '0' COMMENT 'iz_agent',
  `data_type` varchar(50) DEFAULT NULL COMMENT '数据类型',
  `api_method` varchar(10) DEFAULT NULL COMMENT 'api方法：get/post',
  `create_time` datetime DEFAULT NULL,
  `create_by` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  `low_app_id` varchar(32) DEFAULT NULL COMMENT '应用ID',
  `tenant_id` int(10) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `onl_drag_dataset_item` (
  `id` varchar(32) NOT NULL COMMENT 'id',
  `head_id` varchar(36) NOT NULL COMMENT '主表ID',
  `field_name` varchar(36) DEFAULT NULL COMMENT '字段名',
  `field_txt` varchar(1000) DEFAULT NULL COMMENT '字段文本',
  `field_type` varchar(10) DEFAULT NULL COMMENT '字段类型',
  `widget_type` varchar(30) DEFAULT NULL COMMENT '控件类型',
  `dict_code` varchar(500) DEFAULT NULL COMMENT '字典Code',
  `dict_table` varchar(125) DEFAULT NULL,
  `dict_text` varchar(125) DEFAULT NULL,
  `iz_show` varchar(5) DEFAULT NULL COMMENT '是否列表显示',
  `iz_search` varchar(10) DEFAULT NULL COMMENT '是否查询',
  `iz_total` varchar(5) DEFAULT NULL COMMENT '是否计算总计（仅对数值有效）',
  `search_mode` varchar(10) DEFAULT NULL COMMENT '查询模式',
  `order_num` int(11) DEFAULT NULL COMMENT '排序',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_oddi_head_id` (`head_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `onl_drag_dataset_param` (
  `id` varchar(36) NOT NULL,
  `head_id` varchar(36) NOT NULL COMMENT '动态报表ID',
  `param_name` varchar(32) NOT NULL COMMENT '参数字段',
  `param_txt` varchar(32) DEFAULT NULL COMMENT '参数文本',
  `param_value` varchar(1000) DEFAULT NULL COMMENT '参数默认值',
  `order_num` int(11) DEFAULT NULL COMMENT '排序',
  `iz_search` int(11) DEFAULT NULL COMMENT '查询标识0否1是 默认0',
  `widget_type` varchar(50) DEFAULT NULL COMMENT '查询控件类型',
  `search_mode` int(11) DEFAULT NULL COMMENT '查询模式1简单2范围',
  `dict_code` varchar(255) DEFAULT NULL COMMENT '字典',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人登录名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人登录名称',
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_oddp_head_id` (`head_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `onl_drag_page` (
  `id` varchar(50) NOT NULL COMMENT '主键',
  `name` varchar(100) DEFAULT NULL COMMENT '界面名称',
  `path` varchar(100) DEFAULT NULL COMMENT '访问路径',
  `background_color` varchar(10) DEFAULT NULL COMMENT '背景色',
  `background_image` varchar(255) DEFAULT NULL COMMENT '背景图',
  `design_type` int(1) DEFAULT NULL COMMENT '设计模式(1:pc,2:手机,3:平板)',
  `theme` varchar(10) DEFAULT NULL COMMENT '主题色',
  `style` varchar(20) DEFAULT NULL COMMENT '面板主题',
  `cover_url` varchar(500) DEFAULT NULL COMMENT '封面图',
  `des_json` varchar(500) DEFAULT NULL COMMENT '仪表盘主配置JSON',
  `template` longtext COMMENT '布局json',
  `protection_code` varchar(32) DEFAULT NULL COMMENT '保护码',
  `type` varchar(64) DEFAULT NULL COMMENT '文件夹类',
  `iz_template` varchar(10) DEFAULT '0' COMMENT '是否模板(1:是；0不是)',
  `create_by` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '创建人登录名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '更新人登录名称',
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  `low_app_id` varchar(50) DEFAULT NULL COMMENT '应用ID',
  `tenant_id` int(10) DEFAULT NULL COMMENT '租户ID',
  `update_count` int(10) DEFAULT '1',
  `visits_num` int(11) DEFAULT NULL COMMENT '访问次数',
  `del_flag` int(11) DEFAULT NULL COMMENT '删除状态( 0未删除 1已删除)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='可视化拖拽界面';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `onl_drag_page_comp` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `parent_id` varchar(32) DEFAULT NULL COMMENT '父组件ID',
  `page_Id` varchar(50) DEFAULT NULL COMMENT '界面ID',
  `comp_id` varchar(32) DEFAULT NULL COMMENT '组件库ID',
  `component` varchar(50) DEFAULT NULL COMMENT '组件名称',
  `config` longtext COMMENT '组件配置',
  `create_by` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '创建人登录名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '更新人登录名称',
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='可视化拖拽页面组件';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `onl_drag_share` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `drag_id` varchar(32) DEFAULT NULL COMMENT '在线仪表盘设计器id',
  `preview_url` varchar(1000) DEFAULT NULL COMMENT '预览地址',
  `preview_lock` varchar(4) DEFAULT NULL COMMENT '密码锁',
  `last_update_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  `term_of_validity` varchar(1) DEFAULT NULL COMMENT '有效期(0:永久有效，1:1天，7:7天)',
  `status` varchar(1) DEFAULT NULL COMMENT '是否过期(0未过期，1已过期)',
  `preview_lock_status` varchar(1) DEFAULT NULL COMMENT '是否为密码锁(0 否,1是)',
  `share_token` varchar(32) DEFAULT NULL COMMENT '分享token',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_ods_drag_id` (`drag_id`) USING BTREE COMMENT '仪表盘id唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='仪表盘预览分享表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `onl_drag_table_relation` (
  `id` varchar(50) NOT NULL COMMENT '主键',
  `aggregation_name` varchar(100) DEFAULT NULL COMMENT '聚合表名称',
  `aggregation_desc` varchar(100) DEFAULT NULL COMMENT '聚合表描述',
  `relation_forms` longtext COMMENT '关联表单',
  `filter_condition` longtext COMMENT '过滤条件',
  `header_fields` longtext COMMENT '表头字段',
  `calculate_fields` longtext COMMENT '公式字段',
  `validate_info` longtext COMMENT '校验信息',
  `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除状态(0-正常,1-已删除)',
  `low_app_id` varchar(50) DEFAULT NULL COMMENT '应用ID',
  `tenant_id` int(11) DEFAULT NULL COMMENT '租户ID',
  `create_by` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '创建人登录名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '更新人登录名称',
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_aggregation_name` (`aggregation_name`) USING BTREE,
  KEY `idx_del_flag` (`del_flag`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_create_by` (`create_by`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='仪表盘聚合表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `system_home_app_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '应用ID',
  `menu_id` bigint(20) NOT NULL COMMENT '关联菜单ID（来自system_menu表）',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用名称',
  `icon` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '应用图标（支持iconify图标名称或图片URL）',
  `color` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标颜色（十六进制颜色值）',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '应用描述',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序（数字越小越靠前）',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态（0=启用 1=禁用）',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_menu_id` (`menu_id`) USING BTREE COMMENT '菜单ID索引',
  KEY `idx_sort` (`sort`) USING BTREE COMMENT '排序索引',
  KEY `idx_status` (`status`) USING BTREE COMMENT '状态索引'
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统级应用配置表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `system_home_app_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户应用ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `menu_id` bigint(20) NOT NULL COMMENT '关联菜单ID（来自system_menu表）',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '自定义应用名称（为空则使用菜单名称）',
  `icon` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '自定义图标（为空则使用菜单图标）',
  `color` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '自定义图标颜色',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序（数字越小越靠前）',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态（0=显示 1=隐藏）',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_menu` (`user_id`,`menu_id`,`deleted`) USING BTREE COMMENT '用户菜单唯一索引',
  KEY `idx_user_id` (`user_id`) USING BTREE COMMENT '用户ID索引',
  KEY `idx_menu_id` (`menu_id`) USING BTREE COMMENT '菜单ID索引',
  KEY `idx_sort` (`sort`) USING BTREE COMMENT '排序索引'
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户级应用配置表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `system_home_component` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '组件ID',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组件名称',
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组件编码',
  `component_path` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组件路径',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组件描述',
  `preview_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '预览图',
  `default_width` int(11) NOT NULL DEFAULT '12' COMMENT '默认宽度（网格列数1-24）',
  `default_height` int(11) NOT NULL DEFAULT '4' COMMENT '默认高度（网格行数）',
  `config_schema` text COLLATE utf8mb4_unicode_ci COMMENT '配置Schema（JSON格式）',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态（0停用 1启用）',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`,`deleted`) USING BTREE,
  KEY `idx_category_id` (`category_id`,`deleted`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`,`deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='首页组件定义表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `system_home_component_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类编码',
  `icon` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类图标',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`,`deleted`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`,`deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='首页组件分类表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `system_home_page` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '首页ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '首页名称',
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '首页编码',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '首页描述',
  `preview_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '预览图',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认首页',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态（0停用 1启用）',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`,`deleted`) USING BTREE,
  KEY `idx_is_default` (`is_default`,`deleted`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`,`deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='首页配置表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `system_home_page_layout` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '布局ID',
  `page_id` bigint(20) NOT NULL COMMENT '首页ID',
  `component_code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组件编码',
  `position_x` int(11) NOT NULL DEFAULT '0' COMMENT 'X坐标',
  `position_y` int(11) NOT NULL DEFAULT '0' COMMENT 'Y坐标',
  `width` int(11) NOT NULL DEFAULT '6' COMMENT '宽度（栅格数）',
  `height` int(11) NOT NULL DEFAULT '4' COMMENT '高度（栅格数）',
  `config` text COLLATE utf8mb4_unicode_ci COMMENT '组件配置（JSON）',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_page_id` (`page_id`,`deleted`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`,`deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=428 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='首页布局配置表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `system_notice_read` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `notice_id` bigint(20) NOT NULL COMMENT '公告ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `read_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_notice_user` (`notice_id`,`user_id`,`deleted`) USING BTREE COMMENT '公告用户唯一索引',
  KEY `idx_notice_id` (`notice_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户公告已读关系表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `system_schedule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日程ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '内容',
  `schedule_date` date NOT NULL COMMENT '日程日期',
  `start_time` time DEFAULT NULL COMMENT '开始时间',
  `end_time` time DEFAULT NULL COMMENT '结束时间',
  `schedule_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '日程类型（字典：schedule_type）',
  `schedule_category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '日程分类（字典：schedule_category）',
  `creator_id` bigint(20) NOT NULL COMMENT '创建人ID',
  `creator_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人姓名',
  `is_pushed` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否推送（0否 1是）',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态（0开启 1停用）',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_schedule_date` (`schedule_date`,`deleted`) USING BTREE,
  KEY `idx_creator_id` (`creator_id`,`deleted`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`,`deleted`) USING BTREE,
  KEY `idx_status` (`status`,`deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日程管理表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `system_schedule_receiver` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `schedule_id` bigint(20) NOT NULL COMMENT '日程ID',
  `receiver_id` bigint(20) NOT NULL COMMENT '接收人ID',
  `receiver_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接收人姓名',
  `read_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '已读状态（0未读 1已读）',
  `read_time` datetime DEFAULT NULL COMMENT '已读时间',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_schedule_receiver` (`schedule_id`,`receiver_id`,`deleted`) USING BTREE,
  KEY `idx_schedule_id` (`schedule_id`,`deleted`) USING BTREE,
  KEY `idx_receiver_id` (`receiver_id`,`deleted`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`,`deleted`) USING BTREE,
  KEY `idx_read_status` (`read_status`,`deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日程接收人关系表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `system_user_home_page` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `page_id` bigint(20) NOT NULL COMMENT '首页ID',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_id` (`user_id`,`deleted`) USING BTREE,
  KEY `idx_page_id` (`page_id`,`deleted`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`,`deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户首页关联表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `wms_goods_common_operation_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `purchase_order_id` bigint(32) DEFAULT NULL COMMENT '采购订单id',
  `purchase_order_code` varchar(32) DEFAULT NULL COMMENT '采购订单编码',
  `purchase_order_name` varchar(200) DEFAULT NULL COMMENT '采购订单名称',
  `in_warehousing_id` bigint(32) DEFAULT NULL COMMENT '入库单Id',
  `warehousing_entry_code` varchar(32) NOT NULL COMMENT '入库单编码',
  `warehousing_entry_name` varchar(200) DEFAULT NULL COMMENT '入库单名称',
  `common_operation_date` datetime DEFAULT NULL COMMENT '领用、退库、归还、借用日期',
  `common_operation_desc` varchar(500) DEFAULT NULL COMMENT '领用、退库、归还、借用库说明',
  `common_operation_file` varchar(200) DEFAULT NULL COMMENT '领用、退库、归还、借用附件',
  `stock_return_total_data` decimal(24,6) DEFAULT NULL COMMENT '采购物品合计',
  `common_operation_total_amount` decimal(24,6) DEFAULT NULL COMMENT '入库物品金额合计',
  `company_id` varchar(255) DEFAULT NULL COMMENT '公司id',
  `company_name` varchar(255) DEFAULT NULL COMMENT '公司名称',
  `dept_id` varchar(32) DEFAULT NULL COMMENT '领用、退库、归还、借用部门编码',
  `dept_name` varchar(200) DEFAULT NULL COMMENT '领用、退库、归还、借用部门名称',
  `user_id` varchar(32) DEFAULT NULL COMMENT '领用、退库、归还、借用人编码',
  `user_name` varchar(64) DEFAULT NULL COMMENT '领用、退库、归还、借用人名称',
  `expect_return_date` datetime DEFAULT NULL COMMENT '预计归还日期',
  `transfer_company_id` varchar(255) DEFAULT NULL COMMENT '调入公司id',
  `transfer_company_name` varchar(255) DEFAULT NULL COMMENT '调入公司名称',
  `transfer_dept_id` varchar(32) DEFAULT NULL COMMENT '调入部门编码',
  `transfer_dept_name` varchar(200) DEFAULT NULL COMMENT '调入部门名称',
  `process_instance_id` varchar(64) DEFAULT NULL COMMENT '流程实例编号',
  `process_status` tinyint(4) DEFAULT NULL COMMENT '单据状态',
  `sort` int(11) DEFAULT '0' COMMENT '显示顺序',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='领用、退库、归还、借用、调拨主表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `wms_goods_warehousing_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `purchase_order_id` bigint(32) DEFAULT NULL COMMENT '采购订单id',
  `purchase_order_code` varchar(100) DEFAULT NULL COMMENT '关联采购单编码',
  `purchase_order_name` varchar(200) DEFAULT NULL COMMENT '采购订单名称',
  `common_operation_id` bigint(32) DEFAULT NULL COMMENT '公共操作Id',
  `common_operation_code` varchar(32) DEFAULT NULL COMMENT '公共操作编码',
  `common_operation_name` varchar(200) DEFAULT NULL COMMENT '公共操作名称',
  `common_operation_type` varchar(4) DEFAULT NULL COMMENT '公共操作类型',
  `asset_id` bigint(32) DEFAULT NULL COMMENT '资产ID',
  `asset_code` varchar(32) DEFAULT NULL COMMENT '资产编码',
  `asset_name` varchar(200) DEFAULT NULL COMMENT '资产名称',
  `asset_category_code` varchar(36) DEFAULT NULL COMMENT '资产类型编码',
  `asset_category_name` varchar(100) DEFAULT NULL COMMENT '资产类型名称',
  `asset_model` varchar(100) DEFAULT NULL COMMENT '规格型号',
  `asset_unit` varchar(100) DEFAULT NULL COMMENT '计量单位',
  `manufacturer` varchar(100) DEFAULT NULL COMMENT '厂商',
  `brand` varchar(120) DEFAULT NULL COMMENT '品牌',
  `serial_number` varchar(100) DEFAULT NULL COMMENT '序列号',
  `asset_status_code` varchar(36) DEFAULT NULL COMMENT '资产状态编码',
  `asset_status_name` varchar(64) DEFAULT NULL COMMENT '资产状态名称',
  `asset_source_code` varchar(36) DEFAULT NULL COMMENT '资产来源编码',
  `asset_source_name` varchar(64) DEFAULT NULL COMMENT '资料来源名称',
  `purchase_date` datetime DEFAULT NULL COMMENT '购买日期',
  `purchase_price` decimal(24,6) DEFAULT NULL COMMENT '购买价格',
  `date_of_production` datetime DEFAULT NULL COMMENT '出场日期',
  `admin_company_id` varchar(255) DEFAULT NULL COMMENT '管理公司id',
  `admin_company_name` varchar(255) DEFAULT NULL COMMENT '管理公司名称',
  `admin_dept_id` varchar(32) DEFAULT NULL COMMENT '管理部门编码',
  `admin_dept_name` varchar(100) DEFAULT NULL COMMENT '管理部门名称',
  `admin_manager_id` varchar(32) DEFAULT NULL COMMENT '管理人员编码',
  `admin_manager_name` varchar(64) DEFAULT NULL COMMENT '管理人员名称',
  `wms_store_code` varchar(36) DEFAULT NULL COMMENT '存放仓库编码',
  `wms_store_name` varchar(100) DEFAULT NULL COMMENT '存放仓库名称',
  `residual_value_rate` decimal(12,4) DEFAULT NULL COMMENT '残值率',
  `use_company_id` varchar(255) DEFAULT NULL COMMENT '使用公司id',
  `use_company_name` varchar(255) DEFAULT NULL COMMENT '使用公司名称',
  `use_dept_id` varchar(32) DEFAULT NULL COMMENT '使用部门编码',
  `use_dept_name` varchar(100) DEFAULT NULL COMMENT '使用部门名称',
  `use_account_id` varchar(32) DEFAULT NULL COMMENT '使用人员编码',
  `use_account_name` varchar(64) DEFAULT NULL COMMENT '使用人员名称',
  `transfer_use_dept_id` varchar(32) DEFAULT NULL COMMENT '调入部门编码',
  `transfer_use_dept_name` varchar(200) DEFAULT NULL COMMENT '调入部门名称',
  `transfer_use_account_id` varchar(32) DEFAULT NULL COMMENT '调入部门使用人员编码',
  `transfer_use_account_name` varchar(64) DEFAULT NULL COMMENT '调入使用人员名称',
  `asset_icon` varchar(200) DEFAULT NULL COMMENT '资产图片',
  `asset_file` varchar(200) DEFAULT NULL COMMENT '资产附件',
  `is_join_asset` varchar(1) DEFAULT NULL COMMENT '是否进入资产列表',
  `sort` int(11) DEFAULT '0' COMMENT '显示顺序',
  `status` tinyint(4) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `store_address` varchar(300) DEFAULT NULL COMMENT '仓库地址',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购入库、领用、退库、归还、借用、调拨明细表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `wms_purchase_in_warehousing` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `purchase_order_id` bigint(32) DEFAULT NULL COMMENT '采购订单id',
  `purchase_order_code` varchar(32) DEFAULT NULL COMMENT '采购订单编码',
  `purchase_order_name` varchar(200) DEFAULT NULL COMMENT '采购订单名称',
  `warehousing_entry_code` varchar(32) NOT NULL COMMENT '入库单编码',
  `warehousing_entry_name` varchar(200) DEFAULT NULL COMMENT '入库单名称',
  `in_warehousing_date` datetime DEFAULT NULL COMMENT '入库日期',
  `in_warehousing_desc` varchar(500) DEFAULT NULL COMMENT '入库说明',
  `in_warehousing_file` varchar(200) DEFAULT NULL COMMENT '附件',
  `in_warehousing_total_data` decimal(24,6) DEFAULT NULL COMMENT '采购物品合计',
  `in_warehousing_total_amount` decimal(24,6) DEFAULT NULL COMMENT '入库物品金额合计',
  `sort` int(11) DEFAULT '0' COMMENT '显示顺序',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购入库表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `wms_purchase_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `purchase_order_code` varchar(32) NOT NULL COMMENT '采购订单编码',
  `purchase_order_name` varchar(200) DEFAULT NULL COMMENT '采购订单名称',
  `applicant_date` datetime DEFAULT NULL COMMENT '申请日期',
  `company_id` varchar(255) DEFAULT NULL COMMENT '公司id',
  `company_name` varchar(255) DEFAULT NULL COMMENT '公司名称',
  `applicant_dept_id` varchar(32) DEFAULT NULL COMMENT '申请部门编码',
  `applicant_dept_name` varchar(200) DEFAULT NULL COMMENT '申请部门名称',
  `applicant_user_id` varchar(32) DEFAULT NULL COMMENT '申请人编码',
  `applicant_user_name` varchar(64) DEFAULT NULL COMMENT '申请人名称',
  `supplier_code` varchar(32) DEFAULT NULL COMMENT '供应商编码',
  `supplier_name` varchar(200) DEFAULT NULL COMMENT '供应商名称',
  `applicant_desc` varchar(500) DEFAULT NULL COMMENT '申请说明',
  `purchase_order_file` varchar(200) DEFAULT NULL COMMENT '附件',
  `purchase_order_total_data` decimal(24,6) DEFAULT NULL COMMENT '采购物品合计',
  `purchase_order_total_amount` decimal(24,6) DEFAULT NULL COMMENT '采购物品金额合计',
  `process_instance_id` varchar(64) DEFAULT NULL COMMENT '流程实例编号',
  `process_status` tinyint(4) DEFAULT NULL COMMENT '单据状态',
  `sort` int(11) DEFAULT '0' COMMENT '显示顺序',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='采购订单表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `wms_purchase_order_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `purchase_order_id` varchar(255) DEFAULT NULL COMMENT '采购订单id',
  `purchase_order_code` varchar(32) DEFAULT NULL COMMENT '采购订单编码',
  `purchase_order_name` varchar(200) DEFAULT NULL COMMENT '采购订单名称',
  `category_code` varchar(32) DEFAULT NULL COMMENT '资产类别编码',
  `category_name` varchar(200) DEFAULT NULL COMMENT '资产类别名称',
  `goods_code` varchar(32) DEFAULT NULL COMMENT '物品名称编码',
  `goods_name` varchar(200) DEFAULT NULL COMMENT '物品名称名称',
  `purchase_price` decimal(24,6) DEFAULT NULL COMMENT '采购单据',
  `purchase_num` decimal(24,6) DEFAULT NULL COMMENT '采购数量',
  `purchase_total_amonut` decimal(24,6) DEFAULT NULL COMMENT '采购总价',
  `sort` int(11) DEFAULT '0' COMMENT '显示顺序',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COMMENT='采购订单明细表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `wms_warehousing` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `warehousing_code` varchar(32) NOT NULL COMMENT '仓库编码',
  `warehousing_name` varchar(200) DEFAULT NULL COMMENT '仓库名称',
  `parent_id` bigint(20) NOT NULL COMMENT '上级id',
  `level` bigint(20) DEFAULT NULL COMMENT '级别',
  `warehousing_category_code` varchar(36) DEFAULT NULL COMMENT '仓库类型编码',
  `warehousing_category_name` varchar(100) DEFAULT NULL COMMENT '仓库类型名称',
  `warehousing_address` varchar(300) DEFAULT NULL COMMENT '仓库地址',
  `company_id` varchar(255) DEFAULT NULL COMMENT '公司id',
  `company_name` varchar(255) DEFAULT NULL COMMENT '公司名称',
  `sort` int(11) DEFAULT '0' COMMENT '显示顺序',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='仓库信息';
/*!40101 SET character_set_client = @saved_cs_client */;
