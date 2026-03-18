-- 100 Popular Indian Railway Stations
-- Run this in MySQL after selecting train_db
-- Existing stations (IDs 1-8) are preserved via INSERT IGNORE

USE train_db;

INSERT IGNORE INTO station (id, name, code, city, state) VALUES
-- Delhi / NCR
(9,  'Hazrat Nizamuddin',         'NZM',  'New Delhi',        'Delhi'),
(10, 'Delhi Cantt',               'DEC',  'New Delhi',        'Delhi'),
(11, 'Anand Vihar Terminal',      'ANVT', 'New Delhi',        'Delhi'),
(12, 'Delhi Sarai Rohilla',       'DEE',  'New Delhi',        'Delhi'),
(13, 'Ghaziabad Junction',        'GZB',  'Ghaziabad',        'Uttar Pradesh'),
(14, 'Faridabad',                 'FDB',  'Faridabad',        'Haryana'),

-- Mumbai
(15, 'Mumbai CST',                'CSTM', 'Mumbai',           'Maharashtra'),
(16, 'Lokmanya Tilak Terminus',   'LTT',  'Mumbai',           'Maharashtra'),
(17, 'Bandra Terminus',           'BDTS', 'Mumbai',           'Maharashtra'),
(18, 'Dadar',                     'DR',   'Mumbai',           'Maharashtra'),
(19, 'Thane',                     'TNA',  'Thane',            'Maharashtra'),

-- Kolkata
(20, 'Sealdah',                   'SDAH', 'Kolkata',          'West Bengal'),
(21, 'Kolkata',                   'KOAA', 'Kolkata',          'West Bengal'),
(22, 'Shalimar',                  'SHM',  'Kolkata',          'West Bengal'),

-- Chennai
(23, 'Chennai Egmore',            'MS',   'Chennai',          'Tamil Nadu'),
(24, 'Chennai Park Town',         'MPT',  'Chennai',          'Tamil Nadu'),

-- Bangalore
(25, 'Bangalore Cantonment',      'BNC',  'Bangalore',        'Karnataka'),
(26, 'Yeshwanthpur Junction',     'YPR',  'Bangalore',        'Karnataka'),
(27, 'Krishnarajapuram',          'KJM',  'Bangalore',        'Karnataka'),

-- Hyderabad / Telangana
(28, 'Secunderabad Junction',     'SC',   'Hyderabad',        'Telangana'),
(29, 'Kacheguda',                 'KCG',  'Hyderabad',        'Telangana'),
(30, 'Nampally',                  'NPA',  'Hyderabad',        'Telangana'),

-- Uttar Pradesh
(31, 'Lucknow Junction',          'LKO',  'Lucknow',          'Uttar Pradesh'),
(32, 'Lucknow NE',                'LJN',  'Lucknow',          'Uttar Pradesh'),
(33, 'Kanpur Central',            'CNB',  'Kanpur',           'Uttar Pradesh'),
(34, 'Varanasi Junction',         'BSB',  'Varanasi',         'Uttar Pradesh'),
(35, 'Prayagraj Junction',        'PRYJ', 'Prayagraj',        'Uttar Pradesh'),
(36, 'Agra Cantt',                'AGC',  'Agra',             'Uttar Pradesh'),
(37, 'Agra Fort',                 'AF',   'Agra',             'Uttar Pradesh'),
(38, 'Mathura Junction',          'MTJ',  'Mathura',          'Uttar Pradesh'),
(39, 'Gwalior Junction',          'GWL',  'Gwalior',          'Madhya Pradesh'),
(40, 'Jhansi Junction',           'JHS',  'Jhansi',           'Uttar Pradesh'),
(41, 'Gorakhpur Junction',        'GKP',  'Gorakhpur',        'Uttar Pradesh'),
(42, 'Mughalsarai Junction',      'MGS',  'Chandauli',        'Uttar Pradesh'),

-- Rajasthan
(43, 'Jaipur Junction',           'JP',   'Jaipur',           'Rajasthan'),
(44, 'Jodhpur Junction',          'JU',   'Jodhpur',          'Rajasthan'),
(45, 'Udaipur City',              'UDZ',  'Udaipur',          'Rajasthan'),
(46, 'Ajmer Junction',            'AII',  'Ajmer',            'Rajasthan'),
(47, 'Bikaner Junction',          'BKN',  'Bikaner',          'Rajasthan'),
(48, 'Kota Junction',             'KOTA', 'Kota',             'Rajasthan'),

-- Madhya Pradesh
(49, 'Bhopal Junction',           'BPL',  'Bhopal',           'Madhya Pradesh'),
(50, 'Habibganj',                 'HBJ',  'Bhopal',           'Madhya Pradesh'),
(51, 'Indore Junction',           'INDB', 'Indore',           'Madhya Pradesh'),
(52, 'Jabalpur Junction',         'JBP',  'Jabalpur',         'Madhya Pradesh'),
(53, 'Ujjain Junction',           'UJN',  'Ujjain',           'Madhya Pradesh'),

-- Gujarat
(54, 'Surat',                     'ST',   'Surat',            'Gujarat'),
(55, 'Vadodara Junction',         'BRC',  'Vadodara',         'Gujarat'),
(56, 'Rajkot Junction',           'RJT',  'Rajkot',           'Gujarat'),
(57, 'Bhavnagar Terminus',        'BVC',  'Bhavnagar',        'Gujarat'),
(58, 'Gandhinagar Capital',       'GNC',  'Gandhinagar',      'Gujarat'),

-- Maharashtra (non-Mumbai)
(59, 'Nagpur Junction',           'NGP',  'Nagpur',           'Maharashtra'),
(60, 'Nashik Road',               'NK',   'Nashik',           'Maharashtra'),
(61, 'Aurangabad',                'AWB',  'Aurangabad',       'Maharashtra'),
(62, 'Solapur Junction',          'SUR',  'Solapur',          'Maharashtra'),
(63, 'Kolhapur',                  'KOP',  'Kolhapur',         'Maharashtra'),

-- Punjab / Haryana / Himachal
(64, 'Amritsar Junction',         'ASR',  'Amritsar',         'Punjab'),
(65, 'Ludhiana Junction',         'LDH',  'Ludhiana',         'Punjab'),
(66, 'Jalandhar City',            'JUC',  'Jalandhar',        'Punjab'),
(67, 'Chandigarh',                'CDG',  'Chandigarh',       'Chandigarh'),
(68, 'Ambala Cantt',              'UMB',  'Ambala',           'Haryana'),
(69, 'Shimla',                    'SML',  'Shimla',           'Himachal Pradesh'),
(70, 'Kalka',                     'KLK',  'Kalka',            'Haryana'),

-- Bihar / Jharkhand
(71, 'Patna Junction',            'PNBE', 'Patna',            'Bihar'),
(72, 'Gaya Junction',             'GAYA', 'Gaya',             'Bihar'),
(73, 'Muzaffarpur Junction',      'MFP',  'Muzaffarpur',      'Bihar'),
(74, 'Ranchi Junction',           'RNC',  'Ranchi',           'Jharkhand'),
(75, 'Dhanbad Junction',          'DHN',  'Dhanbad',          'Jharkhand'),
(76, 'Jamshedpur',                'TATA', 'Jamshedpur',       'Jharkhand'),

-- Odisha
(77, 'Bhubaneswar',               'BBS',  'Bhubaneswar',      'Odisha'),
(78, 'Puri',                      'PURI', 'Puri',             'Odisha'),
(79, 'Cuttack Junction',          'CTC',  'Cuttack',          'Odisha'),

-- Andhra Pradesh
(80, 'Visakhapatnam Junction',    'VSKP', 'Visakhapatnam',    'Andhra Pradesh'),
(81, 'Vijayawada Junction',       'BZA',  'Vijayawada',       'Andhra Pradesh'),
(82, 'Tirupati',                  'TPTY', 'Tirupati',         'Andhra Pradesh'),
(83, 'Guntur Junction',           'GNT',  'Guntur',           'Andhra Pradesh'),

-- Tamil Nadu (non-Chennai)
(84, 'Coimbatore Junction',       'CBE',  'Coimbatore',       'Tamil Nadu'),
(85, 'Madurai Junction',          'MDU',  'Madurai',          'Tamil Nadu'),
(86, 'Tiruchirappalli Junction',  'TPJ',  'Tiruchirappalli',  'Tamil Nadu'),
(87, 'Salem Junction',            'SA',   'Salem',            'Tamil Nadu'),
(88, 'Erode Junction',            'ED',   'Erode',            'Tamil Nadu'),

-- Kerala
(89, 'Thiruvananthapuram Central','TVC',  'Thiruvananthapuram','Kerala'),
(90, 'Ernakulam Junction',        'ERS',  'Kochi',            'Kerala'),
(91, 'Kozhikode',                 'CLT',  'Kozhikode',        'Kerala'),
(92, 'Thrissur',                  'TCR',  'Thrissur',         'Kerala'),
(93, 'Kollam Junction',           'QLN',  'Kollam',           'Kerala'),

-- Karnataka (non-Bangalore)
(94, 'Mysuru Junction',           'MYS',  'Mysuru',           'Karnataka'),
(95, 'Hubli Junction',            'UBL',  'Hubli',            'Karnataka'),
(96, 'Mangaluru Central',         'MAQ',  'Mangaluru',        'Karnataka'),

-- North East / Assam
(97, 'Guwahati',                  'GHY',  'Guwahati',         'Assam'),
(98, 'Dibrugarh Town',            'DBRG', 'Dibrugarh',        'Assam'),

-- Uttarakhand / J&K
(99, 'Dehradun',                  'DDN',  'Dehradun',         'Uttarakhand'),
(100,'Haridwar Junction',         'HW',   'Haridwar',         'Uttarakhand'),
(101,'Jammu Tawi',                'JAT',  'Jammu',            'Jammu & Kashmir'),
(102,'Udhampur',                  'UHP',  'Udhampur',         'Jammu & Kashmir'),

-- Goa / West Coast
(103,'Madgaon Junction',          'MAO',  'Margao',           'Goa'),
(104,'Vasco Da Gama',             'VSG',  'Vasco Da Gama',    'Goa'),

-- West Bengal (non-Kolkata)
(105,'New Jalpaiguri',            'NJP',  'Siliguri',         'West Bengal'),
(106,'Asansol Junction',          'ASN',  'Asansol',          'West Bengal'),
(107,'Durgapur',                  'DGR',  'Durgapur',         'West Bengal'),
(108,'Kharagpur Junction',        'KGP',  'Kharagpur',        'West Bengal');

SELECT CONCAT('Total stations in DB: ', COUNT(*)) AS Status FROM station;
