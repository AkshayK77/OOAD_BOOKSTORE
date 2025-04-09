-- Run this script directly in your MySQL console or MySQL Workbench
-- This creates an admin user with username: admin@admin.com and password: admin123

-- First make sure you're using the correct database
USE bookstore;

-- Insert admin user if it doesn't exist
INSERT INTO users (email, password, first_name, last_name, role)
SELECT 'admin@admin.com', '$2a$10$jmWaQ9yL4vzOmMgO0BPwMeRQgMq7xiFvq3UsBRNQs9P9oLKMNUvnK', 'Admin', 'User', 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@admin.com');

-- Verify the user was inserted
SELECT * FROM users WHERE email = 'admin@admin.com'; 