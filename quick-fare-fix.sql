-- QUICK FIX FOR FARE SERVICE ISSUES
-- Run this script to immediately fix fare service problems

USE fare_db;

-- 1. Fix fare table structure if needed
ALTER TABLE fare ADD COLUMN IF NOT EXISTS base_fare DECIMAL(10,2);
UPDATE fare SET base_fare = amount WHERE base_fare IS NULL;

-- 2. Ensure fare data exists for testing
INSERT IGNORE INTO fare (train_id, coach_type, base_fare, distance_km) VALUES
(1, 'AC_1_TIER', 3500.00, 1400),
(1, 'AC_2_TIER', 2500.00, 1400),
(1, 'AC_3_TIER', 1800.00, 1400),
(2, 'AC_1_TIER', 4000.00, 2200),
(2, 'AC_2_TIER', 3000.00, 2200),
(2, 'AC_3_TIER', 2200.00, 2200),
(3, 'AC_2_TIER', 2800.00, 2000),
(3, 'AC_3_TIER', 2000.00, 2000),
(3, 'SLEEPER', 900.00, 2000);

-- 3. Verify data
SELECT 'Fare data verification:' as Status;
SELECT train_id, coach_type, 
       COALESCE(base_fare, amount) as fare_amount,
       distance_km
FROM fare 
ORDER BY train_id, coach_type;

SELECT 'Quick fix completed!' as Result;