# Banking API

A RESTful API built with Spring Boot for managing bank accounts. This API provides basic banking operations including account creation, deposits, withdrawals, and balance inquiries.

## Features

- Create new bank accounts
- Retrieve all accounts
- Deposit money into accounts
- Withdraw money from accounts
- Balance validation and management
- In-memory H2 database for data persistence

## Technologies Used

- **Java 21** - Programming language
- **Spring Boot 4.0.2** - Application framework
- **Spring Web MVC** - REST API development
- **Spring Data JPA** - Database interaction and ORM
- **H2 Database** - In-memory database
- **Maven** - Dependency management and build tool

## Prerequisites

Before running this application, make sure you have the following installed:

- Java 21 or higher
- Maven 3.6+ (or use the included Maven wrapper)

## Installation

1. Clone the repository:
```bash
git clone https://github.com/OmarElmelegy/Banking-api.git
cd Banking-api
```

2. Build the project:
```bash
./mvnw clean install
```

## Running the Application

You can run the application using the Maven wrapper:

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Base URL
```
http://localhost:8080/api/accounts
```

### Available Endpoints

#### 1. Get All Accounts
```http
GET /api/accounts
```
Returns a list of all bank accounts.

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

## Database Configuration

The application uses an in-memory H2 database. Data is stored in memory and will be lost when the application stops.

### H2 Console (Optional)
If you want to access the H2 console for database inspection, you can add the following to `src/main/resources/application.properties`:

```properties
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:mem:bankingdb
```

Then access the console at: `http://localhost:8080/h2-console`

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
│   │       ├── BankingApiApplication.java    # Main application class
│   │       ├── controller/
│   │       │   └── AccountController.java     # REST endpoints
│   │       ├── model/
│   │       │   └── Account.java               # Account entity
│   │       ├── repository/
│   │       │   └── AccountRepository.java     # Data access layer
│   │       └── service/
│   │           └── AccountService.java        # Business logic
│   └── resources/
│       └── application.properties             # Application configuration
└── test/
    └── java/                                  # Test classes
```

## Error Handling

The API handles the following error scenarios:

- **Account not found** - Returns error when trying to access non-existent account
- **Invalid amount** - Returns error for negative or zero amounts
- **Insufficient funds** - Returns error when withdrawal amount exceeds account balance

## Future Enhancements

Potential improvements for this project:

- Add authentication and authorization
- Implement transfer between accounts
- Add transaction history
- Use persistent database (PostgreSQL, MySQL)
- Add account types (savings, checking)
- Implement interest calculation
- Add comprehensive exception handling with custom error responses
- Add API documentation with Swagger/OpenAPI

## License

This project is a demo application for learning Spring Boot.

## Author

Omar Elmelegy
