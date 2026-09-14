-- ============================================
-- 首页组件数据诊断和修复脚本
-- ============================================

-- 1. 检查组件分类表
SELECT '=== 检查组件分类表 ===' AS step;
SELECT 
    id, 
    name, 
    code, 
    sort,
    CASE WHEN deleted = 0 THEN '正常' ELSE '已删除' END AS status
FROM system_home_component_category
ORDER BY sort;

-- 2. 检查组件表
SELECT '=== 检查组件表 ===' AS step;
SELECT 
    c.id,
    c.name AS component_name,
    c.code AS component_code,
    c.category_id,
    cat.name AS category_name,
    c.status,
    CASE WHEN c.deleted = 0 THEN '正常' ELSE '已删除' END AS deleted_status
FROM system_home_component c
LEFT JOIN system_home_component_category cat ON c.category_id = cat.id
ORDER BY c.category_id, c.sort;

-- 3. 统计每个分类的组件数量
SELECT '=== 每个分类的组件数量 ===' AS step;
SELECT 
    cat.id AS category_id,
    cat.name AS category_name,
    COUNT(c.id) AS component_count
FROM system_home_component_category cat
LEFT JOIN system_home_component c ON cat.id = c.category_id AND c.deleted = 0 AND c.status = 1
WHERE cat.deleted = 0
GROUP BY cat.id, cat.name
ORDER BY cat.sort;

-- ============================================
-- 如果上面的查询结果显示数据为空，执行下面的修复脚本
-- ============================================

-- 4. 清理并重新初始化数据（可选，需要手动执行）
-- 注意：这会删除现有数据，请谨慎操作！

/*
-- 清理现有数据
DELETE FROM system_home_component WHERE 1=1;
DELETE FROM system_home_component_category WHERE 1=1;

-- 重新初始化分类数据
INSERT INTO `system_home_component_category` (`name`, `code`, `icon`, `sort`, `creator`) VALUES
('统计卡片', 'statistics', 'lucide:bar-chart-2', 1, '1'),
('图表组件', 'chart', 'lucide:pie-chart', 2, '1'),
('列表组件', 'list', 'lucide:list', 3, '1'),
('快捷导航', 'navigation', 'lucide:compass', 4, '1'),
('其他组件', 'other', 'lucide:box', 99, '1');

-- 重新初始化组件数据
-- 1. 访问统计卡片
INSERT INTO `system_home_component` (
  `category_id`, `name`, `code`, `component_path`, `description`,
  `default_width`, `default_height`, `config_schema`, `status`, `sort`, `creator`
) VALUES (
  1, '访问统计', 'analytics_visits',
  'dashboard/home/components/statistics/analytics-visits.vue',
  '展示网站访问数据统计卡片',
  6, 2,
  '{"properties":[{"key":"title","label":"标题","type":"string","default":"访问统计","required":true}]}',
  1, 1, '1'
);

-- 2. 访问来源图表
INSERT INTO `system_home_component` (
  `category_id`, `name`, `code`, `component_path`, `description`,
  `default_width`, `default_height`, `config_schema`, `status`, `sort`, `creator`
) VALUES (
  2, '访问来源图表', 'analytics_visits_source',
  'dashboard/home/components/charts/analytics-visits-source.vue',
  '展示访问来源的饼图分析',
  12, 4,
  '{"properties":[{"key":"title","label":"标题","type":"string","default":"访问来源","required":true}]}',
  1, 1, '1'
);

-- 3. 项目列表
INSERT INTO `system_home_component` (
  `category_id`, `name`, `code`, `component_path`, `description`,
  `default_width`, `default_height`, `config_schema`, `status`, `sort`, `creator`
) VALUES (
  3, '项目列表', 'workbench_project',
  'dashboard/home/components/lists/workbench-project.vue',
  '展示项目卡片列表',
  12, 6,
  '{"properties":[{"key":"title","label":"标题","type":"string","default":"项目","required":true}]}',
  1, 1, '1'
);

-- 4. 快捷导航
INSERT INTO `system_home_component` (
  `category_id`, `name`, `code`, `component_path`, `description`,
  `default_width`, `default_height`, `config_schema`, `status`, `sort`, `creator`
) VALUES (
  4, '快捷导航', 'workbench_quick_nav',
  'dashboard/home/components/navigation/workbench-quick-nav.vue',
  '展示快捷导航入口',
  12, 3,
  '{"properties":[{"key":"title","label":"标题","type":"string","default":"快捷导航","required":true}]}',
  1, 1, '1'
);

-- 5. 动态列表
INSERT INTO `system_home_component` (
  `category_id`, `name`, `code`, `component_path`, `description`,
  `default_width`, `default_height`, `config_schema`, `status`, `sort`, `creator`
) VALUES (
  3, '动态列表', 'workbench_trends',
  'dashboard/home/components/lists/workbench-trends.vue',
  '展示最新动态列表',
  12, 6,
  '{"properties":[{"key":"title","label":"标题","type":"string","default":"最新动态","required":true}]}',
  1, 2, '1'
);

-- 6. 访问数据卡片
INSERT INTO `system_home_component` (
  `category_id`, `name`, `code`, `component_path`, `description`,
  `default_width`, `default_height`, `config_schema`, `status`, `sort`, `creator`
) VALUES (
  1, '访问数据', 'analytics_visits_data',
  'dashboard/home/components/statistics/analytics-visits-data.vue',
  '展示访问数据统计',
  18, 2,
  '{"properties":[{"key":"title","label":"标题","type":"string","default":"访问数据","required":true}]}',
  1, 2, '1'
);

-- 验证数据
SELECT '=== 验证修复结果 ===' AS step;
SELECT 
    cat.name AS category_name,
    COUNT(c.id) AS component_count
FROM system_home_component_category cat
LEFT JOIN system_home_component c ON cat.id = c.category_id AND c.deleted = 0 AND c.status = 1
WHERE cat.deleted = 0
GROUP BY cat.id, cat.name
ORDER BY cat.sort;
*/
