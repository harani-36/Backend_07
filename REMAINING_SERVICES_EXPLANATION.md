# Booking, Payment, Fare, Search & Notification Services - Complete Explanation

---

## 🔷 BOOKING SERVICE (Port: 8083)

### Overview
Manages ticket bookings, coordinates with Train and Payment services.

### Entity: Booking

```java
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Long journeyId;
    
    @Column(nullable = false)
    private Long trainId;
    
    @Column(nullable = false)
    private Long seatId;
    
    @Column(nullable = false, length = 100)
    private String passengerName;
    
    @Column(nullable = false, length = 20)
    private String coachType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus bookingStatus;
    
    @Column
    private Long paymentId;
    
    @Column(nullable = false)
    private Double fare;
    
    @Column(nullable = false)
    private LocalDateTime bookingTime;
}
```

**Why store IDs instead of relationships?**
- Microservices principle: Loose coupling
- Train/Journey data in different service
- Avoids distributed transactions
- Simpler data model

### BookingStatus Enum

```java
public enum BookingStatus {
    PENDING,    // Booking created, payment pending
    CONFIRMED,  // Payment successful
    CANCELLED   // Payment failed or user cancelled
}
```

### BookingServiceImpl

```java
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TrainClient trainClient;
    private final PaymentClient paymentClient;

    @Transactional
    @Override
    public BookingResponse createBooking(BookingRequest request) {
        // Step 1: Lock seat in Train Service
        trainClient.lockSeat(request.getSeatId());

        // Step 2: Create booking with PENDING status
        Booking booking = new Booking();
        booking.setUserId(request.getUserId());
        booking.setJourneyId(request.getJourneyId());
        booking.setTrainId(request.getTrainId());
        booking.setSeatId(request.getSeatId());
        booking.setPassengerName(request.getPassengerName());
        booking.setCoachType(request.getCoachType());
        booking.setFare(request.getFare());
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setBookingTime(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);

        // Step 3: Create Razorpay order via Payment Service
        PaymentOrderRequest orderRequest = new PaymentOrderRequest(
            savedBooking.getId(), 
            request.getFare()
        );
        PaymentOrderResponse orderResponse = paymentClient.createOrder(orderRequest);

        // Step 4: Return booking with payment details
        BookingResponse response = new BookingResponse();
        response.setBookingId(savedBooking.getId());
        response.setRazorpayOrderId(orderResponse.getRazorpayOrderId());
        response.setRazorpayKeyId(orderResponse.getRazorpayKeyId());
        // ... set other fields
        
        return response;
    }

    @Transactional
    @Override
    public void confirmBooking(Long bookingId, Long paymentId) {
        Booking booking = getBookingById(bookingId);
        
        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Booking is not in PENDING status");
        }
        
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setPaymentId(paymentId);
        bookingRepository.save(booking);
        
        // Permanently book the seat
        trainClient.bookSeat(booking.getSeatId());
    }

    @Transactional
    @Override
    public void cancelBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        
        if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot cancel confirmed booking");
        }
        
        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        
        // Unlock the seat
        trainClient.unlockSeat(booking.getSeatId());
    }
}
```

**Booking Flow**:
```
1. User initiates booking
2. Lock seat (Train Service)
3. Create booking (PENDING)
4. Create Razorpay order (Payment Service)
5. Return order details to user
6. User completes payment on Razorpay
7. Payment Service verifies payment
8. Payment Service calls confirmBooking()
9. Booking status → CONFIRMED
10. Seat permanently booked
```

**Interview Q**: Why create booking before payment?  
**Answer**: Need booking ID to associate with payment. Booking in PENDING state reserves seat temporarily. If payment fails, booking can be cancelled and seat unlocked.

**Interview Q**: What if payment succeeds but confirmBooking() fails?  
**Answer**: Payment Service has rollback logic. If confirmBooking() fails, payment status reverts to PENDING. User can retry or get refund.

---

## 🔷 PAYMENT SERVICE (Port: 8086)

### Overview
Handles Razorpay payment integration, signature verification, event publishing.

### Entity: Payment

```java
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(nullable = false)
    private Long bookingId;
    
    @NotNull
    @Positive
    @Column(nullable = false)
    private Double amount;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus paymentStatus;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod paymentMethod;
    
    @Column(length = 100)
    private String razorpayOrderId;
    
    @Column(length = 100)
    private String razorpayPaymentId;
    
    @Column(length = 255)
    private String razorpaySignature;
    
    @NotNull
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
```

### PaymentStatus Enum

```java
public enum PaymentStatus {
    PENDING,   // Order created, payment not completed
    SUCCESS,   // Payment verified successfully
    FAILED     // Payment verification failed
}
```

### PaymentMethod Enum

```java
public enum PaymentMethod {
    UPI,
    CARD,
    NETBANKING
}
```

### RazorpayConfig

```java
@Configuration
@Slf4j
public class RazorpayConfig {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Bean
    public RazorpayClient razorpayClient() throws RazorpayException {
        log.info("Initializing Razorpay client");
        return new RazorpayClient(keyId, keySecret);
    }
}
```

**RazorpayClient**
- Official Razorpay Java SDK
- Handles API communication
- Managed as Spring bean

### PaymentServiceImpl

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingClient bookingClient;
    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;
    
    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Override
    @Transactional
    public PaymentOrderResponse createOrder(PaymentOrderRequest request) {
        // Check for duplicate payment
        Optional<Payment> existingPayment = paymentRepository
            .findByBookingId(request.getBookingId());
        if (existingPayment.isPresent() && 
            existingPayment.get().getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new RuntimeException("Payment already completed");
        }
        
        // Validate booking exists
        bookingClient.getBookingById(request.getBookingId());

        // Create Razorpay order
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", Math.round(request.getAmount() * 100)); // Paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "booking_" + request.getBookingId());

        Order order = razorpayClient.orders.create(orderRequest);
        String orderId = order.get("id");
        
        // Save payment with PENDING status
        Payment payment = existingPayment.orElse(new Payment());
        payment.setBookingId(request.getBookingId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setRazorpayOrderId(orderId);
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
        
        return new PaymentOrderResponse(orderId, request.getAmount(), "INR", razorpayKeyId);
    }

    @Override
    @Transactional
    public Payment verifyPayment(PaymentVerificationRequest request) {
        // Find payment by order ID
        Payment payment = paymentRepository
            .findByRazorpayOrderId(request.getRazorpayOrderId())
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        
        // Verify signature
        String generatedSignature = generateSignature(
            request.getRazorpayOrderId(), 
            request.getRazorpayPaymentId()
        );
        
        if (generatedSignature.equals(request.getRazorpaySignature())) {
            // Signature valid
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setRazorpaySignature(request.getRazorpaySignature());
            Payment savedPayment = paymentRepository.save(payment);
            
            // Confirm booking
            try {
                bookingClient.confirmBooking(request.getBookingId(), savedPayment.getId());
            } catch (Exception e) {
                // Rollback payment status
                payment.setPaymentStatus(PaymentStatus.PENDING);
                paymentRepository.save(payment);
                throw new RuntimeException("Failed to confirm booking", e);
            }
            
            return savedPayment;
        } else {
            // Signature invalid
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            
            // Cancel booking
            bookingClient.cancelBooking(request.getBookingId());
            
            throw new RuntimeException("Payment signature verification failed");
        }
    }
    
    private String generateSignature(String orderId, String paymentId) {
        try {
            String payload = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                razorpayKeySecret.getBytes(), 
                "HmacSHA256"
            );
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(payload.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature", e);
        }
    }
}
```

**Razorpay Integration**:

**Step 1: Create Order**
```
POST /payments/create-order
{
  "bookingId": 1,
  "amount": 1500.00,
  "paymentMethod": "UPI"
}

Response:
{
  "razorpayOrderId": "order_xxxxx",
  "amount": 1500.00,
  "currency": "INR",
  "razorpayKeyId": "rzp_test_xxxxx"
}
```

**Step 2: User Pays on Razorpay**
- Frontend integrates Razorpay Checkout
- User completes payment
- Razorpay returns: paymentId, orderId, signature

**Step 3: Verify Payment**
```
POST /payments/verify
{
  "bookingId": 1,
  "razorpayOrderId": "order_xxxxx",
  "razorpayPaymentId": "pay_xxxxx",
  "razorpaySignature": "generated_signature"
}
```

**Signature Verification**:
```
payload = orderId + "|" + paymentId
signature = HMAC-SHA256(payload, secret)

If signature matches Razorpay's signature:
  → Payment genuine
Else:
  → Payment tampered
```

**Interview Q**: Why verify payment signature?  
**Answer**: Prevents payment tampering. Attacker could send fake payment success. Signature proves payment came from Razorpay. Only Razorpay knows the secret key.

**Interview Q**: Why convert amount to paise?  
**Answer**: Razorpay API expects amount in smallest currency unit (paise for INR). ₹15.00 = 1500 paise. Prevents floating-point precision issues.

---

## 🔷 FARE SERVICE (Port: 8084)

### Overview
Simple service to manage fare for train and coach type combinations.

### Entity: Fare

```java
@Entity
@Table(name="fare")
@Getter
@Setter
@NoArgsConstructor
public class Fare {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(nullable = false)
    private Long trainId;
    
    @NotNull
    @Column(nullable = false, length = 20)
    private String coachType;
    
    @NotNull
    @Positive
    @Column(nullable = false)
    private Double amount;
}
```

### FareServiceImpl

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class FareServiceImpl implements FareService {
    private final FareRepository fareRepository;
    
    @Override
    public FareResponse addFare(FareRequest request) {
        Fare fare = new Fare();
        fare.setTrainId(request.getTrainId());
        fare.setCoachType(request.getCoachType());
        fare.setAmount(request.getAmount());
        Fare saved = fareRepository.save(fare);
        return new FareResponse(
            saved.getTrainId(),
            saved.getCoachType(),
            saved.getAmount()
        );
    }

    @Override
    public FareResponse getFare(Long trainId, String coachType) {
        Fare fare = fareRepository
            .findByTrainIdAndCoachType(trainId, coachType)
            .orElseThrow(() -> new RuntimeException("Fare not found"));
        return new FareResponse(
            fare.getTrainId(),
            fare.getCoachType(),
            fare.getAmount()
        );
    }
}
```

**FareRepository**:
```java
public interface FareRepository extends JpaRepository<Fare, Long> {
    Optional<Fare> findByTrainIdAndCoachType(Long trainId, String coachType);
}
```

**Usage**:
```
Admin adds fare:
POST /fare
{
  "trainId": 1,
  "coachType": "AC",
  "amount": 1500.00
}

Train Service fetches fare:
GET /fare?trainId=1&coachType=AC
Response: { "trainId": 1, "coachType": "AC", "amount": 1500.00 }
```

**Interview Q**: Why separate Fare Service?  
**Answer**: 
- Single Responsibility: Fare logic isolated
- Independent scaling: Fare queries can be cached
- Flexibility: Can add complex pricing logic later
- Microservices principle: Separate bounded contexts

---

## 🔷 SEARCH SERVICE (Port: 8085)

### Overview
Stateless service that searches trains by source and destination.

### SearchController

```java
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final TrainClient trainClient;

    @GetMapping
    public List<TrainResponse> searchTrains(
            @RequestParam String source,
            @RequestParam String destination) {
        
        log.info("Searching trains from {} to {}", source, destination);
        
        List<TrainResponse> results = trainClient.getAllTrains()
                .stream()
                .filter(train ->
                        train.getSource().equalsIgnoreCase(source)
                        && train.getDestination().equalsIgnoreCase(destination))
                .toList();
        
        log.debug("Found {} trains", results.size());
        return results;
    }
}
```

**TrainClient (Feign)**:
```java
@FeignClient(name = "TRAIN-SERVICE")
public interface TrainClient {
    @GetMapping("/trains")
    List<TrainResponse> getAllTrains();
}
```

**Flow**:
```
1. User: GET /search?source=Mumbai&destination=Delhi
2. Search Service calls Train Service via Feign
3. Gets all trains
4. Filters by source and destination
5. Returns matching trains
```

**Interview Q**: Why not query database directly?  
**Answer**: 
- Microservices principle: Each service owns its data
- Search Service doesn't have train database
- Loose coupling: Train Service can change database without affecting Search
- Feign provides service-to-service communication

**Interview Q**: Performance concern with fetching all trains?  
**Answer**: Yes, not scalable. Better approach:
- Add search endpoint in Train Service with database query
- Use caching (Redis)
- Implement pagination
- Use Elasticsearch for advanced search

---

## 🔷 NOTIFICATION SERVICE (Port: 8087)

### Overview
Event-driven service that listens to RabbitMQ and sends notifications.

### RabbitMQConfig

```java
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange}")
    private String exchange;
    
    @Value("${rabbitmq.queue}")
    private String queue;
    
    @Value("${rabbitmq.routingKey}")
    private String routingKey;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue queue() {
        return new Queue(queue);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }
}
```

**RabbitMQ Components**:
- **Exchange**: Routes messages to queues
- **Queue**: Stores messages
- **Routing Key**: Determines which queue receives message
- **Binding**: Links exchange to queue with routing key

### PaymentEventListener

```java
@Component
@Slf4j
public class PaymentEventListener {

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void handlePaymentSuccessEvent(String message) {
        log.info("Received payment event: {}", message);
        
        if (message.startsWith("BOOKING_PAYMENT_SUCCESS:")) {
            String bookingId = message.split(":")[1];
            sendEmailNotification(bookingId);
        }
    }

    private void sendEmailNotification(String bookingId) {
        log.info("📧 Email Notification: Payment successful for Booking ID: {}", bookingId);
        System.out.println("==============================================");
        System.out.println("📧 NOTIFICATION SENT");
        System.out.println("Payment successful for Booking ID: " + bookingId);
        System.out.println("==============================================");
    }
}
```

**@RabbitListener**
- Listens to specified queue
- Automatically deserializes message
- Method called when message arrives

**Event Flow**:
```
1. Payment Service: Payment verified successfully
2. Payment Service publishes to RabbitMQ:
   rabbitTemplate.convertAndSend(exchange, routingKey, "BOOKING_PAYMENT_SUCCESS:123")
3. RabbitMQ routes message to notification queue
4. Notification Service listener triggered
5. Sends email notification (simulated)
```

**Interview Q**: Why use RabbitMQ instead of direct HTTP call?  
**Answer**:
- **Decoupling**: Payment Service doesn't need to know about Notification Service
- **Reliability**: Message persisted if Notification Service is down
- **Scalability**: Multiple notification consumers can process messages
- **Asynchronous**: Payment doesn't wait for notification to complete

**Interview Q**: What if notification fails?  
**Answer**: 
- RabbitMQ can retry automatically
- Dead Letter Queue for failed messages
- Manual retry mechanism
- Alert monitoring team

---

## 🔗 Feign Client Configuration

### FeignConfig (Used in multiple services)

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

**Purpose**: Propagate JWT token to downstream services

**How it works**:
```
1. User sends request with Authorization header
2. Booking Service receives request
3. Booking Service calls Train Service via Feign
4. FeignConfig intercepts the call
5. Extracts Authorization header from current request
6. Adds header to Feign request
7. Train Service receives request with JWT token
```

**Without this**:
- Feign calls would have no Authorization header
- Downstream services would reject requests (401 Unauthorized)

**Interview Q**: Why propagate JWT token?  
**Answer**: Downstream services need to validate user identity and permissions. Without token, they can't perform authorization checks. Token propagation maintains security across service calls.

---

## 📊 Complete System Flow

### End-to-End Booking Flow:

```
1. User Registration
   POST /auth/register → Auth Service
   
2. User Login
   POST /auth/login → Auth Service
   Returns JWT token
   
3. Search Trains
   GET /search?source=Mumbai&destination=Delhi → Search Service
   → Calls Train Service via Feign
   Returns matching trains
   
4. Get Train with Fare
   GET /trains/1/fare?coachType=AC → Train Service
   → Calls Fare Service via Feign
   Returns train details + fare
   
5. Create Booking
   POST /booking → Booking Service
   → Locks seat (Train Service)
   → Creates booking (PENDING)
   → Creates Razorpay order (Payment Service)
   Returns booking + order details
   
6. User Pays on Razorpay
   Frontend integrates Razorpay Checkout
   User completes payment
   
7. Verify Payment
   POST /payments/verify → Payment Service
   → Verifies signature
   → Updates payment status (SUCCESS)
   → Confirms booking (Booking Service)
   → Books seat permanently (Train Service)
   → Publishes event to RabbitMQ
   
8. Send Notification
   Notification Service listens to RabbitMQ
   Sends email notification
```

---

## 🎯 Key Interview Questions

**Q1: How do microservices communicate?**

**Answer**: 
- **Synchronous**: Feign Client (REST over HTTP)
- **Asynchronous**: RabbitMQ (message broker)
- Feign for request-response (booking, payment)
- RabbitMQ for fire-and-forget (notifications)

**Q2: How do you handle distributed transactions?**

**Answer**: 
- Avoid distributed transactions (2PC)
- Use Saga pattern (choreography)
- Each service has local transaction
- Compensating transactions for rollback
- Example: Payment fails → Cancel booking → Unlock seat

**Q3: What if a service is down?**

**Answer**:
- Circuit breaker (Resilience4j)
- Fallback methods
- Retry logic
- Graceful degradation
- Health checks and monitoring

**Q4: How do you ensure data consistency?**

**Answer**:
- Eventual consistency (not strong consistency)
- Event-driven architecture
- Idempotency (prevent duplicate operations)
- Compensating transactions
- Database-level constraints

**Q5: Why use DTOs instead of entities?**

**Answer**:
- API contract separate from database model
- Security (don't expose internal structure)
- Versioning (change DTO without changing entity)
- Validation (different rules for API vs database)

---

## ✅ Key Takeaways

✅ **Booking Service**: Orchestrates booking flow, coordinates services  
✅ **Payment Service**: Razorpay integration, signature verification  
✅ **Fare Service**: Simple CRUD for fare management  
✅ **Search Service**: Stateless, delegates to Train Service  
✅ **Notification Service**: Event-driven, RabbitMQ consumer  
✅ **Feign Client**: Synchronous inter-service communication  
✅ **RabbitMQ**: Asynchronous event-driven architecture  
✅ **JWT Propagation**: Security across service boundaries  

---
