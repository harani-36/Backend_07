# RailNova - Interview Preparation Guide

## 🎯 Complete Q&A for Interview Success

---

## 📋 Table of Contents

1. [Architecture & Design Questions](#architecture--design-questions)
2. [Microservices Concepts](#microservices-concepts)
3. [Security & Authentication](#security--authentication)
4. [Database & Transactions](#database--transactions)
5. [Inter-Service Communication](#inter-service-communication)
6. [Payment Integration](#payment-integration)
7. [Concurrency & Race Conditions](#concurrency--race-conditions)
8. [Spring Boot Specific](#spring-boot-specific)
9. [Scenario-Based Questions](#scenario-based-questions)
10. [Code Walkthrough Questions](#code-walkthrough-questions)

---

## Architecture & Design Questions

### Q1: Explain the overall architecture of RailNova.

**Answer**:
RailNova follows a microservices architecture with 9 services:

1. **Eureka Server** (8761): Service discovery registry
2. **API Gateway** (8080): Single entry point, JWT validation, routing
3. **Auth Service** (8081): User authentication, JWT generation
4. **Train Service** (8082): Train, journey, coach, seat management
5. **Booking Service** (8083): Booking orchestration
6. **Fare Service** (8084): Fare management
7. **Search Service** (8085): Train search functionality
8. **Payment Service** (8086): Razorpay integration, payment verification
9. **Notification Service** (8087): Event-driven notifications via RabbitMQ

**Communication**:
- Synchronous: Feign Client (REST)
- Asynchronous: RabbitMQ (events)

**Security**: JWT-based stateless authentication

---

### Q2: Why did you choose microservices over monolithic architecture?

**Answer**:

**Advantages**:
1. **Independent Deployment**: Can deploy Payment Service without affecting Train Service
2. **Scalability**: Scale high-traffic services (Search) independently
3. **Technology Flexibility**: Each service can use different tech stack
4. **Fault Isolation**: Failure in Notification Service doesn't crash booking
5. **Team Autonomy**: Different teams can own different services
6. **Easier Maintenance**: Smaller codebases are easier to understand

**Disadvantages** (be honest):
1. **Complexity**: More moving parts, harder to debug
2. **Network Latency**: Inter-service calls add overhead
3. **Data Consistency**: No ACID transactions across services
4. **Deployment Overhead**: Need to manage multiple deployments

**When to use**:
- Large applications with multiple teams
- Need for independent scaling
- Different parts have different requirements

---

### Q3: What design patterns did you use in this project?

**Answer**:

1. **API Gateway Pattern**: Single entry point for all requests
2. **Service Discovery Pattern**: Eureka for dynamic service location
3. **Circuit Breaker Pattern**: Implicit with Feign (can add Resilience4j)
4. **Repository Pattern**: JpaRepository for data access
5. **DTO Pattern**: Separate request/response objects from entities
6. **Service Layer Pattern**: Business logic separated from controllers
7. **Event-Driven Pattern**: RabbitMQ for asynchronous communication
8. **Saga Pattern**: Distributed transaction management (booking flow)

---

### Q4: How do you handle distributed transactions?

**Answer**:

We use the **Saga Pattern** (choreography-based):

**Example: Booking Flow**
```
1. Booking Service: Lock seat → Create booking (PENDING)
2. Payment Service: Create order → Verify payment
3. If payment SUCCESS:
   - Confirm booking
   - Book seat permanently
   - Send notification
4. If payment FAILED:
   - Cancel booking
   - Unlock seat
```

**Key Points**:
- No 2-Phase Commit (2PC)
- Each service has local transactions
- Compensating transactions for rollback
- Eventual consistency (not strong consistency)

**Alternative**: Orchestration-based Saga with a coordinator service

---

## Microservices Concepts

### Q5: What is Service Discovery and how does Eureka work?

**Answer**:

**Service Discovery**: Mechanism for services to find each other dynamically.

**Eureka Workflow**:
```
1. Service Startup:
   Train Service → Registers with Eureka
   Name: TRAIN-SERVICE
   Host: localhost
   Port: 8082

2. Service Discovery:
   Booking Service needs Train Service
   → Queries Eureka: "Where is TRAIN-SERVICE?"
   → Eureka responds: "localhost:8082"
   → Booking Service makes HTTP call

3. Health Checks:
   Services send heartbeat every 30 seconds
   No heartbeat for 90 seconds → Service evicted
```

**Benefits**:
- No hardcoded URLs
- Dynamic scaling (multiple instances)
- Automatic failover
- Load balancing

---

### Q6: What is the role of API Gateway?

**Answer**:

**Responsibilities**:
1. **Single Entry Point**: All client requests go through gateway
2. **Authentication**: Validates JWT tokens centrally
3. **Routing**: Routes requests to appropriate microservices
4. **Load Balancing**: Distributes load across service instances
5. **Cross-Cutting Concerns**: Logging, monitoring, rate limiting

**Request Flow**:
```
Client → API Gateway (JWT validation) → Microservice
```

**Without Gateway**:
- Clients need to know all service URLs
- Each service must validate JWT
- CORS configuration in every service
- No centralized logging/monitoring

---

### Q7: What is Feign Client and why use it?

**Answer**:

**Feign**: Declarative HTTP client for inter-service communication.

**Example**:
```java
@FeignClient(name = "TRAIN-SERVICE")
public interface TrainClient {
    @GetMapping("/trains/{id}")
    TrainResponse getTrain(@PathVariable Long id);
}
```

**Benefits**:
1. **Declarative**: No boilerplate HTTP code
2. **Service Discovery**: Integrates with Eureka
3. **Load Balancing**: Automatic via Spring Cloud
4. **Error Handling**: Built-in exception handling
5. **Interceptors**: Can add headers (JWT propagation)

**Alternative**: RestTemplate (more verbose, manual configuration)

---

## Security & Authentication

### Q8: How does JWT authentication work in your project?

**Answer**:

**JWT Structure**: `Header.Payload.Signature`

**Flow**:
```
1. User Login:
   POST /auth/login
   { "email": "user@example.com", "password": "pass123" }

2. Auth Service:
   - Validates credentials
   - Generates JWT token
   - Signs with HMAC-SHA256

3. Token Structure:
   {
     "sub": "user@example.com",
     "role": "USER",
     "iat": 1234567890,
     "exp": 1234654290
   }

4. Client Stores Token:
   localStorage or cookie

5. Subsequent Requests:
   Authorization: Bearer <token>

6. API Gateway:
   - Extracts token
   - Verifies signature
   - Checks expiration
   - Forwards request if valid

7. Downstream Services:
   - Receive token in header
   - Can extract user info
   - Perform authorization (@PreAuthorize)
```

**Security Features**:
- Stateless (no session storage)
- Tamper-proof (signature verification)
- Expiration (time-limited validity)
- Role-based access control

---

### Q9: Why use BCrypt for password hashing?

**Answer**:

**BCrypt Advantages**:
1. **Slow by Design**: Prevents brute force attacks
2. **Automatic Salt**: No need to manage salt separately
3. **Adaptive**: Can increase rounds as hardware improves
4. **One-Way**: Cannot decrypt (only verify)

**Example**:
```java
// Registration
String hashed = passwordEncoder.encode("password123");
// Result: $2a$10$N9qo8uLOickgx2ZMRZoMye...

// Login
boolean matches = passwordEncoder.matches("password123", hashed);
// Returns: true
```

**Why not MD5/SHA?**
- MD5/SHA are fast (bad for passwords)
- No built-in salt
- Vulnerable to rainbow tables
- Designed for data integrity, not passwords

---

### Q10: Explain @PreAuthorize annotation.

**Answer**:

**Purpose**: Method-level authorization based on roles.

**Examples**:
```java
@PreAuthorize("hasRole('ADMIN')")
public TrainResponse addTrain(TrainRequest request) {
    // Only ADMIN can access
}

@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public Payment createPayment(PaymentRequest request) {
    // Both USER and ADMIN can access
}
```

**How it works**:
1. JWT token contains role claim
2. Spring Security extracts role
3. Evaluates SpEL expression
4. Allows/denies access

**Without @PreAuthorize**:
- Manual role checking in code
- Inconsistent authorization
- Harder to maintain

---

## Database & Transactions

### Q11: Explain the database schema and relationships.

**Answer**:

**Train Service**:
```
Train (1) -----> (*) Journey
Journey (1) -----> (*) Coach
Coach (1) -----> (*) Seat
```

**Relationships**:
- One train has many journeys (same train, different dates)
- One journey has many coaches (AC, SL, etc.)
- One coach has many seats (dynamically generated)

**Other Services**:
- Auth Service: User table (id, email, password, role)
- Booking Service: Booking table (stores IDs, not relationships)
- Payment Service: Payment table (linked to booking)
- Fare Service: Fare table (trainId + coachType → amount)

**Why store IDs in Booking instead of relationships?**
- Microservices principle: Loose coupling
- Train data in different service/database
- Avoids distributed joins
- Simpler data model

---

### Q12: How do you prevent double booking of seats?

**Answer**:

**Problem**: Two users book same seat simultaneously.

**Solution**: Pessimistic Locking (SELECT FOR UPDATE)

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT s FROM Seat s WHERE s.id = :seatId")
Optional<Seat> findSeatForUpdate(@Param("seatId") Long seatId);
```

**How it works**:
```
Transaction 1:
1. SELECT * FROM seat WHERE id = 1 FOR UPDATE
2. Row locked
3. Check status (AVAILABLE)
4. Update status to BOOKED
5. Commit (lock released)

Transaction 2:
1. SELECT * FROM seat WHERE id = 1 FOR UPDATE
2. Waits for lock...
3. Lock released
4. Read seat (status = BOOKED)
5. Throw exception (seat already booked)
```

**Alternative**: Optimistic Locking (version field)
- Better for low contention
- Pessimistic better for high contention (seat booking)

---

### Q13: What is @Transactional and why use it?

**Answer**:

**Purpose**: Ensures atomicity of database operations.

**Example**:
```java
@Transactional
public void confirmBooking(Long bookingId, Long paymentId) {
    // Step 1: Update booking status
    booking.setBookingStatus(BookingStatus.CONFIRMED);
    bookingRepository.save(booking);
    
    // Step 2: Book seat permanently
    trainClient.bookSeat(booking.getSeatId());
    
    // If Step 2 fails, Step 1 is rolled back
}
```

**ACID Properties**:
- **Atomicity**: All or nothing
- **Consistency**: Database remains valid
- **Isolation**: Concurrent transactions don't interfere
- **Durability**: Committed changes persist

**Without @Transactional**:
- Partial updates possible
- Data inconsistency
- Manual rollback needed

---

## Inter-Service Communication

### Q14: How do services communicate with each other?

**Answer**:

**Two Approaches**:

**1. Synchronous (Feign Client)**:
```java
@FeignClient(name = "TRAIN-SERVICE")
public interface TrainClient {
    @PutMapping("/trains/seats/{seatId}/lock")
    void lockSeat(@PathVariable Long seatId);
}
```

**Use Cases**:
- Request-response pattern
- Need immediate result
- Example: Lock seat, get fare

**2. Asynchronous (RabbitMQ)**:
```java
// Publisher (Payment Service)
rabbitTemplate.convertAndSend(exchange, routingKey, message);

// Consumer (Notification Service)
@RabbitListener(queues = "${rabbitmq.queue}")
public void handleEvent(String message) {
    // Process event
}
```

**Use Cases**:
- Fire-and-forget
- Don't need immediate response
- Example: Send notification

---

### Q15: How do you propagate JWT token across services?

**Answer**:

**Problem**: Feign calls don't include Authorization header by default.

**Solution**: Feign Request Interceptor

```java
@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");
                
                if (authHeader != null) {
                    requestTemplate.header("Authorization", authHeader);
                }
            }
        };
    }
}
```

**Flow**:
```
1. User request with JWT token
2. Booking Service receives request
3. Booking Service calls Train Service via Feign
4. Interceptor extracts Authorization header
5. Adds header to Feign request
6. Train Service receives request with JWT
```

---

## Payment Integration

### Q16: How does Razorpay payment integration work?

**Answer**:

**Flow**:

**Step 1: Create Order**
```java
JSONObject orderRequest = new JSONObject();
orderRequest.put("amount", Math.round(amount * 100)); // Paise
orderRequest.put("currency", "INR");
orderRequest.put("receipt", "booking_123");

Order order = razorpayClient.orders.create(orderRequest);
String orderId = order.get("id");
```

**Step 2: User Pays**
- Frontend integrates Razorpay Checkout
- User completes payment
- Razorpay returns: paymentId, orderId, signature

**Step 3: Verify Signature**
```java
String payload = orderId + "|" + paymentId;
String signature = HMAC-SHA256(payload, secret);

if (signature.equals(razorpaySignature)) {
    // Payment genuine
} else {
    // Payment tampered
}
```

**Why verify signature?**
- Prevents fake payment success
- Only Razorpay knows the secret
- Ensures payment authenticity

---

### Q17: Why convert amount to paise?

**Answer**:

**Razorpay Requirement**: Amount in smallest currency unit.

**Example**:
```
₹15.00 = 1500 paise
₹1500.50 = 150050 paise
```

**Code**:
```java
Math.round(amount * 100)
```

**Why Math.round()?**
- Prevents floating-point precision issues
- 15.00 * 100 might be 1499.9999999
- Math.round() ensures 1500

---

## Concurrency & Race Conditions

### Q18: What race conditions exist and how do you handle them?

**Answer**:

**Race Condition 1: Double Booking**

**Problem**:
```
User A: Read seat (AVAILABLE)
User B: Read seat (AVAILABLE)
User A: Book seat
User B: Book seat
Result: Both think they booked!
```

**Solution**: Pessimistic locking (SELECT FOR UPDATE)

---

**Race Condition 2: Duplicate Payment**

**Problem**:
```
User clicks "Pay" twice
Two payment orders created
User charged twice
```

**Solution**: Idempotency check
```java
Optional<Payment> existing = paymentRepository.findByBookingId(bookingId);
if (existing.isPresent() && existing.get().getStatus() == SUCCESS) {
    throw new RuntimeException("Payment already completed");
}
```

---

**Race Condition 3: Journey Time Conflict**

**Problem**:
```
Admin creates Journey 1: 10:00-14:00
Admin creates Journey 2: 12:00-16:00
Same train, same date, overlapping times
```

**Solution**: Time conflict validation
```java
for (Journey existing : existingJourneys) {
    boolean overlaps = !(newArrival.isBefore(existing.getDeparture()) || 
                         newDeparture.isAfter(existing.getArrival()));
    if (overlaps) {
        throw new IllegalArgumentException("Time conflict");
    }
}
```

---

## Spring Boot Specific

### Q19: Explain key Spring Boot annotations.

**Answer**:

**@SpringBootApplication**:
- Combines @Configuration, @EnableAutoConfiguration, @ComponentScan
- Entry point of application

**@RestController**:
- Combines @Controller + @ResponseBody
- Returns JSON automatically

**@Service**:
- Marks service layer component
- Spring manages as singleton bean

**@Repository**:
- Marks data access layer
- Enables exception translation

**@Component**:
- Generic stereotype annotation
- Spring-managed bean

**@Autowired** (avoid, use constructor injection):
- Injects dependencies
- Better: Use @RequiredArgsConstructor (Lombok)

**@Transactional**:
- Manages database transactions
- Rollback on exception

**@PreAuthorize**:
- Method-level authorization
- Role-based access control

---

### Q20: What is the difference between @Component, @Service, and @Repository?

**Answer**:

**Technically**: All are stereotypes, functionally same.

**Semantically**:
- **@Component**: Generic component
- **@Service**: Business logic layer
- **@Repository**: Data access layer (adds exception translation)
- **@Controller**: Web layer

**Best Practice**: Use specific annotations for clarity.

**Example**:
```java
@Service
public class BookingServiceImpl { }

@Repository
public interface BookingRepository extends JpaRepository { }

@RestController
public class BookingController { }
```

---

## Scenario-Based Questions

### Q21: What happens if Eureka Server goes down?

**Answer**:

**Immediate Impact**:
- New services cannot register
- Existing services cannot discover new instances

**Mitigation**:
- Services cache registry locally
- Existing communication continues
- Cached data used for service discovery

**Long-Term Solution**:
- Run multiple Eureka instances (cluster)
- High availability setup
- Health checks and monitoring

---

### Q22: What if Payment Service is down during booking?

**Answer**:

**Current Behavior**:
- Booking Service calls Payment Service via Feign
- FeignException thrown
- Booking creation fails
- User sees error

**Better Solutions**:

**1. Circuit Breaker (Resilience4j)**:
```java
@CircuitBreaker(name = "payment-service", fallbackMethod = "paymentFallback")
public PaymentOrderResponse createOrder(PaymentOrderRequest request) {
    return paymentClient.createOrder(request);
}

public PaymentOrderResponse paymentFallback(Exception e) {
    // Return cached response or error message
}
```

**2. Retry Logic**:
```java
@Retry(name = "payment-service", maxAttempts = 3)
```

**3. Async Processing**:
- Queue booking request
- Process when Payment Service is back

---

### Q23: How do you handle partial failures in distributed systems?

**Answer**:

**Example Scenario**:
```
1. Booking created (SUCCESS)
2. Seat locked (SUCCESS)
3. Payment order created (SUCCESS)
4. Payment verification (SUCCESS)
5. Confirm booking (FAILED) ← Network issue
```

**Current Solution**: Rollback in Payment Service
```java
try {
    bookingClient.confirmBooking(bookingId, paymentId);
} catch (Exception e) {
    // Rollback payment status
    payment.setPaymentStatus(PaymentStatus.PENDING);
    paymentRepository.save(payment);
    throw new RuntimeException("Failed to confirm booking");
}
```

**Better Solutions**:
1. **Retry with Exponential Backoff**
2. **Idempotent Operations** (safe to retry)
3. **Saga Pattern** with compensating transactions
4. **Event Sourcing** (replay events)

---

### Q24: How would you scale this application?

**Answer**:

**Horizontal Scaling**:
1. **Multiple Instances**: Run multiple instances of each service
2. **Load Balancing**: Eureka + Spring Cloud LoadBalancer
3. **Database Replication**: Master-slave setup
4. **Caching**: Redis for frequently accessed data

**Vertical Scaling**:
- Increase CPU/RAM of servers

**Specific Optimizations**:

**1. Search Service**:
- Cache train data (Redis)
- Elasticsearch for advanced search
- CDN for static data

**2. Booking Service**:
- Queue-based processing
- Async booking confirmation
- Database sharding by region

**3. Payment Service**:
- Separate read/write databases
- Event sourcing for audit trail

**4. Notification Service**:
- Multiple consumers for RabbitMQ
- Batch notifications
- Third-party email service (SendGrid)

---

## Code Walkthrough Questions

### Q25: Walk me through the complete booking flow.

**Answer**:

```
1. User Login:
   POST /auth/login
   → Auth Service validates credentials
   → Returns JWT token

2. Search Trains:
   GET /search?source=Mumbai&destination=Delhi
   → Search Service calls Train Service
   → Filters trains by source/destination
   → Returns matching trains

3. Get Fare:
   GET /trains/1/fare?coachType=AC
   → Train Service calls Fare Service
   → Returns train details + fare

4. Create Booking:
   POST /booking
   {
     "userId": 1,
     "journeyId": 1,
     "trainId": 1,
     "seatId": 1,
     "passengerName": "John Doe",
     "coachType": "AC",
     "fare": 1500.00
   }
   
   → Booking Service:
     a. Lock seat (Train Service via Feign)
     b. Create booking (status = PENDING)
     c. Create Razorpay order (Payment Service)
     d. Return booking + order details

5. User Pays:
   → Frontend integrates Razorpay Checkout
   → User completes payment
   → Razorpay returns paymentId, orderId, signature

6. Verify Payment:
   POST /payments/verify
   {
     "bookingId": 1,
     "razorpayOrderId": "order_xxx",
     "razorpayPaymentId": "pay_xxx",
     "razorpaySignature": "signature_xxx"
   }
   
   → Payment Service:
     a. Find payment by orderId
     b. Verify signature (HMAC-SHA256)
     c. If valid:
        - Update payment status (SUCCESS)
        - Confirm booking (Booking Service)
        - Book seat permanently (Train Service)
        - Publish event to RabbitMQ
     d. If invalid:
        - Update payment status (FAILED)
        - Cancel booking (Booking Service)
        - Unlock seat (Train Service)

7. Send Notification:
   → Notification Service listens to RabbitMQ
   → Receives "BOOKING_PAYMENT_SUCCESS:1"
   → Sends email notification
```

---

### Q26: Explain how JWT validation works in API Gateway.

**Answer**:

```java
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    // 1. Extract path
    String path = exchange.getRequest().getURI().getPath();
    
    // 2. Allow public endpoints
    if (path.contains("/auth/register") || path.contains("/auth/login")) {
        return chain.filter(exchange);
    }
    
    // 3. Extract Authorization header
    String authHeader = exchange.getRequest()
            .getHeaders()
            .getFirst("Authorization");
    
    // 4. Validate header format
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
    
    // 5. Extract token
    String token = authHeader.substring(7);
    
    // 6. Validate token
    if (!jwtUtil.validateToken(token)) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
    
    // 7. Forward request
    return chain.filter(exchange);
}
```

**validateToken() method**:
```java
public boolean validateToken(String token) {
    try {
        Jwts.parserBuilder()
            .setSigningKey(getSignKey())
            .build()
            .parseClaimsJws(token);
        return true;
    } catch (Exception e) {
        return false;
    }
}
```

**What it checks**:
- Signature validity (HMAC-SHA256)
- Expiration time
- Token format

---

## 🎯 Final Tips for Interview

### Do's:
✅ Explain the "why" behind design decisions  
✅ Mention trade-offs and alternatives  
✅ Be honest about limitations  
✅ Use diagrams to explain flows  
✅ Relate to real-world scenarios  
✅ Show enthusiasm about the project  

### Don'ts:
❌ Memorize code without understanding  
❌ Claim perfection (no system is perfect)  
❌ Ignore interviewer's questions  
❌ Use buzzwords without explanation  
❌ Panic if you don't know something  

### If You Don't Know:
1. Admit it honestly
2. Explain your thought process
3. Suggest how you'd find the answer
4. Relate to something you do know

---

## 🚀 Quick Revision Checklist

Before interview, ensure you can explain:

□ Overall architecture diagram  
□ Why microservices over monolith  
□ How JWT authentication works  
□ Complete booking flow (step-by-step)  
□ How to prevent double booking  
□ Razorpay payment integration  
□ Feign Client vs RestTemplate  
□ Eureka service discovery  
□ API Gateway role  
□ RabbitMQ event-driven architecture  
□ @Transactional and ACID properties  
□ @PreAuthorize for authorization  
□ DTO vs Entity  
□ Pessimistic vs Optimistic locking  
□ How to handle service failures  

---

**Good Luck with Your Interview! 🎉**

Remember: Confidence comes from understanding, not memorization.
