# ============================================================
# 自动检测 JAVA_HOME
# ============================================================
function Find-JavaHome {
    # 1. 检查系统环境变量
    if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\javac.exe")) {
        return $env:JAVA_HOME
    }
    # 2. 检查 PATH 中的 java
    $javaCmd = Get-Command java -ErrorAction SilentlyContinue
    if ($javaCmd) {
        $dir = Split-Path (Split-Path $javaCmd.Source -Parent) -Parent
        if (Test-Path "$dir\bin\javac.exe") { return $dir }
    }
    # 3. 扫描所有盘符下的常见安装位置
    $searchRoots = @()
    Get-PSDrive -PSProvider FileSystem | ForEach-Object { $searchRoots += $_.Root }
    foreach ($root in $searchRoots) {
        $candidates = @(
            "$root\Program Files\Java",
            "$root\Program Files (x86)\Java",
            "$root\Program Files\Eclipse Adoptium",
            "$root\Program Files\Microsoft",
            "$root\Program Files\Zulu",
            "$root\Program Files\Amazon Corretto",
            "$root\AI",
            "$root\tools",
            "$root\Tool",
            "$root\Dev",
            "$root\SDK"
        )
        foreach ($base in $candidates) {
            if (-not (Test-Path $base)) { continue }
            $found = Get-ChildItem -Path $base -Recurse -Filter "javac.exe" -Depth 4 -ErrorAction SilentlyContinue | Select-Object -First 1
            if ($found) {
                $jdkDir = $found.Directory.Parent.FullName
                if (Test-Path "$jdkDir\bin\javac.exe") { return $jdkDir }
                $jdkDir = $found.Directory.FullName
                if (Test-Path "$jdkDir\javac.exe") { return (Split-Path $jdkDir -Parent) }
            }
        }
    }
    return $null
}

$env:JAVA_HOME = Find-JavaHome

if (-not $env:JAVA_HOME) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Yellow
    Write-Host "  JDK 11+ 未检测到，正在自动下载..." -ForegroundColor Yellow
    Write-Host "========================================" -ForegroundColor Yellow
    Write-Host ""

    $jdkLocalDir = Join-Path "$PSScriptRoot\..\..\tools" "jdk-11"
    if (-not (Test-Path $jdkLocalDir)) {
        New-Item -ItemType Directory -Force -Path $jdkLocalDir | Out-Null
    }

    # 从 Adoptium API 获取最新 JDK 11 下载地址
    try {
        $releaseJson = Invoke-RestMethod -Uri "https://api.adoptium.net/v3/assets/latest/11/hotspot?os=windows&architecture=x64&image_type=jdk" -TimeoutSec 15
        $package = $releaseJson | Select-Object -First 1
        if (-not $package) { throw "API returned empty" }
        $downloadUrl = $package.binaries[0].package.link
        $versionStr  = $package.version.openjdk_version
        Write-Host "  最新版本: $versionStr" -ForegroundColor Cyan
        Write-Host "  下载地址: $downloadUrl" -ForegroundColor DarkGray
    } catch {
        Write-Host "  [WARNING] 无法获取最新版本，使用固定链接" -ForegroundColor Yellow
        $downloadUrl = "https://github.com/adoptium/temurin11-binaries/releases/download/jdk-11.0.26%2B4/OpenJDK11U-jdk_x64_windows_hotspot_11.0.26_4.zip"
        $versionStr  = "11.0.26"
    }

    $zipFile = Join-Path $jdkLocalDir "jdk-11.zip"
    $extractDir = $jdkLocalDir

    # 下载 JDK
    Write-Host "  正在下载 JDK 11 (约 190MB)..." -ForegroundColor Cyan
    try {
        $ProgressPreference = 'SilentlyContinue'
        Invoke-WebRequest -Uri $downloadUrl -OutFile $zipFile -TimeoutSec 600 -ErrorAction Stop
        Write-Host "  下载完成！" -ForegroundColor Green
    } catch {
        Write-Host "  [ERROR] 下载失败: $_" -ForegroundColor Red
        Write-Host ""
        Write-Host "  请手动安装 JDK 11+:" -ForegroundColor Yellow
        Write-Host "    https://adoptium.net/download/" -ForegroundColor Gray
        Write-Host "    或设置环境变量: `$env:JAVA_HOME = ""你的JDK路径""" -ForegroundColor Gray
        Write-Host ""
        exit 1
    }

    # 解压
    Write-Host "  正在解压..." -ForegroundColor Cyan
    try {
        # 清理解压目录旧文件
        Get-ChildItem -Path $extractDir -Exclude "*.zip" -ErrorAction SilentlyContinue | Remove-Item -Recurse -Force -ErrorAction SilentlyContinue
        Expand-Archive -Path $zipFile -DestinationPath $extractDir -Force
        Remove-Item -Path $zipFile -Force -ErrorAction SilentlyContinue
    } catch {
        Write-Host "  [ERROR] 解压失败: $_" -ForegroundColor Red
        exit 1
    }

    # 找到解压后的 JDK 根目录（可能嵌套了一层）
    $extractedJdk = Get-ChildItem -Path $extractDir -Directory | Where-Object { Test-Path "$($_.FullName)\bin\javac.exe" } | Select-Object -First 1
    if ($extractedJdk) {
        $env:JAVA_HOME = $extractedJdk.FullName
    } else {
        # 可能直接解压在根目录
        if (Test-Path "$extractDir\bin\javac.exe") {
            $env:JAVA_HOME = $extractDir
        } else {
            Write-Host "  [ERROR] 未找到解压后的 JDK 目录" -ForegroundColor Red
            exit 1
        }
    }

    Write-Host "  JDK 安装完成: $env:JAVA_HOME" -ForegroundColor Green
    Write-Host ""
}

# ============================================================
# 自动检测 ANDROID_HOME
# ============================================================
function Find-AndroidHome {
    if ($env:ANDROID_HOME -and (Test-Path "$env:ANDROID_HOME\platform-tools\adb.exe")) {
        return $env:ANDROID_HOME
    }
    $searchRoots = @()
    Get-PSDrive -PSProvider FileSystem | ForEach-Object { $searchRoots += $_.Root }
    foreach ($root in $searchRoots) {
        $candidates = @(
            "$root\Users\*\AppData\Local\Android\Sdk",
            "$root\Users\*\AppData\Roaming\Android\Sdk",
            "$root\Android\Sdk",
            "$root\SDK\Android"
        )
        foreach ($base in $candidates) {
            $found = Get-ChildItem -Path $base -Filter "adb.exe" -Recurse -Depth 2 -ErrorAction SilentlyContinue | Select-Object -First 1
            if ($found) {
                return $found.Directory.Parent.FullName
            }
        }
    }
    return $null
}

$env:ANDROID_HOME = Find-AndroidHome

if (-not $env:ANDROID_HOME) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Yellow
    Write-Host "  [WARNING] 未找到 Android SDK！" -ForegroundColor Yellow
    Write-Host "========================================" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "  建议安装 Android Studio:" -ForegroundColor Cyan
    Write-Host "    https://developer.android.com/studio" -ForegroundColor Gray
    Write-Host ""
    Write-Host "  或手动设置:" -ForegroundColor Cyan
    Write-Host "    `$env:ANDROID_HOME = ""你的Android SDK路径""" -ForegroundColor Gray
    Write-Host ""
}

$env:Path = "$env:JAVA_HOME\bin;$env:ANDROID_HOME\platform-tools;$env:Path"

Write-Host "========================================"
Write-Host "JAVA_HOME = $env:JAVA_HOME"
if ($env:ANDROID_HOME) { Write-Host "ANDROID_HOME = $env:ANDROID_HOME" }
Write-Host "========================================"

& java -version
Write-Host ""

Set-Location "$PSScriptRoot\..\.."
& .\gradlew.bat assembleJava32Release @args