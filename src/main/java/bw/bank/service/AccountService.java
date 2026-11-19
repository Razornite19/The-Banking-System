package bw.bank.service;

import bw.bank.dao.AccountDAO;
import bw.bank.dao.CustomerDAO;
import bw.bank.dao.DatabaseConnection;
import bw.bank.dao.TransactionDAO;
import bw.bank.model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AccountService {
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public void registerCustomer(Customer customer) throws SQLException {
        customerDAO.save(customer);
    }

    public List<Customer> searchCustomers(String keyword) throws SQLException {
        return customerDAO.search(keyword);
    }

    public SavingsAccount openSavingsAccount(String customerId, double initialDeposit, String branch) throws SQLException {
        Customer customer = customerDAO.findById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer ID not found: " + customerId);
        }
        String accNum = "ACC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        SavingsAccount account = new SavingsAccount(accNum, initialDeposit, branch, LocalDate.now(), customerId);
        accountDAO.save(account);
        if (initialDeposit > 0) {
            recordInitialTransaction(account, initialDeposit, "Initial Savings Deposit");
        }
        return account;
    }

    public InvestmentAccount openInvestmentAccount(String customerId, double initialDeposit, String branch) throws SQLException {
        Customer customer = customerDAO.findById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer ID not found: " + customerId);
        }
        String accNum = "ACC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        // InvestmentAccount constructor enforces >= BWP 500.00 minimum
        InvestmentAccount account = new InvestmentAccount(accNum, initialDeposit, branch, LocalDate.now(), customerId);
        accountDAO.save(account);
        recordInitialTransaction(account, initialDeposit, "Initial Investment Deposit");
        return account;
    }

    public ChequeAccount openChequeAccount(String customerId, double initialDeposit, String branch) throws SQLException {
        Customer customer = customerDAO.findById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer ID not found: " + customerId);
        }
        if (!(customer instanceof EmployedCustomer)) {
            throw new IllegalArgumentException("Only employed customers with employer details can open a Cheque Account.");
        }
        String accNum = "ACC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        ChequeAccount account = new ChequeAccount(accNum, initialDeposit, branch, LocalDate.now(), customerId);
        accountDAO.save(account);
        if (initialDeposit > 0) {
            recordInitialTransaction(account, initialDeposit, "Initial Cheque Deposit");
        }
        return account;
    }

    public void deposit(String accountNumber, double amount, String reference) throws SQLException {
        Account account = accountDAO.findByNumber(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }
        if (reference != null && !reference.trim().isEmpty()) {
            account.deposit(amount, reference);
        } else {
            account.deposit(amount);
            reference = "Cash/Electronic Deposit";
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                accountDAO.updateBalance(account, conn);
                Transaction txn = new Transaction(
                    "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    account.getAccountNumber(),
                    LocalDateTime.now(),
                    "DEPOSIT",
                    amount,
                    account.getBalance(),
                    reference
                );
                transactionDAO.save(txn, conn);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public boolean withdraw(String accountNumber, double amount) throws SQLException {
        Account account = accountDAO.findByNumber(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }
        if (account instanceof SavingsAccount) {
            throw new UnsupportedOperationException("Withdrawals are strictly prohibited on Savings Accounts.");
        }
        if (!account.withdraw(amount)) {
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                accountDAO.updateBalance(account, conn);
                Transaction txn = new Transaction(
                    "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    account.getAccountNumber(),
                    LocalDateTime.now(),
                    "WITHDRAWAL",
                    amount,
                    account.getBalance(),
                    "Customer Withdrawal"
                );
                transactionDAO.save(txn, conn);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
        return true;
    }

    public int processMonthlyInterest() throws SQLException {
        List<Account> allAccounts = accountDAO.findAll();
        int creditedCount = 0;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (Account account : allAccounts) {
                    double interest = account.calculateInterest();
                    if (interest > 0) {
                        account.deposit(interest, "Monthly Interest Credit");
                        accountDAO.updateBalance(account, conn);
                        Transaction txn = new Transaction(
                            "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                            account.getAccountNumber(),
                            LocalDateTime.now(),
                            "INTEREST",
                            interest,
                            account.getBalance(),
                            "Monthly Interest Payout"
                        );
                        transactionDAO.save(txn, conn);
                        creditedCount++;
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
        return creditedCount;
    }

    public List<Account> getCustomerAccounts(String customerId) throws SQLException {
        return accountDAO.findByCustomerId(customerId);
    }

    public List<Transaction> getTransactionHistory(String accountNumber) throws SQLException {
        return transactionDAO.findByAccountNumber(accountNumber);
    }

    private void recordInitialTransaction(Account account, double amount, String ref) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Transaction txn = new Transaction(
                    "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    account.getAccountNumber(),
                    LocalDateTime.now(),
                    "DEPOSIT",
                    amount,
                    account.getBalance(),
                    ref
                );
                transactionDAO.save(txn, conn);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}