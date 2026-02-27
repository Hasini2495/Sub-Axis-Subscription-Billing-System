package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for password hashing and verification.
 * Uses SHA-256 with salt for secure password storage.
 */
public class PasswordUtil {
    
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    
    /**
     * Hashes a password using SHA-256 with a random salt.
     * @param password Plain text password
     * @return Hashed password with salt (format: salt$hash)
     */
    public static String hashPassword(String password) {
        try {
            // Generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hash password with salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Encode to Base64 for storage
            String saltString = Base64.getEncoder().encodeToString(salt);
            String hashString = Base64.getEncoder().encodeToString(hashedPassword);
            
            // Return format: salt$hash
            return saltString + "$" + hashString;
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Verifies a password against a stored hash.
     * @param password Plain text password to verify
     * @param storedHash Stored hash (format: salt$hash)
     * @return true if password matches
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Split stored hash into salt and hash
            String[] parts = storedHash.split("\\$");
            if (parts.length != 2) {
                return false;
            }
            
            String saltString = parts[0];
            String hashString = parts[1];
            
            // Decode salt from Base64
            byte[] salt = Base64.getDecoder().decode(saltString);
            
            // Hash input password with same salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Encode to Base64 and compare
            String inputHash = Base64.getEncoder().encodeToString(hashedPassword);
            return inputHash.equals(hashString);
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Validates password strength.
     * @param password Password to validate
     * @return true if password meets requirements
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUppercase = true;
            if (Character.isLowerCase(c)) hasLowercase = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        
        return hasUppercase && hasLowercase && hasDigit;
    }
}
