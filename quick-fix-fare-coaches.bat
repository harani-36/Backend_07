@echo off
echo ========================================
echo RailNova - Quick Fix for Fare and Coach Issues
echo ========================================

echo.
echo Step 1: Fixing fare database schema...
mysql -u root -p -e "source fix-fare-database.sql"

if %errorlevel% neq 0 (
    echo ERROR: Failed to fix fare database. Please check MySQL connection.
    pause
    exit /b 1
)

echo.
echo Step 2: Restarting services to apply changes...

echo Stopping all services...
taskkill /f /im java.exe 2>nul

echo.
echo Starting Eureka Server...
start "Eureka" cmd /c "cd eureka && mvn spring-boot:run"
timeout /t 30

echo.
echo Starting API Gateway...
start "Gateway" cmd /c "cd api-gateway && mvn spring-boot:run"
timeout /t 15

echo.
echo Starting Auth Service...
start "Auth" cmd /c "cd auth-service && mvn spring-boot:run"
timeout /t 15

echo.
echo Starting Fare Service...
start "Fare" cmd /c "cd fare-service && mvn spring-boot:run"
timeout /t 15

echo.
echo Starting Train Service...
start "Train" cmd /c "cd train-service && mvn spring-boot:run"
timeout /t 15

echo.
echo Starting Search Service...
start "Search" cmd /c "cd search-service && mvn spring-boot:run"
timeout /t 15

echo.
echo ========================================
echo All services started successfully!
echo ========================================
echo.
echo Test the fixes:
echo 1. Open http://localhost:5173 (Frontend)
echo 2. Search for trains (Delhi to Mumbai)
echo 3. Select a train and check coach types
echo 4. Verify fare details display correctly
echo.
echo API Gateway: http://localhost:8080
echo Eureka Dashboard: http://localhost:8761
echo.
pause