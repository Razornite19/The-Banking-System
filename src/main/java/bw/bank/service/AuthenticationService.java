package bw.bank.service;

import bw.bank.dao.CustomerDAO;
import bw.bank.dao.StaffDAO;
import bw.bank.model.BankStaff;
import bw.bank.model.Customer;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class AuthenticationService {
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final StaffDAO staffDAO = new StaffDAO();

    public static String hashPassword(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public Customer authenticateCustomer(String username, String password) throws SQLException {
        Customer customer = customerDAO.findByUsername(username);
        if (customer != null && customer.getPasswordHash().equals(hashPassword(password))) {
            return customer;
        }
        return null;
    }

    public BankStaff authenticateStaff(String username, String password) throws SQLException {
        BankStaff staff = staffDAO.findByUsername(username);
        if (staff != null && staff.getPasswordHash().equals(hashPassword(password))) {
            return staff;
        }
        return null;
    }
}