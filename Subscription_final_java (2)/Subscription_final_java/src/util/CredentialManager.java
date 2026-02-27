package util;

import java.io.*;
import java.util.Properties;
import java.util.Base64;

/**
 * Manages saved login credentials with basic encoding.
 * Stores credentials in a properties file in the user's home directory.
 */
public class CredentialManager {
    
    private static final String CREDENTIALS_FILE = System.getProperty("user.home") + 
                                                   File.separator + ".saas_credentials.properties";
    private static final String EMAIL_KEY = "saved.email";
    private static final String PASSWORD_KEY = "saved.password";
    private static final String REMEMBER_KEY = "remember.me";
    
    /**
     * Saves credentials to file with basic encoding.
     */
    public static boolean saveCredentials(String email, String password) {
        Properties props = new Properties();
        
        try {
            // Encode credentials (basic Base64 - not secure, just obfuscation)
            String encodedEmail = Base64.getEncoder().encodeToString(email.getBytes());
            String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());
            
            props.setProperty(EMAIL_KEY, encodedEmail);
            props.setProperty(PASSWORD_KEY, encodedPassword);
            props.setProperty(REMEMBER_KEY, "true");
            
            try (FileOutputStream out = new FileOutputStream(CREDENTIALS_FILE)) {
                props.store(out, "SaaS Platform Saved Credentials");
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save credentials: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Loads saved credentials if they exist.
     */
    public static SavedCredentials loadCredentials() {
        File file = new File(CREDENTIALS_FILE);
        
        if (!file.exists()) {
            return null;
        }
        
        Properties props = new Properties();
        
        try (FileInputStream in = new FileInputStream(file)) {
            props.load(in);
            
            String rememberMe = props.getProperty(REMEMBER_KEY);
            if (!"true".equals(rememberMe)) {
                return null;
            }
            
            String encodedEmail = props.getProperty(EMAIL_KEY);
            String encodedPassword = props.getProperty(PASSWORD_KEY);
            
            if (encodedEmail == null || encodedPassword == null) {
                return null;
            }
            
            // Decode credentials
            String email = new String(Base64.getDecoder().decode(encodedEmail));
            String password = new String(Base64.getDecoder().decode(encodedPassword));
            
            return new SavedCredentials(email, password);
            
        } catch (IOException e) {
            System.err.println("Failed to load credentials: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Clears saved credentials.
     */
    public static boolean clearCredentials() {
        File file = new File(CREDENTIALS_FILE);
        
        if (file.exists()) {
            return file.delete();
        }
        
        return true;
    }
    
    /**
     * Checks if credentials are saved.
     */
    public static boolean hasStoredCredentials() {
        return new File(CREDENTIALS_FILE).exists();
    }
    
    /**
     * Simple data class for saved credentials.
     */
    public static class SavedCredentials {
        private final String email;
        private final String password;
        
        public SavedCredentials(String email, String password) {
            this.email = email;
            this.password = password;
        }
        
        public String getEmail() {
            return email;
        }
        
        public String getPassword() {
            return password;
        }
    }
}
