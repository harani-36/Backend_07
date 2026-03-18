-- Fix Fare Data for RailNova
-- Run this script to insert correct fare data

USE fare_db;

-- Clear existing fare data
DELETE FROM fare;

-- Insert fare data with correct structure (matching Java entity)
INSERT INTO fare (train_id, coach_type, amount) VALUES
-- Train 1 (Rajdhani Express)
(1, 'SLEEPER', 800.00),
(1, 'AC_3_TIER', 1800.00),
(1, 'AC_2_TIER', 2500.00),
(1, 'AC_1_TIER', 3500.00),

-- Train 2 (Shatabdi Express)
(2, 'SLEEPER', 900.00),
(2, 'AC_3_TIER', 2200.00),
(2, 'AC_2_TIER', 3000.00),
(2, 'AC_1_TIER', 4000.00),

-- Train 3 (Duronto Express)
(3, 'SLEEPER', 900.00),
(3, 'AC_3_TIER', 2000.00),
(3, 'AC_2_TIER', 2800.00),
(3, 'AC_1_TIER', 3800.00),

-- Train 4 (Garib Rath)
(4, 'SLEEPER', 750.00),
(4, 'AC_3_TIER', 1700.00),
(4, 'AC_2_TIER', 2400.00),
(4, 'AC_1_TIER', 3300.00),

-- Train 5 (Jan Shatabdi)
(5, 'SLEEPER', 600.00),
(5, 'AC_3_TIER', 1400.00),
(5, 'AC_2_TIER', 2000.00),
(5, 'AC_1_TIER', 2800.00),

-- Train 6 (Vande Bharat)
(6, 'SLEEPER', 1000.00),
(6, 'AC_3_TIER', 2500.00),
(6, 'AC_2_TIER', 3500.00),
(6, 'AC_1_TIER', 4500.00),

-- Train 7 (Humsafar Express)
(7, 'SLEEPER', 850.00),
(7, 'AC_3_TIER', 1900.00),
(7, 'AC_2_TIER', 2700.00),
(7, 'AC_1_TIER', 3600.00),

-- Train 8 (Tejas Express)
(8, 'SLEEPER', 700.00),
(8, 'AC_3_TIER', 1600.00),
(8, 'AC_2_TIER', 2300.00),
(8, 'AC_1_TIER', 3200.00);

-- Verify the data
SELECT 'Fare data inserted successfully!' as Status;
SELECT COUNT(*) as TotalFareRecords FROM fare;
SELECT train_id, coach_type, amount FROM fare ORDER BY train_id, coach_type;