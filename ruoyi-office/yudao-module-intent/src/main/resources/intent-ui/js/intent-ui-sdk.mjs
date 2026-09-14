/**
 * ESM 入口：构建式宿主（Vite / webpack / Rollup）用 `import { IntentUI } from 'intent-ui-sdk'`。
 * 源码本体是框架无关的原生 JS（js/intent-ui-sdk.js），挂在 window.IntentUI 上；
 * 这里只把它转成模块导出，避免宿主被迫写 <script> 标签。
 */
import './intent-ui-sdk.js';

const IntentUI = globalThis.IntentUI;

export { IntentUI };
export default IntentUI;