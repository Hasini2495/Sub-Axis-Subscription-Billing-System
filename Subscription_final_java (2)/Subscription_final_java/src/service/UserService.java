package service;

import dao.UserDAO;
import model.User;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service layer for User operations.
 * Includes business logic and validation.
 */
public class UserService {
    private final UserDAO userDAO;
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    public UserService() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Creates a new user.
     * @param user User to create
     * @return true if successful
     * @throws IllegalArgumentException if validation fails
     */
    public boolean createUser(User user) {
        // Business validation
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (user.getEmail() == null || !EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        return userDAO.createUser(user);
    }
    
    /**
     * Retrieves all users.
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }
    
    /**
     * Retrieves a user by ID.
     * @param userId User ID
     * @return User object
     */
    public User getUserById(int userId) {
        return userDAO.getUserById(userId);
    }
    
    /**
     * Retrieves a user by email address.
     * @param email User's email
     * @return User object or null if not found
     */
    public User getUserByEmail(String email) {
        List<User> users = getAllUsers();
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * Gets the total count of users.
     * @return Total number of users
     */
    public int getUserCount() {
        return getAllUsers().size();
    }
    
    /**
     * Registers a new user with password hashing.
     * Includes email uniqueness check and validation.
     * @param user User to register (with hashed password)
     * @return true if registration successful
     * @throws IllegalArgumentException if validation fails
     */
    public boolean registerUser(User user) {
        // Validate name
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        
        // Validate email format
        if (user.getEmail() == null || !EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        // Check email uniqueness
        if (getUserByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        // Validate password is present (should already be hashed)
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        
        // Set default values
        if (user.getRole() == null) {
            user.setRole("USER");
        }
        if (user.getStatus() == null) {
            user.setStatus("ACTIVE");
        }
        
        return userDAO.createUser(user);
    }
}
