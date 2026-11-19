package bw.bank.model;

import java.time.LocalDate;

public class InvestmentAccount extends Account {
    public static final double INTEREST_RATE = 0.05; // 5.0% monthly
    public static final double MIN_INITIAL_DEPOSIT = 500.00; // BWP 500.00

    public InvestmentAccount(String accountNumber, double initialDeposit, String branch, LocalDate dateCreated, String customerId) {
        super(accountNumber, validateInitialDeposit(initialDeposit), branch, dateCreated, customerId);
    }

    private static double validateInitialDeposit(double initialDeposit) {
        if (initialDeposit < MIN_INITIAL_DEPOSIT) {
            throw new IllegalArgumentException("Investment Account requires a minimum initial deposit of BWP 500.00.");
        }
        return initialDeposit;
    }

    @Override
    public double calculateInterest() {
        return Math.round((getBalance() * INTEREST_RATE) * 100.0) / 100.0;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > getBalance()) {
            return false;
        }
        setBalance(getBalance() - amount);
        return true;
    }

    @Override
    public String getAccountType() {
        return "INVESTMENT";
    }
}