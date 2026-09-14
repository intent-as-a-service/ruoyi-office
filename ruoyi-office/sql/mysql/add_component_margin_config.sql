-- =============================================
-- 为所有首页组件添加外边距配置支持
-- 执行时间：2026-01-02
-- 说明：添加组件级别的外边距配置（margin），用于控制组件之间的间距
-- =============================================

-- 注意：此脚本需要在 add_component_padding_config.sql 之后执行
-- 因为我们需要在现有的 config_schema 基础上添加 margin 配置项

-- 1. 访问统计组件
UPDATE `system_home_component`
SET `config_schema` = JSON_SET(
  `config_schema`,
  '$.properties', JSON_ARRAY(
    JSON_OBJECT('key', 'showTitle', 'label', '显示标题', 'type', 'boolean', 'default', true, 'required', false),
    JSON_OBJECT('key', 'title', 'label', '标题文本', 'type', 'string', 'default', '访问统计', 'required', false),
    JSON_OBJECT('key', 'paddingTop', 'label', '上内边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'paddingRight', 'label', '右内边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'paddingBottom', 'label', '下内边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'paddingLeft', 'label', '左内边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'marginTop', 'label', '上外边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'marginRight', 'label', '右外边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'marginBottom', 'label', '下外边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'marginLeft', 'label', '左外边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50)
  )
)
WHERE `code` = 'analytics_visits';

-- 2. 访问来源组件
UPDATE `system_home_component`
SET `config_schema` = JSON_SET(
  `config_schema`,
  '$.properties', JSON_ARRAY(
    JSON_OBJECT('key', 'showTitle', 'label', '显示标题', 'type', 'boolean', 'default', true, 'required', false),
    JSON_OBJECT('key', 'title', 'label', '标题文本', 'type', 'string', 'default', '访问来源', 'required', false),
    JSON_OBJECT('key', 'paddingTop', 'label', '上内边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'paddingRight', 'label', '右内边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'paddingBottom', 'label', '下内边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'paddingLeft', 'label', '左内边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'marginTop', 'label', '上外边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'marginRight', 'label', '右外边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'marginBottom', 'label', '下外边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'marginLeft', 'label', '左外边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50)
  )
)
WHERE `code` = 'analytics_visits_source';

-- 3. 项目列表组件
UPDATE `system_home_component`
SET `config_schema` = JSON_SET(
  `config_schema`,
  '$.properties', JSON_ARRAY(
    JSON_OBJECT('key', 'showTitle', 'label', '显示标题', 'type', 'boolean', 'default', true, 'required', false),
    JSON_OBJECT('key', 'title', 'label', '标题文本', 'type', 'string', 'default', '项目', 'required', false),
    JSON_OBJECT('key', 'paddingTop', 'label', '上内边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'paddingRight', 'label', '右内边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'paddingBottom', 'label', '下内边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'paddingLeft', 'label', '左内边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'marginTop', 'label', '上外边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'marginRight', 'label', '右外边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'marginBottom', 'label', '下外边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'marginLeft', 'label', '左外边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50)
  )
)
WHERE `code` = 'workbench_project';

-- 4. 快捷导航组件
UPDATE `system_home_component`
SET `config_schema` = JSON_SET(
  `config_schema`,
  '$.properties', JSON_ARRAY(
    JSON_OBJECT('key', 'showTitle', 'label', '显示标题', 'type', 'boolean', 'default', true, 'required', false),
    JSON_OBJECT('key', 'title', 'label', '标题文本', 'type', 'string', 'default', '快捷导航', 'required', false),
    JSON_OBJECT('key', 'paddingTop', 'label', '上内边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'paddingRight', 'label', '右内边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'paddingBottom', 'label', '下内边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'paddingLeft', 'label', '左内边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'marginTop', 'label', '上外边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'marginRight', 'label', '右外边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'marginBottom', 'label', '下外边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50),
    JSON_OBJECT('key', 'marginLeft', 'label', '左外边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 50)
  )
)
WHERE `code` = 'workbench_quick_nav';

-- 验证更新结果
SELECT 
  `code`, 
  `name`, 
  JSON_PRETTY(`config_schema`) as config_schema
FROM `system_home_component`
WHERE `code` IN ('analytics_visits', 'analytics_visits_source', 'workbench_project', 'workbench_quick_nav')
ORDER BY `sort`;
