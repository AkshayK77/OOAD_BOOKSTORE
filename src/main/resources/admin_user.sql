-- Insert admin user if it doesn't exist already
-- Password is 'admin123' encrypted with BCrypt
INSERT INTO users (email, password, first_name, last_name, role)
SELECT 'admin@admin.com', '$2a$10$jmWaQ9yL4vzOmMgO0BPwMeRQgMq7xiFvq3UsBRNQs9P9oLKMNUvnK', 'Admin', 'User', 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@admin.com'); 