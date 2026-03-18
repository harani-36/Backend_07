@echo off
echo ========================================
echo RailNova System Health Check
echo ========================================

echo.
echo Checking Backend Services...

echo [1] Eureka Server (8761)...
curl -s http://localhost:8761/actuator/health >nul 2>&1
if errorlevel 1 (
    echo ❌ Eureka Server - NOT RUNNING
) else (
    echo ✅ Eureka Server - RUNNING
)

echo [2] API Gateway (8080)...
curl -s http://localhost:8080/actuator/health >nul 2>&1
if errorlevel 1 (
    echo ❌ API Gateway - NOT RUNNING
) else (
    echo ✅ API Gateway - RUNNING
)

echo [3] Auth Service (8081)...
curl -s http://localhost:8081/actuator/health >nul 2>&1
if errorlevel 1 (
    echo ❌ Auth Service - NOT RUNNING
) else (
    echo ✅ Auth Service - RUNNING
)

echo [4] Train Service (8082)...
curl -s http://localhost:8082/actuator/health >nul 2>&1
if errorlevel 1 (
    echo ❌ Train Service - NOT RUNNING
) else (
    echo ✅ Train Service - RUNNING
)

echo [5] Booking Service (8083)...
curl -s http://localhost:8083/actuator/health >nul 2>&1
if errorlevel 1 (
    echo ❌ Booking Service - NOT RUNNING
) else (
    echo ✅ Booking Service - RUNNING
)

echo [6] Search Service (8084)...
curl -s http://localhost:8084/actuator/health >nul 2>&1
if errorlevel 1 (
    echo ❌ Search Service - NOT RUNNING
) else (
    echo ✅ Search Service - RUNNING
)

echo [7] Fare Service (8085)...
curl -s http://localhost:8085/actuator/health >nul 2>&1
if errorlevel 1 (
    echo ❌ Fare Service - NOT RUNNING
) else (
    echo ✅ Fare Service - RUNNING
)

echo [8] Payment Service (8086)...
curl -s http://localhost:8086/actuator/health >nul 2>&1
if errorlevel 1 (
    echo ❌ Payment Service - NOT RUNNING
) else (
    echo ✅ Payment Service - RUNNING
)

echo [9] Notification Service (8087)...
curl -s http://localhost:8087/actuator/health >nul 2>&1
if errorlevel 1 (
    echo ❌ Notification Service - NOT RUNNING
) else (
    echo ✅ Notification Service - RUNNING
)

echo.
echo Checking Database Connectivity...
mysql -u root -proot -e "SELECT 'Database Connection: OK' as Status;" 2>nul
if errorlevel 1 (
    echo ❌ MySQL Database - NOT ACCESSIBLE
) else (
    echo ✅ MySQL Database - ACCESSIBLE
)

echo.
echo Checking Frontend...
netstat -an | find "5173" >nul
if errorlevel 1 (
    echo ❌ Frontend (5173) - NOT RUNNING
) else (
    echo ✅ Frontend (5173) - RUNNING
)

echo.
echo ========================================
echo Health Check Complete
echo ========================================
echo.
echo Quick Access URLs:
echo • Frontend: http://localhost:5173
echo • API Gateway: http://localhost:8080
echo • Eureka Dashboard: http://localhost:8761
echo • Swagger UI: http://localhost:8080/swagger-ui.html
echo.
pause