package ui.utils;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Centralized utility class for input validation.
 * Provides reusable validation logic across UI forms.
 */
public class ValidationUtils {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    /**
     * Validates if a string is not null or empty.
     * @param value String to validate
     * @return true if valid
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Validates email format.
     * @param email Email to validate
     * @return true if valid email format
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validates if a string can be parsed as a positive number.
     * @param value String to validate
     * @return true if valid positive number
     */
    public static boolean isPositiveNumber(String value) {
        try {
            BigDecimal number = new BigDecimal(value);
            return number.compareTo(BigDecimal.ZERO) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validates if a string can be parsed as a positive integer.
     * @param value String to validate
     * @return true if valid positive integer
     */
    public static boolean isPositiveInteger(String value) {
        try {
            int number = Integer.parseInt(value);
            return number > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validates if a string has minimum length.
     * @param value String to validate
     * @param minLength Minimum required length
     * @return true if length is valid
     */
    public static boolean hasMinLength(String value, int minLength) {
        return value != null && value.trim().length() >= minLength;
    }
    
    /**
     * Gets validation error message for empty field.
     * @param fieldName Name of the field
     * @return Error message
     */
    public static String getEmptyFieldError(String fieldName) {
        return fieldName + " cannot be empty";
    }
    
    /**
     * Gets validation error message for invalid number.
     * @param fieldName Name of the field
     * @return Error message
     */
    public static String getInvalidNumberError(String fieldName) {
        return fieldName + " must be a valid positive number";
    }
}
