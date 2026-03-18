@echo off
echo ========================================
echo RailNova Complete System Startup
echo ========================================

echo.
echo [1/4] Checking Prerequisites...
echo Checking if MySQL is running...
netstat -an | find "3306" >nul
if errorlevel 1 (
    echo ERROR: MySQL is not running on port 3306
    echo Please start MySQL service first
    pause
    exit /b 1
) else (
    echo ✓ MySQL is running
)

echo.
echo [2/4] Setting up databases...
echo Running database setup script...
mysql -u root -proot < complete-database-setup.sql
if errorlevel 1 (
    echo ERROR: Database setup failed
    echo Please check MySQL credentials and try again
    pause
    exit /b 1
) else (
    echo ✓ Database setup completed
)

echo.
echo [3/4] Starting Backend Services...
echo Starting Eureka Server (Port 8761)...
start "Eureka Server" cmd /k "cd /d eureka && mvn spring-boot:run"

echo Waiting for Eureka to start...
timeout /t 30 /nobreak

echo Starting API Gateway (Port 8080)...
start "API Gateway" cmd /k "cd /d api-gateway && mvn spring-boot:run"

echo Waiting for API Gateway...
timeout /t 20 /nobreak

echo Starting Auth Service (Port 8081)...
start "Auth Service" cmd /k "cd /d auth-service && mvn spring-boot:run"

echo Starting Train Service (Port 8082)...
start "Train Service" cmd /k "cd /d train-service && mvn spring-boot:run"

echo Starting Booking Service (Port 8083)...
start "Booking Service" cmd /k "cd /d booking-service && mvn spring-boot:run"

echo Starting Search Service (Port 8084)...
start "Search Service" cmd /k "cd /d search-service && mvn spring-boot:run"

echo Starting Fare Service (Port 8085)...
start "Fare Service" cmd /k "cd /d fare-service && mvn spring-boot:run"

echo Starting Payment Service (Port 8086)...
start "Payment Service" cmd /k "cd /d payment-service && mvn spring-boot:run"

echo Starting Notification Service (Port 8087)...
start "Notification Service" cmd /k "cd /d notification-service && mvn spring-boot:run"

echo.
echo [4/4] System Status...
echo ========================================
echo All services are starting...
echo Please wait 2-3 minutes for all services to be ready
echo.
echo Service URLs:
echo • Eureka Dashboard: http://localhost:8761
echo • API Gateway: http://localhost:8080
echo • Swagger UI: http://localhost:8080/swagger-ui.html
echo.
echo Test Credentials:
echo • Admin: admin@railnova.com / admin123
echo • User: user@railnova.com / user123
echo ========================================

echo.
echo Starting Frontend...
cd /d ..\Frontend
start "Frontend" cmd /k "npm run dev"

echo.
echo System startup completed!
echo Frontend will be available at: http://localhost:5173
echo.
pause