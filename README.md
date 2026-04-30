# E-Commerce REST API — Lab 9 (Security & Validation)

A RESTful API backend for an e-commerce product catalog built with **Spring Boot 4.0.5**, secured with **Spring Security** (session-based authentication) and **Bean Validation**. Data is persisted to a MySQL database via Spring Data JPA and Hibernate.

**Authors:** Jules Ian C. Tomacas & Jovan P. Atencio  
**Course:** WS101 — Laboratory 9 (Securing the API with Sessions & Input Validation)

---

## Table of Contents

- [Security Architecture](#security-architecture)
- [Validation Rules](#validation-rules)
- [Setup Instructions](#setup-instructions)
- [API Endpoint Reference](#api-endpoint-reference)
- [Database Schema](#database-schema)
- [Project Structure](#project-structure)
- [Development Notes](#development-notes)

---

## Security Architecture

### Session-Based Authentication (How It Works)

This API uses **HTTP Session-Based Authentication** with Form Login — not JWT tokens.

```
┌──────────┐                    ┌──────────────┐
│  Client  │  POST /login       │  Spring Boot │
│ (Browser)│ ──────────────────>│    Server    │
│          │  username+password  │              │
│          │                    │  ┌──────────┐ │
│          │  Set-Cookie:       │  │ Security │ │
│          │ <──────────────────│  │  Filter  │ │
│          │  JSESSIONID=abc123 │  │  Chain   │ │
│          │                    │  └──────────┘ │
│          │                    │              │
│          │  GET /api/v1/orders│  ┌──────────┐ │
│          │ ──────────────────>│  │ Session  │ │
│          │  Cookie:           │  │  Store   │ │
│          │  JSESSIONID=abc123 │  │(Server)  │ │
│          │                    │  └──────────┘ │
└──────────┘                    └──────────────┘
```

1. **Login Flow**: The client sends a `POST /login` with `username` and `password` (plus a CSRF token). Spring Security verifies the credentials against the database (BCrypt hash comparison).
2. **Session Creation**: On success, the server creates an HTTP session and sends back a `JSESSIONID` cookie.
3. **Subsequent Requests**: The browser automatically includes the `JSESSIONID` cookie with every request. Spring Security reads the session to identify the user.
4. **Logout**: `POST /logout` invalidates the session and clears the cookie.
5. **CSRF Protection**: All state-changing requests (POST, PUT, DELETE, PATCH) require a valid CSRF token, delivered via a cookie named `XSRF-TOKEN`.

### Role-Based Access Control (RBAC)

| Role    | Permissions |
|---------|-------------|
| `USER`  | Browse products, place orders, view own account |
| `SELLER`| All USER permissions + create/update products |
| `ADMIN` | All SELLER permissions + delete products, view all orders, manage users |

### Password Security

- Passwords are **never stored in plain text**.
- BCrypt adaptive hashing algorithm is used via `BCryptPasswordEncoder`.
- Each hash automatically includes a unique salt.

---

## Validation Rules

Validation is enforced using **Jakarta Bean Validation** (`spring-boot-starter-validation`) with `@Valid` on controller method parameters.

### User Registration (`RegisterUserDto`)

| Field      | Constraint                         | Error Message |
|------------|------------------------------------|---------------|
| `username` | `@NotBlank`, `@Size(min=8, max=20)`, `@Pattern(alphanumeric)` | "Username must be between 8 and 20 characters" |
| `password` | `@NotBlank`, `@Size(min=8)`        | "Password must be at least 8 characters long" |
| `role`     | `@NotNull`                         | "Role is required" |

### Product Creation (`CreateProductDto`)

| Field           | Constraint                              | Error Message |
|-----------------|-----------------------------------------|---------------|
| `name`          | `@NotBlank`, `@Size(min=2, max=100)`    | "Product name must be between 2 and 100 characters" |
| `description`   | `@Size(max=500)`                        | "Description must not exceed 500 characters" |
| `price`         | `@NotNull`, `@Positive`                 | "Product price must be a positive number" |
| `category`      | `@NotBlank`                             | "Product category is required" |
| `stockQuantity` | `@NotNull`, `@PositiveOrZero`           | "Stock quantity must be non-negative" |

### Order Creation (`CreateOrderDto`)

| Field           | Constraint                     | Error Message |
|-----------------|--------------------------------|---------------|
| `customerName`  | `@NotBlank`                    | "Customer name is required" |
| `customerEmail` | `@NotBlank`, `@Email`          | "Customer email must be a valid email address" |
| `items`         | `@NotEmpty`                    | "Order must contain at least one item" |
| `items[].productId` | `@NotNull`                | "Product ID is required" |
| `items[].quantity`   | `@NotNull`, `@Positive`   | "Quantity must be a positive number" |

### Validation Error Response Format

When validation fails, the API returns a `400 Bad Request` with:

```json
{
  "timestamp": "2026-04-29T19:30:00",
  "status": 400,
  "error": "Validation Failed",
  "errors": [
    "Field 'price': Product price must be a positive number",
    "Field 'name': Product name is required"
  ]
}
```

---

## Setup Instructions

### Prerequisites
- Java 26+ (JDK)
- MySQL 8.x (running on `localhost:3306`)
- Git

### Database Setup
1. Ensure MySQL is running on `localhost:3306`
2. The application automatically creates the `ecommerce_db` database
3. Default credentials: `root` / `root` (configurable in `application.properties`)

### How to Run
1. Clone the repository:
   ```
   git clone https://github.com/julesssssssswrld/ecommerce-api-lab.git
   cd ecommerce-api-lab
   ```
2. Build the project:
   ```
   .\gradlew.bat build -x test
   ```
3. Run the application:
   ```
   .\gradlew.bat bootRun
   ```
4. The API is available at `http://localhost:8080`

---

## API Endpoint Reference

### Authentication

| Method | Endpoint              | Auth Required | Description |
|--------|-----------------------|---------------|-------------|
| POST   | `/api/v1/auth/register` | No          | Register a new user |
| POST   | `/login`              | No            | Log in (form login) |
| POST   | `/logout`             | Yes           | Log out (invalidate session) |
| GET    | `/api/v1/auth/me`     | Yes           | Get current user info |

### Products

| Method | Endpoint                    | Auth Required | Role Required      | Description |
|--------|-----------------------------|---------------|--------------------|-------------|
| GET    | `/api/v1/products`          | No            | —                  | List all products |
| GET    | `/api/v1/products/{id}`     | No            | —                  | Get product by ID |
| GET    | `/api/v1/products/filter`   | No            | —                  | Filter products |
| POST   | `/api/v1/products`          | Yes           | ADMIN or SELLER    | Create product |
| PUT    | `/api/v1/products/{id}`     | Yes           | ADMIN or SELLER    | Replace product |
| PATCH  | `/api/v1/products/{id}`     | Yes           | ADMIN or SELLER    | Update product fields |
| DELETE | `/api/v1/products/{id}`     | Yes           | ADMIN only         | Delete product |

### Orders

| Method | Endpoint              | Auth Required | Role Required | Description |
|--------|-----------------------|---------------|---------------|-------------|
| GET    | `/api/v1/orders`      | Yes           | ADMIN only    | List all orders |
| GET    | `/api/v1/orders/{id}` | Yes           | Authenticated | Get order by ID |
| POST   | `/api/v1/orders`      | Yes           | Authenticated | Create new order |

---

## Database Schema

```
┌──────────────┐     ┌──────────────────┐
│   categories │     │     products     │
├──────────────┤     ├──────────────────┤
│ id (PK)      │─1:N→│ id (PK)          │
│ name (UNIQUE)│     │ name             │
└──────────────┘     │ description      │
                     │ price            │
                     │ category         │
                     │ stock_quantity   │
                     │ image_url        │
                     │ category_id (FK) │
                     └──────────────────┘

┌──────────────┐     ┌──────────────────┐
│    orders    │     │   order_items    │
├──────────────┤     ├──────────────────┤
│ id (PK)      │─1:N→│ id (PK)          │
│ customer_name│     │ quantity         │
│ customer_email│    │ price_at_purchase│
│ total_amount │     │ order_id (FK)    │
│ order_date   │     │ product_id (FK)  │
└──────────────┘     └──────────────────┘

┌──────────────┐
│    users     │
├──────────────┤
│ id (PK)      │
│ username     │
│ password     │  ← BCrypt hashed
│ role         │  ← USER, SELLER, ADMIN
└──────────────┘
```

---

## Project Structure

```
src/main/java/com/ws101/tomacas/EcommerceApi/
├── EcommerceApiApplication.java      # Spring Boot entry point
├── config/
│   ├── SecurityConfig.java           # Security filter chain, RBAC rules
│   ├── WebConfig.java                # CORS configuration
│   └── package-info.java
├── controller/
│   ├── AuthController.java           # Registration & current user endpoints
│   ├── OrderController.java          # Order CRUD (secured)
│   ├── ProductController.java        # Product CRUD (role-based)
│   ├── GlobalExceptionHandler.java   # Validation + security error handler
│   └── package-info.java
├── dto/
│   ├── RegisterUserDto.java          # Registration request (validated)
│   ├── CreateProductDto.java         # Product creation (validated)
│   ├── UpdateProductDto.java         # Product patch (validated)
│   ├── CreateOrderDto.java           # Order creation (validated)
│   └── ProductListingEntry.java      # Lightweight product listing DTO
├── model/
│   ├── User.java                     # User entity (implements UserDetails)
│   ├── Role.java                     # USER, SELLER, ADMIN enum
│   ├── Product.java                  # Product entity
│   ├── Category.java                 # Category entity
│   ├── Order.java                    # Order entity
│   ├── OrderItem.java                # Order item entity
│   ├── ErrorResponse.java            # Standard error response model
│   └── package-info.java
├── repository/
│   ├── UserRepository.java           # User data access
│   ├── ProductRepository.java        # Product data access
│   ├── CategoryRepository.java       # Category data access
│   ├── OrderRepository.java          # Order data access
│   └── package-info.java
└── service/
    ├── CustomUserDetailsService.java  # Loads users for Spring Security
    ├── AuthService.java              # Registration logic
    ├── ProductService.java           # Product business logic
    ├── OrderService.java             # Order business logic
    └── package-info.java
```

---

## Development Notes

The core backend architecture, entity relationships, and REST controller design were developed manually by the project members. For the complex integration of Spring Security's session-based authentication with CSRF token handling and the implementation of Bean Validation with custom error responses, AI assistance was utilized for targeted guidance and code refinement.
