-- =============================================
-- 首页组件 - 欢迎组件初始化SQL
-- =============================================
-- 组件说明：展示欢迎信息、用户信息和天气
-- 组件特点：
-- 1. 显示时间段问候语（早上好、下午好等）
-- 2. 显示用户信息（从Store获取）
-- 3. 显示实时天气信息（和风天气API）
-- 4. 支持自定义提示语
-- 5. 精美的渐变背景和动画效果
-- =============================================

-- 插入组件定义
-- 注意：id 和 category_id 需要根据实际情况调整
INSERT INTO `ruoyi-office`.system_home_component
(id, category_id, name, code, component_path, description, preview_image, default_width, default_height, config_schema, status, sort, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES(
  20,                                                           -- id: 组件ID（需要根据实际情况调整）
  2,                                                            -- category_id: 分类ID（2=工作台组件）
  '欢迎组件',                                                   -- name: 组件名称
  'workbench_welcome',                                          -- code: 组件编码（唯一）
  'dashboard/home/components/welcome/workbench-welcome.vue',    -- component_path: 组件路径
  '展示欢迎信息、用户信息和天气，支持和风天气API',              -- description: 组件描述
  NULL,                                                         -- preview_image: 预览图（可选）
  24,                                                           -- default_width: 默认宽度（24列=全宽）
  4,                                                            -- default_height: 默认高度（4行=240px）
  '{
    "properties": [
      {
        "key": "greeting",
        "type": "string",
        "label": "提示语",
        "default": "欢迎回来，开始您的工作吧！",
        "required": false
      },
      {
        "key": "showWeather",
        "type": "boolean",
        "label": "显示天气",
        "default": true,
        "required": false
      },
      {
        "key": "weatherApiKey",
        "type": "string",
        "label": "和风天气API Key",
        "default": "",
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
  5,                                                            -- sort: 排序（数字越小越靠前）
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
-- greeting: 自定义提示语
--   - 类型: string
--   - 默认值: "欢迎回来，开始您的工作吧！"
--   - 显示在用户名下方的问候语
-- 
-- showWeather: 是否显示天气
--   - 类型: boolean
--   - 默认值: true
--   - 控制是否显示天气信息模块
-- 
-- weatherApiKey: 和风天气API Key
--   - 类型: string
--   - 默认值: ""（空表示使用模拟数据）
--   - 需要在 https://dev.qweather.com/ 注册获取
--   - 免费版可满足基本需求
-- 
-- 内边距和外边距配置：
--   - paddingTop/Right/Bottom/Left: 组件内部内容与边框的距离
--   - marginTop/Right/Bottom/Left: 组件与其他组件之间的距离
--   - 默认值均为0，可根据实际布局需求调整
-- 
-- =============================================
-- 使用说明
-- =============================================
-- 
-- 1. 天气API配置：
--    - 访问 https://dev.qweather.com/ 注册账号
--    - 创建应用获取 API Key
--    - 在组件配置中填入 API Key
--    - 如果不配置，组件会使用模拟天气数据
-- 
-- 2. 用户信息来源：
--    - 用户名称: userStore.userInfo.realName 或 username
--    - 部门信息: userStore.userInfo.deptName
--    - 自动根据当前时间显示问候语
-- 
-- 3. 视觉效果：
--    - 渐变背景（紫色系）
--    - 云朵装饰动画
--    - 天气图标动画
--    - 响应式布局
-- 
-- 4. 推荐布局：
--    - 宽度: 24列（全宽）
--    - 高度: 4行（适中高度）
--    - 位置: 首页顶部
-- 

