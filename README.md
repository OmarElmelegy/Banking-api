# Banking API

A secure RESTful API built with Spring Boot for managing bank accounts. This API provides comprehensive banking operations including user authentication, account management, deposits, withdrawals, and money transfers with Spring Security integration.

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
- Balance validation and management
- Authorization checks (users can only operate on their own accounts)
- **Admin endpoints** for privileged operations
- MySQL database for persistent data storage

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
  "accountHolderName": "John Doe",
  "balance": 1000.0
}
```

**Response:**
```json
{
  "id": 1,
  "accountHolderName": "John Doe",
  "balance": 1000.0,
  "user": {
    "id": 1,
    "username": "john_doe"
  }
}
```

**Validation:**
- Account holder name is required
- Initial balance cannot be negative
```

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
```

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
  - Columns: `id`, `account_holder_name`, `balance`, `user_id`
  - Foreign key: `user_id` references `users(id)` (Many-to-One relationship)

## Testing

Run the tests using Maven:

```bash
./mvnw test
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/bank/api/
│   │       ├── BankingApiApplication.java       # Main application class
│   │       ├── config/
│   │       │   └── SecurityConfig.java          # Spring Security configuration
│   │       ├── controller/
│   │       │   ├── AccountController.java       # REST endpoints for accounts
│   │       │   ├── AuthController.java          # REST endpoints for authentication
│   │       │   └── UserController.java          # Admin endpoints
│   │       ├── model/
│   │       │   ├── Account.java                 # Account entity
│   │       │   └── User.java                    # User entity
│   │       ├── repository/
│   │       │   ├── AccountRepository.java       # Account data access layer
│   │       │   └── UserRepository.java          # User data access layer
│   │       ├── security/
│   │       │   ├── JwtAuthenticationFilter.java # JWT filter for request authentication
│   │       │   └── JwtUtil.java                 # JWT token generation and validation
│   │       └── service/
│   │           ├── AccountService.java          # Account business logic
│   │           └── MyUserDetailsService.java    # User authentication service
│   └── resources/
│       └── application.properties               # Application configuration
└── test/
    └── java/                                    # Test classes
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

The API handles the following error scenarios:

- **Account not found** - Returns 404 error when trying to access non-existent account
- **Unauthorized account access** - Returns error when user tries to access/modify accounts they don't own
- **Invalid amount** - Returns 400 error for negative or zero amounts
- **Insufficient funds** - Returns error when withdrawal/transfer amount exceeds account balance
- **Invalid account data** - Validates account holder name and balance during creation
- **Duplicate username** - Prevents registration with existing username
- **Unauthorized access** - Returns 401 for unauthenticated requests
- **Transfer validation** - Prevents transfers to the same account
- **User not found** - Returns error when authenticated user doesn't exist in database

## Testing with Postman

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

4. **Test account operations**:
   - Create account: POST `/api/accounts`
   - Get accounts: GET `/api/accounts`
   - Deposit: POST `/api/accounts/1/deposit`
   - Withdraw: POST `/api/accounts/1/withdraw`
   - Transfer: POST `/api/accounts/transfer`

5. **Test admin operations** (requires ADMIN role):
   - Get all accounts: GET `/api/admin/accounts`

## Future Enhancements

Potential improvements for this project:

- ✅ ~~JWT token-based authentication~~ (Implemented)
- ✅ ~~Role-based access control (ADMIN, USER)~~ (Implemented)
- Add refresh token mechanism
- Add transaction history and audit logging
- Implement account types (savings, checking)
- Add interest calculation for savings accounts
- Enhanced exception handling with global error handler
- API documentation with Swagger/OpenAPI
- Rate limiting and request throttling
- Email notifications for transactions
- Multi-currency support
- Account statements generation (PDF)
- Token refresh endpoint
- Password reset functionality

## License

This project is a demo application for learning Spring Boot and Spring Security.

## Contributing

This is an educational project. Feel free to fork and experiment!
