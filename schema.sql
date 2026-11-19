CREATE TABLE IF NOT EXISTS customers (
    customer_id TEXT PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    national_id TEXT UNIQUE NOT NULL,
    phone_number TEXT NOT NULL,
    address TEXT NOT NULL,
    employer_name TEXT,
    employer_address TEXT,
    username TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS staff (
    staff_id TEXT PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS accounts (
    account_number TEXT PRIMARY KEY,
    customer_id TEXT NOT NULL,
    account_type TEXT NOT NULL,
    balance REAL NOT NULL,
    branch TEXT NOT NULL,
    date_created TEXT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE IF NOT EXISTS transactions (
    transaction_id TEXT PRIMARY KEY,
    account_number TEXT NOT NULL,
    date_time TEXT NOT NULL,
    type TEXT NOT NULL,
    amount REAL NOT NULL,
    balance_after REAL NOT NULL,
    reference TEXT,
    FOREIGN KEY (account_number) REFERENCES accounts(account_number)
);

-- Sample Staff (Password for all: password123)
INSERT OR IGNORE INTO staff VALUES
('STF-001', 'admin', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'ADMIN'),
('STF-002', 'teller1', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TELLER');

-- Sample Customers (Password for all: password123)
INSERT OR IGNORE INTO customers VALUES
('CUST-001', 'Thabo', 'Kgosidintsi', '901214567', '+26771234567', 'Plot 124, Gaborone West', 'Debswana Diamond Co.', 'Plot 54351, Gaborone', 'thabo.k', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f'),
('CUST-002', 'Kefilwe', 'Motsumi', '880521890', '+26772345678', 'Block 8, Gaborone', 'Botswana Power Corp', 'Motlakase House, Gaborone', 'kefilwe.m', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f'),
('CUST-003', 'Neo', 'Modise', '950811234', '+26773456789', 'Extension 9, Gaborone', NULL, NULL, 'neo.m', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f'),
('CUST-004', 'Olebogeng', 'Seretse', '920304556', '+26774567890', 'Phase 4, Gaborone', 'Orange Botswana', 'Plot 54368, CBD Gaborone', 'olebogeng.s', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f');

-- Sample Accounts
INSERT OR IGNORE INTO accounts VALUES
('ACC-1001', 'CUST-001', 'SAVINGS', 2500.00, 'Gaborone Main', '2026-01-15'),
('ACC-1002', 'CUST-001', 'INVESTMENT', 5000.00, 'Gaborone Main', '2026-02-01'),
('ACC-1003', 'CUST-001', 'CHEQUE', 12400.00, 'Gaborone Main', '2026-02-10'),
('ACC-1004', 'CUST-002', 'INVESTMENT', 1500.00, 'Game City Branch', '2026-03-05'),
('ACC-1005', 'CUST-003', 'SAVINGS', 800.00, 'Airport Junction', '2026-04-12'),
('ACC-1006', 'CUST-004', 'CHEQUE', 6200.00, 'CBD Branch', '2026-05-20');

-- Sample Transactions
INSERT OR IGNORE INTO transactions VALUES
('TXN-001', 'ACC-1001', '2026-01-15T10:30:00', 'DEPOSIT', 2500.00, 2500.00, 'Initial Savings Deposit'),
('TXN-002', 'ACC-1002', '2026-02-01T11:15:00', 'DEPOSIT', 5000.00, 5000.00, 'Investment Opening'),
('TXN-003', 'ACC-1003', '2026-02-10T09:00:00', 'DEPOSIT', 15000.00, 15000.00, 'Monthly Salary'),
('TXN-004', 'ACC-1003', '2026-02-15T14:20:00', 'WITHDRAWAL', 2600.00, 12400.00, 'Rent Payment'),
('TXN-005', 'ACC-1004', '2026-03-05T16:45:00', 'DEPOSIT', 1500.00, 1500.00, 'Initial Investment');