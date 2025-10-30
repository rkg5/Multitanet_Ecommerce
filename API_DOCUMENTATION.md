# E-commerce API Documentation

## Base URL
- Development: `http://localhost:8080`
- Production: `https://your-domain.com`

## Authentication
All API endpoints require JWT token authentication via Keycloak. Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## Response Format
All responses follow a consistent format:
```json
{
  "data": { ... },
  "message": "Success",
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## Error Format
```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2024-01-01T00:00:00Z",
  "errors": {
    "field": "error message"
  }
}
```

## Admin Endpoints

### Tenant Management

#### Create Tenant
```http
POST /api/admin/tenants
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "Nike",
  "domain": "nike",
  "description": "Nike Sports Brand",
  "isActive": true
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Nike",
  "domain": "nike",
  "description": "Nike Sports Brand",
  "isActive": true
}
```

#### Get All Tenants
```http
GET /api/admin/tenants?page=0&size=10&sort=name,ASC
Authorization: Bearer <token>
```

#### Get Tenant by ID
```http
GET /api/admin/tenants/{id}
Authorization: Bearer <token>
```

#### Update Tenant
```http
PUT /api/admin/tenants/{id}
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "Nike Updated",
  "domain": "nike",
  "description": "Updated Nike Sports Brand",
  "isActive": true
}
```

#### Delete Tenant
```http
DELETE /api/admin/tenants/{id}
Authorization: Bearer <token>
```

### User Management

#### Create User
```http
POST /api/admin/users
Content-Type: application/json
Authorization: Bearer <token>

{
  "username": "john_doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "keycloakId": "john-keycloak-id",
  "role": "USER",
  "tenantId": 1
}
```

#### Get All Users
```http
GET /api/admin/users?page=0&size=10&sort=username,ASC
Authorization: Bearer <token>
```

#### Get Users by Tenant
```http
GET /api/admin/tenants/{tenantId}/users?page=0&size=10
Authorization: Bearer <token>
```

## Tenant Endpoints

### Product Management

#### Create Product
```http
POST /api/tenant/products?tenantId=1
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "Air Max 90",
  "description": "Classic running shoe",
  "price": 120.00,
  "quantity": 50,
  "category": "Shoes",
  "brand": "Nike",
  "isActive": true
}
```

#### Get Tenant Products
```http
GET /api/tenant/products?tenantId=1&page=0&size=10&sort=name,ASC
Authorization: Bearer <token>
```

#### Search Products
```http
GET /api/tenant/products/search?tenantId=1&name=Air&category=Shoes&page=0&size=10
Authorization: Bearer <token>
```

#### Update Product
```http
PUT /api/tenant/products/{id}?tenantId=1
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "Air Max 90 Updated",
  "description": "Updated classic running shoe",
  "price": 130.00,
  "quantity": 45,
  "category": "Shoes",
  "brand": "Nike",
  "isActive": true
}
```

#### Update Product Quantity
```http
PUT /api/tenant/products/{id}/quantity?quantity=100&tenantId=1
Authorization: Bearer <token>
```

#### Delete Product
```http
DELETE /api/tenant/products/{id}?tenantId=1
Authorization: Bearer <token>
```

#### Get Categories
```http
GET /api/tenant/products/categories?tenantId=1
Authorization: Bearer <token>
```

#### Get Brands
```http
GET /api/tenant/products/brands?tenantId=1
Authorization: Bearer <token>
```

## User Endpoints

### Product Browsing

#### Get All Products
```http
GET /api/user/products?page=0&size=10&sort=name,ASC
Authorization: Bearer <token>
```

#### Search Products
```http
GET /api/user/products/search?name=Air&category=Shoes&brand=Nike&page=0&size=10
Authorization: Bearer <token>
```

#### Get Categories
```http
GET /api/user/products/categories
Authorization: Bearer <token>
```

#### Get Brands
```http
GET /api/user/products/brands
Authorization: Bearer <token>
```

### Order Management

#### Create Order
```http
POST /api/user/orders?userId=1
Content-Type: application/json
Authorization: Bearer <token>

{
  "orderItems": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```

**Response:**
```json
{
  "id": 1,
  "orderNumber": "ORD-12345678",
  "totalQuantity": 3,
  "totalAmount": 250.00,
  "status": "PENDING",
  "userId": 1,
  "username": "john_doe",
  "createdAt": "2024-01-01T00:00:00Z",
  "orderItems": [
    {
      "id": 1,
      "productId": 1,
      "productName": "Air Max 90",
      "quantity": 2,
      "unitPrice": 120.00,
      "totalPrice": 240.00
    }
  ]
}
```

#### Get User Orders
```http
GET /api/user/orders?userId=1&page=0&size=10&sort=createdAt,DESC
Authorization: Bearer <token>
```

#### Get Order by ID
```http
GET /api/user/orders/{id}?userId=1
Authorization: Bearer <token>
```

#### Update Order Status
```http
PUT /api/user/orders/{id}/status?status=CONFIRMED&userId=1
Authorization: Bearer <token>
```

#### Cancel Order
```http
PUT /api/user/orders/{id}/cancel?userId=1
Authorization: Bearer <token>
```

### Favorites Management

#### Add to Favorites
```http
POST /api/user/favorites/{productId}?userId=1
Authorization: Bearer <token>
```

#### Remove from Favorites
```http
DELETE /api/user/favorites/{productId}?userId=1
Authorization: Bearer <token>
```

#### Get Favorite Products
```http
GET /api/user/favorites?userId=1&page=0&size=10
Authorization: Bearer <token>
```

#### Check if Favorite
```http
GET /api/user/favorites/check/{productId}?userId=1
Authorization: Bearer <token>
```

**Response:**
```json
true
```

## Multi-Tenant Endpoints

### Tenant-Specific Product Browsing

#### Get Tenant Products
```http
GET /{tenant}/products?page=0&size=10&sort=name,ASC
Authorization: Bearer <token>
```

#### Search Tenant Products
```http
GET /{tenant}/products/search?name=Air&category=Shoes&page=0&size=10
Authorization: Bearer <token>
```

#### Get Tenant Categories
```http
GET /{tenant}/products/categories
Authorization: Bearer <token>
```

#### Get Tenant Brands
```http
GET /{tenant}/products/brands
Authorization: Bearer <token>
```

### Tenant-Specific Order Management

#### Create Tenant Order
```http
POST /{tenant}/orders?userId=1
Content-Type: application/json
Authorization: Bearer <token>

{
  "orderItems": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

#### Get Tenant Orders
```http
GET /{tenant}/orders?userId=1&page=0&size=10
Authorization: Bearer <token>
```

### Tenant-Specific Favorites

#### Add to Tenant Favorites
```http
POST /{tenant}/favorites/{productId}?userId=1
Authorization: Bearer <token>
```

#### Remove from Tenant Favorites
```http
DELETE /{tenant}/favorites/{productId}?userId=1
Authorization: Bearer <token>
```

#### Get Tenant Favorite Products
```http
GET /{tenant}/favorites?userId=1&page=0&size=10
Authorization: Bearer <token>
```

## Status Codes

- `200 OK` - Request successful
- `201 Created` - Resource created successfully
- `204 No Content` - Request successful, no content returned
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `409 Conflict` - Resource already exists
- `422 Unprocessable Entity` - Validation failed
- `500 Internal Server Error` - Server error

## Pagination

All list endpoints support pagination with the following query parameters:
- `page` - Page number (0-based, default: 0)
- `size` - Page size (default: 10)
- `sort` - Sort criteria (format: `field,direction`)

Example:
```
GET /api/user/products?page=0&size=20&sort=name,ASC&sort=price,DESC
```

## Filtering and Search

### Product Search Parameters
- `name` - Search by product name (partial match)
- `category` - Filter by category (exact match)
- `brand` - Filter by brand (exact match)

### Order Status Values
- `PENDING` - Order created, awaiting confirmation
- `CONFIRMED` - Order confirmed
- `SHIPPED` - Order shipped
- `DELIVERED` - Order delivered
- `CANCELLED` - Order cancelled

## Rate Limiting

API endpoints are rate-limited to prevent abuse:
- 1000 requests per hour per user
- 100 requests per minute per IP

## Webhooks

The application supports webhooks for order status changes:
- `POST /webhooks/order-status-changed`
- Payload includes order details and new status

## SDKs and Libraries

Official SDKs are available for:
- Java
- Python
- JavaScript/Node.js
- PHP

## Support

For API support and questions:
- Email: api-support@ecommerce.com
- Documentation: https://docs.ecommerce.com
- Status Page: https://status.ecommerce.com
