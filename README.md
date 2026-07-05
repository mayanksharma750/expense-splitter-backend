# Expense Splitter Backend

A RESTful backend service built using **Java** and **Spring Boot** that allows users to create expense groups, record shared expenses, calculate member balances, and generate simplified settlement transactions similar to Splitwise.

The application follows a layered architecture using Spring Boot, Spring Data JPA, and an in-memory H2 database.

## Features

- Create expense groups
- View all groups
- View group details
- Add shared expenses
- Delete expenses
- Calculate member balances
- Generate simplified settlement transactions
- Centralized exception handling
- Unit and Controller testing using JUnit 5 and MockMvc

## Technology Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 Database
- Maven
- JUnit 5
- Mockito
- MockMvc

## Setup & Run

### Clone the repository

```bash
git clone https://github.com/mayanksharma750/expense-splitter-backend.git
```

### Navigate to the project

```bash
cd expense-splitter-backend
```

### Run the application

```bash
mvn spring-boot:run
```

The application starts at:

```
http://localhost:8080
```

## Running Tests

Execute all unit and controller tests using:

```bash
mvn test
```

or

```bash
mvn clean install
```

All tests should pass successfully.

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/groups` | Create a group |
| GET | `/api/groups` | Get all groups |
| GET | `/api/groups/{groupId}` | Get group details |
| POST | `/api/groups/{groupId}/expenses` | Add expense |
| GET | `/api/groups/{groupId}/expenses` | List expenses |
| DELETE | `/api/groups/{groupId}/expenses/{expenseId}` | Delete expense |
| GET | `/api/groups/{groupId}/balances` | Get balances |
| GET | `/api/groups/{groupId}/settlements` | Get settlement transactions |

## Design Decisions

### Data Model

The application is designed around two primary entities:

- **Group**
    - id
    - name
    - members
    - createdAt

- **Expense**
    - id
    - title
    - amount
    - paidBy
    - splitAmong
    - createdAt
    - group

### Balance Calculation

Balances are calculated dynamically whenever the balances endpoint is called.

For every expense:

- The payer receives credit for the full expense amount.
- Each participant owes an equal share.
- Net balance is calculated as:

```
Net Balance = Amount Paid − Amount Owed
```

Positive balance indicates money to receive.

Negative balance indicates money to pay.

### Settlement Algorithm

The settlement logic uses a greedy algorithm to minimize the number of transactions.

Algorithm:

1. Separate creditors and debtors.
2. Match the largest debtor with the largest creditor.
3. Transfer the minimum possible amount.
4. Repeat until all balances become zero.

This approach significantly reduces the total number of settlement transactions.

### Why BigDecimal?

Money is represented using **BigDecimal** instead of **double** because it:

- avoids floating-point precision issues
- provides accurate monetary calculations
- supports proper rounding to two decimal places
- is the recommended approach for financial applications

## Error Handling

The application uses centralized exception handling with **@ControllerAdvice** and **@ExceptionHandler** to return consistent error responses.

Supported HTTP status codes:

| Scenario | HTTP Status |
|----------|-------------|
| Missing required fields | 400 Bad Request |
| Group not found | 404 Not Found |
| Expense not found | 404 Not Found |
| `paidBy` is not a group member | 422 Unprocessable Entity |
| `splitAmong` contains non-group members | 422 Unprocessable Entity |
| Unexpected server error | 500 Internal Server Error |

Example error response:

```json
{
  "error": "Group not found"
}
```

## AI Usage

AI-assisted development tools were used during development to improve productivity and code quality.

AI was primarily used for:

- Understanding assignment requirements
- Generating initial boilerplate code
- Suggesting project structure and DTOs
- Reviewing business logic
- Improving unit tests and documentation

All generated code was manually reviewed, modified, tested, and validated using Postman and JUnit before submission.

## Future Improvements

Possible enhancements include:

- User authentication and authorization
- Persistent database such as PostgreSQL or MySQL
- Unequal expense splitting
- Expense categories
- User accounts
- Docker containerization
- Swagger/OpenAPI documentation

