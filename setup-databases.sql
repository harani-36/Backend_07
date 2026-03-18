-- RailNova Database Setup Script
-- Run this script in MySQL to create all required databases

-- Create databases
CREATE DATABASE IF NOT EXISTS auth_db;
CREATE DATABASE IF NOT EXISTS train_db;
CREATE DATABASE IF NOT EXISTS booking_db;
CREATE DATABASE IF NOT EXISTS fare_db;
CREATE DATABASE IF NOT EXISTS payment_db;

-- Show created databases
SHOW DATABASES;

-- Grant permissions (if needed)
-- GRANT ALL PRIVILEGES ON auth_db.* TO 'root'@'localhost';
-- GRANT ALL PRIVILEGES ON train_db.* TO 'root'@'localhost';
-- GRANT ALL PRIVILEGES ON booking_db.* TO 'root'@'localhost';
-- GRANT ALL PRIVILEGES ON fare_db.* TO 'root'@'localhost';
-- GRANT ALL PRIVILEGES ON payment_db.* TO 'root'@'localhost';
-- FLUSH PRIVILEGES;

SELECT 'Database setup completed successfully!' as Status;