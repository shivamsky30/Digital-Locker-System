// DAO class: UserDao.java
package com.digitallocker.dao;

import com.digitallocker.model.User;
import com.digitallocker.util.PasswordHasher;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Data Access Object (DAO) for managing User data persistence.
 * Handles reading from and writing to the `users.txt` file.
 */
public class UserDao {
    private final Path usersFilePath; // Path to the file storing user credentials.

    /**
     * Constructs a UserDao.
     *
     * @param dataDirectory The base directory where user data files are stored.
     */
    public UserDao(String dataDirectory) {
        this.usersFilePath = Paths.get(dataDirectory, "users.txt");
        // Ensure the users.txt file exists. If not, create it.
        try {
            if (!Files.exists(usersFilePath)) {
                Files.createFile(usersFilePath);
            }
        } catch (IOException e) {
            System.err.println("Error ensuring users.txt file exists: " + e.getMessage());
            // In a real application, you might throw a runtime exception or handle this more robustly.
        }
    }

    /**
     * Finds a user by their username.
     *
     * @param username The username to search for.
     * @return An Optional containing the User object if found, or empty if not found.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public Optional<User> findUserByUsername(String username) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2 && parts[0].equals(username)) {
                    return Optional.of(new User(parts[0], parts[1]));
                }
            }
        }
        return Optional.empty(); // User not found.
    }

    /**
     * Saves a new user to the users file.
     *
     * @param user The User object to save.
     * @return true if the user was saved successfully, false if a user with that username already exists.
     * @throws IOException If an I/O error occurs while writing to the file.
     */
    public boolean saveUser(User user) throws IOException {
        // Check if user already exists to prevent duplicates.
        if (findUserByUsername(user.getUsername()).isPresent()) {
            return false; // User with this username already exists.
        }

        // Append the new user's data to the users file.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFilePath.toFile(), true))) {
            writer.write(user.getUsername() + "|" + user.getHashedPassword());
            writer.newLine();
        }
        return true; // User saved successfully.
    }
}
