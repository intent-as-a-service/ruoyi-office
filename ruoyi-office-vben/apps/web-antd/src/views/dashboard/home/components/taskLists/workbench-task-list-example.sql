-- =============================================
-- 任务列表组件示例配置SQL
-- 说明：用于在首页中添加任务列表组件的示例配置
-- =============================================

-- 注意：此SQL仅作为示例，实际使用时请通过首页设计器界面进行配置
-- 执行前请确认首页ID，可通过以下SQL查询：
-- SELECT id, name FROM system_home WHERE deleted = 0;

-- 假设首页ID为1，请根据实际情况修改
SET @home_page_id = 1;

-- 添加任务列表组件到首页布局
-- 建议放置在首页中上部，占据较大区域以便显示足够的任务信息
INSERT INTO `system_home_layout` (
  `home_id`,          -- 首页ID
  `component_code`,   -- 组件编码
  `position_x`,       -- X轴位置（0-23列）
  `position_y`,       -- Y轴位置（第几行）
  `width`,            -- 宽度（占用列数）
  `height`,           -- 高度（占用行数）
  `config`,           -- 组件配置（JSON格式）
  `creator`,
  `create_time`,
  `updater`,
  `update_time`,
  `deleted`
) VALUES (
  @home_page_id,                     -- 首页ID
  'workbench_task_list',             -- 任务列表组件编码
  0,                                 -- X=0，从最左侧开始
  0,                                 -- Y=0，第一行
  24,                                -- 宽度=24列，占满整行
  8,                                 -- 高度=8行（480px）
  '{
    "maxRecordNum": 10,
    "paddingTop": 16,
    "paddingRight": 16,
    "paddingBottom": 16,
    "paddingLeft": 16
  }',                                -- 组件配置
  'admin',
  NOW(),
  'admin',
  NOW(),
  b'0'
);

-- =============================================
-- 其他布局示例
-- =============================================

-- 示例1：任务列表占左侧，右侧放置其他组件
-- 任务列表：左侧，占16列
INSERT INTO `system_home_layout` (
  `home_id`, `component_code`, `position_x`, `position_y`, `width`, `height`, `config`,
  `creator`, `create_time`, `updater`, `update_time`, `deleted`
) VALUES (
  @home_page_id, 'workbench_task_list', 0, 0, 16, 8,
  '{"maxRecordNum": 10, "paddingTop": 16, "paddingRight": 16, "paddingBottom": 16, "paddingLeft": 16}',
  'admin', NOW(), 'admin', NOW(), b'0'
);

-- 快捷导航：右上侧，占8列
INSERT INTO `system_home_layout` (
  `home_id`, `component_code`, `position_x`, `position_y`, `width`, `height`, `config`,
  `creator`, `create_time`, `updater`, `update_time`, `deleted`
) VALUES (
  @home_page_id, 'workbench_quick_nav', 16, 0, 8, 4,
  '{"title": "快捷导航", "showTitle": true}',
  'admin', NOW(), 'admin', NOW(), b'0'
);

-- 项目列表：右下侧，占8列
INSERT INTO `system_home_layout` (
  `home_id`, `component_code`, `position_x`, `position_y`, `width`, `height`, `config`,
  `creator`, `create_time`, `updater`, `update_time`, `deleted`
) VALUES (
  @home_page_id, 'workbench_project', 16, 4, 8, 4,
  '{"title": "最近项目", "showTitle": true}',
  'admin', NOW(), 'admin', NOW(), b'0'
);

-- =============================================
-- 配置说明
-- =============================================

-- 组件配置项说明（config字段的JSON内容）：
-- {
--   "maxRecordNum": 10,               // 显示任务最大值（范围5-50）
--   "paddingTop": 16,                 // 内边距-上（px）
--   "paddingRight": 16,               // 内边距-右（px）
--   "paddingBottom": 16,              // 内边距-下（px）
--   "paddingLeft": 16,                // 内边距-左（px）
--   "marginTop": 0,                   // 外边距-上（px）
--   "marginRight": 0,                 // 外边距-右（px）
--   "marginBottom": 0,                // 外边距-下（px）
--   "marginLeft": 0                   // 外边距-左（px）
-- }

-- 布局网格说明：
-- - 总共24列（colNum: 24）
-- - 每行高度60px（rowHeight: 60）
-- - position_x: 0-23（组件左上角的列位置）
-- - position_y: 0-N（组件左上角的行位置）
-- - width: 1-24（组件占用的列数）
-- - height: 1-N（组件占用的行数）

-- 推荐尺寸：
-- - 任务列表全屏显示：width=24, height=7-10
-- - 任务列表左侧显示：width=16, height=7-10
-- - 任务列表占半屏：width=12, height=7-10

-- 注意事项：
-- 1. 请通过首页设计器界面进行可视化配置，而不是直接执行此SQL
-- 2. 如需手动执行，请先确认home_id和布局位置不冲突
-- 3. 组件需要相应的BPM权限才能正常显示数据
-- 4. 建议给任务列表组件预留足够的高度（至少7行，420px）以显示完整内容

