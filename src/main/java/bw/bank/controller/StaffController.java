package bw.bank.controller;

import bw.bank.model.*;
import bw.bank.service.AccountService;
import bw.bank.service.AuthenticationService;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class StaffController {
    private final AccountService accountService = new AccountService();

    public Customer registerCustomer(String firstName, String lastName, String nationalId,
                                     String phone, String address, String username, String password,
                                     boolean isEmployed, String employerName, String employerAddress) throws SQLException {
        if (firstName.isBlank() || lastName.isBlank() || nationalId.isBlank() ||
            phone.isBlank() || address.isBlank() || username.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException("All personal and login fields are required.");
        }

        String custId = "CUST-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String hash = AuthenticationService.hashPassword(password);

        Customer customer;
        if (isEmployed) {
            customer = new EmployedCustomer(custId, firstName.trim(), lastName.trim(), nationalId.trim(),
                phone.trim(), address.trim(), username.trim(), hash, employerName.trim(), employerAddress.trim());
        } else {
            customer = new Customer(custId, firstName.trim(), lastName.trim(), nationalId.trim(),
                phone.trim(), address.trim(), username.trim(), hash);
        }

        accountService.registerCustomer(customer);
        return customer;
    }

    public Account openAccount(String customerId, String accountType, String initialDepositText, String branch) throws SQLException {
        if (customerId == null || customerId.isBlank() || branch == null || branch.isBlank()) {
            throw new IllegalArgumentException("Customer ID and Branch are required.");
        }
        double deposit;
        try {
            deposit = Double.parseDouble(initialDepositText.trim());
            if (deposit < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Initial deposit must be a valid non-negative number.");
        }

        return switch (accountType.toUpperCase()) {
            case "SAVINGS" -> accountService.openSavingsAccount(customerId.trim(), deposit, branch.trim());
            case "INVESTMENT" -> accountService.openInvestmentAccount(customerId.trim(), deposit, branch.trim());
            case "CHEQUE" -> accountService.openChequeAccount(customerId.trim(), deposit, branch.trim());
            default -> throw new IllegalArgumentException("Invalid account type selected.");
        };
    }

    public List<Customer> searchCustomers(String keyword) throws SQLException {
        return accountService.searchCustomers(keyword == null ? "" : keyword.trim());
    }

    public List<Account> getCustomerAccounts(String customerId) throws SQLException {
        return accountService.getCustomerAccounts(customerId);
    }

    public List<Transaction> getAccountTransactions(String accountNumber) throws SQLException {
        return accountService.getTransactionHistory(accountNumber);
    }

    public int runMonthlyInterestBatch() throws SQLException {
        return accountService.processMonthlyInterest();
    }
}
