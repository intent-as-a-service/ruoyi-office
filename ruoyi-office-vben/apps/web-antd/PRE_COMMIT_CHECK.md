# 提交前检查指南

## 🎯 目的

在提交代码到 Git 之前进行全面检查，确保：

- ✅ TypeScript 类型正确
- ✅ 代码符合规范（ESLint）
- ✅ 构建能够成功
- ✅ 避免在 Jenkins CI 中构建失败

## 🚀 快速使用

### Windows (PowerShell)

```powershell
# 进入 web-antd 目录
cd w:\ruoyi-office\ruoyi-office-vben\apps\web-antd

# 运行检查脚本
powershell -ExecutionPolicy Bypass -File pre-commit-check.ps1
```

### Linux / macOS / Git Bash

```bash
# 进入 web-antd 目录
cd /w/ruoyi-office/ruoyi-office-vben/apps/web-antd

# 给脚本添加执行权限（首次运行）
chmod +x pre-commit-check.sh

# 运行检查脚本
./pre-commit-check.sh
```

## 📋 检查内容

脚本会依次执行以下检查：

### 1. TypeScript 类型检查 (typecheck)

- **作用**: 检查 TypeScript 类型错误
- **失败原因**: 类型不匹配、缺少类型定义等
- **修复**: 根据错误提示修改代码

### 2. ESLint 代码规范检查 (lint)

- **作用**: 检查代码规范和潜在错误
- **失败原因**: 代码格式、未使用的变量、错误的语法等
- **修复**:
  - 手动修改代码
  - 或运行 `pnpm run lint:fix` 自动修复

### 3. 导入路径检查

- **作用**: 检查相对路径导入是否正确
- **失败原因**: 文件路径错误、文件不存在
- **修复**: 修正导入路径

### 4. 构建测试 (build)

- **作用**: 完整构建应用，确保能在 Jenkins 中成功构建
- **失败原因**:
  - 模块解析失败
  - TypeScript 编译错误
  - 资源加载失败
- **修复**: 根据构建日志修复错误

### 5. Git 状态检查

- **作用**: 显示 Git 工作区状态
- **信息**:
  - 未跟踪的文件
  - 未暂存的修改
  - 已暂存的文件

## ⚡ 快捷命令

### 单独执行检查

```bash
# TypeScript 类型检查
pnpm run typecheck

# ESLint 检查
pnpm run lint

# ESLint 自动修复
pnpm run lint:fix

# 构建
pnpm run build

# 清理并重新构建
pnpm clean && pnpm install && pnpm run build
```

### 组合检查（推荐）

```bash
# 检查 + 修复 + 构建
pnpm run lint:fix && pnpm run typecheck && pnpm run build
```

## 🔧 常见问题

### Q1: TypeScript 检查失败

**问题**: `pnpm run typecheck` 报错

**解决**:

1. 查看具体的类型错误
2. 修复类型定义或添加类型注解
3. 如果是依赖包的类型问题，可能需要安装 `@types/*` 包

### Q2: ESLint 检查失败

**问题**: `pnpm run lint` 报错

**解决**:

1. 尝试自动修复: `pnpm run lint:fix`
2. 如果自动修复无效，手动修改代码
3. 查看 `.eslintrc.js` 了解规则配置

### Q3: 构建失败 - 找不到模块

**问题**: `Could not resolve "../data"`

**解决**:

1. 检查导入路径是否正确
2. 确认目标文件是否存在
3. 检查文件扩展名（.ts, .tsx, .vue）
4. 考虑使用绝对路径（`#/` 别名）

示例：

```typescript
// ❌ 错误（文件不在这个位置）
import { useFormSchema } from '../data';

// ✅ 正确（使用正确的相对路径）
import { useFormSchema } from '../list/data';

// ✅ 或使用绝对路径
import { useFormSchema } from '#/views/wms/purchaseinwarehousing/list/data';
```

### Q4: 构建失败 - Monorepo 包问题

**问题**: `Failed to resolve entry for package "@vben-core/design"`

**解决**:

1. 清理依赖: `pnpm clean`
2. 重新安装: `pnpm install`
3. 构建内部包: `pnpm -r --filter "./packages/**" build`
4. 再构建应用: `pnpm run build`

### Q5: 脚本权限问题（Linux/macOS）

**问题**: `Permission denied`

**解决**:

```bash
chmod +x pre-commit-check.sh
```

### Q6: PowerShell 执行策略限制

**问题**: `无法加载文件，因为在此系统上禁止运行脚本`

**解决**:

```powershell
# 临时允许执行脚本
powershell -ExecutionPolicy Bypass -File pre-commit-check.ps1

# 或永久修改策略（管理员权限）
Set-ExecutionPolicy RemoteSigned
```

## 📊 检查时间参考

| 检查项          | 预计时间       |
| --------------- | -------------- |
| TypeScript 检查 | 10-30秒        |
| ESLint 检查     | 10-30秒        |
| 导入路径检查    | 5-10秒         |
| 完整构建        | 1-3分钟        |
| **总计**        | **约 2-4分钟** |

## 💡 最佳实践

### 1. 定期检查

```bash
# 开发过程中，每完成一个功能就检查一次
pnpm run typecheck && pnpm run lint

# 提交前必须运行完整检查
./pre-commit-check.sh
```

### 2. 使用 Git Hooks

可以配置 Git hooks 自动运行检查：

创建 `.git/hooks/pre-commit` 文件：

```bash
#!/bin/bash
cd apps/web-antd
./pre-commit-check.sh
```

### 3. 结合 IDE

配置 VSCode 或其他 IDE：

- 启用 TypeScript 检查
- 启用 ESLint 自动修复
- 配置保存时自动格式化

### 4. CI/CD 保持一致

确保本地检查与 Jenkins 构建使用相同的命令和配置。

## 🎓 学习资源

- [TypeScript 文档](https://www.typescriptlang.org/)
- [ESLint 文档](https://eslint.org/)
- [Vite 构建文档](https://vitejs.dev/guide/build.html)
- [pnpm 文档](https://pnpm.io/)

## 📝 提交流程建议

```bash
# 1. 开发完成后，运行检查
./pre-commit-check.sh

# 2. 检查通过后，查看修改
git status
git diff

# 3. 暂存修改
git add .

# 4. 提交
git commit -m "feat: 添加新功能"

# 5. 推送
git push
```

## ⚠️ 注意事项

1. **构建产物不要提交**: `dist/` 目录已在 `.gitignore` 中，不会被提交
2. **依赖文件要提交**: `pnpm-lock.yaml` 应该提交，确保依赖一致
3. **检查通过不保证 100% 没问题**: 但能大大降低 CI 构建失败的概率
4. **首次运行会慢**: 需要下载依赖和构建，后续会快很多

## 🆘 需要帮助？

如果遇到问题：

1. 查看检查日志，定位错误
2. 参考 [故障排查文档](./docs/部署.md#故障排查)
3. 查看 Jenkins 构建日志对比
4. 联系团队其他成员

---

**最后更新**: 2026-01-21  
**维护者**: DevOps Team
