-- Fix Fare Database Schema and Data
-- Run this script to fix the fare table structure and add proper test data

USE fare_db;

-- Drop existing table if it has wrong structure
DROP TABLE IF EXISTS fare;

-- Create fare table with correct structure
CREATE TABLE fare (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_id BIGINT NOT NULL,
    coach_type VARCHAR(20) NOT NULL,
    amount DOUBLE NOT NULL,
    UNIQUE KEY unique_train_coach (train_id, coach_type)
);

-- Insert comprehensive fare data for all trains and coach types
INSERT INTO fare (train_id, coach_type, amount) VALUES
-- Rajdhani Express (Train ID: 1) - Delhi to Mumbai
(1, 'SLEEPER', 800.00),
(1, 'AC_3_TIER', 1800.00),
(1, 'AC_2_TIER', 2500.00),
(1, 'AC_1_TIER', 3500.00),

-- Shatabdi Express (Train ID: 2) - Delhi to Bangalore  
(2, 'AC_3_TIER', 2200.00),
(2, 'AC_2_TIER', 3000.00),
(2, 'AC_1_TIER', 4000.00),

-- Duronto Express (Train ID: 3) - Mumbai to Kolkata
(3, 'SLEEPER', 900.00),
(3, 'AC_3_TIER', 2000.00),
(3, 'AC_2_TIER', 2800.00),

-- Garib Rath (Train ID: 4) - Chennai to Delhi
(4, 'SLEEPER', 750.00),
(4, 'AC_3_TIER', 1600.00),
(4, 'AC_2_TIER', 2200.00),

-- Jan Shatabdi (Train ID: 5) - Pune to Hyderabad
(5, 'SLEEPER', 400.00),
(5, 'AC_3_TIER', 800.00),
(5, 'AC_2_TIER', 1200.00);

-- Verify the data
SELECT 'Fare data inserted successfully!' as Status;
SELECT train_id, coach_type, amount FROM fare ORDER BY train_id, coach_type;