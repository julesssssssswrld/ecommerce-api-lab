# EcommerceApi — Spring Boot RESTful API

A RESTful API backend for an e-commerce product catalog built with **Spring Boot 4.0.5**, persisting data to a **MySQL** database via **Spring Data JPA** and **Hibernate**.

> **Authors:** Jules Ian C. Tomacas & Jovan P. Atencio  
> **Course:** WS101 — Laboratory 8 (Database Integration & Fetch API)

---

## Table of Contents

- [Project Overview](#project-overview)
- [Database Schema](#database-schema)
- [Setup Instructions](#setup-instructions)
- [API Endpoint Reference](#api-endpoint-reference)
- [Sample Requests & Responses](#sample-requests--responses)
- [HTTP Status Codes](#http-status-codes)
- [Project Structure](#project-structure)
- [Known Limitations](#known-limitations)

---

## Project Overview

This project implements a RESTful API for managing an e-commerce product catalog. It demonstrates:

- **HTTP Methods**: GET, POST, PUT, PATCH, DELETE
- **REST Principles**: Resource-based URLs, proper status codes, JSON responses
- **Database Persistence**: Products, categories, orders, and order items stored in MySQL via Spring Data JPA
- **Entity Relationships**: One-to-Many (Category → Product, Order → OrderItem), Many-to-One (OrderItem → Product)
- **Repository Pattern**: Spring Data JPA repositories with method naming queries and custom JPQL
- **Input Validation**: Server-side validation for all create/update operations
- **Error Handling**: Global exception handler covering EntityNotFoundException, DataIntegrityViolationException, and more
- **CORS Configuration**: Frontend (Live Server) can communicate with the backend via WebMvcConfigurer

---

## Database Schema

The application uses four tables with the following relationships:

```
┌──────────────┐       ┌──────────────────┐
│  categories  │       │     products     │
├──────────────┤       ├──────────────────┤
│ id (PK)      │──1:N─→│ id (PK)          │
│ name (UNIQUE)│       │ name             │
└──────────────┘       │ description      │
                       │ price            │
                       │ category         │
                       │ stock_quantity   │
                       │ image_url        │
                       │ category_id (FK) │
                       └──────────────────┘
                              │
┌──────────────┐              │ N:1
│    orders    │       ┌──────────────────┐
├──────────────┤       │   order_items    │
│ id (PK)      │──1:N─→├──────────────────┤
│ customer_name│       │ id (PK)          │
│ customer_email│      │ quantity         │
│ total_amount │       │ price_at_purchase│
│ order_date   │       │ order_id (FK)    │
└──────────────┘       │ product_id (FK)  │
                       └──────────────────┘
```

### Relationships

| Relationship | Parent | Child | Type | Description |
|---|---|---|---|---|
| Category → Product | `categories` | `products` | One-to-Many | One category has many products |
| Order → OrderItem | `orders` | `order_items` | One-to-Many | One order has many items |
| OrderItem → Product | `order_items` | `products` | Many-to-One | Many items reference one product |

### JPA Annotations Used

- `@Entity`, `@Table` — Maps classes to database tables
- `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)` — Auto-increment primary keys
- `@OneToMany(mappedBy = ...)` — Inverse side of the relationship
- `@ManyToOne(fetch = FetchType.LAZY)` — Owning side with lazy loading
- `@JoinColumn(name = ...)` — Specifies the foreign key column
- `CascadeType.ALL` — Propagates save/delete from parent to children
- `orphanRemoval = true` — Auto-deletes children removed from parent's list

---

## Setup Instructions

### Prerequisites

- **Java 26+** (JDK)
- **MySQL 8.x** (running on localhost:3306)
- **Git**

### Database Setup

1. Ensure MySQL is running on `localhost:3306`
2. The application will automatically create the `ecommerce_db` database (via `createDatabaseIfNotExist=true`)
3. Default credentials: `root` / `root` (configurable in `application.properties`)

### How to Run

1. **Clone the repository:**

   ```bash
   git clone https://github.com/julesssssssswrld/ecommerce-api-lab.git
   cd ecommerce-api-lab
   ```

2. **Build the project** (Gradle wrapper is included, no need to install Gradle):

   ```bash
   ./gradlew build -x test
   ```

   On Windows:

   ```bash
   .\gradlew.bat build -x test
   ```

3. **Run the application:**

   ```bash
   ./gradlew bootRun
   ```

4. The API will be available at `http://localhost:8080`
5. On first startup, the database is automatically seeded with 10 sample products.

---

## API Endpoint Reference

| Method   | Path                             | Description                     | Request Body | Status Code |
|----------|----------------------------------|---------------------------------|:------------:|:-----------:|
| `GET`    | `/api/v1/products`               | Retrieve all products           | —            | 200         |
| `GET`    | `/api/v1/products/{id}`          | Retrieve a product by ID        | —            | 200 / 404   |
| `GET`    | `/api/v1/products/filter`        | Filter products by criteria     | —            | 200 / 400   |
| `POST`   | `/api/v1/products`               | Create a new product            | JSON         | 201 / 400   |
| `PUT`    | `/api/v1/products/{id}`          | Replace an entire product       | JSON         | 200 / 404   |
| `PATCH`  | `/api/v1/products/{id}`          | Partially update a product      | JSON         | 200 / 404   |
| `DELETE` | `/api/v1/products/{id}`          | Delete a product                | —            | 204 / 404   |

### Filter Parameters

The filter endpoint accepts two query parameters:

| Parameter     | Description                                               |
|---------------|-----------------------------------------------------------|
| `filterType`  | The criteria to filter by: `category`, `name`, or `price` |
| `filterValue` | The value to match (e.g., `Electronics`, `Keyboard`, `2000`) |

**Example:** `GET /api/v1/products/filter?filterType=category&filterValue=Electronics`

---

## Sample Requests & Responses

### GET All Products

```
GET /api/v1/products
```

**Response (200 OK):**

```json
[
  {
    "id": 1,
    "name": "Wireless Bluetooth Headphones",
    "description": "Over-ear noise-cancelling headphones with 30-hour battery life.",
    "price": 2499.0,
    "category": "Electronics",
    "stockQuantity": 45,
    "imageUrl": "https://placehold.co/400x400?text=Headphones"
  },
  ...
]
```

### GET Product by ID

```
GET /api/v1/products/1
```

**Response (200 OK):**

```json
{
  "id": 1,
  "name": "Wireless Bluetooth Headphones",
  "description": "Over-ear noise-cancelling headphones with 30-hour battery life.",
  "price": 2499.0,
  "category": "Electronics",
  "stockQuantity": 45,
  "imageUrl": "https://placehold.co/400x400?text=Headphones"
}
```

### POST Create Product

```
POST /api/v1/products
Content-Type: application/json
```

```json
{
  "name": "Gaming Mouse",
  "description": "RGB gaming mouse with 16000 DPI sensor.",
  "price": 2499.00,
  "category": "Electronics",
  "stockQuantity": 80,
  "imageUrl": "https://placehold.co/400x400?text=Mouse"
}
```

**Response (201 Created):**

```json
{
  "id": 11,
  "name": "Gaming Mouse",
  "description": "RGB gaming mouse with 16000 DPI sensor.",
  "price": 2499.0,
  "category": "Electronics",
  "stockQuantity": 80,
  "imageUrl": "https://placehold.co/400x400?text=Mouse"
}
```

### PUT Update Product

```
PUT /api/v1/products/11
Content-Type: application/json
```

```json
{
  "name": "Updated Gaming Mouse",
  "description": "Wireless RGB gaming mouse with 25000 DPI sensor.",
  "price": 3299.00,
  "category": "Electronics",
  "stockQuantity": 50,
  "imageUrl": "https://placehold.co/400x400?text=MouseV2"
}
```

**Response (200 OK):** Returns the updated product object.

### PATCH Partial Update

```
PATCH /api/v1/products/5
Content-Type: application/json
```

```json
{
  "price": 1399.00,
  "stockQuantity": 70
}
```

**Response (200 OK):** Returns the product with only the specified fields updated.

### DELETE Product

```
DELETE /api/v1/products/10
```

**Response:** `204 No Content` (empty body)

### Filter by Category

```
GET /api/v1/products/filter?filterType=category&filterValue=Electronics
```

**Response (200 OK):** Returns an array of products in the "Electronics" category.

### Error Response Example

```
GET /api/v1/products/999
```

**Response (404 Not Found):**

```json
{
  "timestamp": "2026-04-28T15:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product with ID 999 was not found."
}
```

---

## HTTP Status Codes

| Code | Meaning                | When It's Used                               |
|------|------------------------|----------------------------------------------|
| 200  | OK                     | Successful GET, PUT, PATCH                    |
| 201  | Created                | Successful POST (new product created)         |
| 204  | No Content             | Successful DELETE                             |
| 400  | Bad Request            | Invalid input, missing fields, bad JSON, DB constraint violation |
| 404  | Not Found              | Product with given ID does not exist          |
| 500  | Internal Server Error  | Unexpected server-side error                  |

---

## Project Structure

```
src/main/java/com/ws101/tomacas/EcommerceApi/
├── EcommerceApiApplication.java       # Spring Boot entry point
├── model/
│   ├── Product.java                   # Product entity (JPA)
│   ├── Category.java                  # Category entity (One-to-Many with Product)
│   ├── Order.java                     # Order entity (One-to-Many with OrderItem)
│   ├── OrderItem.java                 # OrderItem entity (Many-to-One with Order & Product)
│   └── ErrorResponse.java            # Standard error response format
├── repository/
│   ├── ProductRepository.java         # JPA repository with custom queries
│   └── CategoryRepository.java        # JPA repository for categories
├── service/
│   └── ProductService.java           # Business logic (backed by JPA repository)
├── controller/
│   ├── ProductController.java        # REST API endpoints
│   └── GlobalExceptionHandler.java   # Centralized error handling (incl. DB exceptions)
└── config/
    └── WebConfig.java                 # CORS configuration for frontend integration
```

---

## Known Limitations

- **No Authentication**: The API does not implement any authentication or authorization.
- **No Pagination**: The GET all products endpoint returns every product at once. For large datasets, pagination would be needed.
- **Order API**: The Order and OrderItem entities are defined for schema demonstration purposes. Full CRUD endpoints for orders are not yet implemented.

---

## Development Notes

The core architecture, API endpoints, fundamental controllers, and initial service logic were developed manually by the project members. To ensure adherence to enterprise best practices and to handle the highly complex technical requirements—such as intricate Spring Data JPA relationship mapping, cascade persistence, and advanced CORS configuration—AI assistance was utilized for targeted technical guidance and boilerplate generation.
