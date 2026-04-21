package com.quanlydatvemaybay.config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    private static final String SERVER   = "localhost";
    private static final int    PORT     = 1433;
    private static final String DATABASE = "DuAn1";
    private static final String USERNAME = "sa";           // đổi theo tài khoản SQL Server của bạn
    private static final String PASSWORD = "Abc123$%^";       // đổi theo mật khẩu của bạn

    private static final String URL =
            "jdbc:sqlserver://" + SERVER + ":" + PORT
                    + ";databaseName=" + DATABASE
                    + ";encrypt=true"
                    + ";trustServerCertificate=true";

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
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQL Server JDBC Driver not found: " + e.getMessage());
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