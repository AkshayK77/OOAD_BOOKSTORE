# Bookstore Application

A full-stack Java and React application for managing a bookstore.

## Running the Application

### Backend

1. Start the backend server:
   ```
   cd catesh
   mvn spring-boot:run
   ```

2. To run without initializing data (in case of duplicate key errors):
   ```
   mvn spring-boot:run -Dspring-boot.run.arguments=--spring.sql.init.mode=never
   ```

### Frontend

1. Start the frontend application:
   ```
   cd frontend
   npm start
   ```

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