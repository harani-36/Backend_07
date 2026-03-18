-- Check and Insert Missing Stations from Train Table
-- Run these queries in MySQL on train_db (port 3306)

USE train_db;

-- Step 1: Check current station count
SELECT 'Current station count:' AS info, COUNT(*) AS count FROM station;

-- Step 2: Find all unique stations from train table (source + destination)
SELECT 'Unique stations in train table:' AS info, COUNT(*) AS count FROM (
    SELECT source AS station_name FROM train
    UNION
    SELECT destination AS station_name FROM train
) AS unique_stations;

-- Step 3: Find stations that exist in train table but NOT in station table
SELECT 'Missing stations (in trains but not in station table):' AS info;
SELECT DISTINCT missing_stations.station_name
FROM (
    SELECT source AS station_name FROM train
    UNION
    SELECT destination AS station_name FROM train
) AS missing_stations
LEFT JOIN station ON station.name = missing_stations.station_name
WHERE station.name IS NULL
ORDER BY missing_stations.station_name;

-- Step 4: Get the next available ID for station table
SELECT 'Next available station ID:' AS info, (MAX(id) + 1) AS next_id FROM station;

-- Step 5: Insert missing stations with generated codes and estimated cities/states
-- Note: We'll use the station name to derive city and generate codes

INSERT IGNORE INTO station (name, code, city, state)
SELECT DISTINCT 
    missing_stations.station_name AS name,
    -- Generate a 3-4 character code from the station name
    CASE 
        WHEN missing_stations.station_name = 'New Delhi Railway Station' THEN 'NDLS'
        WHEN missing_stations.station_name = 'Mumbai Central' THEN 'BCT'
        WHEN missing_stations.station_name = 'Howrah Junction' THEN 'HWH'
        WHEN missing_stations.station_name = 'Chennai Central' THEN 'MAS'
        WHEN missing_stations.station_name = 'Bangalore City Junction' THEN 'SBC'
        WHEN missing_stations.station_name = 'Pune Junction' THEN 'PUNE'
        WHEN missing_stations.station_name = 'Hyderabad Deccan' THEN 'HYB'
        WHEN missing_stations.station_name = 'Ahmedabad Junction' THEN 'ADI'
        WHEN missing_stations.station_name = 'Sealdah' THEN 'SDAH'
        WHEN missing_stations.station_name = 'Hazrat Nizamuddin' THEN 'NZM'
        WHEN missing_stations.station_name = 'Amritsar Junction' THEN 'ASR'
        WHEN missing_stations.station_name = 'Bhopal Junction' THEN 'BPL'
        WHEN missing_stations.station_name = 'Chandigarh' THEN 'CDG'
        WHEN missing_stations.station_name = 'Jaipur Junction' THEN 'JP'
        WHEN missing_stations.station_name = 'Puri' THEN 'PURI'
        WHEN missing_stations.station_name = 'Ernakulam Junction' THEN 'ERS'
        WHEN missing_stations.station_name = 'Secunderabad Junction' THEN 'SC'
        WHEN missing_stations.station_name = 'Madgaon Junction' THEN 'MAO'
        WHEN missing_stations.station_name = 'Dibrugarh Town' THEN 'DBRG'
        WHEN missing_stations.station_name = 'Thiruvananthapuram Central' THEN 'TVC'
        WHEN missing_stations.station_name = 'Firozpur Cantt' THEN 'FZR'
        WHEN missing_stations.station_name = 'Mumbai CST' THEN 'CSTM'
        -- Generate codes for other stations by taking first 3-4 characters
        ELSE UPPER(LEFT(REPLACE(REPLACE(missing_stations.station_name, ' ', ''), 'Junction', ''), 4))
    END AS code,
    -- Extract city name from station name
    CASE 
        WHEN missing_stations.station_name LIKE '%New Delhi%' OR missing_stations.station_name LIKE '%Nizamuddin%' THEN 'New Delhi'
        WHEN missing_stations.station_name LIKE '%Mumbai%' THEN 'Mumbai'
        WHEN missing_stations.station_name LIKE '%Howrah%' THEN 'Kolkata'
        WHEN missing_stations.station_name LIKE '%Chennai%' THEN 'Chennai'
        WHEN missing_stations.station_name LIKE '%Bangalore%' THEN 'Bangalore'
        WHEN missing_stations.station_name LIKE '%Pune%' THEN 'Pune'
        WHEN missing_stations.station_name LIKE '%Hyderabad%' THEN 'Hyderabad'
        WHEN missing_stations.station_name LIKE '%Ahmedabad%' THEN 'Ahmedabad'
        WHEN missing_stations.station_name LIKE '%Sealdah%' THEN 'Kolkata'
        WHEN missing_stations.station_name LIKE '%Amritsar%' THEN 'Amritsar'
        WHEN missing_stations.station_name LIKE '%Bhopal%' THEN 'Bhopal'
        WHEN missing_stations.station_name LIKE '%Chandigarh%' THEN 'Chandigarh'
        WHEN missing_stations.station_name LIKE '%Jaipur%' THEN 'Jaipur'
        WHEN missing_stations.station_name LIKE '%Puri%' THEN 'Puri'
        WHEN missing_stations.station_name LIKE '%Ernakulam%' THEN 'Kochi'
        WHEN missing_stations.station_name LIKE '%Secunderabad%' THEN 'Hyderabad'
        WHEN missing_stations.station_name LIKE '%Madgaon%' THEN 'Margao'
        WHEN missing_stations.station_name LIKE '%Dibrugarh%' THEN 'Dibrugarh'
        WHEN missing_stations.station_name LIKE '%Thiruvananthapuram%' THEN 'Thiruvananthapuram'
        WHEN missing_stations.station_name LIKE '%Firozpur%' THEN 'Firozpur'
        -- Default: use the first word as city
        ELSE SUBSTRING_INDEX(missing_stations.station_name, ' ', 1)
    END AS city,
    -- Assign states based on city/station patterns
    CASE 
        WHEN missing_stations.station_name LIKE '%New Delhi%' OR missing_stations.station_name LIKE '%Nizamuddin%' THEN 'Delhi'
        WHEN missing_stations.station_name LIKE '%Mumbai%' THEN 'Maharashtra'
        WHEN missing_stations.station_name LIKE '%Howrah%' OR missing_stations.station_name LIKE '%Sealdah%' THEN 'West Bengal'
        WHEN missing_stations.station_name LIKE '%Chennai%' THEN 'Tamil Nadu'
        WHEN missing_stations.station_name LIKE '%Bangalore%' THEN 'Karnataka'
        WHEN missing_stations.station_name LIKE '%Pune%' THEN 'Maharashtra'
        WHEN missing_stations.station_name LIKE '%Hyderabad%' OR missing_stations.station_name LIKE '%Secunderabad%' THEN 'Telangana'
        WHEN missing_stations.station_name LIKE '%Ahmedabad%' THEN 'Gujarat'
        WHEN missing_stations.station_name LIKE '%Amritsar%' THEN 'Punjab'
        WHEN missing_stations.station_name LIKE '%Bhopal%' THEN 'Madhya Pradesh'
        WHEN missing_stations.station_name LIKE '%Chandigarh%' THEN 'Chandigarh'
        WHEN missing_stations.station_name LIKE '%Jaipur%' THEN 'Rajasthan'
        WHEN missing_stations.station_name LIKE '%Puri%' THEN 'Odisha'
        WHEN missing_stations.station_name LIKE '%Ernakulam%' OR missing_stations.station_name LIKE '%Thiruvananthapuram%' THEN 'Kerala'
        WHEN missing_stations.station_name LIKE '%Madgaon%' THEN 'Goa'
        WHEN missing_stations.station_name LIKE '%Dibrugarh%' THEN 'Assam'
        WHEN missing_stations.station_name LIKE '%Firozpur%' THEN 'Punjab'
        -- Default state assignment
        ELSE 'Unknown'
    END AS state
FROM (
    SELECT source AS station_name FROM train
    UNION
    SELECT destination AS station_name FROM train
) AS missing_stations
LEFT JOIN station ON station.name = missing_stations.station_name
WHERE station.name IS NULL;

-- Step 6: Verify all stations are now present
SELECT 'Verification - stations after insert:' AS info, COUNT(*) AS count FROM station;

-- Step 7: Check if any train stations are still missing
SELECT 'Final check - missing stations (should be 0):' AS info, COUNT(*) AS count FROM (
    SELECT DISTINCT missing_stations.station_name
    FROM (
        SELECT source AS station_name FROM train
        UNION
        SELECT destination AS station_name FROM train
    ) AS missing_stations
    LEFT JOIN station ON station.name = missing_stations.station_name
    WHERE station.name IS NULL
) AS still_missing;

-- Step 8: Show all stations used in trains with their details
SELECT 'All stations used in train routes:' AS info;
SELECT DISTINCT s.id, s.name, s.code, s.city, s.state
FROM station s
INNER JOIN (
    SELECT source AS station_name FROM train
    UNION
    SELECT destination AS station_name FROM train
) AS train_stations ON s.name = train_stations.station_name
ORDER BY s.name;

-- Step 9: Summary report
SELECT 'SUMMARY REPORT' AS '=== SUMMARY ===';
SELECT 'Total stations in station table:' AS metric, COUNT(*) AS value FROM station
UNION ALL
SELECT 'Unique stations in train routes:' AS metric, COUNT(*) AS value FROM (
    SELECT source AS station_name FROM train
    UNION
    SELECT destination AS station_name FROM train
) AS unique_train_stations
UNION ALL
SELECT 'Total trains:' AS metric, COUNT(*) AS value FROM train;