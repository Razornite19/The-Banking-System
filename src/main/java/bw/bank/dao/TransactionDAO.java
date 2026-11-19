package bw.bank.dao;

import bw.bank.model.Transaction;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public void save(Transaction txn, Connection conn) throws SQLException {
        String sql = """
            INSERT INTO transactions (transaction_id, account_number, date_time, type, amount, balance_after, reference)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, txn.getTransactionId());
            pstmt.setString(2, txn.getAccountNumber());
            pstmt.setString(3, txn.getDateTime().toString());
            pstmt.setString(4, txn.getType());
            pstmt.setDouble(5, txn.getAmount());
            pstmt.setDouble(6, txn.getBalanceAfterTransaction());
            pstmt.setString(7, txn.getReference());
            pstmt.executeUpdate();
        }
    }

    public List<Transaction> findByAccountNumber(String accountNumber) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY date_time DESC";
        List<Transaction> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Transaction(
                        rs.getString("transaction_id"),
                        rs.getString("account_number"),
                        LocalDateTime.parse(rs.getString("date_time")),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getDouble("balance_after"),
                        rs.getString("reference")
                    ));
                }
            }
        }
        return list;
    }
}