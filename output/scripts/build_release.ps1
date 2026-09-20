$env:JAVA_HOME = "D:\AI\AI-Code\tools\app\jdk-11"
$env:ANDROID_HOME = "D:\AI\AI-Code\tools\app\android-sdk"
$env:Path = "$env:JAVA_HOME\bin;$env:ANDROID_HOME\platform-tools;$env:Path"

Write-Host "========================================"
Write-Host "JAVA_HOME = $env:JAVA_HOME"
Write-Host "ANDROID_HOME = $env:ANDROID_HOME"
Write-Host "========================================"

& java -version
Write-Host ""

Set-Location "e:\works\TOOLS\video\TV\TVLive"
& .\gradlew.bat assembleJava32Release @args