package bw.bank.model;

import java.time.LocalDate;

public abstract class Account implements InterestBearing {
    private String accountNumber;
    private double balance;
    private String branch;
    private LocalDate dateCreated;
    private String customerId;

    public Account(String accountNumber, double balance, String branch, LocalDate dateCreated, String customerId) {
        if (balance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.branch = branch;
        this.dateCreated = dateCreated;
        this.customerId = customerId;
    }

    // Overloaded deposit method 1
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }
        this.balance = Math.round((this.balance + amount) * 100.0) / 100.0;
    }

    // Overloaded deposit method 2 (matches your Class Diagram)
    public void deposit(double amount, String reference) {
        if (reference == null || reference.trim().isEmpty()) {
            throw new IllegalArgumentException("Deposit reference cannot be empty.");
        }
        deposit(amount);
    }

    // Abstract withdraw method overridden by subclasses
    public abstract boolean withdraw(double amount);

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    protected void setBalance(double balance) {
        this.balance = Math.round(balance * 100.0) / 100.0;
    }

    public String getBranch() {
        return branch;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public String getCustomerId() {
        return customerId;
    }

    public abstract String getAccountType();
}