-- CORRECTED RAILNOVA DATABASE SCHEMA
-- Fixes all identified issues with coach, seat, and fare tables

-- ===== TRAIN DATABASE =====
USE train_db;

-- 1. STATIONS TABLE (Correct)
CREATE TABLE IF NOT EXISTS station (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(10) UNIQUE NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL
);

-- 2. TRAINS TABLE (Correct)
CREATE TABLE IF NOT EXISTS train (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    train_number VARCHAR(20) UNIQUE NOT NULL,
    source_station VARCHAR(255) NOT NULL,
    destination_station VARCHAR(255) NOT NULL,
    total_seats INT NOT NULL
);

-- 3. JOURNEYS TABLE (Correct)
CREATE TABLE IF NOT EXISTS journey (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    train_id BIGINT NOT NULL,
    departure_time TIME NOT NULL,
    arrival_time TIME NOT NULL,
    journey_date DATE NOT NULL,
    available_seats INT NOT NULL,
    FOREIGN KEY (train_id) REFERENCES train(id),
    UNIQUE KEY unique_train_date (train_id, journey_date)
);

-- 4. COACHES TABLE (FIXED - belongs to TRAIN, not JOURNEY)
DROP TABLE IF EXISTS coach;
CREATE TABLE coach (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    coach_number VARCHAR(10) NOT NULL,
    coach_type ENUM('AC_1_TIER', 'AC_2_TIER', 'AC_3_TIER', 'SLEEPER', 'GENERAL') NOT NULL,
    train_id BIGINT NOT NULL,  -- FIXED: coaches belong to trains
    total_seats INT NOT NULL DEFAULT 72,
    FOREIGN KEY (train_id) REFERENCES train(id),
    UNIQUE KEY unique_coach_train (train_id, coach_number)
);

-- 5. SEATS TABLE (FIXED - includes journey_id for availability tracking)
DROP TABLE IF EXISTS seat;
CREATE TABLE seat (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    seat_number VARCHAR(10) NOT NULL,
    coach_id BIGINT NOT NULL,
    journey_id BIGINT NOT NULL,  -- FIXED: seat availability per journey
    status ENUM('AVAILABLE', 'BOOKED', 'BLOCKED') DEFAULT 'AVAILABLE',
    booking_id BIGINT NULL,
    FOREIGN KEY (coach_id) REFERENCES coach(id),
    FOREIGN KEY (journey_id) REFERENCES journey(id),
    UNIQUE KEY unique_seat_journey (coach_id, seat_number, journey_id)
);

-- ===== FARE DATABASE =====
USE fare_db;

-- 6. FARE TABLE (FIXED - supports dynamic pricing)
DROP TABLE IF EXISTS fare;
CREATE TABLE fare (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    train_id BIGINT NOT NULL,
    coach_type ENUM('AC_1_TIER', 'AC_2_TIER', 'AC_3_TIER', 'SLEEPER', 'GENERAL') NOT NULL,
    base_fare DECIMAL(10,2) NOT NULL,  -- FIXED: renamed from amount
    distance_km INT NOT NULL,
    price_per_km DECIMAL(5,2) DEFAULT 0.50,
    surge_multiplier DECIMAL(3,2) DEFAULT 1.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_train_coach (train_id, coach_type)
);

-- ===== SAMPLE DATA =====

-- Insert coach configurations for trains
USE train_db;
INSERT IGNORE INTO coach (train_id, coach_number, coach_type, total_seats) VALUES
-- Rajdhani Express (Train 1) - Premium train
(1, 'H1', 'AC_1_TIER', 18),
(1, 'A1', 'AC_2_TIER', 46),
(1, 'A2', 'AC_2_TIER', 46),
(1, 'B1', 'AC_3_TIER', 72),
(1, 'B2', 'AC_3_TIER', 72),

-- Shatabdi Express (Train 2) - Day train
(2, 'C1', 'AC_1_TIER', 18),
(2, 'C2', 'AC_2_TIER', 46),
(2, 'C3', 'AC_2_TIER', 46),
(2, 'C4', 'AC_3_TIER', 72),

-- Duronto Express (Train 3) - Long distance
(3, 'A1', 'AC_2_TIER', 46),
(3, 'A2', 'AC_2_TIER', 46),
(3, 'B1', 'AC_3_TIER', 72),
(3, 'B2', 'AC_3_TIER', 72),
(3, 'S1', 'SLEEPER', 72),
(3, 'S2', 'SLEEPER', 72);

-- Insert fare data with base pricing
USE fare_db;
INSERT IGNORE INTO fare (train_id, coach_type, base_fare, distance_km) VALUES
-- Rajdhani Express fares
(1, 'AC_1_TIER', 3500.00, 1400),
(1, 'AC_2_TIER', 2500.00, 1400),
(1, 'AC_3_TIER', 1800.00, 1400),

-- Shatabdi Express fares  
(2, 'AC_1_TIER', 4000.00, 2200),
(2, 'AC_2_TIER', 3000.00, 2200),
(2, 'AC_3_TIER', 2200.00, 2200),

-- Duronto Express fares
(3, 'AC_2_TIER', 2800.00, 2000),
(3, 'AC_3_TIER', 2000.00, 2000),
(3, 'SLEEPER', 900.00, 2000);

SELECT 'Schema fixes applied successfully!' as Status;