@echo off
echo ========================================
echo Starting TapToEat Backend Server
echo ========================================
echo.

cd /d "%~dp0"
echo Current directory: %CD%
echo.

echo Killing existing node processes...
taskkill /F /IM node.exe >nul 2>&1

echo Waiting 2 seconds...
timeout /t 2 /nobreak >nul

echo.
echo Starting server...
echo.
node server.js
