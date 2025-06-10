// DAO class: FileDao.java
package com.digitallocker.dao;

import com.digitallocker.model.FileMetadata;
import com.digitallocker.model.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Data Access Object (DAO) for managing FileMetadata persistence for each user.
 * Handles reading from and writing to user-specific metadata files (e.g., `username_files.txt`).
 */
public class FileDao {
    private final String dataDirectory; // Base directory for all data.

    /**
     * Constructs a FileDao.
     *
     * @param dataDirectory The base directory where user-specific file metadata files are stored.
     */
    public FileDao(String dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    /**
     * Returns the path to the metadata file for a given user.
     * The filename is derived from the username to ensure uniqueness.
     *
     * @param user The user whose metadata file path is needed.
     * @return The Path object for the user's metadata file.
     */
    private Path getUserMetadataFilePath(User user) {
        return Paths.get(dataDirectory, user.getUsername() + "_files.txt");
    }

    /**
     * Retrieves all file metadata for a specific user.
     *
     * @param user The user for whom to retrieve file metadata.
     * @return A list of FileMetadata objects. Returns an empty list if no files or file doesn't exist.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public List<FileMetadata> getFilesMetadata(User user) throws IOException {
        Path userMetadataFile = getUserMetadataFilePath(user);
        List<FileMetadata> files = new ArrayList<>();

        // If the metadata file doesn't exist, return an empty list.
        if (!Files.exists(userMetadataFile)) {
            return files;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(userMetadataFile.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    // Parse each line into a FileMetadata object.
                    files.add(FileMetadata.fromFileString(line));
                } catch (IllegalArgumentException e) {
                    System.err.println("Warning: Corrupted file metadata line for user " + user.getUsername() + ": " + line + " - " + e.getMessage());
                }
            }
        }
        return files;
    }

    /**
     * Saves new file metadata for a user. Appends the metadata to the user's file.
     * Ensures the metadata file exists.
     *
     * @param user The user for whom to save the file metadata.
     * @param fileMetadata The FileMetadata object to save.
     * @throws IOException If an I/O error occurs while writing to the file.
     */
    public void saveFileMetadata(User user, FileMetadata fileMetadata) throws IOException {
        Path userMetadataFile = getUserMetadataFilePath(user);

        // Ensure the metadata file exists.
        if (!Files.exists(userMetadataFile)) {
            Files.createFile(userMetadataFile);
        }

        // Append the new file metadata to the user's metadata file.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userMetadataFile.toFile(), true))) {
            writer.write(fileMetadata.toFileString());
            writer.newLine();
        }
    }

    /**
     * Finds a specific file's metadata by its ID for a given user.
     *
     * @param user The user who owns the file.
     * @param fileId The unique ID of the file to find.
     * @return An Optional containing the FileMetadata object if found, or empty if not found.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public Optional<FileMetadata> findFileById(User user, String fileId) throws IOException {
        return getFilesMetadata(user).stream()
                .filter(file -> file.getId().equals(fileId))
                .findFirst();
    }

    /**
     * Updates a file's metadata for a user by rewriting the entire metadata file.
     * This method is less efficient for frequent updates but simpler for file-based storage.
     *
     * @param user The user who owns the file.
     * @param updatedMetadata The updated FileMetadata object.
     * @throws IOException If an I/O error occurs while writing to the file.
     */
    public void updateFileMetadata(User user, FileMetadata updatedMetadata) throws IOException {
        Path userMetadataFile = getUserMetadataFilePath(user);
        List<FileMetadata> currentFiles = getFilesMetadata(user);

        // Remove the old metadata and add the updated one.
        List<FileMetadata> updatedList = currentFiles.stream()
                .map(f -> f.getId().equals(updatedMetadata.getId()) ? updatedMetadata : f)
                .collect(Collectors.toList());

        // Rewrite the entire file with the updated list.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userMetadataFile.toFile()))) {
            for (FileMetadata file : updatedList) {
                writer.write(file.toFileString());
                writer.newLine();
            }
        }
    }

    /**
     * Deletes a file's metadata for a user.
     * This involves rewriting the entire metadata file without the deleted entry.
     *
     * @param user The user who owns the file.
     * @param fileId The ID of the file to delete metadata for.
     * @return true if the metadata was deleted, false if not found.
     * @throws IOException If an I/O error occurs while writing to the file.
     */
    public boolean deleteFileMetadata(User user, String fileId) throws IOException {
        Path userMetadataFile = getUserMetadataFilePath(user);
        List<FileMetadata> currentFiles = getFilesMetadata(user);

        long initialSize = currentFiles.size();
        List<FileMetadata> remainingFiles = currentFiles.stream()
                .filter(f -> !f.getId().equals(fileId))
                .collect(Collectors.toList());

        // If the size is different, it means a file was removed, so rewrite the file.
        if (remainingFiles.size() < initialSize) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(userMetadataFile.toFile()))) {
                for (FileMetadata file : remainingFiles) {
                    writer.write(file.toFileString());
                    writer.newLine();
                }
            }
            return true;
        }
        return false; // File metadata not found.
    }
}
