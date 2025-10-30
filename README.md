# Multi-Tenant E-commerce Application

A comprehensive e-commerce application built with Java Spring Boot and Keycloak authentication, featuring multi-tenancy support for different brands/tenants.

## Features

### Core Features
1. **User Management**
   - User registration and login with Keycloak
   - Role-based access control (Admin, Tenant, User)
   - Unique username validation

2. **Multi-Tenancy**
   - Platform admin can manage tenants (brands)
   - Tenant-specific domains and routing
   - Tenant isolation for products and users

3. **Product Management**
   - Add/update/remove products by tenant
   - Product categorization and filtering
   - Search products by name
   - Quantity management

4. **Order Management**
   - Create orders with multiple items
   - Quantity validation before order creation
   - Order history for users
   - Order status management

5. **Favorites (Bonus Feature)**
   - Mark/unmark products as favorites
   - View favorite products list
   - Tenant-specific favorites

### Technical Features
- **Security**: Keycloak integration with JWT tokens
- **Multi-tenancy**: Tenant-based routing and data isolation
- **Pagination**: Support for paginated results
- **Validation**: Comprehensive input validation
- **Error Handling**: Global exception handling
- **Testing**: Unit and integration tests
- **Database**: H2 in-memory database for development

## Architecture

### Entities
- **User**: Platform users with roles and tenant association
- **Tenant**: Brand/company entities with domain routing
- **Role**: User roles (ADMIN, TENANT, USER)
- **Product**: Products with tenant association
- **Order**: User orders with multiple items
- **OrderItem**: Individual items within orders
- **FavoriteProduct**: User's favorite products

### API Endpoints

#### Admin Endpoints (`/api/admin`)
- `POST /tenants` - Create tenant
- `GET /tenants` - Get all tenants
- `GET /tenants/{id}` - Get tenant by ID
- `PUT /tenants/{id}` - Update tenant
- `DELETE /tenants/{id}` - Delete tenant
- `POST /users` - Create user
- `GET /users` - Get all users
- `GET /users/{id}` - Get user by ID
- `PUT /users/{id}` - Update user
- `DELETE /users/{id}` - Delete user

#### Tenant Endpoints (`/api/tenant`)
- `POST /products` - Create product
- `GET /products` - Get tenant products
- `GET /products/{id}` - Get product by ID
- `PUT /products/{id}` - Update product
- `DELETE /products/{id}` - Delete product
- `PUT /products/{id}/quantity` - Update product quantity
- `GET /products/search` - Search products
- `GET /products/categories` - Get categories
- `GET /products/brands` - Get brands

#### User Endpoints (`/api/user`)
- `GET /products` - Get all products
- `GET /products/search` - Search products
- `GET /products/categories` - Get categories
- `GET /products/brands` - Get brands
- `POST /orders` - Create order
- `GET /orders` - Get user orders
- `GET /orders/{id}` - Get order by ID
- `PUT /orders/{id}/status` - Update order status
- `PUT /orders/{id}/cancel` - Cancel order
- `POST /favorites/{productId}` - Add to favorites
- `DELETE /favorites/{productId}` - Remove from favorites
- `GET /favorites` - Get favorite products
- `GET /favorites/check/{productId}` - Check if favorite

#### Multi-Tenant Endpoints (`/{tenant}`)
- `GET /products` - Get tenant products
- `GET /products/search` - Search tenant products
- `GET /products/categories` - Get tenant categories
- `GET /products/brands` - Get tenant brands
- `POST /orders` - Create order in tenant context
- `GET /orders` - Get user orders in tenant context
- `GET /orders/{id}` - Get order by ID in tenant context
- `POST /favorites/{productId}` - Add to tenant favorites
- `DELETE /favorites/{productId}` - Remove from tenant favorites
- `GET /favorites` - Get tenant favorite products

## Setup and Installation

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- Keycloak server (for authentication)

### Running the Application

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd ecommerce-app
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - Application: http://localhost:8080
   - H2 Console: http://localhost:8080/h2-console
   - Actuator: http://localhost:8080/actuator

### Keycloak Configuration

1. **Start Keycloak server**
2. **Create a realm** named `ecommerce-realm`
3. **Create a client** named `ecommerce-app`
4. **Create roles**: `ADMIN`, `TENANT`, `USER`
5. **Create users** and assign appropriate roles

### Database Configuration

The application uses H2 in-memory database by default. To use MySQL:

1. **Update application.yml**
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/ecommerce
       username: your_username
       password: your_password
       driver-class-name: com.mysql.cj.jdbc.Driver
   ```

2. **Add MySQL dependency** (already included in pom.xml)

## Testing

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn verify
```

### Test Coverage
The project includes comprehensive test coverage for:
- Service layer unit tests
- Controller integration tests
- Repository tests
- Exception handling tests

## API Usage Examples

### Create a Tenant
```bash
curl -X POST http://localhost:8080/api/admin/tenants \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <jwt-token>" \
  -d '{
    "name": "Nike",
    "domain": "nike",
    "description": "Nike Sports Brand",
    "isActive": true
  }'
```

### Create a Product
```bash
curl -X POST "http://localhost:8080/api/tenant/products?tenantId=1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <jwt-token>" \
  -d '{
    "name": "Air Max 90",
    "description": "Classic running shoe",
    "price": 120.00,
    "quantity": 50,
    "category": "Shoes",
    "brand": "Nike",
    "isActive": true
  }'
```

### Create an Order
```bash
curl -X POST "http://localhost:8080/api/user/orders?userId=1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <jwt-token>" \
  -d '{
    "orderItems": [
      {
        "productId": 1,
        "quantity": 2
      }
    ]
  }'
```

### Access Tenant-Specific Products
```bash
curl -X GET http://localhost:8080/nike/products \
  -H "Authorization: Bearer <jwt-token>"
```

## Security

The application implements comprehensive security measures:

- **Authentication**: Keycloak JWT token-based authentication
- **Authorization**: Role-based access control
- **Multi-tenancy**: Tenant isolation and validation
- **Input Validation**: Comprehensive validation using Bean Validation
- **Error Handling**: Secure error responses without sensitive information

## Performance Considerations

- **Pagination**: All list endpoints support pagination
- **Database Indexing**: Proper indexing on frequently queried fields
- **Lazy Loading**: JPA entities use lazy loading where appropriate
- **Caching**: Ready for Redis integration (not implemented per requirements)

## Monitoring

The application includes Spring Boot Actuator for monitoring:
- Health checks: `/actuator/health`
- Metrics: `/actuator/metrics`
- Application info: `/actuator/info`

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For support and questions, please contact the development team or create an issue in the repository.
