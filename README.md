# EcommerceApi — Spring Boot RESTful API

A RESTful API backend for an e-commerce product catalog built with **Spring Boot 4.0.5**, using in-memory data storage via `ArrayList<Product>`.

> **Authors:** Jules Ian C. Tomacas & Jovan P. Atencio  
> **Course:** WS101 — Laboratory 7

---

## Table of Contents

- [Project Overview](#project-overview)
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
- **In-Memory Storage**: Products stored in `ArrayList<Product>` — no database required
- **Input Validation**: Server-side validation for all create/update operations
- **Error Handling**: Global exception handler with consistent error response format

---

## Setup Instructions

### Prerequisites

- **Java 26+** (JDK)
- **Git**

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
  "timestamp": "2026-04-23T15:30:00",
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
| 400  | Bad Request            | Invalid input, missing fields, bad JSON       |
| 404  | Not Found              | Product with given ID does not exist          |
| 500  | Internal Server Error  | Unexpected server-side error                  |

---

## Project Structure

```
src/main/java/com/ws101/tomacas/EcommerceApi/
├── EcommerceApiApplication.java       # Spring Boot entry point
├── model/
│   ├── Product.java                   # Product entity (7 fields)
│   └── ErrorResponse.java            # Standard error response format
├── service/
│   └── ProductService.java           # Business logic & in-memory storage
├── controller/
│   ├── ProductController.java        # REST API endpoints
│   └── GlobalExceptionHandler.java   # Centralized error handling
└── config/                            # Reserved for future configuration
```

---

## Known Limitations

- **In-Memory Storage**: All product data is stored in an `ArrayList<Product>`. Data is lost when the application restarts. There is no persistence layer or database.
- **No Authentication**: The API does not implement any authentication or authorization.
- **Single-Threaded Safety**: While the ID counter uses `AtomicLong`, the `ArrayList` itself is not synchronized. This is acceptable for this lab's scope but would need addressing in production.
- **No Pagination**: The GET all products endpoint returns every product at once. For large datasets, pagination would be needed.
