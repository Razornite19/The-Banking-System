package bw.bank.dao;

import bw.bank.model.BankStaff;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StaffDAO {

    public BankStaff findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM staff WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new BankStaff(
                        rs.getString("staff_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role")
                    );
                }
            }
        }
        return null;
    }
}