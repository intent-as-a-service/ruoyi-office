-- =============================================
-- 首页组件 - 我的日程组件初始化SQL
-- =============================================
-- 组件说明：展示日程日历和待办事项
-- 组件特点：
-- 1. 日历表展示，显示有日程的日期（红色点提示）
-- 2. 点击日期显示该日期的日程列表
-- 3. 右上角"全部日程"按钮跳转到日程列表页面
-- 4. 支持配置标题、最大显示条数等
-- =============================================

-- 插入组件定义
-- 注意：id 和 category_id 需要根据实际情况调整
INSERT INTO `ruoyi-office`.system_home_component
(id, category_id, name, code, component_path, description, preview_image, default_width, default_height, config_schema, status, sort, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES(
  22,                                                           -- id: 组件ID（需要根据实际情况调整）
  3,                                                            -- category_id: 分类ID（3=列表组件）
  '我的日程',                                                   -- name: 组件名称
  'workbench_schedule',                                         -- code: 组件编码（唯一）
  'dashboard/home/components/schedule/workbench-schedule.vue', -- component_path: 组件路径
  '展示日程日历和待办事项，支持日期选择和日程查看',            -- description: 组件描述
  NULL,                                                         -- preview_image: 预览图（可选）
  12,                                                           -- default_width: 默认宽度（12列=半宽）
  10,                                                           -- default_height: 默认高度（10行=600px）
  '{
    "properties": [
      {
        "key": "title",
        "type": "string",
        "label": "标题",
        "default": "日程待办",
        "required": true
      },
      {
        "key": "maxRecordNum",
        "type": "number",
        "label": "最大显示条数",
        "default": 10,
        "min": 5,
        "max": 50,
        "required": false
      },
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
      }
    ]
  }',                                                           -- config_schema: 组件配置Schema（JSON格式）
  1,                                                            -- status: 状态（1=启用）
  7,                                                            -- sort: 排序（数字越小越靠前）
  '1',                                                          -- creator: 创建者
  NOW(),                                                        -- create_time: 创建时间
  '',                                                           -- updater: 更新者
  NOW(),                                                        -- update_time: 更新时间
  0,                                                            -- deleted: 是否删除
  1                                                             -- tenant_id: 租户ID
);

-- =============================================
-- 组件配置项说明
-- =============================================
-- 
-- title: 组件标题
--   - 类型: string
--   - 默认值: "日程待办"
--   - 显示在组件顶部
-- 
-- maxRecordNum: 最大显示条数
--   - 类型: number
--   - 默认值: 10
--   - 范围: 5-50
--   - 控制选中日期后显示的日程条数
-- 
-- 内边距和外边距配置：
--   - paddingTop/Right/Bottom/Left: 组件内部内容与边框的距离
--   - marginTop/Right/Bottom/Left: 组件与其他组件之间的距离
--   - 默认值均为16px（内边距）和0px（外边距）
-- 
-- =============================================
-- 功能说明
-- =============================================
-- 
-- 1. 日历显示：
--    - 使用 Ant Design Vue Calendar 组件
--    - 有日程的日期下方显示红色点提示
--    - 支持月份切换
-- 
-- 2. 日程列表：
--    - 点击日期后，在日历下方显示该日期的日程列表
--    - 显示日程标题、时间、类型、分类等信息
--    - 点击日程可查看详情或跳转到列表页
-- 
-- 3. 交互功能：
--    - 点击"全部日程"按钮：跳转到日程列表页面
--    - 支持日期选择查看不同日期的日程
-- 
-- 4. 推荐布局：
--    - 宽度: 12列（半宽）或 16列（2/3宽）
--    - 高度: 10行（适中高度，可显示完整日历和列表）
--    - 位置: 左侧或中间内容区
-- 
-- 5. 数据来源：
--    - API: /system/schedule/dates - 获取有日程的日期列表
--    - API: /system/schedule/list-by-date - 根据日期查询日程列表
-- 
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
WHERE code = 'workbench_schedule' AND deleted = 0;

