// Service class: LockerService.java
package com.digitallocker.service;

import com.digitallocker.dao.FileDao;
import com.digitallocker.dao.UserDao;
import com.digitallocker.model.FileMetadata;
import com.digitallocker.model.User;
import com.digitallocker.util.PasswordHasher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service layer for the Digital Locker System.
 * Handles business logic, orchestrates DAO operations, and enforces access control.
 */
public class LockerService {
    private final UserDao userDao;
    private final FileDao fileDao;
    private final String dataDirectory; // Base directory for all data (users.txt, user files)

    /**
     * Constructs a LockerService.
     *
     * @param dataDirectory The base directory where all application data is stored.
     */
    public LockerService(String dataDirectory) {
        this.dataDirectory = dataDirectory;
        this.userDao = new UserDao(dataDirectory);
        this.fileDao = new FileDao(dataDirectory);

        // Ensure the main data directory exists when the service is initialized.
        try {
            Files.createDirectories(Paths.get(dataDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base data directory: " + e.getMessage());
            // This error is critical, ideally should be handled at application startup.
        }
    }

    /**
     * Registers a new user.
     *
     * @param username The username for the new user.
     * @param password The password for the new user (will be hashed).
     * @return true if registration is successful, false if the username already exists.
     * @throws IOException If an I/O error occurs during user data saving.
     */
    public boolean registerUser(String username, String password) throws IOException {
        // Hash the password before storing it.
        String hashedPassword = PasswordHasher.hashPassword(password);
        User newUser = new User(username, hashedPassword);

        // Attempt to save the user using the UserDao.
        boolean saved = userDao.saveUser(newUser);
        if (saved) {
            // If user registered successfully, create their dedicated file directory.
            Path userFilesDir = getUserFilesDirectory(newUser);
            Files.createDirectories(userFilesDir);
        }
        return saved;
    }

    /**
     * Authenticates a user.
     *
     * @param username The username to authenticate.
     * @param password The plain-text password provided by the user.
     * @return The User object if authentication is successful, null otherwise.
     * @throws IOException If an I/O error occurs during user data retrieval.
     */
    public User authenticateUser(String username, String password) throws IOException {
        Optional<User> userOptional = userDao.findUserByUsername(username);

        // Check if the user exists and if the provided password matches the hashed password.
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (PasswordHasher.verifyPassword(password, user.getHashedPassword())) {
                return user; // Authentication successful.
            }
        }
        return null; // Authentication failed.
    }

    /**
     * Uploads a file for the given user.
     * Copies the file to the user's dedicated directory and stores its metadata.
     *
     * @param user The user who is uploading the file.
     * @param sourceFilePath The path to the file to be uploaded.
     * @throws IOException If an I/O error occurs during file copy or metadata saving.
     */
    public void uploadFile(User user, Path sourceFilePath) throws IOException {
        Path userFilesDir = getUserFilesDirectory(user);
        Files.createDirectories(userFilesDir); // Ensure user's directory exists.

        String originalFilename = sourceFilePath.getFileName().toString();
        // Generate a unique filename to avoid collisions and for secure storage.
        String storedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        Path destinationPath = userFilesDir.resolve(storedFilename);

        // Copy the file to the user's directory.
        Files.copy(sourceFilePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

        // Create file metadata.
        String fileId = UUID.randomUUID().toString(); // Unique ID for this file in the locker.
        String uploadDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        long fileSize = Files.size(sourceFilePath);
        FileMetadata metadata = new FileMetadata(fileId, originalFilename, storedFilename, uploadDate, fileSize);

        // Save the file metadata.
        fileDao.saveFileMetadata(user, metadata);
    }

    /**
     * Downloads a file for the given user.
     * Retrieves the file from the user's dedicated directory.
     *
     * @param user The user who is downloading the file.
     * @param fileId The unique ID of the file to download.
     * @param destinationDirectory The directory where the file should be downloaded.
     * @throws IOException If an I/O error occurs during file copy.
     * @throws IllegalArgumentException If the file ID is not found or not owned by the user.
     */
    public void downloadFile(User user, String fileId, Path destinationDirectory) throws IOException {
        Optional<FileMetadata> fileMetadataOptional = fileDao.findFileById(user, fileId);

        if (fileMetadataOptional.isEmpty()) {
            throw new IllegalArgumentException("File with ID " + fileId + " not found in your locker.");
        }

        FileMetadata metadata = fileMetadataOptional.get();
        Path userFilesDir = getUserFilesDirectory(user);
        Path sourceFilePath = userFilesDir.resolve(metadata.getStoredFilename());

        // Construct the destination path using the original filename.
        Path destinationPath = destinationDirectory.resolve(metadata.getOriginalFilename());

        // Validate if the source file actually exists on disk.
        if (!Files.exists(sourceFilePath)) {
            // This indicates a discrepancy between metadata and actual files.
            System.err.println("Warning: Stored file " + sourceFilePath + " not found on disk for metadata ID " + fileId);
            throw new IOException("Stored file content missing. Please contact support.");
        }

        // Copy the stored file to the desired destination.
        Files.copy(sourceFilePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Lists all files for the given user.
     *
     * @param user The user for whom to list files.
     * @return A list of FileMetadata objects representing the user's files.
     * @throws IOException If an I/O error occurs during file metadata retrieval.
     */
    public List<FileMetadata> listFiles(User user) throws IOException {
        return fileDao.getFilesMetadata(user);
    }

    /**
     * Helper method to get the path to a user's specific files directory.
     *
     * @param user The user for whom to get the directory path.
     * @return The Path object representing the user's file storage directory.
     */
    private Path getUserFilesDirectory(User user) {
        return Paths.get(dataDirectory, user.getUsername());
    }
}
