# 首页设计器优化更新说明

## 📅 更新日期
2026-01-02

## 🎯 优化内容

### 1️⃣ 组件标题功能
**问题**：首页组件没有标题显示

**解决方案**：
- ✅ 修改 `component-wrapper.vue`，添加标题显示功能
- ✅ 支持动态显示/隐藏标题
- ✅ 支持标题前显示图标
- ✅ 支持图标颜色自定义

**新增配置项**：
- `showTitle`: 是否显示标题（默认 true）
- `title`: 标题文本
- `titleIcon`: 标题图标（Iconify格式，如 `lucide:bar-chart-2`）
- `titleIconColor`: 图标颜色（默认 `#1890ff`）

### 2️⃣ 配置面板优化
**问题**：配置面板无法收起，占用空间

**解决方案**：
- ✅ 使用 Ant Design 的 Collapse 组件实现折叠功能
- ✅ 分为"基础配置"和"组件信息"两个面板
- ✅ 默认展开"基础配置"面板
- ✅ 新增颜色选择器组件（ColorPicker）
- ✅ 新增图标选择器（支持快捷选择常用图标）

**常用图标列表**：
- lucide:home - 首页
- lucide:user - 用户
- lucide:bar-chart - 柱状图
- lucide:pie-chart - 饼图
- lucide:trending-up - 上升趋势
- 等...

### 3️⃣ 网格布局优化
**问题**：24列网格太密，难以对齐

**解决方案**：
- ✅ 改为 **12列布局**，网格间距加倍
- ✅ 双层网格线系统：
  - 主网格线（深色）：每4列/4行
  - 次网格线（浅色）：每1列/1行
- ✅ 更新所有组件默认宽度（适配12列）

**列数对比**：
| 组件 | 24列宽度 | 12列宽度 |
|------|----------|----------|
| 访问统计 | 6 | 3 |
| 访问数据 | 18 | 9 |
| 访问来源 | 12 | 6 |
| 项目列表 | 12 | 6 |
| 动态列表 | 12 | 6 |
| 快捷导航 | 12 | 6 |

## 📋 SQL 执行顺序

### 必须执行（按顺序）：

1. **更新组件配置Schema**
```bash
mysql -u root -p your_database < update_component_config_schema.sql
```
这个脚本会为所有组件添加标题、图标配置项。

2. **更新组件默认宽度**
```bash
mysql -u root -p your_database < update_component_width_for_12col.sql
```
这个脚本会将所有组件宽度适配到12列布局。

### 可选执行：

如果需要重新初始化组件数据，执行：
```bash
mysql -u root -p your_database < quick_fix_home_component.sql
```

## 🎨 前端修改文件

| 文件 | 修改内容 |
|------|---------|
| `component-wrapper.vue` | 添加标题显示功能 |
| `config-panel.vue` | 添加折叠面板、颜色选择器、图标选择器 |
| `designer-canvas.vue` | 优化网格线显示，改为12列 |
| `designer/index.vue` | 更新列数配置为12 |
| `layout-renderer.vue` | 更新渲染器列数为12 |

## 🧪 测试检查项

### 1. 组件标题显示
- [ ] 默认显示标题
- [ ] 可以隐藏标题（showTitle = false）
- [ ] 标题前显示图标
- [ ] 图标颜色可自定义

### 2. 配置面板
- [ ] 基础配置面板可以折叠
- [ ] 组件信息面板可以折叠
- [ ] 颜色选择器正常工作
- [ ] 图标选择器可以快速选择图标
- [ ] 手动输入图标代码有效

### 3. 网格布局
- [ ] 网格线清晰可见
- [ ] 主网格线（深色）每4列显示
- [ ] 组件可以精确对齐到网格
- [ ] 拖拽时吸附到网格
- [ ] 保存后布局保持一致

## 📊 数据库变更

### system_home_component 表
- **config_schema** 字段更新：添加标题、图标配置
- **default_width** 字段更新：适配12列布局

### 示例配置 Schema：
```json
{
  "properties": [
    {
      "key": "showTitle",
      "label": "显示标题",
      "type": "boolean",
      "default": true
    },
    {
      "key": "title",
      "label": "标题",
      "type": "string",
      "default": "访问统计"
    },
    {
      "key": "titleIcon",
      "label": "标题图标",
      "type": "icon",
      "default": "lucide:bar-chart-2"
    },
    {
      "key": "titleIconColor",
      "label": "图标颜色",
      "type": "color",
      "default": "#1890ff"
    }
  ]
}
```

## 🎯 使用指南

### 设置组件标题

1. 在设计器中选中一个组件
2. 在右侧"属性配置"面板中展开"基础配置"
3. 设置以下属性：
   - **显示标题**：开关控制是否显示
   - **标题**：输入标题文本
   - **标题图标**：选择或输入图标代码
   - **图标颜色**：点击颜色选择器选择颜色

### 快速选择图标

配置面板提供了常用图标快捷选择网格，点击即可应用。也可以手动输入任何 Iconify 图标代码，格式为 `collection:icon-name`。

更多图标请访问：https://icon-sets.iconify.design/

### 网格对齐技巧

- 使用 **主网格线**（深色）作为主要对齐参考
- 组件宽度建议使用：3、6、9、12（整数倍）
- 组件高度建议使用：2、4、6、8（偶数）

## 🔄 回滚方案

如果需要回滚到24列布局：

1. 恢复前端代码中的 `colNum: 24`
2. 执行以下SQL恢复组件宽度：
```sql
UPDATE system_home_component SET default_width = default_width * 2;
```

## 📞 技术支持

如有问题，请检查：
1. 是否执行了所有SQL脚本
2. 前端是否已刷新缓存（Ctrl+Shift+R）
3. 组件是否重新加载
4. 浏览器控制台是否有错误信息
