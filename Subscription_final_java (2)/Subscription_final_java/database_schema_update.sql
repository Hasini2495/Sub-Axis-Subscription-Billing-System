-- Database Schema Update for Full Functional Integration
-- Run this to add missing fields and tables

USE subscription_db;

-- Add missing columns to subscriptions table
ALTER TABLE subscriptions 
ADD COLUMN IF NOT EXISTS next_billing_date DATE AFTER end_date;

ALTER TABLE subscriptions 
ADD COLUMN IF NOT EXISTS auto_renew BOOLEAN DEFAULT TRUE AFTER next_billing_date;

-- Create activity_log table
CREATE TABLE IF NOT EXISTS activity_log (
    activity_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    related_entity_id INT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_timestamp (user_id, timestamp DESC)
);

-- Update existing subscriptions to set next_billing_date
UPDATE subscriptions 
SET next_billing_date = end_date 
WHERE next_billing_date IS NULL AND status = 'ACTIVE';

-- Update existing subscriptions to set auto_renew
UPDATE subscriptions 
SET auto_renew = TRUE 
WHERE auto_renew IS NULL;

-- Insert sample activity log entries for existing data
INSERT INTO activity_log (user_id, event_type, description, related_entity_id, timestamp)
SELECT 
    s.user_id, 
    'SUBSCRIPTION_CREATED',
    CONCAT('✓ Subscribed to ', p.plan_name, ' plan'),
    s.subscription_id,
    s.created_at
FROM subscriptions s
JOIN plans p ON s.plan_id = p.plan_id
WHERE NOT EXISTS (
    SELECT 1 FROM activity_log al 
    WHERE al.user_id = s.user_id 
    AND al.event_type = 'SUBSCRIPTION_CREATED' 
    AND al.related_entity_id = s.subscription_id
);

INSERT INTO activity_log (user_id, event_type, description, related_entity_id, timestamp)
SELECT 
    s.user_id,
    'INVOICE_GENERATED',
    CONCAT('📄 Invoice #', i.invoice_id, ' generated for $', FORMAT(i.amount, 2)),
    i.invoice_id,
    i.created_at
FROM invoices i
JOIN subscriptions s ON i.subscription_id = s.subscription_id
WHERE NOT EXISTS (
    SELECT 1 FROM activity_log al 
    WHERE al.user_id = s.user_id 
    AND al.event_type = 'INVOICE_GENERATED' 
    AND al.related_entity_id = i.invoice_id
);

INSERT INTO activity_log (user_id, event_type, description, related_entity_id, timestamp)
SELECT 
    s.user_id,
    'PAYMENT_COMPLETED',
    CONCAT('💳 Payment of $', FORMAT(p.amount, 2), ' processed successfully'),
    p.payment_id,
    p.created_at
FROM payments p
JOIN invoices i ON p.invoice_id = i.invoice_id
JOIN subscriptions s ON i.subscription_id = s.subscription_id
WHERE NOT EXISTS (
    SELECT 1 FROM activity_log al 
    WHERE al.user_id = s.user_id 
    AND al.event_type = 'PAYMENT_COMPLETED' 
    AND al.related_entity_id = p.payment_id
);

-- Display summary
SELECT 'Schema Update Complete!' AS Status;
SELECT COUNT(*) AS TotalActivities FROM activity_log;
SELECT COUNT(*) AS SubscriptionsWithBillingDate FROM subscriptions WHERE next_billing_date IS NOT NULL;
