package dao;

import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User entity.
 * Handles all database operations for users.
 */
public class UserDAO {

    public UserDAO() {
        ensureColumns();
    }

    /**
     * Automatically adds the 'password' and 'phone' columns to the users table
     * if they don't already exist. Catches error 1060 (Duplicate column) so it
     * is safe to call on both old and new schemas.
     */
    private void ensureColumns() {
        tryAddColumn("ALTER TABLE users ADD COLUMN password VARCHAR(255) AFTER email");
        tryAddColumn("ALTER TABLE users ADD COLUMN phone VARCHAR(20) AFTER password");
    }

    /** Runs an ALTER TABLE statement; silently ignores 'Duplicate column' (1060). */
    private void tryAddColumn(String sql) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            if (e.getErrorCode() != 1060) { // 1060 = Duplicate column name
                System.err.println("Schema migration warning: " + e.getMessage());
            }
        }
    }

    /**
     * Creates a new user in the database.
     * Works whether or not the password/phone columns exist:
     *  - If password/phone are provided they are included in the INSERT.
     *  - If they are null the INSERT uses only the base columns (name, email, role, status).
     * @param user User object to create
     * @return true if successful
     */
    public boolean createUser(User user) {
        boolean hasPassword = user.getPassword() != null && !user.getPassword().isEmpty();
        boolean hasPhone    = user.getPhone()    != null && !user.getPhone().isEmpty();

        String sql;
        if (hasPassword && hasPhone) {
            sql = "INSERT INTO users (name, email, password, phone, role, status) VALUES (?, ?, ?, ?, ?, ?)";
        } else if (hasPassword) {
            sql = "INSERT INTO users (name, email, password, role, status) VALUES (?, ?, ?, ?, ?)";
        } else if (hasPhone) {
            sql = "INSERT INTO users (name, email, phone, role, status) VALUES (?, ?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO users (name, email, role, status) VALUES (?, ?, ?, ?)";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int idx = 1;
            stmt.setString(idx++, user.getName());
            stmt.setString(idx++, user.getEmail());
            if (hasPassword) stmt.setString(idx++, user.getPassword());
            if (hasPhone)    stmt.setString(idx++, user.getPhone());
            stmt.setString(idx++, user.getRole());
            stmt.setString(idx,   user.getStatus());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            // Re-throw so the UI layer can show a meaningful message
            // (e.g. duplicate email triggers MySQL error code 1062)
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    /**
     * Retrieves all users from the database.
     * @return List of all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setPhone(rs.getString("phone"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                users.add(user);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Retrieves a user by ID.
     * @param userId User ID
     * @return User object or null if not found
     */
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setPhone(rs.getString("phone"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                return user;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Updates an existing user in the database.
     * @param user User object with updated data
     * @return true if successful
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, password = ?, phone = ?, role = ?, status = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getRole());
            stmt.setString(6, user.getStatus());
            stmt.setInt(7, user.getUserId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deletes a user from the database.
     * @param userId User ID to delete
     * @return true if successful
     */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
