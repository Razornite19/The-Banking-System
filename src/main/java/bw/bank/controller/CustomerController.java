package bw.bank.controller;

import bw.bank.model.Account;
import bw.bank.model.Transaction;
import bw.bank.service.AccountService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

public class CustomerController {
    private final AccountService accountService = new AccountService();

    public List<Account> loadAccounts(String customerId) throws SQLException {
        return accountService.getCustomerAccounts(customerId);
    }

    public List<Transaction> loadTransactions(String accountNumber) throws SQLException {
        return accountService.getTransactionHistory(accountNumber);
    }

    public void handleDeposit(String accountNumber, String amountText, String reference) throws SQLException {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Please select an account first.");
        }
        double amount = parsePositiveAmount(amountText);
        accountService.deposit(accountNumber, amount, reference);
    }

    public void handleWithdraw(String accountNumber, String amountText) throws SQLException {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Please select an account first.");
        }
        double amount = parsePositiveAmount(amountText);
        boolean success = accountService.withdraw(accountNumber, amount);
        if (!success) {
            throw new IllegalArgumentException("Insufficient funds for this withdrawal.");
        }
    }

    public void exportTransactionsCSV(String accountNumber, File file) throws SQLException, IOException {
        List<Transaction> transactions = accountService.getTransactionHistory(accountNumber);
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("Transaction ID,Account Number,Date/Time,Type,Amount (BWP),Balance After (BWP),Reference");
            for (Transaction t : transactions) {
                writer.printf("%s,%s,%s,%s,%.2f,%.2f,%s%n",
                    t.getTransactionId(),
                    t.getAccountNumber(),
                    t.getDateTime(),
                    t.getType(),
                    t.getAmount(),
                    t.getBalanceAfterTransaction(),
                    t.getReference() == null ? "" : t.getReference().replace(",", " ")
                );
            }
        }
    }

    private double parsePositiveAmount(String amountText) {
        try {
            double val = Double.parseDouble(amountText.trim());
            if (val <= 0) {
                throw new IllegalArgumentException("Amount must be greater than BWP 0.00.");
            }
            return val;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please enter a valid numeric amount.");
        }
    }
}
