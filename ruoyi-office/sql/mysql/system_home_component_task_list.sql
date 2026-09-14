-- =============================================
-- 任务列表组件初始化SQL
-- 执行时间：2026-01-08
-- 说明：在 system_home_component 表中插入任务列表组件配置
-- =============================================

-- 注意事项：
-- 1. 执行前请确认 category_id（组件分类ID）
-- 2. 如果组件已存在，请先删除或更新
-- 3. id 字段请根据实际情况调整，避免主键冲突

-- 查询现有组件的最大ID（可选）
-- SELECT MAX(id) FROM system_home_component WHERE deleted = 0;

-- 查询组件分类（列表类组件通常为分类2）
-- SELECT id, name FROM system_home_component_category WHERE deleted = 0;

-- 插入任务列表组件
INSERT INTO `ruoyi-office`.system_home_component
(
  id, 
  category_id, 
  name, 
  code, 
  component_path, 
  description, 
  preview_image, 
  default_width, 
  default_height, 
  config_schema, 
  status, 
  sort, 
  creator, 
  create_time, 
  updater, 
  update_time, 
  deleted, 
  tenant_id
)
VALUES
(
  10,                                                           -- id: 组件ID，请根据实际情况调整
  2,                                                            -- category_id: 组件分类ID（2=列表组件）
  '任务列表',                                                    -- name: 组件名称
  'workbench_task_list',                                        -- code: 组件编码（与前端注册的code一致）
  'dashboard/home/components/taskLists/workbench-task-list.vue',    -- component_path: 组件路径
  '展示我的单据、待办任务、已办任务、抄送我的四个Tab页签',          -- description: 组件描述
  NULL,                                                         -- preview_image: 预览图（可选）
  24,                                                           -- default_width: 默认宽度（24列=全宽）
  8,                                                            -- default_height: 默认高度（8行=480px）
  '{
    "properties": [
      {
        "key": "paddingTop",
        "type": "number",
        "label": "内边距-上(px)",
        "default": 16,
        "min": 0,
        "max": 100,
        "required": false
      },
      {
        "key": "paddingRight",
        "type": "number",
        "label": "内边距-右(px)",
        "default": 16,
        "min": 0,
        "max": 100,
        "required": false
      },
      {
        "key": "paddingBottom",
        "type": "number",
        "label": "内边距-下(px)",
        "default": 16,
        "min": 0,
        "max": 100,
        "required": false
      },
      {
        "key": "paddingLeft",
        "type": "number",
        "label": "内边距-左(px)",
        "default": 16,
        "min": 0,
        "max": 100,
        "required": false
      },
      {
        "key": "marginTop",
        "type": "number",
        "label": "外边距-上(px)",
        "default": 0,
        "min": 0,
        "max": 100,
        "required": false
      },
      {
        "key": "marginRight",
        "type": "number",
        "label": "外边距-右(px)",
        "default": 0,
        "min": 0,
        "max": 100,
        "required": false
      },
      {
        "key": "marginBottom",
        "type": "number",
        "label": "外边距-下(px)",
        "default": 0,
        "min": 0,
        "max": 100,
        "required": false
      },
      {
        "key": "marginLeft",
        "type": "number",
        "label": "外边距-左(px)",
        "default": 0,
        "min": 0,
        "max": 100,
        "required": false
      },
      {
        "key": "maxRecordNum",
        "type": "number",
        "label": "显示任务最大值",
        "default": 10,
        "min": 5,
        "max": 50,
        "required": false
      }
    ]
  }',                                                           -- config_schema: 组件配置Schema（JSON格式）
  0,                                                            -- status: 状态（0=启用）
  10,                                                           -- sort: 排序（数字越小越靠前）
  '1',                                                          -- creator: 创建者
  '2026-01-08 10:00:00',                                        -- create_time: 创建时间
  '',                                                           -- updater: 更新者
  '2026-01-08 10:00:00',                                        -- update_time: 更新时间
  0,                                                            -- deleted: 是否删除（0=未删除）
  1                                                             -- tenant_id: 租户ID
);

-- =============================================
-- 验证SQL
-- =============================================

-- 查询刚插入的组件
SELECT 
  id,
  category_id,
  name,
  code,
  component_path,
  default_width,
  default_height,
  status,
  sort
FROM system_home_component 
WHERE code = 'workbench_task_list' AND deleted = 0;

-- =============================================
-- 回滚SQL（如需删除组件，执行以下SQL）
-- =============================================

-- DELETE FROM system_home_component WHERE code = 'workbench_task_list' AND deleted = 0;

-- 或者使用软删除
-- UPDATE system_home_component SET deleted = 1 WHERE code = 'workbench_task_list' AND deleted = 0;

-- =============================================
-- 配置说明
-- =============================================

-- 组件配置项说明（config_schema中的properties）：
-- 
-- 内边距（组件内部内容与边框的距离）：
--   - paddingTop/Right/Bottom/Left: 四个方向的内边距，默认均为16px
--
-- 外边距（组件与其他组件之间的距离）：
--   - marginTop/Right/Bottom/Left: 四个方向的外边距，默认均为0px
--
-- 数据展示：
--   - maxRecordNum: 显示任务最大值，默认10，范围5-50（控制每个Tab显示的任务条数）
--
-- 推荐配置：
-- 1. 全屏展示：width=24, height=8-10
-- 2. 左侧展示：width=16, height=8-10
-- 3. 半屏展示：width=12, height=8-10
--
-- 注意事项：
-- 1. 组件需要相应的BPM权限才能正常使用（bpm:process-instance:query, bpm:task:query）
-- 2. 建议给组件预留足够的高度（至少7行，420px）以显示完整内容
-- 3. 组件会自动加载四个Tab的数据：我的单据、待办任务、已办任务、抄送我的
-- 4. 每个Tab默认显示最近10条数据，点击"查看更多"可跳转到完整列表

