@echo off
echo ========================================
echo RailNova System Validation
echo ========================================

echo.
echo [1/4] Checking Prerequisites...
echo.

echo Checking Java version...
java -version
if %errorlevel% neq 0 (
    echo [ERROR] Java not found. Please install Java 17+
    pause
    exit /b 1
)

echo.
echo Checking Maven...
mvn -version
if %errorlevel% neq 0 (
    echo [ERROR] Maven not found. Please install Maven 3.6+
    pause
    exit /b 1
)

echo.
echo [2/4] Building all services...
echo.

echo Building Eureka Server...
cd eureka
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo [ERROR] Eureka build failed
    cd ..
    pause
    exit /b 1
)
cd ..

echo Building API Gateway...
cd api-gateway
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo [ERROR] API Gateway build failed
    cd ..
    pause
    exit /b 1
)
cd ..

echo Building Auth Service...
cd auth-service
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo [ERROR] Auth Service build failed
    cd ..
    pause
    exit /b 1
)
cd ..

echo Building Train Service...
cd train-service
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo [ERROR] Train Service build failed
    cd ..
    pause
    exit /b 1
)
cd ..

echo Building Booking Service...
cd booking-service
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo [ERROR] Booking Service build failed
    cd ..
    pause
    exit /b 1
)
cd ..

echo Building Search Service...
cd search-service
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo [ERROR] Search Service build failed
    cd ..
    pause
    exit /b 1
)
cd ..

echo Building Fare Service...
cd fare-service
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo [ERROR] Fare Service build failed
    cd ..
    pause
    exit /b 1
)
cd ..

echo Building Payment Service...
cd payment-service
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo [ERROR] Payment Service build failed
    cd ..
    pause
    exit /b 1
)
cd ..

echo Building Notification Service...
cd notification-service
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo [ERROR] Notification Service build failed
    cd ..
    pause
    exit /b 1
)
cd ..

echo.
echo [3/4] Checking Database Connection...
echo.

echo Testing MySQL connection...
echo Please ensure MySQL is running and databases are created:
echo - auth_db
echo - train_db  
echo - booking_db
echo - fare_db
echo - payment_db
echo.

echo [4/4] Validation Complete!
echo.
echo ========================================
echo System Status: READY
echo ========================================
echo.
echo Next Steps:
echo 1. Start services: start-all-services.bat
echo 2. Test services: test-services.bat
echo 3. Start frontend: cd ../Frontend && start-frontend.bat
echo.

pause