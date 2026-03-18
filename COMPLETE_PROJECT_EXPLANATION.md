# RailNova - Complete Project Explanation

## 🚀 Project Overview

**RailNova** is a comprehensive railway ticket booking system built using **Spring Boot Microservices Architecture**. It demonstrates enterprise-level design patterns, inter-service communication, security, payment integration, and event-driven architecture.

---

## 📋 Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Microservices List](#microservices-list)
3. [Technology Stack](#technology-stack)
4. [System Flow](#system-flow)
5. [Key Design Patterns](#key-design-patterns)
6. [Database Schema](#database-schema)
7. [Security Implementation](#security-implementation)
8. [Inter-Service Communication](#inter-service-communication)

---

## Architecture Overview

### Microservices Architecture Pattern

RailNova follows a **distributed microservices architecture** where each service is:
- **Independent**: Can be deployed separately
- **Scalable**: Can scale horizontally
- **Resilient**: Failure in one service doesn't crash the entire system
- **Technology Agnostic**: Each service can use different technologies

### Architecture Diagram

```
                                    ┌─────────────────┐
                                    │  Eureka Server  │
                                    │  (Port: 8761)   │
                                    └────────┬────────┘
                                             │
                                             │ Service Registration
                                             │
                    ┌────────────────────────┼────────────────────────┐
                    │                        │                        │
         ┌──────────▼──────────┐  ┌─────────▼─────────┐  ┌──────────▼──────────┐
         │   API Gateway        │  │   Auth Service    │  │   Train Service     │
         │   (Port: 8080)       │  │   (Port: 8081)    │  │   (Port: 8082)      │
         └──────────┬───────────┘  └───────────────────┘  └──────────┬──────────┘
                    │                                                  │
         JWT Validation                                        Feign Client
                    │                                                  │
    ┌───────────────┼──────────────────────────────────────────┬─────┴─────┐
    │               │                                           │           │
┌───▼────┐  ┌──────▼──────┐  ┌──────────────┐  ┌─────────────▼──┐  ┌─────▼──────┐
│ Search │  │  Booking    │  │   Payment    │  │   Fare Service │  │  Journey   │
│ Service│  │  Service    │  │   Service    │  │   (Port: 8084) │  │  Service   │
│(8085)  │  │  (Port:8083)│  │  (Port:8086) │  └────────────────┘  └────────────┘
└────────┘  └──────┬──────┘  └──────┬───────┘
                   │                 │
                   │                 │ RabbitMQ Event
                   │                 │
                   │         ┌───────▼────────────┐
                   │         │   Notification     │
                   │         │   Service          │
                   │         │   (Port: 8087)     │
                   │         └────────────────────┘
                   │
                   └─────► Razorpay Payment Gateway
```

---

## Microservices List

### 1. **Eureka Server** (Port: 8761)
- **Purpose**: Service Discovery
- **Responsibility**: Maintains registry of all microservices
- **Key Feature**: Enables dynamic service discovery

### 2. **API Gateway** (Port: 8080)
- **Purpose**: Single entry point for all client requests
- **Responsibility**: 
  - Route requests to appropriate services
  - JWT token validation
  - Cross-cutting concerns (logging, security)
- **Key Feature**: Centralized authentication

### 3. **Auth Service** (Port: 8081)
- **Purpose**: User authentication and authorization
- **Responsibility**:
  - User registration
  - User login
  - JWT token generation
  - Password encryption
- **Database**: MySQL (user table)

### 4. **Train Service** (Port: 8082)
- **Purpose**: Train and journey management
- **Responsibility**:
  - Add trains
  - Create journeys with coaches and seats
  - Seat locking/booking
  - Fetch train details with fare
- **Database**: MySQL (train, journey, coach, seat tables)

### 5. **Booking Service** (Port: 8083)
- **Purpose**: Booking management
- **Responsibility**:
  - Create bookings
  - Confirm bookings after payment
  - Cancel bookings
  - Coordinate with Train and Payment services
- **Database**: MySQL (booking table)

### 6. **Fare Service** (Port: 8084)
- **Purpose**: Fare management
- **Responsibility**:
  - Add fare for train and coach type
  - Retrieve fare information
- **Database**: MySQL (fare table)

### 7. **Search Service** (Port: 8085)
- **Purpose**: Train search functionality
- **Responsibility**:
  - Search trains by source and destination
  - Filter available trains
- **Database**: None (uses Train Service via Feign)

### 8. **Payment Service** (Port: 8086)
- **Purpose**: Payment processing
- **Responsibility**:
  - Create Razorpay payment orders
  - Verify payment signatures
  - Update payment status
  - Notify Booking Service
  - Publish events to RabbitMQ
- **Database**: MySQL (payment table)
- **Integration**: Razorpay Payment Gateway

### 9. **Notification Service** (Port: 8087)
- **Purpose**: Event-driven notifications
- **Responsibility**:
  - Listen to RabbitMQ events
  - Send email notifications (simulated)
- **Database**: None
- **Integration**: RabbitMQ

---

## Technology Stack

### Backend Framework
- **Spring Boot 3.x**
- **Spring Cloud** (Eureka, Gateway, OpenFeign)
- **Spring Security** (JWT Authentication)
- **Spring Data JPA** (Database Access)

### Database
- **MySQL** (Relational Database)

### Message Broker
- **RabbitMQ** (Event-Driven Communication)

### Payment Gateway
- **Razorpay** (Payment Processing)

### Security
- **JWT (JSON Web Tokens)** for stateless authentication
- **BCrypt** for password hashing
- **HMAC-SHA256** for payment signature verification

### API Documentation
- **Swagger/OpenAPI 3.0**

### Build Tool
- **Maven**

---

## System Flow

### 1. User Registration & Login Flow

```
User → API Gateway → Auth Service
                      ↓
                   Validate Input
                      ↓
                   Hash Password (BCrypt)
                      ↓
                   Save to Database
                      ↓
                   Generate JWT Token
                      ↓
                   Return Token to User
```

### 2. Train Search Flow

```
User → API Gateway → Search Service
                      ↓
                   Feign Client Call
                      ↓
                   Train Service (Get All Trains)
                      ↓
                   Filter by Source & Destination
                      ↓
                   Return Filtered Results
```

### 3. Booking & Payment Flow

```
User → API Gateway → Booking Service
                      ↓
                   Lock Seat (Train Service via Feign)
                      ↓
                   Create Booking (Status: PENDING)
                      ↓
                   Create Razorpay Order (Payment Service)
                      ↓
                   Return Order Details to User
                      ↓
User Completes Payment on Razorpay
                      ↓
                   Verify Payment Signature
                      ↓
                   Update Payment Status (SUCCESS)
                      ↓
                   Confirm Booking (Booking Service)
                      ↓
                   Book Seat Permanently (Train Service)
                      ↓
                   Publish Event to RabbitMQ
                      ↓
                   Notification Service Listens
                      ↓
                   Send Email Notification
```

---

## Key Design Patterns

### 1. **Microservices Pattern**
- Each service is independently deployable
- Services communicate via REST APIs

### 2. **API Gateway Pattern**
- Single entry point for all requests
- Centralized authentication and routing

### 3. **Service Discovery Pattern**
- Eureka Server maintains service registry
- Services register themselves on startup

### 4. **Circuit Breaker Pattern** (Implicit with Feign)
- Handles service failures gracefully

### 5. **Event-Driven Architecture**
- RabbitMQ for asynchronous communication
- Decouples Payment and Notification services

### 6. **Repository Pattern**
- JpaRepository for database operations
- Abstraction over data access layer

### 7. **DTO Pattern**
- Separate DTOs for request/response
- Prevents exposing entity structure

### 8. **Service Layer Pattern**
- Business logic separated from controllers
- Interface-based design for flexibility

---

## Database Schema

### Auth Service Database

**Table: user**
```sql
id (PK) | username | email (UNIQUE) | password (HASHED) | role (ENUM: USER, ADMIN)
```

### Train Service Database

**Table: train**
```sql
id (PK) | train_number (UNIQUE) | name | source | destination
```

**Table: journey**
```sql
id (PK) | train_id (FK) | journey_date | departure_time | arrival_time
```

**Table: coach**
```sql
id (PK) | coach_number | coach_type | journey_id (FK)
```

**Table: seat**
```sql
id (PK) | seat_number | status (ENUM: AVAILABLE, BOOKED) | coach_id (FK)
```

### Booking Service Database

**Table: booking**
```sql
id (PK) | user_id | journey_id | train_id | seat_id | passenger_name | 
coach_type | booking_status (ENUM: PENDING, CONFIRMED, CANCELLED) | 
payment_id | fare | booking_time
```

### Payment Service Database

**Table: payment**
```sql
id (PK) | booking_id | amount | payment_status (ENUM: PENDING, SUCCESS, FAILED) | 
payment_method (ENUM: UPI, CARD, NETBANKING) | razorpay_order_id | 
razorpay_payment_id | razorpay_signature | created_at
```

### Fare Service Database

**Table: fare**
```sql
id (PK) | train_id | coach_type | amount
```

---

## Security Implementation

### 1. **JWT Authentication**

**How it works:**
1. User logs in with email/password
2. Auth Service validates credentials
3. Generates JWT token with user email and role
4. Token contains: subject (email), role claim, issued time, expiration time
5. Token signed with HMAC-SHA256 using secret key

**Token Structure:**
```
Header.Payload.Signature
```

**Payload:**
```json
{
  "sub": "user@example.com",
  "role": "USER",
  "iat": 1234567890,
  "exp": 1234654290
}
```

### 2. **API Gateway Security**

- Validates JWT token for all requests (except auth endpoints)
- Extracts token from `Authorization: Bearer <token>` header
- Verifies signature using same secret key
- Rejects invalid/expired tokens with 401 Unauthorized

### 3. **Role-Based Access Control (RBAC)**

**@PreAuthorize Annotation:**
```java
@PreAuthorize("hasRole('ADMIN')")  // Only ADMIN can access
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")  // Both can access
```

**Roles:**
- **ADMIN**: Can add trains, journeys, fares
- **USER**: Can search, book, make payments

### 4. **Password Security**

- **BCrypt** hashing algorithm
- Salt automatically generated
- One-way encryption (cannot be decrypted)
- Strength: 10 rounds (default)

### 5. **Payment Security**

- **HMAC-SHA256** signature verification
- Prevents payment tampering
- Razorpay webhook signature validation

---

## Inter-Service Communication

### 1. **Synchronous Communication (Feign Client)**

**Example: Booking Service → Train Service**

```java
@FeignClient(name = "TRAIN-SERVICE")
public interface TrainClient {
    @PutMapping("/trains/seats/{seatId}/lock")
    void lockSeat(@PathVariable Long seatId);
}
```

**How it works:**
1. Feign creates HTTP client automatically
2. Service name resolved via Eureka
3. Load balancing handled by Spring Cloud
4. JWT token propagated via FeignConfig

### 2. **Asynchronous Communication (RabbitMQ)**

**Example: Payment Service → Notification Service**

**Publisher (Payment Service):**
```java
rabbitTemplate.convertAndSend(exchange, routingKey, message);
```

**Consumer (Notification Service):**
```java
@RabbitListener(queues = "${rabbitmq.queue}")
public void handlePaymentSuccessEvent(String message) {
    // Process event
}
```

**Benefits:**
- Decoupling: Services don't need to know each other
- Resilience: Messages queued if consumer is down
- Scalability: Multiple consumers can process messages

---

## Interview Preparation Summary

### Top 10 Questions You Should Be Ready For:

1. **Why microservices over monolithic architecture?**
2. **How does JWT authentication work in your project?**
3. **Explain the complete booking flow from user request to confirmation**
4. **How do you handle seat locking to prevent double booking?**
5. **What is the role of API Gateway?**
6. **How does Eureka Server enable service discovery?**
7. **Why use Feign Client instead of RestTemplate?**
8. **How do you verify Razorpay payment signatures?**
9. **What is the purpose of RabbitMQ in your project?**
10. **How do you handle transaction management across services?**

### Key Points to Emphasize:

✅ **Scalability**: Each service can scale independently  
✅ **Security**: JWT + Role-based access control  
✅ **Resilience**: Service discovery + Circuit breaker  
✅ **Event-Driven**: RabbitMQ for async communication  
✅ **Payment Integration**: Razorpay with signature verification  
✅ **Best Practices**: DTO pattern, Service layer, Exception handling  

---

**Next Steps**: Read individual service explanation files for detailed class-by-class breakdown.
