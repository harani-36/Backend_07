@echo off
echo ========================================
echo Starting RailNova Backend Services
echo ========================================

echo.
echo Starting Eureka Server (Port 8761)...
start "Eureka Server" cmd /k "cd /d eureka && mvn spring-boot:run"

echo Waiting for Eureka to start...
timeout /t 30 /nobreak

echo.
echo Starting API Gateway (Port 8080)...
start "API Gateway" cmd /k "cd /d api-gateway && mvn spring-boot:run"

echo Waiting for API Gateway to start...
timeout /t 20 /nobreak

echo.
echo Starting Auth Service (Port 8081)...
start "Auth Service" cmd /k "cd /d auth-service && mvn spring-boot:run"

echo.
echo Starting Train Service (Port 8082)...
start "Train Service" cmd /k "cd /d train-service && mvn spring-boot:run"

echo.
echo Starting Booking Service (Port 8083)...
start "Booking Service" cmd /k "cd /d booking-service && mvn spring-boot:run"

echo.
echo Starting Search Service (Port 8084)...
start "Search Service" cmd /k "cd /d search-service && mvn spring-boot:run"

echo.
echo Starting Fare Service (Port 8085)...
start "Fare Service" cmd /k "cd /d fare-service && mvn spring-boot:run"

echo.
echo Starting Payment Service (Port 8086)...
start "Payment Service" cmd /k "cd /d payment-service && mvn spring-boot:run"

echo.
echo Starting Notification Service (Port 8087)...
start "Notification Service" cmd /k "cd /d notification-service && mvn spring-boot:run"

echo.
echo ========================================
echo All services are starting...
echo Please wait for all services to be ready
echo Check Eureka Dashboard: http://localhost:8761
echo API Gateway: http://localhost:8080
echo ========================================

pause