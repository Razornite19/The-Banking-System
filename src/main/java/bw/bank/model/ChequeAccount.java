package bw.bank.model;

import java.time.LocalDate;

public class ChequeAccount extends Account {
    public static final double INTEREST_RATE = 0.0; // 0% monthly

    public ChequeAccount(String accountNumber, double balance, String branch, LocalDate dateCreated, String customerId) {
        super(accountNumber, balance, branch, dateCreated, customerId);
    }

    @Override
    public double calculateInterest() {
        return 0.0;
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
        return "CHEQUE";
    }
}