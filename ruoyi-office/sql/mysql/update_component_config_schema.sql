-- ============================================
-- 更新首页组件配置Schema，添加标题、图标等通用配置
-- ============================================

-- 更新访问统计组件
UPDATE system_home_component 
SET config_schema = '{
  "properties": [
    {
      "key": "showTitle",
      "label": "显示标题",
      "type": "boolean",
      "default": true,
      "required": false
    },
    {
      "key": "title",
      "label": "标题",
      "type": "string",
      "default": "访问统计",
      "required": true
    },
    {
      "key": "titleIcon",
      "label": "标题图标",
      "type": "icon",
      "default": "lucide:bar-chart-2",
      "required": false
    },
    {
      "key": "titleIconColor",
      "label": "图标颜色",
      "type": "color",
      "default": "#1890ff",
      "required": false
    }
  ]
}'
WHERE code = 'analytics_visits';

-- 更新访问数据组件
UPDATE system_home_component 
SET config_schema = '{
  "properties": [
    {
      "key": "showTitle",
      "label": "显示标题",
      "type": "boolean",
      "default": true,
      "required": false
    },
    {
      "key": "title",
      "label": "标题",
      "type": "string",
      "default": "访问数据",
      "required": true
    },
    {
      "key": "titleIcon",
      "label": "标题图标",
      "type": "icon",
      "default": "lucide:trending-up",
      "required": false
    },
    {
      "key": "titleIconColor",
      "label": "图标颜色",
      "type": "color",
      "default": "#52c41a",
      "required": false
    }
  ]
}'
WHERE code = 'analytics_visits_data';

-- 更新访问来源图表组件
UPDATE system_home_component 
SET config_schema = '{
  "properties": [
    {
      "key": "showTitle",
      "label": "显示标题",
      "type": "boolean",
      "default": true,
      "required": false
    },
    {
      "key": "title",
      "label": "标题",
      "type": "string",
      "default": "访问来源",
      "required": true
    },
    {
      "key": "titleIcon",
      "label": "标题图标",
      "type": "icon",
      "default": "lucide:pie-chart",
      "required": false
    },
    {
      "key": "titleIconColor",
      "label": "图标颜色",
      "type": "color",
      "default": "#fa8c16",
      "required": false
    }
  ]
}'
WHERE code = 'analytics_visits_source';

-- 更新项目列表组件
UPDATE system_home_component 
SET config_schema = '{
  "properties": [
    {
      "key": "showTitle",
      "label": "显示标题",
      "type": "boolean",
      "default": true,
      "required": false
    },
    {
      "key": "title",
      "label": "标题",
      "type": "string",
      "default": "项目",
      "required": true
    },
    {
      "key": "titleIcon",
      "label": "标题图标",
      "type": "icon",
      "default": "lucide:folder",
      "required": false
    },
    {
      "key": "titleIconColor",
      "label": "图标颜色",
      "type": "color",
      "default": "#722ed1",
      "required": false
    }
  ]
}'
WHERE code = 'workbench_project';

-- 更新动态列表组件
UPDATE system_home_component 
SET config_schema = '{
  "properties": [
    {
      "key": "showTitle",
      "label": "显示标题",
      "type": "boolean",
      "default": true,
      "required": false
    },
    {
      "key": "title",
      "label": "标题",
      "type": "string",
      "default": "最新动态",
      "required": true
    },
    {
      "key": "titleIcon",
      "label": "标题图标",
      "type": "icon",
      "default": "lucide:activity",
      "required": false
    },
    {
      "key": "titleIconColor",
      "label": "图标颜色",
      "type": "color",
      "default": "#eb2f96",
      "required": false
    }
  ]
}'
WHERE code = 'workbench_trends';

-- 更新快捷导航组件
UPDATE system_home_component 
SET config_schema = '{
  "properties": [
    {
      "key": "showTitle",
      "label": "显示标题",
      "type": "boolean",
      "default": true,
      "required": false
    },
    {
      "key": "title",
      "label": "标题",
      "type": "string",
      "default": "快捷导航",
      "required": true
    },
    {
      "key": "titleIcon",
      "label": "标题图标",
      "type": "icon",
      "default": "lucide:compass",
      "required": false
    },
    {
      "key": "titleIconColor",
      "label": "图标颜色",
      "type": "color",
      "default": "#13c2c2",
      "required": false
    }
  ]
}'
WHERE code = 'workbench_quick_nav';

-- 验证更新结果
SELECT 
  id,
  name,
  code,
  LEFT(config_schema, 100) AS schema_preview
FROM system_home_component
WHERE deleted = 0 AND status = 1
ORDER BY category_id, sort;
