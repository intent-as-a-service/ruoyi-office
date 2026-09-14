-- ============================================
-- 更新组件默认宽度，适配12列布局
-- 原来24列 -> 现在12列，所有宽度除以2
-- ============================================

-- 访问统计：6x2 -> 3x2
UPDATE system_home_component 
SET default_width = 3, default_height = 2
WHERE code = 'analytics_visits';

-- 访问数据：18x2 -> 9x2
UPDATE system_home_component 
SET default_width = 9, default_height = 2
WHERE code = 'analytics_visits_data';

-- 访问来源图表：12x4 -> 6x4
UPDATE system_home_component 
SET default_width = 6, default_height = 4
WHERE code = 'analytics_visits_source';

-- 项目列表：12x6 -> 6x6
UPDATE system_home_component 
SET default_width = 6, default_height = 6
WHERE code = 'workbench_project';

-- 动态列表：12x6 -> 6x6
UPDATE system_home_component 
SET default_width = 6, default_height = 6
WHERE code = 'workbench_trends';

-- 快捷导航：12x3 -> 6x3
UPDATE system_home_component 
SET default_width = 6, default_height = 3
WHERE code = 'workbench_quick_nav';

-- 验证更新结果
SELECT 
  id,
  name,
  code,
  default_width AS width,
  default_height AS height,
  CONCAT(default_width, 'x', default_height) AS size
FROM system_home_component
WHERE deleted = 0 AND status = 1
ORDER BY category_id, sort;
