# Auth Service - Complete Explanation

## 📌 Service Overview

**Port**: 8081  
**Purpose**: User authentication and authorization  
**Database**: MySQL (user table)  
**Security**: JWT token generation, BCrypt password hashing

---

## 🏗️ Architecture Layers

```
Controller Layer (AuthController)
         ↓
Service Layer (AuthService → AuthServiceImpl)
         ↓
Repository Layer (UserRepository)
         ↓
Database (MySQL - user table)
```

---

## 📦 Class Breakdown

### 1. AuthServiceApplication (Main Class)

**Location**: `com.auth.AuthServiceApplication`

```java
@SpringBootApplication
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner initAdmin(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.findByEmail("admin@gmail.com").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@gmail.com");
                admin.setPassword(encoder.encode("Admin@123"));
                admin.setRole(Role.ADMIN);
                repo.save(admin);
                System.out.println("✅ Default admin created");
            }
        };
    }
}
```

**@Bean CommandLineRunner**
- Executes after application startup
- Creates default admin user if not exists
- **Why needed?**: System needs at least one admin to add trains/fares

**Interview Q**: Why use CommandLineRunner?  
**Answer**: To execute initialization logic after Spring context is loaded. Creates default admin for testing without manual database insertion.

---

### 2. User Entity

**Location**: `com.auth.entity.User`  
**Layer**: Entity (Database Model)

```java
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    @Column(nullable = false, length = 50)
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;
}
```

**Annotation Explanations**:

**@Entity**
- Marks class as JPA entity
- Maps to database table
- Without it: Not recognized as database table

**@Table(name="user")**
- Specifies table name
- Default would be class name (User)
- Explicit naming for clarity

**@Id**
- Marks primary key field
- Required for every entity

**@GeneratedValue(strategy = GenerationType.IDENTITY)**
- Auto-increment primary key
- Database generates ID automatically
- IDENTITY: Uses database auto-increment feature

**@NotBlank**
- Validation: Field cannot be null or empty
- Triggers validation error if violated
- Part of Bean Validation (JSR-380)

**@Size(min = 3, max = 50)**
- Validates string length
- Username must be 3-50 characters

**@Email**
- Validates email format
- Checks for @ symbol and domain

**@Column(unique = true)**
- Database constraint: No duplicate emails
- Throws exception if duplicate email inserted

**@Column(nullable = false)**
- Database constraint: Field cannot be NULL
- Enforced at database level

**@Enumerated(EnumType.STRING)**
- Stores enum as string in database
- Alternative: EnumType.ORDINAL (stores as integer)
- STRING is safer (order-independent)

**Interview Q**: Why use @Column(unique = true) instead of checking in code?  
**Answer**: Database-level constraint is more reliable. Prevents race conditions where two requests simultaneously create users with same email.

---

### 3. Role Enum

**Location**: `com.auth.entity.Role`

```java
public enum Role {
    ADMIN,
    USER
}
```

**Purpose**: Define user roles for authorization

**Usage in JWT**:
```java
String token = jwtUtil.generateToken(email, role.name());
// role.name() converts ADMIN → "ADMIN"
```

**Interview Q**: Why enum instead of String?  
**Answer**: 
- Type safety: Prevents typos like "ADMN"
- Compile-time checking
- IDE autocomplete support
- Centralized role definition

---

### 4. AuthController

**Location**: `com.auth.controller.AuthController`  
**Layer**: Controller (REST API)

```java
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request received for email: {}", request.getEmail());
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for email: {}", request.getEmail());
        return authService.login(request);
    }
}
```

**@RestController**
- Combines @Controller + @ResponseBody
- Returns JSON automatically
- No need for @ResponseBody on each method

**@RequestMapping("/auth")**
- Base path for all endpoints
- /register → /auth/register
- /login → /auth/login

**@RequiredArgsConstructor** (Lombok)
- Generates constructor for final fields
- Enables constructor injection
- Preferred over @Autowired

**@PostMapping**
- Maps HTTP POST requests
- Used for creating/submitting data
- GET for reading, POST for writing

**@Valid**
- Triggers validation on request body
- Validates @NotBlank, @Email, @Size annotations
- Throws MethodArgumentNotValidException if invalid

**@RequestBody**
- Deserializes JSON to Java object
- Content-Type: application/json required
- Jackson library handles conversion

**Interview Q**: Why use @Valid?  
**Answer**: Validates input at controller level before reaching service layer. Prevents invalid data from entering business logic. Returns 400 Bad Request with validation errors.

---

### 5. RegisterRequest DTO

**Location**: `com.auth.dto.RegisterRequest`

```java
@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100)
    private String password;
}
```

**Why DTO instead of Entity?**
- **Separation of Concerns**: API contract separate from database model
- **Security**: Don't expose entity fields (like id, role)
- **Flexibility**: Can have different validations than entity
- **Versioning**: Can change DTO without changing entity

**Interview Q**: What's the difference between Entity and DTO?  
**Answer**:
- **Entity**: Database model, managed by JPA
- **DTO**: Data Transfer Object, for API communication
- Entity has @Entity, DTO doesn't
- DTO prevents exposing internal structure

---

### 6. LoginRequest DTO

```java
@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
}
```

**Why no password validation here?**
- Login accepts any password (validation happens against database)
- Registration needs strong password rules
- Login just checks if password matches stored hash

---

### 7. AuthResponse DTO

```java
@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
}
```

**@AllArgsConstructor** (Lombok)
- Generates constructor with all fields
- Usage: `new AuthResponse(token)`

**Response Example**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### 8. AuthService Interface

**Location**: `com.auth.service.AuthService`

```java
public interface AuthService {
    String register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
```

**Why interface?**
- **Abstraction**: Hide implementation details
- **Testability**: Easy to mock in unit tests
- **Flexibility**: Can have multiple implementations
- **Best Practice**: Program to interface, not implementation

---

### 9. AuthServiceImpl (Business Logic)

**Location**: `com.auth.service.impl.AuthServiceImpl`

```java
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Override
    public String register(RegisterRequest request) {
        log.debug("Starting user registration for email: {}", request.getEmail());
        
        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.error("Registration failed: User already exists");
            throw new UserAlreadyExistsException("User already exists with this email");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        
        log.info("User registered successfully");
        return "Registered Successfully";
    }
    
    @Override
    public AuthResponse login(LoginRequest request) {
        log.debug("Starting login process for email: {}", request.getEmail());
        
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("Login failed: User not found");
                    return new InvalidCredentialsException("Invalid credentials");
                });
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.error("Login failed: Invalid password");
            throw new InvalidCredentialsException("Invalid credentials");
        }
        
        // Generate JWT token
        log.info("User logged in successfully");
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token);
    }
}
```

**@Service**
- Marks class as service layer component
- Spring manages as singleton bean
- Enables component scanning

**passwordEncoder.encode()**
- Hashes password using BCrypt
- One-way encryption (cannot decrypt)
- Automatically generates salt
- Example: "password123" → "$2a$10$N9qo8uLOickgx2ZMRZoMye..."

**passwordEncoder.matches()**
- Compares plain password with hashed password
- Returns true if match, false otherwise
- Handles salt automatically

**Why throw InvalidCredentialsException for both cases?**
- Security: Don't reveal if email exists
- Prevents user enumeration attacks
- Attacker can't determine valid emails

**Interview Q**: Why use BCrypt instead of MD5/SHA?  
**Answer**:
- BCrypt is designed for passwords (slow by design)
- Includes salt automatically
- Resistant to rainbow table attacks
- Adaptive: Can increase rounds as hardware improves

---

### 10. UserRepository

**Location**: `com.auth.repository.UserRepository`

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
```

**JpaRepository<User, Long>**
- User: Entity type
- Long: Primary key type
- Provides CRUD methods automatically

**Inherited Methods** (no code needed):
- `save(user)`: Insert or update
- `findById(id)`: Find by primary key
- `findAll()`: Get all users
- `deleteById(id)`: Delete by ID
- `count()`: Count records

**Custom Method: findByEmail()**
- Spring Data JPA generates implementation automatically
- Naming convention: findBy + FieldName
- Returns Optional (may or may not find user)

**Interview Q**: How does Spring generate implementation?  
**Answer**: Spring Data JPA parses method name, generates SQL query at runtime. "findByEmail" → "SELECT * FROM user WHERE email = ?"

---

### 11. JwtUtil

**Location**: `com.auth.util.JwtUtil`

```java
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey())
                .compact();
    }
}
```

**JWT Structure**:
```
Header.Payload.Signature
```

**Header** (auto-generated):
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**Payload** (our data):
```json
{
  "sub": "user@example.com",
  "role": "USER",
  "iat": 1234567890,
  "exp": 1234654290
}
```

**Signature**:
```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

**Method Breakdown**:

**setSubject(email)**
- Standard JWT claim
- Identifies token owner
- Extracted as "sub" in payload

**claim("role", role)**
- Custom claim
- Can add any key-value pair
- Used for authorization

**setIssuedAt(new Date())**
- Timestamp when token created
- Standard JWT claim (iat)

**setExpiration()**
- Token validity period
- After expiration, token is invalid
- Typical: 24 hours (86400000 ms)

**signWith(getSignKey())**
- Signs token with secret key
- HMAC-SHA256 algorithm
- Prevents tampering

**compact()**
- Converts to string format
- Base64 URL encoded
- Returns final JWT token

**Interview Q**: Can JWT be decoded?  
**Answer**: Yes, JWT is Base64 encoded (not encrypted). Anyone can decode and read payload. Security comes from signature verification, not encryption. Don't store sensitive data in JWT.

---

### 12. SecurityConfig

**Location**: `com.auth.config.SecurityConfig`

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", 
                                 "/swagger-ui.html", "/auth/register", 
                                 "/auth/login").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
```

**@EnableWebSecurity**
- Enables Spring Security
- Activates security filters
- Required for security configuration

**PasswordEncoder Bean**
- BCrypt implementation
- Injected wherever needed
- Singleton (one instance for entire app)

**csrf.disable()**
- Disables CSRF protection
- Safe for stateless REST APIs
- JWT provides security

**requestMatchers().permitAll()**
- Allows public access to specified endpoints
- No authentication required
- Swagger and auth endpoints public

**anyRequest().authenticated()**
- All other requests need authentication
- Currently not enforced (no JWT filter in Auth Service)
- Auth Service doesn't validate tokens, only generates them

**Interview Q**: Why no JWT filter in Auth Service?  
**Answer**: Auth Service generates tokens, doesn't validate them. API Gateway validates tokens. Auth Service endpoints are public (register/login) or unused.

---

### 13. Exception Classes

**InvalidCredentialsException**:
```java
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
```

**UserAlreadyExistsException**:
```java
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
```

**Why RuntimeException?**
- Unchecked exception (no need for try-catch)
- Spring handles automatically
- Cleaner code

---

### 14. GlobalExceptionHandler

**Location**: `com.auth.exception.GlobalExceptionHandler`

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(
            InvalidCredentialsException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyExists(
            UserAlreadyExistsException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("errors", errors);
        response.put("path", request.getDescription(false).replace("uri=", ""));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String message, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        response.put("path", request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(status).body(response);
    }
}
```

**@RestControllerAdvice**
- Global exception handler
- Applies to all controllers
- Returns JSON responses

**@ExceptionHandler**
- Catches specific exception type
- Converts to HTTP response
- Centralizes error handling

**Error Response Format**:
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid credentials",
  "path": "/auth/login"
}
```

**Interview Q**: Why use @RestControllerAdvice?  
**Answer**: Centralizes exception handling. Without it, every controller needs try-catch blocks. Provides consistent error response format across all endpoints.

---

## 🔄 Complete Request Flow

### Registration Flow:
```
1. POST /auth/register
   Body: { "username": "john", "email": "john@example.com", "password": "pass123" }
   
2. AuthController.register() called
   
3. @Valid triggers validation
   
4. AuthServiceImpl.register() called
   
5. Check if email exists: userRepository.findByEmail()
   
6. Hash password: passwordEncoder.encode("pass123")
   Result: "$2a$10$..."
   
7. Create User entity with role = USER
   
8. Save to database: userRepository.save(user)
   
9. Return "Registered Successfully"
```

### Login Flow:
```
1. POST /auth/login
   Body: { "email": "john@example.com", "password": "pass123" }
   
2. AuthController.login() called
   
3. AuthServiceImpl.login() called
   
4. Find user: userRepository.findByEmail()
   
5. Verify password: passwordEncoder.matches("pass123", "$2a$10$...")
   
6. Generate JWT: jwtUtil.generateToken(email, role)
   
7. Return AuthResponse with token
   
8. Client stores token for future requests
```

---

## 📊 Database Schema

```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

-- Default admin
INSERT INTO user (username, email, password, role) 
VALUES ('admin', 'admin@gmail.com', '$2a$10$...', 'ADMIN');
```

---

## 🎯 Interview Questions & Answers

**Q1: How does JWT authentication work?**

**Answer**: 
1. User logs in with credentials
2. Server validates and generates JWT token
3. Token contains user info (email, role) and signature
4. Client stores token (localStorage/cookie)
5. Client sends token in Authorization header for subsequent requests
6. Server validates token signature and extracts user info
7. No session storage needed (stateless)

**Q2: Why use BCrypt for password hashing?**

**Answer**:
- Designed specifically for passwords
- Slow by design (prevents brute force)
- Automatic salt generation
- Adaptive (can increase rounds)
- Industry standard

**Q3: What's the difference between authentication and authorization?**

**Answer**:
- **Authentication**: Verifying identity (who you are) - Login
- **Authorization**: Verifying permissions (what you can do) - Role-based access
- Auth Service handles authentication
- @PreAuthorize handles authorization

**Q4: Why return "Invalid credentials" for both wrong email and wrong password?**

**Answer**: Security best practice. Prevents user enumeration attacks. Attacker can't determine which emails are registered in the system.

**Q5: Can JWT tokens be revoked?**

**Answer**: No, JWT is stateless. Once issued, valid until expiration. Solutions:
- Short expiration time
- Refresh token mechanism
- Token blacklist (defeats stateless purpose)
- Store token version in database

---

## ✅ Key Takeaways

✅ **Stateless Authentication**: JWT eliminates session storage  
✅ **Password Security**: BCrypt with automatic salting  
✅ **DTO Pattern**: Separate API contracts from entities  
✅ **Exception Handling**: Centralized with @RestControllerAdvice  
✅ **Validation**: Bean Validation with @Valid  
✅ **Repository Pattern**: Spring Data JPA auto-implementation  
✅ **Role-Based Access**: Enum for type safety  

---
