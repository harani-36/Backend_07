# Eureka Server & API Gateway - Detailed Explanation

---

## 🔷 EUREKA SERVER (Service Discovery)

### Overview
Eureka Server is Netflix's service discovery solution that maintains a registry of all microservices in the system.

---

### Class: EurekaApplication

**Location**: `com.railnova.EurekaApplication`

**Layer**: Main Application Class

#### Complete Code Explanation:

```java
package com.railnova;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer  // ← Makes this application a Eureka Server
@SpringBootApplication
public class EurekaApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication.class, args);
    }
}
```

#### Line-by-Line Explanation:

**@EnableEurekaServer**
- **Purpose**: Activates Eureka Server functionality
- **What it does**: 
  - Starts embedded Eureka Server
  - Opens port 8761 (default)
  - Provides REST endpoints for service registration
  - Maintains service registry in memory
- **Without it**: Application would be a normal Spring Boot app, not a Eureka Server

**@SpringBootApplication**
- **Purpose**: Composite annotation combining:
  - `@Configuration`: Marks class as configuration source
  - `@EnableAutoConfiguration`: Auto-configures Spring based on dependencies
  - `@ComponentScan`: Scans for Spring components
- **What it does**: Bootstraps Spring Boot application

**SpringApplication.run()**
- **Purpose**: Launches the Spring Boot application
- **What it does**:
  - Creates ApplicationContext
  - Starts embedded Tomcat server
  - Initializes Eureka Server
  - Begins accepting service registrations

---

### How Eureka Server Works

#### 1. Service Registration
When a microservice starts, it registers itself with Eureka:

```
Train Service (Port 8082) → Eureka Server (Port 8761)
Registration: {
  "name": "TRAIN-SERVICE",
  "host": "localhost",
  "port": 8082,
  "healthCheckUrl": "http://localhost:8082/actuator/health"
}
```

#### 2. Service Discovery
When Booking Service needs to call Train Service:

```
Booking Service → Eureka Server: "Where is TRAIN-SERVICE?"
Eureka Server → Booking Service: "TRAIN-SERVICE is at localhost:8082"
Booking Service → Train Service: Makes HTTP call
```

#### 3. Heartbeat Mechanism
- Every 30 seconds, services send heartbeat to Eureka
- If no heartbeat for 90 seconds, service is removed from registry
- Ensures only healthy services are available

---

### Configuration (application.properties)

```properties
server.port=8761
eureka.client.register-with-eureka=false  # Don't register itself
eureka.client.fetch-registry=false        # Don't fetch registry
```

---

### Interview Questions

**Q1: What is Eureka Server and why is it needed?**

**Answer**: Eureka Server is a service discovery tool that maintains a registry of all microservices. It's needed because:
- In microservices, services are deployed on different hosts/ports
- Hardcoding URLs is not scalable
- Services can scale up/down dynamically
- Eureka provides dynamic service discovery

**Q2: What happens if Eureka Server goes down?**

**Answer**: 
- Services already registered continue to work
- New services cannot register
- Existing services cache the registry locally
- Services can still communicate using cached information
- Best practice: Run multiple Eureka instances for high availability

**Q3: How does Eureka detect if a service is down?**

**Answer**:
- Services send heartbeat every 30 seconds
- If no heartbeat for 90 seconds (3 missed heartbeats), service is evicted
- Eureka marks service as DOWN
- Other services won't route requests to it

---

## 🔷 API GATEWAY

### Overview
API Gateway is the single entry point for all client requests. It routes requests to appropriate microservices and handles cross-cutting concerns like authentication.

---

### Class 1: ApiGatewayApplication

**Location**: `com.apigateway.ApiGatewayApplication`

**Layer**: Main Application Class

#### Code:

```java
package com.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
```

**Explanation**:
- Standard Spring Boot application
- No special annotations needed (Gateway configured via properties)
- Starts on port 8080

---

### Class 2: JwtAuthenticationFilter

**Location**: `com.apigateway.config.JwtAuthenticationFilter`

**Layer**: Security Filter

**Purpose**: Validates JWT tokens for all incoming requests

#### Complete Code Explanation:

```java
@Component  // ← Spring manages this as a bean
@RequiredArgsConstructor  // ← Lombok generates constructor for final fields
public class JwtAuthenticationFilter implements GlobalFilter {
```

**@Component**
- Makes this class a Spring-managed bean
- Spring automatically detects and registers it
- Without it: Filter won't be applied to requests

**implements GlobalFilter**
- Interface from Spring Cloud Gateway
- Applied to ALL requests passing through gateway
- Executes before routing to downstream services

#### Field:

```java
private final JwtUtil jwtUtil;  // ← Injected by Spring
```

**Why final?**
- Ensures immutability
- Required by @RequiredArgsConstructor
- Prevents accidental reassignment

#### Main Method:

```java
@Override
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
```

**ServerWebExchange**
- Represents HTTP request and response
- Reactive (non-blocking) approach
- Contains headers, body, path, method

**GatewayFilterChain**
- Chain of filters
- Allows passing request to next filter
- Similar to Servlet Filter Chain

**Mono<Void>**
- Reactive type from Project Reactor
- Represents 0 or 1 result
- Non-blocking asynchronous processing

#### Step 1: Extract Request Path

```java
String path = exchange.getRequest().getURI().getPath();
log.debug("API Gateway - Incoming request: {} {}", 
    exchange.getRequest().getMethod(), path);
```

**Purpose**: Log and identify which endpoint is being accessed

#### Step 2: Allow OPTIONS Requests

```java
if (exchange.getRequest().getMethod().name().equals("OPTIONS")) {
    return chain.filter(exchange);
}
```

**Why?**
- OPTIONS is used for CORS preflight requests
- Browsers send OPTIONS before actual request
- Should not require authentication

#### Step 3: Allow Swagger Endpoints

```java
if (path.startsWith("/swagger") ||
    path.startsWith("/v3/api-docs") ||
    path.contains("api-docs") ||
    path.contains("swagger-ui") ||
    path.contains("webjars")) {
    return chain.filter(exchange);
}
```

**Why?**
- Swagger UI needs to be publicly accessible
- Developers need to view API documentation
- No authentication required for documentation

#### Step 4: Allow Auth Endpoints

```java
if (path.contains("/auth/register") ||
    path.contains("/auth/login")) {
    return chain.filter(exchange);
}
```

**Why?**
- Users need to register/login without token
- These endpoints generate the token
- Chicken-and-egg problem if we require token here

#### Step 5: Extract Authorization Header

```java
String authHeader = exchange.getRequest()
        .getHeaders()
        .getFirst("Authorization");

if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    log.warn("API Gateway - No valid Authorization header for path: {}", path);
    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    return exchange.getResponse().setComplete();
}
```

**Authorization Header Format**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**What happens if missing?**
- Returns 401 Unauthorized
- Request is NOT forwarded to downstream service
- Response is completed immediately

#### Step 6: Extract Token

```java
String token = authHeader.substring(7);  // Remove "Bearer " prefix
```

**Why substring(7)?**
- "Bearer " is 7 characters long
- We only need the actual token part

#### Step 7: Validate Token

```java
if (!jwtUtil.validateToken(token)) {
    log.error("API Gateway - Invalid JWT token for path: {}", path);
    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    return exchange.getResponse().setComplete();
}
```

**What validateToken() does**:
- Parses JWT token
- Verifies signature using secret key
- Checks expiration time
- Returns true if valid, false otherwise

#### Step 8: Forward Request

```java
log.info("API Gateway - JWT validated successfully, forwarding to: {}", path);
return chain.filter(exchange);
```

**What happens here?**
- Request is forwarded to appropriate microservice
- Token is passed along in headers
- Downstream service can extract user info from token

---

### Class 3: SecurityConfig

**Location**: `com.apigateway.config.SecurityConfig`

**Layer**: Security Configuration

#### Code:

```java
@Configuration  // ← Marks this as configuration class
public class SecurityConfig {

    @Bean  // ← Creates Spring-managed bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())  // ← Disable CSRF for REST APIs
                .authorizeExchange(exchange -> exchange
                        .anyExchange().permitAll()  // ← Allow all requests
                )
                .build();
    }
}
```

**@Configuration**
- Indicates this class contains bean definitions
- Spring processes this at startup
- Methods with @Bean are called to create beans

**@Bean**
- Registers return value as Spring bean
- Bean name = method name (securityWebFilterChain)
- Managed by Spring container

**csrf.disable()**
- **CSRF**: Cross-Site Request Forgery protection
- **Why disable?**: 
  - REST APIs are stateless
  - JWT tokens provide security
  - CSRF is for session-based authentication
  - Not needed for token-based auth

**anyExchange().permitAll()**
- Allows all requests to pass through
- Actual authentication done in JwtAuthenticationFilter
- Spring Security won't block any requests

**Why this approach?**
- Custom JWT validation in filter
- More control over authentication logic
- Spring Security used only for CSRF disabling

---

### Class 4: JwtUtil

**Location**: `com.apigateway.util.JwtUtil`

**Layer**: Utility Class

**Purpose**: JWT token validation

#### Code Explanation:

```java
@Component  // ← Spring-managed bean
public class JwtUtil {

    @Value("${jwt.secret}")  // ← Injects value from application.properties
    private String SECRET;
```

**@Value Annotation**
- Injects configuration values
- Reads from application.properties
- Format: `${property.name}`

**application.properties**:
```properties
jwt.secret=mySecretKeyForJWTTokenGenerationAndValidation123456789
```

#### Method 1: getSignKey()

```java
private Key getSignKey() {
    return Keys.hmacShaKeyFor(SECRET.getBytes());
}
```

**Purpose**: Creates cryptographic key from secret string

**Keys.hmacShaKeyFor()**
- From io.jsonwebtoken library
- Converts byte array to Key object
- Used for HMAC-SHA256 algorithm

#### Method 2: extractAllClaims()

```java
public Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(getSignKey())  // ← Set key for verification
            .build()
            .parseClaimsJws(token)  // ← Parse and verify token
            .getBody();  // ← Extract claims (payload)
}
```

**What are Claims?**
- Payload of JWT token
- Contains user information
- Example: `{ "sub": "user@example.com", "role": "USER" }`

**parseClaimsJws()**
- Parses JWT token
- Verifies signature
- Throws exception if invalid/expired

#### Method 3: extractRole()

```java
public String extractRole(String token) {
    return extractAllClaims(token).get("role", String.class);
}
```

**Purpose**: Extract role from token claims

**Usage**: Can be used for role-based routing (not currently used)

#### Method 4: validateToken()

```java
public boolean validateToken(String token) {
    try {
        extractAllClaims(token);  // ← If this succeeds, token is valid
        return true;
    } catch (Exception e) {
        return false;  // ← Any exception means invalid token
    }
}
```

**Exceptions that can occur**:
- `ExpiredJwtException`: Token expired
- `SignatureException`: Invalid signature
- `MalformedJwtException`: Malformed token
- `IllegalArgumentException`: Empty token

---

### Request Flow Through API Gateway

```
1. Client Request
   ↓
   GET http://localhost:8080/trains
   Headers: Authorization: Bearer <token>

2. API Gateway Receives Request
   ↓
   JwtAuthenticationFilter.filter() called

3. Extract Path
   ↓
   path = "/trains"

4. Check if Public Endpoint
   ↓
   Not auth/register or auth/login → Continue

5. Extract Authorization Header
   ↓
   authHeader = "Bearer eyJhbGc..."

6. Extract Token
   ↓
   token = "eyJhbGc..."

7. Validate Token
   ↓
   jwtUtil.validateToken(token) → true

8. Forward to Train Service
   ↓
   Eureka resolves TRAIN-SERVICE → localhost:8082
   ↓
   GET http://localhost:8082/trains
   Headers: Authorization: Bearer <token>

9. Train Service Processes Request
   ↓
   Returns train list

10. API Gateway Returns Response
    ↓
    Client receives train list
```

---

### Configuration (application.yml)

```yaml
server:
  port: 8080

spring:
  application:
    name: API-GATEWAY
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: lb://AUTH-SERVICE  # Load balanced via Eureka
          predicates:
            - Path=/auth/**
        
        - id: train-service
          uri: lb://TRAIN-SERVICE
          predicates:
            - Path=/trains/**, /journeys/**
        
        - id: booking-service
          uri: lb://BOOKING-SERVICE
          predicates:
            - Path=/booking/**
        
        - id: payment-service
          uri: lb://PAYMENT-SERVICE
          predicates:
            - Path=/payments/**
        
        - id: fare-service
          uri: lb://FARE-SERVICE
          predicates:
            - Path=/fare/**
        
        - id: search-service
          uri: lb://SEARCH-SERVICE
          predicates:
            - Path=/search/**

jwt:
  secret: mySecretKeyForJWTTokenGenerationAndValidation123456789
```

**Explanation**:
- **routes**: Define routing rules
- **id**: Unique identifier for route
- **uri**: Destination service (lb = load balanced)
- **predicates**: Conditions to match (Path-based routing)

---

### Interview Questions

**Q1: What is the role of API Gateway in microservices?**

**Answer**: API Gateway serves as:
1. **Single Entry Point**: All client requests go through gateway
2. **Authentication**: Validates JWT tokens centrally
3. **Routing**: Routes requests to appropriate microservices
4. **Load Balancing**: Distributes load across service instances
5. **Cross-Cutting Concerns**: Logging, monitoring, rate limiting

**Q2: How does JWT validation work in API Gateway?**

**Answer**:
1. Extract token from Authorization header
2. Parse token using secret key
3. Verify signature (HMAC-SHA256)
4. Check expiration time
5. If valid, forward request; else return 401

**Q3: What happens if API Gateway goes down?**

**Answer**:
- All client requests fail (single point of failure)
- Microservices are still running but unreachable
- Solution: Run multiple gateway instances behind load balancer
- Use health checks and auto-scaling

**Q4: Why use Spring Cloud Gateway instead of Zuul?**

**Answer**:
- **Reactive**: Non-blocking, better performance
- **Spring 5+**: Built on Spring WebFlux
- **Active Development**: Zuul 1 is in maintenance mode
- **Better Integration**: Native Spring Cloud support

**Q5: How does Gateway know which service to route to?**

**Answer**:
1. Gateway reads path from request (e.g., /trains)
2. Matches path against configured predicates
3. Finds matching route (e.g., TRAIN-SERVICE)
4. Queries Eureka for service location
5. Routes request to resolved host:port

---

### Key Takeaways

✅ **Eureka Server**: Service registry for dynamic discovery  
✅ **API Gateway**: Single entry point with JWT validation  
✅ **GlobalFilter**: Applied to all requests  
✅ **Reactive Programming**: Non-blocking with Mono/Flux  
✅ **Path-Based Routing**: Routes based on URL patterns  
✅ **Load Balancing**: Automatic via Eureka integration  

---
