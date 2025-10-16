-- Dummy Data Generation Script for Microfinance Database
-- This script generates realistic dummy data for all tables in the microfinance database

-- Clear existing data (optional - comment out if you want to keep existing data)
TRUNCATE email_verifications CASCADE;
-- Don't truncate users table as it has the default agent user
-- TRUNCATE users CASCADE;
TRUNCATE loans CASCADE;
TRUNCATE customers CASCADE;

-- Generate Users (50 users including agents and admins)
INSERT INTO users (username, password, email, role)
SELECT
    'user' || n,
    -- Using bcrypt-encoded password (same as agent123 for simplicity)
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV6UiC',
    'user' || n || '@microfinance.com',
    CASE
        WHEN n % 10 = 0 THEN 'ADMIN'
        ELSE 'AGENT'
    END
FROM generate_series(1, 50) AS n
ON CONFLICT (username) DO NOTHING;

-- Generate Customers (500 customers with realistic data)
INSERT INTO customers (name, marital_status, employment_status, employer_name, date_of_birth, id_card, address, phone_number)
SELECT
    -- Realistic names
    CASE WHEN n % 2 = 0 THEN
        (ARRAY['John', 'Mary', 'James', 'Patricia', 'Robert', 'Jennifer', 'Michael', 'Linda', 'William', 'Elizabeth', 
               'David', 'Barbara', 'Richard', 'Susan', 'Joseph', 'Jessica', 'Thomas', 'Sarah', 'Charles', 'Karen'])[1 + mod(n, 20)]
    ELSE
        (ARRAY['Daniel', 'Nancy', 'Matthew', 'Lisa', 'Anthony', 'Betty', 'Mark', 'Dorothy', 'Donald', 'Sandra', 
               'Steven', 'Ashley', 'Paul', 'Kimberly', 'Andrew', 'Donna', 'Joshua', 'Emily', 'Kenneth', 'Michelle'])[1 + mod(n, 20)]
    END || ' ' ||
    (ARRAY['Smith', 'Johnson', 'Williams', 'Jones', 'Brown', 'Davis', 'Miller', 'Wilson', 'Moore', 'Taylor', 
           'Anderson', 'Thomas', 'Jackson', 'White', 'Harris', 'Martin', 'Thompson', 'Garcia', 'Martinez', 'Robinson',
           'Clark', 'Rodriguez', 'Lewis', 'Lee', 'Walker', 'Hall', 'Allen', 'Young', 'Hernandez', 'King'])[1 + mod(n, 30)],
    
    -- Marital status
    (ARRAY['SINGLE', 'MARRIED', 'DIVORCED', 'WIDOWED'])[1 + mod(n, 4)],
    
    -- Employment status
    (ARRAY['EMPLOYED', 'SELF_EMPLOYED', 'UNEMPLOYED', 'RETIRED', 'STUDENT'])[1 + mod(n, 5)],
    
    -- Employer name (for employed and self-employed)
    CASE 
        WHEN mod(n, 5) = 0 THEN 'Self Employed'
        WHEN mod(n, 5) = 2 THEN 'Unemployed'
        WHEN mod(n, 5) = 3 THEN 'Retired'
        WHEN mod(n, 5) = 4 THEN 'Student'
        ELSE (ARRAY['Acme Corporation', 'Globex', 'Stark Industries', 'Wayne Enterprises', 'Umbrella Corp', 
                    'Cyberdyne Systems', 'Soylent Corp', 'Initech', 'Massive Dynamic', 'Oscorp',
                    'Weyland-Yutani', 'Aperture Science', 'Tyrell Corporation', 'Rekall', 'Cybertron Inc',
                    'Local School', 'City Hospital', 'National Bank', 'Tech Solutions', 'Retail Store'])[1 + mod(n, 20)]
    END,
    
    -- Date of birth (between 18 and 70 years old)
    TO_DATE(TO_CHAR(CURRENT_DATE - ((18 + mod(n, 52)) * INTERVAL '1 year') - (mod(n, 365) * INTERVAL '1 day'), 'YYYY-MM-DD'), 'YYYY-MM-DD'),
    
    -- ID card (unique)
    'ID' || TO_CHAR(n, 'FM000000'),
    
    -- Address
    (ARRAY['123 Main St', '456 Oak Ave', '789 Pine Rd', '101 Maple Dr', '202 Cedar Ln', 
           '303 Elm Blvd', '404 Birch Way', '505 Spruce Ct', '606 Willow Path', '707 Fir Circle',
           '808 Redwood Pl', '909 Sequoia Ter', '111 Cherry St', '222 Apple Ave', '333 Orange Rd',
           '444 Banana Dr', '555 Grape Ln', '666 Peach Blvd', '777 Plum Way', '888 Pear Ct'])[1 + mod(n, 20)] || ', ' ||
    (ARRAY['New York', 'Los Angeles', 'Chicago', 'Houston', 'Phoenix', 
           'Philadelphia', 'San Antonio', 'San Diego', 'Dallas', 'San Jose',
           'Austin', 'Jacksonville', 'Fort Worth', 'Columbus', 'Indianapolis',
           'Charlotte', 'San Francisco', 'Seattle', 'Denver', 'Washington'])[1 + mod(n, 20)],
    
    -- Phone number
    '+1' || (ARRAY['212', '213', '312', '713', '602', 
                   '215', '210', '619', '214', '408',
                   '512', '904', '817', '614', '317',
                   '704', '415', '206', '303', '202'])[1 + mod(n, 20)] || 
            TO_CHAR(1000000 + mod(n * 37, 9000000), 'FM0000000')
FROM generate_series(1, 500) AS n;

-- Generate Loans (1000 loans with realistic data and proper relationships)
INSERT INTO loans (customer_id, principal, interest_rate, time_period_years, date_issued, total_amount_payable, status)
SELECT
    -- Link to existing customers using their actual IDs
    c.id,
    
    -- Principal amount (between $500 and $50,000)
    (500 + mod(n * 137, 49500))::NUMERIC,
    
    -- Interest rate (between 3% and 15%)
    (3 + mod(n, 12) + (mod(n, 100)::NUMERIC / 100))::NUMERIC,
    
    -- Loan period in years (1-10 years)
    1 + mod(n, 10),
    
    -- Date issued (within the last 3 years)
    TO_DATE(TO_CHAR(CURRENT_DATE - (mod(n, 1095) * INTERVAL '1 day'), 'YYYY-MM-DD'), 'YYYY-MM-DD'),
    
    -- Total amount payable (will be calculated below)
    0,
    
    -- Status
    CASE 
        WHEN mod(n, 10) < 7 THEN 'ACTIVE'
        WHEN mod(n, 10) = 7 THEN 'COMPLETED'
        WHEN mod(n, 10) = 8 THEN 'DEFAULTED'
        ELSE 'PENDING'
    END
FROM generate_series(1, 1000) AS n
JOIN (SELECT id FROM customers ORDER BY id) AS c ON (n-1) % 500 = (c.id-1) % 500;

-- Update total_amount_payable based on principal, interest rate and time period
UPDATE loans
SET total_amount_payable = principal * (1 + (interest_rate / 100 * time_period_years));

-- Generate Email Verifications (100 pending verifications)
INSERT INTO email_verifications (email, username, encoded_password, otp, expires_at, verified)
SELECT
    'pending' || n || '@microfinance.com',
    'pending_user' || n,
    -- Using bcrypt-encoded password (same as agent123 for simplicity)
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV6UiC',
    -- 6-digit OTP
    TO_CHAR(100000 + mod(n * 263, 900000), 'FM000000'),
    -- Expires in 24 hours from now
    CURRENT_TIMESTAMP + INTERVAL '24 hours',
    -- 20% already verified
    (mod(n, 5) = 0)
FROM generate_series(1, 100) AS n
ON CONFLICT (email) DO NOTHING;

-- Output summary of generated data
SELECT 'Users' AS table_name, COUNT(*) AS record_count FROM users
UNION ALL
SELECT 'Customers', COUNT(*) FROM customers
UNION ALL
SELECT 'Loans', COUNT(*) FROM loans
UNION ALL
SELECT 'Email Verifications', COUNT(*) FROM email_verifications;