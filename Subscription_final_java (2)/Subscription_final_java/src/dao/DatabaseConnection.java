package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class to manage database connections.
 * Provides centralized connection management for all DAOs.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/subscription_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    
    private static Connection connection = null;

    /**
     * Private constructor to prevent instantiation.
     */
    private DatabaseConnection() {
    }

    /**
     * Gets a database connection. Creates one if it doesn't exist.
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL Driver not found", e);
            }
        }
        return connection;
    }

    /**
     * Closes the database connection.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
