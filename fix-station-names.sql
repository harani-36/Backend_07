-- Diagnostic Script: Find Station Name Mismatches
-- Run this in MySQL on train_db to identify and fix the issue

USE train_db;

-- Step 1: Check what stations exist in station table
SELECT 'Stations in station table (first 20):' AS info;
SELECT id, name FROM station ORDER BY name LIMIT 20;

-- Step 2: Check what stations are used in train table
SELECT 'Stations used in train table:' AS info;
SELECT DISTINCT source FROM train 
UNION 
SELECT DISTINCT destination FROM train 
ORDER BY 1;

-- Step 3: Find exact mismatch - stations in trains but not in station table
SELECT 'MISMATCH - Stations in trains but NOT in station table:' AS issue;
SELECT DISTINCT missing_stations.station_name
FROM (
    SELECT source AS station_name FROM train
    UNION
    SELECT destination AS station_name FROM train
) AS missing_stations
LEFT JOIN station ON station.name = missing_stations.station_name
WHERE station.name IS NULL
ORDER BY missing_stations.station_name;

-- Step 4: Check specific route that's failing
SELECT 'Checking Delhi to Mumbai route in train table:' AS info;
SELECT t.id, t.train_number, t.name, t.source, t.destination
FROM train t 
WHERE (t.source LIKE '%Delhi%' OR t.source LIKE '%NDLS%') 
  AND (t.destination LIKE '%Mumbai%' OR t.destination LIKE '%BCT%');

-- Step 5: Check if we have any journeys for these trains
SELECT 'Journeys for Delhi-Mumbai trains:' AS info;
SELECT j.id, j.train_id, j.journey_date, j.departure_time, j.arrival_time, t.name, t.source, t.destination
FROM journey j
JOIN train t ON j.train_id = t.id
WHERE (t.source LIKE '%Delhi%' OR t.source LIKE '%NDLS%') 
  AND (t.destination LIKE '%Mumbai%' OR t.destination LIKE '%BCT%')
  AND j.journey_date = '2026-03-17';

-- Step 6: Check what the frontend is actually searching for
SELECT 'Exact match test - what frontend searches:' AS info;
SELECT t.id, t.train_number, t.name, t.source, t.destination
FROM train t 
WHERE t.source = 'New Delhi Railway Station' 
  AND t.destination = 'Mumbai Central';

-- Step 7: Check station table for these exact names
SELECT 'Station table entries for Delhi and Mumbai:' AS info;
SELECT id, name, code, city, state 
FROM station 
WHERE name LIKE '%Delhi%' OR name LIKE '%Mumbai%' 
ORDER BY name;

-- Step 8: SOLUTION - Update train table to match station table names
-- First, let's see what needs to be updated
SELECT 'Train routes that need station name updates:' AS solution;
SELECT t.id, t.train_number, t.name, 
       t.source as current_source, 
       s1.name as correct_source,
       t.destination as current_destination,
       s2.name as correct_destination
FROM train t
LEFT JOIN station s1 ON s1.name = t.source
LEFT JOIN station s2 ON s2.name = t.destination
WHERE s1.name IS NULL OR s2.name IS NULL;

-- Step 9: Fix the station names in train table to match station table
-- Update common mismatches

-- Fix Delhi stations
UPDATE train SET source = 'New Delhi Railway Station' 
WHERE source IN ('New Delhi', 'Delhi', 'NDLS', 'New Delhi Railway Station');

UPDATE train SET destination = 'New Delhi Railway Station' 
WHERE destination IN ('New Delhi', 'Delhi', 'NDLS', 'New Delhi Railway Station');

-- Fix Mumbai stations  
UPDATE train SET source = 'Mumbai Central' 
WHERE source IN ('Mumbai', 'BCT', 'Mumbai Central');

UPDATE train SET destination = 'Mumbai Central' 
WHERE destination IN ('Mumbai', 'BCT', 'Mumbai Central');

-- Fix other common stations
UPDATE train SET source = 'Howrah Junction' WHERE source IN ('Howrah', 'HWH');
UPDATE train SET destination = 'Howrah Junction' WHERE destination IN ('Howrah', 'HWH');

UPDATE train SET source = 'Chennai Central' WHERE source IN ('Chennai', 'MAS');
UPDATE train SET destination = 'Chennai Central' WHERE destination IN ('Chennai', 'MAS');

UPDATE train SET source = 'Bangalore City Junction' WHERE source IN ('Bangalore', 'SBC');
UPDATE train SET destination = 'Bangalore City Junction' WHERE destination IN ('Bangalore', 'SBC');

UPDATE train SET source = 'Pune Junction' WHERE source IN ('Pune', 'PUNE');
UPDATE train SET destination = 'Pune Junction' WHERE destination IN ('Pune', 'PUNE');

UPDATE train SET source = 'Hyderabad Deccan' WHERE source IN ('Hyderabad', 'HYB');
UPDATE train SET destination = 'Hyderabad Deccan' WHERE destination IN ('Hyderabad', 'HYB');

UPDATE train SET source = 'Ahmedabad Junction' WHERE source IN ('Ahmedabad', 'ADI');
UPDATE train SET destination = 'Ahmedabad Junction' WHERE destination IN ('Ahmedabad', 'ADI');

-- Step 10: Verify the fix
SELECT 'After fix - Delhi to Mumbai trains:' AS verification;
SELECT t.id, t.train_number, t.name, t.source, t.destination
FROM train t 
WHERE t.source = 'New Delhi Railway Station' 
  AND t.destination = 'Mumbai Central';

-- Step 11: Check journeys exist for the fixed route
SELECT 'Journeys available for Delhi to Mumbai on 2026-03-17:' AS final_check;
SELECT j.id, j.train_id, j.journey_date, j.departure_time, j.arrival_time, t.name
FROM journey j
JOIN train t ON j.train_id = t.id
WHERE t.source = 'New Delhi Railway Station' 
  AND t.destination = 'Mumbai Central'
  AND j.journey_date = '2026-03-17';

-- Step 12: Test the search service query
SELECT 'Search service test - what the API should return:' AS api_test;
SELECT t.id, t.train_number, t.name, t.source, t.destination,
       j.departure_time, j.arrival_time, j.journey_date
FROM train t
JOIN journey j ON t.id = j.train_id
WHERE LOWER(t.source) = LOWER('New Delhi Railway Station')
  AND LOWER(t.destination) = LOWER('Mumbai Central')
  AND j.journey_date = '2026-03-17';

SELECT 'Station name standardization completed!' AS status;