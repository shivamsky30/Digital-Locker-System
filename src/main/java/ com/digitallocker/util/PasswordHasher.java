// Utility class: PasswordHasher.java
package com.digitallocker.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for hashing and verifying passwords using SHA-256.
 * Provides basic hashing without salts for simplicity in this example,
 * but emphasizes that salting is crucial for real-world applications.
 */
public class PasswordHasher {

    // For a real-world application, ALWAYS use salting and a strong,
    // adaptive hashing algorithm like bcrypt or Argon2.
    // This simplified example uses SHA-256 for demonstration purposes.

    /**
     * Hashes a plain-text password using SHA-256.
     *
     * @param password The plain-text password to hash.
     * @return The SHA-256 hashed password as a hexadecimal string.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            // Convert byte array to hexadecimal string.
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // This exception should ideally not happen for SHA-256 as it's a standard algorithm.
            throw new RuntimeException("SHA-256 algorithm not found.", e);
        }
    }

    /**
     * Verifies a plain-text password against a stored hashed password.
     *
     * @param plainPassword The plain-text password entered by the user.
     * @param hashedPassword The stored hashed password.
     * @return true if the plain password, when hashed, matches the stored hashed password; false otherwise.
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        // Hash the provided plain password and compare it with the stored hash.
        return hashPassword(plainPassword).equals(hashedPassword);
    }

    /*
    // Example of how salting would be incorporated (more robust for real applications)
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 16 bytes is a good size for a salt
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPasswordWithSalt(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt)); // Apply the salt
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found.", e);
        }
    }

    public static boolean verifyPasswordWithSalt(String plainPassword, String storedHashedPassword, String storedSalt) {
        String hashedPasswordAttempt = hashPasswordWithSalt(plainPassword, storedSalt);
        return hashedPasswordAttempt.equals(storedHashedPassword);
    }
    */
}
