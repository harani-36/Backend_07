@echo off
echo ========================================
echo RailNova Quick Fix Script
echo ========================================

echo.
echo This script will fix common issues:
echo 1. Clean and rebuild all services
echo 2. Kill any stuck processes
echo 3. Reset Maven dependencies
echo.

set /p choice="Do you want to continue? (y/n): "
if /i "%choice%" neq "y" (
    echo Operation cancelled.
    pause
    exit /b 0
)

echo.
echo [1/4] Killing any stuck Java processes...
echo.
taskkill /f /im java.exe >nul 2>&1
echo Java processes terminated.

echo.
echo [2/4] Cleaning Maven cache...
echo.
call mvn dependency:purge-local-repository -q
echo Maven cache cleaned.

echo.
echo [3/4] Rebuilding all services...
echo.

set services=eureka api-gateway auth-service train-service booking-service search-service fare-service payment-service notification-service

for %%s in (%services%) do (
    echo Rebuilding %%s...
    cd %%s
    call mvn clean install -DskipTests -q
    if %errorlevel% neq 0 (
        echo [ERROR] Failed to rebuild %%s
        cd ..
        pause
        exit /b 1
    )
    cd ..
)

echo.
echo [4/4] Verifying builds...
echo.

for %%s in (%services%) do (
    if exist "%%s\target\*.jar" (
        echo [OK] %%s built successfully
    ) else (
        echo [ERROR] %%s build failed
    )
)

echo.
echo ========================================
echo Quick Fix Complete!
echo ========================================
echo.
echo Next steps:
echo 1. Run: start-all-services.bat
echo 2. Wait 2-3 minutes for all services to start
echo 3. Run: check-system-status.bat
echo.

pause