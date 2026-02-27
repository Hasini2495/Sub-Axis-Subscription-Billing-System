-- Subscription Billing System Database Setup
-- Run this script to create the database and tables

-- Create database
CREATE DATABASE IF NOT EXISTS subscription_db;
USE subscription_db;

-- Plans table
CREATE TABLE IF NOT EXISTS plans (
    plan_id INT PRIMARY KEY AUTO_INCREMENT,
    plan_name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    billing_cycle VARCHAR(20) NOT NULL,
    duration INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Users table
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Subscriptions table
CREATE TABLE IF NOT EXISTS subscriptions (
    subscription_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    plan_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (plan_id) REFERENCES plans(plan_id) ON DELETE CASCADE
);

-- Invoices table
CREATE TABLE IF NOT EXISTS invoices (
    invoice_id INT PRIMARY KEY AUTO_INCREMENT,
    subscription_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    invoice_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(subscription_id) ON DELETE CASCADE
);

-- Payments table
CREATE TABLE IF NOT EXISTS payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    invoice_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id) ON DELETE CASCADE
);

-- Insert sample data for testing

-- Sample Plans
INSERT INTO plans (plan_name, price, billing_cycle, duration) VALUES
('Basic Monthly', 9.99, 'MONTHLY', 30),
('Pro Monthly', 19.99, 'MONTHLY', 30),
('Enterprise Monthly', 49.99, 'MONTHLY', 30),
('Basic Yearly', 99.99, 'YEARLY', 365),
('Pro Yearly', 199.99, 'YEARLY', 365);

-- Sample Users
INSERT INTO users (name, email, role, status) VALUES
('John Doe', 'john.doe@example.com', 'USER', 'ACTIVE'),
('Jane Smith', 'jane.smith@example.com', 'USER', 'ACTIVE'),
('Admin User', 'admin@example.com', 'ADMIN', 'ACTIVE'),
('Bob Johnson', 'bob.johnson@example.com', 'USER', 'ACTIVE'),
('Alice Williams', 'alice.williams@example.com', 'USER', 'INACTIVE');

-- Sample Subscriptions
INSERT INTO subscriptions (user_id, plan_id, start_date, end_date, status) VALUES
(1, 1, '2026-01-01', '2026-01-31', 'ACTIVE'),
(2, 2, '2026-01-15', '2026-02-14', 'ACTIVE'),
(4, 4, '2025-12-01', '2026-11-30', 'ACTIVE');

-- Sample Invoices
INSERT INTO invoices (subscription_id, amount, invoice_date, status) VALUES
(1, 9.99, '2026-01-01', 'PAID'),
(2, 19.99, '2026-01-15', 'PAID'),
(3, 99.99, '2025-12-01', 'PAID'),
(1, 9.99, '2026-02-01', 'UNPAID'),
(2, 19.99, '2026-02-15', 'UNPAID');

-- Sample Payments
INSERT INTO payments (invoice_id, amount, payment_date, payment_method) VALUES
(1, 9.99, '2026-01-01', 'CREDIT_CARD'),
(2, 19.99, '2026-01-15', 'PAYPAL'),
(3, 99.99, '2025-12-01', 'BANK_TRANSFER');

-- Display counts
SELECT 'Database Setup Complete!' AS Status;
SELECT COUNT(*) AS TotalPlans FROM plans;
SELECT COUNT(*) AS TotalUsers FROM users;
SELECT COUNT(*) AS TotalSubscriptions FROM subscriptions;
SELECT COUNT(*) AS TotalInvoices FROM invoices;
SELECT COUNT(*) AS TotalPayments FROM payments;
