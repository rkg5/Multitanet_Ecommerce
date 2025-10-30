# E-commerce Application Project Structure

## Overview
This is a comprehensive multi-tenant e-commerce application built with Java Spring Boot and Keycloak authentication.

## Project Structure

```
ecommerce-app/
├── src/
│   ├── main/
│   │   ├── java/com/ecommerce/
│   │   │   ├── EcommerceAppApplication.java          # Main application class
│   │   │   ├── config/                               # Configuration classes
│   │   │   │   ├── SecurityConfig.java              # Security configuration
│   │   │   │   ├── KeycloakConfig.java              # Keycloak configuration
│   │   │   │   ├── TenantContext.java               # Tenant context holder
│   │   │   │   ├── TenantInterceptor.java           # Multi-tenancy interceptor
│   │   │   │   └── WebConfig.java                   # Web configuration
│   │   │   ├── controller/                          # REST controllers
│   │   │   │   ├── AdminController.java             # Admin endpoints
│   │   │   │   ├── TenantController.java            # Tenant management endpoints
│   │   │   │   ├── UserController.java              # User endpoints
│   │   │   │   └── MultiTenantController.java       # Multi-tenant endpoints
│   │   │   ├── dto/                                 # Data Transfer Objects
│   │   │   │   ├── UserDto.java
│   │   │   │   ├── TenantDto.java
│   │   │   │   ├── ProductDto.java
│   │   │   │   ├── OrderDto.java
│   │   │   │   ├── OrderItemDto.java
│   │   │   │   ├── OrderRequestDto.java
│   │   │   │   └── ProductSearchDto.java
│   │   │   ├── entity/                              # JPA entities
│   │   │   │   ├── BaseEntity.java                  # Base entity with audit fields
│   │   │   │   ├── User.java
│   │   │   │   ├── Tenant.java
│   │   │   │   ├── Role.java
│   │   │   │   ├── Product.java
│   │   │   │   ├── Order.java
│   │   │   │   ├── OrderItem.java
│   │   │   │   └── FavoriteProduct.java
│   │   │   ├── exception/                           # Exception handling
│   │   │   │   ├── GlobalExceptionHandler.java      # Global exception handler
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── ValidationException.java
│   │   │   │   └── InsufficientQuantityException.java
│   │   │   ├── repository/                          # JPA repositories
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── TenantRepository.java
│   │   │   │   ├── ProductRepository.java
│   │   │   │   ├── OrderRepository.java
│   │   │   │   ├── OrderItemRepository.java
│   │   │   │   ├── RoleRepository.java
│   │   │   │   └── FavoriteProductRepository.java
│   │   │   └── service/                             # Business logic services
│   │   │       ├── UserService.java
│   │   │       ├── TenantService.java
│   │   │       ├── ProductService.java
│   │   │       ├── OrderService.java
│   │   │       ├── FavoriteProductService.java
│   │   │       └── DataInitializationService.java   # Data seeding
│   │   └── resources/
│   │       ├── application.yml                      # Main configuration
│   │       ├── application-docker.yml              # Docker configuration
│   │       └── logback-spring.xml                  # Logging configuration
│   └── test/
│       ├── java/com/ecommerce/
│       │   ├── EcommerceAppApplicationTests.java   # Application context test
│       │   ├── service/                            # Service unit tests
│       │   │   ├── UserServiceTest.java
│       │   │   ├── ProductServiceTest.java
│       │   │   ├── OrderServiceTest.java
│       │   │   └── FavoriteProductServiceTest.java
│       │   └── controller/                         # Controller integration tests
│       │       ├── AdminControllerTest.java
│       │       └── UserControllerTest.java
│       └── resources/
│           └── application-test.yml                # Test configuration
├── target/                                         # Maven build output
├── pom.xml                                         # Maven configuration
├── Dockerfile                                      # Docker configuration
├── docker-compose.yml                             # Docker Compose setup
├── README.md                                       # Project documentation
├── API_DOCUMENTATION.md                           # API documentation
└── PROJECT_STRUCTURE.md                           # This file
```

## Key Components

### 1. Security & Authentication
- **Keycloak Integration**: JWT-based authentication
- **Role-Based Access Control**: ADMIN, TENANT, USER roles
- **Multi-tenancy Security**: Tenant isolation and validation

### 2. Multi-Tenancy
- **Tenant Context**: Thread-local tenant management
- **Tenant Interceptor**: URL-based tenant resolution
- **Tenant Isolation**: Data and routing isolation

### 3. Data Layer
- **JPA Entities**: Well-designed entity relationships
- **Repositories**: Custom query methods for complex operations
- **Database**: H2 (development) / MySQL (production)

### 4. Business Logic
- **Service Layer**: Comprehensive business logic
- **Validation**: Input validation and business rules
- **Error Handling**: Global exception handling

### 5. API Layer
- **REST Controllers**: Well-structured API endpoints
- **DTOs**: Clean data transfer objects
- **Pagination**: Built-in pagination support

### 6. Testing
- **Unit Tests**: Service layer testing
- **Integration Tests**: Controller testing
- **Test Configuration**: Separate test profiles

## Features Implemented

### ✅ Core Requirements
1. **User Management**: Sign up, login, unique usernames
2. **Admin Functions**: Tenant and user management
3. **Tenant Management**: Product management per tenant
4. **Tenant Isolation**: Users cannot access other tenant domains
5. **Product Features**: Categorization, filtering, search
6. **Order Management**: Order creation with quantity validation
7. **Order History**: User order viewing
8. **Pagination**: All list endpoints support pagination
9. **Error Handling**: Comprehensive error handling
10. **Unit Tests**: Complete test coverage

### ✅ Bonus Features
- **Favorites**: Mark/unmark products as favorites
- **Multi-tenant Favorites**: Tenant-specific favorite products

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.0
- **Security**: Spring Security, Keycloak
- **Database**: H2 (dev), MySQL (prod)
- **ORM**: Spring Data JPA, Hibernate
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Build Tool**: Maven
- **Containerization**: Docker, Docker Compose

## Getting Started

1. **Prerequisites**: Java 17, Maven 3.6+, Keycloak
2. **Build**: `mvn clean install`
3. **Run**: `mvn spring-boot:run`
4. **Test**: `mvn test`
5. **Docker**: `docker-compose up`

## API Endpoints Summary

- **Admin**: `/api/admin/*` - Tenant and user management
- **Tenant**: `/api/tenant/*` - Product management
- **User**: `/api/user/*` - Product browsing and orders
- **Multi-tenant**: `/{tenant}/*` - Tenant-specific operations

## Security Model

- **Authentication**: Keycloak JWT tokens
- **Authorization**: Role-based access control
- **Multi-tenancy**: Tenant-based data isolation
- **Validation**: Input validation and business rules

This project demonstrates enterprise-level Spring Boot development with comprehensive features, security, testing, and documentation.
