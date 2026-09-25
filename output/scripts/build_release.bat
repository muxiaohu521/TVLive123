@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

:: ============================================================
:: 自动检测 JAVA_HOME (全盘扫描)
:: ============================================================
if not defined JAVA_HOME (
    for %%i in (java.exe) do (
        set JAVA_HOME=%%~dp$PATH:i..
        set JAVA_HOME=!JAVA_HOME!
    )
)
if not defined JAVA_HOME (
    set DRIVES=C: D: E: F: G: H: I: J: K: L: M: N: O: P: Q: R: S: T: U: V: W: X: Y: Z:
    for %%D in (!DRIVES!) do (
        if not defined JAVA_HOME (
            if exist "%%D\" (
                for /f "delims=" %%F in ('dir /s /b "%%D\javac.exe" 2^>nul ^| findstr /i "\\jdk"') do (
                    for %%J in ("%%~dpF.") do (
                        if exist "%%~dpF..\bin\javac.exe" set JAVA_HOME=%%~dpF..
                        if exist "%%~dpFjavac.exe"   set JAVA_HOME=%%~dpF
                    )
                    if defined JAVA_HOME goto :found_java
                )
            )
        )
    )
)
:found_java
if not defined JAVA_HOME (
    echo.
    echo ========================================
    echo   JDK 11+ 未检测到，正在自动下载...
    echo ========================================
    echo.
    powershell -ExecutionPolicy Bypass -Command ^
        "$jdkDir = Join-Path '%~dp0..\..\tools' 'jdk-11';" ^
        "if (-not (Test-Path $jdkDir)) { New-Item -ItemType Directory -Force -Path $jdkDir | Out-Null };" ^
        "try { $r = Invoke-RestMethod 'https://api.adoptium.net/v3/assets/latest/11/hotspot?os=windows&architecture=x64&image_type=jdk' -TimeoutSec 15; $url = $r[0].binaries[0].package.link; Write-Host '下载地址:' $url -ForegroundColor DarkGray } catch { $url = 'https://github.com/adoptium/temurin11-binaries/releases/download/jdk-11.0.26%%2B4/OpenJDK11U-jdk_x64_windows_hotspot_11.0.26_4.zip' };" ^
        "$zip = Join-Path $jdkDir 'jdk-11.zip';" ^
        "Write-Host '正在下载 JDK 11 (约190MB)...';" ^
        "$ProgressPreference = 'SilentlyContinue';" ^
        "try { Invoke-WebRequest -Uri $url -OutFile $zip -TimeoutSec 600 -ErrorAction Stop } catch { Write-Host '[ERROR] 下载失败，请手动安装 https://adoptium.net/download/' -ForegroundColor Red; exit 1 };" ^
        "Write-Host '正在解压...';" ^
        "Get-ChildItem $jdkDir -Exclude '*.zip' -ErrorAction SilentlyContinue | Remove-Item -Recurse -Force -ErrorAction SilentlyContinue;" ^
        "Expand-Archive -Path $zip -DestinationPath $jdkDir -Force; Remove-Item $zip -Force -ErrorAction SilentlyContinue;" ^
        "$found = Get-ChildItem $jdkDir -Directory | Where-Object { Test-Path \"$($_.FullName)\bin\javac.exe\" } | Select-Object -First 1;" ^
        "if ($found) { $env:JAVA_HOME = $found.FullName } elseif (Test-Path \"$jdkDir\bin\javac.exe\") { $env:JAVA_HOME = $jdkDir } else { Write-Host '[ERROR] 未找到 JDK' -ForegroundColor Red; exit 1 };" ^
        "[System.Environment]::SetEnvironmentVariable('JAVA_HOME', $env:JAVA_HOME, 'User');" ^
        "Write-Host 'JDK 已安装并写入系统环境变量:' $env:JAVA_HOME -ForegroundColor Green"
    if %errorlevel% neq 0 (
        echo.
        echo 自动下载失败，请手动安装 JDK 11+:
        echo   https://adoptium.net/download/
        pause
        exit /b 1
    )
    :: 重新读取刚写入的环境变量
    for /f "tokens=2* delims= =" %%A in ('reg query "HKCU\Environment" /v JAVA_HOME 2^>nul') do set JAVA_HOME=%%B
    if not defined JAVA_HOME (
        echo [ERROR] JDK 安装失败
        pause
        exit /b 1
    )
)

:: ============================================================
:: 自动检测 ANDROID_HOME
:: ============================================================
if not defined ANDROID_HOME (
    if exist "%LOCALAPPDATA%\Android\Sdk" set ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk
)
if not defined ANDROID_HOME (
    if exist "%APPDATA%\Android\Sdk" set ANDROID_HOME=%APPDATA%\Android\Sdk
)
if not defined ANDROID_HOME (
    for %%D in (C: D: E: F: G:) do (
        if not defined ANDROID_HOME (
            for /f "delims=" %%F in ('dir /s /b "%%D\adb.exe" 2^>nul ^| findstr /i "\\platform-tools\\"') do (
                for %%P in ("%%~dpF.") do set ANDROID_HOME=%%~dpF..
                if defined ANDROID_HOME goto :found_sdk
            )
        )
    )
)
:found_sdk
if not defined ANDROID_HOME (
    echo.
    echo ========================================
    echo   [WARNING] 未找到 Android SDK！
    echo ========================================
    echo.
    echo   建议安装 Android Studio:
    echo     https://developer.android.com/studio
    echo.
    echo   或手动设置:
    echo     set ANDROID_HOME=你的SDK路径
    echo.
)

set PATH=%JAVA_HOME%\bin;%ANDROID_HOME%\platform-tools;%PATH%

echo ========================================
echo JAVA_HOME = %JAVA_HOME%
if defined ANDROID_HOME echo ANDROID_HOME = %ANDROID_HOME%
echo ========================================

java -version
echo.

cd /d "%~dp0..\.."
call gradlew.bat assembleJava32Release %*