# 首页新增组件说明

本次开发新增了三个首页组件，完善了首页组件生态系统。

## 组件清单

### 1. 欢迎组件 (Workbench Welcome)

**组件编码**: `workbench_welcome`  
**文件路径**: `dashboard/home/components/welcome/workbench-welcome.vue`

**功能特性**:

- 显示时间段问候语（早上好、下午好等）
- 显示用户信息（姓名、部门）
- 显示实时天气信息（支持和风天气API）
- 支持自定义提示语
- 精美的渐变背景和云朵动画

**配置项**:

- `greeting`: 自定义提示语（默认："欢迎回来，开始您的工作吧！"）
- `showWeather`: 是否显示天气（默认：true）
- `weatherApiKey`: 和风天气API Key（为空时使用模拟数据）

**推荐布局**:

- 宽度: 24列（全宽）
- 高度: 4行
- 位置: 首页顶部

**天气API配置**:

1. 访问 https://dev.qweather.com/ 注册账号
2. 创建应用获取 API Key
3. 在组件配置中填入 API Key

---

### 2. 通知公告组件 (Workbench Notice)

**组件编码**: `workbench_notice`  
**文件路径**: `dashboard/home/components/notice/workbench-notice.vue`

**功能特性**:

- 列表展示通知公告
- 显示未读数量徽章
- 支持快速预览弹窗
- 点击跳转到详情页
- 智能时间格式化（刚刚、X分钟前等）

**配置项**:

- `maxRecordNum`: 最大显示条数（默认：10，范围：5-50）
- `showBadge`: 显示未读徽章（默认：true）

**推荐布局**:

- 宽度: 12列（半宽）或 8列（1/3宽）
- 高度: 8行
- 位置: 右侧边栏或中间内容区

**数据来源**:

- API: `/system/notice/page`
- 自动分页查询最新通知

---

### 3. 应用中心组件 (Workbench App Center)

**组件编码**: `workbench_app_center`  
**文件路径**: `dashboard/home/components/app-center/workbench-app-center.vue`

**功能特性**:

- 网格显示常用应用
- 支持拖拽排序
- 支持添加/删除应用
- 从有权限的菜单中选择
- 支持系统级和用户级配置

**配置项**:

- `maxAppCount`: 最大显示应用数（默认：12，范围：4-24）
- `gridCols`: 网格列数（默认：4，范围：2-6）
- `enableDrag`: 启用拖拽排序（默认：true）

**推荐布局**:

- 宽度: 12列（半宽，gridCols=3或4）或 16列（2/3宽，gridCols=4或5）
- 高度: 6-10行
- 位置: 中间内容区或右侧边栏

**数据库表**:

- `system_home_app_config`: 系统级应用配置（管理员统一配置）
- `system_home_app_user`: 用户级应用配置（用户个性化配置）

**权限控制**:

- 只能选择有权限的菜单作为常用应用
- 自动过滤无权限的应用

---

## 安装步骤

### 1. 执行数据库脚本

按顺序执行以下SQL文件：

```sql
-- 应用中心数据库表
source sql/mysql/system_home_app_center.sql;

-- 欢迎组件
source sql/mysql/system_home_component_welcome.sql;

-- 通知公告组件
source sql/mysql/system_home_component_notice.sql;

-- 应用中心组件
source sql/mysql/system_home_component_app_center.sql;
```

### 2. 组件已自动注册

组件已在 `registry.ts` 中注册，无需手动操作。

### 3. 在首页设计器中使用

1. 进入"系统管理 → 首页管理"
2. 点击"设计"按钮进入设计器
3. 从左侧组件面板拖拽新组件到画布
4. 在右侧配置面板设置组件属性
5. 保存布局

---

## 开发技术栈

### 前端

- Vue 3 Composition API
- TypeScript
- Ant Design Vue
- VueDraggable (应用中心拖拽)
- Iconify (图标系统)

### 后端 API

- 欢迎组件: 无后端依赖（使用 UserStore + 外部天气API）
- 通知公告: `/system/notice/page`
- 应用中心: `/system/home-app-*` 系列接口

---

## 组件架构

```
components/
├── welcome/                    # 欢迎组件
│   ├── workbench-welcome.vue
│   └── index.ts
├── notice/                     # 通知公告组件
│   ├── workbench-notice.vue
│   ├── notice-preview-modal.vue
│   └── index.ts
├── app-center/                 # 应用中心组件
│   ├── workbench-app-center.vue
│   ├── app-select-modal.vue
│   └── index.ts
└── registry.ts                 # 组件注册表（已更新）
```

---

## 注意事项

### 欢迎组件

- 天气API Key 为空时会使用模拟数据
- 用户信息从 `@vben/stores` 的 `useUserStore` 获取
- 问候语会根据当前时间自动变化

### 通知公告组件

- 未读数量目前为模拟数据，需要后端支持
- 支持 HTML 内容渲染（预览弹窗中）
- 点击"查看更多"跳转到系统通知页面

### 应用中心组件

- 需要后端实现应用中心相关API
- 首次使用会自动从系统配置初始化
- 拖拽排序会实时保存到后端
- 删除操作会二次确认

---

## 后续优化建议

1. **欢迎组件**
   - 支持多种天气API（OpenWeatherMap等）
   - 支持自定义背景图片
   - 支持更多用户统计信息

2. **通知公告组件**
   - 后端支持未读状态标记
   - 支持消息推送通知
   - 支持消息分类筛选

3. **应用中心组件**
   - 支持应用分组
   - 支持自定义应用图标上传
   - 支持应用使用频率统计
   - 支持推荐应用功能

---

## 联系方式

如有问题或建议，请联系开发团队。
