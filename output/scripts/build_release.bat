@echo off
set JAVA_HOME=D:\AI\AI-Code\tools\app\jdk-11
set ANDROID_HOME=D:\AI\AI-Code\tools\app\android-sdk
set PATH=%JAVA_HOME%\bin;%ANDROID_HOME%\platform-tools;%PATH%

echo ========================================
echo JAVA_HOME = %JAVA_HOME%
echo ANDROID_HOME = %ANDROID_HOME%
echo ========================================

java -version
echo.

cd /d e:\works\TOOLS\video\TV\TVLive
call gradlew.bat assembleJava32Release %*