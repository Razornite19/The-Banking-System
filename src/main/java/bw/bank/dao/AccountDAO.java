package bw.bank.dao;

import bw.bank.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public void save(Account account) throws SQLException {
        String sql = """
            INSERT INTO accounts (account_number, customer_id, account_type, balance, branch, date_created)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, account.getAccountNumber());
            pstmt.setString(2, account.getCustomerId());
            pstmt.setString(3, account.getAccountType());
            pstmt.setDouble(4, account.getBalance());
            pstmt.setString(5, account.getBranch());
            pstmt.setString(6, account.getDateCreated().toString());
            pstmt.executeUpdate();
        }
    }

    public void updateBalance(Account account, Connection conn) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, account.getBalance());
            pstmt.setString(2, account.getAccountNumber());
            pstmt.executeUpdate();
        }
    }

    public Account findByNumber(String accountNumber) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<Account> findByCustomerId(String customerId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE customer_id = ?";
        List<Account> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public List<Account> findAll() throws SQLException {
        String sql = "SELECT * FROM accounts";
        List<Account> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Account mapRow(ResultSet rs) throws SQLException {
        String accNum = rs.getString("account_number");
        String custId = rs.getString("customer_id");
        String type = rs.getString("account_type");
        double balance = rs.getDouble("balance");
        String branch = rs.getString("branch");
        LocalDate dateCreated = LocalDate.parse(rs.getString("date_created"));

        return switch (type.toUpperCase()) {
            case "SAVINGS" -> new SavingsAccount(accNum, balance, branch, dateCreated, custId);
            case "INVESTMENT" -> new InvestmentAccount(accNum, balance, branch, dateCreated, custId);
            case "CHEQUE" -> new ChequeAccount(accNum, balance, branch, dateCreated, custId);
            default -> throw new IllegalStateException("Unknown account type: " + type);
        };
    }
}