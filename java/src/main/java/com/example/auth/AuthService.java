package com.example.auth;

import java.sql.*;
import java.security.MessageDigest;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Authentication Service
 * Contains intentional security vulnerabilities for compliance demo
 */
public class AuthService {

    // ============================================
    // CRITICAL ISSUES - Will BLOCK the PR
    // ============================================
    
    // CRITICAL: Hardcoded credentials
    private static final String DB_PASSWORD = "ProductionPass123!";
    private static final String API_KEY = "sk-live-abcdef1234567890";
    private static final String SECRET_KEY = "super-secret-encryption-key";
    
    private Connection connection;
    
    public AuthService() {
        try {
            // CRITICAL: Hardcoded database credentials
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/users",
                "root",
                "RootPassword123!"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // CRITICAL: SQL Injection vulnerability
    public User authenticate(String username, String password) throws SQLException {
        // Vulnerable: String concatenation in SQL query
        String query = "SELECT * FROM users WHERE username = '" + username + 
                       "' AND password = '" + password + "'";
        
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        if (rs.next()) {
            return new User(rs.getString("username"), rs.getString("email"));
        }
        return null;
    }
    
    // CRITICAL: Command injection via Runtime.exec
    public void generateReport(String reportName) {
        try {
            // CRITICAL: Command injection
            Runtime.getRuntime().exec("generate-report.sh " + reportName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    // ============================================
    // HIGH ISSUES - Will BLOCK the PR
    // ============================================
    
    // HIGH: Weak cryptographic hash (MD5)
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            return null;
        }
    }
    
    // HIGH: SHA1 is deprecated
    public String generateToken(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] hash = md.digest(data.getBytes());
            return bytesToHex(hash);
        } catch (Exception e) {
            return null;
        }
    }
    
    // HIGH: Insecure deserialization
    public Object loadSession(String filename) {
        try {
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            // HIGH: Deserializing untrusted data
            return ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }
    
    
    // ============================================
    // MEDIUM ISSUES - Will show as SUGGESTIONS
    // ============================================
    
    // MEDIUM: Debug mode enabled
    private static final boolean DEBUG = true;
    private static final boolean VERBOSE_LOGGING = true;
    
    // MEDIUM: HTTP without TLS
    private static final String API_ENDPOINT = "http://api.internal.com/v1";
    private static final String WEBHOOK_URL = "http://hooks.example.com/notify";
    
    public void logDebug(String message) {
        if (DEBUG) {
            System.out.println("[DEBUG] " + message);
        }
    }
    
    
    // ============================================
    // LOW ISSUES - Informational only
    // ============================================
    
    // TODO: Add input validation
    // FIXME: Need to implement rate limiting
    // TODO: Add proper error handling
    
    public void processRequest(String data) {
        // Placeholder - needs implementation
    }
    
    // CRITICAL: ReDoS vulnerability
    public boolean validateEmail(String email) {
        // Vulnerable: Catastrophic backtracking
        Pattern pattern = Pattern.compile("^([a-zA-Z0-9]+)+@[a-zA-Z0-9]+\\.[a-zA-Z]+$");
        return pattern.matcher(email).matches();
    }
    
    // CRITICAL: Weak encryption
    public byte[] encryptData(String data) throws Exception {
        // Vulnerable: ECB mode, weak key
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec("12345678".getBytes(), "DES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data.getBytes());
    }
    
    // HIGH: Logging sensitive data
    public void logUserLogin(String username, String password) {
        System.out.println("Login attempt: user=" + username + ", pass=" + password);
    }
    
    // HIGH: Null pointer dereference
    public String getUserRole(User user) {
        return user.getUsername().toUpperCase();  // No null check!
    }
    
    
    // Helper methods
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

class User {
    private String username;
    private String email;
    
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
    
    public String getUsername() { return username; }
    public String getEmail() { return email; }
}
