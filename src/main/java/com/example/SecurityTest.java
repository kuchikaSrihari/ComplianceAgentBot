package com.example;

import java.sql.*;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Test file with intentional vulnerabilities for AI Compliance Bot testing
 */
public class SecurityTest {
    
    private static final Logger logger = LogManager.getLogger(SecurityTest.class);
    
    // CWE-798: Hardcoded credentials
    private static final String DB_PASSWORD = "admin123";
    private static final String API_KEY = "sk-secret-key-12345";
    
    // CWE-89: SQL Injection
    public User getUser(String username) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/db");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE name = '" + username + "'");
        return null;
    }
    
    // CVE-2021-44228: Log4j JNDI Injection
    public void logUserInput(String input) {
        logger.info("User input: " + input);
    }
    
    // CWE-78: Command Injection
    public void executeCommand(String cmd) throws IOException {
        Runtime.getRuntime().exec(cmd);
    }
    
    // CWE-502: Unsafe Deserialization
    public Object readObject(InputStream is) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(is);
        return ois.readObject();
    }
    
    // CWE-327: Weak Cryptography
    public byte[] hashData(String data) throws Exception {
        return java.security.MessageDigest.getInstance("MD5").digest(data.getBytes());
    }
}
