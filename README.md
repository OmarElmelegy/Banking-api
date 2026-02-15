# Banking API

A secure RESTful API built with Spring Boot for managing bank accounts. This API provides comprehensive banking operations including user authentication, account management, deposits, withdrawals, and money transfers with Spring Security integration.

## Features

- User registration and authentication (Basic Auth)
- Spring Security with BCrypt password hashing
- Create and manage bank accounts
- Retrieve all accounts (authenticated users only)
- Deposit money into accounts
- Withdraw money from accounts
- Transfer money between accounts
- Balance validation and management
- MySQL database for persistent data storage

## Technologies Used

- **Java 21** - Programming language
- **Spring Boot 4.0.2** - Application framework
- **Spring Web MVC** - REST API development
- **Spring Data JPA** - Database interaction and ORM
- **Spring Security** - Authentication and authorization
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

This API uses **HTTP Basic Authentication**. All endpoints (except user registration) require authentication.

### Register a New User
First, create a user account:

```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "securePassword123"
  }'
```

### Authenticate Requests
For all subsequent requests, include Basic Auth credentials:

```bash
# Using curl
curl -u john_doe:securePassword123 http://localhost:8080/api/accounts

# Or using Authorization header
curl -H "Authorization: Basic am9obl9kb2U6c2VjdXJlUGFzc3dvcmQxMjM=" \
     http://localhost:8080/api/accounts
```

In **Postman**:
1. Select the "Authorization" tab
2. Choose "Basic Auth" as the type
3. Enter your username and password

## API Endpoints

### Base URLs
```
Authentication: http://localhost:8080/api/users
Banking: http://localhost:8080/api/accounts
```

### Authentication Endpoints

#### Register New User (Public)
```http
POST /api/users/register
```
Creates a new user account. **No authentication required.**

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
  "username": "john_doe"
}
```

### Account Management Endpoints

**Note:** All account endpoints require authentication.

#### 1. Get All Accounts
```http
GET /api/accounts
```
Returns a list of all bank accounts.

**Authentication:** Required

**Response Example:**
```json
[
  {
    "id": 1,
    "accountHolderName": "John Doe",
    "balance": 1000.0
  }
]
```

#### 2. Create Account
```http
POST /api/accounts
```
Creates a new bank account.

**Authentication:** Required

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
  "balance": 1000.0
}
```

#### 3. Deposit Money
```http
POST /api/accounts/{id}/deposit
```
Deposits money into a specified account.

**Authentication:** Required

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

#### 4. Withdraw Money
```http
POST /api/accounts/{id}/withdraw
```
Withdraws money from a specified account.

**Authentication:** Required

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

**Error Response (Insufficient Funds):**
```json
{
  "error": "Account not found or insufficient funds"
}
```

#### 5. Transfer Money Between Accounts
```http
POST /api/accounts/transfer
```
Transfers money from one account to another.

**Authentication:** Required

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
- Missing parameters: `"Missing required parameters"`
- Invalid amount: `"Amount must be positive"`
- Same account transfer: `"Invalid destination Id, source and destination accounts cannot be the same"`
- Account not found: `"Transfer failed: Sending Account not found"` or `"Transfer failed: Receiving Account not found"`
- Insufficient funds: `"Transfer failed: Source Account does not have enough balance"`

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

- **users** - Stores user credentials (username, hashed password)
- **accounts** - Stores bank account information (account holder name, balance)

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
│   │       │   └── AccountController.java       # REST endpoints for accounts
│   │       ├── model/
│   │       │   ├── Account.java                 # Account entity
│   │       │   └── User.java                    # User entity
│   │       ├── repository/
│   │       │   ├── AccountRepository.java       # Account data access layer
│   │       │   └── UserRepository.java          # User data access layer
│   │       └── service/
│   │           ├── AccountService.java          # Account business logic
│   │           └── MyUserDetailsService.java    # User authentication service
│   └── resources/
│       └── application.properties               # Application configuration
└── test/
    └── java/                                    # Test classes
```

## Security Features

- **Spring Security** with HTTP Basic Authentication
- **BCrypt password hashing** for secure password storage
- **Authentication required** for all account operations
- **Public registration endpoint** for new users
- Role-based access control (USER role)

## Error Handling

The API handles the following error scenarios:

- **Account not found** - Returns 404 error when trying to access non-existent account
- **Invalid amount** - Returns 400 error for negative or zero amounts
- **Insufficient funds** - Returns error when withdrawal/transfer amount exceeds account balance
- **Duplicate username** - Prevents registration with existing username
- **Unauthorized access** - Returns 401 for unauthenticated requests
- **Transfer validation** - Prevents transfers to the same account

## Testing with Postman

1. **Register a user**:
   - POST to `http://localhost:8080/api/users/register`
   - Body: `{"username": "testuser", "password": "password123"}`

2. **Configure authentication**:
   - Go to Authorization tab
   - Select "Basic Auth"
   - Enter username: `testuser`, password: `password123`

3. **Test account operations**:
   - Create account: POST `/api/accounts`
   - Get accounts: GET `/api/accounts`
   - Deposit: POST `/api/accounts/1/deposit`
   - Withdraw: POST `/api/accounts/1/withdraw`
   - Transfer: POST `/api/accounts/transfer`

## Future Enhancements

Potential improvements for this project:

- Add JWT token-based authentication
- Implement role-based access (ADMIN, USER)
- Add transaction history and audit logging
- Implement account types (savings, checking)
- Add interest calculation for savings accounts
- Enhanced exception handling with global error handler
- API documentation with Swagger/OpenAPI
- Rate limiting and request throttling
- Email notifications for transactions
- Multi-currency support
- Account statements generation (PDF)

## License

This project is a demo application for learning Spring Boot and Spring Security.

## Contributing

This is an educational project. Feel free to fork and experiment!
