package com.quanlybanhang.config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    /** Oracle trên Docker (image gvenzl/oracle-free) — PDB mặc định FREEPDB1. */
    private static final String HOST         = "localhost";
    private static final int    PORT         = 1521;
    private static final String SERVICE_NAME = "FREEPDB1";
    private static final String USERNAME     = "quanlybh";
    private static final String PASSWORD     = "abc123";

    /** Thin driver: @//host:port/service_name */
    private static final String URL =
            "jdbc:oracle:thin:@//" + HOST + ":" + PORT + "/" + SERVICE_NAME;

    private static DatabaseConfig instance;
    private Connection connection;

    private DatabaseConfig() {}

    public static DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("oracle.jdbc.OracleDriver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Oracle JDBC Driver not found: " + e.getMessage());
            }
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}