# 意图前端（Vben 宿主）

**意图面板的唯一实现是原生 JS SDK**（本目录不再有面板渲染代码）：

```
yudao-module-intent/src/main/resources/intent-ui/   ← 真源（后端静态资源，同时就是 npm 包）
├─ js/intent-ui-sdk.js     框架无关的原生实现（悬浮入口 / 菜单 / 表单 / 卡片 / 待办 / 历史 / 反馈）
├─ js/intent-ui-sdk.mjs    ESM 入口：import { IntentUI } from 'intent-ui-sdk'
├─ js/intent-ui-sdk.d.ts   类型声明
└─ css/intent-ui.css       设计系统（类名与 SDK 一一对应）
```

宿主侧只有"注入 + 挂载"：

| 文件 | 定位 |
| --- | --- |
| `sdk.ts` | 宿主注入点（幂等）：apiPrefix / 鉴权头 / 实体候选下拉 / 层级 / 文案 |
| `components/IntentFloating.vue` | 全局浮标 + 抽屉的薄适配器（约 60 行：`mountFloating` + 生命周期收尾） |
| `pageContext.ts` | 页面上下文注册中心（业务页面声明"本页能提供什么上下文"） |
| `center/index.vue` | 意图调试台：`IntentUI.mount({ mode: 'debug' })`（全量目录，点意图先填槽位） |
| `config/index.vue`、`executor/index.vue` | 意图管理后台（上架配置 / 执行器档案）——管理端，不是嵌入式组件 |

## 接入方式

SDK 以 npm 依赖（`link:`）指向后端真源：零拷贝，改 SDK 立刻 HMR 生效。

```json
// apps/web-antd/package.json
"intent-ui-sdk": "link:../../../ruoyi-office/yudao-module-intent/src/main/resources/intent-ui"
```

对接外部宿主时把 `link:` 换成发布版本号，或直接 `<script src="/intent-ui/js/intent-ui-sdk.js">`。

## 维护约定

- **新增/修改意图交互**：改 `intent-ui-sdk.js` + `intent-ui.css`（一份，所有宿主共用）；
- **本目录的 Vue 文件**：只做宿主适配（注入、挂载、上下文、管理端页面），不写面板渲染逻辑；
- 两者出现功能差异时，**以 SDK 为准**。曾经并存的 `IntentRunner.vue` 已删除。