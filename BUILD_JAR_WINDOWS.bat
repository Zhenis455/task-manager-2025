@echo off
setlocal
cd /d "%~dp0"
powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\gradle-bootstrap.ps1" -Task bootJar
echo.
echo If successful, the executable JAR is in build\libs.
pause
endlocal
