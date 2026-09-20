<#
  ruoyi-office 本地开发环境一键启动
  ============================================================================
  起三个东西（MySQL 是 Windows 服务，开机自启，这里不管）：

    1. Redis     127.0.0.1:6379
    2. 后端      127.0.0.1:48080   （yudao-server，连 3306 的 ruoyi-office 库）
    3. 前端      127.0.0.1:5666    （vben web-antd 开发服务器）

  用法：  pwsh -File .\start-dev.ps1
  停止：  各窗口 Ctrl+C，或 pwsh -File .\stop-dev.ps1

  为什么不用 `pnpm dev`：Vite 的 esbuild 需要命名管道，受限沙箱下 pnpm 包装器会
  直接 spawn EPERM。这里用 node 直接调 vite 的 bin，绕开包装器，行为完全一致。
#>

$ErrorActionPreference = 'Continue'
$Root      = Split-Path -Parent $MyInvocation.MyCommand.Path
$Vben      = Join-Path $Root 'ruoyi-office-vben'
$Jar       = Join-Path $Root 'ruoyi-office\yudao-server\target\yudao-server.jar'
$RedisExe  = 'D:\software\Redis-x64-5.0.14.1\redis-server.exe'
$RedisConf = Join-Path $env:TEMP 'ruoyi-office-redis.conf'
$JavaExe   = 'D:\software\jdk-21.0.5\bin\java.exe'
$LogDir    = Join-Path $Root 'logs'
$ViteBin   = Join-Path $Vben 'node_modules\vite\bin\vite.js'

New-Item -ItemType Directory -Force -Path $LogDir | Out-Null

# 本地密钥（未跟踪）：DEEPSEEK_API_KEY 等。没有这个文件也不影响启动，
# 只是 agent 型意图会因为没有 Key 而失败（报 LLM_ERROR: No API key for provider）。
# 用 dot-source 加载，变量会进入本进程环境，随后 Start-Process 起的后端自动继承。
$EnvFile = Join-Path $Root '.env.local.ps1'
if (Test-Path $EnvFile) {
  . $EnvFile
  Write-Host '[+] 已加载本地密钥 .env.local.ps1' -ForegroundColor Green
} else {
  Write-Host '[!] 未找到 .env.local.ps1 —— agent 型意图会因缺少 DEEPSEEK_API_KEY 失败' -ForegroundColor Yellow
}

function Test-Port([int]$Port) {
  return [bool](Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue)
}

Write-Host '=== ruoyi-office 本地开发环境 ===' -ForegroundColor Cyan

# ---------------------------------------------------------------- Redis
if (Test-Port 6379) {
  Write-Host '[=] Redis      已在运行 (6379)'
} else {
  @"
port 6379
bind 127.0.0.1
protected-mode no
save ""
appendonly no
dir $LogDir
"@ | Set-Content -Path $RedisConf -Encoding ASCII
  Start-Process -FilePath $RedisExe -ArgumentList $RedisConf -WindowStyle Hidden
  Start-Sleep -Seconds 2
  if (Test-Port 6379) { Write-Host '[+] Redis      已启动 (6379)' -ForegroundColor Green }
  else { Write-Host '[x] Redis      启动失败' -ForegroundColor Red }
}

# ---------------------------------------------------------------- 后端
if (Test-Port 48080) {
  Write-Host '[=] 后端       已在运行 (48080)'
} else {
  if (-not (Test-Path $Jar)) {
    Write-Host "[x] 找不到 $Jar" -ForegroundColor Red
    Write-Host '    先构建：cd ruoyi-office; mvn -o -Pboot clean package -DskipTests -pl :yudao-module-crm-server,:yudao-module-intent,:yudao-server'
  } else {
    # 日志必须落在可写目录：默认会写 %USERPROFILE%\logs，受限沙箱下会因权限启动失败
    Start-Process -FilePath $JavaExe -WindowStyle Hidden -ArgumentList @(
      "-Dlogging.file.name=$LogDir/yudao-server.log".Replace('\', '/'),
      '-jar', $Jar, '--spring.profiles.active=local'
    ) -RedirectStandardOutput (Join-Path $LogDir 'backend.out.log') `
      -RedirectStandardError  (Join-Path $LogDir 'backend.err.log')
    Write-Host '[.] 后端       启动中，约 60-90 秒…' -ForegroundColor Yellow
    for ($i = 0; $i -lt 45; $i++) {
      Start-Sleep -Seconds 3
      if (Test-Port 48080) { break }
    }
    if (Test-Port 48080) { Write-Host '[+] 后端       已启动 (48080)' -ForegroundColor Green }
    else { Write-Host "[x] 后端       未起来，看 $LogDir\yudao-server.log" -ForegroundColor Red }
  }
}

# ---------------------------------------------------------------- 前端
if (Test-Port 5666) {
  Write-Host '[=] 前端       已在运行 (5666)'
} else {
  if (-not (Test-Path $ViteBin)) {
    Write-Host "[x] 找不到 $ViteBin（先在 ruoyi-office-vben 里 pnpm install）" -ForegroundColor Red
  } else {
    Start-Process -FilePath 'node' -ArgumentList @($ViteBin, '--mode', 'development', '--port', '5666', '--host', '127.0.0.1') `
      -WorkingDirectory (Join-Path $Vben 'apps\web-antd') -WindowStyle Hidden `
      -RedirectStandardOutput (Join-Path $LogDir 'frontend.out.log') `
      -RedirectStandardError  (Join-Path $LogDir 'frontend.err.log')
    Write-Host '[.] 前端       启动中，约 20-40 秒…' -ForegroundColor Yellow
    for ($i = 0; $i -lt 30; $i++) {
      Start-Sleep -Seconds 2
      if (Test-Port 5666) { break }
    }
    if (Test-Port 5666) { Write-Host '[+] 前端       已启动 (5666)' -ForegroundColor Green }
    else { Write-Host "[x] 前端       未起来，看 $LogDir\frontend.err.log" -ForegroundColor Red }
  }
}

Write-Host ''
Write-Host '访问地址' -ForegroundColor Cyan
Write-Host '  后台管理    http://127.0.0.1:5666/                admin / admin123'
Write-Host '  意图工作台  http://127.0.0.1:48080/intent-ui/      admin / admin123'
Write-Host '  预览页      http://127.0.0.1:48080/intent-ui/preview.html   （免登录，假数据）'
Write-Host ''
Write-Host "数据库        MySQL 3306 / ruoyi-office    root / $(if ($env:MYSQL_PASSWORD) { $env:MYSQL_PASSWORD } else { '（未设 MYSQL_PASSWORD，用缺省 123456）' })"
Write-Host ''
Write-Host '注意：agent 型意图需要 DEEPSEEK_API_KEY，未配置时这类意图会报 LLM_ERROR。' -ForegroundColor Yellow
