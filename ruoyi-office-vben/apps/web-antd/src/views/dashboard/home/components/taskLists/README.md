# 首页任务列表组件说明

## 组件概述

`workbench-task-list.vue` 是一个用于首页展示的任务列表组件，包含以下四个页签：

1. **我的单据** - 显示当前用户发起的流程实例
2. **待办任务** - 显示需要当前用户处理的待办任务
3. **已办任务** - 显示当前用户已经处理完成的任务
4. **抄送我的** - 显示抄送给当前用户的流程信息

## 功能特性

- ✅ 四个Tab页签切换，实时显示任务数量统计
- ✅ 自动加载前10条数据，支持快速预览
- ✅ 友好的日期显示（刚刚、几分钟前、几小时前、几天前）
- ✅ 完善的任务信息展示（标题、单据编号、摘要、状态等）
- ✅ 点击列表项直接跳转到对应的详情或办理页面
- ✅ 支持查看更多，跳转到完整的任务列表页面
- ✅ 空数据友好提示
- ✅ 加载状态显示

## 使用方法

### 前置条件：初始化数据库

**首次使用前必须执行数据库初始化SQL**，将组件注册到 `system_home_component` 表中。

**SQL文件**：

- 📄 完整版（带详细注释）：`ruoyi-office/sql/mysql/system_home_component_task_list.sql`
- 📄 精简版（快速执行）：`ruoyi-office/sql/mysql/system_home_component_task_list_simple.sql`

**执行步骤**：

```sql
-- 1. 查询现有组件的最大ID
SELECT MAX(id) FROM system_home_component WHERE deleted = 0;

-- 2. 根据查询结果，调整SQL中的id字段，然后执行初始化SQL
-- 执行 system_home_component_task_list_simple.sql 或 system_home_component_task_list.sql

-- 3. 验证是否插入成功
SELECT * FROM system_home_component WHERE code = 'workbench_task_list' AND deleted = 0;
```

### 1. 在首页设计器中添加

1. 进入**首页管理**（`/home/manage`）
2. 点击**设计首页**按钮，进入首页设计器
3. 在左侧组件面板中找到**任务列表**组件
4. 拖拽到画布中合适的位置
5. 在右侧配置面板中设置组件标题和样式
6. 保存并发布

### 2. 组件配置项

| 配置项        | 类型   | 默认值 | 说明                       |
| ------------- | ------ | ------ | -------------------------- |
| maxRecordNum  | number | 10     | 显示任务最大值（范围5-50） |
| paddingTop    | number | 16     | 内边距-上（px）            |
| paddingRight  | number | 16     | 内边距-右（px）            |
| paddingBottom | number | 16     | 内边距-下（px）            |
| paddingLeft   | number | 16     | 内边距-左（px）            |

### 3. 推荐布局尺寸

- **宽度**: 建议占 12-24 列（总共24列）
- **高度**: 建议 7-10 行（每行60px）
- **位置**: 适合放在首页中上部或左侧区域

## 数据来源

组件使用以下API获取数据：

- `getProcessInstanceMyPage` - 获取我的单据列表
- `getTaskTodoPage` - 获取待办任务列表
- `getTaskDonePage` - 获取已办任务列表
- `getProcessInstanceCopyPage` - 获取抄送我的列表

**注意**：每个Tab显示的任务条数由 `maxRecordNum` 配置项控制，默认为10条，可设置范围为5-50条。

## 交互说明

### 列表项点击

- **我的单据**: 跳转到流程实例详情页
- **待办任务**: 跳转到待办任务办理页
- **已办任务**: 跳转到流程实例详情页
- **抄送我的**: 跳转到流程实例详情页

### 查看更多

点击右上角"查看更多"链接，会跳转到对应的完整列表页面：

- **我的单据**: `/bpm/process-instance/my`
- **待办任务**: `/bpm/task/todo`
- **已办任务**: `/bpm/task/done`
- **抄送我的**: `/bpm/task/copy`

## 状态说明

### 任务状态

| 状态值 | 显示文本 | 徽章类型           |
| ------ | -------- | ------------------ |
| 1      | 审批中   | processing（蓝色） |
| 2      | 已通过   | success（绿色）    |
| 3      | 未通过   | error（红色）      |
| 4      | 已取消   | default（灰色）    |

### 特殊状态

- **待办任务**: 统一显示"待处理"状态（黄色warning）
- **抄送我的**: 统一显示"已抄送"状态（蓝色info）

## 组件注册

组件已在 `registry.ts` 中注册，注册代码如下：

```typescript
registerComponent({
  code: 'workbench_task_list',
  component: WorkbenchTaskList,
  name: '任务列表',
  description: '展示我的单据、待办任务、已办任务、抄送我的',
});
```

## 技术实现

- **框架**: Vue 3 + TypeScript
- **UI组件**: Ant Design Vue
- **状态管理**: Composition API
- **路由**: Vue Router
- **样式**: Tailwind CSS + Scoped CSS

## 注意事项

1. 组件需要相应的BPM权限才能正常显示数据
2. 每个Tab首次加载显示10条数据
3. 统计数据在组件挂载时一次性加载所有Tab的总数
4. 摘要字段最多显示2行，超出部分用省略号表示
5. 组件内部包含滚动条，最大高度400px

## 更新日志

### v1.0.0 (2026-01-08)

- ✨ 初始版本发布
- ✨ 支持我的单据、待办任务、已办任务、抄送我的四个Tab
- ✨ 支持点击跳转和查看更多功能
- ✨ 实时统计各Tab的任务数量
