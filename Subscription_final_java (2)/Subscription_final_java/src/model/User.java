package model;

/**
 * Model class representing a user in the system.
 * Follows JavaBean conventions with proper encapsulation.
 */
public class User {
    private Integer userId;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String role; // ADMIN, USER
    private String status; // ACTIVE, INACTIVE

    // Constructors
    public User() {
    }

    public User(Integer userId, String name, String email, String role, String status) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    public User(Integer userId, String name, String email, String password, String phone, String role, String status) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.status = status;
    }

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return name + " (" + email + ")";
    }
}
