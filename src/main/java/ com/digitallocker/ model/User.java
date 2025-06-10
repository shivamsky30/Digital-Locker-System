// Model class: User.java
package com.digitallocker.model;

/**
 * Represents a user in the digital locker system.
 * Contains user's username and hashed password.
 */
public class User {
    private String username;
    private String hashedPassword;

    public User(String username, String hashedPassword) {
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    // You might want to add a method to verify password if needed,
    // but typically verification happens in the service layer using a hasher.
    // public boolean verifyPassword(String password) {
    //     return PasswordHasher.hashPassword(password).equals(this.hashedPassword);
    // }
}
