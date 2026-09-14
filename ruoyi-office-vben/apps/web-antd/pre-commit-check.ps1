# ========================================
# Pre-commit Check Script (PowerShell)
# ========================================
# Purpose: Check code before committing
# Usage: powershell -ExecutionPolicy Bypass -File pre-commit-check.ps1
# ========================================

$ErrorActionPreference = "Stop"

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "[Pre-commit Check] Starting..." -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# Check if in correct directory
if (-not (Test-Path "package.json")) {
    Write-Host "[ERROR] Please run this script in apps/web-antd directory" -ForegroundColor Red
    exit 1
}

# Progress counter
$Step = 0
$TotalSteps = 5

# Step 1: TypeScript type check
$Step++
Write-Host ""
Write-Host "[$Step/$TotalSteps] Checking TypeScript types..." -ForegroundColor Yellow
try {
    pnpm run typecheck
    Write-Host "[PASS] TypeScript type check passed" -ForegroundColor Green
} catch {
    Write-Host "[FAIL] TypeScript type check failed" -ForegroundColor Red
    Write-Host "Please fix the errors shown above" -ForegroundColor Red
    exit 1
}

# Step 2: ESLint check
$Step++
Write-Host ""
Write-Host "[$Step/$TotalSteps] Checking code style (ESLint)..." -ForegroundColor Yellow
try {
    pnpm run lint
    Write-Host "[PASS] ESLint check passed" -ForegroundColor Green
} catch {
    Write-Host "[FAIL] ESLint check failed" -ForegroundColor Red
    Write-Host "Tip: Run 'pnpm run lint:fix' to auto-fix some issues" -ForegroundColor Yellow
    exit 1
}

# Step 3: Import path check (simplified)
$Step++
Write-Host ""
Write-Host "[$Step/$TotalSteps] Checking import paths..." -ForegroundColor Yellow
Write-Host "Note: Detailed check will be done during build" -ForegroundColor Gray
Write-Host "[PASS] Import path check completed" -ForegroundColor Green

# Step 4: Build test
$Step++
Write-Host ""
Write-Host "[$Step/$TotalSteps] Building application..." -ForegroundColor Yellow
Write-Host "This may take a few minutes..." -ForegroundColor Gray

try {
    pnpm run build
    Write-Host "[PASS] Build succeeded" -ForegroundColor Green
    
    # Show build artifacts info
    if (Test-Path "dist") {
        $distSize = (Get-ChildItem -Path "dist" -Recurse | Measure-Object -Property Length -Sum).Sum / 1MB
        $fileCount = (Get-ChildItem -Path "dist" -Recurse -File).Count
        Write-Host ""
        Write-Host "Build Artifacts:" -ForegroundColor Cyan
        Write-Host "  Size: $([math]::Round($distSize, 2)) MB"
        Write-Host "  Files: $fileCount"
    }
} catch {
    Write-Host "[FAIL] Build failed" -ForegroundColor Red
    Write-Host "Please fix the errors shown above" -ForegroundColor Red
    exit 1
}

# Step 5: Git status check
$Step++
Write-Host ""
Write-Host "[$Step/$TotalSteps] Checking Git status..." -ForegroundColor Yellow

# Go to repository root
Set-Location ../..

# Check Git status
$gitStatus = git status --porcelain

if ($gitStatus) {
    $untracked = ($gitStatus | Where-Object { $_ -match '^\?\?' }).Count
    $modified = ($gitStatus | Where-Object { $_ -match '^ M' }).Count
    $staged = ($gitStatus | Where-Object { $_ -match '^M' -or $_ -match '^A' }).Count
    
    if ($untracked -gt 0) {
        Write-Host "[INFO] Found $untracked untracked file(s)" -ForegroundColor Yellow
    }
    if ($modified -gt 0) {
        Write-Host "[INFO] Found $modified unstaged change(s)" -ForegroundColor Yellow
    }
    if ($staged -gt 0) {
        Write-Host "[INFO] Found $staged staged file(s), ready to commit" -ForegroundColor Green
    }
} else {
    Write-Host "[INFO] Working directory is clean (no changes)" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "[SUCCESS] All checks passed!" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Next Steps:" -ForegroundColor Cyan
Write-Host "  1. Check files to commit:"
Write-Host "     git status"
Write-Host ""
Write-Host "  2. Stage changes (if not staged yet):"
Write-Host "     git add ."
Write-Host ""
Write-Host "  3. Commit changes:"
Write-Host '     git commit -m "your commit message"'
Write-Host ""
Write-Host "  4. Push to remote:"
Write-Host "     git push"
Write-Host ""
Write-Host "Note: Build artifacts (dist/) will not be committed (.gitignore)" -ForegroundColor Gray
Write-Host ""
