package bw.bank.dao;

import bw.bank.model.Customer;
import bw.bank.model.EmployedCustomer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public void save(Customer customer) throws SQLException {
        String sql = """
            INSERT INTO customers (customer_id, first_name, last_name, national_id, phone_number,
                                   address, employer_name, employer_address, username, password_hash)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customer.getCustomerId());
            pstmt.setString(2, customer.getFirstName());
            pstmt.setString(3, customer.getLastName());
            pstmt.setString(4, customer.getNationalIdOrPassport());
            pstmt.setString(5, customer.getPhoneNumber());
            pstmt.setString(6, customer.getAddress());

            if (customer instanceof EmployedCustomer emp) {
                pstmt.setString(7, emp.getEmployerName());
                pstmt.setString(8, emp.getEmployerAddress());
            } else {
                pstmt.setNull(7, Types.VARCHAR);
                pstmt.setNull(8, Types.VARCHAR);
            }

            pstmt.setString(9, customer.getUsername());
            pstmt.setString(10, customer.getPasswordHash());
            pstmt.executeUpdate();
        }
    }

    public Customer findById(String customerId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Customer findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM customers WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<Customer> search(String keyword) throws SQLException {
        String sql = """
            SELECT * FROM customers
            WHERE customer_id LIKE ? OR national_id LIKE ? OR first_name LIKE ? OR last_name LIKE ?
            """;
        List<Customer> results = new ArrayList<>();
        String pattern = "%" + keyword + "%";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);
            pstmt.setString(4, pattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        String employerName = rs.getString("employer_name");
        String employerAddress = rs.getString("employer_address");
        if (employerName != null && !employerName.isBlank()) {
            return new EmployedCustomer(
                rs.getString("customer_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("national_id"),
                rs.getString("phone_number"),
                rs.getString("address"),
                rs.getString("username"),
                rs.getString("password_hash"),
                employerName,
                employerAddress
            );
        }
        return new Customer(
            rs.getString("customer_id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("national_id"),
            rs.getString("phone_number"),
            rs.getString("address"),
            rs.getString("username"),
            rs.getString("password_hash")
        );
    }
}