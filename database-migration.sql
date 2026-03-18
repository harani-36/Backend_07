-- RAILNOVA DATABASE MIGRATION SCRIPT
-- Run this script to fix existing issues and migrate to corrected schema

-- ===== BACKUP EXISTING DATA =====
USE train_db;

-- Backup existing coach data
CREATE TABLE IF NOT EXISTS coach_backup AS SELECT * FROM coach;
CREATE TABLE IF NOT EXISTS seat_backup AS SELECT * FROM seat;

-- ===== APPLY SCHEMA FIXES =====

-- 1. Fix Coach Table - Remove journey_id, add train_id
ALTER TABLE coach DROP FOREIGN KEY IF EXISTS coach_ibfk_1;
ALTER TABLE coach DROP COLUMN IF EXISTS journey_id;
ALTER TABLE coach ADD COLUMN IF NOT EXISTS train_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE coach ADD CONSTRAINT fk_coach_train FOREIGN KEY (train_id) REFERENCES train(id);

-- 2. Fix Seat Table - Add journey_id for availability tracking
ALTER TABLE seat ADD COLUMN IF NOT EXISTS journey_id BIGINT;
ALTER TABLE seat ADD CONSTRAINT fk_seat_journey FOREIGN KEY (journey_id) REFERENCES journey(id);

-- 3. Update Fare Table Schema
USE fare_db;
ALTER TABLE fare CHANGE COLUMN amount base_fare DECIMAL(10,2) NOT NULL;
ALTER TABLE fare ADD COLUMN IF NOT EXISTS distance_km INT DEFAULT 1000;
ALTER TABLE fare ADD COLUMN IF NOT EXISTS surge_multiplier DECIMAL(3,2) DEFAULT 1.0;

-- ===== POPULATE CORRECTED DATA =====

-- Fix coach-train relationships
USE train_db;

-- Clear existing coach data and repopulate correctly
DELETE FROM coach;

-- Insert coaches for each train (not per journey)
INSERT INTO coach (train_id, coach_number, coach_type, total_seats) VALUES
-- Rajdhani Express (Train 1)
(1, 'H1', 'AC_1_TIER', 18),
(1, 'A1', 'AC_2_TIER', 46),
(1, 'A2', 'AC_2_TIER', 46),
(1, 'B1', 'AC_3_TIER', 72),
(1, 'B2', 'AC_3_TIER', 72),

-- Shatabdi Express (Train 2)
(2, 'C1', 'AC_1_TIER', 18),
(2, 'C2', 'AC_2_TIER', 46),
(2, 'C3', 'AC_2_TIER', 46),
(2, 'C4', 'AC_3_TIER', 72),

-- Duronto Express (Train 3)
(3, 'A1', 'AC_2_TIER', 46),
(3, 'A2', 'AC_2_TIER', 46),
(3, 'B1', 'AC_3_TIER', 72),
(3, 'B2', 'AC_3_TIER', 72),
(3, 'S1', 'SLEEPER', 72),
(3, 'S2', 'SLEEPER', 72),

-- Garib Rath (Train 4)
(4, 'G1', 'AC_3_TIER', 72),
(4, 'G2', 'AC_3_TIER', 72),
(4, 'S1', 'SLEEPER', 72),

-- Jan Shatabdi (Train 5)
(5, 'J1', 'AC_2_TIER', 46),
(5, 'J2', 'AC_3_TIER', 72);

-- Generate seats for each coach-journey combination
DELIMITER //
CREATE PROCEDURE GenerateSeats()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE coach_id BIGINT;
    DECLARE journey_id BIGINT;
    DECLARE coach_type VARCHAR(20);
    DECLARE total_seats INT;
    DECLARE seat_num INT;
    
    DECLARE coach_cursor CURSOR FOR 
        SELECT c.id, c.coach_type, c.total_seats 
        FROM coach c;
    
    DECLARE journey_cursor CURSOR FOR 
        SELECT j.id 
        FROM journey j;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- Clear existing seats
    DELETE FROM seat;
    
    -- For each coach
    OPEN coach_cursor;
    coach_loop: LOOP
        FETCH coach_cursor INTO coach_id, coach_type, total_seats;
        IF done THEN
            LEAVE coach_loop;
        END IF;
        
        -- For each journey
        SET done = FALSE;
        OPEN journey_cursor;
        journey_loop: LOOP
            FETCH journey_cursor INTO journey_id;
            IF done THEN
                LEAVE journey_loop;
            END IF;
            
            -- Generate seats for this coach-journey combination
            SET seat_num = 1;
            WHILE seat_num <= total_seats DO
                INSERT INTO seat (seat_number, coach_id, journey_id, status) 
                VALUES (CONCAT(SUBSTRING(coach_type, 1, 1), seat_num), coach_id, journey_id, 'AVAILABLE');
                SET seat_num = seat_num + 1;
            END WHILE;
            
        END LOOP journey_loop;
        CLOSE journey_cursor;
        SET done = FALSE;
        
    END LOOP coach_loop;
    CLOSE coach_cursor;
END //
DELIMITER ;

-- Execute the procedure
CALL GenerateSeats();
DROP PROCEDURE GenerateSeats;

-- ===== VERIFICATION QUERIES =====
SELECT 'Migration completed successfully!' as Status;

SELECT 'Coach-Train Relationships:' as Info;
SELECT t.name as train_name, c.coach_type, COUNT(*) as coach_count
FROM train t 
JOIN coach c ON t.id = c.train_id 
GROUP BY t.name, c.coach_type 
ORDER BY t.name, c.coach_type;

SELECT 'Seat Availability by Journey:' as Info;
SELECT j.journey_date, t.name, c.coach_type, 
       COUNT(s.id) as total_seats,
       SUM(CASE WHEN s.status = 'AVAILABLE' THEN 1 ELSE 0 END) as available_seats
FROM journey j
JOIN train t ON j.train_id = t.id
JOIN coach c ON c.train_id = t.id
JOIN seat s ON s.coach_id = c.id AND s.journey_id = j.id
GROUP BY j.journey_date, t.name, c.coach_type
ORDER BY j.journey_date, t.name, c.coach_type;

-- Verify fare data
USE fare_db;
SELECT 'Fare Data:' as Info;
SELECT f.train_id, f.coach_type, f.base_fare, f.distance_km
FROM fare f
ORDER BY f.train_id, f.coach_type;