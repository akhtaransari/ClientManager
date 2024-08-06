# Customer Management Application

## Overview

This is a Customer Management CRUD application built using Spring Boot for the backend, MySQL for the database, and React for the frontend. The application supports creating, reading, updating, and deleting customer records. It also includes JWT authentication for secure access to APIs. Additionally, it features a synchronization mechanism to fetch and update customer data from a remote API.

## Features

- **Customer Management**: Create, update, delete, and retrieve customer records.
- **Authentication**: JWT authentication for secure API access.
- **Pagination, Sorting, and Searching**: Efficiently manage large customer lists.
- **Data Synchronization**: Sync customer data with a remote API.

## Technologies Used

- **Backend**: Spring Boot
- **Database**: MySQL
- **Frontend**: React, HTML, CSS, JavaScript
- **Authentication**: JWT

## Setup Instructions

### Prerequisites

- Java 8 or higher
- Node.js and npm
- MySQL database

### Backend Setup

1. Clone the repository:
    ```bash
    git clone https://github.com/akhtaransari/ClientManager.git
    cd ClientManager
    ```

2. Configure MySQL Database:
    - Create a database named `customer_db`.
    - Update the `application.properties` file in the `src/main/resources` directory with your MySQL credentials.

    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/customer_db
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5Dialect
    ```

3. Build and run the Spring Boot application:
    ```bash
    ./mvnw spring-boot:run
    ```

### Frontend Setup

1. Navigate to the frontend directory:
    ```bash
    cd frontend
    ```

2. Install dependencies:
    ```bash
    npm install
    ```

3. Start the React application:
    ```bash
    npm start
    ```

## API Endpoints

### Authentication

- **Login**: `POST /api/customers/login`
    ```json
    {
        "username": "contact-admin",
        "password": "contact-admin"
    }
    ```
    - Returns a JWT token for authenticated requests.

### Customer Management

- **Create Customer**: `POST /api/customers`
    ```json
    {
        "first_name": "Jane",
        "last_name": "Doe",
        "street": "Elvnu Street",
        "address": "H no 2",
        "city": "Delhi",
        "state": "Delhi",
        "email": "sam@gmail.com",
        "phone": "12345678"
    }
    ```

- **Update Customer**: `PUT /api/customers/{id}`
    ```json
    {
        "first_name": "Jane",
        "last_name": "Doe",
        "street": "Elvnu Street",
        "address": "H no 2",
        "city": "Delhi",
        "state": "Delhi",
        "email": "sam@gmail.com",
        "phone": "12345678"
    }
    ```

- **Get Customer List**: `GET /api/customers`
    - Supports pagination, sorting, and searching via query parameters.

- **Get Single Customer**: `GET /api/customers/{id}`

- **Delete Customer**: `DELETE /api/customers/{id}`

### Data Synchronization

- **Sync Customers**: `POST /api/customers/sync`
   ```json
    {
        "password": "your-password"
    }
    ```
    - Fetches customer data from the remote API and updates the local database.

## Frontend Screens

1. **Login Screen**: Allows users to login and obtain a JWT token.
2. **Customer List Screen**: Displays a list of customers with options to add, update, or delete customers.
3. **Add/Edit Customer Screen**: Form to add a new customer or edit an existing customer.

## Frontend Screenshots 
### Login
![Login](/screenshots/Login.jpeg)
### Dashboard
![Dashboard](/screenshots/Dashboard.jpeg)
### Add Customer
![Add Customer](/screenshots/Add-Customer.jpeg)
### Edit Customer
![Edit Customer](/screenshots/Edit-Customer.jpeg)
### Search By Type
![Search By Type](/screenshots/Search-by-type.jpeg)


## Synchronization

To synchronize customer data, click the "Sync" button on the Customer List screen. This will fetch the customer list from the remote API and update the local database accordingly.

## Remote API Integration

- **Authentication API**: `https://qa.sunbasedata.com/sunbase/portal/api/assignment_auth.jsp`
- **Get Customer List API**: `https://qa.sunbasedata.com/sunbase/portal/api/assignment.jsp`
    - Method: `GET`
    - Parameters: `cmd=get_customer_list`

Ensure you do not hardcode the login credentials in your code. Use environment variables or a configuration file to securely manage credentials.

## Contributing

Contributions are welcome! Please fork the repository and create a pull request with your changes.

## License

This project is licensed under the MIT License.
