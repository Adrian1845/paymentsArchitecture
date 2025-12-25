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
        ON DELETE CASCADE -- If a member is deleted, delete their payments
);

-- 3. Insert Sample Data
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