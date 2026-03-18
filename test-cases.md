# RailNova Railway Booking System - Test Cases

## 🧪 **Comprehensive Test Cases for Search & Booking**

### **Test Environment Setup:**
- **Current Date**: March 17, 2026
- **Available Journey Dates**: March 17, 2026 → April 3, 2026
- **Total Stations**: 108 stations
- **Total Trains**: 55 trains
- **Total Journeys**: 131 journeys

---

## 🚂 **1. PREMIUM TRAIN TEST CASES (Rajdhani Express)**

### **Test Case 1.1: Delhi to Kolkata - Rajdhani Express**
```json
{
  "source": "New Delhi Railway Station",
  "destination": "Howrah Junction", 
  "journey_date": "2026-03-17",
  "expected_trains": ["12301 - Rajdhani Express"],
  "available_coaches": ["AC_1_TIER", "AC_2_TIER", "AC_3_TIER"],
  "departure_time": "16:55",
  "arrival_time": "10:05",
  "test_coach": "AC_2_TIER",
  "expected_fare_range": "₹3,200"
}
```

### **Test Case 1.2: Kolkata to Delhi - Return Journey**
```json
{
  "source": "Howrah Junction",
  "destination": "New Delhi Railway Station",
  "journey_date": "2026-03-18",
  "expected_trains": ["12302 - Rajdhani Express"],
  "available_coaches": ["AC_1_TIER", "AC_2_TIER", "AC_3_TIER"],
  "departure_time": "16:50",
  "arrival_time": "09:55",
  "test_coach": "AC_1_TIER",
  "expected_fare_range": "₹4,500"
}
```

### **Test Case 1.3: Delhi to Mumbai - Mumbai Rajdhani**
```json
{
  "source": "New Delhi Railway Station",
  "destination": "Mumbai Central",
  "journey_date": "2026-03-19",
  "expected_trains": ["12951 - Mumbai Rajdhani"],
  "available_coaches": ["AC_1_TIER", "AC_2_TIER", "AC_3_TIER"],
  "departure_time": "16:00",
  "arrival_time": "08:35",
  "test_coach": "AC_3_TIER",
  "expected_fare_range": "₹2,200"
}
```

---

## 🚄 **2. SHATABDI EXPRESS TEST CASES (Day Trains)**

### **Test Case 2.1: Delhi to Amritsar - Shatabdi**
```json
{
  "source": "New Delhi Railway Station",
  "destination": "Amritsar Junction",
  "journey_date": "2026-03-17",
  "expected_trains": ["12009 - Shatabdi Express"],
  "available_coaches": ["EXECUTIVE", "AC_CHAIR"],
  "departure_time": "06:20",
  "arrival_time": "12:20",
  "test_coach": "EXECUTIVE",
  "expected_fare_range": "₹2,800"
}
```

### **Test Case 2.2: Delhi to Bhopal - Bhopal Shatabdi**
```json
{
  "source": "New Delhi Railway Station",
  "destination": "Bhopal Junction",
  "journey_date": "2026-03-18",
  "expected_trains": ["12019 - Shatabdi Express"],
  "available_coaches": ["EXECUTIVE", "AC_CHAIR"],
  "departure_time": "06:00",
  "arrival_time": "14:05",
  "test_coach": "AC_CHAIR",
  "expected_fare_range": "₹1,200"
}
```

---

## 🚅 **3. LONG DISTANCE EXPRESS TEST CASES**

### **Test Case 3.1: Delhi to Chennai - Tamil Nadu Express**
```json
{
  "source": "New Delhi Railway Station",
  "destination": "Chennai Central",
  "journey_date": "2026-03-17",
  "expected_trains": ["12621 - Tamil Nadu Express"],
  "available_coaches": ["AC_1_TIER", "AC_2_TIER", "AC_3_TIER", "SLEEPER"],
  "departure_time": "22:30",
  "arrival_time": "06:15",
  "test_coach": "SLEEPER",
  "expected_fare_range": "₹1,200"
}
```

### **Test Case 3.2: Delhi to Bangalore - Karnataka Express**
```json
{
  "source": "New Delhi Railway Station",
  "destination": "Bangalore City Junction",
  "journey_date": "2026-03-17",
  "expected_trains": ["12649 - Karnataka Express"],
  "available_coaches": ["AC_2_TIER", "AC_3_TIER", "SLEEPER"],
  "departure_time": "20:15",
  "arrival_time": "04:30",
  "test_coach": "AC_3_TIER",
  "expected_fare_range": "₹2,900"
}
```

### **Test Case 3.3: Delhi to Hyderabad - Telangana Express**
```json
{
  "source": "New Delhi Railway Station",
  "destination": "Secunderabad Junction",
  "journey_date": "2026-03-18",
  "expected_trains": ["12723 - Telangana Express"],
  "available_coaches": ["AC_1_TIER", "AC_2_TIER", "AC_3_TIER", "SLEEPER"],
  "departure_time": "17:40",
  "arrival_time": "09:55",
  "test_coach": "AC_2_TIER",
  "expected_fare_range": "₹3,300"
}
```

---

## 💰 **4. BUDGET TRAIN TEST CASES**

### **Test Case 4.1: Delhi to Bangalore - Garib Rath**
```json
{
  "source": "New Delhi Railway Station",
  "destination": "Bangalore City Junction",
  "journey_date": "2026-03-17",
  "expected_trains": ["12429 - Garib Rath"],
  "available_coaches": ["AC_3_TIER"],
  "departure_time": "12:50",
  "arrival_time": "16:30",
  "test_coach": "AC_3_TIER",
  "expected_fare_range": "₹2,600"
}
```

### **Test Case 4.2: Delhi to Chandigarh - Jan Shatabdi**
```json
{
  "source": "New Delhi Railway Station",
  "destination": "Chandigarh",
  "journey_date": "2026-03-17",
  "expected_trains": ["12023 - Jan Shatabdi"],
  "available_coaches": ["AC_CHAIR", "CC"],
  "departure_time": "07:40",
  "arrival_time": "11:35",
  "test_coach": "CC",
  "expected_fare_range": "₹600"
}
```

### **Test Case 4.3: Delhi to Jaipur - Jan Shatabdi**
```json
{
  "source": "New Delhi Railway Station",
  "destination": "Jaipur Junction",
  "journey_date": "2026-03-19",
  "expected_trains": ["12055 - Jan Shatabdi"],
  "available_coaches": ["AC_CHAIR", "CC"],
  "departure_time": "06:05",
  "arrival_time": "10:30",
  "test_coach": "AC_CHAIR",
  "expected_fare_range": "₹900"
}
```

---

## 🌊 **5. COASTAL & SOUTHERN ROUTES TEST CASES**

### **Test Case 5.1: Delhi to Kerala - Trivandrum Rajdhani**
```json
{
  "source": "New Delhi Railway Station",
  "destination": "Thiruvananthapuram Central",
  "journey_date": "2026-03-18",
  "expected_trains": ["12431 - Trivandrum Rajdhani"],
  "available_coaches": ["AC_1_TIER", "AC_2_TIER", "AC_3_TIER"],
  "departure_time": "11:00",
  "arrival_time": "11:10",
  "test_coach": "AC_1_TIER",
  "expected_fare_range": "₹6,200"
}
```

### **Test Case 5.2: Delhi to Goa - Goa Express**
```json
{
  "source": "Hazrat Nizamuddin",
  "destination": "Madgaon Junction",
  "journey_date": "2026-03-17",
  "expected_trains": ["12779 - Goa Express"],
  "available_coaches": ["AC_1_TIER", "AC_2_TIER", "AC_3_TIER", "SLEEPER"],
  "departure_time": "15:00",
  "arrival_time": "12:00",
  "test_coach": "SLEEPER",
  "expected_fare_range": "₹850"
}
```

### **Test Case 5.3: Kerala to Delhi - Mangala Express**
```json
{
  "source": "Ernakulam Junction",
  "destination": "Hazrat Nizamuddin",
  "journey_date": "2026-03-17",
  "expected_trains": ["12618 - Mangala Express"],
  "available_coaches": ["AC_1_TIER", "AC_2_TIER", "AC_3_TIER", "SLEEPER"],
  "departure_time": "Not specified",
  "arrival_time": "Not specified",
  "test_coach": "AC_2_TIER",
  "expected_fare_range": "₹4,200"
}
```

---

## 🔍 **6. EDGE CASE TEST SCENARIOS**

### **Test Case 6.1: No Trains Available**
```json
{
  "source": "New Delhi Railway Station",
  "destination": "Dibrugarh Town",
  "journey_date": "2026-03-17",
  "expected_result": "No trains available for the selected route",
  "expected_trains": [],
  "test_purpose": "Test no results scenario"
}
```

### **Test Case 6.2: Same Source and Destination**
```json
{
  "source": "New Delhi Railway Station",
  "destination": "New Delhi Railway Station",
  "journey_date": "2026-03-17",
  "expected_result": "Source and destination cannot be the same",
  "expected_trains": [],
  "test_purpose": "Test validation error"
}
```

### **Test Case 6.3: Past Date**
```json
{
  "source": "New Delhi Railway Station",
  "destination": "Mumbai Central",
  "journey_date": "2026-03-16",
  "expected_result": "Cannot book for past dates",
  "expected_trains": [],
  "test_purpose": "Test date validation"
}
```

### **Test Case 6.4: Future Date (No Journeys)**
```json
{
  "source": "New Delhi Railway Station",
  "destination": "Mumbai Central",
  "journey_date": "2026-05-01",
  "expected_result": "No trains available for the selected route",
  "expected_trains": [],
  "test_purpose": "Test far future date"
}
```

---

## 🎯 **7. SPECIFIC COACH TYPE TESTING**

### **Test Case 7.1: AC_1_TIER Availability**
```json
{
  "search_params": {
    "source": "New Delhi Railway Station",
    "destination": "Howrah Junction",
    "journey_date": "2026-03-17"
  },
  "coach_selection": "AC_1_TIER",
  "expected_seats": 36,
  "expected_layout": "1A, 1B, 2A, 2B... 18A, 18B",
  "expected_available": "~30 seats (85% availability)"
}
```

### **Test Case 7.2: SLEEPER Class Testing**
```json
{
  "search_params": {
    "source": "New Delhi Railway Station",
    "destination": "Chennai Central",
    "journey_date": "2026-03-18"
  },
  "coach_selection": "SLEEPER",
  "expected_seats": 72,
  "expected_layout": "1A, 1B, 1C... 24A, 24B, 24C",
  "expected_available": "~60 seats (85% availability)"
}
```

### **Test Case 7.3: EXECUTIVE Class Testing**
```json
{
  "search_params": {
    "source": "New Delhi Railway Station",
    "destination": "Amritsar Junction",
    "journey_date": "2026-03-17"
  },
  "coach_selection": "EXECUTIVE",
  "expected_seats": 56,
  "expected_layout": "1A, 1B, 2A, 2B... 28A, 28B",
  "expected_available": "~48 seats (85% availability)"
}
```

---

## 🚀 **8. API ENDPOINT TESTING**

### **Frontend API Calls to Test:**

#### **8.1: Station Search**
```javascript
// Test: GET /search/stations
// Expected: 108 stations including all train route stations
```

#### **8.2: Train Search**
```javascript
// Test: GET /search?source=New Delhi Railway Station&destination=Mumbai Central&date=2026-03-17
// Expected: Mumbai Rajdhani train details
```

#### **8.3: Fare Lookup**
```javascript
// Test: GET /fare-service/fare?trainId=8&coachType=AC_2_TIER
// Expected: ₹3,000
```

#### **8.4: Train Details with Coaches**
```javascript
// Test: GET /train-service/trains/8/fare?coachType=AC_1_TIER
// Expected: Train details with fare information
```

---

## ✅ **9. COMPLETE USER JOURNEY TEST**

### **Test Case 9.1: End-to-End Booking Flow**
```json
{
  "step_1_search": {
    "source": "New Delhi Railway Station",
    "destination": "Mumbai Central",
    "journey_date": "2026-03-17"
  },
  "step_2_train_selection": {
    "selected_train": "12951 - Mumbai Rajdhani",
    "departure": "16:00",
    "arrival": "08:35"
  },
  "step_3_coach_selection": {
    "available_coaches": ["AC_1_TIER", "AC_2_TIER", "AC_3_TIER"],
    "selected_coach": "AC_2_TIER",
    "fare": "₹3,000"
  },
  "step_4_seat_selection": {
    "coach_number": "A1",
    "available_seats": ["1A", "1B", "2A", "3B", "4A", "..."],
    "selected_seat": "5A"
  },
  "step_5_passenger_details": {
    "name": "Test User",
    "age": 30,
    "gender": "Male"
  },
  "step_6_payment": {
    "amount": "₹3,000",
    "payment_method": "Razorpay"
  },
  "expected_result": "Booking confirmed with PNR"
}
```

---

## 📋 **10. QUICK TEST CHECKLIST**

### **✅ Basic Functionality:**
- [ ] Station dropdown loads all 108 stations
- [ ] Search returns trains for valid routes
- [ ] No results message for invalid routes
- [ ] Date validation works
- [ ] Coach types display correctly
- [ ] Fare calculation is accurate

### **✅ Premium Routes (High Priority):**
- [ ] Delhi → Mumbai (Rajdhani)
- [ ] Delhi → Chennai (Tamil Nadu Express)
- [ ] Delhi → Bangalore (Karnataka Express)
- [ ] Delhi → Kolkata (Rajdhani)

### **✅ Coach Types:**
- [ ] AC_1_TIER (36 seats)
- [ ] AC_2_TIER (48 seats)
- [ ] AC_3_TIER (72 seats)
- [ ] SLEEPER (72 seats)
- [ ] AC_CHAIR (78 seats)
- [ ] EXECUTIVE (56 seats)

### **✅ Date Range:**
- [ ] Today: March 17, 2026
- [ ] Tomorrow: March 18, 2026
- [ ] Next week: March 24, 2026
- [ ] Next month: April 1, 2026

---

## 🎯 **RECOMMENDED TESTING ORDER:**

1. **Start with Test Case 1.1** (Delhi to Kolkata Rajdhani)
2. **Test Case 2.1** (Delhi to Amritsar Shatabdi)
3. **Test Case 3.1** (Delhi to Chennai Tamil Nadu Express)
4. **Test Case 4.2** (Delhi to Chandigarh Jan Shatabdi)
5. **Test Case 6.1** (No trains scenario)
6. **Complete Test Case 9.1** (End-to-end booking)

This covers all major train types, coach classes, and user scenarios! 🚂✨