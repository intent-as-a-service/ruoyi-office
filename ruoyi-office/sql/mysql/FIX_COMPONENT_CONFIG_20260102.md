# 首页设计器问题修复 - 20260102 晚

## 🔧 修复内容

### 问题1: 预览时标题不显示 ✅

**问题描述**：
在设计器中设置标题为"显示"，但预览时标题文字不显示

**根本原因**：
`showTitle` 的默认值判断逻辑有问题，当 `showTitle` 为 `undefined` 时，应该检查是否有 `title` 值

**解决方案**：
```typescript
// component-wrapper.vue
const showTitle = computed(() => {
  // 如果 showTitle 未定义，检查是否有 title，有 title 就显示
  if (props.config?.showTitle === undefined) {
    return !!props.config?.title;
  }
  return props.config.showTitle === true;
});
```

---

### 问题2: 添加组件级别边距配置 ✅

**功能说明**：
- 每个组件可以单独配置上下左右边距（padding）
- 组件级别边距优先级高于全局配置
- 边距范围：0-50px

**实现效果**：
1. 在属性配置面板中，边距配置单独成为一个折叠面板
2. 提供4个边距配置：上边距、右边距、下边距、左边距
3. 边距直接应用到组件外层容器

**代码实现**：
```vue
<!-- component-wrapper.vue -->
<template>
  <div class="component-wrapper" :style="wrapperStyle">
    <!-- 组件内容 -->
  </div>
</template>

<script>
const wrapperStyle = computed(() => {
  const style: Record<string, string> = {};
  
  if (props.config?.paddingTop !== undefined) {
    style.paddingTop = `${props.config.paddingTop}px`;
  }
  if (props.config?.paddingRight !== undefined) {
    style.paddingRight = `${props.config.paddingRight}px`;
  }
  if (props.config?.paddingBottom !== undefined) {
    style.paddingBottom = `${props.config.paddingBottom}px`;
  }
  if (props.config?.paddingLeft !== undefined) {
    style.paddingLeft = `${props.config.paddingLeft}px`;
  }
  
  return style;
});
</script>
```

**配置面板优化**：
```vue
<!-- config-panel.vue -->
<!-- 基础配置 -->
<CollapsePanel key="basic" header="基础配置">
  <FormItem v-for="prop in basicProperties">
    <!-- 基础属性 -->
  </FormItem>
</CollapsePanel>

<!-- 边距配置 -->
<CollapsePanel key="padding" header="边距配置">
  <div class="mb-3 text-xs text-gray-500">
    组件级别边距，优先级高于全局配置
  </div>
  <FormItem v-for="prop in paddingProperties">
    <InputNumber :min="0" :max="50" :step="1" />
  </FormItem>
</CollapsePanel>
```

---

### 问题3: 背景色设置为透明 ✅

**问题描述**：
渲染器容器的背景色与页面背景色不一致，显得突兀

**解决方案**：
```css
/* layout-renderer.vue */
.layout-renderer {
  overflow-y: auto;
  background: transparent; /* 改为透明 */
}
```

---

### 问题4: 切换组件时属性配置不更新 ✅

**问题描述**：
在设计器中选择不同组件时，右侧属性配置面板显示的内容不会更新

**根本原因**：
只监听了 `currentComponent`（组件定义）的变化，没有监听 `selectedItem`（选中的组件实例）的变化

**解决方案**：
```typescript
// config-panel.vue
// 添加对 selectedItem 的监听
watch(
  () => props.selectedItem,
  (newItem) => {
    if (!newItem || !configSchema.value) return;
    
    // 更新 formData 为新组件的配置
    const newFormData: Record<string, any> = {};
    configSchema.value.properties.forEach((prop) => {
      if (newItem.config && newItem.config[prop.key] !== undefined) {
        newFormData[prop.key] = newItem.config[prop.key];
      } else if (prop.default !== undefined) {
        newFormData[prop.key] = prop.default;
      }
    });
    formData.value = newFormData;
  },
  { deep: true }
);
```

---

### 问题5: 减少组件标题空间占用 ✅

**问题描述**：
组件标题占用空间太大，影响内容显示

**解决方案**：
1. **减少标题 padding**：从 `px-3 py-2` 改为 `px-2 py-1`
2. **减小字体**：从 `text-sm` 改为 `text-xs`
3. **移除容器 padding**：将 `.layout-item-content` 的 padding 从 `12px` 改为 `0`

```vue
<!-- component-wrapper.vue -->
<div
  v-if="showTitle && title"
  class="component-title flex items-center border-b bg-white px-2 py-1"
>
  <span class="text-xs font-medium">{{ title }}</span>
</div>
```

```css
/* layout-renderer.vue */
.layout-item-content {
  padding: 0; /* 移除固定padding，由组件自己控制 */
}
```

---

## 📋 修改文件清单

| 文件 | 修改内容 |
|------|---------|
| **component-wrapper.vue** | 1. 修复 showTitle 默认值逻辑<br>2. 添加组件级别边距支持<br>3. 减少标题 padding 和字体大小 |
| **config-panel.vue** | 1. 分离基础配置和边距配置<br>2. 添加对 selectedItem 的监听<br>3. 创建独立的边距配置面板 |
| **layout-renderer.vue** | 1. 背景色改为透明<br>2. 移除容器固定 padding |
| **add_component_padding_config.sql** | 为所有组件添加边距配置项 |

---

## 🎯 效果对比

### 标题显示

**修复前**：
- 设置为"显示"但不显示标题 ❌
- 标题占用空间大（padding: 12px 16px） ❌

**修复后**：
- 设置为"显示"正确显示标题 ✅
- 标题紧凑（padding: 4px 8px） ✅
- 字体更小（text-xs） ✅

### 属性配置

**修复前**：
- 切换组件后，配置面板不更新 ❌

**修复后**：
- 切换组件立即更新配置 ✅
- 基础配置和边距配置分离显示 ✅

### 边距配置

**新增功能**：
```
┌────────────────────────┐
│  基础配置              │
│  - 显示标题            │
│  - 标题文本            │
├────────────────────────┤
│  边距配置 (新增)       │
│  - 上边距(px)         │
│  - 右边距(px)         │
│  - 下边距(px)         │
│  - 左边距(px)         │
│  提示: 优先级高于全局  │
└────────────────────────┘
```

---

## 🧪 测试要点

### 1. 标题显示测试
- [ ] 新添加组件，默认显示标题（如果有title配置）
- [ ] 设置为"显示"，标题正确显示
- [ ] 设置为"不显示"，标题隐藏
- [ ] 预览时标题显示与设计器一致

### 2. 属性配置更新测试
- [ ] 添加第一个组件，配置面板显示该组件属性
- [ ] 添加第二个组件，选中时配置面板更新
- [ ] 在两个组件之间切换，配置面板正确更新
- [ ] 修改配置后切换组件，配置正确保存

### 3. 边距配置测试
- [ ] 打开边距配置面板，4个边距都可以配置
- [ ] 设置上边距，组件顶部有相应间距
- [ ] 设置右边距，组件右侧有相应间距
- [ ] 设置下边距，组件底部有相应间距
- [ ] 设置左边距，组件左侧有相应间距
- [ ] 组件边距优先于全局边距

### 4. 标题样式测试
- [ ] 标题高度明显减小
- [ ] 标题文字大小合适，易读
- [ ] 标题不占用过多内容空间

---

## 💡 使用说明

### 组件级别边距配置

**使用场景**：
1. 需要某个组件与其他组件有不同间距
2. 需要微调组件位置而不影响其他组件
3. 需要组件内容与边缘保持距离

**配置步骤**：
1. 在设计器中选中组件
2. 打开右侧属性配置面板
3. 展开"边距配置"
4. 设置上/右/下/左边距
5. 实时预览效果

**优先级**：
```
组件级别边距 > 全局边距配置
```

示例：
- 全局容器内边距：10px
- 全局组件间距：10px
- 某组件设置上边距：20px
- 结果：该组件顶部间距为 20px，其他边为 10px

---

## 📞 数据库更新

### 执行SQL脚本

```bash
# 为组件添加边距配置
mysql -u root -p your_database < add_component_padding_config.sql
```

### 脚本内容说明

为以下4个组件添加边距配置：
1. analytics_visits（访问统计）
2. analytics_visits_source（访问来源）
3. workbench_project（项目列表）
4. workbench_quick_nav（快捷导航）

每个组件新增配置：
- `paddingTop`: 上边距(px)，默认0，范围0-50
- `paddingRight`: 右边距(px)，默认0，范围0-50
- `paddingBottom`: 下边距(px)，默认0，范围0-50
- `paddingLeft`: 左边距(px)，默认0，范围0-50

---

## ✅ 完成

所有问题已解决！**刷新页面生效** 🎉

现在：
1. ✅ 预览时标题正确显示
2. ✅ 切换组件时属性配置立即更新
3. ✅ 支持组件级别边距配置（优先级高于全局）
4. ✅ 背景色透明，与页面一致
5. ✅ 标题紧凑，节省空间

**使用流程**：
1. 执行 SQL 脚本更新组件配置
2. 刷新前端页面
3. 在设计器中添加/选择组件
4. 在属性配置中设置边距
5. 预览查看效果
