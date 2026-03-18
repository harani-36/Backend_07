# Train Service - Complete Explanation

## 📌 Service Overview

**Port**: 8082  
**Purpose**: Train, Journey, Coach, and Seat management  
**Database**: MySQL (train, journey, coach, seat tables)  
**Key Features**: Dynamic seat generation, seat locking, fare integration

---

## 🏗️ Architecture

```
Controller Layer
  ├── TrainController (Train & Seat operations)
  └── JourneyController (Journey operations)
         ↓
Service Layer
  ├── TrainService → TrainServiceImpl
  └── JourneyService → JourneyServiceImpl
         ↓
Repository Layer
  ├── TrainRepository
  ├── JourneyRepository
  ├── CoachRepository
  └── SeatRepository
         ↓
Database (MySQL)
```

---

## 📦 Entity Classes

### 1. Train Entity

```java
@Entity
@Table(name="train")
public class Train {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 20)
    private String trainNumber;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, length = 100)
    private String source;
    
    @Column(nullable = false, length = 100)
    private String destination;
    
    @OneToMany(mappedBy="train", cascade=CascadeType.ALL)
    private List<Journey> journeys;
}
```

**@OneToMany(mappedBy="train")**
- One train has many journeys
- "mappedBy" indicates Journey owns the relationship
- Journey table has train_id foreign key

**cascade=CascadeType.ALL**
- Operations on Train cascade to Journeys
- Delete train → deletes all its journeys
- Save train → saves associated journeys

**unique = true on trainNumber**
- Prevents duplicate train numbers
- Database constraint
- Example: "12345" can only exist once

**Interview Q**: Why separate Train and Journey?  
**Answer**: Same train runs on different dates/times. Train is master data (static), Journey is operational data (dynamic). Allows same train to have multiple schedules.

---

### 2. Journey Entity

```java
@Entity
@Table(name = "journey", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"train_id", "journey_date", "departure_time"})
})
public class Journey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;
    
    @Column(name = "journey_date", nullable = false)
    private LocalDate journeyDate;
    
    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;
    
    @Column(name = "arrival_time", nullable = false)
    private LocalTime arrivalTime;
    
    @OneToMany(mappedBy = "journey", cascade = CascadeType.ALL)
    private List<Coach> coaches;
}
```

**@ManyToOne**
- Many journeys belong to one train
- Creates foreign key train_id

**@JoinColumn(name = "train_id")**
- Specifies foreign key column name
- Links to Train.id

**@UniqueConstraint**
- Composite unique constraint
- Same train cannot have two journeys at same date/time
- Prevents double booking of train

**LocalDate vs LocalTime**
- LocalDate: 2024-01-15 (date only)
- LocalTime: 10:30:00 (time only)
- LocalDateTime: 2024-01-15T10:30:00 (both)

**Interview Q**: Why use LocalDate instead of Date?  
**Answer**: Java 8+ Time API is better:
- Immutable (thread-safe)
- Clear semantics (Date vs Time vs DateTime)
- Better API design
- No timezone issues with LocalDate/LocalTime

---

### 3. Coach Entity

```java
@Entity
@Table(name="coach")
public class Coach {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    private String coachNumber;  // Example: "AC1", "SL2"
    private String coachType;     // Example: "AC", "SL"
    
    @ManyToOne
    @JoinColumn(name="journey_id")
    private Journey journey;
    
    @OneToMany(mappedBy = "coach", cascade = CascadeType.ALL)
    private List<Seat> seats;
}
```

**coachNumber vs coachType**
- coachType: Category (AC, SL, 2A, 3A)
- coachNumber: Unique identifier (AC1, AC2, SL1)

**Relationship**:
```
Journey 1 -----> * Coach
Coach 1 -----> * Seat
```

---

### 4. Seat Entity

```java
@Entity
@Table(name="seat")
public class Seat {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    private String seatNumber;  // Example: "AC1-1", "AC1-2"
    
    @Enumerated(EnumType.STRING)
    private SeatStatus status;
    
    @ManyToOne
    @JoinColumn(name="coach_id")
    private Coach coach;
}
```

**seatNumber Format**: `{coachNumber}-{seatIndex}`
- Example: "AC1-1", "AC1-2", "SL1-1"

---

### 5. SeatStatus Enum

```java
public enum SeatStatus {
    AVAILABLE,
    BOOKED
}
```

**Status Flow**:
```
AVAILABLE → (booking created) → BOOKED
```

**Interview Q**: Why not have LOCKED status?  
**Answer**: Current implementation uses database-level locking (SELECT FOR UPDATE) instead of status-based locking. More reliable for preventing race conditions.

---

## 🎮 Controller Classes

### 1. TrainController

```java
@RestController
@RequestMapping("/trains")
@RequiredArgsConstructor
public class TrainController {
    
    private final TrainService trainService;
    private final SeatRepository seatRepository;
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public TrainResponse addTrain(@Valid @RequestBody TrainRequest request) {
        return trainService.addTrain(request);
    }
    
    @GetMapping
    public List<TrainResponse> getAllTrains() {
        return trainService.getAllTrains();
    }
    
    @GetMapping("/{id}/fare")
    public TrainFareResponse getTrainWithFare(
            @PathVariable Long id,
            @RequestParam String coachType) {
        return trainService.getTrainWithFare(id, coachType);
    }
    
    @PutMapping("/seats/{seatId}/book")
    @Transactional
    public void bookSeat(@PathVariable Long seatId) {
        Seat seat = seatRepository.findSeatForUpdate(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));

        if (seat.getStatus() == SeatStatus.BOOKED) {
            throw new SeatAlreadyBookedException("Seat already booked");
        }
        
        seat.setStatus(SeatStatus.BOOKED);
        seatRepository.save(seat);
    }
}
```

**@PreAuthorize("hasRole('ADMIN')")**
- Only ADMIN can add trains
- Checked after JWT validation
- Uses role from JWT token

**@Transactional on bookSeat()**
- Ensures atomicity
- If exception occurs, changes are rolled back
- Critical for seat booking

**findSeatForUpdate()**
- Custom repository method
- Uses SELECT FOR UPDATE
- Locks row until transaction completes
- Prevents concurrent bookings

**Interview Q**: Why @Transactional on controller method?  
**Answer**: Seat booking logic is in controller (not service). Transaction needed to ensure atomicity. Better practice would be moving to service layer.

---

### 2. JourneyController

```java
@RestController
@RequestMapping("/journeys")
@RequiredArgsConstructor
public class JourneyController {
    
    private final JourneyService journeyService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JourneyResponse> createJourney(
            @Valid @RequestBody JourneyRequest request) {
        JourneyResponse response = journeyService.createJourney(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<JourneyResponse>> getAllJourneys() {
        return ResponseEntity.ok(journeyService.getAllJourneys());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<JourneyResponse> getJourneyById(@PathVariable Long id) {
        return ResponseEntity.ok(journeyService.getJourneyById(id));
    }
    
    @GetMapping("/by-date")
    public ResponseEntity<List<JourneyResponse>> getJourneysByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(journeyService.getJourneysByDate(date));
    }
}
```

**ResponseEntity.status(HttpStatus.CREATED)**
- Returns 201 Created status
- RESTful best practice for resource creation
- Alternative: Just return object (200 OK)

**@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)**
- Parses date from query parameter
- Format: YYYY-MM-DD (2024-01-15)
- Without it: String parameter, manual parsing needed

**Request Example**:
```
GET /journeys/by-date?date=2024-01-15
```

---

## 💼 Service Implementation

### TrainServiceImpl

```java
@Service
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService {

    private final TrainRepository trainRepository;
    private final FareClient fareClient;
    
    @Override
    public TrainResponse addTrain(TrainRequest request) {
        Train train = new Train();
        train.setTrainNumber(request.getTrainNumber());
        train.setName(request.getName());
        train.setSource(request.getSource());
        train.setDestination(request.getDestination());

        Train saved = trainRepository.save(train);

        return new TrainResponse(
                saved.getId(),
                saved.getTrainNumber(),
                saved.getName(),
                saved.getSource(),
                saved.getDestination()
        );
    }

    @Override
    public TrainFareResponse getTrainWithFare(Long trainId, String coachType) {
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new ResourceNotFoundException("Train not found"));
        
        // Call Fare Service via Feign Client
        FareResponse fare = fareClient.getFare(trainId, coachType);
        
        return new TrainFareResponse(
                train.getId(),
                train.getName(),
                train.getSource(),
                train.getDestination(),
                coachType,
                fare.getAmount()
        );
    }
}
```

**FareClient (Feign)**
- Inter-service communication
- Calls Fare Service to get fare amount
- Synchronous HTTP call

**Interview Q**: What if Fare Service is down?  
**Answer**: FeignException thrown, request fails. Solutions:
- Circuit breaker (Resilience4j)
- Fallback method
- Cache fare data locally
- Return error to client

---

### JourneyServiceImpl (Complex Logic)

```java
@Service
@RequiredArgsConstructor
public class JourneyServiceImpl implements JourneyService {
    
    private final JourneyRepository journeyRepository;
    private final TrainRepository trainRepository;
    
    @Override
    @Transactional
    public JourneyResponse createJourney(JourneyRequest request) {
        // 1. Validate train exists
        Train train = trainRepository.findById(request.getTrainId())
                .orElseThrow(() -> new ResourceNotFoundException("Train not found"));
        
        // 2. Check for time conflicts
        List<Journey> existingJourneys = journeyRepository
                .findByTrainIdAndJourneyDate(request.getTrainId(), request.getJourneyDate());
        
        for (Journey existing : existingJourneys) {
            boolean overlaps = !(request.getArrivalTime().isBefore(existing.getDepartureTime()) || 
                                 request.getDepartureTime().isAfter(existing.getArrivalTime()));
            if (overlaps) {
                throw new IllegalArgumentException("Journey time conflict");
            }
        }
        
        // 3. Create journey
        Journey journey = new Journey();
        journey.setTrain(train);
        journey.setJourneyDate(request.getJourneyDate());
        journey.setDepartureTime(request.getDepartureTime());
        journey.setArrivalTime(request.getArrivalTime());
        
        // 4. Generate coaches and seats
        List<Coach> coaches = generateCoaches(journey, request.getCoachConfigs());
        journey.setCoaches(coaches);
        
        // 5. Save (cascades to coaches and seats)
        Journey saved = journeyRepository.save(journey);
        
        return mapToResponse(saved);
    }
    
    private List<Coach> generateCoaches(Journey journey, List<CoachConfigRequest> configs) {
        List<Coach> coaches = new ArrayList<>();
        Map<String, Integer> coachCounters = new HashMap<>();
        
        for (CoachConfigRequest config : configs) {
            String coachType = config.getCoachType();
            int numberOfCoaches = config.getNumberOfCoaches();
            int seatsPerCoach = config.getSeatsPerCoach();
            
            for (int i = 1; i <= numberOfCoaches; i++) {
                int coachNumber = coachCounters.getOrDefault(coachType, 0) + 1;
                coachCounters.put(coachType, coachNumber);
                
                Coach coach = new Coach();
                coach.setCoachNumber(coachType + coachNumber);
                coach.setCoachType(coachType);
                coach.setJourney(journey);
                
                List<Seat> seats = generateSeats(coach, seatsPerCoach);
                coach.setSeats(seats);
                
                coaches.add(coach);
            }
        }
        
        return coaches;
    }
    
    private List<Seat> generateSeats(Coach coach, int seatsPerCoach) {
        List<Seat> seats = new ArrayList<>();
        
        for (int i = 1; i <= seatsPerCoach; i++) {
            Seat seat = new Seat();
            seat.setSeatNumber(coach.getCoachNumber() + "-" + i);
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setCoach(coach);
            seats.add(seat);
        }
        
        return seats;
    }
}
```

**Time Conflict Check Logic**:
```
Existing: 10:00 - 14:00
New:      12:00 - 16:00
Overlaps: YES (12:00 is between 10:00 and 14:00)

Existing: 10:00 - 12:00
New:      13:00 - 15:00
Overlaps: NO (13:00 is after 12:00)
```

**Coach Generation Example**:
```
Request:
- AC: 2 coaches, 50 seats each
- SL: 3 coaches, 72 seats each

Generated:
- AC1 (50 seats: AC1-1 to AC1-50)
- AC2 (50 seats: AC2-1 to AC2-50)
- SL1 (72 seats: SL1-1 to SL1-72)
- SL2 (72 seats: SL2-1 to SL2-72)
- SL3 (72 seats: SL3-1 to SL3-72)
```

**@Transactional**
- All or nothing: Journey, coaches, seats saved together
- If any step fails, entire operation rolls back
- Critical for data consistency

**Interview Q**: Why generate seats dynamically instead of pre-creating?  
**Answer**: Flexibility. Different journeys can have different coach configurations. Same train can have different seat layouts on different dates.

---

## 🔗 Feign Client

### FareClient

```java
@FeignClient(name = "FARE-SERVICE")
public interface FareClient {
    @GetMapping("/fare")
    FareResponse getFare(@RequestParam Long trainId, @RequestParam String coachType);
}
```

**@FeignClient(name = "FARE-SERVICE")**
- Service name registered in Eureka
- Feign resolves to actual host:port
- Load balancing automatic

**How it works**:
```
1. trainService.getTrainWithFare() called
2. fareClient.getFare(trainId, coachType) invoked
3. Feign queries Eureka: "Where is FARE-SERVICE?"
4. Eureka responds: "localhost:8084"
5. Feign makes HTTP call: GET http://localhost:8084/fare?trainId=1&coachType=AC
6. Fare Service returns fare amount
7. Train Service combines train + fare data
```

---

## 📊 Repository Layer

### SeatRepository (Custom Query)

```java
public interface SeatRepository extends JpaRepository<Seat, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.id = :seatId")
    Optional<Seat> findSeatForUpdate(@Param("seatId") Long seatId);
}
```

**@Lock(LockModeType.PESSIMISTIC_WRITE)**
- Database-level row locking
- Generates: SELECT ... FOR UPDATE
- Other transactions wait until lock released
- Prevents double booking

**Without locking**:
```
Transaction 1: Read seat (AVAILABLE)
Transaction 2: Read seat (AVAILABLE)
Transaction 1: Update to BOOKED
Transaction 2: Update to BOOKED
Result: Both think they booked the seat!
```

**With locking**:
```
Transaction 1: Lock and read seat
Transaction 2: Waits...
Transaction 1: Update to BOOKED, commit, release lock
Transaction 2: Read seat (BOOKED), throw exception
Result: Only one booking succeeds
```

**Interview Q**: Pessimistic vs Optimistic locking?  
**Answer**:
- **Pessimistic**: Lock immediately, others wait (used here)
- **Optimistic**: No lock, check version before update
- Pessimistic better for high contention (seat booking)
- Optimistic better for low contention (profile updates)

---

## 🎯 DTOs

### JourneyRequest

```java
public class JourneyRequest {
    private Long trainId;
    private LocalDate journeyDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private List<CoachConfigRequest> coachConfigs;
}
```

### CoachConfigRequest

```java
public class CoachConfigRequest {
    private String coachType;      // "AC", "SL"
    private int numberOfCoaches;   // 2
    private int seatsPerCoach;     // 50
}
```

**Example Request**:
```json
{
  "trainId": 1,
  "journeyDate": "2024-01-15",
  "departureTime": "10:00:00",
  "arrivalTime": "18:00:00",
  "coachConfigs": [
    {
      "coachType": "AC",
      "numberOfCoaches": 2,
      "seatsPerCoach": 50
    },
    {
      "coachType": "SL",
      "numberOfCoaches": 3,
      "seatsPerCoach": 72
    }
  ]
}
```

---

## 🔄 Complete Flow: Create Journey

```
1. Admin sends POST /journeys request

2. JourneyController receives request

3. @PreAuthorize checks if user is ADMIN

4. JourneyServiceImpl.createJourney() called

5. Validate train exists in database

6. Check for time conflicts with existing journeys

7. Create Journey entity

8. Generate coaches:
   - Loop through coach configs
   - Create Coach entities
   - Assign coach numbers (AC1, AC2, SL1, etc.)

9. Generate seats for each coach:
   - Loop seatsPerCoach times
   - Create Seat entities
   - Assign seat numbers (AC1-1, AC1-2, etc.)
   - Set status = AVAILABLE

10. Save journey (cascades to coaches and seats)

11. Map to JourneyResponse DTO

12. Return response with total/available seats
```

---

## 📊 Database Schema

```sql
CREATE TABLE train (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    train_number VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    source VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL
);

CREATE TABLE journey (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    train_id BIGINT NOT NULL,
    journey_date DATE NOT NULL,
    departure_time TIME NOT NULL,
    arrival_time TIME NOT NULL,
    FOREIGN KEY (train_id) REFERENCES train(id),
    UNIQUE (train_id, journey_date, departure_time)
);

CREATE TABLE coach (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    coach_number VARCHAR(20) NOT NULL,
    coach_type VARCHAR(20) NOT NULL,
    journey_id BIGINT NOT NULL,
    FOREIGN KEY (journey_id) REFERENCES journey(id)
);

CREATE TABLE seat (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    seat_number VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    coach_id BIGINT NOT NULL,
    FOREIGN KEY (coach_id) REFERENCES coach(id)
);
```

---

## 🎯 Interview Questions

**Q1: How do you prevent double booking of seats?**

**Answer**: Database-level pessimistic locking using SELECT FOR UPDATE. When booking starts, row is locked. Other transactions wait. Only one transaction can book the seat.

**Q2: Why separate Train and Journey entities?**

**Answer**: Train is master data (static), Journey is operational data (dynamic). Same train runs on different dates with different seat availability. Separation allows flexibility.

**Q3: How are seats generated dynamically?**

**Answer**: When journey is created, admin specifies coach configuration (type, count, seats per coach). Service generates Coach and Seat entities programmatically, assigns numbers, and saves via cascade.

**Q4: What happens if Fare Service is down?**

**Answer**: getTrainWithFare() fails with FeignException. Client receives error. Solutions: circuit breaker, fallback, caching, or graceful degradation.

**Q5: Explain the time conflict check logic.**

**Answer**: Checks if new journey overlaps with existing journeys on same date. Overlap occurs if new journey starts before existing ends AND new journey ends after existing starts. Prevents train from being double-booked.

---

## ✅ Key Takeaways

✅ **Entity Relationships**: Train → Journey → Coach → Seat (cascading)  
✅ **Pessimistic Locking**: Prevents race conditions in seat booking  
✅ **Dynamic Generation**: Seats created based on configuration  
✅ **Time Conflict Prevention**: Validates journey schedules  
✅ **Feign Client**: Inter-service communication with Fare Service  
✅ **Transaction Management**: Ensures data consistency  

---
