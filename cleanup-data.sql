-- Cleanup script to remove previously inserted dummy data

-- Disable foreign key checks temporarily to allow truncating tables with relationships
SET session_replication_role = 'replica';

-- Truncate tables in reverse order of dependencies
TRUNCATE TABLE email_verifications CASCADE;
TRUNCATE TABLE loans CASCADE;
TRUNCATE TABLE customers CASCADE;

-- Keep the default agent user, but remove other dummy users
DELETE FROM users WHERE username != 'agent';

-- Reset sequences to ensure new IDs start fresh
ALTER SEQUENCE customers_id_seq RESTART WITH 1;
ALTER SEQUENCE loans_id_seq RESTART WITH 1;
ALTER SEQUENCE email_verifications_id_seq RESTART WITH 1;

-- Re-enable foreign key checks
SET session_replication_role = 'origin';

-- Verify cleanup
SELECT 'Users' AS table_name, COUNT(*) AS record_count FROM users
UNION ALL
SELECT 'Customers', COUNT(*) FROM customers
UNION ALL
SELECT 'Loans', COUNT(*) FROM loans
UNION ALL
SELECT 'Email Verifications', COUNT(*) FROM email_verifications;