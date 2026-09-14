-- =============================================
-- 完整更新所有首页组件的配置Schema
-- 执行时间：2026-01-02
-- 说明：包含所有配置项：基础配置、内边距、浮动标题等
-- =============================================

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
    JSON_OBJECT('key', 'floatingTitle', 'label', '浮动标题', 'type', 'boolean', 'default', false, 'required', false),
    JSON_OBJECT('key', 'titleMarginTop', 'label', '标题上边距(px)', 'type', 'number', 'default', 8, 'required', false, 'min', 0, 'max', 100),
    JSON_OBJECT('key', 'titleMarginRight', 'label', '标题右边距(px)', 'type', 'number', 'default', 8, 'required', false, 'min', 0, 'max', 100),
    JSON_OBJECT('key', 'titleMarginBottom', 'label', '标题下边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 100),
    JSON_OBJECT('key', 'titleMarginLeft', 'label', '标题左边距(px)', 'type', 'number', 'default', 8, 'required', false, 'min', 0, 'max', 100),
    JSON_OBJECT('key', 'titleFontSize', 'label', '标题文字大小(px)', 'type', 'number', 'default', 12, 'required', false, 'min', 10, 'max', 24),
    JSON_OBJECT('key', 'titleBold', 'label', '标题文字加粗', 'type', 'boolean', 'default', false, 'required', false),
    JSON_OBJECT('key', 'titleColor', 'label', '标题文字颜色', 'type', 'string', 'default', '#4B5563', 'required', false)
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
    JSON_OBJECT('key', 'floatingTitle', 'label', '浮动标题', 'type', 'boolean', 'default', false, 'required', false),
    JSON_OBJECT('key', 'titleMarginTop', 'label', '标题上边距(px)', 'type', 'number', 'default', 8, 'required', false, 'min', 0, 'max', 100),
    JSON_OBJECT('key', 'titleMarginRight', 'label', '标题右边距(px)', 'type', 'number', 'default', 8, 'required', false, 'min', 0, 'max', 100),
    JSON_OBJECT('key', 'titleMarginBottom', 'label', '标题下边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 100),
    JSON_OBJECT('key', 'titleMarginLeft', 'label', '标题左边距(px)', 'type', 'number', 'default', 8, 'required', false, 'min', 0, 'max', 100),
    JSON_OBJECT('key', 'titleFontSize', 'label', '标题文字大小(px)', 'type', 'number', 'default', 12, 'required', false, 'min', 10, 'max', 24),
    JSON_OBJECT('key', 'titleBold', 'label', '标题文字加粗', 'type', 'boolean', 'default', false, 'required', false),
    JSON_OBJECT('key', 'titleColor', 'label', '标题文字颜色', 'type', 'string', 'default', '#4B5563', 'required', false)
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
    JSON_OBJECT('key', 'floatingTitle', 'label', '浮动标题', 'type', 'boolean', 'default', false, 'required', false),
    JSON_OBJECT('key', 'titleMarginTop', 'label', '标题上边距(px)', 'type', 'number', 'default', 8, 'required', false, 'min', 0, 'max', 100),
    JSON_OBJECT('key', 'titleMarginRight', 'label', '标题右边距(px)', 'type', 'number', 'default', 8, 'required', false, 'min', 0, 'max', 100),
    JSON_OBJECT('key', 'titleMarginBottom', 'label', '标题下边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 100),
    JSON_OBJECT('key', 'titleMarginLeft', 'label', '标题左边距(px)', 'type', 'number', 'default', 8, 'required', false, 'min', 0, 'max', 100),
    JSON_OBJECT('key', 'titleFontSize', 'label', '标题文字大小(px)', 'type', 'number', 'default', 12, 'required', false, 'min', 10, 'max', 24),
    JSON_OBJECT('key', 'titleBold', 'label', '标题文字加粗', 'type', 'boolean', 'default', false, 'required', false),
    JSON_OBJECT('key', 'titleColor', 'label', '标题文字颜色', 'type', 'string', 'default', '#4B5563', 'required', false)
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
    JSON_OBJECT('key', 'floatingTitle', 'label', '浮动标题', 'type', 'boolean', 'default', false, 'required', false),
    JSON_OBJECT('key', 'titleMarginTop', 'label', '标题上边距(px)', 'type', 'number', 'default', 8, 'required', false, 'min', 0, 'max', 100),
    JSON_OBJECT('key', 'titleMarginRight', 'label', '标题右边距(px)', 'type', 'number', 'default', 8, 'required', false, 'min', 0, 'max', 100),
    JSON_OBJECT('key', 'titleMarginBottom', 'label', '标题下边距(px)', 'type', 'number', 'default', 0, 'required', false, 'min', 0, 'max', 100),
    JSON_OBJECT('key', 'titleMarginLeft', 'label', '标题左边距(px)', 'type', 'number', 'default', 8, 'required', false, 'min', 0, 'max', 100),
    JSON_OBJECT('key', 'titleFontSize', 'label', '标题文字大小(px)', 'type', 'number', 'default', 12, 'required', false, 'min', 10, 'max', 24),
    JSON_OBJECT('key', 'titleBold', 'label', '标题文字加粗', 'type', 'boolean', 'default', false, 'required', false),
    JSON_OBJECT('key', 'titleColor', 'label', '标题文字颜色', 'type', 'string', 'default', '#4B5563', 'required', false)
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
