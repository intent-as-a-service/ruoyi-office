-- =============================================
-- 首页组件 - 通知公告组件初始化SQL
-- =============================================
-- 组件说明：展示系统通知公告列表
-- 组件特点：
-- 1. 列表展示通知公告
-- 2. 显示未读数量徽章
-- 3. 支持快速预览弹窗
-- 4. 点击跳转到详情页
-- 5. 支持时间格式化显示
-- =============================================

-- 插入组件定义
-- 注意：id 和 category_id 需要根据实际情况调整
INSERT INTO `ruoyi-office`.system_home_component
(id, category_id, name, code, component_path, description, preview_image, default_width, default_height, config_schema, status, sort, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES(
  21,                                                           -- id: 组件ID（需要根据实际情况调整）
  2,                                                            -- category_id: 分类ID（2=工作台组件）
  '通知公告',                                                   -- name: 组件名称
  'workbench_notice',                                           -- code: 组件编码（唯一）
  'dashboard/home/components/notice/workbench-notice.vue',      -- component_path: 组件路径
  '展示系统通知公告列表，支持徽章和快速预览',                  -- description: 组件描述
  NULL,                                                         -- preview_image: 预览图（可选）
  12,                                                           -- default_width: 默认宽度（12列=半宽）
  8,                                                            -- default_height: 默认高度（8行=480px）
  '{
    "properties": [
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
        "key": "showBadge",
        "type": "boolean",
        "label": "显示未读徽章",
        "default": true,
        "required": false
      },
      {
        "key": "paddingTop",
        "type": "number",
        "label": "内边距-上(px)",
        "default": 0,
        "min": 0,
        "max": 100,
        "required": false
      },
      {
        "key": "paddingRight",
        "type": "number",
        "label": "内边距-右(px)",
        "default": 0,
        "min": 0,
        "max": 100,
        "required": false
      },
      {
        "key": "paddingBottom",
        "type": "number",
        "label": "内边距-下(px)",
        "default": 0,
        "min": 0,
        "max": 100,
        "required": false
      },
      {
        "key": "paddingLeft",
        "type": "number",
        "label": "内边距-左(px)",
        "default": 0,
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
  0,                                                            -- status: 状态（0=启用）
  6,                                                            -- sort: 排序（数字越小越靠前）
  '1',                                                          -- creator: 创建者
  '2026-01-09 16:00:00',                                        -- create_time: 创建时间
  '',                                                           -- updater: 更新者
  '2026-01-09 16:00:00',                                        -- update_time: 更新时间
  0,                                                            -- deleted: 是否删除
  1                                                             -- tenant_id: 租户ID
);

-- =============================================
-- 组件配置项说明
-- =============================================
-- 
-- maxRecordNum: 最大显示条数
--   - 类型: number
--   - 默认值: 10
--   - 范围: 5-50
--   - 控制列表最多显示多少条通知
-- 
-- showBadge: 显示未读徽章
--   - 类型: boolean
--   - 默认值: true
--   - 控制是否在标题旁显示未读数量徽章
-- 
-- 内边距和外边距配置：
--   - paddingTop/Right/Bottom/Left: 组件内部内容与边框的距离
--   - marginTop/Right/Bottom/Left: 组件与其他组件之间的距离
--   - 默认值均为0，可根据实际布局需求调整
-- 
-- =============================================
-- 功能说明
-- =============================================
-- 
-- 1. 列表显示：
--    - 通知类型图标（通知/公告）
--    - 通知标题（支持截断）
--    - 通知内容预览（最多2行）
--    - 创建时间（智能格式化：刚刚、X分钟前、X小时前等）
--    - 创建人信息
--    - 通知状态标签
-- 
-- 2. 交互功能：
--    - 点击列表项：打开预览弹窗
--    - 点击"查看更多"：跳转到通知公告管理页面
--    - 预览弹窗：显示完整的通知内容
-- 
-- 3. 视觉效果：
--    - 列表项hover效果
--    - 通知类型彩色图标
--    - 自定义滚动条样式
--    - 未读徽章动画
-- 
-- 4. 推荐布局：
--    - 宽度: 12列（半宽）或 8列（1/3宽）
--    - 高度: 8行（适中高度）
--    - 位置: 右侧边栏或中间内容区
-- 
-- 5. 数据来源：
--    - API: /system/notice/page
--    - 分页查询最新的通知公告
--    - 自动过滤状态为正常的通知
-- 

