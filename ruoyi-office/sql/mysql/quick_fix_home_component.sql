-- ============================================
-- 快速修复：首页组件数据初始化
-- 执行前提：表结构已存在
-- ============================================

-- 清理可能存在的脏数据（谨慎使用）
-- DELETE FROM system_home_component WHERE 1=1;
-- DELETE FROM system_home_component_category WHERE 1=1;

-- 初始化分类数据（如果已存在会报错，忽略即可）
INSERT INTO `system_home_component_category` (`name`, `code`, `icon`, `sort`, `creator`) VALUES
('统计卡片', 'statistics', 'lucide:bar-chart-2', 1, '1'),
('图表组件', 'chart', 'lucide:pie-chart', 2, '1'),
('列表组件', 'list', 'lucide:list', 3, '1'),
('快捷导航', 'navigation', 'lucide:compass', 4, '1'),
('其他组件', 'other', 'lucide:box', 99, '1');

-- 获取分类ID（根据实际情况调整）
SET @stat_id = (SELECT id FROM system_home_component_category WHERE code = 'statistics' LIMIT 1);
SET @chart_id = (SELECT id FROM system_home_component_category WHERE code = 'chart' LIMIT 1);
SET @list_id = (SELECT id FROM system_home_component_category WHERE code = 'list' LIMIT 1);
SET @nav_id = (SELECT id FROM system_home_component_category WHERE code = 'navigation' LIMIT 1);

-- 初始化组件数据（使用变量保证category_id正确）
INSERT INTO `system_home_component` (
  `category_id`, `name`, `code`, `component_path`, `description`,
  `default_width`, `default_height`, `config_schema`, `status`, `sort`, `creator`
) VALUES
-- 统计卡片类
(@stat_id, '访问统计', 'analytics_visits',
  'dashboard/home/components/statistics/analytics-visits.vue',
  '展示网站访问数据统计卡片', 6, 2,
  '{"properties":[{"key":"title","label":"标题","type":"string","default":"访问统计","required":true}]}',
  1, 1, '1'),
(@stat_id, '访问数据', 'analytics_visits_data',
  'dashboard/home/components/statistics/analytics-visits-data.vue',
  '展示访问数据统计', 18, 2,
  '{"properties":[{"key":"title","label":"标题","type":"string","default":"访问数据","required":true}]}',
  1, 2, '1'),

-- 图表组件类
(@chart_id, '访问来源图表', 'analytics_visits_source',
  'dashboard/home/components/charts/analytics-visits-source.vue',
  '展示访问来源的饼图分析', 12, 4,
  '{"properties":[{"key":"title","label":"标题","type":"string","default":"访问来源","required":true}]}',
  1, 1, '1'),

-- 列表组件类
(@list_id, '项目列表', 'workbench_project',
  'dashboard/home/components/lists/workbench-project.vue',
  '展示项目卡片列表', 12, 6,
  '{"properties":[{"key":"title","label":"标题","type":"string","default":"项目","required":true}]}',
  1, 1, '1'),
(@list_id, '动态列表', 'workbench_trends',
  'dashboard/home/components/lists/workbench-trends.vue',
  '展示最新动态列表', 12, 6,
  '{"properties":[{"key":"title","label":"标题","type":"string","default":"最新动态","required":true}]}',
  1, 2, '1'),

-- 快捷导航类
(@nav_id, '快捷导航', 'workbench_quick_nav',
  'dashboard/home/components/navigation/workbench-quick-nav.vue',
  '展示快捷导航入口', 12, 3,
  '{"properties":[{"key":"title","label":"标题","type":"string","default":"快捷导航","required":true}]}',
  1, 1, '1');

-- 验证结果
SELECT 
    cat.name AS '分类名称',
    COUNT(c.id) AS '组件数量',
    GROUP_CONCAT(c.name SEPARATOR ', ') AS '组件列表'
FROM system_home_component_category cat
LEFT JOIN system_home_component c ON cat.id = c.category_id 
    AND c.deleted = 0 
    AND c.status = 1
WHERE cat.deleted = 0
GROUP BY cat.id, cat.name
ORDER BY cat.sort;
