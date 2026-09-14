import { fileURLToPath, URL } from 'node:url';

import { defineConfig } from '@vben/vite-config';
import { searchForWorkspaceRoot } from 'vite';
import type { Plugin } from 'vite';

/** 意图 UI SDK 的真源目录：后端 yudao-module-intent 静态资源（classpath:/intent-ui/），此处按 link: 依赖引入 */
const INTENT_UI_SDK_DIR = fileURLToPath(
  new URL(
    '../../../ruoyi-office/yudao-module-intent/src/main/resources/intent-ui',
    import.meta.url,
  ),
);

/**
 * 排除指定目录的插件
 * 在构建时排除 src/views/asset 和 src/views/wms 目录
 */
function excludeDirectoriesPlugin(): Plugin {
  return {
    name: 'exclude-directories',
    resolveId(id) {
      // 排除 asset 和 wms 目录下的所有文件
      if (
        id.includes('/src/views/asset/') ||
        id.includes('/src/views/wms/') ||
        id.includes('src/views/asset') ||
        id.includes('src/views/wms') ||
        id.includes('\\src\\views\\asset\\') ||
        id.includes('\\src\\views\\wms\\')
      ) {
        // 返回虚拟模块 ID，标记为外部模块
        return { id: '\0excluded', external: true };
      }
      return null;
    },
    load(id) {
      // 如果是排除的模块，返回空内容避免构建错误
      if (id === '\0excluded') {
        return 'export default {};';
      }
      return null;
    },
  };
}

// @ts-ignore - defineConfig 类型推断问题，不影响运行
export default defineConfig(async () => {
  return {
    application: {},
    vite: {
      // 生产环境部署到 /web 路径下
      base: process.env.NODE_ENV === 'production' ? '/web/' : '/',
      server: {
        allowedHosts: true,
        fs: {
          // Vite 一旦显式设置 fs.allow 就会覆盖默认值（默认 = 工作区根），所以两者都要写：
          // 工作区根照旧放行，另加 SDK 真源目录（它是工作区外的 link: 依赖）。
          allow: [searchForWorkspaceRoot(process.cwd()), INTENT_UI_SDK_DIR],
        },
        proxy: {
          '/admin-api': {
            changeOrigin: true,
            rewrite: (path: string) => path.replace(/^\/admin-api/, ''),
            // mock代理目标地址
            target: 'http://localhost:48080/admin-api',
            ws: true,
          },
        },
      },
      build: {
        rollupOptions: {
          plugins: [
            // 排除 asset 和 wms 目录
            excludeDirectoriesPlugin(),
          ],
        },
      },
    },
  };
});
