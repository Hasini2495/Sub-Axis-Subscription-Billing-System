-- Add password and phone fields to users table
-- Run this if you already have an existing database

USE subscription_db;

ALTER TABLE users 
ADD COLUMN password VARCHAR(255) AFTER email,
ADD COLUMN phone VARCHAR(20) AFTER password;
