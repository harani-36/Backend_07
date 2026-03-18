-- Comprehensive Train, Journey, and Fare Data
-- Run this in MySQL after selecting train_db and fare_db
-- Existing trains (IDs 1-5) are preserved via INSERT IGNORE

USE train_db;

-- Insert 50 popular trains (IDs 6-55, preserving existing 1-5)
INSERT IGNORE INTO train (id, train_number, name, source, destination) VALUES
-- Premium Express Trains
(6,  '12301', 'Rajdhani Express',           'New Delhi Railway Station',    'Howrah Junction'),
(7,  '12302', 'Rajdhani Express',           'Howrah Junction',               'New Delhi Railway Station'),
(8,  '12951', 'Mumbai Rajdhani',            'New Delhi Railway Station',    'Mumbai Central'),
(9,  '12952', 'Mumbai Rajdhani',            'Mumbai Central',                'New Delhi Railway Station'),
(10, '12621', 'Tamil Nadu Express',         'New Delhi Railway Station',    'Chennai Central'),
(11, '12622', 'Tamil Nadu Express',         'Chennai Central',               'New Delhi Railway Station'),
(12, '12009', 'Shatabdi Express',           'New Delhi Railway Station',    'Amritsar Junction'),
(13, '12010', 'Shatabdi Express',           'Amritsar Junction',             'New Delhi Railway Station'),
(14, '12019', 'Shatabdi Express',           'New Delhi Railway Station',    'Bhopal Junction'),
(15, '12020', 'Shatabdi Express',           'Bhopal Junction',               'New Delhi Railway Station'),

-- Duronto Express (Non-stop)
(16, '12259', 'Duronto Express',            'New Delhi Railway Station',    'Sealdah'),
(17, '12260', 'Duronto Express',            'Sealdah',                       'New Delhi Railway Station'),
(18, '12263', 'Duronto Express',            'New Delhi Railway Station',    'Pune Junction'),
(19, '12264', 'Duronto Express',            'Pune Junction',                 'New Delhi Railway Station'),

-- Garib Rath (AC Budget)
(20, '12429', 'Garib Rath',                'New Delhi Railway Station',    'Bangalore City Junction'),
(21, '12430', 'Garib Rath',                'Bangalore City Junction',       'New Delhi Railway Station'),
(22, '12433', 'Garib Rath',                'New Delhi Railway Station',    'Chennai Central'),
(23, '12434', 'Garib Rath',                'Chennai Central',               'New Delhi Railway Station'),

-- Jan Shatabdi (Day trains)
(24, '12023', 'Jan Shatabdi',              'New Delhi Railway Station',    'Chandigarh'),
(25, '12024', 'Jan Shatabdi',              'Chandigarh',                    'New Delhi Railway Station'),
(26, '12055', 'Jan Shatabdi',              'New Delhi Railway Station',    'Jaipur Junction'),
(27, '12056', 'Jan Shatabdi',              'Jaipur Junction',               'New Delhi Railway Station'),

-- Superfast Express
(28, '12423', 'Dibrugarh Rajdhani',        'New Delhi Railway Station',    'Dibrugarh Town'),
(29, '12424', 'Dibrugarh Rajdhani',        'Dibrugarh Town',                'New Delhi Railway Station'),
(30, '12801', 'Purushottam Express',       'New Delhi Railway Station',    'Puri'),
(31, '12802', 'Purushottam Express',       'Puri',                          'New Delhi Railway Station'),
(32, '12615', 'Grand Trunk Express',       'New Delhi Railway Station',    'Chennai Central'),
(33, '12616', 'Grand Trunk Express',       'Chennai Central',               'New Delhi Railway Station'),

-- Regional Superfast
(34, '12137', 'Punjab Mail',               'Mumbai CST',                    'Firozpur Cantt'),
(35, '12138', 'Punjab Mail',               'Firozpur Cantt',                'Mumbai CST'),
(36, '12841', 'Coromandel Express',        'Howrah Junction',               'Chennai Central'),
(37, '12842', 'Coromandel Express',        'Chennai Central',               'Howrah Junction'),
(38, '12859', 'Gitanjali Express',         'Howrah Junction',               'Mumbai CST'),
(39, '12860', 'Gitanjali Express',         'Mumbai CST',                    'Howrah Junction'),

-- Popular Regional Trains
(40, '12617', 'Mangala Express',           'Hazrat Nizamuddin',             'Ernakulam Junction'),
(41, '12618', 'Mangala Express',           'Ernakulam Junction',            'Hazrat Nizamuddin'),
(42, '12649', 'Karnataka Express',         'New Delhi Railway Station',    'Bangalore City Junction'),
(43, '12650', 'Karnataka Express',         'Bangalore City Junction',       'New Delhi Railway Station'),
(44, '12723', 'Telangana Express',         'New Delhi Railway Station',    'Secunderabad Junction'),
(45, '12724', 'Telangana Express',         'Secunderabad Junction',        'New Delhi Railway Station'),

-- West Coast Trains
(46, '12431', 'Trivandrum Rajdhani',       'New Delhi Railway Station',    'Thiruvananthapuram Central'),
(47, '12432', 'Trivandrum Rajdhani',       'Thiruvananthapuram Central',    'New Delhi Railway Station'),
(48, '12779', 'Goa Express',               'Hazrat Nizamuddin',             'Madgaon Junction'),
(49, '12780', 'Goa Express',               'Madgaon Junction',              'Hazrat Nizamuddin'),

-- Mumbai-Bangalore Route
(50, '12627', 'Karnataka Express',         'New Delhi Railway Station',    'Bangalore City Junction'),
(51, '12628', 'Karnataka Express',         'Bangalore City Junction',       'New Delhi Railway Station'),
(52, '16339', 'Nagarcoil Express',         'Mumbai CST',                    'Thiruvananthapuram Central'),
(53, '16340', 'Nagarcoil Express',         'Thiruvananthapuram Central',    'Mumbai CST'),

-- East Coast
(54, '12703', 'Falaknuma Express',         'Secunderabad Junction',        'Howrah Junction'),
(55, '12704', 'Falaknuma Express',         'Howrah Junction',               'Secunderabad Junction');

-- Insert Journey data for next 30 days (multiple journeys per train)
INSERT IGNORE INTO journey (id, train_id, journey_date, departure_time, arrival_time) VALUES
-- Rajdhani Express (Daily)
(8,  6,  '2026-03-17', '16:55:00', '10:05:00'),
(9,  6,  '2026-03-18', '16:55:00', '10:05:00'),
(10, 6,  '2026-03-19', '16:55:00', '10:05:00'),
(11, 7,  '2026-03-17', '16:50:00', '09:55:00'),
(12, 7,  '2026-03-18', '16:50:00', '09:55:00'),
(13, 7,  '2026-03-19', '16:50:00', '09:55:00'),

-- Mumbai Rajdhani (Daily except Wed)
(14, 8,  '2026-03-17', '16:00:00', '08:35:00'),
(15, 8,  '2026-03-19', '16:00:00', '08:35:00'),
(16, 8,  '2026-03-20', '16:00:00', '08:35:00'),
(17, 9,  '2026-03-17', '16:30:00', '09:15:00'),
(18, 9,  '2026-03-19', '16:30:00', '09:15:00'),
(19, 9,  '2026-03-20', '16:30:00', '09:15:00'),

-- Tamil Nadu Express (Daily)
(20, 10, '2026-03-17', '22:30:00', '06:15:00'),
(21, 10, '2026-03-18', '22:30:00', '06:15:00'),
(22, 10, '2026-03-19', '22:30:00', '06:15:00'),
(23, 11, '2026-03-17', '22:45:00', '06:20:00'),
(24, 11, '2026-03-18', '22:45:00', '06:20:00'),
(25, 11, '2026-03-19', '22:45:00', '06:20:00'),

-- Shatabdi Express (Daily except Sunday)
(26, 12, '2026-03-17', '06:20:00', '12:20:00'),
(27, 12, '2026-03-18', '06:20:00', '12:20:00'),
(28, 12, '2026-03-19', '06:20:00', '12:20:00'),
(29, 13, '2026-03-17', '15:30:00', '21:30:00'),
(30, 13, '2026-03-18', '15:30:00', '21:30:00'),
(31, 13, '2026-03-19', '15:30:00', '21:30:00'),

-- Bhopal Shatabdi (Daily except Sunday)
(32, 14, '2026-03-17', '06:00:00', '14:05:00'),
(33, 14, '2026-03-18', '06:00:00', '14:05:00'),
(34, 14, '2026-03-19', '06:00:00', '14:05:00'),
(35, 15, '2026-03-17', '14:40:00', '22:45:00'),
(36, 15, '2026-03-18', '14:40:00', '22:45:00'),
(37, 15, '2026-03-19', '14:40:00', '22:45:00'),

-- Duronto Express (3 days a week)
(38, 16, '2026-03-17', '20:50:00', '12:55:00'),
(39, 16, '2026-03-19', '20:50:00', '12:55:00'),
(40, 16, '2026-03-21', '20:50:00', '12:55:00'),
(41, 17, '2026-03-18', '15:50:00', '07:40:00'),
(42, 17, '2026-03-20', '15:50:00', '07:40:00'),
(43, 17, '2026-03-22', '15:50:00', '07:40:00'),

-- Pune Duronto (2 days a week)
(44, 18, '2026-03-18', '17:25:00', '06:00:00'),
(45, 18, '2026-03-21', '17:25:00', '06:00:00'),
(46, 19, '2026-03-19', '17:15:00', '05:50:00'),
(47, 19, '2026-03-22', '17:15:00', '05:50:00'),

-- Garib Rath (Weekly)
(48, 20, '2026-03-17', '12:50:00', '16:30:00'),
(49, 20, '2026-03-24', '12:50:00', '16:30:00'),
(50, 21, '2026-03-18', '20:15:00', '23:45:00'),
(51, 21, '2026-03-25', '20:15:00', '23:45:00'),

-- Jan Shatabdi (Daily except Sunday)
(52, 24, '2026-03-17', '07:40:00', '11:35:00'),
(53, 24, '2026-03-18', '07:40:00', '11:35:00'),
(54, 24, '2026-03-19', '07:40:00', '11:35:00'),
(55, 25, '2026-03-17', '17:25:00', '21:20:00'),
(56, 25, '2026-03-18', '17:25:00', '21:20:00'),
(57, 25, '2026-03-19', '17:25:00', '21:20:00'),

-- Jaipur Jan Shatabdi
(58, 26, '2026-03-17', '06:05:00', '10:30:00'),
(59, 26, '2026-03-18', '06:05:00', '10:30:00'),
(60, 26, '2026-03-19', '06:05:00', '10:30:00'),
(61, 27, '2026-03-17', '15:50:00', '20:15:00'),
(62, 27, '2026-03-18', '15:50:00', '20:15:00'),
(63, 27, '2026-03-19', '15:50:00', '20:15:00'),

-- More popular routes
(64, 30, '2026-03-17', '14:05:00', '15:20:00'),
(65, 30, '2026-03-19', '14:05:00', '15:20:00'),
(66, 31, '2026-03-18', '19:15:00', '20:30:00'),
(67, 31, '2026-03-20', '19:15:00', '20:30:00'),

-- Regional Express
(68, 36, '2026-03-17', '14:15:00', '13:50:00'),
(69, 36, '2026-03-18', '14:15:00', '13:50:00'),
(70, 37, '2026-03-17', '08:25:00', '07:50:00'),
(71, 37, '2026-03-18', '08:25:00', '07:50:00'),

-- Karnataka Express
(72, 42, '2026-03-17', '20:15:00', '04:30:00'),
(73, 42, '2026-03-18', '20:15:00', '04:30:00'),
(74, 43, '2026-03-17', '21:35:00', '05:50:00'),
(75, 43, '2026-03-18', '21:35:00', '05:50:00'),

-- Telangana Express
(76, 44, '2026-03-17', '17:40:00', '09:55:00'),
(77, 44, '2026-03-18', '17:40:00', '09:55:00'),
(78, 45, '2026-03-17', '20:10:00', '12:25:00'),
(79, 45, '2026-03-18', '20:10:00', '12:25:00'),

-- Trivandrum Rajdhani (2 days a week)
(80, 46, '2026-03-18', '11:00:00', '11:10:00'),
(81, 46, '2026-03-21', '11:00:00', '11:10:00'),
(82, 47, '2026-03-19', '16:15:00', '16:25:00'),
(83, 47, '2026-03-22', '16:15:00', '16:25:00'),

-- Goa Express (Daily)
(84, 48, '2026-03-17', '15:00:00', '12:00:00'),
(85, 48, '2026-03-18', '15:00:00', '12:00:00'),
(86, 48, '2026-03-19', '15:00:00', '12:00:00'),
(87, 49, '2026-03-17', '07:00:00', '04:00:00'),
(88, 49, '2026-03-18', '07:00:00', '04:00:00'),
(89, 49, '2026-03-19', '07:00:00', '04:00:00'),

-- Additional journeys for next week (March 20-26, 2026)
(90, 6,  '2026-03-20', '16:55:00', '10:05:00'),
(91, 6,  '2026-03-21', '16:55:00', '10:05:00'),
(92, 6,  '2026-03-22', '16:55:00', '10:05:00'),
(93, 7,  '2026-03-20', '16:50:00', '09:55:00'),
(94, 7,  '2026-03-21', '16:50:00', '09:55:00'),
(95, 7,  '2026-03-22', '16:50:00', '09:55:00'),

-- Mumbai Rajdhani next week
(96, 8,  '2026-03-21', '16:00:00', '08:35:00'),
(97, 8,  '2026-03-22', '16:00:00', '08:35:00'),
(98, 8,  '2026-03-24', '16:00:00', '08:35:00'),
(99, 9,  '2026-03-21', '16:30:00', '09:15:00'),
(100, 9, '2026-03-22', '16:30:00', '09:15:00'),
(101, 9, '2026-03-24', '16:30:00', '09:15:00'),

-- Tamil Nadu Express next week
(102, 10, '2026-03-20', '22:30:00', '06:15:00'),
(103, 10, '2026-03-21', '22:30:00', '06:15:00'),
(104, 10, '2026-03-22', '22:30:00', '06:15:00'),
(105, 11, '2026-03-20', '22:45:00', '06:20:00'),
(106, 11, '2026-03-21', '22:45:00', '06:20:00'),
(107, 11, '2026-03-22', '22:45:00', '06:20:00'),

-- Weekend and future journeys
(108, 12, '2026-03-20', '06:20:00', '12:20:00'),
(109, 12, '2026-03-21', '06:20:00', '12:20:00'),
(110, 13, '2026-03-20', '15:30:00', '21:30:00'),
(111, 13, '2026-03-21', '15:30:00', '21:30:00'),

-- More future dates (March 23-30, 2026)
(112, 24, '2026-03-23', '07:40:00', '11:35:00'),
(113, 24, '2026-03-24', '07:40:00', '11:35:00'),
(114, 24, '2026-03-25', '07:40:00', '11:35:00'),
(115, 25, '2026-03-23', '17:25:00', '21:20:00'),
(116, 25, '2026-03-24', '17:25:00', '21:20:00'),
(117, 25, '2026-03-25', '17:25:00', '21:20:00'),

-- Karnataka Express future dates
(118, 42, '2026-03-23', '20:15:00', '04:30:00'),
(119, 42, '2026-03-24', '20:15:00', '04:30:00'),
(120, 42, '2026-03-25', '20:15:00', '04:30:00'),
(121, 43, '2026-03-23', '21:35:00', '05:50:00'),
(122, 43, '2026-03-24', '21:35:00', '05:50:00'),
(123, 43, '2026-03-25', '21:35:00', '05:50:00'),

-- Goa Express future dates
(124, 48, '2026-03-23', '15:00:00', '12:00:00'),
(125, 48, '2026-03-24', '15:00:00', '12:00:00'),
(126, 48, '2026-03-25', '15:00:00', '12:00:00'),
(127, 49, '2026-03-23', '07:00:00', '04:00:00'),
(128, 49, '2026-03-24', '07:00:00', '04:00:00'),
(129, 49, '2026-03-25', '07:00:00', '04:00:00'),

-- April 2026 journeys (next month)
(130, 6,  '2026-04-01', '16:55:00', '10:05:00'),
(131, 6,  '2026-04-02', '16:55:00', '10:05:00'),
(132, 6,  '2026-04-03', '16:55:00', '10:05:00'),
(133, 10, '2026-04-01', '22:30:00', '06:15:00'),
(134, 10, '2026-04-02', '22:30:00', '06:15:00'),
(135, 10, '2026-04-03', '22:30:00', '06:15:00'),
(136, 42, '2026-04-01', '20:15:00', '04:30:00'),
(137, 42, '2026-04-02', '20:15:00', '04:30:00'),
(138, 42, '2026-04-03', '20:15:00', '04:30:00');

-- Switch to fare_db for fare data
USE fare_db;

-- Insert comprehensive fare data for all trains and coach types
INSERT IGNORE INTO fare (id, train_id, coach_type, amount) VALUES
-- Rajdhani Express (Premium pricing)
(11, 6,  'AC_1_TIER',  4500.00),
(12, 6,  'AC_2_TIER',  3200.00),
(13, 6,  'AC_3_TIER',  2400.00),
(14, 7,  'AC_1_TIER',  4500.00),
(15, 7,  'AC_2_TIER',  3200.00),
(16, 7,  'AC_3_TIER',  2400.00),

-- Mumbai Rajdhani
(17, 8,  'AC_1_TIER',  4200.00),
(18, 8,  'AC_2_TIER',  3000.00),
(19, 8,  'AC_3_TIER',  2200.00),
(20, 9,  'AC_1_TIER',  4200.00),
(21, 9,  'AC_2_TIER',  3000.00),
(22, 9,  'AC_3_TIER',  2200.00),

-- Tamil Nadu Express
(23, 10, 'AC_1_TIER',  5200.00),
(24, 10, 'AC_2_TIER',  3800.00),
(25, 10, 'AC_3_TIER',  2800.00),
(26, 10, 'SLEEPER',    1200.00),
(27, 11, 'AC_1_TIER',  5200.00),
(28, 11, 'AC_2_TIER',  3800.00),
(29, 11, 'AC_3_TIER',  2800.00),
(30, 11, 'SLEEPER',    1200.00),

-- Shatabdi Express (Day train - Chair Car)
(31, 12, 'AC_CHAIR',   1800.00),
(32, 12, 'EXECUTIVE',  2800.00),
(33, 13, 'AC_CHAIR',   1800.00),
(34, 13, 'EXECUTIVE',  2800.00),

-- Bhopal Shatabdi
(35, 14, 'AC_CHAIR',   1200.00),
(36, 14, 'EXECUTIVE',  1800.00),
(37, 15, 'AC_CHAIR',   1200.00),
(38, 15, 'EXECUTIVE',  1800.00),

-- Duronto Express (Non-stop premium)
(39, 16, 'AC_1_TIER',  4800.00),
(40, 16, 'AC_2_TIER',  3400.00),
(41, 16, 'AC_3_TIER',  2600.00),
(42, 17, 'AC_1_TIER',  4800.00),
(43, 17, 'AC_2_TIER',  3400.00),
(44, 17, 'AC_3_TIER',  2600.00),

-- Pune Duronto
(45, 18, 'AC_1_TIER',  3800.00),
(46, 18, 'AC_2_TIER',  2800.00),
(47, 18, 'AC_3_TIER',  2000.00),
(48, 19, 'AC_1_TIER',  3800.00),
(49, 19, 'AC_2_TIER',  2800.00),
(50, 19, 'AC_3_TIER',  2000.00),

-- Garib Rath (Budget AC)
(51, 20, 'AC_3_TIER',  2600.00),
(52, 21, 'AC_3_TIER',  2600.00),
(53, 22, 'AC_3_TIER',  2800.00),
(54, 23, 'AC_3_TIER',  2800.00),

-- Jan Shatabdi (Budget day train)
(55, 24, 'AC_CHAIR',   800.00),
(56, 24, 'CC',         600.00),
(57, 25, 'AC_CHAIR',   800.00),
(58, 25, 'CC',         600.00),

-- Jaipur Jan Shatabdi
(59, 26, 'AC_CHAIR',   900.00),
(60, 26, 'CC',         700.00),
(61, 27, 'AC_CHAIR',   900.00),
(62, 27, 'CC',         700.00),

-- Dibrugarh Rajdhani (Longest route)
(63, 28, 'AC_1_TIER',  6500.00),
(64, 28, 'AC_2_TIER',  4800.00),
(65, 28, 'AC_3_TIER',  3600.00),
(66, 29, 'AC_1_TIER',  6500.00),
(67, 29, 'AC_2_TIER',  4800.00),
(68, 29, 'AC_3_TIER',  3600.00),

-- Purushottam Express
(69, 30, 'AC_1_TIER',  4800.00),
(70, 30, 'AC_2_TIER',  3400.00),
(71, 30, 'AC_3_TIER',  2600.00),
(72, 30, 'SLEEPER',    1100.00),
(73, 31, 'AC_1_TIER',  4800.00),
(74, 31, 'AC_2_TIER',  3400.00),
(75, 31, 'AC_3_TIER',  2600.00),
(76, 31, 'SLEEPER',    1100.00),

-- Grand Trunk Express
(77, 32, 'AC_1_TIER',  5000.00),
(78, 32, 'AC_2_TIER',  3600.00),
(79, 32, 'AC_3_TIER',  2700.00),
(80, 32, 'SLEEPER',    1150.00),
(81, 33, 'AC_1_TIER',  5000.00),
(82, 33, 'AC_2_TIER',  3600.00),
(83, 33, 'AC_3_TIER',  2700.00),
(84, 33, 'SLEEPER',    1150.00),

-- Punjab Mail
(85, 34, 'AC_2_TIER',  3800.00),
(86, 34, 'AC_3_TIER',  2800.00),
(87, 34, 'SLEEPER',    1300.00),
(88, 35, 'AC_2_TIER',  3800.00),
(89, 35, 'AC_3_TIER',  2800.00),
(90, 35, 'SLEEPER',    1300.00),

-- Coromandel Express
(91, 36, 'AC_1_TIER',  4200.00),
(92, 36, 'AC_2_TIER',  3000.00),
(93, 36, 'AC_3_TIER',  2200.00),
(94, 36, 'SLEEPER',    950.00),
(95, 37, 'AC_1_TIER',  4200.00),
(96, 37, 'AC_2_TIER',  3000.00),
(97, 37, 'AC_3_TIER',  2200.00),
(98, 37, 'SLEEPER',    950.00),

-- Gitanjali Express
(99,  38, 'AC_1_TIER',  4400.00),
(100, 38, 'AC_2_TIER',  3200.00),
(101, 38, 'AC_3_TIER',  2400.00),
(102, 38, 'SLEEPER',    1000.00),
(103, 39, 'AC_1_TIER',  4400.00),
(104, 39, 'AC_2_TIER',  3200.00),
(105, 39, 'AC_3_TIER',  2400.00),
(106, 39, 'SLEEPER',    1000.00),

-- Mangala Express
(107, 40, 'AC_1_TIER',  5800.00),
(108, 40, 'AC_2_TIER',  4200.00),
(109, 40, 'AC_3_TIER',  3200.00),
(110, 40, 'SLEEPER',    1400.00),
(111, 41, 'AC_1_TIER',  5800.00),
(112, 41, 'AC_2_TIER',  4200.00),
(113, 41, 'AC_3_TIER',  3200.00),
(114, 41, 'SLEEPER',    1400.00),

-- Karnataka Express
(115, 42, 'AC_1_TIER',  5400.00),
(116, 42, 'AC_2_TIER',  3900.00),
(117, 42, 'AC_3_TIER',  2900.00),
(118, 42, 'SLEEPER',    1250.00),
(119, 43, 'AC_1_TIER',  5400.00),
(120, 43, 'AC_2_TIER',  3900.00),
(121, 43, 'AC_3_TIER',  2900.00),
(122, 43, 'SLEEPER',    1250.00),

-- Telangana Express
(123, 44, 'AC_1_TIER',  4600.00),
(124, 44, 'AC_2_TIER',  3300.00),
(125, 44, 'AC_3_TIER',  2500.00),
(126, 44, 'SLEEPER',    1050.00),
(127, 45, 'AC_1_TIER',  4600.00),
(128, 45, 'AC_2_TIER',  3300.00),
(129, 45, 'AC_3_TIER',  2500.00),
(130, 45, 'SLEEPER',    1050.00),

-- Trivandrum Rajdhani
(131, 46, 'AC_1_TIER',  6200.00),
(132, 46, 'AC_2_TIER',  4500.00),
(133, 46, 'AC_3_TIER',  3400.00),
(134, 47, 'AC_1_TIER',  6200.00),
(135, 47, 'AC_2_TIER',  4500.00),
(136, 47, 'AC_3_TIER',  3400.00),

-- Goa Express
(137, 48, 'AC_1_TIER',  3800.00),
(138, 48, 'AC_2_TIER',  2700.00),
(139, 48, 'AC_3_TIER',  2000.00),
(140, 48, 'SLEEPER',    850.00),
(141, 49, 'AC_1_TIER',  3800.00),
(142, 49, 'AC_2_TIER',  2700.00),
(143, 49, 'AC_3_TIER',  2000.00),
(144, 49, 'SLEEPER',    850.00),

-- Additional Karnataka Express
(145, 50, 'AC_1_TIER',  5400.00),
(146, 50, 'AC_2_TIER',  3900.00),
(147, 50, 'AC_3_TIER',  2900.00),
(148, 50, 'SLEEPER',    1250.00),
(149, 51, 'AC_1_TIER',  5400.00),
(150, 51, 'AC_2_TIER',  3900.00),
(151, 51, 'AC_3_TIER',  2900.00),
(152, 51, 'SLEEPER',    1250.00),

-- Nagarcoil Express
(153, 52, 'AC_2_TIER',  4800.00),
(154, 52, 'AC_3_TIER',  3600.00),
(155, 52, 'SLEEPER',    1600.00),
(156, 53, 'AC_2_TIER',  4800.00),
(157, 53, 'AC_3_TIER',  3600.00),
(158, 53, 'SLEEPER',    1600.00),

-- Falaknuma Express
(159, 54, 'AC_2_TIER',  3600.00),
(160, 54, 'AC_3_TIER',  2700.00),
(161, 54, 'SLEEPER',    1150.00),
(162, 55, 'AC_2_TIER',  3600.00),
(163, 55, 'AC_3_TIER',  2700.00),
(164, 55, 'SLEEPER',    1150.00);

-- Summary
USE train_db;
SELECT CONCAT('Total trains: ', COUNT(*)) AS TrainCount FROM train;
SELECT CONCAT('Total journeys: ', COUNT(*)) AS JourneyCount FROM journey;

USE fare_db;
SELECT CONCAT('Total fare entries: ', COUNT(*)) AS FareCount FROM fare;

SELECT 'Data insertion completed successfully!' AS Status;