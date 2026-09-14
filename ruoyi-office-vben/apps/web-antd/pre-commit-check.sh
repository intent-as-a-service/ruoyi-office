#!/bin/bash

# ========================================
# 提交前检查脚本
# ========================================
# 用途：在提交代码前进行全面检查
# 使用：bash pre-commit-check.sh
# ========================================

set -e  # 遇到错误立即退出

echo "=========================================="
echo "🚀 开始提交前检查..."
echo "=========================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查是否在正确的目录
if [ ! -f "package.json" ]; then
    echo -e "${RED}❌ 错误：请在 apps/web-antd 目录下运行此脚本${NC}"
    exit 1
fi

# 进度计数
STEP=0
TOTAL_STEPS=5

# 步骤1：检查 TypeScript 类型
STEP=$((STEP+1))
echo ""
echo -e "${YELLOW}[$STEP/$TOTAL_STEPS] 🔍 检查 TypeScript 类型...${NC}"
if pnpm run typecheck 2>&1 | tee /tmp/typecheck.log; then
    echo -e "${GREEN}✓ TypeScript 类型检查通过${NC}"
else
    echo -e "${RED}✗ TypeScript 类型检查失败${NC}"
    echo "请查看上方错误信息并修复"
    exit 1
fi

# 步骤2：ESLint 检查
STEP=$((STEP+1))
echo ""
echo -e "${YELLOW}[$STEP/$TOTAL_STEPS] 📝 检查代码规范 (ESLint)...${NC}"
if pnpm run lint 2>&1 | tee /tmp/lint.log; then
    echo -e "${GREEN}✓ ESLint 检查通过${NC}"
else
    echo -e "${RED}✗ ESLint 检查失败${NC}"
    echo "提示：可以运行 'pnpm run lint:fix' 自动修复部分问题"
    exit 1
fi

# 步骤3：检查未解析的导入路径
STEP=$((STEP+1))
echo ""
echo -e "${YELLOW}[$STEP/$TOTAL_STEPS] 🔗 检查导入路径...${NC}"
IMPORT_ERRORS=0

# 检查相对路径导入是否指向存在的文件
echo "检查 src/views/ 目录下的导入..."
while IFS= read -r file; do
    # 提取 from 语句中的相对路径
    grep -E "from ['\"]\.\.?/" "$file" | while read -r line; do
        # 提取路径
        path=$(echo "$line" | sed -E "s/.*from ['\"](\.\.?\/[^'\"]+)['\"].*/\1/")
        
        # 获取文件所在目录
        dir=$(dirname "$file")
        
        # 解析相对路径（简化版，可能需要改进）
        if [[ "$path" == ../* ]]; then
            target="$dir/../${path#../}"
        elif [[ "$path" == ./* ]]; then
            target="$dir/${path#./}"
        else
            target="$dir/$path"
        fi
        
        # 检查文件是否存在（添加可能的扩展名）
        if [ ! -f "$target" ] && [ ! -f "$target.ts" ] && [ ! -f "$target.tsx" ] && [ ! -f "$target.vue" ] && [ ! -f "$target/index.ts" ]; then
            echo -e "${RED}  ✗ 可能的路径错误: $file${NC}"
            echo "    导入: $path"
            echo "    目标: $target (或 .ts/.tsx/.vue/index.ts)"
            IMPORT_ERRORS=$((IMPORT_ERRORS+1))
        fi
    done
done < <(find src/views -name "*.vue" -o -name "*.ts" -o -name "*.tsx")

if [ $IMPORT_ERRORS -gt 0 ]; then
    echo -e "${RED}✗ 发现 $IMPORT_ERRORS 个可能的导入路径问题${NC}"
    echo "注意：此检查可能有误报，请结合构建结果判断"
else
    echo -e "${GREEN}✓ 导入路径检查通过（未发现明显错误）${NC}"
fi

# 步骤4：尝试构建
STEP=$((STEP+1))
echo ""
echo -e "${YELLOW}[$STEP/$TOTAL_STEPS] 🏗️  尝试构建应用...${NC}"
echo "这可能需要几分钟..."

if pnpm run build 2>&1 | tee /tmp/build.log; then
    echo -e "${GREEN}✓ 构建成功${NC}"
    
    # 显示构建产物信息
    if [ -d "dist" ]; then
        echo ""
        echo "📦 构建产物信息："
        echo "  大小: $(du -sh dist | cut -f1)"
        echo "  文件数: $(find dist -type f | wc -l)"
    fi
else
    echo -e "${RED}✗ 构建失败${NC}"
    echo "请查看上方错误信息并修复"
    echo "构建日志已保存到: /tmp/build.log"
    exit 1
fi

# 步骤5：Git 状态检查
STEP=$((STEP+1))
echo ""
echo -e "${YELLOW}[$STEP/$TOTAL_STEPS] 📋 检查 Git 状态...${NC}"

# 回到仓库根目录
cd ../../

# 检查是否有未跟踪的文件
UNTRACKED=$(git ls-files --others --exclude-standard | wc -l)
if [ $UNTRACKED -gt 0 ]; then
    echo -e "${YELLOW}⚠️  发现 $UNTRACKED 个未跟踪的文件${NC}"
    echo "运行 'git status' 查看详情"
fi

# 检查是否有未暂存的修改
UNSTAGED=$(git diff --name-only | wc -l)
if [ $UNSTAGED -gt 0 ]; then
    echo -e "${YELLOW}⚠️  发现 $UNSTAGED 个未暂存的修改${NC}"
    echo "运行 'git add <files>' 暂存修改"
fi

# 检查是否有已暂存的修改
STAGED=$(git diff --cached --name-only | wc -l)
if [ $STAGED -gt 0 ]; then
    echo -e "${GREEN}✓ 发现 $STAGED 个已暂存的文件，准备提交${NC}"
fi

echo ""
echo "=========================================="
echo -e "${GREEN}✅ 所有检查通过！${NC}"
echo "=========================================="
echo ""
echo "📝 下一步操作："
echo "  1. 查看要提交的文件:"
echo "     git status"
echo ""
echo "  2. 提交更改:"
echo "     git commit -m \"你的提交信息\""
echo ""
echo "  3. 推送到远程:"
echo "     git push"
echo ""
echo "💡 提示: 构建产物 (dist/) 不会被提交（已在 .gitignore 中）"
echo ""
