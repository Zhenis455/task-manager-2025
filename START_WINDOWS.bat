@echo off
setlocal
cd /d "%~dp0"
echo =============================================================
echo   TASK MANAGER COMPLETE - Java 21 / Spring Boot / Bootstrap
 echo =============================================================
echo.
echo Your website:     http://localhost:8081/tasks
echo Kanban board:     http://localhost:8081/board
echo API documentation: open README_RU.md
echo Stop server: Ctrl+C
 echo.
powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\gradle-bootstrap.ps1" -Task bootRun
if errorlevel 1 (
  echo.
  echo STARTUP ERROR. Read the message above.
  pause
)
endlocal
