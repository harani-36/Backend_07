-- Quick Database Verification & Working Test Cases
-- Run these queries to see what data actually exists

USE train_db;

-- 1. See what trains actually exist
SELECT 'All trains in database:' AS info;
SELECT id, train_number, name, source, destination FROM train ORDER BY id LIMIT 10;

-- 2. See what journeys exist for today
SELECT 'Journeys available for 2026-03-17:' AS info;
SELECT j.id, j.train_id, j.journey_date, j.departure_time, j.arrival_time, 
       t.train_number, t.name, t.source, t.destination
FROM journey j
JOIN train t ON j.train_id = t.id
WHERE j.journey_date = '2026-03-17'
ORDER BY j.departure_time
LIMIT 10;

-- 3. Check exact station names in station table
SELECT 'Station names containing Delhi:' AS info;
SELECT id, name FROM station WHERE name LIKE '%Delhi%';

SELECT 'Station names containing Mumbai:' AS info;
SELECT id, name FROM station WHERE name LIKE '%Mumbai%';

-- 4. Check what the search service should find
SELECT 'Direct search test:' AS info;
SELECT t.id, t.train_number, t.name, t.source, t.destination
FROM train t
WHERE t.source LIKE '%Delhi%' AND t.destination LIKE '%Mumbai%';

-- 5. WORKING TEST CASES based on actual data
SELECT 'WORKING TEST CASES - Use these exact names:' AS working_tests;

-- Get first 5 actual train routes with journeys
SELECT CONCAT('Source: "', t.source, '"') as test_source,
       CONCAT('Destination: "', t.destination, '"') as test_destination,
       CONCAT('Date: "', j.journey_date, '"') as test_date,
       CONCAT('Expected: ', t.train_number, ' - ', t.name) as expected_train
FROM journey j
JOIN train t ON j.train_id = t.id
WHERE j.journey_date >= '2026-03-17'
GROUP BY t.source, t.destination, t.train_number, t.name
ORDER BY j.journey_date, j.departure_time
LIMIT 10;