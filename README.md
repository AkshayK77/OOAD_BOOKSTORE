# Online Bookstore Application

A full-stack e-commerce application for an online bookstore built with Spring Boot and React.

## Features

- User authentication and authorization
- Book catalog browsing with search and filtering
- Shopping cart functionality
- Order management
- Admin panel for product and order management
- Payment processing simulation

## Technology Stack

### Backend
- Java 11+
- Spring Boot
- Spring Security with JWT
- Spring Data JPA
- MySQL Database
- Maven

### Frontend
- React
- Redux for state management
- Material-UI components
- Axios for API calls

## Project Structure

- `/src` - Backend Java code
- `/frontend` - React frontend application

## Getting Started

### Prerequisites
- JDK 11 or newer
- Maven
- Node.js and npm
- MySQL

### Running the Backend
```bash
mvn spring-boot:run
```

### Running the Frontend
```bash
cd frontend
npm install
npm start
```

## API Documentation

The API documentation is available at `/swagger-ui.html` when the application is running.

## Admin Access

There are two ways to log in as admin:

1. Click the "Login as Admin" button on the login page
2. Manually enter the admin credentials:
   - Username: admin@admin.com
   - Password: admin123

Admin users have access to additional features:
- Manage Books: Add, edit, or delete books
- Manage Orders: View all orders from all users and update order status

## Troubleshooting

### Backend Issues

- If port 8080 is already in use, find and stop the process:
  ```
  netstat -ano | findstr :8080
  taskkill /F /PID <PID_NUMBER>
  ```

### Frontend Issues

- If port 3000 is already in use, you'll be prompted to use a different port
- If you can't remove items from the cart, ensure you're using the latest version from the repository 