# Inventory-backend

Backend REST API for an Inventory Management System, built by team **Bugwarts**.
It handles stock receiving, issuing, transfers and adjustments across multiple locations, with role-based access control.

## Tech Stack

- Java 24
- Spring Boot 4.0.5 (Web MVC, Data JPA, Security)
- PostgreSQL
- JWT authentication (jjwt 0.13.0)
- Lombok
- Maven (wrapper included)

## Modules

- **Auth and users**: login, registration, profile, roles and permissions
- **Item master**: items and item groups
- **GRN**: goods received notes
- **Opening stock**
- **Stock adjustment**
- **Stock issue**
- **Stock transfer and stock receipt**
- **Bin card and stock view**
- **System management**: locations and suppliers
- **Dashboard**: summaries and analytics

## Getting Started

### Prerequisites

- JDK 24
- PostgreSQL running locally

### Setup

1. Create a database:
   ```sql
   CREATE DATABASE ims_last_db;
   ```
2. Update the database credentials in `src/main/resources/application.yaml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/ims_last_db
       username: <your-username>
       password: <your-password>
   ```
3. Run the app:
   ```bash
   ./mvnw spring-boot:run
   ```
   On Windows use `mvnw.cmd spring-boot:run`.

The API starts on `http://localhost:8080`. Tables are created automatically (`ddl-auto: update`).

### First run

On an empty database, `DataSeeder` creates the default permissions, roles, locations, suppliers and one admin user (`admin`). This is for development only: change the password and remove the seeder before any real deployment.

## Authentication

1. `POST /api/auth/login` with a username and password to receive a JWT.
2. Send it on later requests as `Authorization: Bearer <token>`.

The CORS config allows the frontend at `http://localhost:3000`.

## Roles

`SYSTEM_ADMIN`, `STORE_STAFF`, `APPROVING_AUTHORITY`, `REQUESTING_OFFICER`, `FINANCIAL_SUPPLIER`

## Running Tests

```bash
./mvnw test
```

## Project Structure

```
src/main/java/com/example/inventoryAuth/
├── Config/       Security and data seeding
├── Controller/   REST endpoints
├── DTO/          Request and response objects
├── Entity/       JPA entities
├── Filter/       JWT authentication filter
├── Repository/   Spring Data repositories
├── Service/      Business logic
└── Utility/      JWT helper
```