package bw.bank.dao;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static String dbUrl = "jdbc:sqlite:banking.db";
    private static boolean initialized = false;

    public static void setDbUrl(String url) {
        dbUrl = url;
        initialized = false;
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(dbUrl);
        if (!initialized) {
            initializeDatabase(conn);
            initialized = true;
        }
        return conn;
    }

    private static void initializeDatabase(Connection conn) {
        try {
            String sql = null;
            try (InputStream is = DatabaseConnection.class.getResourceAsStream("/schema.sql")) {
                if (is != null) {
                    sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                }
            }
            if (sql == null && Files.exists(Path.of("schema.sql"))) {
                sql = Files.readString(Path.of("schema.sql"));
            }
            if (sql == null || sql.isBlank()) {
                throw new RuntimeException("schema.sql not found or empty!");
            }

            for (String statement : sql.split(";")) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(trimmed);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database from schema.sql", e);
        }
    }
}