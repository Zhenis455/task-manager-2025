@echo off
setlocal
cd /d "%~dp0"
powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\gradle-bootstrap.ps1" -Task test
echo.
pause
endlocal
