-- RailNova Complete Database Setup Script
-- Run this script in MySQL to create all required databases and test data

-- Create databases
CREATE DATABASE IF NOT EXISTS auth_db;
CREATE DATABASE IF NOT EXISTS train_db;
CREATE DATABASE IF NOT EXISTS booking_db;
CREATE DATABASE IF NOT EXISTS fare_db;
CREATE DATABASE IF NOT EXISTS payment_db;

-- Use train_db for test data
USE train_db;

-- Insert test stations
INSERT IGNORE INTO station (id, name, code, city, state) VALUES
(1, 'New Delhi Railway Station', 'NDLS', 'New Delhi', 'Delhi'),
(2, 'Mumbai Central', 'BCT', 'Mumbai', 'Maharashtra'),
(3, 'Howrah Junction', 'HWH', 'Kolkata', 'West Bengal'),
(4, 'Chennai Central', 'MAS', 'Chennai', 'Tamil Nadu'),
(5, 'Bangalore City Junction', 'SBC', 'Bangalore', 'Karnataka'),
(6, 'Pune Junction', 'PUNE', 'Pune', 'Maharashtra'),
(7, 'Hyderabad Deccan', 'HYB', 'Hyderabad', 'Telangana'),
(8, 'Ahmedabad Junction', 'ADI', 'Ahmedabad', 'Gujarat');

-- Insert test trains
INSERT IGNORE INTO train (id, name, train_number, source_station, destination_station, total_seats) VALUES
(1, 'Rajdhani Express', '12001', 'New Delhi Railway Station', 'Mumbai Central', 300),
(2, 'Shatabdi Express', '12002', 'New Delhi Railway Station', 'Bangalore City Junction', 250),
(3, 'Duronto Express', '12003', 'Mumbai Central', 'Howrah Junction', 400),
(4, 'Garib Rath', '12004', 'Chennai Central', 'New Delhi Railway Station', 350),
(5, 'Jan Shatabdi', '12005', 'Pune Junction', 'Hyderabad Deccan', 200);

-- Insert test journeys with future dates
INSERT IGNORE INTO journey (id, train_id, departure_time, arrival_time, journey_date, available_seats) VALUES
(1, 1, '06:00:00', '14:30:00', '2024-12-25', 300),
(2, 1, '06:00:00', '14:30:00', '2024-12-26', 300),
(3, 2, '08:00:00', '20:00:00', '2024-12-25', 250),
(4, 2, '08:00:00', '20:00:00', '2024-12-26', 250),
(5, 3, '10:00:00', '18:30:00', '2024-12-25', 400),
(6, 4, '15:00:00', '09:00:00', '2024-12-26', 350),
(7, 5, '07:30:00', '13:00:00', '2024-12-25', 200);

-- Use fare_db for fare data
USE fare_db;

-- Insert test fare data
INSERT IGNORE INTO fare (id, train_id, coach_type, base_fare, distance_km) VALUES
(1, 1, 'AC_1_TIER', 3500.00, 1400),
(2, 1, 'AC_2_TIER', 2500.00, 1400),
(3, 1, 'AC_3_TIER', 1800.00, 1400),
(4, 1, 'SLEEPER', 800.00, 1400),
(5, 2, 'AC_1_TIER', 4000.00, 2200),
(6, 2, 'AC_2_TIER', 3000.00, 2200),
(7, 2, 'AC_3_TIER', 2200.00, 2200),
(8, 3, 'AC_2_TIER', 2800.00, 2000),
(9, 3, 'AC_3_TIER', 2000.00, 2000),
(10, 3, 'SLEEPER', 900.00, 2000);

-- Use auth_db for test user
USE auth_db;

-- Insert test admin user (password: admin123)
INSERT IGNORE INTO user (id, email, password, first_name, last_name, phone, role, created_at, updated_at) VALUES
(1, 'admin@railnova.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM5lHZHpSRUbYijTrcee', 'Admin', 'User', '9999999999', 'ADMIN', NOW(), NOW());

-- Insert test regular user (password: user123)
INSERT IGNORE INTO user (id, email, password, first_name, last_name, phone, role, created_at, updated_at) VALUES
(2, 'user@railnova.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Test', 'User', '8888888888', 'USER', NOW(), NOW());

SELECT 'Database setup completed successfully!' as Status;
SELECT 'Test Users Created:' as Info;
SELECT 'admin@railnova.com / admin123 (ADMIN)' as AdminUser;
SELECT 'user@railnova.com / user123 (USER)' as TestUser;