# 调试配置面板 Watch 问题

## 🐛 问题描述

**现象**：
- 切换组件时，右侧属性配置不刷新
- 收起配置面板再展开，配置就刷新了
- 说明 watch 没有正确触发

## 🔍 根本原因

**问题分析**：
1. Vue 的 watch 对于对象引用的监听存在限制
2. `props.selectedItem` 是一个对象，即使切换了组件，对象引用可能不变
3. 之前的 watch 可能因为各种原因没有触发

## ✅ 解决方案

### 方案1: 监听组件ID + 去重检查

**核心思路**：
- 监听 `props.selectedItem?.i`（组件唯一ID）
- 使用 `lastSelectedItemId` 记录上次的ID
- 只有ID真正变化时才更新配置

**代码实现**：
```typescript
const lastSelectedItemId = ref<string | null>(null);

watch(
  () => props.selectedItem?.i,
  (newId) => {
    // 检查是否真的切换了组件
    if (newId === lastSelectedItemId.value) {
      return; // 相同组件，不更新
    }
    
    lastSelectedItemId.value = newId || null;
    
    // 更新配置...
  },
  { immediate: true }
);
```

### 方案2: 添加详细的调试日志

**目的**：
- 确认 watch 是否被触发
- 查看组件查找是否成功
- 检查 schema 解析是否正确
- 验证 formData 更新是否生效

**调试日志**：
```typescript
console.log('[ConfigPanel] Watch triggered, newId:', newId);
console.log('[ConfigPanel] Found component:', component?.name);
console.log('[ConfigPanel] Parsed schema:', schema);
console.log('[ConfigPanel] Updated formData:', newFormData);
```

---

## 🧪 测试步骤

### 步骤1: 刷新页面
```
打开浏览器控制台（F12）
刷新设计器页面
```

### 步骤2: 添加第一个组件
```
从左侧拖拽一个组件到画布
观察控制台输出：
- 应该看到 "[ConfigPanel] Watch triggered"
- 应该看到 "[ConfigPanel] Found component"
- 应该看到 "[ConfigPanel] Updated formData"
```

### 步骤3: 添加第二个组件
```
再拖拽另一个不同的组件到画布
观察控制台输出：
- 应该看到新的 watch 触发日志
```

### 步骤4: 切换组件
```
点击第一个组件
观察控制台：应该看到 watch 触发
观察右侧面板：应该显示第一个组件的配置

点击第二个组件
观察控制台：应该看到 watch 触发
观察右侧面板：应该显示第二个组件的配置
```

---

## 📊 预期日志输出

### 正常情况
```
[ConfigPanel] Watch triggered, newId: item-1, lastId: null
[ConfigPanel] selectedItem changed: item-1, analytics_visits
[ConfigPanel] Found component: 访问统计, analytics_visits
[ConfigPanel] Parsed schema: { properties: [...] }
[ConfigPanel] Updated formData: { showTitle: true, title: "访问统计" }
```

### 切换组件时
```
[ConfigPanel] Watch triggered, newId: item-2, lastId: item-1
[ConfigPanel] selectedItem changed: item-2, workbench_project
[ConfigPanel] Found component: 项目列表, workbench_project
[ConfigPanel] Parsed schema: { properties: [...] }
[ConfigPanel] Updated formData: { showTitle: true, title: "项目" }
```

### 如果 watch 没触发
```
(控制台没有任何 [ConfigPanel] 日志)
→ 说明 watch 没有被触发，需要检查依赖
```

---

## 🔧 可能的问题和解决方案

### 问题1: watch 根本不触发

**症状**：控制台没有任何日志

**原因**：
- `props.selectedItem?.i` 的值没有变化
- 或者 props 本身没有更新

**解决方案**：
检查父组件（designer/index.vue）：
```typescript
// 确保 selectedItemId 正确更新
const selectedItemId = ref<null | string>(null);

function handleItemSelected(itemId: string) {
  console.log('[Designer] Selected item changed to:', itemId);
  selectedItemId.value = itemId;
}
```

---

### 问题2: watch 触发但找不到组件

**症状**：
```
[ConfigPanel] Watch triggered
[ConfigPanel] Found component: undefined, undefined
```

**原因**：
- `props.components` 为空或未加载
- 组件 code 不匹配

**解决方案**：
```typescript
// 在 watch 中添加检查
if (!props.components || props.components.length === 0) {
  console.error('[ConfigPanel] Components not loaded!');
  return;
}
```

---

### 问题3: schema 解析失败

**症状**：
```
[ConfigPanel] Failed to parse config schema: SyntaxError
```

**原因**：
- configSchema 不是有效的 JSON
- 数据库中的数据有问题

**解决方案**：
```sql
-- 检查数据库中的 config_schema
SELECT code, name, config_schema 
FROM system_home_component 
WHERE code = 'analytics_visits';
```

---

### 问题4: formData 更新了但界面不刷新

**症状**：
```
[ConfigPanel] Updated formData: {...}
但界面上的表单没有变化
```

**原因**：
- 表单组件的 v-model 绑定有问题
- 或者是 Vue 的响应性失效

**解决方案**：
```typescript
// 强制触发响应性更新
formData.value = { ...newFormData };

// 或者使用 nextTick
import { nextTick } from 'vue';
nextTick(() => {
  formData.value = newFormData;
});
```

---

## 💡 调试技巧

### 技巧1: 使用 Vue DevTools
```
1. 安装 Vue DevTools 浏览器插件
2. 打开 Components 面板
3. 找到 ConfigPanel 组件
4. 查看 props 和 data 的实时值
```

### 技巧2: 添加更多日志
```typescript
watch(
  () => props.selectedItem?.i,
  (newId, oldId) => {
    console.log(`[ConfigPanel] ID changed from ${oldId} to ${newId}`);
    console.log('[ConfigPanel] selectedItem:', props.selectedItem);
    console.log('[ConfigPanel] components:', props.components);
    // ...
  }
);
```

### 技巧3: 临时禁用去重检查
```typescript
// 注释掉去重逻辑，看是否是去重导致的问题
// if (newId === lastSelectedItemId.value) {
//   return;
// }
```

---

## ✅ 验证修复成功

### 成功标准

1. **日志正确**：
   - ✅ 切换组件时看到 watch 触发日志
   - ✅ 能找到对应的组件定义
   - ✅ schema 解析成功
   - ✅ formData 更新成功

2. **界面正确**：
   - ✅ 切换组件时配置面板立即更新
   - ✅ 显示正确的配置项
   - ✅ 配置值正确显示
   - ✅ 修改配置后保存成功

3. **性能正常**：
   - ✅ 切换无延迟
   - ✅ 无卡顿
   - ✅ 控制台无错误

---

## 📞 如果问题仍然存在

**请提供以下信息**：

1. **浏览器控制台截图**：
   - 包含所有 [ConfigPanel] 开头的日志
   - 包含任何错误信息

2. **操作步骤录屏**：
   - 显示如何添加组件
   - 显示如何切换组件
   - 显示配置面板的表现

3. **Vue DevTools 截图**：
   - ConfigPanel 组件的 props
   - ConfigPanel 组件的 data
   - 父组件传递的 selectedItem 值

---

## 🎉 预期结果

修复后，配置面板应该：

1. ✅ **立即响应**：点击组件，配置面板立即更新
2. ✅ **准确无误**：显示正确的配置项和值
3. ✅ **稳定可靠**：100次切换都正常工作
4. ✅ **性能优秀**：无延迟、无卡顿

---

**现在请刷新页面，打开控制台，测试切换组件功能！**

查看控制台日志，告诉我你看到了什么！ 🔍
