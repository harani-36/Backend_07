@echo off
echo ========================================
echo RailNova System Health Check
echo ========================================

set "EUREKA_STATUS=DOWN"
set "GATEWAY_STATUS=DOWN"
set "AUTH_STATUS=DOWN"
set "TRAIN_STATUS=DOWN"
set "BOOKING_STATUS=DOWN"
set "SEARCH_STATUS=DOWN"
set "FARE_STATUS=DOWN"
set "PAYMENT_STATUS=DOWN"
set "NOTIFICATION_STATUS=DOWN"

echo.
echo [1/3] Testing Core Services...
echo.

echo Testing Eureka Server (8761)...
curl -s http://localhost:8761/actuator/health >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] Eureka Server is running
    set "EUREKA_STATUS=UP"
) else (
    echo [ERROR] Eureka Server is not responding
)

echo.
echo Testing API Gateway (8080)...
curl -s http://localhost:8080/actuator/health >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] API Gateway is running
    set "GATEWAY_STATUS=UP"
) else (
    echo [ERROR] API Gateway is not responding
)

echo.
echo [2/3] Testing Business Services...
echo.

echo Testing Auth Service (8081)...
curl -s http://localhost:8081/actuator/health >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] Auth Service is running
    set "AUTH_STATUS=UP"
) else (
    echo [ERROR] Auth Service is not responding
)

echo Testing Train Service (8082)...
curl -s http://localhost:8082/actuator/health >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] Train Service is running
    set "TRAIN_STATUS=UP"
) else (
    echo [ERROR] Train Service is not responding
)

echo Testing Booking Service (8083)...
curl -s http://localhost:8083/actuator/health >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] Booking Service is running
    set "BOOKING_STATUS=UP"
) else (
    echo [ERROR] Booking Service is not responding
)

echo Testing Search Service (8084)...
curl -s http://localhost:8084/actuator/health >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] Search Service is running
    set "SEARCH_STATUS=UP"
) else (
    echo [ERROR] Search Service is not responding
)

echo Testing Fare Service (8085)...
curl -s http://localhost:8085/actuator/health >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] Fare Service is running
    set "FARE_STATUS=UP"
) else (
    echo [ERROR] Fare Service is not responding
)

echo Testing Payment Service (8086)...
curl -s http://localhost:8086/actuator/health >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] Payment Service is running
    set "PAYMENT_STATUS=UP"
) else (
    echo [ERROR] Payment Service is not responding
)

echo Testing Notification Service (8087)...
curl -s http://localhost:8087/actuator/health >nul 2>&1
if %errorlevel% == 0 (
    echo [OK] Notification Service is running
    set "NOTIFICATION_STATUS=UP"
) else (
    echo [ERROR] Notification Service is not responding
)

echo.
echo [3/3] Testing API Gateway Routes...
echo.

if "%GATEWAY_STATUS%"=="UP" (
    echo Testing Auth Service through Gateway...
    curl -s http://localhost:8080/auth-service/actuator/health >nul 2>&1
    if %errorlevel% == 0 (
        echo [OK] Auth Service accessible through Gateway
    ) else (
        echo [WARNING] Auth Service not accessible through Gateway
    )

    echo Testing Train Service through Gateway...
    curl -s http://localhost:8080/train-service/actuator/health >nul 2>&1
    if %errorlevel% == 0 (
        echo [OK] Train Service accessible through Gateway
    ) else (
        echo [WARNING] Train Service not accessible through Gateway
    )
) else (
    echo [SKIP] Gateway is down, skipping route tests
)

echo.
echo ========================================
echo System Status Summary
echo ========================================
echo Eureka Server:      %EUREKA_STATUS%
echo API Gateway:        %GATEWAY_STATUS%
echo Auth Service:       %AUTH_STATUS%
echo Train Service:      %TRAIN_STATUS%
echo Booking Service:    %BOOKING_STATUS%
echo Search Service:     %SEARCH_STATUS%
echo Fare Service:       %FARE_STATUS%
echo Payment Service:    %PAYMENT_STATUS%
echo Notification:       %NOTIFICATION_STATUS%
echo ========================================

echo.
echo Quick Links:
echo - Eureka Dashboard: http://localhost:8761
echo - API Gateway: http://localhost:8080
echo - Swagger UI: http://localhost:8080/swagger-ui.html
echo - Frontend: http://localhost:5173
echo.

if "%EUREKA_STATUS%"=="UP" if "%GATEWAY_STATUS%"=="UP" if "%AUTH_STATUS%"=="UP" (
    echo [SUCCESS] Core services are running!
    echo You can now start the frontend.
) else (
    echo [WARNING] Some core services are not running.
    echo Please check the logs and restart failed services.
)

echo.
pause