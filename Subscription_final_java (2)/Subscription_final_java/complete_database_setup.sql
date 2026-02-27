-- Complete Database Setup for Subscription Billing System
-- Run this script to create the database with all tables and sample data

-- Create database
DROP DATABASE IF EXISTS subscription_db;
CREATE DATABASE subscription_db;
USE subscription_db;

-- Plans table
CREATE TABLE plans (
    plan_id INT PRIMARY KEY AUTO_INCREMENT,
    plan_name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    billing_cycle VARCHAR(20) NOT NULL,
    duration INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Users table
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255),
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Subscriptions table with new fields
CREATE TABLE subscriptions (
    subscription_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    plan_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    next_billing_date DATE,
    auto_renew BOOLEAN DEFAULT TRUE,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (plan_id) REFERENCES plans(plan_id) ON DELETE CASCADE
);

-- Invoices table
CREATE TABLE invoices (
    invoice_id INT PRIMARY KEY AUTO_INCREMENT,
    subscription_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    invoice_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(subscription_id) ON DELETE CASCADE
);

-- Payments table
CREATE TABLE payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    invoice_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id) ON DELETE CASCADE
);

-- Activity Log table
CREATE TABLE activity_log (
    activity_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    related_entity_id INT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_timestamp (user_id, timestamp DESC)
);

-- Insert sample plans
INSERT INTO plans (plan_name, price, billing_cycle, duration) VALUES
('Basic Monthly', 9.99, 'MONTHLY', 30),
('Pro Monthly', 19.99, 'MONTHLY', 30),
('Enterprise Monthly', 49.99, 'MONTHLY', 30),
('Basic Yearly', 99.99, 'YEARLY', 365),
('Pro Yearly', 199.99, 'YEARLY', 365);

-- Insert sample users
INSERT INTO users (name, email, role, status) VALUES
('John Doe', 'john.doe@example.com', 'USER', 'ACTIVE'),
('Jane Smith', 'jane.smith@example.com', 'USER', 'ACTIVE'),
('Admin User', 'admin@example.com', 'ADMIN', 'ACTIVE'),
('Bob Johnson', 'bob.johnson@example.com', 'USER', 'ACTIVE'),
('Test User', 'test@example.com', 'USER', 'ACTIVE');

-- Display setup confirmation
SELECT 'Database setup complete!' AS Status;
SELECT COUNT(*) AS 'Total Plans' FROM plans;
SELECT COUNT(*) AS 'Total Users' FROM users;
SELECT 'Ready to accept subscriptions!' AS Message;
