package bw.bank.controller;

import bw.bank.model.BankStaff;
import bw.bank.model.Customer;
import bw.bank.service.AuthenticationService;

import java.sql.SQLException;

public class LoginController {
    private final AuthenticationService authService = new AuthenticationService();

    public Customer loginCustomer(String username, String password) throws SQLException {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Username and password are required.");
        }
        Customer customer = authService.authenticateCustomer(username.trim(), password);
        if (customer == null) {
            throw new IllegalArgumentException("Invalid customer username or password.");
        }
        return customer;
    }

    public BankStaff loginStaff(String username, String password) throws SQLException {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Username and password are required.");
        }
        BankStaff staff = authService.authenticateStaff(username.trim(), password);
        if (staff == null) {
            throw new IllegalArgumentException("Invalid staff username or password.");
        }
        return staff;
    }
}
