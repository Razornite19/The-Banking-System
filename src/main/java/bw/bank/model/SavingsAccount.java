package bw.bank.model;

import java.time.LocalDate;

public class SavingsAccount extends Account {
    public static final double INTEREST_RATE = 0.0005; // 0.05% monthly

    public SavingsAccount(String accountNumber, double balance, String branch, LocalDate dateCreated, String customerId) {
        super(accountNumber, balance, branch, dateCreated, customerId);
    }

    @Override
    public double calculateInterest() {
        return Math.round((getBalance() * INTEREST_RATE) * 100.0) / 100.0;
    }

    @Override
    public boolean withdraw(double amount) {
        // FR 4.3: Strictly prohibit and reject withdrawal attempts from SavingsAccount
        return false;
    }

    @Override
    public String getAccountType() {
        return "SAVINGS";
    }
}