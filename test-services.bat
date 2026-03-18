@echo off
echo ========================================
echo RailNova System Health Check
echo ========================================

echo.
echo Testing Eureka Server...
curl -s http://localhost:8761/actuator/health > nul
if %errorlevel% == 0 (
    echo [OK] Eureka Server is running
) else (
    echo [ERROR] Eureka Server is not responding
)

echo.
echo Testing API Gateway...
curl -s http://localhost:8080/actuator/health > nul
if %errorlevel% == 0 (
    echo [OK] API Gateway is running
) else (
    echo [ERROR] API Gateway is not responding
)

echo.
echo Testing Auth Service...
curl -s http://localhost:8081/actuator/health > nul
if %errorlevel% == 0 (
    echo [OK] Auth Service is running
) else (
    echo [ERROR] Auth Service is not responding
)

echo.
echo Testing Train Service...
curl -s http://localhost:8082/actuator/health > nul
if %errorlevel% == 0 (
    echo [OK] Train Service is running
) else (
    echo [ERROR] Train Service is not responding
)

echo.
echo Testing Booking Service...
curl -s http://localhost:8083/actuator/health > nul
if %errorlevel% == 0 (
    echo [OK] Booking Service is running
) else (
    echo [ERROR] Booking Service is not responding
)

echo.
echo Testing Search Service...
curl -s http://localhost:8084/actuator/health > nul
if %errorlevel% == 0 (
    echo [OK] Search Service is running
) else (
    echo [ERROR] Search Service is not responding
)

echo.
echo Testing Fare Service...
curl -s http://localhost:8085/actuator/health > nul
if %errorlevel% == 0 (
    echo [OK] Fare Service is running
) else (
    echo [ERROR] Fare Service is not responding
)

echo.
echo Testing Payment Service...
curl -s http://localhost:8086/actuator/health > nul
if %errorlevel% == 0 (
    echo [OK] Payment Service is running
) else (
    echo [ERROR] Payment Service is not responding
)

echo.
echo Testing Notification Service...
curl -s http://localhost:8087/actuator/health > nul
if %errorlevel% == 0 (
    echo [OK] Notification Service is running
) else (
    echo [ERROR] Notification Service is not responding
)

echo.
echo ========================================
echo Testing API Gateway Routes...
echo ========================================

echo.
echo Testing Auth Service through Gateway...
curl -s http://localhost:8080/auth-service/actuator/health > nul
if %errorlevel% == 0 (
    echo [OK] Auth Service accessible through Gateway
) else (
    echo [ERROR] Auth Service not accessible through Gateway
)

echo.
echo Testing Train Service through Gateway...
curl -s http://localhost:8080/train-service/actuator/health > nul
if %errorlevel% == 0 (
    echo [OK] Train Service accessible through Gateway
) else (
    echo [ERROR] Train Service not accessible through Gateway
)

echo.
echo ========================================
echo Health Check Complete
echo ========================================
echo.
echo Services Status:
echo - Eureka Dashboard: http://localhost:8761
echo - API Gateway: http://localhost:8080
echo - Swagger UI: http://localhost:8080/swagger-ui.html
echo - Frontend: http://localhost:5173
echo.

pause