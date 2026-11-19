package bw.bank.model;

import java.time.LocalDateTime;

public class Transaction {
    private String transactionId;
    private String accountNumber;
    private LocalDateTime dateTime;
    private String type; // "DEPOSIT", "WITHDRAWAL", "INTEREST"
    private double amount;
    private double balanceAfterTransaction;
    private String reference;

    public Transaction(String transactionId, String accountNumber, LocalDateTime dateTime,
                       String type, double amount, double balanceAfterTransaction, String reference) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.dateTime = dateTime;
        this.type = type;
        this.amount = amount;
        this.balanceAfterTransaction = balanceAfterTransaction;
        this.reference = reference;
    }

    public String getTransactionId() { return transactionId; }
    public String getAccountNumber() { return accountNumber; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public double getBalanceAfterTransaction() { return balanceAfterTransaction; }
    public String getReference() { return reference; }
}