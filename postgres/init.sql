-- Purpose: Initialize the database schema and insert sample data.


-- 1. Create the MEMBERS table
CREATE TABLE members (
    member_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 2. Create the PAYMENTS table
CREATE TABLE payments (
    payment_id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),


    CONSTRAINT fk_member
        FOREIGN KEY (member_id)
        REFERENCES members (member_id)
        ON DELETE CASCADE
);

-- 3. Create the FAILED_EVENTS table
CREATE TABLE failed_events (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    payload TEXT NOT NULL,
    source_topic VARCHAR(255) NOT NULL,
    error_message TEXT,
    status VARCHAR(50) DEFAULT 'PENDING_REPLAY',
    occurred_at TIMESTAMP NOT NULL,
    retry_count INT DEFAULT 0
);

-- 4. Insert Data
INSERT INTO members (name, email) VALUES 
('Alice Johnson', 'alice.j@example.com'),
('Bob Smith', 'bob.s@example.com');

-- Inserting an initial payment for Alice
INSERT INTO payments (member_id, amount, currency) VALUES
(
    (SELECT member_id FROM members WHERE email = 'alice.j@example.com'),
    49.99,
    'USD'
);

-- Inserting an initial payment for Bob
INSERT INTO payments (member_id, amount, currency) VALUES
(
    (SELECT member_id FROM members WHERE email = 'bob.s@example.com'),
    12.50,
    'EUR'
);