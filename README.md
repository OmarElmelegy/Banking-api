# Banking API

A secure RESTful API built with Spring Boot for managing bank accounts. This API provides comprehensive banking operations including user authentication, account management, deposits, withdrawals, and money transfers with Spring Security integration.

## Architecture Overview

![Banking API Architecture](/docs/Banking%20API%20Architecture.png)

The system follows a layered architecture pattern with clear separation of concerns:
- **Security Layer**: JWT authentication filter, token validation, and configuration
- **Controller Layer**: REST endpoints for authentication, accounts, and admin operations
- **Service Layer**: Business logic, validation, and authorization checks
- **Repository Layer**: Data access using Spring Data JPA
- **Model Layer**: Entity models with relationships
- **DTO Layer**: Data Transfer Objects for API responses (prevents circular references)
- **Mapper Layer**: Converts entities to DTOs for safe serialization
- **Database**: MySQL for persistent storage

## Features

- **JWT-based authentication** (stateless, token-based)
- User registration and login with JWT token generation
- Spring Security with BCrypt password hashing
- **Role-based access control** (USER and ADMIN roles)
- User-Account relationship (users own their accounts)
- Create and manage bank accounts (user-specific)
- Retrieve all user's accounts (authenticated users only)
- Deposit money into owned accounts
- Withdraw money from owned accounts
- Transfer money between accounts (with ownership validation)
- **Transaction tracking and history** for all financial operations
- **Balance snapshots** - Each transaction stores account balances at time of transaction
- Transaction logging for deposits, withdrawals, and transfers
- Retrieve transaction history for any account
- **DTOs and Mappers** - Prevents circular reference issues in JSON serialization
- Balance validation and management
- Authorization checks (users can only operate on their own accounts)
- **Admin endpoints** for privileged operations
- MySQL database for persistent data storage
- **BigDecimal precision** for accurate monetary calculations
- **Paginated transaction history** - configurable page size via query parameters
- **Global exception handler** (`@RestControllerAdvice`) with structured JSON error responses
- **Swagger/OpenAPI documentation** - interactive API explorer at `/swagger-ui/index.html`
- **Account types** (CHECKING, SAVINGS) with `AccountType` enum stored as string in DB
- **Interest rate field** on accounts (default 0, extendable for savings logic)
- **Input validation** on `AccountRequestDTO` with `@Valid` + JSR-303 annotations

## Technologies Used

- **Java 21** - Programming language
- **Spring Boot 4.0.2** - Application framework
- **Spring Web MVC** - REST API development
- **Spring Data JPA** - Database interaction and ORM
- **Spring Security** - Authentication and authorization
- **JWT (JSON Web Tokens)** - Stateless authentication (jjwt 0.11.5)
- **MySQL 8.0+** - Relational database
- **BCrypt** - Password hashing
- **Maven** - Dependency management and build tool
- **springdoc-openapi 2.3.0** - Swagger/OpenAPI documentation

## Prerequisites

Before running this application, make sure you have the following installed:

- Java 21 or higher
- Maven 3.6+ (or use the included Maven wrapper)
- MySQL 8.0 or higher

## Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd banking-api
```

2. Set up MySQL database:
```sql
CREATE DATABASE banking_db;
CREATE USER 'bank_user'@'localhost' IDENTIFIED BY '147258369';
GRANT ALL PRIVILEGES ON banking_db.* TO 'bank_user'@'localhost';
FLUSH PRIVILEGES;
```

3. Configure database connection (if different from defaults):
   - Edit `src/main/resources/application.properties`
   - Update the following properties with your MySQL credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/banking_db
   spring.datasource.username=bank_user
   spring.datasource.password=147258369
   ```

4. Build the project:
```bash
./mvnw clean install
```

## Running the Application

You can run the application using the Maven wrapper:

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## Authentication

This API uses **JWT (JSON Web Token) authentication**. The authentication flow is:

1. Register a new user
2. Login to receive a JWT token
3. Include the JWT token in the Authorization header for all subsequent requests

### 1. Register a New User
First, create a user account:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "securePassword123"
  }'
```

**Response:**
```json
{
  "id": 1,
  "username": "john_doe",
  "role": "ROLE_USER"
}
```

### 2. Login to Get JWT Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "securePassword123"
  }'
```

**Response:**
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huX2RvZSIsImlhdCI6MTcwOTg...
```

### 3. Use JWT Token for Authenticated Requests
Include the token in the Authorization header with "Bearer " prefix:

```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
     http://localhost:8080/api/accounts
```

In **Postman**:
1. Select the "Authorization" tab
2. Choose "Bearer Token" as the type
3. Paste your JWT token

## API Endpoints

### Base URLs
```
Authentication: http://localhost:8080/api/auth
Banking: http://localhost:8080/api/accounts
Admin: http://localhost:8080/api/admin
```

### Authentication Endpoints

#### Register New User (Public)
```http
POST /api/auth/register
```
Creates a new user account with USER role. **No authentication required.**

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "id": 1,
  "username": "john_doe",
  "role": "ROLE_USER"
}
```

#### Login (Public)
```http
POST /api/auth/login
```
Authenticates user and returns a JWT token. **No authentication required.**

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "securePassword123"
}
```

**Response:**
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huX2RvZSIsImlhdCI6MTcwOTgzMjQwMCwiZXhwIjoxNzA5ODY4NDAwfQ...
```

**Token Details:**
- Algorithm: HS256
- Expiration: 10 hours from issuance
- Subject: Username

### Account Management Endpoints

**Note:** All account endpoints require authentication.

#### 1. Get All Accounts
```http
GET /api/accounts
```
Returns a list of all bank accounts **owned by the authenticated user**.

**Authentication:** Required

**Authorization:** Returns only accounts belonging to the authenticated user

**Response Example:**
```json
[
  {
    "id": 1,
    "accountHolderName": "John Doe",
    "balance": 1000.0,
    "user": {
      "id": 1,
      "username": "john_doe"
    }
  }
]
```

#### 2. Create Account
```http
POST /api/accounts
```
Creates a new bank account **for the authenticated user**.

**Authentication:** Required

**Authorization:** Account is automatically linked to the authenticated user

**Request Body:**
```json
{
  "ownerName": "John Doe",
  "accountType": "CHECKING",
  "initialBalance": 1000.0
}
```

**Response:**
```json
{
  "id": 1,
  "accountHolderName": "John Doe",
  "balance": 1000.0,
  "type": "CHECKING",
  "user": {
    "id": 1,
    "username": "john_doe"
  }
}
```

**Validation (enforced via `@Valid`):**
- `ownerName` — required, non-blank
- `accountType` — required (`CHECKING` or `SAVINGS`)
- `initialBalance` — required, must be ≥ 0

#### 3. Deposit Money
```http
POST /api/accounts/{id}/deposit
```
Deposits money into a specified account.

**Authentication:** Required

**Authorization:** Only the account owner can deposit money

**Path Parameter:**
- `id` - Account ID

**Request Body:**
```json
{
  "amount": 500.0
}
```

**Response:**
```json
{
  "id": 1,
  "accountHolderName": "John Doe",
  "balance": 1500.0
}
```

**Error Response (Unauthorized):**
```json
{
  "error": "You do not own this account"
}
```

#### 4. Withdraw Money
```http
POST /api/accounts/{id}/withdraw
```
Withdraws money from a specified account.

**Authentication:** Required

**Authorization:** Only the account owner can withdraw money

**Path Parameter:**
- `id` - Account ID

**Request Body:**
```json
{
  "amount": 200.0
}
```

**Response:**
```json
{
  "id": 1,
  "accountHolderName": "John Doe",
  "balance": 1300.0
}
```

**Error Responses:**
- Unauthorized: `"You do not own this account"`
- Insufficient funds: `"Insufficient funds"`
- Invalid amount: `"Invalid amount"`
- Account not found: `"Account not found or insufficient funds"`


#### 5. Transfer Money Between Accounts

```http
POST /api/accounts/transfer
```
Transfers money from one account to another.

**Authentication:** Required

**Authorization:** Only the source account owner can initiate a transfer

**Request Body:**
```json
{
  "fromId": 1,
  "toId": 2,
  "amount": 300.0
}
```

**Success Response:**
```
Transfer successful
```

**Error Responses:**
- Unauthorized: `"You do not own this account"`
- Missing parameters: `"Missing required parameters"`
- Invalid amount: `"Amount must be positive"`
- Same account transfer: `"Invalid destination Id, source and destination accounts cannot be the same"`
- Account not found: `"Transfer failed: Sending Account not found"` or `"Transfer failed: Receiving Account not found"`
- Insufficient funds: `"Transfer failed: Source Account does not have enough balance"`

#### 6. Get Transaction History
```http
GET /api/accounts/{id}/transactions?page=0&size=20
```
Retrieves paginated transactions associated with a specific account (as source or target), ordered by most recent first.

**Authentication:** Required

**Authorization:** Only the account owner can view transaction history

**Path Parameter:**
- `id` - Account ID

**Query Parameters:**
- `page` - Page number (0-based, default: `0`)
- `size` - Page size (default: `20`)

**Response Example:**
```json
[
  {
    "id": 1,
    "amount": 500.00,
    "type": "DEPOSIT",
    "timestamp": "2026-02-18T10:30:00",
    "sourceHistoricalBalance": null,
    "targetHistoricalBalance": 1500.00,
    "sourceAccount": null,
    "targetAccount": {
      "id": 1,
      "accountHolderName": "John Doe",
      "user": {
        "id": 1,
        "username": "john_doe"
      }
    },
    "initiator": {
      "id": 1,
      "username": "john_doe"
    }
  },
  {
    "id": 2,
    "amount": 200.00,
    "type": "WITHDRAWAL",
    "timestamp": "2026-02-18T11:15:00",
    "sourceHistoricalBalance": 1300.00,
    "targetHistoricalBalance": null,
    "sourceAccount": {
      "id": 1,
      "accountHolderName": "John Doe",
      "user": {
        "id": 1,
        "username": "john_doe"
      }
    },
    "targetAccount": null,
    "initiator": {
      "id": 1,
      "username": "john_doe"
    }
  },
  {
    "id": 3,
    "amount": 300.00,
    "type": "TRANSFER",
    "timestamp": "2026-02-18T12:00:00",
    "sourceHistoricalBalance": 1000.00,
    "targetHistoricalBalance": 2800.00,
    "sourceAccount": {
      "id": 1,
      "accountHolderName": "John Doe",
      "user": {
        "id": 1,
        "username": "john_doe"
      }
    },
    "targetAccount": {
      "id": 2,
      "accountHolderName": "Jane Smith",
      "user": {
        "id": 2,
        "username": "jane_smith"
      }
    },
    "initiator": {
      "id": 1,
      "username": "john_doe"
    }
  }
]
```

**Response Fields:**
- `sourceHistoricalBalance` - Balance of source account after transaction (null for deposits)
- `targetHistoricalBalance` - Balance of target account after transaction (null for withdrawals)
- Account and user data returned as summary DTOs (prevents circular references)

**Transaction Types:**
- `DEPOSIT` - Money added to account (no source account)
- `WITHDRAWAL` - Money removed from account (no target account)
- `TRANSFER` - Money moved between accounts (both source and target present)

### Admin Endpoints

**Note:** All admin endpoints require ADMIN role.

#### Get All Accounts (Admin Only)
```http
GET /api/admin/accounts
```
Returns all bank accounts in the system (not just the authenticated user's accounts).

**Authentication:** Required (JWT)

**Authorization:** ADMIN role required

**Response Example:**
```json
[
  {
    "id": 1,
    "accountHolderName": "John Doe",
    "balance": 1000.0,
    "user": {
      "id": 1,
      "username": "john_doe",
      "role": "ROLE_USER"
    }
  },
  {
    "id": 2,
    "accountHolderName": "Jane Smith",
    "balance": 2500.0,
    "user": {
      "id": 2,
      "username": "jane_smith",
      "role": "ROLE_USER"
    }
  }
]
```

**Error Response (Forbidden):**
```json
{
  "error": "Access Denied"
}
```

#### Get All Users (Admin Only)
```http
GET /api/admin/users
```
Returns all users in the system.

**Authentication:** Required (JWT)

**Authorization:** ADMIN role required

**Response Example:**
```json
[
  {
    "id": 1,
    "username": "john_doe",
    "password": "$2a$10$...", 
    "role": "ROLE_USER"
  },
  {
    "id": 2,
    "username": "jane_smith",
    "password": "$2a$10$...",
    "role": "ROLE_USER"
  },
  {
    "id": 3,
    "username": "admin",
    "password": "$2a$10$...",
    "role": "ROLE_ADMIN"
  }
]
```

**Note:** Passwords are returned as BCrypt hashes (cannot be reversed).

**Error Response (Forbidden):**
```json
{
  "error": "Access Denied"
}
```

## Database Configuration

The application uses **MySQL** for persistent data storage.

### Configuration
Database settings are located in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/banking_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=bank_user
spring.datasource.password=147258369
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

### Schema
Tables are automatically created by Hibernate based on JPA entities:

- **users** - Stores user credentials and roles
  - Columns: `id`, `username` (unique), `password` (BCrypt hashed), `role`
- **accounts** - Stores bank account information
  - Columns: `id`, `owner_name`, `balance`, `user_id`, `account_type` (CHECKING/SAVINGS), `interest_rate`
  - Foreign key: `user_id` references `users(id)` (Many-to-One relationship)
- **transactions** - Stores transaction history for all operations
  - Columns: `id`, `amount` (BigDecimal), `type` (DEPOSIT/WITHDRAWAL/TRANSFER), `timestamp`, `source_account_id`, `target_account_id`, `source_balance_after`, `target_balance_after`, `initiator_id`
  - Foreign keys: 
    - `source_account_id` references `accounts(id)` (Many-to-One)
    - `target_account_id` references `accounts(id)` (Many-to-One)
    - `initiator_id` references `users(id)` (Many-to-One, required)
  - Balance snapshots: `source_balance_after` and `target_balance_after` store account balances at time of transaction

## Swagger / OpenAPI

The API is documented with Swagger UI. Once the application is running, open:

```
http://localhost:8080/swagger-ui/index.html
```

The interactive explorer allows you to authenticate with a Bearer token and test all endpoints directly from the browser. The raw OpenAPI spec is available at:

```
http://localhost:8080/v3/api-docs
```

## Project Structure

```
src/main/
├── java/
│   └── com/
│       ├── bank/
│       │   └── exception/
│       │       ├── GlobalExceptionHandler.java      # @RestControllerAdvice – structured error responses
│       │       ├── InsufficientFundsException.java
│       │       ├── InvalidArgumentException.java
│       │       ├── ResourceNotFoundException.java
│       │       └── UnauthorizedActionException.java
│       └── bank/api/
│           ├── BankingApiApplication.java           # Main application class
│           ├── config/
│           │   ├── OpenApiConfig.java               # Swagger / OpenAPI configuration
│           │   └── SecurityConfig.java              # Spring Security configuration
│           ├── controller/
│           │   ├── AccountController.java           # REST endpoints for accounts
│           │   ├── AuthController.java              # REST endpoints for authentication
│           │   └── UserController.java              # Admin-only endpoints
│           ├── dto/
│           │   ├── AccountRequestDTO.java           # Account creation request DTO (validated)
│           │   ├── AccountResponseDTO.java          # Account response DTO
│           │   ├── AccountSummaryDTO.java           # Account summary DTO
│           │   ├── DepositRequestDTO.java           # Deposit request DTO
│           │   ├── ErrorResponseDTO.java            # Structured error response DTO
│           │   ├── TransactionResponseDTO.java      # Transaction response DTO
│           │   ├── TransferRequestDTO.java          # Transfer request DTO
│           │   ├── UserSummaryDTO.java              # User summary DTO
│           │   └── WithdrawRequestDTO.java          # Withdraw request DTO
│           ├── mapper/
│           │   ├── AccountResponseMapper.java       # Account → DTO mapper
│           │   ├── TransactionMapper.java           # Transaction → DTO mapper
│           │   └── UserMapper.java                  # User → DTO mapper
│           ├── model/
│           │   ├── Account.java                     # Account entity
│           │   ├── Transaction.java                 # Transaction entity
│           │   └── User.java                        # User entity
│           ├── repository/
│           │   ├── AccountRepository.java           # Account data access layer
│           │   ├── TransactionRepository.java       # Transaction data access layer
│           │   └── UserRepository.java              # User data access layer
│           ├── security/
│           │   ├── JwtAuthenticationFilter.java     # JWT filter for request authentication
│           │   └── JwtUtil.java                     # JWT token generation and validation
│           └── service/
│               ├── AccountService.java              # Account business logic
│               ├── MyUserDetailsService.java        # UserDetailsService for Spring Security
│               └── UserService.java                 # User management service
└── resources/
    └── application.properties                       # Application configuration
```

## Security Features

- **JWT (JSON Web Token) authentication** - Stateless authentication mechanism
- **Spring Security** with custom JWT filter
- **BCrypt password hashing** for secure password storage
- **Stateless session management** - No server-side sessions
- **Token expiration** - JWT tokens valid for 10 hours
- **Role-based access control (RBAC)** - USER and ADMIN roles
- **Method-level security** with `@PreAuthorize` annotations
- **Authentication required** for all account operations
- **Authorization checks** - users can only access and modify their own accounts
- **Ownership validation** for deposits, withdrawals, and transfers
- **Public endpoints** for registration and login
- **User-Account relationship** - accounts are linked to users via foreign key
- **Admin-only endpoints** protected by role verification

## Error Handling

All errors are handled by a global `@RestControllerAdvice` and return a consistent JSON structure:

```json
{
  "timestamp": "2026-03-02T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Account not found",
  "path": "/api/accounts/99"
}
```

| Scenario | HTTP Status |
|---|---|
| Account / user not found | `404 Not Found` |
| Invalid amount or missing fields | `400 Bad Request` |
| Insufficient funds | `400 Bad Request` |
| User does not own the account | `403 Forbidden` |
| Unauthenticated request | `401 Unauthorized` |
| Duplicate username on register | `500 Internal Server Error` |
| Transfer to same account | `400 Bad Request` |

## Quick Start with Postman

1. **Register a user**:
   - POST to `http://localhost:8080/api/auth/register`
   - Body: `{"username": "testuser", "password": "password123"}`

2. **Login to get JWT token**:
   - POST to `http://localhost:8080/api/auth/login`
   - Body: `{"username": "testuser", "password": "password123"}`
   - Copy the returned token

3. **Configure authentication**:
   - Go to Authorization tab
   - Select "Bearer Token"
   - Paste the JWT token

4. **Use account operations**:
   - Create account: POST `/api/accounts`
   - Get accounts: GET `/api/accounts`
   - Deposit: POST `/api/accounts/1/deposit`
   - Withdraw: POST `/api/accounts/1/withdraw`
   - Transfer: POST `/api/accounts/transfer`
   - Transaction history: GET `/api/accounts/1/transactions?page=0&size=20`

5. **Admin operations** (requires ADMIN role):
   - Get all accounts: GET `/api/admin/accounts`
   - Get all users: GET `/api/admin/users`

Alternatively, use the built-in **Swagger UI** at `http://localhost:8080/swagger-ui/index.html` — authenticate with your JWT token via the "Authorize" button.

## Future Enhancements

Potential improvements for this project:

- ✅ ~~JWT token-based authentication~~ (Implemented)
- ✅ ~~Role-based access control (ADMIN, USER)~~ (Implemented)
- ✅ ~~Transaction history and audit logging~~ (Implemented)
- ✅ ~~DTOs and Mappers for clean API responses~~ (Implemented)
- ✅ ~~Balance snapshots in transaction history~~ (Implemented)
- ✅ ~~Implement pagination for transaction history~~ (Implemented)
- ✅ ~~Enhanced exception handling with global error handler (@ControllerAdvice)~~ (Implemented)
- ✅ ~~API documentation with Swagger/OpenAPI~~ (Implemented)
- ✅ ~~Implement account types (savings, checking)~~ (Implemented — `CHECKING` / `SAVINGS` enum)
- ✅ ~~Input validation on request DTOs~~ (Implemented — `@Valid` + JSR-303)
- Add refresh token mechanism
- Add interest calculation for savings accounts
- Rate limiting and request throttling
- Email notifications for transactions
- Multi-currency support
- Account statements generation (PDF)
- Token refresh endpoint
- Password reset functionality
- Transaction search and filtering
- Scheduled jobs for interest calculations

## License

This project is a demo application for learning Spring Boot and Spring Security.

## Contributing

This is an educational project. Feel free to fork and experiment!
